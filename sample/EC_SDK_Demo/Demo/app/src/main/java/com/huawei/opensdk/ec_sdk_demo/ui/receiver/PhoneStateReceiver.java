package com.huawei.opensdk.ec_sdk_demo.ui.receiver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;

/**
 * This class is about receiver the phone state.
 */
public class PhoneStateReceiver extends BroadcastReceiver {

    private static final String TAG = PhoneStateReceiver.class.getSimpleName();

    private static final int CALL_STATE_IDLE = 0;
    private static final int CALL_STATE_RINGING = 1;
    private static final int CALL_STATE_OFF_HOOK = 2;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        //如果是去电
        if (action.equals(Intent.ACTION_NEW_OUTGOING_CALL)){
            String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            Log.i(TAG, "call OUT:" + phoneNumber);
        }
        else
        {
            TelephonyManager manager = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
            switch (manager.getCallState())
            {
                case TelephonyManager.CALL_STATE_IDLE:
                    Log.i(TAG, "call state idle");
                    LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_CALL_STATE_IDLE, CALL_STATE_IDLE);
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.i(TAG, "call state ringing");
                    LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_CALL_STATE_RINGING, CALL_STATE_RINGING);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.i(TAG, "call state off the hook");
                    LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_CALL_STATE_OFF_HOOK, CALL_STATE_OFF_HOOK);
                    break;
                default:
                    break;
            }

            // 为了兼容有的Android8.0手机不能通过此listener获取到电话状态，当前采用上面的流程去获取
//            manager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }

    PhoneStateListener listener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String phoneNumber) {
            super.onCallStateChanged(state, phoneNumber);
            switch (state)
            {
                case TelephonyManager.CALL_STATE_IDLE:
                    Log.i(TAG, "call state idle");
                    LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_CALL_STATE_IDLE, CALL_STATE_IDLE);
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.i(TAG, "call state ringing");
                    LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_CALL_STATE_RINGING, CALL_STATE_RINGING);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.i(TAG, "call state off the hook");
                    LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_CALL_STATE_OFF_HOOK, CALL_STATE_OFF_HOOK);
                    break;
                default:
                    break;
            }
        }
    };
}


