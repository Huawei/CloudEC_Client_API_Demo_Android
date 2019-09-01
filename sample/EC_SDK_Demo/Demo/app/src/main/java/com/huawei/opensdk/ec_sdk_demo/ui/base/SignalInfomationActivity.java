package com.huawei.opensdk.ec_sdk_demo.ui.base;

import android.content.Intent;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huawei.ecterminalsdk.base.TsdkAudioStreamInfo;
import com.huawei.ecterminalsdk.base.TsdkCallStatisticInfo;
import com.huawei.ecterminalsdk.base.TsdkShareStatisticInfo;
import com.huawei.ecterminalsdk.base.TsdkVideoStreamInfo;
import com.huawei.opensdk.callmgr.CallInfo;
import com.huawei.opensdk.callmgr.CallMgr;
import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcastReceiver;
import com.huawei.opensdk.demoservice.MeetingMgr;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;

import java.util.List;

public class SignalInfomationActivity extends BaseActivity implements View.OnClickListener,LocBroadcastReceiver{

    private ImageView signalClose;
    private TextView audioSendBandwidth;
    private TextView audioSendLost;
    private TextView audioSendDelay;
    private TextView audioSendJitter;
    private TextView audioReceiveBandwidth;
    private TextView audioReceiveLost;
    private TextView audioReceiveDelay;
    private TextView audioReceiveJitter;

    private TextView videoSendBandwidth;
    private TextView videoSendLost;
    private TextView videoSendDelay;
    private TextView videoSendJitter;
    private TextView videoReceiveBandwidth;
    private TextView videoReceiveLost;
    private TextView videoReceiveDelay;
    private TextView videoReceiveJitter;
    private TextView videoSendResolution;
    private TextView videoSendFrame;
    private TextView videoReceiveResolution;
    private TextView videoReceiveFrame;

    private TextView videoSendBandwidth_two;
    private TextView videoSendLost_two;
    private TextView videoSendDelay_two;
    private TextView videoSendJitter_two;
    private TextView videoReceiveBandwidth_two;
    private TextView videoReceiveLost_two;
    private TextView videoReceiveDelay_two;
    private TextView videoReceiveJitter_two;
    private TextView videoSendResolution_two;
    private TextView videoSendFrame_two;
    private TextView videoReceiveResolution_two;
    private TextView videoReceiveFrame_two;

    private TextView videoSendBandwidth_three;
    private TextView videoSendLost_three;
    private TextView videoSendDelay_three;
    private TextView videoSendJitter_three;
    private TextView videoReceiveBandwidth_three;
    private TextView videoReceiveLost_three;
    private TextView videoReceiveDelay_three;
    private TextView videoReceiveJitter_three;
    private TextView videoSendResolution_three;
    private TextView videoSendFrame_three;
    private TextView videoReceiveResolution_three;
    private TextView videoReceiveFrame_three;

    private TextView videoSendBandwidth_four;
    private TextView videoSendLost_four;
    private TextView videoSendDelay_four;
    private TextView videoSendJitter_four;
    private TextView videoReceiveBandwidth_four;
    private TextView videoReceiveLost_four;
    private TextView videoReceiveDelay_four;
    private TextView videoReceiveJitter_four;
    private TextView videoSendResolution_four;
    private TextView videoSendFrame_four;
    private TextView videoReceiveResolution_four;
    private TextView videoReceiveFrame_four;

    private TextView shareSendBandwidth;
    private TextView shareSendDelay;
    private TextView shareReceiveBandwidth;
    private TextView shareReceiveDelay;
    private TextView shareSendResolution;
    private TextView shareSendFrame;
    private TextView shareReceiveResolution;
    private TextView shareReceiveFrame;

    private LinearLayout sendTwo;
    private LinearLayout receiveTwo;
    private LinearLayout sendThree;
    private LinearLayout receiveThree;
    private LinearLayout sendFour;
    private LinearLayout receiveFour;
    private LinearLayout sendFive;
    private LinearLayout receiveFive;

    private LinearLayout shareData;
    private LinearLayout shareCategory;
    private LinearLayout shareSend;
    private LinearLayout shareReceive;

    private CallInfo mCallInfo;

    private TextView localSendNameOne;
    private TextView localSendNameTwo;
    private TextView localSendNameThree;
    private TextView localSendNameFour;
    private TextView localReceNameOne;
    private TextView localReceNameTwo;
    private TextView localReceNameThree;
    private TextView localReceNameFour;

    private String[] mActions = new String[]{
            CustomBroadcastConstants.ACTION_CALL_END,
            CustomBroadcastConstants.GET_CONF_END};
    @Override
    public void initializeData() {
        setContentView(R.layout.activity_signal);
        signalClose = (ImageView)findViewById(R.id.signal_close);
        //音频模块
        audioSendBandwidth = (TextView)findViewById(R.id.audio_send_bandwidth);
        audioSendLost = (TextView)findViewById(R.id.audio_send_lost);
        audioSendDelay = (TextView)findViewById(R.id.audio_send_delay);
        audioSendJitter = (TextView)findViewById(R.id.audio_send_jitter);
        audioReceiveBandwidth = (TextView)findViewById(R.id.audio_receive_bandwidth);
        audioReceiveLost = (TextView)findViewById(R.id.audio_receive_lost);
        audioReceiveDelay = (TextView)findViewById(R.id.audio_receive_delay);
        audioReceiveJitter = (TextView)findViewById(R.id.audio_receive_jitter);
        //视频模块

        localSendNameOne = (TextView)findViewById(R.id.local_send_name_one);
        localReceNameOne = (TextView)findViewById(R.id.local_rece_name_one);
        sendTwo= (LinearLayout)findViewById(R.id.send_two);
        receiveTwo = (LinearLayout)findViewById(R.id.receive_two);
        videoSendBandwidth = (TextView)findViewById(R.id.video_send_bandwidth_two);
        videoSendLost = (TextView)findViewById(R.id.video_send_lost_two);
        videoSendDelay = (TextView)findViewById(R.id.video_send_delay_two);
        videoSendJitter = (TextView)findViewById(R.id.video_send_jitter_two);
        videoReceiveBandwidth = (TextView)findViewById(R.id.video_receive_bandwidth_two);
        videoReceiveLost = (TextView)findViewById(R.id.video_receive_lost_two);
        videoReceiveDelay = (TextView)findViewById(R.id.video_receive_delay_two);
        videoReceiveJitter = (TextView)findViewById(R.id.video_receive_jitter_two);
        videoSendResolution = (TextView)findViewById(R.id.video_send_resolution_two);
        videoSendFrame = (TextView)findViewById(R.id.video_send_frame_two);
        videoReceiveResolution = (TextView)findViewById(R.id.video_receive_resolution_two);
        videoReceiveFrame = (TextView)findViewById(R.id.video_receive_frame_two);

        localSendNameTwo = (TextView)findViewById(R.id.local_send_name_two);
        localReceNameTwo = (TextView)findViewById(R.id.local_rece_name_two);
        sendThree = (LinearLayout)findViewById(R.id.send_three);
        receiveThree = (LinearLayout)findViewById(R.id.receive_three);
        videoSendBandwidth_two = (TextView)findViewById(R.id.video_send_bandwidth_three);
        videoSendLost_two = (TextView)findViewById(R.id.video_send_lost_three);
        videoSendDelay_two = (TextView)findViewById(R.id.video_send_delay_three);
        videoSendJitter_two = (TextView)findViewById(R.id.video_send_jitter_three);
        videoReceiveBandwidth_two = (TextView)findViewById(R.id.video_receive_bandwidth_three);
        videoReceiveLost_two = (TextView)findViewById(R.id.video_receive_lost_three);
        videoReceiveDelay_two = (TextView)findViewById(R.id.video_receive_delay_three);
        videoReceiveJitter_two = (TextView)findViewById(R.id.video_receive_jitter_three);
        videoSendResolution_two = (TextView)findViewById(R.id.video_send_resolution_three);
        videoSendFrame_two = (TextView)findViewById(R.id.video_send_frame_three);
        videoReceiveResolution_two = (TextView)findViewById(R.id.video_receive_resolution_three);
        videoReceiveFrame_two = (TextView)findViewById(R.id.video_receive_frame_three);

        localSendNameThree = (TextView)findViewById(R.id.local_send_name_three);
        localReceNameThree = (TextView)findViewById(R.id.local_rece_name_three);
        sendFour = (LinearLayout)findViewById(R.id.send_four);
        receiveFour = (LinearLayout)findViewById(R.id.receive_four);
        videoSendBandwidth_three = (TextView)findViewById(R.id.video_send_bandwidth_four);
        videoSendLost_three = (TextView)findViewById(R.id.video_send_lost_four);
        videoSendDelay_three = (TextView)findViewById(R.id.video_send_delay_four);
        videoSendJitter_three = (TextView)findViewById(R.id.video_send_jitter_four);
        videoReceiveBandwidth_three = (TextView)findViewById(R.id.video_receive_bandwidth_four);
        videoReceiveLost_three = (TextView)findViewById(R.id.video_receive_lost_four);
        videoReceiveDelay_three = (TextView)findViewById(R.id.video_receive_delay_four);
        videoReceiveJitter_three = (TextView)findViewById(R.id.video_receive_jitter_four);
        videoSendResolution_three = (TextView)findViewById(R.id.video_send_resolution_four);
        videoSendFrame_three = (TextView)findViewById(R.id.video_send_frame_four);
        videoReceiveResolution_three = (TextView)findViewById(R.id.video_receive_resolution_four);
        videoReceiveFrame_three = (TextView)findViewById(R.id.video_receive_frame_four);

        localSendNameFour = (TextView)findViewById(R.id.local_send_name_four);
        localReceNameFour = (TextView)findViewById(R.id.local_rece_name_four);
        sendFive = (LinearLayout)findViewById(R.id.send_five);
        receiveFive = (LinearLayout)findViewById(R.id.receive_five);
        videoSendBandwidth_four = (TextView)findViewById(R.id.video_send_bandwidth_five);
        videoSendLost_four = (TextView)findViewById(R.id.video_send_lost_five);
        videoSendDelay_four = (TextView)findViewById(R.id.video_send_delay_five);
        videoSendJitter_four = (TextView)findViewById(R.id.video_send_jitter_five);
        videoReceiveBandwidth_four = (TextView)findViewById(R.id.video_receive_bandwidth_five);
        videoReceiveLost_four = (TextView)findViewById(R.id.video_receive_lost_five);
        videoReceiveDelay_four = (TextView)findViewById(R.id.video_receive_delay_five);
        videoReceiveJitter_four = (TextView)findViewById(R.id.video_receive_jitter_five);
        videoSendResolution_four = (TextView)findViewById(R.id.video_send_resolution_five);
        videoSendFrame_four = (TextView)findViewById(R.id.video_send_frame_five);
        videoReceiveResolution_four = (TextView)findViewById(R.id.video_receive_resolution_five);
        videoReceiveFrame_four = (TextView)findViewById(R.id.video_receive_frame_five);

        //共享数据模块
        shareData = (LinearLayout)findViewById(R.id.share_data);
        shareCategory = (LinearLayout)findViewById(R.id.share_category);
        shareSend = (LinearLayout)findViewById(R.id.share_send);
        shareReceive = (LinearLayout)findViewById(R.id.share_receive);

        shareSendBandwidth = (TextView)findViewById(R.id.share_send_bandwidth);
        shareSendDelay = (TextView)findViewById(R.id.share_send_delay);
        shareReceiveBandwidth = (TextView)findViewById(R.id.share_receive_bandwidth);
        shareReceiveDelay = (TextView)findViewById(R.id.share_receive_delay);
        shareSendResolution = (TextView)findViewById(R.id.share_send_resolution);
        shareSendFrame = (TextView)findViewById(R.id.share_send_frame);
        shareReceiveResolution = (TextView)findViewById(R.id.share_receive_resolution);
        shareReceiveFrame = (TextView)findViewById(R.id.share_receive_frame);


        signalClose.setOnClickListener(this);

        shareData.setVisibility(View.GONE);
        shareCategory.setVisibility(View.GONE);
        shareSend.setVisibility(View.GONE);
        shareReceive.setVisibility(View.GONE);
    }

    @Override
    public void initializeComposition() {
        setWindowsSize();
        showLineView();
        updateSignalData();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.signal_close:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onReceive(String broadcastName, Object obj) {
        switch (broadcastName){
            case CustomBroadcastConstants.ACTION_CALL_END:
                finish();
                break;
            case CustomBroadcastConstants.GET_CONF_END:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        mCallInfo = (CallInfo) intent.getSerializableExtra(UIConstants.CALL_INFO);
        LocBroadcast.getInstance().registerBroadcast(this, mActions);

        if(MeetingMgr.getInstance().getCurrentConferenceSelf()!=null && MeetingMgr.getInstance().getCurrentConferenceSelf().isInDataConference()){
            shareData.setVisibility(View.VISIBLE);
            shareCategory.setVisibility(View.VISIBLE);
            shareSend.setVisibility(View.VISIBLE);
            shareReceive.setVisibility(View.VISIBLE);
            getShareStatisticInfo();
            getCallStatisticInfo();
        }else {
            getCallStatisticInfo();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocBroadcast.getInstance().unRegisterBroadcast(this, mActions);
    }

    private void updateSignalData(){
        TsdkCallStatisticInfo callStatisticInfo = CallMgr.getInstance().getCurrentCallStatisticInfo();
        if (callStatisticInfo == null)
        {
            return;
        }
        //音频数据
        TsdkAudioStreamInfo audioStreamInfo = callStatisticInfo.getAudioStreamInfo();
        if (audioStreamInfo != null)
        {
            audioSendBandwidth.setText(audioStreamInfo.getSendBitRate()+"");
            audioSendLost.setText(audioStreamInfo.getSendLossFraction()+"");
            audioSendDelay.setText(audioStreamInfo.getSendDelay()+"");
            audioSendJitter.setText(audioStreamInfo.getSendJitter()+"");
            audioReceiveBandwidth.setText(audioStreamInfo.getRecvBitRate()+"");
            audioReceiveLost.setText(audioStreamInfo.getRecvLossFraction()+"");
            audioReceiveDelay.setText(audioStreamInfo.getRecvDelay()+"");
            audioReceiveJitter.setText(audioStreamInfo.getRecvJitter()+"");
        }


        TsdkVideoStreamInfo videoStreamInfo = callStatisticInfo.getVideoStreamInfo();
        List<TsdkVideoStreamInfo> videoStreamInfoList = callStatisticInfo.getSvcStreamInfo();
        if (callStatisticInfo.getIsSvcConf()==0){
            if (videoStreamInfo == null)
            {
                return;
            }
            //单流视频
            if(videoStreamInfo.getSendFrameSize().equals("")||videoStreamInfo.getSendFrameSize().equals("0*0")){
                showTextView(sendTwo,View.GONE);
            }
            if(videoStreamInfo.getSendFrameSize().equals("")||videoStreamInfo.getSendFrameSize().equals("0*0")){
                showTextView(receiveTwo,View.GONE);
            }
            videoSendBandwidth.setText((videoStreamInfo.getSendBitRate()/1000)+"");
            videoSendLost.setText(videoStreamInfo.getSendLossFraction()+"");
            videoSendDelay.setText(videoStreamInfo.getSendDelay()+"");
            videoSendJitter.setText(videoStreamInfo.getSendJitter()+"");
            videoReceiveBandwidth.setText(videoStreamInfo.getRecvBitRate()/1000+"");
            videoReceiveLost.setText(videoStreamInfo.getRecvLossFraction()+"");
            videoReceiveDelay.setText(videoStreamInfo.getRecvDelay()+"");
            videoReceiveJitter.setText(videoStreamInfo.getRecvJitter()+"");
            videoSendResolution.setText(videoStreamInfo.getSendFrameSize());
            videoSendFrame.setText(videoStreamInfo.getSendFrameRate()+"");
            videoReceiveResolution.setText(videoStreamInfo.getRecvFrameSize());
            videoReceiveFrame.setText(videoStreamInfo.getRecvFrameRate()+"");

        }else {
            //多流视频
            if (videoStreamInfoList.size()>0){
                if (videoStreamInfoList.get(0).getSendFrameSize().equals("")||videoStreamInfoList.get(0).getSendFrameSize().equals("0*0")){
                    showTextView(sendTwo,View.GONE);
                }
                if(videoStreamInfoList.get(0).getRecvFrameSize().equals("")||videoStreamInfoList.get(0).getRecvFrameSize().equals("0*0")){
                    showTextView(receiveTwo,View.GONE);
                }
                localReceNameOne.setText(videoStreamInfoList.get(0).getRecvSsrcLabel()+"接收");
                videoSendBandwidth.setText((videoStreamInfoList.get(0).getSendBitRate()/1000)+"");
                videoSendLost.setText(videoStreamInfoList.get(0).getSendLossFraction()+"");
                videoSendDelay.setText(videoStreamInfoList.get(0).getSendDelay()+"");
                videoSendJitter.setText(videoStreamInfoList.get(0).getSendJitter()+"");
                videoReceiveBandwidth.setText(videoStreamInfoList.get(0).getRecvBitRate()/1000+"");
                videoReceiveLost.setText(videoStreamInfoList.get(0).getRecvLossFraction()+"");
                videoReceiveDelay.setText(videoStreamInfoList.get(0).getRecvDelay()+"");
                videoReceiveJitter.setText(videoStreamInfoList.get(0).getRecvJitter()+"");
                videoSendResolution.setText(videoStreamInfoList.get(0).getSendFrameSize());
                videoSendFrame.setText(videoStreamInfoList.get(0).getSendFrameRate()+"");
                videoReceiveResolution.setText(videoStreamInfoList.get(0).getRecvFrameSize());
                videoReceiveFrame.setText(videoStreamInfoList.get(0).getRecvFrameRate()+"");
            }
            if (videoStreamInfoList.size()>1){
                if (videoStreamInfoList.get(1).getSendFrameSize().equals("")||videoStreamInfoList.get(1).getSendFrameSize().equals("0*0")){
                    showTextView(sendThree,View.GONE);
                }
                if(videoStreamInfoList.get(1).getRecvFrameSize().equals("")||videoStreamInfoList.get(1).getRecvFrameSize().equals("0*0")){
                    showTextView(receiveThree,View.GONE);
                }
                localReceNameTwo.setText(videoStreamInfoList.get(1).getRecvSsrcLabel()+"接收");
                videoSendBandwidth_two.setText((videoStreamInfoList.get(1).getSendBitRate()/1000)+"");
                videoSendLost_two.setText(videoStreamInfoList.get(1).getSendLossFraction()+"");
                videoSendDelay_two.setText(videoStreamInfoList.get(1).getSendDelay()+"");
                videoSendJitter_two.setText(videoStreamInfoList.get(1).getSendJitter()+"");
                videoReceiveBandwidth_two.setText(videoStreamInfoList.get(1).getRecvBitRate()/1000+"");
                videoReceiveLost_two.setText(videoStreamInfoList.get(1).getRecvLossFraction()+"");
                videoReceiveDelay_two.setText(videoStreamInfoList.get(1).getRecvDelay()+"");
                videoReceiveJitter_two.setText(videoStreamInfoList.get(1).getRecvJitter()+"");
                videoSendResolution_two.setText(videoStreamInfoList.get(1).getSendFrameSize());
                videoSendFrame_two.setText(videoStreamInfoList.get(1).getSendFrameRate()+"");
                videoReceiveResolution_two.setText(videoStreamInfoList.get(1).getRecvFrameSize());
                videoReceiveFrame_two.setText(videoStreamInfoList.get(1).getRecvFrameRate()+"");

            }
            if (videoStreamInfoList.size()>2){
                if (videoStreamInfoList.get(2).getSendFrameSize().equals("")||videoStreamInfoList.get(2).getSendFrameSize().equals("0*0")){
                    showTextView(sendFour,View.GONE);
                }
                if("0*0".equals(videoStreamInfoList.get(2).getRecvFrameSize())||videoStreamInfoList.get(2).getRecvFrameSize().equals("")){
                    showTextView(receiveFour,View.GONE);
                }
                localReceNameThree.setText(videoStreamInfoList.get(2).getRecvSsrcLabel()+"接收");
                videoSendBandwidth_three.setText((videoStreamInfoList.get(2).getSendBitRate()/1000)+"");
                videoSendLost_three.setText(videoStreamInfoList.get(2).getSendLossFraction()+"");
                videoSendDelay_three.setText(videoStreamInfoList.get(2).getSendDelay()+"");
                videoSendJitter_three.setText(videoStreamInfoList.get(2).getSendJitter()+"");
                videoReceiveBandwidth_three.setText(videoStreamInfoList.get(2).getRecvBitRate()/1000+"");
                videoReceiveLost_three.setText(videoStreamInfoList.get(2).getRecvLossFraction()+"");
                videoReceiveDelay_three.setText(videoStreamInfoList.get(2).getRecvDelay()+"");
                videoReceiveJitter_three.setText(videoStreamInfoList.get(2).getRecvJitter()+"");
                videoSendResolution_three.setText(videoStreamInfoList.get(2).getSendFrameSize());
                videoSendFrame_three.setText(videoStreamInfoList.get(2).getSendFrameRate()+"");
                videoReceiveResolution_three.setText(videoStreamInfoList.get(2).getRecvFrameSize());
                videoReceiveFrame_three.setText(videoStreamInfoList.get(2).getRecvFrameRate()+"");

            }
            if (videoStreamInfoList.size()>3){
                if (videoStreamInfoList.get(3).getSendFrameSize().equals("")||videoStreamInfoList.get(3).getSendFrameSize().equals("0*0")){
                    showTextView(sendFive,View.GONE);
                }
                if(videoStreamInfoList.get(3).getRecvFrameSize().equals("")||videoStreamInfoList.get(3).getRecvFrameSize().equals("0*0")){
                    showTextView(receiveFive,View.GONE);
                }
                localReceNameFour.setText(videoStreamInfoList.get(3).getRecvSsrcLabel()+"接收");
                videoSendBandwidth_four.setText((videoStreamInfoList.get(3).getSendBitRate()/1000)+"");
                videoSendLost_four.setText(videoStreamInfoList.get(3).getSendLossFraction()+"");
                videoSendDelay_four.setText(videoStreamInfoList.get(3).getSendDelay()+"");
                videoSendJitter_four.setText(videoStreamInfoList.get(3).getSendJitter()+"");
                videoReceiveBandwidth_four.setText(videoStreamInfoList.get(3).getRecvBitRate()/1000+"");
                videoReceiveLost_four.setText(videoStreamInfoList.get(3).getRecvLossFraction()+"");
                videoReceiveDelay_four.setText(videoStreamInfoList.get(3).getRecvDelay()+"");
                videoReceiveJitter_four.setText(videoStreamInfoList.get(3).getRecvJitter()+"");
                videoSendResolution_four.setText(videoStreamInfoList.get(3).getSendFrameSize());
                videoSendFrame_four.setText(videoStreamInfoList.get(3).getSendFrameRate()+"");
                videoReceiveResolution_four.setText(videoStreamInfoList.get(3).getRecvFrameSize());
                videoReceiveFrame_four.setText(videoStreamInfoList.get(3).getRecvFrameRate()+"");

            }
        }

        TsdkShareStatisticInfo shareStatisticInfo = MeetingMgr.getInstance().getCurrentShareStatisticInfo();
        if (shareStatisticInfo==null){
            return;
        }
        if (shareStatisticInfo.getStatus()==1){
            shareSendBandwidth.setText(shareStatisticInfo.getSendBitRate()+"");
            shareSendDelay.setText("");
            shareSendResolution.setText(shareStatisticInfo.getSendFrameSizeHeight()+"*"+shareStatisticInfo.getSendFrameSizeHeight());
            shareSendFrame.setText(shareStatisticInfo.getSendFrameRate()+"");
        }else {
            shareReceiveBandwidth.setText(shareStatisticInfo.getRecvBitRate()+"");
            shareReceiveDelay.setText("");
            shareReceiveResolution.setText(shareStatisticInfo.getRecvFrameSizeHeight()+"*"+shareStatisticInfo.getRecvFrameSizeWidth());
            shareReceiveFrame.setText(shareStatisticInfo.getRecvFrameRate()+"");
        }


    }

    private void showTextView(final LinearLayout view, final int isGone){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.setVisibility(isGone);
            }
        });
    }

    private void showLineView(){
        TsdkCallStatisticInfo callStatisticInfo = CallMgr.getInstance().getCurrentCallStatisticInfo();
        if (callStatisticInfo == null)
        {
            return;
        }
        int count = callStatisticInfo.getSvcStreamCount();
        if (count==1||count==0){
            sendThree.setVisibility(View.GONE);
            receiveThree.setVisibility(View.GONE);
            sendFour.setVisibility(View.GONE);
            receiveFour.setVisibility(View.GONE);
            sendFive.setVisibility(View.GONE);
            receiveFive.setVisibility(View.GONE);
        }else if(count==2){
            sendThree.setVisibility(View.VISIBLE);
            receiveThree.setVisibility(View.VISIBLE);
            sendFour.setVisibility(View.GONE);
            receiveFour.setVisibility(View.GONE);
            sendFive.setVisibility(View.GONE);
            receiveFive.setVisibility(View.GONE);
        }else if(count==3){
            sendThree.setVisibility(View.VISIBLE);
            receiveThree.setVisibility(View.VISIBLE);
            sendFour.setVisibility(View.VISIBLE);
            receiveFour.setVisibility(View.VISIBLE);
            sendFive.setVisibility(View.GONE);
            receiveFive.setVisibility(View.GONE);
        }else {
            sendThree.setVisibility(View.VISIBLE);
            receiveThree.setVisibility(View.VISIBLE);
            sendFour.setVisibility(View.VISIBLE);
            receiveFour.setVisibility(View.VISIBLE);
            sendFive.setVisibility(View.VISIBLE);
            receiveFive.setVisibility(View.VISIBLE);
        }
    }

    private void getShareStatisticInfo(){
        MeetingMgr.getInstance().getShareStatisticInfo();
    }

    private void getCallStatisticInfo(){
        CallMgr.getInstance().getCallStatisticInfo(mCallInfo.getCallID());
    }

    private void setWindowsSize(){
        /*设置窗口样式activity宽高start*/
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay();  //为获取屏幕宽、高
        WindowManager.LayoutParams p = getWindow().getAttributes();  //获取对话框当前的参数值
        p.height = (int) (d.getHeight() * 0.8);   //高度设置为屏幕的0.6
        p.width = (int) (d.getWidth() * 0.8);    //宽度设置为屏幕的0.7
        p.alpha = 0.5f;      //设置本身透明度
        p.dimAmount = 0.0f;      //设置窗口外黑暗度
        getWindow().setAttributes(p);
        /*设置窗口样式activity宽高end*/
    }
}
