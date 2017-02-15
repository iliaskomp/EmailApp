package com.iliaskomp.emailapp.network;

import com.iliaskomp.emailapp.models.InboxEmail;

import java.util.List;

/**
 * Created by elias on 11/02/17.
 */

public interface AsyncResponseForFetchEmail {
    void processFinish(List<InboxEmail> emails);
}
