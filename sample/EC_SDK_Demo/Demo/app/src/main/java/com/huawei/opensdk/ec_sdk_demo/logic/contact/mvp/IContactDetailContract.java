package com.huawei.opensdk.ec_sdk_demo.logic.contact.mvp;

import android.content.Context;


public interface IContactDetailContract
{
    interface IContactDetailView
    {
        void refreshDeleteContactButton(boolean isFriend);

        void showCustomToast(int res);
    }

    interface IContactDetailPresenter
    {
        void gotoChatActivity(Context context);

        void makeCall(String number);

        void makeVideo(String number);

        void deleteContact();

        void addContact(String teamId);

        void registerBroadcast();

        void unregisterBroadcast();
    }
}
