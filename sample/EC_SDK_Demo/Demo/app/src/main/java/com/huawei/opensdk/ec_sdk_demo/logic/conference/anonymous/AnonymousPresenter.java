package com.huawei.opensdk.ec_sdk_demo.logic.conference.anonymous;

import android.content.SharedPreferences;

import com.huawei.opensdk.loginmgr.LoginConstant;

public class AnonymousPresenter{

    private final SharedPreferences mSharedPreferences;

    public AnonymousPresenter(SharedPreferences sharedPreferences)
    {
        this.mSharedPreferences = sharedPreferences;
    }


    public void saveAnonymousAddress(String anonymousAddress,  String anonymousPort, String nickname, boolean isFirst)
    {
        mSharedPreferences.edit()
                .putString(LoginConstant.ANONYMOUS_ADDRESS, anonymousAddress)
                .putString(LoginConstant.ANONYMOUS_PORT, anonymousPort)
                .putString(LoginConstant.NICKNAME, nickname)
                .putBoolean(LoginConstant.FIRSTSTART, isFirst)
                .commit();
    }

    public String getAnonymousAddress(){
        return mSharedPreferences.getString(LoginConstant.ANONYMOUS_ADDRESS, LoginConstant.BLANK_STRING);
    }

    public String getAnonymousPort(){
        return mSharedPreferences.getString(LoginConstant.ANONYMOUS_PORT, LoginConstant.BLANK_STRING);
    }

    public String getNickname(){
        return mSharedPreferences.getString(LoginConstant.NICKNAME, LoginConstant.NICKNAME);
    }

    public boolean getFirstStart(){
        return mSharedPreferences.getBoolean(LoginConstant.FIRSTSTART, true);
    }
}
