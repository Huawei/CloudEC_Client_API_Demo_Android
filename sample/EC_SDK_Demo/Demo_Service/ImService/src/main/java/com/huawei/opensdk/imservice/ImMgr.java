package com.huawei.opensdk.imservice;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.huawei.common.constant.ResponseCodeHandler;
import com.huawei.contacts.ContactLogic;
import com.huawei.contacts.PersonalContact;
import com.huawei.dao.impl.RecentChatContactDao;
import com.huawei.data.AddFriendResp;
import com.huawei.data.ConstGroup;
import com.huawei.data.ConstGroupContact;
import com.huawei.data.ExecuteResult;
import com.huawei.data.GetRoamingChatRecordData;
import com.huawei.data.GroupChangeNotifyData;
import com.huawei.data.GroupMemberChangedNotifyData;
import com.huawei.data.InviteToGroupResp;
import com.huawei.data.JoinConstantGroupResp;
import com.huawei.data.LeaveGroupResp;
import com.huawei.data.LoginDeviceResp;
import com.huawei.data.ManageGroupResp;
import com.huawei.data.MarkReadMessageNotifyData;
import com.huawei.data.PersonalTeam;
import com.huawei.data.PresenceNotifyData;
import com.huawei.data.QueryGroupMembersResponseData;
import com.huawei.data.RequestJoinInGroupNotifyData;
import com.huawei.data.SearchContactsResp;
import com.huawei.data.UploadHeadPhotoResp;
import com.huawei.data.ViewHeadPhotoData;
import com.huawei.data.ViewHeadPhotoParam;
import com.huawei.data.base.BaseResponseData;
import com.huawei.data.entity.InstantMessage;
import com.huawei.data.entity.RecentChatContact;
import com.huawei.data.unifiedmessage.MediaResource;
import com.huawei.ecs.mtk.log.LogLevel;
import com.huawei.factory.ResourceGenerator;
import com.huawei.http.FileTransfer;
import com.huawei.module.um.UmConstant;
import com.huawei.module.um.UmUtil;
import com.huawei.opensdk.commonservice.common.LocContext;
import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;
import com.huawei.opensdk.imservice.data.UmTransProgressData;
import com.huawei.opentup.ITupIm;
import com.huawei.opentup.TupIm;
import com.huawei.opentup.TupImCallback;
import com.huawei.opentup.TupUm;
import com.huawei.opentup.TupUmNotify;
import com.huawei.service.login.LoginError;
import com.huawei.service.login.LoginErrorResp;
import com.huawei.service.login.NetworkInfoManager;
import com.huawei.tup.TUPInterfaceService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class is about instant messaging function management class.
 * Im模块管理类
 */
public class ImMgr implements IImMgr, TupImCallback, TupUmNotify
{
    private static final String TAG = ImMgr.class.getSimpleName();

    /**
     * The user info.
     * 用户自己的信息
     */
    private PersonalContact mSelfContact;

    /**
     * Instance object of im component.
     * 获取一个ImMgr对象
     */
    private static final ImMgr mInstance = new ImMgr();

    /**
     * Self account info.
     * 用户自己的账号信息
     */
    private ImAccountInfo imAccountInfo;

    /**
     * Initial state of the user:away state.
     * 在线ON_LINE = 0，繁忙BUSY = 1，离开XA = 3，离线AWAY = 4
     */
    private ImConstant.ImStatus mImLoginStatus = ImConstant.ImStatus.AWAY;

    /**
     * The Im function object.
     * ITupIm对象
     */
    private ITupIm mTupIm;

    /**
     * The Um function object.
     * TupUm对象
     */
    private TupUm mTupUm;

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

    /**
     * A map collection of message ID and message content.
     * 已读消息，消息id和消息体的map集合
     */
    private Map<String, InstantMessage> mUnMarkReadMessageMap = new ConcurrentHashMap<>();

    /**
     * Record saved path.
     * 录音的保存路径
     */
    private String mRecordPath;


    private TUPInterfaceService baseServiceIns;

    /**
     * This is a constructor of ImMgr class.
     * 构造方法
     */
    private ImMgr()
    {
        mTupIm = TupIm.instance();
        mTupUm = TupUm.instance();
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

    /**
     * This method is used to get strangers list.
     * 获取陌生人
     * @return Return contact list
     *                返回获得的列表
     */
    @Override
    public List<PersonalContact> getStrangers()
    {
        return mTupIm.getStrangers();
    }


    private void initBaseSdk(String appPath) {
        if (this.baseServiceIns == null) {
            this.baseServiceIns = new TUPInterfaceService();

            /* start base service */
            this.baseServiceIns.StartUpService();

            /* set app path */
            this.baseServiceIns.SetAppPath(appPath);
        }

        return;
    }

    /**
     * This method is used to unit init service component.
     * 初始化业务组件
     * @param context Indicates context
     *                上下文
     */
    @Override
    public void sdkInit(Context context, String appPath)
    {
        initBaseSdk(appPath);

        //配置日志
        configPath(ImConstant.LogParam.LOG_STORAGE, ImConstant.LogParam.LOG_DIRECTORY, ImConstant.LogParam.LOG_NAME, LogLevel.DEBUG, ImConstant.LogParam.MAX_LOG_SIZE_KB);

        try
        {
            if (null == getCurPackageName(context) || "".equals(getCurPackageName(context)))
            {
                return;
            }
            //初始化im业务组件
            mTupIm.init(context, getCurPackageName(context));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        //im业务组件注册回调
        mTupIm.registerImCallback(this);

        //初始化um业务组件
        mTupUm.init(context, this, this.baseServiceIns);
    }

    /**
     * This method is used to login.
     * 登陆
     * @param imAccountInfo the account info
     *                      账号信息
     */
    public void login(ImAccountInfo imAccountInfo)
    {
        if (imAccountInfo == null)
        {
            return;
        }

        this.imAccountInfo = imAccountInfo;
        this.mMyAccount = imAccountInfo.getAccount();

        // 配置 MAA 网络信息
        mTupIm.configNetworkInfo(imAccountInfo.getMaaServer(), imAccountInfo.getMaaServerPort());

        // Config PUSH network info
        // TODO

        // IM 登陆
        String account = imAccountInfo.getAccount();
//        String password = imAccountInfo.getPassword();
        String password = imAccountInfo.getToken();
        String token = imAccountInfo.getToken();
        NetworkInfoManager.AutServerType autServerType = NetworkInfoManager.AutServerType.UPORTAL;
        TupIm.instance().login(LocContext.getContext(), account, password, autServerType, token);

    }

    /**
     * This method is used to logout.
     * 登出(注销)
     */
    public void imLogout()
    {
        //将个人状态设置为离线
        setImLoginStatus(ImConstant.ImStatus.AWAY);
        ExecuteResult result = TupIm.instance().logout(false);
        if (null != result && result.isResult() == false)
        {
            Log.e(TAG, "Im logout failed.");
        }
//        imUnInit();
//        umUnInit();

        //Clear unread message map.
        //清除未读消息的map集合
        UnreadMessageService.getInstance().clearUnreadMap();

        TupIm.instance().clearResources();
    }

    /**
     * This method is used to search contact.
     * 查询企业通讯录
     * @param condition Indicates search condition
     *                  查询条件
     * @return ExecuteResult Return execute result.
     *                       返回执行结果
     */
    public ExecuteResult searchFuzzyContact(String condition)
    {
        return searchContact(condition, true, ImConstant.SearchType.SEARCH_TYPE_FUZZY, 50, 1);
    }

    public void setSelfContact(PersonalContact contact)
    {
        this.mSelfContact = contact;
    }

    public PersonalContact getSelfContact()
    {
        return mSelfContact;
    }

    /**
     * This method is used to get head photo.
     * 获取用户头像
     * @param contacts Indicates the contacts
     *                 联系人列表
     * @return List<ViewHeadPhotoData> Return the head photo list
     *                                 返回头像数据列表
     */
    public List<ViewHeadPhotoData> getHeadPhoto(List<PersonalContact> contacts)
    {
        List<ViewHeadPhotoParam> headImageReq = new ArrayList<>();

        for (PersonalContact contact : contacts)
        {
            if (contact == null)
            {
                continue;
            }
            //If the user number is not empty and the avatar is not empty.
            if (!TextUtils.isEmpty(contact.getEspaceNumber()) && !TextUtils.isEmpty(contact.getHead()))
            {
                //Set avatar data param.
                ViewHeadPhotoParam photoParam = new ViewHeadPhotoParam();
                //头像宽、高不设置有默认值（120*120）
                /*photoParam.setH("120");
                photoParam.setW("120");*/
                photoParam.setJid(contact.getEspaceNumber());
                photoParam.setHeadId(contact.getHead());
                headImageReq.add(photoParam);
            }
        }
        if (headImageReq.size() == 0)
        {
            return null;
        }
        else
        {
            return mTupIm.getHeadPhoto(headImageReq);
        }
    }

    /**
     * This method is used to load friend and groups info.
     * 加载好友和群组信息
     */
    public void loadFriendAndGroups()
    {
        //Query myself info
        //查询自己的信息
        queryMyContactInfo();
        mTupIm.loadFriendAndGroups();
    }

    /**
     * This method is used to get friend list.
     * 获取好友列表
     * @return List<PersonalContact> Return the friends list
     *                               返回好友列表
     */
    public List<PersonalContact> getFriends()
    {
        List<PersonalContact> list = mTupIm.getFriends();
        if (list.size() == 0)
        {
            Log.i(TAG, "No queries to friends.");
        }
        return list;
    }

    /**
     * This method is used to add friend.
     * 添加好友
     * @param contact Indicates contact be added
     *                被添加为好友的联系人
     * @param teamId  Indicates the team id
     *                要添加的分组id
     * @return boolean If success return true, otherwise return false
     *                 成功返回TRUE，失败返回false
     */
    public boolean addFriend(PersonalContact contact, String teamId)
    {
        ExecuteResult result = mTupIm.addFriend(contact.getEspaceNumber(), teamId, false);
        if (result.isResult() == false)
        {
            Log.e(TAG, "Add friend failed.");
        }
        return result.isResult();
    }

    /**
     * This method is used to delete friend.
     * 从好友分组中删除好友
     * @param contact Indicates contact be deleted
     *                被删除的好友
     * @return ExecuteResult Return execute result
     *                       返回执行结果
     */
    public ExecuteResult deleteFriend(PersonalContact contact)
    {
        //要删除好友的号码、id和分组id
        String contactName = contact.getEspaceNumber();
        String contactId = contact.getContactId();
        String teamId = contact.getTeamId();
        ExecuteResult result = mTupIm.deleteFriend(contactName, teamId, contactId);
        if (result.isResult() == false)
        {
            Log.e(TAG, "Delete friend failed.");
        }
        return result;
    }

    /**
     * This method is used to get friend team list.
     * 获取好友分组
     * @return List<PersonalTeam> Return contact team list
     *                            返回好友分组列表
     */
    public List<PersonalTeam> getTeams()
    {
        List<PersonalTeam> list = mTupIm.getTeams();
        if (list.size() == 0)
        {
            Log.i(TAG, "No queries to friend teams.");
        }
        return list;
    }

    /**
     * This method is used to query myself info.
     * 查询登陆用户信息
     * @return ExecuteResult Return execute result
     *                       返回执行结果
     */
    public ExecuteResult queryMyContactInfo()
    {
        ExecuteResult result = mTupIm.searchContact(mMyAccount, false, ImConstant.SearchType.SEARCH_TYPE_NUMBER, 1, 1);
        if (result.isResult() == false)
        {
            Log.e(TAG, "Query self info failed.");
        }
        return result;
    }

    /**
     * This method is used to subscribe contact status.
     * 批量注册联系人状态
     * @param userList Indicates contact account list
     *                 联系人账号列表
     * @return ExecuteResult Return execute result
     *                       返回执行结果
     */
    public ExecuteResult subscribeState(List<String> userList)
    {
        ExecuteResult result = mTupIm.subscribeState(userList);
        if (result.isResult() == false)
        {
            Log.e(TAG, "Subscribe contact status failed.");
        }
        return result;
    }

    /**
     * This method is used to set own status.
     * 设置登陆用户的状态
     * @param status Indicates status
     *               登陆状态
     * @return ExecuteResult Return execute result
     *                       返回执行结果
     */
    public ExecuteResult setStatus(ImConstant.ImStatus status)
    {
        setImLoginStatus(status);
        ExecuteResult result = mTupIm.setStatus(status.getIndex());
        if (result.isResult() == false)
        {
            Log.e(TAG, "Set self status failed.");
        }
        return result;
    }

    public ImConstant.ImStatus getStatus()
    {
        return mImLoginStatus;
    }

    /**
     * This method is used to set own signature.
     * 设置签名
     * @param signature Indicates signature
     *                  签名内容
     * @return ExecuteResult Return execute result
     *                       返回执行结果
     */
    public ExecuteResult setSignature(String signature)
    {
        ExecuteResult result = mTupIm.setSignature(signature);
        if (result.isResult() == false)
        {
            Log.e(TAG, "Set self signature failed.");
        }
        return result;
    }

    /**
     * This method is used to get own signature.
     * 获取签名
     * @return String Return signature
     *                返回签名内容
     */
    public String getSignature()
    {
        return mTupIm.getSignature();
    }

    /**
     * This method is used to get own groups.
     * 获取群组
     * @return List<ConstGroup> Return group list
     *                          返回群组列表
     */
    public List<ConstGroup> getGroups()
    {
        return mTupIm.getGroups();
    }

    /**
     * This method is used to delete own one group.
     * 删除群组
     * @param group Indicates be deleted group
     *              要删除的群组
     * @return ExecuteResult Return execute result
     *                       返回执行结果
     */
    public ExecuteResult deleteGroup(ConstGroup group)
    {
        ExecuteResult result = mTupIm.deleteGroup(group);
        if (result.isResult() == false)
        {
            Log.e(TAG, "Delete this group failed.");
        }
        return result;
    }

    /**
     * This method is used to Leave this group.
     * 离开群组
     * @param group Indicates group object
     *              要离开的群组
     * @return ExecuteResult Return execute result
     *                       返回执行结果
     */
    public ExecuteResult leaveGroup(ConstGroup group)
    {
        ExecuteResult result = mTupIm.leaveGroup(group);
        if (result.isResult() == false)
        {
            Log.e(TAG, "Leave this group failed.");
        }
        return result;
    }

    /**
     * This method is used to get group head icon.
     * 获取群组头像
     * @param constGroup Indicates group object
     *                   想要获取头像的群组
     * @return String Return group head photo list string
     *                返回群头像列表字符串
     */
    public String getGroupHeadIcon(ConstGroup constGroup)
    {
        return mTupIm.getGroupHeadPhoto(constGroup);
    }

    /**
     * This method is used to modify this group.
     * 修改群组信息
     * @param group Indicates group object
     *              要修改的群组对象
     * @return ExecuteResult Return execute result
     *                       返回执行结果
     */
    public ExecuteResult modifyGroup(ConstGroup group)
    {
        ExecuteResult result = mTupIm.modifyGroup(group);
        if (result.isResult() == false)
        {
            Log.e(TAG, "Modify this group failed.");
        }
        return result;
    }

    /**
     * This method is used to create one discuss group.
     * 创建讨论组
     * @param members Indicates discuss group members
     *                要添加的讨论组成员列表
     * @return ExecuteResult Return execute result
     *                       返回执行结果
     */
    public ExecuteResult createDiscussGroup(List<PersonalContact> members)
    {
        ExecuteResult result = mTupIm.createDiscussGroup(members);
        if (result.isResult() == false)
        {
            Log.e(TAG, "Create this discuss group failed.");
        }
        return result;
    }

    /**
     * This method is used to invite members join this discuss group.
     * 邀请加入群组
     * @param group      Indicates discuss group object
     *                   群组对象
     * @param inviteList Indicates account list wait for inviting
     *                   待邀请者帐号列表
     * @param remark     Indicates add remark to invitation
     *                   邀请添加备注消息
     * @return ExecuteResult Return execute result
     *                       返回执行结果
     */
    public ExecuteResult inviteJoinGroup(ConstGroup group, List<String> inviteList, String remark)
    {
        ExecuteResult result = mTupIm.inviteJoinGroup(group, inviteList, remark);
        if (result.isResult() == false)
        {
            Log.e(TAG, "Invite members join this discuss group failed.");
        }
        return result;
    }

    /**
     * This method is used to kick a group member.
     * 从群组中踢出成员
     * @param constGroup Indicates group object
     *                   群组对象
     * @param account    Indicates be kicked member account
     *                   要踢出的成员账号
     * @return ExecuteResult Return execute result
     *                       返回执行结果
     */
    public ExecuteResult kickGroupMember(ConstGroup constGroup, String account)
    {
        ExecuteResult result = mTupIm.kickGroupMember(constGroup, account);
        if (result.isResult() == false)
        {
            Log.e(TAG, "Kick member from this group failed.");
        }
        return result;
    }

    /**
     * This method is used to batch kick group members by account list.
     * 批量踢出联系人
     * @param constGroup Indicates group object
     *                   群组对象
     * @param accounts   Indicates be kicked member account list
     *                   被踢出的账号列表
     * @return ExecuteResult Return execute result
     *                       返回执行结果
     */
    public ExecuteResult kickGroupMembers(ConstGroup constGroup, List<String> accounts)
    {
        ExecuteResult result = mTupIm.kickGroupMembers(constGroup, accounts);
        if (result.isResult() == false)
        {
            Log.e(TAG, "Kick members from this group failed.");
        }
        return result;
    }

    /**
     * This method is used to query this group members.
     * 查询群组成员
     * @param constGroup Indicates group object
     *                   群组对象
     * @return ExecuteResult Return execute result
     *                       返回执行结果
     */
    public ExecuteResult queryGroupMembers(ConstGroup constGroup)
    {
        ExecuteResult result = mTupIm.queryGroupMemberList(constGroup);
        if (result.isResult() == false)
        {
            Log.e(TAG, "Query members list from this group failed.");
        }
        return result;
    }

    /**
     * This method is used to get group members by id.
     * 根据id 获取本地群成员
     * @param groupId Indicates group id
     *                群组id
     * @return List<ConstGroupContact> Return group member list
     *                                 返回成员列表
     */
    public List<ConstGroupContact> getGroupMemberById(String groupId)
    {
        return mTupIm.getGroupMembers(groupId);
    }

    /**
     * This method is used to get group by id.
     * 根据id查找本地群
     * @param groupId Indicates group id
     *                群组id
     * @return ConstGroup Return group object
     *                    返回查询到的群组对象
     */
    public ConstGroup getGroupById(String groupId)
    {
        return mTupIm.findGroup(groupId);
    }

    /**
     * This method is used to transform group type.
     * Only the discussion group is currently supported for conversion to a fixed group.
     * 转化群组类型--目前移动端仅支持讨论组固化为固定群
     * @param constGroup Indicates be transformed discuss group
     *                   被固化的讨论组
     * @return ExecuteResult Return execute result.
     *                       返回执行结果
     */
    public ExecuteResult transformGroup(ConstGroup constGroup)
    {
        ExecuteResult result = mTupIm.transformGroup(constGroup);
        if (result.isResult() == false)
        {
            Log.e(TAG, "This discuss group transform fixed group failed.");
        }
        return result;
    }

    /**
     * This method is used to accept join group.
     * 接受加入固定群
     * @param data Indicates request join in group notify data object
     *             选择加入的通知消息
     * @return ExecuteResult Return execute result
     *                       返回执行结果
     */
    public ExecuteResult acceptJoinGroup(RequestJoinInGroupNotifyData data)
    {
        ExecuteResult result = mTupIm.acceptJoinGroup(data);
        if (result.isResult() == false)
        {
            Log.e(TAG, "Accept join this group failed.");
        }
        return result;
    }

    /**
     * This method is used to reject join group.
     * 拒绝加入固定群
     * @param data Indicates request join in group notify data object
     *             拒绝入群通知数据对象
     * @return ExecuteResult Return execute result
     *                       返回执行结果
     */
    public ExecuteResult rejectJoinGroup(RequestJoinInGroupNotifyData data)
    {
        ExecuteResult result = mTupIm.rejectJoinGroup(data);
        if (result.isResult() == false)
        {
            Log.e(TAG, "Reject join this group failed.");
        }
        return result;
    }

    /**
     * This method is used to find local group by id.
     * 根据id 获取群组信息
     * @param groupId Indicates group id
     *                群组id
     * @return ConstGroup Return the group info
     *                    返回群组对象
     */
    public ConstGroup getConstGroupById(String groupId)
    {
        return mTupIm.findConstGroupById(groupId);
    }

    /**
     * This method is used to load recent sessions record.
     * 获取最近会话记录
     * @return List<RecentChatContact> Return sessions record list
     *                                 返回会话记录列表
     */
    public List<RecentChatContact> loadRecentSession()
    {
        return mTupIm.loadRecentSessions();
    }

    /**
     * This method is used to delete appointed recent session record.
     * 删除最近会话记录
     * @param chatContact Indicates appointed recent session
     *                    指定的最近对话
     */
    public void deleteRecentSession(RecentChatContact chatContact)
    {
        mTupIm.deleteRecentSession(chatContact);
    }

    /**
     * This method is used to delete all recent seesion record.
     * 删除所有最近对话记录
     */
    public void deleteAllRecentSessions()
    {
        mTupIm.deleteAllRecentSessions();
    }

    /**
     * This method is used to set or cancel top message.
     * 设置取消或者置顶
     * @param recentChatContact Indicates recent chat
     *                          最近对话
     */
    public void insertRecentSession(RecentChatContact recentChatContact)
    {
        mTupIm.replace(recentChatContact);
    }

    /**
     * This method is used to get last im message.
     * 获取最新的一条消息
     * @param account Indicates account
     *                账号
     * @param msgType Indicates msg type
     *                消息类型：点对点消息； 群组消息
     * @return InstantMessage Return last im message
     *                        返回消息内容
     */
    public InstantMessage getRecentLastIm(String account, int msgType)
    {
        return mTupIm.getLastIm(account, msgType);
    }

    /**
     * This method is used to send im message.
     * 发送即时消息
     * @param toId    Indicates other side account or group id
     *                对方账号或者群组id
     * @param isGroup Indicates whether is group chat
     *                是否是群组聊天
     * @param content Indicates content sent
     *                发送的文本消息
     * @return InstantMessage Return message object
     *                        返回发送的消息体对象
     */
    public InstantMessage sendMessage(String toId, boolean isGroup, String content)
    {
        return mTupIm.sendImMessage(toId, isGroup, content);
    }

    /**
     * This method is used to send um message.
     * 发送富媒体消息
     * @param toId      Indicates other side account or group id
     *                  对方账号或者群组ID
     * @param isGroup   Indicates whether is group chat
     *                  是否为群组聊天
     * @param path      Indicates media path(picture or video)
     *                  媒体资源路径
     * @param mediaType Indicates media type
     *                  媒体类型
     * @return InstantMessage Return message object
     *                        返回发送的消息体对象
     */
    public InstantMessage sendMessage(String toId, boolean isGroup, String path, int mediaType)
    {
        return sendMessage(toId, isGroup, path, mediaType, 0);
    }

    /**
     * This method is used to send um message.
     * 发送富媒体消息
     * @param toId      Indicates other side account or group id
     *                  对方账号或者群组ID
     * @param isGroup   Indicates whether is group chat
     *                  是否为群组聊天
     * @param path      Indicates media path(picture or video)
     *                  媒体资源路径
     * @param mediaType Indicates media type
     *                  媒体类型
     * @param time      Indicates time
     *                  发送时间
     * @return InstantMessage Return message object
     *                        返回发送的消息体对象
     */
    public InstantMessage sendMessage(String toId, boolean isGroup, String path, int mediaType, int time)
    {
        ResourceGenerator generator = new ResourceGenerator(path, mediaType);
        //解析媒体资源
        MediaResource mediaResource = generator.parseMediaResource(mMyAccount, -1);
        if (mediaResource == null)
        {
            Log.e(TAG, "mediaResource is null");
            return null;
        }
        if (time > 0)
        {
            //设置发送持续时间
            mediaResource.setDuration(time);
        }
        return mTupIm.sendUmMessage(toId, isGroup, mediaResource);
    }

    /**
     * This method is used to download thumb nail.
     * 下载缩略图
     * @param message   Indicates um message object
     * @param thumbNail Indicates whether download thumb nail, appointed to picture
     *                  是否下载缩略图，针对图片使用
     * @return boolean Return whether download successful
     *                 返回是否下载成功
     */
    public boolean downloadFile(InstantMessage message, boolean thumbNail)
    {
        MediaResource resource = message.getMediaRes();
        boolean result = mTupIm.downloadFile(message, resource, thumbNail);
        if (result == false)
        {
            Log.e(TAG, "download thumb nail failed.");
        }
        return result;
    }

    /**
     * This method is used to cancel send um message.
     * 取消发送富媒体
     * @param msgId   Indicates message id
     *                消息ID
     * @param mediaId Indicates media id
     *                富媒体ID
     */
    public void cancelTransFile(long msgId, int mediaId)
    {
        mTupIm.cancelTransFile(msgId, mediaId);
    }

    public String getRecordPath()
    {
        return mRecordPath;
    }

    /**
     * This method is used to get record path.
     * 获取录音文件保存路径
     * @return String Return path
     *                返回路径
     */
    public String getPath()
    {
        //获取音频文件编解码
        String codec = ContactLogic.getIns().getMyOtherInfo().getUmVoiceCodecs();

        //通过编解码获取音频格式
        if ("AMR-WB".equals(codec))
        {
            mRecordPath = UmUtil.createTempResPath(UmConstant.AMR);
        }
        else
        {
            mRecordPath = UmUtil.createTempResPath(UmConstant.WAV);
        }
        return mRecordPath;
    }

    /**
     * This method is used to start record.
     * 开始录音
     * @return int If success return 0, otherwise return corresponding error code
     *             成功返回0，失败返回相应的错误码
     */
    public int startRecord()
    {
        mRecordPath = "";
        int result = mTupUm.startRecord(getPath());
        if (result != 0)
        {
            Log.e(TAG, "Start record failed.");
        }
        return result;
    }

    /**
     * This method is used to stop record.
     * 停止录音
     * @return int If success return 0, otherwise return corresponding error code
     *             成功返回0，失败返回相应的错误码
     */
    public int stopRecord()
    {
        int result = mTupUm.stopRecord();
        if (result != 0)
        {
            Log.e(TAG, "Stop record failed.");
        }
        return result;
    }

    /**
     * This method is used to get micro volume.
     * 获取麦克风音量大小
     * @return int Return micro volume
     *             返回麦克风音量
     */
    public int getVolume()
    {
        return mTupUm.getMicroVolume();
    }

    /**
     * This method is used to start playing record.
     * 开始播放
     * @param path Indicates resource path
     *             媒体资源存放路径
     * @param loop Indicates whether loop playing, 0 means yes, more than 0 means no
     *             是否循环播放，0循环，大于0不循环
     * @return int If success return 0, otherwise return corresponding error code
     *             成功返回0，失败返回相应的错误码
     */
    public int startPlay(String path, int loop)
    {
        if (TextUtils.isEmpty(path))
        {
            Log.e(TAG, "audio path is null");
            return -1;
        }
        int result = mTupUm.startPlay(path, loop);
        if (result != 0)
        {
            Log.e(TAG, "startPlay result = " + result);
        }
        return result;
    }

    /**
     * This method is used to stop play record.
     * 停止播放录音
     * @param handle Indicates play handle
     *               播放句柄
     * @return int If success return 0, otherwise return corresponding error code
     *             成功返回0，失败返回相应的错误码
     */
    public int stopPlay(int handle)
    {
        int result = mTupUm.stopPlay(handle);
        Log.i(TAG, "stopPlay param = " + handle + ", result = " + result);
        return result;
    }

    /**
     * This method is used to query history message.
     * 查询历史消息
     * @param msgType  Indicates message type
     *                 希望获取的消息类型，1：按联系人取IM消息；2：按固定群取；
     * @param targetId Indicates target id for login eSpace account or fixed group, rely on msgType
     *                 所获取的用户的登录帐号或固定群ID，依赖msgType
     * @param msgId    Indicates message start id
     *                 消息起始id
     * @param number   Indicates request number
     *                 请求数量，默认10条
     * @return ExecuteResult Return execute result
     *                       返回执行结果
     */
    public ExecuteResult queryHistoryMessage(int msgType, String targetId, String msgId, int number)
    {
        ExecuteResult result =  mTupIm.queryHistoryMessage(msgType, targetId, msgId, number);
        if (result.isResult() == false)
        {
            Log.e(TAG, "Query history message failed.");
        }
        return result;
    }

    /**
     * This method is used to delete message.
     * 删除消息
     * @param msgType   Indicates msg type
     *                  1: 普通IM消息 2: 固定群消息 3: 讨论组消息
     * @param targetId  Indicates target id
     *                  msgType为1时填对方的eSpace 账号，msgType为2时填群组ID
     * @param type      Indicates delete type
     *                  0: 按条删除 1: 删除所有记录
     * @param messageId Indicates message id
     *                  消息id
     * @param idList    Indicates message id list
     *                  消息id列表
     */
    public void deleteMessage(int msgType, String targetId, short type, String messageId, long idList)
    {
        List<String> msgList = new ArrayList<>();
        msgList.add(messageId);

        List<String> msgLocalIdList = new ArrayList<>();
        msgLocalIdList.add(String.valueOf(idList));
        mTupIm.deleteMessage(msgType, targetId, type, msgList, msgLocalIdList);
    }

    /**
     * This method is used to delete message.
     * 删除消息
     * @param msgType   Indicates msg type
     *                  1: 普通IM消息 2: 固定群消息 3: 讨论组消息
     * @param targetId  Indicates target id
     *                  msgType为1时填对方的eSpace 账号，msgType为2时填群组ID
     * @param type      Indicates delete type
     *                  0: 按条删除 1: 删除所有记录
     * @param msgList   Indicates message id list
     *                  消息message id列表（用于删除服务器消息）
     * @param idList    Indicates message id list
     *                  消息id列表
     */
    public void deleteAllMessages(int msgType, String targetId, short type, List<String> msgList, List<String> idList)
    {
        mTupIm.deleteMessage(msgType, targetId, type, msgList, idList);
    }

    /**
     * This method is used to mark message read.
     * 标记消息已读
     * @param id       Indicates message id
     *                 填IM的ID或固定群消息的ID，如果是6或其他值，填对应消息的消息ID
     * @param to       the to
     *                 markType = 1或msgTag = 0填写对方的登录帐号；markType = 2或msgTag = 1填写群组ID； markType = 6或其它值，填写登录用户的帐号（此时和from中的值一致）
     * @param markType Indicates mark type
     *                 1: IM 2: 固定群  6: 其它消息
     * @param msgTag   Indicates msg tag
     *                 0：点对点IM消息 1：固定群/讨论组IM消息  2：短信 3：系统公告  4：其他
     * @return ExecuteResult Return execute result
     *                       返回执行结果
     */
    public ExecuteResult markRead(String id, String to, ImConstant.GroupMark markType, ImConstant.GroupTag msgTag)
    {
        int type = 0;
        switch (markType)
        {
            //其它消息（系统公告、部门通知、固定群邀请、固定群邀请回复通知、固定群主动加入申请、固定群主动加入申请回复通知、UMS传真通知、好友提示）
            case GROUP_MARK_TYPE:
                type = 6;
                break;
            //点对点消息(im消息)
            case GROUP_MARK_TYPE_IM:
                type = 1;
                break;
            //固定群
            case GROUP_MARK_TYPE_FIXED:
                type = 2;
                break;
            default:
                break;
        }

        int tag = 0;
        switch (msgTag)
        {
            case GROUP_MARK_NOTIFY_TAG_IM:
                tag = 0;
                break;
            case GROUP_MARK_NOTIFY_TAG_GROUP:
                tag = 1;
                break;
            //4：其他（P2P文件传输，好友邀请，群组邀请，群组通知）
            case GROUP_MARK_NOTIFY_TAG:
                tag = 4;
                break;
            default:
                break;
        }
        ExecuteResult result = mTupIm.markRead(id, to, type, tag);
        if (result.isResult() == false)
        {
            Log.e(TAG, "Mark message read failed.");
        }
        return result;
    }

    /**
     * This method is used to mark read.
     * @param message  Indicates message object
     *                 标记为已读的消息对象
     * @param chatId   Indicates chat id
     *                 消息id
     * @param chatType Indicates chat type
     *                 消息类型
     */
    public void proMarkRead(InstantMessage message, String chatId, int chatType)
    {
        //遍历消息集合
        Iterator<Map.Entry<String, InstantMessage>> iterator = mUnMarkReadMessageMap.entrySet().iterator();
        while (iterator.hasNext())
        {
            Map.Entry<String, InstantMessage> entry = iterator.next();
            InstantMessage instantMessage = entry.getValue();
            if (message.getMessageId().equals(instantMessage.getMessageId()))
            {
                //设置消息已读
                markRead(instantMessage.getMessageId(), chatId, getMarkType(chatType), getMarkTag(chatType));
                //如果设置消息已读，则该消息id从消息map中删除
                mUnMarkReadMessageMap.remove(entry.getKey());
                //点对点消息
                if (chatType == RecentChatContact.ESPACECHATTER)
                {
                    UnreadMessageService.getInstance().clearUnreadMessageAccount(instantMessage.getFromId());
                }
                //群组消息
                else if (chatType == RecentChatContact.DISCUSSIONCHATTER || chatType == RecentChatContact.GROUPCHATTER)
                {
                    UnreadMessageService.getInstance().clearUnreadMessageAccount(instantMessage.getToId());
                }
            }
        }
    }

    /**
     * This method is used to get mark type.
     * 获取标记类型
     * @param msgType Indicates msg type
     *                消息类型
     * @return Return mark type
     *         返回标记类型
     */
    public ImConstant.GroupMark getMarkType(int msgType)
    {
        return RecentChatContact.ESPACECHATTER == msgType ? ImConstant.GroupMark.GROUP_MARK_TYPE_IM : ImConstant.GroupMark.GROUP_MARK_TYPE_FIXED;
    }

    /**
     * This method is used to get mark tag.
     * 获取标记的标签
     * @param msgType Indicates msg type
     *                消息类型
     * @return Return the mark tag
     *         返回标签类型
     */
    public ImConstant.GroupTag getMarkTag(int msgType)
    {
        return RecentChatContact.ESPACECHATTER == msgType ? ImConstant.GroupTag.GROUP_MARK_NOTIFY_TAG_IM : ImConstant.GroupTag.GROUP_MARK_NOTIFY_TAG_GROUP;
    }

    /**
     * This method is used to save self info.
     * 保存登陆用户自己的信息
     * @param searchContactResult Indicates list of contacts that are queried
     *                            查询到的联系人列表
     */
    private void saveSelfContact(List<PersonalContact> searchContactResult)
    {
        for (PersonalContact contact : searchContactResult)
        {
            if (mMyAccount.equals(contact.getEspaceNumber()))
            {
                setSelfContact(contact);
                break;
            }
        }
    }

    /**
     * This method is used to search contact.
     * 查询联系人
     * @param condition  Indicates search condition
     *                   查询条件
     * @param showStatus Indicates show status of search result
     *                   查询结果是否带状态
     * @param queryField Indicates query field,precise query only can appointed to be this three field: eSpaceNumber,bindNo,staffNo
     *                   if value is null or not this three field then do vague search
     *                   查询类型：精确查询仅能指定如下三个字段：eSpaceNumber,bindNo,staffNo三个字段；内容值为空或不是约定的那三个字段，则进行模糊查找；
     * @param pageCount  Indicates search result display in each page
     *                   查询结果每页显示的数量
     * @param pageNo     Indicates current search request page number
     *                   查询请求显示第几页的内容的页码
     * @return ExecuteResult Return execute result
     *                       查询结果
     */
    private ExecuteResult searchContact(String condition, boolean showStatus, String queryField, int pageCount,
                                        int pageNo)
    {
        ExecuteResult result = mTupIm.searchContact(condition, showStatus, queryField, pageCount, pageNo);
        if (result.isResult() == false)
        {
            Log.e(TAG, "Search contacts failed.");
        }
        return result;
    }

    /**
     * This method is used to uninit um service component.
     * 去初始化业务组件
     * @return int Return method invoking result
     *             返回接口调用结果，成功返回0，失败返回相应的错误码
     */
    private int umUnInit()
    {
        return mTupUm.uninit();
    }

    /**
     * This method is used to uninit im service component.
     * 去初始化业务组件
     * @return int Return method invoking result
     *             返回接口调用结果，成功返回0，失败返回相应的错误码
     */
    private void imUnInit()
    {
        mTupIm.unInit();
    }

    /**
     * This method is used to config log and application file path.
     * 日志配置和应用文件路径配置
     * @param storage   Indicates file root route
     *                  文件根路径
     * @param directory Indicates log path
     *                  日志路径
     * @param logName   Indicates log file name
     *                  日志文件名
     * @param logLevel  Indicates log level
     *                  日志级别
     * @param maxSize   Indicates the maximum size(KB) of a log file
     *                  单个日志文件的最大值
     */
    private void configPath(String storage, String directory, String logName, LogLevel logLevel, int maxSize)
    {
        //应用文件路径配置
        mTupIm.configStorage(storage);
        //配置日志
        mTupIm.configLog(directory, logName, logLevel, maxSize);
    }

    /**
     * This method is used to get current package name.
     * 获取当前组件名称
     * @param context context
     *                上下文
     * @return String Return package name
     *                返回组件名称
     * @throws Exception
     */
    private String getCurPackageName(Context context) throws Exception
    {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = activityManager.getRunningTasks(1);
        if (null == taskInfo)
        {
            Log.e(TAG, "-->the taskInfo is null");
            return null;
        }
        ComponentName componentName = taskInfo.get(0).topActivity;
        return componentName.getPackageName();
    }

    private void setImLoginStatus(ImConstant.ImStatus status)
    {
        this.mImLoginStatus = status;
    }

    /**
     * This method is used to handle send to message.
     * 处理发来的消息
     * @param messages Indicates message list
     *                 消息列表
     */
    private void handleIncomingMessage(List<InstantMessage> messages)
    {
        for (InstantMessage message : messages)
        {
            //将消息放到map集合中
            mUnMarkReadMessageMap.put(message.getMessageId(), message);

            if (message.getMsgType() == RecentChatContact.ESPACECHATTER)
            {
                addRecentContactMessage(message.getFromId(), message);

                int number = UnreadMessageService.getInstance().getUnreadMessageCountByAccount(message.getFromId());
                UnreadMessageService.getInstance().saveUnreadMessage(message.getFromId(), number + 1);

            }
            else if (message.getMsgType() == RecentChatContact.DISCUSSIONCHATTER || message.getMsgType() == RecentChatContact.GROUPCHATTER)
            {
                addRecentContactMessage(message.getToId(), message);

                //Send yourself（PC group sent, mobile phone received）
                if (!mMyAccount.equals(message.getFromId()))
                {
                    //Group save getToId()  (toId is GroupId)
                    int number = UnreadMessageService.getInstance().getUnreadMessageCountByAccount(message.getToId());
                    UnreadMessageService.getInstance().saveUnreadMessage(message.getToId(), number + 1);
                }
            }
            mNotification.onRefreshRecentSession();
        }

        for (InstantMessage message : messages)
        {
            //Send yourself（PC group sent, mobile phone received）
//            if (!mMyAccount.equals(message.getFromId()))
//            {
//                int number = UnreadMessageService.getInstance().getUnreadMessageCountByAccount(message.getFromId());
//                UnreadMessageService.getInstance().saveUnreadMessage(message.getFromId(), number + 1);
//            }
        }
    }

    /**
     * This method is used to add recent contact message.
     * 添加最近联系人聊天记录
     * @param chatId  Indicates chat id
     *                会话id
     * @param message Indicates message object
     *                消息内容
     */
    private void addRecentContactMessage(String chatId, InstantMessage message)
    {
        RecentChatContact chatContact = getRecentChatContact(chatId, message.getMsgType(), message.getNickname());
        chatContact.setEndTime(message.getTime());
        insertRecentSession(chatContact);
    }

    /**
     * This method is used to get recent chat contact.
     * 获取最近联系人聊天记录
     * @param chatId   Indicates chat id
     *                 会话id
     * @param msgType  Indicates message type
     *                 最近会话对象类型，包括个人，固定群，讨论组
     * @param nickName Indicates nike name
     *                 昵称
     * @return RecentChatContact Return chat contact object.
     *                           返回最近对话聊天联系人的数据结构
     */
    private static RecentChatContact getRecentChatContact(String chatId, int msgType, String nickName)
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

    /**
     * This method is used to notify login success.
     * 登陆成功响应
     */
    @Override
    public void onLoginSuccess()
    {
        Log.i(TAG, "-->onLoginSuccess");
        setImLoginStatus(ImConstant.ImStatus.ON_LINE);
        searchFuzzyContact(mMyAccount);
        mNotification.onImLoginSuccess();
    }

    /**
     * This method is used to notify login error.
     * 登陆失败响应
     * @param loginErrorResp Indicates login error result
     *                       登录异常结果
     * @param error          Indicates error code
     *                       返回失败错误码
     */
    @Override
    public void onLoginError(LoginErrorResp loginErrorResp, int error)
    {
        Log.i(TAG, "-->onImLoginError");
        //通用错误
        if (error == ResponseCodeHandler.ResponseCode.COMMON_ERROR.ordinal())
        {
            Log.i(TAG, "im Login error : COMMON_ERROR");
        }
        //会话不存在，长时间与服务器失去连接，需要重新登录
        else if (error == ResponseCodeHandler.ResponseCode.SESSION_TIMEOUT.ordinal())
        {
            Log.i(TAG, "im Login error : SESSION_TIMEOUT");
        }
        //一般的链接错误，使用此值
        else if (error == LoginError.CONNECT_ERROR.ordinal())
        {
            Log.i(TAG, "im Login error : CONNECT_ERROR");
            mNotification.onImLoginError();
        }
        setImLoginStatus(ImConstant.ImStatus.AWAY);
    }

    /**
     * This method is used to notify be kicked off.
     * 被抢登陆通知
     */
    @Override
    public void onKickOffNotify()
    {
        Log.i(TAG, "-->onKickOffNotify");
        mNotification.onImKickOffNotify();
    }

    /**
     * This method is used to notify multi terminal login.
     * 多终端登陆响应
     * @param i               Indicates response result, 0:failed; 1:success
     *                        响应结果 0：失败；1：成功
     * @param loginDeviceResp Indicates response of multi terminal login
     *                        多终端登陆响应数据结构
     */
    @Override
    public void onMultiTerminalNotify(int i, LoginDeviceResp loginDeviceResp)
    {
        Log.i(TAG, "-->onMultiTerminalNotify");
        //Multi Terminal login
        if (i == 1)
        {
            Log.i(TAG, "onMultiTerminalNotify loginDeviceResp = " + loginDeviceResp.getStatus());
            mNotification.onImMultiTerminalNotify();
        }
    }

    /**
     * This method is used to synchronize contact.
     * 全量同步、增量同步响应
     * @param i Indicates response result, 0:failed; 1:success
     *          响应结果 0：失败；1：成功
     */
    @Override
    public void onSynchronizeContact(int i)
    {
        Log.i(TAG, "-->onSynchronizeContact i = " + i);
    }

    /**
     * This method is used to notify friend status change.
     * 订阅后好友状态变更推送【通知】
     * @param i    Indicates response result, 0:failed; 1:success
     *             响应结果 0：失败；1：成功
     * @param i1   Indicates change type; 1:state change; 2:attribute change;
     *             变更类型：1：好友状态变更；2：好友属性变更；
     * @param list Indicates changed friends account
     *             变更的好友账号
     */
    @Override
    public void onFriendChangeNotify(int i, int i1, List<String> list)
    {
        Log.i(TAG, "-->onFriendChangeNotify");
        if (i == 0)
        {
            Log.e(TAG, "onFriendChangeNotify failed");
            return;
        }
        if (list != null && list.size() > 0)
        {
            mNotification.onFriendStateChanged(list);
        }
    }

    /**
     * This method is used to notify set head photo result.
     * 设置头像【响应】
     * @param i                   Indicates response result, 0:failed; 1:success
     *                            响应结果 0：失败；1：成功
     * @param uploadHeadPhotoResp Indicates upload head photo object
     *                            上传头像的响应数据结构
     */
    @Override
    public void onSetHeadPhoto(int i, UploadHeadPhotoResp uploadHeadPhotoResp)
    {
        Log.i(TAG, "-->onSetHeadPhoto");
        LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_IM_SET_HEAD_PHOTO, uploadHeadPhotoResp);
    }

    /**
     * This method is used to notify set state response.
     * 设置状态【响应】
     * @param i                Indicates response result, 0:failed; 1:success
     *                         响应结果 0：失败；1：成功
     * @param baseResponseData Indicates set state response
     *                         设置状态响应数据结构
     */
    @Override
    public void onSetState(int i, BaseResponseData baseResponseData)
    {
        Log.i(TAG, "-->onSetState");
        if (baseResponseData == null)
        {
            return;
        }
        if (baseResponseData.getStatus() == ResponseCodeHandler.ResponseCode.REQUEST_SUCCESS)
        {
            mNotification.onSetStatusSuccess();
        }
    }

    /**
     * This method is used to notify set signature result.
     * 设置签名【响应】
     * @param result           Indicates response result, 0:failed; 1:success
     *                         响应结果 0：失败；1：成功
     * @param baseResponseData Indicates set signature response.
     *                         回调数据结构
     */
    @Override
    public void onSetSignature(int result, BaseResponseData baseResponseData)
    {
        Log.i(TAG, "-->onSetSignatureSuccess");
        if (baseResponseData == null)
        {
            return;
        }
        if (baseResponseData.getStatus() == ResponseCodeHandler.ResponseCode.REQUEST_SUCCESS)
        {
            mNotification.onSetSignatureSuccess(result);
        }
    }

    /**
     * This method is used to notify add friend response.
     * 添加好友【响应】
     * @param i             Indicates response result, 0:failed; 1:success
     *                      响应结果 0：失败；1：成功
     * @param addFriendResp Indicates response data struct
     *                      添加结果的数据结构
     */
    @Override
    public void onAddFriend(int i, AddFriendResp addFriendResp)
    {
        Log.i(TAG, "-->onAddFriend = " + addFriendResp.toString());
        mNotification.onAddContactResult(addFriendResp);
    }

    /**
     * This method is used to notify add friend result.
     * 好友添加成功推送【通知】
     * @param i                  Indicates response result, 0:failed; 1:success
     *                           响应结果 0：失败；1：成功
     * @param presenceNotifyData Indicates response data struct
     */
    @Override
    public void onAddFriendNotify(int i, PresenceNotifyData presenceNotifyData)
    {
        Log.i(TAG, "-->onAddFriendNotify = " + presenceNotifyData.toString());
    }

    /**
     * This method is used to notify add friend success response(not supported in produce).
     * 好友添加成功推送【通知】(暂不支持)
     * @param i                  Indicates response result, 0:failed; 1:success
     * @param presenceNotifyData Indicates response data struct
     */
    @Override
    public void onAddFriendSuccessNotify(int i, PresenceNotifyData presenceNotifyData)
    {
        Log.i(TAG, "-->onAddFriendSuccessNotify");
        if (presenceNotifyData.getStatus() == ResponseCodeHandler.ResponseCode.REQUEST_SUCCESS)
        {
            Log.i(TAG, "onAddFriendSuccessNotify = " + presenceNotifyData.toString());
            mNotification.onRefreshFriendsOrGroups();
        }
        else
        {
            String errorDescribe = presenceNotifyData.getDesc();
            if (TextUtils.isEmpty(errorDescribe))
            {
                errorDescribe = "add friend error";
            }
            mNotification.onCommonErrorNotify(errorDescribe);
            Log.e(TAG, "add friend error");
        }
    }

    /**
     * This method is used to notify add friend fail response(not supported in produce).
     * 好友添加失败推送【通知】(暂不支持)
     * @param i                  Indicates response result, 0:failed; 1:success
     * @param presenceNotifyData Indicates response data struct
     */
    @Override
    public void onAddFriendFailNotify(int i, PresenceNotifyData presenceNotifyData)
    {
        Log.i(TAG, "-->onAddFriendFailNotify = " + presenceNotifyData.toString());
    }

    /**
     * This method is used to notify delete friend response.
     * 删除好友【响应】
     * @param i                Indicates response result, 0:failed; 1:success
     *                         响应结果 0：失败；1：成功
     * @param baseResponseData Indicates response data struct
     *                         删除结果的数据结构
     */
    @Override
    public void onDeleteFriend(int i, BaseResponseData baseResponseData)
    {
        if (baseResponseData == null)
        {
            return;
        }
        Log.i(TAG, "-->onDeleteFriend:" + baseResponseData.getStatus());

        mNotification.onDeleteContactResult(baseResponseData);
        if (baseResponseData.getStatus() == ResponseCodeHandler.ResponseCode.REQUEST_SUCCESS)
        {
            mNotification.onRefreshFriendsOrGroups();
            Log.i(TAG, "onDeleteFriend = " + baseResponseData.toString());
        }
        else if (baseResponseData.getStatus() == ResponseCodeHandler.ResponseCode.COMMON_ERROR)
        {
            String errorDescribe = baseResponseData.getDesc();
            if (TextUtils.isEmpty(errorDescribe))
            {
                errorDescribe = "delete friend error";
            }
            mNotification.onCommonErrorNotify(errorDescribe);
        }
    }

    /**
     * This method is used to notify delete friend result(not supported in produce).
     * 好友删除推送【通知】(暂不支持)
     * @param i                  Indicates response result, 0:failed; 1:success
     * @param presenceNotifyData Indicates response data struct
     */
    @Override
    public void onDeleteFriendNotify(int i, PresenceNotifyData presenceNotifyData)
    {
        Log.i(TAG, "-->onDeleteFriendNotify = " + presenceNotifyData.toString());
        //LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_IM_DELETE_CONTACT_RESULT, deleteContactResult);
    }

    /**
     * This method is used to notify create group result.
     * 创建群组【响应】
     * @param i               Indicates response result, 0:failed; 1:success
     *                        响应结果 0：失败；1：成功
     * @param manageGroupResp Indicates response data struct
     *                        创建结果的数据结构
     */
    @Override
    public void onCreateGroup(int i, ManageGroupResp manageGroupResp)
    {
        Log.i(TAG, "-->onCreateGroup");
        LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_CREATE_GROUP_RESULT, manageGroupResp);
        mNotification.onRefreshFriendsOrGroups();
    }

    /**
     * This method is used to notify delete group result.
     * 删除群组【响应】
     * @param i               Indicates response result, 0:failed; 1:success.
     *                        响应结果 0：失败；1：成功
     * @param manageGroupResp Indicates response data struct
     *                        删除结果的数据结构
     */
    @Override
    public void onDeleteGroup(int i, ManageGroupResp manageGroupResp)
    {
        Log.i(TAG, "-->onDeleteGroup");
        if (manageGroupResp == null)
        {
            Log.e(TAG, "onDeleteGroup manageGroupResp = null");
            return;
        }
        if (manageGroupResp.getStatus() == ResponseCodeHandler.ResponseCode.REQUEST_SUCCESS)
        {
            Log.i(TAG, "onDeleteGroup = " + manageGroupResp.toString());
            mNotification.onRefreshFriendsOrGroups();
        }
        else
        {
            String errorDescribe = manageGroupResp.getDesc();
            if (TextUtils.isEmpty(errorDescribe))
            {
                errorDescribe = "delete group error";
            }
            mNotification.onCommonErrorNotify(errorDescribe);
        }
    }

    /**
     * This method is used to notify transfer group result.
     * 转让群组权限【响应】
     * @param i               Indicates response result, 0:failed; 1:success
     *                        响应结果 0：失败；1：成功
     * @param manageGroupResp Indicates response data struct
     *                        转让结果的数据结构
     */
    @Override
    public void onTransferGroup(int i, ManageGroupResp manageGroupResp)
    {
        Log.i(TAG, "-->onTransferGroup = " + manageGroupResp.toString());
    }

    /**
     * This method is used to notify modify group result.
     * 修改群组【响应】
     * @param i               Indicates response result, 0:failed; 1:success
     *                        响应结果 0：失败；1：成功
     * @param manageGroupResp Indicates response data struct
     *                        修改结果的数据结构
     */
    @Override
    public void onModifyGroup(int i, ManageGroupResp manageGroupResp)
    {
        Log.i(TAG, "-->onModifyGroup");
        if (manageGroupResp == null)
        {
            Log.e(TAG, "onModifyGroup manageGroupResp = null");
            return;
        }
        if (manageGroupResp.getStatus() == ResponseCodeHandler.ResponseCode.TERMINAL_REQUEST_ERROR)
        {
            String errorDescribe = manageGroupResp.getDesc();
            if (TextUtils.isEmpty(errorDescribe))
            {
                errorDescribe = "modify group error";
            }
            mNotification.onCommonErrorNotify(errorDescribe);
            Log.e(TAG, "modify group error");
        }
//        if (manageGroupResp.getStatus() == ResponseCodeHandler.ResponseCode.REQUEST_SUCCESS)
//        {
//            LogUtil.i(Constant.DEMO_TAG, "onModifyGroup = " + manageGroupResp.toString());
//            mNotification.onModifyGroupSuccess(manageGroupResp.getGroupId());
//        }
//        else
//        {
//            String errorDescribe = manageGroupResp.getDesc();
//            if (TextUtils.isEmpty(errorDescribe))
//            {
//                errorDescribe = "modify group error";
//            }
//            mNotification.onCommonErrorNotify(errorDescribe);
//            LogUtil.e(Constant.DEMO_TAG, "modify group error");
//        }
    }

    /**
     * This method is used to notify member leave group result.
     * 群成员离开群组【响应】
     * @param i              Indicates response result, 0:failed; 1:success
     *                       响应结果 0：失败；1：成功
     * @param leaveGroupResp Indicates response data struct
     *                       离开群组结果的数据结构
     */
    @Override
    public void onLeaveGroup(int i, LeaveGroupResp leaveGroupResp)
    {
        Log.i(TAG, "-->onLeaveGroup");
        if (ResponseCodeHandler.ResponseCode.REQUEST_SUCCESS == leaveGroupResp.getStatus())
        {
            mNotification.onLeaveGroupSuccess();
        }else {
            mNotification.onLeaveGroupFail();
        }
    }

    /**
     * This method is used to notify search contact result.
     * 查询联系人【响应】
     * @param i                  Indicates response result, 0:failed; 1:success
     *                           响应结果 0：失败；1：成功
     * @param searchContactsResp Indicates response data struct
     *                           查询结果的数据结构
     */
    @Override
    public void onSearchContact(int i, SearchContactsResp searchContactsResp)
    {
        Log.i(TAG, "-->onSearchContact");
        if (searchContactsResp == null || searchContactsResp.getContacts() == null)
        {
            return;
        }
        List<PersonalContact> searchContactResult = searchContactsResp.getContacts();
        //判断查询的是否为自己
        boolean isMyself = searchContactResult.size() == 1 && searchContactResult.get(0).getEspaceNumber().equals(mMyAccount);
        if (isMyself)
        {
            //保存自己的信息
            saveSelfContact(searchContactResult);
        }
        else
        {
            mNotification.onSearchContactResult(searchContactResult);
        }
    }

    /**
     * This method is used to notify query group member result.
     * 查询群成员【响应】
     * @param i                             Indicates response result, 0:failed; 1:success
     *                                      响应结果 0：失败；1：成功
     * @param queryGroupMembersResponseData Indicates response data struct
     *                                      查询结果的数据结构
     */
    @Override
    public void onQueryGroupMemberList(int i, QueryGroupMembersResponseData queryGroupMembersResponseData)
    {
        Log.i(TAG, "-->onQueryGroupMemberList");
        if (queryGroupMembersResponseData.getStatus() == ResponseCodeHandler.ResponseCode.REQUEST_SUCCESS)
        {
            mNotification.onQueryGroupMemberSuccess();
        }
    }

    /**
     * This method is used to notify group info update result.
     * 群组信息更新推送【通知】
     * @param i                     Indicates response result, 0:failed; 1:success
     *                              响应结果 0：失败；1：成功
     * @param groupChangeNotifyData Indicates response data struct
     *                              群组信息更新的数据结构
     */
    @Override
    public void onGroupUpdateNotify(int i, GroupChangeNotifyData groupChangeNotifyData)
    {
        Log.i(TAG, "-->onGroupUpdateNotify");
        mNotification.onRefreshFriendsOrGroups();
        //群组信息更新成功
        if (groupChangeNotifyData.getStatus() == ResponseCodeHandler.ResponseCode.REQUEST_SUCCESS)
        {
            Log.i(TAG, "onModifyGroup = " + groupChangeNotifyData.toString());
            mNotification.onModifyGroupSuccess(groupChangeNotifyData.getGroupId());
        }
        //群组信息更新失败
        else
        {
            String errorDescribe = groupChangeNotifyData.getDesc();
            if (TextUtils.isEmpty(errorDescribe))
            {
                errorDescribe = "modify group error";
            }
            mNotification.onCommonErrorNotify(errorDescribe);
            Log.e(TAG, "modify group error");
        }
    }

    /**
     * This method is used to notify group dismiss result.
     * 群组被解散推送【通知】
     * @param i                     Indicates response result, 0:failed; 1:success
     *                              响应结果 0：失败；1：成功
     * @param groupChangeNotifyData Indicates dismiss group date
     *                              群组信息更新的数据结构
     */
    @Override
    public void onGroupDismissNotify(int i, GroupChangeNotifyData groupChangeNotifyData)
    {
        Log.i(TAG, "-->onGroupDismissNotify");
        mNotification.onRefreshFriendsOrGroups();

        if (groupChangeNotifyData == null)
        {
            Log.e(TAG, "GroupChangeNotifyData is null");
            return;
        }
        markRead("0", mMyAccount, ImConstant.GroupMark.GROUP_MARK_TYPE, ImConstant.GroupTag.GROUP_MARK_NOTIFY_TAG);
    }

    /**
     * This method is used to notify group member change result.
     * 群成员变更推送【通知】
     * @param i                            Indicates response result, 0:failed; 1:success
     *                                     响应结果 0：失败；1：成功
     * @param groupMemberChangedNotifyData Indicates member change date
     *                                     群组成员变更的数据结构
     */
    @Override
    public void onGroupMemberChangeNotify(int i, GroupMemberChangedNotifyData groupMemberChangedNotifyData)
    {
        Log.i(TAG, "-->onGroupMemberChangeNotify");
        mNotification.onRefreshFriendsOrGroups();
    }

    /**
     * This method is used to notify mark message read result.
     * 消息置已读推送【通知】
     * @param i                         Indicates response result, 0:failed; 1:success
     *                                  响应结果 0：失败；1：成功
     * @param markReadMessageNotifyData Indicates make read date
     *                                  消息设置已读的数据结构
     */
    @Override
    public void onMarkReadMessageNotify(int i, MarkReadMessageNotifyData markReadMessageNotifyData)
    {
        Log.i(TAG, "-->onMarkReadMessageNotify = " + markReadMessageNotifyData.toString());
        String messageId = markReadMessageNotifyData.getMessageId();
        mUnMarkReadMessageMap.remove(messageId);
    }

    /**
     * This method is used to notify unread message response.
     * 未读消息推送【通知】
     * @param i    Indicates response result, 0:failed; 1:success
     *             响应结果 0：失败；1：成功
     * @param list Indicates unread message list
     *             消息未读的数据结构
     */
    @Override
    public void onUnreadMessageNotify(int i, List<InstantMessage> list)
    {
        if (list == null || list.size() <= 0)
        {
            Log.e(TAG, "onUnreadMessageNotify list is null or size = 0");
            return;
        }
        Log.i(TAG, "-->onUnreadMessageNotify size = " + list.size());
        //处理未读消息
        handleIncomingMessage(list);
        mNotification.onRefreshUnreadMessage();
    }

    /**
     * This method is used to notify query message history result.
     * 查询漫游消息记录【响应】
     * @param i    Indicates response result, 0:failed; 1:success
     *             响应结果 0：失败；1：成功
     * @param list Indicates query history message list
     *             漫游消息列表
     */
    @Override
    public void onQueryMessageHistory(int i, List<InstantMessage> list)
    {
        if (list == null)
        {
            Log.e(TAG, "onQueryMessageHistory InstantMessage list is null");
            return;
        }
        Log.i(TAG, "-->onQueryMessageHistory size = " + list.size());
        if (list.size() > 0)
        {
            mNotification.onQueryHistoryMessagesSuccess(list);
        }
    }

    /**
     * This method is used to notify receive message result.
     * 接收消息，消息推送【通知】
     * @param i    Indicates response result, 0:failed; 1:success
     *             响应结果 0：失败；1：成功
     * @param list Indicates message list
     *             接收到的消息列表
     */
    @Override
    public void onReceiveMessage(int i, List<InstantMessage> list)
    {
        if (list == null)
        {
            Log.e(TAG, "onReceiveMessage InstantMessage list is null");
            return;
        }
        Log.i(TAG, "-->onReceiveMessage InstantMessages size = " + list.size());
        handleIncomingMessage(list);
        mNotification.onReceiveMessages(list);
        mNotification.onRefreshUnreadMessage();
    }

    /**
     * This method is used to notify mark read message.
     * 消息设置已读【响应】
     * @param i                Indicates response result, 0:failed; 1:success
     *                         响应结果 0：失败；1：成功
     * @param baseResponseData Indicates response date
     *                         设置已读响应的数据结构
     */
    @Override
    public void onMarkReadMessage(int i, BaseResponseData baseResponseData)
    {
        Log.i(TAG, "-->onMarkReadMessage = " + baseResponseData.toString());
    }

    /**
     * This method is used to query recent session record.
     * 查询最近会话记录【响应】
     * @param i                        Indicates response result, 0:failed; 1:success
     *                                 响应结果 0：失败；1：成功
     * @param getRoamingChatRecordData Indicates chat record date
     *                                 查询响应的数据结构
     */
    @Override
    public void onQueryRecentSession(int i, GetRoamingChatRecordData getRoamingChatRecordData)
    {
        Log.i(TAG, "-->onQueryRecentSession = " + getRoamingChatRecordData.toString());
    }

    /**
     * This method is used to notify invite to join group.
     * 邀请加入群组推送【通知】
     * @param requestJoinInGroupNotifyData Indicates join result
     *                                     邀请加入的数据结构
     */
    @Override
    public void onInviteJoinGroupNotify(RequestJoinInGroupNotifyData requestJoinInGroupNotifyData)
    {
        Log.i(TAG, "-->onInviteJoinGroupNotify");
        if (requestJoinInGroupNotifyData == null)
        {
            Log.e(TAG, "requestJoinInGroupNotifyData is null");
            return;
        }
        //加入群组成功
        if (requestJoinInGroupNotifyData.getStatus() == ResponseCodeHandler.ResponseCode.REQUEST_SUCCESS)
        {
            mNotification.onInviteJoinGroupNotify(requestJoinInGroupNotifyData);
            Log.i(TAG, "onInviteJoinGroupNotify = " + requestJoinInGroupNotifyData.toString());
            if (!TextUtils.isEmpty(requestJoinInGroupNotifyData.getId()))
            {
//                int GROUP_MARK_TYPE = 6;
//                int GROUP_MARK_NOTIFY_TAG = 4;
                //设置消息已读
                markRead(requestJoinInGroupNotifyData.getId(), mMyAccount, ImConstant.GroupMark.GROUP_MARK_TYPE, ImConstant.GroupTag.GROUP_MARK_NOTIFY_TAG);
            }
        }
    }

    /**
     * This method is used to response invite to join group.
     * 邀请加入群组【响应】
     * @param i                 Indicates response result, 0:failed; 1:success
     *                          响应结果 0：失败；1：成功
     * @param inviteToGroupResp Indicates response data struct
     *                          邀请加入结果的数据结构
     */
    @Override
    public void onInviteJoinGroup(int i, InviteToGroupResp inviteToGroupResp)
    {
        Log.i(TAG, "-->onInviteJoinGroup status = " + inviteToGroupResp.getStatus());
        if (ResponseCodeHandler.ResponseCode.REQUEST_SUCCESS == inviteToGroupResp.getStatus())
        {
            mNotification.onInviteJoinGroupSuccess();
        }else {
            mNotification.onInviteJoinGroupFail();
        }
    }

    /**
     * This method is used to notify user accept to join group.
     * 群成员同意加入群组推送【通知】
     * @param i                     Indicates response result, 0:failed; 1:success
     *                              响应结果 0：失败；1：成功
     * @param joinConstantGroupResp Indicates response data struct
     *                              同意加入群组结果的数据结构
     */
    @Override
    public void onAcceptJoinGroupNotify(int i, JoinConstantGroupResp joinConstantGroupResp)
    {
        Log.i(TAG, "-->onAcceptJoinGroupNotify = " + joinConstantGroupResp.toString());
    }

    /**
     * This method is used to notify user reject join group.
     * 群成员拒绝加入群组推送【通知】
     * @param i                Indicates response result, 0:failed; 1:success
     *                         响应结果 0：失败；1：成功
     * @param baseResponseData Indicates response data struct
     *                         拒绝加入群组结果的数据结构
     */
    @Override
    public void onRejectJoinGroupNotify(int i, BaseResponseData baseResponseData)
    {
        Log.i(TAG, "-->onRejectJoinGroupNotify = " + baseResponseData.toString());
    }

    /**
     * 可以添加其他相关功能的回调消息.
     * @param i
     * @param o
     */
    @Override
    public void onOtherCallback(int i, Object o)
    {
        Log.i(TAG, "-->onOtherCallback");
    }

    /**
     * This method is used to notify offline file upload process.
     * 富媒体上传进度通知
     * @param instantMessage Indicates instant message
     *                       消息
     * @param mediaResource  Indicates media resource
     *                       富媒体资源
     * @param progressInfo   Indicates last time process
     *                       上次进度信息
     */
    @Override
    public void notifyUploadProcess(InstantMessage instantMessage, MediaResource mediaResource,
                                    FileTransfer.ProgressInfo progressInfo)
    {
        Log.i(TAG, "-->notifyUploadProcess");
        if (progressInfo != null)
        {
            int process = progressInfo.getProgress();
            //设置上传媒体资源信息
            UmTransProgressData processData = new UmTransProgressData();
            processData.setMsgId(instantMessage.getId());
            processData.setMediaSourceId(mediaResource.getMediaId());
            processData.setProgress(process);
            mNotification.onDownloadFinished(processData);
        }
    }

    /**
     * This method is used to notify offline file download process.
     * 富媒体下载进度通知
     * @param instantMessage Indicates instant message
     *                       消息
     * @param mediaResource  Indicates um resource
     *                       富媒体资源
     * @param progressInfo   Indicates last time process
     *                       上次进度信息
     */
    @Override
    public void notifyDownloadProcess(InstantMessage instantMessage, MediaResource mediaResource,
                                      FileTransfer.ProgressInfo progressInfo)
    {
        Log.i(TAG, "-->notifyDownloadProcess");
        if (progressInfo != null)
        {
            int process = progressInfo.getProgress();
            //设置下载进度详情
            UmTransProgressData processData = new UmTransProgressData();
            processData.setMsgId(instantMessage.getId());
            processData.setMediaSourceId(mediaResource.getMediaId());
            processData.setProgress(process);
            mNotification.notifyDownloadProcess(processData);
        }
    }

    /**
     * This method is used to notify offline file transfer finish.
     * 富媒体传输完成通知
     * @param instantMessage Indicates instant message
     *                       消息
     * @param mediaResource  Indicates um resource
     *                       富媒体资源
     * @param b              Indicates is thumbnail
     *                       是否是缩略图
     * @param i              Indicates error code return by server
     *                       服务器返回错误码
     * @param success        Indicates transfer result success or failed
     *                       传输结果成功或者失败
     */
    @Override
    public void notifyFinish(InstantMessage instantMessage, MediaResource mediaResource, boolean b, int i,
                             boolean success)
    {
        Log.i(TAG, "-->notifyFinish status:" + success);
        if (success)
        {
            mNotification.notifyFinish();
        }
    }

    /**
     * This method is used to notify the result of message send
     * 消息发送结果通知
     * @param instantMessage Indicates instant message
     *                       消息
     * @param result         Indicates send result success or failed
     *                       返回结果，true成功，false失败
     */
    @Override
    public void sendMessageResponse(InstantMessage instantMessage, boolean result)
    {
        Log.i(TAG, "-->sendMessageResponse result:" + result);
        if (result)
        {
            mNotification.onSendMessagesSuccess(instantMessage);
        }
        else
        {
            mNotification.onSendMessagesFail(instantMessage);
        }
    }

    /**
     * This method is used to notify audio play ended
     * 停止播放音频响应
     * @param i Indicates play handle
     *          播放句柄
     */
    @Override
    public void onAudioPlayEnd(int i)
    {
        Log.i(TAG, "-->onAudioPlayEnd");
        mNotification.onAudioPlayEnd();
    }

}
