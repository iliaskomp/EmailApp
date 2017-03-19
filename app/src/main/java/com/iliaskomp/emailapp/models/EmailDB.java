package com.iliaskomp.emailapp.models;

import java.util.List;
import java.util.UUID;

/**
 * Created by IliasKomp on 19/03/17.
 */

interface EmailDB {

    EmailModel getEmailFromId (UUID id);

    List<EmailModel> getEmails();

    void addEmail(EmailModel email);

    int getEmailCount();


}
