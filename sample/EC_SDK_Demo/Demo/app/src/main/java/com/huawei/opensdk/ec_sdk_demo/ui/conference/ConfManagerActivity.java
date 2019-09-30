package com.huawei.opensdk.ec_sdk_demo.ui.conference;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.projection.MediaProjectionManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.ecterminalsdk.base.TsdkConfAsActionType;
import com.huawei.opensdk.callmgr.CallConstant;
import com.huawei.opensdk.callmgr.CallInfo;
import com.huawei.opensdk.callmgr.CallMgr;
import com.huawei.opensdk.commonservice.common.LocContext;
import com.huawei.opensdk.commonservice.util.DeviceManager;
import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.demoservice.ConfBaseInfo;
import com.huawei.opensdk.demoservice.ConfConstant;
import com.huawei.opensdk.demoservice.MeetingMgr;
import com.huawei.opensdk.demoservice.Member;
import com.huawei.opensdk.ec_sdk_demo.ECApplication;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.adapter.PopupConfListAdapter;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.floatView.screenShare.FloatWindowsManager;
import com.huawei.opensdk.ec_sdk_demo.floatView.util.DeviceUtil;
import com.huawei.opensdk.ec_sdk_demo.logic.conference.mvp.ConfManagerBasePresenter;
import com.huawei.opensdk.ec_sdk_demo.logic.conference.mvp.ConfManagerPresenter;
import com.huawei.opensdk.ec_sdk_demo.logic.conference.mvp.IConfManagerContract;
import com.huawei.opensdk.ec_sdk_demo.ui.IntentConstant;
import com.huawei.opensdk.ec_sdk_demo.ui.base.ActivityStack;
import com.huawei.opensdk.ec_sdk_demo.ui.base.MVPBaseActivity;
import com.huawei.opensdk.ec_sdk_demo.ui.base.NetworkConnectivityListener;
import com.huawei.opensdk.ec_sdk_demo.ui.base.SignalInfomationActivity;
import com.huawei.opensdk.ec_sdk_demo.util.ActivityUtil;
import com.huawei.opensdk.ec_sdk_demo.util.CommonUtil;
import com.huawei.opensdk.ec_sdk_demo.util.DisplayUtils;
import com.huawei.opensdk.ec_sdk_demo.util.PopupWindowUtil;
import com.huawei.opensdk.ec_sdk_demo.widget.ConfirmDialog;
import com.huawei.opensdk.ec_sdk_demo.widget.EditDialog;
import com.huawei.opensdk.ec_sdk_demo.widget.SimpleListDialog;
import com.huawei.opensdk.ec_sdk_demo.widget.ThreeInputDialog;
import com.huawei.opensdk.ec_sdk_demo.widget.TripleDialog;
import com.huawei.opensdk.loginmgr.LoginConstant;
import com.huawei.opensdk.loginmgr.LoginMgr;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.huawei.ecterminalsdk.base.TsdkConfEnvType.TSDK_E_CONF_ENV_HOSTED_CONVERGENT_CONFERENCE;

public class ConfManagerActivity extends MVPBaseActivity<IConfManagerContract.ConfManagerView, ConfManagerBasePresenter>
        implements IConfManagerContract.ConfManagerView, View.OnClickListener,NetworkConnectivityListener.OnNetWorkListener
{
    private ConfManagerPresenter mPresenter;
    private RelativeLayout mVideoConfLayout;
    private RelativeLayout mTitleLayout;
    private LinearLayout mConfMediaLayout;
    private FrameLayout mConfRemoteBigVideoLayout;
    private FrameLayout mConfLocalVideoLayout;
    private FrameLayout mConfRemoteSmallVideoLayout_01;
    private FrameLayout mConfRemoteSmallVideoLayout_02;
    private FrameLayout mConfRemoteSmallVideoLayout_03;
    private FrameLayout mHideVideoLayout;
    private ImageView mRecordPoint;
    private TextView mConfRemoteBigVideoText;
    private TextView mConfLocalVideoText;
    private TextView mConfRemoteSmallVideoText_01;
    private TextView mConfRemoteSmallVideoText_02;
    private TextView mConfRemoteSmallVideoText_03;

    private ImageView mLeaveIV;
    private TextView mTitleTV;
    private ImageView mRightIV;
    private ImageView mShareIV;
    private FrameLayout mConfHangup;
    private FrameLayout mConfMute;
    private FrameLayout mConfSpeaker;
    private FrameLayout mConfAddAttendee;
    private FrameLayout mConfShare;
    private FrameLayout mConfAttendee;
    private PopupWindow mPopupWindow;
    private ListView mConfMemberListView;
    private PopupConfListAdapter mAdapter;
    private FrameLayout mConfMore;
    private ImageView cameraStatusIV;
    private TextView cameraStatusTV;
    private RelativeLayout mAudioConfLayout;
    private TextView mAudioConfAttendeeTV;
    private LinearLayout mConfSmallVideoWndLL;
    private ImageView mConfVideoBackIV;
    private ImageView mConfVideoForwardIV;
    private ImageView mSignalView;
    private ImageView mAudioSignalView;

    private String confID;
    private CallInfo mCallInfo;
    private boolean isCameraClose = false;
    private boolean isVideo = false;
    private boolean isSvcConf = false;
    private boolean isDateConf = false;
    private List<Object> items = new ArrayList<>();
    private int mOrientation = 1;

    private MyTimerTask myTimerTask;
    private Timer timer;
    private boolean isFirstStart = true;
    private boolean isPressTouch = false;
    private boolean isShowBar = false;

    private boolean isStartShare = false;
    private boolean isAllowAnnot = false;

    private boolean isActiveOpenCamera = true;
    private boolean isActiveShare = false;

    private int mScreenWidth;
    private int mScreenHeight;
    private boolean isHideVideoWindow = false;
    private boolean isOnlyLocal = true;
    private boolean isSetOnlyLocalWind = false;
    private String mCurrentActivity = ConfManagerActivity.class.getSimpleName();
    private NetworkConnectivityListener networkConnectivityListener = new NetworkConnectivityListener();

    private static final int START_SCREEN_SHARE_HANDLE = 666;
    private static final int STOP_SCREEN_SHARE_HANDLE = 888;
    private static final int ROB_STOP_SCREEN_SHARE_HANDLE = 222;
    private static final int REQUEST_SCREEN_SHARE_HANDLE = 444;
    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case START_SCREEN_SHARE_HANDLE:
                    isAllowAnnot = true;
                    FloatWindowsManager.getInstance().createScreenShareFloatWindow(ECApplication.getApp());
                    DeviceUtil.jumpToHomeScreen();
                    break;
                case STOP_SCREEN_SHARE_HANDLE:
                    FloatWindowsManager.getInstance().removeAllScreenShareFloatWindow(ECApplication.getApp());
                    break;
                case ROB_STOP_SCREEN_SHARE_HANDLE:
                    // 在后台先将app拉到前台
                    if (!DeviceUtil.isAppForeground()) {
                        DeviceUtil.bringTaskBackToFront();
                    }
                    FloatWindowsManager.getInstance().removeAllScreenShareFloatWindow(ECApplication.getApp());
                    break;
                case REQUEST_SCREEN_SHARE_HANDLE:
                    if (!FloatWindowsManager.getInstance().checkPermission(ConfManagerActivity.this)) {
                        FloatWindowsManager.getInstance().applyPermission(ConfManagerActivity.this);
                    }
                    showRequestScreenDialog();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected IConfManagerContract.ConfManagerView createView()
    {
        return this;
    }

    @Override
    protected ConfManagerBasePresenter createPresenter()
    {
        mPresenter = new ConfManagerPresenter();
        return mPresenter;
    }

    @Override
    public void initializeComposition()
    {
        setContentView(R.layout.conf_manager_activity);
        mVideoConfLayout = (RelativeLayout) findViewById(R.id.conference_video_layout);
        mTitleLayout = (RelativeLayout) findViewById(R.id.title_layout_transparent);
        mConfMediaLayout = (LinearLayout) findViewById(R.id.media_btn_group);
        mRecordPoint = (ImageView) findViewById(R.id.record_view);

        //title
        mLeaveIV = (ImageView) findViewById(R.id.leave_iv);
        mTitleTV = (TextView) findViewById(R.id.conf_title);
        mRightIV = (ImageView) findViewById(R.id.right_iv);
        mShareIV = (ImageView) findViewById(R.id.share_iv);

        //main tab
        mConfHangup = (FrameLayout) findViewById(R.id.conf_hangup);
        mConfMute = (FrameLayout) findViewById(R.id.conf_mute);
        mConfSpeaker = (FrameLayout) findViewById(R.id.conf_loud_speaker);
        mConfAddAttendee = (FrameLayout) findViewById(R.id.conf_add_attendee);
        mConfAttendee = (FrameLayout) findViewById(R.id.conf_attendee);
        mConfMore = (FrameLayout) findViewById(R.id.btn_conf_more);
        mConfShare = (FrameLayout) findViewById(R.id.conf_share);

        // 在与会者列表上报之前会控按钮全部屏蔽
        hideConfCtrlButton();

        if (isVideo)
        {
            //video layout
            mVideoConfLayout.setVisibility(View.VISIBLE);
            mHideVideoLayout = (FrameLayout) findViewById(R.id.hide_video_view);

            mConfSmallVideoWndLL = (LinearLayout) findViewById(R.id.conf_video_ll);
            mConfRemoteBigVideoLayout = (FrameLayout) findViewById(R.id.conf_remote_big_video_layout);
            mConfLocalVideoLayout = (FrameLayout) findViewById(R.id.conf_local_video_layout);
            mConfRemoteSmallVideoLayout_01 = (FrameLayout) findViewById(R.id.conf_remote_small_video_layout_01);
            mConfRemoteSmallVideoLayout_02 = (FrameLayout) findViewById(R.id.conf_remote_small_video_layout_02);
            mConfRemoteSmallVideoLayout_03 = (FrameLayout) findViewById(R.id.conf_remote_small_video_layout_03);

            mConfRemoteBigVideoText = (TextView) findViewById(R.id.conf_remote_big_video_text);
            mConfLocalVideoText = (TextView) findViewById(R.id.conf_local_video_text);
            mConfRemoteSmallVideoText_01 = (TextView) findViewById(R.id.conf_remote_small_video_text_01);
            mConfRemoteSmallVideoText_02 = (TextView) findViewById(R.id.conf_remote_small_video_text_02);
            mConfRemoteSmallVideoText_03 = (TextView) findViewById(R.id.conf_remote_small_video_text_03);

            mConfVideoBackIV = (ImageView) findViewById(R.id.watch_previous_page);
            mConfVideoForwardIV = (ImageView) findViewById(R.id.watch_next_page);

            mSignalView = (ImageView) findViewById(R.id.signal_view);
            mSignalView.setVisibility(View.VISIBLE);


            if (!isSvcConf)
            {
                mConfVideoBackIV.setVisibility(View.INVISIBLE);
                mConfVideoForwardIV.setVisibility(View.INVISIBLE);
                mConfRemoteBigVideoText.setVisibility(View.INVISIBLE);
                mConfLocalVideoText.setVisibility(View.INVISIBLE);
            }

            //title
            mRightIV.setVisibility(View.VISIBLE);

            mConfLocalVideoLayout.setVisibility(View.GONE);
            mConfRemoteSmallVideoLayout_01.setVisibility(View.GONE);
            mConfRemoteSmallVideoLayout_02.setVisibility(View.GONE);
            mConfRemoteSmallVideoLayout_03.setVisibility(View.GONE);
            mConfRemoteBigVideoText.setVisibility(View.GONE);
            mConfLocalVideoText.setVisibility(View.GONE);
            mConfRemoteSmallVideoText_01.setVisibility(View.GONE);
            mConfRemoteSmallVideoText_02.setVisibility(View.GONE);
            mConfRemoteSmallVideoText_03.setVisibility(View.GONE);

            mRightIV.setOnClickListener(this);
            mConfVideoBackIV.setOnClickListener(this);
            mConfVideoForwardIV.setOnClickListener(this);
            mSignalView.setOnClickListener(this);
        }
        else
        {
            mAudioConfAttendeeTV = (TextView) findViewById(R.id.tv_audio_conf_attendee);
            mAudioConfLayout = (RelativeLayout) findViewById(R.id.audio_conf_layout_logo);
            mAudioSignalView = (ImageView) findViewById(R.id.audio_signal_view);
            mAudioSignalView.setVisibility(View.VISIBLE);

            mAudioConfAttendeeTV.setSelected(true);

            mAudioConfLayout.setVisibility(View.VISIBLE);
            mVideoConfLayout.setVisibility(View.INVISIBLE);

            //title
            mRightIV.setVisibility(View.GONE);

            mAudioSignalView.setOnClickListener(this);
        }

        mVideoConfLayout.setOnClickListener(this);
        mConfHangup.setOnClickListener(this);
        mConfMute.setOnClickListener(this);
        mConfSpeaker.setOnClickListener(this);
        mConfAddAttendee.setOnClickListener(this);
        mConfShare.setOnClickListener(this);
        mConfAttendee.setOnClickListener(this);
        mConfMore.setOnClickListener(this);
        mLeaveIV.setOnClickListener(this);
        mShareIV.setOnClickListener(this);
    }

    private void hideConfCtrlButton()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConfMute.setVisibility(View.GONE);
                mConfAddAttendee.setVisibility(View.GONE);
                mConfShare.setVisibility(View.GONE);
                mConfAttendee.setVisibility(View.GONE);
                mConfMore.setVisibility(View.GONE);
                mShareIV.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mPresenter.registerBroadcast();
        networkConnectivityListener.registerListener(this);
        networkConnectivityListener.startListening(this);

        // 刷新当前扬声器状态
        updateLoudSpeakerButton(CallMgr.getInstance().getCurrentAudioRoute());

        if (!isVideo)
        {
            return;
        }

        if (isFirstStart)
        {
            startTimer();
        }

        if (mOrientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            mConfSmallVideoWndLL.setOrientation(LinearLayout.VERTICAL);
            setConfVideoSize(false);
        }
        else
        {
            mConfSmallVideoWndLL.setOrientation(LinearLayout.HORIZONTAL);
            setConfVideoSize(true);
        }

        if (isSvcConf) {
            if (isOnlyLocal)
            {
                mPresenter.setOnlyLocalVideoContainer(this, mConfRemoteBigVideoLayout, mHideVideoLayout);
                mConfSmallVideoWndLL.setVisibility(View.GONE);
                isSetOnlyLocalWind = true;
            }
            else
            {
                mPresenter.setSvcAllVideoContainer(this, mConfLocalVideoLayout, mConfRemoteBigVideoLayout, mHideVideoLayout,
                        mConfRemoteSmallVideoLayout_01, mConfRemoteSmallVideoLayout_02, mConfRemoteSmallVideoLayout_03);
                mConfSmallVideoWndLL.setVisibility(View.VISIBLE);
                isSetOnlyLocalWind = false;
            }
        } else {
            // AVC会议保持原逻辑不变
            mPresenter.setAvcVideoContainer(this, mConfLocalVideoLayout, mConfRemoteBigVideoLayout, mHideVideoLayout);
        }

        mPresenter.setAutoRotation(this, true, mOrientation);
    }

    private void setConfVideoSize(boolean isVertical)
    {
        if (isVertical)
        {
            mConfVideoBackIV.setRotation(0);
            mConfVideoForwardIV.setRotation(0);
            mConfLocalVideoLayout.getLayoutParams().width = mScreenWidth / 4;
            mConfLocalVideoLayout.getLayoutParams().height = mScreenHeight / 4;
            mConfRemoteSmallVideoLayout_01.getLayoutParams().width = mScreenWidth / 4;
            mConfRemoteSmallVideoLayout_01.getLayoutParams().height = mScreenHeight / 4;
            mConfRemoteSmallVideoLayout_02.getLayoutParams().width = mScreenWidth / 4;
            mConfRemoteSmallVideoLayout_02.getLayoutParams().height = mScreenHeight / 4;
            mConfRemoteSmallVideoLayout_03.getLayoutParams().width = mScreenWidth / 4;
            mConfRemoteSmallVideoLayout_03.getLayoutParams().height = mScreenHeight / 4;
        }
        else
        {
            mConfVideoBackIV.setRotation(90);
            mConfVideoForwardIV.setRotation(90);
            mConfLocalVideoLayout.getLayoutParams().width = mScreenHeight / 4;
            mConfLocalVideoLayout.getLayoutParams().height = mScreenWidth / 4;
            mConfRemoteSmallVideoLayout_01.getLayoutParams().width = mScreenHeight / 4;
            mConfRemoteSmallVideoLayout_01.getLayoutParams().height = mScreenWidth / 4;
            mConfRemoteSmallVideoLayout_02.getLayoutParams().width = mScreenHeight / 4;
            mConfRemoteSmallVideoLayout_02.getLayoutParams().height = mScreenWidth / 4;
            mConfRemoteSmallVideoLayout_03.getLayoutParams().width = mScreenHeight / 4;
            mConfRemoteSmallVideoLayout_03.getLayoutParams().height = mScreenWidth / 4;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isVideo)
        {
            return;
        }
        if (isActiveOpenCamera){
            mPresenter.closeOrOpenLocalVideo(false);
        }else {
            mPresenter.closeOrOpenLocalVideo(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isVideo)
        {
            if (!DeviceUtil.isAppForeground()) {
                mPresenter.closeOrOpenLocalVideo(true);
            }
        }
        stopTimer();
        isFirstStart = true;
        isPressTouch = false;
        isShowBar = false;
    }

    @Override
    public void initializeData()
    {
        Intent intent = getIntent();
        confID = intent.getStringExtra(UIConstants.CONF_ID);
        isVideo = intent.getBooleanExtra(UIConstants.IS_VIDEO_CONF, false);
        isSvcConf = intent.getBooleanExtra(UIConstants.IS_SVC_VIDEO_CONF, false);
        mCallInfo = (CallInfo) intent.getSerializableExtra(UIConstants.CALL_INFO);
        if (confID == null)
        {
            showToast(R.string.empty_conf_id);
            return;
        }

        mPresenter.setConfID(confID);
        mAdapter = new PopupConfListAdapter(this);

        if (!isVideo)
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
            return;
        }
        if (ConfConstant.ConfProtocol.IDO_PROTOCOL == MeetingMgr.getInstance().getConfProtocol())
        {
            items.add(getString(R.string.voice_control_mode));
        }
        else
        {
            items.add(getString(R.string.broadcast_mode));
            items.add(getString(R.string.voice_control_mode));
            items.add(getString(R.string.free_mode));
        }

        // 获取屏幕方向
        Configuration configuration = this.getResources().getConfiguration();
        mOrientation = configuration.orientation;

        // 获取屏幕的宽和高
        int px = DisplayUtils.dp2px(this, 40);
        if (mOrientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            mScreenWidth = DisplayUtils.getScreenHeightPixels(this) - px;
            mScreenHeight = DisplayUtils.getScreenWidthPixels(this) - px;
        }
        else
        {
            mScreenWidth = DisplayUtils.getScreenWidthPixels(this) - px;
            mScreenHeight = DisplayUtils.getScreenHeightPixels(this) - px;
        }
    }

    @Override
    public void finishActivity()
    {
        if (isAllowAnnot){
            if (!DeviceUtil.isAppForeground()) {
                DeviceUtil.bringTaskBackToFront();
            }
            mHandler.sendEmptyMessage(STOP_SCREEN_SHARE_HANDLE);
        }
        stopTimer();
        finish();
    }

    @Override
    public void showLoading() {

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
            case R.id.conference_video_layout:
                if (!isVideo)
                {
                    return;
                }
                if (isFirstStart)
                {
                    return;
                }
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
            case R.id.conf_hangup:
                LogUtil.i(UIConstants.DEMO_TAG, "conference hangup!");
                if (!mPresenter.isChairMan())
                {
                    showLeaveConfDialog();
                }
                else
                {
                    showEndConfDialog();
                }
                break;
            case R.id.conf_mute:
                LogUtil.i(UIConstants.DEMO_TAG, "conference mute!");
                mPresenter.muteSelf();
                break;
            case R.id.conf_loud_speaker:
                LogUtil.i(UIConstants.DEMO_TAG, "conference speaker!");
                updateLoudSpeakerButton(mPresenter.switchLoudSpeaker());
                break;
            case R.id.right_iv:
                final List<Member> memberList = mPresenter.getWatchMemberList();
                if (null == memberList || memberList.size()<=0)
                {
                    return;
                }
                final View popupView = getLayoutInflater().inflate(R.layout.popup_conf_list, null);
                mConfMemberListView = (ListView) popupView.findViewById(R.id.popup_conf_member_list);

                View headView = getLayoutInflater().inflate(R.layout.popup_video_conf_list_item, null);
                final TextView tvDisplayName = (TextView) headView.findViewById(R.id.name_tv);
                ImageView isMainHall = (ImageView) headView.findViewById(R.id.host_logo);
                tvDisplayName.setText(LocContext.getString(R.string.main_conference));
                isMainHall.setImageResource(R.drawable.group_detail_group_icon);
                mConfMemberListView.addHeaderView(headView);

                mAdapter.setData(memberList);
                mConfMemberListView.setAdapter(mAdapter);
                mPopupWindow = PopupWindowUtil.getInstance().generatePopupWindow(popupView);
                mPopupWindow.showAsDropDown(findViewById(R.id.right_iv));
                mConfMemberListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    {
                        Member conferenceMemberEntity = new Member();
                        if (0 == position)
                        {
                            conferenceMemberEntity.setDisplayName(tvDisplayName.getText().toString());
                            conferenceMemberEntity.setNumber("");
                        }
                        else
                        {
                            conferenceMemberEntity = memberList.get(position - 1);
                        }

                        if (null != conferenceMemberEntity)
                        {
                            mPresenter.watchAttendee(conferenceMemberEntity);
                        }
                        mPopupWindow.dismiss();
                    }
                });
                break;
            case R.id.conf_add_attendee:
                showAddMemberDialog();
                break;
            case R.id.conf_attendee:
                Intent intent = new Intent(IntentConstant.CONF_MEMBER_LIST_ACTIVITY_ACTION);
                intent.putExtra(UIConstants.CONF_ID, confID);
                intent.putExtra(UIConstants.IS_VIDEO_CONF, isVideo);
                intent.putExtra(UIConstants.IS_DATE_CONF, isDateConf);
                ActivityUtil.startActivity(this, intent);
                break;
            case R.id.conf_share:
                if (FloatWindowsManager.getInstance().checkPermission(this)) {
                    requestScreenSharePermission();
                } else {
                    FloatWindowsManager.getInstance().applyPermission(this);
                }
                break;
            case R.id.btn_conf_more:
                showMoreConfCtrl();
                break;
            case R.id.leave_iv:
                if (!mPresenter.isChairMan())
                {
                    showLeaveConfDialog();
                }
                else
                {
                    showEndConfDialog();
                }
                break;
            case R.id.share_iv:
                Intent shareIntent = new Intent(IntentConstant.CONF_DATA_ACTIVITY_ACTION);
                shareIntent.putExtra(UIConstants.CONF_ID, this.confID);
                shareIntent.putExtra(UIConstants.IS_VIDEO_CONF, this.isVideo);
                shareIntent.putExtra(UIConstants.IS_START_SHARE_CONF, this.isStartShare);
                shareIntent.putExtra(UIConstants.IS_ALLOW_ANNOT, this.isAllowAnnot);
                shareIntent.putExtra(UIConstants.IS_ACTIVE_SHARE, this.isActiveShare);
                ActivityUtil.startActivity(this, shareIntent);
                break;
			case R.id.watch_next_page:
                turnPage(false);
                break;
            case R.id.watch_previous_page:
                turnPage(true);
                break;	
            case R.id.signal_view:
                Intent signalIntent = new Intent(ConfManagerActivity.this, SignalInfomationActivity.class);
                signalIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                signalIntent.addCategory(IntentConstant.DEFAULT_CATEGORY);
                signalIntent.putExtra(UIConstants.CALL_INFO, mCallInfo);
                startActivity(signalIntent);
                break;
            case R.id.audio_signal_view:
                Intent audioSignalIntent = new Intent(ConfManagerActivity.this, SignalInfomationActivity.class);
                audioSignalIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                audioSignalIntent.addCategory(IntentConstant.DEFAULT_CATEGORY);
                audioSignalIntent.putExtra(UIConstants.CALL_INFO, mCallInfo);
                startActivity(audioSignalIntent);
                break;
            default:
                break;
        }
    }
	
	private void turnPage(boolean isBack)
    {
        int page = MeetingMgr.getInstance().getCurrentWatchPage();
        int sumPage = MeetingMgr.getInstance().getTotalWatchablePage();
        if (isBack)
        {
            // 向后翻页
            if (1 == page)
            {
                showToast(R.string.first_page);
                return;
            }
            page = page - 1;
        }
        else
        {
            // 向前翻页
            if (page == sumPage)
            {
                showToast(R.string.last_page);
                return;
            }
            page = page + 1;
        }

        MeetingMgr.getInstance().watchAttendee(page);
    }

    private View.OnClickListener moreButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mPopupWindow != null && mPopupWindow.isShowing())
            {
                mPopupWindow.dismiss();
            }
            switch (v.getId())
            {
                case R.id.switch_camera_ll:
                    LogUtil.i(UIConstants.DEMO_TAG, "conference switch camera!");
                    if (isCameraClose)
                    {
                        isCameraClose = false;
                        isActiveOpenCamera = true;
                    }
                    mPresenter.switchCamera();
                    break;
                case R.id.close_camera_ll:
                    isActiveOpenCamera = !isActiveOpenCamera;
                    isCameraClose = !isCameraClose;
                    boolean result = mPresenter.closeOrOpenLocalVideo(isCameraClose);
                    if (!result)
                    {
                        showToast(cameraStatusIV.isActivated() ? R.string.close_video_failed : R.string.open_video_failed);
                    }
                    break;
                case R.id.hand_up_ll:
                    mPresenter.handUpSelf();
                    break;
                case R.id.mute_all_ll:
                    mPresenter.muteConf(true);
                    break;
                case R.id.cancel_mute_all_ll:
                    mPresenter.muteConf(false);
                    break;
                case R.id.lock_conf_ll:
                    mPresenter.lockConf(true);
                    break;
                case R.id.un_lock_conf_ll:
                    mPresenter.lockConf(false);
                    break;
                case R.id.upgrade_conf_ll:
                    mPresenter.updateConf();
                    break;
                case R.id.request_chairman_ll:
                    showRequestChairmanDialog();
                    break;
                case R.id.release_chairman_ll:
                    mPresenter.releaseChairman();
                    break;
                case R.id.set_conf_mode_ll:
                    showConfMode();
                    break;
                case R.id.start_record_ll:
                    mPresenter.recordConf(true);
                    break;
                case R.id.stop_record_ll:
                    mPresenter.recordConf(false);
                    break;
                case R.id.hide_remote_video_ll:
                    switchRemoteVideoWindowHideStatus();
                    break;
                default:
                    break;
            }
        }
    };

    private void switchRemoteVideoWindowHideStatus()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isHideVideoWindow)
                {
                    isHideVideoWindow = false;
                    if (isSvcConf)
                    {
                        setSmallVideoVisible(MeetingMgr.getInstance().getCurrentWatchSmallCount() + 1);
                    }
                    else
                    {
                        mPresenter.changeLocalVideoVisible(true);
                        mHideVideoLayout.setVisibility(View.GONE);
                    }

                    mConfSmallVideoWndLL.setVisibility(View.VISIBLE);
                }
                else
                {
                    if (isSvcConf)
                    {
                        setSmallVideoVisible(0);
                    }
                    else
                    {
                        mPresenter.changeLocalVideoVisible(false);
                        mHideVideoLayout.setVisibility(View.VISIBLE);
                    }
                    mConfSmallVideoWndLL.setVisibility(View.INVISIBLE);
                    isHideVideoWindow = true;
                }
            }
        });
    }

    private void showRequestChairmanDialog()
    {
        final EditDialog dialog = new EditDialog(this, R.string.input_chairman_password);
        dialog.setRightButtonListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                CommonUtil.hideSoftInput(ConfManagerActivity.this);
                mPresenter.requestChairman(dialog.getText());
            }
        });
        dialog.show();
    }

    private void showConfMode()
    {
        final SimpleListDialog dialog = new SimpleListDialog(this, items);
        dialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog.dismiss();
                mPresenter.onItemDetailClick((String) items.get(position), null);
            }
        });
        dialog.show();
    }

    @Override
    public void refreshMemberList(final List<Member> list)
    {
        if (null == list || list.size() <= 0)
        {
            return;
        }

        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                showMoreButton();
                mAdapter.setData(list);
                mAdapter.notifyDataSetChanged();
                if (!isVideo)
                {
                    mAudioConfAttendeeTV.setText(getAttendeeName(list));
                }
            }
        });
    }

    @Override
    public void refreshWatchMemberPage() {
        final int currentPage = MeetingMgr.getInstance().getCurrentWatchPage();
        final int totalPage = MeetingMgr.getInstance().getTotalWatchablePage();
        final int watchSum = MeetingMgr.getInstance().getWatchSum();

        if (0 == watchSum)
        {
            isOnlyLocal = true;
        }
        else
        {
            isOnlyLocal = false;
        }
        updateLocalVideo();

        if (isHideVideoWindow)
        {
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isSvcConf && isVideo)
                {
                    if (totalPage <= 1) {
                        mConfVideoBackIV.setVisibility(View.INVISIBLE);
                        mConfVideoForwardIV.setVisibility(View.INVISIBLE);
                    } else {
                        if (currentPage == 1) {
                            mConfVideoBackIV.setVisibility(View.INVISIBLE);
                            mConfVideoForwardIV.setVisibility(View.VISIBLE);
                        } else if (currentPage == totalPage){
                            mConfVideoBackIV.setVisibility(View.VISIBLE);
                            mConfVideoForwardIV.setVisibility(View.INVISIBLE);
                        } else {
                            mConfVideoBackIV.setVisibility(View.VISIBLE);
                            mConfVideoForwardIV.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void setSmallVideoVisible(final int sum) {
        if (!isVideo || !isSvcConf)
        {
            return;
        }

        if (isHideVideoWindow)
        {
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (sum)
                {
                    case 0:
                        mPresenter.getLocalVideoView().setVisibility(View.GONE);
                        mPresenter.getRemoteSmallVideoView_01().setVisibility(View.GONE);
                        mPresenter.getRemoteSmallVideoView_02().setVisibility(View.GONE);
                        mPresenter.getRemoteSmallVideoView_03().setVisibility(View.GONE);

                        mConfSmallVideoWndLL.setVisibility(View.GONE);
                        mConfLocalVideoLayout.setVisibility(View.GONE);
                        mConfRemoteSmallVideoLayout_01.setVisibility(View.GONE);
                        mConfRemoteSmallVideoLayout_02.setVisibility(View.GONE);
                        mConfRemoteSmallVideoLayout_03.setVisibility(View.GONE);

                        mConfLocalVideoText.setVisibility(View.GONE);
                        mConfRemoteSmallVideoText_01.setVisibility(View.GONE);
                        mConfRemoteSmallVideoText_02.setVisibility(View.GONE);
                        mConfRemoteSmallVideoText_03.setVisibility(View.GONE);
                        break;
                    case 1:
                        mPresenter.getLocalVideoView().setVisibility(View.VISIBLE);
                        mPresenter.getRemoteSmallVideoView_01().setVisibility(View.GONE);
                        mPresenter.getRemoteSmallVideoView_02().setVisibility(View.GONE);
                        mPresenter.getRemoteSmallVideoView_03().setVisibility(View.GONE);

                        mConfSmallVideoWndLL.setVisibility(View.GONE);
                        mConfLocalVideoLayout.setVisibility(View.GONE);
                        mConfRemoteSmallVideoLayout_01.setVisibility(View.GONE);
                        mConfRemoteSmallVideoLayout_02.setVisibility(View.GONE);
                        mConfRemoteSmallVideoLayout_03.setVisibility(View.GONE);

                        mConfLocalVideoText.setVisibility(View.GONE);
                        mConfRemoteSmallVideoText_01.setVisibility(View.GONE);
                        mConfRemoteSmallVideoText_02.setVisibility(View.GONE);
                        mConfRemoteSmallVideoText_03.setVisibility(View.GONE);
                        break;
                    case 2:
                        mPresenter.getLocalVideoView().setVisibility(View.VISIBLE);
                        mPresenter.getRemoteSmallVideoView_01().setVisibility(View.VISIBLE);
                        mPresenter.getRemoteSmallVideoView_02().setVisibility(View.GONE);
                        mPresenter.getRemoteSmallVideoView_03().setVisibility(View.GONE);

                        mConfSmallVideoWndLL.setVisibility(View.VISIBLE);
                        mConfLocalVideoLayout.setVisibility(View.VISIBLE);
                        mConfRemoteSmallVideoLayout_01.setVisibility(View.VISIBLE);
                        mConfRemoteSmallVideoLayout_02.setVisibility(View.GONE);
                        mConfRemoteSmallVideoLayout_03.setVisibility(View.GONE);

                        mConfLocalVideoText.setVisibility(View.VISIBLE);
                        mConfRemoteSmallVideoText_01.setVisibility(View.VISIBLE);
                        mConfRemoteSmallVideoText_02.setVisibility(View.GONE);
                        mConfRemoteSmallVideoText_03.setVisibility(View.GONE);
                        break;
                    case 3:
                        mPresenter.getLocalVideoView().setVisibility(View.VISIBLE);
                        mPresenter.getRemoteSmallVideoView_01().setVisibility(View.VISIBLE);
                        mPresenter.getRemoteSmallVideoView_02().setVisibility(View.VISIBLE);
                        mPresenter.getRemoteSmallVideoView_03().setVisibility(View.GONE);

                        mConfSmallVideoWndLL.setVisibility(View.VISIBLE);
                        mConfLocalVideoLayout.setVisibility(View.VISIBLE);
                        mConfRemoteSmallVideoLayout_01.setVisibility(View.VISIBLE);
                        mConfRemoteSmallVideoLayout_02.setVisibility(View.VISIBLE);
                        mConfRemoteSmallVideoLayout_03.setVisibility(View.GONE);

                        mConfLocalVideoText.setVisibility(View.VISIBLE);
                        mConfRemoteSmallVideoText_01.setVisibility(View.VISIBLE);
                        mConfRemoteSmallVideoText_02.setVisibility(View.VISIBLE);
                        mConfRemoteSmallVideoText_03.setVisibility(View.GONE);
                        break;
                    case 4:
                        mPresenter.getLocalVideoView().setVisibility(View.VISIBLE);
                        mPresenter.getRemoteSmallVideoView_01().setVisibility(View.VISIBLE);
                        mPresenter.getRemoteSmallVideoView_02().setVisibility(View.VISIBLE);
                        mPresenter.getRemoteSmallVideoView_03().setVisibility(View.VISIBLE);

                        mConfSmallVideoWndLL.setVisibility(View.VISIBLE);
                        mConfLocalVideoLayout.setVisibility(View.VISIBLE);
                        mConfRemoteSmallVideoLayout_01.setVisibility(View.VISIBLE);
                        mConfRemoteSmallVideoLayout_02.setVisibility(View.VISIBLE);
                        mConfRemoteSmallVideoLayout_03.setVisibility(View.VISIBLE);

                        mConfLocalVideoText.setVisibility(View.VISIBLE);
                        mConfRemoteSmallVideoText_01.setVisibility(View.VISIBLE);
                        mConfRemoteSmallVideoText_02.setVisibility(View.VISIBLE);
                        mConfRemoteSmallVideoText_03.setVisibility(View.VISIBLE);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void refreshSvcWatchDisplayName(final String remote, final String small_01, final String small_02, final String small_03) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!"".equals(remote))
                {
                    mConfRemoteBigVideoText.setVisibility(View.VISIBLE);
                }
                mConfRemoteBigVideoText.setText(remote);
                mConfRemoteSmallVideoText_01.setText(small_01);
                mConfRemoteSmallVideoText_02.setText(small_02);
                mConfRemoteSmallVideoText_03.setText(small_03);
            }
        });
    }

    private String getAttendeeName(List<Member> list)
    {
        if (1 == list.size())
        {
            return list.get(0).getDisplayName();
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < list.size(); i ++)
        {
            if (i == list.size() - 1)
            {
                builder.append(list.get(i).getDisplayName());
            }
            else
            {
                builder.append(list.get(i).getDisplayName() + ", ");
            }
        }

        return builder.toString();
    }

    private void showMoreButton()
    {
        mConfAttendee.setVisibility(View.VISIBLE);
        if(!MeetingMgr.getInstance().isAnonymous()){
            mConfMore.setVisibility(View.VISIBLE);
        }
        if (mPresenter.isChairMan())
        {
            mConfAddAttendee.setVisibility(View.VISIBLE);
        }
        else
        {
            mConfAddAttendee.setVisibility(View.GONE);
        }
    }

    @Override
    public void showItemClickDialog(final List<Object> items, final Member member) {
        final SimpleListDialog dialog = new SimpleListDialog(this, items);
        dialog.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                dialog.dismiss();
                mPresenter.onItemDetailClick((String) items.get(position), member);
            }
        });
        dialog.show();
    }

    @Override
    public void updateUpgradeConfBtn(final boolean isInDataConf) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                isDateConf = isInDataConf;
                mShareIV.setVisibility(isInDataConf ? View.VISIBLE : View.GONE);
                mConfShare.setVisibility(isInDataConf ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public void updateConfTypeIcon(final ConfBaseInfo confBaseInfo) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if( mPresenter.isSupportRecord()&& mPresenter.isRecord())
                {
                    mRecordPoint.setVisibility(View.VISIBLE);
                }else{
                    mRecordPoint.setVisibility(View.GONE);
                }
                mTitleTV.setText(mPresenter.getSubject());
            }
        });
    }

    @Override
    public void showMessage(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ConfManagerActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void confManagerActivityShare(boolean isShare,boolean isAllowAnnot) {
        this.isStartShare = isShare;
        this.isAllowAnnot = isAllowAnnot;
    }

    @Override
    public void jumpToHomeScreen() {
        isActiveShare = true;
        mHandler.sendEmptyMessage(START_SCREEN_SHARE_HANDLE);
    }

    @Override
    public void removeAllScreenShareFloatWindow() {
        isActiveShare = false;
        mHandler.sendEmptyMessage(STOP_SCREEN_SHARE_HANDLE);
    }

    @Override
    public void robShareRemoveAllScreenShareFloatWindow() {
        isActiveShare = false;
        mHandler.sendEmptyMessage(ROB_STOP_SCREEN_SHARE_HANDLE);
    }

    @Override
    public void requestScreen() {
        isActiveShare = false;
        mHandler.sendEmptyMessage(REQUEST_SCREEN_SHARE_HANDLE);
    }

    private void showRequestScreenDialog()
    {
        TripleDialog dialog = new TripleDialog(this);
        dialog.setTitle("Do you want to share ?");
        dialog.setRightText(R.string.reject_resquest_screen);
        dialog.setRightButtonListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                MeetingMgr.getInstance().setAsOwner(MeetingMgr.getInstance().getSelf().getNumber(), TsdkConfAsActionType.TSDK_E_CONF_AS_ACTION_DELETE);
            }
        });
        dialog.setLeftText(R.string.agree_resquest_screen);
        dialog.setLeftButtonListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                requestScreenSharePermission();
            }
        });
        dialog.show();
    }

    private void showLeaveConfDialog()
    {
        ConfirmDialog dialog = new ConfirmDialog(this, R.string.leave_conf);
        dialog.setRightButtonListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mHandler.sendEmptyMessage(STOP_SCREEN_SHARE_HANDLE);
                mPresenter.closeConf();
                ActivityStack.getIns().popup(ConfMemberListActivity.class);
                stopTimer();
                finish();
            }
        });
        dialog.show();
    }

    private void showEndConfDialog()
    {
        TripleDialog dialog = new TripleDialog(this);
        dialog.setRightButtonListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mHandler.sendEmptyMessage(STOP_SCREEN_SHARE_HANDLE);
                mPresenter.closeConf();
                ActivityStack.getIns().popup(ConfMemberListActivity.class);
                finish();
            }
        });
        dialog.setLeftButtonListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mHandler.sendEmptyMessage(STOP_SCREEN_SHARE_HANDLE);
                mPresenter.finishConf();
                ActivityStack.getIns().popup(ConfMemberListActivity.class);
                stopTimer();
                finish();
            }
        });
        dialog.show();
    }

    @Override
    protected void onBack() {
        super.onBack();
        mPresenter.closeConf();
        mPresenter.unregisterBroadcast();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mPresenter.closeConf();
        mPresenter.unregisterBroadcast();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mHandler.sendEmptyMessage(STOP_SCREEN_SHARE_HANDLE);
        mPresenter.leaveVideo();
        mPresenter.unregisterBroadcast();
        mPresenter.setAutoRotation(this, false, mOrientation);
        PopupWindowUtil.getInstance().dismissPopupWindow(mPopupWindow);

        networkConnectivityListener.deregisterListener(this);
        networkConnectivityListener.stopListening();
    }

    @Override
    public void updateMuteButton(final boolean isMute)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                mConfMute.setVisibility(View.VISIBLE);
                mConfMute.setActivated(isMute);
            }
        });
    }

    @Override
    public void updateAttendeeButton(final Member member) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!isVideo || !isSvcConf)
                {
                    return;
                }
                if (isOnlyLocal || isHideVideoWindow)
                {
                    return;
                }
                mConfLocalVideoText.setText(getString(R.string.me) + member.getDisplayName());
            }
        });
    }

    @Override
    public void updateLocalVideo()
    {
        if (!isVideo)
        {
            return;
        }
        if (mOrientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            mConfSmallVideoWndLL.setOrientation(LinearLayout.VERTICAL);
            setConfVideoSize(false);
        }
        else
        {
            mConfSmallVideoWndLL.setOrientation(LinearLayout.HORIZONTAL);
            setConfVideoSize(true);
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isSvcConf) {

                    if (isOnlyLocal)
                    {
                        if (isSetOnlyLocalWind)
                        {
                            return;
                        }
                        mPresenter.setOnlyLocalVideoContainer(ConfManagerActivity.this, mConfRemoteBigVideoLayout, mHideVideoLayout);
                        mConfSmallVideoWndLL.setVisibility(View.GONE);
                        isSetOnlyLocalWind = true;
                    }
                    else
                    {
                        if (!isSetOnlyLocalWind)
                        {
                            return;
                        }
                        mPresenter.setSvcAllVideoContainer(ConfManagerActivity.this, mConfLocalVideoLayout, mConfRemoteBigVideoLayout, mHideVideoLayout,
                                mConfRemoteSmallVideoLayout_01, mConfRemoteSmallVideoLayout_02, mConfRemoteSmallVideoLayout_03);
                        mConfSmallVideoWndLL.setVisibility(View.VISIBLE);
                        isSetOnlyLocalWind = false;
                    }
                } else {
                    // AVC会议保持原逻辑不变
                    mPresenter.setAvcVideoContainer(ConfManagerActivity.this, mConfLocalVideoLayout, mConfRemoteBigVideoLayout, mHideVideoLayout);
                }
            }
        });


    }

    private void updateLoudSpeakerButton(int type)
    {
        if (type == CallConstant.TYPE_LOUD_SPEAKER)
        {
            mConfSpeaker.setActivated(true);
        }
        else
        {
            mConfSpeaker.setActivated(false);
        }
    }

    @TargetApi(21)
    @Override
    public void updateSignal(long signalStrength) {
        final long signal = signalStrength;
        if(isVideo){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(signal==1){
                        mSignalView.setBackground(getDrawable(R.drawable.signal_1));
                    }
                    if(signal==2){
                        mSignalView.setBackground(getDrawable(R.drawable.signal_2));
                    }
                    if(signal==3){
                        mSignalView.setBackground(getDrawable(R.drawable.signal_3));
                    }
                    if(signal==4 || signal==5){
                        mSignalView.setBackground(getDrawable(R.drawable.signal_4));
                    }
                }
            });
        }else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(signal==1){
                        mAudioSignalView.setBackground(getDrawable(R.drawable.signal_1));
                    }
                    if(signal==2){
                        mAudioSignalView.setBackground(getDrawable(R.drawable.signal_2));
                    }
                    if(signal==3){
                        mAudioSignalView.setBackground(getDrawable(R.drawable.signal_3));
                    }
                    if(signal==4 || signal==5){
                        mAudioSignalView.setBackground(getDrawable(R.drawable.signal_4));
                    }
                }
            });
        }
    }

    private synchronized PopupWindow generatePopupWindow(View view, int width, int height)
    {
        final PopupWindow popupWindow = new PopupWindow(view, width, height);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.setOnKeyListener(new View.OnKeyListener()
        {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (keyCode == KeyEvent.KEYCODE_MENU && popupWindow.isShowing())
                {
                    popupWindow.dismiss();
                    return true;
                }
                return false;
            }
        });

        return popupWindow;
    }

    private void showAddMemberDialog()
    {
        final ThreeInputDialog editDialog = new ThreeInputDialog(this);
        editDialog.setRightButtonListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mPresenter.addMember(editDialog.getInput2(), editDialog.getInput1(), editDialog.getInput3());
            }
        });

        editDialog.setHint1(R.string.input_number);
        editDialog.setHint2(R.string.input_name);
        editDialog.setHint3(R.string.input_account);
        editDialog.show();
    }

    /**
     * 请求截图权限
     */
    @TargetApi(21)
    private void requestScreenSharePermission(){
        MediaProjectionManager localMediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        if (null != localMediaProjectionManager) {
            startActivityForResult(localMediaProjectionManager.createScreenCaptureIntent(), UIConstants.REQUEST_MEDIA_PROJECTION);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //设置返回处理,只有require 的权限需要处理取消逻辑，其他的权限场景只处理成功逻辑
        if (Activity.RESULT_OK != resultCode) {
            Log.d("ConfManagerActivity", "resultCode is not ok requestCode: " + requestCode);
            return;
        }
        switch (requestCode){
            case UIConstants.REQUEST_MEDIA_PROJECTION:
                if (data != null) {
                    // 获取到截屏权限后，先判断是否悬浮窗权限
                    mPresenter.confShare(this , data);
                }
                break;
            default:
                break;
        }
    }

    private void showMoreConfCtrl()
    {
        int wrap = LinearLayout.LayoutParams.WRAP_CONTENT;
        View popupView = getLayoutInflater().inflate(R.layout.popup_conf_btn_list, null);

        LinearLayout switchCameraBtn = (LinearLayout) popupView.findViewById(R.id.switch_camera_ll);
        LinearLayout closeCameraBtn = (LinearLayout) popupView.findViewById(R.id.close_camera_ll);
        LinearLayout handUpLayout = (LinearLayout) popupView.findViewById(R.id.hand_up_ll);
        LinearLayout muteAllLayout = (LinearLayout) popupView.findViewById(R.id.mute_all_ll);
        LinearLayout cancelMuteAllLayout = (LinearLayout) popupView.findViewById(R.id.cancel_mute_all_ll);
        LinearLayout lockLayout = (LinearLayout) popupView.findViewById(R.id.lock_conf_ll);
        LinearLayout unlockLayout = (LinearLayout) popupView.findViewById(R.id.un_lock_conf_ll);
        LinearLayout startRecordLayout = (LinearLayout) popupView.findViewById(R.id.start_record_ll);
        LinearLayout endRecordLayout = (LinearLayout) popupView.findViewById(R.id.stop_record_ll);
        LinearLayout upgradeLayout = (LinearLayout) popupView.findViewById(R.id.upgrade_conf_ll);
        LinearLayout requestChairManLayout = (LinearLayout) popupView.findViewById(R.id.request_chairman_ll);
        LinearLayout releaseChairManLayout = (LinearLayout) popupView.findViewById(R.id.release_chairman_ll);
        LinearLayout seConfModeLayout = (LinearLayout) popupView.findViewById(R.id.set_conf_mode_ll);
        LinearLayout hideVideoLayout = (LinearLayout) popupView.findViewById(R.id.hide_remote_video_ll);
        cameraStatusIV = (ImageView) popupView.findViewById(R.id.iv_camera_status);
        cameraStatusTV = (TextView) popupView.findViewById(R.id.tv_camera_status);
        ImageView handUpIV = (ImageView) popupView.findViewById(R.id.hand_up_iv);
        TextView handUpTV = (TextView) popupView.findViewById(R.id.hand_up_tv);
        TextView hideWindowTV = (TextView) popupView.findViewById(R.id.status_video_window);
        ImageView hideWindowIV = (ImageView) popupView.findViewById(R.id.hide_video_iv);

        cameraStatusIV.setActivated(isCameraClose);
        cameraStatusTV.setText(isCameraClose ? getString(R.string.open_local_camera) :
                getString(R.string.close_local_camera));
        hideWindowTV.setText(isHideVideoWindow ? getString(R.string.show_video) : getString(R.string.hide_video));
        hideWindowIV.setImageResource(isHideVideoWindow ? R.drawable.conf_video_show : R.drawable.conf_video_hide);

        // 主席：会场静音、锁定、释放主席权限； 普通与会者：举手、申请主席
        if (mPresenter.isChairMan())
        {
            if (mPresenter.isConfMute())
            {
                cancelMuteAllLayout.setVisibility(View.VISIBLE);
                muteAllLayout.setVisibility(View.GONE);
            }
            else
            {
                cancelMuteAllLayout.setVisibility(View.GONE);
                muteAllLayout.setVisibility(View.VISIBLE);
            }

            if (isDateConf)
            {
                upgradeLayout.setVisibility(View.GONE);
            }
            else
            {
                upgradeLayout.setVisibility(View.VISIBLE);
            }

            // 融合会议显示锁定
            if (TSDK_E_CONF_ENV_HOSTED_CONVERGENT_CONFERENCE == MeetingMgr.getInstance().getConfEnvType()) {
                if (mPresenter.isConfLock()) {
//                    unlockLayout.setVisibility(View.VISIBLE);
                    lockLayout.setVisibility(View.GONE);
                } else {
                    unlockLayout.setVisibility(View.GONE);
//                    lockLayout.setVisibility(View.VISIBLE);
                }

            } else {
                unlockLayout.setVisibility(View.GONE);
                lockLayout.setVisibility(View.GONE);
            }

            if(mPresenter.isSupportRecord()) {
                if (mPresenter.isRecord()) {
                    startRecordLayout.setVisibility(View.GONE);
                    endRecordLayout.setVisibility(View.VISIBLE);
                } else {
                    startRecordLayout.setVisibility(View.VISIBLE);
                    endRecordLayout.setVisibility(View.GONE);
                }
            }else
            {
                startRecordLayout.setVisibility(View.GONE);
                endRecordLayout.setVisibility(View.GONE);
            }

            requestChairManLayout.setVisibility(View.GONE);
            releaseChairManLayout.setVisibility(View.VISIBLE);
            handUpLayout.setVisibility(View.GONE);
        }
        else
        {
            cancelMuteAllLayout.setVisibility(View.GONE);
            muteAllLayout.setVisibility(View.GONE);
            unlockLayout.setVisibility(View.GONE);
            lockLayout.setVisibility(View.GONE);
            upgradeLayout.setVisibility(View.GONE);

            requestChairManLayout.setVisibility(View.VISIBLE);
            releaseChairManLayout.setVisibility(View.GONE);
            seConfModeLayout.setVisibility(View.GONE);

            startRecordLayout.setVisibility(View.GONE);
            endRecordLayout.setVisibility(View.GONE);

            if(TSDK_E_CONF_ENV_HOSTED_CONVERGENT_CONFERENCE == MeetingMgr.getInstance().getConfEnvType())
            {
                if (mPresenter.isHandUp())
                {
                    handUpIV.setActivated(false);
                    handUpTV.setText(R.string.conf_cancel_hand_up);
                }
                else
                {
                    handUpIV.setActivated(true);
                    handUpTV.setText(R.string.conf_hand_up);
                }
            }
            else
            {
                handUpLayout.setVisibility(View.GONE);
            }
        }

        if (isOnlyLocal)
        {
            hideVideoLayout.setVisibility(View.GONE);
        }
        else
        {
            hideVideoLayout.setVisibility(View.VISIBLE);
        }

        // 音频不显示关闭、切换摄像头、选看和广播、隐藏或者显示视频小窗口以及设置会议模式
        if (!isVideo)
        {
            hideVideoLayout.setVisibility(View.GONE);
            switchCameraBtn.setVisibility(View.GONE);
            closeCameraBtn.setVisibility(View.GONE);
            seConfModeLayout.setVisibility(View.GONE);
        }
        else
        {
            switchCameraBtn.setOnClickListener(moreButtonListener);
            closeCameraBtn.setOnClickListener(moreButtonListener);
            seConfModeLayout.setOnClickListener(moreButtonListener);
        }

        // ido 不显示举手
        if (ConfConstant.ConfProtocol.IDO_PROTOCOL == MeetingMgr.getInstance().getConfProtocol())
        {
            handUpLayout.setVisibility(View.GONE);
        }

        handUpLayout.setOnClickListener(moreButtonListener);
        muteAllLayout.setOnClickListener(moreButtonListener);
        cancelMuteAllLayout.setOnClickListener(moreButtonListener);
        lockLayout.setOnClickListener(moreButtonListener);
        unlockLayout.setOnClickListener(moreButtonListener);
        startRecordLayout.setOnClickListener(moreButtonListener);
        endRecordLayout.setOnClickListener(moreButtonListener);
        upgradeLayout.setOnClickListener(moreButtonListener);
        requestChairManLayout.setOnClickListener(moreButtonListener);
        releaseChairManLayout.setOnClickListener(moreButtonListener);
        hideVideoLayout.setOnClickListener(moreButtonListener);

        mPopupWindow = generatePopupWindow(popupView, wrap, wrap);
        mPopupWindow.showAtLocation(findViewById(R.id.media_btn_group), Gravity.RIGHT | Gravity.BOTTOM, 0, mConfMediaLayout.getHeight());
    }

    /**
     * 屏幕旋转时调用此方法
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == 2)
        {
            mConfSmallVideoWndLL.setOrientation(LinearLayout.VERTICAL);
            setConfVideoSize(false);
        }
        else
        {
            mConfSmallVideoWndLL.setOrientation(LinearLayout.HORIZONTAL);
            setConfVideoSize(true);
        }

        if (this.mOrientation == newConfig.orientation)
        {
            return;
        }
        else
        {
            this.mOrientation = newConfig.orientation;
            mPresenter.setAutoRotation(this, true, this.mOrientation);
        }
    }
    
    private void showButton()
    {
        if (mTitleLayout.getVisibility() == View.GONE || mConfMediaLayout.getVisibility() == View.GONE)
        {
            mTitleLayout.setVisibility(View.VISIBLE);
            mConfMediaLayout.setVisibility(View.VISIBLE);
            isShowBar = true;
        }
    }
    
    private void hideButton()
    {
        if (mTitleLayout.getVisibility() == View.VISIBLE || mConfMediaLayout.getVisibility() == View.VISIBLE)
        {
            mTitleLayout.setVisibility(View.GONE);
            mConfMediaLayout.setVisibility(View.GONE);
            isShowBar = false;
        }
    }
    
    private void startTimer()
    {
        initTimer();
        try {
            if (isFirstStart)
            {
                timer.schedule(myTimerTask, 5000);
            }
            else 
            {
                timer.schedule(myTimerTask, 200, 5000);
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
            initTimer();
            timer.schedule(myTimerTask, 5000);
        }
    }
    
    private void stopTimer()
    {
        if (null != timer)
        {
            timer.cancel();
            timer = null;
        }
    }

    private void initTimer()
    {
        timer = new Timer();
        myTimerTask = new MyTimerTask();
    }

    class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isFirstStart)
                    {
                        hideButton();
                        isFirstStart = false;
                        stopTimer();
                    }
                    else 
                    {
                        if (isShowBar)
                        {
                            hideButton();
                            isPressTouch = false;
                            stopTimer();
                        }
                        else
                        {
                            showButton();
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onNetWorkChange(JSONObject nwd) {
        ECApplication.setLastInfo(nwd);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                JSONObject networkInfo = ECApplication.getLastInfo();
                LogUtil.i("onNetWorkChange","conf networkInfo: " + networkInfo);
                try {
                    int type = DeviceManager.getNetworkEnumByStr(networkInfo.getString("type"));
                    if (0 == type)
                    {
                        LoginMgr.getInstance().setConnectedStatus(LoginConstant.NO_NETWORK);
                        showCustomToast(R.string.connect_error);
                    }
                    else
                    {
                        mPresenter.configIpResume();
                    }

                    mCurrentActivity = ActivityUtil.getCurrentActivity(ConfManagerActivity.this);
                    if ("ConfMemberListActivity".equals(mCurrentActivity))
                    {
                        ActivityStack.getIns().popup(ConfMemberListActivity.class);
                    }

                    if ("DataConfActivity".equals(mCurrentActivity))
                    {
                        ActivityStack.getIns().popup(DataConfActivity.class);
                    }

                } catch (JSONException e) {
                    LogUtil.e("onNetWorkChange","JSONException: " + e.toString());
                }
            }
        });
    }

}
