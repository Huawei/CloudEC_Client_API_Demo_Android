package com.huawei.opensdk.imservice;

/**
 * This class is about Im module constant.
 * Im业务功能常量类
 */
public class ImConstant {

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
        ON_LINE(0), //在线
        BUSY(1),    //繁忙BUSY = 1

        XA(3),      //离开XA = 3
        AWAY(4);    //离线AWAY = 4
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

}
