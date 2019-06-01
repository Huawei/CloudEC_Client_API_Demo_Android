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

import java.util.ArrayList;
import java.util.List;

/**
 * The type contact info adapter.
 * 联系人信息适配层
 */
public class ContactAdapter extends BaseAdapter
{
    private Context context;
    private ContactHeadFetcher headFetcher;
    private List<ImContactInfo> contactList = new ArrayList<>();

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
        ImContactInfo contactInfo = contactList.get(position);
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
        viewHolder.nameTv.setText(contactInfo.getName());
        viewHolder.infoTv.setText(contactInfo.getSignature());
//        headFetcher.loadHead(personalContact, viewHolder.headIv, true);
        // TODO: 2019/1/21 此处头像暂为默认 后续修改
        if (1 == contactInfo.getContactType())
        {
            viewHolder.headIv.setImageResource(R.drawable.default_head);
        }
        else
        {
            viewHolder.headIv.setImageResource(R.drawable.default_head_local);
        }
        return convertView;
    }

    public void setData(List<ImContactInfo> contactList)
    {
        this.contactList = contactList;
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
