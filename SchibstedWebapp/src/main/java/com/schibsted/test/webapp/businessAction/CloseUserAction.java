package com.schibsted.test.webapp.businessAction;

import java.util.List;
import java.util.Map;

import com.schibsted.test.webapp.core.controller.IBusinessActionLayer;
import com.schibsted.test.webapp.core.controller.ViewBean;
import com.schibsted.test.webapp.core.session.UserSessionManager;
import com.schibsted.test.webapp.core.session.UserSessionStorage;


/**
 * Loggoff user session
 * Deletes user session in server side (not the cookie in client side!)
 * 
 * @author Anna
 *
 */
public class CloseUserAction implements IBusinessActionLayer{
	
	private static final String FORWARD="success";
	
	
	@Override
	public ViewBean doProcessing(Map<String, List<String>> requestParameters, String sessionId) {
		
		ViewBean viewBean=new ViewBean();
		viewBean.setForwardName(FORWARD);
		UserSessionManager.deleteSession(sessionId, UserSessionStorage.getInstance());
		
		return viewBean;
	}
	
}
