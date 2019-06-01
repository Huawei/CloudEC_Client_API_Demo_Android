package com.huawei.opensdk.imservice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ImRecentChatInfo implements Serializable {

    private String tag;

    private String chatName;

    private List<ImChatMsgInfo> chatMsgList = new ArrayList<>();

    private ImChatMsgInfo lastChatMsg;

    private int unReadMsgCount;

    private List<ImChatMsgInfo> unReadMsgList = new ArrayList<>();

    private boolean isGroupChat;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public List<ImChatMsgInfo> getChatMsgList() {
        return chatMsgList;
    }

    public void setChatMsgList(List<ImChatMsgInfo> chatMsgList) {
        this.chatMsgList = chatMsgList;
    }

    public ImChatMsgInfo getLastChatMsg() {
        return lastChatMsg;
    }

    public void setLastChatMsg(ImChatMsgInfo lastChatMsg) {
        this.lastChatMsg = lastChatMsg;
    }

    public int getUnReadMsgCount() {
        return unReadMsgCount;
    }

    public void setUnReadMsgCount(int unReadMsgCount) {
        this.unReadMsgCount = unReadMsgCount;
    }

    public List<ImChatMsgInfo> getUnReadMsgList() {
        return unReadMsgList;
    }

    public void setUnReadMsgList(List<ImChatMsgInfo> unReadMsgList) {
        this.unReadMsgList = unReadMsgList;
    }

    public boolean isGroupChat() {
        return isGroupChat;
    }

    public void setGroupChat(boolean groupChat) {
        isGroupChat = groupChat;
    }
}
