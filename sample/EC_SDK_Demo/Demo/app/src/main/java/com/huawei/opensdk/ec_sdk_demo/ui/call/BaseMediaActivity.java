package com.huawei.opensdk.ec_sdk_demo.ui.call;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.opensdk.callmgr.CallConstant;
import com.huawei.opensdk.callmgr.CallInfo;
import com.huawei.opensdk.callmgr.CallMgr;
import com.huawei.opensdk.commonservice.common.LocContext;
import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcastReceiver;
import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.demoservice.MeetingMgr;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.logic.call.CallFunc;
import com.huawei.opensdk.ec_sdk_demo.ui.IntentConstant;
import com.huawei.opensdk.ec_sdk_demo.ui.base.BaseActivity;
import com.huawei.opensdk.ec_sdk_demo.util.ActivityUtil;
import com.huawei.opensdk.ec_sdk_demo.util.DialogUtil;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;

/**
 * This class is about base media activity.
 */
public class BaseMediaActivity extends BaseActivity implements View.OnClickListener, LocBroadcastReceiver {
    private static final int CALL_CONNECTED = 100;
    private static final int CALL_UPGRADE = 101;
    private static final int HOLD_CALL_SUCCESS = 102;
    private static final int VIDEO_HOLD_CALL_SUCCESS = 103;
    private static final int MEDIA_CONNECTED = 104;
    private static final int BLD_TRANSFER_SUCCESS = 105;
    private static final int BLD_TRANSFER_FAILED = 106;

    protected ImageView mRejectBtn;
    protected FrameLayout mAudioAcceptCallArea;
    protected FrameLayout mVideoAcceptCallArea;
    protected FrameLayout mDivertCallArea;
    private Timer mDismissDialogTimer;
    private static final int CANCEL_TIME = 25000;

    protected LinearLayout mPlateButton;
    protected LinearLayout mBlindTransferButton;
    protected LinearLayout mHoldCallButton;
    protected LinearLayout mTransferMeeting;
    protected LinearLayout mMuteArea;
    protected LinearLayout mPlateArea;
    protected ImageView mCloseArea;

    protected TextView mCallNumberTv;
    protected TextView mCallNameTv;
    protected TextView mHoldCallText;
    protected Toast toast;

    protected String mCallNumber;
    protected String mDisplayName;
    protected boolean mIsVideoCall;
    protected int mCallID;
    protected String mConfID;
    protected boolean mIsConfCall;

    protected int mConfToCallHandle;

    protected SecondDialPlateControl mPlateControl;

    private String[] mActions = new String[]{CustomBroadcastConstants.ACTION_CALL_CONNECTED,
            CustomBroadcastConstants.CALL_MEDIA_CONNECTED,
            CustomBroadcastConstants.CONF_CALL_CONNECTED,
            CustomBroadcastConstants.ACTION_CALL_END,
            CustomBroadcastConstants.CALL_UPGRADE_ACTION,
            CustomBroadcastConstants.HOLD_CALL_RESULT,
            CustomBroadcastConstants.BLD_TRANSFER_RESULT,
            CustomBroadcastConstants.CALL_TRANSFER_TO_CONFERENCE};
    private LinearLayout mSpeakerButton;
    private LinearLayout mUpgradeVideoArea;

    private CallFunc mCallFunc;

    private boolean mMuteStatus;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MEDIA_CONNECTED:
                    mPlateButton.setVisibility(View.VISIBLE);
                    break;

                case CALL_CONNECTED:
                    showButtons();
                    mAudioAcceptCallArea.setVisibility(View.GONE);
                    mVideoAcceptCallArea.setVisibility(View.GONE);
                    mDivertCallArea.setVisibility(View.GONE);

                    if (msg.obj instanceof CallInfo) {
                        CallInfo callInfo = (CallInfo) msg.obj;
                        if (callInfo.isVideoCall()) {
                            Intent intent = new Intent(IntentConstant.VIDEO_ACTIVITY_ACTION);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addCategory(IntentConstant.DEFAULT_CATEGORY);

                            intent.putExtra(UIConstants.CALL_INFO, callInfo);
                            ActivityUtil.startActivity(BaseMediaActivity.this, intent);
                            finish();
                        }
                    }

                    break;
                case CALL_UPGRADE:
                    mDialog = DialogUtil.generateDialog(BaseMediaActivity.this, R.string.ntf_upgrade_videocall,
                            R.string.accept, R.string.reject,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    cancelDisDiaTimer();
                                    Executors.newSingleThreadExecutor().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (null == Looper.myLooper()) {
                                                Looper.prepare();
                                            }
                                            CallMgr.getInstance().acceptAddVideo(mCallID);
                                        }
                                    });
                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    cancelDisDiaTimer();
                                    CallMgr.getInstance().rejectAddVideo(mCallID);
                                }
                            });
                    mDialog.show();
                    startDismissDiaLogTimer();
                    break;
                case HOLD_CALL_SUCCESS: {
                    String textDisplayName = null == mDisplayName ? "" : mDisplayName;
                    String textCallNumber = null == mCallNumber ? "" : mCallNumber;
                    if ("Hold".equals(mCallNumberTv.getTag())) {
                        textCallNumber = textCallNumber + "Holding";
                    }
                    mCallNameTv.setText(textDisplayName);
                    mCallNumberTv.setText(textCallNumber);
                }
                break;
                case VIDEO_HOLD_CALL_SUCCESS: {
                    String textDisplayName = null == mDisplayName ? "" : mDisplayName;
                    String textCallNumber = null == mCallNumber ? "" : mCallNumber;
                    textCallNumber = textCallNumber + "Holding";
                    mCallNameTv.setText(textDisplayName);
                    mCallNumberTv.setText(textCallNumber);
                    mHoldCallText.setText(R.string.un_hold_call);
                }
                break;
                case BLD_TRANSFER_SUCCESS:
                    Toast.makeText(BaseMediaActivity.this, "Blind transfer success", Toast.LENGTH_SHORT).show();
                    break;
                case BLD_TRANSFER_FAILED:
                    Toast.makeText(BaseMediaActivity.this, "Blind transfer failed", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void initializeComposition() {
        setContentView(R.layout.call_media);
        mPlateButton = (LinearLayout) findViewById(R.id.plate_btn);
        mBlindTransferButton = (LinearLayout) findViewById(R.id.blind_transfer);
        mHoldCallButton = (LinearLayout) findViewById(R.id.hold_call);
        mSpeakerButton = (LinearLayout) findViewById(R.id.speaker_btn);
        mMuteArea = (LinearLayout) findViewById(R.id.mute_btn);
        mPlateArea = (LinearLayout) findViewById(R.id.dial_plate_area);
        mUpgradeVideoArea = (LinearLayout) findViewById(R.id.upgrade_video_btn);
        mRejectBtn = (ImageView) findViewById(R.id.reject_btn);
        mAudioAcceptCallArea = (FrameLayout) findViewById(R.id.audio_accept_call_area);
        mVideoAcceptCallArea = (FrameLayout) findViewById(R.id.video_accept_call_area);
        mDivertCallArea = (FrameLayout) findViewById(R.id.divert_call_area);
        mCallNumberTv = (TextView) findViewById(R.id.call_number);
        mTransferMeeting = (LinearLayout) findViewById(R.id.transfer_meeting);

        mCloseArea = (ImageView) findViewById(R.id.hide_dial_btn);
        mCallNameTv = (TextView) findViewById(R.id.call_name);
        mHoldCallText = (TextView) findViewById(R.id.hold_call_text);

        mPlateControl = new SecondDialPlateControl(mPlateArea, mCallID);
        mPlateControl.hideDialPlate();

        mMuteArea.setOnClickListener(this);
        mCloseArea.setOnClickListener(this);
        mPlateButton.setOnClickListener(this);
        mSpeakerButton.setOnClickListener(this);
        mUpgradeVideoArea.setOnClickListener(this);
        mBlindTransferButton.setOnClickListener(this);
        mHoldCallButton.setOnClickListener(this);
        mHoldCallText.setOnClickListener(this);
        mTransferMeeting.setOnClickListener(this);

        hideViews();
        refreshMuteStatus();
        refreshSpeakerStatus();
    }

    private void startDismissDiaLogTimer() {
        cancelDisDiaTimer();

        mDismissDialogTimer = new Timer("Dismiss Dialog");
        DismissDialogTimerTask dismissDialogTimerTask = new DismissDialogTimerTask(mDialog, mCallID);
        mDismissDialogTimer.schedule(dismissDialogTimerTask, CANCEL_TIME);
    }

    private void cancelDisDiaTimer() {
        if (mDismissDialogTimer != null) {
            mDismissDialogTimer.cancel();
            mDismissDialogTimer = null;
        }
    }

    private AlertDialog mDialog;

    private static class DismissDialogTimerTask extends TimerTask {
        private final AlertDialog dialog;
        private int callID;

        public DismissDialogTimerTask(AlertDialog dialog, int callID) {
            this.dialog = dialog;
            this.callID = callID;
        }

        @Override
        public void run() {
            if (null != dialog) {
                dialog.dismiss();
            }

            CallMgr.getInstance().rejectAddVideo(this.callID);
            LogUtil.i(UIConstants.DEMO_TAG, "dialog time out disAgreeUpg");
        }
    }

    private void hideViews() {
        ImageView deleteNumber = (ImageView) findViewById(R.id.delete_panel_btn);
        ImageView audioBtn = (ImageView) findViewById(R.id.call_audio_btn);
        ImageView videoBtn = (ImageView) findViewById(R.id.call_video_btn);

        audioBtn.setVisibility(View.GONE);
        videoBtn.setVisibility(View.GONE);
        deleteNumber.setVisibility(View.GONE);
        CallConstant.CallStatus callStatus = CallMgr.getInstance().getCallStatus(mCallID);
        boolean isCall = (CallConstant.CallStatus.AUDIO_CALLING == callStatus || CallConstant.CallStatus.VIDEO_CALLING == callStatus);

        mMuteArea.setVisibility(isCall ? View.VISIBLE : View.GONE);
        mPlateButton.setVisibility(isCall ? View.VISIBLE : View.GONE);
        mSpeakerButton.setVisibility(View.VISIBLE);
        mUpgradeVideoArea.setVisibility(isCall ? View.VISIBLE : View.GONE);
        mBlindTransferButton.setVisibility(isCall ? View.VISIBLE : View.GONE);
        mHoldCallButton.setVisibility(isCall ? View.VISIBLE : View.GONE);
        mTransferMeeting.setVisibility(isCall ? View.VISIBLE : View.GONE);
    }

    private void refreshMuteStatus()
    {
        mMuteStatus = mCallFunc.isMuteStatus();
        mMuteArea.setActivated(mMuteStatus);
    }

    private void refreshSpeakerStatus()
    {
        mSpeakerButton.setActivated(CallMgr.getInstance().getCurrentAudioRoute() == CallConstant.TYPE_LOUD_SPEAKER);
    }

    @Override
    public void initializeData() {
        mCallFunc = CallFunc.getInstance();

        Intent intent = getIntent();
        CallInfo callInfo = (CallInfo) intent.getSerializableExtra(UIConstants.CALL_INFO);

        mCallNumber = callInfo.getPeerNumber();
        mDisplayName = callInfo.getPeerDisplayName();
        mIsVideoCall = callInfo.isVideoCall();
        mCallID = callInfo.getCallID();
        mConfID = callInfo.getConfID();
        mIsConfCall = callInfo.isFocus();
        if ((null != mConfID) && (!callInfo.getConfID().equals(""))) {
            mConfToCallHandle = Integer.parseInt(callInfo.getConfID());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.plate_btn:
                mPlateControl.showDialPlate();
                break;
            case R.id.hide_dial_btn:
                mPlateControl.hideDialPlate();
                break;
            case R.id.mute_btn:
                if (CallMgr.getInstance().muteMic(mCallID, !mMuteStatus)) {
                    mCallFunc.setMuteStatus(!mMuteStatus);
                    refreshMuteStatus();
                }
                break;
            case R.id.speaker_btn:
                CallMgr.getInstance().switchAudioRoute();
                refreshSpeakerStatus();
                break;
            case R.id.upgrade_video_btn:
                CallMgr.getInstance().addVideo(mCallID);
                break;
            case R.id.blind_transfer:
                final EditText editText = new EditText(this);
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                new AlertDialog.Builder(this)
                        .setTitle(R.string.ipt_warning)
                        .setMessage(R.string.blind_transfer_number)
                        .setView(editText)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setPositiveButton(R.string.ipt_sure, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CallMgr.getInstance().blindTransfer(mCallID, editText.getText().toString().trim());
                            }
                        })
                        .show();
                break;
            case R.id.hold_call:
                if (LocContext.getString(R.string.hold_call).equals(mHoldCallText.getText())) {
                    mHoldCallText.setText(R.string.un_hold_call);
                    CallMgr.getInstance().holdCall(mCallID);
                } else if (LocContext.getString(R.string.un_hold_call).equals(mHoldCallText.getText())) {
                    mHoldCallText.setText(R.string.hold_call);
                    CallMgr.getInstance().unHoldCall(mCallID);
                }
                break;
            case R.id.transfer_meeting:
                MeetingMgr.getInstance().callTransferToConference(mCallID);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocBroadcast.getInstance().unRegisterBroadcast(this, mActions);
        dismissDialog(mDialog);
    }

    protected void dismissDialog(AlertDialog dialog) {
        if (null != dialog) {
            dialog.dismiss();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocBroadcast.getInstance().registerBroadcast(this, mActions);
    }

    @Override
    public void onReceive(final String broadcastName, final Object obj)
    {
        switch (broadcastName)
        {
            case CustomBroadcastConstants.ACTION_CALL_CONNECTED:
                mHandler.sendMessage(mHandler.obtainMessage(CALL_CONNECTED, obj));
                break;
            case CustomBroadcastConstants.CALL_MEDIA_CONNECTED:
                mHandler.sendMessage(mHandler.obtainMessage(MEDIA_CONNECTED, obj));
                break;

            case CustomBroadcastConstants.CONF_CALL_CONNECTED:
                finish();
                break;
            case CustomBroadcastConstants.ACTION_CALL_END:
                finish();
                break;
            case CustomBroadcastConstants.CALL_UPGRADE_ACTION:
                mHandler.sendEmptyMessage(CALL_UPGRADE);
                break;
            case CustomBroadcastConstants.HOLD_CALL_RESULT:
                if ("HoldSuccess".equals(obj))
                {
                    mCallNumberTv.setTag("Hold");
                    mHandler.sendEmptyMessage(HOLD_CALL_SUCCESS);
                }else if ("UnHoldSuccess".equals(obj))
                {
                    mCallNumberTv.setTag("UnHold");
                    mHandler.sendEmptyMessage(HOLD_CALL_SUCCESS);
                }else if ("VideoHoldSuccess".equals(obj))
                {
                    mHandler.sendEmptyMessage(VIDEO_HOLD_CALL_SUCCESS);
                }
                break;
            case CustomBroadcastConstants.BLD_TRANSFER_RESULT:
                if ("BldTransferSuccess".equals(obj))
                {
                    mHandler.sendEmptyMessage(BLD_TRANSFER_SUCCESS);
                }
                else if ("BldTransferFailed".equals(obj))
                {
                    mHandler.sendEmptyMessage(BLD_TRANSFER_FAILED);
                }
                break;

            case CustomBroadcastConstants.CALL_TRANSFER_TO_CONFERENCE:
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if(CallMgr.getInstance().isResumeHold()){
                            CallMgr.getInstance().unHoldCall(CallMgr.getInstance().getOriginal_CallId());
                        }
                    }
                },20000);
                break;
            default:
                break;
        }
    }

    private void showButtons()
    {
        mMuteArea.setVisibility(View.VISIBLE);
        mPlateButton.setVisibility(View.VISIBLE);
        mSpeakerButton.setVisibility(View.VISIBLE);
        mUpgradeVideoArea.setVisibility(View.VISIBLE);
        mBlindTransferButton.setVisibility(View.VISIBLE);
        mHoldCallButton.setVisibility(View.VISIBLE);
        mTransferMeeting.setVisibility(View.VISIBLE);
        refreshSpeakerStatus();
    }

}
