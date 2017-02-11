package com.iliaskomp.emailapp.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.iliaskomp.emailapp.Data.Config;
import com.iliaskomp.emailapp.Data.Email;
import com.iliaskomp.emailapp.Functionality.SendMail;
import com.iliaskomp.emailapp.R;

/**
 * Created by elias on 11/02/17.
 */

public class NewMailActivity extends AppCompatActivity{
    private EditText mEditTextSender;
    private EditText mEditTextMail;
    private EditText mEditTextSubject;
    private EditText mEditTextMessage;
    private Button mButtonSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_mail);

        //init
        mEditTextSender = (EditText) findViewById(R.id.editTextSender);
        mEditTextMail = (EditText) findViewById(R.id.editTextEmail);
        mEditTextSubject = (EditText) findViewById(R.id.editTextSubject);
        mEditTextMessage = (EditText) findViewById(R.id.editTextMessage);
        mButtonSend = (Button) findViewById(R.id.buttonSend);
        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmail();
            }
        });

        populateDataForTestPurposes();
    }

    private void sendEmail() {
        //Getting content for email
        String recipient = mEditTextMail.getText().toString().trim();
        String subject = mEditTextSubject.getText().toString().trim();
        String message = mEditTextMessage.getText().toString().trim();

        //Create SendMail object
        Email email = new Email(recipient, subject, message);
        SendMail sm = new SendMail(NewMailActivity.this, email);
        sm.execute();
    }

    private void populateDataForTestPurposes() {
        mEditTextSender.setText(Config.EMAIL);
        mEditTextMail.setText(Config.EMAIL);
        mEditTextSubject.setText("email test subject");
        mEditTextMessage.setText("email test message");
    }
}
