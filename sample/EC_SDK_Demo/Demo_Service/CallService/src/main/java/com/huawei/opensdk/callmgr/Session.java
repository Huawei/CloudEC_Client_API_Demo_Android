package com.huawei.opensdk.callmgr;

import com.huawei.ecterminalsdk.base.TsdkDtmfTone;
import com.huawei.ecterminalsdk.models.call.TsdkCall;
import com.huawei.opensdk.commonservice.util.LogUtil;

/**
 * This class is about call session
 * 呼叫会话类
 */
public class Session {
    private static final String TAG = Session.class.getSimpleName();

    /**
     * call object
     * 呼叫信息
     */
    private TsdkCall tsdkCall;

    /**
     * call type
     * 呼叫类型
     */
    private CallConstant.CallStatus callStatus = CallConstant.CallStatus.IDLE;

    /**
     * hold video
     * 是否视频保持
     */
    private boolean isVideoHold;

    /**
     * Blind transfer
     * 是否盲转
     */
    private boolean isBlindTransfer;


    private long callId;

    public Session(TsdkCall tsdkCall){
        this.tsdkCall = tsdkCall;
        this.callId = tsdkCall.getCallInfo().getCallId();
    }

    public TsdkCall getTsdkCall() {
        return tsdkCall;
    }

    public long getCallID() {
        return this.callId;
    }

    public CallConstant.CallStatus getCallStatus() {
        return callStatus;
    }

    public void setCallStatus(CallConstant.CallStatus callStatus) {
        this.callStatus = callStatus;
    }

    public boolean isVideoHold() {
        return isVideoHold;
    }

    public void setVideoHold(boolean videoHold) {
        isVideoHold = videoHold;
    }

    public boolean isBlindTransfer() {
        return isBlindTransfer;
    }

    public void setBlindTransfer(boolean blindTransfer) {
        this.isBlindTransfer = blindTransfer;
    }

    /**
     * This method is used to answer the call
     * @param isVideo
     * @return
     */
    public boolean answerCall(boolean isVideo)
    {
        CallMgr.getInstance().setDefaultAudioRoute(isVideo);
        if (isVideo)
        {
            initVideoWindow();
            VideoMgr.getInstance().setVideoOrient(getCallID(), CallConstant.FRONT_CAMERA);
        }
        int result = tsdkCall.answerCall(isVideo);
        if (result != 0)
        {
            LogUtil.e(TAG, "acceptCall return failed, result = " + result);
            return false;
        }
        return true;
    }

    /**
     * This method is used to end Call
     * @return
     */
    public boolean endCall()
    {
        int result = tsdkCall.endCall();
        if (result != 0)
        {
            LogUtil.e(TAG, "endCall return failed, result = " + result);
            return false;
        }
        return true;
    }


    /**
     * This method is used to launched divert Call
     *  发起偏转呼叫
     *
     * divert call
     * @param divertNumber
     * @return
     */
    public boolean divertCall(String divertNumber)
    {
        int result = tsdkCall.divertCall(divertNumber);
        if (result != 0)
        {
            LogUtil.e(TAG, "divertCall return failed, result = " + result);
            return false;
        }
        return true;
    }

    /**
     * start blind transfer request
     * 发起盲转呼叫请求
     *
     * @param transferNumber 盲转号码
     * @return
     */
    public boolean blindTransfer(String transferNumber)
    {
        int result = tsdkCall.blindTransfer(transferNumber);
        if (result != 0)
        {
            LogUtil.e(TAG, "blindTransfer return failed, result = " + result);
            return false;
        }
        return true;
    }

    /**
     * Call hold
     * 呼叫保持
     *
     * @return
     */
    public boolean holdCall()
    {
        int result = tsdkCall.holdCall();
        if (result != 0)
        {
            LogUtil.e(TAG, "holdCall return failed, result = " + result);
            return false;
        }
        return true;
    }


    /**
     * Cancel Call hold
     * 取消呼叫保持
     *
     * @return
     */
    public boolean unHoldCall()
    {
        int result = tsdkCall.unholdCall();
        if (result != 0)
        {
            LogUtil.e(TAG, "unholdCall return failed, result = " + result);
            return false;
        }
        return true;
    }

    /**
     * send DTMF during call
     * 二次拨号
     *
     * @param code  （0到9，*为10,#为11）
     * @return
     */
    public boolean reDial(int code)
    {
        TsdkDtmfTone tsdkDtmfTone = TsdkDtmfTone.enumOf(code);
        if (null == tsdkDtmfTone)
        {
            LogUtil.e(TAG, "tsdkDtmfTone is null");
            return false;
        }
        LogUtil.d(TAG, "Dtmf Tone ：" + tsdkDtmfTone.getIndex());
        int result = tsdkCall.sendDtmf(tsdkDtmfTone);
        if (result != 0)
        {
            LogUtil.e(TAG, "sendDTMF return failed, result = " + result);
            return false;
        }
        return true;
    }

    /**
     * add video
     * 音频转视频请求
     *
     * @return
     */
    public boolean addVideo()
    {
        initVideoWindow();

        int result = tsdkCall.addVideo();
        if (result != 0)
        {
            LogUtil.e(TAG, "addVideo return failed, result = " + result);
            return false;
        }

        setCallStatus(CallConstant.CallStatus.VIDEO_CALLING);
        return true;
    }

    /**
     * delet video
     * 删除视频请求
     *
     * @return
     */
    public boolean delVideo()
    {
        int result = tsdkCall.delVideo();
        if (result != 0)
        {
            LogUtil.e(TAG, "delVideo return failed, result = " + result);
            return false;
        }

        setCallStatus(CallConstant.CallStatus.AUDIO_CALLING);

        return true;
    }

    /**
     * Reject Audio Transfer Video Call
     * 拒绝音频转视频呼叫
     *
     * @return
     */
    public boolean rejectAddVideo()
    {
        int result = tsdkCall.replyAddVideo(false);
        if (result != 0)
        {
            LogUtil.e(TAG, "replyAddVideo(reject) return failed, result = " + result);
            return false;
        }
        return true;
    }

    /**
     * Agree to Audio transfer video Call
     * 同意音频转视频呼叫
     *
     * @return
     */
    public boolean acceptAddVideo()
    {
        initVideoWindow();

        int result = tsdkCall.replyAddVideo(true);
        if (result != 0)
        {
            LogUtil.e(TAG, "replyAddVideo(accept) return failed, result = " + result);
            return false;
        }
        return true;
    }


    /**
     * set media microphone mute
     * 设置(或取消)麦克风静音
     *
     * @param mute
     * @return
     */
    public boolean muteMic(boolean mute)
    {
        int result = tsdkCall.muteMic(mute);
        if (result != 0)
        {
            LogUtil.e(TAG, "mediaMuteMic return failed, result = " + result);
            return false;
        }
        return true;
    }


    /**
     * set media speaker mute
     * 设置(或取消)扬声器静音
     *
     * @param mute
     * @return
     */
    public boolean muteSpeak(boolean mute)
    {

        return true;
    }


    /**
     * switch Camera
     * 切换摄像头
     *
     * @param cameraIndex       设备下标
     */
    public void switchCamera(int cameraIndex)
    {

    }

    /**
     * Initializing the video window
     * 初始化视频窗口
     */
    public void initVideoWindow()
    {
        VideoMgr.getInstance().initVideoWindow(getCallID());
    }


}
