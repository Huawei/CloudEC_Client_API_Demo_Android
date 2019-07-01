package com.huawei.opensdk.ec_sdk_demo.floatView.screenShare.rom;

import android.os.Build;
import android.text.TextUtils;

import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class RomUtils {
    private static final String TAG = "RomUtils";

    public static double getEmuiVersion() {
        try {
            String emuiVersion = getSystemProperty("ro.build.version.emui");
            if (emuiVersion != null) {
                String version = emuiVersion.substring(emuiVersion.indexOf("_") + 1);
                return Double.parseDouble(version);
            }
        } catch (Exception e) {
            LogUtil.e(UIConstants.DEMO_TAG," getEmuiVersion Exception ");
        }
        return 4.0;
    }

    public static int getMiuiVersion() {
        String version = getSystemProperty("ro.miui.ui.version.name");
        if (version != null) {
            try {
                return Integer.parseInt(version.substring(1));
            } catch (Exception e) {
                LogUtil.e(UIConstants.DEMO_TAG," get miui version code error, version : " + version);
            }
        }
        return -1;
    }

    public static String getSystemProperty(String propName) {
        String line;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(new InputStreamReader(p.getInputStream(), Charset.forName("UTF-8")), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            LogUtil.e(UIConstants.DEMO_TAG," Unable to read sysprop ");
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    LogUtil.e(UIConstants.DEMO_TAG," Exception while closing InputStream");
                }
            }
        }
        return line;
    }
    public static boolean checkIsHuaweiRom() {
        return Build.MANUFACTURER.contains("HUAWEI");
    }

    /**
     * check if is miui ROM
     */
    public static boolean checkIsMiuiRom() {
        return !TextUtils.isEmpty(getSystemProperty("ro.miui.ui.version.name"));
    }

    public static boolean checkIsMeizuRom() {
        String meizuFlymeOSFlag  = getSystemProperty("ro.build.display.id");
        if (TextUtils.isEmpty(meizuFlymeOSFlag)){
            return false;
        }else if (meizuFlymeOSFlag.contains("flyme") || meizuFlymeOSFlag.toLowerCase().contains("flyme")){
            return  true;
        }else {
            return false;
        }
    }

    public static boolean checkIsOppoRom() {
        return Build.MANUFACTURER.contains("OPPO") || Build.MANUFACTURER.contains("oppo");
    }

    public static boolean checkIsOnePlusRom() {
        return Build.BRAND.toLowerCase().equals("oneplus");
    }
}
