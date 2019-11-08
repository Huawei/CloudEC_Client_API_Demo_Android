package com.huawei.opensdk.ec_sdk_demo.floatView.annotation.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.demoservice.MeetingMgr;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.floatView.annotation.common.AnnotationConstants;
import com.huawei.opensdk.ec_sdk_demo.floatView.annotation.common.AnnotationHelper;
import com.huawei.opensdk.ec_sdk_demo.floatView.annotation.widget.adapter.ColorPickAdapter;
import com.huawei.opensdk.ec_sdk_demo.floatView.util.LayoutUtil;

import java.util.HashMap;

public class AnnoToolBar extends LinearLayout implements View.OnClickListener {

    private static final String TAG = AnnoToolBar.class.getSimpleName();

    private GestureDetector mGD;

    private ToolbarScrollListener mListener;

    private Context mContext;

    private ToolbarButton mErase; // 擦除

    private ImageView mExit; // 退出

    private ToolbarButton mPen;  // 画笔

    private ToolbarButton mEmpty; // 清空

    private ToolbarButton mColor; // 颜色

    private PopupWindow mColorPopupWin;

    private ListView mColorList;

    private ColorPickAdapter colorListAdapter;

    private ToolbarButton selectedButton;

    private int mStatusBarHeight;

    private ImageView mArrayUpView;

    private ImageView mArrayDownView;

    private AnnotationHelper annotationHelper;

    private int currentPenColor = 0;//当前画笔颜色

    // 回调接口
    private ICallBack mCallBack;



    public AnnoToolBar(Context context) {
        this(context, null);
        init(context);
    }

    public AnnoToolBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AnnoToolBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        annotationHelper = new AnnotationHelper();
        View.inflate(getContext(), R.layout.anno_toolbar, this);
        mColor = (ToolbarButton) findViewById(R.id.float_anno_color);
        mEmpty = (ToolbarButton) findViewById(R.id.float_anno_empty);
        mErase = (ToolbarButton) findViewById(R.id.float_anno_erase);
        mPen = (ToolbarButton) findViewById(R.id.float_anno_pen);
        mExit = (ImageView) findViewById(R.id.float_anno_exit);
        annotationHelper.initColorList();
        View popupView = LayoutInflater.from(context).inflate(R.layout.float_color_pick_layout, null);
        mColorList = (ListView) popupView.findViewById(R.id.float_color_pick_list);
        colorListAdapter = new ColorPickAdapter(annotationHelper.getColorList(), mContext);
        mColorList.setAdapter(colorListAdapter);
        mArrayDownView = (ImageView) popupView.findViewById(R.id.array_down);
        mArrayUpView = (ImageView)popupView.findViewById(R.id.array_up);
        mColorList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                updateSelectColor(annotationHelper.getColorList().get(arg2));
                mColorPopupWin.dismiss();
            }
        });
        mColorPopupWin = new PopupWindow(this);
        int popupWinHeight = mContext.getResources().getDimensionPixelSize(R.dimen.dp_120) + mContext.getResources().getDimensionPixelSize(R.dimen.dp_12);
        int popupwinWidth = mContext.getResources().getDimensionPixelSize(R.dimen.dp_40);
        mColorPopupWin.setHeight(popupWinHeight);
        mColorPopupWin.setWidth(popupwinWidth);
        mColorPopupWin.setContentView(popupView);
        mColorPopupWin.setBackgroundDrawable(new ColorDrawable());
        mColorPopupWin.setFocusable(true);


        mColor.setText(R.string.anno_color);
        mPen.setText(R.string.anno_pen);
        mErase.setText(R.string.anno_erase);
        mEmpty.setText(R.string.anno_empty);

        updatePenColor();
        mEmpty.setIconBackgroundResource(R.drawable.float_anno_empty);
        mErase.setIconBackgroundResource(R.drawable.float_anno_erase);
        mExit.setBackgroundResource(R.drawable.float_anno_exit);

        mEmpty.setOnClickListener(this);
        mErase.setOnClickListener(this);
        mPen.setOnClickListener(this);
        mColor.setOnClickListener(this);
        mExit.setOnClickListener(this);
        setGestureDetectorListener(new GuestureListener());

        mEmpty.setSelected(false);
        mErase.setSelected(false);
        mPen.setSelected(true);
        mColor.setSelected(false);
        mExit.setSelected(false);
        selectedButton = mPen;
        mStatusBarHeight = LayoutUtil.getStatusBarHeight();
    }


    @Override
    public void onClick(View v) {

        int id = v.getId();
        if (id == R.id.float_anno_color){
            LogUtil.i(TAG,"userClick annotoobar color ");
            showColorPickWin();
        }else if(id == R.id.float_anno_empty){
            LogUtil.i(TAG,"userClick annotoobar empty ");
            MeetingMgr.getInstance().clearAnnotation(false);
        }else if(id == R.id.float_anno_erase){
            LogUtil.i(TAG,"userClick annotoobar erase ");
            if (selectedButton != mErase) {
                mErase.setSelected(true);
                mPen.setSelected(false);
                selectedButton = mErase;
                MeetingMgr.getInstance().eraseAnnotation(false);
            }
        }else if(id == R.id.float_anno_exit){
            LogUtil.i(TAG,"userClick annotoobar exit ");
            MeetingMgr.getInstance().setAnnotationLocalStatus(false);
            this.mCallBack.clickAnnon();
        }else if(id == R.id.float_anno_pen){
            LogUtil.i(TAG,"userClick annotoobar pen ");
            if (selectedButton != mPen) {
                mErase.setSelected(false);
                mPen.setSelected(true);
                selectedButton = mPen;
                if (currentPenColor == 0){
                    currentPenColor = AnnotationConstants.PEN_COLOR_RED;
                }
                int penWidth = (getResources().getDimensionPixelSize(R.dimen.dp_6) * 1440 / LayoutUtil.getScreenDensityDpi());
                MeetingMgr.getInstance().setAnnotationPen(currentPenColor, penWidth);

            }
        }
    }

    private void showColorPickWin() {
        if (mColorPopupWin ==null || mColor == null) {
            LogUtil.e(TAG,"showColorPickWin mColorPopupWin is null ");
            return;
        }

        int windowPos[] = new int[2];
        int anchorLoc[] = new int[2];
        mColor.getLocationOnScreen(anchorLoc);
        int anchorHeight = mColor.getHeight();
        int anchorWidth = mColor.getWidth();

        // 计算contentView的高宽
        final int windowHeight = mColorPopupWin.getHeight();
        final int windowWidth = mColorPopupWin.getWidth();
        // 判断需要向上弹出还是向下弹出显示
        final boolean isNeedShowUp = (anchorLoc[1] > windowHeight + LayoutUtil.getStatusBarHeight());
        windowPos[0] = anchorLoc[0] + (Math.abs(windowWidth - anchorWidth) / 2);
        if (isNeedShowUp) {
            windowPos[1] = anchorLoc[1] - windowHeight;
            mArrayDownView.setVisibility(View.VISIBLE);
            mArrayUpView.setVisibility(View.GONE);
        } else {
            windowPos[1] = anchorLoc[1] + anchorHeight;
            mArrayDownView.setVisibility(View.GONE);
            mArrayUpView.setVisibility(View.VISIBLE);
        }

        mColorPopupWin.showAtLocation(mColor, Gravity.TOP | Gravity.START, windowPos[0], windowPos[1]);
    }


    private void updateSelectColor(int color) {
        LogUtil.i(TAG,"userClick select color: " + color);
        if (mColor != null) {
            mColor.setIconBackgroundResource(annotationHelper.getAnnoColorImg(color));
        }
        currentPenColor = AnnotationConstants.PEN_COLOR_RED;
        int penWidth;
        HashMap<Integer,Integer> colorMap = annotationHelper.getColorMap();
        if (colorMap != null) {
            currentPenColor = colorMap.get(color);
        }
        // 选择颜色后，默认设置为笔
        if (mPen != null) {
            mPen.setIconBackgroundResource(annotationHelper.getAnnoPenImg(color));
            mPen.setSelected(true);
            selectedButton = mPen;
        }

        if (mErase != null) {
            mErase.setSelected(false);
        }
        penWidth = (getResources().getDimensionPixelSize(R.dimen.dp_6) * 1440 / LayoutUtil.getScreenDensityDpi());
        // 共享类型的切换
        MeetingMgr.getInstance().setAnnotationPen(currentPenColor, penWidth);
    }

    private void updatePenColor()
    {
        switch (MeetingMgr.getInstance().getCurrentPenColor())
        {
            case AnnotationConstants.PEN_COLOR_BLACK:
                mPen.setIconBackgroundResource(R.drawable.float_anno_pen_black);
                mColor.setIconBackgroundResource(R.drawable.float_anno_color_black);
                break;
            case AnnotationConstants.PEN_COLOR_RED:
                mPen.setIconBackgroundResource(R.drawable.float_anno_pen_red);
                mColor.setIconBackgroundResource(R.drawable.float_anno_color_red);
                break;
            case AnnotationConstants.PEN_COLOR_GREEN:
                mPen.setIconBackgroundResource(R.drawable.float_anno_pen_green);
                mColor.setIconBackgroundResource(R.drawable.float_anno_color_green);
                break;
            case AnnotationConstants.PEN_COLOR_BLUE:
                mPen.setIconBackgroundResource(R.drawable.float_anno_pen_blue);
                mColor.setIconBackgroundResource(R.drawable.float_anno_color_blue);
                break;
            default:
                mPen.setIconBackgroundResource(R.drawable.anno_pen_red_selector);
                mColor.setIconBackgroundResource(R.drawable.anno_color_red_selector);
                break;
        }
    }

    public void reset(boolean isNeedReset) {
        if (mPen != null && mColor != null) {
            if (isNeedReset) {
                updatePenColor();
            }
            mPen.setSelected(true);
        }

        if (mErase != null) {
            mErase.setSelected(false);
        }

        selectedButton = mPen;
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

    private class GuestureListener
            extends ToolbarScrollListener
    {
        float mLastRawX = -1.0F;
        float mLastRawY = -1.0F;

        public GuestureListener() {}



        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            int viewWidth = getWidth();
            int viewHeight = getHeight();

            View viewParent = (View) getParent();
            int parentWidth = viewParent.getWidth();
            int parentHeight = viewParent.getHeight();

            // 获取移动时的X，Y坐标
            float nowX, nowY, dX, dY;
            if (Math.abs(mLastRawX + 1) < 0.0000001 || Math.abs(mLastRawY + 1) < 0.0000001) {
                mLastRawX = e1.getRawX();
                mLastRawY = e1.getRawY();
            }
            dX = getX() - mLastRawX;
            dY = getY() - mLastRawY;

            nowX = e2.getRawX();
            nowY = e2.getRawY();
            float newX = e2.getRawX() + dX;
            newX = Math.max(0, newX);
            newX = Math.min(parentWidth - viewWidth, newX);

            float newY = e2.getRawY() + dY;
            newY = Math.max(mStatusBarHeight, newY);
            newY = Math.min(parentHeight - viewHeight, newY);

            move(newX, newY);

            //记录当前坐标作为下一次计算的上一次移动的位置坐标
            mLastRawX = nowX;
            mLastRawY = nowY;

            return true;
        }

        public void onTouchEventUp() {
            this.mLastRawX = -1.0F;
            this.mLastRawY = -1.0F;
        }
    }

    // 恢复到默认位置
    public void resetToolbarPosition() {
        if (mColorPopupWin != null && mColorPopupWin.isShowing()) {
            mColorPopupWin.dismiss();
        }
        View viewParent = (View) getParent();
        if (viewParent == null) {
            return;
        }
        int parentHeight = viewParent.getHeight();
        int parentWidth = viewParent.getWidth();
        int viewWidth = getWidth();
        int ViewHeight = getHeight();
        if (viewWidth == 0 || ViewHeight == 0) {
            viewWidth = getResources().getDimensionPixelSize(R.dimen.dp_340);
            ViewHeight = getResources().getDimensionPixelSize(R.dimen.dp_48);
        }
        float newX = (parentWidth - viewWidth) / (float)2;
        int bottom = getResources().getDimensionPixelSize(R.dimen.dp_30);

        float newY = parentHeight - ViewHeight - bottom;
        move(newX, newY);
    }

    private void move(float x, float y) {
        animate()
                .x(x)
                .y(y)
                .setDuration(0)
                .start();
        bringToFront();
    }

    /**
     * 点击键盘中标注按钮后的操作，用于接口回调
     */
    public void setOnClickAnnon(AnnoToolBar.ICallBack mCallBack){
        this.mCallBack = mCallBack;
    }

    public interface ICallBack{
        void clickAnnon();
    }

}
