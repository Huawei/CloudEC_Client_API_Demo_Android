package com.huawei.opensdk.ec_sdk_demo.ui.call;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.huawei.opensdk.callmgr.CallMgr;
import com.huawei.opensdk.callmgr.ctdservice.CtdMgr;
import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.ui.base.AbsFragment;
import com.huawei.opensdk.loginmgr.LoginMgr;

public class CallFragment extends AbsFragment implements View.OnClickListener
{
    private ImageView mBtnCall;
    private ImageView mBtnVideo;
    private String mSipNumber = "";
    private ImageView mHideDial;

    private LinearLayout mDialLayout;

    private DialPlateControl mDialPlateControl;

    private ImageView mShowArea;
    private ImageView mDeleteNumberArea;

    private ImageView mCtdButton;
    private Boolean isChecked = false;
    private static String number;
    private int mCallID = 0;

    public static String getNumber() {
        return number;
    }

    public static void setNumber(String number) {
        CallFragment.number = number;
    }

    /**
     * display call view
     * @param callNumber the call number
     * @param isVideoCall the is video call
     */
    public void showCallingLayout(final String callNumber, final boolean isVideoCall)
    {
        if (TextUtils.isEmpty(callNumber))
        {
            LogUtil.i(UIConstants.DEMO_TAG, "empty CallNumber return!!!");
            return;
        }

        if (this.isChecked)
        {
            CtdMgr.getInstance().makeCtdCall(callNumber,getNumber());
        }
        else
        {
            mCallID = CallMgr.getInstance().startCall(callNumber, isVideoCall);
            if (mCallID == 0)
            {
                return;
            }
        }
    }

    @Override
    public int getLayoutId()
    {
        return R.layout.fragment_call;
    }

    @Override
    public void onViewLoad()
    {
        super.onViewLoad();
    }

    @Override
    public void onDataLoad()
    {
        super.onDataLoad();
    }

    @Override
    public void onViewCreated()
    {
        super.onViewCreated();
        mBtnCall = (ImageView) mView.findViewById(R.id.call_audio_btn);
        mBtnVideo = (ImageView) mView.findViewById(R.id.call_video_btn);
        mHideDial = (ImageView) mView.findViewById(R.id.hide_dial_btn);
        mDialLayout = (LinearLayout) mView.findViewById(R.id.dial_plate_area);
        mShowArea = (ImageView) mView.findViewById(R.id.show_dial_btn);
        mDeleteNumberArea = (ImageView) mView.findViewById(R.id.delete_panel_btn);
        mCtdButton = (ImageView) mView.findViewById(R.id.select_ctd_btn);

        mDialPlateControl = new DialPlateControl(mDialLayout);
        mDialPlateControl.showDialPlate();

        mBtnCall.setOnClickListener(this);
        mBtnVideo.setOnClickListener(this);
        mHideDial.setOnClickListener(this);
        mShowArea.setOnClickListener(this);
        mDeleteNumberArea.setOnClickListener(this);
        mCtdButton.setOnClickListener(this);
        mSipNumber = LoginMgr.getInstance().getTerminal();
    }

    private void hideSoftKeyboard(View v)
    {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private String checkCallNumber(String callNumber)
    {
        if (TextUtils.isEmpty(callNumber))
        {
            showToast(R.string.call_number_is_null);
            return null;
        }

        if (mSipNumber.equals(callNumber))
        {
            showToast(R.string.can_not_call_self);
            return null;
        }
        return callNumber;
    }

    private void showToast(int resId)
    {
        Toast.makeText(context, getString(resId), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v)
    {
        String toNumber = mDialPlateControl.getCallNumber();
        switch (v.getId())
        {
            case R.id.call_audio_btn:
                if (TextUtils.isEmpty(toNumber))
                {
                    LogUtil.i(UIConstants.DEMO_TAG, "call number is empty!");
                    return;
                }
                hideSoftKeyboard(v);
                checkCallNumber(toNumber);
                showCallingLayout(toNumber, false);
                break;
            case R.id.call_video_btn:
                if (TextUtils.isEmpty(toNumber))
                {
                    LogUtil.i(UIConstants.DEMO_TAG, "call number is empty!");
                    return;
                }
                hideSoftKeyboard(v);
                checkCallNumber(toNumber);
                showCallingLayout(toNumber, true);
                break;
            case R.id.hide_dial_btn:
                mDialPlateControl.hideDialPlate();
                break;
            case R.id.show_dial_btn:
                mDialPlateControl.showDialPlate();
                break;
            case R.id.delete_panel_btn:
                if (TextUtils.isEmpty(toNumber))
                {
                    LogUtil.i(UIConstants.DEMO_TAG, context.getString(R.string.number_empty));
                    return;
                }
                toNumber = toNumber.substring(0, toNumber.length() - 1);
                mDialPlateControl.setCallNumber(toNumber);
                break;
            case R.id.select_ctd_btn:
                this.isChecked = !mCtdButton.isSelected();
                mCtdButton.setSelected(isChecked);
                if (isChecked)
                {
                    final EditText callerNumber = new EditText(getActivity());
                    callerNumber.setText(mSipNumber);
                    AlertDialog builder = new AlertDialog.Builder(getActivity())
                            .setTitle(getString(R.string.caller_number))
                            .setView(callerNumber)
                            .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    CallFragment.setNumber(callerNumber.getText().toString());
                                }
                            }).setNegativeButton(R.string.exit,null)
                            .create();
                    builder.show();
                }
                break;
            default:
                break;
        }
    }
}
