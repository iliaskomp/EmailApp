package com.iliaskomp.encryption;

import com.iliaskomp.libs.Base64;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.SecretKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Helper for the Diffie-Hellman protocol
 */
public class DHHelper {

    /**
     * The Public key class.
     * Includes public key related helper methods
     */
    public static class PublicKeyClass {
        private static PublicKey decode(byte[] key) throws NoSuchAlgorithmException,
                InvalidKeySpecException {

            KeyFactory keyFactory = KeyFactory.getInstance("DH");
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(key);

            return keyFactory.generatePublic(x509KeySpec);
        }

        private static byte[] encode(PublicKey key) {
            return key.getEncoded();
        }

        /**
         * Convert a public key to string
         *
         * @param publicKey the public key
         * @return the public key as string
         */
        public static String keyToString(PublicKey publicKey) {
            byte[] publicKeyEncodedSender = encode(publicKey);
            return Base64.encodeToString(publicKeyEncodedSender, Base64.NO_WRAP);
        }

        /**
         * Convert string to public key
         *
         * @param string the string
         * @return the public key
         */
        public static PublicKey stringToKey(String string) {
            try {
                byte[] stringBytes = Base64.decode(string, Base64.NO_WRAP);
                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(stringBytes);
                KeyFactory kf = KeyFactory.getInstance("DH");
                return kf.generatePublic(keySpec);
            } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * Extract Diffie Hellman parameters from a public key
         *
         * @param publicKey the public key
         * @return the dh parameter spec containing the parameters
         */
        public static DHParameterSpec keyToParams(PublicKey publicKey) {
            return ((DHPublicKey) publicKey).getParams();
        }
    }

    /**
     * The Private key class.
     * Includes private key related helper methods
     */
    public static class PrivateKeyClass {
        private static byte[] encode(PrivateKey key) {
            return key.getEncoded();
        }

        private static PrivateKey decode(byte[] key) throws NoSuchAlgorithmException,
                InvalidKeySpecException {

            KeyFactory keyFactory = KeyFactory.getInstance("DH");
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(key);

            return keyFactory.generatePrivate(x509KeySpec);
        }

        /**
         * Convert a private key to string
         *
         * @param privateKey the private key
         * @return the private key as string
         */
        public static String keyToString(PrivateKey privateKey) {
            byte[] privateKeyEncodedSender = encode(privateKey);
            return Base64.encodeToString(privateKeyEncodedSender, Base64.NO_WRAP);
        }

        /**
         * Convert a string to private key.
         *
         * @param string the string
         * @return the private key
         */
        public static PrivateKey stringToKey(String string) {
            try {
                byte[] stringBytes = Base64.decode(string, Base64.NO_WRAP);
                PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(stringBytes);
                KeyFactory kf = KeyFactory.getInstance("DH");
                return kf.generatePrivate(keySpec);
            } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    /**
     * The Secret key class.
     * Includes secret key related helper methods
     */
    public static class SecretKeyClass {

        private static byte[] encode(SecretKey key) {
            return key.getEncoded();
        }

        private static SecretKey decode(byte[] encodedKey) {
            return new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        }

        /**
         * Convert a secret key to string.
         *
         * @param secretKey the secret key
         * @return the secret key as string
         */
        public static String keyToString(SecretKey secretKey) {
            byte[] secretKeyEncodedSender = encode(secretKey);
            return Base64.encodeToString(secretKeyEncodedSender, Base64.NO_WRAP);
        }

        /**
         * Convert a string to secret key.
         *
         * @param string the string
         * @return the secret key
         */
        public static SecretKey stringToKey(String string) {
            byte[] stringBytes = Base64.decode(string, Base64.NO_WRAP);
            SecretKeySpec spec = new SecretKeySpec(stringBytes, "AES");
            return decode(spec.getEncoded());
        }
    }

    /**
     * Convert hex to big integer.
     *
     * @param hex the hex
     * @return the big integer
     */
    public static BigInteger hexToBigInteger(String hex) {
        return new BigInteger(hex, 16);
    }
}
