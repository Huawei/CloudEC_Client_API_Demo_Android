package com.huawei.opensdk.ec_sdk_demo.ui.eaddrbook;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.ecterminalsdk.base.TsdkContactsInfo;
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

import java.util.ArrayList;
import java.util.List;

/**
 * This class is about contact info activity.
 */
public class EnterpriseAddrInfoActivity extends BaseActivity implements View.OnClickListener, LocBroadcastReceiver {

    private Toast toast;
    private ImageView userAvatar = null;
    private TextView account = null;
    private TextView name = null;
    private TextView staff = null;
    private TextView number = null;
    private TextView dept = null;
    private TextView title = null;
    private TextView mobile = null;
    private TextView phone = null;
    private TextView homePhone = null;
    private TextView email = null;
    private TextView otherPhone = null;
    private TextView otherPhone2 = null;
    private TextView zip = null;
    private TextView address = null;
    private TextView signature = null;
    private ImageView blogSex = null;
    private ImageView eaddrBack = null;

    private List<TsdkContactsInfo> listEaddrs = new ArrayList<>();
    private List<EntAddressBookInfo> iconEaddrs = new ArrayList<>();
    private int index;
    private int deptSeq;
    private List<TsdkDepartmentInfo> list = new ArrayList<>();
    private String deptName;

    private String[] eActions = new String[]{
            CustomBroadcastConstants.ACTION_ENTERPRISE_GET_DEPARTMENT_FAILED,
            CustomBroadcastConstants.ACTION_ENTERPRISE_GET_DEPARTMENT_NULL,
            CustomBroadcastConstants.ACTION_ENTERPRISE_GET_DEPARTMENT_RESULT
    };

    @Override
    public void initializeComposition() {
        setContentView(R.layout.activity_enterprise_info);

        //Get contact list (all contact information except Avatar)
        listEaddrs = EnterpriseAddressBookMgr.getInstance().getList();

        //Get Contact Avatar
        iconEaddrs = EnterpriseAddrBookActivity.getList();

        //Set User avatar
        userAvatar = (ImageView)findViewById(R.id.eaddr_head_iv);
        int iconId = iconEaddrs.get(index).getSysIconID();
        String iconPath = iconEaddrs.get(index).getHeadIconPath();
        if (!iconEaddrs.get(index).getHeadIconPath().isEmpty())
        {
            Bitmap headIcon = EnterpriseAddrTools.getBitmapByPath(iconPath);
            userAvatar.setImageBitmap(headIcon);
        }
        else
        {
            userAvatar.setImageResource(iconId);
        }

        account = (TextView)findViewById(R.id.staff_account);
        name = (TextView)findViewById(R.id.eaddr_name_tv);
        staff = (TextView)findViewById(R.id.staff_no);
        number = (TextView)findViewById(R.id.terminal);

        dept = (TextView)findViewById(R.id.dept_name);
        dept.setOnClickListener(this);

        title = (TextView)findViewById(R.id.title);
        mobile = (TextView)findViewById(R.id.mobile);
        phone = (TextView)findViewById(R.id.office_phone);
        homePhone = (TextView)findViewById(R.id.home_phone);
        email = (TextView)findViewById(R.id.email);
        otherPhone = (TextView)findViewById(R.id.other_phone);
        otherPhone2 = (TextView)findViewById(R.id.other_phone2);
        zip = (TextView)findViewById(R.id.zip_code);
        address = (TextView)findViewById(R.id.address);
        signature = (TextView)findViewById(R.id.eaddr_signature_tv);
        blogSex = (ImageView)findViewById(R.id.eaddr_sex_iv);

        eaddrBack = (ImageView)findViewById(R.id.book_back);
        eaddrBack.setOnClickListener(this);

        account.setText(listEaddrs.get(index).getStaffAccount());
        name.setText(listEaddrs.get(index).getPersonName());
        staff.setText(listEaddrs.get(index).getStaffNo());
        number.setText(listEaddrs.get(index).getTerminal());
        dept.setText(listEaddrs.get(index).getDepartmentName());
        title.setText(listEaddrs.get(index).getTitle());
        mobile.setText(listEaddrs.get(index).getMobile());
        phone.setText(listEaddrs.get(index).getOfficePhone());
        homePhone.setText(listEaddrs.get(index).getHomePhone());
        email.setText(listEaddrs.get(index).getEmail());
        otherPhone.setText(listEaddrs.get(index).getOtherPhone());
        otherPhone2.setText(listEaddrs.get(index).getOtherPhone2());
        zip.setText(listEaddrs.get(index).getZipCode());
        address.setText(listEaddrs.get(index).getAddress());
        signature.setText(listEaddrs.get(index).getSignature());
        if(listEaddrs.get(index).getGender().equals("female"))
        {
            blogSex.setBackgroundResource(R.drawable.sex_female);
        }
        else if(listEaddrs.get(index).getGender().equals("male"))
        {
            blogSex.setBackgroundResource(R.drawable.sex_male);
        }
    }

    @Override
    public void initializeData() {
        //Get clicked contact position and list.index() correspondence
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        index = bundle.getInt(UIConstants.CONTACT_POSITION);
    }

    @Override
    protected void onResume() {
        LocBroadcast.getInstance().registerBroadcast(this, eActions);
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

    @Override
    public void onBackPressed() {
        cancelToast();
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.book_back:
                LocBroadcast.getInstance().unRegisterBroadcast(this, eActions);
                finish();
                break;
            case R.id.dept_name:
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
