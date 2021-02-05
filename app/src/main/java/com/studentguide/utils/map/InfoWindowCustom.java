package com.studentguide.utils.map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.studentguide.R;

import java.util.ArrayList;

public class InfoWindowCustom implements GoogleMap.InfoWindowAdapter {

    private final ArrayList<String> markerPlaces;
    private Context context;
    private LayoutInflater inflater;

    public InfoWindowCustom(Context context, ArrayList<String> markerPlaces) {
        this.context = context;
        this.markerPlaces = markerPlaces;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.info_windows_map, null);
        TextView infoWindowsName = v.findViewById(R.id.info_windows_place_name);
        TextView infoWindowsTime = v.findViewById(R.id.info_windows_place_time);
        infoWindowsName.setText(marker.getTitle());
        if (marker.getSnippet() != null && marker.getSnippet().length() > 0) {
            infoWindowsTime.setText(marker.getSnippet());
        } else {
            infoWindowsTime.setVisibility(View.GONE);
        }
        return v;
    }
}

