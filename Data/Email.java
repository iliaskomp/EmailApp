package com.iliaskomp.emailapp.Data;

/**
 * Created by iliaskomp on 11/02/17.
 */

public class Email {
    private String recipient;
    private String subject;
    private String message;

    public Email(String recipient, String subject, String message) {
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
}
