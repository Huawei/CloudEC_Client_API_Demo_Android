package com.huawei.opensdk.ec_sdk_demo.ui.im;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.logic.im.mvp.RecentChatContract;
import com.huawei.opensdk.ec_sdk_demo.logic.im.mvp.RecentChatPresenter;
import com.huawei.opensdk.ec_sdk_demo.ui.IntentConstant;
import com.huawei.opensdk.ec_sdk_demo.ui.base.AbsFragment;
import com.huawei.opensdk.ec_sdk_demo.util.ActivityUtil;
import com.huawei.opensdk.imservice.ImChatGroupInfo;
import com.huawei.opensdk.imservice.ImContactInfo;
import com.huawei.opensdk.imservice.ImMgr;
import com.huawei.opensdk.imservice.ImRecentChatInfo;

import java.util.List;

/**
 * This class is about the recent chat fragment.
 */
public class RecentFragment extends AbsFragment implements RecentChatContract.RecentChatView
{
    private static final int REFRESH_RECENT_LIST = 100;
    private ListView mRecentLv;

    private RecentChatAdapter mAdapter;
    private RecentChatPresenter mPresenter;

    private ImRecentChatInfo mCurrentRecentChat;

    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case REFRESH_RECENT_LIST:
                    if (msg.obj instanceof List)
                    {
                        List<ImRecentChatInfo> list = (List<ImRecentChatInfo>) msg.obj;
                        LogUtil.i(UIConstants.DEMO_TAG, "refresh recent contact list view, size = " + list.size());
//                        RecentChatHelper.sort(list);
                        mAdapter.setData(list);
                        mAdapter.notifyDataSetChanged();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public int getLayoutId()
    {
        return R.layout.recent_fragment;
    }

    @Override
    public void onViewCreated()
    {
        super.onViewCreated();
        mPresenter = new RecentChatPresenter(this);
        mPresenter.regRecentSessionReceiver();
        mRecentLv = (ListView) mView.findViewById(R.id.recent_list);
        mAdapter = new RecentChatAdapter(getActivity());
        final List<ImRecentChatInfo> list = mPresenter.loadRecentChats();
        if (null != list){
            mAdapter.setData(list);
        }
        mRecentLv.setAdapter(mAdapter);

        mRecentLv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                mCurrentRecentChat = mPresenter.getRecentChat().get(position);
                ImMgr.getInstance().setCurrentRecentChat(mCurrentRecentChat);
                skipToChatActivity();
            }
        });
    }

    @Override
    public void onResume()
    {
        mPresenter.loadRecentChats();
        super.onResume();
    }

    @Override
    public void refreshRecentChatList(List<ImRecentChatInfo> list)
    {
        Message msg = Message.obtain();
        msg.what = REFRESH_RECENT_LIST;
        msg.obj = list;
        mHandler.sendMessage(msg);
    }

    private void skipToChatActivity()
    {
        String tag = mCurrentRecentChat.getTag();
        ImMgr.getInstance().setCurrentChatId(tag);
        if (!mCurrentRecentChat.isGroupChat())
        {
            Intent intent = new Intent(IntentConstant.IM_CHAT_ACTIVITY_ACTION);
            ImContactInfo chatObject = new ImContactInfo();
            chatObject.setAccount(mCurrentRecentChat.getTag());
            chatObject.setName(mCurrentRecentChat.getChatName());
            intent.putExtra(UIConstants.CHAT_TYPE, chatObject);
            ActivityUtil.startActivity(getActivity(), intent);
        }
        else
        {
            ImChatGroupInfo group = ImMgr.getInstance().getChatGroupInfo(tag);
            Intent intent = new Intent(IntentConstant.IM_CHAT_ACTIVITY_ACTION);
            intent.putExtra(UIConstants.CHAT_TYPE, group);
            ActivityUtil.startActivity(getActivity(), intent);
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        mPresenter.unregRecentSessionReceiver();
        mPresenter.resetData();
    }
}
