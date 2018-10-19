package com.huawei.opensdk.demoservice;


import com.huawei.ecterminalsdk.base.TsdkAttendee;
import com.huawei.ecterminalsdk.base.TsdkAttendeeBaseInfo;
import com.huawei.ecterminalsdk.base.TsdkAttendeeStatusInfo;
import com.huawei.ecterminalsdk.base.TsdkConfMediaType;
import com.huawei.ecterminalsdk.base.TsdkConfParticipantStatus;
import com.huawei.ecterminalsdk.base.TsdkConfRole;
import com.huawei.ecterminalsdk.base.TsdkConfState;
import com.huawei.tup.confctrl.ConfctrlConfMediatypeFlag;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is about conference Tools
 * 会议工具类
 */
public class ConfConvertUtil {

    /**
     * This method is used to convert conference media type.
     * 会议媒体类型转换
     * @param mediaType 媒体类型
     * @return
     */
    public static int convertConfMediaType(TsdkConfMediaType mediaType)
    {
        int confMediaType;
        switch (mediaType)
        {
            //音频
            case TSDK_E_CONF_MEDIA_VOICE:
                confMediaType = ConfctrlConfMediatypeFlag.CONFCTRL_E_CONF_MEDIATYPE_FLAG_VOICE.getIndex();
                break;

            //视频
            case TSDK_E_CONF_MEDIA_VIDEO:
                confMediaType = ConfctrlConfMediatypeFlag.CONFCTRL_E_CONF_MEDIATYPE_FLAG_VOICE.getIndex()
                        | ConfctrlConfMediatypeFlag.CONFCTRL_E_CONF_MEDIATYPE_FLAG_HDVIDEO.getIndex();
                break;

            //音频数据
            case TSDK_E_CONF_MEDIA_VOICE_DATA:
                confMediaType = ConfctrlConfMediatypeFlag.CONFCTRL_E_CONF_MEDIATYPE_FLAG_VOICE.getIndex()
                        | ConfctrlConfMediatypeFlag.CONFCTRL_E_CONF_MEDIATYPE_FLAG_DATA.getIndex();
                break;

            //视频数据
            case TSDK_E_CONF_MEDIA_VIDEO_DATA:
                confMediaType = ConfctrlConfMediatypeFlag.CONFCTRL_E_CONF_MEDIATYPE_FLAG_VOICE.getIndex()
                        | ConfctrlConfMediatypeFlag.CONFCTRL_E_CONF_MEDIATYPE_FLAG_VIDEO.getIndex()
                        | ConfctrlConfMediatypeFlag.CONFCTRL_E_CONF_MEDIATYPE_FLAG_DATA.getIndex();
                break;

            default:
                confMediaType = ConfctrlConfMediatypeFlag.CONFCTRL_E_CONF_MEDIATYPE_FLAG_BUTT.getIndex();
                break;
        }
        return  confMediaType;
    }

    /**
     * This method is used to convert conference media type.
     * @param confMediaType conference media type 会议媒体类型
     * @return
     */
    public static TsdkConfMediaType convertConfMediaType(int confMediaType)
    {
        TsdkConfMediaType mediaType = TsdkConfMediaType.enumOf(confMediaType);
        return  mediaType;
    }

    /**
     * This method is used to convert conference state
     * 转换会议状态
     * @param state
     * @return
     */
    public static ConfConstant.ConfConveneStatus convertConfctrlConfState(int state)
    {
        ConfConstant.ConfConveneStatus status = ConfConstant.ConfConveneStatus.UNKNOWN;
        switch (state)
        {
            case 0:
                status = ConfConstant.ConfConveneStatus.SCHEDULE;
                break;

            case 1:
                status = ConfConstant.ConfConveneStatus.CREATING;
                break;

            case 2:
                status = ConfConstant.ConfConveneStatus.GOING;
                break;

            case 3:
                status = ConfConstant.ConfConveneStatus.DESTROYED;
                break;

            default:
                break;
        }
        return status;
    }

    /**
     * This method is used to convert conference state
     * 转换会议状态
     * @param state
     * @return
     */
    public static ConfConstant.ConfConveneStatus convertConfctrlConfState(TsdkConfState state)
    {
        ConfConstant.ConfConveneStatus status = ConfConstant.ConfConveneStatus.UNKNOWN;
        switch (state)
        {
            case TSDK_E_CONF_STATE_SCHEDULE:
                status = ConfConstant.ConfConveneStatus.SCHEDULE;
                break;

            case TSDK_E_CONF_STATE_CREATING:
                status = ConfConstant.ConfConveneStatus.CREATING;
                break;

            case TSDK_E_CONF_STATE_GOING:
                status = ConfConstant.ConfConveneStatus.GOING;
                break;

            case TSDK_E_CONF_STATE_DESTROYED:
                status = ConfConstant.ConfConveneStatus.DESTROYED;
                break;

            default:
                break;
        }
        return status;
    }

    public static TsdkConfParticipantStatus convertAttendStatus(int state){
        TsdkConfParticipantStatus tsdkConfParticipantStatus = TsdkConfParticipantStatus.enumOf(state);
        return tsdkConfParticipantStatus;
    }

    /**
     * This method is used to convert conference participant state
     * 转换与会者状态
     * @param state
     * @return
     */
    public static ConfConstant.ParticipantStatus convertConfctrlParticipantStatus(TsdkConfParticipantStatus state)
    {
        ConfConstant.ParticipantStatus status = ConfConstant.ParticipantStatus.UNKNOWN;
        switch (state)
        {
            case TSDK_E_CONF_PARTICIPANT_STATUS_IN_CONF:
                status = ConfConstant.ParticipantStatus.IN_CONF;
                break;

            case TSDK_E_CONF_PARTICIPANT_STATUS_CALLING:
                status = ConfConstant.ParticipantStatus.CALLING;
                break;

            case TSDK_E_CONF_PARTICIPANT_STATUS_JOINING:
                status = ConfConstant.ParticipantStatus.JOINING;
                break;

            case TSDK_E_CONF_PARTICIPANT_STATUS_LEAVED:
                status = ConfConstant.ParticipantStatus.LEAVED;
                break;

            case TSDK_E_CONF_PARTICIPANT_STATUS_NO_EXIST:
                status = ConfConstant.ParticipantStatus.NO_EXIST;
                break;

            case TSDK_E_CONF_PARTICIPANT_STATUS_BUSY:
                status = ConfConstant.ParticipantStatus.BUSY;
                break;

            case TSDK_E_CONF_PARTICIPANT_STATUS_NO_ANSWER:
                status = ConfConstant.ParticipantStatus.NO_ANSWER;
                break;

            case TSDK_E_CONF_PARTICIPANT_STATUS_REJECT:
                status = ConfConstant.ParticipantStatus.REJECT;
                break;

            case TSDK_E_CONF_PARTICIPANT_STATUS_CALL_FAILED:
                status = ConfConstant.ParticipantStatus.CALL_FAILED;
                break;

            default:
                break;
        }
        return status;
    }

    /**
     * This method is used to convert conference protocol
     * 转换会议协议类型
     * @param protocol
     * @return
     */
    public static ConfConstant.ConfProtocol convertConfctrlProtocol(int protocol)
    {
        ConfConstant.ConfProtocol confProtocol = ConfConstant.ConfProtocol.IDO_PROTOCOL;
        switch (protocol)
        {
            case 0:
                confProtocol = ConfConstant.ConfProtocol.IDO_PROTOCOL;
                break;
            case 1:
                confProtocol = ConfConstant.ConfProtocol.REST_PROTOCOL;
                break;
            default:
                break;
        }
        return confProtocol;
    }

    public static List<TsdkAttendee> convertMemberList(List<Member> memberList)
    {
        List<TsdkAttendee> attendeeInfoList = new ArrayList<>();
        for (Member member : memberList)
        {
            TsdkAttendee attendeeInfo = new TsdkAttendee();

            attendeeInfo.getBaseInfo().setNumber(member.getNumber());
            attendeeInfo.getBaseInfo().setDisplayName(member.getDisplayName());
            attendeeInfo.getBaseInfo().setAccountId(member.getAccountId());
            attendeeInfo.getBaseInfo().setEmail(member.getEmail());
            attendeeInfo.getBaseInfo().setSms(member.getSms());
            attendeeInfo.getStatusInfo().setIsMute(member.isMute() ? 1 : 0);

            TsdkConfRole role = ((member.getRole() == TsdkConfRole.TSDK_E_CONF_ROLE_CHAIRMAN) ?
                    TsdkConfRole.TSDK_E_CONF_ROLE_CHAIRMAN : TsdkConfRole.TSDK_E_CONF_ROLE_ATTENDEE);
            attendeeInfo.getBaseInfo().setRole(role);

//            attendeeInfo.setType(ConfctrlAttendeeType.CONFCTRL_E_ATTENDEE_TYPE_NORMAL);

            attendeeInfoList.add(attendeeInfo);
        }
        return attendeeInfoList;
    }


    /**
     * This method is used to convert member info
     * 转变与会者信息
     * @param attendeeInfo
     * @return
     */
    public static Member convertAttendeeInfo(TsdkAttendee attendeeInfo)
    {
        Member member = new Member();

        TsdkAttendeeStatusInfo attendeeStatusInfo = attendeeInfo.getStatusInfo();
        TsdkAttendeeBaseInfo attendeeBaseInfo = attendeeInfo.getBaseInfo();

        member.setParticipantId(attendeeStatusInfo.getParticipantId());
        member.setNumber(attendeeBaseInfo.getNumber());
        member.setDisplayName(attendeeBaseInfo.getDisplayName());
        member.setAccountId(attendeeBaseInfo.getAccountId());
        member.setEmail(attendeeBaseInfo.getEmail());
        member.setSms(attendeeBaseInfo.getSms());
        member.setMute(attendeeStatusInfo.getIsMute()==1? true:false);
        member.setHandUp(attendeeStatusInfo.getIsHandup() == 1? true : false);
        TsdkConfRole role = ((attendeeBaseInfo.getRole() == TsdkConfRole.TSDK_E_CONF_ROLE_CHAIRMAN.getIndex()) ?
                TsdkConfRole.TSDK_E_CONF_ROLE_CHAIRMAN : TsdkConfRole.TSDK_E_CONF_ROLE_ATTENDEE);
        member.setRole(role);
        member.setBroadcastSelf(attendeeStatusInfo.getIsBroadcast() == 1 ? true : false);

        TsdkConfParticipantStatus participantStatus = convertAttendStatus(attendeeStatusInfo.getState());
        if (participantStatus != null) {
            member.setStatus(convertConfctrlParticipantStatus(participantStatus));
        }
        member.setSelf((attendeeStatusInfo.getIsSelf()==1));

        member.setInDataConference(attendeeStatusInfo.getIsJoinDataconf() == 1 ? true: false);
        member.setPresent(attendeeStatusInfo.getIsPresent() == 1 ? true :false);

        return member;
    }

    /**
	 * This method is used to convert attendee information list
     * 转换与会者信息列表
     * @param attendeeInfoList
     * @return
     */
    public static List<Member> convertAttendeeInfoList(List<TsdkAttendeeBaseInfo> attendeeInfoList)
    {
        List<Member> memberList = new ArrayList<>();
        for (TsdkAttendeeBaseInfo attendeeInfo : attendeeInfoList)
        {
            Member member = new Member();

            member.setNumber(attendeeInfo.getNumber());
            member.setDisplayName(attendeeInfo.getDisplayName());
            member.setAccountId(attendeeInfo.getAccountId());
            member.setEmail(attendeeInfo.getEmail());
            member.setSms(attendeeInfo.getSms());
//            member.setMute(attendeeInfo.isMute());
            TsdkConfRole role = ((attendeeInfo.getRole() == TsdkConfRole.TSDK_E_CONF_ROLE_CHAIRMAN.getIndex()) ?
                    TsdkConfRole.TSDK_E_CONF_ROLE_CHAIRMAN : TsdkConfRole.TSDK_E_CONF_ROLE_ATTENDEE);
            member.setRole(role);

            memberList.add(member);
        }
        return memberList;
    }

    /**
     * This method is used to Transform 'memberList' to 'TsdkAttendeeBaseInfo'
     * @param memberList
     * @return
     */
    public static List<TsdkAttendeeBaseInfo> memberListToAttendeeList(List<Member> memberList)
    {
        List<TsdkAttendeeBaseInfo> attendeeList = new ArrayList<>();
        for (Member member : memberList)
        {
            TsdkAttendeeBaseInfo confctrlAttendee = new TsdkAttendeeBaseInfo();
            confctrlAttendee.setNumber(member.getNumber());
            confctrlAttendee.setDisplayName(member.getDisplayName());
            confctrlAttendee.setEmail(member.getEmail());
            confctrlAttendee.setSms(member.getSms());
            confctrlAttendee.setAccountId(member.getAccountId());
            confctrlAttendee.setRole(member.getRole());

            attendeeList.add(confctrlAttendee);
        }

        return attendeeList;
    }

}
