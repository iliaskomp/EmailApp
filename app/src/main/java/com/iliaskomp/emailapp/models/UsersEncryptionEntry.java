package com.iliaskomp.emailapp.models;

import java.util.UUID;

/**
 * Created by IliasKomp on 28/05/17.
 */

public class UsersEncryptionEntry {

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

    private String mState;

    // TODO state of entry?

    public UsersEncryptionEntry(UUID uuid, String myEmail, String theirEmail) {
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

    public UUID getId() {
        return mId;
    }

    public String getMyEmail() {
        return mMyEmail;
    }

    public void setMyEmail(String myEmail) {
        mMyEmail = myEmail;
    }

    public String getTheirEmail() {
        return mTheirEmail;
    }

    public void setTheirEmail(String theirEmail) {
        mTheirEmail = theirEmail;
    }

    public String getMyPublicKey() {
        return mMyPublicKey;
    }

    public void setMyPublicKey(String myPublicKey) {
        mMyPublicKey = myPublicKey;
    }

    public String getMyPrivateKey() {
        return mMyPrivateKey;
    }

    public void setMyPrivateKey(String myPrivateKey) {
        mMyPrivateKey = myPrivateKey;
    }

    public String getTheirPublicKey() {
        return mTheirPublicKey;
    }

    public void setTheirPublicKey(String theirPublicKey) {
        mTheirPublicKey = theirPublicKey;
    }

    public String getState() {
        return mState;
    }

    public void setState(String state) {
        mState = state;
    }
}
