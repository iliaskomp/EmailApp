package com.iliaskomp.email;

import java.util.Enumeration;
import java.util.HashMap;

import javax.mail.Header;

/**
 * Created by IliasKomp on 28/03/17.
 */

public class EmailHelper {
    public static HashMap<String, String> getHeadersMapFromEnumeration(Enumeration headers) {
        HashMap<String, String> headersMap = new HashMap<>();
        while (headers.hasMoreElements()) {
            Header h = (Header) headers.nextElement();
            headersMap.put(h.getName(), h.getValue());
        }
        return headersMap;
    }
}
