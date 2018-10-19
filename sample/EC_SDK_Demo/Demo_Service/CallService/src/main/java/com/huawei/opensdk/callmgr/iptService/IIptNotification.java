package com.huawei.opensdk.callmgr.iptService;

/**
 * This interface is about Ipt module and UI callback..
 * IPT模块UI回调
 */

public interface IIptNotification {

    /**
     * 设置ipt业务成功
     * @param type              Indicates service type
     *                          特征码业务类型
     * @param isEnable          Indicates set ipt enable result
     *                          设置ipt是否启用
     */
    void onSetIptServiceSuc(int type, int isEnable);

    /**
     * 设置ipt业务失败
     * @param type              Indicates service type
     *                          特征码业务类型
     * @param isEnable          Indicates set ipt enable result
     *                          设置ipt是否启用
     */
    void onSetIptServiceFal(int type, int isEnable);
}
