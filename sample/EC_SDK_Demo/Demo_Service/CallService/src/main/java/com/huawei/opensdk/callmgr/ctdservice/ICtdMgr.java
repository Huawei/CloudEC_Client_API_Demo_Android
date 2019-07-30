package com.huawei.opensdk.callmgr.ctdservice;


/**
 * This interface is about interface of CTD business management.
 * This interface defines the CTD business function interface that is provided for invoking by UI.
 * CTD模块功能管理接口
 * 此接口是为了UI 调用而提供的业务功能接口。
 */
public interface ICtdMgr {

    /**
     * This method is used to register ctd module UI callback.
     * 注册回调
     * @param ctdNotification CTD event notification
     *                        回调事件
     */
    void regCtdNotification(ICtdNotification ctdNotification);

    /**
     * This method is used to start a ctd call.
     * 发起一路CTD呼叫
     * @param calleeNumber the callee number
     *                     被叫号码
     * @param callerNumber the caller number
     *                     主叫号码
     * @return result If success return 0, otherwise return corresponding error code.
     *                成功返回0，失败返回相应的错误码
     */
    long makeCtdCall(String calleeNumber, String callerNumber);
}
