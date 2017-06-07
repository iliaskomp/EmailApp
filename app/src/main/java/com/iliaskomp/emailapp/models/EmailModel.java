package com.iliaskomp.emailapp.models;

import com.iliaskomp.emailapp.utils.DateFormatHelper;

import java.util.Date;
import java.util.UUID;

/**
 * Created by elias on 11/02/17.
 */

public class EmailModel {
    private static final int STATE_NO_KOMP = 0;
    private static final int STATE_RECIPIENT_TO_SEND_BACK = 1;
//    private static final int STATE_RECIPIENT_HAS_SEND_BACK = 2;
    private static final int STATE_SENDER_TO_SEND_BACK = 3;
//    private static final int STATE_SENDER_HAS_SEND_BACK = 4;
    private static final int STATE_KOMP_COMPLETE = 5;


    private UUID mId;
    private String mSender;
    private String mRecipient;
    private String mSubject;
    private String mMessage;
    private Date mFullDate;
    private String mHeaders;

//    private UsersEncryptionEntry mEncryptionEntry = null;
    private int stateOfEncryption = STATE_NO_KOMP;

//    private HashMap<String, String> mHeadersMap;

//    public EmailModel(String mSender, String mRecipient, String mSubject, String mMessage, String mHeadersMap) {
//        this.mSender = mSender;
//        this.mRecipient = mRecipient;
//        this.mSubject = mSubject;
//        this.mMessage = mMessage;
//        this.mHeadersMap = mHeadersMap;
//        mId = UUID.randomUUID();
//
//    }

    public EmailModel() {
        mId = UUID.randomUUID();
    }

    public EmailModel(UUID id) {
        mId = id;
    }

    @Override
    public String toString() {
        return "EMAIL INFO" +
                "\nSender: " + mSender +
                "\nRecipient: " + mRecipient +
                "\nSubject: " + mSubject +
                "\nMessage: " + mMessage +
//                "\nHeaders: " + mHeaders +
                "\nSent Date: " + mFullDate;
    }

    public String getRecipient() {
        return mRecipient;
    }

    public String getSubject() {
        return mSubject;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setRecipient(String recipient) {
        this.mRecipient = recipient;
    }

    public void setSubject(String subject) {
        this.mSubject = subject;
    }

    public void setMessage(String message) {
        this.mMessage = message;
    }

    public String getSender() {
        return mSender;
    }

    public void setSender(String sender) {
        this.mSender = sender;
    }

    public String getHeaders() {
        return mHeaders;
    }

    public void setHeaders(String headers) {
        mHeaders = headers;
    }

    public Date getFullDate() {
        return mFullDate;
    }

    public void setFullDate(Date fullDate) {
        this.mFullDate = fullDate;
    }

    public String getFormattedDateString() {
        return DateFormatHelper.getFormatttedDateStringFromFullDate(mFullDate);
    }

    public Date getFormattedDate() {
        return DateFormatHelper.getFormattedDateFromFullDate(mFullDate);
    }

    public UUID getId() {
        return mId;
    }

    // returns null if email model doesn't have an encryption entry set
//    public UsersEncryptionEntry getEncryptionEntry() {
//        return mEncryptionEntry;
//    }

//    public void setEncryptionEntry(UsersEncryptionEntry encryptionEntry) {
//        mEncryptionEntry = encryptionEntry;
//    }

    public int getStateOfEncryption() {
        return stateOfEncryption;
    }

    public void setStateOfEncryption(int stateOfEncryption) {
        this.stateOfEncryption = stateOfEncryption;
    }
}
