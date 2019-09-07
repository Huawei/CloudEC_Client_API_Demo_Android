package com.huawei.opensdk.ec_sdk_demo.logic.conference.mvp;


import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;

import com.huawei.ecterminalsdk.base.TsdkConfAsActionType;
import com.huawei.ecterminalsdk.base.TsdkConfAsStateInfo;
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
import com.huawei.opensdk.demoservice.MeetingMgr;
import com.huawei.opensdk.demoservice.Member;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.floatView.util.DeviceUtil;
import com.huawei.opensdk.ec_sdk_demo.ui.base.MVPBasePresenter;

import java.util.List;



public abstract class ConfManagerBasePresenter extends MVPBasePresenter<IConfManagerContract.ConfManagerView>
        implements IConfManagerContract.ConfManagerPresenter
{
    private String confID;
    private int currentShowSmallWndCount = 0;
    private List<Long> svcLabel = MeetingMgr.getInstance().getSvcConfInfo().getSvcLabel();
    String remoteDisplay = "";
    String smallDisplay_01 = "";
    String smallDisplay_02 = "";
    String smallDisplay_03 = "";

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

                    //远端小窗口+本地窗口数
                    int num = MeetingMgr.getInstance().getCurrentWatchSmallCount() + 1;
                    if (currentShowSmallWndCount != num)
                    {
                        currentShowSmallWndCount = num;
                        getView().setSmallVideoVisible(currentShowSmallWndCount);
                    }
                    break;

                case CustomBroadcastConstants.GET_DATA_CONF_PARAM_RESULT:
                    result = (int)obj;
                    if (result != 0)
                    {
                        getView().showCustomToast(R.string.get_data_conf_params_fail);
                        return;
                    }
                    MeetingMgr.getInstance().joinDataConf();
                    break;

                case CustomBroadcastConstants.DATA_CONFERENCE_JOIN_RESULT:
                    result = (int) obj;
                    if (result != 0)
                    {
                        getView().showCustomToast(R.string.join_data_conf_fail);
                    }
                    break;

                case CustomBroadcastConstants.GET_CONF_END:
                    getView().finishActivity();
                    break;

                case CustomBroadcastConstants.DATE_CONFERENCE_START_SHARE_STATUS:
                    if (obj instanceof TsdkConfAsStateInfo)
                    {
                        TsdkConfAsStateInfo asStartInfo = (TsdkConfAsStateInfo)obj;
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
                        return;
                    }
                    setSelfPresenter();
                    break;

                // 释放主席结果
                case CustomBroadcastConstants.RELEASE_CHAIRMAN_RESULT:
                    result = (int)obj;
                    if (result != 0)
                    {
                        getView().showCustomToast(R.string.release_chairman_fail);
                        return;
                    }
                    break;

                // 发言人通知
                case CustomBroadcastConstants.SPEAKER_LIST_IND:
                    if (!"ConfManagerActivity".equals(getCurrentActivity(LocContext.getContext())))
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

                case CustomBroadcastConstants.SCREEN_SHARE_STATE:
                    TsdkConfAsActionType actionType = (TsdkConfAsActionType)obj;
                    if(actionType == TsdkConfAsActionType.TSDK_E_CONF_AS_ACTION_ADD){
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
        for (TsdkConfSvcWatchAttendee watchAttendee : watchAttendees)
        {
            if (svcLabel.get(0) == watchAttendee.getLabel())
            {
                remoteDisplay = watchAttendee.getBaseInfo().getDisplayName();
            }
            else if (svcLabel.get(1) == watchAttendee.getLabel())
            {
                smallDisplay_01 = watchAttendee.getBaseInfo().getDisplayName();
            }
            else if (svcLabel.get(2) == watchAttendee.getLabel())
            {
                smallDisplay_02 = watchAttendee.getBaseInfo().getDisplayName();
            }
            else if (svcLabel.get(3) == watchAttendee.getLabel())
            {
                smallDisplay_03 = watchAttendee.getBaseInfo().getDisplayName();
            }
        }
        getView().refreshSvcWatchDisplayName(remoteDisplay, smallDisplay_01, smallDisplay_02, smallDisplay_03);
    }

    /**
     * 获取当前显示的activity名称
     * @param context
     * @return
     */
    private String getCurrentActivity(Context context)
    {
        ActivityManager manager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        ComponentName componentName = manager.getRunningTasks(1).get(0).topActivity;
        String className = componentName.getClassName();
        if (null == className)
        {
            return null;
        }

        if (!className.contains("."))
        {
            return className;
        }

        String[] str = className.split("\\.");
        return str[str.length - 1];
    }
}
