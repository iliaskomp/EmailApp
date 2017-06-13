package com.iliaskomp.emailapp.database.usersencryption;

/**
 * Created by IliasKomp on 28/05/17.
 */

public class KompSchema {
    public static final class UsersTable {
        public static final String NAME = "users";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String MY_EMAIL = "my_email";
            public static final String THEIR_EMAIL = "their_email";
            public static final String MY_PUBLIC_KEY = "my_public_key";
            public static final String MY_PRIVATE_KEY = "my_private_key";
            public static final String THEIR_PUBLIC_KEY = "their_public_key";
            public static final String SHARED_SECRET_KEY = "shared_secret_key";
            public static final String STATE = "state";
        }
    }

}
