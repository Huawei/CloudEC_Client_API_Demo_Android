package com.huawei.opensdk.ec_sdk_demo.logic.conference.mvp;


import com.huawei.ecterminalsdk.base.TsdkAttendee;
import com.huawei.ecterminalsdk.base.TsdkConfAsActionType;
import com.huawei.ecterminalsdk.base.TsdkConfAsStateInfo;
import com.huawei.ecterminalsdk.base.TsdkConfEndReason;
import com.huawei.ecterminalsdk.base.TsdkConfMediaType;
import com.huawei.ecterminalsdk.base.TsdkConfRole;
import com.huawei.ecterminalsdk.base.TsdkConfShareSubState;
import com.huawei.ecterminalsdk.base.TsdkConfSvcWatchAttendee;
import com.huawei.ecterminalsdk.base.TsdkConfSvcWatchInfo;
import com.huawei.ecterminalsdk.base.TsdkDocShareDelDocInfo;
import com.huawei.ecterminalsdk.base.TsdkWbDelDocInfo;
import com.huawei.opensdk.callmgr.CallMgr;
import com.huawei.opensdk.commonservice.common.LocContext;
import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcastReceiver;
import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.demoservice.ConfBaseInfo;
import com.huawei.opensdk.demoservice.ConfConstant;
import com.huawei.opensdk.demoservice.MeetingMgr;
import com.huawei.opensdk.demoservice.Member;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.floatView.util.DeviceUtil;
import com.huawei.opensdk.ec_sdk_demo.ui.base.ActivityStack;
import com.huawei.opensdk.ec_sdk_demo.ui.base.MVPBasePresenter;
import com.huawei.opensdk.ec_sdk_demo.ui.conference.DataConfActivity;
import com.huawei.opensdk.ec_sdk_demo.util.ActivityUtil;
import com.huawei.opensdk.loginmgr.LoginMgr;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import static com.huawei.opensdk.ec_sdk_demo.common.UIConstants.CANCEL_MUTE_MIC;
import static com.huawei.opensdk.ec_sdk_demo.common.UIConstants.MUTE_MIC_AND_SPEAK;


public abstract class ConfManagerBasePresenter extends MVPBasePresenter<IConfManagerContract.ConfManagerView>
        implements IConfManagerContract.ConfManagerPresenter
{
    private static final int REMOTE_DISPLAY = 0;
    private static final int SMALL_DISPLAY_01 = 1;
    private static final int SMALL_DISPLAY_02 = 2;
    private static final int SMALL_DISPLAY_03 = 3;

    private String confID;
    private int currentShowSmallWndCount = 0;
    private List<Long> svcLabel = MeetingMgr.getInstance().getSvcConfInfo().getSvcLabel();
    private String remoteDisplay = "";
    private String smallDisplay_01 = "";
    private String smallDisplay_02 = "";
    private String smallDisplay_03 = "";
    private boolean isResuming = false; // 是否在重新login中
    private boolean isNeedConfigIp = false; // 是否需要重新配置本地ip
    private boolean isSharing = false; // 是否有人正在共享(包括:观看共享和主动共享)
    private String currentBroadcastNumber = ""; // 当前被广播的用户号码
    private Map<String, Integer> watchMap = new IdentityHashMap<>();

    private boolean mNeedUnMuteConf; // 收到第三方电话呼叫，挂断后判断是否需要解除静音状态

    protected String[] broadcastNames;
    protected LocBroadcastReceiver receiver = new LocBroadcastReceiver()
    {
        @Override
        public void onReceive(String broadcastName, Object obj)
        {
            int result;
            switch (broadcastName)
            {
                case CustomBroadcastConstants.CONF_STATE_UPDATE:
                    LogUtil.i(UIConstants.DEMO_TAG, "conf state update ");

                    String conferenceID = (String)obj;
                    if (!conferenceID.equals(confID))
                    {
                        return;
                    }

                    //判断会议状态，如果会议结束，则关闭会议界面
                    ConfBaseInfo confBaseInfo = MeetingMgr.getInstance().getCurrentConferenceBaseInfo();
                    if (null == confBaseInfo)
                    {
                        return;
                    }
                    getView().updateConfTypeIcon(confBaseInfo);

                    List<Member> memberList = MeetingMgr.getInstance().getCurrentConferenceMemberList();
                    if (memberList == null)
                    {
                        return;
                    }

                    getView().refreshMemberList(memberList);
                    for (Member member : memberList)
                    {
                        if (member.isSelf())
                        {
                            getView().updateMuteButton(member.isMute());
                            getView().updateUpgradeConfBtn(member.isInDataConference());
                            getView().updateAttendeeButton(member);
                        }
                    }

                    //SVC 会议时的处理
                    getView().refreshWatchMemberPage();

                    //刷新选看窗口的显示名称
                    refreshSvcWatchDisplayName(memberList);

                    //远端小窗口+本地窗口数
                    int num = MeetingMgr.getInstance().getCurrentWatchSmallCount() + 1;
                    if (currentShowSmallWndCount != num)
                    {
                        currentShowSmallWndCount = num;
                        getView().setSmallVideoVisible(currentShowSmallWndCount);
                    }

                    //广播与会者的处理
                    TsdkAttendee broadcastAttendee = confBaseInfo.getBroadcastAttendee();
                    if (null != broadcastAttendee)
                    {
                        if (!currentBroadcastNumber.equals(broadcastAttendee.getBaseInfo().getNumber()))
                        {
                            currentBroadcastNumber = broadcastAttendee.getBaseInfo().getNumber();
                            getView().isWatchAfterBroadcast(broadcastAttendee.getBaseInfo().getDisplayName());
                        }
                    }
                    else
                    {
                        currentBroadcastNumber = "";
                    }

                    // 处理是否有人正在共享
                    for (Member member : memberList)
                    {
                        if (member.isSelf())
                        {
                            continue;
                        }
                        if (member.isShareOwner())
                        {
                            getView().updateSharingStatus(true);
                            return;
                        }
                        getView().updateSharingStatus(false);
                    }
                    break;

                case CustomBroadcastConstants.GET_DATA_CONF_PARAM_RESULT:
                    result = (int)obj;
                    if (result != 0)
                    {
                        getView().showCustomToast(R.string.get_data_conf_params_fail);
                    }
                    break;

                case CustomBroadcastConstants.DATA_CONFERENCE_JOIN_RESULT:
                    result = (int) obj;
                    if (result != 0)
                    {
                        getView().showCustomToast(R.string.join_data_conf_fail);
                    }
                    break;

                case CustomBroadcastConstants.GET_CONF_END:
                    TsdkConfEndReason reasonCode = (TsdkConfEndReason) obj;
                    getView().showMessage(getReasonDescription(reasonCode));
                    getView().finishActivity();
                    break;

                // 升级会议结果
                case CustomBroadcastConstants.UPGRADE_CONF_RESULT:
                    result = (int) obj;
                    if (result != 0) {
                        getView().showCustomToast(R.string.upgrade_conf_fail);
                        return;
                    } else {
                        getView().showCustomToast(R.string.upgrade_conf_success);
                    }
                    break;

                // 静音会议结果
                case CustomBroadcastConstants.MUTE_CONF_RESULT:
                    result = (int)obj;
                    if (result != 0) {
                        getView().showCustomToast(R.string.mute_conf_fail);
                    } else {
                        getView().showCustomToast(R.string.mute_conf_success);
                    }
                    break;

                // 取消静音会议结果
                case CustomBroadcastConstants.UN_MUTE_CONF_RESULT:
                    result = (int)obj;
                    if (result != 0) {
                        getView().showCustomToast(R.string.un_mute_conf_fail);
                    } else {
                        getView().showCustomToast(R.string.un_mute_conf_success);
                    }
                    break;

                // 锁定会议结果
                case CustomBroadcastConstants.LOCK_CONF_RESULT:
                    result = (int)obj;
                    if (result != 0) {
                        getView().showCustomToast(R.string.lock_conf_fail);
                    } else {
                        getView().showCustomToast(R.string.lock_conf_success);
                    }
                    break;

                // 取消锁定会议结果
                case CustomBroadcastConstants.UN_LOCK_CONF_RESULT:
                    result = (int)obj;
                    if (result != 0) {
                        getView().showCustomToast(R.string.un_lock_conf_fail);
                    } else {
                        getView().showCustomToast(R.string.un_lock_conf_success);
                    }
                    break;

                // 邀请与会者结果
                case CustomBroadcastConstants.ADD_ATTENDEE_RESULT:
                    result = (int)obj;
                    LogUtil.i(UIConstants.DEMO_TAG, "add attendee result: " + result);
                    if (result != 0)
                    {
                        getView().showCustomToast(R.string.add_attendee_fail);
                        return;
                    }
                    break;

                // 删除与会者结果
                case CustomBroadcastConstants.DEL_ATTENDEE_RESULT:
                    result = (int)obj;
                    LogUtil.i(UIConstants.DEMO_TAG, "add attendee result: " + result);
                    if (result != 0)
                    {
                        getView().showCustomToast(R.string.del_attendee_fail);
                        return;
                    }
                    break;

                // 挂断与会者结果
                case CustomBroadcastConstants.HANG_UP_ATTENDEE_RESULT:
                    result = (int)obj;
                    if (result != 0)
                    {
                        getView().showCustomToast(R.string.hangup_attendee_fail);
                        return;
                    }
                    break;

                // 静音与会者结果
                case CustomBroadcastConstants.MUTE_ATTENDEE_RESULT:
                    result = (int)obj;
                    if (result != 0)
                    {
                        getView().showCustomToast(R.string.mute_attendee_fail);
                        return;
                    }
                    break;

                // 取消静音与会者结果
                case CustomBroadcastConstants.UN_MUTE_ATTENDEE_RESULT:
                    result = (int)obj;
                    if (result != 0)
                    {
                        getView().showCustomToast(R.string.un_mute_attendee_fail);
                        return;
                    }
                    break;

                // 举手结果
                case CustomBroadcastConstants.HAND_UP_RESULT:
                    result = (int)obj;
                    if (result != 0)
                    {
                        getView().showCustomToast(R.string.handup_fail);
                        return;
                    }
                    break;

                // 取消举手结果
                case CustomBroadcastConstants.CANCEL_HAND_UP_RESULT:
                    result = (int)obj;
                    if (result != 0)
                    {
                        getView().showCustomToast(R.string.cancel_handup_fail);
                        return;
                    }
                    break;

                // 设置会议视频模式结果
                case CustomBroadcastConstants.SET_CONF_MODE_RESULT:
                    result = (int)obj;
                    if (result != 0)
                    {
                        getView().showCustomToast(R.string.set_conf_mode_fail);
                        return;
                    }
                    break;

                // 选看会场结果
                case CustomBroadcastConstants.WATCH_ATTENDEE_CONF_RESULT:
                    result = (int)obj;
                    if (result != 0)
                    {
                        getView().showCustomToast(R.string.watch_conf_fail);
                        return;
                    }
                    break;

                // 广播与会者结果
                case CustomBroadcastConstants.BROADCAST_ATTENDEE_CONF_RESULT:
                    result = (int)obj;
                    if (result != 0)
                    {
                        getView().showCustomToast(R.string.broadcast_conf_fail);
                        return;
                    }
                    break;

                // 取消广播与会者结果
                case CustomBroadcastConstants.CANCEL_BROADCAST_CONF_RESULT:
                    result = (int)obj;
                    if (result != 0)
                    {
                        getView().showCustomToast(R.string.cancel_broadcast_fail);
                        return;
                    }
                    break;

                // 请求主席结果
                case CustomBroadcastConstants.REQUEST_CHAIRMAN_RESULT:
                    result = (int)obj;
                    if (result != 0)
                    {
                        getView().showCustomToast(R.string.request_chairman_fail);
                    }
                    else
                    {
                        getView().showCustomToast(R.string.request_chairman_success);
                        setSelfPresenter();
                    }
                    break;

                // 释放主席结果
                case CustomBroadcastConstants.RELEASE_CHAIRMAN_RESULT:
                    result = (int)obj;
                    if (result != 0)
                    {
                        getView().showCustomToast(R.string.release_chairman_fail);
                    }
                    else
                    {
                        getView().showCustomToast(R.string.release_chairman_success);
                    }
                    break;

                // 发言人通知
                case CustomBroadcastConstants.SPEAKER_LIST_IND:
                    if (!"ConfManagerActivity".equals(ActivityUtil.getCurrentActivity(LocContext.getContext())))
                    {
                        return;
                    }
                    int speakerNum = (int) obj;
                    if (0 == speakerNum)
                    {
                        return;
                    }
                    String[] speakerName = MeetingMgr.getInstance().getSpeakers();
                    getView().showMessage(speakerName[0] + " is speaking.");
                    break;

                case CustomBroadcastConstants.DATE_CONFERENCE_START_SHARE_STATUS:
                    if (obj instanceof TsdkConfAsStateInfo)
                    {
                        TsdkConfAsStateInfo asStartInfo = (TsdkConfAsStateInfo)obj;

                        if (!isSharing && asStartInfo.getState() == 2)
                        {
                            getView().jumpToHomeScreen();
                            isSharing = true;
                        }

                        if (null != asStartInfo){
                            boolean isAllowAnnot = asStartInfo.getSubState() == TsdkConfShareSubState.TSDK_E_CONF_AS_SUB_STATE_ANNOTATION.getIndex()? true:false;
                            getView().confManagerActivityShare(true, isAllowAnnot);
                        }else {
                            getView().confManagerActivityShare(true, false);
                        }
                        return;
                    }
                    getView().confManagerActivityShare(true, false);
                    break;

                case CustomBroadcastConstants.DATE_CONFERENCE_END_SHARE_STATUS:
                    isSharing = false;

                    if (obj instanceof TsdkConfAsStateInfo)
                    {
                        TsdkConfAsStateInfo asStopInfo = (TsdkConfAsStateInfo)obj;
                        if (null != asStopInfo){
                            boolean isAllowAnnot = asStopInfo.getSubState() == TsdkConfShareSubState.TSDK_E_CONF_AS_SUB_STATE_ANNOTATION.getIndex()? true:false;
                            getView().confManagerActivityShare(false,isAllowAnnot);
                        }else {
                            getView().confManagerActivityShare(false,false);
                        }
                    }

                    if (obj instanceof TsdkWbDelDocInfo)
                    {
                        getView().confManagerActivityShare(false,false);
                    }

                    if (obj instanceof TsdkDocShareDelDocInfo)
                    {
                        getView().confManagerActivityShare(false,false);
                    }
                    getView().showCustomToast(R.string.share_end);
                    break;

                case CustomBroadcastConstants.SCREEN_SHARE_STATE:
                    TsdkConfAsActionType actionType = (TsdkConfAsActionType)obj;
                    if(actionType == TsdkConfAsActionType.TSDK_E_CONF_AS_ACTION_ADD && isSharing){
                        getView().jumpToHomeScreen();
                    }else if (actionType == TsdkConfAsActionType.TSDK_E_CONF_AS_ACTION_DELETE){
                        getView().removeAllScreenShareFloatWindow();
                        if (!DeviceUtil.isAppForeground()) {
                            DeviceUtil.bringTaskBackToFront();
                        }
                    }else if(actionType == TsdkConfAsActionType.TSDK_E_CONF_AS_ACTION_REQUEST){
                        getView().requestScreen();
                    } if (actionType == TsdkConfAsActionType.TSDK_E_CONF_AS_ACTION_MODIFY){
                        getView().robShareRemoveAllScreenShareFloatWindow();
                    }
                    break;

                // 正在观看画面信息通知
                case CustomBroadcastConstants.GET_SVC_WATCH_INFO:
                    TsdkConfSvcWatchInfo svcWatchInfo = (TsdkConfSvcWatchInfo) obj;
                    if (svcWatchInfo.getWatchAttendeeNum() <= 0 || svcLabel.size() <= 0)
                    {
                        return;
                    }
                    showSvcWatchInfo(svcWatchInfo.getWatchAttendees());
                    break;

                // 会议恢复中通知
                case CustomBroadcastConstants.RESUME_JOIN_CONF_IND:
                    getView().showMessage(LocContext.getString(R.string.resume_join_conf));
                    // 开始恢复会议
                    getView().setResumeStatus(false);

                    //恢复会议时，SDK会自动停止共享，APP在收到这个消息时自动回到会议主界面
                    getView().removeAllScreenShareFloatWindow();
                    if (!DeviceUtil.isAppForeground()) {
                        DeviceUtil.bringTaskBackToFront();
                    }
                    break;

                // 重新加入会议结果
                case CustomBroadcastConstants.RESUME_JOIN_CONF_RESULT:
                    // 恢复会议结束，在此之前用户不要关闭会议界面
                    getView().setResumeStatus(true);
                    if (0 != (int) obj)
                    {
                        failedConnectedAndExit(R.string.resume_join_failed);
                    }
                    else
                    {
                        setConfID(String.valueOf(MeetingMgr.getInstance().getConfHandle()));
                        svcLabel = MeetingMgr.getInstance().getSvcConfInfo().getSvcLabel();
                    }
                    break;

                // 登录状态恢复中的通知
                case CustomBroadcastConstants.LOGIN_STATUS_RESUME_IND:
                    isResuming = true;
                    break;

                // 登录状态的恢复结果
                case CustomBroadcastConstants.LOGIN_STATUS_RESUME_RESULT:
                    isResuming = false;
                    if (isNeedConfigIp)
                    {
                        configIpResume(false);
                    }
                    break;

                // 会议中，sip注册超时结果(UI主动挂断通话并且离开会议)
                case CustomBroadcastConstants.LOGIN_FAILED:
                    long callID = getCallId();
                    LogUtil.i(UIConstants.DEMO_TAG, "exit during the conf, callId:" + callID);
                    if (0 != callID)
                    {
                        CallMgr.getInstance().endCall(callID);
                    }

                    if (MeetingMgr.getInstance().isExistConf())
                    {
                        MeetingMgr.getInstance().setExistConf(false);
                        getView().closeConf();
                    }
                    break;

                // 加入会议失败
                case CustomBroadcastConstants.JOIN_CONF_FAILED:
                    if (MeetingMgr.getInstance().isExistConf())
                    {
                        MeetingMgr.getInstance().setExistConf(false);
                        getView().closeConf();
                    }
                    break;

                // 第三方来电和接听
                case CustomBroadcastConstants.ACTION_CALL_STATE_RINGING:
                case CustomBroadcastConstants.ACTION_CALL_STATE_OFF_HOOK:
                    getView().setIsCallByPhone(true);
                    isMuteMicAndSpeaker();
                    break;

                //  挂断第三方来电
                case CustomBroadcastConstants.ACTION_CALL_STATE_IDLE:
                    getView().setIsCallByPhone(false);
                    isCancelMuteMicAndSpeaker();
                    break;

                // 设置共享所有者超时
                case CustomBroadcastConstants.SET_SHARE_OWNER_FAILED:
                // 开始共享超时
                case CustomBroadcastConstants.START_SHARE_FAILED:
                    getView().showMessage(String.valueOf(obj));
                    break;

                // 无码流上报
                case CustomBroadcastConstants.NO_STREAM_IND:
                    int duration = Integer.parseInt(obj.toString());
                    if (duration <= 30)
                    {
                        getView().showNoStreamDuration(duration);
                    }
                    break;
                default:
                    break;
            }
            onBroadcastReceive(broadcastName, obj);
        }
    };

    protected abstract void onBroadcastReceive(String broadcastName, Object obj);



    @Override
    public void registerBroadcast()
    {
        LocBroadcast.getInstance().registerBroadcast(receiver, broadcastNames);
    }

    @Override
    public void unregisterBroadcast()
    {
        LocBroadcast.getInstance().unRegisterBroadcast(receiver, broadcastNames);
    }

    @Override
    public void setConfID(String confID) {
        this.confID = confID;
    }

    public String getSubject()
    {
        ConfBaseInfo baseInfo = MeetingMgr.getInstance().getCurrentConferenceBaseInfo();
        if (baseInfo != null) {
            return baseInfo.getSubject();
        } else {
            return null;
        }
    }

    @Override
    public void closeConf()
    {
        int result = MeetingMgr.getInstance().leaveConf();
        if (result != 0) {
            getView().showCustomToast(R.string.leave_conf_fail);
            return;
        }

        LocBroadcast.getInstance().unRegisterBroadcast(receiver, broadcastNames);
    }

    @Override
    public void finishConf()
    {
        int result = MeetingMgr.getInstance().endConf();
        if (result != 0) {
            getView().showCustomToast(R.string.end_audio_conf);
            return;
        }

        LocBroadcast.getInstance().unRegisterBroadcast(receiver, broadcastNames);
    }


    @Override
    public boolean muteSelf()
    {
        Member self = MeetingMgr.getInstance().getCurrentConferenceSelf();

        if (self == null)
        {
            return false;
        }

        ConfBaseInfo confBaseInfo = MeetingMgr.getInstance().getCurrentConferenceBaseInfo();
        if (!confBaseInfo.isAllowUnMute() && self.isMute())
        {
            getView().showNotAllowUnmute();
            return false;
        }

        int result = MeetingMgr.getInstance().muteAttendee(self, !self.isMute());
        if (result != 0)
        {
            return false;
        }
        return true;
    }

    @Override
    public int switchLoudSpeaker()
    {
        return CallMgr.getInstance().switchAudioRoute();
    }

    @Override
    public boolean isChairMan()
    {
        Member self = MeetingMgr.getInstance().getCurrentConferenceSelf();
        if(null == self){
            return false;
        }

        return (self.getRole() == TsdkConfRole.TSDK_E_CONF_ROLE_ATTENDEE ? false:true);
    }

    @Override
    public List<Member> getMemberList()
    {
        return MeetingMgr.getInstance().getCurrentConferenceMemberList();
    }

    @Override
    public void setSelfPresenter() {
        ConfBaseInfo confBaseInfo = MeetingMgr.getInstance().getCurrentConferenceBaseInfo();
        if (null == confBaseInfo)
        {
            return;
        }

        if (confBaseInfo.getMediaType() == TsdkConfMediaType.TSDK_E_CONF_MEDIA_VIDEO
                || confBaseInfo.getMediaType() == TsdkConfMediaType.TSDK_E_CONF_MEDIA_VOICE)
        {
            return;
        }

        int result = 0;
        Member self = MeetingMgr.getInstance().getCurrentConferenceSelf();
        if(null == self)
        {
            return;
        }

        if (self.getRole() == TsdkConfRole.TSDK_E_CONF_ROLE_CHAIRMAN && !self.isPresent())
        {
            result = MeetingMgr.getInstance().setPresenter(self);
        }

        if (0 != result)
        {
            getView().showCustomToast(R.string.set_presenter_failed);
        }
    }

    @Override
    public void showSvcWatchInfo(List<TsdkConfSvcWatchAttendee> watchAttendees) {
        watchMap.clear();
        for (TsdkConfSvcWatchAttendee watchAttendee : watchAttendees)
        {
            if (svcLabel.get(0) == watchAttendee.getLabel())
            {
                remoteDisplay = watchAttendee.getBaseInfo().getDisplayName();
                watchMap.put(watchAttendee.getBaseInfo().getNumber(), REMOTE_DISPLAY);
            }
            else if (svcLabel.get(1) == watchAttendee.getLabel() || svcLabel.get(5) == watchAttendee.getLabel())
            {
                smallDisplay_01 = watchAttendee.getBaseInfo().getDisplayName();
                watchMap.put(watchAttendee.getBaseInfo().getNumber(), SMALL_DISPLAY_01);
            }
            else if (svcLabel.get(2) == watchAttendee.getLabel() || svcLabel.get(6) == watchAttendee.getLabel())
            {
                smallDisplay_02 = watchAttendee.getBaseInfo().getDisplayName();
                watchMap.put(watchAttendee.getBaseInfo().getNumber(), SMALL_DISPLAY_02);
            }
            else if (svcLabel.get(3) == watchAttendee.getLabel() || svcLabel.get(7) == watchAttendee.getLabel())
            {
                smallDisplay_03 = watchAttendee.getBaseInfo().getDisplayName();
                watchMap.put(watchAttendee.getBaseInfo().getNumber(), SMALL_DISPLAY_03);
            }
        }
        getView().refreshSvcWatchDisplayName(remoteDisplay, smallDisplay_01, smallDisplay_02, smallDisplay_03);
    }

    @Override
    public void configIpResume(boolean isFocus) {
        // 如果收到重新登录通知还没有收到重新登录结果则不进行配置本地ip
        isNeedConfigIp = true;
        if (!isResuming)
        {
            LoginMgr.getInstance().resetConfig(false, isFocus);
            isNeedConfigIp = false;
        }
    }

    @Override
    public String getAttendeeName(List<Member> list) {
        List<Member> attendeeName = new ArrayList<>();
        for (Member member : list)
        {
            if (ConfConstant.ParticipantStatus.IN_CONF != member.getStatus())
            {
                continue;
            }
            attendeeName.add(member);
        }

        if (1 == attendeeName.size())
        {
            return attendeeName.get(0).getDisplayName();
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < attendeeName.size(); i ++)
        {

            if (i == attendeeName.size() - 1)
            {
                builder.append(attendeeName.get(i).getDisplayName());
            }
            else
            {
                builder.append(attendeeName.get(i).getDisplayName() + ", ");
            }
        }

        return builder.toString();
    }

    /**
     * 加入会议失败后退出会议界面
     * @param id
     */
    private void failedConnectedAndExit(int id)
    {
        getView().showMessage(LocContext.getString(id));
        ActivityStack.getIns().popup(DataConfActivity.class);
        getView().finishActivity();
    }

    /**
     * 刷新svc会场的显示信息
     * @param list
     */
    private void refreshSvcWatchDisplayName(List<Member> list)
    {
        if (null == watchMap || watchMap.isEmpty())
        {
            return;
        }

        for (Member member : list)
        {
            for (String key : watchMap.keySet())
            {
                if (member.getNumber().equals(key))
                {
                    switch (watchMap.get(key))
                    {
                        case REMOTE_DISPLAY:
                            remoteDisplay = member.getDisplayName();
                            break;
                        case SMALL_DISPLAY_01:
                            smallDisplay_01 = member.getDisplayName();
                            break;
                        case SMALL_DISPLAY_02:
                            smallDisplay_02 = member.getDisplayName();
                            break;
                        case SMALL_DISPLAY_03:
                            smallDisplay_03 = member.getDisplayName();
                            break;
                        default:
                            break;
                    }
                }
            }

            getView().refreshSvcWatchDisplayName(remoteDisplay, smallDisplay_01, smallDisplay_02, smallDisplay_03);
        }
    }

    private boolean isMuteMicAndSpeaker()
    {
        Member self = MeetingMgr.getInstance().getCurrentConferenceSelf();

        if (self == null)
        {
            return false;
        }

        CallMgr.getInstance().muteSpeak(getCallId(), true);

        if (self.isMute())
        {
            return false;
        }
        mNeedUnMuteConf = true;
        int result = MeetingMgr.getInstance().muteAttendee(self, true);
        if (result != 0)
        {
            return false;
        }
        return true;
    }

    private long getCallId()
    {
        return MeetingMgr.getInstance().getCurrentConferenceCallID();
    }

    private boolean isCancelMuteMicAndSpeaker()
    {
        Member self = MeetingMgr.getInstance().getCurrentConferenceSelf();

        if (self == null)
        {
            return false;
        }

        CallMgr.getInstance().muteSpeak(getCallId(), false);

        if (!mNeedUnMuteConf)
        {
            return false;
        }
        if (!self.isMute())
        {
            return false;
        }

        mNeedUnMuteConf = false;

        int result = MeetingMgr.getInstance().muteAttendee(self, false);
        if (result != 0)
        {
            return false;
        }
        return true;
    }

    private String getReasonDescription(TsdkConfEndReason reasonCode)
    {
        String reasonDescription = LocContext.getString(R.string.unknown);
        switch (reasonCode)
        {
            case TSDK_E_CONF_END_REASON_STOP_CONF_HANGUP:
                reasonDescription = LocContext.getString(R.string.stop_conf_hangup);
                break;
            case TSDK_E_CONF_END_REASON_CHAIR_HANGUP:
                reasonDescription = LocContext.getString(R.string.chair_hangup);
                break;
            case TSDK_E_CONF_END_REASON_SESSION_TIMER_TIMEOUT:
                reasonDescription = LocContext.getString(R.string.session_timer_timeout);
                break;
            case TSDK_E_CONF_END_REASON_NOSTREAM_HANGUP:
                reasonDescription = LocContext.getString(R.string.nostream_hangup);
                break;
            case TSDK_E_CONF_END_REASON_CORP_CONFERENCE_RESOURCE_HAS_BEEN_RUN_OUT:
                reasonDescription = LocContext.getString(R.string.corp_conference_resource_has_been_run_out);
                break;
            default:
                break;
        }
        return reasonDescription;
    }
}
