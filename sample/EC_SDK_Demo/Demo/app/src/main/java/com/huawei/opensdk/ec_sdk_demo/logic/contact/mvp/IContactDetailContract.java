package com.huawei.opensdk.ec_sdk_demo.logic.contact.mvp;

import android.content.Context;

import com.huawei.opensdk.imservice.ImConstant;
import com.huawei.opensdk.imservice.ImContactGroupInfo;
import com.huawei.opensdk.imservice.ImContactInfo;

import java.util.List;


public interface IContactDetailContract
{
    interface IContactDetailView
    {
        void refreshDeleteContactButton(boolean isFriend);

        void showCustomToast(int res);

        void finishActivity(int id);

        void showTeamDialog(List<ImContactGroupInfo> groupInfoList);

        void showUserStatus(ImConstant.ImStatus status);
    }

    interface IContactDetailPresenter
    {
        void gotoChatActivity(Context context);

        void makeCall(String number);

        void makeVideo(String number);

        void deleteContact(long contactId);

        void addContact(String teamId);

        void registerBroadcast();

        void unregisterBroadcast();

        void moveContact(long newGroupId, long oldGroupId, long contactId, boolean isMove);

        void refreshGroupList(List<ImContactGroupInfo> contactGroupList, long contactId);

//        void detectUserStatus(String account);

        void setUserInfo(ImContactInfo contactInfo);
    }
}
