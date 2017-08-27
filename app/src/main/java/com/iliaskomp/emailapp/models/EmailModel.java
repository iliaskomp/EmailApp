package com.iliaskomp.emailapp.models;

import com.iliaskomp.emailapp.utils.DateFormatHelper;

import java.util.Date;
import java.util.UUID;

/**
 * Email Model used for this application
 */
public class EmailModel {
//    private static final int STATE_RECIPIENT_TO_SEND_BACK = 1;
//    private static final int STATE_RECIPIENT_HAS_SEND_BACK = 2;
//    private static final int STATE_SENDER_TO_SEND_BACK = 3;
//    private static final int STATE_SENDER_HAS_SEND_BACK = 4;

    private static final int STATE_NO_KOMP = 1;
    private static final int STATE_KOMP_TO_SEND_BACK = 2;
    private static final int STATE_KOMP_COMPLETE = 3;


    private UUID mId;
    private String mSender;
    private String mRecipient;
    private String mSubject;
    private String mMessage;
    private Date mFullDate;
    private String mHeaders;

//    private UsersEncryptionEntry mEncryptionEntry = null;
    private int stateOfEncryption = STATE_NO_KOMP;

    /**
     * Instantiates a new Email model.
     */
    public EmailModel() {
        mId = UUID.randomUUID();
    }

    /**
     * Instantiates a new Email model with an defined id.
     *
     * @param id the id
     */
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

    /**
     * Gets recipient.
     *
     * @return the recipient
     */
    public String getRecipient() {
        return mRecipient;
    }

    /**
     * Gets subject.
     *
     * @return the subject
     */
    public String getSubject() {
        return mSubject;
    }

    /**
     * Gets message.
     *
     * @return the message
     */
    public String getMessage() {
        return mMessage;
    }

    /**
     * Sets recipient.
     *
     * @param recipient the recipient
     */
    public void setRecipient(String recipient) {
        this.mRecipient = recipient;
    }

    /**
     * Sets subject.
     *
     * @param subject the subject
     */
    public void setSubject(String subject) {
        this.mSubject = subject;
    }

    /**
     * Sets message.
     *
     * @param message the message
     */
    public void setMessage(String message) {
        this.mMessage = message;
    }

    /**
     * Gets sender.
     *
     * @return the sender
     */
    public String getSender() {
        return mSender;
    }

    /**
     * Sets sender.
     *
     * @param sender the sender
     */
    public void setSender(String sender) {
        this.mSender = sender;
    }

    /**
     * Gets headers.
     *
     * @return the headers
     */
    public String getHeaders() {
        return mHeaders;
    }

    /**
     * Sets headers.
     *
     * @param headers the headers
     */
    public void setHeaders(String headers) {
        mHeaders = headers;
    }

    /**
     * Gets full date.
     *
     * @return the full date
     */
    public Date getFullDate() {
        return mFullDate;
    }

    /**
     * Sets full date.
     *
     * @param fullDate the full date
     */
    public void setFullDate(Date fullDate) {
        this.mFullDate = fullDate;
    }

    /**
     * Gets formatted date string.
     *
     * @return the formatted date string
     */
    public String getFormattedDateString() {
        return DateFormatHelper.getFormatttedDateStringFromFullDate(mFullDate);
    }

    /**
     * Gets formatted date.
     *
     * @return the formatted date
     */
    public Date getFormattedDate() {
        return DateFormatHelper.getFormattedDateFromFullDate(mFullDate);
    }

    /**
     * Gets id.
     *
     * @return the id
     */
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

    /**
     * Gets state of encryption.
     *
     * @return the state of encryption
     */
    public int getStateOfEncryption() {
        return stateOfEncryption;
    }

    /**
     * Sets state of encryption.
     *
     * @param stateOfEncryption the state of encryption
     */
    public void setStateOfEncryption(int stateOfEncryption) {
        this.stateOfEncryption = stateOfEncryption;
    }
}
