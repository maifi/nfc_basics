package com.nfc_start;

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
import android.view.View;
import android.widget.TextView;

public class ActYubiKey extends Activity {

	private TextView tf_yubi;
	private YubiKey _yubikey;
	
	private NfcAdapter mAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_yubi_key);
		
		tf_yubi = (TextView) findViewById(R.id.tf_yubi);
		
		 mAdapter = NfcAdapter.getDefaultAdapter(this);
	        if(mAdapter == null)
	        	return;
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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.yubi_key, menu);
		return true;
	}
	
	public void selectApplet(View view){
		byte[] response;
		if(_yubikey != null){
			response = _yubikey.SelectApp();
			tf_yubi.setText(Utils.byteArrayToHexString(response));
		}
	}
	
	public void yubiEncrypt(View view){
		byte[] response;
		if(_yubikey != null){
			response = _yubikey.encrypt(null);
			
			tf_yubi.setText(Utils.byteArrayToHexString(response));
			Log.d("nfc", Utils.byteArrayToHexString(response));
		}
	}
	
	public void yubiDecrypt(View view){
		byte[] response;
		if(_yubikey != null){
			String test = tf_yubi.getText().toString();
			Log.d("nfcdec",test);
			byte[] cipher = tf_yubi.getText().toString().getBytes();
			byte[] ci = new byte[16];
			for(int i= 0; i<16; i++){
				ci[i] = cipher[i];
				System.out.println(ci[i]);
			}
			response = _yubikey.decrypt(ci);
			tf_yubi.setText(Utils.byteArrayToHexString(response));
		}
	}
	
    @Override
    public void onNewIntent(Intent intent){
        // fetch the tag from the intent
        Tag tag = (Tag)intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        android.util.Log.v("NFC", "Discovered tag ["+tag+"] with intent: " + intent);
        android.util.Log.v("NFC", "{"+tag+"}");
        
        try {
        	_yubikey = new YubiKey(tag);
			//_yubikey = new YubiKey(tag);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("NFC", "Creating Crypto Tag failed");
		}
        
    }
    
    @Override
    public void onResume()
    {
        super.onResume();
        if(mAdapter != null)
        	mAdapter.enableForegroundDispatch(this, pendingIntent, mFilters, mTechLists);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if(mAdapter != null)
        	mAdapter.disableForegroundDispatch(this);
    }

}
