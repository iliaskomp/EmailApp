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
 * Created by IliasKomp on 14/02/17.
 */

public class EmailPagerActivity extends AppCompatActivity {
    private static final String EXTRA_EMAIL_ID = "com.iliaskomp.email_id";

    private ViewPager mViewPager;
    private List<EmailModel> mEmails;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_pager);

        UUID emailId = (UUID) getIntent().getSerializableExtra(EXTRA_EMAIL_ID);

        mViewPager = (ViewPager) findViewById(R.id.activity_email_pager_view_pager);
        mEmails = InboxDB.get(this).getEmails();

        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                EmailModel email = mEmails.get(position);
                return EmailFragment.newInstance(email.getId());
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


    public static Intent newIntent(Context contextPackage, UUID emailId) {
        Intent intent = new Intent(contextPackage, EmailPagerActivity.class);
        intent.putExtra(EXTRA_EMAIL_ID, emailId);
        return intent;
    }
}
