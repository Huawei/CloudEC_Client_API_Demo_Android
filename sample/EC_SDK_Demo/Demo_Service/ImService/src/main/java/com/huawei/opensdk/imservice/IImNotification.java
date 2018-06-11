package com.huawei.opensdk.imservice;

import com.huawei.contacts.PersonalContact;
import com.huawei.data.AddFriendResp;
import com.huawei.data.RequestJoinInGroupNotifyData;
import com.huawei.data.base.BaseResponseData;
import com.huawei.data.entity.InstantMessage;
import com.huawei.opensdk.imservice.data.UmTransProgressData;

import java.util.List;

/**
 * This interface is about Im function service event notification.
 * Im业务事件通知接口
 */
public interface IImNotification
{
    void onImEventNotify(int code, Object params);

    /**
     * This method is used to notify login success.
     * 登陆成功响应
     */
    void onImLoginSuccess();

    /**
     * This method is used to notify login error.
     * 登陆失败响应
     */
    void onImLoginError();

    /**
     * This method is used to notify be kicked off.
     * 被抢登陆通知
     */
    void onImKickOffNotify();

    /**
     * This method is used to notify multi terminal login.
     * 多终端登陆通知
     */
    void onImMultiTerminalNotify();

    /**
     * This method is used to notify search contact result.
     * 查询联系人结果
     * @param searchContactResult Indicates search contacts list
     *                            查询到的联系人列表
     */
    void onSearchContactResult(List<PersonalContact> searchContactResult);

    /**
     * This method is used to notify set signature result.
     * 设置签名结果
     * @param result Indicates set result
     *               result取值：0：失败；1：成功
     */
    void onSetSignatureSuccess(int result);

    /**
     * This method is used to notify friend status change.
     * 订阅后好友状态变更结果
     * @param list Indicates changed friends account
     *             变更的好友账号
     */
    void onFriendStateChanged(List<String> list);

    /**
     * This method is used to notify friends or groups change.
     * 群组或者好友变更通知
     */
    void onRefreshFriendsOrGroups();

    /**
     * This method is used to notify error.
     * 一般的群组或者好友更新失败的响应
     * @param errorDescribe Indicates error describe
     *                      一般的错误描述
     */
    void onCommonErrorNotify(String errorDescribe);

    /**
     * This method is used to notify set state response.
     * 设置状态成功通知
     */
    void onSetStatusSuccess();

    /**
     * This method is used to notify query group member successful result.
     * 查询群成员成功通知
     */
    void onQueryGroupMemberSuccess();

    /**
     * This method is used to notify audio play ended
     * 停止播放音频通知
     */
    void onAudioPlayEnd();

    /**
     * This method is used to notify the result of message send successful.
     * 消息发送结果成功通知
     * @param instantMessage Indicates instant message
     *                       消息
     */
    void onSendMessagesSuccess(InstantMessage instantMessage);

    /**
     * This method is used to notify the result of message send failed.
     * 消息发送结果失败通知
     * @param instantMessage Indicates instant message
     *                       消息
     */
    void onSendMessagesFail(InstantMessage instantMessage);

    /**
     * This method is used to notify offline file transfer finish.
     * 富媒体传输完成通知
     */
    void notifyFinish();

    /**
     * This method is used to notify offline file download process.
     * 富媒体下载进度通知
     * @param processData Indicates um download progress details.
     *                    富媒体下载进度详情
     */
    void notifyDownloadProcess(UmTransProgressData processData);

    /**
     * This method is used to notify offline file upload process.
     * 富媒体上传进度通知
     * @param processData Indicates um upload progress details.
     *                    富媒体上传进度详情
     */
    void onDownloadFinished(UmTransProgressData processData);

    /**
     * This method is used to notify invite to join group.
     * 邀请加入群组推送通知
     * @param requestJoinInGroupNotifyData Indicates join result
     *                                     邀请加入的数据结构
     */
    void onInviteJoinGroupNotify(RequestJoinInGroupNotifyData requestJoinInGroupNotifyData);

    /**
     * This method is used to refresh recent session.
     * 刷新最近会话
     */
    void onRefreshRecentSession();

    /**
     * This method is used to notify receive message result.
     * 接收消息通知
     * @param list Indicates message list
     *             接收到的消息列表
     */
    void onReceiveMessages(List<InstantMessage> list);

    /**
     * This method is used to refresh unread message.
     * 刷新未读消息
     */
    void onRefreshUnreadMessage();

    /**
     * This method is used to notify query message history result.
     * 查询漫游消息记录响应
     * @param list Indicates query history message list
     *             漫游消息列表
     */
    void onQueryHistoryMessagesSuccess(List<InstantMessage> list);

    /**
     * This method is used to notify add friend response.
     * 添加好友结果响应
     * @param addFriendResp Indicates response data struct
     *                      添加结果的数据结构
     */
    void onAddContactResult(AddFriendResp addFriendResp);

    /**
     * This method is used to notify delete friend response.
     * 删除好友结果响应
     * @param deleteFriendResp Indicates response data struct
     *                         删除结果的数据结构
     */
    void onDeleteContactResult(BaseResponseData deleteFriendResp);

    /**
     * This method is used to response invite to join group successful.
     * 邀请加入群组成功的响应
     */
    void onInviteJoinGroupSuccess();

    /**
     * This method is used to response invite to join group failed.
     * 邀请加入群组失败的响应
     */
    void onInviteJoinGroupFail();

    /**
     * This method is used to notify member leave group result successful.
     * 群成员离开群组成功的响应
     */
    void onLeaveGroupSuccess();

    /**
     * This method is used to notify member leave group result failed.
     * 群成员离开群组失败的响应
     */
    void onLeaveGroupFail();

    /**
     * This method is used to notify group info update result successful.
     * 群组信息更新成功的通知
     * @param groupId Indicates group id
     *                群组id
     */
    void onModifyGroupSuccess(String groupId);
}
