package com.schibsted.test.webapp.core.controller;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import com.schibsted.test.webapp.controller.flowconfig.beans.BusinessAction;
import com.schibsted.test.webapp.core.exceptions.DAOException;
import com.schibsted.test.webapp.core.session.UserSessionManager;
import com.schibsted.test.webapp.core.session.UserSessionStorage;
import com.schibsted.test.webapp.dao.UserDAO;
import com.schibsted.test.webapp.model.IRolTypes.Rol;
import com.schibsted.test.webapp.model.User;
import com.schibsted.test.webapp.viewBean.LoginPageBean;
import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;

@SuppressWarnings("restriction")
public class HTTPRequestFilter extends Filter {

	private static final String FILTER_DESC = "";

	@Override
	public String description() {
		return FILTER_DESC;
	}

	@Override
	public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
		boolean correctAcces=true;
		System.out.println("REQUEST FILTER");
		
		this.validateResource(exchange); 
		
		//Resource unknown
		
		
		//contentTypeValidation(exchange);
		
		URI requestURI = exchange.getRequestURI();
		FlowConfiguration fc = FlowConfiguration.getInstance();

		BusinessAction actionBean = fc.getBusinessActionFromURI(requestURI);
		
		//Validate user authentication
		if (actionBean.isAuthenticated()) {
			System.out.println("valida sesion");
			if(!cookieSessionValidation(exchange)){
				
				
				//HelperController.sendStaticView(exchange,"/loginNOK.html"); //este estaba
				//HelperController.sendStaticView(exchange,"/login");  //este no deberia ir
				
				LoginPageBean viewBean=new LoginPageBean();
				viewBean.setMessage("RESTRICTED ACCES TO "+requestURI.getPath()+". YOU MUST BE AUTHENTICATED. PLEASE, DO A LOGIN: ");
				viewBean.setOriginalRequest(HelperController.getPathFromURI(requestURI));
				try {
					HelperController.sendDynamicView(exchange, "com.schibsted.test.webapp.view.LoginView", viewBean);
				} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			
				correctAcces=false;
			}else{
			
				//VALIDATE USER AUTHORIZATION
				//if(FlowConfiguration.getInstance().isAuthorizatedURI(requestURI))
				if(actionBean.getAccesRole()!=null && !actionBean.getAccesRole().isEmpty()){
					//Getting user from sessionId
					String sessionId=HelperController.getSessionIdFromCookie(exchange);
					Integer userId=UserSessionManager.getUserIdBySessionId(sessionId, UserSessionStorage.getInstance());
					UserDAO<User> dao=new UserDAO<User>();
					try {
						User user = dao.getById(userId);
						List<Rol> userRoles=user.getRols();
						String a=userRoles.get(0).toString();
						
						Optional<Rol> optionalRol=userRoles.stream().filter(rol -> rol.toString().equals( actionBean.getAccesRole().toUpperCase())).findFirst();
						if(!optionalRol.isPresent()){
							System.out.println("USUARI NO AUTORIZAT!");
							HelperController.sendError(exchange, 405, "pues eso que no puedes acceder");
							correctAcces=false;
						}
					} catch (DAOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		if(correctAcces){
			chain.doFilter(exchange);
		}
	}

	//TODO: NOT WORKS
	private boolean contentTypeValidation(HttpExchange exchange){
		return exchange.getRequestHeaders().getFirst("Content-Type").trim().equals("text/html");
	}
	
	private boolean cookieSessionValidation(HttpExchange exchange) {
		boolean userLogged = false;
		String cookieStr = exchange.getRequestHeaders().getFirst("Cookie");
		if (cookieStr != null && cookieStr.contains("SchibstedWebappSessionId=")) {
			// TODO Revisar (la cookie pot portar mes parameters)
			String schibstedWebappSessionId = cookieStr.replaceFirst(HelperController.COOKIE_NAME+"=", "");
			System.out.println(
					"Tengo la cookie: " + cookieStr + ", schibstedWebappSessionId=" + schibstedWebappSessionId);

			UserSessionStorage sessionStorage = UserSessionStorage.getInstance();
			userLogged = UserSessionManager.validateSession(schibstedWebappSessionId, sessionStorage);
		}
		return userLogged;
	}
	
	private void validateResource(HttpExchange exchange) throws IOException{
		URI requestURI = exchange.getRequestURI();
		if(FlowConfiguration.getInstance().getActionNameFromURI(requestURI)==null){
			HelperController.sendError(exchange, 404, "404: PAGE NOT FOUND");
		}
		
		
	}
	
	
}
