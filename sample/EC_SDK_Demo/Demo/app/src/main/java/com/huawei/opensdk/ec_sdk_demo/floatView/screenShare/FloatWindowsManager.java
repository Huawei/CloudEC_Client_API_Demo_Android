package com.huawei.opensdk.ec_sdk_demo.floatView.screenShare;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.floatView.annotation.AnnotationWinManager;
import com.huawei.opensdk.ec_sdk_demo.floatView.screenShare.rom.HuaweiUtils;
import com.huawei.opensdk.ec_sdk_demo.floatView.screenShare.rom.MeizuUtils;
import com.huawei.opensdk.ec_sdk_demo.floatView.screenShare.rom.MiuiUtils;
import com.huawei.opensdk.ec_sdk_demo.floatView.screenShare.rom.OppoUtils;
import com.huawei.opensdk.ec_sdk_demo.floatView.screenShare.rom.RomUtils;
import com.huawei.opensdk.ec_sdk_demo.floatView.util.LayoutUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


public class FloatWindowsManager {

    private static final String TAG = "FloatWindowsManager";

    private static volatile FloatWindowsManager mInstance;

    private WindowManager mWindowManager;

    private ScreenShareFloatWindowView screenShareFloatWindow;

    private LayoutParams screenShareFloatWindowParams;

    private AnnotationWinManager annotToolbarManager;

    public static FloatWindowsManager getInstance() {
        if (mInstance == null) {
            synchronized (FloatWindowsManager.class) {
                if (mInstance == null) {
                    mInstance = new FloatWindowsManager();
                }
            }
        }
        return mInstance;
    }


    /**
     * 创建屏幕共享悬浮窗口
     *
     * @param context
     */
    public void createScreenShareFloatWindow(Context context) {
        LogUtil.i(UIConstants.DEMO_TAG,"create ScreenShareFloatWindow start");
        removeScreenShareFloatWindow(context);
        int screenHeight = LayoutUtil.getScreenHeight();
        int marginLeft = context.getResources().getDimensionPixelSize(R.dimen.dp_15);
        int marginBottom = context.getResources().getDimensionPixelSize(R.dimen.dp_162);
        if (screenShareFloatWindow == null) {
            screenShareFloatWindow = new ScreenShareFloatWindowView(context);
            if (screenShareFloatWindowParams == null) {
                screenShareFloatWindowParams = new LayoutParams();
                screenShareFloatWindowParams.packageName = context.getPackageName();
                screenShareFloatWindowParams.type = LayoutUtil.getFloatWinLayoutParamsType();

                screenShareFloatWindowParams.format = PixelFormat.RGBA_8888;
                screenShareFloatWindowParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL | LayoutParams.FLAG_NOT_FOCUSABLE;
                screenShareFloatWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
                screenShareFloatWindowParams.width = screenShareFloatWindow.getmViewWidth();
                screenShareFloatWindowParams.height = screenShareFloatWindow.getmViewHeight();
                screenShareFloatWindowParams.x = marginLeft;
                screenShareFloatWindowParams.y = screenHeight - marginBottom;
            }
            screenShareFloatWindow.setParams(screenShareFloatWindowParams);
            WindowManager windowManager = (WindowManager)context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
            windowManager.addView(screenShareFloatWindow, screenShareFloatWindowParams);
            LogUtil.i(UIConstants.DEMO_TAG,"create ScreenShareFloatWindow end");
        }
    }

    public void createAnnotToolbarManager() {
        LogUtil.i(UIConstants.DEMO_TAG,TAG + " enter createAnnotToolbarManager");
        removeAnnotToolbarManager();
        annotToolbarManager = new AnnotationWinManager();
        LogUtil.i(UIConstants.DEMO_TAG,TAG + " leave createAnnotToolbarManager");
    }

    public void showAnnotationToolbar() {
        LogUtil.i(UIConstants.DEMO_TAG,TAG + " enter showAnnotationToolbar ");
        if (annotToolbarManager != null) {
            annotToolbarManager.showAnnotationToolbar();
        }
        LogUtil.i(UIConstants.DEMO_TAG,TAG + " leave showAnnotationToolbar ");
    }

    public void removeAnnotToolbarManager() {
        LogUtil.i(UIConstants.DEMO_TAG,TAG + " enter removeAnnotToolbarManager ");
        if (annotToolbarManager != null) {
            annotToolbarManager.destroy();
            annotToolbarManager = null;
        }
        LogUtil.i(UIConstants.DEMO_TAG,TAG + " leave removeAnnotToolbarManager ");
    }


    public void removeScreenShareFloatWindow(Context context) {
        LogUtil.i(UIConstants.DEMO_TAG,"remove ScreenShareFloatWindow start " + screenShareFloatWindow);
        if (screenShareFloatWindow != null) {
            try {
                WindowManager windowManager = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
                windowManager.removeViewImmediate(screenShareFloatWindow);
            } catch (Exception e) {
                LogUtil.e(UIConstants.DEMO_TAG,"removeScreenShareFloatWindow e: " + e.toString());
            } finally {
                screenShareFloatWindow = null;
                screenShareFloatWindowParams = null;
                LogUtil.i(UIConstants.DEMO_TAG,"remove ScreenShareFloatWindow end");
            }
        }
    }



    /**
     * 移除所有屏幕共享相关的悬浮窗
     * @param context
     */
    public void removeAllScreenShareFloatWindow(Context context) {
        LogUtil.i(UIConstants.DEMO_TAG,"enter removeAllScreenShareFloatWindow ");
        removeScreenShareFloatWindow(context);
        removeAnnotToolbarManager();
        LogUtil.i(UIConstants.DEMO_TAG,"leave removeAllScreenShareFloatWindow ");
    }

    private WindowManager getWindowManager(Context context) {
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }
        return mWindowManager;
    }


    public boolean checkPermission(Context context) {
        //6.0 版本之后由于 google 增加了对悬浮窗权限的管理，所以方式就统一了
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (RomUtils.checkIsMiuiRom()) {
                return miuiPermissionCheck(context);
            } else if (RomUtils.checkIsMeizuRom()) {
                return meizuPermissionCheck(context);
            } else if (RomUtils.checkIsHuaweiRom()) {
                return huaweiPermissionCheck(context);
            } else if (RomUtils.checkIsOppoRom()) {
                return oppoROMPermissionCheck(context);
            }
        }
        return commonROMPermissionCheck(context);
    }

    private boolean commonROMPermissionCheck(Context context) {
        if (RomUtils.checkIsMeizuRom()) {
            return meizuPermissionCheck(context);
        } else {
            Boolean result = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                try {
                    Class clazz = Settings.class;
                    Method canDrawOverlays = clazz.getDeclaredMethod("canDrawOverlays", Context.class);
                    result = (Boolean) canDrawOverlays.invoke(null, context);
                } catch (RuntimeException e) {
                    throw e;
                } catch (Exception e) {
                    LogUtil.e(UIConstants.DEMO_TAG,"commonROMPermissionCheck error ");
                }
            }
            return result;
        }
    }

    private boolean huaweiPermissionCheck(Context context) {
        return HuaweiUtils.checkFloatWindowPermission(context);
    }

    private boolean miuiPermissionCheck(Context context) {
        return MiuiUtils.checkFloatWindowPermission(context);
    }

    private boolean meizuPermissionCheck(Context context) {
        return MeizuUtils.checkFloatWindowPermission(context);
    }

    private boolean oppoROMPermissionCheck(Context context) {
        return OppoUtils.checkFloatWindowPermission(context);
    }

    public void applyPermission(Context context) {
        LogUtil.i(UIConstants.DEMO_TAG,"enter apply float window permission.");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (RomUtils.checkIsMiuiRom()) {
                miuiROMPermissionApply(context);
            } else if (RomUtils.checkIsMeizuRom()) {
                meizuROMPermissionApply(context);
            } else if (RomUtils.checkIsHuaweiRom()) {
                huaweiROMPermissionApply(context);
            } else if (RomUtils.checkIsOppoRom()) {
                oppoROMPermissionApply(context);
            }
        } else {
            commonROMPermissionApply(context);
        }
    }

    private void huaweiROMPermissionApply(final Context context) {
        HuaweiUtils.applyPermission(context);
    }

    private void meizuROMPermissionApply(final Context context) {
        MeizuUtils.applyPermission(context);
    }

    private void miuiROMPermissionApply(final Context context) {
        MiuiUtils.applyMiuiPermission(context);
    }

    private void oppoROMPermissionApply(final Context context) {
        OppoUtils.applyOppoPermission(context);
    }

    /**
     * 通用 rom 权限申请
     */
    private void commonROMPermissionApply(final Context context) {
        //这里也一样，魅族系统需要单独适配
        if (RomUtils.checkIsMeizuRom()) {
            meizuROMPermissionApply(context);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                try {
                    commonROMPermissionApplyInternal(context);
                } catch (Exception e) {
                    LogUtil.e(UIConstants.DEMO_TAG," commonROMPermissionApply Exception");
                }
            }
        }
    }

    public static void commonROMPermissionApplyInternal(Context context) throws NoSuchFieldException, IllegalAccessException {
        Class clazz = Settings.class;
        Field field = clazz.getDeclaredField("ACTION_MANAGE_OVERLAY_PERMISSION");

        Intent intent = new Intent(field.get(null).toString());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivity(intent);
    }
}
