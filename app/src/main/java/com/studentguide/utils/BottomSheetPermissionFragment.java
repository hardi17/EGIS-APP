package com.studentguide.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.studentguide.R;



@SuppressWarnings("validFragment")
public class BottomSheetPermissionFragment extends BottomSheetDialogFragment {
    public static final String READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    public static final String WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static final String ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    public static final String CAMERA = Manifest.permission.CAMERA;
    public static final String RECORD_AUDIO = Manifest.permission.RECORD_AUDIO;
    public static final String READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE;

    private int PERMISSION_REQUEST_CODE = 1;
    private AppCompatActivity appCompatActivity;
    private String[] requiredPermission;
    private OnPermissionResult onPermissionResult;


    public BottomSheetPermissionFragment(AppCompatActivity appCompatActivity, OnPermissionResult onPermissionResult, String... requiredPermission) {
        this.appCompatActivity = appCompatActivity;
        this.onPermissionResult = onPermissionResult;
        this.requiredPermission = requiredPermission;

    }


    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = new LinearLayout(appCompatActivity);
        dialog.setContentView(contentView);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean checkPermission() {
        if (hasSelfPermission((Activity) appCompatActivity, requiredPermission)) {
            dismissAllowingStateLoss();
            onPermissionResult.onPermissionAllowed();
            return true;
        } else {
            requestPermissions(requiredPermission, PERMISSION_REQUEST_CODE);
            return false;
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }


    @TargetApi(Build.VERSION_CODES.M)
    public static boolean hasSelfPermission(Activity activity, String[] permissions) {
        // Below Android M all permissions are granted at install time and are already available.
        if (!isMNC()) {
            return true;
        }

        // Verify that all required permissions have been granted
        for (String permission : permissions) {
            if (activity.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static boolean isMNC() {
        /*
         the codename, not the version code. Once the API has been finalised, the following check
         should be used: */
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
        //return "MNC".equals(Build.VERSION.CODENAME);
    }

    public static void openSettingsDialog(final Context context) {
        AlertDialog.Builder ald = new AlertDialog.Builder(context);
        ald.setMessage(context.getResources().getString(R.string.allow_permission_settings));
        ald.setPositiveButton(context.getResources().getString(R.string.settings), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                context.startActivity(intent);
            }
        });
        ald.setNegativeButton(context.getResources().getString(R.string.cancel), null);
        ald.show();
    }

    public interface OnPermissionResult {
        void onPermissionAllowed();

        void onPermissionDenied();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (requiredPermission != null && requiredPermission.length > 0) {
            checkPermission();
        } else {
            dismissAllowingStateLoss();
            onPermissionResult.onPermissionAllowed();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean hadAllowed = true;
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_DENIED) {
                    hadAllowed = false;
                    break;
                }
            }

            if (hadAllowed) {
                dismissAllowingStateLoss();
                onPermissionResult.onPermissionAllowed();
            } else {
                dismissAllowingStateLoss();
                onPermissionResult.onPermissionDenied();
            }
        }

    }
}
