package com.huawei.opensdk.ec_sdk_demo.logic.im.mvp;

import com.huawei.opensdk.imservice.ImRecentChatInfo;

import java.util.List;


public interface RecentChatContract
{
    interface RecentChatView
    {
        void refreshRecentChatList(List<ImRecentChatInfo> list);
    }

    interface RecentChatPresenter
    {
        void regRecentSessionReceiver();

        void unregRecentSessionReceiver();

        List<ImRecentChatInfo> loadRecentChats();

        void resetData();
    }
}
