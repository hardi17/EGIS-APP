package com.studentguide.utils.permissionutils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;

/**
 *
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class PermissionData {
    public static final int REQUEST_NETWORK = 1;
    public static final int REQUEST_PHONE = 2;
    public static final int REQUEST_CALENDAR = 3;
    public static final int REQUEST_CAMERA = 4;
    public static final int REQUEST_STORAGE = 5;
    public static final int REQUEST_STORAGE_CAMERA = 11;
    public static final int REQUEST_GET_ACCOUNTS = 6;
    public static final int REQUEST_CALL_PHONE = 7;
    public static final int REQUEST_STORAGE_PHONE = 7;
    public static final int REQUEST_PHONE_NETWORK = 8;
    public static final int REQUEST_RECEIVE_SMS = 10;
    public static final int REQUEST_ALL = 9;

    public static String[] PERMISSIONS_NETWORK =
            {Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION};


    public static String[] PERMISSIONS_PHONE =
            {Manifest.permission.READ_PHONE_STATE};
    public static String[] PERMISSIONS_PHONE_AND_NETWORK =
            {Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE};

    public static String[] PERMISSIONS_CALENDAR =
            {Manifest.permission.READ_CALENDAR,
                    Manifest.permission.WRITE_CALENDAR};

    public static String[] PERMISSIONS_CAMERA =
            {Manifest.permission.CAMERA};
    public static String[] PERMISSIONS_RECEIVE_SMS =
            {Manifest.permission.RECEIVE_SMS};

    public static String[] PERMISSIONS_STORAGE =
            {Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public static String[] PERMISSIONS_STORAGE_CAMERA =
            {Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA};
    public static String[] PERMISSIONS_ALL_REQUIRED =
            {Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.WAKE_LOCK};
    public static String[] PERMISSIONS_PHONE_Store =
            {Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE};

    public static String[] PERMISSIONS_GET_ACCOUNTS =
            {Manifest.permission.GET_ACCOUNTS};

    public static String[] PERMISSIONS_CALL_PHONE =
            {Manifest.permission.CALL_PHONE};

    @TargetApi(Build.VERSION_CODES.M)
    public static boolean isNetworkPermission(Context context) {
        if (PermissionUtils.hasSelfPermission((Activity) context, PERMISSIONS_NETWORK)) {
            return true;
        } else {
            if (((Activity) context).shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
                    && ((Activity) context).shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                ((Activity) context).requestPermissions(PERMISSIONS_NETWORK, REQUEST_NETWORK);
            } else {
                ((Activity) context).requestPermissions(PERMISSIONS_NETWORK, REQUEST_NETWORK);
            }
            return false;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static boolean isNetworkPermissionForFragment(Context context) {
        if (PermissionUtils.hasSelfPermission((Activity) context, PERMISSIONS_NETWORK)) {
            return true;
        } else {
            if (((Activity) context).shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
                    && ((Activity) context).shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                ((Activity) context).requestPermissions(PERMISSIONS_NETWORK, REQUEST_NETWORK);
            } else {
                ((Activity) context).requestPermissions(PERMISSIONS_NETWORK, REQUEST_NETWORK);
            }
            return false;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static boolean isPhonePermission(Context context) {
        if (PermissionUtils.isMNC()) {
            if (PermissionUtils.hasSelfPermission((Activity) context, PERMISSIONS_PHONE)) {
                return true;
            } else {
                if (((Activity) context).shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)) {
                    ((Activity) context).requestPermissions(PERMISSIONS_PHONE, REQUEST_PHONE);
                } else {
                    ((Activity) context).requestPermissions(PERMISSIONS_PHONE, REQUEST_PHONE);
                }
                return false;
            }
        } else {
            return true;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static boolean isCalendarPermission(Context context) {
        if (PermissionUtils.hasSelfPermission((Activity) context, PERMISSIONS_CALENDAR)) {
            return true;
        } else {
            if (((Activity) context).shouldShowRequestPermissionRationale(Manifest.permission.READ_CALENDAR)
                    && ((Activity) context).shouldShowRequestPermissionRationale(Manifest.permission.WRITE_CALENDAR)) {
                ((Activity) context).requestPermissions(PERMISSIONS_CALENDAR, REQUEST_CALENDAR);
            } else {
                ((Activity) context).requestPermissions(PERMISSIONS_CALENDAR, REQUEST_CALENDAR);
            }
            return false;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static boolean isCameraPermission(Context context) {
        if (PermissionUtils.hasSelfPermission((Activity) context, PERMISSIONS_CAMERA)) {
            return true;
        } else {
            if (((Activity) context).shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                ((Activity) context).requestPermissions(PERMISSIONS_CAMERA, REQUEST_CAMERA);
            } else {
                ((Activity) context).requestPermissions(PERMISSIONS_CAMERA, REQUEST_CAMERA);
            }
            return false;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static boolean isReceiveSMSPermission(Context context) {
        if (PermissionUtils.hasSelfPermission((Activity) context, PERMISSIONS_RECEIVE_SMS)) {
            return true;
        } else {
            if (((Activity) context).shouldShowRequestPermissionRationale(Manifest.permission.RECEIVE_SMS)) {
                ((Activity) context).requestPermissions(PERMISSIONS_RECEIVE_SMS, REQUEST_RECEIVE_SMS);
            } else {
                ((Activity) context).requestPermissions(PERMISSIONS_RECEIVE_SMS, REQUEST_RECEIVE_SMS);
            }
            return false;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static boolean checkReceiveSMSPermission(Context context) {
        return PermissionUtils.hasSelfPermission((Activity) context, PERMISSIONS_RECEIVE_SMS);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static boolean isStoragePermission(Context context) {
        if (PermissionUtils.hasSelfPermission((Activity) context, PERMISSIONS_STORAGE)) {
            return true;
        } else {
            if (((Activity) context).shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
                    && ((Activity) context).shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ((Activity) context).requestPermissions(PERMISSIONS_STORAGE, REQUEST_STORAGE);
            } else {
                ((Activity) context).requestPermissions(PERMISSIONS_STORAGE, REQUEST_STORAGE);
            }
            return false;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static boolean isGetAccountsPermission(Context context) {
        if (PermissionUtils.hasSelfPermission((Activity) context, PERMISSIONS_GET_ACCOUNTS)) {
            return true;
        } else {
            if (((Activity) context).shouldShowRequestPermissionRationale(Manifest.permission.GET_ACCOUNTS)) {
                ((Activity) context).requestPermissions(PERMISSIONS_GET_ACCOUNTS, REQUEST_GET_ACCOUNTS);
            } else {
                ((Activity) context).requestPermissions(PERMISSIONS_GET_ACCOUNTS, REQUEST_GET_ACCOUNTS);
            }
            return false;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static boolean isCallPhonePermission(Context context) {
        if (PermissionUtils.hasSelfPermission((Activity) context, PERMISSIONS_CALL_PHONE)) {
            return true;
        } else {
            if (((Activity) context).shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)) {
                ((Activity) context).requestPermissions(PERMISSIONS_CALL_PHONE, REQUEST_CALL_PHONE);
            } else {
                ((Activity) context).requestPermissions(PERMISSIONS_CALL_PHONE, REQUEST_CALL_PHONE);
            }
            return false;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static boolean isPhoneAndStoragePermission(Context context) {

        if (PermissionUtils.hasSelfPermission((Activity) context, PERMISSIONS_PHONE_Store)) {
            return true;
        } else {
            if (((Activity) context).shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)) {
                ((Activity) context).requestPermissions(PERMISSIONS_PHONE_Store, REQUEST_STORAGE_PHONE);
            } else {
                ((Activity) context).requestPermissions(PERMISSIONS_PHONE_Store, REQUEST_STORAGE_PHONE);
            }
            return false;
        }


    }

    @TargetApi(Build.VERSION_CODES.M)
    public static boolean isAllRequiredPermissions(Context context) {
        if (PermissionUtils.hasSelfPermission((Activity) context, PERMISSIONS_ALL_REQUIRED)) {
            return true;
        } else {
            if (((Activity) context).shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
                    && ((Activity) context).shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ((Activity) context).requestPermissions(PERMISSIONS_ALL_REQUIRED, REQUEST_ALL);
            } else {
                ((Activity) context).requestPermissions(PERMISSIONS_ALL_REQUIRED, REQUEST_ALL);
            }
            return false;
        }
    }

    public static boolean isPhoneAndNetworkPermission(Context context) {
        if (PermissionUtils.isMNC()) {
            if (PermissionUtils.hasSelfPermission((Activity) context, PERMISSIONS_PHONE_AND_NETWORK)) {
                return true;
            } else {
                if (((Activity) context).shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)
                        && ((Activity) context).shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
                        && ((Activity) context).shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    ((Activity) context).requestPermissions(PERMISSIONS_PHONE_AND_NETWORK, REQUEST_PHONE_NETWORK);
                } else {
                    ((Activity) context).requestPermissions(PERMISSIONS_PHONE_AND_NETWORK, REQUEST_PHONE_NETWORK);
                }
                return false;
            }
        } else {
            return true;
        }
    }
}
