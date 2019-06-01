package com.huawei.opensdk.ec_sdk_demo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.module.headphoto.ContactHeadFetcher;
import com.huawei.opensdk.imservice.ImContactInfo;

import java.util.LinkedList;

/**
 * This adapter is about add group members.
 * 添加群组成员列表适配层
 */
public class AddGroupMemberListAdapter extends BaseAdapter
{
    private LayoutInflater inflater;

    private ContactHeadFetcher headFetcher;
    private LinkedList<ImContactInfo> mMemberList = new LinkedList<>();

    public AddGroupMemberListAdapter(Context context)
    {
        inflater = LayoutInflater.from(context);
        headFetcher = new ContactHeadFetcher(context);
    }

    @Override
    public int getCount()
    {
        return mMemberList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mMemberList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        MemberViewHolder viewHolder;
        if (null == convertView)
        {
            viewHolder = new MemberViewHolder();
            convertView = inflater.inflate(R.layout.group_member_item, null);
            viewHolder.contactHead = (ImageView) convertView.findViewById(R.id.contact_head);
            viewHolder.contactName = (TextView) convertView.findViewById(R.id.contact_name);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (MemberViewHolder) convertView.getTag();
        }
        ImContactInfo contact = mMemberList.get(position);
        viewHolder.contactName.setText(contact.getAccount());
//        headFetcher.loadHead(contact, viewHolder.contactHead, false);
        // TODO: 2019/1/26 此处写为默认头像，后面头像整体优化时处理
        viewHolder.contactHead.setImageResource(R.drawable.default_head);
        viewHolder.contactHead.setScaleType(ImageView.ScaleType.FIT_XY);
        return convertView;
    }

    public void setData(LinkedList<ImContactInfo> members)
    {
        this.mMemberList = members;
    }

    /**
	 * View-holding classes improve processing performance
     * to reduce the overhead of view memory consumption
     * 视图持有类，为减少视图占用内存的开销，提高处理性能
     */
    private static class MemberViewHolder
    {
        public ImageView contactHead;
        public TextView contactName;
    }
}
