package com.studentguide.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.studentguide.ParentObj;
import com.studentguide.R;
import com.studentguide.adapter.FamousAdapter;
import com.studentguide.databinding.ActivityNearbyplacesBinding;
import com.studentguide.dialogue.CustDialogLogout;
import com.studentguide.extra.PlaceJSONParser;
import com.studentguide.user.IntroActivity;
import com.studentguide.user.LoginActivity;
import com.studentguide.user.SignupActivity;
import com.studentguide.utils.Logger;
import com.studentguide.utils.ToastView;
import com.studentguide.utils.network.LocationAddressAsyncUtils;
import com.studentguide.utils.network.LocationGPS;
import com.studentguide.utils.permissionutils.PermissionUtils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class NearByPlaceActivity extends FragmentActivity implements LocationListener, OnMapReadyCallback, LocationGPS.LocationData {

    GoogleMap mGoogleMap;


    String[] mPlaceType = null;
    String[] mPlaceTypeName = null;

    double mLatitude = 0.0;
    double mLongitude = 0.0;

    private String currCountryCode = " ";
    private boolean location = false;

    private LocationGPS loc_gps;

    HashMap<String, String> mMarkerPlaceLink = new HashMap<String, String>();

    private SupportMapFragment mapFragment;

    private final int PERMISSION_RESULT_CODE = 3;
    private String[] PERMISSION_ACCESS_LOCATION = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};

    ActivityNearbyplacesBinding binding;

    private ActionBarDrawerToggle toggle;
    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_nearbyplaces);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference("users");
        userId = mAuth.getCurrentUser().getUid();


        SetNavigationDrawerWithStyle();
        setListOfOptionAdapter();
        loc_gps = new LocationGPS(this, this, this);

        // Getting Google Play availability status
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
        if (status != ConnectionResult.SUCCESS) { // Google Play Services are not available

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();
        } else {
            // Google Play Services are available
            getCurrentLocation();
        }

        getUserDetail();
    }

    private void getUserDetail() {
        mRef.child(userId).child("fullName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userName = snapshot.getValue(String.class);
                binding.rightMenuHome.tvMenuName.setText(userName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                ToastView toast = new ToastView(NearByPlaceActivity.this);
                toast.showToast(error.toString());
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly. [For keeping it secured from attack]
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(this, IntroActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_NEW_TASK));
            finish();
        }
    }

    @OnClick(R.id.ll_detail_user)
    public void goToProfile() {
        startActivity(new Intent(this, SignupActivity.class).putExtra("isEditProfile", true));
        binding.rightMenuHome.llProfile.setEnabled(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.rightMenuHome.llProfile.setEnabled(true);
            }
        }, 1000);
    }

    @SuppressLint("WrongConstant")
    private void SetNavigationDrawerWithStyle() {
        toggle = new ActionBarDrawerToggle(
                this,
                binding.drawelayoutHomeactivity,
                binding.toolbarHomeActivity,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close) {
            public void onDrawerClosdrawerLayout_homeActivityed(View view) {
                supportInvalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);

                float moveFactor = 0;
                moveFactor = (drawerView.getWidth() * slideOffset);

                binding.rlHomeActivity.setTranslationX(moveFactor);

            }
        };
        toggle.setDrawerIndicatorEnabled(false);
        binding.drawelayoutHomeactivity.addDrawerListener(toggle);
        toggle.syncState();
        binding.drawelayoutHomeactivity.setScrimColor(getResources().getColor(android.R.color.transparent));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding.drawelayoutHomeactivity.setElevation(0f);
        }

        binding.drawelayoutHomeactivity.setDrawerShadow(R.mipmap.ic_launcher, GravityCompat.END);
        binding.drawelayoutHomeactivity.setDrawerShadow(R.mipmap.ic_launcher, GravityCompat.START);
        binding.drawelayoutHomeactivity.setDrawerShadow(R.mipmap.ic_launcher, GravityCompat.RELATIVE_HORIZONTAL_GRAVITY_MASK);
    }


    @OnClick(R.id.img_left_Toolbar)
    public void menu() {
        binding.drawelayoutHomeactivity.openDrawer(GravityCompat.START);

    }

    @OnClick(R.id.tv_menu_nhsNearby)
    public void openNHSScreen() {
        binding.drawelayoutHomeactivity.closeDrawer(GravityCompat.START);
        startActivity(new Intent(this, NHSActivity.class));
        finish();

    }


    @OnClick(R.id.tv_menu_home)
    public void openHomeScreen() {
        binding.drawelayoutHomeactivity.closeDrawer(GravityCompat.START);

        startActivity(new Intent(this, NearByPlaceActivity.class));
        finish();
    }

    @OnClick(R.id.iv_close)
    public void closeMenu() {
        binding.drawelayoutHomeactivity.closeDrawer(GravityCompat.START);
    }


    @OnClick(R.id.tv_menu_travel)
    public void openTravelScreen() {
        startActivity(new Intent(this, TravelActivity.class));
        binding.rightMenuHome.tvMenuTravel.setEnabled(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.rightMenuHome.tvMenuTravel.setEnabled(true);
            }
        }, 1000);
    }

    @OnClick(R.id.tv_menu_traffic)
    public void openTrafficeScreen() {
        startActivity(new Intent(this, TrafficSignalsWasteGuideActivity.class)
                .putExtra("isTraffic", true));
        binding.rightMenuHome.tvMenuTraffic.setEnabled(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.rightMenuHome.tvMenuTraffic.setEnabled(true);
            }
        }, 1000);
    }

    @OnClick(R.id.tv_menu_bins)
    public void openWasteGuideScreen() {
        startActivity(new Intent(this, TrafficSignalsWasteGuideActivity.class)
                .putExtra("isWaste", true));
        binding.rightMenuHome.tvMenuBins.setEnabled(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.rightMenuHome.tvMenuBins.setEnabled(true);
            }
        }, 1000);
    }

    @OnClick(R.id.tv_menu_currency)
    public void openCurrencyGuideScreen() {
        startActivity(new Intent(this, CurrencyActivity.class));
        binding.rightMenuHome.tvMenuCurrency.setEnabled(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.rightMenuHome.tvMenuCurrency.setEnabled(true);
            }
        }, 1000);
    }


    @OnClick(R.id.tv_menu_logout)
    public void logout() {
        new CustDialogLogout(this, () -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_NEW_TASK));
        }).show();
    }

    @OnClick(R.id.btn_find)
    public void findLocationAccordingChoice() {
        binding.pgb.setVisibility(View.VISIBLE);

        int selectedPosition = binding.sprPlaceType.getSelectedItemPosition();
        String type = mPlaceType[selectedPosition];

        StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        sb.append("location=" + mLatitude + "," + mLongitude);
        sb.append("&radius=30000");
        sb.append("&types=" + type);
        sb.append("&sensor=true");
        sb.append("&key=" + getString(R.string.google_maps_key));

        binding.pgb.setVisibility(View.GONE);

        // Creating a new non-ui thread task to download Google place json data
        PlacesTask placesTask = new PlacesTask();

        // Invokes the "doInBackground()" method of the class PlaceTask
        placesTask.execute(sb.toString());
    }

    private void setListOfOptionAdapter() {
        // Array of place types
        mPlaceType = getResources().getStringArray(R.array.place_type);

        // Array of place type names
        mPlaceTypeName = getResources().getStringArray(R.array.place_type_name);

        // Creating an array adapter with an array of Place types
        // to populate the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, mPlaceTypeName);

        // Setting adapter on Spinner to set place types
        binding.sprPlaceType.setAdapter(adapter);
    }

    //GET CURRENT LOCATION
    private void getCurrentLocation() {
        if (isLocationPermission()) {
            if (ParentObj.getInstance().networkConnectivity.isNetworkAvailable()) {
                location = true;
                loc_gps.get_Initlocation();
                loc_gps.set_request();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean isLocationPermission() {
        if (PermissionUtils.hasSelfPermission(this, PERMISSION_ACCESS_LOCATION)) {
            return true;
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
                    && shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                requestPermissions(PERMISSION_ACCESS_LOCATION, PERMISSION_RESULT_CODE);
            } else {
                requestPermissions(PERMISSION_ACCESS_LOCATION, PERMISSION_RESULT_CODE);
            }
            return false;
        }
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Logger.d("Exception while downloading url" + e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    @Override
    public void onLocationChanged(Location location) {
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
        LatLng latLng = new LatLng(mLatitude, mLongitude);

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(12));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    @Override
    public void GetLocation(double lat, double longitude) {
        if (location) {

            location = false;
            Logger.e("HERE" + "Get Location");
            Logger.d("LATITUDE" + "LATITUDE " + lat);
            Logger.d("LONGITUDE" + "LONGITUDE " + longitude);

            if (lat != 0.0 && longitude != 0.0) {
                mLatitude = lat;
                mLongitude = longitude;
            }

            new SetMap().execute();
        }
    }

    /*set map view*/
    private class SetMap extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            initilizeMap();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (mLatitude != 0.0 && mLongitude != 0.0) {
                IniLizeMap(mLatitude, mLongitude);
            }
        }
    }


    private void initilizeMap() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fl_GoogleMapActivity_map);
//        mapFragment.getMapAsync(this);

        //mSupportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fl_GoogleMapActivity_map);

        if (mapFragment == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mapFragment = SupportMapFragment.newInstance();
            //  fragmentTransaction.replace(R.id.fl_GoogleMapActivity_map, mapFragment).commit();
            fragmentTransaction.replace(R.id.fl_GoogleMapActivity_map, mapFragment).commitAllowingStateLoss();
        }
    }

    public void IniLizeMap(final double map_lat, final double map_lng) {
        try {
            mapFragment.getMapAsync(googleMap -> {
                mGoogleMap = googleMap;
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


                // Showing / hiding your current location

                if (ActivityCompat.checkSelfPermission(NearByPlaceActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(NearByPlaceActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                googleMap.clear();
                googleMap.setMyLocationEnabled(false);
                // Enable / disable navigation tool
                googleMap.getUiSettings().setMapToolbarEnabled(false);
                // Enable / Disable zooming controls
                googleMap.getUiSettings().setZoomControlsEnabled(false);
                // Enable / Disable my location button
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                // Enable / Disable Compass icon
                googleMap.getUiSettings().setCompassEnabled(false);
                // Enable / Disable Rotate gesture
                googleMap.getUiSettings().setRotateGesturesEnabled(true);

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(map_lat, map_lng))
                        .zoom(13)
                        .build();

                googleMap.moveCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition));


                // Enable / Disable zooming functionality
                googleMap.getUiSettings().setZoomGesturesEnabled(true);

                new LocationAddressAsyncUtils(NearByPlaceActivity.this, map_lat, map_lng,
                        (shortAddress, longAddress, city, countryName, countryCode) -> currCountryCode = countryCode).execute();

                Logger.d("map:==" + map_lat + "===" + map_lng);
                mLatitude = map_lat;
                mLongitude = map_lng;

                googleMap.addMarker(new MarkerOptions().position(new LatLng(map_lat, map_lng)))
                        .setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

                binding.pgb.setVisibility(View.GONE);

                mGoogleMap.setOnInfoWindowClickListener(arg0 -> {
                    Intent intent = new Intent(getBaseContext(), PlaceDetailsActivity.class);
                    String reference = mMarkerPlaceLink.get(arg0.getId());
                    intent.putExtra("reference", reference);

                    // Starting the Place Details Activity
                    startActivity(intent);
                });


            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * A class, to download Google Places
     */
    private class PlacesTask extends AsyncTask<String, Integer, String> {

        String data = null;

        // Invoked by execute() method of this object
        @Override
        protected String doInBackground(String... url) {
            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {

                if (mLatitude != 0.0 && mLongitude != 0.0) {
                    IniLizeMap(mLatitude, mLongitude);
                }
                ParentObj.toastView.showToast("Something went wrong");
                Logger.d("Background Task" + e.toString());
            }
            return data;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(String result) {
            ParserTask parserTask = new ParserTask();

            // Start parsing the Google places in JSON format
            // Invokes the "doInBackground()" method of the class ParseTask
            parserTask.execute(result);
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

        JSONObject jObject;

        // Invoked by execute() method of this object
        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> places = null;
            PlaceJSONParser placeJsonParser = new PlaceJSONParser();

            try {
                jObject = new JSONObject(jsonData[0]);

                /** Getting the parsed data as a List construct */
                places = placeJsonParser.parse(jObject);

            } catch (Exception e) {
                Logger.d("Exception" + e.toString());
                ParentObj.toastView.showToast("Something went wrong");
            }
            return places;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(List<HashMap<String, String>> list) {

            // Clears all the existing markers
            mGoogleMap.clear();
            binding.pgb.setVisibility(View.GONE);
            if (list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {

                    // Creating a marker
                    MarkerOptions markerOptions = new MarkerOptions();

                    // Getting a place from the places list
                    HashMap<String, String> hmPlace = list.get(i);

                    // Getting latitude of the place
                    double lat = Double.parseDouble(hmPlace.get("lat"));

                    // Getting longitude of the place
                    double lng = Double.parseDouble(hmPlace.get("lng"));

                    // Getting name
                    String name = hmPlace.get("place_name");

                    // Getting vicinity
                    String vicinity = hmPlace.get("vicinity");

                    LatLng latLng = new LatLng(lat, lng);

                    // Setting the position for the marker
                    markerOptions.position(latLng);

                    // Setting the title for the marker.
                    //This will be displayed on taping the marker
                    markerOptions.title(name + " : " + vicinity);

                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

                    // Placing a marker on the touched position
                    Marker m = mGoogleMap.addMarker(markerOptions);

                    // Linking Marker id and place reference
                    mMarkerPlaceLink.put(m.getId(), hmPlace.get("reference"));
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(13));
                }
            } else {
                if (mLatitude != 0.0 && mLongitude != 0.0) {
                    IniLizeMap(mLatitude, mLongitude);
                }
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_RESULT_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    setListOfOptionAdapter();
                    getCurrentLocation();

                } else {
                    // permission denied,
                    showAlertDialogToGetPermission();
                }
                break;
            default:
                break;
        }
    }

    //SHOW DIALOG FOR NEED TO GET PERMISSION
    private void showAlertDialogToGetPermission() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(NearByPlaceActivity.this);
//        builder.setTitle("Hey " + AppClass.preferences.getUserName() + "...");
        builder.setMessage("You have forcefully denied location permission " +
                "for this action. Please open settings, go to permissions and allow them.");
        builder.setCancelable(false);
        builder.setPositiveButton("SETTING", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + NearByPlaceActivity.this.getPackageName()));
                NearByPlaceActivity.this.startActivity(intent);


            }
        });
        builder.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 110:
                if (resultCode == RESULT_OK) {

                } else {
                    showAlertDialogToTurnOnLocation();
                }
                break;

        }
    }

    //SHOW DIALOG FOR TO TURN ON GPS LOCATION
    private void showAlertDialogToTurnOnLocation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(NearByPlaceActivity.this);
        builder.setMessage("We can not move forward without turning on this service ");
        builder.setCancelable(false);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getCurrentLocation();
            }
        });
        builder.show();

    }

}