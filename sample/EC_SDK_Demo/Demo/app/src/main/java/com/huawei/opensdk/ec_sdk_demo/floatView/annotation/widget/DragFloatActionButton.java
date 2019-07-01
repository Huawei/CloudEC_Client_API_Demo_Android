package com.huawei.opensdk.ec_sdk_demo.floatView.annotation.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.floatView.util.LayoutUtil;


public class DragFloatActionButton extends FloatingActionButton {

    private final static float CLICK_DRAG_TOLERANCE = 10; // Often, there will be a slight, unintentional, drag when the user taps the FAB, so we need to account for this.

    private float downRawX, downRawY;
    private float dX, dY;

    private int mStatusBarHeight;

    private int mAnnoToolbarHeight;

    // 回调接口
    private ICallBack mCallBack;

    public DragFloatActionButton(Context context) {
        super(context);
        init();
    }

    public DragFloatActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DragFloatActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mStatusBarHeight = LayoutUtil.getStatusBarHeight();
        mAnnoToolbarHeight = getResources().getDimensionPixelSize(R.dimen.anno_toolbar_height);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == event.ACTION_DOWN) {

            downRawX = event.getRawX();
            downRawY = event.getRawY();
            dX = getX() - downRawX;
            dY = getY() - downRawY;
            return true; // Consumed

        } else if (action == MotionEvent.ACTION_MOVE) {

            int viewWidth = getWidth();
            int viewHeight = getHeight();

            View viewParent = (View) getParent();
            int parentWidth = viewParent.getWidth();
            int parentHeight = viewParent.getHeight();

            float newX = event.getRawX() + dX;
            newX = Math.max(0, newX); // Don't allow the FAB past the left hand side of the parent
            newX = Math.min(parentWidth - viewWidth, newX); // Don't allow the FAB past the right hand side of the parent

            float newY = event.getRawY() + dY;
            newY = Math.max(mStatusBarHeight, newY); // Don't allow the FAB past the top of the parent
            newY = Math.min(parentHeight - viewHeight - mAnnoToolbarHeight, newY); // Don't allow the FAB past the bottom of the parent

            animate()
                    .x(newX)
                    .y(newY)
                    .setDuration(0)
                    .start();
            bringToFront();
            return true; // Consumed

        } else if (action == MotionEvent.ACTION_UP) {

            float upRawX = event.getRawX();
            float upRawY = event.getRawY();

            float upDX = upRawX - downRawX;
            float upDY = upRawY - downRawY;

            if (Math.abs(upDX) < CLICK_DRAG_TOLERANCE && Math.abs(upDY) < CLICK_DRAG_TOLERANCE) {
                LogUtil.i(UIConstants.DEMO_TAG,"userClick start annotation ");
                // 点击开始标注
                this.mCallBack.clickAnnon();

            } else { // A drag
                return true; // Consumed
            }

        }

        return false;
    }

    // 恢复到默认位置
    public void resetAnnotBtnPosition() {
        float newX = getResources().getDimensionPixelSize(R.dimen.anno_btn_margin_left);
        int bottom = getResources().getDimensionPixelSize(R.dimen.anno_btn_margin_bottom);
        View viewParent = (View) getParent();
        int parentHeight = viewParent.getHeight();
        float newY = parentHeight - getHeight() - bottom;
        animate()
                .x(newX)
                .y(newY)
                .setDuration(0)
                .start();
        bringToFront();
    }

    /**
     * 点击键盘中标注按钮后的操作，用于接口回调
     */
    public void setOnClickAnnon(ICallBack mCallBack){
        this.mCallBack = mCallBack;
    }

    public interface ICallBack {
        void clickAnnon();
    }
}