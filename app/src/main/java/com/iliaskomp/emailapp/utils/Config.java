package com.iliaskomp.emailapp.utils;

/**
 * Created by iliaskomp on 11/02/17.
 */

public class Config {

    public static class Gmail {
        public static final String IMAP_NAME = "imap";
        public static final String IMAP_HOST = "imap.gmail.com";
        public static final String IMAP_PORT = "993";

        public static final String POP_NAME = "pop3";
        public static final String POP_HOST = "pop.gmail.com";
        public static final String POP_PORT = "995";
    }

    public static class Yahoo {
        public static final String IMAP_NAME = "imap";
        public static final String IMAP_HOST = "imap.mail.yahoo.com";
        public static final String IMAP_PORT = "993";

        public static final String POP_NAME = "pop3";
        public static final String POP_HOST = "pop.mail.yahoo.com";
        public static final String POP_PORT = "995";
    }
}
