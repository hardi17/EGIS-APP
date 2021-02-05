package com.studentguide.home;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.studentguide.ParentObj;
import com.studentguide.R;
import com.studentguide.adapter.RoutesAdapter;
import com.studentguide.adapter.TravelOptionAdapter;
import com.studentguide.databinding.ActivityTravelBinding;
import com.studentguide.listener.OnRefreshTravelOptionListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class TravelActivity extends AppCompatActivity implements OnRefreshTravelOptionListener {

    ActivityTravelBinding binding;

    RoutesAdapter routesAdapter;
    private int mSelectedIndex = 0;
    public final int REQUEST_CODE_TO_GOOGLE_MAP_ACTIVTY = 98;
    private double
            lattitude = 0.0,
            longitude = 0.0,
            lat = 0.0,
            lng = 0.0;
    TravelOptionAdapter optionAdapter;
    private String country_code = "",
            area = "",
            broadcast_address = "";

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
        String[] travelOptions = getResources().getStringArray(R.array.travelOption);
        ArrayList<String> travelOptionsList = new ArrayList<>();
        Collections.addAll(travelOptionsList, travelOptions);

        binding.rcvTravelOptions.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        optionAdapter = new TravelOptionAdapter(this, travelOptionsList, this);
        binding.rcvTravelOptions.setAdapter(optionAdapter);

        binding.rcvRoutes.setLayoutManager(new LinearLayoutManager(this));
        routesAdapter = new RoutesAdapter(this, "Walk");
        binding.rcvRoutes.setAdapter(routesAdapter);
    }

    @OnClick(R.id.iv_back)
    public void goToBack() {
        onBackPressed();
    }

    @OnClick(R.id.iv_changeLocation)
    public void changeLocationFromTo() {
        String fromtText = "", toText = "";

        if (binding.tvTravelActivityFrom.getText().length() > 0 && binding.tvTravelActivityTo.getText().length() > 0) {
            fromtText = binding.tvTravelActivityFrom.getText().toString();
            toText = binding.tvTravelActivityTo.getText().toString();
        }

        binding.tvTravelActivityFrom.setText(toText);
        binding.tvTravelActivityTo.setText(fromtText);
    }

    @OnClick(R.id.tv_TravelActivity_from)
    public void goToMapFrom() {
        startActivityForResult(new Intent(TravelActivity.this, MapActivity.class)
                .putExtra("isFrom", true)
                .putExtra("isTo", false)
                .putExtra("lattitude", lat)
                .putExtra("longitude", lng), REQUEST_CODE_TO_GOOGLE_MAP_ACTIVTY);
    }

    @OnClick(R.id.tv_TravelActivity_to)
    public void goToMapTo() {
        startActivityForResult(new Intent(TravelActivity.this, MapActivity.class)
                .putExtra("isFrom", false)
                .putExtra("isTo", true)
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

    @Override
    public void onRefreshRoutes(String optionName) {
        routesAdapter.setItems(optionName);
        routesAdapter.notifyDataSetChanged();
    }
}
