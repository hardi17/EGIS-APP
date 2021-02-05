package com.studentguide.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastView {
    private Context context;

    public ToastView(Context context) {
        this.context = context;
    }

    public void showToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public void showToast(String msg, int duration) {
        Toast.makeText(context, msg, duration).show();
    }
}
