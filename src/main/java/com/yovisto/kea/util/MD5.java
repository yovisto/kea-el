package com.yovisto.kea.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.google.inject.Singleton;

/**
 * The Class MD5.
 *
 * @version $Id: MD5.java 979 2012-02-16 14:58:23Z joerg $
 */
@Singleton
public class MD5 {   
	
	/** The instance. */
	private static MD5 instance = null;
	
	/**
	 * Gets the single instance of MD5.
	 *
	 * @return single instance of MD5
	 */
	public static MD5 getInstance(){
		if (instance == null)
			instance = new MD5();				
		return instance;
	}
	
   /**
    * Converts input string MD5 encrypted string.
    *
    * @param input the input
    * @return the encrypted string
    */
    public String toMD5(String input){
	    MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {			
			e.printStackTrace();
		}
	    md5.reset();
	    md5.update(input.getBytes());
	    byte[] result = md5.digest();

	    StringBuffer hexString = new StringBuffer();
	    for (int i=0; i<result.length; i++) {
	    	
	    	String hex = Integer.toHexString(0xFF & result[i]);
	    	if(hex.length()==1) hexString.append('0');
	        hexString.append(hex);
	    }
	    return hexString.toString();
   }
}
