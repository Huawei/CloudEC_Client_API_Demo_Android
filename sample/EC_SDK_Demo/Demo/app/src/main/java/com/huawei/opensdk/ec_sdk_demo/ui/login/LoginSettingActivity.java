package com.huawei.opensdk.ec_sdk_demo.ui.login;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.annotation.IdRes;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.logic.login.LoginModel;
import com.huawei.opensdk.ec_sdk_demo.ui.base.BaseActivity;
import com.huawei.opensdk.loginmgr.LoginConstant;
import com.huawei.opensdk.servicemgr.ServiceMgr;

public class LoginSettingActivity extends BaseActivity implements View.OnClickListener
{
    private LinearLayout securityMode;
    private LinearLayout netWork;

    private EditText mRegServerEditText;
    private EditText mServerPortEditText;
    private CheckBox mVpnCheckBox;
    private RadioGroup mSrtpGroup;
    private RadioGroup mSipTransportGroup;
    private RadioGroup mAppConfigGroup;
    private RadioGroup mSecurityTunnelGroup;
    private EditText mUdpPortEditText;
    private EditText mTlsPortEditText;
    private RadioGroup mPriorityGroup;
    private RadioGroup mProtocolGroup;
    private String mRegServerAddress;
    private String mServerPort;
    private boolean mIsVpn;
    private int mSrtpMode = 0;
    private int mSipTransport = 0;
    private int mAppConfig = 1;
    private int mSecurityMode = 0;
    private int mPriorityGroupPort = 0;
    private int mControlProtocol = 0;
    private String mUdpPort;
    private String mTlsPort;
    private SharedPreferences mSharedPreferences;
    private LoginModel mSettingPresenter;

    private void initView()
    {
        mRegServerEditText = (EditText) findViewById(R.id.et_register_server_address);
        mServerPortEditText = (EditText) findViewById(R.id.et_server_port);
        mVpnCheckBox = (CheckBox) findViewById(R.id.check_vpn_connect);
        mSrtpGroup = (RadioGroup)findViewById(R.id.rg_srtp);
        mSipTransportGroup = (RadioGroup)findViewById(R.id.rg_sip_transport);
        mAppConfigGroup = (RadioGroup) findViewById(R.id.app_config_enable);
        mSecurityTunnelGroup = (RadioGroup) findViewById(R.id.security_tunnel_mode);
        mUdpPortEditText = (EditText) findViewById(R.id.sip_server_udp_port);
        mTlsPortEditText = (EditText) findViewById(R.id.sip_server_tls_port);
        mPriorityGroup = (RadioGroup) findViewById(R.id.port_config_priority);
        mProtocolGroup = (RadioGroup) findViewById(R.id.conf_ctrl_protocol);

        ImageView navImage = (ImageView) findViewById(R.id.nav_iv);
        navImage.setImageResource(R.drawable.icon_back);
        TextView titleButton = (TextView) findViewById(R.id.title_text);
        titleButton.setText(R.string.login_set);
        ImageView searchButton = (ImageView) findViewById(R.id.right_img);
        searchButton.setVisibility(View.GONE);
        TextView rightButton = (TextView) findViewById(R.id.right_btn);
        rightButton.setVisibility(View.VISIBLE);
        rightButton.setText(getString(R.string.save));

        rightButton.setOnClickListener(this);
        navImage.setOnClickListener(this);

        mVpnCheckBox.setChecked(mSharedPreferences.getBoolean(LoginConstant.TUP_VPN, false));
        mRegServerEditText.setText(mSharedPreferences.getString(LoginConstant.TUP_REGSERVER, LoginConstant.BLANK_STRING));
        mServerPortEditText.setText(mSharedPreferences.getString(LoginConstant.TUP_PORT, LoginConstant.BLANK_STRING));
        mSrtpGroup.check(getSrtpGroupCheckedId(mSharedPreferences.getInt(LoginConstant.TUP_SRTP, 0)));
        mSipTransportGroup.check(getSipTransportGroupCheckedId(mSharedPreferences.getInt(LoginConstant.TUP_SIP_TRANSPORT, 0)));
        mAppConfigGroup.check(getEnableConfigDefaultCheckedId(mSharedPreferences.getInt(LoginConstant.APPLY_CONFIG_PRIORITY, 0)));
        mSecurityTunnelGroup.check(getSecurityTunnelModeCheckedId(mSharedPreferences.getInt(LoginConstant.SECURITY_TUNNEL, 0)));
        mPriorityGroup.check(getPortConfigPriorityCheckedId(mSharedPreferences.getInt(LoginConstant.PORT_CONFIG_PRIORITY, 0)));
        mProtocolGroup.check(getProtocolCheckedId(mSharedPreferences.getInt(LoginConstant.CONF_CTRL_PROTOCOL, 0)));
        mUdpPortEditText.setText(mSharedPreferences.getString(LoginConstant.UDP_PORT, LoginConstant.UDP_DEFAULT));
        mTlsPortEditText.setText(mSharedPreferences.getString(LoginConstant.TLS_PORT, LoginConstant.TLS_DEFAULT));

        securityMode = (LinearLayout) findViewById(R.id.l2TextContainer);
        netWork = (LinearLayout) findViewById(R.id.l3TextContainer);
        securityMode.setVisibility(View.GONE);
        netWork.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.right_btn:
                mRegServerAddress = mRegServerEditText.getText().toString().trim();
                mServerPort = mServerPortEditText.getText().toString().trim();
                mIsVpn = mVpnCheckBox.isChecked();
                mSrtpMode = getSrtpMode(mSrtpGroup.getCheckedRadioButtonId());
                mSipTransport = getSipTransportMode(mSipTransportGroup.getCheckedRadioButtonId());
                mAppConfig = getEnableAppConfig(mAppConfigGroup.getCheckedRadioButtonId());
                mSecurityMode = getSecurityMode(mSecurityTunnelGroup.getCheckedRadioButtonId());
                mPriorityGroupPort = getPortConfigPriority(mPriorityGroup.getCheckedRadioButtonId());
                mControlProtocol = getControlProtocol(mProtocolGroup.getCheckedRadioButtonId());
                mUdpPort = mUdpPortEditText.getText().toString().trim();
                mTlsPort = mTlsPortEditText.getText().toString().trim();
                saveLoginSetting(mIsVpn, mRegServerAddress, mServerPort, mControlProtocol);
                saveSecuritySetting(mSrtpMode, mSipTransport, mAppConfig, mSecurityMode);
                saveNetworkSetting(mUdpPort, mTlsPort, mPriorityGroupPort);
                updataSecurityNetworkParam();
                showToast(R.string.save_success);
                finish();
                break;
            case R.id.check_vpn_connect:
                if (mVpnCheckBox.isChecked())
                {
                    mVpnCheckBox.setChecked(true);
                }
                else
                {
                    mVpnCheckBox.setChecked(false);
                }
                break;
            case R.id.nav_iv:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void initializeComposition()
    {
        setContentView(R.layout.activity_login_setting);
        initView();
        mVpnCheckBox.setOnClickListener(this);

        mSrtpGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId)
                {
                    case R.id.rb_srtp_mandatory:
                        mSrtpMode = 2;
                        break;
                    case R.id.rb_srtp_optional:
                        mSrtpMode = 1;
                        break;
                    case R.id.rb_srtp_disable:
                        mSrtpMode = 0;
                        break;
                    default:
                        break;
                }
            }
        });

        mSipTransportGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId)
                {
                    case R.id.rb_sip_transport_udp:
                        mSipTransport = 0;
                        break;
                    case R.id.rb_sip_transport_tls:
                        mSipTransport = 1;
                        break;
                    case R.id.rb_sip_transport_tcp:
                        mSipTransport = 2;
                        break;
                    default:
                        break;
                }
            }
        });

        mAppConfigGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId)
                {
                    case R.id.app_config_auth:
                        mAppConfig = 0;
                        break;
                    case R.id.app_config_default:
                        mAppConfig = 1;
                        break;
                    default:
                        break;
                }
            }
        });

        mSecurityTunnelGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId)
                {
                    case R.id.security_tunnel_default:
                        mSecurityMode = 0;
                        break;
                    case R.id.security_tunnel_disable:
                        mSecurityMode = 1;
                        break;
                    default:
                        break;
                }
            }
        });

        mPriorityGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId)
                {
                    case R.id.system_push_port:
                        mPriorityGroupPort = 0;
                        break;
                    case R.id.app_config_port:
                        mPriorityGroupPort = 1;
                        break;
                    default:
                        break;
                }
            }
        });

        mProtocolGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId)
                {
                    case R.id.ido_protocol:
                        mControlProtocol = 0;
                        break;
                    case R.id.rest_protocol:
                        mControlProtocol = 1;
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void initializeData()
    {
        mSharedPreferences = getSharedPreferences(LoginConstant.FILE_NAME, Activity.MODE_PRIVATE);
        mSettingPresenter = new LoginModel(mSharedPreferences);
    }

    private void saveLoginSetting(boolean isVpn, String regServerAddress, String serverPort, int mControlProtocol)
    {
        if (TextUtils.isEmpty(regServerAddress) || TextUtils.isEmpty(serverPort))
        {
            showToast(R.string.server_information_not_empty);
            return;
        }
        mSharedPreferences.edit().putBoolean(LoginConstant.TUP_VPN, isVpn)
                .putString(LoginConstant.TUP_REGSERVER, regServerAddress)
                .putString(LoginConstant.TUP_PORT, serverPort)
                .putInt(LoginConstant.CONF_CTRL_PROTOCOL, mControlProtocol)
                .commit();
    }

    private void saveSecuritySetting(int srtpMode, int sipTransport, int appConfig, int securityMode)
    {
        mSharedPreferences.edit().putInt(LoginConstant.TUP_SRTP, srtpMode)
                .putInt(LoginConstant.TUP_SIP_TRANSPORT, sipTransport)
                .putInt(LoginConstant.APPLY_CONFIG_PRIORITY, appConfig)
                .putInt(LoginConstant.SECURITY_TUNNEL, securityMode)
                .commit();
    }

    private void saveNetworkSetting(String udpPort, String tlsPort, int portPriority)
    {
        mSharedPreferences.edit().putString(LoginConstant.UDP_PORT, udpPort)
                .putString(LoginConstant.TLS_PORT, tlsPort)
                .putInt(LoginConstant.PORT_CONFIG_PRIORITY, portPriority)
                .commit();
    }

    private int getSrtpGroupCheckedId(int srtpMode) {
        int id = R.id.rb_srtp_disable;
        switch (srtpMode) {
            case 0:
                id = R.id.rb_srtp_disable;
                break;
            case 1:
                id = R.id.rb_srtp_optional;
                break;
            case 2:
                id = R.id.rb_srtp_mandatory;
                break;
            default:
                break;
        }
        return id;
    }

    private int getSipTransportGroupCheckedId(int sipTransport) {
        int id = R.id.rb_sip_transport_udp;
        switch (sipTransport) {
            case 0:
                id = R.id.rb_sip_transport_udp;
                break;
            case 1:
                id = R.id.rb_sip_transport_tls;
                break;
            case 2:
                id = R.id.rb_sip_transport_tcp;
                break;
            default:
                break;
        }
        return id;
    }
    
    private int getEnableConfigDefaultCheckedId(int isEnable) {
        int id = R.id.app_config_default;
        switch (isEnable) {
            case 0:
                id = R.id.app_config_auth;
                break;
            case 1:
                id = R.id.app_config_default;
                break;
            default:
                break;
        }
        return id;
    }

    private int getSecurityTunnelModeCheckedId(int tunnelMode) {
        int id = R.id.security_tunnel_default;
        switch (tunnelMode)
        {
            case 0:
                id = R.id.security_tunnel_default;
                break;
            case 1:
                id = R.id.security_tunnel_disable;
                break;
            default:
                break;
        }
        return id;
    }

    private int getPortConfigPriorityCheckedId(int priority) {
        int id = R.id.system_push_port;
        switch (priority)
        {
            case 0:
                id = R.id.system_push_port;
                break;
            case 1:
                id = R.id.app_config_port;
                break;
            default:
                break;
        }
        return id;
    }

    private int getProtocolCheckedId(int protocol) {
        int id = R.id.ido_protocol;
        switch (protocol)
        {
            case 0:
                id = R.id.ido_protocol;
                break;
            case 1:
                id = R.id.rest_protocol;
                break;
            default:
                break;
        }
        return id;
    }

    private int getSrtpMode(int checkedId) {
        int srtpMode = 0;
        switch (checkedId)
        {
            case R.id.rb_srtp_mandatory:
                srtpMode = 2;
                break;
            case R.id.rb_srtp_optional:
                srtpMode = 1;
                break;
            case R.id.rb_srtp_disable:
                srtpMode = 0;
                break;
            default:
                break;
        }
        return srtpMode;
    }

    private int getSipTransportMode(int checkedId) {
        int sipTransport = 0;
        switch (checkedId)
        {
            case R.id.rb_sip_transport_udp:
                sipTransport = 0;
                break;
            case R.id.rb_sip_transport_tls:
                sipTransport = 1;
                break;
            case R.id.rb_sip_transport_tcp:
                sipTransport = 2;
                break;
            default:
                break;
        }
        return sipTransport;
    }
    
    private int getEnableAppConfig(int checkedId) {
        int appConfig = 1;
        switch (checkedId)
        {
            case R.id.app_config_auth:
                appConfig = 0;
                break;
            case R.id.app_config_default:
                appConfig = 1;
                break;
            default:
                break;
        }
        return appConfig;
    }

    private int getSecurityMode(int checkedId) {
        int securityMode = 0;
        switch (checkedId)
        {
            case R.id.security_tunnel_default:
                securityMode = 0;
                break;
            case R.id.security_tunnel_disable:
                securityMode = 1;
                break;
            default:
                break;
        }
        return securityMode;
    }

    private int getPortConfigPriority(int checkedId) {
        int portConfig = 0;
        switch (checkedId)
        {
            case R.id.system_push_port:
                portConfig = 0;
                break;
            case R.id.app_config_port:
                portConfig = 1;
                break;
            default:
                break;
        }
        return portConfig;
    }

    private int getControlProtocol(int checkedId) {
        int confConfig = 0;
        switch (checkedId)
        {
            case R.id.ido_protocol:
                confConfig = 0;
                break;
            case R.id.rest_protocol:
                confConfig = 1;
                break;
            default:
                break;
        }
        return confConfig;
    }

    private void updataSecurityNetworkParam(){
        ServiceMgr.getServiceMgr().securityParam(
                mSettingPresenter.getSrtpMode(),
                mSettingPresenter.getSipTransport(),
                mSettingPresenter.getAppConfig(),
                mSettingPresenter.getTunnelMode());
        ServiceMgr.getServiceMgr().networkParam(
                mSettingPresenter.getUdpPort(),
                mSettingPresenter.getTlsPort(),
                mSettingPresenter.getPriority());
    }
}
