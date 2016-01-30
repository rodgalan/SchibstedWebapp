package com.schibsted.test.services.user;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import com.schibsted.test.services.core.RESTServiceClass;
import com.schibsted.test.services.core.Service;
import com.schibsted.test.services.user.bean.UserServiceBean;
import com.schibsted.test.webapp.core.controller.HelperController;
import com.schibsted.test.webapp.core.exceptions.DAOException;
import com.schibsted.test.webapp.dao.UserDAO;
import com.schibsted.test.webapp.model.User;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

@SuppressWarnings("restriction")
public class UserServiceHttpHandler implements HttpHandler {
	
	public static final String userServiceContext = "/service/users";

	//TODO CAPTURAR AQUI TOTES LES EXCEPCIONS NO TRACTADES INTERNAMENT I RETORNAR 500
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		
		String httpMethod = exchange.getRequestMethod();
		String requestPath = HelperController.getPathFromURI(exchange.getRequestURI(), userServiceContext);
		UserService service=new UserService();
		try {
			service.getUser(exchange, requestPath);
		} catch (Exception e) {
			HelperController.sendError(exchange, 500, "INTERNAL SERVER ERROR");
			System.out.println("ERROR! "+e.toString());
		}
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
		
		
		
		
		
		
		
			/*
		if (requestPath.trim().isEmpty()) {
			//USERS OPERATIONS --> ONLY GET ALLOWED
			System.out.println("SERVICE REQUEST :"+requestPath+", USERS OPERATIONS");
			switch (httpMethod) {
				case "GET": 
					//GET service/users
					System.out.println("SERVICE REQUEST :"+requestPath+", USERS OPERATIONS BY GET. RETURN A LIST");
					Headers he=exchange.getRequestHeaders();
					Collection <List<String>> headers=he.values();
					headers.forEach(header -> header.forEach(es-> System.out.println("HEADER: "+es+"\n") ));
					break;
				
				default: 
					HelperController.sendError(exchange,405,"METHOD NOT ALLOWED.");
					break;
			}

		} else if (requestPath.trim().startsWith(("/"))) { //TODO validar ,millor aixo, no hauria de tenir mes barres!
 				//SINGLE USER OPERATIONS --> GET/POST/PUT/DELETE ALLOWED
				Integer userId=null;
				try{
					userId=new Integer(requestPath.trim());
				}catch(NumberFormatException e){
					HelperController.sendError(exchange, 500, "User Id must be an integer");
					//TODO veure quin codi toca tornar aqui (o potser tipus d'errror?)
				}
				
				System.out.println("SERVICE REQUEST :"+requestPath+", SINGLE USER OPERATIONS");
				switch (httpMethod) {
				case "GET": 
					//GET service/users/{userId}
					System.out.println("SERVICE REQUEST :"+requestPath+", SINGLE USER OPERATION BY GET. GETS ID USER AND RETURN USER");
					
					break;
				case "PUT": 
					//PUT service/users/{userId} --> ADD USER INFO (with POST parameters)
					System.out.println("SERVICE REQUEST :"+requestPath+", SINGLE USER OPERATION BY PUT. ");
					Map<String, List<String>> addParametersList = HelperController.getPOSTParameters(exchange);
					List<String> addUserInfo = addParametersList.get(0);
					break;
				case "DELETE": 
					//PUT service/users/{userId} --> DELETE USER INFO (withou parameters)
					System.out.println("SERVICE REQUEST :"+requestPath+", SINGLE USER OPERATION BY DELETE. ");
					break;
				case "POST": 
					//POST service/users/{userId} --> MODIFY USER INFO (with POST parameters)
					System.out.println("SERVICE REQUEST :"+requestPath+", SINGLE USER OPERATION BY POST. ");
					Map<String, List<String>> updParametersList = HelperController.getPOSTParameters(exchange);
					List<String> updUserInfo = updParametersList.get(0);
					break;
				default:
					HelperController.sendError(exchange, 405, "METHOD NOT ALLOWED.");
					break;
				}
			}else{
				//TODO: URL MAL
				HelperController.sendError(exchange, 405, "METHOD NOT ALLOWED.");
			}
		

	exchange.sendResponseHeaders(500, 0);
	exchange.getResponseBody().close();*/

	}
	
	
	
		//TODO IMPROVEMENT It works fine with our case, but not with any case!!!
		private boolean isCorrectPath(String requestPath, String basePath){
			boolean ok=false;
			if(basePath.equals(requestPath)){
				ok=true;
			} else {
				String[] pathLevels=requestPath.split("/");
				String[] basePathLevels=basePath.split("/");
				if(pathLevels.length==basePathLevels.length){
					ok=true;
				}				
			}
			return ok;
		}	
	
	
	
	private static UserServiceBean getUser(Integer userId) throws DAOException {
		UserDAO<User> dao = new UserDAO<User>();
		User user = dao.getById(userId);
		if (user != null) {
			UserServiceBean userService = mapUserModelToUserService(user);
		}
		return null;
	}

	private static List<UserServiceBean> getUsers() {
		return null;
	}

	private static void modifyUser(UserServiceBean user) throws DAOException {
		UserDAO<User> dao = new UserDAO<User>();
		if (user != null) {
			User userModel = mapUserServiceToUserModel(user);
			if (userModel != null) {
				dao.update(userModel);
			}
		}

	}

	private static void addUser(UserServiceBean user) throws DAOException {
		UserDAO<User> dao = new UserDAO<User>();
		if (user != null) {
			User userModel = mapUserServiceToUserModel(user);
			if (userModel != null) {
				dao.add(userModel);
			}
		}
	}

	private static UserServiceBean mapUserModelToUserService(User userModel) {
		UserServiceBean userView = null;
		new UserServiceBean();
		if (userModel != null && userModel.getUserId() != null) {
			userView = new UserServiceBean();
			userView.setUserId(userModel.getUserId());
			userView.setUsername(userModel.getUsername());
			userView.setRols(userModel.getRols());
		}
		return userView;
	}

	private static User mapUserServiceToUserModel(UserServiceBean userView) {
		User userModel = null;
		if (userView != null) {
			userModel = new User();
			userModel.setUserId(userView.getUserId());
			userModel.setUsername(userView.getUsername());
			userModel.setRols(userView.getRols());
		}
		return userModel;
	}

	

	/*private String getQueryString() {

	}*/

}
