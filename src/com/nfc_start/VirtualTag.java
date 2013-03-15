package com.nfc_start;

import iaik.asn1.structures.AlgorithmID;
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

import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;

import android.util.Log;

public class VirtualTag implements ITag{

	private ESDHPrivateKey _priv_key = null;
	private ESDHPublicKey _pub_key = null;
	
	/**
	 * Return shared secret
	 **/
	public byte[] generateSharedAESKey(ESDHPublicKey otherPublicKey){
		if(otherPublicKey == null)
			return null;
		KeyPairGenerator keyGen;
		SecretKey shared_secret = null;
		try {
			keyGen = KeyPairGenerator.getInstance("ESDH","IAIK");
			keyGen.initialize(1024);
			KeyPair dh_keypair = keyGen.generateKeyPair();
			_priv_key = (ESDHPrivateKey)dh_keypair.getPrivate();
			_pub_key = (ESDHPublicKey)dh_keypair.getPublic();
			 
			// we want AES key wrap
			AlgorithmID aesWrap = AlgorithmID.cms_aes128_wrap;
			// key length of KEK:
			int keyLength = 128;
			// generate the OtherInfo
			ESDHKEKParameterSpec otherInfo = new ESDHKEKParameterSpec(aesWrap.getAlgorithm(), keyLength);
			// the sender has supplied random patryAInfo:
			otherInfo.setPartyAInfo(null);
			// now create an ESDHKeyAgreement object:
			KeyAgreement esdh_key_agreement = KeyAgreement.getInstance("ESDH", "IAIK");
			SecureRandom sr = new iaik.security.random.SHA1Random();

			esdh_key_agreement.init(_priv_key, otherInfo, sr);
			 
			esdh_key_agreement.doPhase(otherPublicKey, true);
			shared_secret = esdh_key_agreement.generateSecret("AES");
			
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
		Log.d("AES Tag: ",sc);
		return shared_secret.getEncoded();
	}
	
	public ESDHPublicKey getPublicKey(){
		return _pub_key;
	}
	
	@Override
	public byte[] getUid() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] sign(byte[] data) {
		// TODO Auto-generated method stub
		return null;
	}

}
