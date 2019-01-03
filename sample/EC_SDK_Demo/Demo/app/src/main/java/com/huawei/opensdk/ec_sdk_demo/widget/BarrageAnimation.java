package com.huawei.opensdk.ec_sdk_demo.widget;

import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * This class is about display the animation effect of the barrage.
 */
public class BarrageAnimation {

    private TranslateAnimation translateAnimation;

    public BarrageAnimation(TextView textView, RelativeLayout rLayout, int width, int height)
    {
        int length = rLayout.getBottom() - rLayout.getTop();
        int y = rLayout.getTop() + (int) (Math.random() * length);

        if (y >= rLayout.getBottom() - height * 2)
        {
            y = rLayout.getBottom() -  (new Double(height * 2.5)).intValue();
        }

        translateAnimation = new TranslateAnimation(rLayout.getRight(), -width, y, y);
        translateAnimation.setDuration(6000);
        translateAnimation.setFillAfter(true); //如果fillAfter的值为true,则动画执行后，控件将停留在执行结果的状态
        textView.setAnimation(translateAnimation);
        translateAnimation.setInterpolator(new LinearInterpolator()); // 线性均匀改变
        translateAnimation.start();
    }
}
