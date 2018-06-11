package com.huawei.opensdk.demoservice.data;

import android.util.Log;

import com.huawei.opensdk.commonservice.util.LogUtil;

import common.TupCallParam;
import tupsdk.TupCall;

/**
 * This class is about conference session
 * 会议会话消息
 */
public class ConfSession
{
    private static final int VIDEO_CALL = 1;
    private static final String TAG = ConfSession.class.getSimpleName();

    private TupCall mTupCall;
    private String mCallID;

    
    public ConfSession(TupCall mTupCall)
    {
        this.mTupCall = mTupCall;
        this.mCallID = String.valueOf(mTupCall.getCallId());
    }

    public TupCall getmTupCall()
    {
        return mTupCall;
    }

    public void setmTupCall(TupCall mTupCall)
    {
        this.mTupCall = mTupCall;
    }

    public String getmCallID()
    {
        return mCallID;
    }

    public void setmCallID(String mCallID)
    {
        this.mCallID = mCallID;
    }

    public String getFromNumber()
    {
        return mTupCall.getFromNumber();
    }

    public String getDisplayName()
    {
        return mTupCall.getFromDisplayName();
    }


    /**
     * This method is used to alertingCall.
     * @return the string
     */
    public String alertingCall()
    {
        if (null == mTupCall)
        {
            Log.e(TAG, "mTupCall is null, return");
            return String.valueOf(TupCallParam.CALL_TUP_RESULT.TUP_FAIL);
        }
        else
        {
            int result = mTupCall.alertingCall();
            return String.valueOf(result);
        }
    }

    /**
     * This method is used to answer.
     * @param isVideo is Video
     * @return the string
     */
    public String answer(boolean isVideo)
    {
        int iVideoCall = 0;
        if (isVideo)
        {
            iVideoCall = VIDEO_CALL;
        }
        int result = mTupCall.acceptCall(iVideoCall);
        return String.valueOf(result);
    }

    public boolean endCall()
    {
        int result = mTupCall.endCall();
        return result == 0;
    }
}
