package com.studentguide.utils.map;

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

/**
 * Created by Androiddev on 11/21/2017.
 */

public class MapDirections {

    private OnMapDirectionLoadingComplete onMapDirectionLoadingComplete;
    private int lineColor;
    private Activity activity;

    public MapDirections(LatLng origin, LatLng dest, Activity activity, int lineColor, OnMapDirectionLoadingComplete onMapDirectionLoadingComplete) {
        this.activity = activity;
        this.lineColor = lineColor;
        this.onMapDirectionLoadingComplete = onMapDirectionLoadingComplete;
        String url = getDirectionsUrl(origin, dest);
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
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();
                StaticData.polyLineList = new ArrayList<>();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
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

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        //Mode of transport
        String mode = "mode=driving";

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

    public String dummyLocation = "{\n" +
            "  \"geocoded_waypoints\": [\n" +
            "    {\n" +
            "      \"geocoder_status\": \"OK\",\n" +
            "      \"place_id\": \"ChIJXQWzv8krXDkRjiz-d0lxR40\",\n" +
            "      \"types\": [\n" +
            "        \"route\"\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"geocoder_status\": \"OK\",\n" +
            "      \"place_id\": \"ChIJqyx7g1aEXjkRS8PwAbwmEGY\",\n" +
            "      \"types\": [\n" +
            "        \"street_address\"\n" +
            "      ]\n" +
            "    }\n" +
            "  ],\n" +
            "  \"routes\": [\n" +
            "    {\n" +
            "      \"bounds\": {\n" +
            "        \"northeast\": {\n" +
            "          \"lat\": 23.2154991,\n" +
            "          \"lng\": 72.6430574\n" +
            "        },\n" +
            "        \"southwest\": {\n" +
            "          \"lat\": 23.0217628,\n" +
            "          \"lng\": 72.57143569999999\n" +
            "        }\n" +
            "      },\n" +
            "      \"copyrights\": \"Map data ©2019 Google\",\n" +
            "      \"legs\": [\n" +
            "        {\n" +
            "          \"distance\": {\n" +
            "            \"text\": \"26.9 km\",\n" +
            "            \"value\": 26893\n" +
            "          },\n" +
            "          \"duration\": {\n" +
            "            \"text\": \"39 mins\",\n" +
            "            \"value\": 2369\n" +
            "          },\n" +
            "          \"end_address\": \"4 shantanu banlows rajpath club pachhad sindhu bhavan same bodkdev, Narolgam, Ellisbridge, Ahmedabad, Gujarat 380006, India\",\n" +
            "          \"end_location\": {\n" +
            "            \"lat\": 23.0225306,\n" +
            "            \"lng\": 72.57143569999999\n" +
            "          },\n" +
            "          \"start_address\": \"Unnamed Road, Sector 6, Gandhinagar, Gujarat 382006, India\",\n" +
            "          \"start_location\": {\n" +
            "            \"lat\": 23.2154991,\n" +
            "            \"lng\": 72.63685889999999\n" +
            "          },\n" +
            "          \"steps\": [\n" +
            "            {\n" +
            "              \"distance\": {\n" +
            "                \"text\": \"0.1 km\",\n" +
            "                \"value\": 98\n" +
            "              },\n" +
            "              \"duration\": {\n" +
            "                \"text\": \"1 min\",\n" +
            "                \"value\": 27\n" +
            "              },\n" +
            "              \"end_location\": {\n" +
            "                \"lat\": 23.2151363,\n" +
            "                \"lng\": 72.63773209999999\n" +
            "              },\n" +
            "              \"html_instructions\": \"Head \\u003cb\\u003eeast\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003ePass by Royal Bombay chicken Briyani (on the right)\\u003c/div\\u003e\",\n" +
            "              \"polyline\": {\n" +
            "                \"points\": \"{gulCk{izLZqAf@oABK\"\n" +
            "              },\n" +
            "              \"start_location\": {\n" +
            "                \"lat\": 23.2154991,\n" +
            "                \"lng\": 72.63685889999999\n" +
            "              },\n" +
            "              \"travel_mode\": \"DRIVING\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"distance\": {\n" +
            "                \"text\": \"0.3 km\",\n" +
            "                \"value\": 272\n" +
            "              },\n" +
            "              \"duration\": {\n" +
            "                \"text\": \"1 min\",\n" +
            "                \"value\": 70\n" +
            "              },\n" +
            "              \"end_location\": {\n" +
            "                \"lat\": 23.2130133,\n" +
            "                \"lng\": 72.63640909999999\n" +
            "              },\n" +
            "              \"html_instructions\": \"Turn \\u003cb\\u003eright\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003ePass by Bal Nagari Ground (on the left)\\u003c/div\\u003e\",\n" +
            "              \"maneuver\": \"turn-right\",\n" +
            "              \"polyline\": {\n" +
            "                \"points\": \"seulCy`jzLdB~@~CdBxAl@hAr@\"\n" +
            "              },\n" +
            "              \"start_location\": {\n" +
            "                \"lat\": 23.2151363,\n" +
            "                \"lng\": 72.63773209999999\n" +
            "              },\n" +
            "              \"travel_mode\": \"DRIVING\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"distance\": {\n" +
            "                \"text\": \"0.2 km\",\n" +
            "                \"value\": 208\n" +
            "              },\n" +
            "              \"duration\": {\n" +
            "                \"text\": \"1 min\",\n" +
            "                \"value\": 39\n" +
            "              },\n" +
            "              \"end_location\": {\n" +
            "                \"lat\": 23.2120644,\n" +
            "                \"lng\": 72.63815799999999\n" +
            "              },\n" +
            "              \"html_instructions\": \"Turn \\u003cb\\u003eleft\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003ePass by The Boo Craftcorner (on the right)\\u003c/div\\u003e\",\n" +
            "              \"maneuver\": \"turn-left\",\n" +
            "              \"polyline\": {\n" +
            "                \"points\": \"ixtlCqxizLfDmHTo@\"\n" +
            "              },\n" +
            "              \"start_location\": {\n" +
            "                \"lat\": 23.2130133,\n" +
            "                \"lng\": 72.63640909999999\n" +
            "              },\n" +
            "              \"travel_mode\": \"DRIVING\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"distance\": {\n" +
            "                \"text\": \"0.5 km\",\n" +
            "                \"value\": 489\n" +
            "              },\n" +
            "              \"duration\": {\n" +
            "                \"text\": \"1 min\",\n" +
            "                \"value\": 54\n" +
            "              },\n" +
            "              \"end_location\": {\n" +
            "                \"lat\": 23.2082626,\n" +
            "                \"lng\": 72.6361242\n" +
            "              },\n" +
            "              \"html_instructions\": \"Turn \\u003cb\\u003eright\\u003c/b\\u003e at Gopal Namkeen onto \\u003cb\\u003eGH Rd\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003ePass by Bansari Reading Library (on the left)\\u003c/div\\u003e\",\n" +
            "              \"maneuver\": \"turn-right\",\n" +
            "              \"polyline\": {\n" +
            "                \"points\": \"krtlCocjzLN]|NvHvC`BZLLDRBRB\"\n" +
            "              },\n" +
            "              \"start_location\": {\n" +
            "                \"lat\": 23.2120644,\n" +
            "                \"lng\": 72.63815799999999\n" +
            "              },\n" +
            "              \"travel_mode\": \"DRIVING\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"distance\": {\n" +
            "                \"text\": \"0.8 km\",\n" +
            "                \"value\": 800\n" +
            "              },\n" +
            "              \"duration\": {\n" +
            "                \"text\": \"1 min\",\n" +
            "                \"value\": 76\n" +
            "              },\n" +
            "              \"end_location\": {\n" +
            "                \"lat\": 23.2045855,\n" +
            "                \"lng\": 72.6427783\n" +
            "              },\n" +
            "              \"html_instructions\": \"At \\u003cb\\u003eGH 2 Hemchandra Charya Cir\\u003c/b\\u003e, take the \\u003cb\\u003e1st\\u003c/b\\u003e exit onto \\u003cb\\u003eRd Number 2\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003ePass by Giit Computer Classes (on the left)\\u003c/div\\u003e\",\n" +
            "              \"maneuver\": \"roundabout-left\",\n" +
            "              \"polyline\": {\n" +
            "                \"points\": \"szslCwvizLF?J@d@cAj@qAdAcCf@iAdA}BRc@HOHSFKBI\\\\aAf@gAv@eBhCaGtAyCf@mAH[\"\n" +
            "              },\n" +
            "              \"start_location\": {\n" +
            "                \"lat\": 23.2082626,\n" +
            "                \"lng\": 72.6361242\n" +
            "              },\n" +
            "              \"travel_mode\": \"DRIVING\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"distance\": {\n" +
            "                \"text\": \"1.0 km\",\n" +
            "                \"value\": 1025\n" +
            "              },\n" +
            "              \"duration\": {\n" +
            "                \"text\": \"1 min\",\n" +
            "                \"value\": 87\n" +
            "              },\n" +
            "              \"end_location\": {\n" +
            "                \"lat\": 23.196294,\n" +
            "                \"lng\": 72.6398521\n" +
            "              },\n" +
            "              \"html_instructions\": \"At \\u003cb\\u003eNyay Circle- CH -2\\u003c/b\\u003e, take the \\u003cb\\u003e3rd\\u003c/b\\u003e exit onto \\u003cb\\u003eCH Rd\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003ePass by NVDC Chicken Biryani Non Veg Point (on the left)\\u003c/div\\u003e\",\n" +
            "              \"maneuver\": \"roundabout-left\",\n" +
            "              \"polyline\": {\n" +
            "                \"points\": \"ucslCk`kzL?AAA?AAA?AAA?A?A?A?A?A?A?A?C?A@A?A?A@A@A?A@A@??A@A@A@?BADAD?@?@?@@@?@?@@@?@@@@@@@@@@?@@B@@?@?@?@RVbAh@pFpCbAj@dBv@pBr@l@Tn@RbBh@nAXpAZ~Bd@tCd@xBXzANz@DX@RCNCTE\"\n" +
            "              },\n" +
            "              \"start_location\": {\n" +
            "                \"lat\": 23.2045855,\n" +
            "                \"lng\": 72.6427783\n" +
            "              },\n" +
            "              \"travel_mode\": \"DRIVING\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"distance\": {\n" +
            "                \"text\": \"1.2 km\",\n" +
            "                \"value\": 1189\n" +
            "              },\n" +
            "              \"duration\": {\n" +
            "                \"text\": \"2 mins\",\n" +
            "                \"value\": 92\n" +
            "              },\n" +
            "              \"end_location\": {\n" +
            "                \"lat\": 23.1858477,\n" +
            "                \"lng\": 72.6386686\n" +
            "              },\n" +
            "              \"html_instructions\": \"At \\u003cb\\u003eIndroda Cir\\u003c/b\\u003e, take the \\u003cb\\u003e2nd\\u003c/b\\u003e exit onto \\u003cb\\u003eGandhinagar - Ahmedabad Rd\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003ePass by Mol-tech Solution | Web Development Company (on the right)\\u003c/div\\u003e\",\n" +
            "              \"maneuver\": \"roundabout-left\",\n" +
            "              \"polyline\": {\n" +
            "                \"points\": \"yoqlCanjzL?A@A?A@A?A@A@ALIJGLELCL?N?L@LDBBBBBB@BBD@D@D@DVJNFLDPDV@b@BtADdBJnDRdDNhAHtKn@zDTJ?fG\\\\|G\\\\r@A\"\n" +
            "              },\n" +
            "              \"start_location\": {\n" +
            "                \"lat\": 23.196294,\n" +
            "                \"lng\": 72.6398521\n" +
            "              },\n" +
            "              \"travel_mode\": \"DRIVING\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"distance\": {\n" +
            "                \"text\": \"5.8 km\",\n" +
            "                \"value\": 5792\n" +
            "              },\n" +
            "              \"duration\": {\n" +
            "                \"text\": \"6 mins\",\n" +
            "                \"value\": 382\n" +
            "              },\n" +
            "              \"end_location\": {\n" +
            "                \"lat\": 23.1340291,\n" +
            "                \"lng\": 72.6330947\n" +
            "              },\n" +
            "              \"html_instructions\": \"At \\u003cb\\u003eRaksha Shakti Cir\\u003c/b\\u003e, take the \\u003cb\\u003e2nd\\u003c/b\\u003e exit and stay on \\u003cb\\u003eGandhinagar - Ahmedabad Rd\\u003c/b\\u003e\",\n" +
            "              \"maneuver\": \"roundabout-left\",\n" +
            "              \"polyline\": {\n" +
            "                \"points\": \"qnolCufjzLDABAB?D?B@B?B@B@B@@??@@?@@@?@@|@JjCRxOz@~RbAnBL`UnAtOv@b@B`@Bb@Bb@B`@BlH`@xJj@nDRj]hBjH^zJd@rCPbEVjERdNt@~Ll@xMn@zG\\\\ZBzBL`DP|Kb@L@|Kh@pEVnETvBJvBJlFVzH^nCPr@Bn@?l@A\"\n" +
            "              },\n" +
            "              \"start_location\": {\n" +
            "                \"lat\": 23.1858477,\n" +
            "                \"lng\": 72.6386686\n" +
            "              },\n" +
            "              \"travel_mode\": \"DRIVING\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"distance\": {\n" +
            "                \"text\": \"1.9 km\",\n" +
            "                \"value\": 1923\n" +
            "              },\n" +
            "              \"duration\": {\n" +
            "                \"text\": \"2 mins\",\n" +
            "                \"value\": 138\n" +
            "              },\n" +
            "              \"end_location\": {\n" +
            "                \"lat\": 23.1169937,\n" +
            "                \"lng\": 72.6315824\n" +
            "              },\n" +
            "              \"html_instructions\": \"At \\u003cb\\u003eKoba Cir\\u003c/b\\u003e, take the \\u003cb\\u003e2nd\\u003c/b\\u003e exit and stay on \\u003cb\\u003eGandhinagar - Ahmedabad Rd\\u003c/b\\u003e\",\n" +
            "              \"maneuver\": \"roundabout-left\",\n" +
            "              \"polyline\": {\n" +
            "                \"points\": \"ujelCycizL?A@A?A@A?A@?@C@A@CBABA@ABABAFCHAD?B?B?@?B?@@B?@@B?B@@@B@B@@@B@@B@@@B@@@B@B?@@B?B@BL?F?H@F@HBN@NDF@R@hGVxFVfCLjBLpBJ~GZ|FVrBHjBH@@tNj@rHZV?xERzCPfCNt@DV@PANANCTEl@M\"\n" +
            "              },\n" +
            "              \"start_location\": {\n" +
            "                \"lat\": 23.1340291,\n" +
            "                \"lng\": 72.6330947\n" +
            "              },\n" +
            "              \"travel_mode\": \"DRIVING\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"distance\": {\n" +
            "                \"text\": \"3.5 km\",\n" +
            "                \"value\": 3458\n" +
            "              },\n" +
            "              \"duration\": {\n" +
            "                \"text\": \"4 mins\",\n" +
            "                \"value\": 251\n" +
            "              },\n" +
            "              \"end_location\": {\n" +
            "                \"lat\": 23.086064,\n" +
            "                \"lng\": 72.6301541\n" +
            "              },\n" +
            "              \"html_instructions\": \"At \\u003cb\\u003eBhat Cir\\u003c/b\\u003e, take the \\u003cb\\u003e3rd\\u003c/b\\u003e exit and stay on \\u003cb\\u003eGandhinagar - Ahmedabad Rd\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003ePass by the gas station (on the left in 1.3&nbsp;km)\\u003c/div\\u003e\",\n" +
            "              \"maneuver\": \"roundabout-left\",\n" +
            "              \"polyline\": {\n" +
            "                \"points\": \"e`blCkzhzLFE@?@A@?HCFAF?H@F@FBDDFDDD@B?@@??@@@?@r@TPFRB\\\\@hCPnMn@ZB~H^J@L@`FR|I\\\\`Kf@zDPj@BJ@L?J@b@BzAHbHVB?fBF~@D|BDfCBJ?xD@dCAt@Av@@d@?f@?t@A`DAN?\\\\AfKEhBAzDEvBEt@?v@?|@?hQMhGEhCCrAAD?fAE\"\n" +
            "              },\n" +
            "              \"start_location\": {\n" +
            "                \"lat\": 23.1169937,\n" +
            "                \"lng\": 72.6315824\n" +
            "              },\n" +
            "              \"travel_mode\": \"DRIVING\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"distance\": {\n" +
            "                \"text\": \"1.1 km\",\n" +
            "                \"value\": 1118\n" +
            "              },\n" +
            "              \"duration\": {\n" +
            "                \"text\": \"2 mins\",\n" +
            "                \"value\": 119\n" +
            "              },\n" +
            "              \"end_location\": {\n" +
            "                \"lat\": 23.0837281,\n" +
            "                \"lng\": 72.62067019999999\n" +
            "              },\n" +
            "              \"html_instructions\": \"At \\u003cb\\u003eApollo Cir\\u003c/b\\u003e, take the \\u003cb\\u003e2nd\\u003c/b\\u003e exit onto \\u003cb\\u003eAirport Rd\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003ePass by Sun Shine Bunglows (on the right in 800&nbsp;m)\\u003c/div\\u003e\",\n" +
            "              \"maneuver\": \"roundabout-left\",\n" +
            "              \"polyline\": {\n" +
            "                \"points\": \"{~{kCmqhzL@C@A@ABA@ABA@?@AB?@?@?@?B?@?@?@@B@@?@@@@@@@@@@@@?@@B?@?@@B?@?@?B?@?@AB?@?@A@AB?@A@A@?BA\\\\?FAV?N@B?\\\\AB?b@@B?d@?D?B@`@@f@@f@?d@?H@\\\\@d@@f@?TB`B@f@@F?TFrBDtBB|A@h@FbBBrAN|HFb@H`@JZJTLNTX\\\\^`@\\\\NHTJFBl@P|@Tr@R\"\n" +
            "              },\n" +
            "              \"start_location\": {\n" +
            "                \"lat\": 23.086064,\n" +
            "                \"lng\": 72.6301541\n" +
            "              },\n" +
            "              \"travel_mode\": \"DRIVING\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"distance\": {\n" +
            "                \"text\": \"0.5 km\",\n" +
            "                \"value\": 511\n" +
            "              },\n" +
            "              \"duration\": {\n" +
            "                \"text\": \"1 min\",\n" +
            "                \"value\": 62\n" +
            "              },\n" +
            "              \"end_location\": {\n" +
            "                \"lat\": 23.0815452,\n" +
            "                \"lng\": 72.6165821\n" +
            "              },\n" +
            "              \"html_instructions\": \"Slight \\u003cb\\u003eright\\u003c/b\\u003e to stay on \\u003cb\\u003eAirport Rd\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003ePass by Axis Bank ATM (on the left)\\u003c/div\\u003e\",\n" +
            "              \"maneuver\": \"turn-slight-right\",\n" +
            "              \"polyline\": {\n" +
            "                \"points\": \"ip{kCevfzLx@`@`@V\\\\VNV^v@HZFZF^H`ATlDB\\\\Hj@Jh@DHDJFJDJ`@f@l@t@\\\\\\\\LL^Z\"\n" +
            "              },\n" +
            "              \"start_location\": {\n" +
            "                \"lat\": 23.0837281,\n" +
            "                \"lng\": 72.62067019999999\n" +
            "              },\n" +
            "              \"travel_mode\": \"DRIVING\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"distance\": {\n" +
            "                \"text\": \"3.0 km\",\n" +
            "                \"value\": 3001\n" +
            "              },\n" +
            "              \"duration\": {\n" +
            "                \"text\": \"5 mins\",\n" +
            "                \"value\": 303\n" +
            "              },\n" +
            "              \"end_location\": {\n" +
            "                \"lat\": 23.0628954,\n" +
            "                \"lng\": 72.5970706\n" +
            "              },\n" +
            "              \"html_instructions\": \"At \\u003cb\\u003eAirport Cir\\u003c/b\\u003e, take the \\u003cb\\u003e2nd\\u003c/b\\u003e exit and stay on \\u003cb\\u003eAirport Rd\\u003c/b\\u003e\",\n" +
            "              \"maneuver\": \"roundabout-left\",\n" +
            "              \"polyline\": {\n" +
            "                \"points\": \"ub{kCs|ezL@?@?@?@?@??@@?@@B@@@@@@B@@@B@B@@?B?B@FA@?@?@?@?@?@A@HJTv@`@vA`BzEh@`BrAbEFVzAnElAxDr@lC`@fAP\\\\FJVVHFb@ZnDhCzAhA|@p@f@\\\\`D~BVRbAt@XRdAt@bAv@lA~@TRJJNLr@r@f@f@RPfBfBv@t@\\\\\\\\dAfAPP\\\\ZJHZZPLLHpAp@dD~A`Ah@pE~BLFjDbBbBz@ND`@L`@Jb@JvBZb@NRJRJVLlAp@`ClAp@Z@@NHJJJJDFBFFFFPN`@dBhFFNPh@Tz@\"\n" +
            "              },\n" +
            "              \"start_location\": {\n" +
            "                \"lat\": 23.0815452,\n" +
            "                \"lng\": 72.6165821\n" +
            "              },\n" +
            "              \"travel_mode\": \"DRIVING\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"distance\": {\n" +
            "                \"text\": \"3.7 km\",\n" +
            "                \"value\": 3706\n" +
            "              },\n" +
            "              \"duration\": {\n" +
            "                \"text\": \"6 mins\",\n" +
            "                \"value\": 348\n" +
            "              },\n" +
            "              \"end_location\": {\n" +
            "                \"lat\": 23.0419893,\n" +
            "                \"lng\": 72.57538269999999\n" +
            "              },\n" +
            "              \"html_instructions\": \"Turn \\u003cb\\u003eright\\u003c/b\\u003e onto \\u003cb\\u003eRiverfront Rd\\u003c/b\\u003e\",\n" +
            "              \"maneuver\": \"turn-right\",\n" +
            "              \"polyline\": {\n" +
            "                \"points\": \"cnwkCubbzLHT[@SBK@_@Pe@Rm@ZWJ}Av@MHEDA@AD?@?B?B?BBH@HDJDHJTh@lAhBvDlArCl@hBFTPz@z@nDxA|FBFNb@\\\\z@L`@DPDR@PBXDVFVJXLTFLLN\\\\\\\\PPNRlBpEp@bBb@bABFLXHPTh@JPLLVR^VlCzBJJtAhBpBlCnBdC~ArB@Bt@~@fBpBJJTTb@d@`BtAbE~Cd@Xd@Vp@Z^Pf@T^RXLlB`AhAp@l@b@`@d@p@bATVXVb@`@ZN^NtBj@|@Zp@Pv@PjCh@vAVbANfDh@rCb@vDd@xFn@NBfI`A\"\n" +
            "              },\n" +
            "              \"start_location\": {\n" +
            "                \"lat\": 23.0628954,\n" +
            "                \"lng\": 72.5970706\n" +
            "              },\n" +
            "              \"travel_mode\": \"DRIVING\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"distance\": {\n" +
            "                \"text\": \"2.2 km\",\n" +
            "                \"value\": 2168\n" +
            "              },\n" +
            "              \"duration\": {\n" +
            "                \"text\": \"3 mins\",\n" +
            "                \"value\": 172\n" +
            "              },\n" +
            "              \"end_location\": {\n" +
            "                \"lat\": 23.0226723,\n" +
            "                \"lng\": 72.5769758\n" +
            "              },\n" +
            "              \"html_instructions\": \"Continue straight to stay on \\u003cb\\u003eRiverfront Rd\\u003c/b\\u003e\",\n" +
            "              \"maneuver\": \"straight\",\n" +
            "              \"polyline\": {\n" +
            "                \"points\": \"mkskCc{}yLxFd@D?jBB\\\\@Z@F@`BLF?fABfA@rBB~@E~@IjAMvAYtA[^IrAUrC[zBSlBKZAzIUhISv@Br@D\\\\Bd@@l@@f@Ad@Av@I~AQlCYb@GhBSh@E~@KHAfBOrHm@lAGnCM\"\n" +
            "              },\n" +
            "              \"start_location\": {\n" +
            "                \"lat\": 23.0419893,\n" +
            "                \"lng\": 72.57538269999999\n" +
            "              },\n" +
            "              \"travel_mode\": \"DRIVING\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"distance\": {\n" +
            "                \"text\": \"0.1 km\",\n" +
            "                \"value\": 112\n" +
            "              },\n" +
            "              \"duration\": {\n" +
            "                \"text\": \"1 min\",\n" +
            "                \"value\": 17\n" +
            "              },\n" +
            "              \"end_location\": {\n" +
            "                \"lat\": 23.0217628,\n" +
            "                \"lng\": 72.577282\n" +
            "              },\n" +
            "              \"html_instructions\": \"Turn \\u003cb\\u003eleft\\u003c/b\\u003e\",\n" +
            "              \"maneuver\": \"turn-left\",\n" +
            "              \"polyline\": {\n" +
            "                \"points\": \"urokCce~yL\\\\c@FEJEf@CzAG\"\n" +
            "              },\n" +
            "              \"start_location\": {\n" +
            "                \"lat\": 23.0226723,\n" +
            "                \"lng\": 72.5769758\n" +
            "              },\n" +
            "              \"travel_mode\": \"DRIVING\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"distance\": {\n" +
            "                \"text\": \"0.2 km\",\n" +
            "                \"value\": 214\n" +
            "              },\n" +
            "              \"duration\": {\n" +
            "                \"text\": \"1 min\",\n" +
            "                \"value\": 32\n" +
            "              },\n" +
            "              \"end_location\": {\n" +
            "                \"lat\": 23.0220967,\n" +
            "                \"lng\": 72.57930309999999\n" +
            "              },\n" +
            "              \"html_instructions\": \"Turn \\u003cb\\u003eleft\\u003c/b\\u003e\",\n" +
            "              \"maneuver\": \"turn-left\",\n" +
            "              \"polyline\": {\n" +
            "                \"points\": \"_mokC_g~yLI[WcAC]CY?a@AS?WCUCc@?g@AU@YAIAGAAIG\"\n" +
            "              },\n" +
            "              \"start_location\": {\n" +
            "                \"lat\": 23.0217628,\n" +
            "                \"lng\": 72.577282\n" +
            "              },\n" +
            "              \"travel_mode\": \"DRIVING\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"distance\": {\n" +
            "                \"text\": \"0.8 km\",\n" +
            "                \"value\": 809\n" +
            "              },\n" +
            "              \"duration\": {\n" +
            "                \"text\": \"2 mins\",\n" +
            "                \"value\": 100\n" +
            "              },\n" +
            "              \"end_location\": {\n" +
            "                \"lat\": 23.0225306,\n" +
            "                \"lng\": 72.57143569999999\n" +
            "              },\n" +
            "              \"html_instructions\": \"Sharp \\u003cb\\u003eleft\\u003c/b\\u003e onto \\u003cb\\u003eSwami Vivekananda Rd\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003ePass by Ellisbridge Shopping Centre (on the right in 750&nbsp;m)\\u003c/div\\u003e\",\n" +
            "              \"maneuver\": \"turn-sharp-left\",\n" +
            "              \"polyline\": {\n" +
            "                \"points\": \"cookCss~yLAV@f@FhC@\\\\C\\\\Gb@Ir@Eb@?BIlC]bOGxBCn@B`@?TExAGlA?V@V@VIx@\"\n" +
            "              },\n" +
            "              \"start_location\": {\n" +
            "                \"lat\": 23.0220967,\n" +
            "                \"lng\": 72.57930309999999\n" +
            "              },\n" +
            "              \"travel_mode\": \"DRIVING\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"traffic_speed_entry\": [\n" +
            "            \n" +
            "          ],\n" +
            "          \"via_waypoint\": [\n" +
            "            \n" +
            "          ]\n" +
            "        }\n" +
            "      ],\n" +
            "      \"overview_polyline\": {\n" +
            "        \"points\": \"{gulCk{izLZqAf@oABKdB~@~CdBxAl@hAr@fDmHd@mA|NvHvC`Bh@Rf@FR@pAuCfEoJ^y@dAiC`EgJ|BgFH]CEAMBQJKPCF@LJBJ?@RVbAh@tH|DdBv@pBr@|Ah@rDbApE`AtCd@xBXvCTl@Ad@I@CBGZSZI\\\\?ZFFFJRBJf@R^Jz@DzDPtIb@fTnAdPz@x@CP?NDB@DBhE^xc@~BpX|AxPz@jCNbx@jEfTdAvIh@pThAx[|AtQ`AjLd@nR`AlTdAjMp@bBBl@C@A@ETQZGL?XJHHFLBLf@Bp@L~S~@|EX|Or@vU`AjIZxERzCP|DTh@?bBYLGXEPBLHNN@BfA`@p@DxQ`AtJf@~Pp@|Px@pAFfLd@fDLdGH`KAzD?vQIrMMtB?rYS|EElAEHIRERFHP?TCHCBA`@?r@?hADpELhHRpJJvDN|HFb@T|@Xd@r@x@p@f@\\\\NjBf@r@Rx@`@~@n@n@nAPv@P`BXjETtAJTLVnA|Aj@j@`@ZD?NLFJ@NAF?BFLv@nCjC|HdGdRr@lC`@fAXh@`@^rEdDbJxGzDrCfDjCvBtBpElE`C~Bl@h@~Az@fFhC~EfCnG~Cp@RdAVvBZb@Nf@VdB~@dEtB`@f@NXtBjGx@jCo@Dk@RiErBSNCF?LJ^dDdHlArCl@hBXpAtClLRj@j@|AJd@Dj@Ln@Xn@T\\\\n@n@NRlBpEtAfDP`@^z@X^v@j@xCfCvKpNjD`Ex@z@dHtFjAp@pAl@nExBhAp@l@b@`@d@fAzA|@x@z@^rDfAhBb@bF`AjFx@rCb@vDd@hGr@fI`AxFd@pBBx@BhBNnABzDD~BObDg@tBe@rAUrC[hF_@vJWhISv@BpAHrABlACvC[dI{@dNkA|EUd@i@r@IzAGI[WcAC]C{@IeBAaBCIIGAVHpDAz@W~Bs@zXBv@MfD@n@@VIx@\"\n" +
            "      },\n" +
            "      \"summary\": \"Gandhinagar - Ahmedabad Rd\",\n" +
            "      \"warnings\": [\n" +
            "        \n" +
            "      ],\n" +
            "      \"waypoint_order\": [\n" +
            "        \n" +
            "      ]\n" +
            "    }\n" +
            "  ],\n" +
            "  \"status\": \"OK\"\n" +
            "}";
}

