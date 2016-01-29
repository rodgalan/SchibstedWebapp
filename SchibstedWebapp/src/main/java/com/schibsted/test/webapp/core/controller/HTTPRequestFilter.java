package com.schibsted.test.webapp.core.controller;

import java.io.IOException;
import java.net.URI;

import com.schibsted.test.webapp.controller.flowconfig.beans.BusinessAction;
import com.schibsted.test.webapp.core.session.UserSessionManager;
import com.schibsted.test.webapp.core.session.UserSessionStorage;
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
		// TODO Auto-generated method stub
		System.out.println("REQUEST FILTER");
		
		//contentTypeValidation(exchange);
		
		URI requestURI = exchange.getRequestURI();
		FlowConfiguration fc = FlowConfiguration.getInstance();
		BusinessAction actionBean = fc.getBusinessActionFromURI(requestURI);
		
		//Validate user authentication
		if (actionBean.isAuthenticated()) {
			System.out.println("valida sesion");
			if(!cookieSessionValidation(exchange)){
				//TODO pendent headers i tal
				HelperController.sendStaticView(exchange,"/loginNOK.html");
			}
			
			//Validate user autorization
			if(actionBean.getAccesRoles()!=null){
				//validar si user.rol eta a llista acces rol (son 2 llistes!)
				
			}
			
		}
		
		
		
		chain.doFilter(exchange);
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
}
