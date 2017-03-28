package com.iliaskomp.encryption;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.xml.bind.DatatypeConverter;

public class EncryptionHelper {
	
	public static String[] encrypt(String text, SecretKey key) throws NoSuchAlgorithmException, NoSuchPaddingException, 
	InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        
//		Base64 base64 = new Base64();		
		byte[] textBytes = text.getBytes();		

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] textEncryptedBytes = cipher.doFinal(textBytes);
//        String textEncrypted = base64.encodeToString(textEncryptedBytes);
        String textEncrypted = DatatypeConverter.printBase64Binary(textEncryptedBytes);
        
        byte[] ivBytes = cipher.getIV();
//        String ivString = base64.encodeAsString(ivBytes);
        String ivString = DatatypeConverter.printBase64Binary(ivBytes);
        
        String[] result = new String[2];
        result[0] = textEncrypted;
        result[1] = ivString;
        
		return result;              
	}

	public static String decrypt(String textEncrypted, SecretKey key, String ivString) throws NoSuchAlgorithmException, 
			NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
//		Base64 base64 = new Base64();
//		byte[] iv = base64.decode(ivString);
		byte[] iv = DatatypeConverter.parseBase64Binary(ivString);
				
        Cipher cipher2 = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher2.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
		
//		byte[] textEncryptedBytesR = base64.decode(textEncrypted);
		byte[] textEncryptedBytesR = DatatypeConverter.parseBase64Binary(textEncrypted);
		
		byte[] textBytesDecrypted = cipher2.doFinal(textEncryptedBytesR);
		String textDecrypted = new String(textBytesDecrypted);
	
		return textDecrypted;
	}
}
