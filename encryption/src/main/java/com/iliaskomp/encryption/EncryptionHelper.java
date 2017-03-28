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
	
	public static String[] encrypt(String text, SecretKey key) throws NoSuchAlgorithmException, NoSuchPaddingException, 
	InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        
//		Base64 base64 = new Base64();		
		byte[] textBytes = text.getBytes();		

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] textEncryptedBytes = cipher.doFinal(textBytes);
//        String textEncrypted = base64.encodeToString(textEncryptedBytes);
//        String textEncrypted = DatatypeConverter.printBase64Binary(textEncryptedBytes);
		String textEncrypted = Base64.encodeToString(textEncryptedBytes, Base64.NO_WRAP);

        byte[] ivBytes = cipher.getIV();
//        String ivString = base64.encodeAsString(ivBytes);
//        String ivString = DatatypeConverter.printBase64Binary(ivBytes);
        String ivString = Base64.encodeToString(ivBytes, Base64.NO_WRAP);

        String[] result = new String[2];
        result[0] = textEncrypted;
        result[1] = ivString;
        
		return result;              
	}

	public static String decrypt(String textEncrypted, SecretKey key, String ivString) throws NoSuchAlgorithmException, 
			NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
//		Base64 base64 = new Base64();
//		byte[] iv = base64.decode(ivString);
//		byte[] iv = DatatypeConverter.parseBase64Binary(ivString);
		byte[] iv = Base64.decode(ivString, Base64.NO_WRAP);

        Cipher cipher2 = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher2.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
		
//		byte[] textEncryptedBytesR = base64.decode(textEncrypted);
//		byte[] textEncryptedBytesR = DatatypeConverter.parseBase64Binary(textEncrypted);
		byte[] textEncryptedBytesR = Base64.decode(textEncrypted, Base64.NO_WRAP);

		byte[] textBytesDecrypted = cipher2.doFinal(textEncryptedBytesR);
		String textDecrypted = new String(textBytesDecrypted);
	
		return textDecrypted;
	}
}
