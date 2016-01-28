package com.schibsted.test.webapp.core.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;

/**
 * 
 * Only visible from it's package to be used by controller and filters
 * @author Anna
 *
 */

@SuppressWarnings("restriction")
class HelperController {
	
	static final String COOKIE_NAME = "SchibstedWebappSessionId";
	static final String HTTP_POST = "POST";
	static final String HTTP_GET = "GET";
	
	//Base file path om wich static pages are allocated. 
	//TODO GETS BETTER in config file!!
	static final String baseFileViewPath = "C:/Users/Anna/workspaceSH_Mars1/webapp/src/main/webapp/WEB-INF/views";
	
	
	static void redirectPage(HttpExchange exchange, String newLocation, ViewBean bean) throws IOException { 
		setResponseHeaders(exchange);
		if(bean!=null && bean.getMessage()!=null && !bean.getMessage().isEmpty()){
			newLocation=newLocation.concat("?").concat("message=").concat(bean.getMessage());
		}
		
		exchange.getResponseHeaders().add("Location", "/webapp/" + newLocation);
		exchange.sendResponseHeaders(302, 0);
		exchange.getResponseBody().close();
	}
	
	
	//TODO GETS BETTER: It must be in POST Filter (or may be with an interceptor)
	static void setResponseHeaders(HttpExchange exchange){
		//No cache headers
		exchange.getResponseHeaders().add("Cache-Control", "no-cache, no-store, must-revalidate");
		exchange.getResponseHeaders().add("Pragma", "no-cache");
		exchange.getResponseHeaders().add("Expires", "0");
		//Content type & accept only http
		exchange.getResponseHeaders().add("Content-Type", "text/html");
		exchange.getResponseHeaders().add("Accept", "text/html");
		//UTF-8 encoding
		exchange.getResponseHeaders().add("Accept-Charset", "utf-8");
		
	
	}
	
	/**
	 * Sends httpCode with specified description.
	 * Used for send http errors. 
	 * 
	 * @param exchange
	 * @param httpStatusError
	 * @param httpStatusDescription
	 * @throws IOException
	 */
	static void sendError(HttpExchange exchange, int httpStatusError , String httpStatusDescription) throws IOException {
		setResponseHeaders(exchange);
		OutputStream os = exchange.getResponseBody();
		exchange.sendResponseHeaders(httpStatusError, httpStatusDescription.length());
		try{
			os.write(httpStatusDescription.toString().getBytes());
		}finally{
			os.close();
		}
	}
	
	

	static void sendDynamicView(HttpExchange exchange, String pathViewClass, ViewBean viewBean)
			throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		Class<?> viewClassImpl = Class.forName(pathViewClass);
		IViewLayer view = (IViewLayer) viewClassImpl.newInstance();
		String response = view.renderView(viewBean);

		setResponseHeaders(exchange);
		exchange.sendResponseHeaders(200, response.length());

		OutputStream os = exchange.getResponseBody();
		try{
			os.write(response.toString().getBytes());
		}finally{
			os.close();
		}
	}

	// TODO Metode copiat decontroller, esta duplicat!
	static void sendStaticView(HttpExchange exchange, String pathPage) throws IOException {
		String filePath = baseFileViewPath.concat(pathPage);
		OutputStream os = exchange.getResponseBody();
		// slash is platorm indepenendent. ensure please!
		File file = new File(filePath).getCanonicalFile();

		setResponseHeaders(exchange);
		exchange.sendResponseHeaders(200, file.length()); // redirect

		FileInputStream fs = new FileInputStream(file);
		final byte[] buffer = new byte[0x10000];
		try{
			int count = 0;
			while ((count = fs.read(buffer)) >= 0) {
				os.write(buffer, 0, count);
			}
		}finally{
			fs.close();
			os.close();
		}
	}

	static Map<String, List<String>> getGETParameters(HttpExchange exchange) throws IOException {
		URI requestedUri = exchange.getRequestURI();
		//String query = requestedUri.getRawQuery();
		 String query = requestedUri.getQuery();
		
		Map<String, List<String>> parameters = new HashMap<String, List<String>>();
		if (query != null) {
			String encoding = "utf-8";
			// return parseQuery(query, encoding);
			String defs[] = query.split("[&]");
			for (String def : defs) {
				int ix = def.indexOf('=');
				String name;
				String value;
				if (ix < 0) {
					name = URLDecoder.decode(def, encoding);
					value = "";
				} else {
					name = URLDecoder.decode(def.substring(0, ix), encoding);
					value = URLDecoder.decode(def.substring(ix + 1), encoding);
				}
				List<String> list = parameters.get(name);
				if (list == null) {
					list = new ArrayList<String>();
					parameters.put(name, list);
				}
				list.add(value);
			}
		}
		return parameters;
	}

	static Map<String, List<String>> getPOSTParametersNOVA(HttpExchange exchange) throws IOException {

		// TODO Mal esto habria que leerlo o poner utf o algo (content type
		// puede // contener charset creo
		String encoding = "UTF-8";

		// read the query string from the request body
		String query = null;
		InputStream in = exchange.getRequestBody();
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte buf[] = new byte[4096];
			for (int n = in.read(buf); n > 0; n = in.read(buf)) {
				out.write(buf, 0, n);
			}
			query = new String(out.toByteArray(), encoding);
		} finally {
			in.close();
		}

		// return parseQuery(query,encoding);
		Map<String, List<String>> parameters = new HashMap<String, List<String>>();
		String defs[] = query.split("[&]");
		for (String def : defs) {
			int ix = def.indexOf('=');
			String name;
			String value;
			if (ix < 0) {
				name = URLDecoder.decode(def, encoding);
				value = "";
			} else {
				name = URLDecoder.decode(def.substring(0, ix), encoding);
				value = URLDecoder.decode(def.substring(ix + 1), encoding);
			}
			List<String> list = parameters.get(name);
			if (list == null) {
				list = new ArrayList<String>();
				parameters.put(name, list);
			}
			list.add(value);
		}
		return parameters;
	}

	static Map<String, List<String>> parseQuery(String query, String encoding) throws UnsupportedEncodingException {
		Map<String, List<String>> parameters = new HashMap<String, List<String>>();
		String defs[] = query.split("[&]");
		for (String def : defs) {
			int ix = def.indexOf('=');
			String name;
			String value;
			if (ix < 0) {
				name = URLDecoder.decode(def, encoding);
				value = "";
			} else {
				name = URLDecoder.decode(def.substring(0, ix), encoding);
				value = URLDecoder.decode(def.substring(ix + 1), encoding);
			}
			List<String> list = parameters.get(name);
			if (list == null) {
				list = new ArrayList<String>();
				parameters.put(name, list);
			}
			list.add(value);
		}
		return parameters;

	}

	static Map<String, List<String>> getPOSTParameters(HttpExchange exchange) throws IOException {
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

		// parse the query

		String defs[] = qry.split("[&]");
		for (String def : defs) {
			int ix = def.indexOf('=');
			String name;
			String value;
			if (ix < 0) {
				name = URLDecoder.decode(def, encoding);
				value = "";
			} else {
				name = URLDecoder.decode(def.substring(0, ix), encoding);
				value = URLDecoder.decode(def.substring(ix + 1), encoding);
			}
			List<String> list = parms.get(name);
			if (list == null) {
				list = new ArrayList<String>();
				parms.put(name, list);
			}
			list.add(value);
		}

		return parms;
	}

	/*
	 * Sets new webapp cookie in ResponseHeaders
	 */
	static void setCookie(HttpExchange exchange, String sessionId) {
		// Creates session cookie without expiration: alive usser session is
		// managed in server side (better security)
		exchange.getResponseHeaders().add("Set-Cookie", COOKIE_NAME + "=" + sessionId);
	}

	
	
	static String getSessionIdFromCookie(HttpExchange exchange) {
		String schibstedWebappSessionId = null;
		String cookieStr = exchange.getRequestHeaders().getFirst("Cookie");
		if (cookieStr != null && cookieStr.contains(HelperController.COOKIE_NAME + "=")) {
			schibstedWebappSessionId = cookieStr.replaceFirst(HelperController.COOKIE_NAME + "=", "");
		}
		return schibstedWebappSessionId;

	}


}
