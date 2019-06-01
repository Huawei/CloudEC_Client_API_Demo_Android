package com.huawei.opensdk.ec_sdk_demo.ui.contact;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.adapter.TeamAdapter;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.ui.base.BaseActivity;
import com.huawei.opensdk.ec_sdk_demo.util.CommonUtil;
import com.huawei.opensdk.ec_sdk_demo.widget.EditDialog;
import com.huawei.opensdk.ec_sdk_demo.widget.SimpleListDialog;
import com.huawei.opensdk.imservice.ImConstant;
import com.huawei.opensdk.imservice.ImContactGroupInfo;
import com.huawei.opensdk.imservice.ImMgr;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is about contacts teams list activity.
 */
public class TeamsActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private TextView tvTitle;
    private TextView tvRightTitle;
    private ListView listView;
    private TeamAdapter teamAdapter;

    private List<ImContactGroupInfo> contactGroupInfoList;
    private List<Object> items = new ArrayList<>();
    private long checkGroupId = -1;

    @Override
    public void initializeComposition() {
        setContentView(R.layout.teams);
        tvTitle = (TextView) findViewById(R.id.title_text);
        tvRightTitle = (TextView) findViewById(R.id.right_text);
        listView = (ListView) findViewById(R.id.team_list);

        tvTitle.setText(getString(R.string.team_check));
        tvRightTitle.setText(getString(R.string.new_team));

        refreshContactGroup();
        tvRightTitle.setOnClickListener(this);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (checkGroupId == contactGroupInfoList.get(position).getGroupId())
                {
                    return false;
                }
                showItemClickDialog(position);
                return true;
            }
        });
    }

    @Override
    public void initializeData() {
        teamAdapter = new TeamAdapter(this);
        Intent intent = getIntent();
        checkGroupId = intent.getLongExtra(UIConstants.IM_CHECK_CONTACT_GROUP_NAME, -1);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        Intent intent = new Intent();
        intent.putExtra(UIConstants.IM_RETURN_CONTACT_GROUP_ID, contactGroupInfoList.get(i).getGroupId());
        setResult(UIConstants.IM_RESULT_CODE_CONTACT_GROUP, intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.right_text:
                showTeamNameDialog(0, R.string.new_team);
                break;
                default:
                    break;
        }
    }

    private void refreshContactGroup()
    {
        contactGroupInfoList = ImMgr.getInstance().getAllContactGroupList();
        if (null == contactGroupInfoList || 0 == contactGroupInfoList.size())
        {
            return;
        }
        for (ImContactGroupInfo contactGroupInfo : contactGroupInfoList)
        {
            if (checkGroupId == contactGroupInfo.getGroupId())
            {
                teamAdapter.setCheckGroupId(checkGroupId);
            }
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                teamAdapter.setDate(contactGroupInfoList);
                listView.setAdapter(teamAdapter);
            }
        });
    }

    private void showTeamNameDialog(final long teamId, final int id)
    {
        final EditDialog dialog = new EditDialog(this, id);
        dialog.setRightButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtil.hideSoftInput(TeamsActivity.this);
                if (TextUtils.isEmpty(dialog.getText()))
                {
                    showToast(R.string.invalid_number);
                    return;
                }
                if (id == R.string.new_team)
                {
                    manageContactGroup(ImConstant.ContactGroupOpType.ADD_CONTACT_GROUP, teamId, dialog.getText());
                }
                else
                {
                    manageContactGroup(ImConstant.ContactGroupOpType.MODIFY_CONTACT_GROUP, teamId, dialog.getText());
                }
            }
        });
        dialog.show();
    }

    private void showItemClickDialog(final int index)
    {
        items.clear();
        if (1 == contactGroupInfoList.size())
        {
            items.add(getString(R.string.rename_team));
            items.add(getString(R.string.delete_team));
        }
        else if (0 == index)
        {
            items.add(getString(R.string.move_team_down));
            items.add(getString(R.string.rename_team));
            items.add(getString(R.string.delete_team));
        }
        else if (index == contactGroupInfoList.size() - 1)
        {
            items.add(getString(R.string.move_team_up));
            items.add(getString(R.string.rename_team));
            items.add(getString(R.string.delete_team));
        }
        else
        {
            items.add(getString(R.string.move_team_down));
            items.add(getString(R.string.move_team_up));
            items.add(getString(R.string.rename_team));
            items.add(getString(R.string.delete_team));
        }
        final long chooseId = contactGroupInfoList.get(index).getGroupId();
        final SimpleListDialog listDialog = new SimpleListDialog(this, items);
        listDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listDialog.dismiss();
                if ((items.get(position)).equals(getString(R.string.delete_team)))
                {
                    manageContactGroup(ImConstant.ContactGroupOpType.DEL_CONTACT_GROUP, chooseId, null);
                }
                else if ((items.get(position)).equals(getString(R.string.rename_team)))
                {
                    showTeamNameDialog(chooseId, R.string.rename_team);
                }
                else if ((items.get(position)).equals(getString(R.string.move_team_up)))
                {
                    updateOrder(index, index - 1);
                }
                else
                {
                    updateOrder(index, index + 1);
                }
            }
        });
        listDialog.show();
    }

    private void manageContactGroup(int opType, long groupId, String groupName)
    {
        int opResult;
        switch (opType)
        {
            case ImConstant.ContactGroupOpType.ADD_CONTACT_GROUP:
                long newGroupId = ImMgr.getInstance().addContactGroup(groupName);
                if (-1 == newGroupId)
                {
                    showToast(R.string.new_team_failed);
                    return;
                }
                break;
            case ImConstant.ContactGroupOpType.DEL_CONTACT_GROUP:
                opResult = ImMgr.getInstance().delContactGroup(groupId);
                if (0 != opResult)
                {
                    showToast(R.string.del_team_failed);
                    return;
                }
                break;
            case ImConstant.ContactGroupOpType.MODIFY_CONTACT_GROUP:
                opResult = ImMgr.getInstance().modifyContactGroup(groupId, groupName);
                if (0 != opResult)
                {
                    showToast(R.string.mod_team_failed);
                    return;
                }
                break;
                default:
                    break;
        }
        refreshContactGroup();
    }

    private void updateOrder(int index1, int index2)
    {
        int result = ImMgr.getInstance().updateGroupOrder(contactGroupInfoList, index1, index2);
        if (0 != result)
        {
            showToast(R.string.update_team_order_failed);
            return;
        }
        refreshContactGroup();
    }
}
