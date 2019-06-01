package com.huawei.opensdk.ec_sdk_demo.ui.im;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.adapter.MemberShowAdapter;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.ui.base.BaseActivity;
import com.huawei.opensdk.imservice.ImChatGroupInfo;
import com.huawei.opensdk.imservice.ImContactInfo;
import com.huawei.opensdk.imservice.ImMgr;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is about get group member list activity.
 */
public class GroupMemberActivity extends BaseActivity
{
    private List<ImContactInfo> mContacts;
    private List<ImContactInfo> mDeleteContacts = new ArrayList<>();
    private MemberShowAdapter mAdapter;
    private ListView mGroupMemberLv;
    private TextView mTitleTv;
    private TextView mNameTv;
    private boolean mIsDeleteMode;
    private TextView mOkTv;
    private ImChatGroupInfo mChatGroupInfo;

    @Override
    public void initializeComposition()
    {
        setContentView(R.layout.group_member_activity);
        mGroupMemberLv = (ListView) findViewById(R.id.group_member_lv);
        mTitleTv = (TextView) findViewById(R.id.title_text);
        mOkTv = (TextView) findViewById(R.id.right_text);
        mNameTv = (TextView) findViewById(R.id.contact_name);
        mTitleTv.setText(getString(R.string.group_member));
        for (ImContactInfo contact : mContacts)
        {
            if (contact.getAccount().equals(mChatGroupInfo.getOwnerAccount()))
            {
                mContacts.remove(contact);
                mNameTv.setText(mChatGroupInfo.getOwnerAccount());
                break;
            }
        }
        mAdapter.setMemberList(mContacts);
        mGroupMemberLv.setAdapter(mAdapter);
        if (mIsDeleteMode)
        {
            mOkTv.setText(getString(R.string.btn_sure));
            mGroupMemberLv.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    mDeleteContacts.add(mContacts.get(position));
                    mContacts.remove(position);
                    mAdapter.setMemberList(mContacts);
                    mAdapter.notifyDataSetChanged();
                }
            });

            mOkTv.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    int result = 0;
                    for (ImContactInfo contact : mDeleteContacts)
                    {
                        result = ImMgr.getInstance().delChatGroupMember(contact.getAccount());
                    }
                    if (0 != result)
                    {
                        showToast(R.string.del_attendee_fail);
                        finish();
                    }
                }
            });
        }
    }

    @Override
    public void initializeData()
    {
        Intent intent = getIntent();
        mChatGroupInfo = (ImChatGroupInfo) intent.getSerializableExtra(UIConstants.IM_CHAT_GROUP_INFO);
        mContacts = mChatGroupInfo.getList();
        mIsDeleteMode = UIConstants.GROUP_OPERATE_DELETE.equals(getIntent().getStringExtra(UIConstants.GROUP_OPERATE_MODE));
        mAdapter = new MemberShowAdapter(this, mChatGroupInfo.getOwnerAccount());
    }
}
