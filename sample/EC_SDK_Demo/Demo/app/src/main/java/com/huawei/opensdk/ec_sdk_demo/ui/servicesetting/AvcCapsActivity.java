package com.huawei.opensdk.ec_sdk_demo.ui.servicesetting;

import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.ui.base.BaseActivity;
import com.huawei.opensdk.ec_sdk_demo.widget.SimpleListDialog;
import com.huawei.opensdk.servicemgr.ServiceMgr;

import java.util.ArrayList;
import java.util.List;

/**
 * 设置AVC视频能力级别界面
 */
public class AvcCapsActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvOk;
    private RelativeLayout rlEncodeLevel;
    private RelativeLayout rlDecodeLevel;
    private SimpleListDialog encodeDialog;
    private TextView tvEncodeLevel;
    private TextView tvDecodeLevel;

    private int encodeLeave;
    private int decodeLeave;

    @Override
    public void initializeComposition() {
        setContentView(R.layout.activity_avc_caps);
        tvOk = (TextView) findViewById(R.id.right_text);
        rlEncodeLevel = (RelativeLayout) findViewById(R.id.rl_encode_level);
        rlDecodeLevel = (RelativeLayout) findViewById(R.id.rl_decode_level);
        tvEncodeLevel = (TextView) findViewById(R.id.tv_encode_level);
        tvDecodeLevel = (TextView) findViewById(R.id.tv_decode_level);

        tvOk.setText(R.string.btn_sure);
        showVideoCodecLevel();
        tvOk.setOnClickListener(this);
        rlEncodeLevel.setOnClickListener(this);
        rlDecodeLevel.setOnClickListener(this);
    }

    @Override
    public void initializeData() {
        encodeLeave = ServiceMgr.getServiceMgr().getVideoEncodeLeave();
        decodeLeave = ServiceMgr.getServiceMgr().getVideoDecodeLeave();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.rl_encode_level:
                showEncodeAvcCapsDialog(true);
                break;
            case R.id.rl_decode_level:
                showEncodeAvcCapsDialog(false);
                break;
            case R.id.right_text:
                ServiceMgr.getServiceMgr().setAvcCapsLevel(encodeLeave, decodeLeave);
                finish();
                break;
                default:
                    break;
        }
    }

    private void showVideoCodecLevel()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvEncodeLevel.setText(getLeave(encodeLeave));
                tvDecodeLevel.setText(getLeave(decodeLeave));
            }
        });
    }

    private void showEncodeAvcCapsDialog(final boolean isEncode)
    {
        List<Object> list = new ArrayList<>();
        list.add(getString(R.string.ohd));
        list.add(getString(R.string.hd));
        list.add(getString(R.string.sd));
        list.add(getString(R.string.smooth));
        list.add(getString(R.string.save_traffic));

        encodeDialog = new SimpleListDialog(this, list);
        encodeDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                encodeDialog.dismiss();
                encodeDialog = null;
                if (isEncode)
                {
                    encodeLeave = i;
                    tvEncodeLevel.setText(getLeave(i));
                }
                else
                {
                    decodeLeave = i;
                    tvDecodeLevel.setText(getLeave(i));
                }
            }
        });
        encodeDialog.show();
    }

    private String getLeave(int position)
    {
        String leave = getString(R.string.hd);
        switch (position)
        {
            case 0:
                leave = getString(R.string.ohd);
                break;
            case 1:
                leave = getString(R.string.hd);
                break;
            case 2:
                leave = getString(R.string.sd);
                break;
            case 3:
                leave = getString(R.string.smooth);
                break;
            case 4:
                leave = getString(R.string.save_traffic);
                break;
            default:
                break;
        }
        return leave;
    }
}
