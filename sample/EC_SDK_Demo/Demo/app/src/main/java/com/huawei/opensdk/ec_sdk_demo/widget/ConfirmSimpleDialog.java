package com.huawei.opensdk.ec_sdk_demo.widget;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.huawei.opensdk.ec_sdk_demo.R;

/**
 * 简单的确定弹框
 */
public class ConfirmSimpleDialog extends BaseDialog {

    private TextView leftTV;
    private View view;
    public ConfirmSimpleDialog(Context context, String message)
    {
        super(context);
        setContentView(R.layout.dialog_simple_confirm);
        setMessage(message);
        setCanceledOnTouchOutside(false);
        setLeftButtonListener(null);
        setRightButtonListener(null);

        leftTV = (TextView) findViewById(R.id.dialog_leftbutton);
        view = (View) findViewById(R.id.dialog_divide);
    }

    public void setVisible()
    {
        leftTV.setVisibility(View.VISIBLE);
        view.setVisibility(View.VISIBLE);
    }
}
