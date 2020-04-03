package com.huawei.opensdk.ec_sdk_demo.widget;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huawei.opensdk.ec_sdk_demo.R;

/**
 * 简单的确定弹框
 */
public class ConfirmSimpleDialog extends BaseDialog {

    private TextView titleTV;
    private LinearLayout linearLayout;
    private TextView title02TV;
    private CheckBox checkCB;
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

        titleTV = (TextView) findViewById(R.id.dialog_message);
        linearLayout = (LinearLayout) findViewById(R.id.allow_title);
        title02TV = (TextView) findViewById(R.id.allow_title_tv);
        checkCB = (CheckBox) findViewById(R.id.allow_checked);
        leftTV = (TextView) findViewById(R.id.dialog_leftbutton);
        view = (View) findViewById(R.id.dialog_divide);
    }

    public void setVisible()
    {
        leftTV.setVisibility(View.VISIBLE);
        view.setVisibility(View.VISIBLE);
    }

    public void showAllowTitle(String message)
    {
        if (title02TV != null && message != null)
        {
            title02TV.setText(message);
        }
        titleTV.setVisibility(View.GONE);
        linearLayout.setVisibility(View.VISIBLE);
    }

    public boolean isSelected()
    {
        if (null != checkCB)
        {
            return checkCB.isChecked();
        }

        return false;
    }
}
