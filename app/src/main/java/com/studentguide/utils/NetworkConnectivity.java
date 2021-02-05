package com.studentguide.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkConnectivity {

    Context context;

    public NetworkConnectivity(Context context) {
        super();
        this.context = context;
    }

    /**
     * Check whether the device is connected, and if so, whether the connection
     * is wifi or mobile (it could be something else).
     */
    public boolean isNetworkAvailable() {
        boolean isConnected = false;
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();

        if (connMgr != null) {
            //Tlog.i("getActiveNetworkInfo : " + connMgr.getActiveNetworkInfo());
            //Tlog.i("getAllNetworkInfo : " + connMgr.getAllNetworkInfo());
        }

        if (activeInfo != null
                && (activeInfo.isConnected() || activeInfo.isConnectedOrConnecting())) {
            switch (activeInfo.getType()) {
                case ConnectivityManager.TYPE_MOBILE:
                    isConnected = true;
                    break;
                case ConnectivityManager.TYPE_WIFI:
                    isConnected = true;
                    break;
                case ConnectivityManager.TYPE_WIMAX:
                    isConnected = true;
                    break;
                case ConnectivityManager.TYPE_ETHERNET:
                    isConnected = true;
                    break;

                default:
                    isConnected = false;
                    break;
            }
        }
        Logger.d(""+ "isConnected : " + isConnected);
        return isConnected;
    }

}
