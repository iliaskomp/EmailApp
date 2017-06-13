package com.iliaskomp.emailapp.network.utils;

import com.iliaskomp.emailapp.utils.Config;

import java.util.Properties;

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


}
