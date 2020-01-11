package com.huawei.opensdk.ec_sdk_demo.ui.conference;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huawei.opensdk.commonservice.common.LocContext;
import com.huawei.opensdk.ec_sdk_demo.ECApplication;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.floatView.annotation.widget.AnnoToolBar;
import com.huawei.opensdk.ec_sdk_demo.floatView.annotation.widget.DragFloatActionButton;
import com.huawei.opensdk.ec_sdk_demo.floatView.screenShare.FloatWindowsManager;
import com.huawei.opensdk.ec_sdk_demo.logic.conference.mvp.DataConfPresenter;
import com.huawei.opensdk.ec_sdk_demo.logic.conference.mvp.IDataConfContract;
import com.huawei.opensdk.ec_sdk_demo.ui.base.MVPBaseActivity;
import com.huawei.opensdk.ec_sdk_demo.widget.BarrageAnimation;

import java.util.Timer;
import java.util.TimerTask;

/**
 * This class is about data conf Activity.
 */
public class DataConfActivity extends MVPBaseActivity<IDataConfContract.DataConfView, DataConfPresenter>
        implements IDataConfContract.DataConfView, View.OnClickListener
{

    private FrameLayout mConfShareLayout;
    private RelativeLayout mDataConfLayout;
    private FrameLayout mConfShareEmptyLayout;
    private ImageView mLeaveIV;
    private TextView mTitleTV;
    private ImageView mRightIV;
    private String mSubject;
    private String confID;
    private DataConfPresenter mPresenter;
    private FrameLayout mHideVideoView;
    private FrameLayout mLocalVideoView;
    private RelativeLayout mTitleBar;
    private LinearLayout mChatBottom;
    private EditText mChatMsg;
    private ImageView mChatSend;
    private RelativeLayout mBarrageLayout;
    private DragFloatActionButton mAnnoFloatButton;
    private AnnoToolBar mAnnoToolbar;
    private Handler handler = null;

    private boolean isVideo;
    private MyTimerTask myTimerTask;
    private Timer timer;

    /**
     * 是否第一次执行计时器
     */
    private boolean isFirstStart = true;
    /**
     * 是否触发触摸屏幕事件
     */
    private boolean isPressTouch = false;
    /**
     * 控件是否显示
     */
    private boolean isShowBar = false;

    /**
     * 是否正在共享
     */
    private boolean isStartShare = false;

    /**
     *是否允许标注
     */
    private boolean isAllowAnnot = false;

    /**
     *是否主动共享，若主动共享则隐藏标注笔
     */
    private boolean isActiveShare = false;

    private static final int STOP_SCREEN_SHARE = 222;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case STOP_SCREEN_SHARE:
                    if (mHandler != null){
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                FloatWindowsManager.getInstance().removeAllScreenShareFloatWindow(ECApplication.getApp());
                            }
                        });
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected IDataConfContract.DataConfView createView()
    {
        return this;
    }

    @Override
    protected DataConfPresenter createPresenter()
    {
        mPresenter = new DataConfPresenter();
        return mPresenter;
    }

    @Override
    public void initializeComposition()
    {
        // keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.data_conf_activity);

        // data layout
        mDataConfLayout = (RelativeLayout) findViewById(R.id.date_conf_rl);

        // data share layout
        mConfShareLayout = (FrameLayout) findViewById(R.id.conf_share_layout);

        // Data sharing has not started
        mConfShareEmptyLayout = (FrameLayout) findViewById(R.id.conf_share_empty);

        //标注笔，先隐藏，有标注能力再显示
        mAnnoFloatButton = (DragFloatActionButton)findViewById(R.id.anno_float_button);
        mAnnoToolbar = (AnnoToolBar)findViewById(R.id.anno_toolbar);
        mAnnoFloatButton.setOnClickAnnon(new DragFloatActionButton.ICallBack() {
            @Override
            public void clickAnnon() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setAnnotbtnVisibility(View.GONE);
                        if (mAnnoToolbar != null) {
                            mAnnoToolbar.reset(true);
                            resetToolbarPosition();
                            mAnnoToolbar.setVisibility(View.VISIBLE);
                        }

                        mPresenter.startAnnotation();
                        mPresenter.setAnnotationLocalStatus(true);

                    }
                });
            }
        });

        mAnnoToolbar.setOnClickAnnon(new AnnoToolBar.ICallBack() {
            @Override
            public void clickAnnon() {
                if (mAnnoToolbar != null) {
                    resetToolbarPosition();
                    mAnnoToolbar.setVisibility(View.GONE);
                }
                setAnnotbtnVisibility(View.VISIBLE);
            }
        });

        // 需要隐藏的标题栏
        mTitleBar = (RelativeLayout) findViewById(R.id.title_layout_transparent);
        mChatBottom = (LinearLayout) findViewById(R.id.chat_data_meeting_layout);

        // 采集视频
        mHideVideoView = (FrameLayout) findViewById(R.id.hide_video_view);
        mLocalVideoView = (FrameLayout) findViewById(R.id.local_video_view);

        // title
        mRightIV = (ImageView) findViewById(R.id.right_iv);
        mTitleTV = (TextView) findViewById(R.id.conf_title);
        mLeaveIV = (ImageView) findViewById(R.id.leave_iv);

        // chat
        mChatMsg = (EditText) findViewById(R.id.message_input_et);
        mChatSend = (ImageView) findViewById(R.id.chat_send_iv);

        // barrage display view
        mBarrageLayout = (RelativeLayout) findViewById(R.id.barrage_layout);

        mTitleTV.setText(mSubject);
        mRightIV.setVisibility(View.GONE);

        mConfShareLayout.setOnClickListener(this);
        mDataConfLayout.setOnClickListener(this);
        mLeaveIV.setOnClickListener(this);
        mChatSend.setOnClickListener(this);

        mPresenter.attachSurfaceView(mConfShareLayout, this);
        initHandler();

    }

    //回复标注原始位置
    private void setAnnotbtnVisibility(int visibility) {
        if (null == mAnnoFloatButton) {
            return;
        }
        mAnnoFloatButton.setVisibility(visibility);
        resetAnnotBtnPosition();
    }

    private void resetAnnotBtnPosition() {
        if (mHandler == null) {
            return;
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mAnnoFloatButton.resetAnnotBtnPosition();
            }
        }, 200);
    }

    private void initHandler() {
        try {
            if (handler == null) {
                handler = new Handler();
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void initializeData()
    {
        Intent intent = getIntent();
        confID = intent.getStringExtra(UIConstants.CONF_ID);
        isVideo = intent.getBooleanExtra(UIConstants.IS_VIDEO_CONF, false);
        isStartShare = intent.getBooleanExtra(UIConstants.IS_START_SHARE_CONF, false);
        isAllowAnnot = intent.getBooleanExtra(UIConstants.IS_ALLOW_ANNOT, false);
        isActiveShare = intent.getBooleanExtra(UIConstants.IS_ACTIVE_SHARE, false);
        if (confID == null)
        {
            showToast(R.string.empty_conf_id);
            return;
        }

        mPresenter.setConfID(confID);
        mSubject = mPresenter.getSubject();
    }

    @Override
    public void showLoading() {

    }

    @Override
    protected void onBack() {
        super.onBack();
        if (isAllowAnnot){
            mHandler.sendEmptyMessage(STOP_SCREEN_SHARE);
        }
    }

    @Override
    public void finishActivity() {
        if (isAllowAnnot){
            mHandler.sendEmptyMessage(STOP_SCREEN_SHARE);
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (null != mBarrageLayout)
                {
                    mBarrageLayout.removeAllViews();
                }
                finish();
            }
        });
    }

    @Override
    public void dataConfActivityShare(final boolean isShare,final boolean isAllowAnnot) {
        this.isAllowAnnot = isAllowAnnot;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isShare)
                {
                    mConfShareLayout.setVisibility(View.VISIBLE);
                    mConfShareEmptyLayout.setVisibility(View.GONE);
                }
                else
                {
                    mConfShareLayout.setVisibility(View.GONE);
                    mConfShareEmptyLayout.setVisibility(View.VISIBLE);
                }
                if(!isActiveShare){
                    if (isAllowAnnot){
                        setAnnotbtnVisibility(View.VISIBLE);
                        mAnnoToolbar.setVisibility(View.GONE);
                    }else {
                        setAnnotbtnVisibility(View.GONE);
                        mAnnoToolbar.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    @Override
    public void displayConfChatMag(final boolean isSelf, final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView tvMsg = new TextView(LocContext.getContext());
                if (isSelf)
                {
                    tvMsg.setTextColor(Color.GREEN);
                }
                else
                {
                    tvMsg.setTextColor(Color.BLACK);
                }
                tvMsg.setText(msg);
                tvMsg.setMaxEms(12);
                tvMsg.setTextSize(17);
                tvMsg.setBackgroundResource(R.drawable.conf_msg_bg_normal);
                mBarrageLayout.addView(tvMsg);
                tvMsg.measure(0, 0);
                int width = tvMsg.getMeasuredWidth();
                int height = tvMsg.getMeasuredHeight();
                new BarrageAnimation(tvMsg, mBarrageLayout, width, height);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.registerBroadcast();

        if (isStartShare)
        {
            mConfShareLayout.setVisibility(View.VISIBLE);
            mConfShareEmptyLayout.setVisibility(View.GONE);
        }
        else
        {
            mConfShareLayout.setVisibility(View.GONE);
            mConfShareEmptyLayout.setVisibility(View.VISIBLE);
        }

        if (isVideo)
        {
            mPresenter.setVideoContainer(this, mLocalVideoView, mHideVideoView);
        }
        if (!isActiveShare){
            if (isAllowAnnot){
                setAnnotbtnVisibility(View.VISIBLE);

            }
        }

        // 第一次启动界面让所有按钮显示5s
        if (isFirstStart)
        {
            startTimer();
        }
    }

    /**
     * 这个方法的作用是把触摸事件的分发方法，其返回值代表触摸事件是否被当前 View 处理完成(true/false)。
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mConfShareLayout.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.unregisterBroadcast();
        stopTimer();
        if (null != mBarrageLayout)
        {
            mBarrageLayout.removeAllViews();
        }
    }

    @Override
    public void dismissLoading() {

    }

    @Override
    public void showCustomToast(final int resID)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                showToast(resID);
            }
        });
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.conf_share_layout:
            case R.id.date_conf_rl:
                // 按钮显示，不执行
                if (isFirstStart)
                {
                    return;
                }
                // 点击动作太多，不执行
                if (isPressTouch)
                {
                    return;
                }
                else
                {
                    isPressTouch = true;
                    startTimer();
                }
                break;

            case R.id.chat_send_iv:
                if (null == mChatMsg.getText().toString().trim() || "".equals(mChatMsg.getText().toString().trim()))
                {
                    return;
                }
                mPresenter.sendChatMsg(mChatMsg.getText().toString());
                mChatMsg.setText("");
                break;

            case R.id.leave_iv:
                finish();
                break;

            default:
                break;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        resetToolbarPosition();
    }

    private void resetToolbarPosition() {
        if (mAnnoToolbar != null) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mAnnoToolbar.resetToolbarPosition();
                }
            }, 200);
        }
    }

    /**
     * Show title bar and bottom controls.
     */
    private void showBar()
    {
        if(isAllowAnnot){
            return;
        }
        if (mTitleBar.getVisibility() == View.GONE || mChatBottom.getVisibility() == View.GONE)
        {
            mTitleBar.setVisibility(View.VISIBLE);
            mChatBottom.setVisibility(View.VISIBLE);
            isShowBar = true;
        }
    }

    /**
     * Hide title bar and bottom controls.
     */
    private void hideBar()
    {
        if (mTitleBar.getVisibility() == View.VISIBLE || mChatBottom.getVisibility() == View.VISIBLE)
        {
            mTitleBar.setVisibility(View.GONE);
            mChatBottom.setVisibility(View.GONE);
            isShowBar = false;
        }
    }

    private void initTimer()
    {
        timer = new Timer();
        myTimerTask = new MyTimerTask();
    }

    /**
     * Start timer
     */
    private void startTimer()
    {
        initTimer();
        try
        {
            // 第一次进入界面执行计时器 5s后控件消失；
            // 非第一次执行计时器，0.2s后控件显示再过5s后控件消失
            if (isFirstStart)
            {
                timer.schedule(myTimerTask, 5000);
            }
            else
            {
                timer.schedule(myTimerTask, 200, 5000);
            }
        }
        catch (IllegalStateException e)
        {
            e.printStackTrace();
            initTimer();
            timer.schedule(myTimerTask, 5000);
        }
    }

    /**
     * Stop timer
     */
    private void stopTimer()
    {
        if (null != timer)
        {
            timer.cancel();
            timer = null;
        }
    }

    class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // 第一次启动界面时启动计时
                    if (isFirstStart)
                    {
                        hideBar();
                        isFirstStart = false;
                        stopTimer();
                    }
                    else
                    {
                        if (isShowBar)
                        {
                            // 停止计时器，计时器任务执行完成之后执行stop
                            hideBar();
                            isPressTouch = false;
                            stopTimer();
                        }
                        else
                        {
                            // 启动计时器，触发屏幕时先显示按钮
                            showBar();
                        }
                    }
                }
            });
        }
    }
}
