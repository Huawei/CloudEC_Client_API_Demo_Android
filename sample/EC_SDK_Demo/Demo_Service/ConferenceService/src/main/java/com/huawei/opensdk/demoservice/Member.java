package com.huawei.opensdk.demoservice;

import com.huawei.ecterminalsdk.base.TsdkAttendee;
import com.huawei.ecterminalsdk.base.TsdkAttendeeBaseInfo;
import com.huawei.ecterminalsdk.base.TsdkAttendeeStatusInfo;
import com.huawei.ecterminalsdk.base.TsdkConfParticipantStatus;
import com.huawei.ecterminalsdk.base.TsdkConfRole;
import com.huawei.opensdk.demoservice.data.CameraEntity;
import com.huawei.tup.confctrl.ConfctrlConfRole;

import java.util.ArrayList;
import java.util.List;

/**
 * This method is used to conf member info.
 * 与会者信息
 */
public class Member {
    /**
     * 与会者号码
     */
    private String number;

    /**
     * 来电姓名
     */
    private String displayName;

    /**
     * email
     * 邮箱
     */
    private String email;

    /**
     * SMS
     */
    private String sms;

    /**
     * 帐号
     */
    private String accountId;

    /**
     * 与会者状态
     */
    private ConfConstant.ParticipantStatus status;

    /**
     * 与会者角色
     */
    private TsdkConfRole role;

    /**
     * 是否静音
     */
    private boolean isMute;

    /**
     * 是否举手
     */
    private boolean isHandUp;

    /**
     * 是否已在数据会议中
     */
    private boolean inDataConference;

    /**
     * 是否是主席
     */
    private boolean isHost;

    /**
     * 是否主讲人
     */
    private boolean isPresent;

    /**
     * 用户id
     */
    private long dataUserId;

    /**
     * 与会者id
     */
    private String participantId;

    /**
     * 是否是自己
     */
    private boolean isSelf;

    /**
     * 是否自动邀请
     */
    private boolean isAutoInvite;

    /**
     * 是否广播自己
     */
    private boolean isBroadcastSelf;

    /**
     * 摄像头列表
     */
    private List<CameraEntity> cameraEntityList = new ArrayList<>();

    public Member()
    {

    }

    public Member(String number)
    {
        this.number = number;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDisplayName() {
        if ((displayName == null) || (displayName.equals("")))
        {
            return number;
        }
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSms() {
        return sms;
    }

    public void setSms(String sms) {
        this.sms = sms;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public boolean isMute() {
        return isMute;
    }

    public void setMute(boolean mute) {
        isMute = mute;
    }

    public boolean isAutoInvite() {
        return isAutoInvite;
    }

    public void setAutoInvite(boolean autoInvite) {
        isAutoInvite = autoInvite;
    }

    public boolean isBroadcastSelf() {
        return isBroadcastSelf;
    }

    public void setBroadcastSelf(boolean broadcastSelf) {
        isBroadcastSelf = broadcastSelf;
    }

    public TsdkConfRole getRole() {
        return role;
    }

    public void setRole(TsdkConfRole role) {
        this.role = role;
    }



    public boolean isHandUp() {
        return isHandUp;
    }

    public void setHandUp(boolean handUp) {
        isHandUp = handUp;
    }


    public ConfConstant.ParticipantStatus getStatus() {
        return status;
    }

    public void setStatus(ConfConstant.ParticipantStatus status) {
        this.status = status;
    }

    public boolean isSelf() {
        return isSelf;
    }

    public void setSelf(boolean self) {
        isSelf = self;
    }


    public List<CameraEntity> getCameraEntityList() {
        return cameraEntityList;
    }

    public void setCameraEntityList(List<CameraEntity> cameraEntityList) {
        this.cameraEntityList = cameraEntityList;
    }

    public void replaceCamera(CameraEntity cameraEntity)
    {
        int index = cameraEntityList.indexOf(cameraEntity);
        if (index == -1)
        {
            cameraEntityList.add(cameraEntity);
        }
        else
        {
            cameraEntityList.set(index, cameraEntity);
        }
    }

    public void updateCamera(long deviceID, int status)
    {
        for (CameraEntity cameraEntity : cameraEntityList)
        {
            if (cameraEntity.getDeviceID() == deviceID)
            {
                cameraEntity.setCameraStatus(status);
                return;
            }
        }
    }

    public CameraEntity getOpenedCamera()
    {
        for (CameraEntity cameraEntity : cameraEntityList)
        {
            if (cameraEntity.getCameraStatus() == CameraEntity.CAMERA_STATUS_OPENED)
            {
                return cameraEntity;
            }
        }
        return null;
    }

    public boolean isHost() {
        return isHost;
    }

    public void setHost(boolean host) {
        isHost = host;
    }

    public boolean isPresent() {
        return isPresent;
    }

    public void setPresent(boolean present) {
        this.isPresent = present;
    }

    public boolean isInDataConference() {
        return inDataConference;
    }

    public void setInDataConference(boolean inDataConference) {
        this.inDataConference = inDataConference;
    }

    public long getDataUserId() {
        return dataUserId;
    }

    public void setDataUserId(long dataUserId) {
        this.dataUserId = dataUserId;
    }


    public String getParticipantId() {
        return participantId;
    }

    public void setParticipantId(String participantId) {
        this.participantId = participantId;
    }


    public void update(TsdkAttendee attendeeInfo)
    {
        TsdkAttendeeStatusInfo attendeeStatusInfo = attendeeInfo.getStatusInfo();
        TsdkAttendeeBaseInfo attendeeBaseInfo = attendeeInfo.getBaseInfo();

        setParticipantId(attendeeStatusInfo.getParticipantId());
        setNumber(attendeeBaseInfo.getNumber());
        setDisplayName(attendeeBaseInfo.getDisplayName());
        setAccountId(attendeeBaseInfo.getAccountId());
        setEmail(attendeeBaseInfo.getEmail());
        setSms(attendeeBaseInfo.getSms());
        setMute(attendeeStatusInfo.getIsMute() == 1? true:false);
        setHandUp(attendeeStatusInfo.getIsHandup() == 1? true : false);
        TsdkConfRole role = ((attendeeBaseInfo.getRole() == ConfctrlConfRole.CONFCTRL_E_CONF_ROLE_CHAIRMAN.getIndex()) ?
                TsdkConfRole.TSDK_E_CONF_ROLE_CHAIRMAN : TsdkConfRole.TSDK_E_CONF_ROLE_ATTENDEE);
        setRole(role);
        setBroadcastSelf(attendeeStatusInfo.getIsBroadcast() == 1 ? true : false);

        TsdkConfParticipantStatus participantStatus = ConfConvertUtil.convertAttendStatus(attendeeStatusInfo.getState());
        if (participantStatus != null) {
            setStatus(ConfConvertUtil.convertConfctrlParticipantStatus(participantStatus));
        }

        setSelf((attendeeStatusInfo.getIsSelf()==1));
        setInDataConference(attendeeStatusInfo.getIsJoinDataconf() == 1 ? true: false);
        setPresent(attendeeStatusInfo.getIsPresent() == 1 ? true :false);

    }


    @Override
    public int hashCode() {
        return number.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (obj.getClass() != this.getClass())) {
            return false;
        }

        Member member = (Member) obj;

        if (member.number.equals(this.number)) {
            return true;
        }
        return false;
    }

}
