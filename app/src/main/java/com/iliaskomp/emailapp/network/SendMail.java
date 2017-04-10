package com.iliaskomp.emailapp.network;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.iliaskomp.email.EmailEncryptionSender;
import com.iliaskomp.emailapp.models.EmailToSend;
import com.iliaskomp.emailapp.utils.EmailCredentials;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Created by iliaskomp on 11/02/17.
 */

public class SendMail extends AsyncTask<Void, Void, Void>{

    private Context mContext;
    private Session mSession;

    private String mRecipient;
    private String mSubject;
    private String mMessage;

    private ProgressDialog mProgressDialog;

    public SendMail(Context context, EmailToSend emailToSend) {
        mContext = context;
        this.mRecipient = emailToSend.getRecipient();
        this.mSubject = emailToSend.getSubject();
        this.mMessage = emailToSend.getMessage();
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
    protected Void doInBackground(Void... voids) {
        // Creating properties
        Properties props = new Properties();

        //Configuring properties for gmail
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        //Creating a new session
        mSession = Session.getDefaultInstance(props, new javax.mail.Authenticator(){
            //Authenticating the password
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EmailCredentials.EMAIL, EmailCredentials.PASSWORD);
            }
        });

        try {
            //Creating MimeMessage object
            MimeMessage mm = new MimeMessage(mSession);

            //Setting sender address
            mm.setFrom(new InternetAddress(EmailCredentials.EMAIL));
            //Adding receiver
            mm.addRecipient(Message.RecipientType.TO, new InternetAddress(mRecipient));
            //Adding subject
            mm.setSubject(mSubject);
            //Adding message
            mm.setText(mMessage);

            EmailEncryptionSender ees = new EmailEncryptionSender();
            Message mm2 = null;
            try {
                mm2 = ees.getEmailFirstTimeSending(mm, mSession);
            } catch (InvalidKeySpecException | InvalidAlgorithmParameterException
                    | InvalidParameterSpecException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            //Sending email
            if (mm2 != null) {
                Transport.send(mm2);
            } else
                throw new NullPointerException("Message to sent is null");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
