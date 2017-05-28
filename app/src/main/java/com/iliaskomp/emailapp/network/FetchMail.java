package com.iliaskomp.emailapp.network;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.iliaskomp.email.EmailEncryptionRecipient;
import com.iliaskomp.email.HeaderFields;
import com.iliaskomp.emailapp.database.email.EmailDbSchema;
import com.iliaskomp.emailapp.models.EmailDB;
import com.iliaskomp.emailapp.models.InboxDB;
import com.iliaskomp.emailapp.models.SentDB;

import java.io.IOException;
import java.security.KeyPair;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;

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

    @SuppressLint("Assert")
    @Override
    protected EmailDB doInBackground(String... parameters) throws NullPointerException {
        Log.d(LOG_TAG, "doInBackground starts");
        EmailDB db = null;

        String folderName = parameters[0];
        String email = parameters[1];
        String password = parameters[2];

        String domain = FetchMailUtils.getServiceFromEmail(email); // e.g. gmail.com
        String server = FetchMailUtils.getServerDomain(domain, mProtocol); //e.g. imap.gmail.com

        // check if needing to fetch inbox or sent folder for email db
        if (folderName.equals(EmailDbSchema.InboxTable.NAME)) {
            db = InboxDB.get(mContext);
        } else if (folderName.equals(EmailDbSchema.SentTable.NAME)) {
            db = SentDB.get(mContext);
        }

        //create properties field
        Properties properties = FetchMailUtils.getProperties(server, mProtocol);
        Session emailSession = Session.getInstance(properties); //Session.getDefaultInstance(properties);

        try {
            //create the IMAP/POP3 store object and connect with the pop server
            Store store = emailSession.getStore(mProtocol + "s");
//
//            String host;
//            if (mProtocol.equals(Config.Name.IMAP)) {
//                host = Config.Yahoo.IMAP_SERVER;
//            }
//            else {
//                host = Config.Yahoo.POP_SERVER
//            };

            store.connect(server, email, password);

            Folder[] folders = store.getDefaultFolder().list("*");
            for (Folder folder1 : folders) {
                if ((folder1.getType() & Folder.HOLDS_MESSAGES) != 0) {
                    Log.d(LOG_TAG, folder1.getFullName() + ": " + folder1.getMessageCount());
                }
            }

            //create the folder object and open it
            Folder emailFolder = null;
            if (folderName.equals(EmailDbSchema.InboxTable.NAME)) {
                emailFolder = store.getFolder("INBOX");
            } else if (folderName.equals(EmailDbSchema.SentTable.NAME)) {
                emailFolder = store.getFolder(FetchMailUtils.getSentFolderName(domain));
            }

            if (db != null) {
                Log.d(LOG_TAG, "db.getEmailCount(): " + db.getEmailCount());
            }

            if (emailFolder != null) {
                emailFolder.open(Folder.READ_ONLY);
                Log.d(LOG_TAG, "emailFolder.getMessageCount(): " + emailFolder.getMessageCount());
            }

//            Log.d(LOG_TAG, "first email from emaildb: " + db.getEmails().get(0).toString());
//            Log.d(LOG_TAG, "first javamail message subject: " + emailFolder.getMessage(0).getSubject());

//================================================================================================
            // if db has no emails get all message emails
            // else get only unseen messages
            assert db != null;
            if (db.getEmailCount() == 0) {
                assert emailFolder != null;
                Message[] messages = emailFolder.getMessages();
                for (Message message : messages) {
                    //TODO IMPORTANT check for encryption library headers here
                    db.addEmail(FetchMailUtils.buildEmailFromMessage(message));
                    Log.d(LOG_TAG, "DB email count: " + db.getEmailCount());
                }
            } else {
                assert emailFolder != null;
                if (db.getEmailCount() < emailFolder.getMessageCount()) {
                    // TODO What happens if I add delete functionality
                    for (int i = db.getEmailCount(); i < emailFolder.getMessageCount(); i++) {
                        Message message = emailFolder.getMessage(i + 1); // emailFolder counting starts at 1 instead of 0!

                        // ENCRYPTION LIBRARY CODE HERE===============================================
                        if (FetchMailUtils.encryptionLibraryExists()) {
                            Log.d(LOG_TAG, "Encryption library exists");

                            EmailEncryptionRecipient eer = new EmailEncryptionRecipient();
                            String headerState = eer.getHeaderState(message);

                            switch (headerState) {
                                case HeaderFields.FirstInteractionState.SENDER_FIRST_TIME:
                                    // recipient gets public key and sends his own public key to sender, save to db also
                                    KeyPair keyPairRecipient = eer.createKeyPairFromSender(message);
                                    assert keyPairRecipient != null;

                                    MimeMessage messageBack = eer.createMessageWithPublicKey(message, keyPairRecipient);
                                    //TODO to send email back
                                    // SendMail sm = new SendMail(mContext);
                                    // sm.execute(EmailCredentials.EMAIL_SEND, EmailCredentials.PASSWORD_SEND, messageBack.getRecipients()[0].toString(), messageBack.getSubject(), messageBack.getContent().toString());
                                    break;
                                case HeaderFields.FirstInteractionState.SENDER_SECOND_TIME:
                                    // decrypt email
                                    break;
                                case HeaderFields.SecondPlusInteractionState.SENDER_SENDS:
                                    break;
                                default:
                                    assert headerState.equals(HeaderFields.HeaderX.NO_HEADER_STRING);
                                    break;
                            }
                            db.addEmail(FetchMailUtils.buildEmailFromMessage(message));

                        } else {
                            Log.d(LOG_TAG, "Encryption library does not exist");
                        }
                    }
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

        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        }

        if (db != null) {
            Log.d(LOG_TAG, "db.getEmailCount() end: " + db.getEmailCount());
        }
        return db;
    }
}

