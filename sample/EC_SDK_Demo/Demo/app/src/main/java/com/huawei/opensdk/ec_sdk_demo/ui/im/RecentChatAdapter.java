package com.huawei.opensdk.ec_sdk_demo.ui.im;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.module.headphoto.ContactHeadFetcher;
import com.huawei.opensdk.ec_sdk_demo.module.headphoto.GroupHeadFetcher;
import com.huawei.opensdk.ec_sdk_demo.util.DateUtil;
import com.huawei.opensdk.imservice.ImChatMsgInfo;
import com.huawei.opensdk.imservice.ImRecentChatInfo;

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
    private List<ImRecentChatInfo> mRecentList = new ArrayList<>();

    public void setData(List<ImRecentChatInfo> list)
    {
        this.mRecentList = list;
    }

    public RecentChatAdapter(Context context)
    {
        this.mContext = context;
//        contactHeadFetcher = new ContactHeadFetcher(context);
//        groupHeadFetcher = new GroupHeadFetcher(context);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount()
    {
        return mRecentList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

//    @Override
//    public RecentChatContact getItem(int position)
//    {
//        return mRecentList.get(position);
//    }

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
        ImRecentChatInfo chatContact = mRecentList.get(position);
        ImChatMsgInfo lastMsg = chatContact.getLastChatMsg();

        if (null != lastMsg)
        {
//            if (instantMessage.getMediaRes() == null)
//            {
            String account = chatContact.getChatName();
            chatViewHolder.nameTv.setText(account);
//            contactHeadFetcher.loadHead(chatContact.getContactAccount(), chatViewHolder.logoIv, true);
            SimpleDateFormat dateFormat = new SimpleDateFormat();
            dateFormat.applyPattern(DateUtil.FMT_YMDHM);
            String time = dateFormat.format(lastMsg.getUtcStamp());
            chatViewHolder.dateTv.setText(time);
//            }
//            else
//            {
//                MediaResource mediaRes = instantMessage.getMediaRes();
//                if (mediaRes.getMediaType() == MediaResource.MEDIA_PICTURE)
//                {
//                    chatViewHolder.infoTv.setText(mContext.getString(R.string.media_picture));
//                }
//                else if (mediaRes.getMediaType() == MediaResource.MEDIA_AUDIO)
//                {
//                    chatViewHolder.infoTv.setText(R.string.media_audio);
//                }
//                else if (mediaRes.getMediaType() == MediaResource.MEDIA_VIDEO)
//                {
//                    chatViewHolder.infoTv.setText(R.string.media_video);
//                }
//                else if (mediaRes.getMediaType() == MediaResource.MEDIA_FILE)
//                {
//                    chatViewHolder.infoTv.setText(R.string.media_file);
//                }
//            }
        }

        //p2p聊天 single
        if (!chatContact.isGroupChat())
        {
            chatViewHolder.logoIv.setImageResource(R.drawable.default_head);
            chatViewHolder.infoTv.setText(lastMsg.getContent());
        }
        //群组聊天 group
        else
        {
            chatViewHolder.logoIv.setImageResource(R.drawable.group_head);
            chatViewHolder.infoTv.setText(lastMsg.getFromName() + ":" + lastMsg.getContent());
//            ConstGroup constGroup = ImMgr.getInstance().getConstGroupById(chatContact.getContactAccount());
//            if (constGroup != null)
//            {
//                groupHeadFetcher.loadHead(constGroup, chatViewHolder.logoIv);
//
//                chatViewHolder.nameTv.setText(constGroup.getName());
//            }
//            else
//            {
//                chatViewHolder.nameTv.setText("Not in the Group");
//            }
//
//            SimpleDateFormat dateFormat = new SimpleDateFormat();
//            dateFormat.applyPattern(DateUtil.FMT_YMDHM);
//            String time = dateFormat.format(chatContact.getEndTime());
//            chatViewHolder.dateTv.setText(time);
        }

        int count = chatContact.getUnReadMsgCount();
        if (count == 0)
        {
            chatViewHolder.countTv.setVisibility(View.GONE);
        }
        else if (count > 99)
        {
            chatViewHolder.countTv.setVisibility(View.VISIBLE);
            chatViewHolder.countTv.setActivated(true);
        }
        else
        {
            chatViewHolder.countTv.setVisibility(View.VISIBLE);
            chatViewHolder.countTv.setText(count + "");
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
