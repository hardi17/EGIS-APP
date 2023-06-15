package com.studentguide.home;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.studentguide.ParentObj;
import com.studentguide.R;
import com.studentguide.adapter.RoutesAdapter;
import com.studentguide.adapter.TravelOptionAdapter;
import com.studentguide.databinding.ActivityTravelBinding;
import com.studentguide.extra.PlaceDetailsJSONParser;
import com.studentguide.extra.PlaceIDeDetialsJsonParser;
import com.studentguide.extra.RouteDetailJSONParser;
import com.studentguide.listener.OnRefreshTravelOptionListener;
import com.studentguide.utils.Logger;
import com.studentguide.utils.StringUtils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class TravelActivity extends AppCompatActivity implements OnRefreshTravelOptionListener {

    ActivityTravelBinding binding;

    RoutesAdapter routesAdapter;

    public final int REQUEST_CODE_TO_GOOGLE_MAP_ACTIVTY = 98;
    private double
            lattitude = 0.0,
            longitude = 0.0,
            lat = 0.0,
            lng = 0.0;
    TravelOptionAdapter optionAdapter;
    private String country_code = "",
            area = "",
            broadcast_address = "",
            mode = "",
            duration = "",
            distance = "";
    public double startLat = 0.0,
            startLng = 0.0,
            endLat = 0.0,
            endLng = 0.0;
    public static String fromPlaceId = "",
            toPlaceId = "";

    private boolean isTo = false,
            isFrom = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_travel);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        binding.toolbar.txtTitle.setText(getString(R.string._travel));

        // setSpinnerSize();
        String[] travelOptions = getResources().getStringArray(R.array.travelOption_name);
        ArrayList<String> travelOptionsList = new ArrayList<>();
        Collections.addAll(travelOptionsList, travelOptions);

        String[] travel_id = getResources().getStringArray(R.array.travelOption);
        ArrayList<String> travelOptionsIdList = new ArrayList<>();
        Collections.addAll(travelOptionsIdList, travel_id);

        binding.rcvTravelOptions.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        optionAdapter = new TravelOptionAdapter(this, travelOptionsList, travelOptionsIdList, this);
        binding.rcvTravelOptions.setAdapter(optionAdapter);

        binding.rcvRoutes.setLayoutManager(new LinearLayoutManager(this));
        routesAdapter = new RoutesAdapter(this, "Walk");
        binding.rcvRoutes.setAdapter(routesAdapter);
    }

    @OnClick(R.id.iv_back)
    public void goToBack() {
        onBackPressed();
        fromPlaceId = "";
        toPlaceId = "";
    }

    @OnClick(R.id.iv_changeLocation)
    public void changeLocationFromTo() {
        String fromtText = "",
                fromPlaceIdValue = "",
                toText = "",
                toPlaceIDValue = "";

        if (binding.tvTravelActivityFrom.getText().length() > 0 && binding.tvTravelActivityTo.getText().length() > 0) {
            fromtText = binding.tvTravelActivityFrom.getText().toString();
            fromPlaceIdValue = fromPlaceId;
            toPlaceIDValue = toPlaceId;
            toText = binding.tvTravelActivityTo.getText().toString();
        }

        fromPlaceId = toPlaceIDValue;
        toPlaceId = fromPlaceIdValue;
        binding.tvTravelActivityFrom.setText(toText);
        binding.tvTravelActivityTo.setText(fromtText);
    }

    @OnClick(R.id.tv_TravelActivity_from)
    public void goToMapFrom() {
        startActivityForResult(new Intent(TravelActivity.this, MapActivity.class)
                .putExtra("isFrom", true)
                .putExtra("isTo", false)
                .putExtra("fromPlaceId", fromPlaceId)
                .putExtra("lattitude", lat)
                .putExtra("longitude", lng), REQUEST_CODE_TO_GOOGLE_MAP_ACTIVTY);
    }

    @OnClick(R.id.tv_TravelActivity_to)
    public void goToMapTo() {
        startActivityForResult(new Intent(TravelActivity.this, MapActivity.class)
                .putExtra("isFrom", false)
                .putExtra("isTo", true)
                .putExtra("toPlaceId", toPlaceId)
                .putExtra("lattitude", lat)
                .putExtra("longitude", lng), REQUEST_CODE_TO_GOOGLE_MAP_ACTIVTY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_TO_GOOGLE_MAP_ACTIVTY:
                if (resultCode == -1 && data != null) {

                    lattitude = data.getDoubleExtra("latitude", 0.0);
                    longitude = data.getDoubleExtra("longitude", 0.0);
                    country_code = data.getStringExtra("country_code");
                    area = data.getStringExtra("city");
                    broadcast_address = data.getStringExtra("address");
                    isTo = data.getBooleanExtra("isTo", false);
                    isFrom = data.getBooleanExtra("isFrom", false);
                    broadcast_address = data.getStringExtra("address");

                   /* fromPlaceId = data.getStringExtra("fromPlaceId");
                    toPlaceId = data.getStringExtra("toPlaceId");*/
                    getPlaceID();
                    if (isFrom) {
                        binding.tvTravelActivityFrom.setText(broadcast_address);
                    } else {
                        binding.tvTravelActivityTo.setText(broadcast_address);
                    }
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + requestCode);
        }
    }

    private void getPlaceID() {

/*
        https://maps.googleapis.com/maps/api/geocode/json?address=1600+Amphitheatre+Parkway,+Mountain+View,+CA&key=YOUR_API_KEY
*/

        StringBuilder sb = new StringBuilder(" https://maps.googleapis.com/maps/api/geocode/json?");
        sb.append("address=" + broadcast_address);
        sb.append("&key=" + getString(R.string.google_maps_key));

        // Creating a new non-ui thread task to download Google place details
        PlacesTask placesTask = new PlacesTask();

        // Invokes the "doInBackground()" method of the class PlaceTask
        placesTask.execute(sb.toString());
    }

    /**
     * A class, to download Google Place Details
     */
    private class PlacesTask extends AsyncTask<String, Integer, String> {

        String data = null;

        // Invoked by execute() method of this object
        @Override
        protected String doInBackground(String... url) {
            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(String result) {
            ParserTask parserTask = new ParserTask();

            // Start parsing the Google place details in JSON format
            // Invokes the "doInBackground()" method of the class ParseTask
            parserTask.execute(result);
        }
    }

    /**
     * A class to parse the Google Place Details in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, HashMap<String, String>> {

        JSONObject jObject;

        // Invoked by execute() method of this object
        @Override
        protected HashMap<String, String> doInBackground(String... jsonData) {

            HashMap<String, String> hPlaceDetails = null;
            PlaceIDeDetialsJsonParser placeDetailsJsonParser = new PlaceIDeDetialsJsonParser();

            try {
                jObject = new JSONObject(jsonData[0]);

                // Start parsing Google place details in JSON format
                hPlaceDetails = placeDetailsJsonParser.parse(jObject);

            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return hPlaceDetails;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(HashMap<String, String> hPlaceDetails) {

            String place_id = hPlaceDetails.get("placeID");

            if (isFrom) {
                fromPlaceId = place_id;
            } else {
                toPlaceId = place_id;
            }
        }
    }


    @Override
    public void onRefreshRoutes(String optionName, String type) {

        mode = type;

        StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        sb.append("origin=place_id:" + fromPlaceId);
        sb.append("&destination=place_id:" + toPlaceId);
        sb.append("&mode=" + optionName);
        // sb.append("&sensor=true");
        sb.append("&key=" + getString(R.string.google_maps_key));

        // Creating a new non-ui thread task to download Google place json data
        RouteTask routeTask = new RouteTask();

        // Invokes the "doInBackground()" method of the class PlaceTask
        routeTask.execute(sb.toString());

        routesAdapter.setItems(optionName);
        routesAdapter.notifyDataSetChanged();
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


    private class RouteTask extends AsyncTask<String, Integer, String> {

        String data = null;

        // Invoked by execute() method of this object
        @Override
        protected String doInBackground(String... url) {
            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Logger.d("Background Task" + e.toString());
            }
            return data;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(String result) {
            DetailTask detailTask = new DetailTask();

            // Start parsing the Google places in JSON format
            // Invokes the "doInBackground()" method of the class ParseTask
            detailTask.execute(result);
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class DetailTask extends AsyncTask<String, Integer, HashMap<String, String>> {

        JSONObject jObject;

        // Invoked by execute() method of this object
        @Override
        protected HashMap<String, String> doInBackground(String... jsonData) {

            HashMap<String, String> routeDetails = null;
            RouteDetailJSONParser routeDetailJSONParser = new RouteDetailJSONParser();

            try {
                jObject = new JSONObject(jsonData[0]);

                /** Getting the parsed data as a List construct */
                routeDetails = routeDetailJSONParser.parse(jObject);

            } catch (Exception e) {
                Logger.d("Exception" + e.toString());
            }
            return routeDetails;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(HashMap<String, String> list) {

            String sLat = list.get("startLat");
            if (sLat != null) {
                startLat = Double.parseDouble(sLat);
            }
            String sLng = list.get("startLng");
            if (sLng != null) {
                startLng = Double.parseDouble(sLng);
            }
            String eLat = list.get("endLat");
            if (eLat != null) {
                endLat = Double.parseDouble(eLat);
            }
            String eLng = list.get("endLng");
            if (eLng != null) {
                endLng = Double.parseDouble(eLng);
            }

            String summary = list.get("summary");
            if (StringUtils.isNotEmpty(summary)) {
                binding.tvSummaryValue.setText(" " + summary);
            }
            String warnings = list.get("warnings");
            if (StringUtils.isNotEmpty(warnings)) {
                binding.tvWarningsTitleValue.setText(" " + warnings);
            }
            String start_address = list.get("start_address");
            if (StringUtils.isNotEmpty(start_address)) {
                binding.tvStartAddValue.setText(" " + start_address);
            }

            String end_address = list.get("end_address");
            if (StringUtils.isNotEmpty(end_address)) {
                binding.tvEndAddValue.setText(" " + end_address);
            }

            String arrival_time = list.get("arrival_time");
            if (StringUtils.isNotEmpty(arrival_time)) {
                binding.tvArrivalTimeValue.setText(" " + arrival_time);
            }

            String departure_time = list.get("departure_time");
            if (StringUtils.isNotEmpty(departure_time)) {
                binding.tvdepartureTimeValue.setText(" " + departure_time);
            }

            distance = list.get("distance");
            if (StringUtils.isNotEmpty(distance)) {
                binding.tvDistanceValue.setText(" " + distance);
            }

            duration = list.get("duration");
            if (StringUtils.isNotEmpty(duration)) {
                binding.tvDurationValue.setText(" " + duration);
            }

        }
    }

    @OnClick(R.id.tv_mapView)
    public void gotoMapView() {
        if (startLat != 0.0 && startLng != 0.0 && endLat != 0.0 && endLng != 0.0) {
            String startToAnd = " \"From\" " + binding.tvTravelActivityFrom.getText() + " \"To\" " + binding.tvTravelActivityTo.getText();
            startActivity(new Intent(this, MapActivity.class)
                    .putExtra("isPath", true)
                    .putExtra("duration", duration)
                    .putExtra("distance", distance)
                    .putExtra("mode", mode)
                    .putExtra("startLat", startLat)
                    .putExtra("startLng", startLng)
                    .putExtra("startToAnd", startToAnd)
                    .putExtra("endLat", endLat)
                    .putExtra("endLng", endLng));
        } else {
            ParentObj.snackBarView.snackBarShow(TravelActivity.this, getString(R.string._plzSelectLocation));
        }
    }

}
