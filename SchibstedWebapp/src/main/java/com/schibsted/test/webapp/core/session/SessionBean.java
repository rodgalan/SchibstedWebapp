package com.schibsted.test.webapp.core.session;

public class SessionBean {
	private Integer userId=null;
	private String sessionId=null;
	private long lastRequestTimestamp;
	
	public SessionBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	

	public SessionBean(Integer userId, String sessionId, long lastRequestTimestamp) {
		super();
		this.userId = userId;
		this.sessionId = sessionId;
		this.lastRequestTimestamp = lastRequestTimestamp;
	}
	

	



	public long getLastRequestTimestamp() {
		return lastRequestTimestamp;
	}



	public void setLastRequestTimestamp(long lastRequestTimestamp) {
		this.lastRequestTimestamp = lastRequestTimestamp;
	}



	

	public Integer getUserId() {
		return userId;
	}



	public void setUserId(Integer userId) {
		this.userId = userId;
	}



	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
}
