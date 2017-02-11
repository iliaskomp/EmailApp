package com.iliaskomp.emailapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {


    private EditText editTextSender;
    private EditText editTextMail;
    private EditText editTextSubject;
    private EditText editTextMessage;

    private Button buttonSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //init
        editTextSender = (EditText) findViewById(R.id.editTextSender);
        editTextMail = (EditText) findViewById(R.id.editTextEmail);
        editTextSubject = (EditText) findViewById(R.id.editTextSubject);
        editTextMessage = (EditText) findViewById(R.id.editTextMessage);
        buttonSend = (Button) findViewById(R.id.buttonSend);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmail();
            }
        });

        populateDataForTestPurposes();

    }

    private void sendEmail() {
//        //Getting content for email
//        String recipient = editTextMail.getText().toString().trim();
//        String subject = editTextSubject.getText().toString().trim();
//        String message = editTextMessage.getText().toString().trim();
//
//        //Create SendMail object
//        Email email = new Email(recipient, subject, message);
//        SendMail sm = new SendMail(this, email);
//
//        //Executing sendmail to send mail
//        sm.execute();

        FetchMail fm= new FetchMail(this, "pop.gmail.com", "pop3");
        fm.execute();

    }

    private void populateDataForTestPurposes() {
        editTextSender.setText(Config.EMAIL);
        editTextMail.setText(Config.EMAIL);
        editTextSubject.setText("email test subject2");
        editTextMessage.setText("email test message2");
    }
}
