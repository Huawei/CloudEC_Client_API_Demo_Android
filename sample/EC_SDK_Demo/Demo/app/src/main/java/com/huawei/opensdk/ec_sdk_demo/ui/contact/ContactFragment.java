package com.huawei.opensdk.ec_sdk_demo.ui.contact;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huawei.common.constant.ResponseCodeHandler;
import com.huawei.contacts.PersonalContact;
import com.huawei.data.AddFriendResp;
import com.huawei.data.PersonalTeam;
import com.huawei.data.base.BaseResponseData;
import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcastReceiver;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.adapter.ContactAdapter;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.logic.contact.mvp.IContactRecordContract;
import com.huawei.opensdk.ec_sdk_demo.logic.contact.mvp.ContactRecordPresenter;
import com.huawei.opensdk.ec_sdk_demo.ui.IntentConstant;
import com.huawei.opensdk.ec_sdk_demo.ui.base.AbsFragment;
import com.huawei.opensdk.ec_sdk_demo.util.ActivityUtil;
import com.huawei.opensdk.imservice.ImMgr;

import java.util.List;

/**
 * This class is about the contact group and team fragment.
 */
public class ContactFragment extends AbsFragment implements IContactRecordContract.IContactRecordView, LocBroadcastReceiver
{
    private TextView tvTeamName;
    private ListView listView;
    private RelativeLayout groupItemRL;
    private RelativeLayout teamItemRL;
    private IContactRecordContract.IContactRecordPresenter mPresenter;
    private ContactAdapter adapter;
    private List<PersonalContact> contactList;
    private PersonalTeam personalTeam;

    private String[] teamAction = {CustomBroadcastConstants.ACTION_REFRESH_TEAM_MEMBER,
            CustomBroadcastConstants.ACTION_IM_ADD_CONTACT_RESULT,
            CustomBroadcastConstants.ACTION_IM_DELETE_CONTACT_RESULT};

    private static String checkedTeamName;

    public static String getCheckedTeamName() {
        return checkedTeamName;
    }

    public static void setCheckedTeamName(String checkedTeamName) {
        ContactFragment.checkedTeamName = checkedTeamName;
    }

    private int teamId = -1;

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
                Intent intent = new Intent(IntentConstant.GROUPS_ACTIVITY_ACTION);
                ActivityUtil.startActivity(getActivity(), intent);
            }
        });

        teamItemRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCheckedTeamName(tvTeamName.getText().toString());
                Intent intent = new Intent(IntentConstant.TEAMS_ACTIVITY_ACTION);
                ActivityUtil.startActivity(getActivity(), intent);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                PersonalContact personalContact = contactList.get(position);
                Intent intent = new Intent(IntentConstant.CONTACT_DETAIL_ACTIVITY_ACTION);
                Bundle bundle = new Bundle();
                bundle.putSerializable(UIConstants.PERSONAL_CONTACT, personalContact);
                intent.putExtra(UIConstants.BUNDLE_KEY, bundle);
                ActivityUtil.startActivity(getActivity(), intent);
            }
        });
    }

    private void initData()
    {
        mPresenter = new ContactRecordPresenter(this);
        contactList = ImMgr.getInstance().getFriends();
        ImMgr.getInstance().getGroups();
    }

    @Override
    public void onResume()
    {
        LocBroadcast.getInstance().registerBroadcast(this, teamAction);
        changeContacts();
        adapter.setData(contactList);
        adapter.notifyDataSetChanged();
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
                    if (msg.obj instanceof PersonalTeam)
                    {
                        personalTeam = (PersonalTeam) msg.obj;
                        tvTeamName.setText(personalTeam.getTeamName());
                        contactList = personalTeam.getContactList();
                        teamId = TeamsActivity.getTeamIndex();
                    }
                    else if ((int)msg.obj == -1)
                    {
                        tvTeamName.setText(getString(R.string.contact));
                        contactList = ImMgr.getInstance().getFriends();
                        teamId = -1;
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private void changeContacts()
    {
        contactList = ImMgr.getInstance().getFriends();
        if (teamId == -1)
        {

        }
        else
        {
            personalTeam = ImMgr.getInstance().getTeams().get(teamId);
            contactList = personalTeam.getContactList();
        }
    }

    @Override
    public void onReceive(String broadcastName, Object obj) {
        switch (broadcastName)
        {
            case CustomBroadcastConstants.ACTION_REFRESH_TEAM_MEMBER:
                Message msg = handler.obtainMessage(UIConstants.IM_TEAM_CHECKED, obj);
                handler.sendMessage(msg);
                break;
            case CustomBroadcastConstants.ACTION_IM_ADD_CONTACT_RESULT:
                AddFriendResp addFriendResp = (AddFriendResp) obj;
                if (addFriendResp.getStatus() == ResponseCodeHandler.ResponseCode.REQUEST_SUCCESS)
                {
                    changeContacts();
                }
                break;
            case CustomBroadcastConstants.ACTION_IM_DELETE_CONTACT_RESULT:
                BaseResponseData deleteFriendResp = (BaseResponseData) obj;
                if (deleteFriendResp.getStatus() == ResponseCodeHandler.ResponseCode.REQUEST_SUCCESS)
                {
                    changeContacts();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        LocBroadcast.getInstance().unRegisterBroadcast(this, teamAction);
        super.onDestroy();
    }
}
