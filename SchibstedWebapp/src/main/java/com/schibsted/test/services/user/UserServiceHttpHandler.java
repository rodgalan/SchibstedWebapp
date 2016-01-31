package com.schibsted.test.services.user;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.schibsted.test.webapp.core.controller.HelperController;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

@SuppressWarnings("restriction")
public class UserServiceHttpHandler implements HttpHandler {
	
	public static final String userServiceContext = "/service/users";

	//TODO CAPTURAR AQUI TOTES LES EXCEPCIONS NO TRACTADES INTERNAMENT I RETORNAR 500
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		
		//TODO: ANNOTATION SERVICE NOT FOUND AT RUNTIME! (RetentionPolicy.RUNTIME, ElementType.METHOD)
		/*
		Class<UserService> service = UserService.class;
		if (service.isAnnotationPresent(RESTServiceClass.class)) {
			
			Annotation annotation = service.getAnnotation(RESTServiceClass.class);
			RESTServiceClass restServiceClassAnnotation=(RESTServiceClass) annotation;
			
			if(restServiceClassAnnotation.basePath().equals(userServiceContext)){
				
				for (Method method : service.getDeclaredMethods()) {
					Annotation methodAnnotation = method.getAnnotation(Service.class);
					Service serviceAnnotation = (Service) methodAnnotation; 
					if(methodAnnotation.equals(httpMethod) && isCorrectPath(requestPath,serviceAnnotation.method().toString())){
						//Instantiate class and invoke method :)
						try {
							method.invoke(service.newInstance(), exchange,requestPath);
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
								| InstantiationException e) {
							HelperController.sendError(exchange,500, "INTRENAL SERVER ERROR");
						}
					}
				}
			}
		}*/
		
		//WORKAROUND SINCE FIX PROBLEM WITH ANNOTATION SERVICE
		String httpMethod = exchange.getRequestMethod();
		String requestPath = HelperController.getPathFromURI(exchange.getRequestURI(), userServiceContext);
		UserService service=new UserService();
		try {
			switch(httpMethod){
			case "GET":
				if(requestPath.trim().equals("")){
					service.getUsers(exchange, requestPath);
				}else{
					service.getUser(exchange, requestPath);
				}
				break;
			case "DELETE":
				service.deleteUser(exchange, requestPath);
				break;
			case "PUT":
				service.addUsers(exchange, requestPath);
				break;
			case "POST":
				if(requestPath.trim().equals("")){
					service.modifyUser(exchange, requestPath);
				}else{
					
				}
				break;
			default:
				//TODO: Hi ha dos casos: url incorrecta o http method no acceptat o http method incorrecte. Alguns dells desde el filtre
				HelperController.sendError(exchange, 406, "BLA BLA PENSAR ESTO");
				break;
				
			}
			
		} catch (Exception e) {
			HelperController.sendError(exchange, 500, "INTERNAL SERVER ERROR");
			System.out.println("ERROR! "+e.toString());
		}
		
	}

}
