package com.huawei.opensdk.ec_sdk_demo.ui.servicesetting;

import android.graphics.Typeface;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcastReceiver;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.ui.base.BaseActivity;
import com.huawei.opensdk.ec_sdk_demo.widget.ConfirmSimpleDialog;
import com.huawei.opensdk.loginmgr.LoginMgr;

/**
 * 修改密码界面
 */
public class ModifyPsdActivity extends BaseActivity implements View.OnClickListener, LocBroadcastReceiver {

    private TextView mTitleTV;
    private EditText mOriginalPwdET;
    private EditText mNewPwdET;
    private EditText mNewPwdAgainET;
    private Button mModifyPwdBT;
    private InputFilter inputFilter;

    private String oldPassword;
    private String newPassword;

    private String[] mActions = {CustomBroadcastConstants.MODIFY_PWD_SUCCESS};

    @Override
    public void initializeComposition() {
        setContentView(R.layout.activity_modify_psd);
        mTitleTV = (TextView) findViewById(R.id.title_text);
        mOriginalPwdET = (EditText) findViewById(R.id.original_pwd_et);
        mNewPwdET = (EditText) findViewById(R.id.new_pwd_et);
        mNewPwdAgainET = (EditText) findViewById(R.id.determine_pwd_et);
        mModifyPwdBT = (Button) findViewById(R.id.modify_pwd_btn);

        mTitleTV.setText(getString(R.string.modify_password));

        // EditText禁止输空格+换行键
        mOriginalPwdET.setFilters(new InputFilter[]{inputFilter});
        mNewPwdET.setFilters(new InputFilter[]{inputFilter});
        mNewPwdAgainET.setFilters(new InputFilter[]{inputFilter});

        // EditText中设置hint字体为系统默认字体
        mOriginalPwdET.setTypeface(Typeface.DEFAULT);
        mNewPwdET.setTypeface(Typeface.DEFAULT);
        mNewPwdAgainET.setTypeface(Typeface.DEFAULT);
        mOriginalPwdET.setTransformationMethod(new PasswordTransformationMethod());
        mNewPwdET.setTransformationMethod(new PasswordTransformationMethod());
        mNewPwdAgainET.setTransformationMethod(new PasswordTransformationMethod());

        mModifyPwdBT.setOnClickListener(this);
    }

    @Override
    public void initializeData() {
        inputFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.equals(" ") || source.toString().contentEquals("\n"))return "";
                else return null;
            }
        };

        LocBroadcast.getInstance().registerBroadcast(this, mActions);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocBroadcast.getInstance().unRegisterBroadcast(this, mActions);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.modify_pwd_btn:
                if (mOriginalPwdET.getText().toString().isEmpty())
                {
                    showConfirmDialog(getString(R.string.no_input_ori_pwd));
                    return;
                }
                if (mNewPwdET.getText().toString().isEmpty())
                {
                    showConfirmDialog(getString(R.string.no_input_new_pwd));
                    return;
                }
                if (mNewPwdAgainET.getText().toString().isEmpty())
                {
                    showConfirmDialog(getString(R.string.no_input_pwd_again));
                    return;
                }
                if (!mNewPwdET.getText().toString().equals(mNewPwdAgainET.getText().toString()))
                {
                    showConfirmDialog(getString(R.string.inconsistent_password));
                    return;
                }
                oldPassword = mOriginalPwdET.getText().toString();
                newPassword = mNewPwdAgainET.getText().toString();
                modifyPwdOpt();
                break;
                default:
                    break;
        }
    }

    /**
     * 显示修改密码界面的所有弹框
     * @param tips
     */
    private void showConfirmDialog(String tips)
    {
        ConfirmSimpleDialog dialog = new ConfirmSimpleDialog(this, tips);
        if (getString(R.string.modify_pwd_success).equals(tips))
        {
            dialog.setRightButtonListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LoginMgr.getInstance().logout();
                }
            });
        }
        dialog.show();
    }

    /**
     * 修改密码
     */
    private void modifyPwdOpt()
    {
        int result = LoginMgr.getInstance().modifyPwd(newPassword, oldPassword);
        if (0 != result)
        {
            showToast(R.string.modify_pwd_failed);
        }
    }

    @Override
    public void onReceive(String broadcastName, Object obj) {
        if (CustomBroadcastConstants.MODIFY_PWD_SUCCESS == broadcastName)
        {
            showConfirmDialog(getString(R.string.modify_pwd_success));
        }
    }
}
