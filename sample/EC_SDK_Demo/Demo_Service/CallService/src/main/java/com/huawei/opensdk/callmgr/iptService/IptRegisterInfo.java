package com.huawei.opensdk.callmgr.iptService;

import com.huawei.ecterminalsdk.base.TsdkIptServiceInfo;

import object.TupServiceRightCfg;

/**
 * This class is about IPT Registration status information
 * IPT注册状态信息类
 */
public class IptRegisterInfo extends TsdkIptServiceInfo {
    private String registerNumber;

    public String getRegisterNumber() {
        return registerNumber;
    }

    public IptRegisterInfo setRegisterNumber(String registerNumber) {
        this.registerNumber = registerNumber;
        return this;
    }
}
