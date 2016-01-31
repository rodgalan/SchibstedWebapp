package com.schibsted.test.webapp.core.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import com.schibsted.test.webapp.WebApp;
import com.sun.net.httpserver.HttpExchange;

/**
 * 
 * Helper 
 * @author Anna
 *
 */

@SuppressWarnings("restriction")
public class HelperController {
	
	static final String COOKIE_NAME = "SchibstedWebappSessionId";
	static final String HTTP_POST = "POST";
	static final String HTTP_GET = "GET";
	
	static final String COOKIE_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
	private static final long ONE_HOUR_IN_MILLIS=3600000; 
	private static final long USER_COOKIE_ALIVE_HOURS=24; 
	
	//Base file path om wich static pages are allocated. 
	//TODO GETS BETTER in config file!!
	//static final String baseFileViewPath = "C:/Users/Anna/workspaceSH_Mars1/webapp/src/main/webapp/WEB-INF/views";
	private final static String BASE_STATIC_VIEW_PATH="/staticViews";
	
	
	public static void redirectPage(HttpExchange exchange, String newLocation, ViewBean bean) throws IOException { 
		//setResponseHeaders(exchange);
		if(bean!=null && bean.getMessage()!=null && !bean.getMessage().isEmpty()){
			newLocation=newLocation.concat("?").concat("message=").concat(bean.getMessage());
		}
		
		exchange.getResponseHeaders().add("Location", "/webapp" + newLocation);
		exchange.sendResponseHeaders(302, 0);
		exchange.getResponseBody().close();
	}
	
	
	//TODO GETS BETTER: It must be in POST Filter (or may be with an interceptor)
	public static void setResponseHeaders(HttpExchange exchange){
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
	public static void sendError(HttpExchange exchange, int httpStatusError , String httpStatusDescription) throws IOException {
		//setResponseHeaders(exchange);
		exchange.sendResponseHeaders(httpStatusError, httpStatusDescription.length());
		OutputStream os = exchange.getResponseBody();
		try{
			os.write(httpStatusDescription.toString().getBytes());
		}finally{
			os.close();
		}
	}
	
	

	public static void sendDynamicView(HttpExchange exchange, String pathViewClass, ViewBean viewBean)
			throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		Class<?> viewClassImpl = Class.forName(pathViewClass);
		IViewLayer view = (IViewLayer) viewClassImpl.newInstance();
		String response = view.renderView(viewBean);

		//setResponseHeaders(exchange);
		exchange.sendResponseHeaders(200, response.length());

		OutputStream os = exchange.getResponseBody();
		try{
			os.write(response.toString().getBytes());
		}finally{
			os.close();
		}
	}

	// TODO Metode copiat decontroller, esta duplicat!
	public static void sendStaticView(HttpExchange exchange, String pathPage) throws IOException {
		String filePath = BASE_STATIC_VIEW_PATH.concat(pathPage);
		OutputStream os = exchange.getResponseBody();
		
		
		// slash is platorm indepenendent. ensure please!
		//File file = new File(filePath).getCanonicalFile();
		InputStream htmlStream=FlowConfiguration.class.getResourceAsStream(filePath);
		byte[] contents = new byte[1024];
		int bytesRead=0;
		String strhtml=null;
		try{
			while( (bytesRead = htmlStream.read(contents)) != -1){ 
				 strhtml = new String(contents, 0, bytesRead);               
			}
		}finally{
			htmlStream.close();
		}
		
		
		//setResponseHeaders(exchange);
		exchange.sendResponseHeaders(200, strhtml.length()); // redirect
		
		try{
			os.write(strhtml.toString().getBytes());
		}finally{
			os.close();
		}
		

		/*FileInputStream fs = new FileInputStream(file);
		final byte[] buffer = new byte[0x10000];
		try{
			int count = 0;
			while ((count = fs.read(buffer)) >= 0) {
				os.write(buffer, 0, count);
			}
		}finally{
			fs.close();
			os.close();
		}*/
	}

	public static Map<String, List<String>> getGETParameters(HttpExchange exchange) throws IOException {
		URI requestedUri = exchange.getRequestURI();
		
		/*
		 * 
	
Thanks for this answer! It fails for params whose value includes an ampersand, though. To fix, use getRawQuery() instead of getQuery(), then param = java.net.URLDecoder.decode(param, "UTF-8") after splitting on "&".
		 */
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

	

	public static Map<String, List<String>> parseQuery(String query, String encoding) throws UnsupportedEncodingException {
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

	public static Map<String, List<String>> getPOSTParameters(HttpExchange exchange) throws IOException {
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
	public static void setCookie(HttpExchange exchange, String sessionId) {
		// Creates session cookie with one day expiration
		Long timestamp=new Date().getTime() + (ONE_HOUR_IN_MILLIS*USER_COOKIE_ALIVE_HOURS);
		Date cookieExpires=new Date(timestamp);
		exchange.getResponseHeaders().add("Set-Cookie", COOKIE_NAME + "=" + sessionId+"; HttpOnly; expires="+formatDateForCookie(cookieExpires));
	}
	
	/*
	 * Expires the webapp cookie in ResponseHeaders
	 */
	public static void expireCookie(HttpExchange exchange, String sessionId) {
		exchange.getResponseHeaders().add("Set-Cookie", COOKIE_NAME + "=" + sessionId +"; HttpOnly; token=deleted; expires="+formatDateForCookie(new Date()));
	}


	private static String formatDateForCookie(Date date){
		 SimpleDateFormat formatter = new SimpleDateFormat(COOKIE_DATE_FORMAT, Locale.US);
	     formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
	     String formated=formatter.format(date);
	     return formated;
	}
	
	public static String getSessionIdFromCookie(HttpExchange exchange) {
		String schibstedWebappSessionId = null;
		String cookieStr = exchange.getRequestHeaders().getFirst("Cookie");
		if (cookieStr != null && cookieStr.contains(HelperController.COOKIE_NAME + "=")) {
			schibstedWebappSessionId = cookieStr.replaceFirst(HelperController.COOKIE_NAME + "=", "");
		}
		return schibstedWebappSessionId;

	}
	
	
	/**
	 * Get relative application context from uri
	 * @param uri
	 * @param applicationContext
	 * @return
	 */
	public static String getPathFromURI(URI uri, String applicationContext) {
		String relativePath = null;
		if (uri != null && uri.getPath() != null) {
			//relativePath = uri.getPath().substring(applicationContext.length() - 1).trim().toLowerCase();
			relativePath = uri.getPath().substring(applicationContext.length() ).trim().toLowerCase();
		}
		return relativePath;
	}
	
}
