package com.iliaskomp.emailapp.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by elias on 11/02/17.
 */

public class InboxEmail {
    private UUID id;
    private String sender;
    private String recipient;
    private String subject;
    private String message;
    private Date sentDate;
    private HashMap<String, String> headers;

//    public InboxEmail(String sender, String recipient, String subject, String message, String headers) {
//        this.sender = sender;
//        this.recipient = recipient;
//        this.subject = subject;
//        this.message = message;
//        this.headers = headers;
//        id = UUID.randomUUID();
//
//    }

    public InboxEmail() {
        id = UUID.randomUUID();
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

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(HashMap<String, String> headers) {
        this.headers = headers;
    }

    public Date getSentDate() {
        return sentDate;
    }

    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }

    public UUID getId() {
        return id;
    }

    public String getHeadersText() {
        String output = "";

        for (Map.Entry<String,String> entry : headers.entrySet()) {
            output += entry.getKey() + ": " + entry.getValue() + "\n";
        }

        return output;
    }
}
