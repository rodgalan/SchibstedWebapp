package com.schibsted.test.webapp.businessAction;

import java.util.List;
import java.util.Map;

import com.schibsted.test.webapp.core.controller.IBusinessActionLayer;
import com.schibsted.test.webapp.core.controller.ViewBean;
import com.schibsted.test.webapp.core.exceptions.DAOException;
import com.schibsted.test.webapp.core.session.UserSessionManager;
import com.schibsted.test.webapp.core.session.UserSessionStorage;
import com.schibsted.test.webapp.dao.UserDAO;
import com.schibsted.test.webapp.model.User;
import com.schibsted.test.webapp.viewBean.LoginPageBean;

public class LoginAction implements IBusinessActionLayer{
	
	protected final static String FORWARD="success";
	@Override
	public ViewBean doProcessing(Map<String, List<String>> requestParameters, String sessionId) {
		System.out.println("New LoginAction, pantalleta");
		
		LoginPageBean result=new LoginPageBean();
		if(sessionId!=null && isUserLogged(sessionId)){
			User userLogged=getUserLogged(sessionId);
			if(userLogged.getUsername()!=null && !userLogged.getUsername().isEmpty() ){
				result.setLogedUsername(userLogged.getUsername());
			}
		}
		
		/*if(requestParameters!=null && !requestParameters.isEmpty()){
			List<String> messageList=requestParameters.get("message");
			if(messageList!=null && messageList.size()==1 ){
				String message=messageList.get(0);
				result.setMessage(message);
			}
		}*/
		result.setForwardName(FORWARD);
		return result;
	}
	
	private boolean isUserLogged(String sessionId) {
		boolean userLogged = false;
		UserSessionStorage sessionStorage = UserSessionStorage.getInstance();
		userLogged = UserSessionManager.validateSession(sessionId, sessionStorage);
		return userLogged;
	}
	
	private User getUserLogged(String sessionId) {
		User user=null;
		UserSessionStorage sessionStorage = UserSessionStorage.getInstance();
		if(UserSessionManager.validateSession(sessionId, sessionStorage)){
			UserDAO<User> dao=new UserDAO<User>();
			int userId=UserSessionManager.getUserIdBySessionId(sessionId, sessionStorage);
			try {
				user =dao.getById(userId);
			} catch (DAOException e) {
				//DEIXA PASSAR PER A QUE EL CONTROLLER CAPTURI I ENVII ERROR 500
			}
		}
		return user;
	}

}
