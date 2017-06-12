package com.iliaskomp.emailapp.network;

import android.content.Context;

import com.iliaskomp.dhalgorithm.DHHelper;
import com.iliaskomp.email.HeaderFields;
import com.iliaskomp.emailapp.models.UsersEncryptionDb;
import com.iliaskomp.emailapp.models.UsersEncryptionEntry;
import com.iliaskomp.emailapp.utils.Config;

import java.security.KeyPair;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Created by IliasKomp on 22/05/17.
 */

public class SendMailUtils {

    public static Properties getProperties(String email) {
        String service = getService(email);
        // Creating properties
        Properties props = new Properties();

        //Configuring properties
        switch (service) {
            case Config.Gmail.DOMAIN_NAME: {
                props.put("mail.smtp.host", Config.Gmail.SMTP_SERVER);
                props.put("mail.smtp.socketFactory.port", Config.Gmail.SMTP_PORT);
                props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.port", Config.Gmail.SMTP_PORT);
            }
            break;
            case Config.Yahoo.DOMAIN_NAME: {
                props.put("mail.smtp.host", Config.Yahoo.SMTP_SERVER);
                props.put("mail.smtp.socketFactory.port", Config.Yahoo.SMTP_PORT);
                props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.port", Config.Yahoo.SMTP_PORT);

            }
        }
        return props;
    }
    // e.g. return example.com from test@example.com

    static String getService(String emailName) {
        return emailName.substring(emailName.indexOf("@") + 1);
    }

    static MimeMessage createMimeMessage(Session session, String emailName, String recipient,
                                         String subject, String message) throws MessagingException {

        MimeMessage mm = new MimeMessage(session);

        mm.setFrom(new InternetAddress(emailName));
        mm.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
        mm.setSubject(subject);
        mm.setText(message);

        return mm;
    }

    // returns null if no entry is found
    static UsersEncryptionEntry getUsersEncryptionEntryIfExists(Context context, MimeMessage message) throws MessagingException {
        UsersEncryptionDb db = UsersEncryptionDb.get(context);
        for (UsersEncryptionEntry entry : db.getUsersEncryptionEntries()) {
            if (entry.getMyEmail().equals(message.getFrom()[0].toString()) &&
                    entry.getTheirEmail().equals(message.getAllRecipients()[0].toString()))
                return entry;
        }
        return null;
    }



    static UsersEncryptionEntry createUsersEncryptionEntry(
            MimeMessage message, KeyPair keyPair) throws MessagingException {

        UsersEncryptionEntry entry = new UsersEncryptionEntry(message.getFrom()[0].toString(),
                message.getAllRecipients()[0].toString());

        entry.setMyPublicKey(DHHelper.PublicKeyClass.publicKeyToString(keyPair.getPublic()));
        entry.setMyPrivateKey(DHHelper.PrivateKeyClass.privateKeyToString(keyPair.getPrivate()));
        entry.setState(UsersEncryptionEntry.State.SENDER_ENTRY_NON_COMPLETE);

        return entry;
    }

    public static Message createEncryptedMessage(MimeMessage originalMm, String[] encryptResult, Session session) throws MessagingException {
        String encryptedText = encryptResult[0];
        String iv = encryptResult[1];

        //Creating MimeMessage object
        MimeMessage mm = new MimeMessage(session);

        mm.setFrom(originalMm.getSender());
        mm.addRecipient(Message.RecipientType.TO, originalMm.getAllRecipients()[0]);
        mm.setSubject(originalMm.getSubject());
        mm.setText(encryptedText);
        mm.setHeader(HeaderFields.HeaderX.STATE, HeaderFields.KompState.ENCRYPTED_EMAIL);
        mm.setHeader(HeaderFields.HeaderX.IV, iv);

        return mm;
    }
}
