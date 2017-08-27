package com.iliaskomp.emailapp.database.email;

/**
 * The schema of the email database
 */
public class EmailDbSchema {
    /**
     * The Inbox table.
     */
    public static final class InboxTable {
        /**
         * The constant NAME.
         */
        public static final String NAME = "inbox";

        /**
         * The type Cols.
         */
        public static final class Cols {
            /**
             * The constant UUID.
             */
            public static final String UUID = "uuid";
            /**
             * The constant SENDER.
             */
            public static final String SENDER = "sender";
            /**
             * The constant RECIPIENT.
             */
            public static final String RECIPIENT = "recipient";
            /**
             * The constant SUBJECT.
             */
            public static final String SUBJECT = "subject";
            /**
             * The constant MESSAGE.
             */
            public static final String MESSAGE = "message";
            /**
             * The constant DATE.
             */
            public static final String DATE = "date";
            /**
             * The constant HEADERS.
             */
            public static final String HEADERS = "headers";
        }
    }

    /**
     * The Sent table.
     */
    public static final class SentTable {
        /**
         * The constant NAME.
         */
        public static final String NAME = "sent";

        /**
         * The type Cols.
         */
        public static final class Cols {
            /**
             * The constant UUID.
             */
            public static final String UUID = "uuid";
            /**
             * The constant SENDER.
             */
            public static final String SENDER = "sender";
            /**
             * The constant RECIPIENT.
             */
            public static final String RECIPIENT = "recipient";
            /**
             * The constant SUBJECT.
             */
            public static final String SUBJECT = "subject";
            /**
             * The constant MESSAGE.
             */
            public static final String MESSAGE = "message";
            /**
             * The constant DATE.
             */
            public static final String DATE = "date";
            /**
             * The constant HEADERS.
             */
            public static final String HEADERS = "headers";
        }
    }
}
