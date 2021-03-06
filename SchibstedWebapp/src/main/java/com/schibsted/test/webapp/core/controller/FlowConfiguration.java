package com.schibsted.test.webapp.core.controller;

import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.relation.Role;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.schibsted.test.webapp.WebApp;
import com.schibsted.test.webapp.controller.flowconfig.beans.BusinessAction;
import com.schibsted.test.webapp.controller.flowconfig.beans.FlowConfig;
import com.schibsted.test.webapp.controller.flowconfig.beans.Forward;

/**
 * Singleton pattern (Eager loading) implementation to storage application flow configuration in
 * cache. Eager loading : Configuration always nedded. Once loaded only read
 * acces, no concurrency porblems
 * 
 * @author Anna
 * 
 * Used to test Lambda Java8 style (PoC)
 *
 */

public class FlowConfiguration {
	private static final Logger log = Logger.getLogger(WebApp.class.getName());
	private static FlowConfig flowConfiguration;
	
	//private final static String CONF_FILE_PATH="/resources/config/configuration.xml";
	private final static String CONF_FILE_PATH="/config/configuration.xml";

	// private static volatile FlowConfiguration instance = null; LAZY LOADING
	// NOT NEEDED
	private static final FlowConfiguration instance = new FlowConfiguration();

	private FlowConfiguration() {
		log.log(Level.INFO, "Loading FlowConfiguration");
		if (instance != null) {
			log.log(Level.SEVERE, "Singleton FlowConfiguration already instantiated");
			throw new IllegalStateException("Singleton FlowConfiguration already instantiated");
		}
		loadFlowconfiguration();
	}

	// LAZY LOADING NOT NEEDED
	/*
	 * public static FlowConfiguration getInstance() { if (instance == null) {
	 * synchronized (FlowConfiguration.class) { if (instance == null) { instance
	 * = new FlowConfiguration(); } } } log.log(Level.FINE,
	 * "Getting FlowConfiguration instance."); return instance; }
	 */

	// EAGER LOADING
	public static FlowConfiguration getInstance() {
		log.log(Level.FINE, "Getting FlowConfiguration instance.");
		return instance;
	}

	private static void loadFlowconfiguration() {	
		InputStream configStream=FlowConfiguration.class.getResourceAsStream(CONF_FILE_PATH);
	
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(FlowConfig.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			//flowConfiguration = (FlowConfig) jaxbUnmarshaller.unmarshal(file);
			flowConfiguration = (FlowConfig) jaxbUnmarshaller.unmarshal(configStream);
		} catch (JAXBException e) {
			// TODO Podemos crear una excepcion custom molona que se lance y al
			// captiurarse en el main pare en server. eso mola mucho.
			log.log(Level.SEVERE,
					"Error loading flow application file config. Please review this configuration and ensure you xml file is compliant with configuration.xsd ");
			e.printStackTrace();
		}
	}

	/*
	 * *********************************** FLOW CONFIGURATION API
	 **********************************/

	/**
	 * 
	 */
	public static void loadConfiguration() {
		// Nothing to do: first static invocation loads the singleton instance
		// (eager)
	}
	

	
	
	
	
	
	//TODO: TOT AIXO VA CAP A FlowConfigurationHelper
	
	public BusinessAction getBusinessActionFromURI(URI requestURI){
		return getOptionalBusinessActionFromURI(requestURI).get();
	}
	
	
	

	public String getActionNameFromURI(URI requestURI){
		Optional<BusinessAction> optionalAction=getOptionalBusinessActionFromURI(requestURI);
		return optionalAction.map(action -> action.getBusinessActionClass()).orElse(null);
	}
	
	
	
	public boolean isAuthenticatedURL(URI requestURI){
		Optional<BusinessAction> optionalAction=getOptionalBusinessActionFromURI(requestURI);
		return optionalAction.map(action -> action.isAuthenticated()).orElse(false);
	}
	
	public String getAutorizedRol(URI requestURI){
		String role=null;
		Optional<BusinessAction> optionalAction=getOptionalBusinessActionFromURI(requestURI);
		if(optionalAction.isPresent()){
			role=optionalAction.get().getAccesRole();
		}
		return role;
	}
	
	public boolean isAuthorizatedURI(URI requestURI){
		String role=getAutorizedRol(requestURI);
		return role!=null && !role.isEmpty();
	}
	
	public String getActionForwardPath(String actionClassName, String forwardName){
		String forwardPath=null;
		Optional<BusinessAction> optionalAction = getOptionalBusinessActionFromActionName(actionClassName);
		Optional <List<Forward>> forwardList=optionalAction.map(action -> action.getForward());
		Optional<Forward> optionalForward=forwardList.get().stream().filter(forward -> forward.getName().equals(forwardName)).findFirst();
		if(optionalForward.isPresent()){
			forwardPath=optionalForward.get().getName();
		}
		return forwardPath;
	}
	
	
	

	/*
	 * PRIVATE
	 */
	
	

	
	
	private Optional<BusinessAction> getOptionalBusinessActionFromURI(URI requestURI) {
		String path=HelperController.getPathFromURI(requestURI,AppController.applicationContext);
		List<BusinessAction> actions=flowConfiguration.getBusinessAction();
		Optional<BusinessAction> optionalAction=actions.stream().filter(action -> action.getPath().trim().toLowerCase().equals(path)).findFirst();
		return optionalAction;
	}
	
	private Optional<BusinessAction> getOptionalBusinessActionFromActionName(String actionName) {
		List<BusinessAction> actions=flowConfiguration.getBusinessAction();
		Optional<BusinessAction> optionalAction=actions.stream().filter(action -> action.getBusinessActionClass().equals(actionName)).findFirst();
		return optionalAction;
	}
	
	

	
}
