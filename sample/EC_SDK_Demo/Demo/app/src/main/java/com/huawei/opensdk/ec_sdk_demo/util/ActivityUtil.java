package com.huawei.opensdk.ec_sdk_demo.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.ui.IntentConstant;

public final class ActivityUtil
{
    private ActivityUtil()
    {
    }

    public static void startActivity(Context context, String action, String[] categorys)
    {
        try
        {
            Intent intent = new Intent(action);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            for (int i = 0; i < categorys.length; i++)
            {
                intent.addCategory(categorys[i]);
            }
            context.startActivity(intent);
        }
        catch (ActivityNotFoundException e)
        {
            Log.e(UIConstants.DEMO_TAG, e.getMessage());
        }
    }

    public static void startActivity(Context context, String action)
    {
        try
        {
            Intent intent = new Intent(action);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(IntentConstant.DEFAULT_CATEGORY);
            context.startActivity(intent);
        }
        catch (ActivityNotFoundException e)
        {
            Log.e(UIConstants.DEMO_TAG, e.getMessage());
        }
    }

    public static void startActivity(Context context, Intent intent)
    {
        try
        {
            context.startActivity(intent);
        }
        catch (ActivityNotFoundException e)
        {
            Log.e(UIConstants.DEMO_TAG, e.getMessage());
        }
    }

    public static void startActivityForResult(Activity activity, Intent intent, int requestCode)
    {
        try
        {
            intent.addCategory(IntentConstant.DEFAULT_CATEGORY);
            activity.startActivityForResult(intent, requestCode);
        }
        catch (ActivityNotFoundException e)
        {
            Log.e(UIConstants.DEMO_TAG, e.getMessage());
        }
    }

    public static void startActivityForResult(Activity activity, String action, int requestCode)
    {
        try
        {
            Intent intent = new Intent(action);
            intent.addCategory(IntentConstant.DEFAULT_CATEGORY);
            activity.startActivityForResult(intent, requestCode);
        }
        catch (ActivityNotFoundException e)
        {
            Log.e(UIConstants.DEMO_TAG, e.getMessage());
        }
    }

    /**
     * 获取当前显示的activity名称
     * @param context
     * @return
     */
    public static String getCurrentActivity(Context context)
    {
        ActivityManager manager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        ComponentName componentName = manager.getRunningTasks(1).get(0).topActivity;
        String className = componentName.getClassName();
        if (null == className)
        {
            return null;
        }

        if (!className.contains("."))
        {
            return className;
        }

        String[] str = className.split("\\.");
        return str[str.length - 1];
    }
}
