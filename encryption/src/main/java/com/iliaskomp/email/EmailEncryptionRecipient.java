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
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import static com.iliaskomp.email.HeaderFields.FirstInteractionState;
import static com.iliaskomp.email.HeaderFields.HeaderX;
import static com.iliaskomp.email.HeaderFields.HeaderX.PUBLIC_KEY_SENDER;

/**
 * Created by IliasKomp on 22/05/17.
 */

public class EmailEncryptionRecipient {

    // String messages
    private static final String FIRST_TIME_MESSAGE = "Step 2: This is an automated message to establish " +
            "secret communication with ";

    // if encryption state is not found, it returns null
    public String getHeaderState(Message message) throws MessagingException {
        HashMap<String, String> headers = EmailHelper.getHeadersMapFromEnumeration(message.getAllHeaders());

        for (Map.Entry<String, String> header : headers.entrySet()) {
            if (header.getKey().equals(HeaderX.STATE)) {
                return header.getValue();
            }
        }
        return HeaderX.NO_HEADER_STRING;
    }

    public String getHeaderIv(Message message) throws MessagingException {
        HashMap<String, String> headers = EmailHelper.getHeadersMapFromEnumeration(message.getAllHeaders());

        for (Map.Entry<String, String> header : headers.entrySet()) {
            if (header.getKey().equals(HeaderX.IV)) {
                return header.getValue();
            }
        }
        return HeaderX.NO_HEADER_STRING;
    }

    public MimeMessage createMessageWithPublicKey(Session emailSession, Message message, KeyPair keyPairRecipient) throws MessagingException {
        MimeMessage messageBack = new MimeMessage(emailSession);

        messageBack.setFrom(message.getAllRecipients()[0]);
        messageBack.setRecipient(Message.RecipientType.TO, message.getFrom()[0]);
        messageBack.setSentDate(message.getSentDate());
        messageBack.setSubject(createFirstTimeSubject(message.getAllRecipients()[0].toString())); //TODO check if it works
        messageBack.setText(createFirstTimeMessage(message.getAllRecipients()[0].toString()));

        // Sender encodes encryption state and his public key in order to send it to recipient
        //TODO remove headers
        messageBack.removeHeader(HeaderX.STATE);
        messageBack.removeHeader(HeaderX.PUBLIC_KEY_SENDER);

        //TODO fold public key?
        messageBack.addHeader(HeaderX.STATE, FirstInteractionState.SENDER_GETS_RECIPIENT_PUBLIC_KEY);
        messageBack.addHeader(HeaderX.PUBLIC_KEY_RECIPIENT, DHHelper.PublicKeyClass.publicKeyToString(keyPairRecipient.getPublic()));

        return messageBack;
    }

    public KeyPair createKeyPairFromSender(Message message) throws MessagingException {

        PublicKey publicKeySender = getSenderPublicKeyFromHeader(message);

        try {
            DHParameterSpec paramsFromSender = DHHelper.PublicKeyClass.publicKeyToParams(publicKeySender);
            DHAlgorithm dh = new DHAlgorithm();
            return dh.generateKeyPairFromParameters(paramsFromSender.getP(), paramsFromSender.getG());
        } catch (InvalidKeySpecException | InvalidAlgorithmParameterException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

    public PublicKey getSenderPublicKeyFromHeader(Message message) throws MessagingException {
        HashMap<String, String> headers = EmailHelper.getHeadersMapFromEnumeration(message.getAllHeaders());

        for (Map.Entry<String, String> header : headers.entrySet()) {
            if (header.getKey().equals(PUBLIC_KEY_SENDER)) {
                String publicKeyString = header.getValue();
                return DHHelper.PublicKeyClass.stringToPublicKey(publicKeyString);
            }
        }
        return null;
    }



    private String createFirstTimeSubject(String email) {
        return "Komp Encryption: Step 2";
    }

    private String createFirstTimeMessage(String email) {
        StringBuilder messageSb = new StringBuilder();
        messageSb.append(FIRST_TIME_MESSAGE)
                .append(email)
                .append(" by obtaining their public key.")
                .append("\nThis is the first step towards establishing a secret shared key.")
                .append("\nAn automated message with your public key will be send automatically")
                .append("\nYou do not have to do any action with this email.");

        return messageSb.toString();
    }



}
