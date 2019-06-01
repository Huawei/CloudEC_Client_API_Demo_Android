package com.huawei.opensdk.ec_sdk_demo.ui.contact;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.adapter.ContactAdapter;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.logic.contact.mvp.ContactRecordPresenter;
import com.huawei.opensdk.ec_sdk_demo.logic.contact.mvp.IContactRecordContract;
import com.huawei.opensdk.ec_sdk_demo.ui.IntentConstant;
import com.huawei.opensdk.ec_sdk_demo.ui.base.AbsFragment;
import com.huawei.opensdk.ec_sdk_demo.util.ActivityUtil;
import com.huawei.opensdk.imservice.ImContactGroupInfo;
import com.huawei.opensdk.imservice.ImContactInfo;
import com.huawei.opensdk.imservice.ImMgr;
import com.huawei.opensdk.loginmgr.LoginMgr;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is about the contact group and team fragment.
 */
public class ContactFragment extends AbsFragment implements IContactRecordContract.IContactRecordView
{
    private TextView tvTeamName;
    private ListView listView;
    private RelativeLayout groupItemRL;
    private RelativeLayout teamItemRL;
    private IContactRecordContract.IContactRecordPresenter mPresenter;
    private ContactAdapter adapter;
    private long currentContactGroupId = -1;
    private List<ImContactGroupInfo> contactGroupInfoList = new ArrayList<>();
    private List<ImContactInfo> contactList = new ArrayList<>();
    private String currentName;
    private boolean isFirstGroup = true;
    private boolean isImLogin = LoginMgr.getInstance().isImLogin();

    @Override
    public int getLayoutId()
    {
        return R.layout.contact_fragment;
    }

    @Override
    public void onViewCreated()
    {
        super.onViewCreated();
        initData();
        initView();
    }

    private void initView()
    {
        tvTeamName = (TextView) mView.findViewById(R.id.contacts_iv);
        listView = (ListView) mView.findViewById(R.id.contacts_list);
        groupItemRL = (RelativeLayout) mView.findViewById(R.id.contact_item_group);
        teamItemRL = (RelativeLayout) mView.findViewById(R.id.all_teams_iv);
        adapter = new ContactAdapter(getActivity());
        adapter.setData(contactList);
        listView.setAdapter(adapter);

        groupItemRL.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (isImLogin)
                {
                    Intent intent = new Intent(IntentConstant.GROUPS_ACTIVITY_ACTION);
                    ActivityUtil.startActivity(getActivity(), intent);
                }
            }
        });

        teamItemRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isImLogin)
                {
                    Intent intent = new Intent(IntentConstant.TEAMS_ACTIVITY_ACTION);
                    intent.putExtra(UIConstants.IM_CHECK_CONTACT_GROUP_NAME, currentContactGroupId);
                    startActivityForResult(intent, UIConstants.IM_REQUEST_CODE_CONTACT_GROUP);
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                ImContactInfo contactInfo = contactList.get(position);
                Intent intent = new Intent(IntentConstant.CONTACT_DETAIL_ACTIVITY_ACTION);
                intent.putExtra(UIConstants.IM_CONTACT_INFO, contactInfo);
                intent.putExtra(UIConstants.IM_CONTACT_GROUPS, (Serializable) contactGroupInfoList);
                ActivityUtil.startActivity(getActivity(), intent);
            }
        });
    }

    private void initData()
    {
        mPresenter = new ContactRecordPresenter(this);

        // im 没有登录则不获取联系人列表
        if (!isImLogin)
        {
            return;
        }
        contactGroupInfoList = ImMgr.getInstance().getAllContactGroupList();
        if (null == contactGroupInfoList || contactGroupInfoList.size() == 0)
        {
            return;
        }

        if (isFirstGroup)
        {
            // 登陆成功后第一次获取分组列表中默认的第一个分组
            currentContactGroupId = contactGroupInfoList.get(0).getGroupId();
        }
    }

    @Override
    public void onResume()
    {
        mPresenter.registerBroadcast();
        if (isImLogin)
        {
            mPresenter.getCurrentContactGroup(currentContactGroupId);
        }
        super.onResume();
    }

    @Override
    public void showLoading()
    {

    }

    @Override
    public void dismissLoading()
    {

    }

    @Override
    public void showCustomToast(int resID)
    {

    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case UIConstants.IM_TEAM_CHECKED:
                    mPresenter.getCurrentContactGroup(currentContactGroupId);
                    break;
                case UIConstants.IM_REFRESH_GROUP_INFO:
                    tvTeamName.setText(currentName);
                    adapter.setData(contactList);
                    adapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode)
        {
            case UIConstants.IM_REQUEST_CODE_CONTACT_GROUP:
                if (UIConstants.IM_RESULT_CODE_CONTACT_GROUP == resultCode)
                {
                    isFirstGroup = false;
                    currentContactGroupId = data.getLongExtra(UIConstants.IM_RETURN_CONTACT_GROUP_ID, -1);
                    handler.sendEmptyMessage(UIConstants.IM_TEAM_CHECKED);
                }
                break;
                default:
                    break;
        }
    }

    @Override
    public void onDestroy() {
        mPresenter.unregisterBroadcast();
        super.onDestroy();
    }

    @Override
    public void showCurrentGroupInfo(ImContactGroupInfo contactGroupInfo) {
        currentName = contactGroupInfo.getGroupName();
        contactList = contactGroupInfo.getList();
        handler.sendEmptyMessage(UIConstants.IM_REFRESH_GROUP_INFO);
    }
}
