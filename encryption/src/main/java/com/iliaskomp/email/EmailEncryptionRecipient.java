package com.iliaskomp.email;

import com.iliaskomp.dhalgorithm.DHAlgorithm;
import com.iliaskomp.dhalgorithm.DHHelper;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.spec.DHParameterSpec;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import static com.iliaskomp.email.HeaderFields.FirstInteractionState;
import static com.iliaskomp.email.HeaderFields.HeaderX;
import static com.iliaskomp.email.HeaderFields.HeaderX.PUBLIC_KEY_SENDER;

/**
 * Created by IliasKomp on 22/05/17.
 */

public class EmailEncryptionRecipient {

    // String messages
    private static final String FIRST_TIME_MESSAGE = "This is an automated message to establish" +
            "secret communication with other user by obtaining his/her public key.";

    // if encryption state is not found, it returns null
    public String getHeaderState(Message message) throws MessagingException {
        HashMap<String, String> headers = EmailHelper.getHeadersMapFromEnumeration(message.getAllHeaders());

        for (Map.Entry<String, String> header : headers.entrySet()) {
            if (header.getKey().equals(HeaderX.STATE)) {
                return header.getValue();
            }
        }
        return null;
    }

    public MimeMessage createMessageWithPublicKey(Message message, KeyPair keyPairRecipient) throws MessagingException {
        MimeMessage messageBack = new MimeMessage((MimeMessage) message);

        messageBack.setFrom(message.getFrom()[0]);
        messageBack.setRecipient(Message.RecipientType.TO, message.getAllRecipients()[0]);
        messageBack.setSentDate(message.getSentDate());
        messageBack.setSubject(message.getSubject());
        messageBack.setText(FIRST_TIME_MESSAGE);

        // Sender encodes encryption state and his public key in order to send it to recipient
        messageBack.addHeader(HeaderX.STATE, FirstInteractionState.RECIPIENT_FIRST_TIME);
        messageBack.addHeader(HeaderX.PUBLIC_KEY_RECIPIENT, DHHelper.publicKeyToString(keyPairRecipient.getPublic()));

        return messageBack;
    }

    public KeyPair createKeyPairFromSender(Message message) throws MessagingException {
        HashMap<String, String> headers = EmailHelper.getHeadersMapFromEnumeration(message.getAllHeaders());

        for (Map.Entry<String, String> header : headers.entrySet()) {
            if (header.getKey().equals(PUBLIC_KEY_SENDER)) {
                String publicKeyString = header.getValue();
                PublicKey publicKeySender = DHHelper.stringToPublicKey(publicKeyString);

                try {
                    DHParameterSpec paramsFromSender = DHHelper.publicKeyToParams(publicKeySender);
                    DHAlgorithm dh = new DHAlgorithm();
                    return dh.generateKeyPairFromParameters(paramsFromSender.getP(), paramsFromSender.getG());
                } catch (InvalidKeySpecException | InvalidAlgorithmParameterException | NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }


            }
        }
        return null;
    }
}
