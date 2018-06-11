package com.huawei.opensdk.demoservice.data.callBackWapper;

import com.huawei.tup.confctrl.sdk.TupConfOptResult;

/**
 * This class is about hand up result
 * 举手结果类
 */
public class HandupResult
{
    /**
     * conference operation result
     * 会议操作结果
     */
    private TupConfOptResult tupConfOptResult;

    /**
     * Hand up
     * 是否举手
     */
    private boolean isHandup;

    public TupConfOptResult getTupConfOptResult()
    {
        return tupConfOptResult;
    }

    public void setTupConfOptResult(TupConfOptResult tupConfOptResult)
    {
        this.tupConfOptResult = tupConfOptResult;
    }

    public boolean isHandup()
    {
        return isHandup;
    }

    public void setHandup(boolean handup)
    {
        isHandup = handup;
    }
}
