package com.iliaskomp.emailapp.Functionality;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.iliaskomp.emailapp.Data.Config;
import com.iliaskomp.emailapp.Data.EmailForInbox;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

/**
 * Created by iliaskomp on 11/02/17.
 */

public class FetchMail extends AsyncTask<Void, Void, List<EmailForInbox>> {
    private static final String LOG_TAG = "FetchMail";

    public AsyncResponseForFetchEmail delegate = null;

    private Context context;
    private String host;
    private String storeType;

    private ProgressDialog progressDialog;

    public FetchMail(Context context, String host, String storeType) {
        this.context = context;
        this.host = host;
        this.storeType = storeType;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //Showing progress dialog while sending email
        progressDialog = ProgressDialog.show(context,"Fetching messages","Please wait...",false,false);
    }

    @Override
    protected void onPostExecute(List<EmailForInbox> emails) {
        super.onPostExecute(emails);
        delegate.processFinish(emails);

        progressDialog.dismiss();
        Toast.makeText(context,"Messages Fetched",Toast.LENGTH_LONG).show();
    }

    @Override
    protected List<EmailForInbox> doInBackground(Void... voids) {
        List<EmailForInbox> emails = new ArrayList<>();
        //create properties field
        Properties properties = new Properties();
        properties.put("mail.pop3.host", host);
        properties.put("mail.pop3.port", "995");
        properties.put("mail.pop3.starttls.enable", "true");
        Session emailSession = Session.getDefaultInstance(properties);

        try {
            //create the POP3 store object and connect with the pop server
            Store store = emailSession.getStore("pop3s");
            store.connect(host, Config.EMAIL, Config.PASSWORD);
            //create the folder object and open it
            Folder emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_ONLY);

            // retrieve the messages from the folder in an array and print it
            Message[] messages = emailFolder.getMessages();
            Log.d(LOG_TAG, "messages.length---" + messages.length);

            for (int i = 0, n = messages.length; i < n; i++) {
                Message message = messages[i];
                EmailForInbox email = new EmailForInbox();

                email.setSender(message.getFrom()[0].toString());
                email.setSubject(message.getSubject());
                email.setMessage(message.getContent().toString());
                email.setHeaders(message.getAllHeaders().toString());
                email.setRecipient(message.getAllRecipients()[0].toString());
                email.setSentDate(message.getSentDate());

                emails.add(email);
                Log.d(LOG_TAG, email.toString());

//                Log.d(LOG_TAG, "------------------------");
//                Log.d(LOG_TAG, "Email Number: " + (i + 1));
//                Log.d(LOG_TAG, "Subject: " + message.getSubject());
//                Log.d(LOG_TAG, "From: " + message.getFrom()[0]);
//                Log.d(LOG_TAG, "Text: " + message.getContent().toString());
            }

            //close the store and folder objects
            emailFolder.close(false);
            store.close();

        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return emails;
    }


}

