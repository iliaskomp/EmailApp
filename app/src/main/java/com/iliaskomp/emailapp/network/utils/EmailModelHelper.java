package com.iliaskomp.emailapp.network.utils;

import android.content.Context;
import android.util.Log;

import com.iliaskomp.emailapp.models.EmailModel;
import com.iliaskomp.emailapp.utils.HeadersFormatHelper;

import org.jsoup.Jsoup;

import java.io.IOException;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.internet.MimeMultipart;

/**
 * Created by IliasKomp on 13/06/17.
 */

public class EmailModelHelper {

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
