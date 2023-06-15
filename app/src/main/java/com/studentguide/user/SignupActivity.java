package com.studentguide.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.AlertDialog;
import android.content.Intent;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import android.os.Handler;
import android.util.Patterns;
import android.view.View;

import com.bumptech.glide.Glide;
import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.studentguide.ParentObj;
import com.studentguide.R;
import com.studentguide.databinding.ActivitySignupBinding;
import com.studentguide.home.NearByPlaceActivity;
import com.studentguide.models.User;
import com.studentguide.utils.FinalVariables;
import com.studentguide.utils.KeyBoardUtils;
import com.studentguide.utils.Logger;
import com.studentguide.utils.imagecrop.BottomSheetGetImageVideoFragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignupActivity extends AppCompatActivity {

    ActivitySignupBinding binding;
    private Date last_selected_start_date,
            displayDate;
    private boolean isEditProfile = false;


    String selected_date = "";

    String firstName = "",
            lastName = "",
            dob = "",
            email = "",
            password = "";

    // Firebase variables declaration
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private FirebaseDatabase mDatabase;
    private DatabaseReference dbReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup);
        ButterKnife.bind(this);

        // Firebase Initialization
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        dbReference = mDatabase.getReference("users");

        getIntentData();
        initView();
        getCurrentDate();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        getIntentData();
        if (!isEditProfile) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                startActivity(new Intent(SignupActivity.this, NearByPlaceActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                                | Intent.FLAG_ACTIVITY_NEW_TASK));
                finish();
            }
        }
    }

    private void getIntentData() {
        isEditProfile = getIntent().getBooleanExtra("isEditProfile", false);
    }

    private void initView() {
        if (isEditProfile) {
            binding.toolbar.txtTitle.setText(R.string._editProfile);
            binding.tvSignupActivityLogin.setText(R.string._updateProfile);
            binding.editSignupActivityPwd.setVisibility(View.GONE);
            binding.txtSignupActivityPwd.setVisibility(View.GONE);
            binding.txtSignupActivityCmfpwd.setVisibility(View.GONE);
            binding.editSignupActivityCmfpwd.setVisibility(View.GONE);
            //
            dbReference.child(mCurrentUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    String Name[] = user.getFullName().split("\\s");
                    binding.editSignupActivityFirstName.setText(Name[0]);
                    binding.editSignupActivityLastName.setText(Name[1]);
                    binding.txtSignupActivityDateOfBirth.setText(user.getDob());
                    binding.editSignupActivityEmail.setText(user.getEmail());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            binding.toolbar.txtTitle.setText(R.string._signup);
            binding.tvSignupActivityLogin.setText(R.string.create_An_Account);
        }
    }

    private void getCurrentDate() {
        //get current or last selected date
        if (last_selected_start_date == null) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.YEAR, -19); // to get previous year add -1
            last_selected_start_date = cal.getTime();

            Calendar cal1 = Calendar.getInstance();
            cal1.add(Calendar.YEAR, -18); // to get previous year add -1
            displayDate = cal1.getTime();

            Logger.d("BEFORE 18 YEAR OLD =====>" + last_selected_start_date);
            Logger.d("BEFORE 19 YEAR OLD =====>" + displayDate);


        }
    }

    @OnClick(R.id.iv_back)
    public void goToBack() {
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

    @OnClick(R.id.tv_SignupActivity_login)
    public void createAccount() {
        if (isEditProfile) {
            if (updateValidate()) {
                //
                firstName = binding.editSignupActivityFirstName.getText().toString();
                lastName = binding.editSignupActivityLastName.getText().toString();
                dob = binding.txtSignupActivityDateOfBirth.getText().toString();
                email = binding.editSignupActivityEmail.getText().toString();


                String name = firstName + " " + lastName;

                Map<String, Object> data = new HashMap<>();
                data.put("fullName", name);
                data.put("dob", dob);
                data.put("email", email);
                dbReference.child(mCurrentUser.getUid()).setValue(data);

                ParentObj.snackBarView.snackBarShow(SignupActivity.this, "User Updated!");

                startActivity(new Intent(SignupActivity.this, NearByPlaceActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                                | Intent.FLAG_ACTIVITY_NEW_TASK));
                finish();
            }
        } else {
            if (validate()) {
                //
                firstName = binding.editSignupActivityFirstName.getText().toString();
                lastName = binding.editSignupActivityLastName.getText().toString();
                dob = binding.txtSignupActivityDateOfBirth.getText().toString();
                email = binding.editSignupActivityEmail.getText().toString();
                password = binding.editSignupActivityPwd.getText().toString();

                //
                createUserAccount(firstName, lastName, dob, email, password);
            }
        }
    }

    @OnClick(R.id.txt_SignupActivity_dateOfBirth)
    public void getDateOfBirth() {
        KeyBoardUtils.closeSoftKeyboard(this);

        new SingleDateAndTimePickerDialog.Builder(this)
                .bottomSheet()
                .curved()
                .displayHours(false)
                .displayMinutes(false)
                .displayDays(false)
                .displayMonthNumbers(false)
                .displayMonth(true)
                .displayDaysOfMonth(true)
                .displayYears(true)
                .defaultDate(last_selected_start_date)
                .maxDateRange(displayDate)
                .displayListener(new SingleDateAndTimePickerDialog.DisplayListener() {
                    @Override
                    public void onDisplayed(SingleDateAndTimePicker picker) {
                        //retrieve the SingleDateAndTimePicker
                        picker.setDefaultDate(last_selected_start_date);
                        picker.setDayFormatter(new SimpleDateFormat("MM dd yyyy", Locale.US));
                    }
                })
                .mainColor(getResources().getColor(R.color.color_000000))
                .listener(new SingleDateAndTimePickerDialog.Listener() {
                    @Override
                    public void onDateSelected(Date date) {

                        // last_selected_start_date = date;

                        Logger.d("onDateSelected" + "date:" + date);
                        DateFormat dateFormat = new SimpleDateFormat("MM dd yyyy", Locale.US);
                        DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

                        String formattedDate = dateFormat.format(date);
                        selected_date = formattedDate;

                        binding.txtSignupActivityDateOfBirth.setText(dateFormat1.format(date)); // new added

                    }
                }).display();
    }

    private boolean validate() {

        if (binding.editSignupActivityFirstName.getText().toString().equals("")) {
            ParentObj.snackBarView.snackBarShowRed(this, getString(R.string.please_enter_your_first_name));
            return false;
        } else if (binding.editSignupActivityFirstName.getText().toString().contains(" ")) {
            ParentObj.snackBarView.snackBarShowRed(this, getString(R.string.please_enter_your_valid_first_name));
            return false;
        } else if (binding.editSignupActivityLastName.getText().toString().equals("")) {
            ParentObj.snackBarView.snackBarShowRed(this, getString(R.string.please_enter_your_last_name));
            return false;
        } else if (binding.editSignupActivityLastName.getText().toString().contains(" ")) {
            ParentObj.snackBarView.snackBarShowRed(this, getString(R.string.please_enter_your_valid_last_name));
            return false;
        } else if (ParentObj.validation.isTextNull(binding.txtSignupActivityDateOfBirth)) {
            ParentObj.snackBarView.snackBarShowRed(this, getResources().getString(R.string.please_enter_your_birthdate));
            return false;
        }/*else if (ParentObj.validation.isEditTextNull(binding.editSignupActivityPhoneNo)) {
            ParentObj.snackBarView.snackBarShowRed(this, getResources().getString(R.string.please_enter_your_phoneNo));
            return false;
        }*/ else if (binding.editSignupActivityEmail.getText().toString().equals("")) {
            ParentObj.snackBarView.snackBarShowRed(this, getString(R.string.please_enter_your_email_address));
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.editSignupActivityEmail.getText().toString()).matches()) {
            ParentObj.snackBarView.snackBarShowRed(this, getString(R.string.please_enter_valid_email_address));
            return false;
        } else if (binding.editSignupActivityPwd.getText().toString().equals("")) {
            ParentObj.snackBarView.snackBarShowRed(this, getString(R.string.please_enter_password));
            return false;
        } else if (binding.editSignupActivityPwd.getText().toString().length() < 6) {
            ParentObj.snackBarView.snackBarShowRed(this, getString(R.string.password_cannot_be_less_than_six_characters));
            return false;
        } else if (!binding.editSignupActivityPwd.getText().toString().equals(binding.editSignupActivityCmfpwd.getText().toString().trim())) {
            ParentObj.snackBarView.snackBarShowRed(this, getString(R.string.confirm_password_is_wrong));
            return false;
        }

        return true;
    }

    private boolean updateValidate() {

        if (binding.editSignupActivityFirstName.getText().toString().equals("")) {
            ParentObj.snackBarView.snackBarShowRed(this, getString(R.string.please_enter_your_first_name));
            return false;
        } else if (binding.editSignupActivityFirstName.getText().toString().contains(" ")) {
            ParentObj.snackBarView.snackBarShowRed(this, getString(R.string.please_enter_your_valid_first_name));
            return false;
        } else if (binding.editSignupActivityLastName.getText().toString().equals("")) {
            ParentObj.snackBarView.snackBarShowRed(this, getString(R.string.please_enter_your_last_name));
            return false;
        } else if (binding.editSignupActivityLastName.getText().toString().contains(" ")) {
            ParentObj.snackBarView.snackBarShowRed(this, getString(R.string.please_enter_your_valid_last_name));
            return false;
        } else if (ParentObj.validation.isTextNull(binding.txtSignupActivityDateOfBirth)) {
            ParentObj.snackBarView.snackBarShowRed(this, getResources().getString(R.string.please_enter_your_birthdate));
            return false;
        }/*else if (ParentObj.validation.isEditTextNull(binding.editSignupActivityPhoneNo)) {
            ParentObj.snackBarView.snackBarShowRed(this, getResources().getString(R.string.please_enter_your_phoneNo));
            return false;
        }*/ else if (binding.editSignupActivityEmail.getText().toString().equals("")) {
            ParentObj.snackBarView.snackBarShowRed(this, getString(R.string.please_enter_your_email_address));
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.editSignupActivityEmail.getText().toString()).matches()) {
            ParentObj.snackBarView.snackBarShowRed(this, getString(R.string.please_enter_valid_email_address));
            return false;
        }

        return true;
    }


    /*
     * Firebase Method for Signing-Up user using Email and Password
     * */
    private void createUserAccount(String firstName, String lastName, String dob, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String name = firstName + " " + lastName;
                    mCurrentUser = mAuth.getCurrentUser();
                    updateUserAccount(name, dob, email, mCurrentUser);
                } else {
                    ParentObj.snackBarView.snackBarShowRed(SignupActivity.this, task.getException().getMessage());
                }
            }
        });
    }

    /*
     * Method to update information of the user and push the user information into Firebase Realtime Database
     * */
    private void updateUserAccount(String name, String dob, String email, FirebaseUser mCurrentUser) {
        Map<String, Object> data = new HashMap<>();
        data.put("fullName", name);
        data.put("dob", dob);
        data.put("email", email);
        dbReference.child(mCurrentUser.getUid()).setValue(data);

        ParentObj.snackBarView.snackBarShow(SignupActivity.this, "User Registered!");

        startActivity(new Intent(SignupActivity.this, NearByPlaceActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_NEW_TASK));
        finish();
    }


}
