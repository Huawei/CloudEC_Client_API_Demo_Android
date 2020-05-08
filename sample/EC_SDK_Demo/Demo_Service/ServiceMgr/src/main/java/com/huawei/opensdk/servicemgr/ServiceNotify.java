package com.huawei.opensdk.servicemgr;


import com.huawei.ecterminalsdk.base.TsdkAttendee;
import com.huawei.ecterminalsdk.base.TsdkCallStatisticInfo;
import com.huawei.ecterminalsdk.base.TsdkConfAppShareType;
import com.huawei.ecterminalsdk.base.TsdkConfAsActionType;
import com.huawei.ecterminalsdk.base.TsdkConfAsStateInfo;
import com.huawei.ecterminalsdk.base.TsdkConfBaseInfo;
import com.huawei.ecterminalsdk.base.TsdkConfChatMsgInfo;
import com.huawei.ecterminalsdk.base.TsdkConfDetailInfo;
import com.huawei.ecterminalsdk.base.TsdkConfEndReason;
import com.huawei.ecterminalsdk.base.TsdkConfListInfo;
import com.huawei.ecterminalsdk.base.TsdkConfOperationResult;
import com.huawei.ecterminalsdk.base.TsdkConfSpeakerInfo;
import com.huawei.ecterminalsdk.base.TsdkConfSvcWatchInfo;
import com.huawei.ecterminalsdk.base.TsdkCtdCallStatus;
import com.huawei.ecterminalsdk.base.TsdkDocBaseInfo;
import com.huawei.ecterminalsdk.base.TsdkDocShareDelDocInfo;
import com.huawei.ecterminalsdk.base.TsdkForceLogoutInfo;
import com.huawei.ecterminalsdk.base.TsdkGetIconResult;
import com.huawei.ecterminalsdk.base.TsdkImLoginParam;
import com.huawei.ecterminalsdk.base.TsdkIptServiceInfoSet;
import com.huawei.ecterminalsdk.base.TsdkJoinConfIndInfo;
import com.huawei.ecterminalsdk.base.TsdkLoginFailedInfo;
import com.huawei.ecterminalsdk.base.TsdkLoginSuccessInfo;
import com.huawei.ecterminalsdk.base.TsdkMediaErrorInfo;
import com.huawei.ecterminalsdk.base.TsdkResumeConfIndInfo;
import com.huawei.ecterminalsdk.base.TsdkSearchContactsResult;
import com.huawei.ecterminalsdk.base.TsdkSearchDepartmentResult;
import com.huawei.ecterminalsdk.base.TsdkSecurityTunnelInfo;
import com.huawei.ecterminalsdk.base.TsdkServiceAccountType;
import com.huawei.ecterminalsdk.base.TsdkSessionCodec;
import com.huawei.ecterminalsdk.base.TsdkSessionModified;
import com.huawei.ecterminalsdk.base.TsdkSetIptServiceResult;
import com.huawei.ecterminalsdk.base.TsdkShareStatisticInfo;
import com.huawei.ecterminalsdk.base.TsdkVideoOrientation;
import com.huawei.ecterminalsdk.base.TsdkVideoViewRefresh;
import com.huawei.ecterminalsdk.base.TsdkVoipAccountInfo;
import com.huawei.ecterminalsdk.base.TsdkWbDelDocInfo;
import com.huawei.ecterminalsdk.models.TsdkCommonResult;
import com.huawei.ecterminalsdk.models.TsdkNotify;
import com.huawei.ecterminalsdk.models.call.TsdkCall;
import com.huawei.ecterminalsdk.models.conference.TsdkConference;
import com.huawei.opensdk.callmgr.CallMgr;
import com.huawei.opensdk.callmgr.ctdservice.CtdMgr;
import com.huawei.opensdk.callmgr.iptService.IptMgr;
import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.contactservice.eaddr.EnterpriseAddressBookMgr;
import com.huawei.opensdk.demoservice.MeetingMgr;
import com.huawei.opensdk.loginmgr.LoginMgr;


public class ServiceNotify implements TsdkNotify{

    private static final String TAG = ServiceNotify.class.getSimpleName();

    private static ServiceNotify instance;

    public static ServiceNotify getInstance() {
        if (null == instance) {
            instance = new ServiceNotify();
        }
        return instance;
    }

    @Override
    public void onEvtAuthSuccess(long userId, TsdkImLoginParam imAccountLoginParam) {
        LogUtil.i(TAG, "onEvtAuthSuccess notify.");
        LoginMgr.getInstance().handleAuthSuccess((int)userId, imAccountLoginParam);
    }

    @Override
    public void onEvtAuthFailed(long userId, TsdkCommonResult result) {
        LogUtil.i(TAG, "onEvtAuthFailed notify.");
        LoginMgr.getInstance().handleAuthFailed((int)userId, result);
    }

    @Override
    public void onEvtAuthRefreshFailed(long userId, TsdkCommonResult result) {
        LogUtil.i(TAG, "onEvtAuthRefreshFailed notify.");
        LoginMgr.getInstance().handleAuthRefreshFailed((int)userId, result);

    }

    @Override
    public void onEvtLoginSuccess(long userId, TsdkServiceAccountType serviceAccountType, TsdkLoginSuccessInfo loginSuccessInfo) {
        LogUtil.i(TAG, "onEvtLoginSuccess notify.");
        LoginMgr.getInstance().handleLoginSuccess((int)userId, serviceAccountType, loginSuccessInfo);
    }

    @Override
    public void onEvtLoginFailed(long userId, TsdkServiceAccountType serviceAccountType, TsdkLoginFailedInfo loginFailedInfo) {
        LogUtil.i(TAG, "onEvtLoginFailed notify.");
        LoginMgr.getInstance().handleLoginFailed((int)userId, serviceAccountType, loginFailedInfo);
    }

    @Override
    public void onEvtLogoutSuccess(long userId, TsdkServiceAccountType serviceAccountType) {
        LogUtil.i(TAG, "onEvtLogoutSuccess notify.");
        LoginMgr.getInstance().handleLogoutSuccess((int)userId, serviceAccountType);
    }


    @Override
    public void onEvtLogoutFailed(long userId, TsdkCommonResult result) {
        LogUtil.i(TAG, "onEvtLogoutFailed notify.");
        LoginMgr.getInstance().handleLogoutFailed((int)userId, result);

    }

    @Override
    public void onEvtForceLogout(long userId, TsdkServiceAccountType serviceAccountType, TsdkForceLogoutInfo forceLogoutInfo) {
        LogUtil.i(TAG, "onEvtForceLogout notify.");
        LoginMgr.getInstance().handleForceLogout((int)userId);
    }

    @Override
    public void onEvtVoipAccountStatus(long userId, TsdkVoipAccountInfo voipAccountInfo) {
        LogUtil.i(TAG, "onEvtVoipAccountStatus notify.");
        LoginMgr.getInstance().handleVoipAccountStatus((int)userId, voipAccountInfo);
    }

    @Override
    public void onEvtImAccountStatus(long userId) {
        LogUtil.i(TAG, "onEvtImAccountStatus notify.");
    }

    @Override
    public void onEvtFirewallDetectFailed(long userId, TsdkCommonResult result) {
        LogUtil.i(TAG, "onEvtFirewallDetectFailed notify.");
        LoginMgr.getInstance().handleFirewallDetectFailed((int)userId, result);
    }

    @Override
    public void onEvtBuildStgTunnelFailed(long userId, TsdkCommonResult result) {
        LogUtil.i(TAG, "onEvtBuildStgTunnelFailed notify.");
        LoginMgr.getInstance().handleBuildStgTunnelFailed((int)userId, result);
    }

    @Override
    public void onEvtSecurityTunnelInfoInd(long userId, long firewallMode, TsdkSecurityTunnelInfo securityTunnelInfo) {
        LogUtil.i(TAG, "onEvtSecurityTunnelInfoInd notify.");
        LoginMgr.getInstance().handleSecurityTunnelInfoInd((int)userId, (int)firewallMode, securityTunnelInfo);
    }

    @Override
    public void onEvtModifyPasswordResult(long userId, final TsdkCommonResult result) {
        LogUtil.i(TAG, "onEvtModifyPasswordResult notify.");
        LoginMgr.getInstance().handModifyPasswordResult(result);
    }

    @Override
    public void onEvtLoginResumingInd(long userId) {
        LogUtil.i(TAG, "onEvtLoginResumingInd notify.");
        LoginMgr.getInstance().handLoginResumingInd((int)userId);
    }

    @Override
    public void onEvtLoginResumeResult(long userId, TsdkCommonResult result) {
        LogUtil.i(TAG, "onEvtLoginResumeResult notify.");
        LoginMgr.getInstance().handLoginResumeResult(result);
    }

    @Override
    public void onEvtCallStartResult(TsdkCall call, TsdkCommonResult result) {
        LogUtil.i(TAG, "onEvtCallStartResult notify.");

    }

    @Override
    public void onEvtCallIncoming(TsdkCall call, Boolean maybeVideoCall) {
        LogUtil.i(TAG, "onEvtCallIncoming notify.");
        CallMgr.getInstance().handleCallComing(call, maybeVideoCall);

    }

    @Override
    public void onEvtCallOutgoing(TsdkCall call) {
        LogUtil.i(TAG, "onEvtCallOutgoing notify.");
        CallMgr.getInstance().handleCallGoing(call);

    }

    @Override
    public void onEvtCallRingback(TsdkCall call) {
        LogUtil.i(TAG, "onEvtCallRingback notify.");
        CallMgr.getInstance().handleCallRingback(call);

    }

    @Override
    public void onEvtCallRtpCreated(TsdkCall call) {
        LogUtil.i(TAG, "onEvtCallRtpCreated notify.");
        CallMgr.getInstance().handleCallRtpCreated(call);

    }

    @Override
    public void onEvtCallConnected(TsdkCall call) {
        LogUtil.i(TAG, "onEvtCallConnected notify.");
        CallMgr.getInstance().handleCallConnected(call);

    }

    @Override
    public void onEvtCallEnded(TsdkCall call) {
        LogUtil.i(TAG, "onEvtCallEnded notify.");
        CallMgr.getInstance().handleCallEnded(call);

    }

    @Override
    public void onEvtCallDestroy(TsdkCall call) {
        LogUtil.i(TAG, "onEvtCallDestroy notify.");
        CallMgr.getInstance().handleCallDestroy(call);

    }

    @Override
    public void onEvtOpenVideoReq(TsdkCall call, TsdkVideoOrientation orientType) {
        LogUtil.i(TAG, "onEvtOpenVideoReq notify.");
        CallMgr.getInstance().handleOpenVideoReq(call,orientType);

    }

    @Override
    public void onEvtRefuseOpenVideoInd(TsdkCall call) {
        LogUtil.i(TAG, "onEvtRefuseOpenVideoInd notify.");
        CallMgr.getInstance().handleRefuseOpenVideoInd(call);
    }

    @Override
    public void onEvtCloseVideoInd(TsdkCall call) {
        CallMgr.getInstance().handleCloseVideoInd(call);
        LogUtil.i(TAG, "onEvtCloseVideoInd notify.");

    }

    @Override
    public void onEvtOpenVideoInd(TsdkCall call) {
        LogUtil.i(TAG, "onEvtOpenVideoInd notify.");
        CallMgr.getInstance().handleOpenVideoInd(call);

    }

    @Override
    public void onEvtRefreshViewInd(TsdkCall call, TsdkVideoViewRefresh refreshInfo) {
        LogUtil.i(TAG, "onEvtRefreshViewInd notify.");
        CallMgr.getInstance().handleRefreshViewInd(call, refreshInfo);

    }

    @Override
    public void onEvtCallRouteChange(TsdkCall call, long route) {
        LogUtil.i(TAG, "onEvtCallRouteChange notify.");

    }

    @Override
    public void onEvtPlayMediaEnd(long handle) {
        LogUtil.i(TAG, "onEvtPlayMediaEnd notify.");

    }

    @Override
    public void onEvtSessionModified(TsdkCall call, TsdkSessionModified sessionInfo) {
        LogUtil.i(TAG, "onEvtSessionModified notify.");
    }

    @Override
    public void onEvtSessionCodec(TsdkCall call, TsdkSessionCodec codecInfo) {
        LogUtil.i(TAG, "onEvtSessionCodec notify.");
    }

    @Override
    public void onEvtHoldSuccess(TsdkCall call) {
        LogUtil.i(TAG, "onEvtHoldSuccess notify.");
        CallMgr.getInstance().handleHoldSuccess(call);
    }

    @Override
    public void onEvtHoldFailed(TsdkCall call) {
        LogUtil.i(TAG, "onEvtHoldFailed notify.");
        CallMgr.getInstance().handleHoldFailed(call);
    }

    @Override
    public void onEvtUnholdSuccess(TsdkCall call) {
        LogUtil.i(TAG, "onEvtUnholdSuccess notify.");
        CallMgr.getInstance().handleUnholdSuccess(call);
    }

    @Override
    public void onEvtUnholdFailed(TsdkCall call) {
        LogUtil.i(TAG, "onEvtUnholdFailed notify.");
        CallMgr.getInstance().handleUnholdFailed(call);
    }

    @Override
    public void onEvtEndcallFailed(TsdkCall call, TsdkCommonResult result) {
        LogUtil.i(TAG, "onEvtEndcallFailed notify.");

    }

    @Override
    public void onEvtDivertFailed(TsdkCall call) {
        LogUtil.i(TAG, "onEvtDivertFailed notify.");
        CallMgr.getInstance().handleDivertFailed(call);
    }

    @Override
    public void onEvtBldTransferSuccess(TsdkCall call) {
        LogUtil.i(TAG, "onEvtBldTransferSuccess notify.");
        CallMgr.getInstance().handleBldTransferSuccess(call);
    }

    @Override
    public void onEvtBldTransferFailed(TsdkCall call) {
        LogUtil.i(TAG, "onEvtBldTransferFailed notify.");
        CallMgr.getInstance().handleBldTransferFailed(call);
    }

    @Override
    public void onEvtSetIptServiceResult(long type, TsdkSetIptServiceResult result) {
        LogUtil.i(TAG, "onEvtSetIptServiceResult notify.");
        IptMgr.getInstance().handleSetIptServiceResult((int)type, result);
    }

    @Override
    public void onEvtIptServiceInfo(TsdkIptServiceInfoSet serviceInfo) {
        LogUtil.i(TAG, "onEvtIptServiceInfo notify.");
        IptMgr.getInstance().handleIptServiceInfo(serviceInfo);
    }

    @Override
    public void onEvtGetTempUserResult(long userId, TsdkCommonResult result) {
        LogUtil.i(TAG, "onEvtGetTempUserResult notify.");
        MeetingMgr.getInstance().handleGetTempUserResult((int)userId, result);
    }

    @Override
    public void onEvtBookConfResult(TsdkCommonResult result, TsdkConfBaseInfo confBaseInfo) {
        LogUtil.i(TAG, "onEvtBookConfResult notify.");
        MeetingMgr.getInstance().handleBookConfResult(result, confBaseInfo);
    }

    @Override
    public void onEvtQueryConfListResult(TsdkCommonResult result, TsdkConfListInfo confList) {
        LogUtil.i(TAG, "onEvtQueryConfListResult notify.");
        MeetingMgr.getInstance().handleQueryConfListResult(result, confList);
    }

    @Override
    public void onEvtQueryConfDetailResult(TsdkCommonResult result, TsdkConfDetailInfo confDetailInfo) {
        LogUtil.i(TAG, "onEvtQueryConfDetailResult notify.");
        MeetingMgr.getInstance().handleQueryConfDetailResult(result, confDetailInfo);
    }

    @Override
    public void onEvtJoinConfResult(TsdkConference conference, TsdkCommonResult result, TsdkJoinConfIndInfo info) {
        LogUtil.i(TAG, "onEvtJoinConfResult notify.");
        MeetingMgr.getInstance().handleJoinConfResult(conference, result, info);
    }

    @Override
    public void onEvtGetDataconfParamResult(TsdkConference conference, TsdkCommonResult result) {
        LogUtil.i(TAG, "onEvtGetDataconfParamResult notify.");
        MeetingMgr.getInstance().handleGetDataConfParamsResult(conference, result);
    }

    @Override
    public void onEvtConfctrlOperationResult(TsdkConference conference, TsdkConfOperationResult result) {
        LogUtil.i(TAG, "onEvtConfctrlOperationResult notify.");
        MeetingMgr.getInstance().handleConfctrlOperationResult(conference, result);
    }


    @Override
    public void onEvtInfoAndStatusUpdate(TsdkConference conference) {
        LogUtil.i(TAG, "onEvtInfoAndStatusUpdate notify.");
        MeetingMgr.getInstance().handleInfoAndStatusUpdate(conference);
    }

    @Override
    public void onEvtSpeakerInd(TsdkConference conference, TsdkConfSpeakerInfo speakerList) {
        LogUtil.i(TAG, "onEvtSpeakerInd notify.");
        MeetingMgr.getInstance().handleSpeakerInd(speakerList);
    }

    @Override
    public void onEvtRequestConfRightFailed(TsdkConference conference, TsdkCommonResult result) {
        LogUtil.i(TAG, "onEvtRequestConfRightFailed notify.");
    }

    @Override
    public void onEvtConfIncomingInd(TsdkConference conference) {
        LogUtil.i(TAG, "onEvtConfIncomingInd notify.");
        MeetingMgr.getInstance().handleConfIncomingInd(conference);

    }

    @Override
    public void onEvtConfEndInd(TsdkConference conference, TsdkConfEndReason reasonCode) {
        LogUtil.i(TAG, "onEvtConfEndInd notify.");
        MeetingMgr.getInstance().handleConfEndInd(conference, reasonCode);
    }

    @Override
    public void onEvtJoinDataConfResult(TsdkConference conference, TsdkCommonResult result) {
        LogUtil.i(TAG, "onEvtJoinDataConfResult notify.");
        MeetingMgr.getInstance().handleJoinDataConfResult(conference, result);
    }

    @Override
    public void onEvtAsStateChange(TsdkConference conference, TsdkConfAppShareType shareType, TsdkConfAsStateInfo asStateInfo) {
        LogUtil.i(TAG, "onEvtAsStateChange notify.");
        MeetingMgr.getInstance().handleAsStateChange(asStateInfo);
    }

    @Override
    public void onEvtAsOwnerChange(TsdkConference conference, TsdkConfAsActionType actionType, TsdkAttendee owner) {
        LogUtil.i(TAG, "OnEvtAsOwnerChange notify.");
        MeetingMgr.getInstance().handleAsOwnerChange(actionType, owner);
    }

    @Override
    public void onEvtDsDocNew(TsdkConference conference, TsdkDocBaseInfo docBaseInfo) {
        LogUtil.i(TAG, "onEvtDsDocNew notify.");
        MeetingMgr.getInstance().handleDsDocNew(docBaseInfo);
    }

    @Override
    public void onEvtDsDocDel(TsdkConference conference, TsdkDocShareDelDocInfo docShareDelDocInfo) {
        LogUtil.i(TAG, "onEvtDsDocDel notify.");
        MeetingMgr.getInstance().handleDsDocDel(docShareDelDocInfo);
    }

    @Override
    public void onEvtWbDocNew(TsdkConference conference, TsdkDocBaseInfo docBaseInfo) {
        LogUtil.i(TAG, "onEvtWbDocNew notify.");
        MeetingMgr.getInstance().handleWbDocNew(docBaseInfo);
    }

    @Override
    public void onEvtWbDocDel(TsdkConference conference, TsdkWbDelDocInfo wbDelDocInfo) {
        LogUtil.i(TAG, "onEvtWbDocDel notify.");
        MeetingMgr.getInstance().handleWbDocDel(wbDelDocInfo);
    }

    @Override
    public void onEvtRecvChatMsg(TsdkConference tsdkConference, TsdkConfChatMsgInfo tsdkConfChatMsgInfo) {
        LogUtil.i(TAG, "onEvtRecvChatMsg notify.");
        MeetingMgr.getInstance().handleRecvChatMsg(tsdkConfChatMsgInfo);
    }

    @Override
    public void onEvtCtdStartCallResult(long callId, TsdkCommonResult result) {
	    LogUtil.i(TAG, "onEvtCtdStartCallResult notify.");
        CtdMgr.getInstance().handleStartCallResult((int)callId, result);
    }

    @Override
    public void onEvtCtdEndCallResult(long callId, TsdkCommonResult result) {
        LogUtil.i(TAG, "onEvtCtdEndCallResult notify.");
    }

    @Override
    public void onEvtCtdCallStatusNotify(long callId, TsdkCtdCallStatus status) {
        LogUtil.i(TAG, "onEvtCtdCallStatusNotify notify.");
    }

    @Override
    public void onEvtSearchContactsResult(long querySeqNo, TsdkCommonResult result, TsdkSearchContactsResult searchContactResult) {
        LogUtil.i(TAG, "onEvtSearchContactsResult notify.");
        EnterpriseAddressBookMgr.getInstance().handleSearchContactResult((int)querySeqNo, result, searchContactResult);
    }

    @Override
    public void onEvtSearchDeptResult(long querySeqNo, TsdkCommonResult result, TsdkSearchDepartmentResult searchDeptResult) {
        LogUtil.i(TAG, "onEvtSearchDeptResult notify.");
        EnterpriseAddressBookMgr.getInstance().handleSearchDepartmentResult((int)querySeqNo, result, searchDeptResult);
    }

    @Override
    public void onEvtGetIconResult(long querySeqNo, TsdkCommonResult result, TsdkGetIconResult getIconResult) {
        LogUtil.i(TAG, "onEvtGetIconResult notify.");
        EnterpriseAddressBookMgr.getInstance().handleGetIconResult((int)querySeqNo, result, getIconResult);
    }

    @Override
    public void onEvtTransToConfResult(TsdkCall call, TsdkCommonResult result) {

    }

    @Override
    public void onEvtStatisticInfo(TsdkCall call, long signalStrength, TsdkCallStatisticInfo statisticInfo) {
        LogUtil.i(TAG, "onEvtStatisticInfo notify.");
        CallMgr.getInstance().handleUpDateCallStatisticInfo(signalStrength, statisticInfo);
    }

    @Override
    public void onEvtMediaErrorInfo(TsdkCall tsdkCall, TsdkMediaErrorInfo tsdkMediaErrorInfo) {

    }

    @Override
    public void onEvtNoStream(TsdkCall call, long duration) {
        LogUtil.i(TAG, "onEvtNoStream notify.");
        MeetingMgr.getInstance().handleNoStream(duration);
    }

    @Override
    public void onEvtSvcWatchInfoInd(TsdkConference conference, TsdkConfSvcWatchInfo svcWatchInfo) {
        LogUtil.i(TAG, "onEvtSvcWatchInfoInd notify.");
        MeetingMgr.getInstance().handleSvcWatchInfoInd(conference, svcWatchInfo);
    }

    @Override
    public void onEvtShareStatisticInfo(TsdkConference conference, TsdkShareStatisticInfo statisticInfo) {
        LogUtil.i(TAG, "onEvtShareStatisticInfo notify.");
        MeetingMgr.getInstance().handleShareStatisticInfo(conference, statisticInfo);
    }

    @Override
    public void onEvtConfResumingInd(TsdkConference conference) {
        LogUtil.i(TAG, "onEvtConfResumingInd notify.");
        MeetingMgr.getInstance().handleConfResumingInd();
    }

    @Override
    public void onEvtConfResumeResult(TsdkConference conference, TsdkCommonResult result, TsdkResumeConfIndInfo info) {
        LogUtil.i(TAG, "onEvtConfResumeResult notify.");
        MeetingMgr.getInstance().handleConfResumeResult(conference, result, info);
    }

    @Override
    public void onEvtConfEndResult(TsdkConference tsdkConference, TsdkCommonResult tsdkCommonResult) {
        LogUtil.i(TAG, "onEvtConfEndResult notify. result-> " + tsdkCommonResult.getResult());
    }

    @Override
    public void onEvtConfSetShareOwnerFailed(TsdkConference tsdkConference, TsdkCommonResult tsdkCommonResult) {
        LogUtil.i(TAG, "onEvtConfSetShareOwnerFailed notify.");
        MeetingMgr.getInstance().handleConfSetShareOwnerFailed(tsdkConference, tsdkCommonResult);
    }

    @Override
    public void onEvtConfStartShareFailed(TsdkConference tsdkConference, TsdkCommonResult tsdkCommonResult) {
        LogUtil.i(TAG, "onEvtConfStartShareFailed notify.");
        MeetingMgr.getInstance().handleConfStartShareFailed(tsdkConference, tsdkCommonResult);
    }
}
