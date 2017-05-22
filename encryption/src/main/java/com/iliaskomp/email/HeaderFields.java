package com.iliaskomp.email;

/**
 * Created by IliasKomp on 22/05/17.
 */

public class HeaderFields {
    // SENDER ======================================================================

    // Header names
    // possible states: 1. FIRST_TIME_SEND, STATE_FIRST_TIME_RECEIVE, STATE_SENDER_RECEIVES_KEY, STATE_RECIPIENT_RECEIVES_ENCRYPTED_EMAIL
    public static final class HeaderX {
        public static final String STATE = "X-komp-state";
        public static final String PUBLIC_KEY_SENDER = "X-komp-sender-public-key";
        public static final String PUBLIC_KEY_RECIPIENT = "X-komp-recipient-public-key";
    }


    // Header fields
    // SCENARIO 1: FIRST INTERACTION
    public static final class FirstInteractionState {
        public static final String FIRST_TIME_SEND = "first_interaction_sender_sends";
        public static final String FIRST_TIME_RECEIVE = "first_time_receive";
        public static final String SENDER_RECEIVES_KEY = "sender_receives_key";
        public static final String RECIPIENT_RECEIVES_ENCRYPTED_EMAIL = "recipient_receives_encrypted_email";
    }

    // SCENARIO 2: SECOND+ INTERACTION
    public static final class SecondPlusInteractionState {
        public static final String SENDER_SENDS = "sender_sends_encrypted_to_known_recipient";
    }

    // SCENARIO 3: RECIPIENT NO LIBRARY
    public static final class RecipientNoLibraryState {
        public static final String RECIPIENT_ANSWER = "recipient_no_library";
    }

}
