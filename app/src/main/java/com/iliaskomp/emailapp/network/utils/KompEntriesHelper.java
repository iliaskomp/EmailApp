package com.iliaskomp.emailapp.network.utils;

import android.content.Context;

import com.iliaskomp.dhalgorithm.DHAlgorithm;
import com.iliaskomp.dhalgorithm.DHHelper;
import com.iliaskomp.email.EmailEncryptionRecipient;
import com.iliaskomp.email.HeaderUtils;
import com.iliaskomp.email.MessageBuilder;
import com.iliaskomp.emailapp.models.KompDb;
import com.iliaskomp.emailapp.models.KompEntry;
import com.iliaskomp.emailapp.utils.EmailCredentials;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

/**
 * Created by IliasKomp on 13/06/17.
 */

public class KompEntriesHelper {

    // create new user encryption entry for sender (non-complete until receiving recipient's key)
    public static KompEntry createSenderEntryNonComplete (
            MimeMessage message, KeyPair keyPair) throws MessagingException {

        KompEntry entry = new KompEntry(message.getFrom()[0].toString(),
                message.getAllRecipients()[0].toString());

        entry.setMyPublicKey(DHHelper.PublicKeyClass.keyToString(keyPair.getPublic()));
        entry.setMyPrivateKey(DHHelper.PrivateKeyClass.keyToString(keyPair.getPrivate()));
        entry.setState(KompEntry.State.SENDER_ENTRY_NON_COMPLETE);

        return entry;
    }

    // recipient creates complete entry after getting sender's public key. keyPair: recipient's keypair
    public static KompEntry createRecipientEntry(Message message, KeyPair keyPair)
            throws MessagingException, InvalidKeyException, NoSuchAlgorithmException {

        EmailEncryptionRecipient eer = new EmailEncryptionRecipient();
        DHAlgorithm dhAlgorithm = new DHAlgorithm();

        PublicKey theirPublicKey = HeaderUtils.getHeaderSenderPublicKey(message);
        SecretKey secretKey = DHAlgorithm.agreeSecretKey(keyPair.getPrivate(), theirPublicKey);

        KompEntry entry = new KompEntry(message.getAllRecipients()[0].toString(),
                message.getFrom()[0].toString());
        entry.setMyPrivateKey(DHHelper.PrivateKeyClass.keyToString(keyPair.getPrivate()));
        entry.setMyPublicKey(DHHelper.PublicKeyClass.keyToString(keyPair.getPublic()));
        entry.setTheirPublicKey(DHHelper.PublicKeyClass.keyToString(theirPublicKey));
        entry.setSharedSecretKey(DHHelper.SecretKeyClass.keyToString(secretKey));
        entry.setState(KompEntry.State.ENTRY_COMPLETE);

        return entry;
    }

    // update and complete sender's db entry (second step after receiving recipient's public key)
    public static void updateAndCompleteSenderEntry(Context context, Message message)
            throws MessagingException, InvalidKeyException, NoSuchAlgorithmException {

        EmailEncryptionRecipient eer = new EmailEncryptionRecipient();
        PublicKey theirPublicKey = HeaderUtils.getHeaderRecipientPublicKey(message);

        KompDb db = KompDb.get(context);
        KompEntry oldEntry = db.getEntryFromEmails(
                message.getAllRecipients()[0].toString(), message.getFrom()[0].toString());

        if (oldEntry != null) {
            KompEntry newEntry = new KompEntry(
                    oldEntry.getMyEmail(), oldEntry.getTheirEmail());

            newEntry.setId(oldEntry.getId());
            newEntry.setMyPublicKey(oldEntry.getMyPublicKey());
            newEntry.setMyPrivateKey(oldEntry.getMyPrivateKey());
            newEntry.setState(KompEntry.State.ENTRY_COMPLETE);

            newEntry.setTheirPublicKey(DHHelper.PublicKeyClass.keyToString(theirPublicKey));

            PrivateKey privateKey = DHHelper.PrivateKeyClass.stringToKey(oldEntry.getMyPrivateKey());
            SecretKey secretKey = DHAlgorithm.agreeSecretKey(privateKey, theirPublicKey);
            newEntry.setSharedSecretKey(DHHelper.SecretKeyClass.keyToString(secretKey));

            db.updateEntry(message.getAllRecipients()[0].toString(),
                    message.getFrom()[0].toString(), newEntry);
        }
    }

    // try to get entry for message's sender and recipient. returns null if no entry is found
    public static KompEntry getUsersEncryptionEntryIfExists(Context context, MimeMessage message)
            throws MessagingException {

        KompDb db = KompDb.get(context);
        for (KompEntry entry : db.getAllKompEntries()) {
            if (entry.getMyEmail().equals(message.getFrom()[0].toString()) &&
                    entry.getTheirEmail().equals(message.getAllRecipients()[0].toString()))
                return entry;
        }
        return null;
    }

    // get secret key from db for sender and recipient email
    public static SecretKey getSecretSharedKeyFromDb(Context context, String myEmail,
                                                     String theirEmail) throws MessagingException {

        KompDb db = KompDb.get(context);
        String keyString = db.getSecretKeyForEmails(myEmail, theirEmail);

        return DHHelper.SecretKeyClass.stringToKey(keyString);
    }

    //messagesForRecipient: messages that were saved by sender while waiting for R's public key
    public static List<MimeMessage> encryptMessagesForRecipient(Context context,
                                                                List<MimeMessage> messagesForRecipient)
            throws IOException, MessagingException, InvalidKeyException, BadPaddingException,
            NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException {

        List<MimeMessage> messages = new ArrayList<>();
        EmailEncryptionRecipient eer = new EmailEncryptionRecipient();

        for (MimeMessage message : messagesForRecipient) {
            Properties props = EmailConfigUtils.getSmtpProps(message.getAllRecipients()[0].toString());
            Session session = EmailConfigUtils.getSentSession(EmailCredentials.EMAIL_SEND,
                    EmailCredentials.PASSWORD_SEND, props);
            SecretKey secretKey = getSecretSharedKeyFromDb(context, message.getFrom()[0].toString(),
                    message.getAllRecipients()[0].toString());

            MimeMessage encryptedMessage = MessageBuilder.createEncryptedMessage(session, message, secretKey);
            messages.add(encryptedMessage);
        }
        return messages;
    }

    public static boolean encryptionLibraryExists() {
        try {
            Class cls = Class.forName("com.iliaskomp.email.EmailEncryptionRecipient");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
