package com.iliaskomp.emailapp.utils;

/**
 * Created by iliaskomp on 11/02/17.
 */

public class Config {


    public static class Name {
        public static final String IMAP = "imap";
        public static final String POP = "pop3";

    }

    public static class Gmail {
        public static final String DOMAIN_NAME = "gmail.com";

        public static final String IMAP_SERVER = "imap.gmail.com";
        public static final String IMAP_PORT = "993";

        public static final String POP_SERVER = "pop.gmail.com";
        public static final String POP_PORT = "995";

        public static final String SMTP_SERVER = "smtp.gmail.com";
        public static final String SMTP_PORT = "465";

    }

    public static class Yahoo {
        public static final String DOMAIN_NAME = "yahoo.com";

        public static final String IMAP_SERVER = "imap.mail.yahoo.com";
        public static final String IMAP_PORT = "993";

        public static final String POP_SERVER = "pop.mail.yahoo.com";
        public static final String POP_PORT = "995";

        public static final String SMTP_SERVER = "smtp.mail.yahoo.com";
        public static final String SMTP_PORT = "465";

    }
}
