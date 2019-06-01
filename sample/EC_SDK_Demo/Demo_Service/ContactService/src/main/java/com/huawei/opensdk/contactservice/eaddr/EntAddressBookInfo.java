package com.huawei.opensdk.contactservice.eaddr;

import java.io.Serializable;

/**
 * This class is about contact Information class.
 * 企业通讯录联系人信息类
 */
public class EntAddressBookInfo implements Serializable {

    /**
     * User's account
     * 联系人账号
     */
    private String eaddrAccount;

    /**
     * User's name
     * 联系人账号
     */
    private String eaddrName;

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
     * User's gender
     * 用户的性别
     */
    private String gender;

    /**
     * User's signature
     * 用户的签名
     */
    private String signature;

    /**
     * User's title
     * 用户的职务
     */
    private String title;

    /**
     * User's mobile
     * 用户的手机号码
     */
    private String mobile;

    /**
     * User's email
     * 用户的邮箱
     */
    private String email;

    /**
     * User's address
     * 用户的地址
     */
    private String address;

    /**
     * User's zip code
     * 用户的邮编
     */
    private String zipCode;

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

    /**
     * User's user id
     * 用户的id
     */
    private long userId;

    public EntAddressBookInfo() {
    }

    public String getEaddrAccount() {
        return eaddrAccount;
    }

    public void setEaddrAccount(String eaddrAccount) {
        this.eaddrAccount = eaddrAccount;
    }

    public String getEaddrName() {
        return eaddrName;
    }

    public void setEaddrName(String eaddrName) {
        this.eaddrName = eaddrName;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
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

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
