package com.huawei.opensdk.ec_sdk_demo.logic.im.mvp;

//import com.huawei.contacts.PersonalContact;

import java.util.List;


public interface ImSearchContract
{
    interface ImSearchView
    {
//        void refreshContactList(List<PersonalContact> contactList);
    }

    interface ImSearchPresenter
    {
        void registerBroadcast();

        void unregisterBroadcast();
    }
}
