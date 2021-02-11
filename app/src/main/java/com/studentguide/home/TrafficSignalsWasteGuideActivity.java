package com.studentguide.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.studentguide.ParentObj;
import com.studentguide.R;
import com.studentguide.adapter.TravelSignalWasteGuideAdapter;
import com.studentguide.databinding.ActivityGuideBinding;
import com.studentguide.models.ModelTrafficSignal;
import com.studentguide.models.ModelWasteManagement;
import com.studentguide.utils.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class TrafficSignalsWasteGuideActivity extends AppCompatActivity {

    ActivityGuideBinding binding;

    TravelSignalWasteGuideAdapter adapter;

    boolean isTraffic,
            isWaste;

    //Firebase
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    //
    List<ModelTrafficSignal> modelTrafficSignalList;
    List<ModelWasteManagement> modelWasteManagementList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_guide);
        ButterKnife.bind(this);
        //
        mDatabase = FirebaseDatabase.getInstance();

        getIntentData();
        initView();
    }

    private void getIntentData() {
        if (getIntent() != null) {
            isTraffic = getIntent().getBooleanExtra("isTraffic", false);
            isWaste = getIntent().getBooleanExtra("isWaste", false);
        }
    }

    private void setRecyclerView() {
        binding.rcvTravelSignals.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TravelSignalWasteGuideAdapter(this, isTraffic, isWaste, false, false,modelTrafficSignalList,modelWasteManagementList,null);
        binding.rcvTravelSignals.setAdapter(adapter);
    }

    private void initView() {
        if (isTraffic) {
            binding.toolbar.txtTitle.setText(R.string._trafficSignals);
            mReference = mDatabase.getReference("trafficGuide");
            //
            modelTrafficSignalList = new ArrayList<>();
            mReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    modelTrafficSignalList.clear();
                    for(DataSnapshot trafficSignalSnapshot: snapshot.getChildren()){
                        ModelTrafficSignal model = trafficSignalSnapshot.getValue(ModelTrafficSignal.class);
                        modelTrafficSignalList.add(model);
                    }
                    setRecyclerView();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    ParentObj.snackBarView.snackBarShowRed(TrafficSignalsWasteGuideActivity.this,error.getMessage());
                }
            });
        } else {
            isWaste = true;
            binding.toolbar.txtTitle.setText(R.string._wasteManage);
            mReference = mDatabase.getReference("wasteManagment");
            //
            modelWasteManagementList = new ArrayList<>();
            mReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    modelWasteManagementList.clear();
                    for(DataSnapshot wasteManagmentSnapshot: snapshot.getChildren()){
                        ModelWasteManagement model = wasteManagmentSnapshot.getValue(ModelWasteManagement.class);
                        modelWasteManagementList.add(model);
                    }
                    setRecyclerView();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    ParentObj.snackBarView.snackBarShowRed(TrafficSignalsWasteGuideActivity.this,error.getMessage());
                }
            });
        }
    }

    @OnClick(R.id.iv_back)
    public void goToBack() {
        onBackPressed();
    }

    @OnClick(R.id.tv_checkKnowledge)
    public void checkYourknowledge() {
        startActivity(new Intent(this, QuestionAnsActivity.class).putExtra("isTraffic",isTraffic).putExtra("isWaste",isWaste));
    }


}
