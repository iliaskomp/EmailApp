package com.iliaskomp.emailapp.network.utils;

import android.content.Context;

import com.iliaskomp.encryption.DHHelper;
import com.iliaskomp.email.HeaderUtils;
import com.iliaskomp.email.MessageBuilder;
import com.iliaskomp.emailapp.models.KompDb;
import com.iliaskomp.emailapp.models.KompEntry;
import com.iliaskomp.emailapp.utils.EmailCredentials;
import com.iliaskomp.encryption.EncryptionHelper;

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
 * Helper about the KompDB where the encryption information is saved like key and email information.
 */
public class KompEntriesHelper {

    /**
     * Create non complete komp entry for the first interaction of the sender.
     * Recipient's public key is still needed as well as the secret key to be generated
     * from the recipient's public key.
     *
     * @param message the message
     * @param keyPair the key pair
     * @return the komp entry
     * @throws MessagingException the messaging exception
     */
    public static KompEntry createSenderEntryNonComplete (
            MimeMessage message, KeyPair keyPair) throws MessagingException {

        KompEntry entry = new KompEntry(message.getFrom()[0].toString(),
                message.getAllRecipients()[0].toString());

        entry.setMyPublicKey(DHHelper.PublicKeyClass.keyToString(keyPair.getPublic()));
        entry.setMyPrivateKey(DHHelper.PrivateKeyClass.keyToString(keyPair.getPrivate()));
        entry.setState(KompEntry.State.SENDER_ENTRY_NON_COMPLETE);

        return entry;
    }

    /**
     * Create recipient komp entry.
     * Recipient creates complete entry after getting sender's public key
     *
     * @param message the message
     * @param keyPair the key pair
     * @return the komp entry
     * @throws MessagingException       the messaging exception
     * @throws InvalidKeyException      the invalid key exception
     * @throws NoSuchAlgorithmException the no such algorithm exception
     */
    public static KompEntry createRecipientEntry(Message message, KeyPair keyPair)
            throws MessagingException, InvalidKeyException, NoSuchAlgorithmException {

        PublicKey theirPublicKey = HeaderUtils.getHeaderSenderPublicKey(message);
        SecretKey secretKey = EncryptionHelper.generateSecretKey(keyPair.getPrivate(), theirPublicKey);

        KompEntry entry = new KompEntry(message.getAllRecipients()[0].toString(),
                message.getFrom()[0].toString());
        entry.setMyPrivateKey(DHHelper.PrivateKeyClass.keyToString(keyPair.getPrivate()));
        entry.setMyPublicKey(DHHelper.PublicKeyClass.keyToString(keyPair.getPublic()));
        entry.setTheirPublicKey(DHHelper.PublicKeyClass.keyToString(theirPublicKey));
        entry.setSharedSecretKey(DHHelper.SecretKeyClass.keyToString(secretKey));
        entry.setState(KompEntry.State.ENTRY_COMPLETE);

        return entry;
    }

    /**
     * Update and complete sender's komp entry after receiving recipient's public key.
     *
     * @param context the context
     * @param message the message
     * @throws MessagingException       the messaging exception
     * @throws InvalidKeyException      the invalid key exception
     * @throws NoSuchAlgorithmException the no such algorithm exception
     */
    public static void updateAndCompleteSenderEntry(Context context, Message message)
            throws MessagingException, InvalidKeyException, NoSuchAlgorithmException {

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
            SecretKey secretKey = EncryptionHelper.generateSecretKey(privateKey, theirPublicKey);
            newEntry.setSharedSecretKey(DHHelper.SecretKeyClass.keyToString(secretKey));

            db.updateEntry(message.getAllRecipients()[0].toString(),
                    message.getFrom()[0].toString(), newEntry);
        }
    }

    /**
     * Gets encryption entry if it exists for a specific email address.
     *
     * @param context the context
     * @param message the message
     * @return the users encryption entry if exists
     * @throws MessagingException the messaging exception
     */
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

    /**
     * Gets secret shared key from db for sender and recipient's email address.
     *
     * @param context    the context
     * @param myEmail    the my email
     * @param theirEmail the their email
     * @return the secret shared key from db
     * @throws MessagingException the messaging exception
     */
    public static SecretKey getSecretSharedKeyFromDb(Context context, String myEmail,
                                                     String theirEmail) throws MessagingException {

        KompDb db = KompDb.get(context);
        String keyString = db.getSecretKeyForEmails(myEmail, theirEmail);

        return DHHelper.SecretKeyClass.stringToKey(keyString);
    }

    /**
     * Encrypt all the messages for the recipient. They were previously saved until a secret shared
     * key had been established.
     *
     * @param context              the context
     * @param messagesForRecipient the messages that were temporarily saved in order to establish
     *                             a shared secret key.
     * @return the list
     * @throws IOException               the io exception
     * @throws MessagingException        the messaging exception
     * @throws InvalidKeyException       the invalid key exception
     * @throws BadPaddingException       the bad padding exception
     * @throws NoSuchAlgorithmException  the no such algorithm exception
     * @throws IllegalBlockSizeException the illegal block size exception
     * @throws NoSuchPaddingException    the no such padding exception
     */
    public static List<MimeMessage> encryptMessagesForRecipient(Context context,
                                                                List<MimeMessage> messagesForRecipient)
            throws IOException, MessagingException, InvalidKeyException, BadPaddingException,
            NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException {

        List<MimeMessage> messages = new ArrayList<>();

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

    /**
     * Checks if the komp encryption library exists
     *
     * @return if the komp encryption library exists.
     */
    public static boolean encryptionLibraryExists() {
        try {
            Class cls = Class.forName("com.iliaskomp.email.MessageBuilder");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
