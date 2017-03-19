package com.iliaskomp.emailapp.network;

import com.iliaskomp.emailapp.models.InboxDB;

/**
 * Created by elias on 11/02/17.
 */

public interface AsyncResponseForFetchEmail {
    void processFinish(InboxDB db);
}
