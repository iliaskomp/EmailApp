package com.iliaskomp.email;

import com.iliaskomp.encryption.DHHelper;

import java.security.PublicKey;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;

import static com.iliaskomp.email.HeaderFields.HeaderX.PUBLIC_KEY_RECIPIENT;
import static com.iliaskomp.email.HeaderFields.HeaderX.PUBLIC_KEY_SENDER;

/**
 * Created by IliasKomp on 22/06/17.
 */

public class HeaderUtils {
    // if header encryption state is not found, it returns HeaderX.NO_HEADER_STRING
    public static String getHeaderState(Message message) throws MessagingException {
        HashMap<String, String> headers = getHeadersMapFromEnumeration(message
                .getAllHeaders());

        for (Map.Entry<String, String> header : headers.entrySet()) {
            if (header.getKey().equals(HeaderFields.HeaderX.STATE)) {
                return header.getValue();
            }
        }
        return HeaderFields.HeaderX.NO_HEADER_STRING;
    }

    public static String getHeaderIv(Message message) throws MessagingException {
        HashMap<String, String> headers = getHeadersMapFromEnumeration(message
                .getAllHeaders());

        for (Map.Entry<String, String> header : headers.entrySet()) {
            if (header.getKey().equals(HeaderFields.HeaderX.IV)) {
                return header.getValue();
            }
        }
        return HeaderFields.HeaderX.NO_HEADER_STRING;
    }

    public static PublicKey getHeaderSenderPublicKey(Message message) throws MessagingException {
        HashMap<String, String> headers =
                getHeadersMapFromEnumeration(message.getAllHeaders());

        for (Map.Entry<String, String> header : headers.entrySet()) {
            if (header.getKey().equals(PUBLIC_KEY_SENDER)) {
                String publicKeyString = header.getValue();
                return DHHelper.PublicKeyClass.stringToKey(publicKeyString);
            }
        }
        return null;
    }

    public static PublicKey getHeaderRecipientPublicKey(Message message) throws MessagingException {
        HashMap<String, String> headers =
                getHeadersMapFromEnumeration(message.getAllHeaders());

        for (Map.Entry<String, String> header : headers.entrySet()) {
            if (header.getKey().equals(PUBLIC_KEY_RECIPIENT)) {
                String publicKeyString = header.getValue();
                return DHHelper.PublicKeyClass.stringToKey(publicKeyString);
            }
        }
        return null;
    }

    public static HashMap<String, String> getHeadersMapFromEnumeration(Enumeration headers) {
        HashMap<String, String> headersMap = new HashMap<>();
        while (headers.hasMoreElements()) {
            Header h = (Header) headers.nextElement();
            headersMap.put(h.getName(), h.getValue());
        }
        return headersMap;
    }
}
