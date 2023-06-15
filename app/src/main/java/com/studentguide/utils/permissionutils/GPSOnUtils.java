package com.studentguide.utils.permissionutils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.studentguide.ParentObj;
import com.studentguide.utils.Logger;
import com.studentguide.utils.MyPref;


public class GPSOnUtils implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener // NOTE: This location listener is from Play service
{
    public final static int REQUEST_GPS_ON_SETTINGS = 101;

    public static final int
            GPS_NORMAL_INTERVAL = 3000,
            GPS_FASTEST_INTERVAL = 2000;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest locationRequest;
    // private LocationClient mLocationClient;
    private LocationListener locListener;
    private FusedLocationProviderApi fusedLocationProviderApi;
    private LocationSettingsRequest.Builder builder;
    private PendingResult<LocationSettingsResult> result;
    private LocationManager locManager;
    private LocationRequest mLocationRequest;
    private boolean gps_enabled = false;
    private boolean network_enabled = false;

    private Activity activity;
    private LocationChangeListener locationChangeListener;


    private GoogleApiClient client;

    public GPSOnUtils(Activity activity, LocationChangeListener locationChangeListener) {
        this.activity = activity;
        this.locationChangeListener = locationChangeListener;
    }

    public void initializeLocation() {
        locManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        locationRequest = LocationRequest.create();

        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(GPS_NORMAL_INTERVAL);
        locationRequest.setFastestInterval(GPS_FASTEST_INTERVAL);

        builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        fusedLocationProviderApi = LocationServices.FusedLocationApi;

        mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(AppIndex.API).build();

        mGoogleApiClient.connect();
    }

    public void setRequest() {
        result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                //final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // SUCCESS
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // RESOLUTION_REQUIRED

                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult()
                        try {
                            status.startResolutionForResult(activity, REQUEST_GPS_ON_SETTINGS);
                        } catch (IntentSender.SendIntentException ignore) {
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // SETTINGS_CHANGE_UNAVAILABLE
                        break;
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onConnected(Bundle bundle) {
        Logger.e("==TAG=="+ "=mLastLocation==onConnected==");
        if (mGoogleApiClient.isConnected()) {
            fusedLocationProviderApi.requestLocationUpdates(mGoogleApiClient, locationRequest, GPSOnUtils.this);
        }

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Logger.e("==TAG=="+ "=CheckLocation====" + mLastLocation);

            return;
        }

        mLastLocation = fusedLocationProviderApi.getLastLocation(mGoogleApiClient);
        Logger.e("==TAG=="+ "=mLastLocation====" + mLastLocation);
    }

    public void stopUsingGPS() {
        if (fusedLocationProviderApi != null) {
            if (mGoogleApiClient.isConnected()) {
                fusedLocationProviderApi.removeLocationUpdates(mGoogleApiClient, GPSOnUtils.this);
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        locationChangeListener.getLocationChange(location);

        MyPref myPref = new MyPref(activity);

        myPref.setData(MyPref.Keys.Lat, String.valueOf((float) location.getLatitude()));
        myPref.setData(MyPref.Keys.Lng, String.valueOf((float) location.getLatitude()));

       /* AppClass.currLatitude = location.getLatitude();   // STORE HERE <<<===
        AppClass.currLongitude = location.getLongitude(); // STORE HERE <<<===*/
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public interface LocationChangeListener {
        void getLocationChange(Location location);
    }
}