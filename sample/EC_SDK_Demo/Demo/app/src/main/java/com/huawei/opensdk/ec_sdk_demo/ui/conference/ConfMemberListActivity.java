package com.huawei.opensdk.ec_sdk_demo.ui.conference;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.opensdk.callmgr.CallConstant;
import com.huawei.opensdk.callmgr.CallMgr;
import com.huawei.opensdk.demoservice.ConfBaseInfo;
import com.huawei.opensdk.demoservice.ConfConstant;
import com.huawei.opensdk.demoservice.MeetingMgr;
import com.huawei.opensdk.demoservice.Member;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.adapter.ConfManagerAdapter;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.logic.conference.mvp.ConfMemberListPresenter;
import com.huawei.opensdk.ec_sdk_demo.logic.conference.mvp.IAttendeeListContract;
import com.huawei.opensdk.ec_sdk_demo.ui.base.MVPBaseActivity;
import com.huawei.opensdk.ec_sdk_demo.util.CommonUtil;
import com.huawei.opensdk.ec_sdk_demo.widget.ConfirmSimpleDialog;
import com.huawei.opensdk.ec_sdk_demo.widget.EditDialog;
import com.huawei.opensdk.ec_sdk_demo.widget.SimpleListDialog;
import com.huawei.opensdk.ec_sdk_demo.widget.ThreeInputDialog;

import java.util.List;

import static com.huawei.ecterminalsdk.base.TsdkConfEnvType.TSDK_E_CONF_ENV_HOSTED_CONVERGENT_CONFERENCE;


public class ConfMemberListActivity extends MVPBaseActivity<IAttendeeListContract.IAttendeeListView, ConfMemberListPresenter>
        implements IAttendeeListContract.IAttendeeListView, View.OnClickListener
{

    private ConfMemberListPresenter mPresenter;
    private ConfManagerAdapter adapter;

    private LinearLayout confButtonGroup;
    private ListView confListView;
    private FrameLayout mMuteSelfFL;
    private FrameLayout mAddAttendeeFL;
    private FrameLayout mLoudSpeakerFL;
    private FrameLayout mBtnMoreFL;
    private TextView titleTV;
    PopupWindow mPopupWindow;
    private TextView tvSpeakerOne;
    private TextView tvSpeakerTwo;
    private ImageView ivSpeakerOne;
    private ImageView ivSpeakerTwo;
    private FrameLayout mHideVideoView;
    private FrameLayout mLocalVideoView;

    private String confID;
    private boolean isVideo = false;
    private boolean isDateConf = false;
    private Handler mHandler = new Handler();
    private boolean isTiming = true;

    @Override
    protected IAttendeeListContract.IAttendeeListView createView()
    {
        return this;
    }

    @Override
    protected ConfMemberListPresenter createPresenter()
    {
        mPresenter = new ConfMemberListPresenter();
        return mPresenter;
    }

    @Override
    public void initializeComposition()
    {
        // keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.conf_member_list_activity);
        confButtonGroup = (LinearLayout) findViewById(R.id.media_btn_group);
        confListView = (ListView) findViewById(R.id.member_list);
        mMuteSelfFL = (FrameLayout) findViewById(R.id.conf_mute);
        mAddAttendeeFL = (FrameLayout) findViewById(R.id.conf_add_attendee);
        mLoudSpeakerFL = (FrameLayout) findViewById(R.id.conf_loud_speaker);
        mBtnMoreFL = (FrameLayout) findViewById(R.id.conf_more);
        titleTV = (TextView) findViewById(R.id.title_text);
        tvSpeakerOne = (TextView) findViewById(R.id.speaker_one);
        tvSpeakerTwo = (TextView) findViewById(R.id.speaker_two);
        ivSpeakerOne = (ImageView) findViewById(R.id.speaker_image_one);
        ivSpeakerTwo = (ImageView) findViewById(R.id.speaker_image_two);
        mHideVideoView = (FrameLayout) findViewById(R.id.hide_video_view);
        mLocalVideoView = (FrameLayout) findViewById(R.id.local_video_view);

        ivSpeakerOne.setVisibility(View.GONE);
        ivSpeakerTwo.setVisibility(View.GONE);

        mMuteSelfFL.setOnClickListener(this);
        mLoudSpeakerFL.setOnClickListener(this);
        mAddAttendeeFL.setOnClickListener(this);
        mBtnMoreFL.setOnClickListener(this);

        confListView.setAdapter(adapter);

        if (null != mPresenter.updateAttendeeList() && 0 != mPresenter.updateAttendeeList().size())
        {
            adapter.setData(mPresenter.updateAttendeeList());
            adapter.notifyDataSetChanged();
        }

        mPresenter.updateConfBaseInfo();

        confListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                mPresenter.onItemClick(position);
            }
        });

        updateLoudSpeakerButton(CallMgr.getInstance().getCurrentAudioRoute());
        updateAddAttendeeButton(mPresenter.isChairMan());
        updateMuteButton(mPresenter.isMuteSelf());
    }

    @Override
    public void initializeData()
    {
        Intent intent = getIntent();
        confID = intent.getStringExtra(UIConstants.CONF_ID);
        isVideo = intent.getBooleanExtra(UIConstants.IS_VIDEO_CONF, false);
        isDateConf = intent.getBooleanExtra(UIConstants.IS_DATE_CONF, false);

        mPresenter.setConfID(confID);
        mPresenter.registerBroadcast();
        adapter = new ConfManagerAdapter(this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if (isVideo)
        {
            mPresenter.setVideoContainer(this, mLocalVideoView, mHideVideoView);
        }
    }

    @Override
    public void showLoading()
    {

    }

    @Override
    public void dismissLoading()
    {

    }

    @Override
    public void showCustomToast(final int resID)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                showToast(resID);
            }
        });
    }

    @Override
    protected void onBack()
    {
        super.onBack();
        mPresenter.unregisterBroadcast();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.conf_mute:
                mPresenter.muteSelf();
                break;
            case R.id.conf_loud_speaker:
                mPresenter.switchLoudSpeaker();
                break;
            case R.id.conf_add_attendee:
                showAddMemberDialog();
                break;
            case R.id.conf_more:
                showMoreButton();
                break;
            default:
                break;
        }
    }

    private void showMoreButton()
    {
        int wrap = LinearLayout.LayoutParams.WRAP_CONTENT;
        View popupView = getLayoutInflater().inflate(R.layout.popup_conf_btn_list, null);

        LinearLayout switchCameraBtn = (LinearLayout) popupView.findViewById(R.id.switch_camera_ll);
        LinearLayout closeCameraBtn = (LinearLayout) popupView.findViewById(R.id.close_camera_ll);
        LinearLayout handUpLayout = (LinearLayout) popupView.findViewById(R.id.hand_up_ll);
        LinearLayout muteAllLayout = (LinearLayout) popupView.findViewById(R.id.mute_all_ll);
        LinearLayout cancelMuteAllLayout = (LinearLayout) popupView.findViewById(R.id.cancel_mute_all_ll);
        LinearLayout lockLayout = (LinearLayout) popupView.findViewById(R.id.lock_conf_ll);
        LinearLayout unlockLayout = (LinearLayout) popupView.findViewById(R.id.un_lock_conf_ll);
        LinearLayout startRecordLayout = (LinearLayout) popupView.findViewById(R.id.start_record_ll);
        LinearLayout endRecordLayout = (LinearLayout) popupView.findViewById(R.id.stop_record_ll);
        LinearLayout upgradeLayout = (LinearLayout) popupView.findViewById(R.id.upgrade_conf_ll);
        LinearLayout requestChairManLayout = (LinearLayout) popupView.findViewById(R.id.request_chairman_ll);
        LinearLayout releaseChairManLayout = (LinearLayout) popupView.findViewById(R.id.release_chairman_ll);
        LinearLayout seConfModeLayout = (LinearLayout) popupView.findViewById(R.id.set_conf_mode_ll);
        LinearLayout hideVideoLayout = (LinearLayout) popupView.findViewById(R.id.hide_remote_video_ll);
        ImageView handUpIV = (ImageView) popupView.findViewById(R.id.hand_up_iv);
        TextView handUpTV = (TextView) popupView.findViewById(R.id.hand_up_tv);

        // 与会者列表界面中视频相关的功能屏蔽
        switchCameraBtn.setVisibility(View.GONE);
        closeCameraBtn.setVisibility(View.GONE);
        seConfModeLayout.setVisibility(View.GONE);
        hideVideoLayout.setVisibility(View.GONE);

        // 主席：会场静音、锁定、释放主席权限； 普通与会者：举手、申请主席
        if (mPresenter.isChairMan())
        {
            if (mPresenter.isConfMute())
            {
                cancelMuteAllLayout.setVisibility(View.VISIBLE);
                muteAllLayout.setVisibility(View.GONE);
            }
            else
            {
                cancelMuteAllLayout.setVisibility(View.GONE);
                muteAllLayout.setVisibility(View.VISIBLE);
            }

            if (isDateConf)
            {
                upgradeLayout.setVisibility(View.GONE);
            }
            else
            {
                upgradeLayout.setVisibility(View.VISIBLE);
            }

            // 融合会议显示锁定
            if(TSDK_E_CONF_ENV_HOSTED_CONVERGENT_CONFERENCE == MeetingMgr.getInstance().getConfEnvType())
            {
                if (mPresenter.isConfLock())
                {
//                    unlockLayout.setVisibility(View.VISIBLE);
                    lockLayout.setVisibility(View.GONE);
                }
                else
                {
                    unlockLayout.setVisibility(View.GONE);
//                    lockLayout.setVisibility(View.VISIBLE);
                }
            }
            else
            {
                unlockLayout.setVisibility(View.GONE);
                lockLayout.setVisibility(View.GONE);
            }

            if(mPresenter.isSupportRecord()) {
                if (mPresenter.isRecord()) {
                    startRecordLayout.setVisibility(View.GONE);
                    endRecordLayout.setVisibility(View.VISIBLE);
                    //mRecordTip.setVisibility(View.VISIBLE);
                } else {
                    startRecordLayout.setVisibility(View.VISIBLE);
                    endRecordLayout.setVisibility(View.GONE);
                    //mRecordTip.setVisibility(View.GONE);
                }
            }else
            {
                startRecordLayout.setVisibility(View.GONE);
                endRecordLayout.setVisibility(View.GONE);
            }

            requestChairManLayout.setVisibility(View.GONE);
            releaseChairManLayout.setVisibility(View.VISIBLE);
            handUpLayout.setVisibility(View.GONE);
        }
        else
        {
            cancelMuteAllLayout.setVisibility(View.GONE);
            muteAllLayout.setVisibility(View.GONE);
            unlockLayout.setVisibility(View.GONE);
            lockLayout.setVisibility(View.GONE);
            upgradeLayout.setVisibility(View.GONE);

            requestChairManLayout.setVisibility(View.VISIBLE);
            releaseChairManLayout.setVisibility(View.GONE);

            startRecordLayout.setVisibility(View.GONE);
            endRecordLayout.setVisibility(View.GONE);

            if(TSDK_E_CONF_ENV_HOSTED_CONVERGENT_CONFERENCE == MeetingMgr.getInstance().getConfEnvType())
            {
                if (mPresenter.isHandUp())
                {
                    handUpIV.setActivated(false);
                    handUpTV.setText(R.string.conf_cancel_hand_up);
                }
                else
                {
                    handUpIV.setActivated(true);
                    handUpTV.setText(R.string.conf_hand_up);
                }
            }
            else
            {
                handUpLayout.setVisibility(View.GONE);
            }
        }

        // ido 不显示举手
        if (ConfConstant.ConfProtocol.IDO_PROTOCOL == MeetingMgr.getInstance().getConfProtocol())
        {
            handUpLayout.setVisibility(View.GONE);
        }

        handUpLayout.setOnClickListener(moreButtonListener);
        muteAllLayout.setOnClickListener(moreButtonListener);
        cancelMuteAllLayout.setOnClickListener(moreButtonListener);
        lockLayout.setOnClickListener(moreButtonListener);
        unlockLayout.setOnClickListener(moreButtonListener);
        upgradeLayout.setOnClickListener(moreButtonListener);
        requestChairManLayout.setOnClickListener(moreButtonListener);
        releaseChairManLayout.setOnClickListener(moreButtonListener);
        startRecordLayout.setOnClickListener(moreButtonListener);
        endRecordLayout.setOnClickListener(moreButtonListener);

        mPopupWindow = generatePopupWindow(popupView, wrap, wrap);
        mPopupWindow.showAtLocation(findViewById(R.id.conf_member_list_ll), Gravity.RIGHT | Gravity.BOTTOM, 0, confButtonGroup.getHeight());
    }

    private View.OnClickListener moreButtonListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if (mPopupWindow != null && mPopupWindow.isShowing())
            {
                mPopupWindow.dismiss();
            }
            switch (v.getId())
            {
                case R.id.hand_up_ll:
                    mPresenter.handUpSelf();
                    break;
                case R.id.mute_all_ll:
                    mPresenter.muteConf(true);
                    break;
                case R.id.cancel_mute_all_ll:
                    mPresenter.muteConf(false);
                    break;
                case R.id.lock_conf_ll:
                    mPresenter.lockConf(true);
                    break;
                case R.id.un_lock_conf_ll:
                    mPresenter.lockConf(false);
                    break;
                case R.id.upgrade_conf_ll:
                    mPresenter.updateConf();
                    break;
                case R.id.request_chairman_ll:
                    showRequestChairmanDialog();
                    break;
                case R.id.release_chairman_ll:
                    mPresenter.releaseChairman();
                    break;
                case R.id.start_record_ll:
                    mPresenter.recordConf(true);
                    break;
                case R.id.stop_record_ll:
                    mPresenter.recordConf(false);
                    break;

                default:
                    break;
            }
        }
    };

    private void showRequestChairmanDialog()
    {
        final EditDialog dialog = new EditDialog(this, R.string.input_chairman_password);
        dialog.setRightButtonListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                CommonUtil.hideSoftInput(ConfMemberListActivity.this);
                mPresenter.requestChairman(dialog.getText());
            }
        });
        dialog.show();
    }

    private synchronized PopupWindow generatePopupWindow(View view, int width, int height)
    {
        final PopupWindow popupWindow = new PopupWindow(view, width, height);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.setOnKeyListener(new View.OnKeyListener()
        {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (keyCode == KeyEvent.KEYCODE_MENU && popupWindow.isShowing())
                {
                    popupWindow.dismiss();
                    return true;
                }
                return false;
            }
        });

        return popupWindow;
    }


    private void showAddMemberDialog()
    {
        final ThreeInputDialog editDialog = new ThreeInputDialog(this);
        editDialog.setRightButtonListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mPresenter.addMember(editDialog.getInput2(), editDialog.getInput1(), editDialog.getInput3());
            }
        });

        editDialog.setHint1(R.string.input_number);
        editDialog.setHint2(R.string.input_name);
        editDialog.setHint3(R.string.input_account);
        editDialog.show();
    }

    @Override
    public void refreshMemberList(final List<Member> list)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                mBtnMoreFL.setVisibility(View.VISIBLE);
                adapter.setData(list);
                adapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void updateAddAttendeeButton(final boolean isChairman) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isChairman)
                {
                    mAddAttendeeFL.setVisibility(View.VISIBLE);
                }
                else
                {
                    mAddAttendeeFL.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void updateConfTypeIcon(final ConfBaseInfo confEntity)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (confEntity.getSubject() != null)
                {
                    titleTV.setVisibility(View.VISIBLE);
                    titleTV.setText(confEntity.getSubject());
                }
            }
        });
    }

    @Override
    public void updateVideoBtn(final boolean show)
    {

    }

    @Override
    public void updateUpgradeConfBtn(boolean isDataConf)
    {
        isDateConf = isDataConf;
    }

    @Override
    public void showMessage(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ConfMemberListActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    Runnable run = new Runnable() {
        @Override
        public void run() {
            tvSpeakerOne.setText("");
            tvSpeakerTwo.setText("");
            ivSpeakerOne.setVisibility(View.GONE);
            ivSpeakerTwo.setVisibility(View.GONE);
            isTiming = true;
        }
    };

    @Override
    public void updateSpeaker(final String[] speakers, final boolean noSpeaker) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (noSpeaker)
                {
                    tvSpeakerOne.setText("");
                    tvSpeakerTwo.setText("");
                    ivSpeakerOne.setVisibility(View.GONE);
                    ivSpeakerTwo.setVisibility(View.GONE);
                    return;
                }

                if (2 == speakers.length)
                {
                    tvSpeakerOne.setText(speakers[0]);
                    tvSpeakerTwo.setText(speakers[1]);
                    ivSpeakerOne.setVisibility(View.VISIBLE);
                    ivSpeakerTwo.setVisibility(View.VISIBLE);
                }
                else if (1 == speakers.length)
                {
                    tvSpeakerOne.setText(speakers[0]);
                    tvSpeakerTwo.setText("");
                    ivSpeakerOne.setVisibility(View.VISIBLE);
                    ivSpeakerTwo.setVisibility(View.GONE);
                }

                if (isTiming)
                {
                    mHandler.postDelayed(run, 3000);
                    isTiming = false;
                }
            }
        });
    }

    @Override
    public void showRenameDialog() {
        final EditDialog dialog = new EditDialog(this, R.string.input_new_name);
        dialog.setRightButtonListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                CommonUtil.hideSoftInput(ConfMemberListActivity.this);
                mPresenter.renameSelf(dialog.getText());
            }
        });
        dialog.show();
    }

    @Override
    public void showNotAllowUnmute() {
        ConfirmSimpleDialog dialog = new ConfirmSimpleDialog(this, getString(R.string.conf_not_allow_unmute_tip));
        dialog.show();
    }

    @Override
    public void updateMuteButton(final boolean isMute) {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                mMuteSelfFL.setVisibility(View.VISIBLE);
                mMuteSelfFL.setActivated(isMute);
            }
        });
    }

    @Override
    public void updateLoudSpeakerButton(int type)
    {
        if (type == CallConstant.TYPE_LOUD_SPEAKER)
        {
            mLoudSpeakerFL.setActivated(true);
        }
        else
        {
            mLoudSpeakerFL.setActivated(false);
        }
    }

    @Override
    public void updateTitle(final String title)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                titleTV.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void showItemClickDialog(final List<Object> items, final Member member)
    {
        final SimpleListDialog dialog = new SimpleListDialog(this, items);
        dialog.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                dialog.dismiss();
                mPresenter.onItemDetailClick((String) items.get(position), member);
            }
        });
        dialog.show();
    }

    @Override
    public void finishActivity()
    {
        finish();
    }


    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        mPresenter.unregisterBroadcast();
        closeTiming();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mPresenter.unregisterBroadcast();
        closeTiming();
    }

    private void closeTiming()
    {
        if (null != mHandler)
        {
            mHandler.removeCallbacks(run);
        }
    }
}
