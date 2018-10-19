package com.huawei.opensdk.contactservice.eaddr;

import java.util.List;

/**
 * This class is about get user Information class.
 * 获取到的企业通讯录联系人的信息类
 */
public class QueryContactsInfoResult {

    /**
     * Serial
     * 序列号
     */
    private int querySeq;

    /**
     * The list of user Information
     * 联系人信息列表
     */
    private List<EntAddressBookInfo> list;

    public QueryContactsInfoResult() {
    }

    public int getQuerySeq() {
        return querySeq;
    }

    public void setQuerySeq(int querySeq) {
        this.querySeq = querySeq;
    }

    public List<EntAddressBookInfo> getList() {
        return list;
    }

    public void setList(List<EntAddressBookInfo> list) {
        this.list = list;
    }
}
