package com.huawei.opensdk.ec_sdk_demo.logic.login;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.widget.Toast;

import com.huawei.ecterminalsdk.base.TsdkContactsInfo;
import com.huawei.ecterminalsdk.base.TsdkLoginSuccessInfo;
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
import com.huawei.opensdk.servicemgr.ServiceMgr;

import java.util.List;
import java.util.concurrent.Executors;


public class LoginFunc implements ILoginEventNotifyUI, LocBroadcastReceiver
{
    private static final int VOIP_LOGIN_SUCCESS = 100;
    private static final int LOGIN_FAILED = 102;
    private static final int LOGOUT = 103;
    private static final int FIREWALL_DETECT_FAILED = 104;
    private static final int BUILD_STG_FAILED = 105;
    private static final int MODIFY_PWD_SUCCESS = 106;
    private static final int MODIFY_PWD_FAILED = 107;
    private static final int FIRST_LOGIN = 108;
    private static final int PWD_REMAIN_DAYS = 109;

    private static LoginFunc INSTANCE = new LoginFunc();

    /**
     * 是否是在登录进入主界面后，(切换网络)(断网重连)重新登录成功
     */
    private boolean isResumeAfterLogin = false;

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
            case VOIP_LOGIN_SUCCESS:
                LogUtil.i(UIConstants.DEMO_TAG, "voip login success");
                ServiceMgr.getServiceMgr().setDisplayLocalInfo(LoginMgr.getInstance().getTerminal());
                sendHandlerMessage(VOIP_LOGIN_SUCCESS, description);
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
            case MODIFY_PASSWORD:
                LogUtil.i(UIConstants.DEMO_TAG, "modify password result");
                if (0 == reason)
                {
                    sendHandlerMessage(MODIFY_PWD_SUCCESS, description);
                    return;
                }
                sendHandlerMessage(MODIFY_PWD_FAILED, description);
                break;
            case RESUME_IND:
                LogUtil.i(UIConstants.DEMO_TAG, "login status resume");
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.LOGIN_STATUS_RESUME_IND, null);
                break;
            case RESUME_RESULT:
                LogUtil.i(UIConstants.DEMO_TAG, "login status resume result");
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.LOGIN_STATUS_RESUME_RESULT, reason);
                break;

            case LOGOUT:
                LogUtil.i(UIConstants.DEMO_TAG, "logout");
                sendHandlerMessage(LOGOUT, description);
                break;
            default:
                break;
        }
    }

    @Override
    public void onPwdInfoEventNotify(LoginConstant.LoginUIEvent evt, final Object object) {
        if (LoginConstant.LoginUIEvent.PASSWORD_INFO == evt)
        {
            if (!(object instanceof TsdkLoginSuccessInfo))
            {
                return;
            }
            final TsdkLoginSuccessInfo loginSuccessInfo = (TsdkLoginSuccessInfo) object;

            mMainHandler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (1 == loginSuccessInfo.getIsFirstLogin())
                    {
                        sendHandlerMessage(FIRST_LOGIN, "First login, it is recommended to change the password.");
                    }
                    else if (loginSuccessInfo.getLeftDaysOfPassword() <= 3)
                    {
                        sendHandlerMessage(PWD_REMAIN_DAYS, "Password is less than " + loginSuccessInfo.getLeftDaysOfPassword()
                                +  " days, please change it in time");
                    }
                }
            }, 1000);
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
            case VOIP_LOGIN_SUCCESS:
                LogUtil.i(UIConstants.DEMO_TAG, "voip login success,notify UI!");
                String currentActivity = ActivityUtil.getCurrentActivity(LocContext.getContext());
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.LOGIN_SUCCESS, null);
                if ("LoginActivity".equals(currentActivity) || "AnonymousJoinConfActivity".equals(currentActivity))
                {
                    isResumeAfterLogin = false;
                }
                else
                {
                    isResumeAfterLogin = true;
                }
                if (!isResumeAfterLogin)
                {
                    Toast.makeText(LocContext.getContext(), ((String) msg.obj), Toast.LENGTH_SHORT).show();
                    ActivityUtil.startActivity(LocContext.getContext(), IntentConstant.MAIN_ACTIVITY_ACTION);
                //CallMgr.getInstance().addDefaultAudioRoute();
                    Executors.newSingleThreadExecutor().execute(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            EnterpriseAddressBookMgr.getInstance().searchSelfInfo(LoginMgr.getInstance().getAccount());
                        }
                    });
                }
                break;
            case LOGIN_FAILED:
                LogUtil.i(UIConstants.DEMO_TAG, "login failed,notify UI!");
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.LOGIN_FAILED, null);
                Toast.makeText(LocContext.getContext(), ((String) msg.obj), Toast.LENGTH_SHORT).show();
                break;
            case LOGOUT:
                LogUtil.i(UIConstants.DEMO_TAG, "logout success,notify UI!");
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.LOGOUT, null);
                if (!isResumeAfterLogin)
                {
                    ActivityStack.getIns().popupAbove(LoginActivity.class);
                    Toast.makeText(LocContext.getContext(), ((String) msg.obj), Toast.LENGTH_SHORT).show();
                }
                break;
            case FIREWALL_DETECT_FAILED:
                LogUtil.i(UIConstants.DEMO_TAG, "firewall detect failed,notify UI!");
                Toast.makeText(LocContext.getContext(), ((String) msg.obj), Toast.LENGTH_SHORT).show();
                break;
            case BUILD_STG_FAILED:
                LogUtil.i(UIConstants.DEMO_TAG, "build stg failed,notify UI!");
                Toast.makeText(LocContext.getContext(), ((String) msg.obj), Toast.LENGTH_SHORT).show();
                break;
            case MODIFY_PWD_SUCCESS:
                LogUtil.i(UIConstants.DEMO_TAG, "modify password success,notify UI!");
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.MODIFY_PWD_SUCCESS, null);
                break;
            case MODIFY_PWD_FAILED:
                LogUtil.i(UIConstants.DEMO_TAG, "modify password failed,notify UI!");
                Toast.makeText(LocContext.getContext(), ((String) msg.obj), Toast.LENGTH_SHORT).show();
                break;
            case FIRST_LOGIN:
                LogUtil.i(UIConstants.DEMO_TAG, "first login,notify UI!");
                if (!isResumeAfterLogin)
                {
                    Toast toast = Toast.makeText(LocContext.getContext(), ((String) msg.obj), Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                break;
            case PWD_REMAIN_DAYS:
                LogUtil.i(UIConstants.DEMO_TAG, "password remain days,notify UI!");
                if (!isResumeAfterLogin)
                {
                    Toast pwdToast = Toast.makeText(LocContext.getContext(), ((String) msg.obj), Toast.LENGTH_LONG);
                    pwdToast.setGravity(Gravity.CENTER, 0, 0);
                    pwdToast.show();
                }
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
                break;
            default:
                break;
        }
    }
}
