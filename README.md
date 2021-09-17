### 详情请参考
[Android开发中秋之嫦娥奔月](https://juejin.cn/post/7008429894967230478)

### 2021.09.17 新增嫦娥奔月帧动画。
```
// 提取帧动画
anim = (AnimationDrawable) binding.ivChange.getBackground();
//开始动画
anim.start();
```

drawable下的anim_gif.xml文件
```
<?xml version="1.0" encoding="utf-8"?>
<animation-list xmlns:android="http://schemas.android.com/apk/res/android"
    android:oneshot="false">
    <!--oneshot 单次，false代表动画结束时开始循环-->
    <item
        android:drawable="@drawable/a1"
        android:duration="100" />
    <item
        android:drawable="@drawable/a2"
        android:duration="100" />
    <item
        android:drawable="@drawable/a3"
        android:duration="100" />
</animation-list>
```