package com.schibsted.test.webapp.core.controller;


/**
 * Base bean to pass data values from business actions to dynamic views
 * 
 * NOT ABSTRACT CLASS: If business action not needs specific data, this generic ViewBean can be used
 *  
 * @author Anna
 *
 */
public class ViewBean {
	
	/**
	 * Forward Name defined in configuration.xml
	 */
	private String forwardName=null;
	
	/**
	 * Used to send a message (info/error/warning) to users. 
	 * TODO GETS BETTER: List<message> infoMessage / List<message> errorMessage 
	 */
	private String message=null;
	
	
	

	public ViewBean(String forwardName) {
		super();
		this.forwardName = forwardName;
	}

	public ViewBean() {
		super();
		// TODO Auto-generated constructor stub
	}

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
