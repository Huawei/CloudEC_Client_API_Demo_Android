package com.huawei.opensdk.ec_sdk_demo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

//import com.huawei.contacts.ContactTools;
//import com.huawei.contacts.PersonalContact;
//import com.huawei.data.ConstGroupContact;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.module.headphoto.ContactHeadFetcher;
import com.huawei.opensdk.imservice.ImContactInfo;

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

    private List<ImContactInfo> mContacts;

    public MemberShowAdapter(Context context, String ownerNumber)
    {
        inflater = LayoutInflater.from(context);
        headFetcher = new ContactHeadFetcher(context);
        this.ownerNumber = ownerNumber;
    }

    public void setMemberList(List<ImContactInfo> members)
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
        ImContactInfo contact = mContacts.get(position);
        viewHolder.contactName.setText(contact.getAccount());
//        headFetcher.loadHead(contact, viewHolder.contactHead, false);
        // TODO: 2019/1/28 先使用默认头像
        viewHolder.contactHead.setImageResource(R.drawable.default_head);
        return convertView;
    }

    /**
	 * View-holding classes improve processing performance
     * to reduce the overhead of view memory consumption
     * 视图持有类，为减少视图占用内存的开销，提高处理性能
     */
    private static class GroupMemberViewHolder
    {
        public ImageView contactHead;
        public TextView contactName;
    }
}
