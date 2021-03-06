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
 * Helper class to convert a MimeMessage to the EmailModel defined in this app
 */

public class EmailModelHelper {

    public static EmailModel buildEmailFromMessage(Context context, Message message) throws MessagingException, IOException {
        EmailModel email = new EmailModel();

        email.setSender(message.getFrom()[0].toString());
        email.setSubject(message.getSubject());
        email.setRecipient(message.getAllRecipients()[0].toString());
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

    private static String getEmailContentFromMessageObjects(Part messageObject) throws
            MessagingException, IOException {
        Message message = (Message) messageObject;
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
        return result;
    }
}
