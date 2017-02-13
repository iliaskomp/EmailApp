package com.iliaskomp.emailapp.InboxActivity;

import android.support.v4.app.Fragment;

import com.iliaskomp.emailapp.Activities.SingleFragmentActivity;

/**
 * Created by iliaskomp on 11/02/17.
 */

public class InboxActivity extends SingleFragmentActivity {
    private static final String LOG_TAG = "InboxActivity";

    @Override
    protected Fragment createFragment() {
        return new InboxFragment();
    }

}
