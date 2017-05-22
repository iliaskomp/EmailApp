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

import static com.iliaskomp.email.HeaderFields.FirstInteractionState.FIRST_TIME_SEND;
import static com.iliaskomp.email.HeaderFields.HeaderX.PUBLIC_KEY_SENDER;
import static com.iliaskomp.email.HeaderFields.HeaderX.STATE;

/**
 * Created by IliasKomp on 28/03/17.
 */

public class EmailEncryptionSender {

    // String messages
    private static final String FIRST_TIME_MESSAGE = "This message is part of the encryption " +
            "library. If you have it installed, then an automatic message will be sent to " +
            "establish encrypted communication. Otherwise, either install the library or contact" +
            "the sender for an unencrypted email";


    // Member variables
    private KeyPair mKeyPair;

    public EmailEncryptionSender() {

        try {
            DHAlgorithm dh = new DHAlgorithm();
            mKeyPair = dh.generateKeyPair();
        } catch (NoSuchAlgorithmException | InvalidParameterSpecException |
                InvalidAlgorithmParameterException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }

    // TODO multipart messages??
    // Generate message for the first communication between sender/recipient
    // Constructs a message with generic info as message and public key as header.
    // On the
    public MimeMessage getEmailFirstTimeSending(MimeMessage message, Session session)
            throws InvalidKeySpecException, InvalidAlgorithmParameterException,
            NoSuchAlgorithmException, InvalidParameterSpecException, IOException {

        MimeMessage formattedMessage = new MimeMessage(session); //TODO message or mimemessage

        try {
            formattedMessage.setFrom(message.getFrom()[0]);
            formattedMessage.setRecipient(Message.RecipientType.TO, message.getAllRecipients()[0]);
            formattedMessage.setSentDate(message.getSentDate());
            formattedMessage.setSubject(message.getSubject());

            formattedMessage.setText(FIRST_TIME_MESSAGE);
//            formattedMessage.setText(FIRST_TIME_MESSAGE + "\n\n" + message.getContent().toString()); //TODO pictures etc?

            // Sender encodes his public key in order to send it to recipient
            formattedMessage.addHeader(STATE, FIRST_TIME_SEND);
            formattedMessage.addHeader(PUBLIC_KEY_SENDER, DHHelper.publicKeyToString(mKeyPair.getPublic()));
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return formattedMessage;
    }

    public KeyPair getKeyPair() {
        return mKeyPair;
    }
}
