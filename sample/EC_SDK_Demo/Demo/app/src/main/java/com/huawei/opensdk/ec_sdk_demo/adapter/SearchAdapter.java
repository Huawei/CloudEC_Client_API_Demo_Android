package com.huawei.opensdk.ec_sdk_demo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.huawei.contacts.PersonalContact;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.module.headphoto.ContactHeadFetcher;

import java.util.ArrayList;
import java.util.List;

/**
 * The type search contact adapter.
 * 查询联系人列表适配层
 */
public class SearchAdapter extends BaseAdapter
{
    private Context context;
    private ContactHeadFetcher headFetcher;
    private List<PersonalContact> contactList = new ArrayList<>();

    public SearchAdapter(Context context)
    {
        this.context = context;
        headFetcher = new ContactHeadFetcher(context);
    }

    public void setContactList(List<PersonalContact> contactList)
    {
        this.contactList = contactList;
    }

    @Override
    public int getCount()
    {
        return contactList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return contactList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        PersonalContact contact = contactList.get(position);
        ViewHolder viewHolder;
        if (convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.contact_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.headIv = (ImageView) convertView.findViewById(R.id.head_iv);
            viewHolder.nameTv = (TextView) convertView.findViewById(R.id.name_tv);
            viewHolder.infoTv = (TextView) convertView.findViewById(R.id.info_tv);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.nameTv.setText(contact.getName());
        viewHolder.infoTv.setText(contact.getDepartment());
        headFetcher.loadHead(contact, viewHolder.headIv, true);
        return convertView;
    }

    /**
	 * View-holding classes improve processing performance
     * to reduce the overhead of view memory consumption
     * 视图持有类，为减少视图占用内存的开销，提高处理性能
     */
    public static class ViewHolder
    {
        public ImageView headIv;
        public TextView nameTv;
        public TextView infoTv;
    }
}
