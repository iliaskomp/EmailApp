package com.iliaskomp.emailapp.mainscreen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.iliaskomp.emailapp.R;
import com.iliaskomp.emailapp.database.email.EmailDbSchema;
import com.iliaskomp.emailapp.inboxscreen.EmailListActivity;
import com.iliaskomp.emailapp.newmailscreen.NewMailActivity;
import com.iliaskomp.emailapp.utils.EmailCredentials;

/**
 * The main activity.
 * Entry point for the application
 * Presents the home screen.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // retrieve user email/password (static dummy currently)
        final String email = EmailCredentials.EMAIL_SEND;
        final String password = EmailCredentials.PASSWORD_SEND;

        Button mButtonNewMail = (Button) findViewById(R.id.buttonNewEmail);
        mButtonNewMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = NewMailActivity.newIntent(MainActivity.this, null, email, password);
                startActivity(intent);
            }
        });

        Button mButtonInbox = (Button) findViewById(R.id.buttonInbox);
        mButtonInbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent i = new Intent(MainActivity.this, EmailListActivity.class);
//                startActivity(i);
                Intent intent = EmailListActivity.newIntent(MainActivity.this, EmailDbSchema.InboxTable.NAME, email, password);
                startActivity(intent);
            }
        });

        Button mButtonSent = (Button) findViewById(R.id.buttonSent);
        mButtonSent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = EmailListActivity.newIntent(MainActivity.this, EmailDbSchema.SentTable.NAME, email, password);
                startActivity(intent);

            }
        });


    }

}
