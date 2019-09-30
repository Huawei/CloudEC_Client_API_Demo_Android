package com.huawei.opensdk.loginmgr;

/**
 * This class is about login module constant definition.
 * 登陆模块常量类.
 */
public class LoginConstant {

    public static final String FILE_NAME = "TupLoginParams";
    public static final String ANONYMOUS_FILE_NAME = "AnonymousParams";

    public static final int FIRST_LOGIN = 0;
    public static final int ALREADY_LOGIN = 1;
    public static final String FIRST_LOGIN_FLAG = "firstLogin";
    public static final String TUP_ACCOUNT = "tupAccount";
    public static final String TUP_PASSWORD = "tupPassword";
    public static final String TUP_VPN = "tupVpn";
    public static final String TUP_REGSERVER = "tupRegisterServer";
    public static final String TUP_PORT = "tupPort";
    public static final String TUP_SRTP = "tupSrtp";
    public static final String TUP_SIP_TRANSPORT = "tupSipTransport";
    public static final String BLANK_STRING = "";

    public static final String UPORTAL_REGISTER_SERVER = "bmeeting.huaweicloud.com";
    public static final String UPORTAL_PORT = "443";

    public static final String APPLY_CONFIG_PRIORITY = "applyConfigPriority";
    public static final String UDP_PORT = "udpPort";
    public static final String TLS_PORT = "tlsPort";
    public static final String PORT_CONFIG_PRIORITY = "portConfigPriority";
    public static final String UDP_DEFAULT = "5060";
    public static final String TLS_DEFAULT = "5061";
    public static final String SECURITY_TUNNEL = "securityTunnel";
    public static final String CONF_CTRL_PROTOCOL = "confCtrlProtocol";

    public static final String ANONYMOUS_ADDRESS = "anonymousAddress";
    public static final String ANONYMOUS_PORT = "anonymousPort";
    public static final String NICKNAME = "nickname";
    public static final String FIRSTSTART = "firststart";

    public static final String AUTO_LOGIN = "auto_login";

    /**
     * The constant of Thread pool Size
     * 线程池大小
     */
    public static final int FIXED_NUMBER = 5;

    /**
     * 网络连接状态
     */
    public static final int NO_NETWORK = 10;
    public static final int NETWORK_CONNECTED = 11;
    public static final int NETWORK_CONNECTED_FAILED = 12;
    public static final int NETWORK_CONNECTED_SUCCESS = 13;

    /**
     * This class is about login result
     * 登录结果枚举类
     */
    public enum LoginUIEvent {

        VOIP_LOGIN_SUCCESS(),

        PASSWORD_INFO(),

        IM_LOGIN_SUCCESS(),

        LOGIN_FAILED(),

        FIREWALL_DETECT_FAILED(),

        BUILD_STG_FAILED(),

        MODIFY_PASSWORD(),

        RESUME_IND(),

        RESUME_RESULT(),

        LOGOUT(),
    }

}
