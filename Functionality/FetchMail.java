package com.iliaskomp.emailapp.Functionality;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.iliaskomp.emailapp.Data.Config;
import com.iliaskomp.emailapp.Data.EmailForInbox;
import com.iliaskomp.emailapp.InboxActivity.FormatHelper;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMultipart;

/**
 * Created by iliaskomp on 11/02/17.
 * TODO fix asynctask error when sending a message, then going to inbox
 */

public class FetchMail extends AsyncTask<Void, Void, List<EmailForInbox>> {
    private static final String LOG_TAG = "FetchMail";

    public AsyncResponseForFetchEmail delegate = null;

    private Context mContext;
    private String mProtocol;

    private ProgressDialog progressDialog;

    public FetchMail(Context context, String protocol) {
        mContext = context;
        mProtocol = protocol;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //Showing progress dialog while sending email
        progressDialog = ProgressDialog.show(mContext,"Fetching messages","Please wait...",false,false);
    }

    @Override
    protected void onPostExecute(List<EmailForInbox> emails) {
        super.onPostExecute(emails);
        delegate.processFinish(emails);

        progressDialog.dismiss();
        Toast.makeText(mContext,"Messages Fetched",Toast.LENGTH_LONG).show();
    }

    @Override
    protected List<EmailForInbox> doInBackground(Void... voids) {
        List<EmailForInbox> emails = new ArrayList<>();
        //create properties field
        Properties properties = getProperties();

        Session emailSession = Session.getDefaultInstance(properties);

        try {
            //create the POP3 store object and connect with the pop server
//            Store store = emailSession.getStore("pop3s");
            Store store = emailSession.getStore(mProtocol + "s");

            store.connect(mProtocol == Config.IMAP_NAME ? Config.IMAP_HOST : Config.POP_HOST,
                    Config.EMAIL, Config.PASSWORD);
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
                email.setRecipient(message.getAllRecipients()[0].toString());
                email.setSentDate(message.getSentDate());
                email.setMessage(getMessage(message));
                email.setHeaders(FormatHelper.getHeaders(message.getAllHeaders()));

                emails.add(email);
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

    private Properties getProperties() {
        Properties properties = new Properties();

        switch (mProtocol) {
            case Config.IMAP_NAME:
                properties.put("mail.imap.host", Config.IMAP_HOST);
                properties.put("mail.imap.port", Config.IMAP_PORT);
                break;
            case Config.POP_NAME:
                properties.put("mail.pop3.host", Config.POP_HOST);
                properties.put("mail.pop3.port", Config.POP_PORT);
                break;
        }
//      properties.put("mail.pop3.port", "995");
        properties.put(String.format("mail.%s.starttls.enable", mProtocol), "true");

        return properties;
    }

    private String getMessage(Part messageObject) throws MessagingException, IOException {
        Log.d(LOG_TAG, "getMessage");

        Message message = (Message) messageObject;
       // String type = message.getContentType();
        String result = "";


        if (message.isMimeType("text/plain")) {
            result = message.getContent().toString();
        } else if (message.isMimeType("text/plain")) {
            String html = (String) messageObject.getContent();
            result = result + "\n" + Jsoup.parse(html).text();
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart)message.getContent();
            int count = mimeMultipart.getCount();
            for (int i = 0; i < count; i ++){
                BodyPart bodyPart = mimeMultipart.getBodyPart(i);
                if (bodyPart.isMimeType("text/plain")){
                    result = result + "\n" + bodyPart.getContent();
                    break;  //without break same text appears twice in my tests
                } else if (bodyPart.isMimeType("text/html")){
                    String html = (String) bodyPart.getContent();
                    result = result + "\n" + Jsoup.parse(html).text();
                }
            }
        } else {
            result = "invalid";
        }


        return result;
    }
}

