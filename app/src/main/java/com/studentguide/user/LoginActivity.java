package com.studentguide.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.studentguide.ParentObj;
import com.studentguide.R;
import com.studentguide.databinding.ActivityLoginBinding;
import com.studentguide.home.HomeActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;

    String email = "",
            password = "";

    //Firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        ButterKnife.bind(this);
        //
        mAuth = FirebaseAuth.getInstance();

        initView();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            startActivity(new Intent(LoginActivity.this, HomeActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_NEW_TASK));
            finish();
        }
    }

    private void initView() {
        binding.toolbar.txtTitle.setText(R.string._login);
    }

    @OnClick(R.id.iv_back)
    public void redirectBack() {
            startActivity(new Intent(this, IntroActivity.class));
            finish();
        binding.toolbar.ivBack.setEnabled(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.toolbar.ivBack.setEnabled(true);
            }
        }, 1000);
    }

    @OnClick(R.id.tv_LoginActivity_login)
    public void rediterctHome() {
        if (validate()) {

            email = binding.editLoginActivityEmail.getText().toString();
            password = binding.editLoginActivityPwd.getText().toString();

            signInUser(email,password);
        }
    }


    @OnClick(R.id.tv_LoginActivity_forgotPass)
    public void redirectForgotPwd() {
        startActivity(new Intent(this, ForgotPasswordActivity.class));
    }

    @OnClick(R.id.txt_LoginActivity_signup)
    public void redirectSignup() {
        startActivity(new Intent(this, SignupActivity.class));
    }

    private boolean validate() {

        if (binding.editLoginActivityEmail.getText().toString().equals("")) {
            ParentObj.snackBarView.snackBarShowRed(this, getString(R.string.please_enter_your_email_address));
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.editLoginActivityEmail.getText().toString()).matches()) {
            ParentObj.snackBarView.snackBarShowRed(this, getString(R.string.please_enter_valid_email_address));
            return false;
        } else if (binding.editLoginActivityPwd.getText().toString().equals("")) {
            ParentObj.snackBarView.snackBarShowRed(this, getString(R.string.please_enter_password));
            return false;
        } else if (binding.editLoginActivityPwd.getText().toString().length() < 6) {
            ParentObj.snackBarView.snackBarShowRed(this, getString(R.string.password_cannot_be_less_than_six_characters));
            return false;
        }

        return true;
    }

    /*
    * Firebase method of Signing user to app
    * */
    private void signInUser(String email, String password){

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    ParentObj.snackBarView.snackBarShowGreen(LoginActivity.this,"Login Successful!");
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    | Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    | Intent.FLAG_ACTIVITY_NEW_TASK));
                }
                else{
                    ParentObj.snackBarView.snackBarShowRed(LoginActivity.this,task.getException().getMessage());
                }
            }
        });

    }
}
