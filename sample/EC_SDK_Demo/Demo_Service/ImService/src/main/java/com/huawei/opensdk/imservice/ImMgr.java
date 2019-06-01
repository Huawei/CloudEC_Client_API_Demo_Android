package com.huawei.opensdk.imservice;

import android.util.Log;

import com.huawei.ecterminalsdk.base.TsdkAddContactGroupReqParam;
import com.huawei.ecterminalsdk.base.TsdkBatchChatMsgInfo;
import com.huawei.ecterminalsdk.base.TsdkBeAddedToChatGroupInfo;
import com.huawei.ecterminalsdk.base.TsdkChatGroupInfo;
import com.huawei.ecterminalsdk.base.TsdkChatGroupInfoUpdateType;
import com.huawei.ecterminalsdk.base.TsdkChatGroupMemberGetResult;
import com.huawei.ecterminalsdk.base.TsdkChatGroupModifyOpType;
import com.huawei.ecterminalsdk.base.TsdkChatGroupQueryParam;
import com.huawei.ecterminalsdk.base.TsdkChatGroupQueryResult;
import com.huawei.ecterminalsdk.base.TsdkChatGroupQueryType;
import com.huawei.ecterminalsdk.base.TsdkChatGroupType;
import com.huawei.ecterminalsdk.base.TsdkChatGroupUpdateInfo;
import com.huawei.ecterminalsdk.base.TsdkChatMsgInfo;
import com.huawei.ecterminalsdk.base.TsdkChatMsgMediaType;
import com.huawei.ecterminalsdk.base.TsdkChatMsgType;
import com.huawei.ecterminalsdk.base.TsdkChatMsgWithdrawInfo;
import com.huawei.ecterminalsdk.base.TsdkChatMsgWithdrawResult;
import com.huawei.ecterminalsdk.base.TsdkContactAndChatGroupsInfo;
import com.huawei.ecterminalsdk.base.TsdkContactGroupBaseInfo;
import com.huawei.ecterminalsdk.base.TsdkContactMoveGroupOpType;
import com.huawei.ecterminalsdk.base.TsdkDelHistoryChatMsgOpType;
import com.huawei.ecterminalsdk.base.TsdkGetContactAndChatGroupsReqParam;
import com.huawei.ecterminalsdk.base.TsdkHistoryChatMsgQueryType;
import com.huawei.ecterminalsdk.base.TsdkHistoryChatMsgType;
import com.huawei.ecterminalsdk.base.TsdkImUserBaseInfo;
import com.huawei.ecterminalsdk.base.TsdkImUserInfo;
import com.huawei.ecterminalsdk.base.TsdkImUserStatus;
import com.huawei.ecterminalsdk.base.TsdkImUserStatusInfo;
import com.huawei.ecterminalsdk.base.TsdkImUserStatusUpdateInfo;
import com.huawei.ecterminalsdk.base.TsdkInputtingStatusInfo;
import com.huawei.ecterminalsdk.base.TsdkLeaveChatGroupResult;
import com.huawei.ecterminalsdk.base.TsdkMsgBaseInfo;
import com.huawei.ecterminalsdk.base.TsdkQueryHistoryMsgParam;
import com.huawei.ecterminalsdk.base.TsdkQueryHistoryMsgResult;
import com.huawei.ecterminalsdk.base.TsdkRspJoinChatGroupMsg;
import com.huawei.ecterminalsdk.base.TsdkSelfDefContactInfo;
import com.huawei.ecterminalsdk.base.TsdkSendChatMsgResult;
import com.huawei.ecterminalsdk.base.TsdkSetMsgReadInfo;
import com.huawei.ecterminalsdk.models.im.TsdkChatConversation;
import com.huawei.ecterminalsdk.models.im.TsdkChatGroup;
import com.huawei.ecterminalsdk.models.im.TsdkContactGroup;
import com.huawei.ecterminalsdk.models.im.TsdkImManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This class is about instant messaging function management class.
 * Im模块管理类
 */
public class ImMgr implements IImMgr
{
    private static final String TAG = ImMgr.class.getSimpleName();

    /**
     * The user info.
     * 用户自己的信息
     */
//    private PersonalContact mSelfContact;

    /**
     * Instance object of im component.
     * 获取一个ImMgr对象
     */
    private static final ImMgr mInstance = new ImMgr();

    /**
     * Self account info.
     * 用户自己的账号信息
     */
//    private ImAccountInfo imAccountInfo;

    /**
     * Initial state of the user:away state.
     * 离线AWAY = 0，在线ON_LINE = 1，繁忙BUSY = 3，离开XA = 4，请勿打扰DND = 5
     */
    private ImConstant.ImStatus mImLoginStatus = ImConstant.ImStatus.AWAY;

    /**
     * The TsdkImManager function object.
     * TsdkImManager对象
     */
    private TsdkImManager tsdkImManager;

    private TsdkImUserInfo imSelfInfo;

    private TsdkContactGroup tsdkContactGroup;

    /**
     * 当前聊天群组操作对象
     */
    private TsdkChatGroup currentChatGroup;

    private TsdkChatGroupInfo tsdkChatGroupInfo;

    private TsdkChatConversation tsdkChatConversation;

    /**
     * ImChatGroupInfo对象
     */
    private ImChatGroupInfo chatGroupInfo;

    /**
     *  UI notification.
     *  回调接口对象
     */
    private IImNotification mNotification;

    /**
     * The user account.
     * 账号
     */
    private String mMyAccount;

    private int teamIndex;

    /**
     * A map collection of message ID and message content.
     * 已读消息，消息id和消息体的map集合
     */
//    private Map<String, InstantMessage> mUnMarkReadMessageMap = new ConcurrentHashMap<>();

    /**
     * Record saved path.
     * 录音的保存路径
     */
    private String mRecordPath;

    /**
     * Contact list (friend + defined contact).
     * 联系人列表(好友 + 自定义联系人)
     */
    private List<ImContactInfo> imContactInfoList = new ArrayList<>();

    private List<ImContactGroupInfo> imContactGroupInfoList = new ArrayList<>();

    /**
     * Chat group information list.
     * 聊天群组信息列表
     */
    private List<ImChatGroupInfo> imChatGroupInfoList = new ArrayList<>();

    private Map<String, Integer> statusMap = new HashMap<>();

    private List<ImRecentChatInfo> recentChatList = new ArrayList<>();

    private ImRecentChatInfo currentRecentChat = new ImRecentChatInfo();

    private List<Long> msgIdList = new ArrayList<>();

    private String currentChatId; // 当前聊天界面：p2p为对方账户；群聊为groupId

    /**
     * This is a constructor of ImMgr class.
     * 构造方法
     */
    private ImMgr()
    {
        tsdkImManager = TsdkImManager.getObject();
        tsdkContactGroup = new TsdkContactGroup();
        currentChatGroup = new TsdkChatGroup();
        tsdkChatGroupInfo = new TsdkChatGroupInfo();
        chatGroupInfo = new ImChatGroupInfo();
        teamIndex = 1;
    }

    /**
     * This method is used to get instance object of ImMgr.
     * 获取ImMgr对象实例
     * @return ImMgr Return instance object of ImMgr
     *               返回一个ImMgr对象实例
     */
    public static ImMgr getInstance()
    {
        return mInstance;
    }

    /**
     * This method is used to register im module UI callback.
     * 注册回调
     */
    public void regImServiceNotification(IImNotification notification)
    {
        mNotification = notification;
    }

    public ImConstant.ImStatus getStatus()
    {
        return mImLoginStatus;
    }

    public void setImLoginStatus(ImConstant.ImStatus status)
    {
        this.mImLoginStatus = status;
    }

    public void setStatusMap(Map<String, Integer> statusMap) {
        this.statusMap = statusMap;
    }

    public ImRecentChatInfo getCurrentRecentChat() {
        return currentRecentChat;
    }

    public void setCurrentRecentChat(ImRecentChatInfo currentRecentChat) {

        this.currentRecentChat = currentRecentChat;
    }

    public ImRecentChatInfo getCurrentRecentChatByChatId(String chatId)
    {
        if (null == recentChatList || recentChatList.size() == 0)
        {
            return null;
        }

        for (ImRecentChatInfo recentChatInfo : recentChatList)
        {
            if (recentChatInfo.getTag().equals(chatId))
            {
                return recentChatInfo;
            }
        }

        return null;
    }

    public void setCurrentChatId(String currentChatId) {
        this.currentChatId = currentChatId;
    }

    /**
     * This method is used to get user information.
     * 获取指定用户信息
     * @param account  Indicates account
     *                 账号
     * @return ImContactInfo  Return the obtained user information object
     *                        返回获取到得用户信息对象
     */
    @Override
    public ImContactInfo getUserInfo(String account) {
        Log.i(TAG,  "get user info");
        this.mMyAccount = account;
        this.imSelfInfo = tsdkImManager.getUserInfo(account);
        ImContactInfo imContactInfo = new ImContactInfo();
        imContactInfo.setContactId(imSelfInfo.getContactId());
        imContactInfo.setName(imSelfInfo.getName());
        imContactInfo.setSignature(imSelfInfo.getSignature());
        imContactInfo.setAccount(imSelfInfo.getStaffAccount());
        imContactInfo.setAddress(imSelfInfo.getAddress());
        imContactInfo.setDepartmentName(imSelfInfo.getDepartmentNameCn());
        imContactInfo.setEmail(imSelfInfo.getEmail());
        imContactInfo.setFax(imSelfInfo.getFax());
        imContactInfo.setMobile(imSelfInfo.getMobile());
        imContactInfo.setSoftNumber(imSelfInfo.getVoip());
        imContactInfo.setTitle(imSelfInfo.getTitle());
        imContactInfo.setZipCode(imSelfInfo.getZipCode());
        return imContactInfo;
    }

    /**
     * This method is used to set user information.
     * 设置指定用户信息
     * @param contactInfo Indicates user information
     *                    用户信息
     * @return int Return execute result
     *             返回执行结果
     */
    @Override
    public int setUserInfo(ImContactInfo contactInfo) {
        Log.i(TAG,  "set user info");

        TsdkImUserInfo userInfo = new TsdkImUserInfo();
        userInfo.setStaffAccount(contactInfo.getAccount());
        userInfo.setSignature(contactInfo.getSignature());
        int result = tsdkImManager.setUserInfo(userInfo);
        if (0 != result)
        {
            Log.e(TAG, "setUserInfo result ->" + result);
        }
        return result;
    }

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
    @Override
    public int setUserStatus(ImConstant.ImStatus status, String statusDesc) {
        TsdkImUserStatusInfo statusInfo = new TsdkImUserStatusInfo();
        TsdkImUserStatus userStatus = TsdkImUserStatus.TSDK_E_IM_USER_STATUS_INIT;
        switch (status)
        {
            case AWAY:
                userStatus = TsdkImUserStatus.TSDK_E_IM_USER_STATUS_OFFLINE;
                break;
            case ON_LINE:
                userStatus = TsdkImUserStatus.TSDK_E_IM_USER_STATUS_ONLINE;
                break;
            case BUSY:
                userStatus = TsdkImUserStatus.TSDK_E_IM_USER_STATUS_BUSY;
                break;
            case XA:
                userStatus = TsdkImUserStatus.TSDK_E_IM_USER_STATUS_LEAVE;
                break;
            case DND:
                userStatus = TsdkImUserStatus.TSDK_E_IM_USER_STATUS_DND;
                break;
                default:
                    break;
        }
        statusInfo.setStatus(userStatus);
        statusInfo.setStatusDesc(statusDesc);
        int result = tsdkImManager.setPersonalStatus(statusInfo);
        if (0 != result)
        {
            Log.e(TAG, "setUserStatus result ->" + result);
        }
        else
        {
            setImLoginStatus(status);
        }
        return result;
    }

    @Override
    public int probeUserStatus(List<String> accounts) {
        List<TsdkImUserBaseInfo> accountList = new ArrayList<>();
        for (String account : accounts)
        {
            if (account.equals(""))
            {
                continue;
            }
            TsdkImUserBaseInfo baseInfo = new TsdkImUserBaseInfo(account);
            accountList.add(baseInfo);
        }

        int result = tsdkImManager.detectUserStatus(accountList);
        if (0 != result)
        {
            Log.e(TAG, "probeUserStatus result ->" + result);
        }
        return result;
    }

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
    @Override
    public TsdkContactAndChatGroupsInfo getContactAndChatGroups(boolean isSync, String timestamp) {
        TsdkGetContactAndChatGroupsReqParam reqParam = new TsdkGetContactAndChatGroupsReqParam();
        reqParam.setIsSyncAll(isSync == true? 1 : 0);
        reqParam.setTimestamp(timestamp);
        TsdkContactAndChatGroupsInfo contactAndChatGroupsInfo = tsdkImManager.getContactAndChatGroups(reqParam);
        if (null == contactAndChatGroupsInfo)
        {
            return null;
        }
        return contactAndChatGroupsInfo;
    }

    /**
     * This method is used to get group objects by group id.
     * 通过分组id 获取分组对象
     * @param groupId   Indicates group id
     *                  分组id
     * @return ImContactGroupInfo Return group object.
     *                            返回分组对象
     */
    @Override
    public ImContactGroupInfo getContactGroupByGroupId(long groupId) {
        tsdkContactGroup = tsdkImManager.getContactGroupByGroupId(groupId);

        ImContactGroupInfo contactGroupInfo = new ImContactGroupInfo();
        contactGroupInfo.setGroupId(tsdkContactGroup.getGroupId());
        contactGroupInfo.setGroupName(tsdkContactGroup.getGroupName());

        List<ImContactInfo> contactInfoList = new ArrayList<>();
        if (null != tsdkContactGroup.getFriendList())
        {
            for (TsdkImUserInfo imUserInfo : tsdkContactGroup.getFriendList())
            {
                ImContactInfo imContactInfo = new ImContactInfo();
                imContactInfo.setContactType(1);
                imContactInfo.setStatus(updateUserStatus(imUserInfo.getStaffAccount()));
                imContactInfo.setContactId(imUserInfo.getContactId());
                imContactInfo.setName(imUserInfo.getName());
                imContactInfo.setSignature(imUserInfo.getSignature());
                imContactInfo.setAccount(imUserInfo.getStaffAccount());
                imContactInfo.setAddress(imUserInfo.getAddress());
                imContactInfo.setDepartmentName(imUserInfo.getDepartmentNameCn());
                imContactInfo.setEmail(imUserInfo.getEmail());
                imContactInfo.setFax(imUserInfo.getFax());
                imContactInfo.setMobile(imUserInfo.getMobile());
                imContactInfo.setSoftNumber(imUserInfo.getVoip());
                imContactInfo.setTitle(imUserInfo.getTitle());
                imContactInfo.setZipCode(imUserInfo.getZipCode());
                imContactInfo.setGroupId(groupId);
                contactInfoList.add(imContactInfo);
            }
        }
        if (null != tsdkContactGroup.getContactList())
        {
            for (TsdkSelfDefContactInfo selfDefContactInfo : tsdkContactGroup.getContactList())
            {
                ImContactInfo contactInfo = new ImContactInfo();
                contactInfo.setContactId(selfDefContactInfo.getContactId());
                contactInfo.setContactType(0);
                contactInfo.setName(selfDefContactInfo.getName());
                contactInfoList.add(contactInfo);
            }
        }
        contactGroupInfo.setList(contactInfoList);
        return contactGroupInfo;
    }

    /**
     * This method is used to get all the contacts of the user.
     * 获取用户的所有联系人(好友/自定义联系人)
     * @return List<ImContactInfo> Return contacts list
     *                             返回联系人列表
     */
    @Override
    public List<ImContactInfo> getAllContactList() {
        imContactInfoList.clear();
        if (null != tsdkImManager.getFriendList())
        {
            for (TsdkImUserInfo imUserInfo : tsdkImManager.getFriendList())
            {
                if (0 == imUserInfo.getContactId())
                {
                    continue;
                }
                ImContactInfo imContactInfo = new ImContactInfo();
                imContactInfo.setContactType(1);
                imContactInfo.setStatus(ImConstant.ImStatus.AWAY);
                imContactInfo.setContactId(imUserInfo.getContactId());
                imContactInfo.setName(imUserInfo.getName());
                imContactInfo.setSignature(imUserInfo.getSignature());
                imContactInfo.setAccount(imUserInfo.getStaffAccount());
                imContactInfo.setAddress(imUserInfo.getAddress());
                imContactInfo.setDepartmentName(imUserInfo.getDepartmentNameCn());
                imContactInfo.setEmail(imUserInfo.getEmail());
                imContactInfo.setFax(imUserInfo.getFax());
                imContactInfo.setMobile(imUserInfo.getMobile());
                imContactInfo.setSoftNumber(imUserInfo.getVoip());
                imContactInfo.setTitle(imUserInfo.getTitle());
                imContactInfo.setZipCode(imUserInfo.getZipCode());
            }
        }
        if (null != tsdkImManager.getContactList())
        {
            for (TsdkSelfDefContactInfo selfDefContactInfo : tsdkImManager.getContactList())
            {
                if (0 == selfDefContactInfo.getContactId())
                {
                    continue;
                }
                ImContactInfo contactInfo = new ImContactInfo();
                contactInfo.setContactType(0);
                contactInfo.setName(selfDefContactInfo.getName());
                imContactInfoList.add(contactInfo);
            }
        }
        return imContactInfoList;
    }

    /**
     * 获取所有联系人分组
     * @return
     */
    @Override
    public List<ImContactGroupInfo> getAllContactGroupList() {
        imContactGroupInfoList.clear();
        if (0 != tsdkImManager.getContactGroupList().size())
        {
            for (TsdkContactGroup contactGroup : tsdkImManager.getContactGroupList())
            {
                ImContactGroupInfo imContactGroupInfo = new ImContactGroupInfo();
                int memberNumber = 0;
                imContactGroupInfo.setGroupId(contactGroup.getGroupId());
                imContactGroupInfo.setGroupName(contactGroup.getGroupName());
                imContactGroupInfo.setGroupIndex(contactGroup.getIndex());
                if (null != contactGroup.getContactList())
                {
                    memberNumber = memberNumber + contactGroup.getContactList().size();
                }
                if (null != contactGroup.getFriendList())
                {
                    memberNumber = memberNumber + contactGroup.getFriendList().size();
                }
                imContactGroupInfo.setGroupMember(memberNumber);
                imContactGroupInfoList.add(imContactGroupInfo);
            }
        }

        Collections.sort(imContactGroupInfoList, new Comparator<ImContactGroupInfo>() {
            @Override
            public int compare(ImContactGroupInfo o1, ImContactGroupInfo o2) {
                int result = o1.getGroupIndex() - o2.getGroupIndex();
                return result;
            }
        });
        return imContactGroupInfoList;
    }

    /**
     * 获取聊天分组
     * @return
     */
    @Override
    public List<ImChatGroupInfo> getAllChatGroupList() {
        imChatGroupInfoList.clear();
        if (0 != tsdkImManager.getChatGroupList().size())
        {
            for (TsdkChatGroup chatGroup : tsdkImManager.getChatGroupList())
            {
                chatGroupInfo = new ImChatGroupInfo();
                chatGroupInfo.setGroupId(chatGroup.getChatGroupInfo().getGroupId());
                chatGroupInfo.setGroupType(chatGroup.getChatGroupInfo().getGroupType());
                chatGroupInfo.setGroupName(chatGroup.getChatGroupInfo().getGroupName());
                imChatGroupInfoList.add(chatGroupInfo);
            }
        }
        return imChatGroupInfoList;
    }

    @Override
    public long addFriend(String account, long groupId) {
        getContactGroupByGroupId(groupId);
        long contactId = tsdkContactGroup.addFriend("", account);
        if (-1 == contactId)
        {
            Log.e(TAG, "addFriend failed");
        }
        return contactId;
    }

    @Override
    public int delFriend(long contactId) {
        int result = tsdkContactGroup.delFriend(contactId);
        if (0 != result)
        {
            Log.e(TAG, "delFriend result ->" + result);
        }
        return result;
    }

    @Override
    public long addContactGroup(String groupName) {
        int index = teamIndex++;
        TsdkAddContactGroupReqParam addContactGroupReqParam = new TsdkAddContactGroupReqParam();
        addContactGroupReqParam.setGroupName(groupName);
        addContactGroupReqParam.setIndex(index);
        long groupId = tsdkImManager.addContactGroup(addContactGroupReqParam);
        if (groupId == -1)
        {
            Log.e(TAG, "addContactGroup failed");
        }
        return groupId;
    }

    @Override
    public int delContactGroup(long groupId) {
        int result = 0;
        // 首先获取改分组成员，如果有成员，删除分组前应该先删除组内成员
        imContactInfoList = getContactGroupByGroupId(groupId).getList();
        for (ImContactInfo contactInfo : imContactInfoList)
        {
            if (1 == contactInfo.getContactType())
            {
                tsdkContactGroup.delFriend(contactInfo.getContactId());
                if (0 != result)
                {
                    Log.e(TAG, "delFriend failed");
                }
            }
            else
            {
                tsdkContactGroup.delContact(contactInfo.getContactId());
                if (0 != result)
                {
                    Log.e(TAG, "delContact failed");
                }
            }
        }

        result = tsdkImManager.delContactGroup(groupId);
        if (0 != result)
        {
            Log.e(TAG, "delContactGroup result ->" + result);
        }
        return result;
    }

    @Override
    public int modifyContactGroup(long groupId, String groupName) {
        TsdkContactGroupBaseInfo contactGroupBaseInfo = new TsdkContactGroupBaseInfo();
        contactGroupBaseInfo.setGroupId(groupId);
        contactGroupBaseInfo.setGroupName(groupName);
        int result = tsdkImManager.modifyContactGroup(contactGroupBaseInfo);
        if (0 != result)
        {
            Log.e(TAG, "modifyContactGroup result ->" + result);
        }
        return result;
    }

    @Override
    public int updateGroupOrder(List<ImContactGroupInfo> contactGroupInfoList, int groupIndex1, int groupIndex2) {
        List<Long> groupIdList = new ArrayList<>();
        for (ImContactGroupInfo contactGroupInfo : contactGroupInfoList)
        {
            groupIdList.add(contactGroupInfo.getGroupId());
        }
        Collections.swap(groupIdList, groupIndex1, groupIndex2);
        int result = tsdkImManager.updateContactGroupOrder(groupIdList);
        if (0 != result)
        {
            Log.e(TAG, "updateGroupOrder result ->" + result);
        }
        return result;
    }

    @Override
    public int opGroupContact(long newGroupId, long oldGroupId, long contactId, int opType) {
        tsdkContactGroup = tsdkImManager.getContactGroupByGroupId(oldGroupId);
        TsdkContactMoveGroupOpType moveGroupOpType = TsdkContactMoveGroupOpType.TSDK_E_CONTACT_MOVE_GROUP_MOVE_TO_NEW;
        switch (opType)
        {
            case ImConstant.OpContactType.CONTACT_MOVE_TO_NEW_GROUP:
                moveGroupOpType = TsdkContactMoveGroupOpType.TSDK_E_CONTACT_MOVE_GROUP_MOVE_TO_NEW;
                break;
            case ImConstant.OpContactType.CONTACT_COPY_TO_NEW_GROUP:
                moveGroupOpType = TsdkContactMoveGroupOpType.TSDK_E_CONTACT_MOVE_GROUP_COPY_TO_NEW;
                break;
                default:
                    break;
        }
        int result = tsdkContactGroup.moveContact(newGroupId, contactId, moveGroupOpType);
        if (0 != result)
        {
            Log.e(TAG, "opGroupContact result ->" + result);
        }
        return result;
    }

    @Override
    public String addChatGroup(ImChatGroupInfo chatGroupInfo) {
        tsdkChatGroupInfo.setGroupName(chatGroupInfo.getGroupName());
        tsdkChatGroupInfo.setOwnerAccount(chatGroupInfo.getOwnerAccount());
        if (TsdkChatGroupType.TSDK_E_CHAT_GROUP_FIXED_GROUP.getIndex() == chatGroupInfo.getGroupType())
        {
            tsdkChatGroupInfo.setGroupType(TsdkChatGroupType.TSDK_E_CHAT_GROUP_FIXED_GROUP);
        }
        else
        {
            tsdkChatGroupInfo.setGroupType(TsdkChatGroupType.TSDK_E_CHAT_GROUP_DISCUSSION_GROUP);
        }
        String groupId = tsdkImManager.addChatGroup(tsdkChatGroupInfo);
        if (null == groupId)
        {
            Log.e(TAG, "addChatGroup failed");
        }
        return groupId;
    }

    @Override
    public int delChatGroup(String groupId, int type) {
        TsdkChatGroupType chatGroupType;
        if (type == TsdkChatGroupType.TSDK_E_CHAT_GROUP_DISCUSSION_GROUP.getIndex())
        {
            chatGroupType = TsdkChatGroupType.TSDK_E_CHAT_GROUP_DISCUSSION_GROUP;
        }
        else
        {
            chatGroupType = TsdkChatGroupType.TSDK_E_CHAT_GROUP_FIXED_GROUP;
        }
        int result = tsdkImManager.delChatGroup(groupId, chatGroupType);
        if (0 != result)
        {
            Log.e(TAG, "delChatGroup result ->" + result);
        }
        return result;
    }

    @Override
    public int modifyChatGroupInfo(ImChatGroupInfo imChatGroupInfo, int type) {
        if (null == currentChatGroup)
        {
            Log.e(TAG,  "modify chat group info, currentChatGroup is null ");
            return 0;
        }

        tsdkChatGroupInfo.setGroupId(imChatGroupInfo.getGroupId());
        tsdkChatGroupInfo.setOwnerAccount(imChatGroupInfo.getOwnerAccount());
        tsdkChatGroupInfo.setGroupName(imChatGroupInfo.getGroupName());
        tsdkChatGroupInfo.setManifesto(imChatGroupInfo.getManifesto());
        tsdkChatGroupInfo.setDescription(imChatGroupInfo.getDescription());
        if (ImConstant.DISCUSSION == imChatGroupInfo.getGroupType())
        {
            tsdkChatGroupInfo.setGroupType(TsdkChatGroupType.TSDK_E_CHAT_GROUP_DISCUSSION_GROUP);
        }
        else
        {
            tsdkChatGroupInfo.setGroupType(TsdkChatGroupType.TSDK_E_CHAT_GROUP_FIXED_GROUP);
        }

        TsdkChatGroupModifyOpType opType = TsdkChatGroupModifyOpType.TSDK_E_CHAT_GROUP_MODIFY_DEFAULT_PARAM;
        switch (type)
        {
            case ImConstant.GroupOpType.CHAT_GROUP_MODIFY_DEFAULT_PARAM:
                opType = TsdkChatGroupModifyOpType.TSDK_E_CHAT_GROUP_MODIFY_DEFAULT_PARAM;
                break;
            case ImConstant.GroupOpType.CHAT_GROUP_MODIFY_GROUP_TYPE:
                opType = TsdkChatGroupModifyOpType.TSDK_E_CHAT_GROUP_MODIFY_GROUP_TYPE;
                break;
                default:
                    break;
        }

        int result = currentChatGroup.modifyChatGroup(tsdkChatGroupInfo, opType);
        if (0 != result)
        {
            Log.e(TAG, "modifyChatGroupInfo result ->" + result);
        }
        return result;
    }

    @Override
    public List<ImChatGroupInfo> queryChatGroup(String keyword) {
        TsdkChatGroupQueryParam queryParam = new TsdkChatGroupQueryParam();
        queryParam.setSearchKeyword(keyword);
        queryParam.setQueryType(TsdkChatGroupQueryType.TSDK_E_CHAT_GROUP_QUERY_BY_NAME_ID);
        queryParam.setIsNeedTotalCount(1);  // 是否需要返回总数
        queryParam.setMaxReturnedCount(20); // 本次查询最大返回结果数量
        TsdkChatGroupQueryResult queryResult = tsdkImManager.queryChatGroups(queryParam);
        if (null == queryResult)
        {
            Log.e(TAG, "queryChatGroup failed.");
            return null;
        }

        imChatGroupInfoList.clear();
        List<TsdkChatGroupInfo> chatGroupInfoList;
        if (queryResult.getChatGroupInfoList().size() > 0)
        {
            chatGroupInfoList = queryResult.getChatGroupInfoList();
            for (TsdkChatGroupInfo tsdkChatGroupInfo : chatGroupInfoList)
            {
                ImChatGroupInfo chatGroupInfo = new ImChatGroupInfo();
                chatGroupInfo.setGroupId(tsdkChatGroupInfo.getGroupId());
                chatGroupInfo.setGroupName(tsdkChatGroupInfo.getGroupName());
                chatGroupInfo.setGroupType(tsdkChatGroupInfo.getGroupType());
                imChatGroupInfoList.add(chatGroupInfo);
            }
        }
        return imChatGroupInfoList;
    }

    @Override
    public ImChatGroupInfo getChatGroupInfo(String groupId) {
        this.currentChatGroup = tsdkImManager.getChatGroupByGroupId(groupId);

        if (null == currentChatGroup)
        {
            Log.e(TAG,  "get chat group info, currentChatGroup is null ");
            return null;
        }
        tsdkChatGroupInfo = tsdkImManager.getChatGroupDetail(groupId);

        if (null == tsdkChatGroupInfo)
        {
            Log.e(TAG, "getChatGroupInfo failed.");
            return null;
        }

        chatGroupInfo.setGroupId(tsdkChatGroupInfo.getGroupId());
        chatGroupInfo.setGroupName(tsdkChatGroupInfo.getGroupName());
        chatGroupInfo.setManifesto(tsdkChatGroupInfo.getManifesto());
        chatGroupInfo.setDescription(tsdkChatGroupInfo.getDescription());
        chatGroupInfo.setGroupType(tsdkChatGroupInfo.getGroupType());
        chatGroupInfo.setOwnerAccount(tsdkChatGroupInfo.getOwnerAccount());
        return chatGroupInfo;
    }

    @Override
    public List<ImContactInfo> getChatGroupMembers(String timestamp, boolean isSyncAll) {
        if (null == currentChatGroup)
        {
            Log.e(TAG,  "get chat group members, currentChatGroup is null ");
            return null;
        }

        // 获取聊天群组成员之前先清空上一次成员列表
        imContactInfoList.clear();
        TsdkChatGroupMemberGetResult result = currentChatGroup.getChatGroupMembers(timestamp, isSyncAll);
        if (result.getMemberList().isEmpty())
        {
            return null;
        }
        for (TsdkImUserInfo userInfo : result.getMemberList())
        {
            ImContactInfo contactInfo = new ImContactInfo();
            contactInfo.setAccount(userInfo.getStaffAccount());
            imContactInfoList.add(contactInfo);
        }
        return imContactInfoList;
    }

    @Override
    public int addChatGroupMember(boolean isInvite, String inviteAccount, String joiningAccount) {
        if (null == currentChatGroup)
        {
            Log.e(TAG,  "add chat group member, currentChatGroup is null ");
            return 0;
        }

        /**
         * 通过id获取tsdkChatGroup对象的，但是由于这个方法中获取的对象只在全量更新的时候才能得到，目前实现方式如下：
         * 第一次创建聊天群组成功后，添加群组成员：tsdkChatGroup对象从创建群组的回调中获取
         * 查询已经创建的聊天群组，添加群组成员：tsdkChatGroup对象从查询结果中获取
         */
        int result = currentChatGroup.requestJoinChatGroup(isInvite, inviteAccount , "", joiningAccount);
        if (0 != result)
        {
            Log.e(TAG, "addChatGroupMember result ->" + result);
        }
        return result;
    }

    @Override
    public int delChatGroupMember(String memberAccount) {
        if (null == currentChatGroup)
        {
            Log.e(TAG,  "del chat group member, currentChatGroup is null ");
            return 0;
        }

        int result = currentChatGroup.delChatGroupMember(memberAccount);
        if (0 != result)
        {
            Log.e(TAG, "delChatGroupMember result ->" + result);
        }
        return result;
    }

    @Override
    public int leaveChatGroup() {
        if (null == currentChatGroup)
        {
            Log.e(TAG,  "leave chat group, currentChatGroup is null ");
            return 0;
        }

        int result = currentChatGroup.leaveChatGroup();
        if (0 != result)
        {
            Log.e(TAG, "leaveChatGroup result ->" + result);
        }
        return result;
    }

    @Override
    public int setInputStatus(boolean isInputting, String chatId) {
        TsdkImUserInfo toUserInfo = new TsdkImUserInfo();
        toUserInfo.setStaffAccount(chatId);
        tsdkChatConversation = new TsdkChatConversation(imSelfInfo, toUserInfo);

        int result = tsdkChatConversation.setInputting(isInputting);
        if (0 != result)
        {
            Log.e(TAG, "setInputStatus result ->" + result);
        }
        return result;
    }

    @Override
    public int sendMessage(ImConstant.ChatMsgType msgType, ImConstant.ChatMsgMediaType msgMediaType, String content, String target) {
        TsdkImUserInfo toUserInfo = new TsdkImUserInfo();
        toUserInfo.setStaffAccount(target);
        TsdkChatGroupInfo toGroupInfo = new TsdkChatGroupInfo();
        toGroupInfo.setGroupId(target);
        TsdkChatMsgType chatMsgType = TsdkChatMsgType.TSDK_E_CHAT_MSG_TYPE_SINGLE_CHAT;
        switch (msgType)
        {
            case SINGLE_CHAT:
                chatMsgType = TsdkChatMsgType.TSDK_E_CHAT_MSG_TYPE_SINGLE_CHAT;
                tsdkChatConversation = new TsdkChatConversation(this.imSelfInfo, toUserInfo);
                break;
            case FIXED_GROUP_CHAT:
                chatMsgType = TsdkChatMsgType.TSDK_E_CHAT_MSG_TYPE_FIXED_GROUP;
                tsdkChatConversation = new TsdkChatConversation(this.imSelfInfo, toGroupInfo);
                break;
            case DISCUSSION_GROUP_CHAT:
                chatMsgType = TsdkChatMsgType.TSDK_E_CHAT_MSG_TYPE_DISCUSSION_GROUP;
                tsdkChatConversation = new TsdkChatConversation(this.imSelfInfo, toGroupInfo);
                break;
                default:
                    break;
        }

        TsdkChatMsgMediaType chatMediaType = TsdkChatMsgMediaType.TSDK_E_CHAT_MSG_MEDIA_TYPE_TEXT;
        switch (msgMediaType)
        {
            case CHAT_MSG_MEDIA_TYPE_TEXT:
                chatMediaType = TsdkChatMsgMediaType.TSDK_E_CHAT_MSG_MEDIA_TYPE_TEXT;
                break;
            case CHAT_MSG_MEDIA_TYPE_IMAGE:
                chatMediaType = TsdkChatMsgMediaType.TSDK_E_CHAT_MSG_MEDIA_TYPE_IMAGE;
                break;
            case CHAT_MSG_MEDIA_TYPE_AUDIO:
                chatMediaType = TsdkChatMsgMediaType.TSDK_E_CHAT_MSG_MEDIA_TYPE_AUDIO;
                break;
            case CHAT_MSG_MEDIA_TYPE_VIDEO:
                chatMediaType = TsdkChatMsgMediaType.TSDK_E_CHAT_MSG_MEDIA_TYPE_VIDEO;
                break;
                default:
                    break;
        }

        if (null == tsdkChatConversation)
        {
            return -1;
        }

        String msgContent = htmlToString(content);

        int result = tsdkChatConversation.sendMessage(0, chatMsgType, chatMediaType, msgContent, null);
        if (0 != result)
        {
            Log.e(TAG, "sendMessage result ->" + result);
        }
        return result;
    }

    private String htmlToString(String content)
    {
        return "<r><n></n><g>0</g><c>&lt;imbody&gt;&lt;imagelist&gt;&lt;/imagelist&gt;&lt;content&gt;" +
                content + "&lt;/content&gt;&lt;html&gt;&lt;/html&gt;&lt;/imbody&gt;</c></r>";
    }

    @Override
    public int withdrawMsg(ImChatMsgInfo chatMsgInfo) {
        TsdkImUserInfo toUserInfo = new TsdkImUserInfo();
        toUserInfo.setStaffAccount(chatMsgInfo.getToId());

        TsdkChatGroupInfo toGroupInfo = new TsdkChatGroupInfo();
        toGroupInfo.setGroupId(chatMsgInfo.getToId());
        toGroupInfo.setGroupName(chatMsgInfo.getToName());

        if (currentRecentChat.isGroupChat())
        {
            tsdkChatConversation = new TsdkChatConversation(imSelfInfo, toGroupInfo);
        }
        else
        {
            tsdkChatConversation = new TsdkChatConversation(imSelfInfo, toUserInfo);
        }

        List<TsdkMsgBaseInfo> withdrawMsgList = new ArrayList<>();
        TsdkMsgBaseInfo msgId = new TsdkMsgBaseInfo();
        msgId.setMsgId(chatMsgInfo.getServerMsgId());
        withdrawMsgList.add(msgId);

        int result = tsdkChatConversation.withdrawMessage(withdrawMsgList, true);
        if (0 != result)
        {
            Log.e(TAG, "withdrawMsg result ->" + result);
        }
        return result;
    }

    @Override
    public int setMsgRead(List<ImChatMsgInfo> unReadMsgList, ImConstant.ChatMsgType chatMsgType, String chatId) {
        TsdkImUserInfo toUserInfo = new TsdkImUserInfo();
        toUserInfo.setStaffAccount(chatId);

        TsdkChatGroupInfo toGroupInfo = new TsdkChatGroupInfo();
        toGroupInfo.setGroupId(chatId);

        if (chatMsgType == ImConstant.ChatMsgType.SINGLE_CHAT)
        {
            tsdkChatConversation = new TsdkChatConversation(imSelfInfo, toUserInfo);
        }
        else
        {
            tsdkChatConversation = new TsdkChatConversation(imSelfInfo, toGroupInfo);
        }

        msgIdList.clear();
        List<TsdkSetMsgReadInfo> readMsgList = new ArrayList<>();
        for (ImChatMsgInfo msgInfo : unReadMsgList)
        {
            TsdkSetMsgReadInfo readInfo = new TsdkSetMsgReadInfo();
            if (chatMsgType == ImConstant.ChatMsgType.SINGLE_CHAT)
            {
                readInfo.setHistoryMsgType(TsdkHistoryChatMsgType.TSDK_E_HISTORY_CHAT_MSG_TYPE_SINGLE_CHAT);
                readInfo.setOrigin(msgInfo.getFromId());
            }
            else
            {
                readInfo.setHistoryMsgType(TsdkHistoryChatMsgType.TSDK_E_HISTORY_CHAT_MSG_TYPE_GROUP_CHAT);
                readInfo.setOrigin(msgInfo.getToId());
            }
            readInfo.setMsgId(msgInfo.getServerMsgId());
            readMsgList.add(readInfo);
            msgIdList.add(msgInfo.getServerMsgId());
        }

        int result = tsdkChatConversation.setMessageRead(readMsgList);
        if (0 != result)
        {
            Log.e(TAG, "setMsgRead result ->" + result);
        }
        else
        {
            updateCurrentRecentChat(ImConstant.SET_READ, msgIdList);
        }
        return result;
    }

    @Override
    public List<ImChatMsgInfo> queryHistoryMsg(boolean isFirst, long msgId, int msgType, String origin, int count) {
        TsdkImUserInfo toUserInfo = new TsdkImUserInfo();
        toUserInfo.setStaffAccount(origin);

        TsdkChatGroupInfo toGroupInfo = new TsdkChatGroupInfo();
        toGroupInfo.setGroupId(origin);

        TsdkQueryHistoryMsgParam queryHistoryMsgParam = new TsdkQueryHistoryMsgParam();
        if (isFirst)
        {
            queryHistoryMsgParam.setQueryType(TsdkHistoryChatMsgQueryType.TSDK_E_HISTORY_CHAT_MSG_QUERY_FIRST);
        }
        else
        {
            queryHistoryMsgParam.setQueryType(TsdkHistoryChatMsgQueryType.TSDK_E_HISTORY_CHAT_MSG_QUERY_BEFORE);
            queryHistoryMsgParam.setMsgId(msgId);
        }
        if (0 == msgType)
        {
            queryHistoryMsgParam.setHistoryMsgType(TsdkHistoryChatMsgType.TSDK_E_HISTORY_CHAT_MSG_TYPE_SINGLE_CHAT);
            tsdkChatConversation = new TsdkChatConversation(imSelfInfo, toUserInfo);
        }
        else
        {
            queryHistoryMsgParam.setHistoryMsgType(TsdkHistoryChatMsgType.TSDK_E_HISTORY_CHAT_MSG_TYPE_GROUP_CHAT);
            tsdkChatConversation = new TsdkChatConversation(imSelfInfo, toGroupInfo);
        }
        queryHistoryMsgParam.setOrigin(origin);
        queryHistoryMsgParam.setCount(count);

        TsdkQueryHistoryMsgResult queryHistoryMsgResult = tsdkChatConversation.queryHistoryMessage(queryHistoryMsgParam);
        if (null == queryHistoryMsgResult)
        {
            return null;
        }

        if (null == queryHistoryMsgResult.getChatMsgList() || 0 == queryHistoryMsgResult.getChatMsgCount())
        {
            return null;
        }

        List<ImChatMsgInfo> historyMsgList = new ArrayList<>();
        for (TsdkChatMsgInfo msgInfo : queryHistoryMsgResult.getChatMsgList())
        {
            ImChatMsgInfo chatMsgInfo = new ImChatMsgInfo();
            chatMsgInfo.setContent(msgInfo.getContent());
            chatMsgInfo.setFromId(msgInfo.getOrigin());
            chatMsgInfo.setFromName(msgInfo.getOriginName());
            chatMsgInfo.setToId(msgInfo.getTarget());
            chatMsgInfo.setServerMsgId(msgInfo.getServerMsgId());
            chatMsgInfo.setUtcStamp(msgInfo.getUtcStamp());
            historyMsgList.add(chatMsgInfo);
        }
        return historyMsgList;
    }

    @Override
    public int deleteHistoryMsg(String origin, long msgId, boolean isDelAll) {
        TsdkImUserInfo toUserInfo = new TsdkImUserInfo();
        toUserInfo.setStaffAccount(origin);

        TsdkChatGroupInfo toGroupInfo = new TsdkChatGroupInfo();
        toGroupInfo.setGroupId(origin);

        if (currentRecentChat.isGroupChat())
        {
            tsdkChatConversation = new TsdkChatConversation(imSelfInfo, toGroupInfo);
        }
        else
        {
            tsdkChatConversation = new TsdkChatConversation(imSelfInfo, toUserInfo);
        }

        TsdkDelHistoryChatMsgOpType delHistoryChatMsgOpType;
        if (!isDelAll)
        {
            delHistoryChatMsgOpType = TsdkDelHistoryChatMsgOpType.TSDK_E_DEl_HISTORY_CHAT_MSG_SINGLE;
        }
        else
        {
            delHistoryChatMsgOpType = TsdkDelHistoryChatMsgOpType.TSDK_E_DEl_HISTORY_CHAT_MSG_ALL;
        }

        List<TsdkMsgBaseInfo> delMsgList = new ArrayList<>();
        TsdkMsgBaseInfo msgBaseInfo = new TsdkMsgBaseInfo();
        msgBaseInfo.setMsgId(msgId);
        delMsgList.add(msgBaseInfo);
        int result = tsdkChatConversation.deleteHistoryMessage(origin, delMsgList, delHistoryChatMsgOpType);
        if (0 != result)
        {
            Log.e(TAG, "deleteHistoryMsg result ->" + result);
        }
        else
        {
            msgIdList.clear();
            msgIdList.add(msgId);
            updateCurrentRecentChat(ImConstant.DELETE_MESSAGE, msgIdList);
        }
        return result;
    }

    public ImConstant.ImStatus updateUserStatus(String account)
    {
        ImConstant.ImStatus status = ImConstant.ImStatus.AWAY;
        if (statusMap.containsKey(account))
        {
            switch (TsdkImUserStatus.enumOf(statusMap.get(account)))
            {
                case TSDK_E_IM_USER_STATUS_OFFLINE:
                    status = ImConstant.ImStatus.AWAY;
                    break;
                case TSDK_E_IM_USER_STATUS_ONLINE:
                    status = ImConstant.ImStatus.ON_LINE;
                    break;
                case TSDK_E_IM_USER_STATUS_BUSY:
                    status = ImConstant.ImStatus.BUSY;
                    break;
                case TSDK_E_IM_USER_STATUS_LEAVE:
                    status = ImConstant.ImStatus.XA;
                    break;
                case TSDK_E_IM_USER_STATUS_DND:
                    status = ImConstant.ImStatus.DND;
                    break;
                default:
                    break;
            }
        }
        return status;
    }

    public long getFriendId(String account)
    {
        List<TsdkImUserInfo> friendList = tsdkImManager.getFriendList();
        if (null == friendList || friendList.size() == 0)
        {
            return -1;
        }
        for (TsdkImUserInfo userInfo : friendList)
        {
            if (userInfo.getStaffAccount().equals(account))
            {
                return userInfo.getContactId();
            }
        }
        return -1;
    }

    public void updateChatGroupInfo(TsdkChatGroup chatGroup)
    {
        chatGroupInfo.setGroupId(chatGroup.getChatGroupInfo().getGroupId());
        chatGroupInfo.setGroupName(chatGroup.getChatGroupInfo().getGroupName());
        chatGroupInfo.setManifesto(chatGroup.getChatGroupInfo().getManifesto());
        chatGroupInfo.setDescription(chatGroup.getChatGroupInfo().getDescription());
        chatGroupInfo.setGroupType(chatGroup.getChatGroupInfo().getGroupType());
        chatGroupInfo.setOwnerAccount(chatGroup.getChatGroupInfo().getOwnerAccount());
    }

    public ImChatMsgInfo tsdkTransToImChatMsgInfo(TsdkChatMsgInfo chatMsgInfo)
    {
        ImChatMsgInfo batchMsg = new ImChatMsgInfo();
        batchMsg.setContent(chatMsgInfo.getContent());
        batchMsg.setFromId(chatMsgInfo.getOrigin());
        if (null == chatMsgInfo.getOriginName())
        {
            batchMsg.setFromName(chatMsgInfo.getOrigin());
        }
        else
        {
            batchMsg.setFromName(chatMsgInfo.getOriginName());
        }
        if (chatMsgInfo.getChatType() == ImConstant.ChatMsgType.SINGLE_CHAT.getIndex())
        {
            batchMsg.setToId(chatMsgInfo.getTarget());
            batchMsg.setToName(this.imSelfInfo.getName());
            batchMsg.setMsgType(ImConstant.ChatMsgType.SINGLE_CHAT);
        }
        else if (chatMsgInfo.getChatType() == ImConstant.ChatMsgType.FIXED_GROUP_CHAT.getIndex())
        {
            batchMsg.setToId(chatMsgInfo.getGroupId());
            batchMsg.setToName(chatMsgInfo.getGroupName());
            batchMsg.setMsgType(ImConstant.ChatMsgType.FIXED_GROUP_CHAT);
        }
        else
        {
            batchMsg.setToId(chatMsgInfo.getGroupId());
            batchMsg.setToName(chatMsgInfo.getGroupName());
            batchMsg.setMsgType(ImConstant.ChatMsgType.DISCUSSION_GROUP_CHAT);
        }

        batchMsg.setServerMsgId(chatMsgInfo.getServerMsgId());
        batchMsg.setUtcStamp(chatMsgInfo.getUtcStamp());
        return batchMsg;
    }

    public void addToRecentChatsList(ImChatMsgInfo newMsg, boolean isSender)
    {
        List<ImChatMsgInfo> msgInfo = new ArrayList<>();
        List<ImChatMsgInfo> unReadMsg = new ArrayList<>();
        ImRecentChatInfo imRecentChat;
        boolean isGroup = (0 == newMsg.getMsgType().getIndex()) ? false : true;
        if (isSender)
        {
            imRecentChat = getCurrentRecentChatByChatId(newMsg.getToId());
            if (null == imRecentChat)
            {
                imRecentChat = new ImRecentChatInfo();
                msgInfo.add(newMsg);
                imRecentChat.setTag(newMsg.getToId());
                imRecentChat.setChatName(newMsg.getToName());
                imRecentChat.setUnReadMsgList(unReadMsg);
                imRecentChat.setLastChatMsg(newMsg);
                imRecentChat.setChatMsgList(msgInfo);
                imRecentChat.setUnReadMsgCount(unReadMsg.size());
                imRecentChat.setGroupChat(isGroup);
                recentChatList.add(imRecentChat);
                return;
            }
            msgInfo = imRecentChat.getChatMsgList();
            msgInfo.add(newMsg);
            imRecentChat.setChatMsgList(msgInfo);
            imRecentChat.setLastChatMsg(newMsg);
            imRecentChat.setGroupChat(isGroup);
        }
        else
        {
            if (isGroup)
            {
                imRecentChat = getCurrentRecentChatByChatId(newMsg.getToId());
            }
            else
            {
                imRecentChat = getCurrentRecentChatByChatId(newMsg.getFromId());
            }
            if (null == imRecentChat)
            {
                imRecentChat = new ImRecentChatInfo();
                msgInfo.add(newMsg);
                if (isGroup)
                {
                    imRecentChat.setTag(newMsg.getToId());
                    imRecentChat.setChatName(newMsg.getToName());
                }
                else
                {
                    imRecentChat.setTag(newMsg.getFromId());
                    imRecentChat.setChatName(newMsg.getFromName());
                }
                imRecentChat.setChatMsgList(msgInfo);
                imRecentChat.setLastChatMsg(newMsg);
                unReadMsg.add(newMsg);
                imRecentChat.setUnReadMsgList(unReadMsg);
                imRecentChat.setUnReadMsgCount(unReadMsg.size());
                imRecentChat.setGroupChat(isGroup);
                recentChatList.add(imRecentChat);
                return;
            }
            msgInfo = imRecentChat.getChatMsgList();
            unReadMsg = imRecentChat.getUnReadMsgList();
            msgInfo.add(newMsg);
            imRecentChat.setChatMsgList(msgInfo);
            imRecentChat.setLastChatMsg(newMsg);
            unReadMsg.add(newMsg);
            imRecentChat.setUnReadMsgList(unReadMsg);
            imRecentChat.setUnReadMsgCount(unReadMsg.size());
            imRecentChat.setGroupChat(isGroup);
        }
    }

    private void recentChatsSort(List<ImChatMsgInfo> msgInfoList)
    {
        if (msgInfoList.isEmpty())
        {
            return;
        }
        Collections.sort(msgInfoList, new Comparator<ImChatMsgInfo>() {
            @Override
            public int compare(ImChatMsgInfo o1, ImChatMsgInfo o2) {
                if (o1.getUtcStamp() - o2.getUtcStamp() > 0)
                {
                    return 1;
                }
                else if (o1.getUtcStamp() - o2.getUtcStamp() == 0)
                {
                    return 0;
                }
                else
                {
                    return -1;
                }
            }
        });
    }

    public List<ImRecentChatInfo> loadRecentChats()
    {
        if (null == recentChatList || recentChatList.size() == 0)
        {
            return null;
        }

        return recentChatList;
    }

    public void updateCurrentRecentChat(int type, List<Long> msgIdList)
    {
        if (null == currentRecentChat)
        {
            return;
        }

        if (null == currentRecentChat.getChatMsgList())
        {
            return;
        }

        switch (type)
        {
            case ImConstant.SET_READ:
                for (long msgId : msgIdList)
                {
                    Iterator<ImChatMsgInfo> iterator = currentRecentChat.getUnReadMsgList().iterator();
                    while (iterator.hasNext())
                    {
                        ImChatMsgInfo msgInfo = iterator.next();
                        if (msgInfo.getServerMsgId() == msgId)
                        {
                            iterator.remove();
                            break;
                        }
                    }
                }
                if (null != currentRecentChat.getUnReadMsgList())
                {
                    currentRecentChat.setUnReadMsgCount(currentRecentChat.getUnReadMsgList().size());
                }
                else
                {
                    currentRecentChat.setUnReadMsgCount(0);
                }
                break;
            case ImConstant.DELETE_MESSAGE:
            case ImConstant.WHITDRAW_MESSAGE:
                for (long msgId : msgIdList)
                {
                    Iterator<ImChatMsgInfo> iterator = currentRecentChat.getChatMsgList().iterator();
                    while (iterator.hasNext())
                    {
                        ImChatMsgInfo msgInfo = iterator.next();
                        if (msgInfo.getServerMsgId() == msgId)
                        {
                            iterator.remove();
                            break;
                        }
                    }
                }
                break;
                default:
                    break;
        }
    }

    /**
     * This method is used to logout.
     * 登出(注销)
     */
    public void imLogout()
    {
        //将个人状态设置为离线
        setImLoginStatus(ImConstant.ImStatus.AWAY);
        //Clear unread message map.
        //清除未读消息的map集合
        UnreadMessageService.getInstance().clearUnreadMap();

//        TupIm.instance().clearResources();
    }

    /*********************************************************************************************************************/

    public void handleUserStatusUpdate(List<TsdkImUserStatusUpdateInfo> userStatusInfoList)
    {
        Log.i(TAG, "handleUserStatusUpdate.");
        if (null == userStatusInfoList || userStatusInfoList.size() == 0)
        {
            return;
        }

        for (TsdkImUserStatusUpdateInfo statusUpdateInfo : userStatusInfoList)
        {
            statusMap.put(statusUpdateInfo.getOrigin(), statusUpdateInfo.getStatus());
        }
        setStatusMap(statusMap);
        mNotification.onUserStatusUpdate();
    }

    /**
     * [en]This method is used to handle user update information event.
     * [cn]处理用户更新信息事件
     *
     * @param userInfoList            [en]Indicates user information list
     *                                [cn]更新用户的列表
     */
    public void handleUserInfoUpdate(List<TsdkImUserInfo> userInfoList)
    {
        Log.i(TAG, "handleUserInfoUpdate.");
        if (null != userInfoList)
        {
            List<ImContactInfo> contactInfoList = new ArrayList<>();
            for (TsdkImUserInfo imUserInfo : userInfoList)
            {
                ImContactInfo imContactInfo = new ImContactInfo();
                imContactInfo.setContactId(imUserInfo.getContactId());
                imContactInfo.setName(imUserInfo.getName());
                imContactInfo.setSignature(imUserInfo.getSignature());
                imContactInfo.setAccount(imUserInfo.getStaffAccount());
                imContactInfo.setAddress(imUserInfo.getAddress());
                imContactInfo.setDepartmentName(imUserInfo.getDepartmentNameEn());
                imContactInfo.setEmail(imUserInfo.getEmail());
                imContactInfo.setFax(imUserInfo.getFax());
                imContactInfo.setMobile(imUserInfo.getMobile());
                imContactInfo.setSoftNumber(imUserInfo.getVoip());
                imContactInfo.setTitle(imUserInfo.getTitle());
                imContactInfo.setZipCode(imUserInfo.getZipCode());
                if (mMyAccount.equals(imUserInfo.getStaffAccount()))
                {
                    this.imSelfInfo = imUserInfo;
                }
                contactInfoList.add(imContactInfo);
            }
            mNotification.onUserInfoUpdate(contactInfoList);
        }
        else
        {
            mNotification.onUserInfoUpdate(null);
        }
    }

    public void handleJoinChatGroupRsp(TsdkChatGroup chatGroup, TsdkRspJoinChatGroupMsg rspJoinChatGroupMsg)
    {
        Log.i(TAG, "handleJoinChatGroupRsp.");
        if (null == chatGroup || null == rspJoinChatGroupMsg)
        {
            return;
        }

    }

    public void handleJoinChatGroupInd(TsdkChatGroup chatGroup, TsdkBeAddedToChatGroupInfo beAddedToChatGroupInfo)
    {
        Log.i(TAG, "handleJoinChatGroupInd.");
        if (null == chatGroup || null == beAddedToChatGroupInfo)
        {
            return;
        }
        this.currentChatGroup = chatGroup;
        mNotification.onJoinChatGroupInd(chatGroup);
    }

    public void handleLeaveChatGroupResult(TsdkLeaveChatGroupResult leaveChatGroupResult)
    {
        Log.i(TAG, "handleLeaveChatGroupResult.");
        if (null == leaveChatGroupResult)
        {
            return;
        }

        mNotification.onLeaveChatGroupResult(leaveChatGroupResult.getResult());
    }

    public void handleChatGroupInfoUpdate(TsdkChatGroup chatGroup, TsdkChatGroupUpdateInfo chatGroupUpdateInfo, TsdkChatGroupInfoUpdateType updateType)
    {
        Log.i(TAG, "handleChatGroupInfoUpdate.");
        if (null == chatGroupUpdateInfo)
        {
            return;
        }

        if (null != chatGroup)
        {
            updateChatGroupInfo(chatGroup);
        }

        int type = ImConstant.ChatGroupUpdateType.CHAT_GROUP_INFO_UPDATE;
        switch (updateType)
        {
            case TSDK_E_CHAT_GROUP_DEFAULT_INFO_UPDATE:
                type = ImConstant.ChatGroupUpdateType.CHAT_GROUP_INFO_UPDATE;
                break;
            case TSDK_E_CHAT_GROUP_ADD_MEMBER:
                type = ImConstant.ChatGroupUpdateType.CHAT_GROUP_ADD_MEMBER;
                break;
            case TSDK_E_CHAT_GROUP_DEL_MEMBER:
                type = ImConstant.ChatGroupUpdateType.CHAT_GROUP_DEL_MEMBER;
                if (chatGroupUpdateInfo.getUpdateMemberAccount().equals(mMyAccount))
                {
                    // 如果被删除的成员是自己的话，则删掉本地群组，此时需要增量更新才能删除掉该聊天分组
                    type = ImConstant.ChatGroupUpdateType.CHAT_GROUP_DISMISS;
                }
                break;
            case TSDK_E_CHAT_GROUP_OWNER_UPDATE:
                type = ImConstant.ChatGroupUpdateType.CHAT_GROUP_OWNER_UPDATE;
                break;
            case TSDK_E_CHAT_GROUP_DISMISS:
                type = ImConstant.ChatGroupUpdateType.CHAT_GROUP_DISMISS;
                break;
                default:
                    break;
        }

        mNotification.onChatGroupInfoUpdate(chatGroupInfo, type);
    }

    public void handleInputtingStatusInd(TsdkInputtingStatusInfo inputtingStatusInfo)
    {
        Log.i(TAG, "handleInputtingStatusInd.");
        if (null == inputtingStatusInfo)
        {
            return;
        }

        String origin = inputtingStatusInfo.getOrigin();
        if (!this.currentChatId.equals(origin))
        {
            return;
        }

        boolean isInputting = (1 == inputtingStatusInfo.getIsInputting()) ? true : false;
        mNotification.onInputtingStatusInd(isInputting);
    }

    public void handleChatMsg(TsdkChatMsgInfo chatMsgInfo)
    {
        Log.i(TAG, "handleChatMsg.");
        if (null == chatMsgInfo)
        {
            Log.e(TAG, "handleChatMsg chatMsgInfo is null");
            return;
        }

        ImChatMsgInfo newMsg = tsdkTransToImChatMsgInfo(chatMsgInfo);
        addToRecentChatsList(newMsg, false);
        mNotification.onReceiveMessage(newMsg);
    }

    public void handleBatchChatMsg(TsdkBatchChatMsgInfo batchChatMsgInfo)
    {
        Log.i(TAG, "handleBatchChatMsg.");
        if (batchChatMsgInfo.getCount() == 0 || null == batchChatMsgInfo.getChatMsgList())
        {
            return;
        }

        ImRecentChatInfo recentChatInfo = new ImRecentChatInfo();
        List<ImChatMsgInfo> msgInfoList = new ArrayList<>();
        List<ImChatMsgInfo> unReadMsg = new ArrayList<>();
        String chatId = null;
        String chatName = null;
        boolean isGroup = false;
        for (TsdkChatMsgInfo chatMsgInfo : batchChatMsgInfo.getChatMsgList())
        {
            ImChatMsgInfo batchMsg = tsdkTransToImChatMsgInfo(chatMsgInfo);
            if (chatMsgInfo.getChatType() == ImConstant.ChatMsgType.SINGLE_CHAT.getIndex())
            {
                chatId = chatMsgInfo.getOrigin();
                chatName = chatMsgInfo.getOriginName();
                isGroup = false;
            }
            else
            {
                chatId = chatMsgInfo.getGroupId();
                chatName = chatMsgInfo.getGroupName();
                isGroup = true;
            }
            msgInfoList.add(batchMsg);
            unReadMsg.add(batchMsg);
        }
        recentChatsSort(msgInfoList);
        recentChatInfo.setTag(chatId);
        recentChatInfo.setChatName(chatName);
        recentChatInfo.setUnReadMsgCount(batchChatMsgInfo.getCount());
        recentChatInfo.setChatMsgList(msgInfoList);
        recentChatInfo.setUnReadMsgList(unReadMsg);
        recentChatInfo.setLastChatMsg(msgInfoList.get(msgInfoList.size() - 1));
        recentChatInfo.setGroupChat(isGroup);
        recentChatList.add(recentChatInfo);

        mNotification.onRefreshUnreadMessage(msgInfoList);
    }

    public void handleMsgSendResult(TsdkSendChatMsgResult sendChatMsgResult)
    {
        Log.i(TAG, "handleMsgSendResult.");
        int result = sendChatMsgResult.getResult();
        if (0 != result)
        {
            Log.e(TAG, "msg send result ->" + result);
            return;
        }

        ImChatMsgInfo chatMsgInfo = new ImChatMsgInfo();
        chatMsgInfo.setFromId(sendChatMsgResult.getTarget());
        chatMsgInfo.setFromName(this.imSelfInfo.getName());
        chatMsgInfo.setServerMsgId(sendChatMsgResult.getServerMsgId());
        chatMsgInfo.setUtcStamp(sendChatMsgResult.getUtcStamp());

        mNotification.onSendMessagesSuccess(chatMsgInfo);
    }

    public void handleMsgWithdrawResult(TsdkChatMsgWithdrawResult chatMsgWithdrawResult)
    {
        Log.i(TAG, "handleMsgWithdrawResult.");
        int result = chatMsgWithdrawResult.getResult();
        if (0 != result)
        {
            Log.e(TAG, "msg withdraw result ->" + result);
            mNotification.onWithdrawMessagesFail();
        }
    }

    public void handleMsgWithdrawInd(TsdkChatMsgWithdrawInfo chatMsgWithdrawInfo)
    {
        Log.i(TAG, "handleMsgWithdrawInd.");

        String chatId;
        String withdrawUser;
        if (mMyAccount.equals(chatMsgWithdrawInfo.getOrigin()))
        {
            // 我撤回的消息
            chatId = chatMsgWithdrawInfo.getTarget();
            withdrawUser = mMyAccount;
        }
        else
        {
            if (1 == chatMsgWithdrawInfo.getWithdrawMsgType())
            {
                chatId = chatMsgWithdrawInfo.getOrigin();
            }
            else
            {
                chatId = chatMsgWithdrawInfo.getTarget();
            }
            withdrawUser = chatMsgWithdrawInfo.getOrigin();
        }

        if (null != chatMsgWithdrawInfo.getWithdrawMsgList())
        {
            msgIdList.clear();
            for (TsdkMsgBaseInfo msgBaseInfo : chatMsgWithdrawInfo.getWithdrawMsgList())
            {
                msgIdList.add(msgBaseInfo.getMsgId());
            }
        }
        updateCurrentRecentChat(ImConstant.WHITDRAW_MESSAGE, msgIdList);

        if (chatId.equals(this.currentChatId))
        {
            mNotification.onWithdrawMessagesInd(withdrawUser);
        }
    }
}
