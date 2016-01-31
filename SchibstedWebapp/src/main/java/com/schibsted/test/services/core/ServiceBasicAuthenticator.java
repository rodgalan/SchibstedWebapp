package com.schibsted.test.services.core;

import java.util.function.Predicate;

import com.schibsted.test.webapp.core.exceptions.DAOException;
import com.schibsted.test.webapp.core.security.SecurityHelper;
import com.schibsted.test.webapp.dao.UserDAO;
import com.schibsted.test.webapp.model.User;
import com.sun.net.httpserver.BasicAuthenticator;



/**
 * Basic http authenticator.
 * TODO: Must implement authorization  
 * 
 * @author Anna
 *
 */
@SuppressWarnings("restriction")
public class ServiceBasicAuthenticator extends BasicAuthenticator{
	public static final String SERVICE_AUTH="SERVICE_AUTH";
	private String httpMethod=null;

	public ServiceBasicAuthenticator() {
		super(SERVICE_AUTH);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean checkCredentials(String username, String password){
		String hashpassword=SecurityHelper.getPasswordHash(password);
		UserDAO<User> dao=new UserDAO<User>();
		Predicate<User> getUserCondition=(usu -> usu.getUsername().equals(username) && usu.getPasssword().equals(hashpassword));
		User userLogged=null;
		try {
			userLogged = dao.findItemByCondition(getUserCondition);
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return userLogged!=null;
		
	}

	/*@Override
	public Result authenticate(HttpExchange exchange) {
		//TODO Needs to implement here the authorization getting user:password in base64 from header. To authenticate invoke checkCredentials.  
		 
	}*/
	
	
	
	
	
	

}
