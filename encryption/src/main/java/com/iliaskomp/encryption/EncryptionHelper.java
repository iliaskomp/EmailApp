package com.iliaskomp.encryption;

import com.iliaskomp.email.HeaderUtils;
import com.iliaskomp.libs.Base64;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.mail.Message;
import javax.mail.MessagingException;

/**
 * Helper for the encryption process
 */
public class EncryptionHelper {

	/**
	 * Encrypts a string with the given secret key
	 *
	 * @param text the plaintext
	 * @param key  the secret key
	 * @return the encrypted text
	 * @throws NoSuchAlgorithmException  the no such algorithm exception
	 * @throws NoSuchPaddingException    the no such padding exception
	 * @throws InvalidKeyException       the invalid key exception
	 * @throws IllegalBlockSizeException the illegal block size exception
	 * @throws BadPaddingException       the bad padding exception
	 */
	public static String[] encrypt(String text, SecretKey key)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException {

		byte[] textBytes = text.getBytes();

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] textEncryptedBytes = cipher.doFinal(textBytes);
		String textEncrypted = Base64.encodeToString(textEncryptedBytes, Base64.NO_WRAP);

        byte[] ivBytes = cipher.getIV();
        String ivString = Base64.encodeToString(ivBytes, Base64.NO_WRAP);

        String[] result = new String[2];
        result[0] = textEncrypted;
        result[1] = ivString;

		return result;
	}

	/**
	 * Decrypt a string given a secret key and an initialization vector.
	 *
	 * @param textEncrypted the encrypted text
	 * @param key           the secret key
	 * @param ivString      the iv string
	 * @return the decrypted text
	 * @throws NoSuchAlgorithmException           the no such algorithm exception
	 * @throws NoSuchPaddingException             the no such padding exception
	 * @throws InvalidKeyException                the invalid key exception
	 * @throws IllegalBlockSizeException          the illegal block size exception
	 * @throws BadPaddingException                the bad padding exception
	 * @throws InvalidAlgorithmParameterException the invalid algorithm parameter exception
	 */
	public static String decrypt(String textEncrypted, SecretKey key, String ivString)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {

		byte[] iv = Base64.decode(ivString, Base64.NO_WRAP);

        Cipher cipher2 = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher2.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));

		byte[] textEncryptedBytesR = Base64.decode(textEncrypted, Base64.NO_WRAP);
		byte[] textBytesDecrypted = cipher2.doFinal(textEncryptedBytesR);

		return new String(textBytesDecrypted);
	}

	/**
	 * Generate a recipient's DH key pair from the sender's parameters (extract the from the headers).
	 *
	 * @param message the message
	 * @return the recipient's key pair
	 * @throws MessagingException the messaging exception
	 */
	public static KeyPair generateRecipientKeyPairFromSender(Message message) throws MessagingException {

        PublicKey publicKeySender = HeaderUtils.getHeaderSenderPublicKey(message);

        try {
            DHParameterSpec paramsFromSender = DHHelper.PublicKeyClass.keyToParams(publicKeySender);
            return DHAlgorithm.generateKeyPairFromParameters(paramsFromSender.getP(), paramsFromSender.getG());
        } catch (InvalidAlgorithmParameterException |
                NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

	/**
	 * Create a Diffie-Hellman key pair.
	 *
	 * @return the Diffie-Hellman key pair
	 * @throws InvalidKeySpecException            the invalid key spec exception
	 * @throws InvalidAlgorithmParameterException the invalid algorithm parameter exception
	 * @throws NoSuchAlgorithmException           the no such algorithm exception
	 * @throws InvalidParameterSpecException      the invalid parameter spec exception
	 */
	public static KeyPair createKeyPair() throws InvalidKeySpecException,
			InvalidAlgorithmParameterException, NoSuchAlgorithmException,
			InvalidParameterSpecException {

		return DHAlgorithm.generateKeyPair();
    }

	/**
	 * Generate secret key given a user's private key and the other's users public key
	 *
	 * @param privateKeySelf the private key of the user
	 * @param publicKeyOther the public key of the other user
	 * @return the secret key
	 * @throws InvalidKeyException      the invalid key exception
	 * @throws NoSuchAlgorithmException the no such algorithm exception
	 */
	public static SecretKey generateSecretKey(PrivateKey privateKeySelf, PublicKey publicKeyOther)
			throws InvalidKeyException, NoSuchAlgorithmException {
		return DHAlgorithm.agreeSecretKey(privateKeySelf, publicKeyOther);
	}
}
