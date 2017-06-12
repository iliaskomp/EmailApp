package com.iliaskomp.encryption;

import com.iliaskomp.libs.Base64;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

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
        result[0] = textEncrypted; // TODO check text is not corrupt with repetitions?
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
}
