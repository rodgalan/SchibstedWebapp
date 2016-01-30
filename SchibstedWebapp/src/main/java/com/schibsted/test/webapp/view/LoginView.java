package com.schibsted.test.webapp.view;

import com.schibsted.test.webapp.core.controller.IViewLayer;
import com.schibsted.test.webapp.core.controller.ViewBean;
import com.schibsted.test.webapp.viewBean.LoginPageBean;

public class LoginView implements IViewLayer{
	
	private static final String htmlContent="<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><title>Login</title><script type=\"text/javascript\">function validate(){ if(document.getElementById('password').value =='' || document.getElementById('username').value=='' ){ alert('Username and password are both mandatory'); return false;}}</script></head><body><div id=\"TitleDiv\" align=\"center\"><h1>LOGIN</h1></div><div id=\"messageDiv\"><font color=\"red\">#message# </font><br></div><div id=\"loginDiv\"><form id=\"loginForm\" method=\"post\" action=\"validateUser\" onsubmit=\"return validate();\"><input type=\"hidden\" id=\"goesto\" name=\"goesto\" value=\"#goesto#\" /><labelfor=\"username\">Username</label> <input type=\"text\" id=\"username\" name=\"username\" /><label for=\"password\">Password</label> <input type=\"password\"name=\"password\" id=\"password\" /><input type=\"submit\" name=\"submit\" id=\"submit\"value=\"Login\" /></form></div><div id=\"links\"><a href=\"page1\">page1</a> -- <a href=\"page2\">page2</a> -- <a href=\"page3\">page3</a> --</div></body></html>";
	private static final String messageTag="#message#"; 
	private static final String goestoTag="#goesto#";

	@Override
	public String renderView(ViewBean viewBean) {
		LoginPageBean loginBean=(LoginPageBean)viewBean;
		String message="";
		String goesto="";
		String username="";
		if(loginBean!=null){
			if(loginBean!=null){
				if(loginBean.getLogedUsername()!=null && !loginBean.getLogedUsername().isEmpty()){
					message="USERNAME : "+loginBean.getLogedUsername();
				}else{
					message="USER NOT LOGGED";
				}
			}
			
			/*if(loginBean.getMessage()!=null && !loginBean.getMessage().isEmpty()){
				message=loginBean.getMessage();
			}*/
			if(loginBean.getOriginalRequest()!=null && !loginBean.getOriginalRequest().isEmpty()){
				goesto=loginBean.getOriginalRequest();
			}
		}
		return htmlContent.replaceAll(messageTag, message).replace(goestoTag, goesto);
	}
}
