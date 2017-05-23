package com.iliaskomp.emailapp.network;

import com.iliaskomp.emailapp.utils.EmailCredentials;

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

    static Properties getProperties(String service) {
        // Creating properties
        Properties props = new Properties();

        //Configuring properties
        switch (service) {
            case "gmail": {
                props.put("mail.smtp.host", "smtp.gmail.com");
                props.put("mail.smtp.socketFactory.port", "465");
                props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.port", "465");
            }
            break;
        }
        return props;
    }

    static MimeMessage createMimeMessage(Session session, String email, String recipient,
                                         String subject, String message) throws MessagingException {

        //Creating MimeMessage object
        MimeMessage mm = new MimeMessage(session);

        //Setting sender address
        mm.setFrom(new InternetAddress(EmailCredentials.EMAIL_FETCH_INBOX));
        //Adding receiver
        mm.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
        //Adding subject
        mm.setSubject(subject);
        //Adding message
        mm.setText(message);

        return mm;
    }
}
