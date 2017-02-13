package com.iliaskomp.emailapp.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.iliaskomp.emailapp.Data.Config;
import com.iliaskomp.emailapp.Data.EmailForInbox;
import com.iliaskomp.emailapp.Functionality.AsyncResponseForFetchEmail;
import com.iliaskomp.emailapp.Functionality.FetchMail;
import com.iliaskomp.emailapp.InboxHelper;
import com.iliaskomp.emailapp.R;

import java.util.List;

/**
 * Created by IliasKomp on 13/02/17.
 */

public class InboxFragment extends Fragment implements AsyncResponseForFetchEmail {
    private RecyclerView mInboxRecyclerView;
    private EmailAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inbox, container, false);
        mInboxRecyclerView = (RecyclerView) view.findViewById(R.id.inbox_recycler_view);
        mInboxRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        fetchMail();
//        updateUI();

        return view;
    }
//
//    private void updateUI() {
//        mAdapter = new EmailAdapter(mEmails);
//        mInboxRecyclerView.setAdapter(mAdapter);
//    }

    @Override
    public void processFinish(List<EmailForInbox> emails) {
        mAdapter = new EmailAdapter(emails);
        mInboxRecyclerView.setAdapter(mAdapter);
    }

        private void fetchMail() {
        FetchMail fetchMail = new FetchMail(getActivity(), Config.IMAP_NAME);
//        fm = new FetchMail(InboxActivity.this, "imap", "imap.gmail.com", "993");
        fetchMail.delegate = this;
        fetchMail.execute();

    }

    private class EmailHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private EmailForInbox mEmail;

        private TextView mTextViewSender;
        private TextView mTextViewDate;
        private TextView mTextViewSubject;
        private TextView mTextViewMessage;

        public EmailHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mTextViewSender = (TextView) itemView.findViewById(R.id.textViewSender);
            mTextViewDate = (TextView) itemView.findViewById(R.id.textViewDate);
            mTextViewSubject = (TextView) itemView.findViewById(R.id.textViewSubject);
            mTextViewMessage = (TextView) itemView.findViewById(R.id.textViewMessage);
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(getActivity(),
                    mEmail.getSubject() + " clicked!", Toast.LENGTH_SHORT)
                    .show();
        }

        public void bindEmail(EmailForInbox email) {
            mEmail = email;

            mTextViewSender.setText(mEmail.getSender());
            mTextViewDate.setText(InboxHelper.formatDateForInbox(mEmail.getSentDate()));
            mTextViewSubject.setText(mEmail.getSubject());
            mTextViewMessage.setText(InboxHelper.formatMessageShortForInbox(mEmail.getMessage()));
        }
    }

    private class EmailAdapter extends RecyclerView.Adapter<EmailHolder> {
        private List<EmailForInbox> mEmails;

        public EmailAdapter(List<EmailForInbox> emails) {
            mEmails = emails;
        }

        @Override
        public EmailHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_email, parent, false);

            return new EmailHolder(view);
        }

        @Override
        public void onBindViewHolder(EmailHolder holder, int position) {
            EmailForInbox email = mEmails.get(position);
            holder.bindEmail(email);
        }

        @Override
        public int getItemCount() {
            return mEmails.size();
        }
    }


}