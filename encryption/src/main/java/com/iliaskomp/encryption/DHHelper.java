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

public class DHHelper {

    public static class PublicKeyClass {

        public static PublicKey decode(byte[] key) throws NoSuchAlgorithmException,
                InvalidKeySpecException {

            KeyFactory keyFactory = KeyFactory.getInstance("DH");
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(key);

            return keyFactory.generatePublic(x509KeySpec);
        }

        public static byte[] encode(PublicKey key) {
            return key.getEncoded();
        }

        public static String keyToString(PublicKey publicKey) {
            byte[] publicKeyEncodedSender = encode(publicKey);
            return Base64.encodeToString(publicKeyEncodedSender, Base64.NO_WRAP);
        }

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

        // Get params from public key
        public static DHParameterSpec keyToParams(PublicKey publicKey) {
            return ((DHPublicKey) publicKey).getParams();
        }
    }

    public static class PrivateKeyClass {
        private static byte[] encode(PrivateKey key) {
            return key.getEncoded();
        }

        public static PrivateKey decodePrivateKey(byte[] key) throws NoSuchAlgorithmException,
                InvalidKeySpecException {

            KeyFactory keyFactory = KeyFactory.getInstance("DH");
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(key);

            return keyFactory.generatePrivate(x509KeySpec);
        }

        public static String keyToString(PrivateKey privateKey) {
            byte[] privateKeyEncodedSender = encode(privateKey);
            return Base64.encodeToString(privateKeyEncodedSender, Base64.NO_WRAP);
        }

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

    public static class SecretKeyClass {

        public static byte[] encode(SecretKey key) {
            return key.getEncoded();
        }

        public static SecretKey decode(byte[] encodedKey) {
            return new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        }

        public static String keyToString(SecretKey secretKey) {
            byte[] secretKeyEncodedSender = encode(secretKey);
            return Base64.encodeToString(secretKeyEncodedSender, Base64.NO_WRAP);
        }

        public static SecretKey stringToKey(String string) {
            byte[] stringBytes = Base64.decode(string, Base64.NO_WRAP);
            SecretKeySpec spec = new SecretKeySpec(stringBytes, "AES");
            return decode(spec.getEncoded());
        }
    }

    public static BigInteger hexToBigInteger(String hex) {
        return new BigInteger(hex, 16);
    }
}
