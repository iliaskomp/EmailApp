package com.iliaskomp.emailapp.network;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.iliaskomp.email.HeaderFields;
import com.iliaskomp.email.HeaderUtils;
import com.iliaskomp.email.MessageBuilder;
import com.iliaskomp.emailapp.database.email.EmailDbSchema;
import com.iliaskomp.emailapp.models.EmailDB;
import com.iliaskomp.emailapp.models.EmailModel;
import com.iliaskomp.emailapp.models.InboxDB;
import com.iliaskomp.emailapp.models.KompDb;
import com.iliaskomp.emailapp.models.KompEntry;
import com.iliaskomp.emailapp.models.SentDB;
import com.iliaskomp.emailapp.network.utils.EmailConfigUtils;
import com.iliaskomp.emailapp.network.utils.EmailModelHelper;
import com.iliaskomp.emailapp.network.utils.KompEntriesHelper;
import com.iliaskomp.emailapp.utils.EmailCredentials;
import com.iliaskomp.encryption.EncryptionHelper;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;

/**
 * Created by iliaskomp on 11/02/17.
 */

public class FetchMail extends AsyncTask<String, Void, FetchMail.FetchMailTaskReturnValue> {
    private static final String LOG_TAG = "FetchMail";

    public AsyncResponseForFetchEmail delegate = null;
    private Context mContext;
    private String mProtocol;
    private ProgressDialog mProgressDialog;



    public interface AsyncResponseForFetchEmail {
        void processFinish(EmailDB db);
    }

    class FetchMailTaskReturnValue {
        private EmailDB mEmailDb;
        private List<MimeMessage> mEmailsToAutoReply;

        private FetchMailTaskReturnValue(EmailDB db, List<MimeMessage> emailsToAutoReply) {
            mEmailDb = db;
            mEmailsToAutoReply = emailsToAutoReply;
        }

        private EmailDB getEmailDb() {
            return mEmailDb;
        }

        private List<MimeMessage> getEmailsToAutoReply() {
            return mEmailsToAutoReply;
        }
    }

    public FetchMail(Context context, String protocol) {
        mContext = context;
        mProtocol = protocol;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //Showing progress dialog while sending email
        mProgressDialog = ProgressDialog.show(mContext, "Fetching messages", "Please wait...", false, false);
    }

    @Override
    protected void onPostExecute(FetchMailTaskReturnValue returnValue) {
        super.onPostExecute(returnValue);
        EmailDB db = returnValue.getEmailDb();
        List<MimeMessage> messagesToSend = returnValue.getEmailsToAutoReply();

        delegate.processFinish(db);
        mProgressDialog.dismiss();
        Toast.makeText(mContext, "Messages Fetched", Toast.LENGTH_LONG).show();

        if (messagesToSend.size() != 0) {
            for (MimeMessage message : messagesToSend) {
                SendMail sm = new SendMail(mContext);
                sm.execute(message);

            }
        }

    }

    @SuppressLint("Assert")
    @Override
    protected FetchMailTaskReturnValue doInBackground(String... parameters) throws NullPointerException {
        Log.d(LOG_TAG, "doInBackground starts");

        String folderName = parameters[0];
        String domain = EmailConfigUtils.getServiceFromEmail(EmailCredentials.EMAIL_SEND); // e.g. gmail.com
        String server = EmailConfigUtils.getServerDomain(domain, mProtocol); //e.g. imap.gmail.com

        EmailDB emailDb = null;
        KompDb entriesDb = KompDb.get(mContext);
        List<MimeMessage> emailsToAutoReply = new ArrayList<>(); //TODO


        // check if needing to fetch inbox or sent folder for email db
        if (folderName.equals(EmailDbSchema.InboxTable.NAME)) {
            emailDb = InboxDB.get(mContext);
        } else if (folderName.equals(EmailDbSchema.SentTable.NAME)) {
            emailDb = SentDB.get(mContext);
        }

        //create properties field
        Properties properties = EmailConfigUtils.getImapProps(server, mProtocol);
        Session emailSession = Session.getInstance(properties); //Session.getDefaultInstance(properties);

        try {
            //create the IMAP/POP3 store object and connect with the pop server
            Store store = emailSession.getStore(mProtocol + "s");
            store.connect(server, EmailCredentials.EMAIL_SEND, EmailCredentials.PASSWORD_SEND);

//            Folder[] folders = store.getDefaultFolder().list("*");
//            for (Folder folder1 : folders) {
//                if ((folder1.getType() & Folder.HOLDS_MESSAGES) != 0) {
//                    Log.d(LOG_TAG, folder1.getFullName() + ": " + folder1.getMessageCount());
//                }
//            }

            //create the folder object and open it
            Folder emailFolder = null;
            if (folderName.equals(EmailDbSchema.InboxTable.NAME)) {
                emailFolder = store.getFolder("INBOX");
            } else if (folderName.equals(EmailDbSchema.SentTable.NAME)) {
                emailFolder = store.getFolder(EmailConfigUtils.getSentFolderName(domain));
            }

            if (emailDb != null) {
                Log.d(LOG_TAG, "db.getEmailCount(): " + emailDb.getEmailCount());
            }

            if (emailFolder != null) {
                emailFolder.open(Folder.READ_ONLY);
                Log.d(LOG_TAG, "emailFolder.getMessageCount(): " + emailFolder.getMessageCount());
            }

//========================================================================================================================================
            //TODO only if folder is inbox, search for parameters, komp etc. NOT with sent folder
            // if db has no emails get all message emails else get only unfetched messages
            assert emailDb != null;
            assert emailFolder != null;
            if (emailDb.getEmailCount() < emailFolder.getMessageCount()) {
                for (int i = emailDb.getEmailCount(); i < emailFolder.getMessageCount(); i++) {
                    Message message = emailFolder.getMessage(i + 1); // emailFolder counting starts at 1 instead of 0!
                    EmailModel emailToAddToDb;

                    if (KompEntriesHelper.encryptionLibraryExists() && emailFolder.getName().equals("INBOX")) {
                        String headerState = HeaderUtils.getHeaderState(message);
                        emailToAddToDb = checkHeaderState(emailsToAutoReply, message, headerState);
                    } else {
                        emailToAddToDb = EmailModelHelper.buildEmailFromMessage(mContext, message);
                    }

                    if (emailToAddToDb != null) {
                        emailDb.addEmail(emailToAddToDb);
                    }
                    Log.d(LOG_TAG, "DB email count: " + emailDb.getEmailCount());
                }
            }
            //close the store and folder objects
            emailFolder.close(false);
            store.close();

        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        }

        return new FetchMailTaskReturnValue(emailDb, emailsToAutoReply);
    }


    // Checks header state and does the appropriate action like creating kompEntries, keys etc
    private EmailModel checkHeaderState(List<MimeMessage> emailsToAutoReply, Message message,
                                        String headerState) {
        KompDb entriesDb = KompDb.get(mContext);
        EmailModel emailToAddToDb =  null;

        try {
            switch (headerState) {
                case HeaderFields.KompState.RECIPIENT_GETS_SENDER_PUBLIC_KEY: {
                    // recipient gets public key and sends his own public key to sender, save to db also

                    emailToAddToDb = EmailModelHelper.buildEmailFromMessage(mContext, message);

                    KeyPair keyPairRecipient = EncryptionHelper.generateRecipientKeyPairFromSender(message);
                    assert keyPairRecipient != null;

                    KompEntry entry = KompEntriesHelper.createRecipientEntry(message, keyPairRecipient);
                    entriesDb.addEntry(entry);

                    Session session = EmailConfigUtils.getSentSession(EmailCredentials.EMAIL_SEND, EmailCredentials.PASSWORD_SEND, EmailConfigUtils.getSmtpProps(EmailCredentials.EMAIL_SEND));
                    MimeMessage messageBack = MessageBuilder.createRecipientMessageWithPublicKey(session, message, keyPairRecipient);
                    emailsToAutoReply.add(messageBack);
                    break;
                }
                case HeaderFields.KompState.SENDER_GETS_RECIPIENT_PUBLIC_KEY: {
                    emailToAddToDb = EmailModelHelper.buildEmailFromMessage(mContext, message);
                    KompEntriesHelper.updateAndCompleteSenderEntry(mContext, message); // get recipient's key and complete encryption db for recipient

                    // get messages where sender was waiting to receive recipient's key from sharedprefs
                    List<MimeMessage> messagesForRecipient = EmailSharedPrefsUtils.getOriginalMessagesForEmail(mContext, message.getFrom()[0].toString());
                    EmailSharedPrefsUtils.removeOriginalMessagesForEmail(mContext, message.getFrom()[0].toString());

                    List<MimeMessage> messagesForRecipientEncrypted = KompEntriesHelper.encryptMessagesForRecipient(mContext, messagesForRecipient);
                    emailsToAutoReply.addAll(messagesForRecipientEncrypted);
                    break;
                }
                case HeaderFields.KompState.ENCRYPTED_EMAIL: {
                    SecretKey secretKey = KompEntriesHelper.getSecretSharedKeyFromDb(mContext, message.getAllRecipients()[0].toString(), message.getFrom()[0].toString());
                    String iv = HeaderUtils.getHeaderIv(message);
                    String decryptedText = null;
                    try {
                        decryptedText = EncryptionHelper.decrypt(message.getContent().toString(), secretKey, iv);
                    } catch (BadPaddingException e) {
                        e.printStackTrace();
                    }

                    emailToAddToDb = EmailModelHelper.buildDecryptedEmail(message, decryptedText);
                    break;
                }
                default: {
                    assert headerState.equals(HeaderFields.HeaderX.NO_HEADER_STRING);
                    emailToAddToDb = EmailModelHelper.buildEmailFromMessage(mContext, message);
                    break;
                }
            }
        } catch (MessagingException |
                IOException |
                NoSuchAlgorithmException |
                InvalidAlgorithmParameterException |
                NoSuchPaddingException |
                IllegalBlockSizeException |
                InvalidKeyException |
                BadPaddingException e) {
            e.printStackTrace();
        }
        return emailToAddToDb;
    }
}

