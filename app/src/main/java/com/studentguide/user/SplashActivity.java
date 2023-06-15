package com.studentguide.user;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.studentguide.R;
import com.studentguide.databinding.ActivityMainBinding;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class SplashActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private final int SPLASH_TIME = 1000;

    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        ButterKnife.bind(this);

        startAnimation();
    }


    private void startAnimation() {
        Animation animZoomIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in);
        animZoomIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startHandler();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        binding.ivLogo.startAnimation(animZoomIn);
    }

    private void startHandler() {
        handler.postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, IntroActivity.class);
            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }, SPLASH_TIME);
    }
}