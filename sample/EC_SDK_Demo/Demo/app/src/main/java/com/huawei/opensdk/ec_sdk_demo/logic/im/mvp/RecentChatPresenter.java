package com.huawei.opensdk.ec_sdk_demo.logic.im.mvp;

import android.util.Log;

import com.huawei.contacts.ContactCache;
import com.huawei.contacts.PersonalContact;
import com.huawei.data.entity.RecentChatContact;
import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcastReceiver;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.imservice.ImMgr;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is about recent chat presenter.
 */
public class RecentChatPresenter implements RecentChatContract.RecentChatPresenter, LocBroadcastReceiver
{
    private RecentChatContract.RecentChatView mView;

    private List<RecentChatContact> mRecentChatList = new ArrayList<>();

    private String[] mAction = new String[]{CustomBroadcastConstants.ACTION_RECEIVE_SESSION_CHANGE,
            CustomBroadcastConstants.ACTION_IM_LOGIN_SUCCESS, CustomBroadcastConstants.ACTION_SEND_MESSAGE_SUCCESS,
            CustomBroadcastConstants.ACTION_REFRESH_GROUP_MEMBER};

    @Override
    public List<RecentChatContact> loadRecentChats()
    {
        mRecentChatList = ImMgr.getInstance().loadRecentSession();

        if (mRecentChatList == null || mRecentChatList.size() == 0)
        {
            Log.e(UIConstants.DEMO_TAG, "RecentChatContact size = 0");
            return null;
        }
        for (RecentChatContact chatContact : mRecentChatList)
        {
            String account = chatContact.getContactAccount();
            PersonalContact pContact = ContactCache.getIns().getContactByAccount(account);
            if (pContact == null)
            {
                ImMgr.getInstance().searchFuzzyContact(account);
            }
        }
        mView.refreshRecentChatList(mRecentChatList);
        return mRecentChatList ;
    }

    @Override
    public void resetData()
    {
        mRecentChatList.clear();
    }

    public RecentChatPresenter(RecentChatContract.RecentChatView view)
    {
        mView = view;
        mRecentChatList = ImMgr.getInstance().loadRecentSession();
    }

    public List<RecentChatContact> getRecentChat()
    {
        return mRecentChatList;
    }

    @Override
    public void regRecentSessionReceiver()
    {
        LocBroadcast.getInstance().registerBroadcast(this, mAction);
    }

    @Override
    public void unregRecentSessionReceiver()
    {
        LocBroadcast.getInstance().unRegisterBroadcast(this, mAction);
    }

    @Override
    public void onReceive(String broadcastName, Object obj)
    {
        switch (broadcastName)
        {
            case CustomBroadcastConstants.ACTION_RECEIVE_SESSION_CHANGE:
            case CustomBroadcastConstants.ACTION_IM_LOGIN_SUCCESS:
            case CustomBroadcastConstants.ACTION_SEND_MESSAGE_SUCCESS:
            case CustomBroadcastConstants.ACTION_REFRESH_GROUP_MEMBER:
                mRecentChatList.clear();
                loadRecentChats();
                break;
            default:
                break;
        }
    }
}
