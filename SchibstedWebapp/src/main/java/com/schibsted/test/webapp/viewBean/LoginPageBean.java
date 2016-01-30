package com.schibsted.test.webapp.viewBean;

import com.schibsted.test.webapp.core.controller.ViewBean;

public class LoginPageBean extends ViewBean{

	private String originalRequest=null; 
	private String logedUsername=null; 
	
	public LoginPageBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getOriginalRequest() {
		return originalRequest;
	}

	public void setOriginalRequest(String originalRequest) {
		this.originalRequest = originalRequest;
	}

	public String getLogedUsername() {
		return logedUsername;
	}

	public void setLogedUsername(String logedUsername) {
		this.logedUsername = logedUsername;
	}
	
	
	
	
	
	

}
