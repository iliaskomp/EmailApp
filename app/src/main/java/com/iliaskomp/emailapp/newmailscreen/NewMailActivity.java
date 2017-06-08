package com.iliaskomp.emailapp.newmailscreen;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.iliaskomp.emailapp.R;
import com.iliaskomp.emailapp.models.EmailModel;
import com.iliaskomp.emailapp.models.InboxDB;
import com.iliaskomp.emailapp.network.FetchMailUtils;
import com.iliaskomp.emailapp.network.SendMail;
import com.iliaskomp.emailapp.network.SendMailUtils;
import com.iliaskomp.emailapp.utils.EmailCredentials;

import java.util.Properties;
import java.util.UUID;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Created by elias on 11/02/17.
 */

public class NewMailActivity extends AppCompatActivity{
    private static final String EXTRA_EMAIL_ID = "com.iliaskomp.email_id";
    private static final String EXTRA_EMAIL_NAME = "com.iliaskomp.email_name";
    private static final String EXTRA_PASSWORD = "com.iliaskomp.password";

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
        final String emailName = getIntent().getStringExtra(EXTRA_EMAIL_NAME);
        final String password = getIntent().getStringExtra(EXTRA_PASSWORD);


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
            EmailModel email = InboxDB.get(this).getEmailFromId(emailId);
            mEditTextSender.setText(email.getRecipient());
            mEditTextRecipient.setText(email.getSender());
            mEditTextSubject.setText("Re:" + email.getSubject());
        }

        populateDataForTestPurposes(emailName);

    }

    private void sendEmail() {
        //Getting content for emailToSend
        String recipient = mEditTextRecipient.getText().toString().trim();
        String subject = mEditTextSubject.getText().toString().trim();
        String message = mEditTextMessage.getText().toString().trim();

        //Create SendMail object
        SendMail sm = new SendMail(NewMailActivity.this);
        MimeMessage mm = null;
        try {
            mm = createMessage(recipient, subject, message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        sm.execute(mm);
//        sm.execute(emailName, password, );
    }

    public static Intent newIntent(Context contextPackage, UUID emailId,  String email, String password) {
        Intent intent = new Intent(contextPackage, NewMailActivity.class);
        intent.putExtra(EXTRA_EMAIL_ID, emailId);
        intent.putExtra(EXTRA_EMAIL_NAME, email);
        intent.putExtra(EXTRA_PASSWORD, password);
        return intent;
    }

//    public static Intent newIntent(Context contextPackage, UUID emailId) {
//        Intent intent = new Intent(contextPackage, NewMailActivity.class);
//        intent.putExtra(EXTRA_EMAIL_ID, emailId);
//        return intent;
//    }

    private MimeMessage createMessage(String recipient, String subject, String message) throws MessagingException {
        Properties props = SendMailUtils.getProperties(EmailCredentials.EMAIL_SEND);
        MimeMessage mm = new MimeMessage(FetchMailUtils.getSentSession(EmailCredentials.EMAIL_SEND, EmailCredentials.PASSWORD_SEND, props));

        mm.setFrom(new InternetAddress(EmailCredentials.EMAIL_SEND));
        mm.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
        mm.setSubject(subject);
        mm.setText(message);

        return mm;
    }

    private void populateDataForTestPurposes(String emailName) {
        mEditTextSender.setText(emailName);
//        mEditTextRecipient.setText("fhcrypto@gmail.com");
        mEditTextSubject.setText("email test reply");
        mEditTextMessage.setText("This email is testing the reply of the recipient to sender's first message.");
    }
}
