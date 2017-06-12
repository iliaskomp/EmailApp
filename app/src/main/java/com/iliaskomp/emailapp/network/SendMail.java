package com.iliaskomp.emailapp.network;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.iliaskomp.dhalgorithm.DHHelper;
import com.iliaskomp.email.EmailEncryptionRecipient;
import com.iliaskomp.email.EmailEncryptionSender;
import com.iliaskomp.email.HeaderFields;
import com.iliaskomp.emailapp.models.UsersEncryptionDb;
import com.iliaskomp.emailapp.models.UsersEncryptionEntry;
import com.iliaskomp.emailapp.utils.EmailCredentials;

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

public class SendMail extends AsyncTask<MimeMessage, Void, Void> {

    private Context mContext;
    private ProgressDialog mProgressDialog;

    public SendMail(Context context) {
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //Showing progress dialog while sending email
        mProgressDialog = ProgressDialog.show(mContext, "Sending message", "Please wait...", false, false);

    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        //Dismissing the progress dialog
        mProgressDialog.dismiss();
        //Showing a success message
        Toast.makeText(mContext, "Message Sent", Toast.LENGTH_LONG).show();
    }

    @Override
    protected Void doInBackground(MimeMessage... messageArray) {
        MimeMessage message = messageArray[0];
        if (message == null) {
            return null;
        }

        Properties props = SendMailUtils.getProperties(EmailCredentials.EMAIL_SEND);

        //Creating a new session
        Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
            //Authenticating the password
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EmailCredentials.EMAIL_SEND, EmailCredentials.PASSWORD_SEND);
            }
        });

        try {
//            MimeMessage originalMm = SendMailUtils.createMimeMessage(session, EmailCredentials.EMAIL_SEND,
//                    recipient, subject, content);
            //============================================================//
            EmailEncryptionSender ees = new EmailEncryptionSender();
            EmailEncryptionRecipient eer = new EmailEncryptionRecipient();


            // if recipient doesn't exist, create key pair and send first message with public key
            KeyPair keyPairSender = ees.createKeyPair();
            Message encryptionMm = null;
            UsersEncryptionDb entriesDb = UsersEncryptionDb.get(mContext);

            //1st interaction
            if (FetchMailUtils.encryptionLibraryExists()) {
                String headerState = eer.getHeaderState(message);

                if (headerState.equals(HeaderFields.KompState.SENDER_GETS_RECIPIENT_PUBLIC_KEY)) { // recipient sends his public key
                    encryptionMm = message; // then message is sent from FetchMail correctly with komp headers
                } else if (headerState.equals(HeaderFields.KompState.ENCRYPTED_EMAIL)) { // sender sends encrypted email
                    encryptionMm = message; // then message is sent from FetchMail correctly with komp headers
                } else { // first time sending message so need to save original message and send info email with sender's public key
                    EmailSharedPrefsUtils.saveOriginalMessage(mContext, message);
                    UsersEncryptionEntry entry = SendMailUtils.getUsersEncryptionEntryIfExists(mContext, message);
                    // if email of sender/recipient are not on encryption database add it (first state with only sender's keys)
                    if (entry == null) {
                        encryptionMm = ees.getEmailFirstTimeSending(message, session, keyPairSender);
                        UsersEncryptionEntry newEntry = SendMailUtils.createUsersEncryptionEntry(message, keyPairSender);
                        entriesDb.addEntry(newEntry);
                        // else if recipient exists in database get secret key, encrypt message with it and send it
                    } else {
                        SecretKey secretKey = DHHelper.SecretKeyClass.stringToSecretKey(entry.getSharedSecretKey());
                        encryptionMm = eer.createEncryptedMessage(session, message, secretKey);
                    }
                }
            } else {
                // no library, just send the normal message
                encryptionMm = message;
            }
            //TODO create emailModel and add to sentDB?
            //Sending email
            if (encryptionMm != null) {
                Transport.send(encryptionMm);
            } else {
                throw new NullPointerException("Message to sent is null");
            }
        } catch (MessagingException | IOException | InvalidAlgorithmParameterException | NoSuchAlgorithmException | InvalidKeySpecException | InvalidParameterSpecException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }


}
