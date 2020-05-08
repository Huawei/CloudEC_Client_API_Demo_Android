package com.huawei.opensdk.ec_sdk_demo;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.huawei.AudioDeviceAndroid;
import com.huawei.opensdk.callmgr.CallMgr;
import com.huawei.opensdk.commonservice.common.LocContext;
import com.huawei.opensdk.ec_sdk_demo.ui.call.AudioRouterManager;
import com.huawei.opensdk.ec_sdk_demo.util.ActivityUtil;
import com.huawei.opensdk.loginmgr.LoginMgr;
import com.huawei.opensdk.servicemgr.ServiceMgr;

import org.json.JSONObject;

public class ECApplication extends Application
{
    private static int appCount = 0;

    private static Application app = null;
    private static JSONObject lastInfo = null;

    private AudioDeviceAndroid audioDeviceAndroid;

    @Override
    public void onCreate()
    {
        super.onCreate();
        setApp(this);

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStarted(Activity activity) {
                if (0 == appCount)
                {
                    if (null != LocContext.getContext())
                    {
                        isReRegister(LocContext.getContext());
                    }
                }
                appCount++;
            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
                appCount--;
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });

        registerHeadsetBluetoothListener();
    }

    public static int getAppCount() {
        return appCount;
    }

    public static Application getApp() {
        return app;
    }

    public static void setApp(Application app) {
        ECApplication.app = app;
    }

    public static JSONObject getLastInfo() {
        return lastInfo;
    }

    public static void setLastInfo(JSONObject lastInfo) {
        ECApplication.lastInfo = lastInfo;
    }

    private void isReRegister(Context context)
    {
        String currentActivity = ActivityUtil.getCurrentActivity(context);
        if ("LoginActivity".equals(currentActivity))
        {
            return;
        }
        if ("AnonymousJoinConfActivity".equals(currentActivity))
        {
            return;
        }
        if ("LoginSettingActivity".equals(currentActivity))
        {
            return;
        }

        boolean isInCall = CallMgr.getInstance().isExistCall();
        LoginMgr.getInstance().reRegister(isInCall);
    }

    /**
     * 注册耳机(蓝牙和有线)的监听事件
     */
    public void registerHeadsetBluetoothListener()
    {
        registerBlueToothToHme();

        AudioRouterManager.getInstance().registerOutputDevicesChangeObserver(getApp());
    }

    /**
     * 去注册耳机(蓝牙和有线)的监听事件
     */
    public void unregisterHeadsetBluetoothListener()
    {
        unregisterBlueToothToHme();

        AudioRouterManager.getInstance().unregisterOutputDevicesChangeObserver(getApp());
    }

    /**
     * 注册耳机插拔事件到HME
     */
    private void registerBlueToothToHme()
    {
        if (null == audioDeviceAndroid)
        {
            audioDeviceAndroid = new AudioDeviceAndroid();
        }

        IntentFilter intentFilter = new IntentFilter();
        // 蓝牙SCO使用事件
        intentFilter.addAction("android.media.SCO_AUDIO_STATE_CHANGED");
        // 耳机插拔事件
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        // 蓝牙断开连接事件
        intentFilter.addAction("android.bluetooth.headset.action.STATE_CHANGED"); // OS2.0后支持
        intentFilter.addAction("android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED"); // OS3.0后支持

        getApp().registerReceiver(audioDeviceAndroid.broadcastReceiver, intentFilter);
    }

    /**
     * 去注册蓝牙耳机插拔事件到HME
     */
    private void unregisterBlueToothToHme()
    {
        if (null != audioDeviceAndroid)
        {
            getApp().unregisterReceiver(audioDeviceAndroid.broadcastReceiver);
        }
    }

    @Override
    public void onTerminate()
    {
        super.onTerminate();

        unregisterHeadsetBluetoothListener();

        ServiceMgr.getServiceMgr().stopService();
    }
}
