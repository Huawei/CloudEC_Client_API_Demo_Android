package com.huawei.opensdk.demoservice.data.callBackWapper;

import com.huawei.tup.confctrl.sdk.TupConfAttendeeOptResult;

public class MuteAttendeeResult
{
    private TupConfAttendeeOptResult tupConfAttendeeOptResult;
    private boolean isMute;

    public TupConfAttendeeOptResult getTupConfAttendeeOptResult()
    {
        return tupConfAttendeeOptResult;
    }

    public void setTupConfAttendeeOptResult(TupConfAttendeeOptResult tupConfAttendeeOptResult)
    {
        this.tupConfAttendeeOptResult = tupConfAttendeeOptResult;
    }

    public boolean isMute()
    {
        return isMute;
    }

    public void setMute(boolean mute)
    {
        isMute = mute;
    }
}
