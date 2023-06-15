package com.studentguide.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.studentguide.R;
import com.studentguide.databinding.ActivityQuestionAnsBinding;
import com.studentguide.fragment.QueAnsFragment;

import butterknife.ButterKnife;

public class QuestionAnsActivity extends AppCompatActivity {

    private int offset = 1;
    boolean isTraffic, isWaste;

    ActivityQuestionAnsBinding binding;
    public Integer score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_question_ans);
        ButterKnife.bind(this);

        getIntentData();
        addFragment();
    }

    private void addFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.que_ans_activity, QueAnsFragment.newInstance(this, offset, isTraffic, isWaste));

        //ft.addToBackStack(null);
        ft.commit();
    }

    private void getIntentData() {
        if (getIntent() != null) {
            isTraffic = getIntent().getBooleanExtra("isTraffic", false);
            isWaste = getIntent().getBooleanExtra("isWaste", false);
        }
    }
}
