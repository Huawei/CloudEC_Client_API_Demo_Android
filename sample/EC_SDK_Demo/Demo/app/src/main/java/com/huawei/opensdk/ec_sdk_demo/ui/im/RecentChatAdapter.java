package com.huawei.opensdk.ec_sdk_demo.ui.im;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huawei.data.ConstGroup;
import com.huawei.data.entity.InstantMessage;
import com.huawei.data.entity.RecentChatContact;
import com.huawei.data.unifiedmessage.MediaResource;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.module.headphoto.ContactHeadFetcher;
import com.huawei.opensdk.ec_sdk_demo.module.headphoto.GroupHeadFetcher;
import com.huawei.opensdk.ec_sdk_demo.util.DateUtil;
import com.huawei.opensdk.imservice.ImMgr;
import com.huawei.opensdk.imservice.UnreadMessageService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is about recent chat adapter.
 */
public class RecentChatAdapter extends BaseAdapter
{
    private final LayoutInflater mInflater;
    private final Context mContext;
    private ContactHeadFetcher contactHeadFetcher;
    private GroupHeadFetcher groupHeadFetcher;
    private List<RecentChatContact> mRecentList = new ArrayList<>();

    public void setData(List<RecentChatContact> list)
    {

        this.mRecentList = list;
    }

    public RecentChatAdapter(Context context)
    {
        this.mContext = context;
        contactHeadFetcher = new ContactHeadFetcher(context);
        groupHeadFetcher = new GroupHeadFetcher(context);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount()
    {
        return mRecentList.size();
    }

    @Override
    public RecentChatContact getItem(int position)
    {
        return mRecentList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        RecentChatViewHolder chatViewHolder;
        if (convertView == null)
        {
            convertView = mInflater.inflate(R.layout.recent_item, null);
            chatViewHolder = new RecentChatViewHolder(convertView);
            convertView.setTag(chatViewHolder);
        }
        else
        {
            chatViewHolder = (RecentChatViewHolder) convertView.getTag();
        }
        loadRecentItemData(chatViewHolder, position);
        return convertView;
    }

    private void loadRecentItemData(RecentChatViewHolder chatViewHolder, int position)
    {
        RecentChatContact chatContact = mRecentList.get(position);
        InstantMessage instantMessage = ImMgr.getInstance().getRecentLastIm(chatContact.getContactAccount(), chatContact.getType());
        //p2p聊天 single
        if (chatContact.getType() == RecentChatContact.ESPACECHATTER)
        {
            String account = chatContact.getContactAccount();
            chatViewHolder.nameTv.setText(account);
            contactHeadFetcher.loadHead(chatContact.getContactAccount(), chatViewHolder.logoIv, true);
            SimpleDateFormat dateFormat = new SimpleDateFormat();
            dateFormat.applyPattern(DateUtil.FMT_YMDHM);
            String time = dateFormat.format(chatContact.getEndTime());
            chatViewHolder.dateTv.setText(time);
        }
        //讨论组聊天 discussion,群组聊天 group
        else if (chatContact.getType() == RecentChatContact.DISCUSSIONCHATTER || chatContact.getType() == RecentChatContact.GROUPCHATTER)
        {
            ConstGroup constGroup = ImMgr.getInstance().getConstGroupById(chatContact.getContactAccount());
            if (constGroup != null)
            {
                groupHeadFetcher.loadHead(constGroup, chatViewHolder.logoIv);

                chatViewHolder.nameTv.setText(constGroup.getName());
            }
            else
            {
                chatViewHolder.nameTv.setText("Not in the Group");
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat();
            dateFormat.applyPattern(DateUtil.FMT_YMDHM);
            String time = dateFormat.format(chatContact.getEndTime());
            chatViewHolder.dateTv.setText(time);
        }
        if (instantMessage != null)
        {
            if (instantMessage.getMediaRes() == null)
            {
                chatViewHolder.infoTv.setText(instantMessage.getContent());
            }
            else
            {
                MediaResource mediaRes = instantMessage.getMediaRes();
                if (mediaRes.getMediaType() == MediaResource.MEDIA_PICTURE)
                {
                    chatViewHolder.infoTv.setText(mContext.getString(R.string.media_picture));
                }
                else if (mediaRes.getMediaType() == MediaResource.MEDIA_AUDIO)
                {
                    chatViewHolder.infoTv.setText(R.string.media_audio);
                }
                else if (mediaRes.getMediaType() == MediaResource.MEDIA_VIDEO)
                {
                    chatViewHolder.infoTv.setText(R.string.media_video);
                }
                else if (mediaRes.getMediaType() == MediaResource.MEDIA_FILE)
                {
                    chatViewHolder.infoTv.setText(R.string.media_file);
                }
            }
        }

        int count = UnreadMessageService.getInstance().getUnreadMessageCountByAccount(chatContact.getContactAccount());
        if (count == 0)
        {
            chatViewHolder.countTv.setVisibility(View.GONE);
        }
        else
        {
            chatViewHolder.countTv.setVisibility(View.VISIBLE);
        }
    }

    static class RecentChatViewHolder
    {
        private final View container;
        private ImageView logoIv;
        private TextView dateTv;
        private TextView nameTv;
        private ImageView notifyIv;
        private TextView countTv;
        private TextView infoTv;

        public RecentChatViewHolder(View view)
        {
            container = view;
            logoIv = (ImageView) container.findViewById(R.id.recent_logo);
            dateTv = (TextView) container.findViewById(R.id.recent_date);
            nameTv = (TextView) container.findViewById(R.id.recent_name);
//            notifyIv = (ImageView) container.findViewById(R.id.recent_notify);
            countTv = (TextView) container.findViewById(R.id.recent_count);
            infoTv = (TextView) container.findViewById(R.id.recent_info);
        }
    }
}
