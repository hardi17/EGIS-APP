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
import com.studentguide.databinding.FragmentNotesBinding;
import com.studentguide.home.CurrencyActivity;
import com.studentguide.models.CoinsNotesModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotesFragment extends Fragment {

    CurrencyActivity activity;
    FragmentNotesBinding binding;
    Unbinder unbinder;
    private TravelSignalWasteGuideAdapter adapter;

    //
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    List<CoinsNotesModel> coinsNotesModelList;

    public NotesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        activity = (CurrencyActivity) getActivity();
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_notes, container, false);
        unbinder = ButterKnife.bind(activity);
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference("notes");

        initView();

        return binding.getRoot();
    }

    private void initView() {
        coinsNotesModelList = new ArrayList<>();
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                coinsNotesModelList.clear();
                for(DataSnapshot noteSnapshot: snapshot.getChildren()){
                    CoinsNotesModel model = noteSnapshot.getValue(CoinsNotesModel.class);
                    coinsNotesModelList.add(model);
                }
                binding.rcvNote.setLayoutManager(new LinearLayoutManager(activity));
                adapter = new TravelSignalWasteGuideAdapter(activity, false, false, true, false,null,null,coinsNotesModelList);
                binding.rcvNote.setAdapter(adapter);
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
