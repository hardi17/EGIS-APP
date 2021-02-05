package com.studentguide.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.studentguide.R;
import com.studentguide.adapter.OptionsAdapter;
import com.studentguide.databinding.FragmentQueAnsBinding;
import com.studentguide.home.CongoActivity;
import com.studentguide.home.QuestionAnsActivity;

import java.util.ArrayList;
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

    private int offset = 0;

    public QueAnsFragment() {
        // Required empty public constructor
    }

    public static QueAnsFragment newInstance(QuestionAnsActivity activity, int offset) {
        Bundle args = new Bundle();
        args.putInt("offset", offset);
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

        /*getArguments*/
        if (getArguments() != null) {

            offset = getArguments().getInt("offset");
        }

        initView();

        return binding.getRoot();
    }

    private void initView() {
        adapter = new OptionsAdapter(activity);
        binding.rcvOptions.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.VERTICAL, false));
        binding.rcvOptions.setAdapter(adapter);

        if (offset == 5) {
            binding.tvNext.setText(getString(R.string.submit));
            binding.tvPrevious.setVisibility(View.INVISIBLE);
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
            ft.replace(R.id.fragment, QueAnsFragment.newInstance(activity, offset));
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
            ft.replace(R.id.fragment, QueAnsFragment.newInstance(activity, offset));
            ft.addToBackStack(null);
            ft.commit();
        } else {
            activity.finish();
        }
    }


}
