package com.iliaskomp.email;

/**
 * Encapsulates all the header-related constants.
 */
public class HeaderFields {

    /**
     * The header fields' names that are used by the library
     */
    public static final class HeaderX {
        /**
         * The header field STATE.
         * Denotes in the headers the state of the encryption process.
         */
        public static final String STATE = "X-komp-state";
        /**
         * The header field PUBLIC_KEY_SENDER.
         */
        public static final String PUBLIC_KEY_SENDER = "X-komp-sender-public-key";
        /**
         * The header field PUBLIC_KEY_RECIPIENT.
         */
        public static final String PUBLIC_KEY_RECIPIENT = "X-komp-recipient-public-key";
        /**
         * The header field IV. (Initialization Vector)
         */
        public static final String IV = "X-komp-iv";
        /**
         * The header field NO_HEADER_STRING.
         * This value is only used when encryption headers are not found
         * in order to have a common constant in the API.
         */
        public static final String NO_HEADER_STRING = "no_header_string";
    }

    /**
     * The possible values of the STATE header field.
     */
    public static final class KompState {
        /**
         * The RECIPIENT_GETS_SENDER_PUBLIC_KEY state.
         * This state occurs when the sender first sends an email with his public key.
         */
        public static final String RECIPIENT_GETS_SENDER_PUBLIC_KEY =
                "first_interaction_recipient_gets_sender_public_key";

        /**
         * The SENDER_GETS_RECIPIENT_PUBLIC_KEY state.
         * This state occurs when the recipient replies with an email with his own public key.
         */
        public static final String SENDER_GETS_RECIPIENT_PUBLIC_KEY =
                "first_interaction_sender_gets_recipient_public_key";

        /**
         * This state occurs when the sender sends an encrypted email to the recipient.
         * The constant ENCRYPTED_EMAIL.
         */
        public static final String ENCRYPTED_EMAIL = "sender_sends_encrypted_to_known_recipient";
    }
}
