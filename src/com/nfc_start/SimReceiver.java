package com.nfc_start;

import iaik.asn1.structures.AlgorithmID;
import iaik.security.cipher.SecretKey;
import iaik.security.dh.ESDHKEKParameterSpec;
import iaik.security.dh.ESDHPrivateKey;
import iaik.security.dh.ESDHPublicKey;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyAgreement;
import javax.crypto.NoSuchPaddingException;

import android.util.Log;

public class SimReceiver {

	private ESDHPrivateKey _priv_key = null;
	private ESDHPublicKey _pub_key = null;
	
	public ESDHPublicKey getPublicKey(){
		return _pub_key;
	}
	
	public SimReceiver(){
		KeyPairGenerator keyGen;

		try {
			keyGen = KeyPairGenerator.getInstance("ESDH","IAIK");
			keyGen.initialize(1024);
			KeyPair dh_keypair = keyGen.generateKeyPair();
			_priv_key = (ESDHPrivateKey)dh_keypair.getPrivate();
			_pub_key = (ESDHPublicKey)dh_keypair.getPublic();
			 
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public byte[] resolveSharedAESKey(ESDHPublicKey ohterPublicKey){
		SecretKey shared_secret = null;
		try{
		// we want AES key wrap
		AlgorithmID aesWrap = AlgorithmID.cms_aes128_wrap;
		// key length of KEK:
		int keyLength = 128;
		// generate the OtherInfo
		ESDHKEKParameterSpec otherInfo = new ESDHKEKParameterSpec(aesWrap.getAlgorithm(), keyLength);
		
		KeyAgreement esdh_key_agreement= KeyAgreement.getInstance("ESDH", "IAIK");
		SecureRandom sr = new iaik.security.random.SHA1Random();
		esdh_key_agreement.init(_priv_key, otherInfo, sr);
		 
		esdh_key_agreement.doPhase(ohterPublicKey, true);
		 
		shared_secret = (SecretKey) esdh_key_agreement.generateSecret("AES");
		
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String sc = Utils.byteArrayToHexString(shared_secret.getEncoded());
		Log.d("AES Receiver: ",sc);
		return shared_secret.getEncoded();
		
	}

	public byte[] decrypt(byte[] cipherText, byte[] resolvedAESKey) {
		
		byte[] result = null;
		Cipher cipher = null;
		try {
			cipher  = Cipher.getInstance("Rijndael", "IAIK");
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NoSuchProviderException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NoSuchPaddingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    
	    SecretKey sk = new SecretKey(resolvedAESKey, "AES");

	    try {
			cipher.init(Cipher.DECRYPT_MODE, sk);
		} catch (InvalidKeyException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    try {
	    	result = cipher.doFinal(cipherText);
		} catch (IllegalBlockSizeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (BadPaddingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return result;
	}
	
}
