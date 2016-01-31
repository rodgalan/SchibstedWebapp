package com.schibsted.test.services.core;

import java.io.IOException;

import com.schibsted.test.webapp.core.controller.HelperController;
import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;



/**
 * Generic filter for REST services
 * @author Anna
 *
 */
@SuppressWarnings("restriction")
public class ServicesRequestFilter extends Filter{

	@Override
	public String description() {
		// TODO Auto-generated method stub
		return null;
	}

	
	/**
	 * Sets standard response headers for REST services and validates HTTP methods allowed
	 */
	@Override
	public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
		// TODO Auto-generated method stub		
		ServicesHelper.setResponseHeaders(exchange);
		if(validateHTTPMethod(exchange)){
			chain.doFilter(exchange);
		}
	}
	
	
	
	private boolean validateHTTPMethod(HttpExchange exchange) throws IOException {
		String rqMethod = exchange.getRequestMethod();
		boolean ok = true;

		// 1. Validate standard value
		if (rqMethod == null || (!rqMethod.equals("GET") && !rqMethod.equals("POST") && !rqMethod.equals("PUT")
				&& !rqMethod.equals("DELETE") && !rqMethod.equals("PUT") && !rqMethod.equals("OPTIONS")
				&& !rqMethod.equals("HEAD") && !rqMethod.equals("TRACE") && !rqMethod.equals("CONNECT"))) {
			HelperController.sendError(exchange, 406, "NOT ACCEPTABLE");
			ok = false;
		} else {
			if (rqMethod == null || (!rqMethod.equals("GET") && !rqMethod.equals("POST") && !rqMethod.equals("PUT")
					&& !rqMethod.equals("DELETE"))) {
				HelperController.sendError(exchange, 405, "METHOD NOT ALLOWED");
				ok = false;
			}
		}
		return ok;
	}
	
	

}
