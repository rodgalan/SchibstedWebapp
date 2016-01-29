package com.schibsted.test.webapp.core.session;

import java.util.Date;
import java.util.HashMap;

import com.schibsted.test.webapp.core.security.SecurityHelper;


public class UserSessionManager {
	private static final long ONE_MINUTE_IN_MILLIS=60000; 
	private static final long USER_SESSION_ALIVE_MINUTES=1; 
	
	public static Integer getUserIdBySessionId(String sessionId, UserSessionStorage sessionStorage){
		return sessionStorage.getActiveSessions().get(sessionId).getUserId();
	}
	
	
	/**
	 * Validates user session: sessionId exists && is not expired
	 * If user session is ok, updates user session alive for 5 minutes more.
	 * 
	 * @param sessionId
	 * @param sessionStorage
	 * @return
	 */
	public static boolean validateSession(String sessionId, UserSessionStorage sessionStorage){
		boolean sessionOk=false;
		HashMap<String, SessionBean> activeSessions=sessionStorage.getActiveSessions();
		if(activeSessions.get(sessionId)!=null && validateSessionNotExpired(activeSessions.get(sessionId))){
			updateUserSessionTimestamp(activeSessions.get(sessionId));
			sessionOk=true;
		}
		return sessionOk;
	}
	
	
	/**
	 * Creates new userSession.
	 * Not validates username and password, it's delegated to login action (perimeter security).
	 * Ensure username and password are validated before invoke createNewSession!!! 
	 * 
	 * @param userId
	 * @param sessionStorage
	 * @return
	 */
	public static String createNewSession(int userId,UserSessionStorage sessionStorage){
		String sessionId=SecurityHelper.generateNewSessionId();
		SessionBean newSession=new SessionBean(userId,sessionId,new Date().getTime());
		sessionStorage.getActiveSessions().put(sessionId, newSession);
		return sessionId;
	}
	
	
	/**
	 * Delete userSession
	 * @param userId
	 * @param sessionStorage
	 * @return
	 */
	public static boolean deleteSession(String sessionId, UserSessionStorage sessionStorage){
		HashMap<String, SessionBean> activeSessions=sessionStorage.getActiveSessions();
		return activeSessions.remove(sessionId)!=null;
	}
	

	private static boolean validateSessionNotExpired(SessionBean sessionBean){
		
		long lastUserEvent=sessionBean.getLastRequestTimestamp();
		long expirationTimeFromLasUserevent=lastUserEvent+ USER_SESSION_ALIVE_MINUTES * ONE_MINUTE_IN_MILLIS;
		long now=new Date().getTime();
		
		return now<expirationTimeFromLasUserevent;
		
		/*long expirationTime=sessionBean.getLastRequestTimestamp();
		Date afterAddingTenMins=new Date();
		afterAddingTenMins.setTime(afterAddingTenMins.getTime()+ USER_SESSION_ALIVE_MINUTES * ONE_MINUTE_IN_MILLIS);
		return lastUserEvent.compareTo(afterAddingTenMins)<0;*/
		
	}
	
	private static void updateUserSessionTimestamp(SessionBean sessionBean){
		long newTimestamp=new Date().getTime()+USER_SESSION_ALIVE_MINUTES * ONE_MINUTE_IN_MILLIS;
		sessionBean.setLastRequestTimestamp(newTimestamp);
	}
		
	
	/*private static String generateNewSessionId(){
	      SecureRandom secureRandom=null;
	      String sessionId=null;
		try {
			secureRandom = SecureRandom.getInstance("SHA1PRNG");
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
	}*/
	

}
