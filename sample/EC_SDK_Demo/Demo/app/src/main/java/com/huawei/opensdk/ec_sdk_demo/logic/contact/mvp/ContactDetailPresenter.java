package com.huawei.opensdk.ec_sdk_demo.logic.contact.mvp;

import android.content.Context;
import android.content.Intent;

import com.huawei.common.constant.ResponseCodeHandler;
import com.huawei.contacts.PersonalContact;
import com.huawei.data.AddFriendResp;
import com.huawei.data.base.BaseResponseData;
import com.huawei.opensdk.callmgr.CallMgr;
import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcastReceiver;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.ui.IntentConstant;
import com.huawei.opensdk.ec_sdk_demo.ui.base.MVPBasePresenter;
import com.huawei.opensdk.ec_sdk_demo.util.ActivityUtil;
import com.huawei.opensdk.imservice.ImMgr;


public class ContactDetailPresenter extends MVPBasePresenter<IContactDetailContract.IContactDetailView> implements IContactDetailContract.IContactDetailPresenter
{
    private PersonalContact personalContact;

    private String[] broadcastNames = new String[]{CustomBroadcastConstants.ACTION_IM_DELETE_CONTACT_RESULT,
            CustomBroadcastConstants.ACTION_IM_ADD_CONTACT_RESULT};

    private LocBroadcastReceiver receiver = new LocBroadcastReceiver()
    {
        @Override
        public void onReceive(String broadcastName, Object obj)
        {
            switch (broadcastName)
            {
                case CustomBroadcastConstants.ACTION_IM_DELETE_CONTACT_RESULT:
                    BaseResponseData deleteFriendResp = (BaseResponseData) obj;
                    if (deleteFriendResp.getStatus() == ResponseCodeHandler.ResponseCode.REQUEST_SUCCESS)
                    {
                        getView().refreshDeleteContactButton(false);
                    }
                    else
                    {
                        getView().showCustomToast(R.string.delete_friend_failed);
                    }
                    break;
                case CustomBroadcastConstants.ACTION_IM_ADD_CONTACT_RESULT:
                    AddFriendResp addFriendResp = (AddFriendResp) obj;
                    if (addFriendResp.getStatus() == ResponseCodeHandler.ResponseCode.REQUEST_SUCCESS)
                    {
                        getView().refreshDeleteContactButton(true);
                    }
                    else
                    {
                        getView().showCustomToast(R.string.add_friend_failed);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public void setPersonalContact(PersonalContact personalContact)
    {
        this.personalContact = personalContact;
    }

    @Override
    public void gotoChatActivity(Context context)
    {
        Intent intent = new Intent(IntentConstant.IM_CHAT_ACTIVITY_ACTION);
        intent.putExtra(UIConstants.CHAT_TYPE, personalContact);
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
    public void deleteContact()
    {
        ImMgr.getInstance().deleteFriend(personalContact);
    }

    @Override
    public void addContact(String teamId) {
        ImMgr.getInstance().addFriend(personalContact, teamId);
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
}
