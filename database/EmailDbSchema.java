package com.iliaskomp.emailapp.database;

/**
 * Created by IliasKomp on 15/02/17.
 */

public class EmailDbSchema {
    public static final class EmailTable {
        public static final String NAME = "emails";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String SENDER = "sender";
            public static final String RECIPIENT = "recipient";
            public static final String SUBJECT = "subject";
            public static final String MESSAGE = "message";
            public static final String DATE = "date";
            public static final String HEADERS = "headers";
        }
    }
}