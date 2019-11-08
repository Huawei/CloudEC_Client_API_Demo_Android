package com.huawei.opensdk.ec_sdk_demo.floatView.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.huawei.opensdk.commonservice.common.LocContext;
import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.ec_sdk_demo.ECApplication;

import java.lang.reflect.Field;


/**
 * This class is about Layout util.
 */
public final class LayoutUtil
{
    private static final String TAG = LayoutUtil.class.getSimpleName();

    private static final LayoutUtil INSTANCE = new LayoutUtil();

    private static final float DENSITYDPI_REFERENCE_STANDARD = 1f;

    private static final int HOME_CARD_WIRDH = 390;

    private static final int HOME_SLIDE_VIEW_PADDING_STANDARD = 55;

    private static final int DEFAULT_MOVE_DINSTANCE = 30;

    private static final int WIDTH_REFERENCE_STANDARD = 1280;

    private static boolean isPhoneTag;

    private static float scaleMultiple = -1; // default -1

    private int screenWidth = WIDTH_REFERENCE_STANDARD; // default 1280

    private int screenHeight = 800; // default 800

    private BitmapFactory.Options dpiBitMapOptions = new BitmapFactory.Options();

    private BitmapFactory.Options pxBitMapOptions = new BitmapFactory.Options();

    private BitmapFactory.Options realDPIBitMapOptions = new BitmapFactory.Options();

    private float dpiScale = 1f;

    private double screenPXScale = 1f;

    private float realDensity = 1f;

    private int homeCardRealPxWidth = HOME_CARD_WIRDH;

    private int needMoveDistance = DEFAULT_MOVE_DINSTANCE;

    private int homeSlideViewPadding = HOME_SLIDE_VIEW_PADDING_STANDARD;

    private int densityDpi;

    private LayoutUtil()
    {
    }

    /**
     * Gets screen px scale.
     *
     * @return the screen px scale
     */
    public float getScreenPXScale()
    {
        float tmpScreenPXScale = Double.valueOf(screenPXScale).floatValue();
        return tmpScreenPXScale;
    }

    /**
     * initialize
     *
     * @param density
     * @param screenWidthPx
     */
    private void initialize(int screenWidthPx, int height, float density)
    {
        screenWidth = screenWidthPx;
        screenHeight = height;
        LogUtil.i(TAG, "Initialize Screen info density = " + density);

        if (Math.abs(screenWidthPx * DENSITYDPI_REFERENCE_STANDARD
                - WIDTH_REFERENCE_STANDARD * density) < 0.0000001)
        {
            setScaleMultiple(density);
            dpiScale = density;
            LogUtil.i(TAG, "Layout for normal scale screen such as MediaPad S10");
        }
        else
        {
            setScaleMultiple(1);
            dpiScale = 1;
            LogUtil.i(TAG, "Layout for PX scale screen ");
        }

        if (screenWidthPx / density < 900)
        {
            LogUtil.i(TAG, "Layout for mobile screen");
        }
        else
        {
            LogUtil.i(TAG, "Layout for pad screen");
        }

        realDensity = density;
        screenPXScale = (Integer.valueOf(screenWidth > screenHeight
                ? screenWidth : screenHeight).doubleValue() / WIDTH_REFERENCE_STANDARD);
        homeCardRealPxWidth = doubleToInt(HOME_CARD_WIRDH * screenPXScale);
        needMoveDistance = doubleToInt(DEFAULT_MOVE_DINSTANCE * screenPXScale);
        homeSlideViewPadding = doubleToInt(HOME_SLIDE_VIEW_PADDING_STANDARD * screenPXScale);

        dpiBitMapOptions.inDensity = doubleToInt(dpiScale * DENSITYDPI_REFERENCE_STANDARD);
        pxBitMapOptions.inDensity = doubleToInt(screenPXScale * DENSITYDPI_REFERENCE_STANDARD);
        realDPIBitMapOptions.inDensity = doubleToInt(density * DENSITYDPI_REFERENCE_STANDARD);
    }

    /**
     * Double to int int.
     *
     * @param floatValue the float value
     * @return the int
     */
    public static int doubleToInt(double floatValue)
    {
        return Double.valueOf(floatValue).intValue();
    }

    /**
     * Sets scale multiple.
     *
     * @param multiple the multiple
     */
    public static void setScaleMultiple(float multiple)
    {
        LayoutUtil.scaleMultiple = multiple;
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static LayoutUtil getInstance()
    {
        return LayoutUtil.INSTANCE;
    }

    /**
     * Sets view end ellipse.
     *
     * @param view the view
     */
    public static void setViewEndEllipse(final TextView view)
    {
        seViewEndEllipse(view, 1);
    }

    /**
     * seViewEndEllipse
     *
     * @param view      the view
     * @param lineCount the line count
     */
    public static void seViewEndEllipse(final TextView view, final int lineCount)
    {
        if (lineCount < 1)
        {
            return;
        }
        ViewTreeObserver vto = view.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener()
        {

            @Override
            public void onGlobalLayout()
            {
                ViewTreeObserver obs = view.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
                if (view.getLineCount() > lineCount)
                {
                    int lineEndIndex = view.getLayout().getLineEnd(lineCount - 1);
                    String text = view.getText().subSequence(0, lineEndIndex) + "...";
                    try
                    {
                        text = view.getText().subSequence(0, lineEndIndex - 2) + "...";
                    }
                    catch (IndexOutOfBoundsException e)
                    {
                        LogUtil.i(TAG, "text size to small.");
                    }
                    view.setText(text);
                }
            }

        });
    }

    /**
     * setEndEllipse
     *
     * @param textView the text view
     * @param str      the str
     * @param len      the len
     */
    public static void setEndEllipse(final TextView textView, final String str, int len)
    {
        if (null == str)
        {
            LogUtil.e(TAG, "str is null");
            return;
        }
        CharSequence charc = TextUtils.ellipsize(str, textView.getPaint(), len, TextUtils.TruncateAt.END);
        textView.setText(charc);
    }

    /**
     * Is phone boolean.
     *
     * @return the boolean
     */
    public static boolean isPhone()
    {
        return isPhoneTag;
    }

    /**
     * Sets is phone.
     *
     * @param isPhone the is phone
     */
    public static void  setIsPhone(boolean isPhone)
    {
         isPhoneTag = isPhone;
    }

    /**
     * setFrontToLock
     *
     * @param activity the activity
     */
    public static void setFrontToLock(Activity activity)
    {
        Window win = activity.getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        win.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }

    /**
     * initialize
     */
    public void initialize()
    {
        DisplayMetrics metric = new DisplayMetrics();

        WindowManager wm = (WindowManager) LocContext.getContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;
        int height = metric.heightPixels;
        float density = metric.density;
        this.densityDpi = metric.densityDpi;

        if (isPadScreen())
        {
            setIsPhone(false);
        }
        else
        {
            setIsPhone(true);
        }

        LogUtil.i(TAG, metric + ",densityDpi:" + densityDpi);
        if (height > width)
        {
            int temp = height;
            height = width;
            width = temp;
        }
        initialize(width, height, density);
    }


    /**
     * isPadScreen
     *
     * @return boolean
     */
    private boolean isPadScreen()
    {
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager wm = (WindowManager) LocContext.getContext().
                getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;
        int height = metric.heightPixels;
        float xdpi = metric.xdpi;
        float ydpi = metric.ydpi;
        float density = metric.density;
        double size = Math.sqrt(Math.pow(width / xdpi, 2) + Math.pow(height / ydpi, 2));
        LogUtil.i(TAG, "screen size: " + size);
        if (size > 6.600000 && Math.max(width, height) / density > 900)
        {
            return true;
        }
        return false;
    }

    /**
     * Return the height of screen, in pixel.
     *
     * @return the height of screen, in pixel
     */
    public static int getScreenHeight() {
        WindowManager wm = (WindowManager) LocContext.getContext().
                getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wm.getDefaultDisplay().getRealSize(point);
        } else {
            wm.getDefaultDisplay().getSize(point);
        }
        return point.y;
    }

    /**
     * 获取悬浮窗Type
     * @return
     */
    public static int getFloatWinLayoutParamsType() {
        int type;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
//            type = WindowManager.LayoutParams.TYPE_TOAST;
            type =  WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        return type;
    }

    /**
     * 获取状态栏的高度
     *
     * @return
     */
    public static int getStatusBarHeight() {
        int mStatusBarHeight = 0;
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object o = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = (Integer) field.get(o);
            return LocContext.getContext().getApplicationContext().getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            LogUtil.e(TAG, "getStatusBarHeight exception.");
        }
        return mStatusBarHeight;
    }

    /**
     * 该方法的作用:dip转换为像素
     * @param context
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int getScreenDensityDpi() {
        return Resources.getSystem().getDisplayMetrics().densityDpi;
    }

    public static int getFloatWinLayoutParamsFlags() {
        int flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_FULLSCREEN;

        return flags;

    }

    /**
     * 获取屏幕真实宽度
     * 包含系统UI，状态栏、导航栏
     *
     * @return realWindowWidth
     */
    public static int getRealWindowWidth() {
        WindowManager mWindowManager = (WindowManager) ECApplication.getApp().getSystemService(Context.WINDOW_SERVICE);
        if (mWindowManager == null) {
            LogUtil.e(TAG, "getWindowWidth: get WindowManager failed");
            return -1;
        }
        Display display = mWindowManager.getDefaultDisplay();

        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        display.getRealMetrics(realDisplayMetrics);

        return realDisplayMetrics.widthPixels;
    }

    /**
     * 获取屏幕真实高度
     * 包含系统UI，状态栏、导航栏
     *
     * @return realWindowHeight
     */
    public static int getRealWindowHeight() {
        WindowManager mWindowManager = (WindowManager) ECApplication.getApp().getSystemService(Context.WINDOW_SERVICE);
        if (mWindowManager == null) {
            LogUtil.e(TAG, "getWindowWidth: get WindowManager failed");
            return -1;
        }
        Display display = mWindowManager.getDefaultDisplay();

        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        display.getRealMetrics(realDisplayMetrics);

        return realDisplayMetrics.heightPixels;
    }

    public static int getNavigationBarHeight() {
        int mNavigationBarHeight = 0;
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object o = c.newInstance();
            Field field = c.getField("navigation_bar_height");
            int x = (Integer) field.get(o);
            return ECApplication.getApp().getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            LogUtil.e(TAG, "getStatusBarHeight exception.");
        }

        return mNavigationBarHeight;
    }

}
