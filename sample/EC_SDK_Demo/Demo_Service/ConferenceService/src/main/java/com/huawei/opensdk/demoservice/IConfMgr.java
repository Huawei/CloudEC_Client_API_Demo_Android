package com.huawei.opensdk.demoservice;

import com.huawei.opensdk.demoservice.data.ConferenceEntity;
import com.huawei.tup.confctrl.ConfctrlConfMode;
import com.huawei.tup.confctrl.sdk.TupConfDataConfParamsGetReq;
import com.huawei.tup.confctrl.sdk.TupConfECAttendeeInfo;

import java.util.List;


/**
 * This class is about conference UI Callback Management
 * 会控UI回调管理类
 */
public interface IConfMgr
{

    int bookConference(BookConferenceInfo bookConferenceInfo);

    /**
     * This method is used to query my conference list
     * 查询会议列表
     * @param myRight
     * @return
     */
    int queryMyConfList(ConfConstant.ConfRight myRight);

    /**
     * This method is used to query the conference detail
     * 查询会议详情
     * @param confID
     * @return
     */
    int queryConfDetail(String confID);

    /**
     * This method is used to join conference
     * 加入会议
     * @param confId              会议ID
     * @param password            会议接入密码
     * @param needInviteYourself  通过会议列表等方式主动入会议时，需要邀请自己的号码；
     *                             被邀请或IVR主动入会等方式加入时，不需要邀请自己的号码
     * @return
     */
    int joinConf(String confId, String password, boolean needInviteYourself);


    /**
     * This method is used to create conference
     * 创建会议
     * @param conferenceEntity 创会信息
     * @return
     */
    boolean createConference(ConferenceEntity conferenceEntity);

//    boolean queryConfList(TupConfGetConfList tupConfGetConfList);
//
//    boolean queryConfDetail(TupConfGetConfInfo tupConfGetConfInfo);

//    boolean joinConf(TupConfAccessInfo tupConfAccessInfo,
//                     boolean isRequestConfctrlRight, String tmpToken, String chairmanPassword,
//                     String softTerminalNumber);

//    TUPCommonResponse requestConfCtrlRight(String tmpToken, String chairmanPassword, String softTerminalNumber);

    /**
     * This method is used to add attendee
     * 添加与会者
     * @param attendees 与会者信息
     * @return
     */
    boolean addECAttendee(List<TupConfECAttendeeInfo> attendees);

    /**
     * This method is used to end call
     * 结束通话
     * @return
     */
    boolean endCall();

    /**
     * This method is used to hang up attendee
     * 挂断与会者
     * @param attendeeNumber 与会者号码
     * @return
     */
    boolean hangupAttendee(String attendeeNumber);

    /**
     * 离开会议
     * @return
     */
    boolean leaveConf();

    /**
     * 结束会议
     * @return
     */
    boolean endConf();

    /**
     * This method is used to mute attendee
     * 静音与会者
     * @param attendee 与会者号码
     * @param isMute 是否静音
     * @return
     */
    boolean muteAttendee(String attendee, boolean isMute);

    /**
     * This method is used to mute conference
     * 静音会议
     * @param isMute 是否静音
     * @return
     */
    boolean muteConf(boolean isMute);

    /**
     * This method is used to release chairman
     * 释放主席
     * @param number 与会者号码
     * @return
     */
    boolean releaseChairman(String number);

    /**
     * This method is used to request chairman
     * 请求会控主席
     * @param number 与会者号码
     * @param chairmanPassword 主席密码
     * @return
     */
    boolean requestChairman(String number, String chairmanPassword);

    /**
     * This method is used to hand up
     * 举手
     * @param handUp 是否举手
     * @param number 与会者号码
     * @return
     */
    boolean handUp(boolean handUp, String number);

    /**
     * This method is used to switch audio route
     * 切换音频
     * @return
     */
    int switchAudioRoute();

    /**
     * This method is used to set conf mode
     * 设置会议模式
     * @param confctrlConfMode
     * @return
     */
    boolean setConfMode(ConfctrlConfMode confctrlConfMode);

    /**
     * This method is used to broadcast attendee
     * 广播与会者
     * @param attendee 与会者号码
     * @param isBroadcast 是否广播
     * @return
     */
    boolean broadcastAttendee(String attendee, boolean isBroadcast);

    /**
     * This method is used to watch attendee
     * 观看与会者
     * @param attendee 与会者号码
     * @return
     */
    boolean watchAttendee(String attendee);

    /**
     * This method is used to upgrade conf
     * 升级会议
     * @param mediaType 媒体类型
     * @param groupUri 组网
     * @return
     */
    boolean upgradeConf(int mediaType, String groupUri);

    /**
     * This method is used to get data conf params
     * 获取数据会议参数
     * @param dataConfParams 会议大参数入参
     * @return
     */
    boolean getDataConfParams(TupConfDataConfParamsGetReq dataConfParams);

    /**
     * This method is used to switch camera
     * 选择摄像头
     * @param cameraIndex 摄像头下标
     */
    void switchCamera(int cameraIndex);

    boolean isLoudSpeaker();
}
