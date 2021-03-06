package com.iliaskomp.email;

import com.iliaskomp.encryption.DHHelper;
import com.iliaskomp.encryption.EncryptionHelper;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import static com.iliaskomp.email.HeaderFields.HeaderX;
import static com.iliaskomp.email.HeaderFields.HeaderX.PUBLIC_KEY_SENDER;
import static com.iliaskomp.email.HeaderFields.HeaderX.STATE;
import static com.iliaskomp.email.HeaderFields.KompState;
import static com.iliaskomp.email.HeaderFields.KompState.RECIPIENT_GETS_SENDER_PUBLIC_KEY;


/**
 * Contains method used to construct specific email messages in accordance with the library
 */
public class MessageBuilder {

    /**
     * Create a mime message for the first contact of the sender.
     * Sender and recipient have not established a shared secret key yet therefore the sender
     * sends a placeholder email with his public key in the headers.
     *
     * @param message       the message
     * @param session       the session
     * @param keyPairSender the key pair of the sender
     * @return the mime message
     * @throws MessagingException the messaging exception
     */
    public static MimeMessage createEmailFirstTimeSending(MimeMessage message, Session session, KeyPair
            keyPairSender)
            throws MessagingException {

        MimeMessage formattedMessage = new MimeMessage(session);

        formattedMessage.setFrom(message.getFrom()[0]);
        formattedMessage.setRecipient(Message.RecipientType.TO, message.getAllRecipients()[0]);
        formattedMessage.setSentDate(message.getSentDate());
        formattedMessage.setSubject(createSubjectStepOne(message.getFrom()[0].toString()));
        formattedMessage.setText(createMessageStepOne(message.getFrom()[0].toString()));

        // Sender encodes encryption state and his public key in order to send it to recipient
        String publicKey = DHHelper.PublicKeyClass.keyToString(keyPairSender.getPublic());
        formattedMessage.addHeader(STATE, RECIPIENT_GETS_SENDER_PUBLIC_KEY);
        formattedMessage.addHeader(PUBLIC_KEY_SENDER, publicKey);

        return formattedMessage;
    }

    /**
     * Create a mime message for the first contact of the recipient.
     * Recipient has received sender's public key and now has to reply back with a placeholder
     * email with his own public key.
     *
     * @param emailSession     the email session
     * @param message          the message
     * @param keyPairRecipient the key pair of the recipient
     * @return the mime message
     * @throws MessagingException the messaging exception
     */
    public static MimeMessage createRecipientMessageWithPublicKey(Session emailSession, Message message, KeyPair
            keyPairRecipient) throws MessagingException {
        MimeMessage messageBack = new MimeMessage(emailSession);

        messageBack.setFrom(message.getAllRecipients()[0]);
        messageBack.setRecipient(Message.RecipientType.TO, message.getFrom()[0]);
        messageBack.setSentDate(message.getSentDate());
        messageBack.setSubject(createSubjectStepTwo(message.getAllRecipients()[0].toString()));
        messageBack.setText(createMessageStepTwo(message.getAllRecipients()[0].toString()));

        // Sender encodes encryption state and his public key in order to send it to recipient
        messageBack.removeHeader(STATE);
        messageBack.removeHeader(PUBLIC_KEY_SENDER);

        String publicKeyString = DHHelper.PublicKeyClass.keyToString(keyPairRecipient.getPublic());
        messageBack.addHeader(STATE, KompState.SENDER_GETS_RECIPIENT_PUBLIC_KEY);
        messageBack.addHeader(HeaderX.PUBLIC_KEY_RECIPIENT, publicKeyString);

        return messageBack;
    }

    /**
     * Create encrypted email mime message.
     * Gets the original message and encrypts it with a secret key.
     *
     * @param session   the session
     * @param message   the message
     * @param secretKey the secret key
     * @return the mime message
     * @throws IOException               the io exception
     * @throws MessagingException        the messaging exception
     * @throws IllegalBlockSizeException the illegal block size exception
     * @throws InvalidKeyException       the invalid key exception
     * @throws BadPaddingException       the bad padding exception
     * @throws NoSuchAlgorithmException  the no such algorithm exception
     * @throws NoSuchPaddingException    the no such padding exception
     */
    public static MimeMessage createEncryptedMessage(Session session, MimeMessage message,
                                                     SecretKey secretKey)
            throws IOException, MessagingException, IllegalBlockSizeException, InvalidKeyException,
            BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {

        MimeMessage encryptedMessage = new MimeMessage(session);

        String content = (String) message.getContent();
        String[] encryptionResult = EncryptionHelper.encrypt(content, secretKey);
        String encryptedText = encryptionResult[0];
        String ivString = encryptionResult[1];

        encryptedMessage.setFrom(message.getFrom()[0]);
        encryptedMessage.setRecipient(Message.RecipientType.TO, message.getAllRecipients()[0]);
        encryptedMessage.setSentDate(message.getSentDate());
        encryptedMessage.setSubject(message.getSubject());
        encryptedMessage.setText(encryptedText);

        encryptedMessage.addHeader(STATE, KompState.ENCRYPTED_EMAIL);
        encryptedMessage.addHeader(HeaderX.IV, ivString);

        return encryptedMessage;
    }

    private static String createSubjectStepOne(String emailAddress) {
        return "Komp Encryption Step 1 with " + emailAddress;
    }

    private static String createMessageStepOne(String recipientEmail) {
        return "Step 1: This is an automated message to " +
                "establish secret communication with " +
                recipientEmail +
                "\n\nThis message is part of the komp encryption library.\n\nIf you have the " +
                "library installed, an automatic message will be sent to establish encrypted " +
                "communication. In any other case, you should either install the library or " +
                "contact the sender for an unencrypted email";
    }

    private static String createSubjectStepTwo(String email) {
        return "Komp Encryption Step 2 with " + email;
    }

    private static String createMessageStepTwo(String email) {
        return "Step 2: This is an automated message to establish secret communication with " +
                email +
                " by obtaining their public key." +
                "\nThis is the first step towards establishing a secret shared key." +
                "\nAn automated message with your public key will be send automatically" +
                "\nYou do not have to do any action with this email.";
    }
}
