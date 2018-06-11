package com.huawei.opensdk.ec_sdk_demo.logic.contact.mvp;

import android.content.Context;
import android.content.Intent;

import com.huawei.data.ConstGroup;
import com.huawei.data.ConstGroupContact;
import com.huawei.data.ExecuteResult;
import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcastReceiver;
import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.ui.IntentConstant;
import com.huawei.opensdk.ec_sdk_demo.ui.base.MVPBasePresenter;
import com.huawei.opensdk.ec_sdk_demo.util.ActivityUtil;
import com.huawei.opensdk.imservice.ImMgr;
import com.huawei.opensdk.loginmgr.LoginMgr;

import java.util.ArrayList;
import java.util.List;

/**
 * Group Information logic Processing class.
 */
public class GroupDetailSettingPresenter extends MVPBasePresenter<IGroupDetailSettingContract.IGroupDetailSettingView> implements IGroupDetailSettingContract.IGroupDetailSettingPresenter, LocBroadcastReceiver
{
    private Context context;
    private ConstGroup constGroup;
    private String[] mBroadcastNames = new String[]{CustomBroadcastConstants.ACTION_REFRESH_GROUP_MEMBER,
            CustomBroadcastConstants.ACTION_MODIFY_GROUP_MEMBER, CustomBroadcastConstants.ACTION_QUERY_GROUP_MEMBER};
    private List<ConstGroupContact> mContacts = new ArrayList<>();

    public GroupDetailSettingPresenter(Context context)
    {
        this.context = context;
    }

    @Override
    public void setConstGroup(ConstGroup constGroup)
    {
        this.constGroup = constGroup;
    }

    @Override
    public List<ConstGroupContact> getGroupMembers()
    {
        return ImMgr.getInstance().getGroupMemberById(constGroup.getGroupId());
    }

    @Override
    public void enterChat()
    {
        Intent intent = new Intent(IntentConstant.IM_CHAT_ACTIVITY_ACTION);
        intent.putExtra(UIConstants.CHAT_TYPE, constGroup);
        ActivityUtil.startActivity(context, intent);
    }

    @Override
    public void showGroupMembers()
    {
        if (mContacts.isEmpty())
        {
            return;
        }
        Intent intent = new Intent(IntentConstant.GROUP_MEMBER_MEMBER_ACTIVITY_ACTION);
        intent.putExtra(UIConstants.GROUP_MEMBER, (ArrayList<ConstGroupContact>) mContacts);
        intent.putExtra(UIConstants.CONST_GROUP, constGroup);
        ActivityUtil.startActivity(context, intent);
    }

    @Override
    public void lockGroup()
    {
        ExecuteResult result = ImMgr.getInstance().transformGroup(constGroup);
        if (result.isResult())
        {
            constGroup.setGroupType(ConstGroup.FIXED);
        }
    }

    @Override
    public void clearHistory()
    {
        // TODO: 2017/9/29
    }

    @Override
    public void leaveGroup()
    {
        // TODO: 2017/9/29
    }

    @Override
    public void enterAddMember()
    {
        Intent intent = new Intent(IntentConstant.GROUP_ADD_MEMBER_ACTIVITY_ACTION);
        intent.putExtra(UIConstants.CONST_GROUP, constGroup);
        ActivityUtil.startActivity(context, intent);
    }

    @Override
    public void enterDelMembers()
    {
        if (mContacts.isEmpty())
        {
            return;
        }
        Intent intent = new Intent(IntentConstant.GROUP_MEMBER_MEMBER_ACTIVITY_ACTION);
        intent.putExtra(UIConstants.GROUP_MEMBER, (ArrayList<ConstGroupContact>) mContacts);
        intent.putExtra(UIConstants.CONST_GROUP, constGroup);
        intent.putExtra(UIConstants.GROUP_OPERATE_MODE, UIConstants.GROUP_OPERATE_DELETE);
        ActivityUtil.startActivity(context, intent);
    }

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
    public void queryGroupMembers()
    {
        ImMgr.getInstance().queryGroupMembers(constGroup);
    }

    @Override
    public void quitGroup()
    {
        if (LoginMgr.getInstance().getAccount().equals(constGroup.getOwner()) &&
                ConstGroup.FIXED == constGroup.getGroupType())
        {
            ImMgr.getInstance().deleteGroup(constGroup);
        }
        else
        {
            ImMgr.getInstance().leaveGroup(constGroup);
        }
    }

    @Override
    public void modifyGroup(ConstGroup constGroup)
    {
        ImMgr.getInstance().modifyGroup(constGroup);
    }

    @Override
    public void onReceive(String broadcastName, Object obj)
    {
        switch (broadcastName)
        {
            case CustomBroadcastConstants.ACTION_REFRESH_GROUP_MEMBER:
                queryGroupMembers();
                break;
            case CustomBroadcastConstants.ACTION_MODIFY_GROUP_MEMBER:
                String groupId = (String)obj;
                if (null == groupId)
                {
                    break;
                }
                mView.updateGroupInfo(groupId);
                break;
            case CustomBroadcastConstants.ACTION_QUERY_GROUP_MEMBER:
                mContacts = ImMgr.getInstance().getGroupMemberById(constGroup.getGroupId());
//                constGroup = ImMgr.getInstance().getGroupById(constGroup.getGroupId());
//                Log.d("constGroup", "intro:" + constGroup.getIntro() + "|ann:" + constGroup.getAnnounce());
                int size = mContacts.size();
                if (0 == size)
                {
                    break;
                }
                mView.updateTotalMember(size);
                LogUtil.i(UIConstants.DEMO_TAG, "group member size:" + size);
                break;
            default:
                break;
        }
    }
}
