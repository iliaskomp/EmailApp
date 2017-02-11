package com.iliaskomp.emailapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

/**
 * Created by iliaskomp on 11/02/17.
 */

public class InboxActivity extends AppCompatActivity {
    private Button mButtonNewEmail;
    private Button mButtonFetchEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

    }

    private void fetchMail() {
        FetchMail fm = new FetchMail(getApplicationContext(), "pop.gmail.com", "pop3");
        fm.execute();
    }
}
