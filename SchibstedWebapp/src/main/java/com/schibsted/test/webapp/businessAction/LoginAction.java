package com.schibsted.test.webapp.businessAction;

import java.util.List;
import java.util.Map;

import com.schibsted.test.webapp.core.controller.IBusinessActionLayer;
import com.schibsted.test.webapp.core.controller.ViewBean;
import com.schibsted.test.webapp.viewBean.LoginPageBean;

public class LoginAction implements IBusinessActionLayer{
	
	protected final static String FORWARD="success";
	@Override
	public ViewBean doProcessing(Map<String, List<String>> requestParameters, String sessionId) {
		System.out.println("New LoginAction, pantalleta");
		LoginPageBean result=new LoginPageBean();
		if(requestParameters!=null && !requestParameters.isEmpty()){
			List<String> messageList=requestParameters.get("message");
			if(messageList!=null && messageList.size()==1 ){
				String message=messageList.get(0);
				result.setMessage(message);
			}
		}
		result.setForwardName(FORWARD);
		return result;
	}
	
	

}
