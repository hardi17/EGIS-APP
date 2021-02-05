package com.studentguide.utils;

import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */

public class FieldValidation {

    // Check Edittext is null or not
    public boolean isEditTextNull(EditText editText) {
        if (editText.getText().length() > 0) {
            return false;
        } else {
            return true;
        }
    }

    // Check Edittext is null or not
    public boolean isTextNull(TextView textView) {
        if (textView.getText().length() > 0) {
            return false;
        } else {
            return true;
        }
    }

    // Email valid
    public boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches();
    }

    // Check confirm password
    public boolean isPasswordConfirm(EditText pass, EditText conpass) {
        if ((pass.getText().toString().trim()).equals(conpass.getText().toString().trim())) {
            return true;
        } else {
            return false;
        }
    }

    //Validation for email address
    public boolean isValidEmail(String email) {
        //for restrict special chracter
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        //allow special character
      //  String EMAIL_PATTERN = "^[\\w!#%&'*+/=?`{|}(_).~^-]+(?:\\.[\\w!#%&'*+/=?`{|}(_).~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }


}
