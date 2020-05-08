package com.huawei.opensdk.ec_sdk_demo.ui.call;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.text.TextUtils;

import com.huawei.opensdk.commonservice.util.LogUtil;

/**
 * This class is about audio router manager class.
 */
public class AudioRouterManager {

    private static final String TAG = AudioRouterManager.class.getSimpleName();
    
    private static AudioRouterManager instance;

    private AudioRouterState currentRouteState = AudioRouterState.STATE_NULL;

    public static AudioRouterManager getInstance() {
        if (null == instance)
        {
            instance = new AudioRouterManager();
        }
        return instance;
    }

    /**
     * 注册音频路由改变的监听事件
     * @param context
     */
    public void registerOutputDevicesChangeObserver(Context context)
    {
        if (null == context)
        {
            return;
        }
        
        IntentFilter intentFilter = new IntentFilter();
        // 蓝牙SCO使用事件
        intentFilter.addAction("android.media.SCO_AUDIO_STATE_CHANGED");
        // 这个广播只是针对有线耳机，或者无线耳机的手机断开连接的事件，收不到有线耳机和蓝牙耳机的接入
        intentFilter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        // 耳机插拔通知
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        // 蓝牙打开关闭通知
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        //OS3.0后支持 真正的蓝牙耳机连接和关闭通知，但是打开关闭蓝牙无通知
        intentFilter.addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED);

        // 蓝牙系统广播添加权限字符串规避安全扫描
        context.registerReceiver(headsetAndBluetoothReceiver, intentFilter
                , Manifest.permission.BLUETOOTH, null);
    }

    /**
     * 去注册音频路由改变的监听事件
     * @param context
     */
    public void unregisterOutputDevicesChangeObserver(Context context)
    {
        if (null != context)
        {
            context.unregisterReceiver(headsetAndBluetoothReceiver);
        }
    }

//    public AudioDeviceConnection isHeadSetOrBluetoothConnect(Context context)
//    {
//        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
//        boolean isWireHeadSetConnected = audioManager.isWiredHeadsetOn();
//        boolean isBluetoothHeadSetConnected = BluetoothAdapter.getDefaultAdapter()
//                .getProfileConnectionState(android.bluetooth.BluetoothProfile.HEADSET)
//                != android.bluetooth.BluetoothProfile.STATE_DISCONNECTED;
//        AudioDeviceConnection connectType = AudioDeviceConnection.CONNECTION_NULL;
//
//        LogUtil.i("蓝牙" ,"isWireHeadSetConnected: " + isWireHeadSetConnected
//                + " isBluetoothHeadSetConnected: " + isBluetoothHeadSetConnected);
//        //有线耳机优先
//        if (isWireHeadSetConnected)
//        {
//            connectType = AudioDeviceConnection.CONNECTION_HEADSET;    //耳机接入
//        }
//        else if (isBluetoothHeadSetConnected)
//        {
//            connectType = AudioDeviceConnection.CONNECTION_BLUETOOTH;    //蓝牙接入
//        }
//        return connectType;
//    }

    /**
     * 音频路由改变的监听
     */
    private BroadcastReceiver headsetAndBluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) 
            {
                LogUtil.e(TAG, "intent is null return.");
                return;
            }

            String action = intent.getAction();
            if (TextUtils.isEmpty(action))
            {
                LogUtil.e(TAG, "action is null or 0-length return.");
                return;
            }

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED))
            {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                // 蓝牙关闭
                if (state == BluetoothAdapter.STATE_OFF)
                {
                    currentRouteState = AudioRouterState.BLUETOOTH_OFF;
                }
            }
            else if (action.equals(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED))
            {
                int state = intent.getIntExtra(BluetoothProfile.EXTRA_STATE, -1);
                // 蓝牙耳机断开连接
                if (state == BluetoothProfile.STATE_DISCONNECTED)
                {
                    currentRouteState = AudioRouterState.BLUETOOTH_DISCONNECTED;
                }
                // 蓝牙耳机连接上通知
                else if (state == BluetoothProfile.STATE_CONNECTED)
                {
                    currentRouteState = AudioRouterState.BLUETOOTH_CONNECTED;
                }
            }
            else if (action.equals(Intent.ACTION_HEADSET_PLUG))
            {
                int state = intent.getIntExtra("state", -1);
                switch (state)
                {
                    case 0:
                        //拔出耳机
                        currentRouteState = AudioRouterState.HEADSET_DISCONNECTED;
                        break;
                    case 1:
                        //插入耳机
                        currentRouteState = AudioRouterState.HEADSET_CONNECTED;
                        break;
                    default:
                        break;
                }
            }
            else
            {
                return;
            }

            LogUtil.i(TAG, "currentRouteState: " + currentRouteState.name());
        }
    };

    /**
     * 音频路由的状态枚举类
     */
    public enum AudioRouterState
    {
        STATE_NULL,
        HEADSET_CONNECTED,
        HEADSET_DISCONNECTED,
        BLUETOOTH_CONNECTED,
        BLUETOOTH_DISCONNECTED,
        BLUETOOTH_OFF
    }
}
