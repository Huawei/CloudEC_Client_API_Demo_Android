package com.huawei.opensdk.ec_sdk_demo.ui.im;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.contacts.PersonalContact;
import com.huawei.data.ConstGroup;
import com.huawei.data.ManageGroupResp;
import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcastReceiver;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.adapter.AddGroupMemberListAdapter;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.ui.IntentConstant;
import com.huawei.opensdk.ec_sdk_demo.ui.base.BaseActivity;
import com.huawei.opensdk.ec_sdk_demo.util.ActivityUtil;
import com.huawei.opensdk.imservice.ImMgr;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is about create group activity.
 */
public class CreateGroupActivity extends BaseActivity implements View.OnClickListener
{
    public static final int ADD_MEMBER_REQUEST_CODE = 1;
    private AddGroupMemberListAdapter mAddMemberAdapter;
    private ListView mFriendsAddGroupMemberLv;
    private ImageView mSearchBtn;
    private TextView mTitleTv;
    private TextView mOkBtn;
    private List<PersonalContact> mAddMemberContacts = new ArrayList<>();
    private String[] broadcastNames = new String[]{CustomBroadcastConstants.ACTION_CREATE_GROUP_RESULT};

    private LocBroadcastReceiver receiver = new LocBroadcastReceiver()
    {
        @Override
        public void onReceive(String broadcastName, Object obj)
        {
            switch (broadcastName)
            {
                case CustomBroadcastConstants.ACTION_CREATE_GROUP_RESULT:
                    if (obj == null)
                    {
                        createFailed();
                    }
                    else
                    {
                        ManageGroupResp manageGroupResp = (ManageGroupResp) obj;
                        String groupId = manageGroupResp.getGroupId();
                        ConstGroup constGroup = ImMgr.getInstance().getGroupById(groupId);
                        if (constGroup == null)
                        {
                            createFailed();
                        }
                        else
                        {
                            Intent intent = new Intent(IntentConstant.IM_CHAT_ACTIVITY_ACTION);
                            intent.putExtra(UIConstants.CHAT_TYPE, constGroup);
                            ActivityUtil.startActivity(CreateGroupActivity.this, intent);
                            finish();
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private void createFailed()
    {
        showToast(R.string.create_group_fail);
        finish();
    }


    @Override
    public void initializeComposition()
    {
        setContentView(R.layout.chat_member_add_activity);
        mFriendsAddGroupMemberLv = (ListView) findViewById(R.id.add_group_members_lv);
        mOkBtn = (TextView) findViewById(R.id.ok_tv);
        mSearchBtn = (ImageView) findViewById(R.id.search_btn);
        mTitleTv = (TextView) findViewById(R.id.title_text);
        mTitleTv.setText(getString(R.string.contact_select));

        mAddMemberAdapter = new AddGroupMemberListAdapter(this);
        mAddMemberAdapter.setMemberList(mAddMemberContacts);
        mFriendsAddGroupMemberLv.setAdapter(mAddMemberAdapter);

        mSearchBtn.setOnClickListener(this);
        mOkBtn.setOnClickListener(this);
    }

    @Override
    public void initializeData()
    {
        LocBroadcast.getInstance().registerBroadcast(receiver, broadcastNames);

        Bundle bundle = getIntent().getBundleExtra(UIConstants.BUNDLE_KEY);
        if (bundle == null)
        {
            return;
        }
        PersonalContact personalContact = (PersonalContact) bundle.getSerializable(UIConstants.PERSONAL_CONTACT);
        if (personalContact != null)
        {
            mAddMemberContacts.add(personalContact);
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.search_btn:
                Intent intent = new Intent(IntentConstant.IM_SEARCH_ACTIVITY_ACTION);
                intent.putExtra(UIConstants.GROUP_OPERATE_MODE, UIConstants.GROUP_OPERATE_ADD);
                ActivityUtil.startActivityForResult(this, intent, ADD_MEMBER_REQUEST_CODE);
                break;
            case R.id.ok_tv:
                if (mAddMemberContacts.isEmpty())
                {
                    finish();
                }
                ImMgr.getInstance().createDiscussGroup(mAddMemberContacts);
                //finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (Activity.RESULT_OK == resultCode && GroupMemberAddActivity.ADD_MEMBER_REQUEST_CODE == requestCode)
        {
            PersonalContact personalContact = (PersonalContact) data.getSerializableExtra(UIConstants.PERSONAL_CONTACT);
            if (null != personalContact)
            {
                if (mAddMemberContacts.contains(personalContact))
                {
                    Toast.makeText(this, getString(R.string.discussion_exist_tip), Toast.LENGTH_SHORT).show();
                    return;
                }
                mAddMemberContacts.add(personalContact);
                mAddMemberAdapter.setMemberList(mAddMemberContacts);
                mAddMemberAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        LocBroadcast.getInstance().unRegisterBroadcast(receiver, broadcastNames);
    }
}
