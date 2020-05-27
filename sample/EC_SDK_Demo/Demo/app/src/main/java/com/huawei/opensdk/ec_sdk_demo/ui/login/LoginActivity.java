package com.huawei.opensdk.ec_sdk_demo.ui.login;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;

import com.huawei.opensdk.callmgr.CallMgr;
import com.huawei.opensdk.callmgr.ctdservice.CtdMgr;
import com.huawei.opensdk.commonservice.common.LocContext;
import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcastReceiver;
import com.huawei.opensdk.commonservice.util.CrashUtil;
import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.contactservice.eaddr.EnterpriseAddressBookMgr;
import com.huawei.opensdk.demoservice.ConfConvertUtil;
import com.huawei.opensdk.demoservice.MeetingMgr;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.logic.call.CallFunc;
import com.huawei.opensdk.ec_sdk_demo.logic.conference.ConfFunc;
import com.huawei.opensdk.ec_sdk_demo.logic.eaddrbook.EnterpriseAddrBookFunc;
import com.huawei.opensdk.ec_sdk_demo.logic.login.ILoginContract;
import com.huawei.opensdk.ec_sdk_demo.logic.login.LoginFunc;
import com.huawei.opensdk.ec_sdk_demo.logic.login.LoginModel;
import com.huawei.opensdk.ec_sdk_demo.logic.login.LoginPresenter;
import com.huawei.opensdk.ec_sdk_demo.ui.IntentConstant;
import com.huawei.opensdk.ec_sdk_demo.ui.base.MVPBaseActivity;
import com.huawei.opensdk.ec_sdk_demo.util.ActivityUtil;
import com.huawei.opensdk.ec_sdk_demo.util.FileUtil;
import com.huawei.opensdk.ec_sdk_demo.util.PermissionDialog;
import com.huawei.opensdk.ec_sdk_demo.util.ZipUtil;
import com.huawei.opensdk.ec_sdk_demo.widget.ConfirmSimpleDialog;
import com.huawei.opensdk.loginmgr.LoginConstant;
import com.huawei.opensdk.loginmgr.LoginMgr;
import com.huawei.opensdk.servicemgr.ServiceMgr;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is about login ui logic
 */
public class LoginActivity extends MVPBaseActivity<ILoginContract.LoginBaseView, LoginPresenter>
        implements ILoginContract.LoginBaseView, LocBroadcastReceiver
{

    /**
     * user name
      */
    private String mUserName;

    /**
     * password
     */
    private String mPassword;

    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private Button mLoginButton;
    private Button mAnonymousButton;

    private CheckBox mAutoLoginBox;
    private ImageView mLoginSettingBtn;
    private ImageView mEyeBtn;

    private ProgressDialog mDialog;
    private ConfirmSimpleDialog mSimpleDialog;
    private String[] mActions = new String[]{CustomBroadcastConstants.LOGIN_SUCCESS,
            CustomBroadcastConstants.AUTH_FAILED,
            CustomBroadcastConstants.LOGIN_FAILED,
            CustomBroadcastConstants.LOGOUT};

    private SharedPreferences mSharedPreferences;
    private LoginModel mSettingPresenter;

    @Override
    public void initializeComposition()
    {
        setContentView(R.layout.activity_login);

        mUsernameEditText = (EditText) findViewById(R.id.et_account);
        mPasswordEditText = (EditText) findViewById(R.id.et_password);
        mLoginButton = (Button) findViewById(R.id.btn_login);
        mAnonymousButton = (Button) findViewById(R.id.btn_anonymous);
        mLoginSettingBtn = (ImageView) findViewById(R.id.iv_login_setting);
        mAutoLoginBox = (CheckBox) findViewById(R.id.check_auto_login);
        mEyeBtn = (ImageView) findViewById(R.id.iv_eye);
        mAutoLoginBox.setChecked(mSharedPreferences.getBoolean(LoginConstant.AUTO_LOGIN, false));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //6.0才用动态权限
            initPermission();
        }else {
            initResource();
        }

        mPresenter.onLoginParams();

        mAutoLoginBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSharedPreferences.edit().putBoolean(LoginConstant.AUTO_LOGIN, isChecked).commit();
            }
        });

        mLoginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                showLoginDialog(getString(R.string.logining_msg));
                mUserName = mUsernameEditText.getText().toString().trim();
                mPassword = mPasswordEditText.getText().toString();
                new Thread(runnable).start();
            }
        });

        mLoginSettingBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dismissLoginDialog();
                ActivityUtil.startActivity(LoginActivity.this, IntentConstant.LOGIN_SETTING_ACTIVITY_ACTION,
                        new String[]{IntentConstant.DEFAULT_CATEGORY});
            }
        });

        mAnonymousButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                ActivityUtil.startActivity(LoginActivity.this, IntentConstant.ANONYMOUS_CONF_ACTIVITY_ACTION,
                        new String[]{IntentConstant.DEFAULT_CATEGORY});
            }
        });

        mEyeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEyeBtn.isSelected())
                {
                    mEyeBtn.setSelected(false);
                    // 密码不可见
                    mPasswordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                else
                {
                    mEyeBtn.setSelected(true);
                    // 密码可见
                    mPasswordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });

        mPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString()))
                {
                    mEyeBtn.setVisibility(View.GONE);
                }
                else
                {
                    mEyeBtn.setVisibility(View.VISIBLE);
                }
            }
        });

        // 禁止口令输入组件提供拷出功能
        mPasswordEditText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });

        if (mAutoLoginBox.isChecked())
        {
            showLoginDialog(getString(R.string.logining_msg));
            mUserName = mUsernameEditText.getText().toString().trim();
            mPassword = mPasswordEditText.getText().toString();
            mPresenter.doLogin(mUserName, mPassword);
        }
    }

    /*最新的API要求需要在子线程里面判断网络信息，和一些耗时操作*/
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            mPresenter.doLogin(mUserName, mPassword);
        }
    };

    @Override
    public void initializeData()
    {
        mPresenter.initServerData();
        mSharedPreferences = getSharedPreferences(LoginConstant.FILE_NAME, Activity.MODE_PRIVATE);
        mSettingPresenter = new LoginModel(mSharedPreferences);
        mIdoProtocol = mSharedPreferences.getInt(LoginConstant.CONF_CTRL_PROTOCOL, 0);
        MeetingMgr.getInstance().setConfProtocol(ConfConvertUtil.convertConfctrlProtocol(mIdoProtocol));
        LoginConstant.HAVE_WRITE_PERMISSION = mSharedPreferences.getBoolean(LoginConstant.WRITE_PERMISSION, false);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        LocBroadcast.getInstance().registerBroadcast(this, mActions);
    }

    @Override
    public void dismissLoginDialog()
    {
        if (mDialog != null && mDialog.isShowing())
        {
            mDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        dismissLoginDialog();
        LocBroadcast.getInstance().unRegisterBroadcast(this, mActions);
    }

    public void showLoginDialog(String msg)
    {
        if (null == mDialog)
        {
            mDialog = new ProgressDialog(this);
        }

        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setTitle(msg);
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.show();
    }

    @Override
    public void setEditText(String account, String password)
    {
        mUsernameEditText.setText(account);
        mPasswordEditText.setText(password);
        if (TextUtils.isEmpty(mPasswordEditText.getText().toString()))
        {
            mEyeBtn.setVisibility(View.GONE);
        }
        else
        {
            mEyeBtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showToast(int resId)
    {
        super.showToast(resId);
    }

    @Override
    protected ILoginContract.LoginBaseView createView()
    {
        return this;
    }

    @Override
    protected LoginPresenter createPresenter()
    {
        return new LoginPresenter(this);
    }

    @Override
    public void onReceive(String broadcastName, Object obj)
    {
        switch (broadcastName)
        {
            case CustomBroadcastConstants.LOGIN_SUCCESS:
                LogUtil.i(UIConstants.DEMO_TAG, "login success");
                dismissLoginDialog();
                break;
            case CustomBroadcastConstants.AUTH_FAILED:
            case CustomBroadcastConstants.LOGIN_FAILED:
                LogUtil.i(UIConstants.DEMO_TAG, "login fail");
                dismissLoginDialog();
                break;
            case CustomBroadcastConstants.LOGOUT:
                break;
            default:
                break;
        }
    }

    /********************************************权限申请部分 Begin ******************************************************/

    /**
     * 声明一个数组permissions，将需要的权限都放在里面
     */
    String[] permissions = new String[]{
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.PROCESS_OUTGOING_CALLS,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.ACCESS_FINE_LOCATION};

    /**
     * 创建一个mPermissionList，逐个判断哪些权限未授予，未授予的权限存储到mPerrrmissionList中
     */
    List<String> mPermissionList = new ArrayList<>();
    /**
     * 权限请求码
     */
    private final int mRequestCode = 100;
    /**
     *  文件长度
     */
    private static final int EXPECTED_FILE_LENGTH = 7;
    /**
     *
     */
    private static final String FRONT_PKG = "com.huawei.opensdk.ec_sdk_demo";
    /**
     *  IDO协议
     */
    private int mIdoProtocol = 0;

    /**
     * 权限判断和申请
     */
    private void initPermission() {

        mPermissionList.clear();//清空没有通过的权限

        //逐个判断你要的权限是否已经通过
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(LoginActivity.this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permissions[i]);//添加还未授予的权限
            }
        }
        //申请权限
        if (mPermissionList.size() > 0) {
            //有权限没有通过，需要申请
            showPermissionDialog();
        }else{
            //说明权限都已经通过，可以做你想做的事情去
            initResource();
        }
    }

    /**
     *
     * @param requestCode   是我们自己定义的权限请求码
     * @param permissions   是我们请求的权限名称数组
     * @param grantResults  是我们在弹出页面后是否允许权限的标识数组，数组的长度对应的是权限名称数组的长度，数组的数据0表示允许权限，-1表示我们点击了禁止权限
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasPermissionDismiss = false;//有权限没有通过
        if (mRequestCode == requestCode) {
            for (int i = 0; i < grantResults.length; i++) {
                if ("android.permission.WRITE_EXTERNAL_STORAGE".equals(permissions[i]) && 0 == grantResults[i])
                {
                    mSharedPreferences.edit().putBoolean(LoginConstant.WRITE_PERMISSION, true).commit();
                    LoginConstant.HAVE_WRITE_PERMISSION = true;
                    break;
                }
            }
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == -1) {
                    hasPermissionDismiss = true;
                    break;
                }
            }
            //如果有权限没有被允许
            if (hasPermissionDismiss) {
                //跳转到系统设置权限页面，或者直接关闭页面，不让他继续访问
                mSimpleDialog = new ConfirmSimpleDialog(this, getString(R.string.insufficient_permission));
                mSimpleDialog.setRightButtonListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        System.exit(0);
                    }
                });
                mSimpleDialog.show();
            }else{
                //全部权限通过，进行EC资源的初始化
                initResource();
            }
        }
    }

    /**
     * 权限申请弹框
     */
    private void showPermissionDialog(){
//        ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, mRequestCode);
        PermissionDialog permissionDialog = new PermissionDialog(LoginActivity.this);
        permissionDialog.setOnCertainButtonClickListener(new PermissionDialog.OnCertainButtonClickListener() {
            @Override
            public void onCertainButtonClick() {
                //调用运行时权限申请框架
                ActivityCompat.requestPermissions(LoginActivity.this, permissions, mRequestCode);
            }});
        permissionDialog.show();
    }
    /********************************************权限申请部分 End ******************************************************/

    /********************************************资源初始化部分 Begin ******************************************************/
    public void initResource(){
        if (!isFrontProcess(this,FRONT_PKG))
        {
            LocContext.init(this);
            CrashUtil.getInstance().init(this);
            Log.i("SDKDemo", "onCreate: PUSH Process.");
            return;
        }

//        String appPath = getApplicationInfo().dataDir + "/lib";
        String appPath = getApplication().getApplicationInfo().nativeLibraryDir;
        ServiceMgr.getServiceMgr().startService(this, appPath, mIdoProtocol);
        Log.i("SDKDemo", "onCreate: MAIN Process.");

        LoginMgr.getInstance().regLoginEventNotification(LoginFunc.getInstance());
        CallMgr.getInstance().regCallServiceNotification(CallFunc.getInstance());
        CtdMgr.getInstance().regCtdNotification(CallFunc.getInstance());
        MeetingMgr.getInstance().regConfServiceNotification(ConfFunc.getInstance());
        EnterpriseAddressBookMgr.getInstance().registerNotification(EnterpriseAddrBookFunc.getInstance());

        ServiceMgr.getServiceMgr().securityParam(
                mSettingPresenter.getSrtpMode(),
                mSettingPresenter.getSipTransport(),
                mSettingPresenter.getAppConfig(),
                mSettingPresenter.getTunnelMode());
        ServiceMgr.getServiceMgr().networkParam(
                mSettingPresenter.getUdpPort(),
                mSettingPresenter.getTlsPort(),
                mSettingPresenter.getPriority());

        initResourceFile();
    }

    private static boolean isFrontProcess(Context context, String frontPkg)
    {
        ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningAppProcessInfo> infos = manager.getRunningAppProcesses();
        if (infos == null || infos.isEmpty())
        {
            return false;
        }

        final int pid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : infos)
        {
            if (info.pid == pid)
            {
                Log.i(UIConstants.DEMO_TAG, "processName-->"+info.processName);
                return frontPkg.equals(info.processName);
            }
        }

        return false;
    }

    private void initResourceFile()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                initDataConfRes();
            }
        }).start();
    }

    private void initDataConfRes()
    {
        String path = LocContext.getContext().getFilesDir() + "/AnnoRes";
        File file = new File(path);
        if (file.exists())
        {
            LogUtil.i(UIConstants.DEMO_TAG,  file.getAbsolutePath());
            File[] files = file.listFiles();
            if (null != files && EXPECTED_FILE_LENGTH == files.length)
            {
                return;
            }
            else
            {
                FileUtil.deleteFile(file);
            }
        }

        try
        {
            InputStream inputStream = getAssets().open("AnnoRes.zip");
            ZipUtil.unZipFile(inputStream, path);
        }
        catch (IOException e)
        {
            LogUtil.i(UIConstants.DEMO_TAG,  "close...Exception->e" + e.toString());
        }
    }
    /********************************************资源初始化部分 End ******************************************************/
}
