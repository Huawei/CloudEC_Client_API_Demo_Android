package com.huawei.opensdk.demoservice.data.callBackWapper;

import com.huawei.tup.confctrl.sdk.TupConfOptResult;

/**
 * This class is about conference result
 * 会议结果类
 */
public class ConfOptResult extends TupConfOptResult{
    /**
     * conference id
     * 会议id
     */
    private String confID;

    public String getConfID() {
        return confID;
    }

    public void setConfID(String confID) {
        this.confID = confID;
    }
}
