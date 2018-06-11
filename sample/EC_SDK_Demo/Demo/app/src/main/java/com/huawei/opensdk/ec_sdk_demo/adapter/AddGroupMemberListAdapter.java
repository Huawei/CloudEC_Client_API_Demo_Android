package com.huawei.opensdk.ec_sdk_demo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huawei.contacts.ContactTools;
import com.huawei.contacts.PersonalContact;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.module.headphoto.ContactHeadFetcher;

import java.util.ArrayList;
import java.util.List;

/**
 * This adapter is about add group members.
 * 添加群组成员列表适配层
 */
public class AddGroupMemberListAdapter extends BaseAdapter
{
    private LayoutInflater inflater;

    private ContactHeadFetcher headFetcher;
    private List<PersonalContact> mMemberList = new ArrayList<>();

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
        PersonalContact contact = mMemberList.get(position);
        viewHolder.contactName.setText(ContactTools.getDisplayName(contact));
        headFetcher.loadHead(contact, viewHolder.contactHead, false);
        viewHolder.contactHead.setScaleType(ImageView.ScaleType.FIT_XY);
        return convertView;
    }

    public void setMemberList(List<PersonalContact> members)
    {
        this.mMemberList = members;
    }

    private static class MemberViewHolder
    {
        public ImageView contactHead;
        public TextView contactName;
    }
}
