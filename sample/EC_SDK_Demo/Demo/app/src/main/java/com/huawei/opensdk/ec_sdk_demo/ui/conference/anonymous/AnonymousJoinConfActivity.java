package com.huawei.opensdk.ec_sdk_demo.ui.conference.anonymous;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.huawei.ecterminalsdk.models.TsdkCommonResult;
import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcastReceiver;
import com.huawei.opensdk.demoservice.MeetingMgr;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.logic.conference.anonymous.AnonymousPresenter;
import com.huawei.opensdk.ec_sdk_demo.ui.base.BaseActivity;
import com.huawei.opensdk.loginmgr.LoginConstant;

public class AnonymousJoinConfActivity extends BaseActivity {

    private Button mJoinAnonymous;
    private EditText mAnonymousConfId;
    private EditText mAnonymousConfNickname;
    private EditText mAnonymousConfPassword;
    private EditText mAnonymousAddress;
    private EditText mAnonymousPort;
    private ProgressDialog mDialog;

    private SharedPreferences mSharedPreferences;
    private SharedPreferences mSettingPreferences;
    private AnonymousPresenter mAnonymousPresenter;
    private boolean isFirst = false;

    private String[] mActions = new String[]{CustomBroadcastConstants.GET_TEMP_USER_RESULT,
            CustomBroadcastConstants.CONF_CALL_CONNECTED,
            CustomBroadcastConstants.JOIN_CONF_FAILED};

    @Override
    public void initializeComposition() {
        setContentView(R.layout.anonymous_join_conf_activity);
        mJoinAnonymous = (Button) findViewById(R.id.btn_join_anonymous);


        mAnonymousConfId = (EditText) findViewById(R.id.anonymous_conf_id);
        mAnonymousConfNickname = (EditText) findViewById(R.id.anonymous_conf_nickname);
        mAnonymousConfPassword = (EditText) findViewById(R.id.anonymous_conf_password);
        mAnonymousAddress = (EditText) findViewById(R.id.anonymous_service_address);
        mAnonymousPort = (EditText) findViewById(R.id.anonymous_service_port);

        mAnonymousConfNickname.setText(mAnonymousPresenter.getNickname());
        mAnonymousAddress.setText(mAnonymousPresenter.getAnonymousAddress());
        mAnonymousPort.setText(mAnonymousPresenter.getAnonymousPort());



        if(isFirst){
            SpannableString span = new SpannableString(mAnonymousPresenter.getNickname());
            span.setSpan(new ForegroundColorSpan(Color.BLUE),0,8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            isFirst = ! isFirst;
            mAnonymousConfNickname.setText(span);
        }



        mJoinAnonymous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                joinConf(mAnonymousConfId.getText().toString().trim(),
                        mAnonymousConfNickname.getText().toString().trim(),
                        mAnonymousConfPassword.getText().toString().trim(),
                        mAnonymousAddress.getText().toString().trim(),
                        mAnonymousPort.getText().toString().trim());

                //保存会议地址和端口
                mAnonymousPresenter.saveAnonymousAddress(
                        mAnonymousAddress.getText().toString().trim(),
                        mAnonymousPort.getText().toString().trim(),
                        mAnonymousConfNickname.getText().toString().trim(),
                        isFirst);
            }
        });
    }

    @Override
    public void initializeData() {
        mSharedPreferences = getSharedPreferences(LoginConstant.ANONYMOUS_FILE_NAME, Activity.MODE_PRIVATE);
        mSettingPreferences = getSharedPreferences(LoginConstant.FILE_NAME, Activity.MODE_PRIVATE);
        mAnonymousPresenter = new AnonymousPresenter(mSharedPreferences);
        isFirst = mAnonymousPresenter.getFirstStart();
    }

    public void joinConf(String confId, String nickname,
                        String confPassword, String serviceAddress,
                        String servicePort)
    {

        if (TextUtils.isEmpty(confId))
        {
            Toast.makeText(this, "conference ID cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if ( TextUtils.isEmpty(nickname))
        {
            Toast.makeText(this, "conference Nickname cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(serviceAddress) || TextUtils.isEmpty(servicePort))
        {
            Toast.makeText(this, "Server address cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        int result = MeetingMgr.getInstance().joinConferenceByAnonymous(
                confId,
                nickname,
                confPassword,
                serviceAddress,
                servicePort,
                mSettingPreferences.getBoolean(LoginConstant.TUP_VPN, false));

        if(0 == result){
            showJoiningDialog();
        }
    }

    private void showJoiningDialog()
    {
        if (null == mDialog)
        {
            mDialog = new ProgressDialog(this);
        }
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setMessage("joining...");
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.show();
    }

    private void dismissJoiningDialog()
    {
        if (null != mDialog && mDialog.isShowing())
        {
            mDialog.dismiss();
        }
    }

    private LocBroadcastReceiver receiver = new LocBroadcastReceiver() {
        @Override
        public void onReceive(String broadcastName, Object obj) {

            switch (broadcastName){
                case CustomBroadcastConstants.GET_TEMP_USER_RESULT:
                    if (obj instanceof TsdkCommonResult){
                        TsdkCommonResult result = (TsdkCommonResult)obj;
                        if(0 != result.getResult()){
                            dismissJoiningDialog();
                            final String reason = "join conf failed ,reason:"+result.getReasonDescription();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(AnonymousJoinConfActivity.this, reason, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                    break;
                case CustomBroadcastConstants.CONF_CALL_CONNECTED:
                case CustomBroadcastConstants.JOIN_CONF_FAILED:
                    dismissJoiningDialog();
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    protected void onResume()
    {
        super.onResume();
        LocBroadcast.getInstance().registerBroadcast(receiver, mActions);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        dismissJoiningDialog();
        LocBroadcast.getInstance().unRegisterBroadcast(receiver, mActions);
    }
}
