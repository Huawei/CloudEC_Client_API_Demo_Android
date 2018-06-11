package com.huawei.opensdk.imservice;

/**
 * This class is about im account server parameters.
 * IM 账号配置相关参数类
 */
public class ImAccountInfo {

    /**
     * login account
     * 登陆账号
     */
    private String account;

    /**
     * auth token
     * 鉴权token值
     */
    private String token;

    /**
     * password
     * 密码
     */
    private String password;

    /**
     * Photo server address
     * 头像服务器地址
     */
    private String portraitServer;

    /**
     * Push message server address
     * 推送消息服务器地址
     */
    private String pushServer;

    /**
     * MAA server address
     * MAA服务器地址
     */
    private String maaServer;

    /**
     * MAA server port
     * MAA服务器端口号
     */
    private int maaServerPort;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPortraitServer() {
        return portraitServer;
    }

    public void setPortraitServer(String portraitServer) {
        this.portraitServer = portraitServer;
    }

    public String getPushServer() {
        return pushServer;
    }

    public void setPushServer(String pushServer) {
        this.pushServer = pushServer;
    }

    public String getMaaServer() {
        return maaServer;
    }

    public void setMaaServer(String maaServer) {
        this.maaServer = maaServer;
    }

    public int getMaaServerPort() {
        return maaServerPort;
    }

    public void setMaaServerPort(int maaServerPort) {
        this.maaServerPort = maaServerPort;
    }
}
