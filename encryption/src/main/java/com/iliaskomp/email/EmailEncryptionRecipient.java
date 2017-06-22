package com.iliaskomp.email;

import com.iliaskomp.dhalgorithm.DHAlgorithm;
import com.iliaskomp.dhalgorithm.DHHelper;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.spec.DHParameterSpec;
import javax.mail.Message;
import javax.mail.MessagingException;

/**
 * Created by IliasKomp on 22/05/17.
 */

public class EmailEncryptionRecipient {

    public KeyPair generateRecipientKeyPairFromSender(Message message) throws MessagingException {

        PublicKey publicKeySender = HeaderUtils.getHeaderSenderPublicKey(message);

        try {
            DHParameterSpec paramsFromSender =
                    DHHelper.PublicKeyClass.keyToParams(publicKeySender);
            DHAlgorithm dh = new DHAlgorithm();
            return DHAlgorithm.generateKeyPairFromParameters(paramsFromSender.getP(), paramsFromSender.getG());
        } catch (InvalidKeySpecException |
                InvalidAlgorithmParameterException |
                NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }


}
