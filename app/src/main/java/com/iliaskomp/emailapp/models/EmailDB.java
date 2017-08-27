package com.iliaskomp.emailapp.models;

import java.util.List;
import java.util.UUID;

/**
 * Database storing emails.
 */
public interface EmailDB {

    /**
     * Gets email from id.
     *
     * @param id the id
     * @return the email from id
     */
    EmailModel getEmailFromId (UUID id);

    /**
     * Gets emails.
     *
     * @return the emails
     */
    List<EmailModel> getEmails();

    /**
     * Add email.
     *
     * @param email the email
     */
    void addEmail(EmailModel email);

    /**
     * Gets email count.
     *
     * @return the email count
     */
    int getEmailCount();


}
