package com.schibsted.test.webapp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.schibsted.test.services.user.UserServiceRequestFilter;
import com.schibsted.test.services.user.UserServiceHttpHandler;
import com.schibsted.test.webapp.core.controller.AppController;
import com.schibsted.test.webapp.core.controller.FlowConfiguration;
import com.schibsted.test.webapp.core.controller.HTTPRequestFilter;
import com.schibsted.test.webapp.core.exceptions.CoreException;
import com.schibsted.test.webapp.core.security.SecurityHelper;
import com.schibsted.test.webapp.core.session.UserSessionStorage;
import com.schibsted.test.webapp.dao.UserDAO;
import com.schibsted.test.webapp.dataStorage.UserDataStorage;
import com.schibsted.test.webapp.model.IRolTypes.Rol;
import com.schibsted.test.webapp.model.User;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;

/**
 * Hello world!
 *
 */
@SuppressWarnings("restriction")
public class WebApp {

	private static final Logger log = Logger.getLogger(WebApp.class.getName());
	
	// Aixo ens ho podem endur a un fitxer de configuracio del server
	private static int port = 8080;
	/*public static String applicationContext = "/webapp/";
	public static String userServiceContext = "/service/users";*/

	/*
	 * DEFAULT USERS
	 * TODO IMPORVE TO CONFIG FILE
	 */
	private static final String defaultAdminUserName = "admin";
	private static final String defaultAdminUserPassword = "admin";
	private static final Rol[] defaultAdminRols = { Rol.ADMIN };
	private static final User defaultAdminUser = new User(defaultAdminUserName, SecurityHelper.getPasswordHash(defaultAdminUserPassword),  Arrays.asList(defaultAdminRols));

	private static final String testUserRol1Name = "user1";
	private static final String testUserRol1Password = "user1";
	private static final Rol[] testUserRol1Rols = { Rol.PAGE1 };
	private static final User user1 = new User(testUserRol1Name, SecurityHelper.getPasswordHash(testUserRol1Password), Arrays.asList(testUserRol1Rols));

	private static final String testUserRol2Name = "user2";
	private static final String testUserRol2Password = "user2";
	private static final Rol[] testUserRol2Rols = { Rol.PAGE2 };
	private static final User user2 = new User(testUserRol2Name, SecurityHelper.getPasswordHash(testUserRol2Password), Arrays.asList(testUserRol2Rols));

	private static final String testUserRol3Name = "user3";
	private static final String testUserRol3Password = "user3";
	private static final Rol[] testUserRol3Rols = { Rol.PAGE3 };
	private static final User user3 = new User(testUserRol3Name, SecurityHelper.getPasswordHash(testUserRol3Password), Arrays.asList(testUserRol3Rols));
	
	private static final User[] defaultUsers={defaultAdminUser, user1, user2, user3};

	
	
	
	// INITS SERVER
	public static void main(String[] args) throws IOException, CoreException {

		// A controlar esto :) Exception in thread "main"
		// java.net.BindException: Address already in use: bind ()haremos puerto
		// +1
		
		log.log(Level.INFO, "Starting Server on port ...");
		HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);


		//CREATE SERVER CONTEXT & ASSIGN HANDLER:  FOR WEBAPP AND USER SERVICE
		HttpContext httpContext = server.createContext(AppController.applicationContext, new AppController());
		HttpContext httpServiceContext = server.createContext(UserServiceHttpHandler.userServiceContext, new UserServiceHttpHandler());

		//ASSIGN REUQEST FILTER FOR EACH HANDLER:  FOR WEBAPP AND USER SERVICE
		httpServiceContext.getFilters().add(new UserServiceRequestFilter());
		httpContext.getFilters().add(new HTTPRequestFilter());

		//LOAD FLOW CONFIG, USER STORAGE AND SESSION
		FlowConfiguration.loadConfiguration();
		UserDataStorage.loadUserDataStorage();   // No physical storage for this PoC. ONLY FOR ACADEMICAL PURPOSE!
		UserSessionStorage.loadSessionStorage();

		//INITS DEFAULT USERS
		UserDAO<User> dao=new UserDAO<User>();
		for(int i=0; i<defaultUsers.length; i++){
			//UserDataStorage.getInstance().setUser(defaultUsers[i]);
			dao.add(defaultUsers[i]);
		}


		//START SERVER
		server.start();

		
		log.log(Level.INFO, "Server Started successfully");

	}

}
