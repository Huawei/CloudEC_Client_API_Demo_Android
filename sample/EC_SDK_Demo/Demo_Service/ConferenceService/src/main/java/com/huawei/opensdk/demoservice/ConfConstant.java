package com.huawei.opensdk.demoservice;


public class ConfConstant {

    public static final int PAGE_SIZE = 20;

    /**
     * Book conference status.
     */
    public enum BookConfStatus
    {
        IDLE(),
        INSTANT_BOOKING(),
        RESERVED_BOOKING()
    }

    /**
     * Conference convening status.
     */
    public enum ConfConveneStatus
    {
        UNKNOWN(),
        SCHEDULE(),
        CREATING(),
        GOING(),
        DESTROYED()
    }

    public enum ParticipantStatus
    {
        IN_CONF(),
        CALLING(),
        JOINING(),
        LEAVED(),
        NO_EXIST(),
        BUSY(),
        NO_ANSWER(),
        REJECT(),
        CALL_FAILED(),
        UNKNOWN()
    }

    public enum CONF_EVENT {
        BOOK_CONF_SUCCESS(),    //预约会议成功
        BOOK_CONF_FAILED(),     //预约会议失败

        QUERY_CONF_LIST_SUCCESS(), //获取会议列表成功
        QUERY_CONF_LIST_FAILED(), //获取会议列表失败

        QUERY_CONF_DETAIL_SUCCESS(), //获取会议详情成功
        QUERY_CONF_DETAIL_FAILED(), //获取会议详情失败

        JOIN_CONF_SUCCESS(),
        JOIN_CONF_FAILED(),

        REQUEST_RIGHT_FAILED(),

        ADD_YOURSELF_FAILED(),
        ADD_ATTENDEE_RESULT(),
        DEL_ATTENDEE_RESULT(),

        MUTE_ATTENDEE_RESULT(),
        UN_MUTE_ATTENDEE_RESULT(),

        MUTE_CONF_RESULT(),
        UN_MUTE_CONF_RESULT(),

        LOCK_CONF_RESULT(),
        UN_LOCK_CONF_RESULT(),

        HAND_UP_RESULT(),
        CANCEL_HAND_UP_RESULT(),

        REQUEST_CHAIRMAN_RESULT(),
        RELEASE_CHAIRMAN_RESULT(),

        WILL_TIMEOUT(),
        POSTPONE_CONF_RESULT(),

        SET_CONF_MODE_RESULT(),
        GET_DATA_CONF_PARAM_RESULT(),

        UPGRADE_CONF_RESULT(),

        SPEAKER_LIST_IND(),
        STATE_UPDATE(),

        JOIN_DATA_CONF_RESULT(),
        JOIN_DATA_CONF_LEAVE(),
        JOIN_DATA_CONF_TERMINATE(),
        END_AS_SHARE(),

        CAMERA_STATUS_UPDATE(),

        CONF_INCOMING_TO_CALL_INCOMING(),//会议来电转到呼叫来电 为了显示呼叫界面

        LEAVE_CONF(),

        BUTT()
    }

    /**
     * Conference media type
     */
    public enum ConfMediaType {
        VOICE_CONF(),
        VIDEO_CONF(),
        VOICE_AND_DATA_CONF(),
        VIDEO_AND_DATA_CONF()
    }

    /**
     * Conference role
     */
    public enum ConfRole {
        CHAIRMAN(),
        ATTENDEE()
    }

    /**
     * Conference right
     */
    public enum ConfRight {
        MY_CREATE(),
        MY_JOIN(),
        MY_CREATE_AND_JOIN()
    }

}
