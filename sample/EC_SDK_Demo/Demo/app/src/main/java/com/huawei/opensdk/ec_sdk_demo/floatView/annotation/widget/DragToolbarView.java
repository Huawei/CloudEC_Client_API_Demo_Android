package com.huawei.opensdk.ec_sdk_demo.floatView.annotation.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class DragToolbarView extends LinearLayout {
    private GestureDetector mGD;
    private ToolbarScrollListener mListener;

    public DragToolbarView(Context context) {
        super(context);
    }

    public DragToolbarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DragToolbarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public DragToolbarView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent) {
        if (this.mGD != null) {
            return this.mGD.onTouchEvent(paramMotionEvent);
        }
        return super.onInterceptTouchEvent(paramMotionEvent);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.mGD != null) {
            if ((this.mListener != null) && (event.getAction() == MotionEvent.ACTION_UP)) {
                this.mListener.onTouchEventUp();
            }
            return this.mGD.onTouchEvent(event);
        }
        return super.onTouchEvent(event);
    }

    public void setGestureDetectorListener(ToolbarScrollListener toolbarScrollListener) {
        this.mListener = toolbarScrollListener;
        if (toolbarScrollListener == null) {
            this.mGD = null;
            return;
        }
        this.mGD = new GestureDetector(getContext(), toolbarScrollListener);
        this.mGD.setIsLongpressEnabled(false);
    }

    public static class ToolbarScrollListener
            extends GestureDetector.SimpleOnGestureListener {
        public void onTouchEventUp() {
        }
    }
}

