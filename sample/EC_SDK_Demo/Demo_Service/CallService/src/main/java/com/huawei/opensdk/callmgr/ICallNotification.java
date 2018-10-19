package com.huawei.opensdk.callmgr;


/**
 * Call module and UI callback.
 */
public interface ICallNotification
{
    void onCallEventNotify(CallConstant.CallEvent event, Object params);
}
