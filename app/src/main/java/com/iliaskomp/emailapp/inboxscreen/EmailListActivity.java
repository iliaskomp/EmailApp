package com.iliaskomp.emailapp.inboxscreen;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.iliaskomp.emailapp.R;
import com.iliaskomp.emailapp.SingleFragmentActivity;
import com.iliaskomp.emailapp.emailscreen.EmailFragment;
import com.iliaskomp.emailapp.emailscreen.EmailPagerActivity;
import com.iliaskomp.emailapp.models.EmailModel;

/**
 * Email List Activity
 * Calls the Email List Fragment for the screen.
 */
public class EmailListActivity extends SingleFragmentActivity implements EmailListFragment.Callbacks {
    private static final String EXTRA_FOLDER_NAME = "com.iliaskomp.emailapp.foldername";

    private static String mFolderName;
    private static String mEmail;
    private static String mPassword;

    @Override
    protected Fragment createFragment() {
        return EmailListFragment.newInstance(mFolderName, mEmail, mPassword);
    }

    @Override
    protected int getLayoutRedId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onEmailSelected(EmailModel email) {
        if (findViewById(R.id.detail_fragment_container) == null) {
            Intent intent = EmailPagerActivity.newIntent(this, email.getId(), mEmail, mPassword);
            startActivity(intent);
        } else {
            Fragment newDetail = EmailFragment.newInstance(email.getId(), mEmail, mPassword);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                    .commit();
        }
    }

    /**
     * Static new intent method.
     * Other classes call this method when they send an intent to this class.
     *
     * @param context    the context
     * @param folderName the folder name
     * @param email      the email
     * @param password   the password
     * @return the intent
     */
    public static Intent newIntent(Context context, String folderName, String email, String password) {
        mFolderName = folderName;
        mEmail = email;
        mPassword = password;

        Intent intent = new Intent(context, EmailListActivity.class);
        intent.putExtra(EXTRA_FOLDER_NAME, folderName);
        return intent;
    }
}
