package com.iliaskomp.emailapp.Data;

import java.util.Date;

/**
 * Created by elias on 11/02/17.
 */

public class EmailForInbox {
    private String sender;
    private String recipient;
    private String subject;
    private String message;
    private String headers;

    private Date sentDate;

    public EmailForInbox(String sender, String recipient, String subject, String message, String headers) {
        this.sender = sender;
        this.recipient = recipient;
        this.subject = subject;
        this.message = message;
        this.headers = headers;
    }

    public EmailForInbox() {

    }

    @Override
    public String toString() {
        return "EMAIL INFO" +
                "\nSender: " + sender +
                "\nRecipient: " + recipient +
                "\nSubject: " + subject +
                "\nMessage: " + message +
                "\nHeaders: " + headers +
                "\nSent Date: " + sentDate;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getSubject() {
        return subject;
    }

    public String getMessage() {
        return message;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getHeaders() {
        return headers;
    }

    public void setHeaders(String headers) {
        this.headers = headers;
    }

    public Date getSentDate() {
        return sentDate;
    }

    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }

}
