package com.huawei.opensdk.ec_sdk_demo.ui.im;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

//import com.huawei.contacts.PersonalContact;
//import com.huawei.data.ConstGroup;
//import com.huawei.data.ManageGroupResp;
import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcastReceiver;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.adapter.AddGroupMemberListAdapter;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.ui.IntentConstant;
import com.huawei.opensdk.ec_sdk_demo.ui.base.BaseActivity;
import com.huawei.opensdk.ec_sdk_demo.util.ActivityUtil;
import com.huawei.opensdk.ec_sdk_demo.widget.TripleDialog;
import com.huawei.opensdk.ec_sdk_demo.widget.TwoInputDialog;
import com.huawei.opensdk.imservice.ImChatGroupInfo;
import com.huawei.opensdk.imservice.ImConstant;
import com.huawei.opensdk.imservice.ImContactInfo;
import com.huawei.opensdk.imservice.ImMgr;
import com.huawei.opensdk.loginmgr.LoginMgr;

import java.util.LinkedList;

/**
 * This class is about create group activity.
 */
public class CreateGroupActivity extends BaseActivity implements View.OnClickListener, LocBroadcastReceiver
{
    public static final int ADD_MEMBER_REQUEST_CODE = 1;
    private AddGroupMemberListAdapter mAddMemberAdapter;
    private ListView mFriendsAddGroupMemberLv;
    private ImageView mSearchBtn;
    private TextView mTitleTv;
    private TextView mOkBtn;
    private RelativeLayout mGroupTypeRL;
    private EditText mGroupNameEt;
    private TextView mGroupTypeTv;
    private ImageButton mClearGroupNameBtn;
    private String[] broadcastNames = new String[]{CustomBroadcastConstants.ACTION_CREATE_GROUP_RESULT,
        CustomBroadcastConstants.ACTION_IM_CHAT_GROUP_ADD,
        CustomBroadcastConstants.ACTION_IM_CHAT_GROUP_UPDATE};

    private ImChatGroupInfo chatGroupInfo = new ImChatGroupInfo();
    private String mSelfAccount;
    private String groupId;
    private LinkedList<ImContactInfo> mAddMemberContacts = new LinkedList<>();

    private void createFailed()
    {
        showToast(R.string.create_group_fail);
        finish();
    }


    @Override
    public void initializeComposition()
    {
        setContentView(R.layout.chat_group_create_activity);
        mFriendsAddGroupMemberLv = (ListView) findViewById(R.id.add_group_members_lv);
        mOkBtn = (TextView) findViewById(R.id.ok_tv);
        mSearchBtn = (ImageView) findViewById(R.id.search_btn);
        mTitleTv = (TextView) findViewById(R.id.title_text);
        mGroupTypeRL = (RelativeLayout) findViewById(R.id.rl_group_type);
        mGroupNameEt = (EditText) findViewById(R.id.group_name_et);
        mClearGroupNameBtn = (ImageButton) findViewById(R.id.edit_name);
        mGroupTypeTv = (TextView) findViewById(R.id.tv_group_type);
        mTitleTv.setText(getString(R.string.contact_select));
        mGroupNameEt.setText(mSelfAccount);

        mAddMemberAdapter = new AddGroupMemberListAdapter(this);
        mAddMemberAdapter.setData(mAddMemberContacts);
        mFriendsAddGroupMemberLv.setAdapter(mAddMemberAdapter);

        mSearchBtn.setOnClickListener(this);
        mOkBtn.setOnClickListener(this);
        mClearGroupNameBtn.setOnClickListener(this);
        mGroupTypeRL.setOnClickListener(this);
    }

    @Override
    public void initializeData()
    {
        mSelfAccount = LoginMgr.getInstance().getAccount();
        ImContactInfo selfInfo = new ImContactInfo();
        selfInfo.setAccount(mSelfAccount);
        mAddMemberContacts.addFirst(selfInfo);
        LocBroadcast.getInstance().registerBroadcast(this, broadcastNames);

        Bundle bundle = getIntent().getBundleExtra(UIConstants.BUNDLE_KEY);
        if (bundle == null)
        {
            return;
        }
//        PersonalContact personalContact = (PersonalContact) bundle.getSerializable(UIConstants.PERSONAL_CONTACT);
//        if (personalContact != null)
//        {
//            mAddMemberContacts.add(personalContact);
//        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.search_btn:
//                Intent intent = new Intent(IntentConstant.IM_SEARCH_ACTIVITY_ACTION);
//                intent.putExtra(UIConstants.GROUP_OPERATE_MODE, UIConstants.GROUP_OPERATE_ADD);
//                ActivityUtil.startActivityForResult(this, intent, ADD_MEMBER_REQUEST_CODE);
                showAddMemberDialog();
                break;
            case R.id.ok_tv:
//                if (mAddMemberContacts.isEmpty())
//                {
//                    finish();
//                }
//                ImMgr.getInstance().createDiscussGroup(mAddMemberContacts);
                chatGroupInfo.setGroupName(mGroupNameEt.getText().toString());
                chatGroupInfo.setOwnerAccount(mSelfAccount);
                groupId = ImMgr.getInstance().addChatGroup(chatGroupInfo);
                if (null == groupId)
                {
                    showToast(R.string.create_group_fail);
                    finish();
                }
                break;
            case R.id.rl_group_type:
                showTypePicker();
                break;
            case R.id.edit_name:
                mGroupNameEt.setText("");
                break;
            default:
                break;
        }
    }

    private void showTypePicker()
    {
        TripleDialog tripleDialog = new TripleDialog(this);
        tripleDialog.setTitle(getString(R.string.group_type));
        tripleDialog.setLeftText(getString(R.string.fixed_group));
        tripleDialog.setRightText(getString(R.string.discussion_group));
        tripleDialog.show();

        tripleDialog.setLeftButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatGroupInfo.setGroupType(ImConstant.FIXED);
                mGroupTypeTv.setText(R.string.fixed_group);
            }
        });

        tripleDialog.setRightButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatGroupInfo.setGroupType(ImConstant.DISCUSSION);
                mGroupTypeTv.setText(R.string.discussion_group);
            }
        });
    }

    private void showAddMemberDialog()
    {
        final TwoInputDialog inputDialog = new TwoInputDialog(this);
        inputDialog.setHint1(R.string.input_account);
        inputDialog.setHint2(R.string.input_name);
        inputDialog.show();

        inputDialog.setRightButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(inputDialog.getInput1()))
                {
                    showToast(R.string.invalid_number);
                    return;
                }
                ImContactInfo contactInfo = new ImContactInfo();
                contactInfo.setAccount(inputDialog.getInput1());
                mAddMemberContacts.add(contactInfo);
                refreshGroupMembers();
            }
        });
    }

    private void refreshGroupMembers()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAddMemberAdapter.setData(mAddMemberContacts);
                mAddMemberAdapter.notifyDataSetChanged();
            }
        });
    }

    private void addChatGroupMember()
    {
        if (null == mAddMemberContacts || 0 == mAddMemberContacts.size())
        {
            return;
        }
        for (ImContactInfo contactInfo : mAddMemberContacts)
        {
            if (mSelfAccount.equals(contactInfo.getAccount()))
            {
                continue;
            }
            int result = ImMgr.getInstance().addChatGroupMember(true, mSelfAccount, contactInfo.getAccount());
            if (0 != result)
            {
                showToast(R.string.add_member_failed);
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (Activity.RESULT_OK == resultCode && GroupMemberAddActivity.ADD_MEMBER_REQUEST_CODE == requestCode)
        {
//            PersonalContact personalContact = (PersonalContact) data.getSerializableExtra(UIConstants.PERSONAL_CONTACT);
//            if (null != personalContact)
//            {
//                if (mAddMemberContacts.contains(personalContact))
//                {
//                    Toast.makeText(this, getString(R.string.discussion_exist_tip), Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                mAddMemberContacts.add(personalContact);
//                mAddMemberAdapter.setMemberList(mAddMemberContacts);
                mAddMemberAdapter.notifyDataSetChanged();
//            }
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        LocBroadcast.getInstance().unRegisterBroadcast(this, broadcastNames);
    }

    @Override
    public void onReceive(String broadcastName, Object obj) {
        switch (broadcastName)
        {
            case CustomBroadcastConstants.ACTION_IM_CHAT_GROUP_ADD:
                addChatGroupMember();
                setResult(UIConstants.IM_RESULT_CODE_CHAT_GROUP_CREATE);
                finish();
                break;
            case CustomBroadcastConstants.ACTION_IM_CHAT_GROUP_UPDATE:

                break;
            case CustomBroadcastConstants.ACTION_CREATE_GROUP_RESULT:
                if (obj == null)
                {
                    createFailed();
                }
                else
                {
//                        ManageGroupResp manageGroupResp = (ManageGroupResp) obj;
//                        String groupId = manageGroupResp.getGroupId();
//                        ConstGroup constGroup = ImMgr.getInstance().getGroupById(groupId);
//                        if (constGroup == null)
                    {
                        createFailed();
                    }
//                        else
                    {
                        Intent intent = new Intent(IntentConstant.IM_CHAT_ACTIVITY_ACTION);
//                            intent.putExtra(UIConstants.CHAT_TYPE, constGroup);
                        ActivityUtil.startActivity(CreateGroupActivity.this, intent);
                        finish();
                    }
                }
                break;
            default:
                break;
        }
    }
}
