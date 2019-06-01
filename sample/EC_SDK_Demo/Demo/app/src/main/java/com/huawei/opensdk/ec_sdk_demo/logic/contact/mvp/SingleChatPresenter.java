package com.huawei.opensdk.ec_sdk_demo.logic.contact.mvp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

//import com.huawei.contacts.PersonalContact;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.ui.IntentConstant;
import com.huawei.opensdk.ec_sdk_demo.ui.base.MVPBasePresenter;
import com.huawei.opensdk.ec_sdk_demo.util.ActivityUtil;


public class SingleChatPresenter extends MVPBasePresenter<ISingleChatContract.ISingleChatView> implements ISingleChatContract.ISingleChatPresenter
{
    private Context context;

    public SingleChatPresenter(Context context)
    {
        this.context = context;
    }

//    @Override
//    public void addMember(PersonalContact personalContact)
//    {
//        // TODO: 2017/9/29
//        Intent intent = new Intent(IntentConstant.GROUP_CREATE_ACTIVITY_ACTION);
//        Bundle bundle = new Bundle();
//        bundle.putSerializable(UIConstants.PERSONAL_CONTACT, personalContact);
//        intent.putExtra(UIConstants.BUNDLE_KEY, bundle);
//        ActivityUtil.startActivity(context, intent);
//    }

    @Override
    public void clearHistory()
    {
        // TODO: 2017/9/29
    }
}
