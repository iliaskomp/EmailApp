package com.iliaskomp.emailapp.Activities;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.iliaskomp.emailapp.Data.Config;
import com.iliaskomp.emailapp.Data.EmailForInbox;
import com.iliaskomp.emailapp.Functionality.AsyncResponseForFetchEmail;
import com.iliaskomp.emailapp.Functionality.FetchMail;
import com.iliaskomp.emailapp.Functionality.InboxArrayAdapter;
import com.iliaskomp.emailapp.R;

import java.util.List;

/**
 * Created by iliaskomp on 11/02/17.
 */

public class InboxActivity extends ListActivity implements AsyncResponseForFetchEmail {
    private static final String LOG_TAG = "InboxActivity";
    private FetchMail fm;

    private ListView listViewInbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        listViewInbox = (ListView) findViewById(android.R.id.list);
        listViewInbox.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(), "ListItem Number: " + i, Toast.LENGTH_LONG)
                        .show();
            }
        });

        fetchMail();
    }

    private void fetchMail() {
        fm = new FetchMail(InboxActivity.this, Config.IMAP_NAME);
//        fm = new FetchMail(InboxActivity.this, "imap", "imap.gmail.com", "993");
        fm.delegate = this;
        fm.execute();

    }

    @Override
    public void processFinish(List<EmailForInbox> emails) {
        listViewInbox.setAdapter(new InboxArrayAdapter(this, emails));
    }
}
