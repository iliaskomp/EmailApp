package com.iliaskomp.dhalgorithm;
import java.math.BigInteger;
import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
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

public class DHAlgorithm {
    private static final int AES_KEY_SIZE = 128;
    private static final int DH_PARAMETERS_SIZE = 1024;
    
	public KeyPair generateKeyPair() throws NoSuchAlgorithmException, InvalidParameterSpecException,
			InvalidAlgorithmParameterException, InvalidKeySpecException {
		// generate DH parameters
		DHParameterSpec kp = generateParameters();
		BigInteger p = kp.getP();
		BigInteger g = kp.getG();	
		
		// generates public/private key from p, g
		return generateKeyPairFromParameters(p, g);	
	}
	
	// returns keypair kp.getPublic(), kp.getPrivate()
	public KeyPair generateKeyPairFromParameters(BigInteger p, BigInteger g)
			  throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeySpecException {
		  
		    KeyPairGenerator kpg = KeyPairGenerator.getInstance("DH");

		    DHParameterSpec param = new DHParameterSpec(p, g);
		    kpg.initialize(param);
		    KeyPair kp = kpg.generateKeyPair();

		    KeyFactory kfactory = KeyFactory.getInstance("DH");

//		    DHPublicKeySpec kspec = (DHPublicKeySpec) kfactory.getKeySpec(kp.getPublic(),  DHPublicKeySpec.class);
		    		    
		    return kp;
	  }
	
    public SecretKey agreeSecretKey(PrivateKey privateKeySelf, PublicKey publicKeyOther)
    		throws NoSuchAlgorithmException, InvalidKeyException, IllegalStateException {
    	
    	KeyAgreement keyAgreement = KeyAgreement.getInstance("DH");
    	keyAgreement.init(privateKeySelf);
    
    	// Computes the KeyAgreement
    	keyAgreement.doPhase(publicKeyOther, true);
            
        // Generates the shared secret
        byte[] secret = keyAgreement.generateSecret();

        // === Generates an AES key ===

        // you should really use a Key Derivation Function instead, but this is
        // rather safe

        MessageDigest sha256 = MessageDigest.getInstance("SHA-256"); 
        byte[] keyEncoded = Arrays.copyOf(sha256.digest(secret), AES_KEY_SIZE / Byte.SIZE);

        SecretKey secretKey = new SecretKeySpec(keyEncoded, "AES");
        return secretKey;
    }

	// ============================================= HELPER ======================================================
	
	//    p = dhSpec.getP();;
	//    g = dhSpec.getG();
	private DHParameterSpec generateParameters(int bits) throws NoSuchAlgorithmException, InvalidParameterSpecException {
		
		AlgorithmParameterGenerator generator = AlgorithmParameterGenerator.getInstance("DH");
		generator.init(bits);
		
		AlgorithmParameters params = generator.generateParameters();	
	    DHParameterSpec dhSpec = (DHParameterSpec) params.getParameterSpec(DHParameterSpec.class);

	    return dhSpec;
	    
	}
	
	// Default 1024 bits
	private DHParameterSpec generateParameters() throws NoSuchAlgorithmException, InvalidParameterSpecException {
		return generateParameters(DH_PARAMETERS_SIZE);
	    
	}

	// KeyAgreement =====================================
	
	public void initKeyAgreement(KeyAgreement keyAgreement, PrivateKey privateKey)
			throws NoSuchAlgorithmException, InvalidKeyException {		
		keyAgreement.init(privateKey);
	}
	
	public KeyAgreement generateKeyAgreement() throws NoSuchAlgorithmException {
		return KeyAgreement.getInstance("DH");
	}
  
	public byte[] generateSecretKeyEncoded(KeyAgreement keyAgreement) {
		return keyAgreement.generateSecret();
	}
  
	public SecretKey generateSecretKey(KeyAgreement keyAgreement)
			throws InvalidKeyException, IllegalStateException, NoSuchAlgorithmException {
		return keyAgreement.generateSecret("AES");
	}
}
