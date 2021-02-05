package com.studentguide.utils.network;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import com.studentguide.utils.Logger;
import com.studentguide.utils.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by WINDOWS-D20 on 17-7-2017.
 */
public class LocationAddressAsyncUtils extends AsyncTask<Object, Object, ArrayList<String>> {
    private Context context;
    private double
            latitude,
            longitude;
    private ServiceDoneListener listener;

    public LocationAddressAsyncUtils(Context context, double latitude, double longitude, ServiceDoneListener listener) {

        /**
         * How to cancel this async task?
         *
         * if (getAddressAsync != null) {
         *   if (getAddressAsync.getStatus() != AsyncTask.Status.FINISHED) {
         *       getAddressAsync.cancel(true);
         *   }
         * }
         */

        this.context = context;
        this.latitude = latitude;
        this.longitude = longitude;
        this.listener = listener;
    }

    @Override
    protected ArrayList<String> doInBackground(Object... params) {

        ArrayList<String> dataList = new ArrayList<>();

        try {
            Geocoder geocoder = new Geocoder(context, Locale.US/*Locale.getDefault()*/);
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);

                String
                        shortAddress = "",
                        longAddress = "",
                        city = address.getLocality(),
                        countryName = address.getCountryName(),
                        countryCode = address.getCountryCode(); // IN, US etc


                // E.g. Gandhinagar, Gujarat, India
                shortAddress = (StringUtils.isNotEmpty(address.getLocality()) ? address.getLocality() + ", " : "") +
                        (StringUtils.isNotEmpty(address.getAdminArea()) ? address.getAdminArea() + ", " : "") +
                        (StringUtils.isNotEmpty(address.getCountryName()) ? address.getCountryName() : "");

                // Detailed Address Lines
                int addressLineCount = address.getMaxAddressLineIndex();
                if (addressLineCount > 0) {
                    for (int i = 0; i <= addressLineCount; i++) {
                        if (i == addressLineCount) {
                            longAddress += (StringUtils.isNotEmpty(address.getAddressLine(i)) ? address.getAddressLine(i) : "");
                        } else {
                            longAddress += (StringUtils.isNotEmpty(address.getAddressLine(i)) ? address.getAddressLine(i) + ", " : "");
                        }
                    }
                }

                dataList.add(shortAddress);
                dataList.add(longAddress);
                dataList.add(city);
                dataList.add(countryName);
                dataList.add(countryCode);
            }
        } catch (IOException e) {
            Logger.e("tag"+ e.getMessage());
        }

        return dataList;
    }

    @Override
    protected void onPostExecute(ArrayList<String> dataList) {
        super.onPostExecute(dataList);

        if (!dataList.isEmpty()) {
            listener.onDone(
                    dataList.get(0),  // shortAddress // Gandhinagar, Gujarat, India
                    dataList.get(1),  // longAddress  // Detailed Address Lines
                    dataList.get(2),  // city         // Gandhinagar
                    dataList.get(3),  // countryName  // India
                    dataList.get(4)   // countryCode  // IN, US etc
            );
        }
    }

    public interface ServiceDoneListener {
        void onDone(
                String shortAddress,// Gandhinagar, Gujarat, India
                String longAddress, // Detailed Address Lines
                String city,        // Gandhinagar
                String countryName, // India
                String countryCode  // IN, US etc
        );
    }
}
