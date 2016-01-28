package com.schibsted.test.webapp.core.session;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.schibsted.test.webapp.WebApp;
import com.schibsted.test.webapp.model.User;


/**
 * @author Anna
 * 
 *         Singleton pattern (Eager loading) implementation to storage UserSessionStorage in cache (no
 *         physical storage for this PoC). ONLY FOR ACADEMICAL PURPOSE!
 *         
 *         Session is persisted at client side with a session cookie. If user session was persisted at server side
 *         users session don't be lost in server reestart. 
 *          
 */
public class UserSessionStorage {
	private static final Logger log = Logger.getLogger(WebApp.class.getName());
	//Key --> sessionId (to ensure not duplicated sessionIds).Not order nedded.Always searching by sessionId.
	private static HashMap<String, SessionBean> activeSessions=null;

	// private static volatile UserSession instance = null; LAZY LOADING
	private static final UserSessionStorage instance = new UserSessionStorage();
	
	private UserSessionStorage() {
		log.log(Level.INFO, "Loading UserSession");
		if (instance != null) {
			log.log(Level.SEVERE, "Singleton UserSession already instantiated");
			throw new IllegalStateException("Singleton UserSession already instantiated");
		}
		activeSessions=new HashMap<String, SessionBean>();
	}
	
	public static void loadSessionStorage() {
		// Nothing to do: first static invocation loads the singleton instance
		// (eager)
	}

	
	public static UserSessionStorage getInstance() {
		log.log(Level.FINE, "Getting UserSession instance.");
		return instance;
	}

	public HashMap<String, SessionBean> getActiveSessions() {
		return activeSessions;
	}

}
