package com.huawei.opensdk.ec_sdk_demo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huawei.ecterminalsdk.base.TsdkChatGroupInfo;
import com.huawei.ecterminalsdk.base.TsdkChatGroupType;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.module.headphoto.GroupHeadFetcher;
import com.huawei.opensdk.imservice.ImChatGroupInfo;

import java.util.ArrayList;
import java.util.List;


/**
 * This adapter is about group.
 * 群组适配层
 */
public class GroupAdapter extends BaseAdapter
{
    private Context context;
    private List<ImChatGroupInfo> groupList = new ArrayList<>();
    private ImChatGroupInfo chatGroupInfo;
    private GroupHeadFetcher headFetcher;

    public GroupAdapter(Context context)
    {
        this.context = context;
        headFetcher = new GroupHeadFetcher(context);
    }

    @Override
    public int getCount()
    {
        return groupList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return groupList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        chatGroupInfo = groupList.get(position);
        ViewHolder viewHolder;
        if (convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.group_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.headIv = (ImageView) convertView.findViewById(R.id.group_head);
            viewHolder.nameTv = (TextView) convertView.findViewById(R.id.group_name);
            viewHolder.lockIv = (ImageView) convertView.findViewById(R.id.group_type_img);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.nameTv.setText(chatGroupInfo.getGroupName());
        // TODO: 2019/1/22 此处头像暂为默认，后续修改
        viewHolder.headIv.setImageResource(R.drawable.group_head);
//        headFetcher.loadHead(constGroup, viewHolder.headIv);
        viewHolder.lockIv.setVisibility(TsdkChatGroupType.TSDK_E_CHAT_GROUP_FIXED_GROUP.getIndex() == chatGroupInfo.getGroupType() ? View.VISIBLE : View.GONE);
        return convertView;
    }

    public void setData(List<ImChatGroupInfo> groupList)
    {
        this.groupList = groupList;
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
        public ImageView lockIv;
    }
}
