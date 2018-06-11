package com.huawei.opensdk.ec_sdk_demo.logic.login;

import android.content.SharedPreferences;

import com.huawei.opensdk.loginmgr.LoginConstant;

/**
 * This class is about login model logic.
 */
public class LoginModel
{
    private final SharedPreferences mSharedPreferences;

    public LoginModel(SharedPreferences sharedPreferences)
    {
        this.mSharedPreferences = sharedPreferences;
    }

    /**
     * get account
     * @return account
     */
    public String getAccount()
    {
        return mSharedPreferences.getString(LoginConstant.TUP_ACCOUNT, LoginConstant.BLANK_STRING);
    }

    /**
     * Whether it is the first login
     * @return 0: the first time to log in; 1: not the first time to log in
     */
    public int getFirstLogin()
    {
        return mSharedPreferences.getInt(LoginConstant.FIRST_LOGIN_FLAG, LoginConstant.FIRST_LOGIN);
    }

    /**
     * get password
     * @return password
     */
    public String getPassword()
    {
        return mSharedPreferences.getString(LoginConstant.TUP_PASSWORD, LoginConstant.BLANK_STRING);
    }

    /**
     * get registered server address
     * @return server address
     */
    public String getRegServer()
    {
        return mSharedPreferences.getString(LoginConstant.TUP_REGSERVER, LoginConstant.BLANK_STRING);
    }

    /**
     * get server port
     * @return server port
     */
    public String getPort()
    {
        return mSharedPreferences.getString(LoginConstant.TUP_PORT, LoginConstant.BLANK_STRING);
    }

    public int getSrtpMode()
    {
        return mSharedPreferences.getInt(LoginConstant.TUP_SRTP, 0);
    }

    public int getSipTransport()
    {
        return mSharedPreferences.getInt(LoginConstant.TUP_SIP_TRANSPORT, 0);
    }

    public int getAppConfig()
    {
        return mSharedPreferences.getInt(LoginConstant.APPLY_CONFIG_PRIORITY, 0);
    }

    public String getUdpPort()
    {
        return mSharedPreferences.getString(LoginConstant.UDP_PORT, LoginConstant.UDP_DEFAULT);
    }

    public String getTlsPort()
    {
        return mSharedPreferences.getString(LoginConstant.TLS_PORT, LoginConstant.TLS_DEFAULT);
    }

    public int getPriority()
    {
        return mSharedPreferences.getInt(LoginConstant.PORT_CONFIG_PRIORITY, 0);
    }

    /**
     * save login params
     * @param userName user name
     * @param password password
     */
    public void saveLoginParams(String userName, String password)
    {
        mSharedPreferences.edit().putString(LoginConstant.TUP_ACCOUNT, userName)
                .putString(LoginConstant.TUP_PASSWORD, password)
                .putString(LoginConstant.TUP_PASSWORD, password)
                .putInt(LoginConstant.FIRST_LOGIN_FLAG, LoginConstant.ALREADY_LOGIN)
                .commit();
    }

    /**
     * initialize data
     */
    public void initServerData()
    {
        if (LoginConstant.FIRST_LOGIN == getFirstLogin())
        {
            mSharedPreferences.edit().putBoolean(LoginConstant.TUP_VPN, false)
                    .putString(LoginConstant.TUP_REGSERVER, LoginConstant.UPORTAL_REGISTER_SERVER)
                    .putString(LoginConstant.TUP_PORT, LoginConstant.UPORTAL_PORT)
                    .putInt(LoginConstant.TUP_SRTP, 0)
                    .putInt(LoginConstant.TUP_SIP_TRANSPORT, 0)
                    .putInt(LoginConstant.APPLY_CONFIG_PRIORITY, 1)
                    .putString(LoginConstant.UDP_PORT, LoginConstant.UDP_DEFAULT)
                    .putString(LoginConstant.TLS_PORT, LoginConstant.TLS_DEFAULT)
                    .putInt(LoginConstant.PORT_CONFIG_PRIORITY, 0)
                    .commit();
        }
    }
}
