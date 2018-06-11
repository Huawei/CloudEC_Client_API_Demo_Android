package com.huawei.opensdk.ec_sdk_demo.logic.im.mvp;

import com.huawei.data.entity.RecentChatContact;

import java.util.List;


public interface RecentChatContract
{
    interface RecentChatView
    {
        void refreshRecentChatList(List<RecentChatContact> list);
    }

    interface RecentChatPresenter
    {
        void regRecentSessionReceiver();

        void unregRecentSessionReceiver();

        List<RecentChatContact> loadRecentChats();

        void resetData();
    }
}
