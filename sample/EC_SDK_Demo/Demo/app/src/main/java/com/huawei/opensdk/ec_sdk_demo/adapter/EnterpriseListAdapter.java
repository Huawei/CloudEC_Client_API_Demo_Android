package com.huawei.opensdk.ec_sdk_demo.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huawei.opensdk.contactservice.eaddr.EntAddressBookInfo;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.ui.eaddrbook.EnterpriseAddrTools;

import java.util.List;

/**
 * The type enterprise adapter.
 * 企业通讯录适配层
 */
public class EnterpriseListAdapter extends BaseAdapter {

    private Context context;
    private List<EntAddressBookInfo> data;
    private EntAddressBookInfo entAddressBookInfo = new EntAddressBookInfo();

    public EnterpriseListAdapter(Context context, List<EntAddressBookInfo> data) {
        this.context = context;
        this.data = data;
    }

    /**
     * Refresh the date(Contacts list).
     * @param adapterData the contacts list
     */
    public void notifyDataSetChanged(List<EntAddressBookInfo> adapterData)
    {
        this.data = adapterData;
        notifyDataSetChanged();
    }

	/**
	 * View-holding classes improve processing performance
     * to reduce the overhead of view memory consumption
     * 视图持有类，为减少视图占用内存的开销，提高处理性能
     */
    static class ContactViewHolder
    {
        private ImageView ivHead;
        private TextView tvName;
        private TextView tvDept;
    }

    @Override
    public int getCount() {
        if(data.size() != 0)
        {
            return data.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ContactViewHolder viewHolder;
        if(convertView == null)
        {
            viewHolder = new ContactViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.activity_enterprise_list,parent, false);
            viewHolder.ivHead = (ImageView) convertView.findViewById(R.id.head);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.user_name);
            viewHolder.tvDept = (TextView) convertView.findViewById(R.id.dept);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ContactViewHolder) convertView.getTag();
        }
        entAddressBookInfo = data.get(position);
        if (!entAddressBookInfo.getHeadIconPath().isEmpty())
        {
            Bitmap headIcon = EnterpriseAddrTools.getBitmapByPath(entAddressBookInfo.getHeadIconPath());
            viewHolder.ivHead.setImageBitmap(headIcon);
        }
        else if (entAddressBookInfo.getSysIconID() == 10)
        {
            viewHolder.ivHead.setImageResource(R.drawable.default_head);
        }
        else
        {
            viewHolder.ivHead.setImageResource(entAddressBookInfo.getSysIconID());
        }
        viewHolder.tvName.setText(entAddressBookInfo.getEaddrName());
        viewHolder.tvDept.setText(entAddressBookInfo.getEaddrDept());
        return convertView;
    }
}
