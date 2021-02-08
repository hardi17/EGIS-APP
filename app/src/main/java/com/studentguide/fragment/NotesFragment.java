package com.studentguide.fragment;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.studentguide.R;
import com.studentguide.adapter.TravelSignalWasteGuideAdapter;
import com.studentguide.databinding.FragmentNotesBinding;
import com.studentguide.home.CurrencyActivity;

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

        initView();

        return binding.getRoot();
    }

    private void initView() {
        binding.rcvNote.setLayoutManager(new LinearLayoutManager(activity));
//        adapter = new TravelSignalWasteGuideAdapter(activity, false, false, false, true);
//        binding.rcvNote.setAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
