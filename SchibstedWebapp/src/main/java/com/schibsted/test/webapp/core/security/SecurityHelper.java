package com.schibsted.test.webapp.core.security;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class SecurityHelper {
	
	
	/**
	 * Calculates SHA1 (hex representation) for sessionId
	 * @return
	 */
	public static String generateNewSessionId(){
	      String sessionId=null;
		try {
			SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
			String randomNum = new Integer(secureRandom.nextInt()).toString();
			MessageDigest sha = MessageDigest.getInstance("SHA-1");
			byte[] result =  sha.digest(randomNum.getBytes());
			sessionId=javax.xml.bind.DatatypeConverter.printHexBinary(result);
		    System.out.println("Generating new session: "+sessionId);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return sessionId;
	}
	
	/**
	 * Generates md5 hash for password representation
	 * @param password
	 * @return
	 */
	public static String getPasswordHash(String password){
		String hash=null;
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(password.getBytes(), 0, password.length());
			hash = new BigInteger(1, digest.digest()).toString(16);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hash;
	}
	
	

}
