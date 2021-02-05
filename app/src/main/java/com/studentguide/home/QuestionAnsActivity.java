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

    private int offset=0;

    ActivityQuestionAnsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_question_ans);
        ButterKnife.bind(this);

        addFragment();
    }

    private void addFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.que_ans_activity, QueAnsFragment.newInstance(this,offset));
        //ft.addToBackStack(null);
        ft.commit();
    }
}
