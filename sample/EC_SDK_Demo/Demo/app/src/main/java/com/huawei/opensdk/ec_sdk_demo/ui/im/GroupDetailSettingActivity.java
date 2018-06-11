package com.huawei.opensdk.ec_sdk_demo.ui.im;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.data.ConstGroup;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.logic.contact.mvp.GroupDetailSettingPresenter;
import com.huawei.opensdk.ec_sdk_demo.logic.contact.mvp.IGroupDetailSettingContract;
import com.huawei.opensdk.ec_sdk_demo.module.headphoto.GroupHeadFetcher;
import com.huawei.opensdk.ec_sdk_demo.ui.MainActivity;
import com.huawei.opensdk.ec_sdk_demo.ui.base.ActivityStack;
import com.huawei.opensdk.ec_sdk_demo.ui.base.MVPBaseActivity;
import com.huawei.opensdk.imservice.ImMgr;
import com.huawei.opensdk.loginmgr.LoginMgr;

/**
 * This class is about set group detail info activity.
 */
public class GroupDetailSettingActivity extends MVPBaseActivity<IGroupDetailSettingContract.IGroupDetailSettingView, GroupDetailSettingPresenter>
        implements IGroupDetailSettingContract.IGroupDetailSettingView, View.OnClickListener
{
    private ConstGroup constGroup;
    private GroupHeadFetcher groupHeadFetcher;

    private ImageView headIv;
    private TextView nameTv;
    private TextView groupNumberTv;
    private ImageView enterChatIv;
    private TextView groupNameTv;
    private TextView bulletinTv;
    private TextView descriptionTv;
    private LinearLayout totalMemberRl;
    private ImageView lockIv;
    private LinearLayout clearHistoryLl;
    private LinearLayout leaveGroupLl;
    private ImageView addMemberBtn;
    private ImageView delMemberBtn;
    private TextView totalMemberTv;
    private Button quitBtn;
    private LinearLayout groupNameLayout;
    private LinearLayout bulletinLayout;
    private LinearLayout overviewLayout;

    private int maxLen;
    private String originText;
    private String ownerName = LoginMgr.getInstance().getAccount();

    @Override
    protected IGroupDetailSettingContract.IGroupDetailSettingView createView()
    {
        return this;
    }

    @Override
    protected GroupDetailSettingPresenter createPresenter()
    {
        return new GroupDetailSettingPresenter(this);
    }

    @Override
    public void initializeComposition()
    {
        setContentView(R.layout.chat_setting_group);

        headIv = (ImageView) findViewById(R.id.group_photo_logo);
        nameTv = (TextView) findViewById(R.id.group_name_tv);
        groupNumberTv = (TextView) findViewById(R.id.group_number);
        enterChatIv = (ImageView) findViewById(R.id.enter_chat);
        groupNameTv = (TextView) findViewById(R.id.group_name);
        bulletinTv = (TextView) findViewById(R.id.group_bulletin_content);
        descriptionTv = (TextView) findViewById(R.id.group_brief_introduction_content);
        bulletinLayout = (LinearLayout) findViewById(R.id.group_notice_layout);
        overviewLayout = (LinearLayout) findViewById(R.id.group_brief_introduction_layout);
        totalMemberRl = (LinearLayout) findViewById(R.id.totalMemberNum_layout);
        totalMemberTv = (TextView) findViewById(R.id.totalMemberNum_tag);
        lockIv = (ImageView) findViewById(R.id.lock_img);
        clearHistoryLl = (LinearLayout) findViewById(R.id.im_setting_clear_history);
        leaveGroupLl = (LinearLayout) findViewById(R.id.quit);
        groupNameLayout = (LinearLayout) findViewById(R.id.group_name_layout);
        quitBtn = (Button) findViewById(R.id.quit_btn);

        addMemberBtn = (ImageView) findViewById(R.id.add_member_btn);
        delMemberBtn = (ImageView) findViewById(R.id.del_member_btn);

        if (ownerName.equals(constGroup.getOwner()))
        {
            delMemberBtn.setVisibility(View.VISIBLE);
        }

        setData();
    }

    private void setData()
    {
        loadHead();
        mPresenter.queryGroupMembers();
        nameTv.setText(constGroup.getName());
        groupNumberTv.setText(constGroup.getGroupId());
        groupNameTv.setText(constGroup.getName());
        bulletinTv.setText(constGroup.getAnnounce());
        descriptionTv.setText(constGroup.getIntro());
        updateTotalMember(ImMgr.getInstance().getGroupMemberById(constGroup.getGroupId()).size());
        if (!constGroup.isAnnounceEmpty())
        {
            bulletinTv.setText(constGroup.getAnnounce());
        }
        if (!constGroup.isIntroEmpty())
        {
            descriptionTv.setText(constGroup.getIntro());
        }

        if (constGroup.getGroupType() == ConstGroup.FIXED)
        {
            lockIv.setSelected(true);
        }
        else
        {
            lockIv.setSelected(false);
        }

        enterChatIv.setOnClickListener(this);
        totalMemberRl.setOnClickListener(this);
        lockIv.setOnClickListener(this);
        clearHistoryLl.setOnClickListener(this);
        leaveGroupLl.setOnClickListener(this);
        addMemberBtn.setOnClickListener(this);
        delMemberBtn.setOnClickListener(this);
        quitBtn.setOnClickListener(this);
        groupNameLayout.setOnClickListener(this);
        bulletinLayout.setOnClickListener(this);
        overviewLayout.setOnClickListener(this);
    }

    @Override
    public void updateTotalMember(final int count)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                totalMemberTv.setText(getString(R.string.group_setting_all_member_nubmer, count));
            }
        });
    }

    @Override
    public void updateGroupInfo(final String groupId)
    {
        totalMemberRl.postDelayed(new Runnable() {
            @Override
            public void run() {
                constGroup = ImMgr.getInstance().getGroupById(groupId);
                if (null == constGroup)
                {
                    return;
                }
                nameTv.setText(constGroup.getName());
                groupNameTv.setText(constGroup.getName());
                bulletinTv.setText(constGroup.getAnnounce());
                descriptionTv.setText(constGroup.getIntro());
                if (ownerName.equals(constGroup.getOwner())) {
                    Toast.makeText(GroupDetailSettingActivity.this, getString(R.string.modify_success), Toast.LENGTH_SHORT).show();
                }
            }
        }, 1000);
        /*runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                constGroup = ImMgr.getInstance().getGroupById(groupId);
                nameTv.setText(constGroup.getName());
                groupNameTv.setText(constGroup.getName());
                bulletinTv.setText(constGroup.getAnnounce());
                descriptionTv.setText(constGroup.getIntro());
                if (ownerName.equals(constGroup.getOwner()))
                {
//                    Toast.makeText(getParent(), getString(R.string.modify_success), Toast.LENGTH_SHORT).show();
                }
            }
        });*/
    }

    @Override
    public void loadHead()
    {
        groupHeadFetcher.loadHead(constGroup, headIv);
    }

    @Override
    public void initializeData()
    {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra(UIConstants.BUNDLE_KEY);
        constGroup = (ConstGroup) bundle.get(UIConstants.CONST_GROUP);
        mPresenter.registerBroadcast();
        groupHeadFetcher = new GroupHeadFetcher(this);
        mPresenter.setConstGroup(constGroup);
}

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.group_name_layout:
                if (ownerName.equals(constGroup.getOwner()) ||
                        ConstGroup.DISCUSSION == constGroup.getGroupType())
                {
                    showGroupNameDialog();
                }
                break;
            case R.id.enter_chat:
                mPresenter.enterChat();
                break;
            case R.id.totalMemberNum_layout:
                mPresenter.showGroupMembers();
                break;
            case R.id.lock_img:
                if (lockIv.isSelected())
                {
                    Toast.makeText(this, getString(R.string.no_trans_group), Toast.LENGTH_SHORT).show();
                    return;
                }
                String myAccount = ownerName;
                if (!myAccount.equals(constGroup.getOwner()))
                {
                    Toast.makeText(this, getString(R.string.not_group_owner), Toast.LENGTH_SHORT).show();
                    return;
                }
                mPresenter.lockGroup();
                lockIv.setSelected(true);
                break;
            /*case R.id.im_setting_clear_history:
                mPresenter.clearHistory();
                break;
            case R.id.quit:
                mPresenter.leaveGroup();
                break;*/
            case R.id.add_member_btn:
                mPresenter.enterAddMember();
                break;
            case R.id.del_member_btn:
                mPresenter.enterDelMembers();
                break;
            case R.id.quit_btn:
                mPresenter.quitGroup();
                ActivityStack.getIns().popupAbove(MainActivity.class);
                break;
            case R.id.group_notice_layout:
                if (ownerName.equals(constGroup.getOwner()) ||
                        ConstGroup.DISCUSSION == constGroup.getGroupType())
                {
                    showGroupNoticeDialog();
                }
                break;
            case R.id.group_brief_introduction_layout:
                if (ownerName.equals(constGroup.getOwner()) ||
                        ConstGroup.DISCUSSION == constGroup.getGroupType())
                {
                    showGroupAboutDialog();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mPresenter.unregisterBroadcast();
    }

    private void showGroupNameDialog()
    {
        maxLen = ConstGroup.DISCUSSION_NAME_MAXLEN;
        originText = constGroup.getName();

        final EditText editText = new EditText(this);
        editText.setText(originText);
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(
                maxLen)});
        new AlertDialog.Builder(this).setView(editText)
                .setNegativeButton(getString(R.string.cancel), null)
                .setPositiveButton(getString(R.string.conform), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        String newText = editText.getText().toString();
                        if (!TextUtils.isEmpty(newText))
                        {
                            constGroup.setName(newText);
//                            mPresenter.setConstGroup(constGroup);
                            mPresenter.modifyGroup(constGroup);
                        }
                    }
                }).show();
    }

    private void showGroupNoticeDialog()
    {
        maxLen = ConstGroup.GROUP_ANNOUNCE_MAXLEN;
        originText = constGroup.getAnnounce();

        final EditText etNotice = new EditText(this);
        etNotice.setText(originText);
        etNotice.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLen)});
        new AlertDialog.Builder(this)
                .setTitle("Please input the group notice")
                .setView(etNotice)
                .setNegativeButton(getString(R.string.cancel), null)
                .setPositiveButton(getString(R.string.conform), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newText = etNotice.getText().toString();
                        if (!TextUtils.isEmpty(newText))
                        {
                            constGroup.setAnnounce(newText);
//                            mPresenter.setConstGroup(constGroup);
                            mPresenter.modifyGroup(constGroup);
                        }
                    }
                }).show();
    }

    private void showGroupAboutDialog()
    {
        maxLen = ConstGroup.GROUP_ABOUT_MAXLEN;
        originText = constGroup.getIntro();

        final EditText etAbout = new EditText(this);
        etAbout.setText(originText);
        new AlertDialog.Builder(this)
                .setTitle("Please input the group introduction")
                .setView(etAbout)
                .setNegativeButton(getString(R.string.cancel), null)
                .setPositiveButton(getString(R.string.conform), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newText = etAbout.getText().toString();
                        if (!TextUtils.isEmpty(newText))
                        {
                            constGroup.setIntro(newText);
//                            mPresenter.setConstGroup(constGroup);
                            mPresenter.modifyGroup(constGroup);
                        }
                    }
                }).show();
    }
}
