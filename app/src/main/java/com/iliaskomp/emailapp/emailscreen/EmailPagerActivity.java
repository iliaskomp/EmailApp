package com.iliaskomp.emailapp.emailscreen;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.iliaskomp.emailapp.R;
import com.iliaskomp.emailapp.models.InboxDB;
import com.iliaskomp.emailapp.models.EmailModel;

import java.util.List;
import java.util.UUID;

/**
 * Email pager activity that calls the fragment for presenting the screen.
 * Additional functionality is that the user can swipe left and right to go the previous or next email.
 */
public class EmailPagerActivity extends AppCompatActivity {
    private static final String EXTRA_EMAIL_ID = "com.iliaskomp.email_id";
    private static final String EXTRA_EMAIL_NAME = "com.iliaskomp.email_name";
    private static final String EXTRA_EMAIL_PASSWORD = "com.iliaskomp.email_password";

    private ViewPager mViewPager;
    private List<EmailModel> mEmails;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_pager);

        UUID emailId = (UUID) getIntent().getSerializableExtra(EXTRA_EMAIL_ID);
        final String emailName = getIntent().getStringExtra(EXTRA_EMAIL_NAME);
        final String password = getIntent().getStringExtra(EXTRA_EMAIL_PASSWORD);

        mViewPager = (ViewPager) findViewById(R.id.activity_email_pager_view_pager);
        mEmails = InboxDB.get(this).getEmails();

        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                EmailModel email = mEmails.get(position);
                return EmailFragment.newInstance(email.getId(), emailName, password);
            }

            @Override
            public int getCount() {
                return mEmails.size();
            }
        });

        for (int i = 0; i < mEmails.size(); i++) {
            if (mEmails.get(i).getId().equals(emailId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }


    /**
     * Static new intent method.
     * Other classes call this method when they send an intent to this class.
     *
     * @param contextPackage the context package
     * @param emailId        the email id
     * @param emailName      the email name
     * @param password       the password
     * @return the intent
     */
    public static Intent newIntent(Context contextPackage, UUID emailId, String emailName, String password) {
        Intent intent = new Intent(contextPackage, EmailPagerActivity.class);
        intent.putExtra(EXTRA_EMAIL_ID, emailId);
        intent.putExtra(EXTRA_EMAIL_NAME, emailName);
        intent.putExtra(EXTRA_EMAIL_PASSWORD, password);

        return intent;
    }
}
