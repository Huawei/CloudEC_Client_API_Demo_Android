package com.huawei.opensdk.ec_sdk_demo.logic.contact.mvp;

import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcastReceiver;
import com.huawei.opensdk.imservice.ImContactGroupInfo;
import com.huawei.opensdk.imservice.ImMgr;

public class ContactRecordPresenter implements IContactRecordContract.IContactRecordPresenter, LocBroadcastReceiver
{
    private IContactRecordContract.IContactRecordView mView;
    private ImContactGroupInfo contactGroupInfo;
    private long currentContactGroupId;

    public ContactRecordPresenter(IContactRecordContract.IContactRecordView view)
    {
        mView = view;
    }

    private String[] broadcastNames = new String[]{CustomBroadcastConstants.ACTION_IM_USER_STATUS_CHANGE};

    @Override
    public void getCurrentContactGroup(long currentContactGroupId) {
        this.currentContactGroupId = currentContactGroupId;
        contactGroupInfo = ImMgr.getInstance().getContactGroupByGroupId(currentContactGroupId);
        if (null == contactGroupInfo)
        {
            return;
        }
        mView.showCurrentGroupInfo(contactGroupInfo);
    }

    @Override
    public void registerBroadcast() {
        LocBroadcast.getInstance().registerBroadcast(this, broadcastNames);
    }

    @Override
    public void unregisterBroadcast() {
        LocBroadcast.getInstance().unRegisterBroadcast(this, broadcastNames);
    }

    @Override
    public void onReceive(String broadcastName, Object obj) {
        switch (broadcastName)
        {
            case CustomBroadcastConstants.ACTION_IM_USER_STATUS_CHANGE:
                getCurrentContactGroup(currentContactGroupId);
                break;
                default:
                    break;
        }
    }
}
