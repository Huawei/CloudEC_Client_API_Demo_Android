package com.huawei.opensdk.ec_sdk_demo.logic.conference.mvp;


import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;

import com.huawei.ecterminalsdk.base.TsdkConfMediaType;
import com.huawei.ecterminalsdk.base.TsdkConfRole;
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
import com.huawei.opensdk.ec_sdk_demo.ui.base.MVPBasePresenter;

import java.util.List;



public abstract class ConfManagerBasePresenter extends MVPBasePresenter<IConfManagerContract.ConfManagerView>
        implements IConfManagerContract.ConfManagerPresenter
{
    private String confID;

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
                    getView().startAsShare(true);
                    break;

                case CustomBroadcastConstants.DATE_CONFERENCE_END_SHARE_STATUS:
                    getView().startAsShare(false);
                    getView().showCustomToast(R.string.share_end);
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
        return MeetingMgr.getInstance().getCurrentConferenceBaseInfo().getSubject();
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
