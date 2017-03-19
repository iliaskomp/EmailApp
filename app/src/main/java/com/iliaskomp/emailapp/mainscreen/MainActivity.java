package com.iliaskomp.emailapp.mainscreen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.iliaskomp.emailapp.R;
import com.iliaskomp.emailapp.database.EmailDbSchema;
import com.iliaskomp.emailapp.inboxscreen.EmailListActivity;
import com.iliaskomp.emailapp.newmailscreen.NewMailActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button mButtonNewMail = (Button) findViewById(R.id.buttonNewEmail);
        mButtonNewMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, NewMailActivity.class);
                startActivity(i);
            }
        });

        Button mButtonInbox = (Button) findViewById(R.id.buttonInbox);
        mButtonInbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent i = new Intent(MainActivity.this, EmailListActivity.class);
//                startActivity(i);
                Intent intent = EmailListActivity.newIntent(MainActivity.this, EmailDbSchema.InboxTable.NAME);
                startActivity(intent);
            }
        });

        Button mButtonSent = (Button) findViewById(R.id.buttonSent);
        mButtonSent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = EmailListActivity.newIntent(MainActivity.this, EmailDbSchema.SentTable.NAME);
                startActivity(intent);

            }
        });


    }

}
