package com.huawei.opensdk.ec_sdk_demo.floatView.annotation;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.demoservice.MeetingMgr;
import com.huawei.opensdk.ec_sdk_demo.ECApplication;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.floatView.annotation.common.AnnotationConstants;
import com.huawei.opensdk.ec_sdk_demo.floatView.annotation.common.AnnotationHelper;
import com.huawei.opensdk.ec_sdk_demo.floatView.annotation.widget.DragToolbarView;
import com.huawei.opensdk.ec_sdk_demo.floatView.annotation.widget.ToolbarButton;
import com.huawei.opensdk.ec_sdk_demo.floatView.annotation.widget.adapter.ColorPickAdapter;
import com.huawei.opensdk.ec_sdk_demo.floatView.screenShare.FloatWindowsManager;
import com.huawei.opensdk.ec_sdk_demo.floatView.util.LayoutUtil;

import static com.huawei.opensdk.commonservice.common.LocContext.getResources;


/**
 *  标注相关悬浮窗管理类  包括 标注工具条悬浮窗 颜色选择悬浮窗 标注悬浮窗
 *  用于主动屏幕共享场景
 *
 */
public class AnnotationWinManager implements View.OnClickListener {

    private static final String TAG = AnnotationWinManager.class.getSimpleName();

    private WindowManager.LayoutParams mToolbarLayoutParams;

    // 全屏透明悬浮窗
    private View mFullScreenTransView;

    // 标注工具条悬浮窗
    private DragToolbarView mAnnotationToolbarView;

    private ToolbarButton mErase; // 擦除

    private ImageView mExit; // 退出

    private ToolbarButton mPen;  // 画笔

    private ToolbarButton mEmpty; // 清空

    private ToolbarButton mColor; // 颜色

    private ListView mColorList;

    private ColorPickAdapter mColorListAdapter;

    private WindowManager.LayoutParams mColorPickWindowParams;

    // 颜色选择悬浮窗
    private View mColorPickView;

    private View mColorDissmissView;

    private boolean isColorPickViewShow = false;

    // 标注绘制悬浮窗
    private AnnotationDrawingView mAnnotationDrawingView;

    // 当前选中的按钮
    private ToolbarButton mSelectedButton;

    private final Context mContext = ECApplication.getApp();

    private final WindowManager mWindowManager = (WindowManager)this.mContext.getSystemService(Context.WINDOW_SERVICE);

    private ImageView mArrayUpView;

    private ImageView mArrayDownView;

    private AnnotationHelper anntationHelper = new AnnotationHelper();

    private int mOrientation;

    private int mScreenWidth;

    private int mScreenHeight;

    private int currentPenColor = 0;


    public AnnotationWinManager() {
        init();
    }

    private void init() {
        LogUtil.i(TAG," enter init ");
        getScreenOrientation();
        addFullScreenTransView();
        addAnnotationDrawingView();
        addColorPickView();
        addAnnotationToolbarView();
    }

    public void showAnnotationToolbar() {
        LogUtil.i(TAG," enter showAnnotationToolbar ");
        showAnnotationDrawingView();
        showAnnotationToolbarView();
        hideFullScreenTransView();
    }

    private void addFullScreenTransView() {
        LogUtil.i(TAG," enter addFullScreenTransView ");
        mFullScreenTransView = View.inflate(this.mContext, R.layout.fullscreen_trans_layout, null);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = LayoutUtil.getFloatWinLayoutParamsType();
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        layoutParams.x = 0;
        layoutParams.y = 0;
        layoutParams.width = mScreenWidth;
        layoutParams.height = mScreenHeight;
        layoutParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_FULLSCREEN;
        layoutParams.screenOrientation = mOrientation;

        mWindowManager.addView(mFullScreenTransView, layoutParams);
        LogUtil.i(TAG," leave addFullScreenTransView ");
    }

    private void addAnnotationDrawingView() {
        LogUtil.i(TAG," enter addAnnotationDrawingView ");
        mAnnotationDrawingView = new AnnotationDrawingView(mContext);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = LayoutUtil.getFloatWinLayoutParamsType();
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        layoutParams.x = 0;
        layoutParams.y = 0;
        layoutParams.width = mScreenWidth;
        layoutParams.height = mScreenHeight;
        layoutParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_FULLSCREEN;
        layoutParams.screenOrientation = mOrientation;

        mWindowManager.addView(mAnnotationDrawingView, layoutParams);
        mAnnotationDrawingView.setVisibility(View.GONE);
        LogUtil.i(TAG," leave addAnnotationDrawingView ");
    }

    private void addAnnotationToolbarView() {
        LogUtil.i(TAG," enter addAnnotationToolbarView ");
        mAnnotationToolbarView = ((DragToolbarView)View.inflate(this.mContext, R.layout.annot_toolbar_float_layout, null));
        mColor = (ToolbarButton) mAnnotationToolbarView.findViewById(R.id.float_anno_color);
        mEmpty = (ToolbarButton) mAnnotationToolbarView.findViewById(R.id.float_anno_empty);
        mErase = (ToolbarButton) mAnnotationToolbarView.findViewById(R.id.float_anno_erase);
        mPen = (ToolbarButton) mAnnotationToolbarView.findViewById(R.id.float_anno_pen);
        mExit = (ImageView) mAnnotationToolbarView.findViewById(R.id.float_anno_exit);
        mColor.setText(R.string.anno_color);
        mPen.setText(R.string.anno_pen);
        mErase.setText(R.string.anno_erase);
        mEmpty.setText(R.string.anno_empty);
        mPen.setSelected(true);

        mEmpty.setIconBackgroundResource(R.drawable.float_anno_empty);
        mErase.setIconBackgroundResource(R.drawable.float_anno_erase);
        mPen.setIconBackgroundResource(R.drawable.float_anno_pen_red);
        mColor.setIconBackgroundResource(R.drawable.float_anno_color_red);
        mExit.setBackgroundResource(R.drawable.float_anno_exit);

        mEmpty.setOnClickListener(this);
        mErase.setOnClickListener(this);
        mPen.setOnClickListener(this);
        mColor.setOnClickListener(this);
        mExit.setOnClickListener(this);
        mAnnotationToolbarView.setGestureDetectorListener(new GuestureListener());
        mToolbarLayoutParams = new WindowManager.LayoutParams();
        mToolbarLayoutParams.packageName = mContext.getPackageName();
        mToolbarLayoutParams.type = LayoutUtil.getFloatWinLayoutParamsType();

        mToolbarLayoutParams.format = PixelFormat.RGBA_8888;
        mToolbarLayoutParams.flags = LayoutUtil.getFloatWinLayoutParamsFlags();
        mToolbarLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        mToolbarLayoutParams.width = mContext.getResources().getDimensionPixelSize(R.dimen.dp_340);
        mToolbarLayoutParams.height = mContext.getResources().getDimensionPixelSize(R.dimen.dp_48);
        int marginLeft = (mScreenWidth - mToolbarLayoutParams.width) / 2;
        int marginBottom = mContext.getResources().getDimensionPixelSize(R.dimen.dp_30) + mToolbarLayoutParams.height;
        if (mOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            marginBottom += LayoutUtil.getStatusBarHeight() + mToolbarLayoutParams.height;
        }
        mToolbarLayoutParams.x = marginLeft;
        mToolbarLayoutParams.y = mScreenHeight - marginBottom;
        mToolbarLayoutParams.screenOrientation = mOrientation;
        mWindowManager.addView(mAnnotationToolbarView, mToolbarLayoutParams);
        mAnnotationToolbarView.setVisibility(View.GONE);
        LogUtil.i(TAG," leave addAnnotationToolbarView ");
    }


    private void addColorPickView() {
        LogUtil.i(TAG," enter addColorPickView ");
        anntationHelper.initColorList();
        mColorDissmissView = new View(mContext);
        mColorDissmissView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideColorPickView();
                return false;
            }
        });
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = LayoutUtil.getFloatWinLayoutParamsType();
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.flags = LayoutUtil.getFloatWinLayoutParamsFlags();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        mWindowManager.addView(mColorDissmissView, layoutParams);

        mColorPickView = LayoutInflater.from(mContext).inflate(R.layout.float_color_pick_layout, null);
        mColorList = (ListView) mColorPickView.findViewById(R.id.float_color_pick_list);
        mArrayDownView = (ImageView) mColorPickView.findViewById(R.id.array_down);
        mArrayUpView = (ImageView) mColorPickView.findViewById(R.id.array_up);
        mColorListAdapter = new ColorPickAdapter(anntationHelper.getColorList(), mContext);
        mColorList.setAdapter(mColorListAdapter);
        mColorList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                updateSelectColor(anntationHelper.getColorList().get(arg2));
                hideColorPickView();
            }
        });
        mColorPickWindowParams = new WindowManager.LayoutParams();
        mColorPickWindowParams.packageName = mContext.getPackageName();
        mColorPickWindowParams.type = LayoutUtil.getFloatWinLayoutParamsType();

        mColorPickWindowParams.format = PixelFormat.RGBA_8888;
        mColorPickWindowParams.flags = LayoutUtil.getFloatWinLayoutParamsFlags();
        mColorPickWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
        mColorPickWindowParams.width = mContext.getResources().getDimensionPixelSize(R.dimen.dp_40);
        mColorPickWindowParams.height = mContext.getResources().getDimensionPixelSize(R.dimen.dp_120) + mContext.getResources().getDimensionPixelSize(R.dimen.array_down_height);
        mWindowManager.addView(mColorPickView, mColorPickWindowParams);
        hideColorPickView();
        LogUtil.i(TAG," leave addColorPickView ");
    }

    private void showAnnotationDrawingView() {
        LogUtil.i(TAG," enter showAnnotationDrawingView ");
        if (mAnnotationDrawingView != null) {
            mAnnotationDrawingView.setVisibility(View.VISIBLE);
        }
    }

    private void showAnnotationToolbarView() {
        LogUtil.i(TAG," enter showAnnotationToolbarView ");
        if (mAnnotationToolbarView != null) {
            mAnnotationToolbarView.setVisibility(View.VISIBLE);
        }
    }

    private void showColorPickView() {
        LogUtil.i(TAG," enter showColorPickView ");
        isColorPickViewShow = true;
        if (mColorPickView != null) {
            mColorPickView.setVisibility(View.VISIBLE);
        }

        if (mColorDissmissView != null) {
            mColorDissmissView.setVisibility(View.VISIBLE);
        }

        updateColorPickViewLayout();
    }

    private void hideColorPickView() {
        LogUtil.i(TAG," enter hideColorPickView ");
        isColorPickViewShow = false;
        if (mColorPickView != null) {
            mColorPickView.setVisibility(View.GONE);
        }

        if (mColorDissmissView != null) {
            mColorDissmissView.setVisibility(View.GONE);
        }
    }


    private void hideFullScreenTransView() {
        LogUtil.i(TAG," enter hideFullScreenTransView ");
        if (mFullScreenTransView != null) {
            mFullScreenTransView.setVisibility(View.GONE);
        }
    }

    private void updateColorPickViewLayout() {
        if (mAnnotationToolbarView == null
                || mColorPickView == null
                || mColorPickView.getVisibility() == View.GONE
                || mColorPickWindowParams == null
                || mToolbarLayoutParams == null) {
            LogUtil.e(TAG,"updateColorPickViewLayout error ");
            return;
        }

        int position = (mToolbarLayoutParams.width / 4 - mColorPickWindowParams.width) / 2;
        mColorPickWindowParams.x = mToolbarLayoutParams.x + position;
        mColorPickWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
        if (mToolbarLayoutParams.y > mColorPickWindowParams.height) {
            mColorPickWindowParams.y = mToolbarLayoutParams.y - mColorPickWindowParams.height;
            mArrayUpView.setVisibility(View.GONE);
            mArrayDownView.setVisibility(View.VISIBLE);
        } else {
            mColorPickWindowParams.y = mToolbarLayoutParams.y + mToolbarLayoutParams.height;
            mArrayUpView.setVisibility(View.VISIBLE);
            mArrayDownView.setVisibility(View.GONE);
        }

        mWindowManager.updateViewLayout(mColorPickView, mColorPickWindowParams);
    }

    public synchronized void destroy() {
        LogUtil.i(TAG," enter destroy ");
        try {
            if (mWindowManager != null) {
                if (mFullScreenTransView != null) {
                    mWindowManager.removeViewImmediate(mFullScreenTransView);
                }
                if (mAnnotationDrawingView != null) {
                    mAnnotationDrawingView.clearData();
                    mWindowManager.removeViewImmediate(mAnnotationDrawingView);
                }
                if (mAnnotationToolbarView != null) {
                    mWindowManager.removeViewImmediate(mAnnotationToolbarView);
                }

                if (mColorDissmissView != null) {
                    mWindowManager.removeViewImmediate(mColorDissmissView);
                }

                if (mColorPickView != null) {
                    mWindowManager.removeViewImmediate(mColorPickView);
                }
            }
        } catch (Exception e) {
             LogUtil.i(TAG," destory Exception " + e.toString());
        } finally {
            mFullScreenTransView = null;
            mAnnotationDrawingView = null;
            mAnnotationToolbarView = null;
            mToolbarLayoutParams = null;
            mColorPickView = null;
            mColorDissmissView = null;
            mColorPickWindowParams = null;
            isColorPickViewShow = false;
            LogUtil.i(TAG," leave destroy ");
        }

    }

    private void updateSelectColor(int color) {
         LogUtil.i(TAG,"userClick select color: " + color);
        if (mColor != null) {
            mColor.setIconBackgroundResource(anntationHelper.getAnnoColorImg(color));
        }
        Integer penColorInt = anntationHelper.getColorMap().get(color);
        if (null == penColorInt)
        {
            //默认颜色红色
            penColorInt = AnnotationConstants.PEN_COLOR_RED;
        }
        int penWidth = (getResources().getDimensionPixelSize(R.dimen.dp_6) * 1440 / LayoutUtil.getScreenDensityDpi());
        currentPenColor  = penColorInt;

        // 选择颜色后，默认设置为笔
        if (mPen != null) {
            mPen.setIconBackgroundResource(anntationHelper.getAnnoPenImg(color));
            mPen.setSelected(true);
            mSelectedButton = mPen;
        }

        if (mErase != null) {
            mErase.setSelected(false);
        }

        if (mAnnotationDrawingView != null) {
            mAnnotationDrawingView.handleAnnotSwitch(currentPenColor, penWidth);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id != R.id.float_anno_color) {
            hideColorPickView();
        }

        if (id == R.id.float_anno_color) {
            LogUtil.i(TAG," userClick annotoobar color ");
            if (isColorPickViewShow) {
                hideColorPickView();
            } else {
                showColorPickView();
            }
        }
        else if (id == R.id.float_anno_empty) {
            LogUtil.i(TAG," userClick annotoobar empty ");
            MeetingMgr.getInstance().clearAnnotation(false);
        }
        else if (id == R.id.float_anno_erase) {
            LogUtil.i(TAG," userClick annotoobar erase ");
            if (mSelectedButton != mErase) {
                mErase.setSelected(true);
                mPen.setSelected(false);
                mSelectedButton = mErase;
                MeetingMgr.getInstance().eraseAnnotation(true);
            }
        }
        else if (id == R.id.float_anno_exit) {
            LogUtil.i(TAG," userClick annotoobar exit ");
            MeetingMgr.getInstance().setAnnotationLocalStatus(false);
            MeetingMgr.getInstance().stopAnnotation();
            FloatWindowsManager.getInstance().removeAnnotToolbarManager();
            FloatWindowsManager.getInstance().createScreenShareFloatWindow(ECApplication.getApp());
        }
        else if (id == R.id.float_anno_pen) {
            LogUtil.i(TAG," userClick annotoobar pen ");
            if (mSelectedButton != mPen) {
                mErase.setSelected(false);
                mPen.setSelected(true);
                mSelectedButton = mPen;
                int penWidth = (getResources().getDimensionPixelSize(R.dimen.dp_6) * 1440 / LayoutUtil.getScreenDensityDpi());
                if (currentPenColor == 0){
                    currentPenColor = AnnotationConstants.PEN_COLOR_RED;
                }
                MeetingMgr.getInstance().setAnnotationPen(currentPenColor, penWidth);
            }
        } else {
            LogUtil.i(TAG," unknown click ");
        }
    }

    private class GuestureListener
            extends DragToolbarView.ToolbarScrollListener
    {
        float mLastRawX = -1.0F;
        float mLastRawY = -1.0F;

        public GuestureListener() {}

        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (mToolbarLayoutParams == null || mAnnotationToolbarView == null) {
                LogUtil.e(TAG, " mToolbarLayoutParams == null or mAnnotationToolbarView == null ");
                return true;
            }
            int width = mAnnotationToolbarView.getWidth();
            int height = mAnnotationToolbarView.getHeight();
            int screenWidth = LayoutUtil.getRealWindowWidth();
            int screenHeight = LayoutUtil.getRealWindowHeight();

            if (screenHeight > screenWidth) {
                screenHeight = screenHeight - LayoutUtil.getNavigationBarHeight();
            } else {
                screenWidth -= LayoutUtil.getNavigationBarHeight();
            }

            // 获取移动时的X，Y坐标
            float nowX, nowY, tranX, tranY;
            if (Math.abs(mLastRawX + 1) < 0.0000001
                    || Math.abs(mLastRawY + 1) < 0.0000001) {
                mLastRawX = e1.getRawX();
                mLastRawY = e1.getRawY();
            }
            nowX = e2.getRawX();
            nowY = e2.getRawY();
            // 计算XY坐标偏移量
            tranX = nowX - mLastRawX;
            tranY = nowY - mLastRawY;

            if (tranX + mToolbarLayoutParams.x < 0) {
                tranX = -mToolbarLayoutParams.x;
            } else if (tranX + mToolbarLayoutParams.x + width > screenWidth) {
                tranX = screenWidth - width - mToolbarLayoutParams.x;
            }

            if (tranY + mToolbarLayoutParams.y < 0) {
                tranY = -mToolbarLayoutParams.y;
            } else if (tranY + mToolbarLayoutParams.y + height > screenHeight) {
                tranY = screenHeight - height - mToolbarLayoutParams.y;
            }

            // 移动悬浮窗
            mToolbarLayoutParams.x += tranX;
            mToolbarLayoutParams.y += tranY;
            //更新悬浮窗位置
            mWindowManager.updateViewLayout(mAnnotationToolbarView, mToolbarLayoutParams);
            //记录当前坐标作为下一次计算的上一次移动的位置坐标
            mLastRawX = nowX;
            mLastRawY = nowY;

            if (isColorPickViewShow) {
                hideColorPickView();
            }
            return true;
        }


        public void onTouchEventUp() {
            this.mLastRawX = -1.0F;
            this.mLastRawY = -1.0F;
        }
    }

    private void getScreenOrientation() {
        mScreenHeight = LayoutUtil.getRealWindowHeight();
        mScreenWidth = LayoutUtil.getRealWindowWidth();
        if (mScreenHeight > mScreenWidth) {
            mOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        } else {
            mOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        }

        LogUtil.i(TAG," getScreenOrientation orientation: " + mOrientation + " (0: LANDSCAPE 1: PORTRAIT)");
    }
}
