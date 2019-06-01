package com.huawei.opensdk.demoservice;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import com.huawei.ecterminalsdk.base.TsdkAddAttendeesInfo;
import com.huawei.ecterminalsdk.base.TsdkAttendee;
import com.huawei.ecterminalsdk.base.TsdkAttendeeBaseInfo;
import com.huawei.ecterminalsdk.base.TsdkBookConfInfo;
import com.huawei.ecterminalsdk.base.TsdkConfAnonymousJoinParam;
import com.huawei.ecterminalsdk.base.TsdkConfAsStateInfo;
import com.huawei.ecterminalsdk.base.TsdkConfAttendeeUpdateType;
import com.huawei.ecterminalsdk.base.TsdkConfBaseInfo;
import com.huawei.ecterminalsdk.base.TsdkConfChatMsgInfo;
import com.huawei.ecterminalsdk.base.TsdkConfChatType;
import com.huawei.ecterminalsdk.base.TsdkConfDetailInfo;
import com.huawei.ecterminalsdk.base.TsdkConfEnvType;
import com.huawei.ecterminalsdk.base.TsdkConfJoinParam;
import com.huawei.ecterminalsdk.base.TsdkConfLanguage;
import com.huawei.ecterminalsdk.base.TsdkConfListInfo;
import com.huawei.ecterminalsdk.base.TsdkConfMediaType;
import com.huawei.ecterminalsdk.base.TsdkConfOperationResult;
import com.huawei.ecterminalsdk.base.TsdkConfOperationType;
import com.huawei.ecterminalsdk.base.TsdkConfRight;
import com.huawei.ecterminalsdk.base.TsdkConfRole;
import com.huawei.ecterminalsdk.base.TsdkConfShareState;
import com.huawei.ecterminalsdk.base.TsdkConfSpeaker;
import com.huawei.ecterminalsdk.base.TsdkConfSpeakerInfo;
import com.huawei.ecterminalsdk.base.TsdkConfType;
import com.huawei.ecterminalsdk.base.TsdkConfVideoMode;
import com.huawei.ecterminalsdk.base.TsdkDocBaseInfo;
import com.huawei.ecterminalsdk.base.TsdkDocShareDelDocInfo;
import com.huawei.ecterminalsdk.base.TsdkJoinConfIndInfo;
import com.huawei.ecterminalsdk.base.TsdkLocalAddress;
import com.huawei.ecterminalsdk.base.TsdkQueryConfDetailReq;
import com.huawei.ecterminalsdk.base.TsdkQueryConfListReq;
import com.huawei.ecterminalsdk.base.TsdkWatchAttendees;
import com.huawei.ecterminalsdk.base.TsdkWatchAttendeesInfo;
import com.huawei.ecterminalsdk.base.TsdkWbDelDocInfo;
import com.huawei.ecterminalsdk.models.TsdkCommonResult;
import com.huawei.ecterminalsdk.models.TsdkManager;
import com.huawei.ecterminalsdk.models.call.TsdkCall;
import com.huawei.ecterminalsdk.models.conference.TsdkConference;
import com.huawei.opensdk.callmgr.CallMgr;
import com.huawei.opensdk.callmgr.Session;
import com.huawei.opensdk.callmgr.VideoMgr;
import com.huawei.opensdk.commonservice.util.DeviceManager;
import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.loginmgr.LoginMgr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import static com.huawei.ecterminalsdk.base.TsdkConfEnvType.TSDK_E_CONF_ENV_HOSTED_CONVERGENT_CONFERENCE;
import static com.huawei.ecterminalsdk.base.TsdkConfMediaType.TSDK_E_CONF_MEDIA_VIDEO;
import static com.huawei.ecterminalsdk.base.TsdkConfMediaType.TSDK_E_CONF_MEDIA_VOICE;
import static com.huawei.ecterminalsdk.base.TsdkConfRecordStatus.TSDK_E_CONF_RECORD_START;
import static com.huawei.ecterminalsdk.base.TsdkConfRecordStatus.TSDK_E_CONF_RECORD_STOP;

/**
 * This class is about meeting function management.
 * 会议服务管理类
 */
public class MeetingMgr implements IMeetingMgr{

    private static final String TAG = MeetingMgr.class.getSimpleName();

    private static MeetingMgr mInstance;

    /**
     * UI回调
     */
    private IConfNotification mConfNotification;

    /**
     * 当前正在召开的会议
     */

    private TsdkConference currentConference;


    /**
     * 会议基础信息
     */
    private ConfBaseInfo confBaseInfo;


    /**
     * 与会者列表
     */
    private List<Member> memberList;
    private Member self;

    /**
     * SMC组网下静音会场标识
     */
    private boolean isMuteConf;

    /**
     *组网模式，为适配SMC组网配置
     */
    private TsdkConfEnvType confEnvType;

    /**
     * 会议协议类型
     */
    private ConfConstant.ConfProtocol confProtocol;

    /**
     * 会议中的发言人
     */
    private String[] speakers;

    /**
     * 是否是匿名入会，用于判断会议界面按钮的显示
     */
    private boolean isAnonymous = false;

    /**
     * 获取匿名会议临时账号是否成功，用于判断会议界面按钮的显示
     */
    private boolean getTempUserSuccess = false;

    /**
     * 是否是通话转会议
     */
    private boolean callTransferToConference = false;

    /**
     * 是否正在桌面共享
     */
    private boolean isShareAs = false;

    /**
     * 共享文档和白板的id
     */
    private List<Integer> documentId = new ArrayList<>();

    private MeetingMgr()
    {
        this.confBaseInfo = new ConfBaseInfo();
    }

    public static MeetingMgr getInstance()
    {
        if (mInstance == null)
        {
            mInstance = new MeetingMgr();
        }
        return mInstance;
    }

    public void regConfServiceNotification(IConfNotification confNotification)
    {
        this.mConfNotification = confNotification;
    }



//    public void setJoinConfNumber(String joinConfNumber) {
//        this.joinConfNumber = joinConfNumber;
//    }


//    public boolean isInConference() {
//        if (null == currentConference)
//        {
//            return false;
//        }
//        return true;
//    }

    public int getCurrentConferenceCallID() {
        if (null == currentConference)
        {
            return 0;
        }

        TsdkCall tsdkCall = currentConference.getCall();
        if (null != tsdkCall) {
            return tsdkCall.getCallInfo().getCallId();
        }
        else
        {
            return 0;
        }
    }

//    public void setCurrentConferenceCallID(int callID) {
//        if (null == currentConference)
//        {
//            return;
//        }
//        //currentConference.setCallID(callID);
//    }

    public List<Member> getCurrentConferenceMemberList() {
        if (null == currentConference)
        {
            return null;
        }
        return getMemberList();
    }

    public Member getCurrentConferenceSelf() {
        if (null == currentConference)
        {
            return null;
        }
        return getSelf();
    }

    public ConfBaseInfo getCurrentConferenceBaseInfo() {
        if (null == currentConference)
        {
            return null;
        }
        return this.getConfBaseInfo();
    }

//
//    public void updateCurrentConferenceBaseInfo(ConfDetailInfo confDetailInfo) {
//        if (null == currentConference)
//        {
//            return;
//        }
//
//        if (getCurrentConferenceBaseInfo().getConfID().equals(confDetailInfo.getConfID()))
//        {
//            //currentConference.updateConfInfo(confDetailInfo);
//        }
//    }
//



//    public boolean isInDataConf()
//    {
//        if (null == currentConference)
//        {
//            return false;
//        }
//
//        return true;
//    }

    public Member getSelf() {
        return self;
    }

    public void setSelf(Member self) {
        this.self = self;
    }


    public List<Member> getMemberList() {
        return memberList;
    }

    public void setMemberList(List<Member> memberList) {
        this.memberList = memberList;
    }


    public ConfBaseInfo getConfBaseInfo() {
        return confBaseInfo;
    }

    public void setConfBaseInfo(ConfBaseInfo confBaseInfo) {
        this.confBaseInfo = confBaseInfo;
    }


    private boolean judgeMemberWhetherOnline(Member member, List<TsdkAttendee> attendeeList) {
        for (TsdkAttendee attendeeInfo : attendeeList) {
            if (member.getNumber().equals(attendeeInfo.getBaseInfo().getNumber())) {
                return true;
            }
        }
        return false;
    }

    public TsdkConfEnvType getConfEnvType() {
        return confEnvType;
    }

    public void setConfEnvType(TsdkConfEnvType confEnvType) {
        this.confEnvType = confEnvType;
    }

    public ConfConstant.ConfProtocol getConfProtocol() {
        return confProtocol;
    }

    public void setConfProtocol(ConfConstant.ConfProtocol confProtocol) {
        this.confProtocol = confProtocol;
    }

    public boolean isAnonymous() {
        return isAnonymous;
    }

    public void setAnonymous(boolean anonymous) {
        isAnonymous = anonymous;
    }

    public boolean isGetTempUserSuccess() {
        return getTempUserSuccess;
    }

    public void setGetTempUserSuccess(boolean getTempUserSuccess) {
        this.getTempUserSuccess = getTempUserSuccess;
    }

    public String[] getSpeakers() {
        if (null != speakers)
        {
            return speakers.clone();
        }

        return new String[0];
    }

    /**
     * This method is used to update conf info
     * 更新会议信息
     * @param conference 会议信息
     */
    public void updateConfInfo(TsdkConference conference)
    {
        this.confEnvType = conference.getConfEnvType();
        confBaseInfo.setSize(conference.getSize());
        confBaseInfo.setConfID(conference.getConfId());
        confBaseInfo.setSubject(conference.getSubject());
        confBaseInfo.setSchedulerName(conference.getScheduserName());
        confBaseInfo.setConfState(ConfConvertUtil.convertConfctrlConfState(conference.getConfState()));
        confBaseInfo.setMediaType(conference.getConfMediaType());
        confBaseInfo.setLock(conference.isLock());
        confBaseInfo.setRecord(conference.isRecord());
        confBaseInfo.setSupportRecord(conference.isSupportRecordBroadcast());

        if (TSDK_E_CONF_ENV_HOSTED_CONVERGENT_CONFERENCE == conference.getConfEnvType()){
            confBaseInfo.setMuteAll(conference.isAllMute());
        }else {
            confBaseInfo.setMuteAll(this.isMuteConf);
        }



        LogUtil.i(TAG, "ConfState." + confBaseInfo.getConfState());

        if (memberList == null) {
            memberList = new ArrayList<>();
        }

        Member temp;
        for (TsdkAttendee attendee : conference.getAttendeeList()) {
            temp = getMemberByNumber(attendee.getBaseInfo().getNumber());
            if (temp == null) {
                Member member = ConfConvertUtil.convertAttendeeInfo(attendee);
                if (attendee.getStatusInfo().getIsSelf() == 1) {
                    member.setSelf(true);
                    this.setSelf(member);
                }

                if (member.getRole() == TsdkConfRole.TSDK_E_CONF_ROLE_CHAIRMAN) {
                    memberList.add(0, member);
                } else {
                    memberList.add(member);
                }

            } else {
                temp.update(attendee);
                if ((temp.getRole() == TsdkConfRole.TSDK_E_CONF_ROLE_CHAIRMAN) && (memberList.indexOf(temp) != 0)){
                    memberList.remove(temp);
                    memberList.add(0, temp);
                }
            }
        }

        // 暂只支持全量更新
        if (conference.getConfAttendeeUpdateType() == TsdkConfAttendeeUpdateType.TSDK_E_CONF_ATTENDEE_UPDATE_ALL) {
            for (Member member : memberList) {
                boolean isOnline = judgeMemberWhetherOnline(member, conference.getAttendeeList());
                if (isOnline != true) {
                    member.setStatus(ConfConstant.ParticipantStatus.LEAVED);
                    member.setRole(TsdkConfRole.TSDK_E_CONF_ROLE_ATTENDEE);
                    member.setHost(false);
                    member.setPresent(false);
                }
            }
        }

//        if ((self.getRole() == TsdkConfRole.TSDK_E_CONF_ROLE_ATTENDEE) && (self.getStatus() == ConfConstant.ParticipantStatus.LEAVED)) {
//            confBaseInfo.setConfState(ConfConstant.ConfConveneStatus.DESTROYED);
//        }

        return;
    }

    /**
     * This method is used to get a speaker by volume.
     * @param speakerInfo 发言人信息
     * @return
     */
    public String[] updateSpeaker(TsdkConfSpeakerInfo speakerInfo)
    {
        this.speakers = new String[speakerInfo.getSpeakerNum()];
        List<TsdkConfSpeaker> confSpeakers = speakerInfo.getSpeakers();

        Collections.sort(confSpeakers, new Comparator<TsdkConfSpeaker>() {
            @Override
            public int compare(TsdkConfSpeaker o1, TsdkConfSpeaker o2) {
                int result = o2.getSpeakingVolume() - o1.getSpeakingVolume();
                return result;
            }
        });

        if (speakerInfo.getSpeakerNum() >= 2)
        {
            this.speakers[0] = confSpeakers.get(0).getBaseInfo().getDisplayName();
            this.speakers[1] = confSpeakers.get(1).getBaseInfo().getDisplayName();
        }
        else
        {
            this.speakers[0] = confSpeakers.get(0).getBaseInfo().getDisplayName();
        }

        return speakers.clone();
    }

    /**
     * This method is used to book instant conference or reserved conference
     * @param bookConferenceInfo 创会信息
     * @return
     */
    public int bookConference(BookConferenceInfo bookConferenceInfo)
    {
        Log.i(TAG, "bookConference.");

        if (bookConferenceInfo == null)
        {
            Log.e(TAG, "booKConferenceInfo obj is null");
            return -1;
        }

        TsdkBookConfInfo bookConfInfo = new TsdkBookConfInfo();

        if(bookConferenceInfo.isInstantConference()){
            bookConfInfo.setConfType(TsdkConfType.TSDK_E_CONF_INSTANT);
            bookConfInfo.setIsAutoProlong(1);
        }else {
            bookConfInfo.setConfType(TsdkConfType.TSDK_E_CONF_RESERVED);
        }

        // 创建会议时设置为高清会议
        bookConfInfo.setIsHdConf(1);
        bookConfInfo.setSubject(bookConferenceInfo.getSubject());
        bookConfInfo.setConfMediaType(bookConferenceInfo.getMediaType());
        bookConfInfo.setStartTime(bookConferenceInfo.getStartTime());
        bookConfInfo.setDuration(bookConferenceInfo.getDuration());
        bookConfInfo.setSize(bookConferenceInfo.getSize());
        bookConfInfo.setIsAutoRecord(bookConferenceInfo.getIs_auto()? 1:0);
        bookConfInfo.setRecordMode(bookConferenceInfo.getRecordType());

        List<TsdkAttendeeBaseInfo> attendeeList = ConfConvertUtil.memberListToAttendeeList(bookConferenceInfo.getMemberList());
        bookConfInfo.setAttendeeList(attendeeList);
        bookConfInfo.setAttendeeNum(attendeeList.size());

        //The other parameters are optional, using the default value
        //其他参数可选，使用默认值即可
        bookConfInfo.setLanguage(TsdkConfLanguage.TSDK_E_CONF_LANGUAGE_EN_US);

        int result = TsdkManager.getInstance().getConferenceManager().bookConference(bookConfInfo);
        if (result != 0)
        {
            Log.e(TAG, "bookReservedConf result ->" + result);
            return  result;
        }

        return 0;
    }

    /**
     * This method is used to query my conference list
     * 查询会议列表
     * @param myRight 会议类型
     * @return
     */
    public int queryMyConfList(ConfConstant.ConfRight myRight)
    {
        Log.i(TAG, "query my conf list.");

        TsdkConfRight tupConfRight;
        switch (myRight)
        {
            case MY_CREATE:
                tupConfRight = TsdkConfRight.TSDK_E_CONF_RIGHT_CREATE;
                break;

            case MY_JOIN:
                tupConfRight = TsdkConfRight.TSDK_E_CONF_RIGHT_JOIN;
                break;

            case MY_CREATE_AND_JOIN:
                tupConfRight = TsdkConfRight.TSDK_E_CONF_RIGHT_CREATE_JOIN;
                break;
            default:
                tupConfRight = TsdkConfRight.TSDK_E_CONF_RIGHT_CREATE_JOIN;
                break;
        }

        TsdkQueryConfListReq queryReq = new TsdkQueryConfListReq();
        queryReq.setPageSize(ConfConstant.PAGE_SIZE);
        queryReq.setPageIndex(1);    //当前Demo只查询一页，实际可根据需要分多页查询
        queryReq.setIsIncludeEnd(0); //不包含已结束的会议
        queryReq.setConfRight(tupConfRight);

        int result = TsdkManager.getInstance().getConferenceManager().queryConferenceList(queryReq);
        if (result != 0)
        {
            Log.e(TAG, "getConfList result ->" + result);
            return  result;
        }
        return 0;
    }

    /**
     * This method is used to query the conference detail
     * 查询会议详情
     * @param confID
     * @return
     */
    public int queryConfDetail(String confID)
    {
        Log.i(TAG,  "query conf detail");

        TsdkQueryConfDetailReq queryReq = new TsdkQueryConfDetailReq();
        queryReq.setConfId(confID);
        queryReq.setPageIndex(1);
        queryReq.setPageSize(20);

        int result = TsdkManager.getInstance().getConferenceManager().queryConferenceDetail(queryReq);
        if (result != 0)
        {
            Log.e(TAG, "getConfInfo result ->" + result);
            return  result;
        }
        return result;
    }


    /**
     * This method is used to join conference
     * 加入会议
     *
     * @param confJoinParam     会议参数
     * @param isVideo           是否是视频
     * @param joinNumber        加入号码
     * @return
     */
    public int joinConf(TsdkConfJoinParam confJoinParam, boolean isVideo, String joinNumber)
    {
        Log.i(TAG,  "join conf.");

        int result = TsdkManager.getInstance().getConferenceManager().joinConference(confJoinParam, isVideo, joinNumber);
        if (result != 0)
        {
            Log.e(TAG, "joinConf result ->" + result);
            currentConference = null;
            return result;
        }

        return 0;
    }


    /**
     * This method is used to accept conference
     * 接受会议邀请
     *
     * @param isVideo           是否是视频
     * @return
     */
    public int acceptConf(boolean isVideo)
    {
        Log.i(TAG,  "accept conf.");

        if (null == currentConference)
        {
            Log.i(TAG,  "accept conf, currentConference is null ");
            return 0;
        }

        int result = currentConference.acceptConference(isVideo);
        if (result == 0) {
            Log.i(TAG,  "accept conf");
        }

        return result;
    }


    /**
     * This method is used to reject conference
     * 拒绝会议邀请
     *
     * @return
     */
    public int rejectConf()
    {
        Log.i(TAG,  "reject conf.");

        if (null == currentConference)
        {
            Log.i(TAG,  "reject conf, currentConference is null ");
            return 0;
        }

        int result = currentConference.rejectConference();
        if (result == 0) {
            currentConference = null;
        }

        return result;
    }



    /**
     * This method is used to leave conf
     * 离会
     * @return
     */
    public int leaveConf()
    {
        if (null == currentConference)
        {
            Log.i(TAG,  "leave conf, currentConference is null ");
            return 0;
        }

        int result = currentConference.leaveConference();
        if (result == 0) {
            currentConference = null;

            // 离开会议后将数据会议的共享状态初始化，重新进入会议后此共享状态会重新推送
            isShareAs = false;
            documentId.clear();
        }

        setAnonymous(false);
        setGetTempUserSuccess(false);
        callTransferToConference = false;

        return result;
    }

    /**
     * This method is used to end conf
     * 结束会议
     * @return
     */
    public int endConf()
    {
        if (null == currentConference)
        {
            Log.i(TAG,  "end conf, currentConference is null ");
            return 0;
        }

        int result =  currentConference.endConference();
        if (result == 0) {
            currentConference = null;

            // 结束会议后将数据会议的共享状态初始化，重新进入会议后此共享状态会重新推送
            isShareAs = false;
            documentId.clear();
        }

        setAnonymous(false);
        setGetTempUserSuccess(false);
        callTransferToConference = false;

        return result;
    }

    /**
     * This method is used to add attendee
     * 添加与会者
     * @param attendee 与会者信息
     * @return
     */
    public int addAttendee(Member attendee)
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "add attendee failed, currentConference is null ");
            return -1;
        }

        TsdkAttendeeBaseInfo attendeeBaseInfo = new TsdkAttendeeBaseInfo();
        attendeeBaseInfo.setNumber(attendee.getNumber());
        attendeeBaseInfo.setDisplayName(attendee.getDisplayName());
        attendeeBaseInfo.setAccountId(attendee.getAccountId());
        attendeeBaseInfo.setRole(attendee.getRole());

        List<TsdkAttendeeBaseInfo> attendeeList = new ArrayList<>();
        attendeeList.add(attendeeBaseInfo);

        TsdkAddAttendeesInfo addAttendeeInfo = new TsdkAddAttendeesInfo();
        addAttendeeInfo.setAttendeeList(attendeeList);
        addAttendeeInfo.setAttendeeNum(attendeeList.size());

        int result =  currentConference.addAttendee(addAttendeeInfo);

        return result;
    }


    /**
     * This method is used to remove attendee
     * 移除与会者
     * @param attendee 与会者信息
     * @return
     */
    public int removeAttendee(Member attendee)
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "remove attendee failed, currentConference is null ");
            return -1;
        }

        int result =  currentConference.removeAttendee(attendee.getNumber());

        return result;
    }

    /**
     * This method is used to hang up attendee
     * 挂断与会者(预留，暂不使用)
     * @param attendee 与会者信息
     * @return
     */
    public int hangupAttendee(Member attendee)
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "hangup attendee failed, currentConference is null ");
            return -1;
        }

        int result =  currentConference.handupAttendee(attendee.getNumber());

        return result;
    }

    /**
     * This method is used to redial attendee
     * 重播与会者(预留，暂不使用)
     * @param attendee 与会者信息
     * @return
     */
    public int redialAttendee(Member attendee)
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "redial attendee failed, currentConference is null ");
            return -1;
        }

        int result =  currentConference.redialAttendee(attendee.getNumber());

        return result;
    }

    /**
     * This method is used to lock conf
     * 锁定会议
     * @param isLock 是否锁定
     * @return
     */
    public int lockConf(boolean isLock)
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "mute conf failed, currentConference is null ");
            return -1;
        }

        int result =  currentConference.lockConference(isLock);

        return result;
    }

    /**
     * This method is used to record conference
     * 录播会议
     * @param isRecode 是否开启录制
     * @return
     */
    public int recordConf(boolean isRecode)
    {
        int result;

        if (null == currentConference) {
            Log.e(TAG, "record  conf failed, currentConference is null ");
            return -1;
        }

        if (isRecode) {
            result = currentConference.setRecordBroadcast(TSDK_E_CONF_RECORD_START);
        } else {
            result = currentConference.setRecordBroadcast(TSDK_E_CONF_RECORD_STOP);
        }

        return result;
    }


    /**
     * This method is used to mute attendee
     * 静音与会者
     * @param attendee 与会者信息
     * @param isMute 是否静音
     * @return
     */
    public int muteAttendee(Member attendee, boolean isMute)
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "mute attendee failed, currentConference is null ");
            return -1;
        }

        int result =  currentConference.muteAttendee(attendee.getNumber(), isMute);

        //TODO
        return result;
    }

    /**
     * This method is used to mute conf
     * 静音会议
     * @param isMute 是否静音
     * @return
     */
    public int muteConf(boolean isMute)
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "mute conf failed, currentConference is null ");
            return -1;
        }

        int result =  currentConference.muteConference(isMute);

        return result;
    }

    /**
     * This method is used to release chairman
     * 是否主席
     * @return
     */
    public int releaseChairman()
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "release chairman failed, currentConference is null ");
            return -1;
        }

        int result =  currentConference.releaseChairman();

        return result;
    }

    /**
     * This method is used to request chairman
     * 请求主席
     * @param chairmanPassword 主席密码
     * @return
     */
    public int requestChairman(String chairmanPassword)
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "request chairman failed, currentConference is null ");
            return -1;
        }

        int result =  currentConference.requestChairman(chairmanPassword);

        return result;
    }

    /**
     * [en]This method is used to set conference presenter.
     * [cn]设置主讲人
     *
     * @param attendee   与会者信息
     * @return int
     */
    public int setPresenter(Member attendee)
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "set presenter failed, currentConference is null ");
            return -1;
        }

        int result = currentConference.setPresenter(attendee.getNumber());

        return result;
    }

    /**
     * This method is used to set host
     * 设置主席
     * @param attendee 与会者信息
     * @return
     */
    public int setHost(Member attendee)
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "set presenter failed, currentConference is null ");
            return -1;
        }

        //int result =  currentConference.setHost(attendee);

        //TODO
        //return result;
        return 0;
    }

    /**
     * This method is used to hand up
     * 设置举手
     * @param handUp 是否举手
     * @param attendee 与会者信息
     * @return
     */
    public int handup(boolean handUp, Member attendee)
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "request chairman failed, currentConference is null ");
            return -1;
        }

        int result =  currentConference.setHandup(attendee.getNumber(), handUp);

        //TODO
        return result;
    }

    /**
     * This method is used to postpone conf
     * 延长会议
     * @param time 延长时长
     * @return
     */
    public int postpone(int time)
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "postpone conf failed, currentConference is null ");
            return -1;
        }

        //int result =  currentConference.postpone(time);

        //TODO
        return 0;
    }

    /**
     * This method is used to join a conference by anonymous
     * 加入匿名会议
     *
     * @param confId            会议ID
     * @param displayName       与会者名称
     * @param confPassword      会议密码
     * @param serviceAddress    匿名会议地址
     * @param servicePort       匿名会议端口
     * @param isVPN             是否VPN
     * @return
     */
    public int joinConferenceByAnonymous(String confId, String displayName,
                                         String confPassword, String serviceAddress,
                                         String servicePort,boolean isVPN)
    {
        Log.i(TAG,  "joinConferenceByAnonymous");

        //设置本端IP
        String localIpAddress = DeviceManager.getLocalIpAddress(isVPN);
        TsdkLocalAddress localAddress = new TsdkLocalAddress(localIpAddress);
        TsdkManager.getInstance().setConfigParam(localAddress);

        TsdkConfAnonymousJoinParam anonymousParam = new TsdkConfAnonymousJoinParam();
        anonymousParam.setConfId(confId);
        anonymousParam.setConfPassword(confPassword);
        anonymousParam.setDisplayName(displayName);
        anonymousParam.setServerAddr(serviceAddress);
        anonymousParam.setServerPort(Integer.parseInt(servicePort));
        anonymousParam.setUserId(1);

        int result = TsdkManager.getInstance().getConferenceManager().joinConferenceByAnonymous(anonymousParam);

        if (result != 0)
        {
            Log.e(TAG, "join anonymous conference result ->" + result);
            return result;
        }

        return 0;
    }


    /**
     * This method is used to call transfer to conference.
     * 普通通话转成会议
     *
     * @param call_id               [en]Indicates call id.
     *                              [cn]呼叫ID
     * @return                      [en]If success return TSDK_SUCCESS,otherwise return corresponding error code
     *                              [cn]成功返回TSDK_SUCCESS，失败返回相应错误码
     */
    public int callTransferToConference(int call_id){

        Log.i(TAG, "callTransferToConference.");

        Session callSession = CallMgr.getInstance().getCallSessionByCallID(call_id);
        if (callSession == null)
        {
            Log.e(TAG, "call Session is null.");
            return -1;
        }
        //用于转会议失败之后，恢复原通话。
        CallMgr.getInstance().setOriginal_CallId(call_id);
        callTransferToConference = true;

        TsdkCall tsdkCall =  callSession.getTsdkCall();
        if (tsdkCall == null)
        {
            Log.e(TAG, "call is invalid.");
            return -1;
        }

        TsdkBookConfInfo bookConfInfo = new TsdkBookConfInfo();

        bookConfInfo.setConfType(TsdkConfType.TSDK_E_CONF_INSTANT);
        bookConfInfo.setIsAutoProlong(1);

        bookConfInfo.setSubject(LoginMgr.getInstance().getAccount() + "'s Meeting");
        if (1 == tsdkCall.getCallInfo().getIsVideoCall()) {
            bookConfInfo.setConfMediaType(TSDK_E_CONF_MEDIA_VIDEO);
        }else {
            bookConfInfo.setConfMediaType(TSDK_E_CONF_MEDIA_VOICE);
        }

        bookConfInfo.setSize(2);

        List<TsdkAttendeeBaseInfo> attendeeList = new ArrayList<>();
        TsdkAttendeeBaseInfo confctrlAttendee = new TsdkAttendeeBaseInfo();
        //confctrlAttendee.setNumber(callInfo.getPeerNumber());
        confctrlAttendee.setRole(TsdkConfRole.TSDK_E_CONF_ROLE_ATTENDEE);
        attendeeList.add(confctrlAttendee);

        bookConfInfo.setAttendeeList(attendeeList);
        bookConfInfo.setAttendeeNum(attendeeList.size());

        //The other parameters are optional, using the default value
        //其他参数可选，使用默认值即可
        bookConfInfo.setLanguage(TsdkConfLanguage.TSDK_E_CONF_LANGUAGE_EN_US);

        int result = TsdkManager.getInstance().getConferenceManager().p2pTransferToConference(tsdkCall, bookConfInfo);
        if (result != 0) {
            Log.e(TAG, "call transfer to conference is return failed, result = " + result);
        }
        return result;
    }

    /**
     * This method is used to set conf mode
     * 设置会议类型
     * @param confVideoMode 会议类型
     * @return
     */
    public int setConfMode(ConfConstant.ConfVideoMode confVideoMode)
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "set conf mode failed, currentConference is null ");
            return -1;
        }

        TsdkConfVideoMode tsdkConfVideoMode = TsdkConfVideoMode.TSDK_E_CONF_VIDEO_BROADCAST;

        switch (confVideoMode)
        {
            case CONF_VIDEO_BROADCAST:
                tsdkConfVideoMode = TsdkConfVideoMode.TSDK_E_CONF_VIDEO_BROADCAST;
                break;
            case CONF_VIDEO_VAS:
                tsdkConfVideoMode = TsdkConfVideoMode.TSDK_E_CONF_VIDEO_VAS;
                break;
            case CONF_VIDEO_FREE:
                tsdkConfVideoMode = TsdkConfVideoMode.TSDK_E_CONF_VIDEO_FREE;
                break;
            default:
                break;
        }

        int result = currentConference.setVideoMode(tsdkConfVideoMode);

        return result;
    }

    /**
     * This method is used to broadcast attendee
     * 广播与会者
     * @param attendee 与会者信息
     * @param isBroadcast 是否广播
     * @return
     */
    public int broadcastAttendee(Member attendee, boolean isBroadcast)
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "broadcast attendee failed, currentConference is null ");
            return -1;
        }

        int result;

        if (isBroadcast)
        {
            result = currentConference.broadcastAttendee(attendee.getNumber(), isBroadcast);
        }
        else
        {
            //取消广播在mediaX环境下必须填空 SMC下才需要写与会者号码
            result = currentConference.broadcastAttendee("", isBroadcast);
        }

        return result;
    }

    /**
     * This method is used to watch attendee
     * 观看与会者
     * @param attendee 与会者信息
     * @return
     */
    public int watchAttendee(Member attendee)
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "broadcast attendee failed, currentConference is null ");
            return -1;
        }

        TsdkWatchAttendeesInfo watchAttendeesInfo = new TsdkWatchAttendeesInfo();

        TsdkWatchAttendees attendees = new TsdkWatchAttendees();
        attendees.setNumber(attendee.getNumber());

        List<TsdkWatchAttendees> list = new ArrayList<>();
        list.add(attendees);

        watchAttendeesInfo.setWatchAttendeeList(list);
        watchAttendeesInfo.setWatchAttendeeNum(list.size());

        int result = currentConference.watchAttendee(watchAttendeesInfo);

        return result;
    }

    /**
     * This method is used to upgrade conf
     * 会议升级
     * @return
     */
    public int upgradeConf()
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "upgrade conf failed, currentConference is null ");
            return -1;
        }

        int result =  currentConference.upgradeConference("");

        //TODO
        return result;
    }

    /**
     * This method is used to join data conf
     * 加入数据会议
     * @return
     */
    public int joinDataConf()
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "join data conf failed, currentConference is null ");
            return -1;
        }

        int result =  currentConference.joinDataConference();

        return result;
    }

    /**
     * 升级为数据会议前检查
     */
    public void checkUpgradeDataConf()
    {
        if (null == currentConference)
        {
            return;
        }

        //currentConference.checkUpgradeDataConf();
    }

    public boolean judgeInviteFormMySelf(String confID)
    {
        if ((confID == null) || (confID.equals("")))
        {
            return false;
        }

        //TODO
        if (currentConference != null && getCurrentConferenceBaseInfo().getConfID().equals(confID))
        {
            return true;
        }

        return false;
    }

    private Member getMemberByNumber(String number)
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "upgrade conf failed, currentConference is null ");
            return null;
        }

        Member temp = new Member(number);
        int index = memberList.indexOf(temp);
        if (index == -1) {
            return null;
        } else {
            return memberList.get(index);
        }
    }

    public void attachSurfaceView(ViewGroup container, Context context)
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "attach surface view failed, currentConference is null ");
            return;
        }
        currentConference.attachSurfaceView(container, context);
    }

    public void sendConfMessage(String message)
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "send chat failed, currentConference is null ");
            return;
        }
        TsdkConfChatMsgInfo chatMsgInfo = new TsdkConfChatMsgInfo();
        chatMsgInfo.setChatType(TsdkConfChatType.TSDK_E_CONF_CHAT_PUBLIC);
        chatMsgInfo.setChatMsg(message);
        if (null == self)
        {
            chatMsgInfo.setSenderDisplayName(LoginMgr.getInstance().getAccount());
        }
        else
        {
            chatMsgInfo.setSenderDisplayName(self.getDisplayName());
        }
        currentConference.sendChatMsg(chatMsgInfo);
    }



    /*********************************************** TSDK CALL BACK ********************************************************************************/


    /**
     * 创会结果
     * @param result
     * @param confBaseInfo
     */
    public void handleBookConfResult(TsdkCommonResult result, TsdkConfBaseInfo confBaseInfo){
        Log.i(TAG, "onBookReservedConfResult");
        if ((result == null) || (confBaseInfo == null))
        {
            Log.e(TAG, "book conference is failed, unknown error.");
            if (callTransferToConference){
                Session callSession = CallMgr.getInstance().getCallSessionByCallID(CallMgr.getInstance().getOriginal_CallId());
                if (callSession != null)
                {
                    callSession.unHoldCall();
                }
            }
            mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.BOOK_CONF_FAILED, -1);
            return;
        }

        if (result.getResult() != 0)
        {
            Log.e(TAG, "book conference is failed, return ->" + result.getResult());
            if (callTransferToConference){
                Session callSession = CallMgr.getInstance().getCallSessionByCallID(CallMgr.getInstance().getOriginal_CallId());
                if (callSession != null)
                {
                    callSession.unHoldCall();
                }
            }
            mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.BOOK_CONF_FAILED, result.getResult());
            return;
        }

        Log.i(TAG, "book conference is success.");
        if (callTransferToConference){
            CallMgr.getInstance().setResumeHold(true);
            mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.CALL_TRANSFER_TO_CONFERENCE, result.getResult());
        }else {
            mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.BOOK_CONF_SUCCESS, result.getResult());
        }
    }



    /**
     * 查询会议列表结果
     * @param result
     * @param confList
     */
    public void handleQueryConfListResult(TsdkCommonResult result, TsdkConfListInfo confList){

        Log.i(TAG, "onGetConfListResult");
        if (result == null)
        {
            Log.e(TAG, "get conference list is failed, unknown error.");
            mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.QUERY_CONF_LIST_FAILED, -1);
            return;
        }
        else if (result.getResult() != 0)
        {
            Log.e(TAG, "get conference list is failed, return ->" + result.getReasonDescription());
            mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.QUERY_CONF_LIST_FAILED, result.getResult());
            return;
        }

        List<ConfBaseInfo> confBaseInfoList = new ArrayList<>();

        List<TsdkConfBaseInfo> tsdkConfBaseInfos = confList.getConfInfoList();
        if (null != tsdkConfBaseInfos) {
            for (TsdkConfBaseInfo confInfo : tsdkConfBaseInfos) {
                ConfBaseInfo confBaseInfo = new ConfBaseInfo();
                confBaseInfo.setSize(confInfo.getSize());

                confBaseInfo.setConfID(confInfo.getConfId());
                confBaseInfo.setSubject(confInfo.getSubject());
                confBaseInfo.setAccessNumber(confInfo.getAccessNumber());
                confBaseInfo.setChairmanPwd(confInfo.getChairmanPwd());
                confBaseInfo.setGuestPwd(confInfo.getGuestPwd());
                confBaseInfo.setSchedulerNumber(confInfo.getScheduserAccount());
                confBaseInfo.setSchedulerName(confInfo.getScheduserName());
                confBaseInfo.setStartTime(confInfo.getStartTime());
                confBaseInfo.setEndTime(confInfo.getEndTime());

                confBaseInfo.setMediaType(ConfConvertUtil.convertConfMediaType(confInfo.getConfMediaType()));
                confBaseInfo.setConfState(ConfConvertUtil.convertConfctrlConfState(confInfo.getConfState()));

                confBaseInfoList.add(confBaseInfo);
            }
        }

        mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.QUERY_CONF_LIST_SUCCESS, confBaseInfoList);
    }


    /**
     * 获取会议详情处理
     * @param result
     * @param tsdkConfDetailInfo
     */
    public void handleQueryConfDetailResult(TsdkCommonResult result, TsdkConfDetailInfo tsdkConfDetailInfo){

        Log.i(TAG, "onGetConfInfoResult");
        if ((result == null) || (tsdkConfDetailInfo == null))
        {
            Log.e(TAG, "get conference detail is failed, unknown error.");
            mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.QUERY_CONF_DETAIL_FAILED, -1);
            return;
        }

        if (result.getResult() != 0)
        {
            Log.e(TAG, "get conference detail is failed, return ->" + result.getResult());
            mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.QUERY_CONF_DETAIL_FAILED, result.getResult());
            return;
        }

        ConfDetailInfo confDetailInfo = new ConfDetailInfo();
        confDetailInfo.setSize(tsdkConfDetailInfo.getConfInfo().getSize());

        confDetailInfo.setConfID(tsdkConfDetailInfo.getConfInfo().getConfId());
        confDetailInfo.setSubject(tsdkConfDetailInfo.getConfInfo().getSubject());
        confDetailInfo.setAccessNumber(tsdkConfDetailInfo.getConfInfo().getAccessNumber());
        confDetailInfo.setChairmanPwd(tsdkConfDetailInfo.getConfInfo().getChairmanPwd());
        confDetailInfo.setGuestPwd(tsdkConfDetailInfo.getConfInfo().getGuestPwd());
        confDetailInfo.setSchedulerNumber(tsdkConfDetailInfo.getConfInfo().getScheduserAccount());
        confDetailInfo.setSchedulerName(tsdkConfDetailInfo.getConfInfo().getScheduserName());
        confDetailInfo.setStartTime(tsdkConfDetailInfo.getConfInfo().getStartTime());
        confDetailInfo.setEndTime(tsdkConfDetailInfo.getConfInfo().getEndTime());

        confDetailInfo.setMediaType(ConfConvertUtil.convertConfMediaType(tsdkConfDetailInfo.getConfInfo().getConfMediaType()));
        confDetailInfo.setConfState(ConfConvertUtil.convertConfctrlConfState(tsdkConfDetailInfo.getConfInfo().getConfState()));

        List<Member> memberList = ConfConvertUtil.convertAttendeeInfoList(tsdkConfDetailInfo.getAttendeeList());
        confDetailInfo.setMemberList(memberList);

        mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.QUERY_CONF_DETAIL_SUCCESS, confDetailInfo);
    }


    /**
     * 加入会议结果
     * @param tsdkConference
     * @param commonResult
     * @param tsdkJoinConfIndInfo
     */
    public void  handleJoinConfResult(TsdkConference tsdkConference, TsdkCommonResult commonResult, TsdkJoinConfIndInfo tsdkJoinConfIndInfo){

        Log.i(TAG, "handleJoinConfResult");
        if ((tsdkConference == null) || (commonResult == null)) {
            return;
        }

        CallMgr.getInstance().setResumeHold(false);

        int result = commonResult.getResult();
        if (result == 0)
        {
            this.currentConference = tsdkConference;
            this.memberList = null;
            this.self = null;

            if (isGetTempUserSuccess()){
                setAnonymous(true);
            }

            TsdkCall tsdkCall = tsdkConference.getCall();
            if (null != tsdkCall) {
                Session newSession = CallMgr.getInstance().getCallSessionByCallID(tsdkCall.getCallInfo().getCallId());
                if (null == newSession) {
                    newSession = new Session(tsdkCall);
                    CallMgr.getInstance().putCallSessionToMap(newSession);
                }

                if (tsdkCall.getCallInfo().getIsVideoCall() == 1) {
                    VideoMgr.getInstance().initVideoWindow(tsdkCall.getCallInfo().getCallId());
                }
            }

            if (ConfConvertUtil.convertConfMediaType(tsdkJoinConfIndInfo.getConfMediaType()) == TsdkConfMediaType.TSDK_E_CONF_MEDIA_VOICE
                    || ConfConvertUtil.convertConfMediaType(tsdkJoinConfIndInfo.getConfMediaType()) == TsdkConfMediaType.TSDK_E_CONF_MEDIA_VOICE_DATA)
            {
                mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.JOIN_VOICE_CONF_SUCCESS, tsdkConference.getHandle() + "");
            }
            else
            {
                mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.JOIN_VIDEO_CONF_SUCCESS, tsdkConference.getHandle() + "");
            }
        }
        else
        {
            if(callTransferToConference){
                Session callSession = CallMgr.getInstance().getCallSessionByCallID(CallMgr.getInstance().getOriginal_CallId());
                if (callSession != null)
                {
                    callSession.unHoldCall();
                }
            }
            mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.JOIN_CONF_FAILED, result);
        }

    }


    /**
     * 获取数据会议参数结果
     * @param tsdkConference
     * @param commonResult
     */
    public void  handleGetDataConfParamsResult(TsdkConference tsdkConference, TsdkCommonResult commonResult){

        Log.i(TAG, "handleJoinConfResult");
        if ((tsdkConference == null) || (commonResult == null)) {
            return;
        }

        int result = commonResult.getResult();

        mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.GET_DATA_CONF_PARAM_RESULT, result);
    }


    /**
     * 加入数据会议结果
     * @param tsdkConference
     * @param commonResult
     */
    public void  handleJoinDataConfResult(TsdkConference tsdkConference, TsdkCommonResult commonResult){

        Log.i(TAG, "handleJoinDataConfResult");
        if ((tsdkConference == null) || (commonResult == null)) {
            return;
        }

        int result = commonResult.getResult();

        mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.JOIN_DATA_CONF_RESULT, result);
    }


    /**
     * This method is used to conf ctrl operation result.
     * 会控操作结果
     *
     * @param conference    Indicates conference info.
     *                      会议信息
     * @param result        Indicates conference info.
     *                      会控操作结果
     */
    public void handleConfctrlOperationResult(TsdkConference conference, TsdkConfOperationResult result)
    {
        Log.i(TAG, "handleConfctrlOperationResult");

        if (null == conference || null == result)
        {
            return;
        }

        int ret = result.getReasonCode();

        if (ret != 0)
        {
            Log.e(TAG, "conf ctrl operation failed: " + result.getDescription());
        }
        switch (TsdkConfOperationType.enumOf(result.getOperationType()))
        {
            //升级会议
            case TSDK_E_CONF_UPGRADE_CONF:
                mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.UPGRADE_CONF_RESULT, ret);
                break;
            //闭音会场
            case TSDK_E_CONF_MUTE_CONF:
                this.isMuteConf = true;
                mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.MUTE_CONF_RESULT, ret);
                break;
            //取消闭音
            case TSDK_E_CONF_UNMUTE_CONF:
                this.isMuteConf = false;
                mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.UN_MUTE_CONF_RESULT, ret);
                break;
            //锁定会议
            case TSDK_E_CONF_LOCK_CONF:
                mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.LOCK_CONF_RESULT, ret);
                break;
            //取消锁定
            case TSDK_E_CONF_UNLOCK_CONF:
                mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.UN_LOCK_CONF_RESULT, ret);
                break;
            //添加与会者
            case TSDK_E_CONF_ADD_ATTENDEE:
                mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.ADD_ATTENDEE_RESULT, ret);
                break;
            //删除与会者
            case TSDK_E_CONF_REMOVE_ATTENDEE:
                mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.DEL_ATTENDEE_RESULT, ret);
                break;

            //闭音与会者
            case TSDK_E_CONF_MUTE_ATTENDEE:
                mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.MUTE_ATTENDEE_RESULT, ret);
                break;
            //取消闭音与会者
            case TSDK_E_CONF_UNMUTE_ATTENDEE:
                mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.UN_MUTE_ATTENDEE_RESULT, ret);
                break;
            //设置举手
            case TSDK_E_CONF_SET_HANDUP:
                mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.HAND_UP_RESULT, ret);
                break;
            //取消设置举手
            case TSDK_E_CONF_CANCLE_HANDUP:
                mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.CANCEL_HAND_UP_RESULT, ret);
                break;
            //设置会议视频模式
            case TSDK_E_CONF_SET_VIDEO_MODE:
                mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.SET_CONF_MODE_RESULT, ret);
                break;
            //选看与会者
            case TSDK_E_CONF_WATCH_ATTENDEE:
                mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.WATCH_ATTENDEE_RESULT, ret);
                break;
            //广播与会者
            case TSDK_E_CONF_BROADCAST_ATTENDEE:
                mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.BROADCAST_ATTENDEE_RESULT, ret);
                break;
            //取消广播与会者
            case TSDK_E_CONF_CANCEL_BROADCAST_ATTENDEE:
                mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.CANCEL_BROADCAST_RESULT, ret);
                break;
            //申请主席权限
            case TSDK_E_CONF_REQUEST_CHAIRMAN:
                mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.REQUEST_CHAIRMAN_RESULT, ret);
                break;
            //释放主席权限
            case TSDK_E_CONF_RELEASE_CHAIRMAN:
                mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.RELEASE_CHAIRMAN_RESULT, ret);
                break;
            //开始录制会议
            case TSDK_E_CONF_START_RECORD_BROADCAST:
                mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.START_RECORD_RESULT, ret);
                break;
            //停止录制会议
            case TSDK_E_CONF_STOP_RECORD_BROADCAST:
                mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.STOP_RECORD_RESULT, ret);
                break;
            default:
                break;
        }
    }

    /**
     * This method is used to end conf ctrl result.
     * 结束会议操作结果
     *
     * @param conference    Indicates conference info.
     *                      会议信息
     */
    public void handleConfEndInd(TsdkConference conference)
    {
        Log.i(TAG, "handleConfEndInd" + conference.getHandle());
        currentConference = null;
        mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.LEAVE_CONF, conference.getHandle());
    }



    /**
     * 与会者状态信息和状态更新处理
     * @param conference
     */
    public void handleInfoAndStatusUpdate(TsdkConference conference){
        Log.i(TAG, "onConfStatusUpdateInd");
        if ((currentConference == null) || (conference == null))
        {
            return;
        }

        if (currentConference.getHandle() != conference.getHandle())
        {
            return;
        }

        String handle = conference.getHandle()+"";

        this.updateConfInfo(conference);
        mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.STATE_UPDATE, handle);
    }

    /**
     * 发言人处理
     * @param speakerList
     */
    public void handleSpeakerInd(TsdkConfSpeakerInfo speakerList) {
        Log.i(TAG, "onEvtSpeakerInd");

        if (null == speakerList)
        {
            return;
        }

        if (speakerList.getSpeakerNum() > 0)
        {
            this.updateSpeaker(speakerList);
        }

        mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.SPEAKER_LIST_IND, speakerList.getSpeakerNum());
    }


    /**
     * 会议来电处理
     * @param conference
     */
    public void handleConfIncomingInd(TsdkConference conference){
        Log.i(TAG, "handleConfIncomingInd");

        if (null == conference)
        {
            return;
        }

        currentConference = conference;

        TsdkCall tsdkCall = conference.getCall();
        if (null != tsdkCall) {
            Session newSession = CallMgr.getInstance().getCallSessionByCallID(tsdkCall.getCallInfo().getCallId());
            if (null == newSession) {
                newSession = new Session(tsdkCall);
                CallMgr.getInstance().putCallSessionToMap(newSession);
            }
        }

        mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.CONF_INCOMING_TO_CALL_INCOMING, conference);

    }

    /**
     * This method is used to share status change processing
     * 共享状态变更处理
     * @param asStateInfo Indicates sharing status information
     *                    共享状态信息
     */
    public void handleAsStateChange(TsdkConfAsStateInfo asStateInfo)
    {
        Log.i(TAG, "handleAsStateChange");

        switch (TsdkConfShareState.enumOf(asStateInfo.getState()))
        {
            // 开始共享
            case TSDK_E_CONF_AS_STATE_VIEW:
                isShareAs = true;
                mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.START_DATA_CONF_SHARE, asStateInfo);
                break;

            // 结束共享
            case TSDK_E_CONF_AS_STATE_NULL:
                isShareAs = false;
                if (!isShareAs && (0 == documentId.size() || null == documentId))
                {
                    mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.END_DATA_CONF_SHARE, asStateInfo);
                }
                break;
            default:
                break;
        }
    }

    /**
     * This method is used to handle a new document status.
     * 处理开始新建文档的共享状态
     * @param docBaseInfo Indicates basic document information
     *                    文档基础信息
     */
    public void handleDsDocNew(TsdkDocBaseInfo docBaseInfo)
    {
        Log.i(TAG, "handleDsDocNew");

        if (null == docBaseInfo)
        {
            return;
        }

        documentId.add(docBaseInfo.getDocumentId());
        mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.START_DATA_CONF_SHARE, docBaseInfo);
    }

    /**
     * This method is used to handle delete document status.
     * 处理删除文档的共享状态
     * @param docShareDelDocInfo Indicates delete document information
     *                           文档删除信息
     */
    public void handleDsDocDel(TsdkDocShareDelDocInfo docShareDelDocInfo)
    {
        Log.i(TAG, "handleDsDocDel");

        if (null == docShareDelDocInfo)
        {
            return;
        }

        Iterator<Integer> iterator = documentId.iterator();
        while (iterator.hasNext())
        {
            if (iterator.next() == docShareDelDocInfo.getDocBaseInfo().getDocumentId())
            {
                iterator.remove();
            }
        }

        if (0 == documentId.size() && !isShareAs)
        {
            mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.END_DATA_CONF_SHARE, docShareDelDocInfo);
        }
    }

    /**
     * This method is used to handle a new whiteboard status.
     * 处理开始新建白板的共享状态
     * @param docBaseInfo Indicates basic whiteboard information
     *                    白板基础信息
     */
    public void handleWbDocNew(TsdkDocBaseInfo docBaseInfo)
    {
        Log.i(TAG, "handleWbDocNew");

        if (null == docBaseInfo)
        {
            return;
        }

        documentId.add(docBaseInfo.getDocumentId());
        mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.START_DATA_CONF_SHARE, docBaseInfo);
    }

    /**
     * This method is used to handle delete whiteboard status.
     * 处理删除白板的共享状态
     * @param wbDelDocInfo Indicates delete whiteboard information
     *                     删除的白板信息
     */
    public void handleWbDocDel(TsdkWbDelDocInfo wbDelDocInfo)
    {
        Log.i(TAG, "handleWbDocDel");

        if (null == wbDelDocInfo)
        {
            return;
        }

        Iterator<Integer> iterator = documentId.iterator();
        while (iterator.hasNext())
        {
            if (iterator.next() == wbDelDocInfo.getWbBaseInfo().getDocumentId())
            {
                iterator.remove();
            }
        }

        if (0 == documentId.size() && !isShareAs)
        {
            mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.END_DATA_CONF_SHARE, wbDelDocInfo);
        }
    }

    /**
     * This method is used to handle receive chat message notify in conf.
     * 处理接收到的聊天消息
     * @param confChatMsgInfo    Indicates chat message info.
     *                           聊天信息
     */
    public void handleRecvChatMsg(TsdkConfChatMsgInfo confChatMsgInfo)
    {
        Log.i(TAG, "handleRecvChatMsg");

        mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.CONF_CHAT_MSG, confChatMsgInfo);
    }

    /**
     * [en]This method is used to get temporary user results for anonymous meetings.
     * [cn]获取用于匿名方式加入会议的临时用户结果通知.
     *
     * @param userId            [en]Indicates user id
     *                          [cn]用户ID
     * @param result            [en]Indicates operation result.
     *                          [cn]操作结果
     */
    public void handleGetTempUserResult(int userId, TsdkCommonResult result)
    {
        Log.i(TAG, "handleGetTempUserResult");

        if(result == null){
            return;
        }
        if (0 == result.getResult()){
            setGetTempUserSuccess(true);
        }
        mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.GET_TEMP_USER_RESULT, result);
    }

}
