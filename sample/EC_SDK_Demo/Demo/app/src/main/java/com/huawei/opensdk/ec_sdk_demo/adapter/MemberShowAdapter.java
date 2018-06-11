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
import com.huawei.data.ConstGroupContact;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.module.headphoto.ContactHeadFetcher;

import java.util.List;

/**
 * This adapter is about select group member list.
 * 查询群组成员适配层
 */
public class MemberShowAdapter extends BaseAdapter
{
    private final ContactHeadFetcher headFetcher;
    private final String ownerNumber;
    private LayoutInflater inflater;

    private List<ConstGroupContact> mContacts;

    public MemberShowAdapter(Context context, String ownerNumber)
    {
        inflater = LayoutInflater.from(context);
        headFetcher = new ContactHeadFetcher(context);
        this.ownerNumber = ownerNumber;
    }

    public void setMemberList(List<ConstGroupContact> members)
    {
        this.mContacts = members;
    }

    @Override
    public int getCount()
    {
        return mContacts.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mContacts.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        GroupMemberViewHolder viewHolder;
        if (null == convertView)
        {
            viewHolder = new GroupMemberViewHolder();
            convertView = inflater.inflate(R.layout.group_member_item, null);
            viewHolder.contactHead = (ImageView) convertView.findViewById(R.id.contact_head);
            viewHolder.contactName = (TextView) convertView.findViewById(R.id.contact_name);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (GroupMemberViewHolder) convertView.getTag();
        }
        PersonalContact contact = mContacts.get(position);
        viewHolder.contactName.setText(ContactTools.getDisplayName(contact));
        headFetcher.loadHead(contact, viewHolder.contactHead, false);
        viewHolder.contactHead.setScaleType(ImageView.ScaleType.FIT_XY);
        return convertView;
    }

    private static class GroupMemberViewHolder
    {
        public ImageView contactHead;
        public TextView contactName;
    }
}
