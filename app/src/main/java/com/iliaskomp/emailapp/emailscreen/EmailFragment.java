package com.iliaskomp.emailapp.emailscreen;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.iliaskomp.emailapp.R;
import com.iliaskomp.emailapp.models.EmailDb;
import com.iliaskomp.emailapp.models.EmailModel;
import com.iliaskomp.emailapp.newmailscreen.NewMailActivity;
import com.iliaskomp.emailapp.utils.DateFormatHelper;

import java.util.UUID;

/**
 * Created by IliasKomp on 13/02/17.
 */

public class EmailFragment extends Fragment {
    private static final String ARG_EMAIL_ID = "email_id";
    private static final String DIALOG_HEADERS = "HeadersDialog";
    
    private EmailModel mEmail;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        UUID emailId = (UUID) getArguments().getSerializable(ARG_EMAIL_ID);
        mEmail = EmailDb.getEmailFromId(emailId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_email, container, false);

        if (mEmail != null) {
            TextView emailSubjectTextView = (TextView) view.findViewById(R.id.email_text_view_subject);
            TextView emailSenderTextView = (TextView) view.findViewById(R.id.email_text_view_sender);
            TextView emailRecipientTextView = (TextView) view.findViewById(R.id.email_text_view_recipient);
            TextView emailDateTextView = (TextView) view.findViewById(R.id.email_text_view_date);
            TextView emailMessageTextView = (TextView) view.findViewById(R.id.email_text_view_message);
            Button emailHeadersButton = (Button) view.findViewById(R.id.email_button_headers);

            emailSubjectTextView.setText(mEmail.getSubject());
            emailSenderTextView.setText(mEmail.getSender());
            emailRecipientTextView.setText(mEmail.getRecipient());
            emailDateTextView.setText(DateFormatHelper.getFormatttedDateStringFromFullDate(mEmail.getFullDate()));
            emailMessageTextView.setText(mEmail.getMessage());
            emailHeadersButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentManager manager = getFragmentManager();
                    EmailHeadersFragment dialog = EmailHeadersFragment.newInstance(mEmail.getId());
                    dialog.show(manager, DIALOG_HEADERS);
                }
            });
        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_email_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_email:
                Intent intent = NewMailActivity.newIntent(getActivity(), mEmail.getId());
                startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public static EmailFragment newInstance(UUID emailId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_EMAIL_ID, emailId);

        EmailFragment fragment = new EmailFragment();
        fragment.setArguments(args);

        return fragment;

    }
}
