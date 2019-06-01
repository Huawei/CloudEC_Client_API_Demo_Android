package com.huawei.opensdk.ec_sdk_demo.logic.im.mvp;

import android.content.Intent;

import com.huawei.opensdk.ec_sdk_demo.logic.im.MessageItemType;
import com.huawei.opensdk.imservice.ImChatMsgInfo;
import com.huawei.opensdk.imservice.ImConstant;

import java.util.List;

public interface ChatContract
{
    interface ChatView
    {
        void refreshRecentChatList(List<MessageItemType> list);

        void toast(int id);

        void updatePersonalStatus(ImConstant.ImStatus status);

        void updateGroupName(String obj);

        void showInputtingStatus(boolean isInputting);

        void showWithdrawResult(String origin);
    }

    interface ChatPresenter
    {
        void initData(Object o);

        String getChatId();

        String getName();

        String getMyAccount();

        ImConstant.ChatMsgType getChatType();

        void loadStatus();

        boolean isIsGroup();

        void loadMoreHistoryMessage();

        void loadHistoryMessage();

        void loadUnReadMessage();

        void loadCurrentChat();

        void registerBroadcast();

        void unregisterBroadcast();

        CharSequence parseInnerEmotion(String ss);

        int setInputStatus(boolean isInputting);

        ImChatMsgInfo sendMessage(String trim);

        CharSequence parseSpan(String content);

        void addItem(MessageItemType item);

        void refreshViewAfterSendMessage(ImChatMsgInfo instantMessage);

        void startRecord();

//        InstantMessage stopRecord();

        void handleActivityResult(int requestCode, int resultCode, Intent data);

        void makeCall();

        void gotoDetailActivity();

        void subscribeContactState();

        void delHistoryMessage(int position);

        void withdrawMessage(int position);
    }
}
