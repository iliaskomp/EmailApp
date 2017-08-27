package com.iliaskomp.emailapp.network.utils;

import com.iliaskomp.emailapp.utils.Config;

import java.util.Properties;

import javax.mail.PasswordAuthentication;
import javax.mail.Session;

/**
 * Email provider related utilities
 */

public class EmailConfigUtils {

    // e.g. return example.com from test@example.com
    public static String getServiceFromEmail(String emailName) {
        return emailName.substring(emailName.indexOf("@") + 1);
    }

    public static Properties getSmtpProps(String email) {
        String service = getServiceFromEmail(email);
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

    public static Properties getImapProps(String server, String protocol) {
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

    // e.g. imap.mail.yahoo.com
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

    // e.g. gmail has different naming than the default "Sent"
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

    public static Session getSentSession(final String email, final String password, Properties props) {
        return Session.getDefaultInstance(props, new javax.mail.Authenticator() {
            //Authenticating the password
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email, password);
            }
        });
    }
}
