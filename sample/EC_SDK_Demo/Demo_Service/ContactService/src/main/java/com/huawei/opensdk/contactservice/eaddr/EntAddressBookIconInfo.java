package com.huawei.opensdk.contactservice.eaddr;

/**
 * This class is about get user Avatar Information class.
 * 获取到的企业通讯录联系人的头像信息类
 */
public class EntAddressBookIconInfo {

    /**
     * Account of the user
     * 用户账号
     */
    private String account;

    /**
     * Icon file of the user query
     * 头像路径--> 用户的头像为自定义头像
     */
    private String iconFile = "";

    /**
     * Icon id of the user query
     * 系统头像id--> 用户头像为系统头像
     */
    private int iconId = -1;

    /**
     * Serial number of the user query
     * 查询头像对应的系列号
     */
    private int iconSeq;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getIconFile() {
        return iconFile;
    }

    public void setIconFile(String iconFile) {
        this.iconFile = iconFile;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public int getIconSeq() {
        return iconSeq;
    }

    public void setIconSeq(int iconSeq) {
        this.iconSeq = iconSeq;
    }
}
