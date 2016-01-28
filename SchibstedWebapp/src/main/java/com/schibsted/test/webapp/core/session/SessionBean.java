package com.schibsted.test.webapp.core.session;

import java.util.Date;

import com.schibsted.test.webapp.model.User;

public class SessionBean {
	private Integer userId=null;
	private String sessionId=null;
	private Date lastRequestTimestamp=null;
	
	public SessionBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	

	public SessionBean(Integer userId, String sessionId, Date lastRequestTimestamp) {
		super();
		this.userId = userId;
		this.sessionId = sessionId;
		this.lastRequestTimestamp = lastRequestTimestamp;
	}
	

	public Date getLastRequestTimestamp() {
		return lastRequestTimestamp;
	}



	public void setLastRequestTimestamp(Date lastRequestTimestamp) {
		this.lastRequestTimestamp = lastRequestTimestamp;
	}



	public Integer getUserId() {
		return userId;
	}

	public void setUser(Integer userId) {
		this.userId = userId;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
}
