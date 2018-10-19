package com.huawei.opensdk.ec_sdk_demo.ui.contact;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.huawei.contacts.ContactLogic;
import com.huawei.contacts.group.ConstGroupManager;
import com.huawei.data.ConstGroup;
import com.huawei.device.DeviceManager;
import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcastReceiver;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.adapter.GroupAdapter;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.ui.IntentConstant;
import com.huawei.opensdk.ec_sdk_demo.ui.base.BaseActivity;
import com.huawei.opensdk.ec_sdk_demo.util.ActivityUtil;
import com.huawei.opensdk.imservice.ImMgr;
import com.huawei.os.ActivityStack;

import java.util.List;

/**
 * This class is about group list activity.
 * 群组列表
 */
public class GroupsActivity extends BaseActivity implements View.OnClickListener, LocBroadcastReceiver
{

    private ListView listView;

    private List<ConstGroup> groupList;

    private GroupAdapter groupAdapter;
    private TextView rightTv;
    private TextView titleTv;
    private String[] mBroadcastNames = new String[]{CustomBroadcastConstants.ACTION_REFRESH_GROUP_MEMBER};


    @Override
    public void initializeData()
    {
        LocBroadcast.getInstance().registerBroadcast(this, mBroadcastNames);
        groupList = ImMgr.getInstance().getGroups();
    }

    @Override
    public void initializeComposition()
    {
        setContentView(R.layout.groups);

        listView = (ListView) findViewById(R.id.group_list);
        rightTv = (TextView) findViewById(R.id.right_text);
        titleTv = (TextView) findViewById(R.id.title_text);
        rightTv.setText(getString(R.string.create_group));
        groupAdapter = new GroupAdapter(this);
        // 滑动优化使用
        groupAdapter.setData(groupList);
        listView.setAdapter(groupAdapter);
        //setRightImg(R.drawable.nav_search, this);
        titleTv.setText(getString(R.string.constant_group));
        loadGroups();

        rightTv.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                gotoCreateGroupActivity();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                gotoGroupChatActivity(position);
            }
        });
    }

    private void gotoCreateGroupActivity()
    {
        Intent intent = new Intent(IntentConstant.GROUP_CREATE_ACTIVITY_ACTION);
        ActivityUtil.startActivity(this, intent);
    }

    private void gotoGroupChatActivity(int position)
    {
        ConstGroup constGroup = groupList.get(position);
        Intent intent = new Intent(IntentConstant.IM_CHAT_ACTIVITY_ACTION);
        intent.putExtra(UIConstants.CHAT_TYPE, constGroup);
        ActivityUtil.startActivity(this, intent);
    }

    @Override
    protected void onResume()
    {
        loadGroups();
        super.onResume();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (data == null)
        {
            return;
        }
        setResult(resultCode, data);
        ActivityStack.getIns().popup(this);
    }

    private void onGroupClicked(ConstGroup group)
    {
        if (!ContactLogic.getIns().getAbility().isIMAbility())
        {
            //ChatUtil.gotoChatSetting(this, group.getGroupId(), RecentChatContact.GROUPCHATTER);
            return;
        }

        /*if(!selectGroupFlag)
        {
            //讨论组
            ChatUtil.gotoGroupChat(this, group.getGroupId(), group.getUIName());
        }
        else
        {
            Intent intent = new Intent();
            intent.putExtra(IntentData.GROUP_ENTITY, group);
            setResult(Activity.RESULT_OK, intent);

            ActivityStack.getIns().popup(this);
        }*/
    }

    private void loadGroups()
    {
        //只显示固定群和固化讨论组
        List<ConstGroup> groups = ConstGroupManager.ins().getShowGroups();

        if (!groups.isEmpty())
        {
            groupAdapter.setData(groupList);
        }
    }

    public void onClick(View v)
    {
        if(DeviceManager.isFastClick())
        {
            return;
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        LocBroadcast.getInstance().unRegisterBroadcast(this, mBroadcastNames);
    }

    @Override
    public void onReceive(String broadcastName, Object obj)
    {
        if (CustomBroadcastConstants.ACTION_REFRESH_GROUP_MEMBER.equals(broadcastName))
        {
            groupList = ImMgr.getInstance().getGroups();
            refreshGroupList();
        }
    }

    private void refreshGroupList(){
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                groupAdapter.setData(groupList);
                groupAdapter.notifyDataSetChanged();
            }
        });
    }
}
