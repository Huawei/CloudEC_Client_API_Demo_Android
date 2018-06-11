package com.huawei.opensdk.ec_sdk_demo.logic.im.mvp;

import com.huawei.contacts.PersonalContact;
import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcastReceiver;
import com.huawei.opensdk.ec_sdk_demo.ui.base.MVPBasePresenter;

import java.util.List;

public class ImSearchPresenter extends MVPBasePresenter<ImSearchContract.ImSearchView>
        implements ImSearchContract.ImSearchPresenter
{
    private String[] broadcastNames = new String[]{CustomBroadcastConstants.ACTION_IM_SEARCH_CONTACT_RESULT};
    private LocBroadcastReceiver receiver = new LocBroadcastReceiver()
    {
        @Override
        public void onReceive(String broadcastName, Object obj)
        {
            switch (broadcastName)
            {
                case CustomBroadcastConstants.ACTION_IM_SEARCH_CONTACT_RESULT:
                    List<PersonalContact> contactList = (List<PersonalContact>) obj;
                    getView().refreshContactList(contactList);
                    break;
                default:
                    break;
            }
        }
    };

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
}
