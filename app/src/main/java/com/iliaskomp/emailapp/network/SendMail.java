package com.iliaskomp.emailapp.network;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.iliaskomp.email.EmailEncryptionRecipient;
import com.iliaskomp.email.EmailEncryptionSender;
import com.iliaskomp.email.HeaderFields;
import com.iliaskomp.emailapp.models.UsersEncryptionDb;
import com.iliaskomp.emailapp.models.UsersEncryptionEntry;
import com.iliaskomp.emailapp.utils.EmailCredentials;
import com.iliaskomp.libs.Base64;

import java.io.ByteArrayOutputStream;
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

public class SendMail extends AsyncTask<MimeMessage, Void, Void>{

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
    protected Void doInBackground(MimeMessage... messageArray) {
        MimeMessage message = messageArray[0];
        if (message == null) {return null;}

        Properties props = SendMailUtils.getProperties(EmailCredentials.EMAIL_SEND);

        //Creating a new session
        Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator(){
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

                if (headerState.equals(HeaderFields.FirstInteractionState.SENDER_GETS_RECIPIENT_PUBLIC_KEY)) { // recipient sends his public key
                    encryptionMm = message; // then message is sent from FetchMail correctly with komp headers
                } else {
                    saveOriginalMessage(message);
                    UsersEncryptionEntry entry = SendMailUtils.getUsersEncryptionEntryIfExists(mContext, message);
                    // if email of sender/recipient are not on encryption database add it (first state with only sender's keys)
                    if (entry == null) {
//                        encryptionMm = eer.createMessageWithPublicKey(originalMm, keyPairSender);
                        encryptionMm = ees.getEmailFirstTimeSending(message, session, keyPairSender);
                        //TODO save original message in db, wait for recipient public key, then encrypt and send
                        UsersEncryptionEntry newEntry = SendMailUtils.createUsersEncryptionEntry(message, keyPairSender);
                        entriesDb.addEntry(newEntry);
//                }
                        // else if entry exists, get public key of the other user and encrypt message with it

                    }
//                    else {
//                        // if recipient exists in database get secret key, encrypt message with it and send it
//                        SecretKey secretKey = DHHelper.SecretKeyClass.stringToSecretKey(entry.getSharedSecretKey());
//                        try {
//                            String[] encryptResult = EncryptionHelper.encrypt(originalMm.getContent().toString(), secretKey);
//                            encryptionMm = SendMailUtils.createEncryptedMessage(originalMm, encryptResult, session);
//
//                        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
                }



//                switch (headerState) {
//                    case HeaderFields.FirstInteractionState.RECIPIENT_GETS_SENDER_PUBLIC_KEY:
//                        break;
//                    case HeaderFields.FirstInteractionState.SENDER_GETS_RECIPIENT_PUBLIC_KEY:
//                        break;
//                    case HeaderFields.SecondPlusInteractionState.ENCRYPTED_EMAIL:
//                        break;
//                    default:
//                        assert headerState.equals(HeaderFields.HeaderX.NO_HEADER_STRING);
//                        break;
//                }






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

    private void saveOriginalMessage(MimeMessage message) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            message.writeTo(baos);
        } catch (IOException | MessagingException e) {
            e.printStackTrace();
        }
        byte[] bytearray = baos.toByteArray();
        String base64encodedmessage = Base64.encodeToString(bytearray, Base64.NO_WRAP);


        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString("EmailMessage ", base64encodedmessage);
        prefsEditor.commit();

//        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
//        SharedPreferences.Editor prefsEditor = mPrefs.edit();
//        Gson gson = new Gson();
//        String json = gson.toJson(message);
//        prefsEditor.putString("EmailMessage ", json);
//        prefsEditor.commit();
    }

//    private MimeMessage getOriginalMessage() {
//        Base64Decoder decoder = new Base64Decoder();
//        byte[] bytearray = decoder.decodeBuffer(base64encodedmessage );
//        ByteArrayInputStream bais = new ByteArrayInputStream(bytearray);
//
//        mailprops.setProperty('mail.from',sender);
//        Session session = Session.getInstance(mailprops,null);
//        session.setDebug(debug);
//
//        MimeMessage mimemessage = new MimeMessage(session,bais);
//
//        return mimemessage;
////        Gson gson = new Gson();
////        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
////        String json = mPrefs.getString("MyObject", "");
////        MimeMessage mm = gson.fromJson(json, MimeMessage.class);
////        return mm;
//    }

    private void removeMessage() {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.remove("MyObject");
        editor.commit();
    }
}
