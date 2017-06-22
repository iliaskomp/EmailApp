package com.iliaskomp.email;

/**
 * Created by IliasKomp on 22/05/17.
 */

public class HeaderFields {

    // Header field names
    public static final class HeaderX {
        public static final String STATE = "X-komp-state";
        public static final String PUBLIC_KEY_SENDER = "X-komp-sender-public-key";
        public static final String PUBLIC_KEY_RECIPIENT = "X-komp-recipient-public-key";
        public static final String IV = "X-komp-iv";
        //returns NO_HEADER_STRING when encryption headers are not found
        public static final String NO_HEADER_STRING = "no_header_string";
    }

    // Header states
    public static final class KompState {
        public static final String RECIPIENT_GETS_SENDER_PUBLIC_KEY =
                "first_interaction_recipient_gets_sender_public_key";

        public static final String SENDER_GETS_RECIPIENT_PUBLIC_KEY =
                "first_interaction_sender_gets_recipient_public_key";

        public static final String ENCRYPTED_EMAIL = "sender_sends_encrypted_to_known_recipient";
    }
}
