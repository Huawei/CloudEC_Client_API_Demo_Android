package com.huawei.opensdk.ec_sdk_demo.logic.contact.mvp;

import com.huawei.opensdk.ec_sdk_demo.logic.BaseView;
import com.huawei.opensdk.imservice.ImContactGroupInfo;


public interface IContactRecordContract
{
    interface IContactRecordView extends BaseView
    {
        void showCurrentGroupInfo(ImContactGroupInfo contactGroupInfo);
    }

    interface IContactRecordPresenter
    {
        void getCurrentContactGroup(long currentContactGroupId);

        void registerBroadcast();

        void unregisterBroadcast();
    }
}
