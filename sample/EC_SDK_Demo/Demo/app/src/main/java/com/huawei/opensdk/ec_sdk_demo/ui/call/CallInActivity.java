package com.huawei.opensdk.ec_sdk_demo.ui.call;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.huawei.opensdk.callmgr.CallMgr;
import com.huawei.opensdk.demoservice.MeetingMgr;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.util.DialogUtil;
import com.huawei.opensdk.ec_sdk_demo.util.MediaUtil;


public class CallInActivity extends BaseMediaActivity
{
    private AlertDialog mDialog;
    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            if (KeyEvent.KEYCODE_BACK == msg.what)
            {
                mDialog = DialogUtil.generateDialog(CallInActivity.this, R.string.ntf_end_call,
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                if (mIsConfCall)
                                {
                                    finish();
                                }
                                CallMgr.getInstance().endCall(mCallID);
                            }
                        });
                mDialog.show();
            }
        }
    };

    @Override
    public void initializeData()
    {
        super.initializeData();
    }

    @Override
    public void initializeComposition()
    {
        super.initializeComposition();

        mRejectBtn.setVisibility(View.VISIBLE);
        mAudioAcceptCallArea.setVisibility(View.VISIBLE);
        mAudioAcceptCallArea.setActivated(false);

        if (mIsVideoCall == true)
        {
            mVideoAcceptCallArea.setVisibility(View.VISIBLE);
            mVideoAcceptCallArea.setActivated(true);
        }

        mDivertCallArea.setVisibility(View.VISIBLE);

        mCallNameTv.setText(null == mDisplayName ? "" : mDisplayName);
        mCallNumberTv.setText(null == mCallNumber ? "" : mCallNumber);

        mRejectBtn.setOnClickListener(this);
        mAudioAcceptCallArea.setOnClickListener(this);
        mVideoAcceptCallArea.setOnClickListener(this);
        mDivertCallArea.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        super.onClick(v);
        switch (v.getId())
        {
            case R.id.reject_btn:
                if(0 == mConfToCallHandle){
                    CallMgr.getInstance().endCall(mCallID);
                }else {
                    if (mUseSdkMethod)
                    {
                        CallMgr.getInstance().stopPlayRingingTone();
                        CallMgr.getInstance().stopPlayRingBackTone();
                    }
                    else
                    {
                        MediaUtil.getInstance().stopPlayFromRawFile();
                    }

                    MeetingMgr.getInstance().rejectConf();
                }

                finish();
                break;
            case R.id.audio_accept_call_area:
                if(0 == mConfToCallHandle){
                    CallMgr.getInstance().answerCall(mCallID, false);
                }else {
                    if (mUseSdkMethod)
                    {
                        CallMgr.getInstance().stopPlayRingingTone();
                        CallMgr.getInstance().stopPlayRingBackTone();
                    }
                    else
                    {
                        MediaUtil.getInstance().stopPlayFromRawFile();
                    }

                    MeetingMgr.getInstance().acceptConf(false);
                    finish();
                }
                break;

            case R.id.video_accept_call_area:
                if(0 == mConfToCallHandle)
                {
                    CallMgr.getInstance().answerCall(mCallID, mIsVideoCall);
                }
                else
                {
                    if (mUseSdkMethod)
                    {
                        CallMgr.getInstance().stopPlayRingingTone();
                        CallMgr.getInstance().stopPlayRingBackTone();
                    }
                    else
                    {
                        MediaUtil.getInstance().stopPlayFromRawFile();
                    }

                    MeetingMgr.getInstance().acceptConf(mIsVideoCall);
                    finish();
                }
                break;

            case R.id.divert_call_area:
                final EditText editText = new EditText(this);
                new AlertDialog.Builder(this)
                        .setView(editText)
                        .setTitle("Please input divert number")
                        .setPositiveButton("sure", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String number = editText.getText().toString();
                                CallMgr.getInstance().divertCall(mCallID, number);
                            }
                        })
                        .setNegativeButton("exit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed()
    {
        mHandler.sendEmptyMessage(KeyEvent.KEYCODE_BACK);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        dismissDialog(mDialog);
        mHandler.removeCallbacksAndMessages(null);
    }
}
