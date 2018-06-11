package com.huawei.opensdk.demoservice;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import com.huawei.ecterminalsdk.base.TsdkAddAttendeesInfo;
import com.huawei.ecterminalsdk.base.TsdkAttendee;
import com.huawei.ecterminalsdk.base.TsdkAttendeeBaseInfo;
import com.huawei.ecterminalsdk.base.TsdkBookConfInfo;
import com.huawei.ecterminalsdk.base.TsdkConfAsStateInfo;
import com.huawei.ecterminalsdk.base.TsdkConfAttendeeUpdateType;
import com.huawei.ecterminalsdk.base.TsdkConfBaseInfo;
import com.huawei.ecterminalsdk.base.TsdkConfDetailInfo;
import com.huawei.ecterminalsdk.base.TsdkConfJoinParam;
import com.huawei.ecterminalsdk.base.TsdkConfLanguage;
import com.huawei.ecterminalsdk.base.TsdkConfListInfo;
import com.huawei.ecterminalsdk.base.TsdkConfOperationResult;
import com.huawei.ecterminalsdk.base.TsdkConfRight;
import com.huawei.ecterminalsdk.base.TsdkConfRole;
import com.huawei.ecterminalsdk.base.TsdkConfType;
import com.huawei.ecterminalsdk.base.TsdkConfVideoMode;
import com.huawei.ecterminalsdk.base.TsdkJoinConfIndInfo;
import com.huawei.ecterminalsdk.base.TsdkQueryConfDetailReq;
import com.huawei.ecterminalsdk.base.TsdkQueryConfListReq;
import com.huawei.ecterminalsdk.base.TsdkWatchAttendees;
import com.huawei.ecterminalsdk.base.TsdkWatchAttendeesInfo;
import com.huawei.ecterminalsdk.models.TsdkCommonResult;
import com.huawei.ecterminalsdk.models.TsdkManager;
import com.huawei.ecterminalsdk.models.call.TsdkCall;
import com.huawei.ecterminalsdk.models.conference.TsdkConference;
import com.huawei.opensdk.callmgr.CallMgr;
import com.huawei.opensdk.callmgr.Session;
import com.huawei.opensdk.callmgr.VideoMgr;
import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.tup.confctrl.sdk.TupConfParam;

import java.util.ArrayList;
import java.util.List;

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
     * 自己加入会议的号码
     */
//    private String joinConfNumber;

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


    /**
     * This method is used to update conf info
     * 更新会议信息
     * @param conference 会议信息
     */
    public void updateConfInfo(TsdkConference conference)
    {
        confBaseInfo.setSize(conference.getSize());
        confBaseInfo.setConfID(conference.getConfId());
        confBaseInfo.setSubject(conference.getSubject());
        confBaseInfo.setSchedulerName(conference.getScheduserName());
        confBaseInfo.setConfState(ConfConvertUtil.convertConfctrlConfState(conference.getConfState()));
        confBaseInfo.setMediaType(conference.getConfMediaType());
        confBaseInfo.setLock(conference.isLock());
        confBaseInfo.setMuteAll(conference.isAllMute());


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

        bookConfInfo.setSubject(bookConferenceInfo.getSubject());
        bookConfInfo.setConfMediaType(bookConferenceInfo.getMediaType());
        bookConfInfo.setStartTime(bookConferenceInfo.getStartTime());
        bookConfInfo.setDuration(bookConferenceInfo.getDuration());
        bookConfInfo.setSize(bookConferenceInfo.getSize());

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
        }

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
        }

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

        TsdkAddAttendeesInfo addAttendeeInfo = new TsdkAddAttendeesInfo(attendeeList, attendeeList.size());

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

    public int setPresenter(Member attendee)
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "set presenter failed, currentConference is null ");
            return -1;
        }

        //int result =  currentConference.se(attendee);

        return 0;
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
     * This method is used to set conf mode
     * 设置会议类型
     * @param confctrlConfMode 会议类型
     * @return
     */
    public int setConfMode(TsdkConfVideoMode confctrlConfMode)
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "set conf mode failed, currentConference is null ");
            return -1;
        }

        int result =  currentConference.setVideoMode(confctrlConfMode);

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

        int result =  currentConference.broadcastAttendee(attendee.getNumber(), isBroadcast);

        //TODO
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
            mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.BOOK_CONF_FAILED, -1);

            return;
        }

        if (result.getResult() != TupConfParam.CONF_RESULT.TUP_SUCCESS)
        {
            Log.e(TAG, "book conference is failed, return ->" + result.getResult());
            mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.BOOK_CONF_FAILED, result.getResult());
            return;
        }

        Log.i(TAG, "book conference is success.");
        mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.BOOK_CONF_SUCCESS, result.getResult());
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

        if (result.getResult() != TupConfParam.CONF_RESULT.TUP_SUCCESS)
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

        int result = commonResult.getResult();
        if (result == 0)
        {
            this.currentConference = tsdkConference;
            this.memberList = null;
            this.self = null;

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

            mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.JOIN_CONF_SUCCESS, tsdkConference.getHandle() + "");
        }
        else
        {
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
            return;
        }
        int confOperationType = result.getOperationType();
        switch (confOperationType)
        {
            //升级会议
            case 1:
                mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.UPGRADE_CONF_RESULT, ret);
                break;
            //闭音会场
            case 2:
                mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.MUTE_CONF_RESULT, ret);
                break;
            //取消闭音
            case 3:
                mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.UN_MUTE_CONF_RESULT, ret);
                break;
            //锁定会议
            case 4:
                mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.LOCK_CONF_RESULT, ret);
                break;
            //取消锁定
            case 5:
                mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.UN_LOCK_CONF_RESULT, ret);
                break;
            //添加与会者
            case 6:
                mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.ADD_ATTENDEE_RESULT, ret);
                break;
            //删除与会者
            case 7:
                mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.DEL_ATTENDEE_RESULT, ret);
                break;

            //闭音与会者
            case 10:
                mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.MUTE_ATTENDEE_RESULT, ret);
                break;
            //取消闭音与会者
            case 11:
                mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.UN_MUTE_ATTENDEE_RESULT, ret);
                break;
            //设置举手
            case 12:
                mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.HAND_UP_RESULT, ret);
                break;
            //取消设置举手
            case 13:
                mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.CANCEL_HAND_UP_RESULT, ret);
                break;
            //设置会议视频模式
            case 14:
                mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.SET_CONF_MODE_RESULT, ret);
                break;

            //申请主席权限
            case 18:
                mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.REQUEST_CHAIRMAN_RESULT, ret);
                break;
            //释放主席权限
            case 19:
                mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.RELEASE_CHAIRMAN_RESULT, ret);
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
     * 会议来电处理
     * @param conference
     */
    public void handleConfIncomingInd(TsdkConference conference){

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
        switch (asStateInfo.getState())
        {
            case 0:
                mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.END_AS_SHARE, asStateInfo);
                break;
            default:
                break;
        }
    }

}
