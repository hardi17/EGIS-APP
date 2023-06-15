package com.studentguide.utils.network;

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
import com.studentguide.utils.Logger;


public class LocationGPS implements  GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener
{
    Context ctx;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    private LocationManager locManager;
    private LocationRequest mLocationRequest;
    private boolean gps_enabled = false;
    private boolean network_enabled = false;
    LocationRequest locationRequest;
    // private LocationClient mLocationClient;
    LocationListener locListener;
    FusedLocationProviderApi fusedLocationProviderApi;
    LocationSettingsRequest.Builder builder;

    private GoogleApiClient client;
    Activity activity;
    PendingResult<LocationSettingsResult> result;
    LocationData locationData;
    public static  final int REQUEST_CHECK_SETTINGS=110;
    private String TAG= "Locaion_gps";


    public LocationGPS(Context context, Activity activity, LocationData locationData)
    {
        this.ctx = context;
        this.activity = activity;
        this.locationData = locationData;
    }

    public void get_Initlocation()
    {

        locManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);


        locationRequest = LocationRequest.create();


        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(4000);
        locationRequest.setFastestInterval(3000);

        builder= new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        fusedLocationProviderApi = LocationServices.FusedLocationApi;

        mGoogleApiClient = new GoogleApiClient.Builder(ctx)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(AppIndex.API).build();

        mGoogleApiClient.connect();


    }

    public  void set_request()
    {
        result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                //final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
//                        Toast.makeText(ctx, "SUCCESS", Toast.LENGTH_SHORT).show();

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
//                        Toast.makeText(ctx, "RESOLUTION_REQUIRED", Toast.LENGTH_SHORT).show();

                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().ctx
                            status.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Logger.d("==TAG=="  + "=SendIntentException==" + e);
                            // Ignore the error.
                        }

                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
//                        Toast.makeText(ctx, "SETTINGS_CHANGE_UNAVAILABLE", Toast.LENGTH_SHORT).show();

                        break;
                }
            }
        });

    }



    @SuppressLint("MissingPermission")
    @Override
    public void onConnected(Bundle bundle)
    {
        Logger.e(TAG+ "=mLastLocation==onConnected==");

        fusedLocationProviderApi.requestLocationUpdates(mGoogleApiClient,  locationRequest, LocationGPS.this);


        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Logger.e(TAG+ "=CheckLocation====" + mLastLocation);

            return;
        }

        mLastLocation =fusedLocationProviderApi.getLastLocation(mGoogleApiClient);
        Logger.e(TAG+ "=mLastLocation====" + mLastLocation);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location)
    {
//        Toast.makeText(ctx, ""+location.getLatitude()+"==Longitude=="+location.getLongitude(), Toast.LENGTH_SHORT).show();
        locationData.GetLocation(location.getLatitude(),location.getLongitude());
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public interface LocationData{
        public void GetLocation(double lat, double longitude);
    }
}
