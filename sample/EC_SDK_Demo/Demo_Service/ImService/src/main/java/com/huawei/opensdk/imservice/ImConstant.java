package com.huawei.opensdk.imservice;

/**
 * This class is about Im module constant.
 * Im业务功能常量类
 */
public class ImConstant {

    public static final int FIXED = 0;
    public static final int DISCUSSION = 1;
    public static final int SET_READ = 2;
    public static final int WHITDRAW_MESSAGE = 3;
    public static final int DELETE_MESSAGE = 4;

    /**
     * This is a static final class which is about log param..
     * 有关日志配置和应用文件路径配置
     */
    public static final class LogParam
    {
        public static final int MAX_LOG_SIZE_KB = 10 * 1024; //每个日志文件的最大值，单位:KB，建议为10*1024
        public static final String LOG_NAME = "ImLog.txt";   //日志文件名
        public static final String LOG_DIRECTORY = "ImLOG";  //日志路径
        public static final String LOG_STORAGE = "ECSDKDemo";//应用文件路径
    }

    /**
     * This is a static final class which is about search
     * 查询联系人类型相关的字段
     */
    public static final class SearchType
    {
        //精确查询，查询的字段为：eSpace号码、绑定号码或者工号
        public static final String SEARCH_TYPE_NUMBER = "eSpaceNumber";
        public static final String SEARCH_TYPE_BINDN = "bindno";
        public static final String SEARCH_TYPE_STAFF = "staffNo";

        //模糊查询，查询的字段为空的字符串或者其他
        public static final String SEARCH_TYPE_FUZZY = "";
    }

    /**
     * The enumeration class of message type.
     * 消息标记类型
     */
    public enum GroupMark
    {
        GROUP_MARK_TYPE(),       //其它消息
        GROUP_MARK_TYPE_IM(),    //Im
        GROUP_MARK_TYPE_FIXED()  //固定群
    }

    /**
     * The enumeration class of message tag.
     *  消息标签
     */
    public enum GroupTag
    {
        GROUP_MARK_NOTIFY_TAG_IM(),         //0：点对点IM消息
        GROUP_MARK_NOTIFY_TAG_GROUP(),      //1：固定群/讨论组IM消息
        GROUP_MARK_NOTIFY_TAG_SMS(),        //2：短信
        GROUP_MARK_NOTIFY_TAG_SYS_NOTIFY(), //3：系统公告
        GROUP_MARK_NOTIFY_TAG()             //4：其他（P2P文件传输，好友邀请，群组邀请，群组通知）
    }

    /**
     * The enumeration class of user state.
     * 用户状态
     */
    public enum ImStatus
    {
        AWAY(0),    //离线AWAY = 0
        ON_LINE(1), //在线 = 1

        BUSY(3),    //繁忙BUSY = 3
        XA(4),      //离开XA = 4
        DND(5);     //请勿打扰 = 5

        private int status;

        public int getIndex()
        {
            return status;
        }

        ImStatus(int status)
        {
            this.status = status;
        }
    }

    /**
     *
     */
    public static final class ChatGroupUpdateType
    {
        public static final int CHAT_GROUP_INFO_UPDATE = 0;
        public static final int CHAT_GROUP_ADD_MEMBER =1;
        public static final int CHAT_GROUP_DEL_MEMBER = 2;
        public static final int CHAT_GROUP_OWNER_UPDATE = 3;
        public static final int CHAT_GROUP_DISMISS = 4;
    }

    public static final class GroupOpType
    {
        public static final int CHAT_GROUP_MODIFY_DEFAULT_PARAM = 0;      // 修改默认参数，即群组中可修改的所有参数
        public static final int CHAT_GROUP_MODIFY_OWNER = 1;              // 修改(转移)群组管理员
        public static final int CHAT_GROUP_MODIFY_MSG_PROMOT_POLICY = 2;  // 修改群组消息提示策略
        public static final int CHAT_GROUP_MODIFY_FIX_DISCUSS_STAUTS = 3; // 修改讨论组固化状态
        public static final int CHAT_GROUP_MODIFY_GROUP_TYPE = 4;         // 修改群组类型
    }

    public static final class ContactGroupOpType
    {
        public static final int ADD_CONTACT_GROUP = 0;     // 添加联系人分组
        public static final int DEL_CONTACT_GROUP = 1;     // 删除联系人分组
        public static final int MODIFY_CONTACT_GROUP = 2;  // 重命名联系人分组
    }

    public static final class OpContactType
    {
        public static final int CONTACT_MOVE_TO_NEW_GROUP = 0; // 将分组成员移动至其他分组
        public static final int CONTACT_COPY_TO_NEW_GROUP = 1; // 将分组成员复制至其他分组
    }

    public enum ChatMsgType
    {
        SINGLE_CHAT(0),           // 单聊
        FIXED_GROUP_CHAT(2),      // 固定群聊
        DISCUSSION_GROUP_CHAT(6); // 多人会话讨论组

        private int index;

        ChatMsgType(int index)
        {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }
    }

    public enum ChatMsgMediaType
    {
        CHAT_MSG_MEDIA_TYPE_TEXT(0),
        CHAT_MSG_MEDIA_TYPE_AUDIO(1),
        CHAT_MSG_MEDIA_TYPE_VIDEO(2),
        CHAT_MSG_MEDIA_TYPE_IMAGE(3);

        private int index;

        ChatMsgMediaType(int index){
            this.index = index;
        }

        public int getIndex() {
            return index;
        }
    }
}
