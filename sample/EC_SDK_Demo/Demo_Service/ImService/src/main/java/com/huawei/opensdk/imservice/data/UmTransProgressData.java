package com.huawei.opensdk.imservice.data;

/**
 * This class is a base class about the type Um trans progress data.
 * 富媒体资源传输进度数据类
 */
public class UmTransProgressData
{
    /**
     * The message id.
     * 消息id
     */
    private long msgId;

    /**
     * The um resource id.
     * 媒体资源id
     */
    private int mediaSourceId;

    /**
     * Rich Media transfer Progress
     * 富媒体传输进度
     */
    private int progress;

    public long getMsgId()
    {
        return msgId;
    }

    public void setMsgId(long msgId)
    {
        this.msgId = msgId;
    }

    public int getMediaSourceId()
    {
        return mediaSourceId;
    }

    public void setMediaSourceId(int mediaSourceId)
    {
        this.mediaSourceId = mediaSourceId;
    }

    public int getProgress()
    {
        return progress;
    }

    public void setProgress(int progress)
    {
        this.progress = progress;
    }
}
