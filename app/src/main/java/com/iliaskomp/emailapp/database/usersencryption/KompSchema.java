package com.iliaskomp.emailapp.database.usersencryption;

/**
 * The schema of the komp encryption database
 */
public class KompSchema {
    /**
     * The Users table for komp encryption.
     */
    public static final class UsersTable {
        /**
         * The constant NAME.
         */
        public static final String NAME = "users";

        /**
         * The type Cols.
         */
        public static final class Cols {
            /**
             * The constant UUID.
             */
            public static final String UUID = "uuid";
            /**
             * The constant MY_EMAIL.
             */
            public static final String MY_EMAIL = "my_email";
            /**
             * The constant THEIR_EMAIL.
             */
            public static final String THEIR_EMAIL = "their_email";
            /**
             * The constant MY_PUBLIC_KEY.
             */
            public static final String MY_PUBLIC_KEY = "my_public_key";
            /**
             * The constant MY_PRIVATE_KEY.
             */
            public static final String MY_PRIVATE_KEY = "my_private_key";
            /**
             * The constant THEIR_PUBLIC_KEY.
             */
            public static final String THEIR_PUBLIC_KEY = "their_public_key";
            /**
             * The constant SHARED_SECRET_KEY.
             */
            public static final String SHARED_SECRET_KEY = "shared_secret_key";
            /**
             * The constant STATE.
             */
            public static final String STATE = "state";
        }
    }

}
