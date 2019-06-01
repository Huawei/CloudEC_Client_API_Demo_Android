package com.huawei.opensdk.ec_sdk_demo.ui.im;

import android.app.ProgressDialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.logic.contact.mvp.GroupDetailSettingPresenter;
import com.huawei.opensdk.ec_sdk_demo.logic.contact.mvp.IGroupDetailSettingContract;
import com.huawei.opensdk.ec_sdk_demo.module.headphoto.GroupHeadFetcher;
import com.huawei.opensdk.ec_sdk_demo.ui.base.MVPBaseActivity;
import com.huawei.opensdk.ec_sdk_demo.util.CommonUtil;
import com.huawei.opensdk.ec_sdk_demo.widget.EditDialog;
import com.huawei.opensdk.imservice.ImChatGroupInfo;
import com.huawei.opensdk.imservice.ImConstant;
import com.huawei.opensdk.loginmgr.LoginMgr;

/**
 * This class is about set group detail info activity.
 */
public class GroupDetailSettingActivity extends MVPBaseActivity<IGroupDetailSettingContract.IGroupDetailSettingView, GroupDetailSettingPresenter>
        implements IGroupDetailSettingContract.IGroupDetailSettingView, View.OnClickListener
{
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
    private ProgressDialog mDialog;
    private ImageView moreBtn;

    private int maxLen;
    private String originText;
    private String ownerName = LoginMgr.getInstance().getAccount();

    private ImChatGroupInfo chatGroupInfo;

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

        moreBtn = (ImageView) findViewById(R.id.right_iv);
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

        moreBtn.setVisibility(View.GONE);
        setData();
    }

    @Override
    public void initializeData()
    {
        Intent intent = getIntent();
        chatGroupInfo = (ImChatGroupInfo) intent.getSerializableExtra(UIConstants.IM_CHAT_GROUP_INFO);

        mPresenter.registerBroadcast();
        groupHeadFetcher = new GroupHeadFetcher(this);
        mPresenter.setChatGroupInfo(chatGroupInfo);
    }

    private void setData()
    {
        loadHead();
        mPresenter.queryGroupMembers();
        nameTv.setText(chatGroupInfo.getGroupName());
        groupNumberTv.setText(chatGroupInfo.getGroupId());
        groupNameTv.setText(chatGroupInfo.getGroupName());
        bulletinTv.setText(chatGroupInfo.getManifesto());
        descriptionTv.setText(chatGroupInfo.getDescription());
        if (chatGroupInfo.getGroupType() == ImConstant.FIXED)
        {
            lockIv.setSelected(true);
        }
        else
        {
            lockIv.setSelected(false);
        }

        updateActionMember();

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

    private void updateActionMember()
    {
        if (ownerName.equals(chatGroupInfo.getOwnerAccount()))
        {
            addMemberBtn.setVisibility(View.VISIBLE);
            delMemberBtn.setVisibility(View.VISIBLE);
        }

        if (chatGroupInfo.getGroupType() == ImConstant.FIXED && !ownerName.equals(chatGroupInfo.getOwnerAccount()))
        {
            addMemberBtn.setVisibility(View.GONE);
            delMemberBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public void updateTotalMember(final int count)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                totalMemberTv.setText(getString(R.string.group_setting_all_member_number, count));
            }
        });
    }

    @Override
    public void updateGroupInfo(final ImChatGroupInfo imChatGroupInfo)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                chatGroupInfo = imChatGroupInfo;
                setData();
                dismissLoginDialog();
            }
        });
    }

    @Override
    public void toast(final int id) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(GroupDetailSettingActivity.this, getString(id), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void finishActivity() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setResult(UIConstants.IM_RESULT_CODE_CHAT_GROUP_DELETE);
                finish();
            }
        });
    }

    @Override
    public void loadHead()
    {
//        groupHeadFetcher.loadHead(constGroup, headIv);
        // TODO: 2019/1/28 头像目前先采用默认设置，待处理
        headIv.setImageResource(R.drawable.group_head_large);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.group_name_layout:
                if (ownerName.equals(chatGroupInfo.getOwnerAccount()) ||
                        ImConstant.DISCUSSION == chatGroupInfo.getGroupType())
                {
                    showGroupInfoDialog(R.string.input_group_name);
                }
                break;
            case R.id.enter_chat:
                mPresenter.enterChat();
                break;
            case R.id.totalMemberNum_layout:
                mPresenter.showGroupMembers();
                break;
            case R.id.lock_img:
                if (!ownerName.equals(chatGroupInfo.getOwnerAccount()))
                {
                    Toast.makeText(this, getString(R.string.not_group_owner), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (lockIv.isSelected())
                {
                    chatGroupInfo.setGroupType(ImConstant.DISCUSSION);
                }
                else
                {
                    chatGroupInfo.setGroupType(ImConstant.FIXED);
                }
                showLoginDialog();
                mPresenter.modifyGroup(chatGroupInfo, ImConstant.GroupOpType.CHAT_GROUP_MODIFY_GROUP_TYPE);
                break;
            case R.id.add_member_btn:
                mPresenter.enterAddMember();
                break;
            case R.id.del_member_btn:
                mPresenter.enterDelMembers();
                break;
            case R.id.quit_btn:
                mPresenter.quitGroup();
//                ActivityStack.getIns().popupAbove(MainActivity.class);
                break;
            case R.id.group_notice_layout:
                if (ownerName.equals(chatGroupInfo.getOwnerAccount()) ||
                        ImConstant.DISCUSSION == chatGroupInfo.getGroupType())
                {
                    showGroupInfoDialog(R.string.input_group_bulletin);
                }
                break;
            case R.id.group_brief_introduction_layout:
                if (ownerName.equals(chatGroupInfo.getOwnerAccount()) ||
                        ImConstant.DISCUSSION == chatGroupInfo.getGroupType())
                {
                    showGroupInfoDialog(R.string.input_group_description);
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

    public void showLoginDialog()
    {
        if (null == mDialog)
        {
            mDialog = new ProgressDialog(this);
        }

        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.show();
    }

    public void dismissLoginDialog() {
        if (mDialog != null && mDialog.isShowing())
        {
            mDialog.dismiss();
        }
    }

    private void showGroupInfoDialog(final int id)
    {
        final EditDialog editDialog = new EditDialog(this, id);
        editDialog.setRightButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtil.hideSoftInput(GroupDetailSettingActivity.this);
                if (TextUtils.isEmpty(editDialog.getText()))
                {
                    showToast(R.string.invalid_number);
                    return;
                }
                setChangeInfo(id, editDialog.getText());
                showLoginDialog();
                mPresenter.modifyGroup(chatGroupInfo, ImConstant.GroupOpType.CHAT_GROUP_MODIFY_DEFAULT_PARAM);
            }
        });
        editDialog.show();
    }

    private void setChangeInfo(int id, String changeInfo)
    {
        switch (id)
        {
            case R.string.input_group_name:
                chatGroupInfo.setGroupName(changeInfo);
                break;
            case R.string.input_group_bulletin:
                chatGroupInfo.setManifesto(changeInfo);
                break;
            case R.string.input_group_description:
                chatGroupInfo.setDescription(changeInfo);
                break;
                default:
                    break;
        }
    }
}
