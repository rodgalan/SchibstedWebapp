package com.schibsted.test.webapp.core.controller;

import java.util.List;
import java.util.Map;

public interface IBusinessActionLayer {
	
	/**
	 * 
	 * Method to implement the BuinessAction logic.
	 * 
	 * @param requestParameters - Get or Post parameters. If not parameters in user request the Map is initialized but empty
	 * @param sessionId - SessionId. It can be null in not authenticated cases.
	 * @return
	 * 
	 * TODO GETS BETTER: Distinct interfaces for each case (auth and not auth) extending same interface parent
	 */
	
	public ViewBean doProcessing(Map<String, List<String>> requestParameters, String sessionId);
}
