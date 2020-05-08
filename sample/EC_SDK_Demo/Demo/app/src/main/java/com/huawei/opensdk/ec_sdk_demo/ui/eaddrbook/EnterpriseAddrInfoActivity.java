package com.huawei.opensdk.ec_sdk_demo.ui.eaddrbook;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
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
import com.huawei.opensdk.ec_sdk_demo.module.headphoto.HeadIconTools;
import com.huawei.opensdk.ec_sdk_demo.ui.base.BaseActivity;
import com.huawei.opensdk.ec_sdk_demo.widget.CircleView;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is about contact info activity.
 */
public class EnterpriseAddrInfoActivity extends BaseActivity implements View.OnClickListener, LocBroadcastReceiver {

    private Toast toast;
    private CircleView circleAvatar = null;
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
    private Bitmap headIcon = null;

    private List<TsdkDepartmentInfo> list = new ArrayList<>();
    private int deptSeq;
    private String deptName;
    private EntAddressBookInfo entAddressBookInfo;

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
        circleAvatar = (CircleView) findViewById(R.id.blog_head_iv);
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
        enterChat.setVisibility(View.GONE);
        deleteFriend.setVisibility(View.GONE);

        setData();
    }

    private void setData()
    {
        if (!entAddressBookInfo.getHeadIconPath().isEmpty())
        {
            headIcon = HeadIconTools.getBitmapByPath(entAddressBookInfo.getHeadIconPath());
        }
        else
        {
            headIcon = HeadIconTools.getBitmapByIconId(entAddressBookInfo.getSysIconID());
        }
        circleAvatar.setBitmapParams(headIcon);
        circleAvatar.invalidate();

        name.setText(showContent(entAddressBookInfo.getEaddrName()));
        number.setVisibility(View.GONE);
        signature.setVisibility(View.VISIBLE);
        signature.setText(showContent(entAddressBookInfo.getSignature()));
        account.setText(showContent(entAddressBookInfo.getEaddrAccount()));
        dept.setText(showContent(entAddressBookInfo.getEaddrDept()));
        title.setText(showContent(entAddressBookInfo.getTitle()));
        softPhone.setText(showContent(entAddressBookInfo.getTerminal()));
        mobile.setText(showContent(entAddressBookInfo.getMobile()));
        softVideoPhone.setText(showContent(entAddressBookInfo.getTerminal()));
        email.setText(showContent(entAddressBookInfo.getEmail()));
        address.setText(showContent(entAddressBookInfo.getAddress()));
        zip.setText(showContent(entAddressBookInfo.getZipCode()));

        dept.setOnClickListener(this);
    }

    @Override
    public void initializeData() {
        //Get the contact details of the click
        LocBroadcast.getInstance().registerBroadcast(this, eActions);
        Intent intent = getIntent();
        entAddressBookInfo = (EntAddressBookInfo) intent.getSerializableExtra(UIConstants.CONTACT_INFO);
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
     * This method is used to show what needs to be displayed.
     * @param content Indicates show content
     * @return
     */
    private String showContent(String content)
    {
        return TextUtils.isEmpty(content) ? (getString(R.string.none)) : content;
    }

    /**
     * This method is used to cancel Toast content.
     */
    private void cancelToast()
    {
        if (toast != null)
        {
            toast.cancel();
        }
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
            default:
                break;
        }
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
            default:
                break;
        }
    }
}
