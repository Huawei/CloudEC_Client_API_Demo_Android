package com.huawei.opensdk.commonservice.localbroadcast;

/**
 * This interface is about custom broadcast address constants.
 * 自定义广播消息接口
 */
public interface CustomBroadcastConstants
{
    String ACTION_CALL_CONNECTED = "com.huawei.opensdk.call.connected";
    String CALL_MEDIA_CONNECTED = "com.huawei.opensdk.call.mediaconnected";
    String ACTION_CALL_END = "com.huawei.opensdk.call.end";
    String ACTION_CALL_END_FAILED = "com.huawei.opensdk.call.end.failed";
    String ADD_LOCAL_VIEW = "com.huawei.opensdk.call.addlocalview";
    String DEL_LOCAL_VIEW = "com.huawei.opensdk.call.dellocalview";
    //String BOOK_RESERVED_CONF_RESULT = "com.huawei.opensdk.conf.bookconfresult";
    String GET_CONF_LIST_RESULT = "com.huawei.opensdk.conf.getconflistresult";
    String GET_CONF_DETAIL_RESULT = "com.huawei.opensdk.conf.getconfdetailresult";
    String GET_CONF_SUBSCRIBE_RESULT = "com.huawei.opensdk.conf.getconfsubscriberesult";
    String ADD_SELF_RESULT = "com.huawei.opensdk.conf.addselfresult";
    String ADD_ATTENDEE_RESULT = "com.huawei.opensdk.conf.addattendeeresult";
    String DEL_ATTENDEE_RESULT = "com.huawei.opensdk.conf.delattendeeresult";
    String HANG_UP_ATTENDEE_RESULT = "com.huawei.opensdk.conf.hangupattendeeresult";
    //String GET_CONF_COMING = "com.huawei.opensdk.conf.getconfcoming";
    String GET_CONF_CONNECTED = "com.huawei.opensdk.conf.getconfconnected";
    String GET_CONF_END = "com.huawei.opensdk.conf.getconfend";
    String CONF_STATE_UPDATE = "com.huawei.opensdk.conf.confstateupdate";
    String ACTION_VIDEO_TO_AUDIO = "com.huawei.opensdk.call.videoswitchaudio";
    String CALL_UPGRADE_ACTION = "com.huawei.opensdk.call.upgrade";
    String REQUEST_CONF_RIGHT_RESULT = "com.huawei.opensdk.conf.requestconfrightresult";
    String CONF_CALL_ANSWERED = "com.huawei.opensdk.conf.confcallanswered";
    String LOGIN_SUCCESS = "com.huawei.opensdk.login.success";
    String LOGIN_FAILED = "com.huawei.opensdk.login.failed";
    String AUTH_FAILED = "com.huawei.opensdk.auth.failed";
    String LOGOUT = "com.huawei.opensdk.login.logout";
    String MODIFY_PWD_SUCCESS = "com.huawei.opensdk.modifypwd.success";
    String LOGIN_STATUS_RESUME_IND = "com.huawei.opensdk.loginstatusresumeind";
    String LOGIN_STATUS_RESUME_RESULT = "com.huawei.opensdk.loginstatusresumeresult";
    String MUTE_ATTENDEE_RESULT = "com.huawei.opensdk.conf.muteattendeeresult";
    String UN_MUTE_ATTENDEE_RESULT = "com.huawei.opensdk.conf.unmuteattendeeresult";
    String MUTE_CONF_RESULT = "com.huawei.opensdk.conf.muteconfresult";
    String UN_MUTE_CONF_RESULT = "com.huawei.opensdk.conf.unmuteconfresult";
    String LOCK_CONF_RESULT = "com.huawei.opensdk.conf.lockconfresult";
    String UN_LOCK_CONF_RESULT = "com.huawei.opensdk.conf.unlockconfresult";
    String START_RECORD_RESULT = "com.huawei.opensdk.conf.startrecordresult";
    String STOP_RECORD_RESULT = "com.huawei.opensdk.conf.stoprecordresult";
    String SESSION_MODIFIED_RESULT = "com.huawei.opensdk.conf.sessionmodifiedresult";
    String REQUEST_CHAIRMAN_RESULT = "com.huawei.opensdk.conf.requestchairmanresult";
    String RELEASE_CHAIRMAN_RESULT = "com.huawei.opensdk.conf.releasechairmanresult";
    String HAND_UP_RESULT = "com.huawei.opensdk.conf.handupresult";
    String CANCEL_HAND_UP_RESULT = "com.huawei.opensdk.conf.cancelhandupresult";
    String WILL_TIMEOUT = "com.huawei.opensdk.conf.willtimeout";
    String POSTPONE_CONF_RESULT = "com.huawei.opensdk.conf.postponeconfresult";
    String SPEAKER_LIST_IND = "com.huawei.opensdk.conf.speakerlistind";
    String STATISTIC_LOCAL_QOS = "com.huawei.opensdk.conf.statisticlocalqos";
    String RENAME_SELF_RESULT = "com.huawei.opensdk.conf.renameselfresult";

    String SET_CONF_MODE_RESULT = "com.huawei.opensdk.conf.setconfmoderesult";
    String HOLD_CALL_RESULT = "com.huawei.opensdk.conf.holdcallresult";
    String CONF_CALL_CONNECTED = "com.huawei.opensdk.conf.confcallconnected";
    String DIVERT_RESULT = "com.huawei.opensdk.conf.divertresult";
    String BLD_TRANSFER_RESULT = "com.huawei.opensdk.conf.bldtransferresult";
    String JOIN_CONF_FAILED = "com.huawei.opensdk.conf.joinconffailed";

    String UPGRADE_CONF_RESULT = "com.huawei.opensdk.conf.upgradeconfresult";
    String WATCH_ATTENDEE_CONF_RESULT = "com.huawei.opensdk.conf.watchattendeeconfresult";
    String BROADCAST_ATTENDEE_CONF_RESULT = "com.huawei.opensdk.conf.broadcastattendeeconfresult";
    String CANCEL_BROADCAST_CONF_RESULT = "com.huawei.opensdk.conf.cancelbroadcastconfresult";

    String ACCESS_RESERVED_CONF = "com.huawei.opensdk.conf.accessreservedconf";

    String GET_TEMP_USER_RESULT = "com.huawei.opensdk.conf.gettempuserresult";

    String CALL_TRANSFER_TO_CONFERENCE = "com.huawei.opensdk.conf.calltransfertoconference";
    String GET_SVC_WATCH_INFO = "com.huawei.opensdk.conf.getsvcwatchinfo";
    String RESUME_JOIN_CONF_RESULT = "com.huawei.opensdk.conf.resumejoinconfresult";
    String RESUME_JOIN_CONF_IND = "com.huawei.opensdk.conf.resumejoinconfind";

    //DataConf
    String CONF_INFO_PARAM = "com.huawei.opensdk.conf.dataconfparam";
    String GET_DATA_CONF_PARAM_RESULT = "com.huawei.opensdk.conf.getdataconfparamresult";
    String UPDATE_HOST_INFO = "com.huawei.opensdk.conf.updatehostinfo";
    String DATA_CONF_USER_LEAVE = "com.huawei.opensdk.conf.dataconfuserleave";
    String CONF_MSG_ON_CONFERENCE_TERMINATE = "com.huawei.opensdk.conf.confmsgonconferenceterminate";
    String CONF_MSG_ON_CONFERENCE_LEAVE = "com.huawei.opensdk.conf.confmsgonconferenceleave";
    String COMPT_MSG_VIDEO_ON_NOTIFY = "com.huawei.opensdk.conf.comptmsgvideoonnotify";
    String DATA_CONFERENCE_JOIN_RESULT = "com.huawei.opensdk.conf.dataconferencejoinresult";
    String DATA_CONFERENCE_USER_JOIN = "com.huawei.opensdk.conf.dataconferenceuserjoin";
    String DATA_CONFERENCE_PRESENTER_CHANGE_IND = "com.huawei.opensdk.conf.dataconferencepresenterchangeind";
    String DATA_CONFERENCE_HOST_CHANGE_IND = "com.huawei.opensdk.conf.dataconferencehostchangeind";
    String DATA_CONFERENCE_GET_DEVICE_INFO_RESULT = "com.huawei.opensdk.conf.dataconferencegetdeviceinforesult";
    String DATA_CONFERENCE_EXTEND_DEVICE_INFO = "com.huawei.opensdk.conf.dataconferenceextenddeviceinfo";
    String DATA_CONFERENCE_CAMERA_STATUS_UPDATE = "com.huawei.opensdk.conf.dataconferencecamerastatusupdate";
    String DATE_CONFERENCE_START_SHARE_STATUS = "com.huawei.opensdk.conf.dateconferencestartsharestatus";
    String DATE_CONFERENCE_END_SHARE_STATUS = "com.huawei.opensdk.conf.dateconferenceendsharestatus";
    String DATE_CONFERENCE_CHAT_MSG = "com.huawei.opensdk.conf.dateconferencechatmsg";
    String SCREEN_SHARE_STATE = "com.huawei.opensdk.conf.screensharestate";

    //IM
    String ACTION_LOGIN_IM = "com.huawei.opensdk.im.imlogin";
    String ACTION_RECEIVE_SESSION_CHANGE = "com.huawei.opensdk.im.messagereceive";
    String ACTION_RECEIVE_BATCH_MESSAGES = "com.huawei.opensdk.im.receivebatchmessages";
    String ACTION_IM_LOGIN_SUCCESS = "com.huawei.opensdk.im.loginsuccess";
    String ACTION_IM_LOGIN_ERROR = "com.huawei.opensdk.im.loginerror";
    String ACTION_IM_QUERY_HISTORY = "com.huawei.opensdk.im.queryhistory";
    String ACTION_IM_DELETE_CONTACT_RESULT = "com.huawei.opensdk.im.deletecontactresult";
    String ACTION_IM_SEARCH_CONTACT_RESULT = "com.huawei.opensdk.im.searchcontactresult";
    String ACTION_IM_ADD_CONTACT_RESULT = "com.huawei.opensdk.im.addcontactresult";
    String ACTION_IM_FRIEND_STATUS_CHANGE = "com.huawei.opensdk.im.friendstatuschange";
    String ACTION_DOWNLOAD_PROCESS = "com.huawei.opensdk.im.downloadprocess";
    String ACTION_DOWNLOAD_FINISH = "com.huawei.opensdk.im.downloadfinish";
    String ACTION_SEND_MESSAGE_SUCCESS = "com.huawei.opensdk.im.sendmessagesuccess";
    String ACTION_SEND_MESSAGE_FAIL = "com.huawei.opensdk.im.sendmessagefail";
    String ACTION_QUERY_GROUP_MEMBER = "com.huawei.opensdk.im.querygroupmember";
    String ACTION_REFRESH_GROUP_MEMBER = "com.huawei.opensdk.im.refreshgroupmember";
    String ACTION_MODIFY_GROUP_MEMBER = "com.huawei.opensdk.im.modifygroupmember";
    String ACTION_CREATE_GROUP_RESULT = "com.huawei.opensdk.im.creategroupresult";
    String ACTION_SET_STATUS = "com.huawei.opensdk.im.modifygroupmember";
    String ACTION_IM_SET_HEAD_PHOTO = "com.huawei.opensdk.im.setheadphoto";
    String ACTION_REFRESH_TEAM_MEMBER = "com.huawei.opensdk.im.refreshteammember";

    String ACTION_IM_USER_INFO_CHANGE = "com.huawei.opensdk.im.userinfochange";
    String ACTION_IM_USER_STATUS_CHANGE = "com.huawei.opensdk.im.userstatuschange";
    String ACTION_IM_USER_INFO_CHANGE_FAILED = "com.huawei.opensdk.im.userinfochangefailed";
    String ACTION_IM_CHAT_GROUP_ADD = "com.huawei.opensdk.im.chatgroupadd";
    String ACTION_IM_CHAT_GROUP_DISMISS = "com.huawei.opensdk.im.chatgroupupdismiss";
    String ACTION_IM_CHAT_GROUP_UPDATE = "com.huawei.opensdk.im.chatgroupupdate";
    String ACTION_IM_CHAT_GROUP_ADD_MEMBER = "com.huawei.opensdk.im.chatgroupaddmember";
    String ACTION_IM_CHAT_GROUP_DEL_MEMBER = "com.huawei.opensdk.im.chatgroupdelmember";
    String ACTION_IM_CHAT_GROUP_LEAVE_RESULT = "com.huawei.opensdk.im.chatgroupleaveresult";
    String ACTION_IM_CHAT_WITHDRAW_MSG_FAILED = "com.huawei.opensdk.im.chatwithdrawmsgfailed";
    String ACTION_IM_CHAT_WITHDRAW_MSG_IND = "com.huawei.opensdk.im.chatwithdrawmsgind";
    String ACTION_IM_CHAT_INPUTTING_STATUS_IND = "com.huawei.opensdk.im.chatinputtingstatusind";

    //Enterprise address book
    String ACTION_ENTERPRISE_GET_SELF_RESULT = "com.huawei.opensdk.eaddr.getselfresult";
    String ACTION_ENTERPRISE_GET_CONTACT_RESULT = "com.huawei.opensdk.eaddr.getcontactresult";
    String ACTION_ENTERPRISE_GET_CONTACT_NULL = "com.huawei.opensdk.eaddr.getcontactnull";
    String ACTION_ENTERPRISE_GET_CONTACT_FAILED = "com.huawei.opensdk.eaddr.getcontactfailed";
    String ACTION_ENTERPRISE_GET_SELF_PHOTO_RESULT = "com.huawei.opensdk.eaddr.getselfphotoresult";
    String ACTION_ENTERPRISE_GET_HEAD_PHOTO_FAILED = "com.huawei.opensdk.eaddr.getheadphotofailed";
    String ACTION_ENTERPRISE_GET_HEAD_SYS_PHOTO = "com.huawei.opensdk.eaddr.getheadsysphoto";
    String ACTION_ENTERPRISE_GET_HEAD_DEF_PHOTO = "com.huawei.opensdk.eaddr.getheaddefphoto";
    String ACTION_ENTERPRISE_GET_DEPARTMENT_RESULT = "com.huawei.opensdk.eaddr.getdepartmentresult";
    String ACTION_ENTERPRISE_GET_DEPARTMENT_NULL = "com.huawei.opensdk.eaddr.getdepartmentnull";
    String ACTION_ENTERPRISE_GET_DEPARTMENT_FAILED = "com.huawei.opensdk.eaddr.getdepartmentfailed";

    //conference to call
    String CONF_INCOMING_TO_CALL_INCOMING = "com.huawei.opensdk.conf.conftocall";

    String ACTION_CALL_STATE_IDLE = "com.huawei.opensdk.callstateidle";
    String ACTION_CALL_STATE_RINGING = "com.huawei.opensdk.callstatering";
    String ACTION_CALL_STATE_OFF_HOOK = "com.huawei.opensdk.callstateoffhook";
}
