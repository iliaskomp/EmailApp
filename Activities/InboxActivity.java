package com.iliaskomp.emailapp.Activities;

import android.support.v4.app.Fragment;

/**
 * Created by iliaskomp on 11/02/17.
 */

//public class InboxActivity extends ListActivity implements AsyncResponseForFetchEmail {
public class InboxActivity extends SingleFragmentActivity {
    private static final String LOG_TAG = "InboxActivity";
  //  private FetchMail fm;

   // private ListView listViewInbox;

    @Override
    protected Fragment createFragment() {
        return new InboxFragment();
    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.fragment_inbox);
//
//        listViewInbox = (ListView) findViewById(android.R.id.list);
//        listViewInbox.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Toast.makeText(getApplicationContext(), "ListItem Number: " + i, Toast.LENGTH_LONG)
//                        .show();
//            }
//        });
//
//        fetchMail();
//    }
//
//    private void fetchMail() {
//        fm = new FetchMail(InboxActivity.this, Config.IMAP_NAME);
////        fm = new FetchMail(InboxActivity.this, "imap", "imap.gmail.com", "993");
//        fm.delegate = this;
//        fm.execute();
//
//    }
//
//    @Override
//    public void processFinish(List<EmailForInbox> emails) {
//        listViewInbox.setAdapter(new InboxArrayAdapter(this, emails));
//    }
}
