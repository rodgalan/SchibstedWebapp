package com.schibsted.test.services.user;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.schibsted.test.services.core.RESTServiceClass;
import com.schibsted.test.services.core.Service;
import com.schibsted.test.services.core.Service.HttpMethods;
import com.schibsted.test.services.user.bean.RolsInfo;
import com.schibsted.test.services.user.bean.UserInfo;
import com.schibsted.test.services.user.bean.UserServiceBean;
import com.schibsted.test.webapp.core.exceptions.DAOException;
import com.schibsted.test.webapp.dao.UserDAO;
import com.schibsted.test.webapp.model.IRolTypes.Rol;
import com.schibsted.test.webapp.model.User;
import com.sun.net.httpserver.HttpExchange;

@RESTServiceClass(basePath=UserServiceHttpHandler.userServiceContext, serviceName="User")
public class UserService {
	
	@Service(method=HttpMethods.GET,relativeURI="")
	public void getUsers(HttpExchange exchange){
		System.out.println("UserService.getUsers");
		
		
	}
	
	@Service(method=HttpMethods.GET,relativeURI="/{userId}")
	public void getUser(HttpExchange exchange, String relativePath) throws JAXBException, IOException{
		System.out.println("UserService.getUser");
		Integer userId=getUserId(relativePath);
	
		if(userId!=null){
			UserDAO<User> dao = new UserDAO<User>();
			try {
				User userModel = dao.getById(userId);
				if (userModel != null) {
					UserInfo userInfo = mapUserModelToUserService(userModel);
					//String json=getJsonObject(userService);
					sendUserXML(userInfo, exchange); 
				}
			} catch (DAOException e) {
				//TODO ver que hacemos, error 500
			}
		}
	}
	
	@Service(method=HttpMethods.POST,relativeURI="/{userId}")
	public void modifyUser(HttpExchange exchange, String relativePath){
		System.out.println("UserService.modifyUsers");

	}
	
	@Service(method=HttpMethods.PUT,relativeURI="/{userId}")
	public void addUsers(HttpExchange exchange, String relativePath){
		System.out.println("UserService.addUsers");
	}
	
	@Service(method=HttpMethods.DELETE,relativeURI="/{userId}")
	public void DeleteUser(HttpExchange exchange, String relativePath){
		System.out.println("UserService.deleteUsers");
	}
	
	/* ***
	 * PRIVATE METHODS
	 * ***/

	private boolean validateUserIdFormat(String userIdParam){
		boolean ok=false;
		Integer userId=null;
		try{
			userId=new Integer(userIdParam);
		}catch(NumberFormatException e){
			ok=true;
		}
		return ok;
	}
	
	private Integer getUserId(String relativePath){
		Integer userId=null;
		String userIdParam=relativePath.substring(relativePath.indexOf("/")+1);
		try{
			userId=new Integer(userIdParam);
		}catch(NumberFormatException e){
			
		}
		return userId;
		
	}
	
	private static UserInfo mapUserModelToUserService(User userModel) {
		UserInfo userView = null;
		new UserServiceBean();
		if (userModel != null && userModel.getUserId() != null) {
			userView = new UserInfo();
			userView.setUserId(userModel.getUserId());
			userView.setUsername(userModel.getUsername());
			
			RolsInfo rols=new RolsInfo();
			if(userModel.getRols()!=null && !userModel.getRols().isEmpty()){
				List<String> rolList = new ArrayList<String>();
				for (Rol rol : userModel.getRols()){
					rolList.add(rol.toString());
				}
			}
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
	
	private void sendUserXML(UserInfo user, HttpExchange exchange) throws JAXBException, IOException{
		JAXBContext jaxbContext = JAXBContext.newInstance(UserInfo.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		//Can't use mashal to outputstream, we need size :(
		StringWriter writer=new StringWriter();
		jaxbMarshaller.marshal(user, writer);
		exchange.sendResponseHeaders(200, writer.getBuffer().length());
		OutputStream os=exchange.getResponseBody();
		try{
			os.write(writer.toString().getBytes());
		}finally{
			os.close();
		}
	}
}
