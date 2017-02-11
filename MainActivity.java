package com.iliaskomp.emailapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {



    private Button mButtonNewMail;
    private Button mButtonInbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButtonNewMail = (Button) findViewById(R.id.buttonNewEmail);
        mButtonNewMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, NewMailActivity.class);
                startActivity(i);
            }
        });

        mButtonInbox = (Button) findViewById(R.id.buttonInbox);
        mButtonInbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, InboxActivity.class);
                startActivity(i);
            }
        });



    }

}
