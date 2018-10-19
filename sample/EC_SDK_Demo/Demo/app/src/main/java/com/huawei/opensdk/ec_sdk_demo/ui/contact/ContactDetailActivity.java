package com.huawei.opensdk.ec_sdk_demo.ui.contact;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huawei.contacts.PersonalContact;
import com.huawei.data.PersonalTeam;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.logic.contact.mvp.ContactDetailPresenter;
import com.huawei.opensdk.ec_sdk_demo.logic.contact.mvp.IContactDetailContract;
import com.huawei.opensdk.ec_sdk_demo.module.headphoto.ContactHeadFetcher;
import com.huawei.opensdk.ec_sdk_demo.ui.base.MVPBaseActivity;
import com.huawei.opensdk.ec_sdk_demo.widget.SimpleListDialog;
import com.huawei.opensdk.imservice.ImMgr;
import com.huawei.opensdk.loginmgr.LoginMgr;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is about contact detail info activity.
 */
public class ContactDetailActivity extends MVPBaseActivity<IContactDetailContract.IContactDetailView, ContactDetailPresenter> implements IContactDetailContract.IContactDetailView, View.OnClickListener
{
    private PersonalContact personalContact;
    private ContactHeadFetcher headFetcher;

    private ImageView headIv;
    private ImageView stateIv;
    private TextView nameTv;
    private TextView numberTv;
    private TextView signatureTv;
    private ImageView enterChatIv;
    private TextView accountTv;
    private TextView departmentTv;
    private TextView postTv;
    private TextView softPhoneTv;
    private TextView mobilePhoneTv;
    private TextView videoSoftPhoneTv;
    private TextView emailTv;
    private TextView locationTv;
    private TextView faxTv;
    private TextView zipTv;
    private Button deleteFriendBtn;
    private LinearLayout callItemSoft;
    private LinearLayout callItemMobile;
    private LinearLayout videoItemSoft;

    private SimpleListDialog teamDialog;
    private List<PersonalTeam> teamList;

    private String userAccount = LoginMgr.getInstance().getAccount();

    @Override
    protected IContactDetailContract.IContactDetailView createView()
    {
        return this;
    }

    @Override
    protected ContactDetailPresenter createPresenter()
    {
        return new ContactDetailPresenter();
    }

    @Override
    public void initializeComposition()
    {
        setContentView(R.layout.contact_detail);

        headIv = (ImageView) findViewById(R.id.blog_head_iv);
        stateIv = (ImageView) findViewById(R.id.blog_state_iv);
        nameTv = (TextView) findViewById(R.id.blog_name_tv);
        numberTv = (TextView) findViewById(R.id.blog_number_tv);
        signatureTv = (TextView) findViewById(R.id.contact_signature_tv);
        enterChatIv = (ImageView) findViewById(R.id.enter_chat);
        accountTv = (TextView) findViewById(R.id.detail_content_text1);
        departmentTv = (TextView) findViewById(R.id.detail_content_text2);
        postTv = (TextView) findViewById(R.id.detail_content_text3);
        softPhoneTv = (TextView) findViewById(R.id.call_content_text1);
        mobilePhoneTv = (TextView) findViewById(R.id.call_content_text2);
        videoSoftPhoneTv = (TextView) findViewById(R.id.video_content_text1);
        emailTv = (TextView) findViewById(R.id.email_content_text);
        locationTv = (TextView) findViewById(R.id.location_content_text);
        faxTv = (TextView) findViewById(R.id.fax_content_text);
        zipTv = (TextView) findViewById(R.id.zip_content_text);
        deleteFriendBtn = (Button) findViewById(R.id.deletecontact);
        callItemSoft = (LinearLayout) findViewById(R.id.call_Item_layout1);
        callItemMobile = (LinearLayout) findViewById(R.id.call_Item_layout2);
        videoItemSoft = (LinearLayout) findViewById(R.id.video_Item_layout1);

        if (userAccount.equals(personalContact.getEspaceNumber()))
        {
            enterChatIv.setVisibility(View.GONE);
            deleteFriendBtn.setVisibility(View.GONE);
        }

        setData();
    }

    private void setData()
    {
        headFetcher.loadHead(personalContact, headIv, true);
        nameTv.setText(personalContact.getName());
        numberTv.setVisibility(View.GONE);
        signatureTv.setVisibility(View.VISIBLE);
        signatureTv.setText(personalContact.getSignature());
        accountTv.setText(personalContact.getEspaceNumber());
        departmentTv.setText(personalContact.getDepartmentName());
        postTv.setText(personalContact.getPosition());
        softPhoneTv.setText(personalContact.getBinderNumber());
        mobilePhoneTv.setText(personalContact.getMobile());
        videoSoftPhoneTv.setText(personalContact.getBinderNumber());
        emailTv.setText(personalContact.getEmail());
        locationTv.setText(personalContact.getAddress());
        faxTv.setText(personalContact.getFax());
        zipTv.setText(personalContact.getPostalcode());
        deleteFriendBtn.setText(personalContact.isFriend() ? R.string.deletecontact : R.string.addtofriend);

        enterChatIv.setOnClickListener(this);
        callItemSoft.setOnClickListener(this);
        callItemMobile.setOnClickListener(this);
        videoItemSoft.setOnClickListener(this);
        deleteFriendBtn.setOnClickListener(this);

    }

    @Override
    public void initializeData()
    {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra(UIConstants.BUNDLE_KEY);
        personalContact = (PersonalContact) bundle.get(UIConstants.PERSONAL_CONTACT);
        if (personalContact == null)
        {
            finish();
        }
        mPresenter.setPersonalContact(personalContact);
        headFetcher = new ContactHeadFetcher(this);
        mPresenter.registerBroadcast();

        teamList = ImMgr.getInstance().getTeams();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mPresenter.unregisterBroadcast();
        dismissDialog(teamDialog);
    }

    private void dismissDialog(SimpleListDialog dialog)
    {
        if (null != dialog)
        {
            dialog.dismiss();
        }
    }

    private void showTeamDialog()
    {
        List<Object> teamId = new ArrayList<>();
        for (int i = 0; i < teamList.size(); i++)
        {
            teamId.add(teamList.get(i).getTeamName());
        }
        teamDialog = new SimpleListDialog(this, teamId);
        teamDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                teamDialog.dismiss();
                teamDialog = null;
                mPresenter.addContact(teamList.get(i).getTeamId());
            }
        });
        teamDialog.show();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.enter_chat:
                mPresenter.gotoChatActivity(this);
                break;
            case R.id.call_Item_layout1:
                mPresenter.makeCall(personalContact.getBinderNumber());
                break;
            case R.id.call_Item_layout2:
                mPresenter.makeCall(personalContact.getMobile());
                break;
            case R.id.video_Item_layout1:
                mPresenter.makeVideo(personalContact.getBinderNumber());
                break;
            case R.id.deletecontact:
                if (personalContact.isFriend())
                {
                    mPresenter.deleteContact();
                }
                else
                {
                    showTeamDialog();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void refreshDeleteContactButton(boolean isFriend)
    {
        personalContact.setFriend(isFriend ? 1 : 0);
        deleteFriendBtn.setText(isFriend ? R.string.deletecontact : R.string.addtofriend);
    }

    @Override
    public void showCustomToast(final int res)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                showToast(res);
            }
        });
    }
}
