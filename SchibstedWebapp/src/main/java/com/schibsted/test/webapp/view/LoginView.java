package com.schibsted.test.webapp.view;

import com.schibsted.test.webapp.core.controller.IViewLayer;
import com.schibsted.test.webapp.core.controller.ViewBean;
import com.schibsted.test.webapp.viewBean.LoginPageBean;
import com.schibsted.test.webapp.viewBean.UserInfoBean;

public class LoginView implements IViewLayer{
	
	private static final String htmlContent="<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><title>Login</title></head><body><div id=\"TitleDiv\" align=\"center\">LOGIN </div><div id=\"loginDiv\">#message# <br></div><div id=\"loginDiv\">     <form id=\"loginForm\" method=\"post\" action=\"validateUser\"> <label for=\"username\">Username</label> <input type=\"text\" name=\"username\"> <label for=\"password\">Password</label> <input type=\"password\" name=\"password\"><input type=\"submit\" name=\"submit\" id=\"submit\" value=\"Login\" /></form></div><div id=\"links\"><a href=\"page1\">page1</a><a href=\"page2\">page2</a><a href=\"page3\">page3</a></div></body></html>";
	private static final String messageTag="#message#"; 

	@Override
	public String renderView(ViewBean viewBean) {
		String message="";
		if(viewBean!=null){
			LoginPageBean loginBean=(LoginPageBean)viewBean;
			if(loginBean.getMessage()!=null){
				message=loginBean.getMessage();
			}
		}
		return htmlContent.replaceAll(messageTag, message);
	}
}
