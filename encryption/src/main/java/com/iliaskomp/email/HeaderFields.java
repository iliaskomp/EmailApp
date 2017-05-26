package com.iliaskomp.email;

/**
 * Created by IliasKomp on 22/05/17.
 */

public class HeaderFields {
    // SENDER ======================================================================

    // Header names
    // possible states: 1. SENDER_FIRST_TIME, STATE_FIRST_TIME_RECEIVE, STATE_SENDER_RECEIVES_KEY, STATE_RECIPIENT_RECEIVES_ENCRYPTED_EMAIL
    public static final class HeaderX {
        public static final String STATE = "X-komp-state";
        public static final String PUBLIC_KEY_SENDER = "X-komp-sender-public-key";
        public static final String PUBLIC_KEY_RECIPIENT = "X-komp-recipient-public-key";
    }


    // Header fields
    // SCENARIO 1: FIRST INTERACTION
    public static final class FirstInteractionState {
        public static final String SENDER_FIRST_TIME = "first_interaction_sender_first_time"; // recipient receives sender's public key
        public static final String RECIPIENT_FIRST_TIME = "first_interaction_recipient_first_time"; // sender receives recipient's key
        public static final String SENDER_SECOND_TIME = "first_interaction_sender_second_time"; // recipient receives encrypted email
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
