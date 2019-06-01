package com.huawei.opensdk.imservice;

import java.io.Serializable;
import java.util.List;

public class ImContactGroupInfo implements Serializable {

    private long groupId;
    private String groupName;
    private int groupIndex;
    private int groupMember;
    private List<ImContactInfo> list;

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getGroupIndex() {
        return groupIndex;
    }

    public void setGroupIndex(int groupIndex) {
        this.groupIndex = groupIndex;
    }

    public int getGroupMember() {
        return groupMember;
    }

    public void setGroupMember(int groupMember) {
        this.groupMember = groupMember;
    }

    public List<ImContactInfo> getList() {
        return list;
    }

    public void setList(List<ImContactInfo> list) {
        this.list = list;
    }
}
