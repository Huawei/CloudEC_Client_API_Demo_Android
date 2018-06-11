package com.huawei.opensdk.ec_sdk_demo.logic.contact.mvp;



public class ContactRecordPresenter implements IContactRecordContract.IContactRecordPresenter
{
    private IContactRecordContract.IContactRecordView mView;

    public ContactRecordPresenter(IContactRecordContract.IContactRecordView view)
    {
        mView = view;
    }
}
