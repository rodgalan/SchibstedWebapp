package com.schibsted.test.services.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * USED FOR ANNOTATE CLASSES THAT IMPLEMENTS REST SERVICES
 * @author Anna
 *
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE) //on class level
public @interface RESTServiceClass {
	
	String serviceName();
	String basePath();
	

}
