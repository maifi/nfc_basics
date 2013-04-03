package com.nfc_start;

import iaik.security.dh.ESDHPublicKey;

import java.io.IOException;

import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.util.Log;

/***
 * 
 * 
 */
public class YubiKey implements ITag {

	private IsoDep currentIsoDep;

	private static byte[] SELECT_BY_ID = new byte[] { (byte) 0x00, (byte) 0xA4,
			(byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x00 };
	private static byte[] READ_BINARY = new byte[] { (byte) 0x00, (byte) 0xB0,
			(byte) 0x00, (byte) 0x00, (byte) 0x00 };
	private static byte[] INTERNAL_AUTHENTICATE_ECDSA = new byte[] {
			(byte) 0x00, (byte) 0x88, (byte) 0x01, (byte) 0x00, (byte) 0x10,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x30 };
	private static byte[] GET_CHALLENGE = new byte[] { (byte) 0x00, (byte) 0x84,
		(byte) 0x00, (byte) 0x00, (byte) 0x08 };
	
	private static byte[] GET_ID = new byte[]{(byte) 0xFF, (byte) 0xCA, 0x00, 0x00, 0x00};

	byte[] SELECT = {
			(byte) 0x00,
			// CLA
			(byte) 0xA4, // INS Instruction
			(byte) 0x04, // P1 Parameter 1
			(byte) 0x00, // P2 Parameter 2
			(byte) 0x07, // Length
			(byte) 0xD2,0x76,0x00,0x01,0x24,0x01,0x02
			//0x63,0x64,0x63,0x00,0x00,0x00,0x00,0x32,0x32,0x31 // AID
			};
	
	private static final boolean DEBUG = true;
	private static final short FILEID_UID = (short) 0x0005;
	private static final short FILEID_CERT = (short) 0x0001;

	public YubiKey(Tag tag) throws Exception {
		this.currentIsoDep = IsoDep.get(tag);
		if (this.currentIsoDep == null) {
			throw new Exception("Tag does not support ISO-DEP.");
		}
		try {
			this.currentIsoDep.connect();
		} catch (IOException e) {
			throw new Exception("Could not connect to Tag.");
		}
	}

	public byte[] SelectApp(){
		byte[] response = null;
		try {
			response = this.currentIsoDep
					.transceive(SELECT);
		} catch (IOException e) {
			Log.e("NFC-CryptoTag", "NFC: IOException caught during transceive(): "
					+ e.getMessage());
			return null;
		}
		Log.d("NFC-CryptoTag", "Select Applset sent");
		return response;
	}
	
	public byte[] encrypt(byte[] plaintext){
		byte[] response = new byte[16];
		
		byte[] ENCRYPT = {
				(byte) 0x00,
				// CLA
				(byte) 0x01, // INS Instruction
				(byte) 0x00, // P1 Parameter 1
				(byte) 0x00, // P2 Parameter 2
				(byte) 0x10, // Length
				(byte) 1,1,1,1,2,2,2,2,3,3,3,3,4,4,4,4,
				(byte) 0x10
				};
		
		try {
			response = this.currentIsoDep
					.transceive(ENCRYPT);
		} catch (IOException e) {
			Log.e("NFC-CryptoTag", "NFC: IOException caught during transceive(): "
					+ e.getMessage());
			return null;
		}
		Log.d("NFC-CryptoTag", "Encrypt sent");

		return response;
	}
	
	public byte[] decrypt(byte[] ciphertext){
		byte[] response = new byte[16];
		
		byte[] DECRYPT = {
				(byte) 0x00,
				// CLA
				(byte) 0x02, // INS Instruction
				(byte) 0x00, // P1 Parameter 1
				(byte) 0x00, // P2 Parameter 2
				(byte) 0x10, // Length
				(byte) 0xe1,(byte) 0xe6,(byte) 0x92, (byte)0x33, (byte)0x18, (byte) 0xbd, (byte)0xc4, (byte)0x2e, (byte)0x09, (byte)0x51, (byte)0x66,(byte) 0x1f, (byte)0xbd,(byte) 0x96,(byte) 0x8a,(byte) 0x88,
				(byte) 0x10
				};
		
		try {
			response = this.currentIsoDep
					.transceive(DECRYPT);
		} catch (IOException e) {
			Log.e("NFC-CryptoTag", "NFC: IOException caught during transceive(): "
					+ e.getMessage());
			return null;
		}
		Log.d("NFC-CryptoTag", "Decrypt sent");

		return response;
	}
	
	@Override
	public byte[] sign(byte[] data){
		byte[] response = new byte[256];
		
		byte[] SIGN = {
				(byte) 0x00,
				// CLA
				(byte) 0x05, // INS Instruction
				(byte) 0x00, // P1 Parameter 1
				(byte) 0x00, // P2 Parameter 2
				(byte) 0x10, // Length
				(byte) 1,1,1,1,2,2,2,2,3,3,3,3,4,4,4,4,
				(byte) 0x10
				};
		
		try {
			response = this.currentIsoDep
					.transceive(SIGN);
		} catch (IOException e) {
			Log.e("NFC-CryptoTag", "NFC: IOException caught during transceive(): "
					+ e.getMessage());
			return null;
		}
		Log.d("NFC-CryptoTag", "SIGN sent");

		return response;
	}

	@Override
	public ESDHPublicKey getPublicKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] generateSharedAESKey(ESDHPublicKey otherPublicKey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] getUid() {
		// TODO Auto-generated method stub
		return null;
	}

	
}