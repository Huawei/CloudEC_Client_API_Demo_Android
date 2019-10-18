package com.huawei.opensdk.ec_sdk_demo;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.huawei.opensdk.callmgr.CallMgr;
import com.huawei.opensdk.commonservice.common.LocContext;
import com.huawei.opensdk.ec_sdk_demo.util.ActivityUtil;
import com.huawei.opensdk.loginmgr.LoginMgr;
import com.huawei.opensdk.servicemgr.ServiceMgr;

import org.json.JSONObject;

public class ECApplication extends Application
{
    private static int appCount = 0;

    private static Application app = null;
    private static JSONObject lastInfo = null;

    @Override
    public void onCreate()
    {
        super.onCreate();
        setApp(this);
        //Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStarted(Activity activity) {
                if (0 == appCount)
                {
                    String currentActivity = ActivityUtil.getCurrentActivity(LocContext.getContext());
                    if (!currentActivity.equals("LoginActivity") || !currentActivity.equals("AnonymousJoinConfActivity"))
                    {
                        boolean isInCall = CallMgr.getInstance().isExistCall();
                        LoginMgr.getInstance().reRegister(isInCall);
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

    @Override
    public void onTerminate()
    {
        super.onTerminate();

        ServiceMgr.getServiceMgr().stopService();
    }
}
