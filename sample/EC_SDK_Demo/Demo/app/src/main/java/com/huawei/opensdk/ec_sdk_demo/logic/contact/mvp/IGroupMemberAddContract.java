package com.huawei.opensdk.ec_sdk_demo.logic.contact.mvp;

import android.content.Intent;

import com.huawei.contacts.PersonalContact;
import com.huawei.data.ConstGroup;
import com.huawei.data.ConstGroupContact;

import java.util.List;

public interface IGroupMemberAddContract
{
    interface IGroupMemberAddView
    {
        void refreshGroupMember(List<PersonalContact> list);

        void toast(int discussion_exist_tip);
    }
    interface IGroupMemberAddPresenter
    {
        void setConstGroup(ConstGroup mConstGroup);

        void registerBroadcast();

        void unregisterBroadcast();

        List<ConstGroupContact> getGroupMember();

        void queryGroupMembers();

        void handleResult(int requestCode, int resultCode, Intent data);

        void searchContact(String text);

        List<PersonalContact> getSearchResult();

        void inviteToGroup();
    }
}
