package com.iliaskomp.emailapp.inboxscreen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iliaskomp.emailapp.R;
import com.iliaskomp.emailapp.database.email.EmailDbSchema;
import com.iliaskomp.emailapp.models.EmailDB;
import com.iliaskomp.emailapp.models.EmailModel;
import com.iliaskomp.emailapp.models.InboxDB;
import com.iliaskomp.emailapp.models.SentDB;
import com.iliaskomp.emailapp.network.AsyncResponseForFetchEmail;
import com.iliaskomp.emailapp.network.FetchMail;
import com.iliaskomp.emailapp.newmailscreen.NewMailActivity;
import com.iliaskomp.emailapp.utils.Config;
import com.iliaskomp.emailapp.utils.DateFormatHelper;

import java.util.List;

/**
 * Created by IliasKomp on 13/02/17.
 */

public class EmailListFragment extends Fragment implements AsyncResponseForFetchEmail {
    private static final String ARGS_FOLDER = "folderName";
    private static final String ARGS_EMAIL = "email";
    private static final String ARGS_PASSWORD = "password";

    private RecyclerView mEmailListRecyclerView;
    private EmailAdapter mAdapter;
    private Callbacks mCallbacks;
    private static String mFolderName;
    private static String mEmail;
    private static String mPassword;


    public interface Callbacks {
        void onEmailSelected(EmailModel email);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFolderName = getArguments().getString(ARGS_FOLDER);
        mEmail = getArguments().getString(ARGS_EMAIL);
        mPassword = getArguments().getString(ARGS_PASSWORD);

        if (getActivity().findViewById(R.id.detail_fragment_container) == null) {
            setHasOptionsMenu(true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_email_list, container, false);

        mEmailListRecyclerView = (RecyclerView) view.findViewById(R.id.inbox_recycler_view);
        mEmailListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        TextView textViewTitle = (TextView) view.findViewById(R.id.text_view_email_list_title);
        if (mFolderName.equals(EmailDbSchema.InboxTable.NAME)) {
            textViewTitle.setText(R.string.folder_name_inbox);
        } else if (mFolderName.equals(EmailDbSchema.SentTable.NAME)) {
            textViewTitle.setText(R.string.folder_name_sent);
        }

        fetchMail();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
//        fetchMail();
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
                Intent intent = NewMailActivity.newIntent(getActivity(), null, mEmail, mPassword);
                startActivity(intent);
                break;
            case R.id.menu_item_refresh:
                fetchMail();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void processFinish(EmailDB db) {
        Log.d("FetchMail", "EmailListFragment processFinish");

        if (mFolderName.equals(EmailDbSchema.InboxTable.NAME)) {
            mAdapter = new EmailAdapter(InboxDB.get(getActivity()).getEmails());
        } else if (mFolderName.equals(EmailDbSchema.SentTable.NAME)) {
            mAdapter = new EmailAdapter(SentDB.get(getActivity()).getEmails());
        }

        mEmailListRecyclerView.setAdapter(mAdapter);
    }

//    public void updateUI(List<EmailModel> emails) {
//        mAdapter = new EmailAdapter(emails);
//        mEmailListRecyclerView.setAdapter(mAdapter);
//    }

    private void fetchMail() {
        FetchMail fetchMail = new FetchMail(getActivity(), Config.Name.IMAP);
        fetchMail.delegate = this;
        fetchMail.execute(mFolderName);
    }

    public static EmailListFragment newInstance(String folder, String email, String password) {
        EmailListFragment fragment = new EmailListFragment();
        Bundle args = new Bundle();
        args.putString(ARGS_FOLDER, folder);
        args.putString(ARGS_EMAIL, email);
        args.putString(ARGS_PASSWORD, password);

        fragment.setArguments(args);
        return fragment;
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
            mCallbacks.onEmailSelected(mEmail);
//            Fragment fragment = EmailFragment.newInstance(mEmail.getId());
//            FragmentManager fm = getActivity().getSupportFragmentManager();
//            fm.beginTransaction()
//                    .add(R.id.detail_fragment_container, fragment)
//                    .commit();

//            Intent intent = EmailPagerActivity.newIntent(getActivity(), mEmail.getId());
//            startActivity(intent);
        }

        public void bindEmail(EmailModel email) {
            mEmail = email;

            mTextViewSender.setText(mEmail.getSender());
            mTextViewDate.setText(DateFormatHelper.getFormatttedDateStringFromFullDate(mEmail.getFullDate()));
            mTextViewSubject.setText(mEmail.getSubject());
            mTextViewMessage.setText(EmailListHelper.formatShortMessageForEmailList(mEmail.getMessage()));
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
