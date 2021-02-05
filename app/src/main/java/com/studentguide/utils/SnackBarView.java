package com.studentguide.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.studentguide.R;


public class SnackBarView {
    public void snackBarShow(Context context, String text) {
        Snackbar snackbar = Snackbar.make(((Activity) context).findViewById(android.R.id.content), text, Snackbar.LENGTH_LONG);
        //snackbar.setActionTextColor(R.color.profileDivider);
        View view = snackbar.getView();
        view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
        TextView textView = (TextView) view.findViewById(R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(context, R.color.white_color));
        snackbar.show();
    }

    public void snackBarShowRed(Context context, String text) {
        Snackbar snackbar = Snackbar.make(((Activity) context).findViewById(android.R.id.content), text, Snackbar.LENGTH_LONG);
        //snackbar.setActionTextColor(R.color.profileDivider);
        View view = snackbar.getView();
        view.setBackgroundColor(ContextCompat.getColor(context, R.color.color_EC1D24));
        TextView textView = (TextView) view.findViewById(R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(context, R.color.white_color));
        snackbar.show();
    }

    public void snackBarShowGreen(Context context, String text) {
        Snackbar snackbar = Snackbar.make(((Activity) context).findViewById(android.R.id.content), text, Snackbar.LENGTH_LONG);
        //snackbar.setActionTextColor(R.color.profileDivider);
        View view = snackbar.getView();
        view.setBackgroundColor(ContextCompat.getColor(context, R.color.color_41C216));
        TextView textView = (TextView) view.findViewById(R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(context, R.color.white_color));
        snackbar.show();
    }

    public void snackBarShowWithDuration(Context context, String text, int duration) {
        Snackbar snackbar = Snackbar.make(((Activity) context).findViewById(android.R.id.content), text, duration);
        //snackbar.setActionTextColor(R.color.profileDivider);
        View view = snackbar.getView();
        view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
        TextView textView = (TextView) view.findViewById(R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(context, R.color.white_color));
        snackbar.show();
    }
}
