package com.huawei.opensdk.demoservice.data.callBackWapper;

import com.huawei.opensdk.demoservice.data.ConferenceEntity;

/**
 * This class is about conference result
 * 创会结果类
 */
public class BookConfResult
{
    /**
     * Conference information entity
     * 会议信息实体
     */
    private ConferenceEntity conferenceEntity;

    /**
     * Conference result code
     *  创会结果状态码
     */
    private int resultCode;

    public ConferenceEntity getConferenceEntity()
    {
        return conferenceEntity;
    }

    public void setConferenceEntity(ConferenceEntity conferenceEntity)
    {
        this.conferenceEntity = conferenceEntity;
    }

    public int getResultCode()
    {
        return resultCode;
    }

    public void setResultCode(int resultCode)
    {
        this.resultCode = resultCode;
    }
}
