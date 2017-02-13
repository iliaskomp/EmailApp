package com.iliaskomp.emailapp.EmailActivity;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iliaskomp.emailapp.Data.EmailDB;
import com.iliaskomp.emailapp.Data.EmailForInbox;
import com.iliaskomp.emailapp.InboxActivity.FormatHelper;
import com.iliaskomp.emailapp.R;

import java.util.UUID;

/**
 * Created by IliasKomp on 13/02/17.
 */

public class EmailFragment extends Fragment {
    private static final String ARG_EMAIL_ID = "email_id";

    private EmailForInbox mEmail;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID emailId = (UUID) getArguments().getSerializable(ARG_EMAIL_ID);
        mEmail = EmailDB.getEmailFromId(emailId);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_email, container, false);

        if (mEmail != null) {
            TextView emailSubjectTextView = (TextView) view.findViewById(R.id.email_text_view_subject);
            TextView emailSenderTextView = (TextView) view.findViewById(R.id.email_text_view_sender);
            TextView emailRecipientTextView = (TextView) view.findViewById(R.id.email_text_view_recipient);
            TextView emailDateTextView = (TextView) view.findViewById(R.id.email_text_view_date);
            TextView emailMessageTextView = (TextView) view.findViewById(R.id.email_text_view_message);
            TextView emailHeadersTextView = (TextView) view.findViewById(R.id.email_text_view_headers);

            emailSubjectTextView.setText(mEmail.getSubject());
            emailSenderTextView.setText(mEmail.getSender());
            emailRecipientTextView.setText(mEmail.getRecipient());
            emailDateTextView.setText(FormatHelper.formatDateForInbox(mEmail.getSentDate()));
            emailMessageTextView.setText(mEmail.getMessage());
            emailHeadersTextView.setText(mEmail.getHeadersText());
        }
        return view;
    }

    public static EmailFragment newInstance(UUID emailId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_EMAIL_ID, emailId);

        EmailFragment fragment = new EmailFragment();
        fragment.setArguments(args);

        return fragment;

    }
}
