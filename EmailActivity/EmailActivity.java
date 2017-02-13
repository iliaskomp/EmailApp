package com.iliaskomp.emailapp.EmailActivity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.iliaskomp.emailapp.Activities.SingleFragmentActivity;
import com.iliaskomp.emailapp.Data.EmailDB;
import com.iliaskomp.emailapp.Data.EmailForInbox;

import java.util.UUID;

public class EmailActivity extends SingleFragmentActivity {
    private static final String EXTRA_EMAIL_ID = "com.iliaskomp.email_id";
    @Override
    protected Fragment createFragment() {
        UUID emailId = (UUID) getIntent().getSerializableExtra(EXTRA_EMAIL_ID);
        return EmailFragment.newInstance(emailId);
    }

    public static Intent newIntent(Context contextPackage, UUID emailId) {
        EmailForInbox email = EmailDB.getEmailFromId(emailId);
        Intent intent = new Intent(contextPackage, EmailActivity.class);
        intent.putExtra(EXTRA_EMAIL_ID, emailId);
        return intent;
    }
}
