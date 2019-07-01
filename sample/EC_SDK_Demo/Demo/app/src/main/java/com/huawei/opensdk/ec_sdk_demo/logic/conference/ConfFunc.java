package com.huawei.opensdk.ec_sdk_demo.logic.conference;


import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.huawei.opensdk.commonservice.common.LocContext;
import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcastReceiver;
import com.huawei.opensdk.demoservice.ConfConstant;
import com.huawei.opensdk.demoservice.IConfNotification;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.ui.IntentConstant;
import com.huawei.opensdk.ec_sdk_demo.util.ActivityUtil;

public class ConfFunc implements IConfNotification
{
    private static final int BOOK_CONF_SUCCESS = 100;
    private static final int BOOK_CONF_FAILED = 101;
    private static final int QUERY_CONF_LIST_SUCCESS = 102;
    private static final int QUERY_CONF_LIST_FAILED = 103;
    private static final int QUERY_CONF_DETAIL_FAILED = 104;
    private static final int QUERY_CONF_DETAIL_SUCCESS = 105;

    private static final int JOIN_VOICE_CONF_SUCCESS = 109;
    private static final int JOIN_VIDEO_CONF_SUCCESS = 110;
    private static final int JOIN_CONF_FAILED = 111;

    private static ConfFunc mInstance = new ConfFunc();
    private String[] broadcastNames = new String[]{CustomBroadcastConstants.CONF_INFO_PARAM};

    private ConfFunc()
    {
        LocBroadcast.getInstance().registerBroadcast(receiver,broadcastNames);
    }

    private Handler mHandler = new Handler(Looper.getMainLooper())
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case BOOK_CONF_SUCCESS:
                    Toast.makeText(LocContext.getContext(), "book conf success.",
                            Toast.LENGTH_SHORT).show();
                    break;

                case BOOK_CONF_FAILED:
                    Toast.makeText(LocContext.getContext(), "book conf failed.",
                            Toast.LENGTH_SHORT).show();
                    break;

                case QUERY_CONF_LIST_FAILED:
                    Toast.makeText(LocContext.getContext(), "query conf list failed.",
                            Toast.LENGTH_SHORT).show();
                    break;

                case QUERY_CONF_DETAIL_FAILED:
                    Toast.makeText(LocContext.getContext(), "query conf detail failed.",
                            Toast.LENGTH_SHORT).show();
                    break;

                case JOIN_VOICE_CONF_SUCCESS:
                    if (msg.obj instanceof String)
                    {
                        String confID = (String)msg.obj;
//                        Intent intent = new Intent(IntentConstant.CONF_MEMBER_LIST_ACTIVITY_ACTION);
                        Intent intent = new Intent(IntentConstant.CONF_MANAGER_ACTIVITY_ACTION);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(UIConstants.CONF_ID, confID);
                        intent.putExtra(UIConstants.IS_VIDEO_CONF, false);
                        ActivityUtil.startActivity(LocContext.getContext(), intent);
                    }
                    break;

                case JOIN_VIDEO_CONF_SUCCESS:
                    if (msg.obj instanceof String)
                    {
                        String confID = (String)msg.obj;
                        Intent intent = new Intent(IntentConstant.CONF_MANAGER_ACTIVITY_ACTION);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(UIConstants.CONF_ID, confID);
                        intent.putExtra(UIConstants.IS_VIDEO_CONF, true);
                        ActivityUtil.startActivity(LocContext.getContext(), intent);
                    }
                    break;

                case JOIN_CONF_FAILED:
                    Toast.makeText(LocContext.getContext(), "join conf failed.",
                            Toast.LENGTH_SHORT).show();
                    break;

                default:
                    break;
            }
        }
    };


    private LocBroadcastReceiver receiver = new LocBroadcastReceiver()
    {
        @Override
        public void onReceive(String broadcastName, Object obj)
        {
//            switch (broadcastName)
//            {
//                default:
//                    break;
//            }
        }
    };


    public static ConfFunc getInstance()
    {
        return mInstance;
    }

    @Override
    public void onConfEventNotify(ConfConstant.CONF_EVENT confEvent, Object params) {
        switch (confEvent)
        {
            case BOOK_CONF_SUCCESS:
                mHandler.sendEmptyMessage(BOOK_CONF_SUCCESS);
                break;

            case BOOK_CONF_FAILED:
                mHandler.sendEmptyMessage(BOOK_CONF_FAILED);
                break;

            case QUERY_CONF_LIST_FAILED:
                mHandler.sendEmptyMessage(QUERY_CONF_LIST_FAILED);
                break;

            case QUERY_CONF_LIST_SUCCESS:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.GET_CONF_LIST_RESULT, params);
                break;

            case QUERY_CONF_DETAIL_FAILED:
                mHandler.sendEmptyMessage(QUERY_CONF_DETAIL_FAILED);
                break;

            case QUERY_CONF_DETAIL_SUCCESS:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.GET_CONF_DETAIL_RESULT, params);
                break;

            case JOIN_VOICE_CONF_SUCCESS:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.CONF_CALL_CONNECTED, params);
                mHandler.sendMessage(mHandler.obtainMessage(JOIN_VOICE_CONF_SUCCESS, params));
                break;

            case JOIN_VIDEO_CONF_SUCCESS:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.CONF_CALL_CONNECTED, params);
                mHandler.sendMessage(mHandler.obtainMessage(JOIN_VIDEO_CONF_SUCCESS, params));
                break;

            case JOIN_CONF_FAILED:
                mHandler.sendEmptyMessage(JOIN_CONF_FAILED);
                break;

            case REQUEST_RIGHT_FAILED:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.REQUEST_CONF_RIGHT_RESULT, params);
                break;

            case STATE_UPDATE:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.CONF_STATE_UPDATE, params);
                break;

            case ADD_YOURSELF_FAILED:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ADD_SELF_RESULT, params);
                break;

            case ADD_ATTENDEE_RESULT:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ADD_ATTENDEE_RESULT, params);
                break;

            case DEL_ATTENDEE_RESULT:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.DEL_ATTENDEE_RESULT, params);
                break;

            case MUTE_ATTENDEE_RESULT:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.MUTE_ATTENDEE_RESULT, params);
                break;

            case UN_MUTE_ATTENDEE_RESULT:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.UN_MUTE_ATTENDEE_RESULT, params);
                break;

            case MUTE_CONF_RESULT:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.MUTE_CONF_RESULT, params);
                break;

            case UN_MUTE_CONF_RESULT:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.UN_MUTE_CONF_RESULT, params);
                break;

            case LOCK_CONF_RESULT:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.LOCK_CONF_RESULT, params);
                break;

            case UN_LOCK_CONF_RESULT:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.UN_LOCK_CONF_RESULT, params);
                break;

            case HAND_UP_RESULT:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.HAND_UP_RESULT, params);
                break;

            case CANCEL_HAND_UP_RESULT:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.CANCEL_HAND_UP_RESULT, params);
                break;

            case REQUEST_CHAIRMAN_RESULT:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.REQUEST_CHAIRMAN_RESULT, params);
                break;

            case RELEASE_CHAIRMAN_RESULT:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.RELEASE_CHAIRMAN_RESULT, params);
                break;

            case WILL_TIMEOUT:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.WILL_TIMEOUT, params);
                break;

            case POSTPONE_CONF_RESULT:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.POSTPONE_CONF_RESULT, params);
                break;

            case SPEAKER_LIST_IND:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.SPEAKER_LIST_IND, params);
                break;

            case SET_CONF_MODE_RESULT:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.SET_CONF_MODE_RESULT, params);
                break;

            case WATCH_ATTENDEE_RESULT:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.WATCH_ATTENDEE_CONF_RESULT, params);
                break;

            case BROADCAST_ATTENDEE_RESULT:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.BROADCAST_ATTENDEE_CONF_RESULT, params);
                break;

            case CANCEL_BROADCAST_RESULT:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.CANCEL_BROADCAST_CONF_RESULT, params);
                break;

            case START_RECORD_RESULT:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.START_RECORD_RESULT, params);
                break;

            case STOP_RECORD_RESULT:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.STOP_RECORD_RESULT, params);
                break;

            case GET_DATA_CONF_PARAM_RESULT:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.GET_DATA_CONF_PARAM_RESULT, params);
                break;

            case UPGRADE_CONF_RESULT:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.UPGRADE_CONF_RESULT, params);
                break;

            case JOIN_DATA_CONF_RESULT:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.DATA_CONFERENCE_JOIN_RESULT, params);
                break;

            case CAMERA_STATUS_UPDATE:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.DATA_CONFERENCE_CAMERA_STATUS_UPDATE, params);
                break;

            case CONF_INCOMING_TO_CALL_INCOMING:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.CONF_INCOMING_TO_CALL_INCOMING, params);
                break;

            case START_DATA_CONF_SHARE:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.DATE_CONFERENCE_START_SHARE_STATUS, params);
                break;

            case END_DATA_CONF_SHARE:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.DATE_CONFERENCE_END_SHARE_STATUS, params);
                break;

            case SCREEN_SHARE_STATE:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.SCREEN_SHARE_STATE, params);
                break;

            case LEAVE_CONF:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.GET_CONF_END, params);
                break;
				
			case CONF_CHAT_MSG:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.DATE_CONFERENCE_CHAT_MSG, params);
                break;
            case GET_TEMP_USER_RESULT:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.GET_TEMP_USER_RESULT, params);
                break;

            case CALL_TRANSFER_TO_CONFERENCE:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.CALL_TRANSFER_TO_CONFERENCE, params);
                break;

            default:
                break;
        }
    }
}
