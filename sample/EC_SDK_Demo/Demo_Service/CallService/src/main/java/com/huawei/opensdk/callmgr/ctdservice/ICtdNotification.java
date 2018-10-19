package com.huawei.opensdk.callmgr.ctdservice;

/**
 * This interface is about Ctd event notify.
 * CTD事件回调接口
 */
public interface ICtdNotification {

    /**
     * This method is used to get start ctd call result.
     * 开始呼叫结果响应事件
     * @param result      Indicates start Ctd call operation result
     *                    结果响应，成功返回0，失败返回相应的错误码
     * @param description Indicates description
     *                    结果描述
     */
    void onStartCtdCallResult(int result, String description);
}
