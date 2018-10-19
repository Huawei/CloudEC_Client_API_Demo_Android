package com.huawei.opensdk.demoservice.data;

import java.io.Serializable;

/**
 * This class is about data conference parameter entity
 * 数据会议参数实体类
 */
public class DataConfParamEntity implements Serializable
{
    /**
     * 会议接入码
     */
    private String passCode;
    /**
     * 会议URL
     */
    private String dataConfUrl;
    /**
     * 会议随机码
     */
    private String dataRandom;
    /**
     * 会议ID
     */
    private String dataConfId;

    public String getPassCode()
    {
        return passCode;
    }

    public void setPassCode(String passCode)
    {
        this.passCode = passCode;
    }

    public String getDataConfUrl()
    {
        return dataConfUrl;
    }

    public void setDataConfUrl(String dataConfUrl)
    {
        this.dataConfUrl = dataConfUrl;
    }

    public String getDataRandom()
    {
        return dataRandom;
    }

    public void setDataRandom(String dataRandom)
    {
        this.dataRandom = dataRandom;
    }

    public String getDataConfId()
    {
        return dataConfId;
    }

    public void setDataConfId(String dataConfId)
    {
        this.dataConfId = dataConfId;
    }
}
