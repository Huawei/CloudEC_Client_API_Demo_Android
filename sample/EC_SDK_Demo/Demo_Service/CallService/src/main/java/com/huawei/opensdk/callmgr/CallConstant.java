package com.huawei.opensdk.callmgr;

/**
 * This class is about call module constant definition
 * 呼叫模块常量类
 */
public class CallConstant {

    /**
     * The constant TYPE_LOUD_SPEAKER.
     * 扬声器
     */
    public static final int TYPE_LOUD_SPEAKER = 1;

    /**
     * The constant CAMERA_NON.
     * 无摄像头
     */
    public static final int CAMERA_NON = -1;

    /**
     * Rear camera
     * 后置摄像头
     */
    public static final int BACK_CAMERA = 0;

    /**
     * Big screen device camera
     * Android大屏设备的摄像头
     */
    public static final int BIG_SCREEN_CAMERA = 0;

    /**
     * front camera
     * 前置摄像头
     */
    public static final int FRONT_CAMERA = 1;

    /**
     * Whether to use the SDK to provide methods to play.
     * 是否使用SDK提供播放媒体方法来播放铃声
     */
    public static boolean playFileBySDK = false;

    /**
     * This class is about call event enumeration
     * 呼叫事件枚举类
     */
    public enum CallEvent
    {
        CALL_COMING(),

        CALL_GOING(),

        PLAY_RING_BACK_TONE(),

        CALL_CONNECTED(),

        CALL_ENDED(),

        CALL_ENDED_FAILED(),

        SESSION_MODIFIED(),

        RTP_CREATED(),


        AUDIO_HOLD_SUCCESS(),
        AUDIO_HOLD_FAILED(),
        VIDEO_HOLD_SUCCESS(),
        VIDEO_HOLD_FAILED(),
        UN_HOLD_SUCCESS(),
        UN_HOLD_FAILED(),
        DIVERT_FAILED(),
        BLD_TRANSFER_SUCCESS(),
        BLD_TRANSFER_FAILED(),

        OPEN_VIDEO(),
        CLOSE_VIDEO(),
        REMOTE_REFUSE_ADD_VIDEO_SREQUEST(),
        RECEIVED_REMOTE_ADD_VIDEO_REQUEST(),


        CONF_INFO_NOTIFY(),

        CONF_INCOMING(),
        CONF_END(),


        CALL_VIDEO_TO_AUDIO(),

        ADD_LOCAL_VIEW(),

        DEL_LOCAL_VIEW(),

        STATISTIC_LOCAL_QOS(),

        UNKNOWN()

    }

    /**
     * This class is about call type enumeration
     * 呼叫类型枚举类
     */
    public enum CallStatus
    {
        IDLE(),
        AUDIO_CALLING(),
        VIDEO_CALLING(),
        UNKNOWN()
    }

}
