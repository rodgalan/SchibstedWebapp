package com.schibsted.test.webapp.model;

import java.util.List;

import com.schibsted.test.webapp.model.IRolTypes.Rol;

/**
 * Model - User
 * 
 * POJO
 * 
 * @author Anna
 *
 */
public class User implements Cloneable{
	private Integer userId=null;
	private String username=null;
	private String passsword=null;
	private List<Rol> rols=null;
	
	
	
	public User(String username, String passsword, List<Rol> rols) {
		super();
		this.username = username;
		this.passsword = passsword;
		this.rols = rols;
	}
	
	
	
	public User() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	
	public int getUserId() {
		return userId;
	}



	public void setUserId(int userId) {
		this.userId = userId;
	}



	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPasssword() {
		return passsword;
	}
	public void setPasssword(String passsword) {
		this.passsword = passsword;
	}
	public List<Rol> getRols() {
		return rols;
	}
	public void setRols(List<Rol> rols) {
		this.rols = rols;
	}



	@Override
	public User clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return (User)super.clone();
	}
	
	

}
