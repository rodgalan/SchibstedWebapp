package com.schibsted.test.webapp.dataStorage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.schibsted.test.webapp.WebApp;
import com.schibsted.test.webapp.core.security.SecurityHelper;
import com.schibsted.test.webapp.model.IRolTypes.Rol;
import com.schibsted.test.webapp.model.User;

/**
 * @author Anna
 * 
 *         Singleton pattern (Eager loading) implementation to storage User Data in cache (no
 *         physical storage for this PoC). ONLY FOR ACADEMICAL PURPOSE!
 *         
 *         This cache simulates a physical Storage (generating an internal id for each user and and offering 
 *         an integration interface with basic access methods: CRUD by Id + searching by parametrized method).
 *         
 *         To prevent user data can be modified outside this class (without use CUD methods provided), read
 *         operations returns a clone user object.
 *         
 *          
 *         Eager loading : Users always nedded.
 *         
 */

public class UserDataStorage {

	private static final Logger log = Logger.getLogger(WebApp.class.getName());


	//TODO GETS BETTER: Initial users in config file!
	//TODO GETS BETTER: Load initial users from main 
	
	// Default admin user
	/*private static final String defaultAdminUserName = "admin";
	private static final String defaultAdminUserPassword = "admin";
	private static final Rol[] defaultAdminRols = { Rol.ADMIN };
	
	//Inital Test Users for the PoC
	private static final String testUserRol1Name = "user1";
	private static final String testUserRol1Password = "user1";
	private static final Rol[] testUserRol1Rols = { Rol.PAGE_1 };
	
	private static final String testUserRol2Name = "user2";
	private static final String testUserRol2Password = "user2";
	private static final Rol[] testUserRol2Rols = { Rol.PAGE_2 };
	
	private static final String testUserRol3Name = "user3";
	private static final String testUserRol3Password = "user3";
	private static final Rol[] testUserRol3Rols = { Rol.PAGE_3 };*/

	private static HashMap<Integer, User> users; //TODO crec que aixo deixa de ser un hashmap
	
	//used to assign userIds (userId starts at 1 to ensure no problems with default values)
	private static int usersIdCount=0;
	
	
	
	
	//private static volatile UserDataStorage instance = new UserDataStorage(); LAZY LOADING NOT NEEDED
	private static final UserDataStorage instance = new UserDataStorage(); 

	/*private static User defaultAdminUser() {
		log.log(Level.INFO, "Creating default user admin");
		List<Rol> adminRols = Arrays.asList(defaultAdminRols);
		User defaultAdminUser = new User(defaultAdminUserName, SecurityHelper.getPasswordHash(defaultAdminUserPassword), adminRols);
		
		List<Rol> user1Rols = Arrays.asList(testUserRol1Rols);
		User user1User = new User(testUserRol1Name, SecurityHelper.getPasswordHash(testUserRol1Password), user1Rols);
		
		List<Rol> user2Rols = Arrays.asList(testUserRol2Rols);
		User user2User = new User(testUserRol2Name, SecurityHelper.getPasswordHash(testUserRol2Password), user2Rols);
		
		List<Rol> user3Rols = Arrays.asList(testUserRol3Rols);
		User user3User = new User(testUserRol3Name, SecurityHelper.getPasswordHash(testUserRol3Password), user3Rols);
		
		return defaultAdminUser;
	}*/
	
	

	
	private UserDataStorage() {
		log.log(Level.INFO, "Loading User Data Storage");
		
		if(instance!=null){
			log.log(Level.SEVERE, "Singleton UserDataStorage already instantiated");
			throw new IllegalStateException("Singleton UserDataStorage already instantiated");
		}
		
		users = new HashMap<Integer, User>();
		//User defaultAdminUser = defaultAdminUser();
		//this.setUser(defaultAdminUser);
	}

	
	// LAZY LOADING NOT NEEDED
	/*public static UserDataStorage getInstance() {
		if (instance == null) {
			synchronized (UserDataStorage.class) {
				if (instance == null) {
					instance = new UserDataStorage();
				}
			}
		}
		log.log(Level.FINE, "Getting UserDataStorage instance.");
		return instance;
	}*/
	
	public static void loadUserDataStorage() {
		// Nothing to do: first static invocation loads the singleton instance
		// (eager)
	}

	// EAGER LOADING
	public static UserDataStorage getInstance() {
		log.log(Level.FINE, "Getting UserDataStorage instance.");
		return instance;
	}
	
	
	/**
	 * Returns a copy of userInstance (ensures object modifications not directly to cached object)
	 * 
	 * @param userId
	 * @return
	 * @throws CloneNotSupportedException
	 */
	
	public User getUserByCondition(int userId) throws CloneNotSupportedException {
		log.log(Level.INFO, "UserDataStorage.getUser with userId=" + userId);
		return users.get(userId).clone();
	}
	
	public User getUserById(int userId) throws CloneNotSupportedException {
		log.log(Level.INFO, "UserDataStorage.getUser with userId=" + userId);
		return users.get(userId).clone();
	}
	
	
	// Modifying data, thread-safety acces to object users list
	public void setUser(User user){
		user.setUserId(++usersIdCount);
		log.log(Level.FINE, "UserDataStorage.setUser with userId=" + user.toString());
		synchronized (users) {
			users.put(user.getUserId(), user);
		}
	}
	
	// Modifying data, thread-safety acces to object users list
	public void deleteUser(int userId) {
		log.log(Level.FINE, "UserDataStorage.deleteUser with userId=" + userId);
		synchronized (users) {
			users.remove(userId);
		}
	}
	
	// Modifying data, thread-safety access to object users list
	public void modifyUser(User user) {
		log.log(Level.FINE, "UserDataStorage.modifyUserData with:" + user);
		synchronized (users) {
			users.remove(user.getUserId());
			users.put(user.getUserId(), user);
		}
	}
	
	public User findUserByPredicate(Predicate<User> filterCondition) throws CloneNotSupportedException{
		User user=null;
		List<User> usersList=users.values().stream().filter(filterCondition).collect(Collectors.<User>toList());
		if(usersList!=null&&usersList.size()==1){
			user=usersList.get(0).clone();
		}
		return user;
	}
	
}

