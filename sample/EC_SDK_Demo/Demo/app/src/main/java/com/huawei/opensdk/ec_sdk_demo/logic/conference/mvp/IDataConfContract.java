package com.huawei.opensdk.ec_sdk_demo.logic.conference.mvp;

import android.content.Context;
import android.view.SurfaceView;
import android.view.ViewGroup;

import com.huawei.opensdk.ec_sdk_demo.logic.BaseView;



public interface IDataConfContract
{
    interface DataConfView extends BaseView
    {
        void finishActivity();
        void dataConfActivityShare(boolean isShare,boolean isAllowAnnot);
        void displayConfChatMag(boolean isSelf, String msg);
    }

    interface IDataConfPresenter
    {
        void setConfID(String confID);

        String getSubject();

        boolean muteSelf();

        int switchLoudSpeaker();

        boolean isChairMan();

        void closeConf();

        void finishConf();

        void attachSurfaceView(ViewGroup container, Context context);

        void sendChatMsg(String content);

        void registerBroadcast();

        void unregisterBroadcast();

        SurfaceView getHideVideoView();

        SurfaceView getLocalVideoView();

        void setVideoContainer(Context context, ViewGroup smallLayout, ViewGroup hideLayout);

        int startAnnotation();

        void setAnnotationLocalStatus(boolean enable);
    }
}
