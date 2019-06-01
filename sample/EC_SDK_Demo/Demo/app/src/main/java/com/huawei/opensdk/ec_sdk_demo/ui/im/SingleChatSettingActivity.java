package com.huawei.opensdk.ec_sdk_demo.ui.im;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

//import com.huawei.contacts.PersonalContact;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.logic.contact.mvp.ISingleChatContract;
import com.huawei.opensdk.ec_sdk_demo.logic.contact.mvp.SingleChatPresenter;
import com.huawei.opensdk.ec_sdk_demo.module.headphoto.ContactHeadFetcher;
import com.huawei.opensdk.ec_sdk_demo.ui.base.MVPBaseActivity;

/**
 * This class is about P2P chat add member become discuss group activity.
 */
public class SingleChatSettingActivity extends MVPBaseActivity<ISingleChatContract.ISingleChatView, SingleChatPresenter> implements ISingleChatContract.ISingleChatView, View.OnClickListener
{

//    private PersonalContact personalContact;

    private ImageView headIv;
    private ImageView addContactBtn;
    private TextView clearHistoryTv;

    @Override
    protected ISingleChatContract.ISingleChatView createView()
    {
        return this;
    }

    @Override
    protected SingleChatPresenter createPresenter()
    {
        return new SingleChatPresenter(this);
    }

    @Override
    public void initializeComposition()
    {
        setContentView(R.layout.chat_setting_single);
        headIv = (ImageView) findViewById(R.id.contact_head_1);
        addContactBtn = (ImageView) findViewById(R.id.add_member_btn);
        clearHistoryTv = (TextView) findViewById(R.id.im_setting_clear_history_tv);
        ((TextView) findViewById(R.id.title_text)).setText(R.string.im_setting);

//        new ContactHeadFetcher(this).loadHead(personalContact, headIv, true);
        addContactBtn.setOnClickListener(this);
        clearHistoryTv.setOnClickListener(this);
    }

    @Override
    public void initializeData()
    {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra(UIConstants.BUNDLE_KEY);
//        personalContact = (PersonalContact) bundle.get(UIConstants.PERSONAL_CONTACT);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.add_member_btn:
//                mPresenter.addMember(personalContact);
                break;
            case R.id.im_setting_clear_history_tv:
                mPresenter.clearHistory();
                break;
            default:
                break;
        }
    }
}
