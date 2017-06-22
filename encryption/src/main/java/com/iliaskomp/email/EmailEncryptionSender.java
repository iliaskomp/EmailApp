package com.iliaskomp.email;

import com.iliaskomp.dhalgorithm.DHAlgorithm;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;

/**
 * Created by IliasKomp on 28/03/17.
 */

public class EmailEncryptionSender {

    public KeyPair createKeyPair() {
        KeyPair keyPair = null;
        try {
            DHAlgorithm dh = new DHAlgorithm();
            keyPair = DHAlgorithm.generateKeyPair(dh);
        } catch (NoSuchAlgorithmException | InvalidParameterSpecException |
                InvalidAlgorithmParameterException | InvalidKeySpecException e) {
            e.printStackTrace();
        }

        return keyPair;
    }




}
