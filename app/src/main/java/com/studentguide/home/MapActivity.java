package com.studentguide.home;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.studentguide.ParentObj;
import com.studentguide.R;
import com.studentguide.databinding.ActivityMapBinding;
import com.studentguide.utils.KeyBoardUtils;
import com.studentguide.utils.Logger;
import com.studentguide.utils.StringUtils;
import com.studentguide.utils.map.InfoWindowCustom;
import com.studentguide.utils.map.MapDirections;
import com.studentguide.utils.map.StaticData;
import com.studentguide.utils.network.LocationAddressAsyncUtils;
import com.studentguide.utils.network.LocationGPS;
import com.studentguide.utils.permissionutils.PermissionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapActivity extends FragmentActivity implements
        OnMapReadyCallback,
        LocationGPS.LocationData,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    ActivityMapBinding binding;

    private double lattitude = 0.0,
            longitude = 0.0,
            startLat = 0.0,
            startLng = 0.0,
            endLat = 0.0,
            endLng = 0.0;

    private GetAddressAsync getAddressAsync;
    private PlacesClient placesClient;
    private AutoCompleteAdapter adapter;

    private LocationGPS loc_gps;
    private LatLng latLng;

    private GoogleMap google_Map;

    private boolean isFromPermissionScreen = false,
            location = false,
            isFrom = false,
            isTo = false,
            isPath = false;
    private String selectedCity = " ",
            country_code = " ",
            selectedAddress = " ",
            placeDescription = " ",
            currCountryCode = " ",
            address = "",
            city = "",
            knownName = "",
            country = "",
            fromPlaceId = "",
            toPlaceId = "",
            mode = "",
            duration = "",
            distance = "",
            placeId = "",
            startToAnd = "";

    //Location
    private final int PERMISSION_RESULT_CODE = 3;
    private String[] PERMISSION_ACCESS_LOCATION = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};

    private FragmentManager fragmentManager;

    // private SupportMapFragment mapFragment;
    private SupportMapFragment mapFragment;
    private boolean isMoveMap = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_map);
        ButterKnife.bind(this);

        getIntentData();
        initView();
        fragmentManager = getSupportFragmentManager();
        setEditorAction();
        loc_gps = new LocationGPS(this, this, this);
        getLocationBeforeMapLoading();
    }

    private void getIntentData() {
        isFrom = getIntent().getBooleanExtra("isFrom", false);
        isTo = getIntent().getBooleanExtra("isTo", false);
        isPath = getIntent().getBooleanExtra("isPath", false);
        lattitude = getIntent().getDoubleExtra("lattitude", 0.0);
        longitude = getIntent().getDoubleExtra("longitude", 0.0);
        startLat = getIntent().getDoubleExtra("startLat", startLat);
        startLng = getIntent().getDoubleExtra("startLng", startLng);
        endLat = getIntent().getDoubleExtra("endLat", endLat);
        endLng = getIntent().getDoubleExtra("endLng", endLng);
        mode = getIntent().getStringExtra("mode");
        duration = getIntent().getStringExtra("duration");
        distance = getIntent().getStringExtra("distance");
        startToAnd = getIntent().getStringExtra("startToAnd");
    }

    private void initView() {
        binding.toolbar.tvTitle.setText(R.string._Map);

        if (isPath) {
            binding.rlMapActivitySearchView.setVisibility(View.GONE);
            binding.imgGoogleMapActivityMarker.setVisibility(View.GONE);
            binding.toolbar.txtDone.setVisibility(View.GONE);
            binding.rlDetailPath.setVisibility(View.VISIBLE);
            binding.tvLocationName.setText(startToAnd);
            binding.tvDistnaceValue.setText(distance);
            binding.tvTimeValue.setText(duration);
            binding.tvByValue.setText(mode);
        } else {
            binding.imgGoogleMapActivityMarker.setVisibility(View.VISIBLE);
            binding.toolbar.txtDone.setVisibility(View.VISIBLE);
            binding.rlMapActivitySearchView.setVisibility(View.VISIBLE);
            binding.actvGoogleMapActivitySearch.setSelection(binding.actvGoogleMapActivitySearch.getText().length());
            binding.rlDetailPath.setVisibility(View.GONE);
            clearData();
        }
    }

    private void clearData() {

        binding.actvGoogleMapActivitySearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    binding.imgMapActivityClearData.setVisibility(View.VISIBLE);
                    binding.imgMapActivityClearData.setOnClickListener(v -> {
                        binding.actvGoogleMapActivitySearch.setText("");
                        binding.actvGoogleMapActivitySearch.setHint("");
                    });
                } else {
                    binding.imgMapActivityClearData.setVisibility(View.GONE);
                }
            }
        });
    }

    @OnClick(R.id.ivBack)
    public void goToBack() {
        onBackPressed();
    }

    @OnClick(R.id.rl_navigate_map)
    public void goToMap() {
        if (startLat != 0.0 && startLng != 0.0
                && endLat != 0.0 && endLng != 0.0) {
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?saddr=" + startLat + "," + startLng + "&daddr=" + endLat + "," + endLng));
            startActivity(intent);
        }
    }

    @OnClick(R.id.txt_done)
    public void getLatLng() {
        KeyBoardUtils.closeSoftKeyboard(MapActivity.this);
        if (StringUtils.isNotEmpty(selectedAddress)
                && lattitude != 0.0
                && longitude != 0.0) {

            Intent intent = new Intent();
            intent.putExtra("latitude", lattitude);
            intent.putExtra("longitude", longitude);
            intent.putExtra("address", selectedAddress);
            intent.putExtra("city", selectedCity);
            intent.putExtra("country_code", country_code);
            intent.putExtra("isTo", isTo);
            intent.putExtra("isFrom", isFrom);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @SuppressLint("NewApi")
    private void setEditorAction() {
        binding.actvGoogleMapActivitySearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    return true;
                }

                return false;
            }
        });
    }

    private void getLocationBeforeMapLoading() {
        if (isLocationPermission()) {
            if (isPath) {
                getCurrentLocation();
            } else {
                setAutoCompleteTextView();
                getCurrentLocation();

                /*Get location before, if delay map loading*/
                Logger.d("lattitude" + "lattitude===" + lattitude);
                Logger.d("longitude" + "longitude===" + longitude);

                if (lattitude != 0.0 && longitude != 0.0) {
                    new SetMap().execute();
                }
            }
        }
    }

    /*
     * set Autocomplete view
     * */
    private void setAutoCompleteTextView() {
        adapter = new AutoCompleteAdapter(MapActivity.this);
        binding.actvGoogleMapActivitySearch.setAdapter(adapter);

        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        placesClient = Places.createClient(MapActivity.this);


        binding.actvGoogleMapActivitySearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AutoCompletePlace place = (AutoCompletePlace) adapterView.getItemAtPosition(i);
                binding.actvGoogleMapActivitySearch.setText(place.getDescription());
                placeDescription = place.getDescription();
                binding.actvGoogleMapActivitySearch.setSelection(binding.actvGoogleMapActivitySearch.getText().toString().length());
                binding.actvGoogleMapActivitySearch.dismissDropDown();

                String placeId = place.getId();

                // Specify the fields to return.
                List<Place.Field> placeFields = Arrays.asList(Place.Field.ID
                        , Place.Field.NAME
                        , Place.Field.LAT_LNG);

                FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields)
                        .build();

                placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                    @Override
                    public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                        Place place = fetchPlaceResponse.getPlace();
                        Logger.d("Place found: " + place.getName());

                        if (place.getLatLng() != null) {

                            LatLng placeLatLng = place.getLatLng();

                            lattitude = placeLatLng.latitude;
                            longitude = placeLatLng.longitude;

                            if (google_Map != null) {
                                google_Map.clear();

                                CameraPosition cameraPosition = new CameraPosition.Builder()
                                        .target(placeLatLng)
                                        .zoom(15)
                                        .build();

                                google_Map.moveCamera(CameraUpdateFactory
                                        .newCameraPosition(cameraPosition));

                                google_Map.addMarker(new MarkerOptions().position(new LatLng(lattitude, longitude)))
                                        .setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));


                                //  placeId = place1.getId();
                        /*google_Map.animateCamera(CameraUpdateFactory.newLatLngZoom
                                (placeLatLng, google_Map.getCameraPosition().zoom));*/
                            }
                            adapter.clear();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        if (exception instanceof ApiException) {
                            ApiException apiException = (ApiException) exception;
                            int statusCode = apiException.getStatusCode();
                            // Handle error with given status code.
                            Log.e("TAG", "Place not found: " + exception.getMessage());
                        }
                    }
                });
            }
        });


    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    @Override
    public void GetLocation(double lat, double lng) {
        if (location) {

            location = false;
            Logger.e("HERE" + "Get Location");
            Logger.d("LATITUDE" + "LATITUDE " + lat);
            Logger.d("LONGITUDE" + "LONGITUDE " + lng);

            if (lattitude != 0.0 && longitude != 0.0) {
                lattitude = lattitude;
                longitude = longitude;
            } else {
                lattitude = lat;
                longitude = lng;
            }

            new SetMap().execute();
        }
    }


    private void getMapDirectionFromPolyLine(final GoogleMap googleMap,
                                             final double sLat,
                                             final double sLong,
                                             final double dLat,
                                             final double dLong,
                                             int colorName,
                                             final boolean isMapClear,
                                             String mode) {
        new MapDirections(
                new LatLng(sLat, sLong),
                new LatLng(dLat, dLong),
                MapActivity.this,
                colorName,
                mode,
                polylineOptions -> {
                    if (polylineOptions != null) {
                        if (isMapClear) {
                            googleMap.clear();
                        }

                        /*for source*/
                        setPathDetail(googleMap, sLat, sLong, true);
                        /*for destination*/
                        setPathDetail(googleMap, dLat, dLong, false);


                        googleMap.addPolyline(polylineOptions);
                    }
                }
        );

        googleMap.setOnMarkerClickListener(marker -> {
            ArrayList<String> markerPlace = new ArrayList<>();
            markerPlace.add(marker.getTitle());
            markerPlace.add(marker.getSnippet());
            googleMap.setInfoWindowAdapter(new InfoWindowCustom(MapActivity.this, markerPlace));

            return false;
        });
    }

    private void setPathDetail(GoogleMap googleMap, double sLat, double sLong, boolean isSnippet) {
        getAddress(sLat, sLong);
        String sAdd = "";
        if (StringUtils.isNotEmpty(city)) {
            sAdd = city;
        } else {
            sAdd = address;
        }

        MarkerOptions markerOptions = new MarkerOptions();
        if (isSnippet) {
            markerOptions.position(new LatLng(sLat, sLong))
                    .title(sAdd)
                    .snippet(StaticData.duration)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            googleMap.addMarker(markerOptions);
        } else {
            markerOptions.position(new LatLng(sLat, sLong))
                    .title(sAdd)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            googleMap.addMarker(markerOptions);
        }
    }

    private void getAddress(double v, double v1) {
        Geocoder gcd = new Geocoder(MapActivity.this.getBaseContext(), Locale.getDefault());
        List<Address> addresses;

        try {
            addresses = gcd.getFromLocation(v, v1, 1);

            address = addresses.get(0).getAddressLine(0);
            if (addresses.get(0).getFeatureName() != address) {
                knownName = addresses.get(0).getFeatureName();
            }
            city = addresses.get(0).getLocality();
            country = addresses.get(0).getCountryName();

            Logger.d("CITY NAME ====>" + city);
           /* state = addresses.get(0).getAdminArea();
            country = addresses.get(0).getCountryName();
            postalCode = addresses.get(0).getPostalCode();*/

        } catch (IOException e) {
            e.printStackTrace();
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

            if (lattitude != 0.0 && longitude != 0.0) {
                IniLizeMap(lattitude, longitude);
            }
        }
    }

    /*Intialize map in frmlayout*/
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
                google_Map = googleMap;
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


                // Showing / hiding your current location

                if (ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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


                // Enable / Disable zooming functionality
                googleMap.getUiSettings().setZoomGesturesEnabled(true);

                if (isPath) {
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(startLat, startLng))
                            .zoom(15)
                            .build();

                    googleMap.moveCamera(CameraUpdateFactory
                            .newCameraPosition(cameraPosition));

                    if (startLat != 0.0 && startLng != 0.0
                            && endLat != 0.0 && endLng != 0.0) {
                        getMapDirectionFromPolyLine(googleMap, map_lat, map_lng, startLat, startLng, R.color.color_C8102E, true, mode);
                        Logger.d(map_lat + " " + map_lng);
                        Logger.d(startLat + " " + startLng);
                        Logger.d(endLat + " " + endLng);
                        getMapDirectionFromPolyLine(googleMap, startLat, startLng, endLat, endLng, R.color.color_C8102E, true, mode);
                        Logger.d(map_lat + " " + map_lng);
                        Logger.d(startLat + " " + startLng);
                        Logger.d(endLat + " " + endLng);
                    }
                } else {

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(map_lat, map_lng))
                            .zoom(15)
                            .build();

                    googleMap.moveCamera(CameraUpdateFactory
                            .newCameraPosition(cameraPosition));

                    googleMap.addMarker(new MarkerOptions().position(new LatLng(map_lat, map_lng)))
                            .setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));


                    new LocationAddressAsyncUtils(MapActivity.this, map_lat, map_lng,
                            (shortAddress, longAddress, city, countryName, countryCode) -> currCountryCode = countryCode).execute();

                    googleMap.setOnCameraChangeListener(cameraPosition1 -> {
                        binding.actvGoogleMapActivitySearch.dismissDropDown();
                        latLng = cameraPosition1.target;

                        lattitude = latLng.latitude;
                        longitude = latLng.longitude;

                        try {
                            new LocationAddressAsyncUtils(MapActivity.this, map_lat, map_lng,
                                    (shortAddress, longAddress, city, countryName, countryCode) -> currCountryCode = countryCode).execute();


                            //For static search address in auto complete
                            if (!isMoveMap) {
                                isMoveMap = true;
                            } else {
                                selectedAddress = "";
                                isMoveMap = false;
                            }

                            if (getAddressAsync != null) {
                                if (getAddressAsync.getStatus() != AsyncTask.Status.FINISHED) {
                                    getAddressAsync.cancel(true);
                                }
                            }
                            getAddressAsync = new GetAddressAsync();
                            getAddressAsync.execute();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });

                    if (getAddressAsync != null) {
                        if (getAddressAsync.getStatus() != AsyncTask.Status.FINISHED) {
                            getAddressAsync.cancel(true);
                        }
                    }
                    getAddressAsync = new GetAddressAsync();
                    getAddressAsync.execute();
                }

                Logger.d("map:==" + map_lat + "===" + map_lng);
                lattitude = map_lat;
                longitude = map_lng;


            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //getAddressFromGoogleMap
    private class GetAddressAsync extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            String strAddress = "";

            runOnUiThread(() -> {
                // progressDialog.show();

            });

            StringBuilder result = new StringBuilder();
            try {
                Geocoder geocoder = new Geocoder(MapActivity.this, Locale.US/*Locale.getDefault()*/);
                List<Address> addresses = geocoder.getFromLocation(lattitude, longitude, 5);
                if (addresses.size() > 0) {
                    Address address = addresses.get(0);

                    for (int i = 0; i <= addresses.get(0).getMaxAddressLineIndex(); i++) {
                        if (i == addresses.get(0).getMaxAddressLineIndex()) {
                            result.append(addresses.get(0).getAddressLine(i));
                        } else {
                            result.append(addresses.get(0).getAddressLine(i) + ",");
                        }
                    }

                    /*=================   Setting Result Including Space =============*/
                    strAddress = result.toString();
                    strAddress = strAddress.replaceAll("[,]", "$0 ").replaceAll("\\s+", " ");

                    selectedCity = address.getLocality();
                    country_code = address.getCountryCode();

                    //Log.e("tag", "city" + selectedCity + "==strAddress==" + strAddress + "\n==" + addresses.get(0).getAddressLine(0));
                    Logger.e("getSubAdminArea" + "" + address.getSubAdminArea());

                    Logger.e("tag" + "city if" + selectedCity + "==lat:" + lattitude + "==lng:" + longitude);

                    if (selectedCity == null) {
                        selectedCity = address.getSubAdminArea();
                        //Log.e("tag", "city if" + selectedCity+"==lat:"+lattitude+"==lng:"+longitude);
                    }

                    //when get locality null
                    if (selectedCity == null) {
                        for (int i = 0; i < addresses.size(); i++) {
                            Logger.e("tag" + "loop" + addresses.get(i).getLocality());
                            if (addresses.get(i).getLocality() != null) {
                                selectedCity = addresses.get(i).getLocality();
                                break;
                            }
                        }
                    }
                }
            } catch (IOException e) {
                Logger.e("tag" + e.getMessage());
            }
            return strAddress;
        }

        @Override
        protected void onPostExecute(String strAddress) {
            super.onPostExecute(strAddress);

            runOnUiThread(() -> {
                // progressDialog.dismiss();
            });

            //For static search address in auto complete
            if (StringUtils.isNotEmpty(placeDescription)) {
                selectedAddress = placeDescription;
                binding.actvGoogleMapActivitySearch.setText(placeDescription);
            } else {
                selectedAddress = strAddress;
                binding.actvGoogleMapActivitySearch.setText(strAddress);
            }

            // binding.actvGoogleMapActivitySearch.setText(strAddress);
            Logger.d("adressofautcomplete" + strAddress);
            binding.actvGoogleMapActivitySearch.setSelection(binding.actvGoogleMapActivitySearch.getText().toString().length());
            binding.actvGoogleMapActivitySearch.dismissDropDown();
            //selectedAddress = strAddress;
            placeDescription = " ";
        }
    }

    //auto complete adpater
    public class AutoCompleteAdapter extends ArrayAdapter<AutoCompletePlace> {

        private GoogleApiClient mGoogleApiClient;

        public AutoCompleteAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
                holder.text = (TextView) convertView.findViewById(android.R.id.text1);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            //placeDescription = getItem(position).getDescription();
            holder.text.setText(getItem(position).getDescription());

            return convertView;
        }

        public void setGoogleApiClient(GoogleApiClient googleApiClient) {
            this.mGoogleApiClient = googleApiClient;
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(final CharSequence constraint) {

                    /*if (mGoogleApiClient == null || !mGoogleApiClient.isConnected()) {
                        //AppClass.toastView.showMessage("Not connected");
                        return null;
                    }*/

                    runOnUiThread(() -> {
                        clear();
                        displayPredictiveResults(constraint);
                    });

                    return null;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    notifyDataSetChanged();
                }
            };
        }

        private void displayPredictiveResults(CharSequence content) {

            String query = "";

            if (StringUtils.isNotEmpty(content)) {
                query = content.toString();
            }

            AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

            // Create a RectangularBounds object.
            RectangularBounds bounds = RectangularBounds.newInstance(
                    new LatLng(lattitude, longitude),
                    new LatLng(lattitude, longitude));

            FindAutocompletePredictionsRequest request1 = FindAutocompletePredictionsRequest.builder()

                    .setSessionToken(token)
                    .setQuery(query)
                    .build();


            placesClient.findAutocompletePredictions(request1).addOnSuccessListener(findAutocompletePredictionsResponse -> {
                clear();
                for (AutocompletePrediction prediction : findAutocompletePredictionsResponse.getAutocompletePredictions()) {


                    add(new AutoCompletePlace(prediction.getPlaceId(),
                            prediction.getFullText(null).toString()));
                }
            }).addOnFailureListener(exception -> {
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    Logger.d("Place not found: " + apiException.getStatusCode());
                }
            });
        }

        private class ViewHolder {
            TextView text;
        }

    }

    //autocomplete pozo class
    public class AutoCompletePlace {

        private String id;
        private String description;

        public AutoCompletePlace(String id, String description) {
            this.id = id;
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }


    }


    //GET CURRENT LOCATION
    private void getCurrentLocation() {
        if (ParentObj.getInstance().networkConnectivity.isNetworkAvailable()) {
            location = true;
            loc_gps.get_Initlocation();
            loc_gps.set_request();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_RESULT_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    isFromPermissionScreen = false;
                    setAutoCompleteTextView();
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
        final AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
//        builder.setTitle("Hey " + AppClass.preferences.getUserName() + "...");
        builder.setMessage("You have forcefully denied location permission " +
                "for this action. Please open settings, go to permissions and allow them.");
        builder.setCancelable(false);
        builder.setPositiveButton("SETTING", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + MapActivity.this.getPackageName()));
                MapActivity.this.startActivity(intent);


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
        AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
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
