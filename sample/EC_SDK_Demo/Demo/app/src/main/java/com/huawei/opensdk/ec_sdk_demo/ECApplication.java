package com.huawei.opensdk.ec_sdk_demo;

import android.app.Application;

import com.huawei.opensdk.servicemgr.ServiceMgr;

public class ECApplication extends Application
{

    @Override
    public void onCreate()
    {
        super.onCreate();

        //Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
    }

    @Override
    public void onTerminate()
    {
        super.onTerminate();

        ServiceMgr.getServiceMgr().stopService();
    }
}
