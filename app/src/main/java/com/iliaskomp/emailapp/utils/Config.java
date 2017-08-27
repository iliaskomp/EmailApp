package com.iliaskomp.emailapp.utils;

/**
 * Configuration options for email providers
 */
public class Config {


    /**
     * Defining email protocol names.
     */
    public static class Name {
        /**
         * The IMAP protocol.
         */
        public static final String IMAP = "imap";
        /**
         * The POP protocol.
         */
        public static final String POP = "pop3";

    }

    /**
     * Gmail related parameters.
     */
    public static class Gmail {
        /**
         * Domain name.
         */
        public static final String DOMAIN_NAME = "gmail.com";

        /**
         * IMAP server.
         */
        public static final String IMAP_SERVER = "imap.gmail.com";
        /**
         * IMAP port.
         */
        public static final String IMAP_PORT = "993";

        /**
         * POP server.
         */
        public static final String POP_SERVER = "pop.gmail.com";
        /**
         * POP port.
         */
        public static final String POP_PORT = "995";

        /**
         * SMTP server.
         */
        public static final String SMTP_SERVER = "smtp.gmail.com";
        /**
         * SMTP port.
         */
        public static final String SMTP_PORT = "465";

    }

    /**
     * Yahoo related parameters.
     */
    public static class Yahoo {
        /**
         * Domain name.
         */
        public static final String DOMAIN_NAME = "yahoo.com";

        /**
         * IMAP server.
         */
        public static final String IMAP_SERVER = "imap.mail.yahoo.com";
        /**
         * IMAP port.
         */
        public static final String IMAP_PORT = "993";

        /**
         * POP server.
         */
        public static final String POP_SERVER = "pop.mail.yahoo.com";
        /**
         * POP port.
         */
        public static final String POP_PORT = "995";

        /**
         * SMTP server.
         */
        public static final String SMTP_SERVER = "smtp.mail.yahoo.com";
        /**
         * SMTP port.
         */
        public static final String SMTP_PORT = "465";

    }
}
