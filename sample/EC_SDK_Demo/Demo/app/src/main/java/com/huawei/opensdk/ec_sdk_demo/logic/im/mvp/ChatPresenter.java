package com.huawei.opensdk.ec_sdk_demo.logic.im.mvp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;

import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcastReceiver;
import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.logic.im.ChatComparator;
import com.huawei.opensdk.ec_sdk_demo.logic.im.MessageItemType;
import com.huawei.opensdk.ec_sdk_demo.logic.im.emotion.SpannableStringParser;
import com.huawei.opensdk.ec_sdk_demo.ui.IntentConstant;
import com.huawei.opensdk.ec_sdk_demo.ui.base.MVPBasePresenter;
import com.huawei.opensdk.ec_sdk_demo.ui.im.contact.HeadIconTools;
import com.huawei.opensdk.ec_sdk_demo.util.ActivityUtil;
import com.huawei.opensdk.imservice.ImChatGroupInfo;
import com.huawei.opensdk.imservice.ImChatMsgInfo;
import com.huawei.opensdk.imservice.ImConstant;
import com.huawei.opensdk.imservice.ImContactInfo;
import com.huawei.opensdk.imservice.ImMgr;
import com.huawei.opensdk.imservice.ImRecentChatInfo;
import com.huawei.opensdk.loginmgr.LoginMgr;

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
    private ImConstant.ChatMsgType mChatType;
//    private PersonalContact personalContact;
//    private ConstGroup constGroup;
    private String mContent;
    private ImRecentChatInfo mRecentChatInfo;
    private List<ImChatMsgInfo> mChatMsgList = new ArrayList<>();
    private boolean isFirstQuery = false;
    private boolean isFirstReceive = true;

    private List<MessageItemType> mList = new ArrayList<>();
    private MessageItemType itemType;
    private ImChatMsgInfo mSendMsgInfo;

    private String[] mAction = new String[]{CustomBroadcastConstants.ACTION_IM_QUERY_HISTORY,
            CustomBroadcastConstants.ACTION_RECEIVE_SESSION_CHANGE,
            CustomBroadcastConstants.ACTION_MODIFY_GROUP_MEMBER,
            CustomBroadcastConstants.ACTION_IM_FRIEND_STATUS_CHANGE,
            CustomBroadcastConstants.ACTION_DOWNLOAD_FINISH,
            CustomBroadcastConstants.ACTION_IM_USER_STATUS_CHANGE,
            CustomBroadcastConstants.ACTION_SEND_MESSAGE_SUCCESS,
            CustomBroadcastConstants.ACTION_IM_CHAT_INPUTTING_STATUS_IND,
            CustomBroadcastConstants.ACTION_IM_CHAT_WITHDRAW_MSG_FAILED,
            CustomBroadcastConstants.ACTION_IM_CHAT_WITHDRAW_MSG_IND};
    private SpannableStringParser mEmotionParser;
//    private List<PersonalContact> mFriends;
//    private List<PersonalContact> mStrangers;
    private String mName;
    private String mMyAccount;
    private ImContactInfo mChatContactInfo;
    private ImChatGroupInfo mChatGroupInfo;

    public ChatPresenter(Context context)
    {
        this.mContext = context;
    }

    @Override
    public void initData(Object data)
    {
        if (data instanceof ImContactInfo)
        {
            mIsGroup = false;
            mChatContactInfo = (ImContactInfo) data;
            mName = mChatContactInfo.getName();
            mChatId = mChatContactInfo.getAccount();
            mChatType = ImConstant.ChatMsgType.SINGLE_CHAT;
        }
        else if (data instanceof ImChatGroupInfo)
        {
            mIsGroup = true;
            mChatGroupInfo = (ImChatGroupInfo) data;
            mChatId = mChatGroupInfo.getGroupId();
            mName = mChatGroupInfo.getGroupName();
            if (mChatGroupInfo.getGroupType() == ImConstant.FIXED)
            {
                mChatType = ImConstant.ChatMsgType.FIXED_GROUP_CHAT;
            }
            else
            {
                mChatType = ImConstant.ChatMsgType.DISCUSSION_GROUP_CHAT;
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
    public String getMyAccount() {
        mMyAccount = LoginMgr.getInstance().getAccount();
        return mMyAccount;
    }

    @Override
    public ImConstant.ChatMsgType getChatType()
    {
        return mChatType;
    }

    @Override
    public void loadStatus() {
        if (null == mChatContactInfo)
        {
            return;
        }

        if (null == mChatContactInfo.getStatus())
        {
            List<String> accounts = new ArrayList<>();
            accounts.add(mChatContactInfo.getAccount());
            int result = ImMgr.getInstance().probeUserStatus(accounts);
            if (0 != result)
            {
                mView.toast(R.string.detect_user_status_failed);
            }
        }
        else
        {
            mView.updatePersonalStatus(mChatContactInfo.getStatus());
        }
    }

    @Override
    public boolean isIsGroup()
    {
        return mIsGroup;
    }

    @Override
    public void loadMoreHistoryMessage()
    {
        long msgId = getFirstMessageId();
        if (0 == msgId)
        {
            isFirstQuery = true;
        }
        else
        {
            isFirstQuery = false;
        }
        int requestType = getMsgType();
        mChatMsgList = ImMgr.getInstance().queryHistoryMsg(isFirstQuery, msgId, requestType, mChatId, 10);
        if (null == mChatMsgList || 0 == mChatMsgList.size())
        {
            mView.toast(R.string.no_more_data);
            return;
        }
        handleInstantMessage(mChatMsgList, false);
    }

    @Override
    public void loadHistoryMessage()
    {
        int requestType = getMsgType();
//        ImMgr.getInstance().queryHistoryMessage(requestType, mChatId, "", 10);
    }

    @Override
    public void loadUnReadMessage() {
        loadCurrentChat();

        if (mRecentChatInfo == null)
        {
            return;
        }

        if (mRecentChatInfo.getUnReadMsgCount() > 0)
        {
            mChatMsgList = mRecentChatInfo.getUnReadMsgList();
            handleInstantMessage(mChatMsgList, true);
            return;
        }

        if (null != mRecentChatInfo.getChatMsgList() && !mRecentChatInfo.getChatMsgList().isEmpty())
        {
            mChatMsgList = mRecentChatInfo.getChatMsgList();
            handleInstantMessage(mChatMsgList, false);
        }
    }

    @Override
    public void loadCurrentChat() {
        ImMgr.getInstance().setCurrentChatId(mChatId);
        mRecentChatInfo = ImMgr.getInstance().getCurrentRecentChatByChatId(mChatId);
        ImMgr.getInstance().setCurrentRecentChat(mRecentChatInfo);
    }

    private long getFirstMessageId()
    {
        long msgId = 0;
        if (mList == null || mList.isEmpty())
        {
            return 0;
        }
        for (MessageItemType message : mList)
        {
            msgId = message.chatMsgInfo.getServerMsgId();
            return msgId;
        }
        return msgId;
    }

    private int getMsgType()
    {
        int requestMsgType = mChatType.getIndex();
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
    public int setInputStatus(boolean isInputting) {
        if (mIsGroup)
        {
            return -1;
        }
        int result = ImMgr.getInstance().setInputStatus(isInputting, mChatId);
        if (0 != result)
        {
            mView.toast(R.string.send_message_failed);
        }
        return result;
    }

    @Override
    public ImChatMsgInfo sendMessage(String trim)
    {
        this.mContent = trim;
        int result = 0;
        // 当前im发送消息类型全部设置为纯文本
        if (!TextUtils.isEmpty(trim))
        {
            result = ImMgr.getInstance().sendMessage(mChatType, ImConstant.ChatMsgMediaType.CHAT_MSG_MEDIA_TYPE_TEXT, trim, mChatId);
        }
        if (0 != result)
        {
            mView.toast(R.string.send_message_failed);
        }
        else
        {
            mSendMsgInfo = new ImChatMsgInfo();
            mSendMsgInfo.setToId(mChatId);
            mSendMsgInfo.setToName(mName);
            mSendMsgInfo.setFromId(mMyAccount);
            mSendMsgInfo.setContent(trim);
            mSendMsgInfo.setMsgType(mChatType);
        }
        return mSendMsgInfo;
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
    public void refreshViewAfterSendMessage(ImChatMsgInfo imChatMsgInfo) {
//        if (instantMessage.getMediaRes() == null)
//        {
            //IM
            MessageItemType item = new MessageItemType();
            item.chatMsgInfo = imChatMsgInfo;
            item.content = parseSpan(imChatMsgInfo.getContent());
            addItem(item);
//        }
//        else
//        {
//            //UM
//            MessageItemType item = new MessageItemType();
//            item.instantMsg = instantMessage;
//            item.content = instantMessage.getContent();
//            addItem(item);
//        }

        ImMgr.getInstance().addToRecentChatsList(imChatMsgInfo, true);
        mView.refreshRecentChatList(mList);
    }

    @Override
    public void onReceive(String broadcastName, Object obj)
    {
        switch (broadcastName)
        {
            case CustomBroadcastConstants.ACTION_IM_QUERY_HISTORY:
//                List<InstantMessage> list = (List<InstantMessage>) obj;
//                handleInstantMessage(list);
                break;

            // 新消息通知
            case CustomBroadcastConstants.ACTION_RECEIVE_SESSION_CHANGE:
                ImChatMsgInfo newMsg = (ImChatMsgInfo) obj;
                if (ImConstant.ChatMsgType.SINGLE_CHAT == newMsg.getMsgType())
                {
                    if (!mChatId.equals(newMsg.getFromId()))
                    {
                        return;
                    }
                }
                else
                {
                    if (!mChatId.equals(newMsg.getToId()))
                    {
                        return;
                    }
                }

                if (isFirstReceive)
                {
                    loadCurrentChat();
                    isFirstQuery = false;
                }

                if (null != mChatMsgList)
                {
                    mChatMsgList.clear();
                    mChatMsgList.add(newMsg);
                    handleInstantMessage(mChatMsgList, true);
                }
                break;

            case CustomBroadcastConstants.ACTION_IM_FRIEND_STATUS_CHANGE:
                List<String> stateChangedMembers = (List<String>) obj;
                String changedMember = stateChangedMembers.get(0);
//                mFriends = ImMgr.getInstance().getFriends();
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

            // 好友状态推送通知
            case CustomBroadcastConstants.ACTION_IM_USER_STATUS_CHANGE:
                if (mIsGroup)
                {
                    return;
                }
                mChatContactInfo.setStatus(ImMgr.getInstance().updateUserStatus(mChatContactInfo.getAccount()));
                mView.updatePersonalStatus(mChatContactInfo.getStatus());
                break;

            // 消息发送成功通知
            case CustomBroadcastConstants.ACTION_SEND_MESSAGE_SUCCESS:
                ImChatMsgInfo msgInfo = (ImChatMsgInfo) obj;
                if (mMyAccount.equals(msgInfo.getFromId()))
                {
                    mSendMsgInfo.setFromName(msgInfo.getFromName());
                    mSendMsgInfo.setServerMsgId(msgInfo.getServerMsgId());
                    mSendMsgInfo.setUtcStamp(msgInfo.getUtcStamp());
                }
                mView.refreshRecentChatList(mList);
                break;

            // 用户输入状态上报
            case CustomBroadcastConstants.ACTION_IM_CHAT_INPUTTING_STATUS_IND:
                boolean isInputting = (boolean) obj;
                mView.showInputtingStatus(isInputting);
                break;

            // 撤回消息失败
            case CustomBroadcastConstants.ACTION_IM_CHAT_WITHDRAW_MSG_FAILED:
                mView.toast(R.string.withdraw_message_failed);
                break;

            // 撤回消息通知
            case CustomBroadcastConstants.ACTION_IM_CHAT_WITHDRAW_MSG_IND:
                String origin = (String) obj;
                mView.showWithdrawResult(origin);

                loadCurrentChat();
                mList.clear();
                handleInstantMessage(mRecentChatInfo.getChatMsgList(), false);
                break;
            default:
                break;
        }
    }

    private void friendStateChanged(String account)
    {
//        for (PersonalContact contact : mFriends)
//        {
//            if (account.equals(contact.getEspaceNumber()))
//            {
//                mView.updatePersonalStatus(contact);
//            }
//        }
    }

    private void strangerStateChanged(String account)
    {
        //chat with stranger, get it status
//        mStrangers = ImMgr.getInstance().getStrangers();
//        for (PersonalContact contact : mStrangers)
//        {
//            if (account.equals(contact.getEspaceNumber()))
//            {
//                mView.updatePersonalStatus(contact);
//            }
//        }
    }

    private boolean isFriendStateChanged(String account)
    {
//        for (PersonalContact contact : mFriends)
//        {
//            if (account.equals(contact.getEspaceNumber()))
//            {
//                return true;
//            }
//        }
        return false;
    }

//    /**
//     * Add recent contact message.
//     * @param chatId the chat id
//     * @param message the message
//     */
//    private void addRecentContactMessage(String chatId, InstantMessage message)
//    {
//        RecentChatContact chatContact = getRecentChatContact(chatId, message.getMsgType(), message.getNickname());
//        chatContact.setEndTime(message.getTime());
//        ImMgr.getInstance().insertRecentSession(chatContact);
//    }

//    private RecentChatContact getRecentChatContact(String chatId, int msgType, String nickName)
//    {
//        RecentChatContact chatContact = RecentChatContactDao.getContact(chatId, msgType);
//
//        if (chatContact == null)
//        {
//            chatContact = new RecentChatContact(chatId, msgType, nickName);
//        }
//        else if (TextUtils.isEmpty(chatContact.getNickname()))
//        {
//            chatContact.setNickname(nickName);
//        }
//
//        return chatContact;
//    }

    private void handleInstantMessage(List<ImChatMsgInfo> chatMsgInfoList, boolean isUnRead)
    {
        for (ImChatMsgInfo imChatMsgInfo : chatMsgInfoList)
        {
            MessageItemType item = new MessageItemType();
            item.chatMsgInfo = imChatMsgInfo;
            String msgContent = imChatMsgInfo.getContent();
            item.content = mEmotionParser.parseSpan(msgContent);
            mList.add(item);
        }
        Collections.sort(mList, new ChatComparator());
        if (isUnRead)
        {
            markReadMessage(chatMsgInfoList);
        }
        mView.refreshRecentChatList(mList);
    }

    private void markReadMessage(List<ImChatMsgInfo> chatMsgInfoList)
    {
        int result = ImMgr.getInstance().setMsgRead(chatMsgInfoList, mChatType, mChatId);
        if (0 != result)
        {
            mView.toast(R.string.set_read_failed);
        }
    }

    public List<MessageItemType> getMessages()
    {
        return mList;
    }

    public void startRecord()
    {
//        ImMgr.getInstance().startRecord();
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

//    @Override
//    public InstantMessage stopRecord()
//    {
//        ImMgr.getInstance().stopRecord();
//        String recordPath = ImMgr.getInstance().getRecordPath();
//        stopRecordTimeChange();
//        int time = timeCount / (1000 / VOICE_RECORD_TIME_RATE);
//        //record time length less than One Seconds
//        if (time < 1)
//        {
//            mView.toast(R.string.record_failed);
//            delRecordFile(recordPath);
//        }
//        InstantMessage instantMessage = ImMgr.getInstance().sendMessage(mChatId, mIsGroup, recordPath, MediaResource.MEDIA_AUDIO, time);
//        if (instantMessage != null)
//        {
//            refreshViewAfterSendMessage(instantMessage);
//            mView.refreshRecentChatList(mList);
//        }
//        return instantMessage;
//    }

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
//                InstantMessage sendMessage = ImMgr.getInstance().sendMessage(mChatId, mIsGroup, photoUri.getPath(), MediaResource.MEDIA_PICTURE);
//                if (sendMessage != null)
//                {
//                    refreshViewAfterSendMessage(sendMessage);
//                }
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
//                InstantMessage sendMessage = ImMgr.getInstance().sendMessage(mChatId, mIsGroup, videoPath, MediaResource.MEDIA_VIDEO);
//                if (sendMessage != null)
//                {
//                    ChatTools.copyVideoFile(sendMessage);
//                    refreshViewAfterSendMessage(sendMessage);
//                }
            }
        }
    }

    @Override
    public void makeCall()
    {
//        if (mChatType == RecentChatContact.ESPACECHATTER)
//        {
//            CallMgr.getInstance().startCall(mChatId, false);
//        }
    }

    @Override
    public void gotoDetailActivity()
    {
//        if (mChatType == RecentChatContact.ESPACECHATTER && personalContact != null)
        {
            Intent intent = new Intent(IntentConstant.SINGLE_CHAT_SETTING_ACTIVITY_ACTION);
            Bundle bundle = new Bundle();
//            bundle.putSerializable(UIConstants.PERSONAL_CONTACT, personalContact);
            intent.putExtra(UIConstants.BUNDLE_KEY, bundle);
            ActivityUtil.startActivity(mContext, intent);
        }
//        else if (constGroup != null)
        {
            // TODO: 2017/9/28
            Intent intent = new Intent(IntentConstant.GROUP_DETAIL_SETTING_ACTIVITY_ACTION);
            Bundle bundle = new Bundle();
//            bundle.putSerializable(UIConstants.CONST_GROUP, constGroup);
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
//            ImMgr.getInstance().subscribeState(subscribeList);
//            ImMgr.getInstance().searchFuzzyContact(mChatId);
        }
    }

    @Override
    public void delHistoryMessage(int position) {
        itemType = getItem(position);
        int result = ImMgr.getInstance().deleteHistoryMsg(mChatId, itemType.chatMsgInfo.getServerMsgId(), false);
        if (0 != result)
        {
            mView.toast(R.string.delete_message_failed);
            return;
        }
        mList.remove(itemType);
        mView.refreshRecentChatList(mList);
    }

    @Override
    public void withdrawMessage(int position) {
        itemType = getItem(position);
        int result = ImMgr.getInstance().withdrawMsg(itemType.chatMsgInfo);
        if (0 != result)
        {
            mView.toast(R.string.withdraw_message_failed);
            return;
        }
    }

    private MessageItemType getItem(int position)
    {
        return mList.get(position);
    }

    private void delRecordFile(String recordPath)
    {
        // delete former record audio file
//        File file = FileUtil.newFile(recordPath);
//        if (file.exists())
//        {
//            if (!file.delete())
//            {
//                LogUtil.e(UIConstants.DEMO_TAG, "delete file error");
//            }
//        }
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
