package com.huawei.opensdk.ec_sdk_demo.ui.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.huawei.opensdk.commonservice.util.DeviceManager;
import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.ec_sdk_demo.ECApplication;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NetworkConnectivityListener {

    public static final String TAG = NetworkConnectivityListener.class.getSimpleName();

    private Context mContext;

    private List<OnNetWorkListener> listeners = new ArrayList<>();

    private boolean mListening;

    private ConnectivityBroadcastReceiver mReceiver;

    private class ConnectivityBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtil.i("onReceive. intent: " , intent.toString());
            processNetWorkChangeEvent(context, intent);
        }
    }

    private synchronized void processNetWorkChangeEvent(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null && (!action.equals(ConnectivityManager.CONNECTIVITY_ACTION) || !ismListening())) {
            LogUtil.e("no need handle this action. ","");
            return;
        }

        ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = connManager.getActiveNetworkInfo();
        updateConnectionInfo(mNetworkInfo);
    }


    /**
     * Create a new NetworkConnectivityListener.
     */
    public NetworkConnectivityListener() {
        mReceiver = new ConnectivityBroadcastReceiver();
    }

    /**
     * This method starts listening for network connectivity state changes.
     *
     * @param context
     */
    public synchronized void startListening(Context context) {
        if (!mListening) {
            mContext = context;

            IntentFilter filter = new IntentFilter();
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            // 系统广播不会存在安全问题，添加权限字符串规避安全扫描
            String permission = "com.huawei.permission";
            context.registerReceiver(mReceiver, filter, permission, null);
            mListening = true;
        }
    }

    /**
     * This method stops this class from listening for network changes.
     */
    public synchronized void stopListening() {
        if (mListening) {
            if (null != mContext) {
                mContext.unregisterReceiver(mReceiver);
                mContext = null;
            }
            mListening = false;
        }
    }

    public synchronized boolean ismListening() {
        return mListening;
    }

    public void registerListener(OnNetWorkListener listener) {
        if (null != listeners)
        {
            listeners.clear();
        }
        listeners.add(listener);
    }

    public void deregisterListener(OnNetWorkListener listener) {
        listeners.remove(listener);
    }

    public interface OnNetWorkListener {
        void onNetWorkChange(JSONObject nwd);
    }

    /**
     * Updates the JavaScript side whenever the connection changes
     *
     * @param info the current active network info
     * @return
     */
    private void updateConnectionInfo(NetworkInfo info) {
        // send update to javascript "navigator.network.connection"
        // Jellybean sends its own info
        JSONObject thisInfo = DeviceManager.getConnectionInfo(info);
        if (!thisInfo.equals(ECApplication.getLastInfo())) {
            for (OnNetWorkListener listener : listeners) {
                listener.onNetWorkChange(thisInfo);
            }
        }
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
