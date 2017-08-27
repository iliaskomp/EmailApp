package com.iliaskomp.emailapp.utils;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.mail.Header;

/**
 * Helper for header formatting
 */
public class HeadersFormatHelper {
    /**
     * Gets headers map from enumeration of headers.
     *
     * @param headers the headers
     * @return the headers' map
     */
    public static HashMap<String, String> getHeadersMapFromEnumeration(Enumeration headers) {
        HashMap<String, String> headersMap = new HashMap<>();
        while (headers.hasMoreElements()) {
            Header h = (Header) headers.nextElement();
            headersMap.put(h.getName(), h.getValue());
        }
        return headersMap;
    }

    /**
     * Gets headers as a string from a headers' map.
     *
     * @param headersMap the headers' map
     * @return the headers as a string
     */
    public static String getHeadersStringTextFromMap(HashMap<String, String> headersMap) {
        String output = "";

        for (Map.Entry<String,String> entry : headersMap.entrySet()) {
            output += entry.getKey() + ": " + entry.getValue() + "\n";
        }

        return output;
    }

    /**
     * Gets headers' string from enumeration.
     *
     * @param headersEnum the headers enum
     * @return the headers as a string
     */
    public static String getHeadersStringFromEnumeration(Enumeration headersEnum) {
        return getHeadersStringTextFromMap(getHeadersMapFromEnumeration(headersEnum));
    }
}
