package com.schibsted.test.webapp.core.controller;

public class LoginSessionBean extends ViewBean{
	
	private String sessionId;

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public LoginSessionBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	public LoginSessionBean(String sessionId) {
		super();
		this.sessionId = sessionId;
	}
	
	
	
	

}
