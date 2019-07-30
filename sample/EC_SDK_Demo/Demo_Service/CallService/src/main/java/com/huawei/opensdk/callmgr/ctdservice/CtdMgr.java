package com.huawei.opensdk.callmgr.ctdservice;

import android.util.Log;

import com.huawei.ecterminalsdk.base.TsdkCtdCallParam;
import com.huawei.ecterminalsdk.models.TsdkCommonResult;
import com.huawei.ecterminalsdk.models.TsdkManager;
import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.loginmgr.LoginMgr;

/**
 * This class is about Ctd manager.
 * CTD模块功能管理类
 */
public class CtdMgr implements ICtdMgr {
    private static final String TAG = CtdMgr.class.getSimpleName();

    /**
     * CTD Object Management
     * CtdMgr对象
     */
    private static CtdMgr ctdMgr;

    /**
     * CTD event notification
     * CTD回调事件对象
     */
    private ICtdNotification ctdNotification;

    /**
     * This is a constructor of CtdMgr class.
     * 构造方法
     */
    private CtdMgr()
    {

    }

    private long callId;

    /**
     * This method is used to get CTD Object Management instances.
     * 获取ImMgr对象实例
     * @return CtdMgr Return instance object of CtdMgr
     *                返回一个CtdMgr对象实例
     */
    public static synchronized CtdMgr getInstance()
    {
        if (null == ctdMgr)
        {
            ctdMgr = new CtdMgr();
        }
        return ctdMgr;
    }

    /**
     * This method is used to register ctd module UI callback.
     * 注册回调
     * @param ctdNotification CTD event notification
     *                        CTD事件处理对象
     */
    public void regCtdNotification(ICtdNotification ctdNotification) {
        this.ctdNotification = ctdNotification;
    }

    /**
     * This method is used to start a ctd call.
     * 发起一路CTD呼叫
     * @param calleeNumber the callee number
     *                     被叫号码
     * @param callerNumber the caller number
     *                     主叫号码
     * @return result If success return call id, otherwise return -1.
     *                成功返回0，失败返回-1
     */
    public long makeCtdCall(String calleeNumber, String callerNumber)
    {
        LogUtil.e(TAG, "make a ctd call.");

        TsdkCtdCallParam ctdCallParam = new TsdkCtdCallParam();
        ctdCallParam.setCalleeNumber(calleeNumber);
        ctdCallParam.setCallerNumber(callerNumber);
        ctdCallParam.setSubscribeNumber(LoginMgr.getInstance().getSipNumber());

        long result = TsdkManager.getInstance().getCtdManager().startCall(ctdCallParam);
        if (result == -1)
        {
            LogUtil.e(TAG, "start ctd call failed, return -->" + result);
            return result;
        }
        return 0;
    }
;
    /**
     * This method is used to start a ctd call.
     * 结束CTD呼叫(移动端暂不实现)
     * @param callId       the call id
     *                     呼叫id
     * @return result If success return call id, otherwise return corresponding error code.
     *                成功返回0，失败返回相应的错误码
     */
    public int endCtdCall(long callId)
    {
        int result = TsdkManager.getInstance().getCtdManager().endCall(callId);

        if (result != 0)
        {
            LogUtil.e(TAG, "end ctd call failed, return -->" + result);
        }
        return result;
    }

    /**
     * This method is used to get start ctd call result.
     * 发起CTD呼叫结果事件
     * @param callId          Indicates call id
     *                        ctd无订阅功能，呼叫id暂时用不到
     * @param;                Indicates start ctd call operation result
     *                        发起呼叫结果
     */

    public void handleStartCallResult(long callId, TsdkCommonResult result) {
        this.callId = callId;
        if (result.getResult() == 0)
        {
            Log.e(TAG, "Start ctd call success.");
        }
        else
        {
            Log.e(TAG, "Start ctd call failed, result-->" + result.getResult());
        }
        this.ctdNotification.onStartCtdCallResult((int)result.getResult(), result.getReasonDescription());
    }
}
