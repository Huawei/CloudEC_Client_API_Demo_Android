package com.huawei.opensdk.ec_sdk_demo.ui.servicesetting;

import android.graphics.Typeface;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
    private ImageView mOriginalPwdIV;
    private ImageView mNewPwdIV;
    private ImageView mNewPwdAgainIV;
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
        mOriginalPwdIV = (ImageView) findViewById(R.id.iv_ori_eye);
        mNewPwdIV = (ImageView) findViewById(R.id.iv_new_eye);
        mNewPwdAgainIV = (ImageView) findViewById(R.id.iv_det_eye);

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

        mOriginalPwdET.addTextChangedListener(new MyCustomTextWatcher(mOriginalPwdET));
        mNewPwdET.addTextChangedListener(new MyCustomTextWatcher(mNewPwdET));
        mNewPwdAgainET.addTextChangedListener(new MyCustomTextWatcher(mNewPwdAgainET));

        mModifyPwdBT.setOnClickListener(this);
        mOriginalPwdIV.setOnClickListener(this);
        mNewPwdIV.setOnClickListener(this);
        mNewPwdAgainIV.setOnClickListener(this);
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
            case R.id.iv_ori_eye:
                if (mOriginalPwdIV.isSelected())
                {
                    mOriginalPwdIV.setSelected(false);
                    mOriginalPwdET.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                else
                {
                    mOriginalPwdIV.setSelected(true);
                    mOriginalPwdET.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                break;
            case R.id.iv_new_eye:
                if (mNewPwdIV.isSelected())
                {
                    mNewPwdIV.setSelected(false);
                    mNewPwdET.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                else
                {
                    mNewPwdIV.setSelected(true);
                    mNewPwdET.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                break;
            case R.id.iv_det_eye:
                if (mNewPwdAgainIV.isSelected())
                {
                    mNewPwdAgainIV.setSelected(false);
                    mNewPwdAgainET.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                else
                {
                    mNewPwdAgainIV.setSelected(true);
                    mNewPwdAgainET.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
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

    class MyCustomTextWatcher implements TextWatcher {

        private EditText currentEt;

        public MyCustomTextWatcher(EditText currentEt) {
            this.currentEt = currentEt;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            boolean isEmpty = TextUtils.isEmpty(s.toString());
            switch (currentEt.getId())
            {
                case R.id.original_pwd_et:
                    if (isEmpty)
                    {
                        mOriginalPwdIV.setVisibility(View.GONE);
                    }
                    else
                    {
                        mOriginalPwdIV.setVisibility(View.VISIBLE);
                    }
                    break;
                case R.id.new_pwd_et:
                    if (isEmpty)
                    {
                        mNewPwdIV.setVisibility(View.GONE);
                    }
                    else
                    {
                        mNewPwdIV.setVisibility(View.VISIBLE);
                    }
                    break;
                case R.id.determine_pwd_et:
                    if (isEmpty)
                    {
                        mNewPwdAgainIV.setVisibility(View.GONE);
                    }
                    else
                    {
                        mNewPwdAgainIV.setVisibility(View.VISIBLE);
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
