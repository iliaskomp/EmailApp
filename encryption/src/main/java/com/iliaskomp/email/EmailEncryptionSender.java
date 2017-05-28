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

import static com.iliaskomp.email.HeaderFields.FirstInteractionState.SENDER_FIRST_TIME;
import static com.iliaskomp.email.HeaderFields.HeaderX.PUBLIC_KEY_SENDER;
import static com.iliaskomp.email.HeaderFields.HeaderX.STATE;

/**
 * Created by IliasKomp on 28/03/17.
 */

public class EmailEncryptionSender {
    // String messages
    private static final String FIRST_TIME_MESSAGE = "Step 1: This is an automated message to establish" +
            " secret communication with ";

    private static final String FIRST_TIME_MESSAGE_2 = "\n\nThis message is part of the encryption " +
            "library. \n\nIf you have it installed, then an automatic message will be sent to " +
            "establish encrypted communication. Otherwise, either install the library or contact " +
            "the sender for an unencrypted email";

//    private KeyPair mKeyPair;

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

    // TODO First need to check db for email. //No need for message here.
    // TODO multipart messages??
    // TODO IMPORTANT remove session as parameter?
    // Generate message for the first communication between sender/recipient
    // Constructs a message with generic info as message and public key as header.
    public MimeMessage getEmailFirstTimeSending(MimeMessage message, Session session, KeyPair keyPairSender)
            throws InvalidKeySpecException, InvalidAlgorithmParameterException,
            NoSuchAlgorithmException, InvalidParameterSpecException, IOException, MessagingException {

        MimeMessage formattedMessage = new MimeMessage(session); //TODO message or mimemessage

        formattedMessage.setFrom(message.getFrom()[0]);
        formattedMessage.setRecipient(Message.RecipientType.TO, message.getAllRecipients()[0]);
        formattedMessage.setSentDate(message.getSentDate());
        formattedMessage.setSubject(createSubjectFirstTime(message.getFrom()[0].toString()));
        formattedMessage.setText(createFirstTimeMessage(message.getFrom()[0].toString()));

        // Sender encodes encryption state and his public key in order to send it to recipient
        formattedMessage.addHeader(STATE, SENDER_FIRST_TIME);
        formattedMessage.addHeader(PUBLIC_KEY_SENDER, DHHelper.publicKeyToString(keyPairSender.getPublic()));

        return formattedMessage;
    }

    private String createSubjectFirstTime(String emailAddress) {
        return "Establishing secret key with " + emailAddress;
    }

    private String createFirstTimeMessage(String recipientEmail) {
        return FIRST_TIME_MESSAGE + recipientEmail + "\n" + FIRST_TIME_MESSAGE_2;
    }



//            formattedMessage.setText(FIRST_TIME_MESSAGE + "\n\n" + message.getContent().toString()); //TODO pictures etc?


//    public KeyPair getKeyPair() {
//        return mKeyPair;
//    }
}
