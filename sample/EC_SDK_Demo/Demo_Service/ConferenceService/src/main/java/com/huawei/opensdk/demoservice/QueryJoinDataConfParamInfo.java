package com.huawei.opensdk.demoservice;


/**
 * This class is about query join data conference param info.
 * 查询数据会议参数
 */
public class QueryJoinDataConfParamInfo {

    /**
     * 会议id
     */
    private String confId;

    /**
     * 会议接入码
     */
    private String passCode;

    /**
     * 会议URL
     */
    private String confUrl;

    /**
     * 随机码
     */
    private String random;

    public String getConfId() {
        return confId;
    }

    public void setConfId(String confId) {
        this.confId = confId;
    }

    public String getPassCode() {
        return passCode;
    }

    public void setPassCode(String passCode) {
        this.passCode = passCode;
    }

    public String getConfUrl() {
        return confUrl;
    }

    public void setConfUrl(String confUrl) {
        this.confUrl = confUrl;
    }

    public String getRandom() {
        return random;
    }

    public void setRandom(String random) {
        this.random = random;
    }


}
