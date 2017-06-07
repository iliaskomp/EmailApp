package com.iliaskomp.emailapp.network;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.iliaskomp.dhalgorithm.DHHelper;
import com.iliaskomp.email.EmailEncryptionSender;
import com.iliaskomp.emailapp.models.UsersEncryptionDb;
import com.iliaskomp.emailapp.models.UsersEncryptionEntry;
import com.iliaskomp.encryption.EncryptionHelper;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.Properties;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
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
        final String content = parameters[4];

        Properties props = SendMailUtils.getProperties(emailName);

        //Creating a new session
        Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator(){
            //Authenticating the password
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailName, password);
            }
        });

        try {
            MimeMessage originalMm = SendMailUtils.createMimeMessage(session, emailName,
                    recipient, subject, content);


            //============================================================//
            EmailEncryptionSender ees = new EmailEncryptionSender();

            // if recipient doesn't exist, create key pair and send first message with public key
            // TODO save originalMm for further steps if 1st interaction for the next encrypted sending
            KeyPair keyPairSender = ees.createKeyPair();
            Message encryptionMm = null;

            UsersEncryptionDb db = UsersEncryptionDb.get(mContext);
            //1st interaction
            if (FetchMailUtils.encryptionLibraryExists()) {
                UsersEncryptionEntry entry = SendMailUtils.getUsersEncryptionEntryIfExists(mContext, originalMm);
                // if email of sender/recipient are not on encryption database add it (first state with only sender's keys)
                if (entry == null) {
                    encryptionMm = ees.getEmailFirstTimeSending(originalMm, session, keyPairSender);
                    //TODO save original message in db, wait for recipient public key, then encrypt and send
                    UsersEncryptionEntry newEntry = SendMailUtils.createUsersEncryptionEntry(originalMm, keyPairSender);
                    db.addEntry(newEntry);
                    // else if entry exists, get public key of the other user and encrypt message with it
                } else {
                    // if recipient exists in database get secret key, encrypt message with it and send it
                    SecretKey secretKey = DHHelper.SecretKeyClass.stringToSecretKey(entry.getSharedSecretKey());
                    try {
                        String[] encryptResult = EncryptionHelper.encrypt(originalMm.getContent().toString(), secretKey);
                        encryptionMm = SendMailUtils.createEncryptedMessage(originalMm, encryptResult, session);

                    } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | IOException e) {
                        e.printStackTrace();
                    }
                }

            }

            //Sending email
            if (encryptionMm != null) {
                Transport.send(encryptionMm);
            } else {
                throw new NullPointerException("Message to sent is null");
            }
        } catch (MessagingException |
                IOException |
                InvalidAlgorithmParameterException |
                NoSuchAlgorithmException |
                InvalidKeySpecException |
                InvalidParameterSpecException e) {
            e.printStackTrace();
        }
        return null;
    }
}
