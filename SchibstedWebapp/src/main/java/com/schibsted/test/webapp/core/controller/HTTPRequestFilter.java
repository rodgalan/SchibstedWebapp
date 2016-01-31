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

	
	//TODO IMPROVEMENT use one filter for each validations and chain between (better to add and remove validations :
	@Override
	public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
		boolean ok = false;

		
		HelperController.setResponseHeaders(exchange);
		if (this.validateResource(exchange)) {
			
			//TODO
			// if(contentTypeValidation(exchange));

			URI requestURI = exchange.getRequestURI();
			FlowConfiguration fc = FlowConfiguration.getInstance();
			BusinessAction actionBean = fc.getBusinessActionFromURI(requestURI);
			if (this.validateHTTPMethod(exchange, actionBean)) {
				if(this.validateUserAuthentication(actionBean,exchange)){
					ok=this.validateUserAuthorization(actionBean,exchange);
				}
			}
		}
		if(ok){
			chain.doFilter(exchange);
		}
	}

	// TODO: NOT WORKS
	private boolean contentTypeValidation(HttpExchange exchange) {
		return exchange.getRequestHeaders().getFirst("Content-Type").trim().equals("text/html");
	}

	private boolean cookieSessionValidation(HttpExchange exchange) {
		boolean userLogged = false;
		String cookieStr = exchange.getRequestHeaders().getFirst("Cookie");
		if (cookieStr != null && cookieStr.contains("SchibstedWebappSessionId=")) {
			// TODO Revisar (la cookie pot portar mes parameters)
			String schibstedWebappSessionId = cookieStr.replaceFirst(HelperController.COOKIE_NAME + "=", "");

			UserSessionStorage sessionStorage = UserSessionStorage.getInstance();
			userLogged = UserSessionManager.validateSession(schibstedWebappSessionId, sessionStorage);
		}
		return userLogged;
	}

	private boolean validateResource(HttpExchange exchange) throws IOException {
		boolean ok = true;
		URI requestURI = exchange.getRequestURI();
		if (FlowConfiguration.getInstance().getActionNameFromURI(requestURI) == null) {
			HelperController.sendError(exchange, 404, "404: PAGE NOT FOUND");
			ok = false;
		}
		return ok;
	}

	private boolean validateHTTPMethod(HttpExchange exchange, BusinessAction actionBean) throws IOException {
		String rqMethod = exchange.getRequestMethod();
		boolean ok = true;

		// 1. Validate standard value
		if (rqMethod == null || (!rqMethod.equals("GET") && !rqMethod.equals("POST") && !rqMethod.equals("PUT")
				&& !rqMethod.equals("DELETE") && !rqMethod.equals("PUT") && !rqMethod.equals("OPTIONS")
				&& !rqMethod.equals("HEAD") && !rqMethod.equals("TRACE") && !rqMethod.equals("CONNECT"))) {
			HelperController.sendError(exchange, 406, "NOT ACCEPTABLE");
			ok = false;
		} else {
			if (!rqMethod.equals(actionBean.getHttpMethod().toString())) {
				HelperController.sendError(exchange, 405, "METHOD NOT ALLOWED");
				ok = false;
			}
		}
		return ok;
	}

	private boolean validateUserAuthentication(BusinessAction actionBean,HttpExchange exchange) throws IOException{
		boolean ok=true;

		if (actionBean.isAuthenticated()) {
			if(!cookieSessionValidation(exchange)){
				//REDIRECT TO LOGIN PAGE

				ok=false;
				URI requestURI = exchange.getRequestURI();
				try {
					LoginPageBean viewBean=new LoginPageBean();
					viewBean.setMessage("RESTRICTED ACCES TO "+requestURI.getPath()+". YOU MUST BE AUTHENTICATED. PLEASE, DO A LOGIN: ");
					viewBean.setOriginalRequest(HelperController.getPathFromURI(requestURI, AppController.applicationContext));
					HelperController.sendDynamicView(exchange, "com.schibsted.test.webapp.view.LoginView", viewBean);
				} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
					//add log!
					HelperController.sendError(exchange, 500, "INTERNAL SERVER ERROR");
				} 
			}
		}
		return ok;
	}

	private boolean validateUserAuthorization(BusinessAction actionBean, HttpExchange exchange) throws IOException {
		boolean ok=true;
		if (actionBean.getAccesRole() != null && !actionBean.getAccesRole().isEmpty()) {
			// Getting user from sessionId
			String sessionId = HelperController.getSessionIdFromCookie(exchange);
			Integer userId = UserSessionManager.getUserIdBySessionId(sessionId,UserSessionStorage.getInstance());
			UserDAO<User> dao = new UserDAO<User>();
			try {
				User user = dao.getById(userId);
				List<Rol> userRoles = user.getRols();
				String a = userRoles.get(0).toString();

				Optional<Rol> optionalRol = userRoles.stream()
						.filter(rol -> rol.toString().equals(actionBean.getAccesRole().toUpperCase()))
						.findFirst();
				if (!optionalRol.isPresent()) {
					HelperController.sendError(exchange, 403, "FORBIDEN");
					ok = false;
				}
			} catch (DAOException e) {
				//add log!
				HelperController.sendError(exchange, 500, "INTERNAL SERVER ERROR");
			}
		}
		return ok;
	}
}
