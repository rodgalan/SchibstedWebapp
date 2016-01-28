package com.schibsted.test.webapp.core.controller;

public abstract class ViewBean {
	
	/**
	 * Forward Name defined in configuration.xml
	 */
	private String forwardName=null;
	
	/**
	 * Used to send a message (info/error/warning) to users. 
	 * TODO GETS BETTER: List<message> infoMessage / List<message> errorMessage 
	 */
	private String message=null;

	public String getForwardName() {
		return forwardName;
	}

	public void setForwardName(String forwardName) {
		this.forwardName = forwardName;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
