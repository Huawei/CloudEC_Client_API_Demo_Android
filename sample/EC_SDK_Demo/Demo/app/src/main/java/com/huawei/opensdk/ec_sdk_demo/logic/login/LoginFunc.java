package com.huawei.opensdk.ec_sdk_demo.logic.login;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.huawei.ecterminalsdk.base.TsdkContactsInfo;
import com.huawei.opensdk.commonservice.common.LocContext;
import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcastReceiver;
import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.contactservice.eaddr.EnterpriseAddressBookMgr;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.ui.IntentConstant;
import com.huawei.opensdk.ec_sdk_demo.ui.base.ActivityStack;
import com.huawei.opensdk.ec_sdk_demo.ui.login.LoginActivity;
import com.huawei.opensdk.ec_sdk_demo.util.ActivityUtil;
import com.huawei.opensdk.loginmgr.ILoginEventNotifyUI;
import com.huawei.opensdk.loginmgr.LoginConstant;
import com.huawei.opensdk.loginmgr.LoginMgr;

import java.util.List;
import java.util.concurrent.Executors;


public class LoginFunc implements ILoginEventNotifyUI, LocBroadcastReceiver
{
    private static final int LOGIN_SUCCESS = 100;
    private static final int LOGIN_FAILED = 101;
    private static final int LOGOUT = 102;
    private static final int FIREWALL_DETECT_FAILED = 103;
    private static final int BUILD_STG_FAILED = 104;

    private int mSeqNo = -1;

    private static LoginFunc INSTANCE = new LoginFunc();

    private String[] broadcastNames = new String[]{CustomBroadcastConstants.ACTION_ENTERPRISE_GET_SELF_RESULT};

    private LoginFunc()
    {
        LocBroadcast.getInstance().registerBroadcast(this, broadcastNames);
    }

    public static ILoginEventNotifyUI getInstance()
    {
        return INSTANCE;
    }

    private Handler mMainHandler = new Handler(Looper.getMainLooper())
    {
        @Override
        public void handleMessage(Message msg)
        {
            LogUtil.i(UIConstants.DEMO_TAG, "what:" + msg.what);
            parallelHandleMessage(msg);
        }
    };

    private void sendHandlerMessage(int what, Object object)
    {
        if (mMainHandler == null)
        {
            return;
        }
        Message msg = mMainHandler.obtainMessage(what, object);
        mMainHandler.sendMessage(msg);
    }

    @Override
    public void onLoginEventNotify(LoginConstant.LoginUIEvent evt, int reason, String description)
    {
        switch (evt)
        {
            case LOGIN_SUCCESS:
                LogUtil.i(UIConstants.DEMO_TAG, "login success");
                sendHandlerMessage(LOGIN_SUCCESS, description);
                break;
            case LOGIN_FAILED:
                LogUtil.i(UIConstants.DEMO_TAG, "login fail");
                sendHandlerMessage(LOGIN_FAILED, description);
                break;
            case FIREWALL_DETECT_FAILED:
                LogUtil.i(UIConstants.DEMO_TAG, "firewall detect fail");
                sendHandlerMessage(FIREWALL_DETECT_FAILED, description);
                break;
            case BUILD_STG_FAILED:
                LogUtil.i(UIConstants.DEMO_TAG, "build stg fail");
                sendHandlerMessage(BUILD_STG_FAILED, description);
                break;

            case LOGOUT:
                LogUtil.i(UIConstants.DEMO_TAG, "logout");
                sendHandlerMessage(LOGOUT, description);
                break;
            default:
                break;
        }
    }

    /**
     * handle message
     * @param msg
     */
    private void parallelHandleMessage(Message msg)
    {
        switch (msg.what)
        {
            case LOGIN_SUCCESS:
                LogUtil.i(UIConstants.DEMO_TAG, "login success,notify UI!");
                Toast.makeText(LocContext.getContext(), ((String) msg.obj), Toast.LENGTH_SHORT).show();
                ActivityUtil.startActivity(LocContext.getContext(), IntentConstant.MAIN_ACTIVITY_ACTION);
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.LOGIN_SUCCESS, null);
                //CallMgr.getInstance().addDefaultAudioRoute();
                Executors.newSingleThreadExecutor().execute(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mSeqNo = EnterpriseAddressBookMgr.getInstance().searchSelfInfo(LoginMgr.getInstance().getAccount());
                    }
                });
                break;
            case LOGIN_FAILED:
                LogUtil.i(UIConstants.DEMO_TAG, "login failed,notify UI!");
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.LOGIN_FAILED, null);
                Toast.makeText(LocContext.getContext(), ((String) msg.obj), Toast.LENGTH_SHORT).show();
                break;
            case LOGOUT:
                LogUtil.i(UIConstants.DEMO_TAG, "logout success,notify UI!");
                ActivityStack.getIns().popupAbove(LoginActivity.class);
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.LOGOUT, null);
                Toast.makeText(LocContext.getContext(), ((String) msg.obj), Toast.LENGTH_SHORT).show();
                break;
            case FIREWALL_DETECT_FAILED:
                LogUtil.i(UIConstants.DEMO_TAG, "firewall detect failed,notify UI!");
                Toast.makeText(LocContext.getContext(), ((String) msg.obj), Toast.LENGTH_SHORT).show();
                break;
            case BUILD_STG_FAILED:
                LogUtil.i(UIConstants.DEMO_TAG, "build stg failed,notify UI!");
                Toast.makeText(LocContext.getContext(), ((String) msg.obj), Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    @Override
    public void onReceive(String broadcastName, Object obj) {
        switch (broadcastName)
        {
            case CustomBroadcastConstants.ACTION_ENTERPRISE_GET_SELF_RESULT:
                List<TsdkContactsInfo> selfInfo = (List<TsdkContactsInfo>) obj;
                TsdkContactsInfo contactInfo = selfInfo.get(0);

                LoginMgr.getInstance().setSelfInfo(contactInfo);
                if (null != contactInfo.getTerminal() && !contactInfo.getTerminal().equals("")) {
                    LoginMgr.getInstance().setTerminal(contactInfo.getTerminal());
                }
                else {
                    LoginMgr.getInstance().setTerminal(contactInfo.getTerminal2());
                }

                LogUtil.i(UIConstants.DEMO_TAG, "terminal-->" + contactInfo.getTerminal());
                break;
            default:
                break;
        }
    }
}
