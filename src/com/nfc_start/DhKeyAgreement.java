package com.nfc_start;

import iaik.security.cipher.SecretKey;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class DhKeyAgreement extends Activity {
	private TextView tf_Dh, tf_cipher, tf_decrypted,tf_publickeytag, tf_plaintext;
	private VirtualTag vtag;
	private SimReceiver simRec;
	private byte[] cipherText = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dh_key_agreement);
		
		tf_Dh = (TextView) findViewById(R.id.tf_keyagreement);
		tf_cipher = (TextView) findViewById(R.id.tf_cipher);
		tf_decrypted = (TextView) findViewById(R.id.tf_decryptet_text);
		tf_publickeytag = (TextView) findViewById(R.id.tf_publickeytag);
		tf_plaintext = (TextView) findViewById(R.id.tf_plaintext);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_dh_key_agreement, menu);
		return true;
	}
	
	public void decrypt(View view){
		
		//read ciphertext
		byte[] cipherRead = FileIO.readByteArrayFromFile("cipher.txt",this.getApplicationContext());
		
		tf_Dh.setText(tf_Dh.getText() + "Sending cipher + tags pub key to rec...\n");
	      
	    //resolve key on receiver side
	    byte[] resolvedAESKey = simRec.resolveSharedAESKey(vtag.getPublicKey());
	    String sc = Utils.byteArrayToHexString(resolvedAESKey);
	    tf_Dh.setText(tf_Dh.getText() + "Receiver Resolved Secret:\n");
	    tf_Dh.setText(tf_Dh.getText() + sc + "\n");
	    tf_Dh.setText(tf_Dh.getText() + "Receiver Start Decoding Plaintext\n");
        //encrypt and check
	    byte[] receivedBytes = simRec.decrypt(cipherRead,resolvedAESKey);
	    String res = new String(receivedBytes);
	    Log.d("AES Received: ",res);
	    tf_Dh.setText(tf_Dh.getText() + "Received Decoded Plaintext: "+res);
	    tf_decrypted.setText(res);
	}
	
	
	 public void startDHAgreement(View view){
	    	vtag = new VirtualTag();
	    	simRec = new SimReceiver();
		    
		    //generating shared secret
		    byte[] sharedAESKey = vtag.generateSharedAESKey(simRec.getPublicKey());
		    tf_Dh.setText("");
		    tf_Dh.setText("Shared Secret created on Tag\n");
		    String sc = Utils.byteArrayToHexString(sharedAESKey);
		    tf_Dh.setText(tf_Dh.getText() + sc + "\n");
		    //encrypting with AES
		    Cipher cipher = null;
			try {
				cipher = Cipher.getInstance("Rijndael", "IAIK");
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

			tf_Dh.setText(tf_Dh.getText() + "Sender Start Encoding Plaintext...\n");
		    SecretKey sk = new SecretKey(sharedAESKey, "AES");

		    try {
				cipher.init(Cipher.ENCRYPT_MODE, sk);
			} catch (InvalidKeyException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		    byte[] dataToEncrypt = tf_plaintext.getText().toString().getBytes();
		    
		    try {
		    	cipherText = cipher.doFinal(dataToEncrypt);
			} catch (IllegalBlockSizeException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (BadPaddingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		    tf_Dh.setText(tf_Dh.getText() + "Sender Done Encoding Plaintext\n");
		    
		    FileIO.writeByteArrayToFile("cipher.txt", cipherText, this.getApplicationContext());

		    tf_cipher.setText(Utils.byteArrayToHexString(cipherText));
		    tf_publickeytag.setText(Utils.byteArrayToHexString(vtag.getPublicKey().encode()));
	    }

}
