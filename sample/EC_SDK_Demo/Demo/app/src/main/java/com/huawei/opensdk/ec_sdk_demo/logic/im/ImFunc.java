package com.huawei.opensdk.ec_sdk_demo.logic.im;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.huawei.opensdk.commonservice.common.LocContext;
import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.imservice.IImNotification;
import com.huawei.opensdk.imservice.ImChatGroupInfo;
import com.huawei.opensdk.imservice.ImChatMsgInfo;
import com.huawei.opensdk.imservice.ImConstant;
import com.huawei.opensdk.imservice.ImContactInfo;
import com.huawei.opensdk.imservice.ImRecentChatInfo;
import com.huawei.opensdk.imservice.data.UmTransProgressData;

import java.util.List;

public class ImFunc implements IImNotification
{
    private static final ImFunc mInstance = new ImFunc();

    private static final int IM_EVENT_LOGIN_SUCCESS = 100;
    private static final int IM_EVENT_LOGIN_ERROR = 101;
    private static final int SHOW_TOAST = 102;

    private ImLoginStatus mLoginStatus = ImLoginStatus.LOGIN_ERROR;

    enum ImLoginStatus
    {
        LOGIN_SUCCESS, LOGIN_ERROR
    }

    private Handler mHandler = new Handler(Looper.getMainLooper())
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case IM_EVENT_LOGIN_SUCCESS:
                    mLoginStatus = ImLoginStatus.LOGIN_SUCCESS;
                    Toast.makeText(LocContext.getContext(), LocContext.getContext().getString(R.string.im_login_success),
                            Toast.LENGTH_LONG).show();
                    LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_IM_LOGIN_SUCCESS, null);
                    break;
                case IM_EVENT_LOGIN_ERROR:
                    mLoginStatus = ImLoginStatus.LOGIN_ERROR;
                    Toast.makeText(LocContext.getContext(), LocContext.getContext().getString(R.string.im_login_error),
                            Toast.LENGTH_LONG).show();
                    LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_IM_LOGIN_ERROR, null);
                    break;
                case SHOW_TOAST:
                    Toast.makeText(LocContext.getContext(), LocContext.getContext().getString(msg.arg1),
                            Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }

        }
    };

    private ImFunc()
    {
    }

    public static ImFunc getInstance()
    {
        return mInstance;
    }

    public ImLoginStatus getLoginStatus()
    {
        return mLoginStatus;
    }

    @Override
    public void onUserStatusUpdate() {
        LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_IM_USER_STATUS_CHANGE, null);
    }

    @Override
    public void onUserInfoUpdate(List<ImContactInfo> imContactInfoList) {
        if (null == imContactInfoList)
        {
            LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_IM_USER_INFO_CHANGE_FAILED, -1);
        }
        else
        {
            LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_IM_USER_INFO_CHANGE, imContactInfoList);
        }
    }

    @Override
    public void onJoinChatGroupInd(Object object) {
        LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_IM_CHAT_GROUP_ADD, object);
    }

    @Override
    public void onLeaveChatGroupResult(int result) {
        LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_IM_CHAT_GROUP_LEAVE_RESULT, result);
    }

    @Override
    public void onChatGroupInfoUpdate(ImChatGroupInfo chatGroupInfo, Object object) {
        switch ((int) object)
        {
            case ImConstant.ChatGroupUpdateType.CHAT_GROUP_INFO_UPDATE:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_IM_CHAT_GROUP_UPDATE, chatGroupInfo);
                break;
            case ImConstant.ChatGroupUpdateType.CHAT_GROUP_ADD_MEMBER:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_IM_CHAT_GROUP_ADD_MEMBER, object);
                break;
            case ImConstant.ChatGroupUpdateType.CHAT_GROUP_DEL_MEMBER:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_IM_CHAT_GROUP_DEL_MEMBER, object);
                break;
            case ImConstant.ChatGroupUpdateType.CHAT_GROUP_DISMISS:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_IM_CHAT_GROUP_DISMISS, object);
                break;
                default:
                    break;
        }

    }

    @Override
    public void onInputtingStatusInd(boolean isInputting) {
        LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_IM_CHAT_INPUTTING_STATUS_IND, isInputting);
    }

    @Override
    public void onWithdrawMessagesFail() {
        LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_IM_CHAT_WITHDRAW_MSG_FAILED, null);
    }

    @Override
    public void onWithdrawMessagesInd(String origin) {
        LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_IM_CHAT_WITHDRAW_MSG_IND, origin);
    }

    @Override
    public void onImEventNotify(int code, Object params)
    {

    }

    @Override
    public void onImLoginSuccess()
    {
        mHandler.sendEmptyMessage(IM_EVENT_LOGIN_SUCCESS);
    }

    @Override
    public void onImLoginError()
    {

    }

    @Override
    public void onImKickOffNotify()
    {

    }

    @Override
    public void onImMultiTerminalNotify()
    {

    }

//    @Override
//    public void onSearchContactResult(List<PersonalContact> searchContactResult)
//    {
//        LogUtil.i(UIConstants.DEMO_TAG, "onSearchContactResult: " + searchContactResult.size());
//        LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_IM_SEARCH_CONTACT_RESULT, searchContactResult);
//    }

    @Override
    public void onSetSignatureSuccess(int result)
    {
        //result取值：0：失败；1：成功
    }

    @Override
    public void onFriendStateChanged(List<String> list)
    {
        LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_IM_FRIEND_STATUS_CHANGE, list);
    }

    @Override
    public void onRefreshFriendsOrGroups()
    {
        LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_REFRESH_GROUP_MEMBER, null);
    }

    @Override
    public void onCommonErrorNotify(String errorDescribe)
    {

    }

    @Override
    public void onSetStatusSuccess()
    {
        LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_SET_STATUS, null);
    }

    @Override
    public void onQueryGroupMemberSuccess()
    {
        LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_QUERY_GROUP_MEMBER, null);
    }

    @Override
    public void onAudioPlayEnd()
    {

    }

    @Override
    public void onSendMessagesSuccess(ImChatMsgInfo imChatMsgInfo)
    {
        LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_SEND_MESSAGE_SUCCESS, imChatMsgInfo);
    }

//    @Override
//    public void onSendMessagesFail(InstantMessage instantMessage)
//    {
//        LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_SEND_MESSAGE_FAIL, null);
//    }

    @Override
    public void notifyFinish()
    {
        LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_DOWNLOAD_FINISH, null);
    }

    @Override
    public void notifyDownloadProcess(UmTransProgressData processData)
    {
        LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_DOWNLOAD_PROCESS, processData);
    }

    @Override
    public void onDownloadFinished(UmTransProgressData processData)
    {

    }

//    @Override
//    public void onInviteJoinGroupNotify(RequestJoinInGroupNotifyData requestJoinInGroupNotifyData)
//    {
//
//    }

    @Override
    public void onRefreshRecentSession()
    {

    }

    @Override
    public void onReceiveMessage(ImChatMsgInfo newMsg)
    {
        LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_RECEIVE_SESSION_CHANGE, newMsg);
    }

    @Override
    public void onRefreshUnreadMessage(List<ImChatMsgInfo> msgInfoList)
    {
        LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_RECEIVE_BATCH_MESSAGES, msgInfoList);
    }

//    @Override
//    public void onQueryHistoryMessagesSuccess(List<InstantMessage> list)
//    {
//        LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_IM_QUERY_HISTORY, list);
//    }

//    @Override
//    public void onAddContactResult(AddFriendResp addFriendResp)
//    {
//        LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_IM_ADD_CONTACT_RESULT, addFriendResp);
//    }

//    @Override
//    public void onDeleteContactResult(BaseResponseData deleteFriendResp)
//    {
//        LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_IM_DELETE_CONTACT_RESULT, deleteFriendResp);
//    }

    @Override
    public void onInviteJoinGroupSuccess()
    {
        sendShowToastMessage(R.string.group_add_success);
        LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_REFRESH_GROUP_MEMBER, null);
    }

    @Override
    public void onInviteJoinGroupFail()
    {
        sendShowToastMessage(R.string.group_add_fail);
    }

    @Override
    public void onLeaveGroupSuccess()
    {
        sendShowToastMessage(R.string.group_leave_success);
        LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_REFRESH_GROUP_MEMBER, null);
    }

    @Override
    public void onLeaveGroupFail()
    {
        sendShowToastMessage(R.string.group_leaved_fail);
    }

    @Override
    public void onModifyGroupSuccess(String groupId)
    {
//        sendShowToastMessage(R.string.modify_success);
        LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_MODIFY_GROUP_MEMBER, groupId);
    }

    private void sendShowToastMessage(int resId)
    {
        Message msg = Message.obtain();
        msg.what = SHOW_TOAST;
        msg.arg1 = resId;
        mHandler.sendMessage(msg);
    }
}
