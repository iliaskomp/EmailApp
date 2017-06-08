package com.iliaskomp.emailapp.network;

import android.content.Context;
import android.util.Log;

import com.iliaskomp.dhalgorithm.DHAlgorithm;
import com.iliaskomp.dhalgorithm.DHHelper;
import com.iliaskomp.email.EmailEncryptionRecipient;
import com.iliaskomp.emailapp.models.EmailModel;
import com.iliaskomp.emailapp.models.UsersEncryptionDb;
import com.iliaskomp.emailapp.models.UsersEncryptionEntry;
import com.iliaskomp.emailapp.utils.Config;
import com.iliaskomp.emailapp.utils.HeadersFormatHelper;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Properties;

import javax.crypto.SecretKey;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.MimeMultipart;

/**
 * Created by IliasKomp on 22/05/17.
 */

public class FetchMailUtils {

    //TODO image support etc
    private static String getEmailContentFromMessageObjects(Part messageObject) throws MessagingException, IOException {
        Message message = (Message) messageObject;
        // String type = message.getContentType();
        String result = "";

        if (message.isMimeType("text/plain")) {
            result = message.getContent().toString();
        } else if (message.isMimeType("text/plain")) {
            String html = (String) messageObject.getContent();
            result = result + "\n" + Jsoup.parse(html).text();
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            int count = mimeMultipart.getCount();
            for (int i = 0; i < count; i++) {
                BodyPart bodyPart = mimeMultipart.getBodyPart(i);
                if (bodyPart.isMimeType("text/plain")) {
                    result = result + "\n" + bodyPart.getContent();
                    break;  //without break same text appears twice in my tests
                } else if (bodyPart.isMimeType("text/html")) {
                    String html = (String) bodyPart.getContent();
                    result = result + "\n" + Jsoup.parse(html).text();
                }
            }
        } else {
            result = "invalid";
        }

        return result;
    }

    static EmailModel buildEmailFromMessage(Context context, Message message) throws MessagingException, IOException {
        EmailModel email = new EmailModel();

        email.setSender(message.getFrom()[0].toString());
        email.setSubject(message.getSubject());
        email.setRecipient(message.getAllRecipients()[0].toString()); // TODO assuming one recipient
        email.setFullDate(message.getSentDate());
        email.setMessage(getEmailContentFromMessageObjects(message));
        email.setHeaders(HeadersFormatHelper.getHeadersStringFromEnumeration(message.getAllHeaders()));

//        email.setEncryptionEntry(getEncryptionEntry(context, message));
        Log.d("FetchMailUtils", email.toString());

        return email;
    }

//    private static UsersEncryptionEntry getEncryptionEntry(Context context, Message message) throws MessagingException {
//        UsersEncryptionDb db = UsersEncryptionDb.get(context);
//
//        EmailEncryptionRecipient ees = new EmailEncryptionRecipient();
//        ees.getHeaderState(message);
//
//        for (UsersEncryptionEntry entry : db.getUsersEncryptionEntries()) {
//            if (db.getEntryFromEmails(message.getAllRecipients()[0].toString(), message.getFrom()[0].toString()) == null) {
//
//                // entry doesn't exist so create new entry
//            } else {
//                return entry;
//            }
//
////            if (entry.getTheirEmail().equals(message.get))
//        }
//
//
//    }

    public static void updateAndCompleteEntry(Context context, Message message) throws MessagingException, InvalidKeyException, NoSuchAlgorithmException {
        EmailEncryptionRecipient eer = new EmailEncryptionRecipient();
        PublicKey theirPublicKey = eer.getRecipientPublicKeyFromHeader(message);

        UsersEncryptionDb db = UsersEncryptionDb.get(context);
        UsersEncryptionEntry oldEntry = db.getEntryFromEmails(message.getAllRecipients()[0].toString(), message.getFrom()[0].toString());
        UsersEncryptionEntry newEntry = new UsersEncryptionEntry(oldEntry.getMyEmail(), oldEntry.getTheirEmail());

        newEntry.setId(oldEntry.getId());
        newEntry.setMyPublicKey(oldEntry.getMyPublicKey());
        newEntry.setMyPrivateKey(oldEntry.getMyPrivateKey());
        newEntry.setState(UsersEncryptionEntry.State.ENTRY_COMPLETE);

        newEntry.setTheirPublicKey(DHHelper.PublicKeyClass.publicKeyToString(theirPublicKey));


        DHAlgorithm dhAlgorithm = new DHAlgorithm();
        SecretKey secretKey = dhAlgorithm.agreeSecretKey(DHHelper.PrivateKeyClass.stringToPrivateKey(oldEntry.getMyPrivateKey()), theirPublicKey);
        newEntry.setSharedSecretKey(DHHelper.SecretKeyClass.secretKeyToString(secretKey));

        db.updateEntry(message.getAllRecipients()[0].toString(), message.getFrom()[0].toString(), newEntry);
    }

    static UsersEncryptionEntry createRecipientEntry(Message message, KeyPair keyPair) throws MessagingException, InvalidKeyException, NoSuchAlgorithmException {
        EmailEncryptionRecipient eer = new EmailEncryptionRecipient();
        DHAlgorithm dhAlgorithm = new DHAlgorithm();

        PublicKey theirPublicKey = eer.getSenderPublicKeyFromHeader(message);
        SecretKey secretKey = dhAlgorithm.agreeSecretKey(keyPair.getPrivate(), theirPublicKey);

        UsersEncryptionEntry entry = new UsersEncryptionEntry(message.getAllRecipients()[0].toString(), message.getFrom()[0].toString());
        entry.setMyPrivateKey(DHHelper.PrivateKeyClass.privateKeyToString(keyPair.getPrivate()));
        entry.setMyPublicKey(DHHelper.PublicKeyClass.publicKeyToString(keyPair.getPublic()));
        entry.setTheirPublicKey(DHHelper.PublicKeyClass.publicKeyToString(theirPublicKey));
        entry.setSharedSecretKey(DHHelper.SecretKeyClass.secretKeyToString(secretKey));
        entry.setState(UsersEncryptionEntry.State.ENTRY_COMPLETE);

        return entry;
    }

    static Properties getProperties(String server, String protocol) {
        Properties properties = new Properties();
        properties.put("mail.imap.host", server);

        switch (server) {
            case Config.Gmail.IMAP_SERVER:
                properties.put("mail.imap.port", Config.Gmail.IMAP_PORT);
                break;
            case Config.Yahoo.IMAP_SERVER:
                properties.put("mail.imap.port", Config.Yahoo.IMAP_PORT);
                break;
            default:
                properties.put("mail.imap.port", null);
                break;
        }

        properties.put(String.format("mail.%s.starttls.enable", protocol), "true");

        return properties;
    }

    static String getServerDomain(String domain, String protocol) {
        switch (domain) {
            case Config.Gmail.DOMAIN_NAME:
                return protocol.equals(Config.Name.IMAP) ?
                        Config.Gmail.IMAP_SERVER : Config.Gmail.POP_SERVER;
            case Config.Yahoo.DOMAIN_NAME:
                return protocol.equals(Config.Name.IMAP) ?
                        Config.Yahoo.IMAP_SERVER : Config.Yahoo.POP_SERVER;
            default:
                return null;
        }
    }

    public static String getSentFolderName(String domain) {

        switch (domain) {
            case Config.Gmail.DOMAIN_NAME:
                return "[Gmail]/Sent Mail";
            case Config.Yahoo.DOMAIN_NAME:
                return "Sent";
            default:
                return null;
        }
    }

    //returns null if no known service/service found
    static String getServiceFromEmail(String email) {
        return email.substring(email.indexOf("@") + 1);
    }

    public static boolean encryptionLibraryExists() {
        try {
            Class cls = Class.forName("com.iliaskomp.email.EmailEncryptionRecipient");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static SecretKey getSecretSharedKeyFromDb(Context context, Message message) throws MessagingException {
        UsersEncryptionDb db = UsersEncryptionDb.get(context);
        String keyString = db.getSharedSeretFromEmails(message.getAllRecipients()[0].toString(), message.getFrom()[0].toString());

        return DHHelper.SecretKeyClass.stringToSecretKey(keyString);
    }

    public static String getIv(Message message) throws MessagingException {
        EmailEncryptionRecipient eer = new EmailEncryptionRecipient();
        String iv = eer.getHeaderIv(message);
        return iv;
    }

    public static EmailModel buildDecryptedEmail(Message message, String decryptedText) throws MessagingException, IOException {
        EmailModel email = new EmailModel();

        email.setSender(message.getFrom()[0].toString());
        email.setSubject(message.getSubject());
        email.setRecipient(message.getAllRecipients()[0].toString());
        email.setFullDate(message.getSentDate());
        email.setMessage(decryptedText);
        email.setHeaders(HeadersFormatHelper.getHeadersStringFromEnumeration(message.getAllHeaders()));

        return email;
    }

    public static Session getSentSession(final String senderEmail, final String password, Properties props) {
        return Session.getDefaultInstance(props, new javax.mail.Authenticator() {
            //Authenticating the password
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, password);
            }
        });
    }


}
