package com.huawei.opensdk.ec_sdk_demo;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.huawei.opensdk.servicemgr.ServiceMgr;

public class ECApplication extends Application
{
    private static int appCount = 0;

    private static Application app = null;

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

    @Override
    public void onTerminate()
    {
        super.onTerminate();

        ServiceMgr.getServiceMgr().stopService();
    }
}
