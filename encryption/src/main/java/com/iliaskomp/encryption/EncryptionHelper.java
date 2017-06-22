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

public class EncryptionHelper {

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

	public static KeyPair createKeyPair() throws InvalidKeySpecException,
			InvalidAlgorithmParameterException, NoSuchAlgorithmException,
			InvalidParameterSpecException {

		return DHAlgorithm.generateKeyPair();
    }

    public static SecretKey generateSecretKey(PrivateKey privateKeySelf, PublicKey publicKeyOther)
			throws InvalidKeyException, NoSuchAlgorithmException {
		return DHAlgorithm.agreeSecretKey(privateKeySelf, publicKeyOther);
	}
}
