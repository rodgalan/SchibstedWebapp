package com.schibsted.test.webapp.view;

import com.schibsted.test.webapp.core.controller.IViewLayer;
import com.schibsted.test.webapp.core.controller.ViewBean;
import com.schibsted.test.webapp.viewBean.LoginPageBean;

public class LoginView implements IViewLayer{
	
	private static final String htmlContent="<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><title>Login</title></head><body><div id=\"TitleDiv\" align=\"center\"><h1>LOGIN</h1></div><div id=\"messageDiv\"><font color=\"red\">#message# </font><br></div><div id=\"loginDiv\">     <form id=\"loginForm\" method=\"post\" action=\"validateUser\"> <input type=\"hidden\" name=\"goesto\" value=\"#goesto#\"/><label for=\"username\">Username</label> <input type=\"text\" name=\"username\"/> <label for=\"password\">Password</label> <input type=\"password\" name=\"password\"/><input type=\"submit\" name=\"submit\" id=\"submit\" value=\"Login\" /></form></div><div id=\"links\"><a href=\"page1\">page1</a> -- <a href=\"page2\">page2</a> -- <a href=\"page3\">page3</a> -- </div></body></html>";
	private static final String messageTag="#message#"; 
	private static final String goestoTag="#goesto#";

	@Override
	public String renderView(ViewBean viewBean) {
		LoginPageBean loginBean=(LoginPageBean)viewBean;
		String message="";
		String goesto="";
		if(loginBean!=null){
			if(loginBean.getMessage()!=null && !loginBean.getMessage().isEmpty()){
				message=loginBean.getMessage();
			}
			if(loginBean.getOriginalRequest()!=null && !loginBean.getOriginalRequest().isEmpty()){
				goesto=loginBean.getOriginalRequest();
			}
		}
		return htmlContent.replaceAll(messageTag, message).replace(goestoTag, goesto);
	}
}
