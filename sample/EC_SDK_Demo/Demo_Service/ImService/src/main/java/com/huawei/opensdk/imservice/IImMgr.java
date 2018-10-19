package com.huawei.opensdk.imservice;

import android.content.Context;

import com.huawei.contacts.PersonalContact;

import java.util.List;

/**
 * This interface is about Im function init interface.
 * Im功能接口
 */
public interface IImMgr
{

    /**
     * This method is used to unit init service component.
     * 初始化业务组件
     * @param context Indicates context
     *                上下文
     */
    void sdkInit(Context context, String appPath);

    /**
     * This method is used to get strangers list.
     * 获取陌生人
     * @return Return contact list
     *                返回获得的列表
     */
    List<PersonalContact> getStrangers();
}
