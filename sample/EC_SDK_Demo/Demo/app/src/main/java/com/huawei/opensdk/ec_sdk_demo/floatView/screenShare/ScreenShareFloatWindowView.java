package com.huawei.opensdk.ec_sdk_demo.floatView.screenShare;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.demoservice.MeetingMgr;
import com.huawei.opensdk.ec_sdk_demo.ECApplication;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.floatView.util.DeviceUtil;
import com.huawei.opensdk.ec_sdk_demo.floatView.util.LayoutUtil;


public class ScreenShareFloatWindowView extends LinearLayout implements View.OnTouchListener {
    private static int mStatusBarHeight;
    public int mViewWidth;
    public int mViewHeight;
    private WindowManager mWindowManager;

    private WindowManager.LayoutParams mParams;

    private float xInScreen;

    private float yInScreen;

    private float xDownInScreen;

    private float yDownInScreen;

    private float xInView;

    private float yInView;

    private View mScreenFloatLayout;

    private RelativeLayout mStartAnnotBtn;

    private RelativeLayout mStopScreenShareBtn;

    private int mClickId = -1;

    private static final int ACTIVE_START_ANNON = 999;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case ACTIVE_START_ANNON:
                    if (mHandler != null) {
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                MeetingMgr.getInstance().startAnnotation();
                                MeetingMgr.getInstance().setAnnotationLocalStatus(true);
                                FloatWindowsManager.getInstance().showAnnotationToolbar();
                            }
                        }, 1000);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public ScreenShareFloatWindowView(final Context context) {
        super(context);
        LogUtil.i(UIConstants.DEMO_TAG,"enter ScreenShareFloatWindowView create " + this);
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mScreenFloatLayout = LayoutInflater.from(context).inflate(R.layout.float_layout, this);
        View view = (RelativeLayout) mScreenFloatLayout.findViewById(R.id.flow_window_layout);
        mViewWidth = view.getLayoutParams().width;
        mViewHeight = view.getLayoutParams().height;
        mStartAnnotBtn = (RelativeLayout) mScreenFloatLayout.findViewById(R.id.start_annot_layout);
        mStopScreenShareBtn = (RelativeLayout) mScreenFloatLayout.findViewById(R.id.stop_share_layout);
        mScreenFloatLayout.setOnTouchListener(this);
        mStartAnnotBtn.setOnTouchListener(this);
        mStopScreenShareBtn.setOnTouchListener(this);
    }

    public void setParams(WindowManager.LayoutParams params) {
        mParams = params;
    }

    private void updateViewPosition() {
        mParams.x = (int) (xInScreen - xInView);
        mParams.y = (int) (yInScreen - yInView);
        mWindowManager.updateViewLayout(this, mParams);
    }


    private int getStatusBarHeight() {
        if (mStatusBarHeight == 0) {
            mStatusBarHeight = LayoutUtil.getStatusBarHeight();
        }
        return mStatusBarHeight;
    }

    public int getmViewWidth() {
        return mViewWidth;
    }

    public int getmViewHeight() {
        return mViewHeight;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int id = v.getId();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (id == R.id.stop_share_layout || id == R.id.start_annot_layout) {
                    mClickId = id;
                }
                xInView = event.getX();
                yInView = event.getY();
                xDownInScreen = event.getRawX();
                yDownInScreen = event.getRawY() - getStatusBarHeight();
                xInScreen = event.getRawX();
                yInScreen = event.getRawY() - getStatusBarHeight();
                break;
            case MotionEvent.ACTION_MOVE:
                xInScreen = event.getRawX();
                yInScreen = event.getRawY() - getStatusBarHeight();
                if (Math.abs(xDownInScreen - xInScreen) >= 10 || Math.abs(yDownInScreen - yInScreen) >= 10) {
                    updateViewPosition();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (Math.abs(xDownInScreen - xInScreen) < 10 && Math.abs(yDownInScreen - yInScreen) < 10) {
                    if (mClickId == R.id.stop_share_layout) {
                        LogUtil.i(UIConstants.DEMO_TAG,"userClick stop share btn ");
                        handleStopScreenShare();
                    } else if (mClickId == R.id.start_annot_layout) {
                        LogUtil.i(UIConstants.DEMO_TAG,"userClick start annotation btn ");
                        handleStartAnnotation();
                    } else {
                        mClickId = -1;
                    }
                } else {
                    mClickId = -1;
                }
                break;

            default:
                break;
        }
        return false;
    }

    private void handleStopScreenShare() {
        LogUtil.i(UIConstants.DEMO_TAG,"enter handleStopScreenShare");
        // 在后台先将app拉到前台
        if (!DeviceUtil.isAppForeground()) {
            DeviceUtil.bringTaskBackToFront();
        }
        MeetingMgr.getInstance().stopScreenShare();
    }

    private void handleStartAnnotation() {
        LogUtil.i(UIConstants.DEMO_TAG,"enter handleStartAnnotation");
        FloatWindowsManager.getInstance().createAnnotToolbarManager();
        FloatWindowsManager.getInstance().removeScreenShareFloatWindow(ECApplication.getApp());
        // TODO  在这里 延时1s 调用开启标注的接口 并显示悬浮工具条
        mHandler.sendEmptyMessage(ACTIVE_START_ANNON);
//        LogUI.i("enter handleStartAnnotation");
//        if (isFastClick()) {
//            return;
//        }
//
//        FloatWindowsManager.getInstance().createAnnotToolbarManager();
//        FloatWindowsManager.getInstance().removeScreenShareFloatWindow(ApplicationManager.getApp());
//        WeLinkAppContext.getInstance().getWebViewService().noticeDataChange(ObserverConts.AS_START_ANNOTATION,null);
    }

}
