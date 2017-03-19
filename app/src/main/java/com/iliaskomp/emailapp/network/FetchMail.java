package com.iliaskomp.emailapp.network;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.iliaskomp.emailapp.database.EmailDbSchema;
import com.iliaskomp.emailapp.models.EmailDB;
import com.iliaskomp.emailapp.models.EmailModel;
import com.iliaskomp.emailapp.models.InboxDB;
import com.iliaskomp.emailapp.models.SentDB;
import com.iliaskomp.emailapp.utils.Config;
import com.iliaskomp.emailapp.utils.HeadersFormatHelper;

import org.jsoup.Jsoup;

import java.io.IOException;
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
 */

public class FetchMail extends AsyncTask<String, Void, EmailDB> {
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
        progressDialog = ProgressDialog.show(mContext, "Fetching messages", "Please wait...", false, false);
    }

    @Override
    protected void onPostExecute(EmailDB db) {
        super.onPostExecute(db);
        delegate.processFinish(db);

        progressDialog.dismiss();
        Toast.makeText(mContext, "Messages Fetched", Toast.LENGTH_LONG).show();
    }

    @Override
    protected EmailDB doInBackground(String... folder) throws NullPointerException {
        Log.d(LOG_TAG, "doInBackground starts");
        EmailDB db = null;

        if (folder[0].equals(EmailDbSchema.InboxTable.NAME)) {
            db = InboxDB.get(mContext);
        } else if (folder[0].equals(EmailDbSchema.SentTable.NAME)) {
            db = SentDB.get(mContext);
        }

        //create properties field
        Properties properties = getProperties();
        Session emailSession = Session.getInstance(properties); //Session.getDefaultInstance(properties);

        try {
            //create the POP3 store object and connect with the pop server
            Store store = emailSession.getStore(mProtocol + "s");
            String host = mProtocol == Config.IMAP_NAME ? Config.IMAP_HOST : Config.POP_HOST;
            store.connect(host, Config.EMAIL, Config.PASSWORD);

            Folder[] folders = store.getDefaultFolder().list("*");
            for (Folder folder1 : folders) {
                if ((folder1.getType() & Folder.HOLDS_MESSAGES) != 0) {
                    Log.d(LOG_TAG, folder1.getFullName() + ": " + folder1.getMessageCount());
                }
            }

            //create the folder object and open it
            Folder emailFolder = null;
            if (folder[0].equals(EmailDbSchema.InboxTable.NAME)) {
                emailFolder = store.getFolder("INBOX");
            } else if (folder[0].equals(EmailDbSchema.SentTable.NAME)) {
                emailFolder = store.getFolder("[Gmail]/Sent Mail");
            }

            if (emailFolder != null) {
                emailFolder.open(Folder.READ_ONLY);
            }

            Log.d(LOG_TAG, "db.getEmailCount(): " + db.getEmailCount());
            Log.d(LOG_TAG, "emailFolder.getMessageCount(): " + emailFolder.getMessageCount());

//            Log.d(LOG_TAG, "first email from emaildb: " + db.getEmails().get(0).toString());
//            Log.d(LOG_TAG, "first javamail message subject: " + emailFolder.getMessage(0).getSubject());

//================================================================================================
            // if db has no emails get all message emails
            // else get only unseen messages
            if (db.getEmailCount() == 0) {
                Message[] messages = emailFolder.getMessages();
                for (Message message : messages) {
                    db.addEmail(buildEmailFromMessage(message));
                    Log.d(LOG_TAG, "DB email count: " + db.getEmailCount());
                }
            } else if (db.getEmailCount() < emailFolder.getMessageCount()) {
                // TODO What happens if I add delete functionality
                for (int i = db.getEmailCount(); i < emailFolder.getMessageCount(); i++) {
                    Message message = emailFolder.getMessage(i+1);
                    db.addEmail(buildEmailFromMessage(message));
                }
            }
//================================================================================================
            // Redownload all messages every time
//            Message[] messages = emailFolder.getMessages();
//            for (Message message : messages) {
//                db.addEmail(buildEmailFromMessage(message));
//            }
//            Log.d(LOG_TAG, "DB email count: " + db.getEmailCount());
//================================================================================================
//            Message[] unreadMessages = emailFolder.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));

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
        return db;
    }

    private EmailModel buildEmailFromMessage(Message message) throws MessagingException, IOException {
        EmailModel email = new EmailModel();

        email.setSender(message.getFrom()[0].toString());
        email.setSubject(message.getSubject());
        email.setRecipient(message.getAllRecipients()[0].toString());
        email.setFullDate(message.getSentDate());
        email.setMessage(getMessage(message));
        email.setHeaders(HeadersFormatHelper.getHeadersStringFromEnumeration(message.getAllHeaders()));

        Log.d(LOG_TAG, email.toString());

        return email;
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

        Message message = (Message) messageObject;
        // String type = message.getContentType();
        String result = "";


        if (message.isMimeType("text/plain")) {
            result = message.getContent().toString();
        } else if (message.isMimeType("text/plain")) {
            String html = (String) messageObject.getContent();
            result = result + "\n" + Jsoup.parse(html).text();
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            int count = mimeMultipart.getCount();
            for (int i = 0; i < count; i++) {
                BodyPart bodyPart = mimeMultipart.getBodyPart(i);
                if (bodyPart.isMimeType("text/plain")) {
                    result = result + "\n" + bodyPart.getContent();
                    break;  //without break same text appears twice in my tests
                } else if (bodyPart.isMimeType("text/html")) {
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

