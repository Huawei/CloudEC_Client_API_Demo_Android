package com.huawei.opensdk.ec_sdk_demo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

public class LScrollView extends ScrollView {
    private LScrollView.OnScrollYListener onScrollChangeListener;
    public LScrollView(Context context) {
        super(context);
    }

    public LScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        if (this.onScrollChangeListener != null) {
            this.onScrollChangeListener.onScrollChange(this, t, oldt);
        }

    }

    public void setOnScrollYListener(LScrollView.OnScrollYListener listener) {
        this.onScrollChangeListener = listener;
    }

    public interface OnScrollYListener {
        void onScrollChange(View var1, int var2, int var3);
    }
}
