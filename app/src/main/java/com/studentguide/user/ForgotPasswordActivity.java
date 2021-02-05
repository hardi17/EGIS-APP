package com.studentguide.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.studentguide.ParentObj;
import com.studentguide.R;
import com.studentguide.databinding.ActivityForgotPasswordBinding;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ForgotPasswordActivity extends AppCompatActivity {

    ActivityForgotPasswordBinding binding;

    boolean isEmailSend = false,
            isVerifyEmail = false;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password);
        ButterKnife.bind(this);

        getIntentData();
        initView();

        mAuth = FirebaseAuth.getInstance();
    }

    private void getIntentData() {
        isEmailSend = getIntent().getBooleanExtra("isEmailSend", false);
//        isVerifyEmail = getIntent().getBooleanExtra("isVerifyEmail", false);
    }

    private void initView() {
        if (isEmailSend) {
            binding.btnNext.setText(R.string.go_back);
            binding.tvForgotPasswordEmailAddress.setText("Verification Link Sent");
//            binding.etForgotPasswordEmailAddress.setHint(R.string._enterVerifyCode);
            binding.etForgotPasswordEmailAddress.setVisibility(View.GONE);
            binding.llEmailAdd.setVisibility(View.VISIBLE);
            binding.llChangePwsd.setVisibility(View.GONE);
        } /*else if (isVerifyEmail) {
            binding.btnNext.setText(R.string._change_password);
            binding.llChangePwsd.setVisibility(View.VISIBLE);
            binding.llEmailAdd.setVisibility(View.GONE);
        } */else {
            binding.btnNext.setText(R.string.next);
            binding.tvForgotPasswordEmailAddress.setText(R.string.email_address);
            binding.etForgotPasswordEmailAddress.setHint(R.string._enter_email);
            binding.llEmailAdd.setVisibility(View.VISIBLE);
            binding.llChangePwsd.setVisibility(View.GONE);
        }

        binding.toolbar.txtTitle.setText(getString(R.string.forgot_password));
    }

    @OnClick(R.id.iv_back)
    public void goToBack() {
        onBackPressed();
    }

    @OnClick(R.id.btn_next)
    public void sendEmail() {
        if (isEmailSend) {
            startActivity(new Intent(this, LoginActivity.class)
                    .putExtra("isEmailSend", false));
        } /*else if (isVerifyEmail) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }*/ else {
            if(validate()) {
                String email = binding.etForgotPasswordEmailAddress.getText().toString();

                mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        if (task.getResult().getSignInMethods().isEmpty()) {
                            ParentObj.snackBarView.snackBarShowRed(ForgotPasswordActivity.this, "User doesn't exist!");
                        } else {
                            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    ParentObj.snackBarView.snackBarShowGreen(ForgotPasswordActivity.this,"Check your email to reset password.");
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            startActivity(new Intent(ForgotPasswordActivity.this, ForgotPasswordActivity.class)
                                                    .putExtra("isEmailSend", true));
                                        }
                                    }, 2000);
                                }
                            });
                        }
                    }
                });
            }
        }
    }

    private boolean validate() {

        if (binding.etForgotPasswordEmailAddress.getText().toString().equals("")) {
            ParentObj.snackBarView.snackBarShowRed(this, getString(R.string.please_enter_your_email_address));
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.etForgotPasswordEmailAddress.getText().toString()).matches()) {
            ParentObj.snackBarView.snackBarShowRed(this, getString(R.string.please_enter_valid_email_address));
            return false;
        }

        return true;
    }


}
