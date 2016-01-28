package com.schibsted.test.webapp.businessAction;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.schibsted.test.webapp.WebApp;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;





@SuppressWarnings("restriction")
public class Controller implements HttpHandler{
	
	private final static String[] contentTypePost={"application/x-www-form-urlencoded","multipart/form-data","application/json"}; 
	

	//CON MVC MOLON, BUSINESS DELEGATE
	/*public void handle(HttpExchange exchange) throws IOException {
		// TODO Auto-generated method stub
		System.out.println("He capturado la petición porque molo un monton");
		URI requestedUri = exchange.getRequestURI();
		IBusinessService service=BusinessLookup.getBusinessService(requestedUri);
		if(service!=null){
			service.doProcessing();
		}
	}*/
	
	
	//CON MVC SENCILLO, SIN BUSINESS DELEGATE
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		// TODO Auto-generated method stub
		System.out.println("He capturado la petición porque molo un monton");
		URI requestedUri = exchange.getRequestURI();
		
		
		String response = "";
        int statusCode = 200;
        
		if(requestedUri!=null){
			String relativePath=requestedUri.getPath().substring(WebApp.applicationContext.length()-1);
			if(relativePath.endsWith("/")){
				relativePath=relativePath.substring(0, relativePath.length()-1);
			}
			switch(relativePath){
				case "/login":
					getPOSTParameters(exchange);
					exchange.sendResponseHeaders(200, 0);
				    OutputStream os = exchange.getResponseBody();
				    // slash is platorm indepenendent. ensure please!
				    File file = new File("C:/Users/Anna/workspaceSH_Mars1/webapp/src/main/webapp/WEB-INF/views/login.html").getCanonicalFile();
				    FileInputStream fs = new FileInputStream(file);
				      final byte[] buffer = new byte[0x10000];
				      int count = 0;
				      while ((count = fs.read(buffer)) >= 0) {
				        os.write(buffer,0,count);
				      }
				      fs.close();
				      os.close();
					
					break;
					
				case "/validateUser":
					response="VEREMOS SI TE VALIDO CHATIN :p";
					Map<String,List<String>>postParameters=getPOSTParameters(exchange);
					List<String> username=postParameters.get("username");
					List<String> password=postParameters.get("password");
					
					break;
					
				case "/page1": 
					response="Pues hemos pasado por pagina 1!!";
					break;
				case "/page2":
					response="Pues hemos pasado por pagina 2!!";
					break;
				case "/page3":
					response="Pues hemos pasado por pagina 3!!";
					break;
				default: 
					statusCode=404;
					break;
			}
		}
		exchange.sendResponseHeaders(statusCode, response.length());
		 // Send the body response
        OutputStream os = exchange.getResponseBody();
        os.write(response.toString().getBytes());
        os.close();
	}
	
	private Map<String,List<String>> getPOSTParameters(HttpExchange exchange) throws IOException{
		//1. VALIDATE Content-Type (only application/x-www-form-urlencoded or multipart/form-data or application/json alowed. Others must send 415 status)
		//2. Decode values from the message body
		Map<String,List<String>> postParameters= null;
		String statusCode="200";
		if(validatePOSTContentType(exchange)){
			postParameters=getPOSTValues(exchange);
		}else{
			statusCode="415";
		}
		return postParameters;
		
	}
	
	private boolean validatePOSTContentType(HttpExchange exchange){
		String ct=exchange.getRequestHeaders().getFirst("Content-Type");
		boolean valid=false;
		if(ct!=null){
			for (String validct : contentTypePost ){
				if(ct.equals(validct)){
					valid=true;
					break;
				}
			}
		}
		return valid;
	}
	
	
	
	private Map<String,List<String>> getPOSTValues(HttpExchange exchange) throws IOException{
		Map<String,List<String>> parms = new HashMap<String,List<String>>();
		
		//Mal esto habria que leerlo o poner utf o algo (content type puede contener charset creo
		String encoding = "ISO-8859-1";
		
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
		for (String def: defs) {
		    int ix = def.indexOf('=');
		    String name;
		    String value;
		    if (ix < 0) {
		        name = URLDecoder.decode(def, encoding);
		        value = "";
		    } else {
		        name = URLDecoder.decode(def.substring(0, ix), encoding);
		        value = URLDecoder.decode(def.substring(ix+1), encoding);
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

}
