package com.iliaskomp.emailapp.network;

import com.iliaskomp.emailapp.models.EmailDb;

/**
 * Created by elias on 11/02/17.
 */

public interface AsyncResponseForFetchEmail {
    void processFinish(EmailDb db);
}
