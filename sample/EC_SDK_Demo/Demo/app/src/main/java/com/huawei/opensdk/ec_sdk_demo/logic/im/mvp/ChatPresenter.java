package com.huawei.opensdk.ec_sdk_demo.logic.im.mvp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;

import com.huawei.contacts.PersonalContact;
import com.huawei.dao.impl.RecentChatContactDao;
import com.huawei.data.ConstGroup;
import com.huawei.data.GetRoamingMessageData;
import com.huawei.data.entity.InstantMessage;
import com.huawei.data.entity.RecentChatContact;
import com.huawei.data.unifiedmessage.MediaResource;
import com.huawei.opensdk.callmgr.CallMgr;
import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcastReceiver;
import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.logic.im.ChatComparator;
import com.huawei.opensdk.ec_sdk_demo.logic.im.ChatTools;
import com.huawei.opensdk.ec_sdk_demo.logic.im.MessageItemType;
import com.huawei.opensdk.ec_sdk_demo.logic.im.emotion.SpannableStringParser;
import com.huawei.opensdk.ec_sdk_demo.ui.IntentConstant;
import com.huawei.opensdk.ec_sdk_demo.ui.base.MVPBasePresenter;
import com.huawei.opensdk.ec_sdk_demo.ui.im.contact.HeadIconTools;
import com.huawei.opensdk.ec_sdk_demo.util.ActivityUtil;
import com.huawei.opensdk.imservice.ImMgr;
import com.huawei.utils.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This class is about chat presenter.
 */
public class ChatPresenter extends MVPBasePresenter<ChatContract.ChatView> implements ChatContract.ChatPresenter, LocBroadcastReceiver
{
    public static final int SELECT_VIDEO_FROM_LOCAL = 3;

    public static final int VOICE_RECORD_TIME_RATE = 100;
    private final Context mContext;
    private boolean mIsGroup;
    private String mChatId;
    private int timeCount;
    private Timer timer;
    private int mChatType;
    private PersonalContact personalContact;
    private ConstGroup constGroup;

    private List<MessageItemType> mList = new ArrayList<>();

    private String[] mAction = new String[]{CustomBroadcastConstants.ACTION_IM_QUERY_HISTORY,
            CustomBroadcastConstants.ACTION_RECEIVE_SESSION_CHANGE,
            CustomBroadcastConstants.ACTION_MODIFY_GROUP_MEMBER,
            CustomBroadcastConstants.ACTION_IM_FRIEND_STATUS_CHANGE,
            CustomBroadcastConstants.ACTION_DOWNLOAD_FINISH};
    private SpannableStringParser mEmotionParser;
    private List<PersonalContact> mFriends;
    private List<PersonalContact> mStrangers;
    private String mName;

    public ChatPresenter(Context context)
    {
        this.mContext = context;
    }

    @Override
    public void initData(Object data)
    {
        if (data instanceof PersonalContact)
        {
            mIsGroup = false;
            mChatId = ((PersonalContact) data).getEspaceNumber();
            mName = mChatId;
            mChatType = RecentChatContact.ESPACECHATTER;
            personalContact = (PersonalContact) data;
        }
        else if (data instanceof ConstGroup)
        {
            mIsGroup = true;
            constGroup = (ConstGroup) data;
            mChatId = constGroup.getGroupId();
            mName = constGroup.getName();

            if (((ConstGroup) data).getGroupType() == ConstGroup.FIXED)
            {
                mChatType = RecentChatContact.GROUPCHATTER;
            }
            else
            {
                mChatType = RecentChatContact.DISCUSSIONCHATTER;
            }

        }

        mEmotionParser = new SpannableStringParser();
    }

    @Override
    public String getChatId()
    {
        return mChatId;
    }

    @Override
    public String getName()
    {
        return mName;
    }

    @Override
    public int getChatType()
    {
        return mChatType;
    }

    @Override
    public boolean isIsGroup()
    {
        return mIsGroup;
    }

    @Override
    public void loadMoreHistoryMessage()
    {
        String msgId = getFirstMessageId();
        int requestType = getMsgType();
        ImMgr.getInstance().queryHistoryMessage(requestType, mChatId, msgId, 10);
    }

    @Override
    public void loadHistoryMessage()
    {
        int requestType = getMsgType();
        ImMgr.getInstance().queryHistoryMessage(requestType, mChatId, "", 10);
    }

    private String getFirstMessageId()
    {
        String msgId = "";
        if (mList == null || mList.isEmpty())
        {
            return null;
        }
        for (MessageItemType message : mList)
        {
            msgId = message.instantMsg.getMessageId();
            return msgId;
        }
        return msgId;
    }

    private int getMsgType()
    {
        int requestMsgType = 0;
        if (mChatType == RecentChatContact.GROUPCHATTER || mChatType == RecentChatContact.DISCUSSIONCHATTER)
        {
            requestMsgType = GetRoamingMessageData.GROUP_CHAT_MESSAGE;
        }
        else if (mChatType == RecentChatContact.ESPACECHATTER)
        {
            requestMsgType = GetRoamingMessageData.P2P_CHAT_MESSAGE;
        }
        return requestMsgType;
    }

    @Override
    public void registerBroadcast()
    {
        LocBroadcast.getInstance().registerBroadcast(this, mAction);
    }

    @Override
    public void unregisterBroadcast()
    {
        LocBroadcast.getInstance().unRegisterBroadcast(this, mAction);
    }

    @Override
    public CharSequence parseInnerEmotion(String ss)
    {
        return mEmotionParser.parseInnerEmotion(ss);
    }

    @Override
    public InstantMessage sendMessage(String text)
    {
        if (!TextUtils.isEmpty(text))
        {
            return ImMgr.getInstance().sendMessage(mChatId, mIsGroup, text);
        }
        return null;
    }

    @Override
    public CharSequence parseSpan(String content)
    {
        if (TextUtils.isEmpty(content))
        {
            return content;
        }
        SpannableString ss = null;
        try
        {
            ss = mEmotionParser.parseEmotion(content);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return ss == null ? content : ss;
    }

    @Override
    public void addItem(MessageItemType item)
    {
        mList.add(item);
    }

    @Override
    public void onReceive(String broadcastName, Object obj)
    {
        switch (broadcastName)
        {
            case CustomBroadcastConstants.ACTION_IM_QUERY_HISTORY:
                List<InstantMessage> list = (List<InstantMessage>) obj;
                handleInstantMessage(list);
                break;
            case CustomBroadcastConstants.ACTION_RECEIVE_SESSION_CHANGE:
                List<InstantMessage> instantMessages = (List<InstantMessage>) obj;
                if (mIsGroup)
                {
                    if (instantMessages.get(0).getToId().equals(mChatId))
                    {
                        handleInstantMessage(instantMessages);
                    }
                }
                else
                {
                    if (instantMessages.get(0).getFromId().equals(mChatId))
                    {
                        handleInstantMessage(instantMessages);
                    }
                }
                break;
            case CustomBroadcastConstants.ACTION_IM_FRIEND_STATUS_CHANGE:
                List<String> stateChangedMembers = (List<String>) obj;
                String changedMember = stateChangedMembers.get(0);
                mFriends = ImMgr.getInstance().getFriends();
                if (!mIsGroup && changedMember.equals(mChatId) && isFriendStateChanged(changedMember))
                {
                    friendStateChanged(changedMember);
                }
                else
                {
                    strangerStateChanged(changedMember);
                }
                break;
            case CustomBroadcastConstants.ACTION_MODIFY_GROUP_MEMBER:
                mView.updateGroupName((String)obj);
                break;
            case CustomBroadcastConstants.ACTION_DOWNLOAD_FINISH:
                mView.refreshRecentChatList(mList);
                break;
            default:
                break;
        }
    }

    private void friendStateChanged(String account)
    {
        for (PersonalContact contact : mFriends)
        {
            if (account.equals(contact.getEspaceNumber()))
            {
                mView.updatePersonalStatus(contact);
            }
        }
    }

    private void strangerStateChanged(String account)
    {
        //chat with stranger, get it status
        mStrangers = ImMgr.getInstance().getStrangers();
        for (PersonalContact contact : mStrangers)
        {
            if (account.equals(contact.getEspaceNumber()))
            {
                mView.updatePersonalStatus(contact);
            }
        }
    }

    private boolean isFriendStateChanged(String account)
    {
        for (PersonalContact contact : mFriends)
        {
            if (account.equals(contact.getEspaceNumber()))
            {
                return true;
            }
        }
        return false;
    }

    public void refreshViewAfterSendMessage(InstantMessage instantMessage)
    {
        if (instantMessage.getMediaRes() == null)
        {
            //IM
            MessageItemType item = new MessageItemType();
            item.instantMsg = instantMessage;
            item.content = parseSpan(instantMessage.getContent());
            addItem(item);
        }
        else
        {
            //UM
            MessageItemType item = new MessageItemType();
            item.instantMsg = instantMessage;
            item.content = instantMessage.getContent();
            addItem(item);
        }

        addRecentContactMessage(mChatId, instantMessage);
        mView.refreshRecentChatList(mList);
    }

    /**
     * Add recent contact message.
     * @param chatId the chat id
     * @param message the message
     */
    private void addRecentContactMessage(String chatId, InstantMessage message)
    {
        RecentChatContact chatContact = getRecentChatContact(chatId, message.getMsgType(), message.getNickname());
        chatContact.setEndTime(message.getTime());
        ImMgr.getInstance().insertRecentSession(chatContact);
    }

    private RecentChatContact getRecentChatContact(String chatId, int msgType, String nickName)
    {
        RecentChatContact chatContact = RecentChatContactDao.getContact(chatId, msgType);

        if (chatContact == null)
        {
            chatContact = new RecentChatContact(chatId, msgType, nickName);
        }
        else if (TextUtils.isEmpty(chatContact.getNickname()))
        {
            chatContact.setNickname(nickName);
        }

        return chatContact;
    }

    private void handleInstantMessage(List<InstantMessage> instantMessages)
    {
        for (InstantMessage message : instantMessages)
        {
            MessageItemType item = new MessageItemType();
            item.instantMsg = message;
            String msgContent = message.getContent();
            item.content = mEmotionParser.parseSpan(msgContent);
            mList.add(item);
        }
        Collections.sort(mList, new ChatComparator());
        markReadMessage(instantMessages);
        mView.refreshRecentChatList(mList);
    }

    private void markReadMessage(List<InstantMessage> messages)
    {
        for (InstantMessage message : messages)
        {
            ImMgr.getInstance().proMarkRead(message, mChatId, mChatType);
        }
    }

    public List<MessageItemType> getMessages()
    {
        return mList;
    }

    public void startRecord()
    {
        ImMgr.getInstance().startRecord();
        startRecordTimeChange();
    }

    private void startRecordTimeChange()
    {
        timeCount = 0;
        timer = new Timer();
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                timeCount++;
            }
        }, 0, VOICE_RECORD_TIME_RATE);
    }

    @Override
    public InstantMessage stopRecord()
    {
        ImMgr.getInstance().stopRecord();
        String recordPath = ImMgr.getInstance().getRecordPath();
        stopRecordTimeChange();
        int time = timeCount / (1000 / VOICE_RECORD_TIME_RATE);
        //record time length less than One Seconds
        if (time < 1)
        {
            mView.toast();
            delRecordFile(recordPath);
        }
        InstantMessage instantMessage = ImMgr.getInstance().sendMessage(mChatId, mIsGroup, recordPath, MediaResource.MEDIA_AUDIO, time);
        if (instantMessage != null)
        {
            refreshViewAfterSendMessage(instantMessage);
            mView.refreshRecentChatList(mList);
        }
        return instantMessage;
    }

    @Override
    public void handleActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == HeadIconTools.SELECT_PICTURE_FROM_LOCAL)
        {
            try
            {
                Uri photoUri = HeadIconTools.getPhotoUri();
                if (photoUri == null)
                {
                    LogUtil.e(UIConstants.DEMO_TAG, "photoUri is null");
                    return;
                }
                InstantMessage sendMessage = ImMgr.getInstance().sendMessage(mChatId, mIsGroup, photoUri.getPath(), MediaResource.MEDIA_PICTURE);
                if (sendMessage != null)
                {
                    refreshViewAfterSendMessage(sendMessage);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else if (requestCode == SELECT_VIDEO_FROM_LOCAL)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                Bundle bundle = data.getBundleExtra(UIConstants.BUNDLE);
                String videoPath = bundle.getString(UIConstants.VIDEO_SYSTEM_PATH);
                InstantMessage sendMessage = ImMgr.getInstance().sendMessage(mChatId, mIsGroup, videoPath, MediaResource.MEDIA_VIDEO);
                if (sendMessage != null)
                {
                    ChatTools.copyVideoFile(sendMessage);
                    refreshViewAfterSendMessage(sendMessage);
                }
            }
        }
    }

    @Override
    public void makeCall()
    {
        if (mChatType == RecentChatContact.ESPACECHATTER)
        {
            CallMgr.getInstance().startCall(mChatId, false);
        }
    }

    @Override
    public void gotoDetailActivity()
    {
        if (mChatType == RecentChatContact.ESPACECHATTER && personalContact != null)
        {
            Intent intent = new Intent(IntentConstant.SINGLE_CHAT_SETTING_ACTIVITY_ACTION);
            Bundle bundle = new Bundle();
            bundle.putSerializable(UIConstants.PERSONAL_CONTACT, personalContact);
            intent.putExtra(UIConstants.BUNDLE_KEY, bundle);
            ActivityUtil.startActivity(mContext, intent);
        }
        else if (constGroup != null)
        {
            // TODO: 2017/9/28
            Intent intent = new Intent(IntentConstant.GROUP_DETAIL_SETTING_ACTIVITY_ACTION);
            Bundle bundle = new Bundle();
            bundle.putSerializable(UIConstants.CONST_GROUP, constGroup);
            intent.putExtra(UIConstants.BUNDLE_KEY, bundle);
            ActivityUtil.startActivity(mContext, intent);
        }
    }

    @Override
    public void subscribeContactState()
    {
        if (!mIsGroup)
        {
            List<String> subscribeList = new ArrayList<>();
            subscribeList.add(mChatId);
            ImMgr.getInstance().subscribeState(subscribeList);
            ImMgr.getInstance().searchFuzzyContact(mChatId);
        }
    }

    private void delRecordFile(String recordPath)
    {
        // delete former record audio file
        File file = FileUtil.newFile(recordPath);
        if (file.exists())
        {
            if (!file.delete())
            {
                LogUtil.e(UIConstants.DEMO_TAG, "delete file error");
            }
        }
    }

    private void stopRecordTimeChange()
    {
        if (null != timer)
        {
            timer.purge();
            timer.cancel();
            timer = null;
        }
    }
}
