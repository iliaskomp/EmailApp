package com.iliaskomp.emailapp.utils;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.mail.Header;

/**
 * Created by IliasKomp on 15/02/17.
 */
public class HeadersFormatHelper {
    public static HashMap<String, String> getHeadersMapFromEnumeration(Enumeration headers) {
        HashMap<String, String> headersMap = new HashMap<>();
        while (headers.hasMoreElements()) {
            Header h = (Header) headers.nextElement();
            headersMap.put(h.getName(), h.getValue());
        }
        return headersMap;
    }

    public static String getHeadersStringTextFromMap(HashMap<String, String> headersMap) {
        String output = "";

        for (Map.Entry<String,String> entry : headersMap.entrySet()) {
            output += entry.getKey() + ": " + entry.getValue() + "\n";
        }

        return output;
    }

    public static String getHeadersStringFromEnumeration(Enumeration headersEnum) {
        return getHeadersStringTextFromMap(getHeadersMapFromEnumeration(headersEnum));
    }
}
