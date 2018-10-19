package com.huawei.opensdk.contactservice.eaddr;

import com.huawei.ecterminalsdk.base.TsdkDepartmentInfo;

import java.util.List;

public class QueryDepartmentResult {
    /**
     * Serial
     * 序列号
     */
    private int querySeq;

    /**
     * The list of user Information
     * 联系人信息列表
     */
    private List<TsdkDepartmentInfo> list;

    public QueryDepartmentResult() {
    }

    public int getQuerySeq() {
        return querySeq;
    }

    public void setQuerySeq(int querySeq) {
        this.querySeq = querySeq;
    }

    public List<TsdkDepartmentInfo> getList() {
        return list;
    }

    public void setList(List<TsdkDepartmentInfo> list) {
        this.list = list;
    }
}
