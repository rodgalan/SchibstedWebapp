package com.schibsted.test.services.user;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.schibsted.test.services.core.RESTServiceClass;
import com.schibsted.test.services.core.Service;
import com.schibsted.test.services.core.Service.HttpMethods;
import com.schibsted.test.services.core.ServicesHelper;
import com.schibsted.test.services.user.bean.RolsInfo;
import com.schibsted.test.services.user.bean.UserInfo;
import com.schibsted.test.services.user.bean.UserInfoExtended;
import com.schibsted.test.services.user.bean.UsersInfo;
import com.schibsted.test.webapp.core.controller.HelperController;
import com.schibsted.test.webapp.core.exceptions.DAOException;
import com.schibsted.test.webapp.core.exceptions.RolException;
import com.schibsted.test.webapp.core.security.SecurityHelper;
import com.schibsted.test.webapp.dao.UserDAO;
import com.schibsted.test.webapp.model.IRolTypes.Rol;
import com.schibsted.test.webapp.model.User;
import com.sun.net.httpserver.HttpExchange;


/**
 * SERVICE USERS 
 * @author Anna
 *
 */

@SuppressWarnings("restriction")
@RESTServiceClass(basePath = UserServiceHttpHandler.userServiceContext, serviceName = "User")
public class UserService {

	@Service(method = HttpMethods.GET, relativeURI = "")
	public void getUsers(HttpExchange exchange, String relativePath) throws IOException {
		UserDAO<User> dao = new UserDAO<User>();
		List<User> users;
		try {
			users = dao.getAll();
			List<UserInfo> usersView = new ArrayList<UserInfo>();
			if (usersView != null) {
				for (User user : users) {
					usersView.add(mapUserModelToUserService(user));
				}
			}
			sendUsersXML(usersView, exchange);
		} catch (DAOException e) {
			HelperController.sendError(exchange, 500, "INTERNAL SERVER ERROR");
		} catch (JAXBException e) {
			HelperController.sendError(exchange, 500, "INTERNAL SERVER ERROR");
		}
	}

	@Service(method = HttpMethods.GET, relativeURI = "/{userId}")
	public void getUser(HttpExchange exchange, String relativePath) throws JAXBException, IOException {
		Integer userId = getUserId(relativePath);

		if (userId != null) {
			UserDAO<User> dao = new UserDAO<User>();
			try {
				User userModel = dao.getById(userId);
				if (userModel != null) {
					UserInfo userInfo = mapUserModelToUserService(userModel);
					// String json=getJsonObject(userService);
					sendUserXML(userInfo, exchange);
				} else {
					HelperController.sendError(exchange, 422, "UNPROCESSABLE ENTITY. INVALID ENTITY");
				}
			} catch (DAOException e) {
				HelperController.sendError(exchange, 500, "INTERNAL SERVER ERROR");
			}
		} else {
			HelperController.sendError(exchange, 422, "UNPROCESSABLE ENTITY. INVALID ENTITY");
		}
	}

	@Service(method = HttpMethods.POST, relativeURI = "/{userId}")
	public void modifyUser(HttpExchange exchange, String relativePath) throws IOException {
		Integer userId = getUserId(relativePath);
		String message = ServicesHelper.getMessageFromRequest(exchange);
		if (message != null && userId != null) {
			try {
				UserInfo userInfo = getUserInfoFromXML(message);
				User user = null;
				try {
					user = mapUserServiceToUserModel(userInfo);
				} catch (RolException e) {
					HelperController.sendError(exchange, 400, "BAD REQUEST. " + e.getMessage());
				}
				UserDAO<User> dao = new UserDAO<User>();
				user.setUserId(userId);
				// sets userId (from query) and password (from original message)
				// - this two cannot be updated here.
				User originalUser = dao.getById(userId);
				user.setPasssword(originalUser.getPasssword());
				boolean result = dao.update(user);
				if (result) {
					HelperController.sendError(exchange, 204, "USER MODIFIED");
				} else {
					HelperController.sendError(exchange, 422, "UNPROCESSABLE ENTITY. INVALID ENTITY");
				}

			} catch (JAXBException e) {
				HelperController.sendError(exchange, 400, "BAD REQUEST. MALFORMED MESSAGE");
			} catch (DAOException e) {
				HelperController.sendError(exchange, 500, "INTERNAL SERVER ERROR");
			}
		} else {
			HelperController.sendError(exchange, 400,
					"BAD REQUEST. MALFORMED MESSAGE (body and userId cannot be null)");
		}
	}

	@Service(method = HttpMethods.PUT, relativeURI = "")
	public void addUsers(HttpExchange exchange, String relativePath) throws IOException {
		String message = ServicesHelper.getMessageFromRequest(exchange);
		if (message != null && relativePath.equals("")) {
			try {
				UserInfo userInfo = getUserInfoFromXML(message);
				User user = null;
				try {
					user = mapUserServiceToUserModel(userInfo);
				} catch (RolException e) {
					HelperController.sendError(exchange, 400, "BAD REQUEST. " + e.getMessage());
				}
				//Default password = username 
				UserDAO<User> dao = new UserDAO<User>();
				User existsUsername=dao.getByBusinessKey(userInfo.getUsername());
				if(existsUsername!=null){
					HelperController.sendError(exchange, 412, "PRE-CONDITION FAILED. USERNAME ALREADY EXITS.");
					//USER ALREADY EXISTS!
				}else{
					String password=userInfo.getUsername();
					user.setPasssword(SecurityHelper.getPasswordHash(password));
					Integer userId = dao.add(user);
					if (userId != null) {
						user.setUserId(userId);
						UserInfoExtended userInfoExt = mapUserModelToExtendedUserService(user,password);
						sendExtendedUserXML(userInfoExt, exchange);
					} else {
						HelperController.sendError(exchange, 500, "INTERNAL SERVER ERROR");
					}
				}

			} catch (JAXBException e) {
				HelperController.sendError(exchange, 400, "BAD REQUEST. MALFORMED MESSAGE");
			} catch (DAOException e) {
				HelperController.sendError(exchange, 500, "INTERNAL SERVER ERROR");
			}
		} else {
			HelperController.sendError(exchange, 400, "BAD REQUEST. ");
		}
	}

	@Service(method = HttpMethods.DELETE, relativeURI = "/{userId}")
	public void deleteUser(HttpExchange exchange, String relativePath) throws IOException {
		Integer userId = getUserId(relativePath);

		if (userId != null) {
			UserDAO<User> dao = new UserDAO<User>();
			try {
				boolean ok = dao.remove(userId);
				if (ok) {
					HelperController.sendError(exchange, 204, "NO CONTENT. USER HAVE BEEN DELETED");
				} else {
					HelperController.sendError(exchange, 422, "UNPROCESSABLE ENTITY. INVALID ENTITY");
				}
			} catch (DAOException e) {
				HelperController.sendError(exchange, 500, "INTERNAL SERVER ERROR");
			}
		} else {
			HelperController.sendError(exchange, 422, "UNPROCESSABLE ENTITY. INVALID ENTITY");
		}
	}

	/*
	 * *** PRIVATE METHODS
	 ***/

	private Integer getUserId(String relativePath) {
		Integer userId = null;
		String userIdParam = relativePath.substring(relativePath.indexOf("/") + 1);
		try {
			userId = new Integer(userIdParam);
		} catch (NumberFormatException e) {

		}
		return userId;

	}

	private static UserInfo mapUserModelToUserService(User userModel) {
		UserInfo userView = null;
		if (userModel != null && userModel.getUserId() != null) {
			userView = new UserInfo();
			userView.setUsername(userModel.getUsername());

			RolsInfo rols = new RolsInfo();
			userView.setRolsInfo(rols);
			if (userModel.getRols() != null && !userModel.getRols().isEmpty()) {
				for (Rol rol : userModel.getRols()) {
					rols.getRol().add(rol.toString());
				}
			}
		}
		return userView;
	}

	private static UserInfoExtended mapUserModelToExtendedUserService(User userModel, String passwordCreated) {
		UserInfoExtended extendedUserView = new UserInfoExtended();
		UserInfo userView = mapUserModelToUserService(userModel);
		extendedUserView.setUserId(userModel.getUserId());
		extendedUserView.setUserInfo(userView);
		extendedUserView.setPassword(passwordCreated);
		extendedUserView.setNextLink("/service/users/" + userModel.getUserId());
		return extendedUserView;
	}

	private static User mapUserServiceToUserModel(UserInfo userView) throws RolException {
		User userModel = null;
		if (userView != null) {
			userModel = new User();
			userModel.setUsername(userView.getUsername());
			if (userView.getRolsInfo() != null && userView.getRolsInfo().getRol() != null
					&& !userView.getRolsInfo().getRol().isEmpty()) {
				List<Rol> modelRols = new ArrayList<Rol>();
				
				
				//Not to add the same rol twice
				boolean hasAdmin=false;boolean hasP1=false;boolean hasP2=false;boolean hasP3=false;
				for (String rolView : userView.getRolsInfo().getRol()) {
					// TODO: We must have only one enum of ROLE
					userModel.setRols(modelRols);

					switch (rolView) {
					case "ADMIN":
						if(hasAdmin){
							throw new RolException("ROL " + rolView + " NOT ALLOWED. ALLOWED VALUES ARE ADMIN,PAGE1,PAGE2,PAGE3");
						}else{
							modelRols.add(Rol.ADMIN);
							hasAdmin=true;
						}
						break;
					case "PAGE1":
						if(hasP1){
							throw new RolException("ROL " + rolView + " NOT ALLOWED. ALLOWED VALUES ARE ADMIN,PAGE1,PAGE2,PAGE3");
						}else{
							modelRols.add(Rol.PAGE1);
							hasP1=true;
						}
						break;
					case "PAGE2":
						if(hasP2){
							throw new RolException("ROL " + rolView + " NOT ALLOWED. ALLOWED VALUES ARE ADMIN,PAGE1,PAGE2,PAGE3");
						}else{
							modelRols.add(Rol.PAGE2);
							hasP2=true;
						}
						break;
					case "PAGE3":
						if(hasP3){
							throw new RolException("ROL " + rolView + " NOT ALLOWED. ALLOWED VALUES ARE ADMIN,PAGE1,PAGE2,PAGE3");
						}else{
							modelRols.add(Rol.PAGE3);
							hasP3=true;
						}
						break;
					default:
						throw new RolException(
								"ROL " + rolView + " NOT ALLOWED.ALLOWED VALUES ARE ADMIN,PAGE1,PAGE2,PAGE3");
					}
				}
			}
		}
		return userModel;
	}

	private void sendUserXML(UserInfo user, HttpExchange exchange) throws JAXBException, IOException {
		JAXBContext jaxbContext = JAXBContext.newInstance(UserInfo.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		// Can't use mashal to outputstream, we need size :(
		StringWriter writer = new StringWriter();
		jaxbMarshaller.marshal(user, writer);
		exchange.sendResponseHeaders(200, writer.getBuffer().length());
		OutputStream os = exchange.getResponseBody();
		try {
			os.write(writer.toString().getBytes());
		} finally {
			os.close();
		}
	}

	private void sendUsersXML(List<UserInfo> usersView, HttpExchange exchange) throws JAXBException, IOException {
		UsersInfo usersInfo = new UsersInfo();
		for (UserInfo user : usersView) {
			usersInfo.getUserInfo().add(user);
		}

		JAXBContext jaxbContext = JAXBContext.newInstance(UsersInfo.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		// Can't use mashal to outputstream, we need size :(
		StringWriter writer = new StringWriter();
		jaxbMarshaller.marshal(usersInfo, writer);
		exchange.sendResponseHeaders(200, writer.getBuffer().length());
		OutputStream os = exchange.getResponseBody();
		try {
			os.write(writer.toString().getBytes());
		} finally {
			os.close();
		}
	}

	private void sendExtendedUserXML(UserInfoExtended user, HttpExchange exchange) throws JAXBException, IOException {
		JAXBContext jaxbContext = JAXBContext.newInstance(UserInfoExtended.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		// Can't use mashal to outputstream, we need size :(
		StringWriter writer = new StringWriter();
		jaxbMarshaller.marshal(user, writer);
		exchange.sendResponseHeaders(200, writer.getBuffer().length());
		OutputStream os = exchange.getResponseBody();
		try {
			os.write(writer.toString().getBytes());
		} finally {
			os.close();
		}
	}

	private UserInfo getUserInfoFromXML(String message) throws JAXBException, IOException {
		JAXBContext jaxbContext = JAXBContext.newInstance(UserInfo.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		InputStream streamMessage = new ByteArrayInputStream(message.getBytes(StandardCharsets.UTF_8));
		UserInfo userInfo = (UserInfo) jaxbUnmarshaller.unmarshal(streamMessage);
		return userInfo;
	}

}
