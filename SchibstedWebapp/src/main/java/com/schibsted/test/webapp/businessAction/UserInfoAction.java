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
import com.schibsted.test.webapp.viewBean.UserInfoBean;

public class UserInfoAction implements IBusinessActionLayer{


	@Override
	public ViewBean doProcessing(Map<String, List<String>> parameters, String sessionId) {
		UserInfoBean result=new UserInfoBean();
		if(sessionId!=null){
			Integer userId=UserSessionManager.getUserIdBySessionId(sessionId ,UserSessionStorage.getInstance());		
			try {
				UserDAO<User> dao=new UserDAO<User>();
				User user = dao.getById(userId);
				if(user!=null){
					result.setUsername(user.getUsername());
					result.setForwardName("success");
				}
			} catch (DAOException e) {
				//TODO: Global Error is needed to handle this errors!
				result.setForwardName("error");
			}
		}
		return result;
	}

}
