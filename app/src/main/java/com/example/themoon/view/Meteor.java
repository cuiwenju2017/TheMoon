package com.example.themoon.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.Nullable;
import com.example.themoon.R;
import com.example.themoon.entity.MeteorParam;
import com.example.themoon.entity.StarParam;
import com.example.themoon.utils.Unit;
import java.util.ArrayList;
import java.util.List;

public class Meteor extends View {

    private int width;
    private int height;
    private Context context;

    /**
     * 资源
     */
    private Bitmap starBitmap;

    /**
     * 背景缓存 Bitmap
     */
    private Bitmap MeteorBitmap;
    private Paint weatherPaint;
    private Paint starPaint;
    private List<StarParam> starParams = new ArrayList<>();
    private List<MeteorParam> meteorParams = new ArrayList<>();

    //流星参数信息
    final int meteorWidth = 200;
    /**
     * 流星rectF
     */
    private RectF starRectF;
    /// 流星的长度
    final float meteorHeight = 0.8f;

    //流星的高度
    final int radius = 10;

    /**
     * 流星过滤器
     */
    private ColorMatrixColorFilter starIdentity;

    /**
     * 透明filter
     */
    private ColorMatrixColorFilter[] alphaFilters = new ColorMatrixColorFilter[100];

    /**
     * 流星渐变
     */
    private LinearGradient mStarShader;
    /**
     * 背景渐变
     */
    private LinearGradient mBackgroundShader;

    private Rect backgroundRect;

    public Meteor(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void initAlphaFilter() {
        for (int i = 0; i < 100; i++) {
            alphaFilters[i] = new ColorMatrixColorFilter(new float[]{
                    1, 0, 0, 0, 0,
                    0, 1, 0, 0, 0,
                    0, 0, 1, 0, 0,
                    0, 0, 0, 0.01f * i, 0});
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getWidth();
        height = getHeight();
        initStarMeteorParams();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = getWidth();
        height = getHeight();
        initStarMeteorParams();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawWeather(canvas);
        invalidate();
    }

    private void drawWeather(Canvas canvas) {
        if (MeteorBitmap == null) {
            MeteorBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        }
        canvas.drawBitmap(MeteorBitmap, 0, 0, null);
        drawWeatherBg(canvas);
        //绘制星星  流星
        drawStarMeteor(canvas);
    }

    /**
     * 绘制背景
     */
    private void drawWeatherBg(Canvas canvas) {
        int[] color = new int[]{Color.parseColor(context.getResources().getStringArray(R.array.weather_cloudy_light)[0]),
                Color.parseColor(context.getResources().getStringArray(R.array.weather_cloudy_light)[1])};
        mBackgroundShader = new LinearGradient(0, 0, 0, height, color[0], color[1], Shader.TileMode.MIRROR);
        weatherPaint.setShader(mBackgroundShader);
        weatherPaint.setShadowLayer(15, 10, 10, Color.GRAY);
        backgroundRect = new Rect(0, 0, width, height);
        canvas.drawRect(backgroundRect, weatherPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    /**
     * 绘制星星 和流星
     */
    private void drawStarMeteor(Canvas canvas) {
        if (starParams.size() > 0) {
            for (StarParam param : starParams) {
                drawStar(param, canvas);
            }
        }
        if (meteorParams.size() > 0) {
            for (MeteorParam param : meteorParams) {
                drawMeteor(param, canvas);
            }
        }
    }

    /**
     * 绘制星星
     *
     * @param star
     * @param canvas
     */
    private void drawStar(StarParam star, Canvas canvas) {
        if (star == null) {
            return;
        }
        canvas.save();
        int index = (int) (star.alpha * 100);

        if (index < 0) {
            index = 0;
        }
        if (index > 99) {
            index = 99;
        }
        starIdentity = alphaFilters[index];
        starPaint.setColorFilter(starIdentity);
        canvas.scale((float) star.scale, (float) star.scale);
        canvas.drawBitmap(starBitmap, (float) star.x, (float) star.y, starPaint);
        canvas.restore();
        star.move();

    }

    /**
     * 绘制流星
     */
    private void drawMeteor(MeteorParam meteor, Canvas canvas) {
        if (meteor == null) {
            return;
        }

        canvas.save();
        if (mStarShader == null) {
            mStarShader = new LinearGradient(0, 0, 0, width, Color.parseColor("#FFFFFFFF"), Color.parseColor("#00FFFFFF"), Shader.TileMode.MIRROR);
        }

        starPaint.setShader(mStarShader);
        starPaint.setColorFilter(null);
        starPaint.setAntiAlias(true);
        canvas.rotate((float) (Math.PI * meteor.radians));
        float scale = Unit.px2dip(context, width) / 392.0f;
        canvas.scale(scale, scale);
        canvas.translate(
                (float) meteor.translateX, (float) (Math.tan(Math.PI * 0.1) * Unit.dip2px(context, meteorWidth) + meteor.translateY));
        if (starRectF == null) {
            starRectF = new RectF(0, 0, Unit.dip2px(context, meteorWidth), Unit.dip2px(context, meteorHeight));
        }
        float starRadius = Unit.dip2px(context, radius);
        canvas.drawRoundRect(starRectF, starRadius, starRadius, starPaint);
        meteor.move(context);
        canvas.restore();
    }

    private void init() {
        starBitmap = zoomImg(BitmapFactory.decodeResource(getResources(), R.drawable.snow), 20, 20);
        //初始化 画笔
        weatherPaint = new Paint();
        weatherPaint.setAntiAlias(true);
        weatherPaint.setStyle(Paint.Style.FILL);

        starPaint = new Paint();
        starPaint.setMaskFilter(new BlurMaskFilter(1, BlurMaskFilter.Blur.NORMAL));
        starPaint.setColor(Color.WHITE);
        starPaint.setStyle(Paint.Style.FILL);

        initAlphaFilter();
    }

    /**
     * 初始化 星星  流星 参数
     */
    private void initStarMeteorParams() {
        meteorParams.clear();
        starParams.clear();

        if (width == 0 || height == 0) {
            return;
        }

        float widthRatio = Unit.px2dip(context, width) / 392.0f;
        for (int i = 0; i < 50; i++) {
            int index = (int) (Math.random() * 2);
            StarParam starParam = new StarParam(index);
            starParam.init(width, height, widthRatio);
            starParams.add(starParam);
        }

        for (int i = 0; i < 2; i++) {
            MeteorParam param = new MeteorParam();
            param.init(width, height, widthRatio);
            meteorParams.add(param);
        }
    }

    public Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }
}
