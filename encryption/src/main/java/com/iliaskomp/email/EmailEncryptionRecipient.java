package com.iliaskomp.email;

import java.util.HashMap;
import java.util.Map;

import javax.mail.Message;
import javax.mail.MessagingException;

/**
 * Created by IliasKomp on 22/05/17.
 */

public class EmailEncryptionRecipient {

    // if encryption state is not found, it returns null
    public static String getHeaderState(Message message) throws MessagingException {
        HashMap<String, String> headers = EmailHelper.getHeadersMapFromEnumeration(message.getAllHeaders());

        for (Map.Entry<String, String> header : headers.entrySet()) {
            if (header.getKey().equals(HeaderFields.HeaderX.STATE)) {
                return header.getValue();
            }
        }

        return null;
    }
}
