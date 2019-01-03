package com.huawei.opensdk.ec_sdk_demo.logic.conference.mvp;


import android.content.Context;
import android.view.SurfaceView;
import android.view.ViewGroup;

import com.huawei.ecterminalsdk.base.TsdkConfChatMsgInfo;
import com.huawei.ecterminalsdk.base.TsdkConfRole;
import com.huawei.opensdk.callmgr.CallMgr;
import com.huawei.opensdk.callmgr.VideoMgr;
import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcastReceiver;
import com.huawei.opensdk.demoservice.MeetingMgr;
import com.huawei.opensdk.demoservice.Member;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.ui.base.MVPBasePresenter;


public class DataConfPresenter extends MVPBasePresenter<IDataConfContract.DataConfView>
        implements IDataConfContract.IDataConfPresenter
{
    private String confID;

    private String[] broadcastNames = new String[]{
            CustomBroadcastConstants.DATE_CONFERENCE_START_SHARE_STATUS,
            CustomBroadcastConstants.DATE_CONFERENCE_END_SHARE_STATUS,
            CustomBroadcastConstants.DATE_CONFERENCE_CHAT_MSG,
            CustomBroadcastConstants.GET_CONF_END};

    private LocBroadcastReceiver receiver = new LocBroadcastReceiver()
    {
        @Override
        public void onReceive(String broadcastName, Object obj)
        {
            switch (broadcastName)
            {
                case CustomBroadcastConstants.DATE_CONFERENCE_START_SHARE_STATUS:
                    getView().startAsShare(true);
                    break;

                case CustomBroadcastConstants.DATE_CONFERENCE_END_SHARE_STATUS:
                    getView().startAsShare(false);
                    getView().showCustomToast(R.string.share_end);
                    break;

                case CustomBroadcastConstants.GET_CONF_END:
                    getView().finishActivity();
                    break;

                case CustomBroadcastConstants.DATE_CONFERENCE_CHAT_MSG:
                    TsdkConfChatMsgInfo chatMsgInfo = (TsdkConfChatMsgInfo) obj;
                    String msgInfo = chatMsgInfo.getChatMsg();
                    String userName = chatMsgInfo.getSenderDisplayName();
                    String userNumber = chatMsgInfo.getSenderNumber();
                    boolean isSelfMsg = false;

                    Member self = MeetingMgr.getInstance().getCurrentConferenceSelf();
                    if (null != self)
                    {
                        if (self.getDisplayName().equals(userName) || self.getNumber().equals(userNumber))
                        {
                            isSelfMsg = true;
                        }
                    }

                    if (null == userName || "".equals(userName))
                    {
                        if (null == userNumber || "".equals(userNumber))
                        {
                            getView().displayConfChatMag(isSelfMsg, "The sender's name was not obtained.");
                        }
                        else
                        {
                            getView().displayConfChatMag(isSelfMsg, userNumber + ": " + msgInfo);
                        }
                        return;
                    }
                    getView().displayConfChatMag(isSelfMsg, userName + ": " + msgInfo);
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    public void attachSurfaceView(ViewGroup container, Context context)
    {
        MeetingMgr.getInstance().attachSurfaceView(container, context);
    }

    @Override
    public void sendChatMsg(String content) {
        MeetingMgr.getInstance().sendConfMessage(content);
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
    }

    @Override
    public void finishConf()
    {
        int result = MeetingMgr.getInstance().endConf();
        if (result != 0) {
            getView().showCustomToast(R.string.end_audio_conf);
            return;
        }
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

        return (self.getRole() == TsdkConfRole.TSDK_E_CONF_ROLE_ATTENDEE ? false:true);
    }

    @Override
    public void registerBroadcast() {
        LocBroadcast.getInstance().registerBroadcast(receiver, broadcastNames);
    }

    @Override
    public void unregisterBroadcast() {
        LocBroadcast.getInstance().unRegisterBroadcast(receiver, broadcastNames);
    }

    @Override
    public SurfaceView getHideVideoView() {
        return VideoMgr.getInstance().getLocalHideView();
    }

    @Override
    public SurfaceView getLocalVideoView() {
        return VideoMgr.getInstance().getLocalVideoView();
    }

    @Override
    public void setVideoContainer(Context context, ViewGroup smallLayout, ViewGroup hideLayout) {
        if (smallLayout != null) {
            addSurfaceView(smallLayout, getLocalVideoView());
        }

        if (hideLayout != null) {
            addSurfaceView(hideLayout, getHideVideoView());
        }
    }

    private void addSurfaceView(ViewGroup container, SurfaceView child)
    {
        if (child == null)
        {
            return;
        }
        if (child.getParent() != null)
        {
            ViewGroup vGroup = (ViewGroup) child.getParent();
            vGroup.removeAllViews();
        }
        container.addView(child);
    }
}
