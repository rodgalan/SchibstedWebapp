package com.schibsted.test.services.core;

import java.io.IOException;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;

@SuppressWarnings("restriction")
public class ServicesRequestFilter extends Filter{

	@Override
	public String description() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
		// TODO Auto-generated method stub
		System.out.println("Filtre service - peticio intreceptada");
		
		ServicesHelper.setResponseHeaders(exchange);
		chain.doFilter(exchange);
		
	}
	
	

}
