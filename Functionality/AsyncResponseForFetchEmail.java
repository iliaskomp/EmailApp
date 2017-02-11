package com.iliaskomp.emailapp.Functionality;

import com.iliaskomp.emailapp.Data.EmailForInbox;

import java.util.List;

/**
 * Created by elias on 11/02/17.
 */

public interface AsyncResponseForFetchEmail {
    void processFinish(List<EmailForInbox> emails);
}
