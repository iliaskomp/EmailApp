package com.iliaskomp.emailapp.network;

import com.iliaskomp.emailapp.utils.Config;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Created by IliasKomp on 22/05/17.
 */

class SendMailUtils {

    static Properties getProperties(String email) {
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

    static String getService(String emailName) {
        return  emailName.substring(emailName.indexOf("@") + 1);
    }

    static MimeMessage createMimeMessage(Session session, String emailName, String recipient,
                                         String subject, String message) throws MessagingException {

        //Creating MimeMessage object
        MimeMessage mm = new MimeMessage(session);

        //Setting sender address
        mm.setFrom(new InternetAddress(emailName));
        //Adding receiver
        mm.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
        //Adding subject
        mm.setSubject(subject);
        //Adding message
        mm.setText(message);

        return mm;
    }
}
