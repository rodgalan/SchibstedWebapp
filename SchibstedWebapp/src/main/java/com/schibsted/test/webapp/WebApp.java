package com.schibsted.test.webapp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.schibsted.test.webapp.core.controller.Controller;
import com.schibsted.test.webapp.core.controller.FlowConfiguration;
import com.schibsted.test.webapp.core.controller.HTTPRequestFilter;
import com.schibsted.test.webapp.core.session.UserSessionStorage;
import com.schibsted.test.webapp.dataStorage.UserDataStorage;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;




/**
 * Hello world!
 *
 */
@SuppressWarnings("restriction")
public class WebApp 
{
	
	private static final Logger log = Logger.getLogger( WebApp.class.getName() );
	//Aixo ens ho podem endur a un fitxer de configuracio del server 
	private static int port = 8080;
	public static String applicationContext = "/webapp/"; //sin finalment va a config, haurà de deixarar de ser public!
	
	private HttpServer server = null;
	
    //INITS SERVER
	public static void main( String[] args ) throws IOException
    {		
		
		//A controlar esto :) Exception in thread "main" java.net.BindException: Address already in use: bind ()haremos puerto +1
		log.log( Level.INFO, "Starting Server on port ...");
    	
    	//TODO: Revisar que fem amb IOException
    	HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);    	
    	
    	//Create server context & assign the Controller (requests handler, planetar si lo separamos) 
    	HttpContext httpContext = server.createContext(applicationContext, new Controller());
    	
    	httpContext.getFilters().add(new HTTPRequestFilter());
    	
    	FlowConfiguration.loadConfiguration();
    	//No physical storage for this PoC. ONLY FOR ACADEMICAL PURPOSE!
    	UserDataStorage.loadUserDataStorage();
    	UserSessionStorage.loadSessionStorage();
    	
    	
    	server.start();
    	
    	/*User admin=UserDataStorage.getInstance().getUser("admin");
    	System.out.println("Username: "+admin.getUsername());
    	System.out.println("Rol: "+admin.getRols());*/
    	log.log( Level.INFO, "Server Started successfully");
    	
    	

    }
	
	
}
