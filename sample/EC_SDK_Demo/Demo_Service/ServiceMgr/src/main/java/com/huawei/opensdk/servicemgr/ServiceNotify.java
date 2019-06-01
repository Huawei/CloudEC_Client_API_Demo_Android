package com.huawei.opensdk.servicemgr;


import android.util.Log;

import com.huawei.ecterminalsdk.base.TsdkBatchChatMsgInfo;
import com.huawei.ecterminalsdk.base.TsdkBeAddedFriendInfo;
import com.huawei.ecterminalsdk.base.TsdkBeAddedToChatGroupInfo;
import com.huawei.ecterminalsdk.base.TsdkChatGroupInfoUpdateType;
import com.huawei.ecterminalsdk.base.TsdkChatGroupUpdateInfo;
import com.huawei.ecterminalsdk.base.TsdkChatMsgInfo;
import com.huawei.ecterminalsdk.base.TsdkChatMsgUndeliverInfo;
import com.huawei.ecterminalsdk.base.TsdkChatMsgWithdrawInfo;
import com.huawei.ecterminalsdk.base.TsdkChatMsgWithdrawResult;
import com.huawei.ecterminalsdk.base.TsdkConfAppShareType;
import com.huawei.ecterminalsdk.base.TsdkConfAsStateInfo;
import com.huawei.ecterminalsdk.base.TsdkConfBaseInfo;
import com.huawei.ecterminalsdk.base.TsdkConfChatMsgInfo;
import com.huawei.ecterminalsdk.base.TsdkConfDetailInfo;
import com.huawei.ecterminalsdk.base.TsdkConfListInfo;
import com.huawei.ecterminalsdk.base.TsdkConfOperationResult;
import com.huawei.ecterminalsdk.base.TsdkConfSpeakerInfo;
import com.huawei.ecterminalsdk.base.TsdkCtdCallStatus;
import com.huawei.ecterminalsdk.base.TsdkDelChatGroupMemberResult;
import com.huawei.ecterminalsdk.base.TsdkDocBaseInfo;
import com.huawei.ecterminalsdk.base.TsdkDocShareDelDocInfo;
import com.huawei.ecterminalsdk.base.TsdkForceLogoutInfo;
import com.huawei.ecterminalsdk.base.TsdkGetIconResult;
import com.huawei.ecterminalsdk.base.TsdkImLoginParam;
import com.huawei.ecterminalsdk.base.TsdkImUserInfo;
import com.huawei.ecterminalsdk.base.TsdkImUserStatusUpdateInfo;
import com.huawei.ecterminalsdk.base.TsdkInputtingStatusInfo;
import com.huawei.ecterminalsdk.base.TsdkIptServiceInfoSet;
import com.huawei.ecterminalsdk.base.TsdkJoinConfIndInfo;
import com.huawei.ecterminalsdk.base.TsdkLeaveChatGroupResult;
import com.huawei.ecterminalsdk.base.TsdkLoginFailedInfo;
import com.huawei.ecterminalsdk.base.TsdkLoginSuccessInfo;
import com.huawei.ecterminalsdk.base.TsdkMsgReadIndInfo;
import com.huawei.ecterminalsdk.base.TsdkReqJoinChatGroupMsg;
import com.huawei.ecterminalsdk.base.TsdkRspJoinChatGroupMsg;
import com.huawei.ecterminalsdk.base.TsdkSearchContactsResult;
import com.huawei.ecterminalsdk.base.TsdkSearchDepartmentResult;
import com.huawei.ecterminalsdk.base.TsdkSecurityTunnelInfo;
import com.huawei.ecterminalsdk.base.TsdkSendChatMsgResult;
import com.huawei.ecterminalsdk.base.TsdkServiceAccountType;
import com.huawei.ecterminalsdk.base.TsdkSessionCodec;
import com.huawei.ecterminalsdk.base.TsdkSessionModified;
import com.huawei.ecterminalsdk.base.TsdkSetIptServiceResult;
import com.huawei.ecterminalsdk.base.TsdkSmsInfo;
import com.huawei.ecterminalsdk.base.TsdkVideoOrientation;
import com.huawei.ecterminalsdk.base.TsdkVideoViewRefresh;
import com.huawei.ecterminalsdk.base.TsdkVoipAccountInfo;
import com.huawei.ecterminalsdk.base.TsdkWbDelDocInfo;
import com.huawei.ecterminalsdk.models.TsdkCommonResult;
import com.huawei.ecterminalsdk.models.TsdkNotify;
import com.huawei.ecterminalsdk.models.call.TsdkCall;
import com.huawei.ecterminalsdk.models.conference.TsdkConference;
import com.huawei.ecterminalsdk.models.im.TsdkChatGroup;
import com.huawei.opensdk.callmgr.CallMgr;
import com.huawei.opensdk.callmgr.ctdservice.CtdMgr;
import com.huawei.opensdk.callmgr.iptService.IptMgr;
import com.huawei.opensdk.contactservice.eaddr.EnterpriseAddressBookMgr;
import com.huawei.opensdk.demoservice.MeetingMgr;
import com.huawei.opensdk.imservice.ImMgr;
import com.huawei.opensdk.loginmgr.LoginMgr;

import java.util.List;


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
    public void onEvtAuthSuccess(int userId, TsdkImLoginParam imAccountLoginParam) {
        Log.i(TAG, "onEvtAuthSuccess notify.");
        LoginMgr.getInstance().handleAuthSuccess(userId, imAccountLoginParam);
    }

    @Override
    public void onEvtAuthFailed(int userId, TsdkCommonResult result) {
        Log.i(TAG, "onEvtAuthFailed notify.");
        LoginMgr.getInstance().handleAuthFailed(userId, result);
    }

    @Override
    public void onEvtAuthRefreshFailed(int userId, TsdkCommonResult result) {
        Log.i(TAG, "onEvtAuthRefreshFailed notify.");
        LoginMgr.getInstance().handleAuthRefreshFailed(userId, result);

    }

    @Override
    public void onEvtLoginSuccess(int userId, TsdkServiceAccountType serviceAccountType, TsdkLoginSuccessInfo loginSuccessInfo) {
        Log.i(TAG, "onEvtLoginSuccess notify.");
        LoginMgr.getInstance().handleLoginSuccess(userId, serviceAccountType, loginSuccessInfo);
    }

    @Override
    public void onEvtLoginFailed(int userId, TsdkServiceAccountType serviceAccountType, TsdkLoginFailedInfo loginFailedInfo) {
        Log.i(TAG, "onEvtLoginFailed notify.");
        LoginMgr.getInstance().handleLoginFailed(userId, serviceAccountType, loginFailedInfo);
    }

    @Override
    public void onEvtLogoutSuccess(int userId, TsdkServiceAccountType serviceAccountType) {
        Log.i(TAG, "onEvtLogoutSuccess notify.");
        LoginMgr.getInstance().handleLogoutSuccess(userId, serviceAccountType);
    }


    @Override
    public void onEvtLogoutFailed(int userId, TsdkCommonResult result) {
        Log.i(TAG, "onEvtLogoutFailed notify.");
        LoginMgr.getInstance().handleLogoutFailed(userId, result);

    }

    @Override
    public void onEvtForceLogout(int userId, TsdkServiceAccountType serviceAccountType, TsdkForceLogoutInfo forceLogoutInfo) {
        Log.i(TAG, "onEvtForceLogout notify.");
        LoginMgr.getInstance().handleForceLogout(userId);
    }

    @Override
    public void onEvtVoipAccountStatus(int userId, TsdkVoipAccountInfo voipAccountInfo) {
        Log.i(TAG, "onEvtVoipAccountStatus notify.");
        LoginMgr.getInstance().handleVoipAccountStatus(userId, voipAccountInfo);
    }

    @Override
    public void onEvtImAccountStatus(int userId) {
        Log.i(TAG, "onEvtImAccountStatus notify.");
    }

    @Override
    public void onEvtFirewallDetectFailed(int userId, TsdkCommonResult result) {
        Log.i(TAG, "onEvtFirewallDetectFailed notify.");
        LoginMgr.getInstance().handleFirewallDetectFailed(userId, result);
    }

    @Override
    public void onEvtBuildStgTunnelFailed(int userId, TsdkCommonResult result) {
        Log.i(TAG, "onEvtBuildStgTunnelFailed notify.");
        LoginMgr.getInstance().handleBuildStgTunnelFailed(userId, result);
    }

    @Override
    public void onEvtSecurityTunnelInfoInd(int userId, int firewallMode, TsdkSecurityTunnelInfo securityTunnelInfo) {
        Log.i(TAG, "onEvtSecurityTunnelInfoInd notify.");
        LoginMgr.getInstance().handleSecurityTunnelInfoInd(userId, firewallMode, securityTunnelInfo);
    }

    @Override
    public void onEvtCallStartResult(TsdkCall call, TsdkCommonResult result) {
        Log.e(TAG, "onEvtCallStartResult notify.");

    }

    @Override
    public void onEvtCallIncoming(TsdkCall call, Boolean maybeVideoCall) {
        Log.i(TAG, "onEvtCallIncoming notify.");
        CallMgr.getInstance().handleCallComing(call, maybeVideoCall);

    }

    @Override
    public void onEvtCallOutgoing(TsdkCall call) {
        Log.i(TAG, "onEvtCallOutgoing notify.");
        CallMgr.getInstance().handleCallGoing(call);

    }

    @Override
    public void onEvtCallRingback(TsdkCall call) {
        Log.i(TAG, "onEvtCallRingback notify.");
        CallMgr.getInstance().handleCallRingback(call);

    }

    @Override
    public void onEvtCallRtpCreated(TsdkCall call) {
        Log.i(TAG, "onEvtCallRtpCreated notify.");
        CallMgr.getInstance().handleCallRtpCreated(call);

    }

    @Override
    public void onEvtCallConnected(TsdkCall call) {
        Log.i(TAG, "onEvtCallConnected notify.");
        CallMgr.getInstance().handleCallConnected(call);

    }

    @Override
    public void onEvtCallEnded(TsdkCall call) {
        Log.i(TAG, "onEvtCallEnded notify.");
        CallMgr.getInstance().handleCallEnded(call);

    }

    @Override
    public void onEvtCallDestroy(TsdkCall call) {
        Log.i(TAG, "onEvtCallDestroy notify.");
        CallMgr.getInstance().handleCallDestroy(call);

    }

    @Override
    public void onEvtOpenVideoReq(TsdkCall call, TsdkVideoOrientation orientType) {
        Log.i(TAG, "onEvtOpenVideoReq notify.");
        CallMgr.getInstance().handleOpenVideoReq(call,orientType);

    }

    @Override
    public void onEvtRefuseOpenVideoInd(TsdkCall call) {
        Log.i(TAG, "onEvtRefuseOpenVideoInd notify.");
        CallMgr.getInstance().handleRefuseOpenVideoInd(call);
    }

    @Override
    public void onEvtCloseVideoInd(TsdkCall call) {
        CallMgr.getInstance().handleCloseVideoInd(call);
        Log.i(TAG, "onEvtCloseVideoInd notify.");

    }

    @Override
    public void onEvtOpenVideoInd(TsdkCall call) {
        Log.i(TAG, "onEvtOpenVideoInd notify.");
        CallMgr.getInstance().handleOpenVideoInd(call);

    }

    @Override
    public void onEvtRefreshViewInd(TsdkCall call, TsdkVideoViewRefresh refreshInfo) {
        Log.i(TAG, "onEvtRefreshViewInd notify.");
        CallMgr.getInstance().handleRefreshViewInd(call, refreshInfo);

    }

    @Override
    public void onEvtCallRouteChange(TsdkCall call, int route) {
        Log.i(TAG, "onEvtCallRouteChange notify.");

    }

    @Override
    public void onEvtPlayMediaEnd(int handle) {
        Log.i(TAG, "onEvtPlayMediaEnd notify.");

    }

    @Override
    public void onEvtSessionModified(TsdkCall call, TsdkSessionModified sessionInfo) {
        Log.i(TAG, "onEvtSessionModified notify.");
    }

    @Override
    public void onEvtSessionCodec(TsdkCall call, TsdkSessionCodec codecInfo) {
        Log.i(TAG, "onEvtSessionCodec notify.");
    }

    @Override
    public void onEvtHoldSuccess(TsdkCall call) {
        Log.i(TAG, "onEvtHoldSuccess notify.");
        CallMgr.getInstance().handleHoldSuccess(call);
    }

    @Override
    public void onEvtHoldFailed(TsdkCall call) {
        Log.i(TAG, "onEvtHoldFailed notify.");
        CallMgr.getInstance().handleHoldFailed(call);
    }

    @Override
    public void onEvtUnholdSuccess(TsdkCall call) {
        Log.i(TAG, "onEvtUnholdSuccess notify.");
        CallMgr.getInstance().handleUnholdSuccess(call);
    }

    @Override
    public void onEvtUnholdFailed(TsdkCall call) {
        Log.i(TAG, "onEvtUnholdFailed notify.");
        CallMgr.getInstance().handleUnholdFailed(call);
    }

    @Override
    public void onEvtEndcallFailed(TsdkCall call, TsdkCommonResult result) {
        Log.i(TAG, "onEvtEndcallFailed notify.");

    }

    @Override
    public void onEvtDivertFailed(TsdkCall call) {
        Log.i(TAG, "onEvtDivertFailed notify.");
        CallMgr.getInstance().handleDivertFailed(call);
    }

    @Override
    public void onEvtBldTransferSuccess(TsdkCall call) {
        Log.i(TAG, "onEvtBldTransferSuccess notify.");
        CallMgr.getInstance().handleBldTransferSuccess(call);
    }

    @Override
    public void onEvtBldTransferFailed(TsdkCall call) {
        Log.i(TAG, "onEvtBldTransferFailed notify.");
        CallMgr.getInstance().handleBldTransferFailed(call);
    }

    @Override
    public void onEvtSetIptServiceResult(int type, TsdkSetIptServiceResult result) {
        Log.i(TAG, "onEvtSetIptServiceResult notify.");
        IptMgr.getInstance().handleSetIptServiceResult(type, result);
    }

    @Override
    public void onEvtIptServiceInfo(TsdkIptServiceInfoSet serviceInfo) {
        Log.i(TAG, "onEvtIptServiceInfo notify.");
        IptMgr.getInstance().handleIptServiceInfo(serviceInfo);
    }

    @Override
    public void onEvtGetTempUserResult(int userId, TsdkCommonResult result) {
        Log.i(TAG, "onEvtGetTempUserResult notify.");
        MeetingMgr.getInstance().handleGetTempUserResult(userId, result);
    }

    @Override
    public void onEvtBookConfResult(TsdkCommonResult result, TsdkConfBaseInfo confBaseInfo) {
        Log.i(TAG, "onEvtBookConfResult notify.");
        MeetingMgr.getInstance().handleBookConfResult(result, confBaseInfo);
    }

    @Override
    public void onEvtQueryConfListResult(TsdkCommonResult result, TsdkConfListInfo confList) {
        Log.i(TAG, "onEvtQueryConfListResult notify.");
        MeetingMgr.getInstance().handleQueryConfListResult(result, confList);
    }

    @Override
    public void onEvtQueryConfDetailResult(TsdkCommonResult result, TsdkConfDetailInfo confDetailInfo) {
        Log.i(TAG, "onEvtQueryConfDetailResult notify.");
        MeetingMgr.getInstance().handleQueryConfDetailResult(result, confDetailInfo);
    }

    @Override
    public void onEvtJoinConfResult(TsdkConference conference, TsdkCommonResult result, TsdkJoinConfIndInfo info) {
        Log.i(TAG, "onEvtJoinConfResult notify.");
        MeetingMgr.getInstance().handleJoinConfResult(conference, result, info);
    }

    @Override
    public void onEvtGetDataconfParamResult(TsdkConference conference, TsdkCommonResult result) {
        Log.i(TAG, "onEvtGetDataconfParamResult notify.");
        MeetingMgr.getInstance().handleGetDataConfParamsResult(conference, result);
    }

    @Override
    public void onEvtConfctrlOperationResult(TsdkConference conference, TsdkConfOperationResult result) {
        Log.i(TAG, "onEvtConfctrlOperationResult notify.");
        MeetingMgr.getInstance().handleConfctrlOperationResult(conference, result);
    }


    @Override
    public void onEvtInfoAndStatusUpdate(TsdkConference conference) {
        Log.i(TAG, "onEvtInfoAndStatusUpdate notify.");
        MeetingMgr.getInstance().handleInfoAndStatusUpdate(conference);
    }

    @Override
    public void onEvtSpeakerInd(TsdkConference conference, TsdkConfSpeakerInfo speakerList) {
        Log.i(TAG, "onEvtSpeakerInd notify.");
        MeetingMgr.getInstance().handleSpeakerInd(speakerList);
    }

    @Override
    public void onEvtRequestConfRightFailed(TsdkConference conference, TsdkCommonResult result) {
        Log.i(TAG, "onEvtRequestConfRightFailed notify.");
    }

    @Override
    public void onEvtConfIncomingInd(TsdkConference conference) {
        Log.i(TAG, "onEvtConfIncomingInd notify.");
        MeetingMgr.getInstance().handleConfIncomingInd(conference);

    }

    @Override
    public void onEvtConfEndInd(TsdkConference conference) {
        Log.i(TAG, "onEvtConfEndInd notify.");
        MeetingMgr.getInstance().handleConfEndInd(conference);
    }

    @Override
    public void onEvtJoinDataConfResult(TsdkConference conference, TsdkCommonResult result) {
        Log.i(TAG, "onEvtJoinDataConfResult notify.");
        MeetingMgr.getInstance().handleJoinDataConfResult(conference, result);
    }

    @Override
    public void onEvtAsStateChange(TsdkConference conference, TsdkConfAppShareType shareType, TsdkConfAsStateInfo asStateInfo) {
        Log.i(TAG, "onEvtAsStateChange notify.");
        MeetingMgr.getInstance().handleAsStateChange(asStateInfo);
    }

    @Override
    public void onEvtDsDocNew(TsdkConference conference, TsdkDocBaseInfo docBaseInfo) {
        Log.i(TAG, "onEvtDsDocNew notify.");
        MeetingMgr.getInstance().handleDsDocNew(docBaseInfo);
    }

    @Override
    public void onEvtDsDocDel(TsdkConference conference, TsdkDocShareDelDocInfo docShareDelDocInfo) {
        Log.i(TAG, "onEvtDsDocDel notify.");
        MeetingMgr.getInstance().handleDsDocDel(docShareDelDocInfo);
    }

    @Override
    public void onEvtWbDocNew(TsdkConference conference, TsdkDocBaseInfo docBaseInfo) {
        Log.i(TAG, "onEvtWbDocNew notify.");
        MeetingMgr.getInstance().handleWbDocNew(docBaseInfo);
    }

    @Override
    public void onEvtWbDocDel(TsdkConference conference, TsdkWbDelDocInfo wbDelDocInfo) {
        Log.i(TAG, "onEvtWbDocDel notify.");
        MeetingMgr.getInstance().handleWbDocDel(wbDelDocInfo);
    }

    @Override
    public void onEvtRecvChatMsg(TsdkConference tsdkConference, TsdkConfChatMsgInfo tsdkConfChatMsgInfo) {
        Log.i(TAG, "onEvtRecvChatMsg notify.");
        MeetingMgr.getInstance().handleRecvChatMsg(tsdkConfChatMsgInfo);
    }

    @Override
    public void onEvtCtdStartCallResult(int callId, TsdkCommonResult result) {
	    Log.i(TAG, "onEvtCtdStartCallResult notify.");
        CtdMgr.getInstance().handleStartCallResult(callId, result);
    }

    @Override
    public void onEvtCtdEndCallResult(int callId, TsdkCommonResult result) {
        Log.i(TAG, "onEvtCtdEndCallResult notify.");
    }

    @Override
    public void onEvtCtdCallStatusNotify(int callId, TsdkCtdCallStatus status) {
        Log.i(TAG, "onEvtCtdCallStatusNotify notify.");
    }

    @Override
    public void onEvtSearchContactsResult(int querySeqNo, TsdkCommonResult result, TsdkSearchContactsResult searchContactResult) {
        Log.i(TAG, "onEvtSearchContactsResult notify.");
        EnterpriseAddressBookMgr.getInstance().handleSearchContactResult(querySeqNo, result, searchContactResult);
    }

    @Override
    public void onEvtSearchDeptResult(int querySeqNo, TsdkCommonResult result, TsdkSearchDepartmentResult searchDeptResult) {
        Log.i(TAG, "onEvtSearchDeptResult notify.");
        EnterpriseAddressBookMgr.getInstance().handleSearchDepartmentResult(querySeqNo, result, searchDeptResult);
    }

    @Override
    public void onEvtGetIconResult(int querySeqNo, TsdkCommonResult result, TsdkGetIconResult getIconResult) {
        Log.i(TAG, "onEvtGetIconResult notify.");
        EnterpriseAddressBookMgr.getInstance().handleGetIconResult(querySeqNo, result, getIconResult);
    }

    @Override
    public void onEvtAddFriendInd(TsdkBeAddedFriendInfo beAddedFriendInfo) {

    }

    @Override
    public void onEvtUserStatusUpdate(List<TsdkImUserStatusUpdateInfo> userStatusInfoList) {
        Log.i(TAG, "onEvtUserStatusUpdate notify.");
        ImMgr.getInstance().handleUserStatusUpdate(userStatusInfoList);
    }

    @Override
    public void onEvtUserInfoUpdate(List<TsdkImUserInfo> userInfoList) {
        Log.i(TAG, "onEvtUserInfoUpdate notify.");
        ImMgr.getInstance().handleUserInfoUpdate(userInfoList);
    }

    @Override
    public void onEvtJoinChatGroupReq(TsdkChatGroup chatGroup, TsdkReqJoinChatGroupMsg reqJoinChatGroupMsg) {
        Log.i(TAG, "onEvtJoinChatGroupReq notify.");
    }

    @Override
    public void onEvtJoinChatGroupRsp(TsdkChatGroup chatGroup, TsdkRspJoinChatGroupMsg rspJoinChatGroupMsg) {
        Log.i(TAG, "onEvtJoinChatGroupRsp notify.");
        ImMgr.getInstance().handleJoinChatGroupRsp(chatGroup, rspJoinChatGroupMsg);
    }

    @Override
    public void onEvtJoinChatGroupInd(TsdkChatGroup chatGroup, TsdkBeAddedToChatGroupInfo beAddedToChatGroupInfo) {
        Log.i(TAG, "onEvtJoinChatGroupInd notify.");
        ImMgr.getInstance().handleJoinChatGroupInd(chatGroup, beAddedToChatGroupInfo);
    }

    @Override
    public void onEvtDelChatGroupMemberResult(TsdkChatGroup chatGroup, TsdkDelChatGroupMemberResult delChatGroupMemberResult) {
        Log.i(TAG, "onEvtDelChatGroupMemberResult notify.");
    }

    @Override
    public void onEvtLeaveChatGroupResult(TsdkChatGroup chatGroup, TsdkLeaveChatGroupResult leaveChatGroupResult) {
        Log.i(TAG, "onEvtLeaveChatGroupResult notify.");
        ImMgr.getInstance().handleLeaveChatGroupResult(leaveChatGroupResult);
    }

    @Override
    public void onEvtChatGroupInfoUpdate(TsdkChatGroup chatGroup, TsdkChatGroupUpdateInfo chatGroupUpdateInfo, TsdkChatGroupInfoUpdateType updateType) {
        Log.i(TAG, "onEvtChatGroupInfoUpdate notify.");
        ImMgr.getInstance().handleChatGroupInfoUpdate(chatGroup, chatGroupUpdateInfo, updateType);
    }


    @Override
    public void onEvtInputtingStatusInd(TsdkInputtingStatusInfo inputtingStatusInfo) {
        Log.i(TAG, "onEvtInputtingStatusInd notify.");
        ImMgr.getInstance().handleInputtingStatusInd(inputtingStatusInfo);
    }

    @Override
    public void onEvtChatMsg(TsdkChatMsgInfo chatMsgInfo) {
        Log.i(TAG, "onEvtChatMsg notify.");
        ImMgr.getInstance().handleChatMsg(chatMsgInfo);
    }

    @Override
    public void onEvtBatchChatMsg(TsdkBatchChatMsgInfo batchChatMsgInfo) {
        Log.i(TAG, "onEvtBatchChatMsg notify.");
        ImMgr.getInstance().handleBatchChatMsg(batchChatMsgInfo);
    }

    @Override
    public void onEvtSystemBulletin(TsdkChatMsgInfo chatMsgInfo) {

    }

    @Override
    public void onEvtSms(TsdkSmsInfo smsInfo) {

    }

    @Override
    public void onEvtUndeliverInd(TsdkChatMsgUndeliverInfo chatMsgUndeliverInfo) {

    }

    @Override
    public void onEvtMsgReadInd(TsdkMsgReadIndInfo msgReadIndInfo) {

    }

    @Override
    public void onEvtMsgSendResult(TsdkSendChatMsgResult sendChatMsgResult) {
        Log.i(TAG, "onEvtMsgSendResult notify.");
        ImMgr.getInstance().handleMsgSendResult(sendChatMsgResult);
    }

    @Override
    public void onEvtMsgWithdrawResult(TsdkChatMsgWithdrawResult chatMsgWithdrawResult) {
        Log.i(TAG, "onEvtMsgWithdrawResult notify.");
        ImMgr.getInstance().handleMsgWithdrawResult(chatMsgWithdrawResult);
    }

    @Override
    public void onEvtMsgWithdrawInd(TsdkChatMsgWithdrawInfo chatMsgWithdrawInfo) {
        Log.i(TAG, "onEvtMsgWithdrawInd notify.");
        ImMgr.getInstance().handleMsgWithdrawInd(chatMsgWithdrawInfo);
    }

    @Override
    public void onEvtTransToConfResult(TsdkCall call, TsdkCommonResult result) {

    }
}
