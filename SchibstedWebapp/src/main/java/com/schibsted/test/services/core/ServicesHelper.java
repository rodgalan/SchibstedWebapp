package com.schibsted.test.services.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;

@SuppressWarnings("restriction")
public class ServicesHelper {
	
	public static String getMessageFromRequest(HttpExchange exchange) throws IOException {
		Map<String, List<String>> parms = new HashMap<String, List<String>>();

		// TODO Mal esto habria que leerlo o poner utf o algo (content type
		// puede // contener charset creo
		String encoding = "UTF-8";

		// read the query string from the request body
		String qry;
		InputStream in = exchange.getRequestBody();
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte buf[] = new byte[4096];
			for (int n = in.read(buf); n > 0; n = in.read(buf)) {
				out.write(buf, 0, n);
			}
			qry = new String(out.toByteArray(), encoding);
		} finally {
			in.close();
		}
		return qry;
	}
	
	public static void setResponseHeaders(HttpExchange exchange){
		//No cache headers
		exchange.getResponseHeaders().add("Cache-Control", "no-cache, no-store, must-revalidate");
		exchange.getResponseHeaders().add("Pragma", "no-cache");
		exchange.getResponseHeaders().add("Expires", "0");
		//Content type & accept only http
		//exchange.getResponseHeaders().add("Content-Type", "text/html");
		//exchange.getResponseHeaders().add("Accept", "text/html");
		//UTF-8 encoding
		exchange.getResponseHeaders().add("Accept-Charset", "utf-8");
		
	
	}

}
