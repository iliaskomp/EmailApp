package com.iliaskomp.emailapp.models;

/**
 * Created by iliaskomp on 11/02/17.
 */

public class EmailToSend {
    private String recipient;
    private String subject;
    private String message;

    private String headers;


    public EmailToSend(String recipient, String subject, String message) {
        this.recipient = recipient;
        this.subject = subject;
        this.message = message;
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
}
