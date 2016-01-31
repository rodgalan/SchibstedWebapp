package com.schibsted.test.webapp.dataStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.schibsted.test.webapp.WebApp;
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
	private static HashMap<Integer, User> users; //TODO crec que aixo deixa de ser un hashmap
	
	//used to assign userIds (userId starts at 1 to ensure no problems with default values)
	private static int usersIdCount=0;
	
	
	
	
	//private static volatile UserDataStorage instance = new UserDataStorage(); LAZY LOADING NOT NEEDED
	private static final UserDataStorage instance = new UserDataStorage(); 

	private UserDataStorage() {
		log.log(Level.INFO, "Loading User Data Storage");
		
		if(instance!=null){
			log.log(Level.SEVERE, "Singleton UserDataStorage already instantiated");
			throw new IllegalStateException("Singleton UserDataStorage already instantiated");
		}

		users = new HashMap<Integer, User>();
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
	
	
	
	
	
	/* *******
	 * ACCESORS 
	 * 		Modifying data (CUD), thread-safety acces to object users list
	 * 		Getting data return object cloned to ensure is not modified outside the storagge
	 */
	
	public User getUserById(int userId) throws CloneNotSupportedException {
		log.log(Level.INFO, "UserDataStorage.getUser with userId=" + userId);
		User userCloned=null;
		User userSearched=users.get(userId);
		if(userSearched!=null){
			userCloned=userSearched.clone();
		}
		return userCloned;
	}
	
	
	// Modifying data, thread-safety acces to object users list
	public boolean setUser(User user){
		boolean ok=false;
		user.setUserId(++usersIdCount);
		log.log(Level.FINE, "UserDataStorage.setUser with userId=" + user.toString());
		synchronized (users) {
			int sizeBefore=users.size();
			users.put(user.getUserId(), user);
			ok=sizeBefore<users.size();
		}
		return ok;
	}
	
	// Modifying data, thread-safety acces to object users list
	public boolean deleteUser(int userId) {
		log.log(Level.FINE, "UserDataStorage.deleteUser with userId=" + userId);
		boolean ok=false;
		synchronized (users) {
			int sizeBefore=users.size();
			users.remove(userId);
			ok=sizeBefore>users.size();
		}
		return ok;
	}
	
	// Modifying data, thread-safety access to object users list.
	public boolean modifyUser(User user) {
		log.log(Level.FINE, "UserDataStorage.modifyUserData with:" + user);
		synchronized (users) {
			users.remove(user.getUserId());
			users.put(user.getUserId(), user);
		}
		return true;
	}
	
	public User findUserByPredicate(Predicate<User> filterCondition) throws CloneNotSupportedException{
		User user=null;
		List<User> usersList=users.values().stream().filter(filterCondition).collect(Collectors.<User>toList());
		if(usersList!=null&&usersList.size()==1){
			user=usersList.get(0).clone();
		}
		return user;
	}
	
	public List<User> findUsersByPredicate(Predicate<User> filterCondition) throws CloneNotSupportedException{
		List<User> usersList=users.values().stream().filter(filterCondition).collect(Collectors.<User>toList());
		List<User> clonedUsersList=null;
		if(usersList!=null){
			clonedUsersList=new ArrayList<User>();
			for(User user : usersList){
				clonedUsersList.add(user.clone());
			}
		}
		return clonedUsersList;
	}
	
}

