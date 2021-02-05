package com.studentguide.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;

import com.studentguide.R;
import com.studentguide.adapter.TravelSignalWasteGuideAdapter;
import com.studentguide.databinding.ActivityGuideBinding;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class TrafficSignalsWasteGuideActivity extends AppCompatActivity {

    ActivityGuideBinding binding;

    TravelSignalWasteGuideAdapter adapter;

    boolean isTraffic,
            isWaste;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_guide);
        ButterKnife.bind(this);

        getIntentData();
        initView();
        setRecyclerView();
    }

    private void getIntentData() {
        if (getIntent() != null) {
            isTraffic = getIntent().getBooleanExtra("isTraffic", false);
            isWaste = getIntent().getBooleanExtra("isWaste", false);
        }
    }

    private void setRecyclerView() {
        binding.rcvTravelSignals.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TravelSignalWasteGuideAdapter(this, isTraffic, isWaste, false, false);
        binding.rcvTravelSignals.setAdapter(adapter);
    }

    private void initView() {
        if (isTraffic) {
            binding.toolbar.txtTitle.setText(R.string._trafficSignals);
        } else {
            binding.toolbar.txtTitle.setText(R.string._wasteManage);
        }
    }

    @OnClick(R.id.iv_back)
    public void goToBack() {
        onBackPressed();
    }

    @OnClick(R.id.tv_checkKnowledge)
    public void checkYourknowledge() {
        startActivity(new Intent(this, QuestionAnsActivity.class));
    }


}
