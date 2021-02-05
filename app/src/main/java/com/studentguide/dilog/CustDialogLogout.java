package com.studentguide.dilog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatDialog;
import androidx.databinding.DataBindingUtil;

import com.studentguide.R;
import com.studentguide.databinding.DialougeLogoutBinding;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class CustDialogLogout extends AppCompatDialog {

    Context context;

    CustomClickListener listener;

    DialougeLogoutBinding mBinding;


    public CustDialogLogout(Context context, CustomClickListener listener) {
        super(context);
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialouge_logout, null, false);
        setContentView(mBinding.getRoot());
        setCancelable(true);
        ButterKnife.bind(this);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.gravity = Gravity.CENTER;
        getWindow().setAttributes(lp);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @OnClick(R.id.tv_dialouge_yes)
    public void yesLogout() {
        listener.onButtonClick();
    }

    @OnClick(R.id.tv_dialouge_cancel)
    public void noLogout() {
        onBackPressed();
    }

    public interface CustomClickListener {
        void onButtonClick();
    }
}