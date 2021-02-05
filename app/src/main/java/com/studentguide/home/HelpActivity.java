package com.studentguide.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;

import com.studentguide.R;
import com.studentguide.databinding.ActivityHelpBinding;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class HelpActivity extends AppCompatActivity {

    ActivityHelpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_help);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        binding.toolbar.txtTitle.setText(getString(R.string._help));
    }


    @OnClick(R.id.iv_back)
    public void goToBack(){
        startActivity(new Intent(this, HomeActivity.class));
    }

    @OnClick(R.id.tv_helpActivity_aboutUs)
    public void openAboutScreen() {
        startActivity(new Intent(this, WebViewActivity.class)
                .putExtra("openFrom", "About"));
    }

    @OnClick(R.id.tv_helpActivity_privacy)
    public void openPrivacyScreen() {
        startActivity(new Intent(this, WebViewActivity.class)
                .putExtra("openFrom", "Privacy"));
    }

    @OnClick(R.id.tv_helpActivity_termsOfService)
    public void openTermScreen() {
        startActivity(new Intent(this, WebViewActivity.class)
                .putExtra("openFrom", "Terms"));
    }


}
