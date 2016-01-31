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

public class LoginAction implements IBusinessActionLayer {

	protected final static String FORWARD = "success";

	@Override
	public ViewBean doProcessing(Map<String, List<String>> requestParameters, String sessionId) {
		LoginPageBean result = new LoginPageBean();
		if (sessionId != null && isUserLogged(sessionId)) {
			try {
				User userLogged = getUserLogged(sessionId);
				if (userLogged.getUsername() != null && !userLogged.getUsername().isEmpty()) {
					result.setLogedUsername(userLogged.getUsername());
				}
			} catch (DAOException e) {
				// TODO: Global Error is needed to handle this errors!
				result.setForwardName("error");
			}
		}

		result.setForwardName(FORWARD);
		return result;
	}

	private boolean isUserLogged(String sessionId) {
		boolean userLogged = false;
		UserSessionStorage sessionStorage = UserSessionStorage.getInstance();
		userLogged = UserSessionManager.validateSession(sessionId, sessionStorage);
		return userLogged;
	}

	private User getUserLogged(String sessionId) throws DAOException {
		User user = null;
		UserSessionStorage sessionStorage = UserSessionStorage.getInstance();
		if (UserSessionManager.validateSession(sessionId, sessionStorage)) {
			UserDAO<User> dao = new UserDAO<User>();
			int userId = UserSessionManager.getUserIdBySessionId(sessionId, sessionStorage);
			user = dao.getById(userId);
		}
		return user;
	}

}
