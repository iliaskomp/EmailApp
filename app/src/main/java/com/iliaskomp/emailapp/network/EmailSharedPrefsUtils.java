package com.iliaskomp.emailapp.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.iliaskomp.emailapp.network.utils.EmailConfigUtils;
import com.iliaskomp.emailapp.utils.EmailCredentials;
import com.iliaskomp.libs.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

/**
 * Saves and retrieves email messages.
 * Those messages are temporarily saved until a secret key has been established with the
 * recipient, and then are retrieves and sent, encrypted with the secret key.
 */
public class EmailSharedPrefsUtils {

    /**
     * Save original message. (example file format: mm-example@gmail.com-0)
     *
     * @param context the context
     * @param message the message
     * @throws MessagingException the messaging exception
     */
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

    private static String createFilenameForPrefs(Context context, MimeMessage message) throws MessagingException {
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

    /**
     * Retrieves original messages for an email address.
     *
     * @param context   the context
     * @param emailName the email address name
     * @return the original messages for an email address
     * @throws MessagingException the messaging exception
     */
    public static List<MimeMessage> getOriginalMessagesForEmail(Context context, String emailName) throws MessagingException {
        List<MimeMessage> messages = new ArrayList<>();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Map<String,?> keys = prefs.getAll();

        for (Map.Entry<String,?> entry : keys.entrySet()){
            String entryEmail = entry.getKey().split("-")[1];
            if (entryEmail.equals(emailName)) {
                MimeMessage message = getOriginalMessage(context, entry.getKey());
                if (message != null) {
                    messages.add(message);
                }
            }
        }

        return messages;
    }

    /**
     * Remove original messages for email address.
     *
     * @param context   the context
     * @param emailName the email address name
     */
    public static void removeOriginalMessagesForEmail(Context context, String emailName) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Map<String,?> keys = prefs.getAll();

        for(Map.Entry<String,?> entry : keys.entrySet()){
            if (entry.getKey().contains(emailName)) {
                removeOriginalMessage(context, entry.getKey());
            }
        }
    }

    private static void removeOriginalMessage(Context context, String emailNameFile) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(emailNameFile);
        editor.commit();
    }

    private static MimeMessage getOriginalMessage(Context context, String prefsEmailName) throws MessagingException {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String emailEncoded = prefs.getString(prefsEmailName, "");

        byte[] emailByteArray = Base64.decode(emailEncoded, Base64.NO_WRAP);

        ByteArrayInputStream bais = new ByteArrayInputStream(emailByteArray);

        Session session = EmailConfigUtils.getSentSession(EmailCredentials.EMAIL_SEND,
                EmailCredentials.PASSWORD_SEND,
                EmailConfigUtils.getSmtpProps(EmailCredentials.EMAIL_SEND));

        return new MimeMessage(session, bais);
    }
}
