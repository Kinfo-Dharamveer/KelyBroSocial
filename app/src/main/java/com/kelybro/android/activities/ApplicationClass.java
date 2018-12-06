package com.kelybro.android.activities;

import android.app.Application;

import com.orhanobut.hawk.Hawk;

public class ApplicationClass extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        Hawk.init(getApplicationContext()).build();

    }
}
