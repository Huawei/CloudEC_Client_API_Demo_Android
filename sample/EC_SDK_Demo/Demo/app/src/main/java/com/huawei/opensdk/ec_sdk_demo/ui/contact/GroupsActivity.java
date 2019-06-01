package com.huawei.opensdk.ec_sdk_demo.ui.contact;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcastReceiver;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.adapter.GroupAdapter;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.ui.IntentConstant;
import com.huawei.opensdk.ec_sdk_demo.ui.base.BaseActivity;
import com.huawei.opensdk.ec_sdk_demo.util.ActivityUtil;
import com.huawei.opensdk.imservice.ImChatGroupInfo;
import com.huawei.opensdk.imservice.ImMgr;

import java.util.List;

/**
 * This class is about group list activity.
 * 群组列表
 */
public class GroupsActivity extends BaseActivity implements View.OnClickListener, LocBroadcastReceiver
{

    private ListView listView;
    private GroupAdapter groupAdapter;
    private TextView rightTv;
    private TextView titleTv;
    private EditText searchEt;

    private List<ImChatGroupInfo> groupList;
    private String checkedId;
    private String[] mBroadcastNames = new String[]{CustomBroadcastConstants.ACTION_REFRESH_GROUP_MEMBER,
            CustomBroadcastConstants.ACTION_IM_CHAT_GROUP_ADD,
            CustomBroadcastConstants.ACTION_IM_CHAT_GROUP_UPDATE};


    @Override
    public void initializeData()
    {
        LocBroadcast.getInstance().registerBroadcast(this, mBroadcastNames);
        groupList = ImMgr.getInstance().getAllChatGroupList();
    }

    @Override
    public void initializeComposition()
    {
        setContentView(R.layout.groups);

        listView = (ListView) findViewById(R.id.group_list);
        rightTv = (TextView) findViewById(R.id.right_text);
        titleTv = (TextView) findViewById(R.id.title_text);
        searchEt = (EditText) findViewById(R.id.et_search);
        rightTv.setText(getString(R.string.create_group));
        searchEt.setHint(R.string.search_group);
        searchEt.setImeOptions(EditorInfo.IME_ACTION_SEARCH); // 软键盘显示放大镜图片，搜索
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
                gotoDetailActivity(position);
            }
        });

        searchEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                groupList = ImMgr.getInstance().queryChatGroup(searchEt.getText().toString());
                if (null != groupList || groupList.size() > 0)
                {
                    groupAdapter.setData(groupList);
                    groupAdapter.notifyDataSetChanged();
                }
                else
                {
                    showToast(R.string.search_group_failed);
                }
                return false;
            }
        });
    }

    private void gotoCreateGroupActivity()
    {
        Intent intent = new Intent(IntentConstant.GROUP_CREATE_ACTIVITY_ACTION);
        ActivityUtil.startActivityForResult(this, intent, UIConstants.IM_REQUEST_CODE_CHAT_GROUP_CREATE);
    }

    private void gotoDetailActivity(int position)
    {
        checkedId = groupList.get(position).getGroupId();
        ImChatGroupInfo groupInfo = ImMgr.getInstance().getChatGroupInfo(checkedId);
        if (null == groupInfo)
        {
            showToast(R.string.group_info_loading_failed);
            return;
        }
        Intent intent = new Intent(IntentConstant.GROUP_DETAIL_SETTING_ACTIVITY_ACTION);
        intent.putExtra(UIConstants.IM_CHAT_GROUP_INFO, groupInfo);
        ActivityUtil.startActivityForResult(this, intent, UIConstants.IM_REQUEST_CODE_CHAT_GROUP_DELETE);
    }

    @Override
    protected void onResume()
    {
        loadGroups();
        super.onResume();
    }


//    private void onGroupClicked(ConstGroup group)
//    {
//        if (!ContactLogic.getIns().getAbility().isIMAbility())
//        {
//            //ChatUtil.gotoChatSetting(this, group.getGroupId(), RecentChatContact.GROUPCHATTER);
//            return;
//        }
//
//        /*if(!selectGroupFlag)
//        {
//            //讨论组
//            ChatUtil.gotoGroupChat(this, group.getGroupId(), group.getUIName());
//        }
//        else
//        {
//            Intent intent = new Intent();
//            intent.putExtra(IntentData.GROUP_ENTITY, group);
//            setResult(Activity.RESULT_OK, intent);
//
//            ActivityStack.getIns().popup(this);
//        }*/
//    }

    private void loadGroups()
    {
        //只显示固定群和固化讨论组
//        List<ConstGroup> groups = ConstGroupManager.ins().getShowGroups();

//        if (!groups.isEmpty())
//        {
//            groupAdapter.setData(groupList);
//        }
    }

    public void onClick(View v)
    {
//        if(DeviceManager.isFastClick())
//        {
//            return;
//        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        LocBroadcast.getInstance().unRegisterBroadcast(this, mBroadcastNames);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode)
        {
            case UIConstants.IM_REQUEST_CODE_CHAT_GROUP_CREATE:
            case UIConstants.IM_REQUEST_CODE_CHAT_GROUP_DELETE:
                if (resultCode == UIConstants.IM_RESULT_CODE_CHAT_GROUP_CREATE ||
                        resultCode == UIConstants.IM_RESULT_CODE_CHAT_GROUP_DELETE)
                {
                    refreshGroupList();
                }
                break;
                default:
                    break;
        }
    }

    @Override
    public void onReceive(String broadcastName, Object obj)
    {
        switch (broadcastName)
        {
            case CustomBroadcastConstants.ACTION_REFRESH_GROUP_MEMBER:
                // todo
                break;
            case CustomBroadcastConstants.ACTION_IM_CHAT_GROUP_UPDATE:
                // 有人邀请加入聊天群组收到的回调通知
            case CustomBroadcastConstants.ACTION_IM_CHAT_GROUP_ADD:
                refreshGroupList();
                break;
                default:
                    break;
        }
    }

    private void refreshGroupList(){
        groupList = ImMgr.getInstance().getAllChatGroupList();
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
