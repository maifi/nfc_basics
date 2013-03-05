package com.nfc_start;

import java.io.UnsupportedEncodingException;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("NFC", "onCreate");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    protected void onResume() {
        super.onResume();

        Log.d("NFC", "onResume");
        Intent intent = getIntent();
        
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs = null;
            if (rawMsgs != null) {
            	msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            }
            
            for (int i = 0; i < rawMsgs.length; i++) {
            	byte[] data = msgs[i].toByteArray();
            	String arr = null;
            	try {
					arr = new String(data,"UTF8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                Log.d("NFC-data",arr);
            }
            
            
            //get tag technologies
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String[] techlist = tag.getTechList();
            for(int i=0; i<techlist.length;i++)
            	Log.d("NFC-techlist",techlist[i].toString());
 
        }
        

        
    }
    
}
