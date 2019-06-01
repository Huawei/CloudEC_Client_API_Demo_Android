package com.huawei.opensdk.imservice;

import java.io.Serializable;
import java.util.List;

public class ImChatGroupInfo implements Serializable {

    private String groupId;

    private int groupType;

    private String manifesto;

    private String description;

    private String ownerAccount;

    private String groupName;

    private int joinAuthMode;

    private int isFixDiscuss;

    private int contactCount;

    private List<ImContactInfo> list;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public int getGroupType() {
        return groupType;
    }

    public void setGroupType(int groupType) {
        this.groupType = groupType;
    }

    public String getManifesto() {
        return manifesto;
    }

    public void setManifesto(String manifesto) {
        this.manifesto = manifesto;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOwnerAccount() {
        return ownerAccount;
    }

    public void setOwnerAccount(String ownerAccount) {
        this.ownerAccount = ownerAccount;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getJoinAuthMode() {
        return joinAuthMode;
    }

    public void setJoinAuthMode(int joinAuthMode) {
        this.joinAuthMode = joinAuthMode;
    }

    public int getIsFixDiscuss() {
        return isFixDiscuss;
    }

    public void setIsFixDiscuss(int isFixDiscuss) {
        this.isFixDiscuss = isFixDiscuss;
    }

    public int getContactCount() {
        return contactCount;
    }

    public void setContactCount(int contactCount) {
        this.contactCount = contactCount;
    }

    public List<ImContactInfo> getList() {
        return list;
    }

    public void setList(List<ImContactInfo> list) {
        this.list = list;
    }
}
