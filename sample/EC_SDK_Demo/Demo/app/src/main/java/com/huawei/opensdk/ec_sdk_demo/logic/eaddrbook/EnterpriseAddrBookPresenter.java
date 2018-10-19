package com.huawei.opensdk.ec_sdk_demo.logic.eaddrbook;

import android.content.Intent;

import com.huawei.opensdk.ec_sdk_demo.ui.IntentConstant;


public class EnterpriseAddrBookPresenter implements EnterpriseAddrBookContract.EAddrBookPresenter {

    private EnterpriseAddrBookContract.EAddrBookView eAddrBookViewView;

    public EnterpriseAddrBookPresenter(EnterpriseAddrBookContract.EAddrBookView eAddrBookViewView)
    {
        this.eAddrBookViewView = eAddrBookViewView;
    }

    @Override
    public void gotoEAddrBookEntry() {
        Intent intent = new Intent(IntentConstant.EADDR_BOOK_ACTIVITY_ACTION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(IntentConstant.DEFAULT_CATEGORY);
        eAddrBookViewView.doStartActivity(intent);
    }
}
