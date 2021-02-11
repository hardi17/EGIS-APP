package com.studentguide.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.studentguide.R;
import com.studentguide.adapter.OptionsAdapter;
import com.studentguide.databinding.FragmentQueAnsBinding;
import com.studentguide.home.CongoActivity;
import com.studentguide.home.QuestionAnsActivity;
import com.studentguide.models.ModelOptions;
import com.studentguide.models.ModelQuiz;
import com.studentguide.utils.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class QueAnsFragment extends Fragment {

    Unbinder unbinder;
    FragmentQueAnsBinding binding;
    QuestionAnsActivity activity;
    OptionsAdapter adapter;

    private int offset = 1;

    boolean isTraffic, isWaste;
    //
    FirebaseDatabase mDatabase;
    DatabaseReference mRef;
    List<ModelOptions> modelOptionsList;

    public QueAnsFragment() {
        // Required empty public constructor
    }

    public static QueAnsFragment newInstance(QuestionAnsActivity activity, int offset,boolean isTraffic,boolean isWaste) {
        Bundle args = new Bundle();
        args.putInt("offset", offset);
        args.putBoolean("isTraffic",isTraffic);
        args.putBoolean("isWaste",isWaste);
        QueAnsFragment fragment = new QueAnsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        activity = (QuestionAnsActivity) getActivity();
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_que_ans, container, false);
        unbinder = ButterKnife.bind(this, binding.getRoot());
        //ButterKnife.bind(activity);
        //
        mDatabase = FirebaseDatabase.getInstance();

        /*getArguments*/
        if (getArguments() != null) {

            offset = getArguments().getInt("offset");
            isTraffic = getArguments().getBoolean("isTraffic");
            isWaste = getArguments().getBoolean("isWaste");
        }

        if(isTraffic){
            mRef = mDatabase.getReference("trafficQuiz");
            Logger.d("isTraffic");
        } else if(isWaste){
            mRef = mDatabase.getReference("wasteQuiz");
            Logger.d("isWaste");
        } else{
            mRef = mDatabase.getReference("currencyQuiz");
            Logger.d("isCurrency");
        }

        modelOptionsList = new ArrayList<>();
        initView();

        return binding.getRoot();
    }

    private void setRecyclerView(){
        binding.rcvOptions.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.VERTICAL, false));
        adapter = new OptionsAdapter(activity,modelOptionsList);
        binding.rcvOptions.setAdapter(adapter);
    }

    private void initView() {
        setRecyclerView();
        //
        if(offset == 1) {
            mRef.child("que1").child("options").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    modelOptionsList.clear();
                    for (DataSnapshot optionsnap : snapshot.getChildren()) {
                        ModelOptions modelOptions = optionsnap.getValue(ModelOptions.class);
                        modelOptionsList.add(modelOptions);
                    }
                    setRecyclerView();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            //
            mRef.child("que1").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    ModelQuiz modelQuiz = snapshot.getValue(ModelQuiz.class);
                    binding.tvQuestion.setText(modelQuiz.getQuestion());
                    //
                    binding.tvTwoImg.setVisibility(View.GONE);
                    binding.tvThreeImg.setVisibility(View.GONE);
                    binding.tvFourImg.setVisibility(View.GONE);
                    Glide.with(getActivity()).load(modelQuiz.getQuestionImage()).into(binding.tvOneImg);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }else if(offset == 2){
            //
            mRef.child("que2").child("options").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    modelOptionsList.clear();
                    for(DataSnapshot optionsnap : snapshot.getChildren()){
                        ModelOptions modelOptions = optionsnap.getValue(ModelOptions.class);
                        modelOptionsList.add(modelOptions);
                    }
                    setRecyclerView();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            //
            mRef.child("que2").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    ModelQuiz modelQuiz = snapshot.getValue(ModelQuiz.class);
                    binding.tvQuestion.setText(modelQuiz.getQuestion());
                    //
                    binding.tvTwoImg.setVisibility(View.GONE);
                    binding.tvThreeImg.setVisibility(View.GONE);
                    binding.tvFourImg.setVisibility(View.GONE);
                    Glide.with(getActivity()).load(modelQuiz.getQuestionImage()).into(binding.tvOneImg);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else if(offset == 3){
            //
            mRef.child("que3").child("options").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    modelOptionsList.clear();
                    for(DataSnapshot optionsnap : snapshot.getChildren()){
                        ModelOptions modelOptions = optionsnap.getValue(ModelOptions.class);
                        modelOptionsList.add(modelOptions);
                    }
                    setRecyclerView();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            //
            mRef.child("que3").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    ModelQuiz modelQuiz = snapshot.getValue(ModelQuiz.class);
                    binding.tvQuestion.setText(modelQuiz.getQuestion());
                    //
                    binding.tvTwoImg.setVisibility(View.GONE);
                    binding.tvThreeImg.setVisibility(View.GONE);
                    binding.tvFourImg.setVisibility(View.GONE);
                    Glide.with(getActivity()).load(modelQuiz.getQuestionImage()).into(binding.tvOneImg);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else if(offset == 4){
            //
            mRef.child("que4").child("options").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    modelOptionsList.clear();
                    for(DataSnapshot optionsnap : snapshot.getChildren()){
                        ModelOptions modelOptions = optionsnap.getValue(ModelOptions.class);
                        modelOptionsList.add(modelOptions);
                    }
                    setRecyclerView();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            //
            mRef.child("que4").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    ModelQuiz modelQuiz = snapshot.getValue(ModelQuiz.class);
                    binding.tvQuestion.setText(modelQuiz.getQuestion());
                    //
                    binding.tvTwoImg.setVisibility(View.GONE);
                    binding.tvThreeImg.setVisibility(View.GONE);
                    binding.tvFourImg.setVisibility(View.GONE);
                    Glide.with(getActivity()).load(modelQuiz.getQuestionImage()).into(binding.tvOneImg);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        //
        if (offset == 5) {
            binding.tvNext.setText(getString(R.string.submit));
            binding.tvPrevious.setVisibility(View.INVISIBLE);
            //
            mRef.child("que5").child("options").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    modelOptionsList.clear();
                    for(DataSnapshot optionsnap : snapshot.getChildren()){
                        ModelOptions modelOptions = optionsnap.getValue(ModelOptions.class);
                        modelOptionsList.add(modelOptions);
                    }
                    setRecyclerView();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            //
            mRef.child("que5").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    ModelQuiz modelQuiz = snapshot.getValue(ModelQuiz.class);
                    binding.tvQuestion.setText(modelQuiz.getQuestion());
                    //
                    binding.tvTwoImg.setVisibility(View.GONE);
                    binding.tvThreeImg.setVisibility(View.GONE);
                    binding.tvFourImg.setVisibility(View.GONE);
                    Glide.with(getActivity()).load(modelQuiz.getQuestionImage()).into(binding.tvOneImg);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            binding.tvPrevious.setVisibility(View.VISIBLE);
            binding.tvNext.setText(getString(R.string.next));
        }
        binding.tvQuestionNo.setText(String.valueOf(offset));
    }


    @OnClick(R.id.tv_next)
    public void changeQuestion() {
        if (offset <= 4) {
            offset++;
            FragmentTransaction ft = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment, QueAnsFragment.newInstance(activity, offset,isTraffic,isWaste));
            ft.addToBackStack(null);
            ft.commit();
        } else {
            activity.finish();
            startActivity(new Intent(activity, CongoActivity.class));
        }
    }

    @OnClick(R.id.tv_previous)
    public void changePrevQuestion() {
        if (offset >= 2 && offset < 5) {
            offset--;
            FragmentTransaction ft = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment, QueAnsFragment.newInstance(activity, offset,isTraffic,isWaste));
            ft.addToBackStack(null);
            ft.commit();
        } else {
            activity.finish();
        }
    }


}
