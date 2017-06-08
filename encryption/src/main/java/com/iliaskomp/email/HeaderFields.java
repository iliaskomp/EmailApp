package com.iliaskomp.email;

/**
 * Created by IliasKomp on 22/05/17.
 */

public class HeaderFields {
    // SENDER ======================================================================

    // Header names
    // possible states: 1. RECIPIENT_GETS_SENDER_PUBLIC_KEY, STATE_FIRST_TIME_RECEIVE, STATE_SENDER_RECEIVES_KEY, STATE_RECIPIENT_RECEIVES_ENCRYPTED_EMAIL
    public static final class HeaderX {
        public static final String STATE = "X-komp-state";
        public static final String PUBLIC_KEY_SENDER = "X-komp-sender-public-key";
        public static final String PUBLIC_KEY_RECIPIENT = "X-komp-recipient-public-key";
        public static final String IV = "X-komp-iv";
        //returns NO_HEADER_STRING when encryption headers are not found
        public static final String NO_HEADER_STRING = "no_header_string";
    }

    // Header fields
    // SCENARIO 1: FIRST INTERACTION
    public static final class FirstInteractionState {
        public static final String RECIPIENT_GETS_SENDER_PUBLIC_KEY = "first_interaction_recipient_gets_sender_public_key"; // recipient receives sender's public key
        public static final String SENDER_GETS_RECIPIENT_PUBLIC_KEY = "first_interaction_sender_gets_recipient_public_key"; // sender receives recipient's key

//        public static final String SENDER_SECOND_TIME = "first_interaction_sender_second_time"; // recipient receives encrypted email NOT NEEDED
    }

    // SCENARIO 2: SECOND+ INTERACTION
    public static final class SecondPlusInteractionState {
        public static final String SENDS_ENCRYPTED_MSG = "sender_sends_encrypted_to_known_recipient";
    }

    // SCENARIO 3: RECIPIENT NO LIBRARY
    public static final class RecipientNoLibraryState {
        public static final String RECIPIENT_ANSWER = "recipient_no_library";
    }
}
