package com.huawei.opensdk.ec_sdk_demo.ui;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcastReceiver;
import com.huawei.opensdk.commonservice.util.DeviceManager;
import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.contactservice.eaddr.EntAddressBookIconInfo;
import com.huawei.opensdk.contactservice.eaddr.EnterpriseAddressBookMgr;
import com.huawei.opensdk.ec_sdk_demo.ECApplication;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.floatView.screenShare.rom.RomUtils;
import com.huawei.opensdk.ec_sdk_demo.module.headphoto.HeadIconTools;
import com.huawei.opensdk.ec_sdk_demo.ui.base.ActivityStack;
import com.huawei.opensdk.ec_sdk_demo.ui.base.BaseActivity;
import com.huawei.opensdk.ec_sdk_demo.ui.base.NetworkConnectivityListener;
import com.huawei.opensdk.ec_sdk_demo.ui.call.CallFragment;
import com.huawei.opensdk.ec_sdk_demo.ui.discover.DiscoverFragment;
import com.huawei.opensdk.ec_sdk_demo.ui.login.LoginActivity;
import com.huawei.opensdk.ec_sdk_demo.util.ActivityUtil;
import com.huawei.opensdk.ec_sdk_demo.widget.BaseDialog;
import com.huawei.opensdk.ec_sdk_demo.widget.CircleView;
import com.huawei.opensdk.ec_sdk_demo.widget.ConfirmDialog;
import com.huawei.opensdk.loginmgr.LoginConstant;
import com.huawei.opensdk.loginmgr.LoginMgr;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener, LocBroadcastReceiver, NetworkConnectivityListener.OnNetWorkListener
{
    private ImageView mCallTab;
    private ImageView mDiscoverTab;
    private ViewPager mViewPager;
    private List<ImageView> mMainTabs = new ArrayList<>();
    private int mCurrentPosition;
    private CallFragment mCallFragment;
    private DiscoverFragment mDiscoverFragment;
    private final List<Fragment> fragments = new ArrayList<>();
    private ImageView mDrawerBtn;
    private DrawerLayout mDrawerLayout;

    private TextView displayName;
    private TextView sipNumber;
    private BaseDialog mLogoutDialog;
    private ImageView mSearchBtn;
    private CircleView mCircleView;
    private LinearLayout mNetworkStatusLL;
    private ImageView mNetworkAlarmIV;
    private TextView mNetworkStatusTV;
    private Bitmap mBitmap;

    private String[] mActions = new String[]{CustomBroadcastConstants.ACTION_IM_SET_HEAD_PHOTO,
            CustomBroadcastConstants.ACTION_ENTERPRISE_GET_HEAD_PHOTO_FAILED,
            CustomBroadcastConstants.ACTION_ENTERPRISE_GET_SELF_PHOTO_RESULT,
            CustomBroadcastConstants.ACTION_ENTERPRISE_GET_SELF_RESULT,
            CustomBroadcastConstants.LOGIN_STATUS_RESUME_IND,
            CustomBroadcastConstants.LOGIN_STATUS_RESUME_RESULT,
            CustomBroadcastConstants.AUTH_FAILED,
            CustomBroadcastConstants.LOGIN_SUCCESS
    };
    private String mMyAccount;
    private String mIconPath;
    private int mIconId;
    private NetworkConnectivityListener mNetworkConnectivityListener = new NetworkConnectivityListener();
    private boolean mIsResuming = false;
    private String mCurrentWifiInfo = "";
    private boolean mIsFocus = false;

    @Override
    public void initializeComposition()
    {
        setContentView(R.layout.activity_main);
        mCircleView = (CircleView) findViewById(R.id.blog_head_iv);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mCallTab = (ImageView) findViewById(R.id.call_tab);
        mDiscoverTab = (ImageView) findViewById(R.id.discover_tab);
        mDrawerBtn = (ImageView) findViewById(R.id.nav_iv);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mSearchBtn = (ImageView) findViewById(R.id.right_img);
        LinearLayout logoutButton = (LinearLayout) findViewById(R.id.logout_btn);

        LinearLayout settingButton = (LinearLayout) findViewById(R.id.iv_setting);

        displayName = (TextView) findViewById(R.id.blog_name_tv);
        sipNumber = (TextView) findViewById(R.id.blog_number_tv);

        mNetworkStatusLL = (LinearLayout) findViewById(R.id.login_resume_status_ll);
        mNetworkAlarmIV = (ImageView) findViewById(R.id.network_alarm_iv);
        mNetworkStatusTV = (TextView) findViewById(R.id.login_resume_text);

        initIndicator();
        initViewPager();

        settingButton.setOnClickListener(this);
        logoutButton.setOnClickListener(this);
        mDrawerBtn.setOnClickListener(this);
        mSearchBtn.setOnClickListener(this);

        LocBroadcast.getInstance().registerBroadcast(this, mActions);
        mNetworkConnectivityListener.registerListener(this);
        mNetworkConnectivityListener.startListening(this);
    }

    private void initDrawerShow()
    {
        String name = LoginMgr.getInstance().getAccount();
        String number = LoginMgr.getInstance().getTerminal();

        displayName.setText(name);
        sipNumber.setText(number);
    }

    private void initViewPager()
    {
        if (mCallFragment == null)
        {
            mCallFragment = new CallFragment();
        }

        if (mDiscoverFragment == null)
        {
            mDiscoverFragment = new DiscoverFragment();
        }

        fragments.clear();
        fragments.add(mCallFragment);
        fragments.add(mDiscoverFragment);

        FragmentAdapter adapter = new FragmentAdapter(getFragmentManager());
        adapter.setData(fragments);
        mViewPager.setAdapter(adapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float offset, int offsetPixels)
            {
                mCurrentPosition = position;
            }

            @Override
            public void onPageSelected(int position)
            {
                setTabSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int i)
            {
            }
        });
    }

    private void setTabSelected(int position)
    {
        for (int i = 0; i < mMainTabs.size(); i++)
        {
            mMainTabs.get(i).setSelected(position == i);
        }
    }

    @Override
    public void initializeData()
    {
        mMyAccount = LoginMgr.getInstance().getAccount();
    }

    private void initIndicator()
    {
        mMainTabs.add(mCallTab);
        mMainTabs.add(mDiscoverTab);

        mCallTab.setSelected(true);

        for (int i = 0; i < mMainTabs.size(); i++)
        {
            final ImageView tab = mMainTabs.get(i);

            tab.setOnTouchListener(new View.OnTouchListener()
            {
                @Override
                public boolean onTouch(View v, MotionEvent event)
                {
                    if (event.getAction() == MotionEvent.ACTION_UP)
                    {
                        mCurrentPosition = Integer.parseInt((String) v.getTag());
                        mViewPager.setCurrentItem(mCurrentPosition);
                    }
                    return true;
                }
            });
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.blog_head_iv:
                ActivityUtil.startActivity(MainActivity.this, IntentConstant.EADDR_SET_ICON_ACTIVITY_ACTION);
                break;
            case R.id.logout_btn:
                showLogoutDialog();
                break;
            case R.id.nav_iv:
                initDrawerShow();
                mDrawerLayout.openDrawer(Gravity.LEFT);
                break;
            case R.id.iv_setting:
                ActivityUtil.startActivity(MainActivity.this, IntentConstant.SERVICE_SETTING_ACTIVITY_ACTION);
                break;
            case R.id.right_img:
                ActivityUtil.startActivity(MainActivity.this, IntentConstant.EADDR_BOOK_ACTIVITY_ACTION);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume()
    {
        initDrawerShow();
        refreshNetworkStatus(LoginMgr.getInstance().getConnectedStatus());
        super.onResume();
    }

    @Override
    public void onBackPressed()
    {
        Log.i(UIConstants.DEMO_TAG, "on back pressed , logout!");
        showLogoutDialog();
    }

    private void refreshNetworkStatus(int status)
    {
        switch (status)
        {
            case LoginConstant.NO_NETWORK:
                mNetworkStatusLL.setVisibility(View.VISIBLE);
                mNetworkAlarmIV.setVisibility(View.VISIBLE);
                mNetworkStatusTV.setText(R.string.connect_error);
                break;
            case LoginConstant.NETWORK_CONNECTED:
                mNetworkStatusLL.setVisibility(View.VISIBLE);
                mNetworkAlarmIV.setVisibility(View.INVISIBLE);
                mNetworkStatusTV.setText(R.string.connect_progress);
                break;
            case LoginConstant.NETWORK_CONNECTED_FAILED:
                mNetworkStatusLL.setVisibility(View.VISIBLE);
                mNetworkAlarmIV.setVisibility(View.VISIBLE);
                mNetworkStatusTV.setText(R.string.login_resume_failed);
                break;
            case LoginConstant.NETWORK_CONNECTED_SUCCESS:
                mNetworkStatusLL.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    private void showLogoutDialog()
    {
        if (null == mLogoutDialog)
        {
            mLogoutDialog = new ConfirmDialog(this,R.string.sure_logout);
            mLogoutDialog.setRightButtonListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Log.i(UIConstants.DEMO_TAG, "logout");
                    LoginMgr.getInstance().logout();
                    ActivityStack.getIns().popupAbove(LoginActivity.class);
                }
            });
        }
        mLogoutDialog.show();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        LocBroadcast.getInstance().unRegisterBroadcast(this, mActions);
        mNetworkConnectivityListener.stopListening();
        mNetworkConnectivityListener.deregisterListener(this);
        if (null != mLogoutDialog)
        {
            mLogoutDialog.dismiss();
        }
    }

    private boolean isLoginSuccess()
    {
        return LoginMgr.getInstance().isLoginSuccess();
    }

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case UIConstants.ENTERPRISE_HEAD_SELF:
                    if (msg.obj instanceof EntAddressBookIconInfo)
                    {
                        EntAddressBookIconInfo iconInfo = (EntAddressBookIconInfo) msg.obj;
                        String defIcon = iconInfo.getIconFile();
                        if (defIcon.isEmpty())
                        {
                            mIconId = iconInfo.getIconId();
                            mBitmap = HeadIconTools.getBitmapByIconId(mIconId);
                        }
                        else
                        {
                            mIconPath = Environment.getExternalStorageDirectory() + File.separator + "ECSDKDemo" + File.separator + "icon" + File.separator + defIcon;
                            mBitmap = HeadIconTools.getBitmapByPath(mIconPath);
                        }
                    }
                    showMyHeadPhone(mBitmap);
                    break;
                case UIConstants.ENTERPRISE_HEAD_NULL:
                    mBitmap = HeadIconTools.getBitmapByIconId(10);
                    showMyHeadPhone(mBitmap);
                    break;
                case UIConstants.ENTERPRISE_SELF_TERMINAL:
                    initDrawerShow();
                    break;
                case UIConstants.LOGIN_RESUME_IND:
                    mIsResuming = true;
                    refreshNetworkStatus(LoginConstant.NETWORK_CONNECTED);
                    break;
                case UIConstants.LOGIN_RESUME_RESULT:
                    mIsResuming = false;
                    if (0 == (int) msg.obj)
                    {
                        refreshNetworkStatus(LoginConstant.NETWORK_CONNECTED_SUCCESS);
                    }
                    else
                    {
                        refreshNetworkStatus(LoginConstant.NETWORK_CONNECTED_FAILED);
                    }
                    break;
                case UIConstants.LOGIN_FAILED:
                    refreshNetworkStatus(LoginConstant.NETWORK_CONNECTED_FAILED);
                    break;
                case UIConstants.LOGIN_SUCCESS:
                    refreshNetworkStatus(LoginConstant.NETWORK_CONNECTED_SUCCESS);
                    break;
                default:
                    break;

            }
        }
    };

    @Override
    public void onReceive(String broadcastName, Object obj)
    {
        switch (broadcastName)
        {
            case CustomBroadcastConstants.ACTION_ENTERPRISE_GET_SELF_PHOTO_RESULT:
                Message msgSelf = handler.obtainMessage(UIConstants.ENTERPRISE_HEAD_SELF, obj);
                handler.sendMessage(msgSelf);
                break;
            case CustomBroadcastConstants.ACTION_ENTERPRISE_GET_HEAD_PHOTO_FAILED:
                Message msgFailed = handler.obtainMessage(UIConstants.ENTERPRISE_HEAD_NULL, obj);
                handler.sendMessage(msgFailed);
                break;
            case CustomBroadcastConstants.ACTION_ENTERPRISE_GET_SELF_RESULT:
                handler.sendEmptyMessage(UIConstants.ENTERPRISE_SELF_TERMINAL);
                break;
            case CustomBroadcastConstants.LOGIN_STATUS_RESUME_IND:
                handler.sendEmptyMessage(UIConstants.LOGIN_RESUME_IND);
                break;
            case CustomBroadcastConstants.LOGIN_STATUS_RESUME_RESULT:
                Message resumeResult = handler.obtainMessage(UIConstants.LOGIN_RESUME_RESULT, obj);
                handler.sendMessage(resumeResult);
                break;
            case CustomBroadcastConstants.AUTH_FAILED:
                handler.sendEmptyMessage(UIConstants.LOGIN_FAILED);
                break;
            case CustomBroadcastConstants.LOGIN_SUCCESS:
                handler.sendEmptyMessage(UIConstants.LOGIN_SUCCESS);
                break;
            default:
                break;
        }
    }

    private void updateHeadPhoto()
    {
        if (isLoginSuccess())
        {
            EnterpriseAddressBookMgr.getInstance().getSelfIcon(mMyAccount);
        }
    }

    private String getConnectWifiSSID()
    {
        String ssid = "";
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O || Build.VERSION.SDK_INT == Build.VERSION_CODES.P)
        {
            WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (null != wifiManager)
            {
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
                {
                    ssid = wifiInfo.getSSID();
                }
                else
                {
                    if (RomUtils.checkIsHuaweiRom() && Build.VERSION.SDK_INT == Build.VERSION_CODES.P)
                    {
                        // 9.0华为手机无法获取解决方案
                        int networkId = wifiInfo.getNetworkId();
                        List<WifiConfiguration> configurationList = wifiManager.getConfiguredNetworks();
                        for (WifiConfiguration configuration : configurationList)
                        {
                            if (networkId == configuration.networkId)
                            {
                                ssid = configuration.SSID;
                            }
                        }
                    }
                    else
                    {
                        ssid = wifiInfo.getSSID().replace("\"", "");
                    }
                }
            }
        }
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O_MR1)
        {
            ConnectivityManager connectivityManager = (ConnectivityManager) this.getApplicationContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (null != connectivityManager)
            {
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo.isConnected())
                {
                    if (null != networkInfo)
                    {
                        ssid = networkInfo.getExtraInfo().replace("\"", "");
                    }
                }
            }
        }
        Log.i("onNetWorkChange", ssid + "" + " Build.VERSION.SDK_INT->" + Build.VERSION.SDK_INT);
        return ssid;
    }

    private void showMyHeadPhone(final Bitmap bitmap)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCircleView.setBitmapParams(bitmap);
                mCircleView.invalidate();
            }
        });
    }

    @Override
    public void onNetWorkChange(JSONObject nwd) {
        ECApplication.setLastInfo(nwd);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                JSONObject networkInfo = ECApplication.getLastInfo();
                LogUtil.i("onNetWorkChange","main networkInfo: " + networkInfo);
                try {
                    // 将type通知给js type: (0: none 1: Wi-Fi 2: 2G 3: 3G 4: 4G)
                    int type = DeviceManager.getNetworkEnumByStr(networkInfo.getString("type"));
                    if (0 == type)
                    {
                        LoginMgr.getInstance().setConnectedStatus(LoginConstant.NO_NETWORK);
                        refreshNetworkStatus(LoginConstant.NO_NETWORK);
                    }
                    else
                    {
                        // 获取连接状态的WiFi名称
                        String wifiSSID = getConnectWifiSSID();
                        if (!TextUtils.isEmpty(wifiSSID))
                        {
                            if (!mCurrentWifiInfo.equals(wifiSSID))
                            {
                                mCurrentWifiInfo = wifiSSID;
                                mIsFocus = true;
                            }
                        }
                    }

                    // 如果收到重新登录通知还没有收到重新登录结果则不进行配置本地ip
                    if (!mIsResuming)
                    {
                        LoginMgr.getInstance().resetConfig(false, mIsFocus);
                        mIsFocus = false;
                    }
                } catch (JSONException e) {
                    LogUtil.e("onNetWorkChange","JSONException: " + e.toString());
                }
            }
        });
    }
}
