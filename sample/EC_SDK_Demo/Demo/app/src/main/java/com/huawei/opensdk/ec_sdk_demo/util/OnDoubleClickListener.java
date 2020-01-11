package com.huawei.opensdk.ec_sdk_demo.util;

import android.view.MotionEvent;
import android.view.View;

/**
 * This class is about double-click an event.
 */
public class OnDoubleClickListener implements View.OnTouchListener {

    /**
     * 点击次数
     */
    private int count = 0;

    /**
     * 二次点击时间
     */
    private long secondTime = 0;

    /**
     * 首次点击时间
     */
    private long firstTime = 0;

    /**
     * 间隔时间
     */
    private static final int INTERVAL_TIME = 500;

    /**
     * 双击事件的回调通知
     */
    private DoubleClickListenerNotify clickListenerNotify;

    /**
     * true:onTouch的down事件后不触发onClick和onLongClick事件;
     * false:onTouch的down和up事件后会触发click事件，down后长按触发onLongClick事件
     */
    private boolean onTouchResult;

    public interface DoubleClickListenerNotify {
        void OnDoubleClick(View v);
    }

    public OnDoubleClickListener(DoubleClickListenerNotify clickListenerNotify, boolean onTouchResult) {
        this.clickListenerNotify = clickListenerNotify;
        this.onTouchResult = onTouchResult;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (MotionEvent.ACTION_DOWN == event.getAction())
        {
            count++;
            if (1 == count)
            {
                firstTime = System.currentTimeMillis();
            }
            else if (2 == count)
            {
                secondTime = System.currentTimeMillis();
                isDoubleTouch(v);
                secondTime = 0;
            }
        }
        return onTouchResult;
    }

    private void isDoubleTouch(View v)
    {
        if (null == clickListenerNotify)
        {
            return;
        }

        if (secondTime - firstTime <= INTERVAL_TIME)
        {
            clickListenerNotify.OnDoubleClick(v);
            count = 0;
            firstTime = 0;
        }
        else
        {
            firstTime = secondTime;
            count = 1;
        }
    }
}
