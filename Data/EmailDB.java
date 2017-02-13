package com.iliaskomp.emailapp.Data;

import java.util.List;
import java.util.UUID;

/**
 * Created by IliasKomp on 13/02/17.
 */

public class EmailDB {
    private static List<EmailForInbox> sEmails;

    public EmailDB() {

    }

    public static void set(List<EmailForInbox> emails) {
        sEmails = emails;
    }

    public static EmailForInbox getEmailFromId (UUID id) {
        for (EmailForInbox email : sEmails) {
            if (email.getId().equals(id)) {
                return email;
            }
        }
        return null;
    }

    public static List<EmailForInbox> getEmails() {
        return sEmails;
    }
}
