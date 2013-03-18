package com.nfc_start;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;

public final class FileIO {
	
	public static byte[] readByteArrayFromFile(String fn, Context ctx){
		byte[] data = null;
		try {
			FileInputStream fIn = ctx.openFileInput(fn);

			data = new byte[fIn.available()];
	        fIn.read(data);
			fIn.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}
	
	/**
	 * Returns true if ok
	 */
	public static boolean writeByteArrayToFile(String fn, byte[] data, Context ctx){
		 try {
		    	FileOutputStream fOut = ctx.openFileOutput(fn,0);

		    	fOut.write(data);
		    	fOut.flush();
		    	fOut.close();
		    	return true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return false;
	}


}
