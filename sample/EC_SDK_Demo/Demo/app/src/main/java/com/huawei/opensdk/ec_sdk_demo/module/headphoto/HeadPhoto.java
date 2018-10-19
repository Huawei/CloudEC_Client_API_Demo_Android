package com.huawei.opensdk.ec_sdk_demo.module.headphoto;

import android.text.TextUtils;

/**
 * This class is about used to pass the parameters of the avatar, only to support sending a single avatar
 * 用来传递取头像的参数,只支持发送单个头像.
 */
public class HeadPhoto
{
    private String account = "";
    private String id = "";

    public HeadPhoto(String eSpaceNumber, String headId)
    {
        if (!TextUtils.isEmpty(eSpaceNumber))
        {
            account = eSpaceNumber;
        }
        
        if (!TextUtils.isEmpty(headId))
        {
            id = headId;
        }
    }

    public String getAccount()
    {
        return account;
    }

    public String getId()
    {
        return id;
    }

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof HeadPhoto)
        {
            HeadPhoto mPhoto = (HeadPhoto)o;
            // 需要判断id，因为连续两次请求相同账号，不同headid，先后两次请求，imageworker中逻辑会判断是否equal，导致第二次请求被舍弃
            return account.equals(mPhoto.getAccount()) && id.equals(mPhoto.getId());
        }

        return false;
    }

    @Override
    public int hashCode()
    {
        return account.length();
    }

    @Override
    public String toString()
    {
        return account;
    }
}
