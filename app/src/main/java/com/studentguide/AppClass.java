package com.studentguide;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

public class AppClass extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ParentObj.getInstance().create_obj(getApplicationContext());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
