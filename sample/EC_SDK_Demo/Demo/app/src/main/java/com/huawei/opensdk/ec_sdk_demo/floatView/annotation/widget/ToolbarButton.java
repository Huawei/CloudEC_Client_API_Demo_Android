package com.huawei.opensdk.ec_sdk_demo.floatView.annotation.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huawei.opensdk.ec_sdk_demo.R;


public class ToolbarButton extends LinearLayout
{
    private ImageView mImgIcon;
    private View.OnClickListener mOnClickListener;
    private TextView mTxtTitle;

    public ToolbarButton(Context context)
    {
        this(context, null);
    }

    public ToolbarButton(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs, 0);
    }

    @SuppressLint({"NewApi"})
    public ToolbarButton(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr)
    {
        View.inflate(getContext(), R.layout.cl_toolbar_button, this);
        this.mImgIcon = ((ImageView)findViewById(R.id.icon));
        this.mTxtTitle = ((TextView)findViewById(R.id.title));
//        setOnClickListener(new View.OnClickListener()
//        {
//            private long mLastClickTime = 0L;
//
//            public void onClick(View view)
//            {
//                if (mOnClickListener == null) {
//                    return;
//                }
//                long l = SystemClock.elapsedRealtime();
//                if (l - this.mLastClickTime > 500L) {
//                    mOnClickListener.onClick(view);
//                }
//                this.mLastClickTime = l;
//            }
//        });
    }

    public void setIconBackgroundResource(int paramInt)
    {
        if (this.mImgIcon != null) {
            this.mImgIcon.setBackgroundResource(paramInt);
        }
    }

    public void setImageResource(int paramInt)
    {
        if (this.mImgIcon != null) {
            this.mImgIcon.setImageResource(paramInt);
        }
    }

//    public void setOnClickListener(View.OnClickListener onClickListener)
//    {
//        this.mOnClickListener = onClickListener;
//    }

    public void setText(int paramInt)
    {
        if (this.mTxtTitle == null) {
            return;
        }

        if (paramInt <= 0) {
            this.mTxtTitle.setVisibility(View.GONE);
        }

        this.mTxtTitle.setText(paramInt);
    }

    public void setText(CharSequence paramCharSequence)
    {
        if (this.mTxtTitle == null) {
            return;
        }

        if ((paramCharSequence == null) || (paramCharSequence.length() == 0)) {
            this.mTxtTitle.setVisibility(View.GONE);
        }

        this.mTxtTitle.setText(paramCharSequence);
    }
}
