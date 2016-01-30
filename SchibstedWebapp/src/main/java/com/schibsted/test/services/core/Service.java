package com.schibsted.test.services.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD) //on class level
public @interface Service {
	
	public enum HttpMethods {
		   GET, POST, PUT, DELETE, OPTIONS, HEAD, TRACE, CONNECT
		}
	
	
	String relativeURI();
	HttpMethods method();

}
