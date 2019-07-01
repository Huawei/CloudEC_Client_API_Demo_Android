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

import com.huawei.opensdk.callmgr.CallConstant;
import com.huawei.opensdk.callmgr.CallMgr;
import com.huawei.opensdk.commonservice.common.LocContext;
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
import com.huawei.opensdk.ec_sdk_demo.util.ActivityUtil;
import com.huawei.opensdk.ec_sdk_demo.util.CommonUtil;
import com.huawei.opensdk.ec_sdk_demo.util.PopupWindowUtil;
import com.huawei.opensdk.ec_sdk_demo.widget.ConfirmDialog;
import com.huawei.opensdk.ec_sdk_demo.widget.EditDialog;
import com.huawei.opensdk.ec_sdk_demo.widget.SimpleListDialog;
import com.huawei.opensdk.ec_sdk_demo.widget.ThreeInputDialog;
import com.huawei.opensdk.ec_sdk_demo.widget.TripleDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.huawei.ecterminalsdk.base.TsdkConfEnvType.TSDK_E_CONF_ENV_HOSTED_CONVERGENT_CONFERENCE;

public class ConfManagerActivity extends MVPBaseActivity<IConfManagerContract.ConfManagerView, ConfManagerBasePresenter>
        implements IConfManagerContract.ConfManagerView, View.OnClickListener
{
    private RelativeLayout mVideoConfLayout;
    private RelativeLayout mTitleLayout;
    private LinearLayout mConfMediaLayout;
    private FrameLayout mConfRemoteVideoLayout;
    private FrameLayout mConfSmallLayout;
    private FrameLayout mHideVideoLayout;
    private ImageView mRecordPoint;

    private FrameLayout mHideLocalVideoBtn;
    private FrameLayout mShowLocalVideoBtn;

    private LinearLayout mConfButton;
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

    private String confID;
    private boolean isCameraClose = false;
    private boolean isVideo = false;
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

    private int activeCameraTime = 0;
    private boolean isActiveOpenCamera = true;
    private boolean isActiveShare = false;

    private static final int START_SCREEN_SHARE_HANDLE = 666;
    private static final int STOP_SCREEN_SHARE_HANDLE = 888;
    private static final int ROB_STOP_SCREEN_SHARE_HANDLE = 222;
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
        return new ConfManagerPresenter();
    }

    @Override
    public void initializeComposition()
    {
        setContentView(R.layout.conf_manager_activity);
        mConfButton = (LinearLayout) findViewById(R.id.media_btn_group);
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
        mConfMute.setVisibility(View.GONE);
        mConfAddAttendee.setVisibility(View.GONE);
        mConfShare.setVisibility(View.GONE);
        mConfAttendee.setVisibility(View.GONE);
        mConfMore.setVisibility(View.GONE);

        if (isVideo)
        {
            //video layout
            mVideoConfLayout.setVisibility(View.VISIBLE);
            mConfRemoteVideoLayout = (FrameLayout) findViewById(R.id.conf_remote_video_layout);
            mHideLocalVideoBtn = (FrameLayout) findViewById(R.id.local_video_hide);
            mHideVideoLayout = (FrameLayout) findViewById(R.id.hide_video_view);
            mShowLocalVideoBtn = (FrameLayout) findViewById(R.id.local_video_hide_cancel);
            mConfSmallLayout = (FrameLayout) findViewById(R.id.conf_video_small_logo);

            //title
            mRightIV.setVisibility(View.VISIBLE);

            mHideLocalVideoBtn.setOnClickListener(this);
            mShowLocalVideoBtn.setOnClickListener(this);
            mRightIV.setOnClickListener(this);
        }
        else
        {
            mAudioConfAttendeeTV = (TextView) findViewById(R.id.tv_audio_conf_attendee);
            mAudioConfLayout = (RelativeLayout) findViewById(R.id.audio_conf_layout_logo);

            mAudioConfAttendeeTV.setSelected(true);

            mAudioConfLayout.setVisibility(View.VISIBLE);
            mVideoConfLayout.setVisibility(View.INVISIBLE);

            //title
            mRightIV.setVisibility(View.GONE);
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

    @Override
    protected void onResume()
    {
        super.onResume();
        mPresenter.registerBroadcast();

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
            mConfSmallLayout.getLayoutParams().width = dp2ps(this, 160);
            mConfSmallLayout.getLayoutParams().height = dp2ps(this, 90);
        }
        else
        {
            mConfSmallLayout.getLayoutParams().width = dp2ps(this, 90);
            mConfSmallLayout.getLayoutParams().height = dp2ps(this, 160);
        }
        mPresenter.setVideoContainer(this, mConfSmallLayout, mConfRemoteVideoLayout, mHideVideoLayout);
        mPresenter.setAutoRotation(this, true, mOrientation);

        // 以下处理用于PBX下的视频会议
//        Member self = MeetingMgr.getInstance().getCurrentConferenceSelf();
//        if (self == null || self.getCameraEntityList().isEmpty())
//        {
//            LogUtil.i(UIConstants.DEMO_TAG,  "no camera--------- ");
//        }
//        else
//        {
//            //打开前置摄像头
//            mPresenter.shareSelfVideo(self.getCameraEntityList().get(1).getDeviceID());
//        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isActiveOpenCamera){
            mPresenter.closeOrOpenLocalVideo(false);
        }else {
            mPresenter.closeOrOpenLocalVideo(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!DeviceUtil.isAppForeground()) {
            mPresenter.closeOrOpenLocalVideo(true);
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
            case R.id.local_video_hide:
                mPresenter.changeLocalVideoVisible(false);

                mHideLocalVideoBtn.setVisibility(View.GONE);
                mShowLocalVideoBtn.setVisibility(View.VISIBLE);

                mConfSmallLayout.setVisibility(View.GONE);
                mHideVideoLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.local_video_hide_cancel:
                mPresenter.changeLocalVideoVisible(true);

                mHideLocalVideoBtn.setVisibility(View.VISIBLE);
                mShowLocalVideoBtn.setVisibility(View.GONE);

                mHideVideoLayout.setVisibility(View.GONE);
                mConfSmallLayout.setVisibility(View.VISIBLE);

                break;
            case R.id.right_iv:
                final List<Member> memberList = mPresenter.getMemberList();
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
            default:
                break;
        }
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
                    mPresenter.switchCamera();
                    break;
                case R.id.close_camera_ll:
                    activeCameraTime++;
                    if (activeCameraTime%2==0){
                        isActiveOpenCamera = true;
                    }else {
                        isActiveOpenCamera = false;
                    }
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
                default:
                    break;
            }
        }
    };

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
    public void updateAttendeeButton(Member member) {

    }

    @Override
    public void updateLocalVideo()
    {
        mPresenter.setVideoContainer(this, mConfSmallLayout, mConfRemoteVideoLayout, mHideVideoLayout);
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
        cameraStatusIV = (ImageView) popupView.findViewById(R.id.iv_camera_status);
        cameraStatusTV = (TextView) popupView.findViewById(R.id.tv_camera_status);
        ImageView handUpIV = (ImageView) popupView.findViewById(R.id.hand_up_iv);
        TextView handUpTV = (TextView) popupView.findViewById(R.id.hand_up_tv);

        cameraStatusIV.setActivated(isCameraClose);
        cameraStatusTV.setText(isCameraClose ? getString(R.string.open_local_camera) :
                getString(R.string.close_local_camera));

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
                    unlockLayout.setVisibility(View.VISIBLE);
                    lockLayout.setVisibility(View.GONE);
                } else {
                    unlockLayout.setVisibility(View.GONE);
                    lockLayout.setVisibility(View.VISIBLE);
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

        // 音频不显示关闭、切换摄像头、选看和广播以及设置会议模式
        if (!isVideo)
        {
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

        // ido 不显示举手、静音(取消静音)会场
        if (ConfConstant.ConfProtocol.IDO_PROTOCOL == MeetingMgr.getInstance().getConfProtocol())
        {
            handUpLayout.setVisibility(View.GONE);
            cancelMuteAllLayout.setVisibility(View.GONE);
            muteAllLayout.setVisibility(View.GONE);
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

        mPopupWindow = generatePopupWindow(popupView, wrap, wrap);
        mPopupWindow.showAtLocation(findViewById(R.id.media_btn_group), Gravity.RIGHT | Gravity.BOTTOM, 0, mConfButton.getHeight());
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
            mConfSmallLayout.getLayoutParams().width = dp2ps(this, 160);
            mConfSmallLayout.getLayoutParams().height = dp2ps(this, 90);
        }
        else
        {
            mConfSmallLayout.getLayoutParams().width = dp2ps(this, 90);
            mConfSmallLayout.getLayoutParams().height = dp2ps(this, 160);
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

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     * @param context
     * @param dpValue
     * @return
     */
    private int dp2ps(Context context, float dpValue)
    {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (scale * dpValue + 0.5f);
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

}
