package com.iliaskomp.emailapp.models;

import java.util.UUID;

/**
 * Created by IliasKomp on 28/05/17.
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

    public class State {
        // 1st interaction sender sends mail with his public key.
        public final static int SENDER_ENTRY_NON_COMPLETE = 0;
        public final static int ENTRY_COMPLETE = 1;
    }

    // Only for wrapper class when getting uuid from db and reconstructing this object
    public KompEntry(UUID uuid, String myEmail, String theirEmail) {
        mId = uuid;
        mMyEmail = myEmail;
        mTheirEmail = theirEmail;
    }

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

    public UUID getId() {
        return mId;
    }

    public void setId(UUID id) {
        mId = id;
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

    public String getSharedSecretKey() {
        return mSharedSecretKey;
    }

    public void setSharedSecretKey(String sharedSecretKey) {
        mSharedSecretKey = sharedSecretKey;
    }

    public int getState() {
        return mState;
    }

    public void setState(int state) {
        mState = state;
    }
}
