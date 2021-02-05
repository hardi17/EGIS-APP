package com.studentguide.utils.permissionutils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.studentguide.R;


/**
 * Created by bhavesh on 28-2-2018.
 * Prismetric Technology, Gandhinagar, Gujarat
 */
@SuppressLint("ValidFragment")
public class BottomSheetGpsStartUtils extends BottomSheetDialogFragment {
    public final static int REQUEST_GPS_ON_SETTINGS = 101;

    private View rootView;
    private BottomSheetBehavior mBottomSheetBehavior;

    private AppCompatActivity activity;
    private boolean isCompulsoryStartGPS = false;
    private GPSAllowListener listener;

    final int
            GPS_NORMAL_INTERVAL = 3000,
            GPS_FASTEST_INTERVAL = 2000;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest locationRequest;
    private LocationSettingsRequest.Builder builder;

    private boolean checkPermissionOneTime = true;


    /**
     * By Bhavesh<br/>
     * <p/>
     * Permission List
     * <p>
     * Make sure you are declared permission in menifest.xml also
     */

    /**
     * By Bhavesh<br/>
     * <p/>
     *
     * @param activity Must be AppCompatActivity
     */
    @SuppressLint("ValidFragment")
    public BottomSheetGpsStartUtils(AppCompatActivity activity, boolean isCompulsoryStartGPS, GPSAllowListener listener) {
        this.activity = activity;
        this.isCompulsoryStartGPS = isCompulsoryStartGPS;
        this.listener = listener;
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
        LinearLayout layout = new LinearLayout(activity);
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

        if (checkPermissionOneTime) {
            checkPermissionOneTime = false;
            checkPermission();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void checkPermission() {
        new BottomSheetAskPermission(activity,
                new BottomSheetAskPermission.PermissionResultListener() {
                    @Override
                    public void onAllPermissionAllow() {
                        initializeLocationData();
                        showDialogForGPS(); // Ask for first time
                    }

                    @Override
                    public void onPermissionDeny() {
                        listener.onUserDenyPermission();
                        finishBottomSheet();
                    }
                },
                BottomSheetAskPermission.ACCESS_FINE_LOCATION,
                BottomSheetAskPermission.ACCESS_COARSE_LOCATION
        ).show(activity.getSupportFragmentManager(), "");
    }

    private void initializeLocationData() {
        locationRequest = LocationRequest.create();

        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(GPS_NORMAL_INTERVAL);
        locationRequest.setFastestInterval(GPS_FASTEST_INTERVAL);

        builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .addApi(AppIndex.API).build();

        mGoogleApiClient.connect();
    }

    private void showDialogForGPS() {
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                //final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // SUCCESS
                        listener.onStartGPS();
                        finishBottomSheet();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // RESOLUTION_REQUIRED

                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult()
                        try {
                            startIntentSenderForResult(status.getResolution().getIntentSender(), REQUEST_GPS_ON_SETTINGS, null, 0, 0, 0, null);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // SETTINGS_CHANGE_UNAVAILABLE
                        listener.onUserDenyToStartGPS();
                        finishBottomSheet();
                        break;
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_GPS_ON_SETTINGS:
                    // GPS is ON
                    listener.onStartGPS();
                    finishBottomSheet();
                    break;
                default:
                    break;
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            switch (requestCode) {
                case REQUEST_GPS_ON_SETTINGS:
                    // GPS is OFF
                    if (isCompulsoryStartGPS) {
                        showDialogForGPS(); // Ask again when User deny
                    } else {
                        listener.onUserDenyToStartGPS();
                        finishBottomSheet();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void finishBottomSheet() {
        try {
            mGoogleApiClient.disconnect();
        } catch (Exception ignore) {
        }

        dismissAllowingStateLoss();
    }

    public interface GPSAllowListener {
        void onStartGPS();

        void onUserDenyPermission();

        void onUserDenyToStartGPS();
    }
}