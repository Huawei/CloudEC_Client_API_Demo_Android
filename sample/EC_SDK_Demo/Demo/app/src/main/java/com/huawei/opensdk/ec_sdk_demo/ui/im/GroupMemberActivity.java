package com.huawei.opensdk.ec_sdk_demo.ui.im;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.huawei.contacts.ContactTools;
import com.huawei.data.ConstGroup;
import com.huawei.data.ConstGroupContact;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.adapter.MemberShowAdapter;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.ui.base.BaseActivity;
import com.huawei.opensdk.imservice.ImMgr;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is about get group member list activity.
 */
public class GroupMemberActivity extends BaseActivity
{
    private List<ConstGroupContact> mContacts;
    private List<ConstGroupContact> mDeleteContacts = new ArrayList<>();
    private MemberShowAdapter mAdapter;
    private ListView mGroupMemberLv;
    private TextView mTitleTv;
    private TextView mNameTv;
    private boolean mIsDeleteMode;
    private TextView mOkTv;
    private ConstGroup mConstGroup;

    @Override
    public void initializeComposition()
    {
        setContentView(R.layout.group_member_activity);
        mGroupMemberLv = (ListView) findViewById(R.id.group_member_lv);
        mTitleTv = (TextView) findViewById(R.id.title_text);
        mOkTv = (TextView) findViewById(R.id.right_text);
        mNameTv = (TextView) findViewById(R.id.contact_name);
        mTitleTv.setText(getString(R.string.group_member));
        for (ConstGroupContact contact : mContacts)
        {
            if (contact.getEspaceNumber().equals(mConstGroup.getOwner()))
            {
                mContacts.remove(contact);
                mNameTv.setText(ContactTools.getDisplayName(contact));
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
                    for (ConstGroupContact contact : mDeleteContacts)
                    {
                        ImMgr.getInstance().kickGroupMember(mConstGroup, contact.getEspaceNumber());
                    }
                    finish();
                }
            });
        }
    }

    @Override
    public void initializeData()
    {
        mContacts = (ArrayList<ConstGroupContact>) getIntent().getSerializableExtra(UIConstants.GROUP_MEMBER);
        mConstGroup = (ConstGroup)getIntent().getSerializableExtra(UIConstants.CONST_GROUP);
        mIsDeleteMode = UIConstants.GROUP_OPERATE_DELETE.equals(getIntent().getStringExtra(UIConstants.GROUP_OPERATE_MODE));
        mAdapter = new MemberShowAdapter(this, mConstGroup.getOwner());
    }
}
