package com.huawei.opensdk.ec_sdk_demo.ui;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
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
import com.huawei.opensdk.contactservice.eaddr.EntAddressBookIconInfo;
import com.huawei.opensdk.contactservice.eaddr.EnterpriseAddressBookMgr;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.module.headphoto.ContactHeadFetcher;
import com.huawei.opensdk.ec_sdk_demo.ui.base.BaseActivity;
import com.huawei.opensdk.ec_sdk_demo.ui.call.CallFragment;
import com.huawei.opensdk.ec_sdk_demo.ui.contact.ContactFragment;
import com.huawei.opensdk.ec_sdk_demo.ui.discover.DiscoverFragment;
import com.huawei.opensdk.ec_sdk_demo.ui.eaddrbook.EnterpriseAddrTools;
import com.huawei.opensdk.ec_sdk_demo.ui.im.RecentFragment;
import com.huawei.opensdk.ec_sdk_demo.util.ActivityUtil;
import com.huawei.opensdk.ec_sdk_demo.widget.BaseDialog;
import com.huawei.opensdk.ec_sdk_demo.widget.ConfirmDialog;
import com.huawei.opensdk.imservice.ImConstant;
import com.huawei.opensdk.imservice.ImMgr;
import com.huawei.opensdk.loginmgr.LoginMgr;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener, LocBroadcastReceiver
{
    private ImageView mRecentTab;
    private ImageView mCallTab;
    private ImageView mContactTab;
    private ImageView mDiscoverTab;
    private ViewPager mViewPager;
    private List<ImageView> mMainTabs = new ArrayList<>();
    private int mCurrentPosition;
    private RecentFragment mRecentFragment;
    private CallFragment mCallFragment;
    private DiscoverFragment mDiscoverFragment;
    private ContactFragment mContactFragment;
    private final List<Fragment> fragments = new ArrayList<>();
    private ImageView mDrawerBtn;
    private DrawerLayout mDrawerLayout;

    private TextView displayName;
    private TextView sipNumber;
    private BaseDialog mLogoutDialog;
    private ImageView mSearchBtn;
    private ImageView mHeadIv;
    private ImageView mStatusIv;
    private String[] mActions = new String[]{CustomBroadcastConstants.ACTION_SET_STATUS,
            CustomBroadcastConstants.ACTION_IM_LOGIN_SUCCESS,
            CustomBroadcastConstants.ACTION_IM_SET_HEAD_PHOTO,
            CustomBroadcastConstants.ACTION_ENTERPRISE_GET_HEAD_PHOTO_FAILED,
            CustomBroadcastConstants.ACTION_ENTERPRISE_GET_SELF_PHOTO_RESULT,
            CustomBroadcastConstants.ACTION_ENTERPRISE_GET_SELF_RESULT
    };
    private String mMyAccount;
    private ContactHeadFetcher contactHeadFetcher;
    private ImConstant.ImStatus mImStatus;

    private String mIconPath;
    private int mIconId;
    private static int[] mSystemIcon = EnterpriseAddrTools.getSystemIcon();

    @Override
    public void initializeComposition()
    {
        setContentView(R.layout.activity_main);
        mHeadIv = (ImageView) findViewById(R.id.blog_head_iv);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mRecentTab = (ImageView) findViewById(R.id.recent_tab);
        mCallTab = (ImageView) findViewById(R.id.call_tab);
        mContactTab = (ImageView) findViewById(R.id.contact_tab);
        mDiscoverTab = (ImageView) findViewById(R.id.discover_tab);
        mDrawerBtn = (ImageView) findViewById(R.id.nav_iv);
        mStatusIv = (ImageView) findViewById(R.id.blog_state_iv);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mSearchBtn = (ImageView) findViewById(R.id.right_img);
        LinearLayout logoutButton = (LinearLayout) findViewById(R.id.logout_btn);

        LinearLayout settingButton = (LinearLayout) findViewById(R.id.iv_setting);

        displayName = (TextView) findViewById(R.id.blog_name_tv);
        sipNumber = (TextView) findViewById(R.id.blog_number_tv);


        initIndicator();
        initViewPager();
//        initDrawerShow();

        settingButton.setOnClickListener(this);
        logoutButton.setOnClickListener(this);
        mDrawerBtn.setOnClickListener(this);
        mHeadIv.setOnClickListener(this);
        mSearchBtn.setOnClickListener(this);

        LocBroadcast.getInstance().registerBroadcast(this, mActions);
        updateStatus();
        updateHeadPhoto();
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
        if (mRecentFragment == null)
        {
            mRecentFragment = new RecentFragment();
        }

        if (mCallFragment == null)
        {
            mCallFragment = new CallFragment();
        }

        if (mContactFragment == null)
        {
            mContactFragment = new ContactFragment();
        }

        if (mDiscoverFragment == null)
        {
            mDiscoverFragment = new DiscoverFragment();
        }

        fragments.clear();
        fragments.add(mRecentFragment);
        fragments.add(mCallFragment);
        fragments.add(mContactFragment);
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
//        mMyAccount = LoginCenter.getInstance().getAccount();
        mMyAccount = LoginMgr.getInstance().getAccount();
        contactHeadFetcher = new ContactHeadFetcher(this);
        mImStatus = ImMgr.getInstance().getStatus();
    }

    private void initIndicator()
    {
        mMainTabs.add(mRecentTab);
        mMainTabs.add(mCallTab);
        mMainTabs.add(mContactTab);
        mMainTabs.add(mDiscoverTab);

        mRecentTab.setSelected(true);

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
                ActivityUtil.startActivity(MainActivity.this, IntentConstant.SETTING_MORE_ACTIVITY_ACTION);
                break;
            case R.id.logout_btn:
                showLogoutDialog();
                break;
            case R.id.nav_iv:
                EnterpriseAddressBookMgr.getInstance().searchSelfInfo(mMyAccount);
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
        updateStatus();
        mHeadIv.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                updateHeadPhoto();
            }
        }, 1500);
        super.onResume();
    }

    @Override
    public void onBackPressed()
    {
        Log.i(UIConstants.DEMO_TAG, "on back pressed , logout!");
        showLogoutDialog();
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
        if (null != mLogoutDialog)
        {
            mLogoutDialog.dismiss();
        }
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
                            mHeadIv.setImageResource(mSystemIcon[mIconId]);
                        }
                        else
                        {
                            mIconPath = Environment.getExternalStorageDirectory() + File.separator + "tupcontact" + File.separator + "icon" + File.separator + defIcon;
                            Bitmap headIcon = EnterpriseAddrTools.getBitmapByPath(mIconPath);
                            mHeadIv.setImageBitmap(headIcon);
                        }
                    }
                    break;
                case UIConstants.ENTERPRISE_HEAD_NULL:
                    mHeadIv.setBackgroundResource(R.drawable.default_head_local);
                    break;
                case UIConstants.ENTERPRISE_SELF_TERMINAL:
                    initDrawerShow();
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
            case CustomBroadcastConstants.ACTION_SET_STATUS:
//                updateStatus();
                break;
            case CustomBroadcastConstants.ACTION_IM_LOGIN_SUCCESS:
                updateStatus();
                break;
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
            default:
                break;
        }
    }

    private void updateHeadPhoto()
    {
        EnterpriseAddressBookMgr.getInstance().getSelfIcon(mMyAccount);
    }

    private void updateStatus()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                mImStatus = ImMgr.getInstance().getStatus();
                mStatusIv.setImageResource(getStatusResource(mImStatus));
            }
        });
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
}
