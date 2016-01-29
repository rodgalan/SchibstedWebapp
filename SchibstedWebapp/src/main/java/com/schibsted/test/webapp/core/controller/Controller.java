package com.schibsted.test.webapp.core.controller;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.schibsted.test.webapp.controller.flowconfig.beans.BusinessAction;
import com.schibsted.test.webapp.controller.flowconfig.beans.Forward;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

@SuppressWarnings("restriction")
public class Controller implements HttpHandler {

	// private final static String[] contentTypePost = {
	// "application/x-www-form-urlencoded", "multipart/form-data",
	// "application/json" };

	// Haure de juntar el tractament de cookies al algun lloc. ara esta entre el
	// filtre i el controller
//	public static final String COOKIE_NAME = "SchibstedWebappSessionId";
//	public static final String HTTP_POST = "POST";
//	public static final String HTTP_GET = "GET";
	
	

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		System.out.println("He capturado la petición porque molo un monton 2");
		if (exchange != null && exchange.getRequestURI() != null) {
			// TODO important: validar content-type. Hi ha un metode por ahi, el
			// problema es que el meu html es cutre

			String requestMethod = exchange.getRequestMethod();
			Map<String, List<String>> parameters = null;
			switch (requestMethod) {
			case HelperController.HTTP_POST:
				parameters = HelperController.getPOSTParameters(exchange);
				break;
			case HelperController.HTTP_GET:
				parameters = HelperController.getGETParameters(exchange);
				break;
			default: 
				HelperController.sendError(exchange,405,"METHOD NOT ALLOWED.");
			}

			// Map<String, List<String>> parametersNOVA =
			// getPOSTParametersNOVA(exchange);
			// Map<String, List<String>> parameters =
			// getPOSTParameters(exchange);

			URI requestURI = exchange.getRequestURI();
			FlowConfiguration fc = FlowConfiguration.getInstance();
			BusinessAction actionBean = fc.getBusinessActionFromURI(requestURI);

			try {
				Class<?> actionClassImpl = Class.forName(actionBean.getBusinessActionClass());
				IBusinessActionLayer action = (IBusinessActionLayer) actionClassImpl.newInstance();

				// PRE Filter has validated Session. If there are the cookie &&
				// authenticated action then is a valid cookie.
				//TODO GETS BETTER TO IMPLEMENT AN HTTPSESSION TO STORAGGE ANY VALUE LINKED TO SESSIONID
				String sessionId = null;
				if (actionBean.isAuthenticated()) {
					sessionId = HelperController.getSessionIdFromCookie(exchange);
				}
				ViewBean viewBean = action.doProcessing(parameters, sessionId);
				Optional<Forward> optForward = actionBean.getForward().stream()
						.filter(forward -> forward.getName().equals(viewBean.getForwardName())).findFirst();
				if (optForward.isPresent()) {
					Forward forward = optForward.get();

					// Intercept login Actions or use a Filer?
					if (actionBean.getBusinessActionClass()
							.equals("com.schibsted.test.webapp.businessAction.ValidateUserAction")
							&& viewBean instanceof LoginSessionBean && viewBean != null
							&& ((LoginSessionBean) viewBean).getSessionId() != null) {

						HelperController.setCookie(exchange, ((LoginSessionBean) viewBean).getSessionId());
					}
					
					if(forward.getType()!=null){
						switch (forward.getType()) {
						case STATIC:
							
							HelperController.sendStaticView(exchange, forward.getPath());
							break;
						case DYNAMIC:
							HelperController.sendDynamicView(exchange, forward.getPath(), viewBean);
							break;
						case REDIRECT:
							// If exists redirectPath redirect to this (dynamic redirection), else redirects to the forward specified in configuration.xml and selected by action 
							String redirectPath=null;
							if(viewBean!=null && viewBean.getRedirectLocation()!=null && !viewBean.getRedirectLocation().isEmpty()){
								redirectPath=viewBean.getRedirectLocation();
							}else if(forward.getPath()!=null && !forward.getPath().isEmpty()){
								redirectPath=forward.getPath();
							} else{
								HelperController.sendError(exchange, 500, "INTERNAL SERVER ERROR");
								//Not Forward not redirect found (log)
							}
							HelperController.redirectPage(exchange, redirectPath, viewBean);
							break;
	
						default: // @TODO Error de configuracio.
									// ConfigErrorException
						}
					}else{
						//Forward not found (log)
						HelperController.sendError(exchange, 500, "INTERNAL SERVER ERROR");
					}
				}

			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

}
