package com.huawei.opensdk.servicemgr;

import android.content.Context;
import android.os.Environment;

import com.huawei.ecterminalsdk.base.TsdkAppFilePathInfo;
import com.huawei.ecterminalsdk.base.TsdkAppInfoParam;
import com.huawei.ecterminalsdk.base.TsdkLogLevel;
import com.huawei.ecterminalsdk.base.TsdkLogParam;
import com.huawei.ecterminalsdk.models.TsdkManager;
import com.huawei.opensdk.commonservice.common.LocContext;
import com.huawei.opensdk.commonservice.util.CrashUtil;
import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.imservice.ImMgr;

import java.io.File;

import static com.huawei.ecterminalsdk.base.TsdkClientType.TSDK_E_CLIENT_MOBILE;

/**
 * This class is about init and uninit business component classes.
 * 初始化与去初始化业务组件类
 */
public class ServiceMgr
{

    private static final String TAG = ServiceMgr.class.getSimpleName();

    /**
     * Instance object of ServiceMgr component.
     * 新建一个ServiceMgr对象
     */
    private static final ServiceMgr serviceMgr = new ServiceMgr();

    /**
     * The context
     * 上下文
     */
    private Context context;

    /**
     * The app path
     * APP路径
     */
    private String appPath;


    /**
     *
     *
     */
    private TsdkManager tsdkManager;

    private static final String CONTACT_FILE_PATH = Environment.getExternalStorageDirectory() + File.separator + "tupcontact";

    /* 应用程序根据自身业务支持情况进行设置 */
    private int appType = 0;
    private boolean isSupportAudioAndVideoCall = true;
    private boolean isSupportAudioAndVideoConf = true;
    private boolean isSupportDataConf = true;
    private boolean isSupportCTD = true;
    private boolean isSupportIM = true;
    private boolean isSupportRichMediaMessage = true;
    private boolean isSupportAddressbook = true;

    /**
     * This method is used to get instance object of ServiceMgr.
     * 获取ServiceMgr对象实例
     * @return ImMgr Return instance object of ServiceMgr
     *               返回一个ServiceMgr对象实例
     */
    public static ServiceMgr getServiceMgr()
    {
        return serviceMgr;
    }

    /**
     * This method is used to init service.
     * 初始化业务组件
     * @param context
     * @param appPath
     * @return
     */
    public boolean startService(Context context, String appPath)
    {
        int ret;

        /*init crash util*/
        CrashUtil.getInstance().init(context);

        /*set demo log path */
        LogUtil.setLogPath("ECSDKDemo");

        LocContext.init(context);

        LogUtil.i(TAG, "sdk init is begin.");


        tsdkManager = TsdkManager.getInstance(context, appPath, ServiceNotify.getInstance());

        /* Step 1, set log param */
        TsdkLogParam logParam = new TsdkLogParam();
        logParam.setFileCount(1);
        logParam.setLevel(TsdkLogLevel.TSDK_E_LOG_DEBUG);
        logParam.setMaxSizeKb(1024 * 4);
        logParam.setPath(Environment.getExternalStorageDirectory() + File.separator + "ECSDKDemo" + "/");

        ret = tsdkManager.setConfigParam(logParam);
        if (ret != 0) {
            LogUtil.e(TAG, "set log failed." + ret);
            return false;
        }

        //企业通讯录配置
        TsdkAppFilePathInfo appFilePathInfo = new TsdkAppFilePathInfo();
        File files = new File(CONTACT_FILE_PATH + File.separator + "dept" + File.separator);
        if (!files.exists())
        {
            files.mkdirs();
        }
        appFilePathInfo.setDeptFilePath(CONTACT_FILE_PATH + File.separator + "dept" + File.separator);
        File file = new File(CONTACT_FILE_PATH + File.separator + "icon" + File.separator);
        if (!file.exists())
        {
            file.mkdirs();
        }
        appFilePathInfo.setIconFilePath(CONTACT_FILE_PATH + File.separator + "icon" + File.separator);
        ret = tsdkManager.setConfigParam(appFilePathInfo);

        if (ret != 0)
        {
            LogUtil.e(TAG, "config file path failed." + ret);
            return false;
        }

        /* Step 2, init sdk */
        TsdkAppInfoParam appInfoParam = new TsdkAppInfoParam();
        appInfoParam.setClientType(TSDK_E_CLIENT_MOBILE);
        //appInfoParam.setProductName("Huawei TE Mobile");
        //appInfoParam.setProductName("SoftClient On Mobile");
        appInfoParam.setProductName("WeLink-Mobile");
        appInfoParam.setDeviceSn("123");
        appInfoParam.setSupportAudioAndVideoCall(this.isSupportAudioAndVideoCall?1:0);
        appInfoParam.setSupportAudioAndVideoConf(this.isSupportAudioAndVideoConf?1:0);
        appInfoParam.setSupportDataConf(this.isSupportDataConf?1:0);
        appInfoParam.setSupportCtd(this.isSupportCTD?1:0);
        appInfoParam.setSupportEnterpriseAddressBook(this.isSupportAddressbook?1:0);
        appInfoParam.setSupportIm(this.isSupportIM?1:0);
        appInfoParam.setSupportRichMediaMessage(0);

        ret = tsdkManager.init(appInfoParam);
        if (ret != 0)
        {
            LogUtil.e(TAG, "Terminal SDK init failed." + ret);
            return false;
        }

        /*Step 3, config service param */
        ret = configServiceParam();
        if (ret != 0)
        {
            LogUtil.e(TAG, "config service param failed, return " + ret);
            return false;
        }
        LogUtil.i(TAG, "config service param is success.");

        //IM init
        if (isSupportIM)
        {
            ImMgr.getInstance().sdkInit(context, appPath);
        }
        return true;

    }

    /**
     * This method is used to uninit service.
     * 去初始化基础组件
     */
    public void stopService()
    {
        tsdkManager.uninit();
    }

    /**
     * This method is used to config service param.
     * 各种服务的配置
     * @return
     */
    private int configServiceParam()
    {
        //待实现，配置各种业务的基本配置
        //TODO

        return 0;
    }
}

