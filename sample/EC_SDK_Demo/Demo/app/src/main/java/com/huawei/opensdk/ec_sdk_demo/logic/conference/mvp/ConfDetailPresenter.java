package com.huawei.opensdk.ec_sdk_demo.logic.conference.mvp;

import com.huawei.ecterminalsdk.base.TsdkConfJoinParam;
import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.demoservice.ConfDetailInfo;
import com.huawei.opensdk.demoservice.MeetingMgr;
import com.huawei.opensdk.ec_sdk_demo.ui.base.MVPBasePresenter;


public class ConfDetailPresenter extends MVPBasePresenter<ICreateConfContract.ConfDetailView> implements ICreateConfContract.IConfDetailPresenter
{
    private ConfDetailInfo confDetailInfo;

    public ConfDetailPresenter()
    {
    }

    @Override
    public void queryConfDetail(String confID)
    {
        MeetingMgr.getInstance().queryConfDetail(confID);
    }

    @Override
    public void joinConf(String selfJoinNumber)
    {
        if (confDetailInfo == null)
        {
            return;
        }

        //入会时，有主席密码则用主席密码入会，无主席则用普通与会密码入会
        String password = confDetailInfo.getChairmanPwd();
        if ((password == null) || (password.equals("")) || (password.equals("******")))
        {
            password = confDetailInfo.getGuestPwd();
        }

        TsdkConfJoinParam confJoinParam = new TsdkConfJoinParam();
        confJoinParam.setConfPassword(password);
        confJoinParam.setConfId(confDetailInfo.getConfID());
        confJoinParam.setAccessNumber(confDetailInfo.getAccessNumber());

        boolean isVideo = false;
        switch (confDetailInfo.getMediaType()){
            case TSDK_E_CONF_MEDIA_VOICE:
                isVideo = false;
                break;

            case TSDK_E_CONF_MEDIA_VIDEO:
                isVideo = true;
                break;

            default:
                break;
        }

        MeetingMgr.getInstance().joinConf(confJoinParam, isVideo ,selfJoinNumber);
//        MeetingMgr.getInstance().updateCurrentConferenceBaseInfo(confDetailInfo);
//        MeetingMgr.getInstance().setJoinConfNumber(selfJoinNumber);
    }


    @Override
    public void receiveBroadcast(String broadcastName, Object obj)
    {
        switch (broadcastName)
        {
            case CustomBroadcastConstants.GET_CONF_DETAIL_RESULT:
                if (obj instanceof ConfDetailInfo)
                {
                    ConfDetailInfo confDetailInfo = (ConfDetailInfo) obj;
                    this.confDetailInfo = confDetailInfo;
                    getView().refreshView(confDetailInfo);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void updateAccessNumber(String accessNumber)
    {
//        MeetingMgr.getInstance().setJoinConfNumber(accessNumber);
    }

    @Override
    public void endConf()
    {
        MeetingMgr.getInstance().endConf();
    }
}
