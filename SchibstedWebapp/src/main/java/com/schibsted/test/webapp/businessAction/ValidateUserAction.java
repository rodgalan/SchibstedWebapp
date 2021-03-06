package com.schibsted.test.webapp.businessAction;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import com.schibsted.test.webapp.core.controller.IBusinessActionLayer;
import com.schibsted.test.webapp.core.controller.LoginSessionBean;
import com.schibsted.test.webapp.core.controller.ViewBean;
import com.schibsted.test.webapp.core.exceptions.DAOException;
import com.schibsted.test.webapp.core.security.SecurityHelper;
import com.schibsted.test.webapp.core.session.UserSessionManager;
import com.schibsted.test.webapp.core.session.UserSessionStorage;
import com.schibsted.test.webapp.dao.UserDAO;
import com.schibsted.test.webapp.model.User;

public class ValidateUserAction implements IBusinessActionLayer{
	
	public static final String FORWARD_SUCCESS="success";
	public static final String FORWARD_NOT_LOGGED="loginError";
	
	
	@Override
	public ViewBean doProcessing(Map<String, List<String>> requestParameters, String sessionId) {
		
		LoginSessionBean sessionBean=new LoginSessionBean();
		sessionBean.setForwardName(FORWARD_NOT_LOGGED);
		
		List<String> usernameList=requestParameters.get("username");
		List<String> passwordList=requestParameters.get("password");
		List<String> redirectTo=requestParameters.get("goesto");
		
		//1. Getting parameters
		if(usernameList!=null && usernameList.size()==1 && passwordList!=null && passwordList.size()==1 && usernameList.get(0)!=null && !usernameList.get(0).isEmpty() && passwordList.get(0)!=null && !passwordList.get(0).isEmpty()){
			String username=usernameList.get(0);
			String password=passwordList.get(0);
			
			
			//Get password hash
			String hashpassword=SecurityHelper.getPasswordHash(password);
			
			try {
				//Validates user exists
				UserDAO<User> dao=new UserDAO<User>();
				Predicate<User> getUserCondition=(usu -> usu.getUsername().equals(username) && usu.getPasssword().equals(hashpassword));
				User userLogged = dao.findItemByCondition(getUserCondition);
				if(userLogged!=null){
					//Creates new session (now is correct for server, nedded say it to user)
					String session=UserSessionManager.createNewSession(userLogged.getUserId(),UserSessionStorage.getInstance());
					sessionBean = new LoginSessionBean(session);
					sessionBean.setForwardName(FORWARD_SUCCESS);
					
					if(redirectTo!=null && redirectTo.size()==1 && redirectTo.get(0)!=null && !redirectTo.get(0).isEmpty()){
						sessionBean.setRedirectLocation(redirectTo.get(0));
					}
				}
			} catch (DAOException e) {
				sessionBean.setForwardName("error");
				// TODO: Global Error is needed to handle this errors!
			}
		}
		
		return sessionBean;
	}
	
	
	
	
	
}
