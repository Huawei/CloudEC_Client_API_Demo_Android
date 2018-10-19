package com.huawei.opensdk.ec_sdk_demo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huawei.data.PersonalTeam;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.ui.contact.ContactFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * This adapter is about user teams list.
 * 显示用户列表适配层
 */
public class TeamAdapter extends BaseAdapter {

    private Context context;
    private List<PersonalTeam> teamsList = new ArrayList<>();
    private PersonalTeam personalTeam;

    public TeamAdapter(Context context) {
        this.context = context;
    }

    public void setDate(List<PersonalTeam> date) {
        this.teamsList = date;
    }

    /**
	 * View-holding classes improve processing performance
     * to reduce the overhead of view memory consumption
     * 视图持有类，为减少视图占用内存的开销，提高处理性能
     */
    public static class ViewHolder
    {
        private TextView tvName;
        private TextView tvCount;
        private ImageView ivCheck;
    }

    @Override
    public int getCount() {
        return teamsList.size();
    }

    @Override
    public Object getItem(int i) {
        return teamsList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null)
        {
            view = LayoutInflater.from(context).inflate(R.layout.team_item, null);
            viewHolder = new ViewHolder();
            viewHolder.tvName = (TextView) view.findViewById(R.id.team_name);
            viewHolder.tvCount = (TextView) view.findViewById(R.id.members_count);
            viewHolder.ivCheck = (ImageView) view.findViewById(R.id.team_type_img);
            view.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) view.getTag();
        }

        personalTeam = teamsList.get(i);
        viewHolder.tvName.setText(personalTeam.getTeamName());
        viewHolder.tvCount.setText("(" + personalTeam.getContactList().size() + ")");
        if (ContactFragment.getCheckedTeamName().equals(personalTeam.getTeamName()))
        {
            viewHolder.ivCheck.setVisibility(View.VISIBLE);
        }
        else
        {
            viewHolder.ivCheck.setVisibility(View.GONE);
        }
        return view;
    }

}
