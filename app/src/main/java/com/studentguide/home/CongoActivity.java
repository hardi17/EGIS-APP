package com.studentguide.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.studentguide.R;
import com.studentguide.databinding.ActivityCongoBinding;
import com.studentguide.utils.Logger;
import com.studentguide.utils.MyPref;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class CongoActivity extends AppCompatActivity {

    ActivityCongoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_congo);
        ButterKnife.bind(this);


        Integer score = getIntent().getIntExtra("score",0);

        String scoreText = score.toString()+"/5";
        binding.congoScore.setText(scoreText);

    }


    @OnClick(R.id.tvNext)
    public void goToHome(){
        startActivity(new Intent(this,NearByPlaceActivity.class));
        finish();
    }
}
