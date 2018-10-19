package com.huawei.opensdk.contactservice.eaddr;

/**
 * This class is about definition of enterprise address constant.
 * 企业通讯录功能常量类
 */
public class EntAddressBookConstant {

    public enum Event
    {
        /**
         * Failed to search contacts
         * 查询联系人失败
         */
        SEARCH_CONTACTS_FAILED(),

        /**
         * No contacts were searched
         * 查询到0个联系人
         */
        SEARCH_CONTACTS_NOT_FOUND(),

        /**
         * No departments were searched
         * 查询到0个部门
         */
        SEARCH_DEPARTMENTS_NOT_FOUND(),

        /**
         * Search contacts completed
         * 查询联系人成功
         */
        SEARCH_CONTACTS_COMPLETE(),

        /**
         * Search self
         * 查询自己的信息
         */
        SEARCH_SELF_COMPLETE(),

        /**
         * Failed to obtain Avatar
         * 获取头像失败
         */
        GET_CONTACTS_ICON_FAILED(),

        /**
         * Search self icon info
         * 查询自己的头像信息
         */
        GET_SELF_ICON(),

        /**
         * Obtain the system icon
         * 获取系统头像
         */
        GET_CONTACTS_SYSTEM_ICON(),

        /**
         * Obtain the custom icon
         * 获取自定义头像
         */
        GET_CONTACTS_CUSTOM_ICON(),

        /**
         * Failed to search department
         * 查询部门失败
         */
        SEARCH_DEPARTMENT_FAILED(),

        /**
         * Success to search department
         * 查询部门成功
         */
        SEARCH_DEPARTMENT_RESULT(),

        UNKNOWN()
    }
}
