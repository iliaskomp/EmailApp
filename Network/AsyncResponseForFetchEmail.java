package com.iliaskomp.emailapp.Network;

import com.iliaskomp.emailapp.Models.InboxEmail;

import java.util.List;

/**
 * Created by elias on 11/02/17.
 */

public interface AsyncResponseForFetchEmail {
    void processFinish(List<InboxEmail> emails);
}
