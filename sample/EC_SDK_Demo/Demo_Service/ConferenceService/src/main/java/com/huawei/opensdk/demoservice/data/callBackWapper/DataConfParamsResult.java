package com.huawei.opensdk.demoservice.data.callBackWapper;

import com.huawei.tup.confctrl.sdk.TupConfOptResult;
import com.huawei.tup.confctrl.sdk.TupConfctrlDataconfParams;


public class DataConfParamsResult
{
    private TupConfOptResult tupConfOptResult;
    private TupConfctrlDataconfParams tupConfctrlDataconfParams;

    public TupConfOptResult getTupConfOptResult()
    {
        return tupConfOptResult;
    }

    public void setTupConfOptResult(TupConfOptResult tupConfOptResult)
    {
        this.tupConfOptResult = tupConfOptResult;
    }

    public TupConfctrlDataconfParams getTupConfctrlDataconfParams()
    {
        return tupConfctrlDataconfParams;
    }

    public void setTupConfctrlDataconfParams(
            TupConfctrlDataconfParams tupConfctrlDataconfParams)
    {
        this.tupConfctrlDataconfParams = tupConfctrlDataconfParams;
    }
}
