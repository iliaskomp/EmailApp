package com.iliaskomp.emailapp.inboxscreen;

import android.support.v4.app.Fragment;

import com.iliaskomp.emailapp.SingleFragmentActivity;

/**
 * Created by iliaskomp on 11/02/17.
 */

public class EmailListActivity extends SingleFragmentActivity {
    private static final String LOG_TAG = "EmailListActivity";

    @Override
    protected Fragment createFragment() {
        return new EmailListFragment();
    }

}
