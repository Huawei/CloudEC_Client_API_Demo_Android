package com.huawei.opensdk.ec_sdk_demo.ui.servicesetting;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.huawei.opensdk.callmgr.iptService.IIptNotification;
import com.huawei.opensdk.callmgr.iptService.IptMgr;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.ui.base.BaseActivity;


public class IptRegisterActivity extends BaseActivity implements IIptNotification, View.OnClickListener {

    private ProgressDialog mDialog;

    private ImageView imageButton_dnd;
    private ImageView imageButton_cw;
    private ImageView imageButton_cfu;
    private ImageView imageButton_cfb;
    private ImageView imageButton_cfna;
    private ImageView imageButton_cfnr;

    private EditText et_cfu_num;
    private EditText et_cfb_num;
    private EditText et_cfna_num;
    private EditText et_cfnr_num;

    private boolean isEnable = false;

    @Override
    public void initializeComposition() {
        setContentView(R.layout.ipt_service_setting);
        initView();
        showIPT();
    }

    @Override
    public void initializeData() {

    }

    protected void initView() {
        findViewById(R.id.imageButton_dnd).setOnClickListener(this);
        imageButton_dnd = (ImageView) findViewById(R.id.imageButton_dnd);

        findViewById(R.id.imageButton_cw).setOnClickListener(this);
        imageButton_cw = (ImageView) findViewById(R.id.imageButton_cw);

        findViewById(R.id.imageButton_cfu).setOnClickListener(this);
        imageButton_cfu = (ImageView) findViewById(R.id.imageButton_cfu);

        findViewById(R.id.imageButton_cfb).setOnClickListener(this);
        imageButton_cfb = (ImageView) findViewById(R.id.imageButton_cfb);

        findViewById(R.id.imageButton_cfna).setOnClickListener(this);
        imageButton_cfna = (ImageView) findViewById(R.id.imageButton_cfna);

        findViewById(R.id.imageButton_cfnr).setOnClickListener(this);
        imageButton_cfnr = (ImageView) findViewById(R.id.imageButton_cfnr);

        et_cfu_num = (EditText) findViewById(R.id.et_cfu_num);
        et_cfb_num = (EditText) findViewById(R.id.et_cfb_num);
        et_cfna_num = (EditText) findViewById(R.id.et_cfna_num);
        et_cfnr_num = (EditText) findViewById(R.id.et_cfnr_num);

    }

    public void showIPT()
    {
        boolean right;
        boolean register;
         {
             right = IptMgr.getInstance().getDndRegisterInfo().getHasRight() == 1 ? true : false;
             register = IptMgr.getInstance().getDndRegisterInfo().getIsEnable() == 1 ? true : false;
            if (register && right) {
                imageButton_dnd.setImageResource(R.drawable.setting_switch_on);
                imageButton_dnd.setTag("Register");
            } else if(!right){
                imageButton_dnd.setTag("false");
            }
        }

        {
            right = IptMgr.getInstance().getCwRegisterInfo().getHasRight() == 1 ? true : false;
            register = IptMgr.getInstance().getCwRegisterInfo().getIsEnable() == 1 ? true : false;
            if (register && right) {
                imageButton_cw.setImageResource(R.drawable.setting_switch_on);
                imageButton_cw.setTag("Register");
            } else if(!right){
                imageButton_cw.setTag("false");
            }
        }

        {
            right = IptMgr.getInstance().getCfuRegisterInfo().getHasRight() == 1 ? true : false;
            register = IptMgr.getInstance().getCfuRegisterInfo().getIsEnable() == 1 ? true : false;
            if (register && right) {
                imageButton_cfu.setImageResource(R.drawable.setting_switch_on);
                imageButton_cfu.setTag("Register");
                et_cfu_num.setText(IptMgr.getInstance().getCfuRegisterInfo().getRegisterNumber());
                et_cfu_num.setEnabled(false);
            } else if(!right){
                imageButton_cfu.setTag("false");
            }else if (right)
            {
                imageButton_cfu.setTag("first");
            }
        }

        {
            right = IptMgr.getInstance().getCfbRegisterInfo().getHasRight() == 1 ? true : false;
            register = IptMgr.getInstance().getCfbRegisterInfo().getIsEnable() == 1 ? true : false;
            if (register && right) {
                imageButton_cfb.setImageResource(R.drawable.setting_switch_on);
                imageButton_cfb.setTag("Register");
                et_cfb_num.setText(IptMgr.getInstance().getCfbRegisterInfo().getRegisterNumber());
                et_cfb_num.setEnabled(false);
            } else if(!right){
                imageButton_cfb.setTag("false");
            }else if (right)
            {
                imageButton_cfb.setTag("first");
            }
        }

        {
            right = IptMgr.getInstance().getCfnaRegisterInfo().getHasRight() == 1 ? true : false;
            register = IptMgr.getInstance().getCfnaRegisterInfo().getIsEnable() == 1 ? true : false;
            if (register && right) {
                imageButton_cfna.setImageResource(R.drawable.setting_switch_on);
                imageButton_cfna.setTag("Register");
                et_cfna_num.setText(IptMgr.getInstance().getCfnaRegisterInfo().getRegisterNumber());
                et_cfna_num.setEnabled(false);
            } else if(!right){
                imageButton_cfna.setTag("false");
            }else if (right)
            {
                imageButton_cfna.setTag("first");
            }
        }

        {
            right = IptMgr.getInstance().getCfnrRegisterInfo().getHasRight() == 1 ? true : false;
            register = IptMgr.getInstance().getCfnrRegisterInfo().getIsEnable() == 1 ? true : false;
            if (register && right) {
                imageButton_cfnr.setImageResource(R.drawable.setting_switch_on);
                imageButton_cfnr.setTag("Register");
                et_cfnr_num.setText(IptMgr.getInstance().getCfnrRegisterInfo().getRegisterNumber());
                et_cfnr_num.setEnabled(false);
            } else if(!right){
                imageButton_cfnr.setTag("false");
            }else if (right)
            {
                imageButton_cfnr.setTag("first");
            }
        }
    }

    @Override
    public void onSetIptServiceSuc(int type, int isEnable) {
        dismissLoginDialog();

        Bundle bundle = new Bundle();
        bundle.putString("success", "onSetIptServiceSuc");
        bundle.putInt("type", type);
        bundle.putInt("isEnable", isEnable);

        Message message = Message.obtain();
        message.setData(bundle);
        handler.sendMessage(message);
    }

    @Override
    public void onSetIptServiceFal(int type, int isEnable) {
        dismissLoginDialog();

        Bundle bundle = new Bundle();
        bundle.putString("failed", "onSetIptServiceFal");
        bundle.putInt("type", type);
        bundle.putInt("isEnable", isEnable);

        Message message = Message.obtain();
        message.setData(bundle);
        handler.sendMessage(message);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IptMgr.getInstance().regIptNotification(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IptMgr.getInstance().unregIptNotification(this);
    }

    @Override
    public void onClick(View v) {
        String dataNumber = "";
        final ImageView button = (ImageView) v;
        int id = v.getId();
        switch (id) {
            case R.id.imageButton_dnd:
                if(!"false".equals(button.getTag()))
                {
                    showLoginDialog("ipt");
                    if ("Register".equals(button.getTag())) {
                        button.setTag("");
                        isEnable = false;
                    } else {
                        button.setTag("Register");
                        isEnable = true;
                    }
                    IptMgr.getInstance().setIPTService(1, isEnable, "");
                }

                break;
            case R.id.imageButton_cw:
                if(!"false".equals(button.getTag()))
                {
                    showLoginDialog("ipt");
                    if ("Register".equals(button.getTag())) {
                        button.setTag("");
                        isEnable = false;
                    } else {
                        button.setTag("Register");
                        isEnable = true;
                    }
                    IptMgr.getInstance().setIPTService(2, isEnable, "");
                }

                break;
            case R.id.imageButton_cfu:
                if (!"false".equals(button.getTag()))
                {
                    showLoginDialog("ipt");
                    dataNumber = et_cfu_num.getText().toString();
                    if( ("".equals(dataNumber) && "".equals(button.getTag())) || ("".equals(dataNumber) && "first".equals(button.getTag()) ) )
                    {
                        new AlertDialog.Builder(this)
                                .setTitle(R.string.ipt_warning)
                                .setMessage(R.string.ipt_warning_number)
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .setPositiveButton(R.string.ipt_sure, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dismissLoginDialog();
                                    }
                                })
                                .show();
                        return;
                    }

                    if ("Register".equals(button.getTag())) {
                        button.setTag("");
                        et_cfu_num.setEnabled(true);
                        isEnable = false;
                    } else {
                        button.setTag("Register");
                        et_cfu_num.setEnabled(false);
                        isEnable = true;
                    }
                    IptMgr.getInstance().setIPTService(3, isEnable, dataNumber);
                }

                break;
            case R.id.imageButton_cfb:
                if (!"false".equals(button.getTag()))
                {
                    showLoginDialog("ipt");
                    dataNumber = et_cfb_num.getText().toString();
                    if( ("".equals(dataNumber) && "".equals(button.getTag())) || ("".equals(dataNumber) && "first".equals(button.getTag()) ) )
                    {
                        new AlertDialog.Builder(this)
                                .setTitle(R.string.ipt_warning)
                                .setMessage(R.string.ipt_warning_number)
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .setPositiveButton(R.string.ipt_sure, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dismissLoginDialog();
                                    }
                                })
                                .show();
                        return;
                    }

                    if ("Register".equals(button.getTag())) {
                        button.setTag("");
                        et_cfb_num.setEnabled(true);
                        isEnable = false;
                    } else {
                        button.setTag("Register");
                        et_cfb_num.setEnabled(false);
                        isEnable = true;
                    }
                    IptMgr.getInstance().setIPTService(4, isEnable, dataNumber);
                }

                break;
            case R.id.imageButton_cfna:
                if (!"false".equals(button.getTag()))
                {
                    showLoginDialog("ipt");
                    dataNumber = et_cfna_num.getText().toString();
                    if( ("".equals(dataNumber) && "".equals(button.getTag())) || ("".equals(dataNumber) && "first".equals(button.getTag()) ) )
                    {
                        new AlertDialog.Builder(this)
                                .setTitle(R.string.ipt_warning)
                                .setMessage(R.string.ipt_warning_number)
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .setPositiveButton(R.string.ipt_sure, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dismissLoginDialog();
                                    }
                                })
                                .show();
                        return;
                    }

                    if ("Register".equals(button.getTag())) {
                        button.setTag("");
                        et_cfna_num.setEnabled(true);
                        isEnable = false;
                    } else {
                        button.setTag("Register");
                        et_cfna_num.setEnabled(false);
                        isEnable = true;
                    }
                    IptMgr.getInstance().setIPTService(5, isEnable, dataNumber);
                }


                break;
            case R.id.imageButton_cfnr:
                if (!"false".equals(button.getTag()))
                {
                    showLoginDialog("ipt");
                    dataNumber = et_cfnr_num.getText().toString();
                    if( ("".equals(dataNumber) && "".equals(button.getTag())) || ("".equals(dataNumber) && "first".equals(button.getTag()) ) )
                    {
                        new AlertDialog.Builder(this)
                                .setTitle(R.string.ipt_warning)
                                .setMessage(R.string.ipt_warning_number)
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .setPositiveButton(R.string.ipt_sure, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dismissLoginDialog();
                                    }
                                })
                                .show();
                        return;
                    }

                    if ("Register".equals(button.getTag())) {
                        button.setTag("");
                        et_cfnr_num.setEnabled(true);
                        isEnable = false;
                    } else {
                        button.setTag("Register");
                        et_cfnr_num.setEnabled(false);
                        isEnable = true;
                    }
                    IptMgr.getInstance().setIPTService(6, isEnable, dataNumber);
                }

                break;
            default:
                break;
        }
    }

    public void showLoginDialog(String msg) {
        if (null == mDialog) {
            mDialog = new ProgressDialog(this);
        }

        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setTitle(msg);
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.show();
    }

    public void dismissLoginDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if ("onSetIptServiceSuc".equals(msg.getData().getString("success")) && 1 == msg.getData().getInt("isEnable")) {
                switch (msg.getData().getInt("type")) {
                    case 1:
                        imageButton_dnd.setImageResource(R.drawable.setting_switch_on);
                        break;
                    case 2:
                        imageButton_cw.setImageResource(R.drawable.setting_switch_on);
                        break;
                    case 3:
                        imageButton_cfu.setImageResource(R.drawable.setting_switch_on);
                        break;
                    case 4:
                        imageButton_cfb.setImageResource(R.drawable.setting_switch_on);
                        break;
                    case 5:
                        imageButton_cfna.setImageResource(R.drawable.setting_switch_on);
                        break;
                    case 6:
                        imageButton_cfnr.setImageResource(R.drawable.setting_switch_on);
                        break;
                    default:
                        break;
                }
            } else if ("onSetIptServiceSuc".equals(msg.getData().getString("success")) && 0 == msg.getData().getInt("isEnable"))
            {
                switch (msg.getData().getInt("type")) {
                    case 1:
                        imageButton_dnd.setImageResource(R.drawable.setting_switch_off);
                        break;
                    case 2:
                        imageButton_cw.setImageResource(R.drawable.setting_switch_off);
                        break;
                    case 3:
                        imageButton_cfu.setImageResource(R.drawable.setting_switch_off);
                        break;
                    case 4:
                        imageButton_cfb.setImageResource(R.drawable.setting_switch_off);
                        break;
                    case 5:
                        imageButton_cfna.setImageResource(R.drawable.setting_switch_off);
                        break;
                    case 6:
                        imageButton_cfnr.setImageResource(R.drawable.setting_switch_off);
                        break;
                    default:
                        break;
                }
            } else if ("onSetIptServiceFal".equals(msg.getData().getString("failed")) && 1 == msg.getData().getInt("isEnable")) {
                switch (msg.getData().getInt("type")) {
                    case 1:
                        Toast.makeText(IptRegisterActivity.this, R.string.ipt_register_dnd_failed, Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(IptRegisterActivity.this, R.string.ipt_register_cw_failed, Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        Toast.makeText(IptRegisterActivity.this, R.string.ipt_register_cfu_failed, Toast.LENGTH_SHORT).show();
                        break;
                    case 4:
                        Toast.makeText(IptRegisterActivity.this, R.string.ipt_register_cfb_failed, Toast.LENGTH_SHORT).show();
                        break;
                    case 5:
                        Toast.makeText(IptRegisterActivity.this, R.string.ipt_register_cfna_failed, Toast.LENGTH_SHORT).show();
                        break;
                    case 6:
                        Toast.makeText(IptRegisterActivity.this, R.string.ipt_register_cfnr_failed, Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            } else {
                switch (msg.getData().getInt("type")) {
                    case 1:
                        Toast.makeText(IptRegisterActivity.this, R.string.ipt_logoff_dnd_failed, Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(IptRegisterActivity.this, R.string.ipt_logoff_cw_failed, Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        Toast.makeText(IptRegisterActivity.this, R.string.ipt_logoff_cfu_failed, Toast.LENGTH_SHORT).show();
                        break;
                    case 4:
                        Toast.makeText(IptRegisterActivity.this, R.string.ipt_logoff_cfb_failed, Toast.LENGTH_SHORT).show();
                        break;
                    case 5:
                        Toast.makeText(IptRegisterActivity.this, R.string.ipt_logoff_cfna_failed, Toast.LENGTH_SHORT).show();
                        break;
                    case 6:
                        Toast.makeText(IptRegisterActivity.this, R.string.ipt_logoff_cfnr_failed, Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        }
    };

}
