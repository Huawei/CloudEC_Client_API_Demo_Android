package com.huawei.opensdk.demoservice;


import com.huawei.ecterminalsdk.base.TsdkConfMediaType;

/**
 * This class is about conference basic information for meeting List query results
 * 会议基础信息类
 * 用于会议列表查询结果
 */
public class ConfBaseInfo {
    /**
     * conference size
     * 会议个数
     */
    private int size;

    /**
     * conf ID
     * 会议id
     */
    private String confID;

    /**
     * conference subject
     * 会议主题
     */
    private String subject;

    /**
     * Access number
     * 会议接入号
     */
    private String accessNumber;

    /**
     * chairman pwd
     * 主席密码
     */
    private String chairmanPwd;

    /**
     * guest pwd
     * 普通与会者密码
     */
    private String guestPwd;

    /**
     * conference start time
     * 会议开始时间
     */
    private String startTime;

    /**
     * conference end time
     * 会议结束时间
     */
    private String endTime;
    private String schedulerNumber;
    private String schedulerName;
    private String groupUri;

    /**
     * Conference type
     * 会议类型
     */
    private TsdkConfMediaType mediaType;

    /**
     * Conference state
     * 会议状态
     */
    private ConfConstant.ConfConveneStatus confState;

    /**
     * Lock
     * 是否锁定
     */
    private boolean isLock;

    /**
     * record
     * 是否录制
     */
    private boolean record;

    /**
     * record
     * 是否录制
     */
    private boolean supportRecord;

    /**
     * Mute all
     * 是否全部静音
     */
    private boolean isMuteAll;

    /**
     * Join conference
     * 是否加入会议
     */
    private boolean isJoin;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getConfID() {
        return confID;
    }

    public void setConfID(String confID) {
        this.confID = confID;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getAccessNumber() {
        return accessNumber;
    }

    public void setAccessNumber(String accessNumber) {
        this.accessNumber = accessNumber;
    }

    public String getChairmanPwd() {
        return chairmanPwd;
    }

    public void setChairmanPwd(String chairmanPwd) {
        this.chairmanPwd = chairmanPwd;
    }

    public String getGuestPwd() {
        return guestPwd;
    }

    public void setGuestPwd(String guestPwd) {
        this.guestPwd = guestPwd;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getSchedulerNumber() {
        return schedulerNumber;
    }

    public void setSchedulerNumber(String schedulerNumber) {
        this.schedulerNumber = schedulerNumber;
    }

    public String getSchedulerName() {
        return schedulerName;
    }

    public void setSchedulerName(String schedulerName) {
        this.schedulerName = schedulerName;
    }

    public TsdkConfMediaType getMediaType() {
        return mediaType;
    }

    public String getGroupUri() {
        return groupUri;
    }

    public void setGroupUri(String groupUri) {
        this.groupUri = groupUri;
    }

    public void setMediaType(TsdkConfMediaType mediaType) {
        this.mediaType = mediaType;
    }

    public ConfConstant.ConfConveneStatus getConfState() {
        return confState;
    }

    public void setConfState(ConfConstant.ConfConveneStatus confState) {
        this.confState = confState;
    }

    public boolean isLock() {
        return isLock;
    }

    public void setLock(boolean lock) {
        isLock = lock;
    }

    public boolean isRecord() {
        return record;
    }

    public void setRecord(boolean record) {
        this.record = record;
    }

    public boolean isSupportRecord() {
        return supportRecord;
    }

    public void setSupportRecord(boolean supportRecord) {
        this.supportRecord = supportRecord;
    }

    public boolean isMuteAll() {
        return isMuteAll;
    }

    public void setMuteAll(boolean muteAll) {
        isMuteAll = muteAll;
    }

    public boolean isJoin() {
        return isJoin;
    }

    public void setJoin(boolean join) {
        isJoin = join;
    }

}
