package com.iliaskomp.encryption;

import java.math.BigInteger;
import java.security.AlgorithmParameterGenerator;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.Arrays;

import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Takes care of the Diffie-Hellman related process
 */
public class DHAlgorithm {
    private static final int AES_KEY_SIZE = 128;
    private static final int DH_PARAMETERS_SIZE = 2048;

    // 2048 MODP Group: https://tools.ietf.org/html/rfc3526#section-1
    private static final BigInteger gParameter = new BigInteger("2");
    private static final BigInteger pParameter = DHHelper.hexToBigInteger(
                    "FFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD1" +
                    "29024E088A67CC74020BBEA63B139B22514A08798E3404DD" +
                    "EF9519B3CD3A431B302B0A6DF25F14374FE1356D6D51C245" +
                    "E485B576625E7EC6F44C42E9A637ED6B0BFF5CB6F406B7ED" +
                    "EE386BFB5A899FA5AE9F24117C4B1FE649286651ECE45B3D" +
                    "C2007CB8A163BF0598DA48361C55D39A69163FA8FD24CF5F" +
                    "83655D23DCA3AD961C62F356208552BB9ED529077096966D" +
                    "670C354E4ABC9804F1746C08CA237327FFFFFFFFFFFFFFFF");

    /**
     * Generate a Diffie Hellman key pair.
     *
     * @return the key pair
     * @throws NoSuchAlgorithmException           the no such algorithm exception
     * @throws InvalidParameterSpecException      the invalid parameter spec exception
     * @throws InvalidAlgorithmParameterException the invalid algorithm parameter exception
     * @throws InvalidKeySpecException            the invalid key spec exception
     */
    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException, InvalidParameterSpecException,
            InvalidAlgorithmParameterException, InvalidKeySpecException {
        // generate DH parameters
        DHParameterSpec kp = DHAlgorithm.generateParameters();
        BigInteger p = kp.getP();
        BigInteger g = kp.getG();

        // generates public/private key from p, g
        return DHAlgorithm.generateKeyPairFromParameters(p, g);
    }

    /**
     * Generate a Diffie Hellman key pair with parameters p and g.
     *
     * @param p the p
     * @param g the g
     * @return the key pair
     * @throws NoSuchAlgorithmException           the no such algorithm exception
     * @throws InvalidAlgorithmParameterException the invalid algorithm parameter exception
     */
    public static KeyPair generateKeyPairFromParameters(BigInteger p, BigInteger g)
            throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {

        KeyPairGenerator kpg = KeyPairGenerator.getInstance("DH");

        DHParameterSpec param = new DHParameterSpec(p, g);
        kpg.initialize(param);
        return kpg.generateKeyPair();
    }

    /**
     * Generate a secret key given a private key and a public key of another user
     *
     * @param privateKeySelf the private key of the user
     * @param publicKeyOther the public key of the other user
     * @return the secret key
     * @throws NoSuchAlgorithmException the no such algorithm exception
     * @throws InvalidKeyException      the invalid key exception
     * @throws IllegalStateException    the illegal state exception
     */
    public static SecretKey agreeSecretKey(PrivateKey privateKeySelf, PublicKey publicKeyOther)
            throws NoSuchAlgorithmException, InvalidKeyException, IllegalStateException {

        KeyAgreement keyAgreement = KeyAgreement.getInstance("DH");
        keyAgreement.init(privateKeySelf);
        // Computes the KeyAgreement
        keyAgreement.doPhase(publicKeyOther, true);
        // Generates the shared secret
        byte[] secret = keyAgreement.generateSecret();

        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        byte[] keyEncoded = Arrays.copyOf(sha256.digest(secret), AES_KEY_SIZE / Byte.SIZE);
        // Generate AES key
        return new SecretKeySpec(keyEncoded, "AES");
    }

    private static DHParameterSpec generateParameters(int bits) throws NoSuchAlgorithmException {

        AlgorithmParameterGenerator generator = AlgorithmParameterGenerator.getInstance("DH");
        generator.init(bits);
        return new DHParameterSpec(pParameter, gParameter);
    }

    // Default 1024 bits
    private static DHParameterSpec generateParameters() throws NoSuchAlgorithmException {
        return generateParameters(DH_PARAMETERS_SIZE);
    }

}
