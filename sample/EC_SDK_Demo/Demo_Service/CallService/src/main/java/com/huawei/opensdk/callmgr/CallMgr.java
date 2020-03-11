package com.huawei.opensdk.callmgr;

import android.text.TextUtils;

import com.huawei.ecterminalsdk.base.TsdkCallStatisticInfo;
import com.huawei.ecterminalsdk.base.TsdkMobileAuidoRoute;
import com.huawei.ecterminalsdk.base.TsdkVideoOrientation;
import com.huawei.ecterminalsdk.base.TsdkVideoViewRefresh;
import com.huawei.ecterminalsdk.base.TsdkVideoViewRefreshEvent;
import com.huawei.ecterminalsdk.base.TsdkVideoViewType;
import com.huawei.ecterminalsdk.models.TsdkManager;
import com.huawei.ecterminalsdk.models.call.TsdkCall;
import com.huawei.opensdk.commonservice.util.LogUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is about call manager
 * 呼叫管理类
 */
public class CallMgr implements ICallMgr
{
    private static final String TAG = CallMgr.class.getSimpleName();
    /**
     * Single Case Call Management instance
     * 单例呼叫管理实例
     */
    private static final CallMgr mInstance = new CallMgr();

    /**
     * Call Session map collection  include call ID and call session
     * 呼叫会话集合  呼叫id和呼叫会话的集合
     */
    private Map<Long, Session> callSessionMap = new HashMap<Long, Session>();


    /**
     * UI callback
     * UI回调
     * */
    private ICallNotification mCallNotification;

    /**
     * Call Bell Sound handle
     * 呼叫铃音句柄
     */
    private int ringingToneHandle = -1;

    /**
     * Ring back tone handle
     * 回铃音句柄
     */
    private int ringBackToneHandle = -1;

    /**
     * 是否恢复转会议通话
     */
    private boolean resumeHold = false;

    /**
     * 普通通话呼叫ID，用于通话转会议失败之后，恢复原通话
     */
    private long originalCallId = 0;

    private TsdkCallStatisticInfo currentCallStatisticInfo = new TsdkCallStatisticInfo();

    private CallMgr()
    {
    }

    public static CallMgr getInstance()
    {
        return mInstance;
    }

    public boolean isResumeHold() {
        return resumeHold;
    }

    public void setResumeHold(boolean resumeHold) {
        this.resumeHold = resumeHold;
    }

    public long getOriginalCallId() {
        return originalCallId;
    }

    public void setOriginalCallId(long originalCallId) {
        this.originalCallId = originalCallId;
    }

    public TsdkCallStatisticInfo getCurrentCallStatisticInfo() {
        return currentCallStatisticInfo;
    }

    /**
     * This method is used to determine whether a call exists.
     * 判断当前是否在呼叫中
     * @return
     */
    public boolean isExistCall()
    {
        return TsdkManager.getInstance().getCallManager().isExistCall();
    }

    /**
     * This method is used to store call session
     * @param session 会话信息
     */
    public void putCallSessionToMap(Session session)
    {
        callSessionMap.put(session.getCallID(), session);
    }

    /**
     * This method is used to remove call information
     *
     * @param session           会话信息
     */
    public void removeCallSessionFromMap(Session session)
    {
        callSessionMap.remove(session.getCallID());
    }

    /**
     * This method is used to get call information by ID
     *
     * @param callID            呼叫id
     * @return Session          会话信息
     */
    public Session getCallSessionByCallID(long callID)
    {
        return callSessionMap.get(callID);
    }

    /**
     * This method is used to Video Destroy.
     * 释放视频资源
     */
    public void videoDestroy()
    {
        VideoMgr.getInstance().clearCallVideo();
    }

    /**
     * This method is used to gets video device.
     * 获取视频设备
     * @return the video device
     */
    public VideoMgr getVideoDevice()
    {
        return VideoMgr.getInstance();
    }

    @Override
    public void regCallServiceNotification(ICallNotification callNotification)
    {
        this.mCallNotification = callNotification;
    }

    /**
     * This method is used to set the default audio output device
     * 设置默认的音视频路由
     * @param isVideoCall
     */
    public void setDefaultAudioRoute(boolean isVideoCall)
    {
        //获取移动音频路由设备
        TsdkMobileAuidoRoute currentAudioRoute = TsdkManager.getInstance().getCallManager().getMobileAudioRoute();

        if (isVideoCall)
        {
            //如果当前是听筒，则切换默认设备为扬声器
            if (currentAudioRoute == TsdkMobileAuidoRoute.TSDK_E_MOBILE_AUDIO_ROUTE_EARPIECE)
            {
                //This method is used to set mobile audio route
                //设置移动音频路由设备
                TsdkManager.getInstance().getCallManager().setMobileAudioRoute(TsdkMobileAuidoRoute.TSDK_E_MOBILE_AUDIO_ROUTE_LOUDSPEAKER);
            }
        }
        else
        {
            //This method is used to set mobile audio route
            //设置移动音频路由设备
            TsdkManager.getInstance().getCallManager().setMobileAudioRoute(TsdkMobileAuidoRoute.TSDK_E_MOBILE_AUDIO_ROUTE_DEFAULT);
        }
    }

    /**
     * This method is used to configure Call Parameters
     */
    @Override
    public void configCallServiceParam()
    {
        //Optional
    }

    /**
     * This method is used to switching audio routing devices
     * 切换音频路由设备
     *
     * @return
     */
    @Override
    public int switchAudioRoute()
    {
        //获取移动音频路由设备
        int audioRoute = getCurrentAudioRoute();
        LogUtil.i(TAG, "audioRoute is" + audioRoute);

        if (audioRoute == TsdkMobileAuidoRoute.TSDK_E_MOBILE_AUDIO_ROUTE_LOUDSPEAKER.getIndex())
        {
            setAudioRoute(TsdkMobileAuidoRoute.TSDK_E_MOBILE_AUDIO_ROUTE_DEFAULT);
            LogUtil.i(TAG, "set telReceiver Success");
            return TsdkMobileAuidoRoute.TSDK_E_MOBILE_AUDIO_ROUTE_DEFAULT.getIndex();
        }
        else
        {
            //设置移动音频路由设备
            //set up a mobile audio routing device
            setAudioRoute(TsdkMobileAuidoRoute.TSDK_E_MOBILE_AUDIO_ROUTE_LOUDSPEAKER);

            //设置扬声器输出音量大小
            //set speaker output Volume size
            int setMediaSpeakVolumeResult = TsdkManager.getInstance().getCallManager().setSpeakVolume(60);
            LogUtil.i(TAG, "setMediaSpeakVolumeResult" + setMediaSpeakVolumeResult);
            return TsdkMobileAuidoRoute.TSDK_E_MOBILE_AUDIO_ROUTE_LOUDSPEAKER.getIndex();
        }
    }

    /**
     * This method is used to get mobile audio route
     *
     * 获取移动音频路由设备
     * @return the audio route
     */
    @Override
    public int getCurrentAudioRoute()
    {
        if (null == TsdkManager.getInstance().getCallManager().getMobileAudioRoute())
        {
            LogUtil.e(TAG, "getMobileAudioRoute is null");
            return -1;
        }
        return TsdkManager.getInstance().getCallManager().getMobileAudioRoute().getIndex();
    }

    /**
     * This method is used to get speak volume of media.
     * 获取扬声器输出音量大小
     *
     * @return the media speak volume
     */
    private int getMediaSpeakVolume()
    {
        int ret = (int)TsdkManager.getInstance().getCallManager().getSpeakVolume();
        return ret;
    }

    /**
     * This method is used to get call status
     * 获取呼叫状态
     *
     * @param callID            呼叫id
     * @return
     */
    @Override
    public CallConstant.CallStatus getCallStatus(long callID)
    {
        Session callSession = getCallSessionByCallID(callID);
        if (callSession == null)
        {
            return CallConstant.CallStatus.UNKNOWN;
        }

        return callSession.getCallStatus();
    }


    /**
     * This method is used to make call or make video call
     * 创建一个音频或者视频呼叫
     *
     * @param toNumber          呼叫号码
     * @param isVideoCall       是否是视频
     * @return int 0 success
     */
    @Override
    public synchronized long startCall(String toNumber, boolean isVideoCall)
    {
        if (TextUtils.isEmpty(toNumber))
        {
            LogUtil.e(TAG, "call number is null!");
            return 0;
        }

        //创建一路呼叫
        TsdkCall call = TsdkManager.getInstance().getCallManager().startCall(toNumber, isVideoCall);
        if (call != null)
        {
            Session newSession = new Session(call);
            putCallSessionToMap(newSession);

            setDefaultAudioRoute(isVideoCall);
            if (isVideoCall)
            {
                newSession.initVideoWindow();
            }

            LogUtil.i(TAG, "make call is success.");
            return call.getCallInfo().getCallId();
        }

        LogUtil.e(TAG, "make call is failed.");
        return 0;
    }



    /**
     * This method is used to answer incoming call
     * 接听一路呼叫
     *
     * @param callID            呼叫id
     * @param isVideo           是否是视频
     * @return true:success, false:failed
     */
    @Override
    public boolean answerCall(long callID, boolean isVideo)
    {
        Session callSession = getCallSessionByCallID(callID);
        if (callSession == null)
        {
            return false;
        }

        return callSession.answerCall(isVideo);
    }

    /**
     * This method is used to reject or hangup call
     * 结束呼叫
     *
     * @param callID            呼叫id
     * @return true:success, false:failed
     */
    @Override
    public boolean endCall(long callID)
    {

        TsdkCall tsdkCall = TsdkManager.getInstance().getCallManager().getCallByCallId(callID);
        if (null == tsdkCall)
        {
            mCallNotification.onCallEventNotify(CallConstant.CallEvent.CALL_ENDED_FAILED, null);
            return false;
        }

        int result = tsdkCall.endCall();
        if (result != 0)
        {
            LogUtil.e(TAG, "endCall return failed, result = " + result);
            return false;
        }

        return true;
    }

    /**
     * This method is used to divert incoming call
     * 发起偏转呼叫
     *
     * @param callID            呼叫id
     * @param divertNumber      偏转号码
     * @return true:success, false:failed
     */
    @Override
    public boolean divertCall(long callID, String divertNumber)
    {
        Session callSession = getCallSessionByCallID(callID);
        if (callSession == null)
        {
            return false;
        }

        return callSession.divertCall(divertNumber);
    }

    /**
     * This method is used to blind transfer call
     * 发起盲转呼叫请求
     *
     * @param callID            呼叫id
     * @param transferNumber    盲转号码
     * @return true:success, false:failed
     */
    @Override
    public boolean blindTransfer(long callID, String transferNumber)
    {
        Session callSession = getCallSessionByCallID(callID);
        if (callSession == null)
        {
            return false;
        }

        return callSession.blindTransfer(transferNumber);
    }

    /**
     * This method is used to hold call
     * 保持一路音频呼叫
     *
     * @param callID            呼叫id
     * @return true:success, false:failed
     */
    @Override
    public boolean holdCall(long callID)
    {
        Session callSession = getCallSessionByCallID(callID);
        if (callSession == null)
        {
            return false;
        }

        return callSession.holdCall();
    }

    /**
     * This method is used to hold the video Call
     * 保持一路视频呼叫
     *
     * @param callID            呼叫id
     * @return
     */
    @Override
    public boolean holdVideoCall(long callID)
    {
        Session callSession = getCallSessionByCallID(callID);
        if (callSession == null)
        {
            return false;
        }

        //视频保持先移除视频，待视频移除成功后，再保持
        boolean result = callSession.delVideo();
        if (result)
        {
            callSession.setVideoHold(true);
        }

        return result;
    }

    /**
     * This method is used to unhold call
     * 取消保持呼叫
     *
     * @param callID            呼叫id
     * @return true:success, false:failed
     */
    @Override
    public boolean unHoldCall(long callID)
    {
        Session callSession = getCallSessionByCallID(callID);
        if (callSession == null)
        {
            return false;
        }

        return callSession.unHoldCall();
    }



    /**
     * This method is used to send DTMF tone
     * 二次拨号
     *
     * @param callID            呼叫id
     * @param code              （0到9，*为10,#为11）
     * @return true:success, false:failed
     */
    @Override
    public boolean reDial(long callID, int code)
    {
        Session callSession = getCallSessionByCallID(callID);
        if (callSession == null)
        {
            return false;
        }

        return callSession.reDial(code);
    }

    /**
     * This method is used to request change from an audio call to a video call
     * 音频转视频
     *
     * @param callID            呼叫id
     * @return true:success, false:failed
     */
    @Override
    public boolean addVideo(long callID)
    {
        Session callSession = getCallSessionByCallID(callID);
        if (callSession == null)
        {
            return false;
        }

        return callSession.addVideo();
    }

    /**
     * This method is used to request a change from a video call to an audio call
     * 视频转音频
     *
     * @param callID            呼叫id
     * @return true:success, false:failed
     */
    @Override
    public boolean delVideo(long callID)
    {
        Session callSession = getCallSessionByCallID(callID);
        if (callSession == null)
        {
            return false;
        }

        return callSession.delVideo();
    }

    /**
     * This method is used to reject change from an audio call to a video call
     * 拒绝音频转视频请求
     *
     * @param callID            呼叫id
     * @return true:success, false:failed
     */
    @Override
    public boolean rejectAddVideo(long callID)
    {
        Session callSession = getCallSessionByCallID(callID);
        if (callSession == null)
        {
            return false;
        }

        return callSession.rejectAddVideo();
    }

    /**
     * This method is used to accept change from an audio call to a video call
     * 接受音频转视频请求
     *
     * @param callID            呼叫id
     * @return true:success, false:failed
     */
    @Override
    public boolean acceptAddVideo(long callID)
    {
        Session callSession = getCallSessionByCallID(callID);
        if (callSession == null)
        {
            return false;
        }

        boolean result = callSession.acceptAddVideo();
        if (result)
        {
            setDefaultAudioRoute(true);
            callSession.setCallStatus(CallConstant.CallStatus.VIDEO_CALLING);

            CallInfo callInfo = getCallInfo(callSession.getTsdkCall());
            mCallNotification.onCallEventNotify(CallConstant.CallEvent.OPEN_VIDEO, callInfo);
        }

        return result;
    }


    /**
     * This method is used to set whether mute the microphone
     * 设置麦克风静音
     *
     * @param callID            呼叫id
     * @param mute              是否静音
     * @return true:success, false:failed
     */
    @Override
    public boolean muteMic(long callID, boolean mute)
    {
        Session callSession = getCallSessionByCallID(callID);
        if (callSession == null)
        {
            return false;
        }

        return callSession.muteMic(mute);
    }

    /**
     * This method is used to set whether mute the speaker
     * 设置扬声器静音
     *
     * @param callID            呼叫id
     * @param mute              是否静音
     * @return true:success, false:failed
     */
    @Override
    public boolean muteSpeak(long callID, boolean mute)
    {
        Session callSession = getCallSessionByCallID(callID);
        if (callSession == null)
        {
            return false;
        }

        return callSession.muteSpeak(mute);
    }

    /**
     * This method is used to Local preview
     * 本地预览
     *
     * @param callID
     * @param visible
     */
    @Override
    public void switchLocalView(long callID, boolean visible)
    {

    }

    /**
     * This method is used to switch camera
     * 切换摄像头
     *
     * @param callID            呼叫ID
     * @param cameraIndex       摄像头下标
     */
    @Override
    public void switchCamera(long callID, int cameraIndex)
    {
        TsdkCall call = TsdkManager.getInstance().getCallManager().getCallByCallId(callID);
        VideoMgr.getInstance().switchCamera(call, cameraIndex);

    }


    /**
     * This method is used to open camera
     * 打开摄像头
     *
     * @param callID            呼叫id
     */
    public void openCamera(long callID) {
        TsdkCall call = TsdkManager.getInstance().getCallManager().getCallByCallId(callID);

        VideoMgr.getInstance().openCamera(call);
    }

    /**
     * This method is used to close camera
     * 关闭摄像头
     *
     * @param callID            呼叫id
     */
    public void closeCamera(long callID) {
        TsdkCall call = TsdkManager.getInstance().getCallManager().getCallByCallId(callID);

        VideoMgr.getInstance().closeCamera(call);
    }

    /**
     * [en] This interface is used to get call statistic infomation
     * [cn] 获取呼叫统计信息
     *
     * @param callID
     */
    public void getCallStatisticInfo(long callID){
        TsdkCall call = TsdkManager.getInstance().getCallManager().getCallByCallId(callID);
        if (call == null)
        {
            return;
        }
        if (call.getCallStatisticInfo() != null)
        {
            this.currentCallStatisticInfo = call.getCallStatisticInfo();
        }
    }

    /**
     * This method is used to play ringing tone
     * 播放铃音
     *
     * @param ringingFile       音频文件路径
     */
    @Override
    public void startPlayRingingTone(String ringingFile) {
        int result;
//        TupCallManager callManager = TupMgr.getInstance().getCallManagerIns();

        //处理可能的异常
        if (ringingToneHandle != -1) {
            result = TsdkManager.getInstance().getCallManager().stopPlayMedia(ringingToneHandle);
            if (result != 0) {
                LogUtil.e(TAG, "mediaStopplay is return failed, result = " + result);
            }
        }

        //振铃默认使用扬声器播放
        //Ringing by default using speaker playback
        TsdkManager.getInstance().getCallManager().setMobileAudioRoute(TsdkMobileAuidoRoute.TSDK_E_MOBILE_AUDIO_ROUTE_LOUDSPEAKER);

        //播放指定振铃
        //Play the specified ringing
        ringingToneHandle = TsdkManager.getInstance().getCallManager().startPlayMedia(0, ringingFile);
        if (ringingToneHandle == -1) {
            LogUtil.e(TAG, "mediaStartplay is return failed.");
        }
    }

    /**
     * This method is used to stop play ringing tone
     * 停止播放铃音
     */
    @Override
    public void stopPlayRingingTone() {
        if (ringingToneHandle != -1) {
            int result = TsdkManager.getInstance().getCallManager().stopPlayMedia(ringingToneHandle);
            if (result != 0) {
                LogUtil.e(TAG, "mediaStopPlay is return failed, result = " + result);
            }
            ringingToneHandle = -1;
        }
    }

    /**
     * This method is used to play ring back tone
     * 播放回铃音
     *
     * @param ringingFile       音频文件路径
     */
    @Override
    public void startPlayRingBackTone(String ringingFile) {
        int result;

        //处理可能的异常
        if (ringBackToneHandle != -1) {
            result = TsdkManager.getInstance().getCallManager().stopPlayMedia(ringBackToneHandle);
            if (result != 0) {
                LogUtil.e(TAG, "mediaStopPlay is return failed, result = " + result);
            }
        }

        //回铃音使用默认设备播放
        //Ring tone Use default device playback
        if (!isSupportVideo())
        {
            TsdkManager.getInstance().getCallManager().setMobileAudioRoute(TsdkMobileAuidoRoute.TSDK_E_MOBILE_AUDIO_ROUTE_DEFAULT);
        }

        //播放指定回铃音
        //Play the specified ring tone
        ringBackToneHandle = TsdkManager.getInstance().getCallManager().startPlayMedia(0, ringingFile);
        if (ringBackToneHandle == -1) {
            LogUtil.e(TAG, "mediaStartPlay is return failed.");
        }
    }

    /**
     * This method is used to stop play ring back tone
     * 停止播放回铃音
     */
    @Override
    public void stopPlayRingBackTone() {
        if (ringBackToneHandle != -1) {
            int result = TsdkManager.getInstance().getCallManager().stopPlayMedia(ringBackToneHandle);
            if (result != 0) {
                LogUtil.e(TAG, "mediaStopPlay is return failed, result = " + result);
            }
            ringBackToneHandle = -1;
        }
    }


    /**
     * This method is used to get call information
     * 获取呼叫信息
     * @param call
     * @return
     */
    public CallInfo getCallInfo(TsdkCall call)
    {
        String peerNumber = call.getCallInfo().getPeerNumber();
        String peerDisplayName = call.getCallInfo().getPeerDisplayName();
        boolean isFocus = false;
        boolean isVideoCall = false;
        boolean isCaller = call.getCallInfo().getIsCaller()==1? true:false;

        if (call.getCallInfo().getIsFocus() == 1) {
            isFocus = true;
        }

        if (call.getCallInfo().getIsVideoCall() == 1) {
            isVideoCall = true;
        }

        return new CallInfo.Builder()
                .setCallID(call.getCallInfo().getCallId())
                .setConfID(call.getCallInfo().getConfId())
                .setPeerNumber(peerNumber)
                .setPeerDisplayName(peerDisplayName)
                .setVideoCall(isVideoCall)
                .setFocus(isFocus)
                .setCaller(isCaller)
                .setReasonCode(call.getCallInfo().getReasonCode())
                .build();
    }


    /**
     * This method is used to sets audio route.
     * 设置音频路由
     *
     * @param audioSwitch the audio switch
     * @return the audio route
     */
    private boolean setAudioRoute(TsdkMobileAuidoRoute audioSwitch)
    {
        return TsdkManager.getInstance().getCallManager().setMobileAudioRoute(audioSwitch) == 0;
    }


    /**
     * This method is used to support video.
     * 是否支持视频功能
     *
     * @return the boolean
     */
    private boolean isSupportVideo()
    {
        return VideoMgr.getInstance().isSupportVideo();
    }


    /**************************************************************目前先保留 回调转换完删除 为了好找回调***************************************************************************/

    /**
     * [en]This method is used to handle the call incoming.
     * [cn]处理来电事件
     *
     * @param call              [en]Indicates call info
     *                          [cn]呼叫信息
     * @param maybeVideoCall    [en]Indicates maybe video call
     *                          [cn]是否是视频
     */
    public void handleCallComing(TsdkCall call, Boolean maybeVideoCall){
        LogUtil.i(TAG, "onCallComing");
        if (null == call)
        {
            LogUtil.e(TAG, "onCallComing call is null");
            return;
        }
        Session newSession = new Session(call);
        putCallSessionToMap(newSession);

        CallInfo callInfo = getCallInfo(call);
        callInfo.setMaybeVideoCall(maybeVideoCall);

        mCallNotification.onCallEventNotify(CallConstant.CallEvent.CALL_COMING, callInfo);
    }

    /**
     * [en]This method is used to handle the call out going.
     * [cn]处理呼出事件
     *
     * @param call              [en]Indicates call info
     *                          [cn]呼叫信息
     */
    public void handleCallGoing(TsdkCall call){
        LogUtil.i(TAG, "onCallGoing");
        if (null == call)
        {
            LogUtil.e(TAG, "tupCall obj is null");
            return;
        }
        CallInfo callInfo = getCallInfo(call);
        mCallNotification.onCallEventNotify(CallConstant.CallEvent.CALL_GOING, callInfo);
    }

    /**
     * [en]This method is used to handle call connected
     * [cn]处理通话建立事件
     *
     * @param call              [en]Indicates call info
     *                          [cn]呼叫信息
     */
    public void handleCallConnected(TsdkCall call){
        LogUtil.i(TAG, "onCallConnected");
        if (null == call)
        {
            LogUtil.e(TAG, "call obj is null");
            return;
        }

        CallInfo callInfo = getCallInfo(call);
        Session callSession = getCallSessionByCallID(call.getCallInfo().getCallId());
        if (callSession == null)
        {
            LogUtil.e(TAG, "call session obj is null");
            return;
        }

        if (callInfo.isVideoCall())
        {
            callSession.setCallStatus(CallConstant.CallStatus.VIDEO_CALLING);
        }
        else
        {
            callSession.setCallStatus(CallConstant.CallStatus.AUDIO_CALLING);
        }

        mCallNotification.onCallEventNotify(CallConstant.CallEvent.CALL_CONNECTED, callInfo);
    }

    /**
     * [en]This method is used to handle call ring back
     * [cn]处理响铃音事件
     *
     * @param call              [en]Indicates call info
     *                          [cn]呼叫信息
     */
    public void handleCallRingback(TsdkCall call){
        LogUtil.i(TAG, "onCallRingBack");
        if (null == call)
        {
            LogUtil.e(TAG, "onCallRingBack call is null");
            return;
        }
            if (null != mCallNotification)
            {
                mCallNotification.onCallEventNotify(CallConstant.CallEvent.PLAY_RING_BACK_TONE, null);
            }
    }

    /**
     * [en]This method is used to handle call end
     * [cn]处理通话结束
     *
     * @param call              [en]Indicates call info
     *                          [cn]呼叫信息
     */
    public void handleCallEnded(TsdkCall call){
        LogUtil.i(TAG, "onCallEnded");
        if (null == call)
        {
            LogUtil.e(TAG, "onCallEnded call is null");
            return;
        }
        CallInfo callInfo = getCallInfo(call);
        mCallNotification.onCallEventNotify(CallConstant.CallEvent.CALL_ENDED, callInfo);
    }

    /**
     * [en]This method is used to handle call end destroy
     * [cn]处理呼叫销毁事件
     *
     * @param call              [en]Indicates call info
     *                          [cn]呼叫信息
     */
    public void handleCallDestroy(TsdkCall call){
        LogUtil.i(TAG, "onCallDestroy");
        if (null == call)
        {
            LogUtil.e(TAG, "call obj is null");
            return;
        }
        Session callSession = getCallSessionByCallID(call.getCallInfo().getCallId());
        if (callSession == null)
        {
            LogUtil.e(TAG, "call session obj is null");
            return;
        }

        //从会话列表中移除一路会话
        removeCallSessionFromMap(callSession);
    }

    /**
     * [en]This method is used to handle call rtp created.
     * [cn]处理RTP创建事件
     *
     * @param call              [en]Indicates call info
     *                          [cn]呼叫信息
     */
    public void handleCallRtpCreated(TsdkCall call){
        LogUtil.i(TAG, "onCallRTPCreated");
        if (null == call)
        {
            LogUtil.e(TAG, "tupCall obj is null");
            return;
        }

        CallInfo callInfo = getCallInfo(call);

        mCallNotification.onCallEventNotify(CallConstant.CallEvent.RTP_CREATED, callInfo);
    }

    /**
     * [en]This method is used to handle call audio to video request.
     * [cn]处理音频转视频请求
     *
     * @param call              [en]Indicates call info
     *                          [cn]呼叫信息
     * @param orientType        [en]Indicates orient type
     *                          [cn]视频显示方向类型
     */
    public void handleOpenVideoReq(TsdkCall call, TsdkVideoOrientation orientType){
        LogUtil.i(TAG, "onCallAddVideo");
        if (null == call)
        {
            LogUtil.e(TAG, "onCallAddVideo tupCall is null");
            return;
        }

        //音频转视频
        Session callSession = getCallSessionByCallID(call.getCallInfo().getCallId());
        if (callSession == null)
        {
            LogUtil.e(TAG, "call session obj is null");
            return;
        }

        CallConstant.CallStatus callStatus = callSession.getCallStatus();
        boolean isSupportVideo = isSupportVideo();

        if ((!isSupportVideo) || (CallConstant.CallStatus.AUDIO_CALLING != callStatus))
        {
            callSession.rejectAddVideo();
            return;
        }

        mCallNotification.onCallEventNotify(CallConstant.CallEvent.RECEIVED_REMOTE_ADD_VIDEO_REQUEST, null);

    }

    /**
     * [en]This method is used to handle call audio to video request result.
     * [cn]处理音频转视频结果
     *
     * @param call              [en]Indicates call info
     *                          [cn]呼叫信息
     */
    public void handleOpenVideoInd(TsdkCall call){
        int isVideo = call.getCallInfo().getIsVideoCall(); // 1:video, 0: audio
        long callId  = call.getCallInfo().getCallId();
        LogUtil.i(TAG,  "isVideo: " + isVideo + "callId: " + callId);

        Session callSession = getCallSessionByCallID(callId);
        if (callSession == null)
        {
            return;
        }
        CallInfo callInfo = getCallInfo(call);//audio --> video success
        LogUtil.i(TAG, "Upgrade To Video Call");
        VideoMgr.getInstance().setVideoOrient(callId, VideoMgr.getInstance().getCurrentCameraIndex());

        callSession.setCallStatus(CallConstant.CallStatus.VIDEO_CALLING);
        mCallNotification.onCallEventNotify(CallConstant.CallEvent.OPEN_VIDEO, callInfo);
    }

    /**
     * [en]This method is used to handle call video to audio request result
     * [cn]处理视频转音频结果
     *
     * @param call              [en]Indicates call info
     *                          [cn]呼叫信息
     */
    public void handleCloseVideoInd(TsdkCall call){
        if (null == call)
        {
            LogUtil.e(TAG, "onCallDelVideo tupCall is null");
            return;
        }

        Session callSession = getCallSessionByCallID(call.getCallInfo().getCallId());
        if (callSession == null)
        {
            LogUtil.e(TAG, "call session obj is null");
            return;
        }

        callSession.setCallStatus(CallConstant.CallStatus.AUDIO_CALLING);

        //Clear video data
        VideoMgr.getInstance().clearCallVideo();

        if (null != mCallNotification)
        {
            CallInfo callInfo = getCallInfo(call);
            mCallNotification.onCallEventNotify(CallConstant.CallEvent.CLOSE_VIDEO, callInfo);
        }

        if (callSession.isVideoHold())
        {
            callSession.holdCall();
        }
    }

    /**
     * [en]This method is used to handle call window refresh
     * [cn]刷新窗口信息
     *
     * @param call              [en]Indicates call info
     *                          [cn]呼叫信息
     * @param refreshInfo       [en]Indicates refresh Info
     *                          [cn]刷新信息
     */
    public void handleRefreshViewInd(TsdkCall call, TsdkVideoViewRefresh refreshInfo){
        LogUtil.i(TAG, "refreshLocalView");
        TsdkVideoViewType mediaType = TsdkVideoViewType.enumOf(refreshInfo.getViewType());
        TsdkVideoViewRefreshEvent eventType = TsdkVideoViewRefreshEvent.enumOf(refreshInfo.getEvent());
        long callId = call.getCallInfo().getCallId();

        switch (mediaType)
        {
            case TSDK_E_VIEW_LOCAL_PREVIEW: //local video preview
            case TSDK_E_VIEW_VIDEO_VIEW: //general video
                if (eventType == TsdkVideoViewRefreshEvent.TSDK_E_VIDEO_LOCAL_VIEW_ADD) //add local view
                {
                    //VideoDeviceManager.getInstance().refreshLocalVideo(true, callId);
                    mCallNotification.onCallEventNotify(CallConstant.CallEvent.ADD_LOCAL_VIEW, callId);
                }
                else //remove local view
                {
                    //VideoDeviceManager.getInstance().refreshLocalVideo(false, callId);
                    mCallNotification.onCallEventNotify(CallConstant.CallEvent.DEL_LOCAL_VIEW, callId);
                }
                break;

            case TSDK_E_VIEW_AUX_DATA_VIEW: //auxiliary data
                break;

            default:
                break;
        }

    }

    /**
     * [en]This method is used to handle call hold success
     * [cn]处理呼叫保持成功事件
     *
     * @param call              [en]Indicates call info
     *                          [cn]呼叫信息
     */
    public void handleHoldSuccess(TsdkCall call){
        LogUtil.i(TAG, "handleHoldSuccess");
        CallInfo callInfo = getCallInfo(call);
        Session callSession = getCallSessionByCallID(callInfo.getCallID());
        if (callSession.isVideoHold())
        {
            mCallNotification.onCallEventNotify(CallConstant.CallEvent.VIDEO_HOLD_SUCCESS, callInfo);
        }
        else
        {
            mCallNotification.onCallEventNotify(CallConstant.CallEvent.AUDIO_HOLD_SUCCESS, callInfo);
        }
    }

    /**
     * [en]This method is used to handle call hold failed
     * [cn]处理呼叫保持失败事件
     *
     * @param call              [en]Indicates call info
     *                          [cn]呼叫信息
     */
    public void handleHoldFailed(TsdkCall call){
        LogUtil.i(TAG, "handleHoldFailed");
        CallInfo callInfo = getCallInfo(call);
        Session callSession = getCallSessionByCallID(callInfo.getCallID());
        if (callSession.isVideoHold())
        {
            callSession.setVideoHold(false);
            //保持失败，只直接通知UI失败，不自动动恢复视频
            mCallNotification.onCallEventNotify(CallConstant.CallEvent.VIDEO_HOLD_FAILED, callInfo);
        }
        else
        {
            mCallNotification.onCallEventNotify(CallConstant.CallEvent.AUDIO_HOLD_FAILED, callInfo);
        }
    }

    /**
     * [en]This method is used to handle call unhold success
     * [cn]处理取消呼叫保持成功事件
     *
     * @param call              [en]Indicates call info
     *                          [cn]呼叫信息
     */
    public void handleUnholdSuccess(TsdkCall call){
        LogUtil.i(TAG, "handleUnholdSuccess");
        long callId = call.getCallInfo().getCallId();
        Session callSession = getCallSessionByCallID(callId);
        if (callSession == null)
        {
            LogUtil.e(TAG, "call session obj is null");
            return;
        }

        //如果此保持发起时是“视频保持”，则再在“保持恢复”后，请求远端“增加视频”
        if (callSession.isVideoHold())
        {
            addVideo(callId);
            callSession.setVideoHold(false);
        }

        //调试音频
        CallInfo callInfo = getCallInfo(call);
        mCallNotification.onCallEventNotify(CallConstant.CallEvent.UN_HOLD_SUCCESS, callInfo);
    }

    /**
     * [en]This method is used to handle call unhold failed
     * [cn]处理取消呼叫保持失败事件
     *
     * @param call              [en]Indicates call info
     *                          [cn]呼叫信息
     */
    public void handleUnholdFailed(TsdkCall call){
        LogUtil.i(TAG, "handleUnholdFailed");

        CallInfo callInfo = getCallInfo(call);
        mCallNotification.onCallEventNotify(CallConstant.CallEvent.UN_HOLD_FAILED, callInfo);
    }

    /**
     * [en]This method is used to handle call divert failed
     * [cn]处理偏转失败事件
     *
     * @param call              [en]Indicates call info
     *                          [cn]呼叫信息
     */
    public void handleDivertFailed(TsdkCall call){
        LogUtil.i(TAG, "handleDivertFailed");

        CallInfo callInfo = getCallInfo(call);
        mCallNotification.onCallEventNotify(CallConstant.CallEvent.DIVERT_FAILED, callInfo);
    }

    /**
     * [en]This method is used to handle  blind transfer success
     * [cn]处理盲转成功事件
     *
     * @param call              [en]Indicates call info
     *                          [cn]呼叫信息
     */
    public void handleBldTransferSuccess(TsdkCall call){
        LogUtil.i(TAG, "handleBldTransferSuccess");

        CallInfo callInfo = getCallInfo(call);
        mCallNotification.onCallEventNotify(CallConstant.CallEvent.BLD_TRANSFER_SUCCESS, callInfo);
    }

    /**
     * [en]This method is used to handle  blind transfer success failed
     * [cn]处理盲转失败事件
     *
     * @param call              [en]Indicates call info
     *                          [cn]呼叫信息
     */
    public void handleBldTransferFailed(TsdkCall call){
        LogUtil.i(TAG, "handleBldTransferFailed");

        CallInfo callInfo = getCallInfo(call);
        mCallNotification.onCallEventNotify(CallConstant.CallEvent.BLD_TRANSFER_FAILED, callInfo);
    }

    /**
     * [en]This method is used to handle  remote reject audio to video
     * [cn]远端拒绝音频转视频
     *
     * @param call              [en]Indicates call info
     *                          [cn]呼叫信息
     */
    public void handleRefuseOpenVideoInd(TsdkCall call){

        VideoMgr.getInstance().clearCallVideo();

        Session callSession = getCallSessionByCallID(call.getCallInfo().getCallId());
        callSession.setCallStatus(CallConstant.CallStatus.AUDIO_CALLING);

        CallInfo callInfo = getCallInfo(call);
        mCallNotification.onCallEventNotify(CallConstant.CallEvent.REMOTE_REFUSE_ADD_VIDEO_SREQUEST, callInfo);

    }

    /**
     * 网络状态上报
     */
    public void handleUpDateCallStatisticInfo(long signalStrength, TsdkCallStatisticInfo statisticInfo){
        this.currentCallStatisticInfo = statisticInfo;
        mCallNotification.onCallEventNotify(CallConstant.CallEvent.STATISTIC_LOCAL_QOS, signalStrength);
    }

}
