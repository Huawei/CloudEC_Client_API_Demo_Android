package com.huawei.opensdk.ec_sdk_demo.ui.contact;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.huawei.contacts.PersonalContact;
import com.huawei.data.PersonalTeam;
import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.adapter.TeamAdapter;
import com.huawei.opensdk.ec_sdk_demo.ui.base.BaseActivity;
import com.huawei.opensdk.imservice.ImMgr;

import java.util.List;

/**
 * This class is about contacts teams list activity.
 */
public class TeamsActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private TextView tvTitle;
    private ImageView ivIsChecked;
    private ListView listView;
    private TeamAdapter teamAdapter;
    private List<PersonalContact> personalContactList;
    private List<PersonalTeam> personalTeamList;
    private PersonalTeam personalTeam;
    private static int teamIndex;

    public static int getTeamIndex() {
        return teamIndex;
    }

    public static void setTeamIndex(int teamIndex) {
        TeamsActivity.teamIndex = teamIndex;
    }

    @Override
    public void initializeComposition() {
        setContentView(R.layout.teams);
        tvTitle = (TextView) findViewById(R.id.title_text);
        listView = (ListView) findViewById(R.id.team_list);

        tvTitle.setText(getString(R.string.team_check));

        View headView = getLayoutInflater().inflate(R.layout.team_item, null);
        TextView tvAllContacts = (TextView) headView.findViewById(R.id.team_name);
        TextView tvAllCount = (TextView) headView.findViewById(R.id.members_count);
        ivIsChecked = (ImageView) headView.findViewById(R.id.team_type_img);

        tvAllContacts.setText(getString(R.string.all_contacts));
        tvAllCount.setText("(" + personalContactList.size() + ")");
        ivIsChecked.setImageResource(R.drawable.search_member_team);
        if (tvAllContacts.getText().toString().equals(ContactFragment.getCheckedTeamName()))
        {
            ivIsChecked.setVisibility(View.VISIBLE);
        }
        else
        {
            ivIsChecked.setVisibility(View.GONE);
        }
        listView.addHeaderView(headView);

        teamAdapter = new TeamAdapter(this);
        teamAdapter.setDate(personalTeamList);
        listView.setAdapter(teamAdapter);

        listView.setOnItemClickListener(this);
    }

    @Override
    public void initializeData() {
        personalContactList = ImMgr.getInstance().getFriends();
        personalTeamList = ImMgr.getInstance().getTeams();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        if (0 == i)
        {
            LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_REFRESH_TEAM_MEMBER, -1);
            finish();
        }
        else
        {
            personalTeam = personalTeamList.get(i - 1);
            setTeamIndex(i - 1);
            LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_REFRESH_TEAM_MEMBER, personalTeam);
            finish();
        }
    }
}
