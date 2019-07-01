package com.huawei.opensdk.ec_sdk_demo.floatView.screenShare.rom;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;

import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;

import java.lang.reflect.Method;

public class OppoUtils {

    private static final String TAG = "OppoUtils";

    /**
     * 检测 oppo 悬浮窗权限
     */
    public static boolean checkFloatWindowPermission(Context context) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= Build.VERSION_CODES.KITKAT) {
            return checkOp(context, 24); //OP_SYSTEM_ALERT_WINDOW = 24;
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static boolean checkOp(Context context, int op) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= Build.VERSION_CODES.KITKAT) {
            AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            try {
                Class clazz = AppOpsManager.class;
                Method method = clazz.getDeclaredMethod("checkOp", int.class, int.class, String.class);
                return AppOpsManager.MODE_ALLOWED == (int) method.invoke(manager, op, Binder.getCallingUid(), context.getPackageName());
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                LogUtil.e(UIConstants.DEMO_TAG," checkOp Exception ");
            }
        } else {
            LogUtil.e(UIConstants.DEMO_TAG," Below API 19 cannot invoke ");
        }
        return false;
    }

    /**
     * oppo ROM 权限申请
     */
    public static void applyOppoPermission(Context context) {
        try {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName comp = new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.sysfloatwindow.FloatWindowListActivity");//悬浮窗管理页面
            intent.setComponent(comp);
            context.startActivity(intent);
        } catch(Exception e) {
            LogUtil.e(UIConstants.DEMO_TAG," applyOppoPermission Exception ");
        }
    }
}
