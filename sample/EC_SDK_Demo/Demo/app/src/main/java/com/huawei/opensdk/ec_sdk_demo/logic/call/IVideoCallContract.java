package com.huawei.opensdk.ec_sdk_demo.logic.call;

import android.view.SurfaceView;

import com.huawei.opensdk.callmgr.CallInfo;

public interface IVideoCallContract
{
    interface VideoCallBaseView
    {
        void switchMuteBtn(boolean currentMuteStatus);

        void setTime(String time);
    }

    interface VideoCallBaserPresenter
    {
        void setCurrentCallInfo(CallInfo callInfo);

        void endCall();

        void transferToConference();

        String getOppositeNumber();

        boolean getIsSpeakerRoute();

        boolean getIsMuteMic();

        int getCallType();

        String getOppositeName();

        void muteCall();

        int switchAudioRoute();

        void videoToAudio();

        void holdVideo();

        void videoDestroy();

        void removeCallback();

        void switchCamera();

        void switchCameraStatus(boolean isCameraClose);

        void executorShutDown();

        SurfaceView getHideVideoView();

        SurfaceView getLocalVideoView();

        SurfaceView getRemoteVideoView();

        void setAutoRotation(Object object, boolean isOpen);

        void muteSpeaker(boolean isMute);
    }
}
