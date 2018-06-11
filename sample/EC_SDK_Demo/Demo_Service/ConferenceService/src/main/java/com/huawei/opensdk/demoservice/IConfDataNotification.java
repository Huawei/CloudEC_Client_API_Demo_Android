package com.huawei.opensdk.demoservice;

public interface IConfDataNotification
{
    /**
     * On data conf event.
     *
     * @param dataConfEventType the data conf event type
     * @param dataParams        the data params
     */
    void onDataConfEvent(int dataConfEventType, Object dataParams);
}
