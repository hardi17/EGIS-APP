package com.studentguide.utils.network;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.studentguide.utils.Logger;


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

            //SLog.i("isConnected : " + activeInfo.isConnected());
            //SLog.i("isConnectedOrConnecting : "+ activeInfo.isConnectedOrConnecting());
            //Tlog.i("isAvailable : " + activeInfo.isAvailable());
            //Tlog.i("isFailover : " + activeInfo.isFailover());
            //Tlog.i("isRoaming : " + activeInfo.isRoaming());
            //Tlog.i("isRoaming : " + activeInfo.isRoaming());
            //Tlog.i("getType : " + activeInfo);

            switch (activeInfo.getType()) {
                case ConnectivityManager.TYPE_MOBILE:
                    //SLog.i("TYPE_MOBILE : "+ ConnectivityManager.TYPE_MOBILE);
                    isConnected = true;
                    break;
                case ConnectivityManager.TYPE_WIFI:
                    //SLog.i("TYPE_WIFI : " + ConnectivityManager.TYPE_WIFI);
                    isConnected = true;
                    break;
                case ConnectivityManager.TYPE_WIMAX:
                    //SLog.i("TYPE_WIMAX : " + ConnectivityManager.TYPE_WIMAX);
                    isConnected = true;
                    break;
                case ConnectivityManager.TYPE_ETHERNET:
                    //SLog.i("TYPE_ETHERNET : "+ ConnectivityManager.TYPE_ETHERNET);
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
