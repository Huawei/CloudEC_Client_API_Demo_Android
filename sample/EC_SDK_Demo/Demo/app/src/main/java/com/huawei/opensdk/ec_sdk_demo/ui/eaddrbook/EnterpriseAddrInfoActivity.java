package com.huawei.opensdk.ec_sdk_demo.ui.eaddrbook;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.ecterminalsdk.base.TsdkDepartmentInfo;
import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcastReceiver;
import com.huawei.opensdk.contactservice.eaddr.EntAddressBookInfo;
import com.huawei.opensdk.contactservice.eaddr.EnterpriseAddressBookMgr;
import com.huawei.opensdk.contactservice.eaddr.QueryDepartmentResult;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.ui.base.BaseActivity;
import com.huawei.opensdk.ec_sdk_demo.widget.SimpleListDialog;
import com.huawei.opensdk.imservice.ImConstant;
import com.huawei.opensdk.imservice.ImContactGroupInfo;
import com.huawei.opensdk.imservice.ImMgr;
import com.huawei.opensdk.loginmgr.LoginMgr;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is about contact info activity.
 */
public class EnterpriseAddrInfoActivity extends BaseActivity implements View.OnClickListener, LocBroadcastReceiver {

    private Toast toast;
    private ImageView userAvatar = null;
    private ImageView state = null;
    private TextView name = null;
    private TextView number = null;
    private TextView signature = null;
    private ImageView enterChat = null;
    private TextView account = null;
    private TextView dept = null;
    private TextView title = null;
    private TextView softPhone = null;
    private TextView mobile = null;
    private TextView softVideoPhone = null;
    private TextView email = null;
    private TextView address = null;
    private LinearLayout fax = null;
    private TextView zip = null;
    private Button deleteFriend = null;
    private ImageView opContact = null;

    private List<TsdkDepartmentInfo> list = new ArrayList<>();
    private int deptSeq;
    private String deptName;
    private EntAddressBookInfo entAddressBookInfo;
    private String selfAccount = LoginMgr.getInstance().getAccount();
    private long sefFriendId; // 添加为好友之后的contactId
    private List<ImContactGroupInfo> contactGroupList = ImMgr.getInstance().getAllContactGroupList();
    private long checkGroupId;
    private ImConstant.ImStatus selfStatus = ImMgr.getInstance().getStatus();

    private String[] eActions = new String[]{
            CustomBroadcastConstants.ACTION_ENTERPRISE_GET_DEPARTMENT_FAILED,
            CustomBroadcastConstants.ACTION_ENTERPRISE_GET_DEPARTMENT_NULL,
            CustomBroadcastConstants.ACTION_ENTERPRISE_GET_DEPARTMENT_RESULT,
            CustomBroadcastConstants.ACTION_IM_USER_STATUS_CHANGE
    };

    @Override
    public void initializeComposition() {
        setContentView(R.layout.activity_enterprise_info);

        opContact = (ImageView) findViewById(R.id.right_iv);
        userAvatar = (ImageView)findViewById(R.id.blog_head_iv);
        state = (ImageView) findViewById(R.id.blog_state_iv);
        name = (TextView)findViewById(R.id.blog_name_tv);
        number = (TextView)findViewById(R.id.blog_number_tv);
        signature = (TextView) findViewById(R.id.contact_signature_tv);
        enterChat = (ImageView) findViewById(R.id.enter_chat);

        account = (TextView)findViewById(R.id.detail_content_text1);
        dept = (TextView)findViewById(R.id.detail_content_text2);
        title = (TextView)findViewById(R.id.detail_content_text3);
        softPhone = (TextView) findViewById(R.id.call_content_text1);
        mobile = (TextView)findViewById(R.id.call_content_text2);
        softVideoPhone = (TextView)findViewById(R.id.video_content_text1);
        email = (TextView)findViewById(R.id.email_content_text);
        address = (TextView)findViewById(R.id.location_content_text);
        fax = (LinearLayout) findViewById(R.id.fax_Item_layout);
        zip = (TextView)findViewById(R.id.zip_content_text);

        deleteFriend = (Button) findViewById(R.id.deletecontact);

        opContact.setVisibility(View.GONE);
        fax.setVisibility(View.GONE);
        if (selfAccount.equals(entAddressBookInfo.getEaddrAccount()))
        {
            state.setImageResource(getStatusResource(selfStatus));
            enterChat.setVisibility(View.GONE);
            deleteFriend.setVisibility(View.GONE);
        }

        setData();
    }

    private void setData()
    {
        if (!entAddressBookInfo.getHeadIconPath().isEmpty())
        {
            Bitmap headIcon = EnterpriseAddrTools.getBitmapByPath(entAddressBookInfo.getHeadIconPath());
            userAvatar.setImageBitmap(headIcon);
        }
        else
        {
            userAvatar.setImageResource(entAddressBookInfo.getSysIconID());
        }
        name.setText(entAddressBookInfo.getEaddrName());
        number.setVisibility(View.GONE);
        signature.setVisibility(View.VISIBLE);
        signature.setText(entAddressBookInfo.getSignature());
        account.setText(entAddressBookInfo.getEaddrAccount());
        dept.setText(entAddressBookInfo.getEaddrDept());
        title.setText(entAddressBookInfo.getTitle());
        softPhone.setText(entAddressBookInfo.getTerminal());
        mobile.setText(entAddressBookInfo.getMobile());
        softVideoPhone.setText(entAddressBookInfo.getTerminal());
        email.setText(entAddressBookInfo.getEmail());
        address.setText(entAddressBookInfo.getAddress());
        zip.setText(entAddressBookInfo.getZipCode());

        updateDeleteFriendButton();

        dept.setOnClickListener(this);
        deleteFriend.setOnClickListener(this);
    }

    private void updateDeleteFriendButton()
    {
        sefFriendId = ImMgr.getInstance().getFriendId(entAddressBookInfo.getEaddrAccount());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (-1 == sefFriendId)
                {
                    deleteFriend.setText(getString(R.string.addtofriend));
                }
                else
                {
                    deleteFriend.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void initializeData() {
        //Get the contact details of the click
        LocBroadcast.getInstance().registerBroadcast(this, eActions);
        Intent intent = getIntent();
        entAddressBookInfo = (EntAddressBookInfo) intent.getSerializableExtra(UIConstants.CONTACT_INFO);
        // im登陆成功后获取用户状态
        if (LoginMgr.getInstance().isImLogin())
        {
            if (selfAccount.equals(entAddressBookInfo.getEaddrAccount()))
            {
                return;
            }
            List<String> accounts = new ArrayList<>();
            accounts.add(entAddressBookInfo.getEaddrAccount());
            int result = ImMgr.getInstance().probeUserStatus(accounts);
            if (0 != result)
            {
                showToast(R.string.detect_user_status_failed);
            }
        }
    }

    @Override
    protected void onResume() {

        super.onResume();
    }

    /**
     * This method is used to show Toast content.
     * @param text Indicates show content
     */
    public void showToast(String text)
    {
        if (toast == null)
        {
            toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        }
        else
        {
            toast.setText(text);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        toast.show();
    }

    /**
     * This method is used to cancel Toast content.
     */
    public void cancelToast()
    {
        if (toast != null)
        {
            toast.cancel();
        }
    }

    private static int getStatusResource(ImConstant.ImStatus status)
    {
        int drwId = 0;
        switch (status)
        {
            case AWAY:
                drwId = R.drawable.state_offline;
                break;
            case ON_LINE:
                drwId = R.drawable.state_online;
                break;
            case BUSY:
                drwId = R.drawable.state_busy;
                break;
            case XA:
                drwId = R.drawable.state_away;
                break;
            case DND:
                drwId = R.drawable.state_uninterrupt;
                break;
            default:
                break;
        }

        return drwId;
    }

    @Override
    public void onBackPressed() {
        cancelToast();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        LocBroadcast.getInstance().unRegisterBroadcast(this, eActions);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.detail_content_text2:
                //此处查询部门仅作功能验证。ID为-1时代表获取0、1级部门
                deptSeq = EnterpriseAddressBookMgr.getInstance().searchDepartment("-1");
                break;
            case R.id.deletecontact:
                showTeamDialog();
                break;
            default:
                break;
        }
    }

    private void showTeamDialog()
    {
        List<Object> teamName = new ArrayList<>();
        if (null == contactGroupList || contactGroupList.size() == 0)
        {
            showToast(R.string.create_new_group);
            return;
        }
        for (int i = 0; i < contactGroupList.size(); i++)
        {
            teamName.add(contactGroupList.get(i).getGroupName());
        }
        final SimpleListDialog dialog = new SimpleListDialog(this, teamName);
        dialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog.dismiss();
                checkGroupId = contactGroupList.get(position).getGroupId();
                long contactId = ImMgr.getInstance().addFriend(entAddressBookInfo.getEaddrAccount(), checkGroupId);
                if (-1 == contactId)
                {
                    showToast(R.string.add_friend_failed);
                    return;
                }
                updateDeleteFriendButton();
            }
        });
        dialog.show();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case UIConstants.ENTERPRISE_SEARCH_DEPT:
                    QueryDepartmentResult departmentResult = (QueryDepartmentResult) msg.obj;
                    if (deptSeq == departmentResult.getQuerySeq())
                    {
                        list = departmentResult.getList();
                        for (int i = 0; i < list.size(); i++)
                        {
                            deptName = list.get(i).getDepartmentName() + "  ";
                        }
                        AlertDialog builder = new AlertDialog.Builder(EnterpriseAddrInfoActivity.this)
                                .setTitle("Search department result")
                                .setMessage(deptName)
                                .setNegativeButton(R.string.exit,null)
                                .create();
                        builder.show();
                    }
                    break;
                    case UIConstants.IM_USER_STATUS_UPDATE:
                        state.setImageResource(getStatusResource(ImMgr.getInstance().updateUserStatus(entAddressBookInfo.getEaddrAccount())));
                        break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onReceive(String broadcastName, Object obj) {
        switch (broadcastName)
        {
            case CustomBroadcastConstants.ACTION_ENTERPRISE_GET_DEPARTMENT_RESULT:
                Message msg = handler.obtainMessage(UIConstants.ENTERPRISE_SEARCH_DEPT, obj);
                handler.sendMessage(msg);
                break;
            case CustomBroadcastConstants.ACTION_ENTERPRISE_GET_DEPARTMENT_NULL:
                showToast("There is no inquiry to the department!");
                break;
            case CustomBroadcastConstants.ACTION_ENTERPRISE_GET_DEPARTMENT_FAILED:
                showToast("Search department failed!");
                break;
            case CustomBroadcastConstants.ACTION_IM_USER_STATUS_CHANGE:
                handler.sendEmptyMessage(UIConstants.IM_USER_STATUS_UPDATE);
                break;
            default:
                break;
        }
    }
}
