package com.huawei.opensdk.contactservice.eaddr;

/**
 * This class is about contact Information class.
 * 企业通讯录联系人信息类
 */
public class EntAddressBookInfo {

    /**
     * User's account
     * 联系人账号
     */
    private String eaddrAccount;

    /**
     * User's terminal
     * 用户的终端号码
     */
    private String terminal;

    /**
     * User's department
     * 用户所在的部门
     */
    private String eaddrDept;

    /**
     * User's custom avatar path
     * 用户的自定义头像路径
     */
    private String headIconPath = "";

    /**
     * User's system Avatar ID
     * 用户的系统头像id
     */
    private int sysIconID = 0;

    public EntAddressBookInfo() {
    }

    public String getEaddrAccount() {
        return eaddrAccount;
    }

    public void setEaddrAccount(String eaddrAccount) {
        this.eaddrAccount = eaddrAccount;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public String getEaddrDept() {
        return eaddrDept;
    }

    public void setEaddrDept(String eaddrDept) {
        this.eaddrDept = eaddrDept;
    }

    public String getHeadIconPath() {
        return headIconPath;
    }

    public void setHeadIconPath(String headIconPath) {
        this.headIconPath = headIconPath;
    }

    public int getSysIconID() {
        return sysIconID;
    }

    public void setSysIconID(int sysIconID) {
        this.sysIconID = sysIconID;
    }
}
