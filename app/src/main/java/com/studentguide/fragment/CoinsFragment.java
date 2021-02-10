package com.studentguide.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.studentguide.R;
import com.studentguide.adapter.TravelSignalWasteGuideAdapter;
import com.studentguide.databinding.FragmentCoinsBinding;
import com.studentguide.home.CurrencyActivity;
import com.studentguide.models.CoinsNotesModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class CoinsFragment extends Fragment {

    private CurrencyActivity activity;
    Unbinder unbinder;
    FragmentCoinsBinding binding;

    TravelSignalWasteGuideAdapter adapter;
    //
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    List<CoinsNotesModel> coinsNotesModelList;

    public CoinsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        activity = (CurrencyActivity) getActivity();
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_coins, container, false);
        unbinder = ButterKnife.bind(activity);
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference("coins");

        initView();

        return  binding.getRoot();
    }

    private void initView() {
        coinsNotesModelList = new ArrayList<>();
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                coinsNotesModelList.clear();
                for(DataSnapshot coinSnapshot: snapshot.getChildren()){
                    CoinsNotesModel model = coinSnapshot.getValue(CoinsNotesModel.class);
                    coinsNotesModelList.add(model);
                }
                binding.rcvCoins.setLayoutManager(new LinearLayoutManager(activity));
                adapter = new TravelSignalWasteGuideAdapter(activity, false, false,false,true,null,null,coinsNotesModelList);
                binding.rcvCoins.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
