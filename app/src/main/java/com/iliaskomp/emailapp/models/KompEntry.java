package com.iliaskomp.emailapp.models;

import java.util.UUID;

/**
 * A Komp encryption entry
 */
public class KompEntry {

//    public static final String UUID = "uuid";
//    public static final String MY_EMAIL = "my_email";
//    public static final String THEIR_EMAIL = "their_email";
//    public static final String MY_PUBLIC_KEY = "my_public_key";
//    public static final String MY_PRIVATE_KEY = "my_private_key";
//    public static final String THEIR_PUBLIC_KEY = "their_public_key";

    private UUID mId;
    private String mMyEmail;
    private String mTheirEmail;

    private String mMyPublicKey;
    private String mMyPrivateKey;
    private String mTheirPublicKey;

    private String mSharedSecretKey = "";

    private int mState;

    /**
     * The State of the encryption.
     */
    public class State {
        /**
         * The constant SENDER_ENTRY_NON_COMPLETE.
         * 1st interaction where sender sends mail with his public key.
         */
        public final static int SENDER_ENTRY_NON_COMPLETE = 0;
        /**
         * The constant ENTRY_COMPLETE.
         */
        public final static int ENTRY_COMPLETE = 1;
    }

    /**
     * Instantiates a new Komp entry.
     * Only for wrapper class when getting uuid from db and reconstructing this object
     * @param uuid       the uuid
     * @param myEmail    the my email
     * @param theirEmail the their email
     */
    public KompEntry(UUID uuid, String myEmail, String theirEmail) {
        mId = uuid;
        mMyEmail = myEmail;
        mTheirEmail = theirEmail;
    }

    /**
     * Instantiates a new Komp entry.
     *
     * @param myEmail    the my email
     * @param theirEmail the their email
     */
    public KompEntry(String myEmail, String theirEmail) {
        mId = UUID.randomUUID();
        mMyEmail = myEmail;
        mTheirEmail = theirEmail;
    }

    @Override
    public String toString() {
        return "myEmail: " + mMyEmail +
                "\ntheirEmail: " + mTheirEmail +
                "\nmyPublicKey: " + mMyPublicKey +
                "\nmyPrivateKey: " + mMyPrivateKey +
                "\ntheirPublicKey: " + mTheirPublicKey;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public UUID getId() {
        return mId;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(UUID id) {
        mId = id;
    }

    /**
     * Gets my email.
     *
     * @return the my email
     */
    public String getMyEmail() {
        return mMyEmail;
    }

    /**
     * Sets my email.
     *
     * @param myEmail the my email
     */
    public void setMyEmail(String myEmail) {
        mMyEmail = myEmail;
    }

    /**
     * Gets their email.
     *
     * @return the their email
     */
    public String getTheirEmail() {
        return mTheirEmail;
    }

    /**
     * Sets their email.
     *
     * @param theirEmail the their email
     */
    public void setTheirEmail(String theirEmail) {
        mTheirEmail = theirEmail;
    }

    /**
     * Gets my public key.
     *
     * @return the my public key
     */
    public String getMyPublicKey() {
        return mMyPublicKey;
    }

    /**
     * Sets my public key.
     *
     * @param myPublicKey the my public key
     */
    public void setMyPublicKey(String myPublicKey) {
        mMyPublicKey = myPublicKey;
    }

    /**
     * Gets my private key.
     *
     * @return the my private key
     */
    public String getMyPrivateKey() {
        return mMyPrivateKey;
    }

    /**
     * Sets my private key.
     *
     * @param myPrivateKey the my private key
     */
    public void setMyPrivateKey(String myPrivateKey) {
        mMyPrivateKey = myPrivateKey;
    }

    /**
     * Gets their public key.
     *
     * @return the their public key
     */
    public String getTheirPublicKey() {
        return mTheirPublicKey;
    }

    /**
     * Sets their public key.
     *
     * @param theirPublicKey the their public key
     */
    public void setTheirPublicKey(String theirPublicKey) {
        mTheirPublicKey = theirPublicKey;
    }

    /**
     * Gets shared secret key.
     *
     * @return the shared secret key
     */
    public String getSharedSecretKey() {
        return mSharedSecretKey;
    }

    /**
     * Sets shared secret key.
     *
     * @param sharedSecretKey the shared secret key
     */
    public void setSharedSecretKey(String sharedSecretKey) {
        mSharedSecretKey = sharedSecretKey;
    }

    /**
     * Gets state.
     *
     * @return the state
     */
    public int getState() {
        return mState;
    }

    /**
     * Sets state.
     *
     * @param state the state
     */
    public void setState(int state) {
        mState = state;
    }
}
