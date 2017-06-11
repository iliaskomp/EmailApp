package com.iliaskomp.emailapp.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.iliaskomp.emailapp.utils.EmailCredentials;
import com.iliaskomp.libs.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

/**
 * Created by IliasKomp on 11/06/17.
 */

public class EmailSharedPrefsUtils {

    // filename format e.g "mm-example@gmail.com-0"
    public static void saveOriginalMessage(Context context, MimeMessage message) throws MessagingException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            message.writeTo(baos);
        } catch (IOException | MessagingException e) {
            e.printStackTrace();
        }
        byte[] emailByteArray = baos.toByteArray();
        String emailEncoded = Base64.encodeToString(emailByteArray, Base64.NO_WRAP);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = prefs.edit();

        prefsEditor.putString(createFilenameForPrefs(context, message), emailEncoded);
        prefsEditor.commit();
    }

    public static String createFilenameForPrefs(Context context, MimeMessage message) throws MessagingException {
        String filename = "mm-" + message.getAllRecipients()[0].toString();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Map<String,?> keys = prefs.getAll();

        int count = 0;
        for(Map.Entry<String,?> entry : keys.entrySet()){
//            Log.d("map values",entry.getKey() + ": " + entry.getValue().toString());
            if (entry.getKey().contains(filename)) {
                count++;
            }
        }

        filename += "-" + count;

        return filename;
    }

    private List<MimeMessage> getOriginalMessagesForEmail(Context context, String emailName) throws MessagingException {
        List<String> encodedMessages = null;
        List<MimeMessage> messages = null;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Map<String,?> keys = prefs.getAll();

        Session session = FetchMailUtils.getSentSession(EmailCredentials.EMAIL_SEND,
                EmailCredentials.PASSWORD_SEND,
                SendMailUtils.getProperties(EmailCredentials.EMAIL_SEND));

        for (Map.Entry<String,?> entry : keys.entrySet()){
            String entryEmail = entry.getKey().split("-")[1];
            if (entryEmail.equals(emailName)) {
                byte[] emailByteArray = Base64.decode((String) entry.getValue(), Base64.NO_WRAP);
                ByteArrayInputStream bais = new ByteArrayInputStream(emailByteArray);
                messages.add(new MimeMessage(session,bais));
            }
        }

        return messages;
    }

    public void removeOriginalMessagesForEmail(Context context, String emailName) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Map<String,?> keys = prefs.getAll();

        for(Map.Entry<String,?> entry : keys.entrySet()){
            if (entry.getKey().contains(emailName)) {
                removeOriginalMessage(context, entry.getKey());
            }
        }
    }

    private void removeOriginalMessage(Context context, String emailNameFile) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(emailNameFile);
        editor.commit();
    }

    //    private MimeMessage getOriginalMessage(String prefsEmailName) throws MessagingException {
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
//        String emailEncoded = prefs.getString(prefsEmailName, "");
//
//        byte[] emailByteArray = Base64.decode(emailEncoded, Base64.NO_WRAP);
//
//        ByteArrayInputStream bais = new ByteArrayInputStream(emailByteArray);
//
//        Session session = FetchMailUtils.getSentSession(EmailCredentials.EMAIL_SEND,
//                EmailCredentials.PASSWORD_SEND,
//                SendMailUtils.getProperties(EmailCredentials.EMAIL_SEND));
//
//        return new MimeMessage(session,bais);
//    }
}
