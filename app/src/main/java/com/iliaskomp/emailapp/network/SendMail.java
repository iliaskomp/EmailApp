package com.iliaskomp.emailapp.network;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.iliaskomp.email.EmailEncryptionSender;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

/**
 * Created by iliaskomp on 11/02/17.
 */

public class SendMail extends AsyncTask<String, Void, Void>{

    private Context mContext;
    private ProgressDialog mProgressDialog;

    public SendMail(Context context) {
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //Showing progress dialog while sending email
        mProgressDialog = ProgressDialog.show(mContext,"Sending message","Please wait...",false,false);

    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        //Dismissing the progress dialog
        mProgressDialog.dismiss();
        //Showing a success message
        Toast.makeText(mContext,"Message Sent",Toast.LENGTH_LONG).show();
    }

    @Override
    protected Void doInBackground(String... parameters) {

        final String emailName = parameters[0];
        final String password = parameters[1];
        final String recipient = parameters[2];
        final String subject = parameters[3];
        final String message = parameters[4];

        Properties props = SendMailUtils.getProperties(emailName);

        //Creating a new session
        Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator(){
            //Authenticating the password
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailName, password);
            }
        });

        try {
            MimeMessage mm = SendMailUtils.createMimeMessage(session, emailName,
                    recipient, subject, message);


            //============================================================//
            EmailEncryptionSender ees = new EmailEncryptionSender();

            // if recipient exists in database
            // TODO get public key, encrypt message with it and send it

            // if recipient doesn't exist, create key pair and send first message with public key
            KeyPair keyPairSender = ees.createKeyPair();
            Message mm2 = null;

            try {
                mm2 = ees.getEmailFirstTimeSending(mm, session, keyPairSender);

            } catch (InvalidKeySpecException | InvalidAlgorithmParameterException
                    | InvalidParameterSpecException | NoSuchAlgorithmException | IOException e) {
                e.printStackTrace();
            }

            //Sending email
            if (mm2 != null) {
                Transport.send(mm2);
            } else {
                throw new NullPointerException("Message to sent is null");
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
