package com.huawei.opensdk.imservice;

import java.io.Serializable;

public class ImChatMsgInfo implements Serializable {

    private String content = "";

    /**
     * 发送方账号
     */
    private String fromId;

    private String fromName;

    /**
     * 接收方账号或者群组ID
     */
    private String toId;

    private String toName;

    private long serverMsgId;

    private long utcStamp;

    private ImConstant.ChatMsgType msgType;


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public String getToName() {
        return toName;
    }

    public void setToName(String toName) {
        this.toName = toName;
    }

    public long getServerMsgId() {
        return serverMsgId;
    }

    public void setServerMsgId(long serverMsgId) {
        this.serverMsgId = serverMsgId;
    }

    public long getUtcStamp() {
        return utcStamp;
    }

    public void setUtcStamp(long utcStamp) {
        this.utcStamp = utcStamp;
    }

    public ImConstant.ChatMsgType getMsgType() {
        return msgType;
    }

    public void setMsgType(ImConstant.ChatMsgType msgType) {
        this.msgType = msgType;
    }
}
