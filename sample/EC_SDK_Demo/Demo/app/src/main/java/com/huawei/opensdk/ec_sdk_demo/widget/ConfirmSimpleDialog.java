package com.huawei.opensdk.ec_sdk_demo.widget;

import android.content.Context;

import com.huawei.opensdk.ec_sdk_demo.R;

/**
 * 简单的确定弹框
 */
public class ConfirmSimpleDialog extends BaseDialog {

    public ConfirmSimpleDialog(Context context, String message)
    {
        super(context);
        setContentView(R.layout.dialog_simple_confirm);
        setMessage(message);
        setCanceledOnTouchOutside(false);
        setLeftButtonListener(null);
        setRightButtonListener(null);
    }
}
