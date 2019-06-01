package com.huawei.opensdk.ec_sdk_demo.ui.contact;

import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.logic.contact.mvp.ContactDetailPresenter;
import com.huawei.opensdk.ec_sdk_demo.logic.contact.mvp.IContactDetailContract;
import com.huawei.opensdk.ec_sdk_demo.module.headphoto.ContactHeadFetcher;
import com.huawei.opensdk.ec_sdk_demo.ui.base.MVPBaseActivity;
import com.huawei.opensdk.ec_sdk_demo.util.PopupWindowUtil;
import com.huawei.opensdk.ec_sdk_demo.widget.SimpleListDialog;
import com.huawei.opensdk.imservice.ImConstant;
import com.huawei.opensdk.imservice.ImContactGroupInfo;
import com.huawei.opensdk.imservice.ImContactInfo;
import com.huawei.opensdk.loginmgr.LoginMgr;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is about contact detail info activity.
 */
public class ContactDetailActivity extends MVPBaseActivity<IContactDetailContract.IContactDetailView, ContactDetailPresenter> implements IContactDetailContract.IContactDetailView, View.OnClickListener
{
    private ContactHeadFetcher headFetcher;

    private ImageView opContactIv;
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
    private PopupWindow popupWindowPW;
    private SimpleListDialog teamDialog;

    private String userAccount = LoginMgr.getInstance().getAccount();
    private ImContactInfo contactInfo;
    private List<ImContactGroupInfo> contactGroupList = new ArrayList<>();
    private boolean isMove = false;
    private long contactId;
    private long currentGroupId;

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

        opContactIv = (ImageView) findViewById(R.id.right_iv);
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

        if (userAccount.equals(contactInfo.getAccount()))
        {
            opContactIv.setVisibility(View.GONE);
            enterChatIv.setVisibility(View.GONE);
            deleteFriendBtn.setVisibility(View.GONE);
        }

        setData();
    }

    private void setData()
    {
//        headFetcher.loadHead(personalContact, headIv, true);
        stateIv.setImageResource(getStatusResource(contactInfo.getStatus()));
        nameTv.setText(contactInfo.getName());
        numberTv.setVisibility(View.GONE);
        signatureTv.setVisibility(View.VISIBLE);
        signatureTv.setText(contactInfo.getSignature());
        accountTv.setText(contactInfo.getAccount());
        departmentTv.setText(contactInfo.getDepartmentName());
        postTv.setText(contactInfo.getTitle());
        softPhoneTv.setText(contactInfo.getSoftNumber());
        mobilePhoneTv.setText(contactInfo.getMobile());
        videoSoftPhoneTv.setText(contactInfo.getSoftNumber());
        emailTv.setText(contactInfo.getEmail());
        locationTv.setText(contactInfo.getAddress());
        faxTv.setText(contactInfo.getFax());
        zipTv.setText(contactInfo.getZipCode());
        deleteFriendBtn.setText(R.string.deletecontact);

        if (userAccount.equals(contactInfo.getAccount()))
        {
            return;
        }

        opContactIv.setOnClickListener(this);
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
        contactInfo = (ImContactInfo) intent.getSerializableExtra(UIConstants.IM_CONTACT_INFO);
        contactGroupList = (List<ImContactGroupInfo>) intent.getSerializableExtra(UIConstants.IM_CONTACT_GROUPS);
        if (null == contactInfo)
        {
            finish();
        }

        contactId = contactInfo.getContactId();
        currentGroupId = contactInfo.getGroupId();
        // 预留 目前好友状态不用探测，会自动上报用户状态
//        mPresenter.detectUserStatus(contactInfo.getAccount());
        headFetcher = new ContactHeadFetcher(this);
        mPresenter.registerBroadcast();
        mPresenter.setUserInfo(contactInfo);
    }

    private static int getStatusResource(ImConstant.ImStatus status)
    {
        int drwId = 0;
        switch (status)
        {
            case AWAY:
                drwId = R.drawable.state_offline;
                break;
            case ON_LINE:
                drwId = R.drawable.state_online;
                break;
            case BUSY:
                drwId = R.drawable.state_busy;
                break;
            case XA:
                drwId = R.drawable.state_away;
                break;
            case DND:
                drwId = R.drawable.state_uninterrupt;
                break;
            default:
                break;
        }

        return drwId;
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

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.right_iv:
                showMoreManageContact();
                break;
            case R.id.enter_chat:
                mPresenter.gotoChatActivity(this);
                break;
            case R.id.call_Item_layout1:
//                mPresenter.makeCall(personalContact.getBinderNumber());
                break;
            case R.id.call_Item_layout2:
//                mPresenter.makeCall(personalContact.getMobile());
                break;
            case R.id.video_Item_layout1:
//                mPresenter.makeVideo(personalContact.getBinderNumber());
                break;
            case R.id.deletecontact:
                mPresenter.deleteContact(contactId);
                break;
            default:
                break;
        }
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (popupWindowPW != null && popupWindowPW.isShowing())
            {
                popupWindowPW.dismiss();
            }
            switch (v.getId())
            {
                case R.id.move_contact:
                    isMove = true;
                    mPresenter.refreshGroupList(contactGroupList, contactId);
                    break;
                case R.id.copy_contact:
                    isMove = false;
                    mPresenter.refreshGroupList(contactGroupList, contactId);
                    break;
                    default:
                        break;
            }
        }
    };

    private void showMoreManageContact()
    {
        View view = getLayoutInflater().inflate(R.layout.popup_op_contact, null);
        TextView tvMove = (TextView) view.findViewById(R.id.move_contact);
        TextView tvCopy = (TextView) view.findViewById(R.id.copy_contact);

        tvMove.setOnClickListener(clickListener);
        tvCopy.setOnClickListener(clickListener);

        popupWindowPW = PopupWindowUtil.getInstance().generatePopupWindow(view);
        popupWindowPW.showAtLocation(findViewById(R.id.right_iv), Gravity.TOP|Gravity.RIGHT, 60, 250);
    }

    @Override
    public void refreshDeleteContactButton(boolean isFriend)
    {
//        personalContact.setFriend(isFriend ? 1 : 0);
//        deleteFriendBtn.setText(isFriend ? R.string.deletecontact : R.string.addtofriend);
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

    @Override
    public void finishActivity(final int id) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showToast(id);
                finish();
            }
        });
    }

    @Override
    public void showTeamDialog(final List<ImContactGroupInfo> groupInfoList) {
        List<Object> teamName = new ArrayList<>();
        for (ImContactGroupInfo groupInfo : groupInfoList)
        {
            teamName.add(groupInfo.getGroupName());
        }
        if (0 == teamName.size())
        {
            showCustomToast(R.string.no_op_group);
            return;
        }
        teamDialog = new SimpleListDialog(this, teamName);
        teamDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                teamDialog.dismiss();
                long newGroupId = groupInfoList.get(position).getGroupId();
                mPresenter.moveContact(newGroupId, currentGroupId, contactId, isMove);
            }
        });
        teamDialog.show();
    }

    @Override
    public void showUserStatus(final ImConstant.ImStatus status) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                stateIv.setImageResource(getStatusResource(status));
            }
        });
    }
}
