package com.iliaskomp.dhalgorithm;

import java.math.BigInteger;
import java.security.AlgorithmParameterGenerator;
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
    private static final int DH_PARAMETERS_SIZE = 2048;

//	private static final BigInteger gParameter = new BigInteger("30264306634644880594158867625747848706916872841063122828643568287772881942934900848673039430806910043579808817454530154099818054521622258972595261157641516333347121629234446599561327820569512671021929731950993074510705520646105347098491488676762146997514644489157383292667682970661633465348782931969451743765793783311193159935847795481391902790383657835223866853501529943581964575339405008807207039012454146625115003992606581766528346529703722534382380384132098732200632498812862116279204073968537974124649537888511484308923554099212524687954748596092130903138259529875723984124226277648181212356809890678594161961132");
//	private static final BigInteger pParameter = new BigInteger("31564330507231538318848961667459392520201145976425502603300142515349950439454283093028267383310344415289318772357902985734333138456581096138814519433585207592382807629791827612378480463613035340337628293596492873644165731359619805048418783433079307521295470486805260877427951473876259087912929607261712004902944779383008246781072467810099470675255702458490709409788675565960586484043502846440235791514841114718137541865030930409636521204548964445701434527846359126813487756048615385145563102473849655695938424950938241954420115012597318904559970191611274810788020463134239748131176683795217520290737698272843349477697");

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

        // use a Key Derivation Function instead

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
		
//		AlgorithmParameters params = generator.generateParameters();
//	    DHParameterSpec dhSpec = (DHParameterSpec) params.getParameterSpec(DHParameterSpec.class);
//		System.out.println("G: " + dhSpec.getG() + "\nP: " + dhSpec.getP());

		DHParameterSpec dhSpec = new DHParameterSpec(pParameter, gParameter);
		return dhSpec;
	    
	}
	
	// Default 1024 bits
	private DHParameterSpec generateParameters() throws NoSuchAlgorithmException, InvalidParameterSpecException {
		return generateParameters(DH_PARAMETERS_SIZE);
	    
	}

	// KeyAgreement =====================================
	
//	public void initKeyAgreement(KeyAgreement keyAgreement, PrivateKey privateKey)
//			throws NoSuchAlgorithmException, InvalidKeyException {
//		keyAgreement.init(privateKey);
//	}
//
//	public KeyAgreement generateKeyAgreement() throws NoSuchAlgorithmException {
//		return KeyAgreement.getInstance("DH");
//	}
//
//	public byte[] generateSecretKeyEncoded(KeyAgreement keyAgreement) {
//		return keyAgreement.generateSecret();
//	}
//
//	public SecretKey generateSecretKey(KeyAgreement keyAgreement)
//			throws InvalidKeyException, IllegalStateException, NoSuchAlgorithmException {
//		return keyAgreement.generateSecret("AES");
//	}
}
