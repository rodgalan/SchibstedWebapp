package com.schibsted.test.webapp.viewBean;

import java.util.List;

import com.schibsted.test.webapp.core.controller.ViewBean;
import com.schibsted.test.webapp.model.IRolTypes.Rol;

public class UserInfoBean extends ViewBean{
	
	private String username=null;
	private List<Rol> rols=null;

	public UserInfoBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UserInfoBean(String username) {
		super();
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<Rol> getRols() {
		return rols;
	}

	public void setRols(List<Rol> rols) {
		this.rols = rols;
	}
	
	

}
