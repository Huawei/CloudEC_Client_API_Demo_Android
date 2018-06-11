package com.huawei.opensdk.ec_sdk_demo.ui.im;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.contacts.PersonalContact;
import com.huawei.data.ConstGroup;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.adapter.AddGroupMemberListAdapter;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.logic.contact.mvp.GroupMemberAddPresenter;
import com.huawei.opensdk.ec_sdk_demo.logic.contact.mvp.IGroupMemberAddContract;
import com.huawei.opensdk.ec_sdk_demo.ui.IntentConstant;
import com.huawei.opensdk.ec_sdk_demo.ui.base.MVPBaseActivity;
import com.huawei.opensdk.ec_sdk_demo.util.ActivityUtil;

import java.util.List;

/**
 * This class is about add member group activity.
 */
public class GroupMemberAddActivity extends MVPBaseActivity<IGroupMemberAddContract.IGroupMemberAddView,
        GroupMemberAddPresenter> implements IGroupMemberAddContract.IGroupMemberAddView, View.OnClickListener
{
    public static final int ADD_MEMBER_REQUEST_CODE = 1;
    private AddGroupMemberListAdapter mAddMemberAdapter;
    private ListView mFriendsAddGroupMemberLv;
    private ConstGroup mConstGroup;
    private ImageView mSearchBtn;
    private TextView mTitleTv;
    private TextView mOkBtn;

    @Override
    protected void onResume()
    {
        super.onResume();
        mPresenter.registerBroadcast();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mPresenter.unregisterBroadcast();
    }


    @Override
    protected IGroupMemberAddContract.IGroupMemberAddView createView()
    {
        return this;
    }

    @Override
    protected GroupMemberAddPresenter createPresenter()
    {
        return new GroupMemberAddPresenter();
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
        mFriendsAddGroupMemberLv.setAdapter(mAddMemberAdapter);

        mSearchBtn.setOnClickListener(this);
        mOkBtn.setOnClickListener(this);
    }

    @Override
    public void initializeData()
    {
        Intent intent = getIntent();
        mConstGroup = (ConstGroup) intent.getSerializableExtra(UIConstants.CONST_GROUP);
        mPresenter.setConstGroup(mConstGroup);
        mPresenter.queryGroupMembers();
    }

    @Override
    public void refreshGroupMember(final List<PersonalContact> list)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                mAddMemberAdapter.setMemberList(list);
                mAddMemberAdapter.notifyDataSetChanged();
            }
        });
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
                mPresenter.inviteToGroup();
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        mPresenter.handleResult(requestCode, resultCode, data);
    }

    @Override
    public void toast(int resId)
    {
        Toast.makeText(this, getString(resId), Toast.LENGTH_SHORT).show();
    }

}
