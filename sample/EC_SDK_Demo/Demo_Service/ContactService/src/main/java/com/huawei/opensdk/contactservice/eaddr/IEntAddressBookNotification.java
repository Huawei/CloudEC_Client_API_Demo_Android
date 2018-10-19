package com.huawei.opensdk.contactservice.eaddr;

/**
 * This interface is about enterprise address book service event notify.
 * 企业通讯录业务事件通知接口
 */
public interface IEntAddressBookNotification {

    /**
     * This is a callback function to handle the getting user's icon result.
     * 处理获取用户头像返回结果的回调
     * @param event  Indicates event
     *               获取联系人的结果事件
     * @param object Indicates contact info
     *               获取到的具体信息
     */
    void onEntAddressBookNotify(EntAddressBookConstant.Event event, Object object);

    /**
     * This is a callback function to handle the getting user's icon result.
     * 处理获取用户头像返回结果的回调
     * @param event  Indicates event
     *               获取头像的结果事件
     * @param object Indicates icon info
     *               获取到的具体信息
     */
    void onEntAddressBookIconNotify(EntAddressBookConstant.Event event, Object object);

    /**
     * This is a callback function to handle the getting user's department result.
     * 处理获取部门返回结果的回调
     * @param event  Indicates event
     *               获取部门的结果事件
     * @param object Indicates icon info
     *               获取到的具体信息
     */
    void onEntAddressBookDepartmentNotify(EntAddressBookConstant.Event event, Object object);
}
