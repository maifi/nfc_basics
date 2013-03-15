package com.nfc_start;

import iaik.security.cipher.SecretKey;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import android.app.ActionBar;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private NfcAdapter mAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;
    
    private IaikCryptaTag crypta_;
    
    
    //UI
    private TextView tf_Uid;
    private TextView tf_Cert;
    private TextView tf_Challenge;
    private TextView tf_Dh;
    

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

	    iaik.security.provider.IAIK iaik = new iaik.security.provider.IAIK();
	    iaik.addAsProvider(true); 
	    
        mAdapter = NfcAdapter.getDefaultAdapter(this);
        pendingIntent = PendingIntent.getActivity(
          this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

     // Setup an intent filter for all MIME based dispatches
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndef.addDataType("*/*");
        } catch (MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }
        IntentFilter td = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        mFilters = new IntentFilter[] {
                ndef, td
        };

        // Setup a tech list for all NfcF tags
        mTechLists = new String[][] { new String[] { 
                NfcV.class.getName(),
                NfcF.class.getName(),
                NfcA.class.getName(),
                NfcB.class.getName()
            } };
        
        initUI();
        
    }

    private void initUI(){
    	tf_Uid = (TextView) findViewById(R.id.tf_uid);
    	tf_Cert = (TextView) findViewById(R.id.tf_cert);
    	tf_Challenge = (TextView) findViewById(R.id.tf_Challenge);
    	tf_Dh = (TextView) findViewById(R.id.tf_dh);

    }
    
    public void getUidPressed(View view) {
        if(crypta_ == null)
        	return;
        
    	byte[] uid = crypta_.getUid();
        
        if(uid == null){
        	Log.e("NFC", "No UID from Crypta");
        	tf_Uid.setText("");
        }
        else{
        	String hexUid = Utils.byteArrayToHexString(uid);
        	Log.d("NFC", hexUid);
        	tf_Uid.setText(hexUid);
        }
    }
    
    public void getCertPressed(View view){
        if(crypta_ == null)
        	return;
        
    	byte[] cert = crypta_.getCert();
        
        if(cert == null){
        	Log.e("NFC", "No Cert from Crypta");
        	tf_Cert.setText("");
        }
        else{
        	String hexCert = Utils.byteArrayToHexString(cert);
        	Log.d("NFC", hexCert);
        	tf_Cert.setText(hexCert);
        }
    }
    
    public void getChallengePressed(View view){
        if(crypta_ == null)
        	return;
        Log.d("NFC", "in getchallenge");
    	byte[] challenge = crypta_.getChallenge();
        
        if(challenge == null){
        	Log.e("NFC", "No Challenge from Crypta");
        	tf_Challenge.setText("");
        }
        else{
        	String hexCert = Utils.byteArrayToHexString(challenge);
        	Log.d("NFC", hexCert);
        	tf_Challenge.setText(hexCert);
        }
    }
    @Override
    public void onResume()
    {
        super.onResume();
        mAdapter.enableForegroundDispatch(this, pendingIntent, mFilters, mTechLists);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        mAdapter.disableForegroundDispatch(this);
    }

    @Override
    public void onNewIntent(Intent intent){
        // fetch the tag from the intent
        Tag tag = (Tag)intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        android.util.Log.v("NFC", "Discovered tag ["+tag+"] with intent: " + intent);
        android.util.Log.v("NFC", "{"+tag+"}");
        
        try {
			crypta_ = new IaikCryptaTag(tag);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("NFC", "Creating Crypto Tag failed");
		}
        
    }
    
    public void startDHAgreement(View view){
    	VirtualTag vtag = new VirtualTag();
	    SimReceiver simRec = new SimReceiver();
	    
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
	    byte[] dataToEncrypt = "Hello u!".getBytes();
	    
	    byte[] cipherText = null;
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
	    tf_Dh.setText(tf_Dh.getText() + "Sending cipher + tags pub key to rec...\n");
	       
	    //resolve key on receiver side
	    byte[] resolvedAESKey = simRec.resolveSharedAESKey(vtag.getPublicKey());
	    sc = Utils.byteArrayToHexString(resolvedAESKey);
	    tf_Dh.setText(tf_Dh.getText() + "Receiver Resolved Secret:\n");
	    tf_Dh.setText(tf_Dh.getText() + sc + "\n");
	    tf_Dh.setText(tf_Dh.getText() + "Receiver Start Decoding Plaintext\n");
        //encrypt and check
	    byte[] receivedBytes = simRec.decrypt(cipherText,resolvedAESKey);
	    String res = new String(receivedBytes);
	    Log.d("AES Received: ",res);
	    tf_Dh.setText(tf_Dh.getText() + "Received Decoded Plaintext: "+res);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.activity_main, menu);
      return true;
    } 
    
    @Override
	  public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.menuitem1:
	      Toast.makeText(this, "Menu Item 1 selected", Toast.LENGTH_SHORT)
	          .show();
	      break;
	    case R.id.menuitem2:
	      Toast.makeText(this, "Menu item 2 selected", Toast.LENGTH_SHORT)
	          .show();
	      break;

	    default:
	      break;
	    }

	    return true;
	  } 
    
}
