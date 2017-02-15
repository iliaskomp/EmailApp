package com.iliaskomp.emailapp.Models;

import java.util.List;
import java.util.UUID;

/**
 * Created by IliasKomp on 13/02/17.
 */

public class EmailDB {
    private static List<InboxEmail> sEmails;

    public EmailDB() {

    }

    public static void set(List<InboxEmail> emails) {
        sEmails = emails;
    }

    public static InboxEmail getEmailFromId (UUID id) {
        for (InboxEmail email : sEmails) {
            if (email.getId().equals(id)) {
                return email;
            }
        }
        return null;
    }

    public static List<InboxEmail> getEmails() {
        return sEmails;
    }
}
