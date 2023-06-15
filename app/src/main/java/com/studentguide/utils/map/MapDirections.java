package com.studentguide.utils.map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.PolylineOptions;
import com.studentguide.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;



public class MapDirections {

    private OnMapDirectionLoadingComplete onMapDirectionLoadingComplete;
    private int lineColor;
    private String mode;
    private Activity activity;

    public MapDirections(LatLng origin, LatLng dest, Activity activity, int lineColor, String mode,OnMapDirectionLoadingComplete onMapDirectionLoadingComplete) {
        this.activity = activity;
        this.lineColor = lineColor;
        this.mode = mode;
        this.onMapDirectionLoadingComplete = onMapDirectionLoadingComplete;
        String url = getDirectionsUrl(origin, dest,mode);
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(url);
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
                //data = dummyLocation;
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            parserTask.execute(result);

        }
    }

    /* A class to parse the Google Places in JSON format */
    @SuppressLint("StaticFieldLeak")
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();
                StaticData.polyLineList = new ArrayList<>();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(Objects.requireNonNull(point.get("lat")));
                    double lng = Double.parseDouble(Objects.requireNonNull(point.get("lng")));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                    StaticData.polyLineList.add(position);
                }

                int PATTERN_DASH_LENGTH_PX = 10;
                int PATTERN_GAP_LENGTH_PX = 10;
                PatternItem DOT = new Dot();
                PatternItem DASH = new Dash(PATTERN_DASH_LENGTH_PX);
                PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);
                List<PatternItem> PATTERN_POLYGON_ALPHA_DASH = Arrays.asList(GAP, DASH);
                List<PatternItem> PATTERN_POLYGON_ALPHA_DOT = Arrays.asList(GAP, DOT);

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(8);
                lineOptions.color(ContextCompat.getColor(activity, lineColor));
            }

            onMapDirectionLoadingComplete.onGetDirection(lineOptions);
            // Drawing polyline in the Google Map for the i-th route

        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest, String mode1) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        //Mode of transport
        String mode = "mode=" + mode1;

        //Google map key
        String key = "key=" + activity.getResources().getString(R.string.google_maps_key);

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode + "&" + key;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }


    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

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
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


    public interface OnMapDirectionLoadingComplete {
        void onGetDirection(PolylineOptions polylineOptions);
    }
}