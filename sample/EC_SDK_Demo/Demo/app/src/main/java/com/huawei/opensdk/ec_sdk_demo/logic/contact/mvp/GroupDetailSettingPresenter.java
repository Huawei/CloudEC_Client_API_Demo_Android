package com.huawei.opensdk.ec_sdk_demo.logic.contact.mvp;

import android.content.Context;
import android.content.Intent;

import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcastReceiver;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.ui.IntentConstant;
import com.huawei.opensdk.ec_sdk_demo.ui.base.ActivityStack;
import com.huawei.opensdk.ec_sdk_demo.ui.base.MVPBasePresenter;
import com.huawei.opensdk.ec_sdk_demo.ui.im.GroupMemberActivity;
import com.huawei.opensdk.ec_sdk_demo.ui.im.GroupMemberAddActivity;
import com.huawei.opensdk.ec_sdk_demo.util.ActivityUtil;
import com.huawei.opensdk.imservice.ImChatGroupInfo;
import com.huawei.opensdk.imservice.ImContactInfo;
import com.huawei.opensdk.imservice.ImMgr;
import com.huawei.opensdk.loginmgr.LoginMgr;

import java.util.List;

/**
 * Group Information logic Processing class.
 */
public class GroupDetailSettingPresenter extends MVPBasePresenter<IGroupDetailSettingContract.IGroupDetailSettingView> implements IGroupDetailSettingContract.IGroupDetailSettingPresenter, LocBroadcastReceiver
{
    private Context context;
    private ImChatGroupInfo pChatGroupInfo;

    private String[] mBroadcastNames = new String[]{CustomBroadcastConstants.ACTION_IM_CHAT_GROUP_UPDATE,
            CustomBroadcastConstants.ACTION_IM_CHAT_GROUP_ADD_MEMBER,
            CustomBroadcastConstants.ACTION_IM_CHAT_GROUP_DEL_MEMBER,
            CustomBroadcastConstants.ACTION_IM_CHAT_GROUP_DISMISS,
            CustomBroadcastConstants.ACTION_IM_CHAT_GROUP_LEAVE_RESULT};

    public GroupDetailSettingPresenter(Context context)
    {
        this.context = context;
    }

    @Override
    public void setChatGroupInfo(ImChatGroupInfo chatGroupInfo) {
        this.pChatGroupInfo = chatGroupInfo;
    }

//    @Override
//    public List<ConstGroupContact> getGroupMembers()
//    {
//        return ImMgr.getInstance().getGroupMemberById(constGroup.getGroupId());
//    }

    @Override
    public void enterChat()
    {
        Intent intent = new Intent(IntentConstant.IM_CHAT_ACTIVITY_ACTION);
        intent.putExtra(UIConstants.CHAT_TYPE, pChatGroupInfo);
        ActivityUtil.startActivity(context, intent);
    }

    @Override
    public void showGroupMembers()
    {
        if (pChatGroupInfo.getList().isEmpty())
        {
            return;
        }
        Intent intent = new Intent(IntentConstant.GROUP_MEMBER_ACTIVITY_ACTION);
        intent.putExtra(UIConstants.IM_CHAT_GROUP_INFO, pChatGroupInfo);
        ActivityUtil.startActivity(context, intent);
    }

    @Override
    public void lockGroup()
    {
//        ExecuteResult result = ImMgr.getInstance().transformGroup(constGroup);
//        if (result.isResult())
//        {
//            constGroup.setGroupType(ConstGroup.FIXED);
//        }
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
        intent.putExtra(UIConstants.IM_CHAT_GROUP_INFO, pChatGroupInfo);
        ActivityUtil.startActivity(context, intent);
    }

    @Override
    public void enterDelMembers()
    {
        if (pChatGroupInfo.getList().isEmpty())
        {
            return;
        }
        Intent intent = new Intent(IntentConstant.GROUP_MEMBER_ACTIVITY_ACTION);
        intent.putExtra(UIConstants.IM_CHAT_GROUP_INFO, pChatGroupInfo);
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
        List<ImContactInfo> groupMembers = ImMgr.getInstance().getChatGroupMembers("", true);
        pChatGroupInfo.setList(groupMembers);
        if (groupMembers.isEmpty() || null == groupMembers)
        {
            getView().toast(R.string.group_member_empty);
        }
        getView().updateTotalMember(groupMembers.size());
    }

    @Override
    public void quitGroup()
    {
        int result;
        if (LoginMgr.getInstance().getAccount().equals(pChatGroupInfo.getOwnerAccount()))
        {
            result = ImMgr.getInstance().delChatGroup(pChatGroupInfo.getGroupId(), pChatGroupInfo.getGroupType());
        }
        else
        {
            result = ImMgr.getInstance().leaveChatGroup();
        }

        if (0 != result)
        {
            getView().toast(R.string.group_leaved_fail);
        }
    }

    @Override
    public void modifyGroup(ImChatGroupInfo imChatGroupInfo, int type)
    {
        int result = ImMgr.getInstance().modifyChatGroupInfo(imChatGroupInfo, type);
        if (0 != result)
        {
            getView().toast(R.string.update_chat_group_failed);
        }
    }

    @Override
    public void onReceive(String broadcastName, Object obj)
    {
        switch (broadcastName)
        {
                // 默认信息更新
            case CustomBroadcastConstants.ACTION_IM_CHAT_GROUP_UPDATE:
                if (obj instanceof ImChatGroupInfo)
                {
                    pChatGroupInfo = (ImChatGroupInfo) obj;
                }
                getView().updateGroupInfo(pChatGroupInfo);
                getView().toast(R.string.update_chat_group_success);
                break;

                // 群组增加成员
            case CustomBroadcastConstants.ACTION_IM_CHAT_GROUP_ADD_MEMBER:
                queryGroupMembers();
                getView().toast(R.string.add_member_success);
                ActivityStack.getIns().popup(GroupMemberAddActivity.class);
                break;

                // 群组删除成员
            case CustomBroadcastConstants.ACTION_IM_CHAT_GROUP_DEL_MEMBER:
                queryGroupMembers();
                getView().toast(R.string.del_member_success);
                ActivityStack.getIns().popup(GroupMemberActivity.class);
                break;

                // 群组解散
            case CustomBroadcastConstants.ACTION_IM_CHAT_GROUP_DISMISS:
                getView().toast(R.string.dis_chat_group_success);
                getView().finishActivity();
                break;

                // 离开固定群结果
            case CustomBroadcastConstants.ACTION_IM_CHAT_GROUP_LEAVE_RESULT:
                if (0 == (int) obj)
                {
                    getView().toast(R.string.leave_chat_group_success);
                    getView().finishActivity();
                    return;
                }
                getView().toast(R.string.leave_chat_group_failed);
                break;


            case CustomBroadcastConstants.ACTION_REFRESH_GROUP_MEMBER:
//                queryGroupMembers();
                break;
            case CustomBroadcastConstants.ACTION_MODIFY_GROUP_MEMBER:
//                String groupId = (String)obj;
//                if (null == groupId)
//                {
//                    break;
//                }
//                getView.updateGroupInfo(groupId);
                break;
            case CustomBroadcastConstants.ACTION_QUERY_GROUP_MEMBER:
//                mContacts = ImMgr.getInstance().getGroupMemberById(constGroup.getGroupId());
//                constGroup = ImMgr.getInstance().getGroupById(constGroup.getGroupId());
//                Log.d("constGroup", "intro:" + constGroup.getIntro() + "|ann:" + constGroup.getAnnounce());
//                int size = mContacts.size();
//                if (0 == size)
                {
                    break;
                }
//                getView.updateTotalMember(size);
//                LogUtil.i(UIConstants.DEMO_TAG, "group member size:" + size);
//                break;
            default:
                break;
        }
    }
}
