package com.iliaskomp.emailapp.inboxscreen;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iliaskomp.emailapp.models.EmailDb;
import com.iliaskomp.emailapp.models.EmailModel;
import com.iliaskomp.emailapp.newmailscreen.NewMailActivity;
import com.iliaskomp.emailapp.utils.Config;
import com.iliaskomp.emailapp.emailscreen.EmailPagerActivity;
import com.iliaskomp.emailapp.network.AsyncResponseForFetchEmail;
import com.iliaskomp.emailapp.network.FetchMail;
import com.iliaskomp.emailapp.R;
import com.iliaskomp.emailapp.utils.DateFormatHelper;

import java.util.List;

/**
 * Created by IliasKomp on 13/02/17.
 */

public class EmailListFragment extends Fragment implements AsyncResponseForFetchEmail {
    private static final String LOG_CAT = "EmailListFragment";

    private RecyclerView mInboxRecyclerView;
    private EmailAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_email_list, container, false);

        mInboxRecyclerView = (RecyclerView) view.findViewById(R.id.inbox_recycler_view);
        mInboxRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        fetchMail();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchMail();
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
                Intent intent = NewMailActivity.newIntent(getActivity());
                startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void processFinish(EmailDb db) {
        mAdapter = new EmailAdapter(db.getEmails());
        mInboxRecyclerView.setAdapter(mAdapter);
    }

//    public void updateUI(List<EmailModel> emails) {
//        mAdapter = new EmailAdapter(emails);
//        mInboxRecyclerView.setAdapter(mAdapter);
//    }

    private void fetchMail() {
        FetchMail fetchMail = new FetchMail(getActivity(), Config.IMAP_NAME);
        fetchMail.delegate = this;
        fetchMail.execute();
    }


    private class EmailHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private EmailModel mEmail;

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
            Intent intent = EmailPagerActivity.newIntent(getActivity(), mEmail.getId());
            startActivity(intent);
        }

        public void bindEmail(EmailModel email) {
            mEmail = email;

            mTextViewSender.setText(mEmail.getSender());
            mTextViewDate.setText(DateFormatHelper.getFormatttedDateStringFromFullDate(mEmail.getFullDate()));
            mTextViewSubject.setText(mEmail.getSubject());
            mTextViewMessage.setText(InboxHelper.formatMessageShortForInbox(mEmail.getMessage()));
        }
    }



    private class EmailAdapter extends RecyclerView.Adapter<EmailHolder> {
        private List<EmailModel> mEmails;

        public EmailAdapter(List<EmailModel> emails) {
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
            EmailModel email = mEmails.get(position);
            holder.bindEmail(email);
        }

        @Override
        public int getItemCount() {
            return mEmails.size();
        }

        public void setEmails(List<EmailModel> emails) {
            mEmails = emails;
        }
    }


}
