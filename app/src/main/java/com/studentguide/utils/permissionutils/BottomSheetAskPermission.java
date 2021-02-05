package com.studentguide.utils.permissionutils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.studentguide.R;


/**
 * Created by Bhavesh on 15-11-2017.
 */
@SuppressLint("ValidFragment")
public class BottomSheetAskPermission extends BottomSheetDialogFragment {
    private View rootView;
    private BottomSheetBehavior mBottomSheetBehavior;

    private AppCompatActivity context;
    private String[] permissionsList;
    private PermissionResultListener listener;

    private final int REQUEST_PERMISSION_ALL = 10;

    /**
     * By Bhavesh<br/>
     * <p/>
     * Permission List
     * <p>
     * Make sure you are declared permission in menifest.xml also
     */

    // Storage
    public static final String WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static final String READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;

    // Camera
    public static final String CAMERA = Manifest.permission.CAMERA;

    // Location
    public static final String ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

    // Phone State
    public static final String READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE;

    // Calendar
    public static final String READ_CALENDAR = Manifest.permission.READ_CALENDAR;
    public static final String WRITE_CALENDAR = Manifest.permission.WRITE_CALENDAR;

    // Get Account
    public static final String GET_ACCOUNTS = Manifest.permission.GET_ACCOUNTS;

    // Call phone
    public static final String CALL_PHONE = Manifest.permission.CALL_PHONE;

    /**
     * By Bhavesh<br/>
     * <p/>
     * Give permissions Like this : BottomSheetAskPermission.ACCESS_FINE_LOCATION
     *
     * @param context         Must be AppCompatActivity
     * @param listener        If any of the permission is Deny then it will call onPermissionDeny();
     * @param permissionsList List of Permissions to ask
     */
    @SuppressLint("ValidFragment")
    public BottomSheetAskPermission(AppCompatActivity context, PermissionResultListener listener, String... permissionsList) {
        this.context = context;
        this.listener = listener;
        this.permissionsList = permissionsList;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        rootView = createLayoutWithoutXml();
        dialog.setContentView(rootView);

        try {
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            // Out side touch = false (By this resolve issue of Multiple time click to dismiss bottom sheet)
            dialog.getWindow().findViewById(R.id.touch_outside).setClickable(false);
            dialog.getWindow().findViewById(R.id.touch_outside).setFocusable(false);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        setBottomSheetLayout();
    }

    private View createLayoutWithoutXml() {
        LinearLayout layout = new LinearLayout(context);
        // Define the LinearLayout's characteristics
        layout.setGravity(Gravity.CENTER);
        layout.setOrientation(LinearLayout.VERTICAL);

        // Set generic layout parameters
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(params);

        return layout;
    }

    private void setBottomSheetLayout() {
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) rootView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            mBottomSheetBehavior = (BottomSheetBehavior) behavior;
            mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    switch (newState) {
                        case BottomSheetBehavior.STATE_COLLAPSED:
                            break;
                        case BottomSheetBehavior.STATE_SETTLING:
                            break;
                        case BottomSheetBehavior.STATE_EXPANDED:
                            break;
                        case BottomSheetBehavior.STATE_HIDDEN:
                            dismiss();
                            break;
                        case BottomSheetBehavior.STATE_DRAGGING:
                            break;
                        default:
                            break;
                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                    // Log.d("BSB", "sliding " + slideOffset);
                }
            });

            // Used for Show full layout (Height need to measure)
            rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    removeOnGlobalLayoutListener(rootView, this);
                    int height = rootView.getMeasuredHeight();
                    mBottomSheetBehavior.setPeekHeight(height);
                }
            });
        }
    }

    private void finishBottomSheet() {
        dismissAllowingStateLoss();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void removeOnGlobalLayoutListener(View v, ViewTreeObserver.OnGlobalLayoutListener listener) {
        if (Build.VERSION.SDK_INT < 16) {
            v.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        } else {
            v.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (permissionsList != null && permissionsList.length > 0) {
            checkPermission();
        } else {
            finishBottomSheet(); // Permission List is Empty
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void checkPermission() {
        if (hasSelfPermission(context, permissionsList)) {
            listener.onAllPermissionAllow();
            finishBottomSheet(); // Already permission granted
        } else {
            requestPermissions(permissionsList, REQUEST_PERMISSION_ALL);
        }
    }

    /**
     * Returns true if the Activity has access to all given permissions.
     * Always returns true on platforms below M.
     *
     * @see Activity#checkSelfPermission(String)
     */
    @TargetApi(Build.VERSION_CODES.M)
    public static boolean hasSelfPermission(Activity activity, String[] permissions) {
        // Below Android M all permissions are granted at install time and are already available.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_ALL) {
            boolean allPermissionsGranted = true;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (allPermissionsGranted) {
                // Logger.i(TAG + "permission was granted.");
                listener.onAllPermissionAllow();
                finishBottomSheet(); // All permission granted
            } else {
                // Logger.i(TAG + "permission was NOT granted.");
                // openSettingsDialog();
                listener.onPermissionDeny();
                finishBottomSheet(); // Permission denied
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public interface PermissionResultListener {
        void onAllPermissionAllow();

        void onPermissionDeny();
    }
}