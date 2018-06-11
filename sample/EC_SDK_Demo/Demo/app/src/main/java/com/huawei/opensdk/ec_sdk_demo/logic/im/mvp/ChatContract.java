package com.huawei.opensdk.ec_sdk_demo.logic.im.mvp;

import android.content.Intent;

import com.huawei.contacts.PersonalContact;
import com.huawei.data.entity.InstantMessage;
import com.huawei.opensdk.ec_sdk_demo.logic.im.MessageItemType;

import java.util.List;

public interface ChatContract
{
    interface ChatView
    {
        void refreshRecentChatList(List<MessageItemType> list);

        void toast();

        void updatePersonalStatus(PersonalContact contact);

        void updateGroupName(String obj);
    }

    interface ChatPresenter
    {
        void initData(Object o);

        String getChatId();

        String getName();

        int getChatType();

        boolean isIsGroup();

        void loadMoreHistoryMessage();

        void loadHistoryMessage();

        void registerBroadcast();

        void unregisterBroadcast();

        CharSequence parseInnerEmotion(String ss);

        InstantMessage sendMessage(String trim);

        CharSequence parseSpan(String content);

        void addItem(MessageItemType item);

        void refreshViewAfterSendMessage(InstantMessage instantMessage);

        void startRecord();

        InstantMessage stopRecord();

        void handleActivityResult(int requestCode, int resultCode, Intent data);

        void makeCall();

        void gotoDetailActivity();

        void subscribeContactState();
    }
}
