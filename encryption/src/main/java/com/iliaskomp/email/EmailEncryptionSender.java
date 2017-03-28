package com.iliaskomp.email;

import com.iliaskomp.dhalgorithm.DHAlgorithm;
import com.iliaskomp.dhalgorithm.DHHelper;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

/**
 * Created by IliasKomp on 28/03/17.
 */

public class EmailEncryptionSender {

    private KeyPair mKeyPair;

    private static final String FIRST_TIME_MESSAGE = "This message is part of the encryption " +
            "library. If you have it installed, then an automatic message will be sent to " +
            "establish encrypted communication. Otherwise, either install the library or contact" +
            "the sender for an unencrypted email";

    private static final String HEADER_FIELD_PUBLIC_KEY = "x-komp-sender-public-key";

    private static final String HEADER_FIELD_STATE = "x-komp-state";
    // Sender sends this state the first time of establishing a secret key
    private static final String HEADER_STATE_FIRST_TIME_SEND = "first_time_send";
    // Recipient replies with this header state the first time of establishing a secret key
    private static final String HEADER_STATE_FIRST_TIME_RECEIVE = "first_time_receive";

    public EmailEncryptionSender() {
        try {
            DHAlgorithm dh = new DHAlgorithm();
            mKeyPair = dh.generateKeyPair();
        } catch (NoSuchAlgorithmException | InvalidParameterSpecException |
                InvalidAlgorithmParameterException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }

    // TODO string instead of mimemessage?
    // Get message for the first communication between sender/recipient
    // Constructs a message with generic info as message and public key as header.
    public Message getEmailFirstTimeSending(MimeMessage message, Session session) throws InvalidKeySpecException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidParameterSpecException {
        Message formattedMessage = new MimeMessage(session);

        try {
            formattedMessage.setFrom(message.getFrom()[0]);
            formattedMessage.setRecipient(Message.RecipientType.TO, message.getAllRecipients()[0]);
            formattedMessage.setSentDate(message.getSentDate());
            formattedMessage.setSubject(message.getSubject());

            formattedMessage.setText(FIRST_TIME_MESSAGE);


            // Sender encodes his public key in order to send it to recipient
            formattedMessage.addHeader(HEADER_FIELD_STATE, HEADER_STATE_FIRST_TIME_SEND);
            formattedMessage.addHeader(HEADER_FIELD_PUBLIC_KEY, DHHelper.publicKeyToString(mKeyPair.getPublic()));

        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return formattedMessage;
    }

    public KeyPair getKeyPair() {
        return mKeyPair;
    }
}
