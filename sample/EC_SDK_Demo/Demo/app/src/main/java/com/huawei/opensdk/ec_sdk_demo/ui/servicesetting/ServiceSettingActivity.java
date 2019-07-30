package com.huawei.opensdk.ec_sdk_demo.ui.servicesetting;

import android.view.View;

import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.ui.IntentConstant;
import com.huawei.opensdk.ec_sdk_demo.ui.base.BaseActivity;
import com.huawei.opensdk.ec_sdk_demo.util.ActivityUtil;

public class ServiceSettingActivity extends BaseActivity implements View.OnClickListener {

    @Override
    public void initializeComposition() {
        setContentView(R.layout.activity_service_setting);
        findViewById(R.id.ipt_service_setting).setOnClickListener(this);
        findViewById(R.id.other_setting).setOnClickListener(this);
        findViewById(R.id.modify_psd_setting).setOnClickListener(this);
    }

    @Override
    public void initializeData() {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id)
        {
            case R.id.ipt_service_setting:
                ActivityUtil.startActivity(ServiceSettingActivity.this, IntentConstant.IPT_REGISTER_ACTIVITY_ACTION, new String[]{IntentConstant.DEFAULT_CATEGORY});
                break;
            case R.id.other_setting:
                ActivityUtil.startActivity(ServiceSettingActivity.this, IntentConstant.AVC_CAPS_ACTIVITY_ACTION);
                break;
            case R.id.modify_psd_setting:
                ActivityUtil.startActivity(ServiceSettingActivity.this, IntentConstant.MODIFY_PSD_ACTIVITY_ACTION);
                break;
            default:
                break;
        }
    }
}
