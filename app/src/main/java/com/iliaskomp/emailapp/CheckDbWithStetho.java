package com.iliaskomp.emailapp;


import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * For testing purposes.
 * Stetho is used to check the database inside the smartphone.
 */
public class CheckDbWithStetho extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
