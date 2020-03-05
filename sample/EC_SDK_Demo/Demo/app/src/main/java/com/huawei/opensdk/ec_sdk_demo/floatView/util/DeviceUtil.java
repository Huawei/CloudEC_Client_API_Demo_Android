package com.huawei.opensdk.ec_sdk_demo.floatView.util;

import android.app.PendingIntent;
import android.content.Intent;

import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.ec_sdk_demo.ECApplication;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.ui.conference.ConfManagerActivity;

public class DeviceUtil {

    /**
     * 将app拉到前台
     */
    public static void bringTaskBackToFront() {
        //后台拉起临时界面
        LogUtil.i(UIConstants.DEMO_TAG, "bringTaskBackToFront.");

        Intent intent = new Intent(ECApplication.getApp(), ConfManagerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(ECApplication.getApp()
                        , 0, intent, PendingIntent.FLAG_ONE_SHOT);
        try
        {
            pendingIntent.send();
        }
        catch (PendingIntent.CanceledException e)
        {
            LogUtil.e(UIConstants.DEMO_TAG, e.getMessage());
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        ECApplication.getApp().startActivity(intent);
    }

    public static void jumpToHomeScreen() {
        LogUtil.i(UIConstants.DEMO_TAG, "jump to home screen.");
        Intent localIntent = new Intent("android.intent.action.MAIN");
        localIntent.addCategory("android.intent.category.HOME");
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ECApplication.getApp().startActivity(localIntent);
    }

    /**
     * 判断当前是否在前台 true:前台  false:后台
     */
    public static boolean isAppForeground() {
        return ECApplication.getAppCount() > 0;
    }

}
