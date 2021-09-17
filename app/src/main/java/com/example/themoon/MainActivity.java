package com.example.themoon;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.view.WindowManager;

import com.example.themoon.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private Handler handler = new Handler(Looper.getMainLooper());
    private AnimationDrawable anim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //保持屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //立即调用
        handler.post(task);

        // 提取帧动画
        anim = (AnimationDrawable) binding.ivChange.getBackground();
        //开始动画
        anim.start();

        //平移
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(binding.ivChange, "translationX", 1000f, 0f).setDuration(1000 * 10);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(binding.ivChange, "translationY", 1500f, 0f).setDuration(1000 * 10);
        //透明度渐变
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(binding.ivChange, "alpha", 1f, 0f).setDuration(1000 * 1);
        //缩放
        ObjectAnimator animator4 = ObjectAnimator.ofFloat(binding.ivChange, "scaleX", 1.5f, 0.5f).setDuration(1000 * 10);
        ObjectAnimator animator5 = ObjectAnimator.ofFloat(binding.ivChange, "scaleY", 1.5f, 0.5f).setDuration(1000 * 10);
        //组合动画
        AnimatorSet set = new AnimatorSet();
        set.play(animator1).with(animator2).with(animator4).with(animator5).before(animator3);
        //开始动画
        set.start();

        ObjectAnimator.ofFloat(binding.nsv, "translationY", 1200f, 0f).setDuration(1000 * 10).start();
    }

    private Runnable task = new Runnable() {
        public void run() {
            handler.postDelayed(this, 30 * 1000);//设置循环时间，此处是5秒
            //平移
            ObjectAnimator.ofFloat(binding.ivCloud, "translationX", -1000f, 1200f).setDuration(1000 * 30).start();
        }
    };
}