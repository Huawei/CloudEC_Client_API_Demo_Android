package com.huawei.opensdk.imservice;

import com.huawei.ecterminalsdk.base.TsdkContactAndChatGroupsInfo;

import java.util.List;

/**
 * This interface is about Im function init interface.
 * Im功能接口
 */
public interface IImMgr
{
    /**
     * This method is used to get user information.
     * 获取指定用户信息
     * @param account  Indicates account
     *                 账号
     * @return ImContactInfo  Return the obtained user information object
     *                        返回获取到得用户信息对象
     */
    ImContactInfo getUserInfo(String account);

    /**
     * This method is used to set user information.
     * 设置指定用户信息
     * @param contactInfo Indicates user information
     *                    用户信息
     * @return int Return execute result
     *             返回执行结果
     */
    int setUserInfo(ImContactInfo contactInfo);

    /**
     * This method is used to set user states.
     * 设置用户自己的在线状态
     * @param status        Indicates user status
     *                      用户状态
     * @param statusDesc    Indicates status description
     *                      状态描述
     * @return int Return execute result
     *             返回执行结果
     */
    int setUserStatus(ImConstant.ImStatus status, String statusDesc);

    int probeUserStatus(List<String> accounts);

    /**
     * This method is used to get contact group and chat group lists.
     * 获取联系人分组和聊天群组列表
     * @param isSync         Indicates whether to synchronize in full
     *                       是否全量同步
     * @param timestamp      Indicates time stamp of incremental synchronization (year month day hour minute second)
     *                       增量同步时间戳，时间格式：19000000000000
     * @return TsdkContactAndChatGroupsInfo Return contact group and chat group lists object
     *                                      返回获取到得联系人和聊天列表对象
     */
    TsdkContactAndChatGroupsInfo getContactAndChatGroups(boolean isSync, String timestamp);

    /**
     * This method is used to get group objects by group id.
     * 通过分组id 获取分组对象
     * @param groupId   Indicates group id
     *                  群组id
     * @return ImContactGroupInfo Return group object.
     *                            返回分组对象
     */
    ImContactGroupInfo getContactGroupByGroupId(long groupId);

    /**
     * This method is used to get all the contacts of the user.
     * 获取用户的所有联系人(好友/自定义联系人)
     * @return List<ImContactInfo> Return contacts list
     *                             返回联系人列表
     */
    List<ImContactInfo> getAllContactList();

    List<ImContactGroupInfo> getAllContactGroupList();

    List<ImChatGroupInfo> getAllChatGroupList();

    long addFriend(String account, long groupId);

    int delFriend(long contactId);

    long addContactGroup(String groupName);

    int delContactGroup(long groupId);

    int modifyContactGroup(long groupId, String groupName);

    int updateGroupOrder(List<ImContactGroupInfo> contactGroupInfoList, int groupIndex1, int groupIndex2);

    int opGroupContact(long newGroupId, long oldGroupId, long contactId, int opType);

    String addChatGroup(ImChatGroupInfo chatGroupInfo);

    int delChatGroup(String groupId, int type);

    int modifyChatGroupInfo(ImChatGroupInfo imChatGroupInfo, int type);

    List<ImChatGroupInfo> queryChatGroup(String keyword);

    ImChatGroupInfo getChatGroupInfo(String groupId);

    List<ImContactInfo> getChatGroupMembers(String timestamp, boolean isSyncAll);

    int addChatGroupMember(boolean isInvite, String inviteAccount, String joiningAccount);

    int delChatGroupMember(String memberAccount);

    int leaveChatGroup();

    int setInputStatus(boolean isInputting, String chatId);

    int sendMessage(ImConstant.ChatMsgType magType, ImConstant.ChatMsgMediaType msgMediaType, String content, String target);

    int withdrawMsg(ImChatMsgInfo chatMsgInfo);

    int setMsgRead(List<ImChatMsgInfo> unReadMsgList, ImConstant.ChatMsgType chatMsgType, String chatId);

    List<ImChatMsgInfo> queryHistoryMsg(boolean isFirst, long msgId, int msgType, String origin, int count);

    int deleteHistoryMsg(String origin, long msgId, boolean isDelAll);
}
