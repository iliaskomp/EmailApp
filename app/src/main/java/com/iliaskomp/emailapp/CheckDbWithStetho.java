package com.iliaskomp.emailapp;


import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by IliasKomp on 11/06/17.
 */

public class CheckDbWithStetho extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
