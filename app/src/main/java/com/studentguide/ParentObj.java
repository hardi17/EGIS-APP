package com.studentguide;

import android.content.Context;

import com.studentguide.utils.FieldValidation;
import com.studentguide.utils.NetworkConnectivity;
import com.studentguide.utils.SnackBarView;
import com.studentguide.utils.ToastView;

public class ParentObj {

    private Context context;
    public static ParentObj parent_obj;

    public float density, displayWidth;

    public NetworkConnectivity networkConnectivity;
    public static ToastView toastView;
    public static FieldValidation validation;
    public static SnackBarView snackBarView;

    public static ParentObj getInstance() {
        if (parent_obj == null) {
            parent_obj = new ParentObj();
        }
        return parent_obj;
    }

    void create_obj(Context context) {
        this.context = context;

        networkConnectivity = new NetworkConnectivity(context);

        toastView = new ToastView(context);

        validation = new FieldValidation();

        snackBarView = new SnackBarView();

        density = context.getResources().getDisplayMetrics().density;
    }
}
