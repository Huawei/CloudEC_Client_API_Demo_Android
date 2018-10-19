package com.huawei.opensdk.callmgr.iptService;

import com.huawei.ecterminalsdk.base.TsdkIptServiceInfoSet;
import com.huawei.ecterminalsdk.base.TsdkIptServiceType;
import com.huawei.ecterminalsdk.base.TsdkSetIptServiceResult;
import com.huawei.ecterminalsdk.models.call.TsdkCallManager;

/**
 * This class is about IPT manager
 * IPT管理类
 */
public class IptMgr {

    /**
     *  IPT Management object Instance
     * ipt管理类实例
     */
    private static IptMgr instance;

    /**
     * Ipt UI callback
     * ipt UI层回调
     */
    private IIptNotification iptNotification;

    /**
     * Free of disturbing
     * 免打扰
     */
    private IptRegisterInfo dndRegisterInfo;

    /**
     * Call wait
     * 呼叫等待
     */
    private IptRegisterInfo cwRegisterInfo;

    /**
     * Forward unconditionally
     * 无条件前转
     */
    private IptRegisterInfo cfuRegisterInfo;

    /**
     * Turn around before you are busy
     * 遇忙前转
     */
    private IptRegisterInfo cfbRegisterInfo;

    /**
     * Turn before answering
     * 无应答前转
     */
    private IptRegisterInfo cfnaRegisterInfo;

    /**
     * Before you go offline
     * 离线前转
     */
    private IptRegisterInfo cfnrRegisterInfo;

    public IptMgr() {
        dndRegisterInfo = new IptRegisterInfo();
        cwRegisterInfo = new IptRegisterInfo();
        cfuRegisterInfo = new IptRegisterInfo();
        cfbRegisterInfo = new IptRegisterInfo();
        cfnaRegisterInfo = new IptRegisterInfo();
        cfnrRegisterInfo = new IptRegisterInfo();
    }

    public static IptMgr getInstance() {
        if (null == instance) {
            instance = new IptMgr();
        }
        return instance;
    }

    /**
     * This method is used to registering callback Functions
     * 注册回调函数
     * @param iptNotification
     */
    public void regIptNotification(IIptNotification iptNotification)
    {
        this.iptNotification = iptNotification;
    }

    /**
     * This method is used to unregister callback function
     * 注销回调函数
     * @param iptNotification
     */
    public void unregIptNotification(IIptNotification iptNotification)
    {
        if (null != iptNotification)
        {
            this.iptNotification = null;
        }
    }

    /**
     * This method is used to set up IPT Business
     * 设置ipt业务
     *
     * @param type          Business type, taking value reference TsdkIptServiceType
     *                      业务类型，取值参考TsdkIptServiceType
     * @param isEnable      whether service is enable
     *                      业务能力是否开启
     * @param callNumber    Forward number
     *                      前转号码
     */
    public void setIPTService(int type, boolean isEnable, String callNumber) {
        TsdkIptServiceType iptServiceType = TsdkIptServiceType.enumOf(type);
        if (null == iptServiceType)
        {
            return;
        }
        TsdkCallManager.getObject().setIptService(iptServiceType, isEnable, callNumber);
    }

    public IptRegisterInfo getDndRegisterInfo() {
        return dndRegisterInfo;
    }

    public IptRegisterInfo getCwRegisterInfo() {
        return cwRegisterInfo;
    }

    public IptRegisterInfo getCfuRegisterInfo() {
        return cfuRegisterInfo;
    }

    public IptRegisterInfo getCfbRegisterInfo() {
        return cfbRegisterInfo;
    }

    public IptRegisterInfo getCfnaRegisterInfo() {
        return cfnaRegisterInfo;
    }

    public IptRegisterInfo getCfnrRegisterInfo() {
        return cfnrRegisterInfo;
    }

    /**
     * This method is used to get Functional permissions
     * 更新ipt业务状态
     *
     * @param serviceInfo  ipt service right info
     *                     ipt业务信息
     */
    public void handleIptServiceInfo(TsdkIptServiceInfoSet serviceInfo)
    {
        dndRegisterInfo.setHasRight(serviceInfo.getDnd().getHasRight());
        dndRegisterInfo.setIsEnable(serviceInfo.getDnd().getIsEnable());
        dndRegisterInfo.setRegisterNumber(serviceInfo.getDnd().getNumber());

        cwRegisterInfo.setHasRight(serviceInfo.getCallWait().getHasRight());
        cwRegisterInfo.setIsEnable(serviceInfo.getCallWait().getIsEnable());
        cwRegisterInfo.setRegisterNumber(serviceInfo.getCallWait().getNumber());

        cfuRegisterInfo.setHasRight(serviceInfo.getCfu().getHasRight());
        cfuRegisterInfo.setIsEnable(serviceInfo.getCfu().getIsEnable());
        cfuRegisterInfo.setRegisterNumber(serviceInfo.getCfu().getNumber());

        cfbRegisterInfo.setHasRight(serviceInfo.getCfb().getHasRight());
        cfbRegisterInfo.setIsEnable(serviceInfo.getCfb().getIsEnable());
        cfbRegisterInfo.setRegisterNumber(serviceInfo.getCfb().getNumber());

        cfnaRegisterInfo.setHasRight(serviceInfo.getCfn().getHasRight());
        cfnaRegisterInfo.setIsEnable(serviceInfo.getCfn().getIsEnable());
        cfnaRegisterInfo.setRegisterNumber(serviceInfo.getCfn().getNumber());

        cfnrRegisterInfo.setHasRight(serviceInfo.getCfo().getHasRight());
        cfnrRegisterInfo.setIsEnable(serviceInfo.getCfo().getIsEnable());
        cfnrRegisterInfo.setRegisterNumber(serviceInfo.getCfo().getNumber());
    }

    /**
     * The result of registration service
     * 登记业务结果
     *
     * @param type            Indicates service type
     *                        特征码业务类型
     * @param result          Indicates set ipt service result
     *                        设置ipt业务结果
     */
    public void handleSetIptServiceResult(int type, TsdkSetIptServiceResult result)
    {
        if (result.getReasonCode() != 0)
        {
            iptNotification.onSetIptServiceFal(type, result.getIsEnable());
        }
        else
        {
            iptNotification.onSetIptServiceSuc(type, result.getIsEnable());
        }
    }

}


