package com.studentguide.utils.map;

import android.content.Context;
import android.location.LocationManager;
import com.studentguide.utils.permissionutils.GPSOnUtils;
import com.studentguide.utils.permissionutils.PermissionData;


/**
 * Created by WINDOWS-D20 on 18-3-2017.
 */

public class CheckGPSAndLocationServiceAvailable {
    public static boolean checkBoth(Context context, GPSOnUtils gpsOnUtils) {
        boolean isPermissionAvailable;

        // Check Location Permission
        if (PermissionData.isNetworkPermission(context)) {

            gpsOnUtils.initializeLocation();
            gpsOnUtils.setRequest();

            // Check GPS is ON/OFF
            final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                // GPS is OFF
                isPermissionAvailable = false;
            } else {
                // GPS is ON
                isPermissionAvailable = true;
            }
        } else {
            isPermissionAvailable = false;
        }

        return isPermissionAvailable;
    }
}
