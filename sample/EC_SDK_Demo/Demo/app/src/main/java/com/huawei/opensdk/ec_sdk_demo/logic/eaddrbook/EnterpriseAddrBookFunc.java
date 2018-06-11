package com.huawei.opensdk.ec_sdk_demo.logic.eaddrbook;


import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;
import com.huawei.opensdk.contactservice.eaddr.EntAddressBookConstant;
import com.huawei.opensdk.contactservice.eaddr.IEntAddressBookNotification;


public class EnterpriseAddrBookFunc implements IEntAddressBookNotification {

    private static EnterpriseAddrBookFunc enterpriseAddrBookFunc;

    public static EnterpriseAddrBookFunc getInstance()
    {
        if (null == enterpriseAddrBookFunc)
        {
            enterpriseAddrBookFunc = new EnterpriseAddrBookFunc();
        }
        return enterpriseAddrBookFunc;
    }

    @Override
    public void onEntAddressBookNotify(EntAddressBookConstant.Event event, Object object) {
        switch (event)
        {
            case SEARCH_SELF_COMPLETE:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_ENTERPRISE_GET_SELF_RESULT, object);
                break;
            case SEARCH_CONTACTS_COMPLETE:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_ENTERPRISE_GET_CONTACT_RESULT, object);
                break;
            case SEARCH_CONTACTS_NOT_FOUND:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_ENTERPRISE_GET_CONTACT_NULL, null);
                break;
            case SEARCH_CONTACTS_FAILED:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_ENTERPRISE_GET_CONTACT_FAILED, null);
                break;
            default:
                break;
        }
    }

    @Override
    public void onEntAddressBookIconNotify(EntAddressBookConstant.Event event, Object object) {
        switch (event)
        {
            case GET_SELF_ICON:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_ENTERPRISE_GET_SELF_PHOTO_RESULT, object);
                break;
            case GET_CONTACTS_SYSTEM_ICON:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_ENTERPRISE_GET_HEAD_SYS_PHOTO, object);
                break;
            case GET_CONTACTS_CUSTOM_ICON:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_ENTERPRISE_GET_HEAD_DEF_PHOTO, object);
                break;
            case GET_CONTACTS_ICON_FAILED:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_ENTERPRISE_GET_HEAD_PHOTO_FAILED, object);
                break;
            default:
                break;
        }
    }

    @Override
    public void onEntAddressBookDepartmentNotify(EntAddressBookConstant.Event event, Object object) {
        switch (event)
        {
            case SEARCH_DEPARTMENT_RESULT:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_ENTERPRISE_GET_DEPARTMENT_RESULT, object);
                break;
            case SEARCH_DEPARTMENTS_NOT_FOUND:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_ENTERPRISE_GET_DEPARTMENT_NULL, null);
                break;
            case SEARCH_DEPARTMENT_FAILED:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_ENTERPRISE_GET_DEPARTMENT_FAILED, null);
                break;
            default:
                break;
        }
    }
}
