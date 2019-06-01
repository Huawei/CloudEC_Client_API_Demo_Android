package com.huawei.opensdk.ec_sdk_demo.ui.im;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

//import com.huawei.contacts.PersonalContact;
//import com.huawei.data.ConstGroup;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.adapter.AddGroupMemberListAdapter;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.logic.contact.mvp.GroupMemberAddPresenter;
import com.huawei.opensdk.ec_sdk_demo.logic.contact.mvp.IGroupMemberAddContract;
import com.huawei.opensdk.ec_sdk_demo.ui.IntentConstant;
import com.huawei.opensdk.ec_sdk_demo.ui.base.MVPBaseActivity;
import com.huawei.opensdk.ec_sdk_demo.util.ActivityUtil;
import com.huawei.opensdk.ec_sdk_demo.widget.TwoInputDialog;
import com.huawei.opensdk.imservice.ImChatGroupInfo;
import com.huawei.opensdk.imservice.ImContactInfo;
import com.huawei.opensdk.imservice.ImMgr;

import java.util.LinkedList;

/**
 * This class is about add member group activity.
 */
public class GroupMemberAddActivity extends MVPBaseActivity<IGroupMemberAddContract.IGroupMemberAddView,
        GroupMemberAddPresenter> implements IGroupMemberAddContract.IGroupMemberAddView, View.OnClickListener
{
    public static final int ADD_MEMBER_REQUEST_CODE = 1;
    private AddGroupMemberListAdapter mAddMemberAdapter;
    private ListView mFriendsAddGroupMemberLv;
//    private ConstGroup mConstGroup;
    private ImageView mSearchBtn;
    private TextView mTitleTv;
    private TextView mOkBtn;

    private LinkedList<ImContactInfo> mAddMemberContacts = new LinkedList<>();
    private ImChatGroupInfo mChatGroupInfo;

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
        mChatGroupInfo = (ImChatGroupInfo) intent.getSerializableExtra(UIConstants.IM_CHAT_GROUP_INFO);
//        mPresenter.setConstGroup(mConstGroup);
        mPresenter.queryGroupMembers();
    }

//    @Override
//    public void refreshGroupMember(final List<PersonalContact> list)
//    {
//        runOnUiThread(new Runnable()
//        {
//            @Override
//            public void run()
//            {
//                mAddMemberAdapter.setMemberList(list);
//                mAddMemberAdapter.notifyDataSetChanged();
//            }
//        });
//    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.search_btn:
                showAddMemberDialog();
                break;
            case R.id.ok_tv:
//                mPresenter.inviteToGroup();
                addChatGroupMember();
//                finish();
                break;
            default:
                break;
        }
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
            if (mChatGroupInfo.getOwnerAccount().equals(contactInfo.getAccount()))
            {
                continue;
            }
            int result = ImMgr.getInstance().addChatGroupMember(true, mChatGroupInfo.getOwnerAccount(), contactInfo.getAccount());
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
        mPresenter.handleResult(requestCode, resultCode, data);
    }

    @Override
    public void toast(int resId)
    {
        Toast.makeText(this, getString(resId), Toast.LENGTH_SHORT).show();
    }

}
