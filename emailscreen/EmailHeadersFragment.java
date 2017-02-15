package com.iliaskomp.emailapp.emailscreen;


import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.iliaskomp.emailapp.R;

import java.util.UUID;

/**
 * Created by IliasKomp on 14/02/17.
 */

public class EmailHeadersFragment extends DialogFragment {
    private static final String ARG_EMAIL_ID = "com.iliaskomp.email_id";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_headers, null);

        UUID emailId = (UUID) getArguments().getSerializable(ARG_EMAIL_ID);

        TextView headersText = (TextView) v.findViewById(R.id.text_view_headers);
//        TODO static context email screen
//        headersText.setText(EmailDB.getEmailFromId(emailId).getHeadersText());

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.headers_dialog_title)
                .setPositiveButton(android.R.string.ok, null)
                .create();
    }

    public static EmailHeadersFragment newInstance(UUID emailId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_EMAIL_ID, emailId);

        EmailHeadersFragment fragment = new EmailHeadersFragment();
        fragment.setArguments(args);

        return fragment;

    }
}
