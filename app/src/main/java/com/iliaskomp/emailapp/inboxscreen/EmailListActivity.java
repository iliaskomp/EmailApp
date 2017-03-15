package com.iliaskomp.emailapp.inboxscreen;

import android.content.Intent;
import android.support.v4.app.Fragment;

import com.iliaskomp.emailapp.R;
import com.iliaskomp.emailapp.SingleFragmentActivity;
import com.iliaskomp.emailapp.emailscreen.EmailFragment;
import com.iliaskomp.emailapp.emailscreen.EmailPagerActivity;
import com.iliaskomp.emailapp.models.EmailModel;

/**
 * Created by iliaskomp on 11/02/17.
 */

public class EmailListActivity extends SingleFragmentActivity implements EmailListFragment.Callbacks {
    private static final String LOG_TAG = "EmailListActivity";

    @Override
    protected Fragment createFragment() {
        return new EmailListFragment();
    }

    @Override
    protected int getLayoutRedId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onEmailSelected(EmailModel email) {
        if (findViewById(R.id.detail_fragment_container) == null) {
            Intent intent = EmailPagerActivity.newIntent(this, email.getId());
            startActivity(intent);
        } else {
            Fragment newDetail = EmailFragment.newInstance(email.getId());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                    .commit();
        }
    }
}