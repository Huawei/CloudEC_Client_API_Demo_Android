package com.huawei.opensdk.ec_sdk_demo.logic.contact.mvp;

import android.app.Activity;
import android.content.Intent;

import com.huawei.contacts.PersonalContact;
import com.huawei.data.ConstGroup;
import com.huawei.data.ConstGroupContact;
import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcastReceiver;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.ui.base.MVPBasePresenter;
import com.huawei.opensdk.ec_sdk_demo.ui.im.GroupMemberAddActivity;
import com.huawei.opensdk.imservice.ImMgr;

import java.util.ArrayList;
import java.util.List;

public class GroupMemberAddPresenter extends MVPBasePresenter<IGroupMemberAddContract.IGroupMemberAddView>
        implements IGroupMemberAddContract.IGroupMemberAddPresenter, LocBroadcastReceiver/*, LocBroadcastReceiver*/
{
    private String[] mBroadcastNames = new String[]{CustomBroadcastConstants.ACTION_QUERY_GROUP_MEMBER};
    private ConstGroup mConstGroup;
    private List<PersonalContact> mAddMemberContacts = new ArrayList<>();
    private List<ConstGroupContact> mGroupContacts;

    @Override
    public void registerBroadcast()
    {
        LocBroadcast.getInstance().registerBroadcast(this, mBroadcastNames);
    }

    @Override
    public void unregisterBroadcast()
    {
        LocBroadcast.getInstance().unRegisterBroadcast(this, mBroadcastNames);
    }

    @Override
    public void onReceive(String broadcastName, Object obj)
    {
        if (CustomBroadcastConstants.ACTION_QUERY_GROUP_MEMBER.equals(broadcastName))
        {
            mGroupContacts = ImMgr.getInstance().getGroupMemberById(mConstGroup.getGroupId());
            mView.refreshGroupMember(mAddMemberContacts);
        }
    }

    /**
     * Whether it already exists
     */
    private boolean isExistGroup(PersonalContact personalContact)
    {
        if (mGroupContacts == null)
        {
            return false;
        }

        for (PersonalContact friend : mGroupContacts)
        {
            if (friend.getEspaceNumber().equals(personalContact.getEspaceNumber()))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public void handleResult(int requestCode, int resultCode, Intent data)
    {
        if (Activity.RESULT_OK == resultCode && GroupMemberAddActivity.ADD_MEMBER_REQUEST_CODE == requestCode)
        {
            PersonalContact personalContact = (PersonalContact) data.getSerializableExtra(UIConstants.PERSONAL_CONTACT);
            if (null != personalContact)
            {
                if (isExistGroup(personalContact))
                {
                    mView.toast(R.string.discussion_exist_tip);
                    return;
                }
                mAddMemberContacts.add(personalContact);
                mView.refreshGroupMember(mAddMemberContacts);
            }
        }
    }

    @Override
    public void setConstGroup(ConstGroup mConstGroup)
    {
        this.mConstGroup = mConstGroup;
    }

    @Override
    public List<ConstGroupContact> getGroupMember()
    {
        return mGroupContacts;
    }

    @Override
    public void queryGroupMembers()
    {
        ImMgr.getInstance().queryGroupMembers(mConstGroup);
    }

    @Override
    public void searchContact(String text)
    {
        ImMgr.getInstance().searchFuzzyContact(text);
    }

    @Override
    public List<PersonalContact> getSearchResult()
    {
        return null;
    }

    @Override
    public void inviteToGroup()
    {
        List<String> contactsAccount = new ArrayList<>();
        for (PersonalContact contact : mAddMemberContacts)
        {
            contactsAccount.add(contact.getEspaceNumber());
        }
        ImMgr.getInstance().inviteJoinGroup(mConstGroup, contactsAccount, "");
    }
}
