package com.iliaskomp.emailapp.network;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.iliaskomp.encryption.DHHelper;
import com.iliaskomp.email.HeaderFields;
import com.iliaskomp.email.HeaderUtils;
import com.iliaskomp.email.MessageBuilder;
import com.iliaskomp.emailapp.models.KompDb;
import com.iliaskomp.emailapp.models.KompEntry;
import com.iliaskomp.emailapp.network.utils.EmailConfigUtils;
import com.iliaskomp.emailapp.network.utils.KompEntriesHelper;
import com.iliaskomp.emailapp.utils.EmailCredentials;
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
 * Sends email using JavaMail library.
 */
public class SendMail extends AsyncTask<MimeMessage, Void, Void> {

    private Context mContext;
    private ProgressDialog mProgressDialog;
    private boolean mSavingMessage = false;

    /**
     * Instantiates a new Send mail.
     *
     * @param context the context
     */
    public SendMail(Context context) {
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //Showing progress dialog while sending email
        if (mSavingMessage) {
            mProgressDialog = ProgressDialog.show(mContext, "Saving message (will be sent after securing encryption", "Please wait...", false, false);
        } else {
            mProgressDialog = ProgressDialog.show(mContext, "Sending message", "Please wait...", false, false);
        }

    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        //Dismissing the progress dialog
        mProgressDialog.dismiss();
        //Showing a success message
        if (mSavingMessage) {
            Toast.makeText(mContext, "Message Saved (Saving message will be sent after securing encryption)", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(mContext, "Message Sent", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected Void doInBackground(MimeMessage... messageArray) {
        MimeMessage message = messageArray[0];
        if (message == null) {
            return null;
        }

        Properties props = EmailConfigUtils.getSmtpProps(EmailCredentials.EMAIL_SEND);

        //Creating a new session
        Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
            //Authenticating the password
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EmailCredentials.EMAIL_SEND, EmailCredentials.PASSWORD_SEND);
            }
        });

        try {

            // if recipient doesn't exist, create key pair and send first message with public key
            Message encryptionMm = null;

            //1st interaction
            if (KompEntriesHelper.encryptionLibraryExists()) {
                KeyPair keyPairSender = null;
                try {
                    keyPairSender = EncryptionHelper.createKeyPair();
                } catch (NoSuchAlgorithmException | InvalidParameterSpecException
                        | InvalidAlgorithmParameterException | InvalidKeySpecException e) {
                    e.printStackTrace();
                }
                KompDb entriesDb = KompDb.get(mContext);

                String headerState = HeaderUtils.getHeaderState(message);
                switch (headerState) {
                    case HeaderFields.KompState.SENDER_GETS_RECIPIENT_PUBLIC_KEY:  // recipient sends his public key
                        encryptionMm = message; // then message is sent from FetchMail correctly with komp headers
                        break;
                    case HeaderFields.KompState.ENCRYPTED_EMAIL:  // sender sends encrypted email
                        encryptionMm = message; // then message is sent from FetchMail correctly here with komp headers
                        break;
                    // need to construct message here
                    // first time sending message so need to save original message and
                    // send info email with sender's public key
                    default:
                        EmailSharedPrefsUtils.saveOriginalMessage(mContext, message);
                        KompEntry entry = KompEntriesHelper
                                .getUsersEncryptionEntryIfExists(mContext, message);
                        // if email of sender/recipient are not on encryption database add it
                        // (first state with only sender's keys)
                        if (entry == null) {

                            encryptionMm = MessageBuilder.createEmailFirstTimeSending(message, session,
                                    keyPairSender);

                            KompEntry newEntry = KompEntriesHelper
                                    .createSenderEntryNonComplete(message, keyPairSender);
                            entriesDb.addEntry(newEntry);
                            mSavingMessage = true;
                            // else if recipient exists in db but still hasn't send his public key
                            // do nothing (komp step message already send)
                        } else {
                            if (entry.getState() == KompEntry.State.SENDER_ENTRY_NON_COMPLETE) {
                                mSavingMessage = true;
                            }
                            // else if recipient exists in database get secret key, encrypt
                            // message with it and send it
                            else {
                                SecretKey secretKey = DHHelper.SecretKeyClass.stringToKey(entry
                                        .getSharedSecretKey());
                                try {
                                    encryptionMm = MessageBuilder.createEncryptedMessage(session, message,
                                            secretKey);
                                } catch (IOException | MessagingException | IllegalBlockSizeException | InvalidKeyException | BadPaddingException | NoSuchAlgorithmException | NoSuchPaddingException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        break;
                }
            } else {
                // no library, just send the normal message
                encryptionMm = message;
            }
            //Sending email
            if (encryptionMm != null) {
                Transport.send(encryptionMm);
            }
//            else {
//                throw new NullPointerException("Message to sent is null");
//            }
        }
        catch (MessagingException  e) {
            e.printStackTrace();
        }
        return null;
    }


}
