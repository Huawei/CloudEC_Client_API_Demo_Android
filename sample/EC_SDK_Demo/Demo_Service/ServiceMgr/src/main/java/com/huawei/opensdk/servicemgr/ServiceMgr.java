package com.huawei.opensdk.servicemgr;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.huawei.ecterminalsdk.base.TsdkAppFilePathInfo;
import com.huawei.ecterminalsdk.base.TsdkAppInfoParam;
import com.huawei.ecterminalsdk.base.TsdkAvcCapsLevel;
import com.huawei.ecterminalsdk.base.TsdkAvcCapsLevelInfo;
import com.huawei.ecterminalsdk.base.TsdkConfCtrlParam;
import com.huawei.ecterminalsdk.base.TsdkConfCtrlProtocol;
import com.huawei.ecterminalsdk.base.TsdkDisplayLocalInfo;
import com.huawei.ecterminalsdk.base.TsdkLogLevel;
import com.huawei.ecterminalsdk.base.TsdkLogParam;
import com.huawei.ecterminalsdk.base.TsdkMediaSrtpMode;
import com.huawei.ecterminalsdk.base.TsdkNetworkInfoParam;
import com.huawei.ecterminalsdk.base.TsdkSecurityTunnelMode;
import com.huawei.ecterminalsdk.base.TsdkServiceSecurityParam;
import com.huawei.ecterminalsdk.base.TsdkSipTransportMode;
import com.huawei.ecterminalsdk.models.TsdkManager;
import com.huawei.opensdk.commonservice.common.LocContext;
import com.huawei.opensdk.commonservice.util.CrashUtil;
import com.huawei.opensdk.commonservice.util.LogUtil;

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
     * The coding ability level
     * AVC视频编码能力级别
     */
    private int videoEncodeLeave = 1;

    /**
     * The decoding capability level
     * AVC视频解码能力级别
     */
    private int videoDecodeLeave = 1;

    /**
     *
     *
     */
    private TsdkManager tsdkManager;

    private static final String CONTACT_FILE_PATH = Environment.getExternalStorageDirectory() + File.separator + "ECSDKDemo";

    /* 应用程序根据自身业务支持情况进行设置 */
    private boolean isSupportAudioAndVideoCall = true;
    private boolean isSupportAudioAndVideoConf = true;
    private boolean isSupportDataConf = true;
    private boolean isSupportCTD = true;
    private boolean isSupportIM = false;
    private boolean isSupportRichMediaMessage = false;
    private boolean isSupportAddressbook = true;

    public int getVideoEncodeLeave() {
        return videoEncodeLeave;
    }

    public int getVideoDecodeLeave() {
        return videoDecodeLeave;
    }

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
    public boolean startService(Context context, String appPath, int isIdo)
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

        /* set conference control param */
        if (0 == isIdo)
        {
            TsdkConfCtrlParam confCtrlParam = new TsdkConfCtrlParam();
            confCtrlParam.setProtocol(TsdkConfCtrlProtocol.TSDK_E_CONF_CTRL_PROTOCOL_IDO);
            ret = tsdkManager.setConfigParam(confCtrlParam);
            if (ret != 0) {
                LogUtil.e(TAG, "set conference control param failed." + ret);
                return false;
            }
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
        //appInfoParam.setProductName("WeLink-Mobile");
        appInfoParam.setProductName("eSDK-Mobile");
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
//        if (isSupportIM)
//        {
//            ImMgr.getInstance().sdkInit(context, appPath);
//        }
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

    /**
     * This method is used to security configuration.
     * 安全配置
     *
     * @param mSrtpMode         SRTP模式
     * @param mSipTransportMode sip传输模式
     * @param mAppConfig        应用层配置标识
     * @param mTunnelMode       安全隧道模式
     */
    public void securityParam(int mSrtpMode, int mSipTransportMode,int mAppConfig,int mTunnelMode)
    {
        //Set security param
        if(isSupportAudioAndVideoCall){
            TsdkServiceSecurityParam serviceSecurityParam = new TsdkServiceSecurityParam();

            TsdkMediaSrtpMode srtpMode = TsdkMediaSrtpMode.enumOf(mSrtpMode);
            if(null != srtpMode) {
                serviceSecurityParam.setMediaSrtpMode(srtpMode);
            }

            TsdkSipTransportMode sipTransportMode = TsdkSipTransportMode.enumOf(mSipTransportMode);
            if(null != sipTransportMode) {
                serviceSecurityParam.setSipTransportMode(sipTransportMode);
            }

            serviceSecurityParam.setIsApplyConfigPriority(mAppConfig);

            TsdkSecurityTunnelMode tunnelMode = TsdkSecurityTunnelMode.enumOf(mTunnelMode);
            if (null != tunnelMode)
            {
                serviceSecurityParam.setSecurityTunnelMode(tunnelMode);
            }

            TsdkManager.getInstance().setConfigParam(serviceSecurityParam);
        }
    }

    /**
     * This method is used to network param.
     * 网络参数
     *
     * @param mUdpPort          UDP端口号
     * @param mTlsPort          TLS端口号
     * @param mPriority         应用层网络端口启用标识
     */
    public void networkParam(String mUdpPort,String mTlsPort,int mPriority)
    {
        //Set network param
        if(isSupportAudioAndVideoCall){
            if (TextUtils.isEmpty(mUdpPort))
            {
                mUdpPort = "0";
            }
            if (TextUtils.isEmpty(mTlsPort))
            {
                mTlsPort = "0";
            }
            TsdkNetworkInfoParam networkInfoParam = new TsdkNetworkInfoParam();
            if (1 == mPriority)
            {
                networkInfoParam.setSipServerUdpPort(Integer.parseInt(mUdpPort));
                networkInfoParam.setSipServerTlsPort(Integer.parseInt(mTlsPort));
            }
            else
            {
                networkInfoParam.setSipServerUdpPort(0);
                networkInfoParam.setSipServerTlsPort(0);
            }

            TsdkManager.getInstance().setConfigParam(networkInfoParam);
        }
    }

    /**
     * This method is used to set AVC video capability level information.
     * 设置AVC视频能力级别信息
     *
     * @param encodeLevel   编码能力级别
     * @param decodeLevel   解码能力级别
     */
    public void setAvcCapsLevel(int encodeLevel, int decodeLevel)
    {
        this.videoEncodeLeave = encodeLevel;
        this.videoDecodeLeave = decodeLevel;

        //Set avc caps level
        TsdkAvcCapsLevel enCapsLevel = transCapsLevel(encodeLevel);
        TsdkAvcCapsLevel deCapsLevel = transCapsLevel(decodeLevel);

        TsdkAvcCapsLevelInfo avcCapsLevelInfo = new TsdkAvcCapsLevelInfo(enCapsLevel, deCapsLevel);
        TsdkManager.getInstance().setConfigParam(avcCapsLevelInfo);
    }

    /**
     * This method is used to set local information of the conference display.
     * 设置会议中显示的本端信息
     * @param displayName 本地显示名称
     */
    public void setDisplayLocalInfo(String displayName)
    {
        TsdkDisplayLocalInfo displayLocalInfo = new TsdkDisplayLocalInfo(displayName);
        TsdkManager.getInstance().setConfigParam(displayLocalInfo);
    }

    private TsdkAvcCapsLevel transCapsLevel(int leave)
    {
        TsdkAvcCapsLevel capsLevel = TsdkAvcCapsLevel.TSDK_E_AVC_CAPS_LEVEL_HD;
        switch (leave)
        {
            case 0:
                capsLevel = TsdkAvcCapsLevel.TSDK_E_AVC_CAPS_LEVEL_OHD;
                break;
            case 1:
                capsLevel = TsdkAvcCapsLevel.TSDK_E_AVC_CAPS_LEVEL_HD;
                break;
            case 2:
                capsLevel = TsdkAvcCapsLevel.TSDK_E_AVC_CAPS_LEVEL_SD;
                break;
            case 3:
                capsLevel = TsdkAvcCapsLevel.TSDK_E_AVC_CAPS_LEVEL_SMOOTH;
                break;
            case 4:
                capsLevel = TsdkAvcCapsLevel.TSDK_E_AVC_CAPS_LEVEL_SAVE_TRAFFIC;
                break;
            default:
                break;
        }
        return capsLevel;
    }
}

