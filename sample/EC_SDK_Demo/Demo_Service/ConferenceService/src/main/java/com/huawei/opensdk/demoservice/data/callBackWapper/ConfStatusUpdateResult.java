package com.huawei.opensdk.demoservice.data.callBackWapper;

import com.huawei.tup.confctrl.sdk.TupConfECAttendeeInfo;
import com.huawei.tup.confctrl.sdk.TupConfInfo;

import java.util.List;

/**
 * This class is about conference Status Update Results
 * 会议状态更新结果
 */
public class ConfStatusUpdateResult
{
    /**
     * Conference info
     * 会议信息
     */
    private TupConfInfo tupConfInfo;

    /**
     * EC conference attendee info
     * EC创建即时会议结果
     */
    private List<TupConfECAttendeeInfo> list;

    public TupConfInfo getTupConfInfo()
    {
        return tupConfInfo;
    }

    public void setTupConfInfo(TupConfInfo tupConfInfo)
    {
        this.tupConfInfo = tupConfInfo;
    }

    public List<TupConfECAttendeeInfo> getList()
    {
        return list;
    }

    public void setList(List<TupConfECAttendeeInfo> list)
    {
        this.list = list;
    }
}
