package com.iliaskomp.email;

import com.iliaskomp.dhalgorithm.DHAlgorithm;
import com.iliaskomp.dhalgorithm.DHHelper;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import static com.iliaskomp.email.HeaderFields.HeaderX.PUBLIC_KEY_SENDER;
import static com.iliaskomp.email.HeaderFields.HeaderX.STATE;
import static com.iliaskomp.email.HeaderFields.KompState.RECIPIENT_GETS_SENDER_PUBLIC_KEY;

/**
 * Created by IliasKomp on 28/03/17.
 */

public class EmailEncryptionSender {
    // String messages
    private static final String FIRST_TIME_MESSAGE = "Step 1: This is an automated message to " +
            "establish secret communication with ";

    private static final String FIRST_TIME_MESSAGE_2 = "\nThis message is part of the komp " +
            "encryption library.\n\nIf you have the library installed, an automatic message will " +
            "be sent to establish encrypted communication. In any other case, you should either " +
            "install the library or contact the sender for an unencrypted email";

    public EmailEncryptionSender() {

    }

    public KeyPair createKeyPair() {
        KeyPair keyPair = null;
        try {
            DHAlgorithm dh = new DHAlgorithm();
            keyPair = dh.generateKeyPair();
        } catch (NoSuchAlgorithmException | InvalidParameterSpecException |
                InvalidAlgorithmParameterException | InvalidKeySpecException e) {
            e.printStackTrace();
        }

        return keyPair;
    }

    // TODO multipart messages??
    // Generate message for the first communication between sender/recipient
    // Constructs a message with generic info as message and public key as header.
    // Message is not needed for its content  but sender/recip/date.
    public MimeMessage getEmailFirstTimeSending(MimeMessage message, Session session, KeyPair
            keyPairSender)
            throws InvalidKeySpecException, InvalidAlgorithmParameterException,
            NoSuchAlgorithmException, InvalidParameterSpecException, IOException,
            MessagingException {

        MimeMessage formattedMessage = new MimeMessage(session);

        formattedMessage.setFrom(message.getFrom()[0]);
        formattedMessage.setRecipient(Message.RecipientType.TO, message.getAllRecipients()[0]);
        formattedMessage.setSentDate(message.getSentDate());
        formattedMessage.setSubject(createSubjectFirstTime(message.getFrom()[0].toString()));
        formattedMessage.setText(createFirstTimeMessage(message.getFrom()[0].toString()));

        // Sender encodes encryption state and his public key in order to send it to recipient
        //TODO fold public key?
        String publicKey = DHHelper.PublicKeyClass.keyToString(keyPairSender.getPublic());
        formattedMessage.addHeader(STATE, RECIPIENT_GETS_SENDER_PUBLIC_KEY);
        formattedMessage.addHeader(PUBLIC_KEY_SENDER, publicKey);

        return formattedMessage;
    }

    private String createSubjectFirstTime(String emailAddress) {
        return "Komp Encryption Step 1 with " + emailAddress;
    }

    private String createFirstTimeMessage(String recipientEmail) {
        return FIRST_TIME_MESSAGE + recipientEmail + "\n" + FIRST_TIME_MESSAGE_2;
    }
}
