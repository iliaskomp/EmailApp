package com.iliaskomp.emailapp.network.utils;

import android.content.Context;
import android.util.Log;

import com.iliaskomp.email.EmailEncryptionRecipient;
import com.iliaskomp.emailapp.models.EmailModel;
import com.iliaskomp.emailapp.utils.Config;
import com.iliaskomp.emailapp.utils.EmailCredentials;
import com.iliaskomp.emailapp.utils.HeadersFormatHelper;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * Created by IliasKomp on 22/05/17.
 */

public class FetchMailUtils {


    public static EmailModel buildEmailFromMessage(Context context, Message message) throws MessagingException, IOException {
        EmailModel email = new EmailModel();

        email.setSender(message.getFrom()[0].toString());
        email.setSubject(message.getSubject());
        email.setRecipient(message.getAllRecipients()[0].toString()); // TODO assuming one recipient
        email.setFullDate(message.getSentDate());
        email.setMessage(getEmailContentFromMessageObjects(message));
        email.setHeaders(HeadersFormatHelper.getHeadersStringFromEnumeration(message.getAllHeaders()));

        Log.d("FetchMailUtils", email.toString());

        return email;
    }

    public static Properties getProperties(String server, String protocol) {
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

    public static String getServerDomain(String domain, String protocol) {
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
    public static String getServiceFromEmail(String email) {
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




    public static List<MimeMessage> encryptMessagesForRecipient(Context context, List<MimeMessage> messagesForRecipient) throws IOException, MessagingException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException {
        List<MimeMessage> messages = new ArrayList<>();
        EmailEncryptionRecipient eer = new EmailEncryptionRecipient();

        for (MimeMessage message : messagesForRecipient) {
            Session session = getSentSession(EmailCredentials.EMAIL_SEND, EmailCredentials.PASSWORD_SEND, SendMailUtils.getProperties(message.getAllRecipients()[0].toString()));
            SecretKey secretKey = UsersEncryptionEntryHelper.getSecretSharedKeyFromDb(context, message.getFrom()[0].toString(), message.getAllRecipients()[0].toString());

            MimeMessage encryptedMessage = eer.createEncryptedMessage(session, message, secretKey);
            messages.add(encryptedMessage);
            //messages.add(createEncryptedMessage(context, m));
        }
        return messages;
    }

    //TODO image support etc
    private static String getEmailContentFromMessageObjects(Part messageObject) throws
            MessagingException, IOException {
        Message message = (Message) messageObject;
//         String type = message.getContentType();
        String result = "";

        if (message.isMimeType("text/plain")) {
            result = message.getContent().toString();
        } else if (message.isMimeType("text/html")) {
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

//        else if (message.isMimeType("image/jpeg")) {
//            System.out.println("--------> image/jpeg");
//            Object o = message.getContent();
//
//            InputStream x = (InputStream) o;
//            // Construct the required byte array
//            System.out.println("x.length = " + x.available());
//            while ((i = (int) ((InputStream) x).available()) > 0) {
//                int result = (int) (((InputStream) x).read(bArray));
//                if (result == -1) {
//                    int i = 0;
//                }
//                byte[] bArray = new byte[x.available()];
//
//                break;
//            }
//            FileOutputStream f2 = new FileOutputStream("/tmp/image.jpg");
//            f2.write(bArray);
//        }

//        else if (message.getContentType().contains("image/")) {
//                Log.d("content type", message.getContentType());
//                File f = new File("image" + new Date().getTime() + ".jpg");
//                DataOutputStream output = new DataOutputStream(
//                        new BufferedOutputStream(new FileOutputStream(f)));
//                com.sun.mail.util.BASE64DecoderStream test =
//                        (com.sun.mail.util.BASE64DecoderStream) message
//                                .getContent();
//                byte[] buffer = new byte[1024];
//                int bytesRead;
//                while ((bytesRead = test.read(buffer)) != -1) {
//                    output.write(buffer, 0, bytesRead);
//                }
//


        return result;
    }

}
