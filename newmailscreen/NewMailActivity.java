package com.iliaskomp.emailapp.newmailscreen;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.iliaskomp.emailapp.models.EmailToSend;
import com.iliaskomp.emailapp.models.EmailDB;
import com.iliaskomp.emailapp.models.InboxEmail;
import com.iliaskomp.emailapp.network.SendMail;
import com.iliaskomp.emailapp.R;

import java.util.UUID;

/**
 * Created by elias on 11/02/17.
 */

public class NewMailActivity extends AppCompatActivity{
    private static final String EXTRA_EMAIL_ID = "com.iliaskomp.email_id";

    private EditText mEditTextSender;
    private EditText mEditTextRecipient;
    private EditText mEditTextSubject;
    private EditText mEditTextMessage;
    private Button mButtonSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_mail);

        UUID emailId = (UUID) getIntent().getSerializableExtra(EXTRA_EMAIL_ID);

        mEditTextSender = (EditText) findViewById(R.id.editTextSender);
        mEditTextRecipient = (EditText) findViewById(R.id.editTextRecipient);
        mEditTextSubject = (EditText) findViewById(R.id.editTextSubject);
        mEditTextMessage = (EditText) findViewById(R.id.editTextMessage);
        mButtonSend = (Button) findViewById(R.id.buttonSend);
        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmail();
            }
        });

        // If emailId is not null, it means that the intent passed an id,
        // meaning the source was from reading the email, ergo replying to email
        if (emailId != null) {
            InboxEmail email = EmailDB.getEmailFromId(emailId);
            mEditTextSender.setText(email.getRecipient());
            mEditTextRecipient.setText(email.getSender());
            mEditTextSubject.setText("Re:" + email.getSubject());
        }
//        populateDataForTestPurposes();
    }

    private void sendEmail() {
        //Getting content for emailToSend
        String recipient = mEditTextRecipient.getText().toString().trim();
        String subject = mEditTextSubject.getText().toString().trim();
        String message = mEditTextMessage.getText().toString().trim();

        //Create SendMail object
        EmailToSend emailToSend = new EmailToSend(recipient, subject, message);
        SendMail sm = new SendMail(NewMailActivity.this, emailToSend);
        sm.execute();
    }

    public static Intent newIntent(Context contextPackage) {
        return new Intent(contextPackage, NewMailActivity.class);
    }

    public static Intent newIntent(Context contextPackage, UUID emailId) {
        Intent intent = new Intent(contextPackage, NewMailActivity.class);
        intent.putExtra(EXTRA_EMAIL_ID, emailId);
        return intent;
    }

//    private void populateDataForTestPurposes() {
//        mEditTextSender.setText(Config.EMAIL);
//        mEditTextRecipient.setText(Config.EMAIL);
//        mEditTextSubject.setText("email test subject");
//        mEditTextMessage.setText("email test message");
//    }
}
