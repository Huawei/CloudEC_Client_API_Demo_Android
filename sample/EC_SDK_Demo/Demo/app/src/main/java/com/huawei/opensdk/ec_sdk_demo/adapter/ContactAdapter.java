package com.huawei.opensdk.ec_sdk_demo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huawei.contacts.PersonalContact;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.module.headphoto.ContactHeadFetcher;

import java.util.ArrayList;
import java.util.List;

/**
 * The type contact info adapter.
 */
public class ContactAdapter extends BaseAdapter
{
    private Context context;
    private ContactHeadFetcher headFetcher;
    private List<PersonalContact> contactList = new ArrayList<>();

    public ContactAdapter(Context context)
    {
        this.context = context;
        headFetcher = new ContactHeadFetcher(context);
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
        PersonalContact personalContact = contactList.get(position);
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
        viewHolder.nameTv.setText(personalContact.getName());
        viewHolder.infoTv.setText(personalContact.getSignature());
        headFetcher.loadHead(personalContact, viewHolder.headIv, true);
        return convertView;
    }

    public void setData(List<PersonalContact> contactList)
    {
        this.contactList = contactList;
    }

    public static class ViewHolder
    {
        public ImageView headIv;
        public TextView nameTv;
        public TextView infoTv;
    }
}
