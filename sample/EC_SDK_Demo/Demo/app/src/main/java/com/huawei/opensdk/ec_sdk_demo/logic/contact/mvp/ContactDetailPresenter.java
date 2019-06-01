package com.huawei.opensdk.ec_sdk_demo.logic.contact.mvp;

import android.content.Context;
import android.content.Intent;

import com.huawei.opensdk.callmgr.CallMgr;
import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcastReceiver;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.ui.IntentConstant;
import com.huawei.opensdk.ec_sdk_demo.ui.base.MVPBasePresenter;
import com.huawei.opensdk.ec_sdk_demo.util.ActivityUtil;
import com.huawei.opensdk.imservice.ImConstant;
import com.huawei.opensdk.imservice.ImContactGroupInfo;
import com.huawei.opensdk.imservice.ImContactInfo;
import com.huawei.opensdk.imservice.ImMgr;

import java.util.ArrayList;
import java.util.List;

public class ContactDetailPresenter extends MVPBasePresenter<IContactDetailContract.IContactDetailView> implements IContactDetailContract.IContactDetailPresenter
{
    private List<ImContactGroupInfo> groupInfoList = new ArrayList<>();
    private String detectAccount;
    private ImContactInfo contactInfo;

    private String[] broadcastNames = new String[]{CustomBroadcastConstants.ACTION_IM_DELETE_CONTACT_RESULT,
            CustomBroadcastConstants.ACTION_IM_ADD_CONTACT_RESULT,
            CustomBroadcastConstants.ACTION_IM_USER_STATUS_CHANGE};

    private LocBroadcastReceiver receiver = new LocBroadcastReceiver()
    {
        @Override
        public void onReceive(String broadcastName, Object obj)
        {
            switch (broadcastName)
            {
                case CustomBroadcastConstants.ACTION_IM_DELETE_CONTACT_RESULT:
//                    BaseResponseData deleteFriendResp = (BaseResponseData) obj;
//                    if (deleteFriendResp.getStatus() == ResponseCodeHandler.ResponseCode.REQUEST_SUCCESS)
                    {
                        getView().refreshDeleteContactButton(false);
                    }
//                    else
                    {
                        getView().showCustomToast(R.string.delete_friend_failed);
                    }
                    break;
                case CustomBroadcastConstants.ACTION_IM_ADD_CONTACT_RESULT:
//                    AddFriendResp addFriendResp = (AddFriendResp) obj;
//                    if (addFriendResp.getStatus() == ResponseCodeHandler.ResponseCode.REQUEST_SUCCESS)
                    {
                        getView().refreshDeleteContactButton(true);
                    }
//                    else
                    {
                        getView().showCustomToast(R.string.add_friend_failed);
                    }
                    break;
                case CustomBroadcastConstants.ACTION_IM_USER_STATUS_CHANGE:
                    contactInfo.setStatus(ImMgr.getInstance().updateUserStatus(detectAccount));
                    getView().showUserStatus(contactInfo.getStatus());
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void gotoChatActivity(Context context)
    {
        Intent intent = new Intent(IntentConstant.IM_CHAT_ACTIVITY_ACTION);
        intent.putExtra(UIConstants.CHAT_TYPE, contactInfo);
        ActivityUtil.startActivity(context, intent);
    }

    @Override
    public void makeCall(String number)
    {
        CallMgr.getInstance().startCall(number, false);
    }

    @Override
    public void makeVideo(String number)
    {
        CallMgr.getInstance().startCall(number, false);
    }

    @Override
    public void deleteContact(long contactId)
    {
        int result = ImMgr.getInstance().delFriend(contactId);
        if (0 != result)
        {
            getView().showCustomToast(R.string.delete_friend_failed);
            return;
        }
        getView().finishActivity(R.string.delfriendsuccess);
    }

    @Override
    public void addContact(String teamId) {
//        ImMgr.getInstance().addFriend(personalContact, teamId);
    }

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

    @Override
    public void moveContact(long newGroupId, long oldGroupId, long contactId, boolean isMove) {
        int opType;
        if (isMove)
        {
            opType = ImConstant.OpContactType.CONTACT_MOVE_TO_NEW_GROUP;
        }
        else
        {
            opType = ImConstant.OpContactType.CONTACT_COPY_TO_NEW_GROUP;
        }
        int result = ImMgr.getInstance().opGroupContact(newGroupId, oldGroupId, contactId, opType);
        if (0 != result)
        {
            getView().showCustomToast(R.string.move_copy_contact_failed);
            return;
        }
        getView().finishActivity(R.string.move_copy_contact_success);
    }

    @Override
    public void refreshGroupList(List<ImContactGroupInfo> contactGroupList, long contactId) {
        groupInfoList.clear();
        for (ImContactGroupInfo groupInfo : contactGroupList)
        {
            boolean isAdd = true;
            ImContactGroupInfo info = ImMgr.getInstance().getContactGroupByGroupId(groupInfo.getGroupId());
            if (null == info.getList() || info.getList().size() == 0)
            {
                groupInfoList.add(groupInfo);
                continue;
            }
            for (ImContactInfo contactInfo : info.getList())
            {
                if (contactId == contactInfo.getContactId())
                {
                    isAdd = false;
                    break;
                }
            }
            if (isAdd)
            {
                groupInfoList.add(groupInfo);
            }
        }
        getView().showTeamDialog(groupInfoList);
    }

//    @Override
//    public void detectUserStatus(String account) {
//        this.detectAccount = account;
//        List<String> accounts = new ArrayList<>();
//        accounts.add(detectAccount);
//        int result = ImMgr.getInstance().probeUserStatus(accounts);
//        if (0 != result)
//        {
//            getView().showCustomToast(R.string.detect_user_status_failed);
//        }
//    }

    @Override
    public void setUserInfo(ImContactInfo contactInfo) {
        this.contactInfo = contactInfo;
        this.detectAccount = contactInfo.getAccount();
    }

}
