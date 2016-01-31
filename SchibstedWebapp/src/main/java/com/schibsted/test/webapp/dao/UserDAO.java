package com.schibsted.test.webapp.dao;

import java.util.List;
import java.util.function.Predicate;

import com.schibsted.test.webapp.core.dao.IDAO;
import com.schibsted.test.webapp.core.exceptions.DAOException;
import com.schibsted.test.webapp.dataStorage.UserDataStorage;
import com.schibsted.test.webapp.model.User;

public class UserDAO<T> implements IDAO<User>{

	@Override
	public boolean add(User user) throws DAOException {
		return UserDataStorage.getInstance().setUser(user);
	}

	@Override
	public boolean update(User user) throws DAOException {
		return UserDataStorage.getInstance().modifyUser(user);
	}

	@Override
	public boolean remove(int userId) throws DAOException {
		return UserDataStorage.getInstance().deleteUser(userId);
	}

	@Override
	public User getById(int userId) throws DAOException {
		User user=null;
		try {
			user=UserDataStorage.getInstance().getUserById(userId);
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return user;
	}

	@Override
	public User getByBusinessKey(String username) throws DAOException {
		User user=null;
		Predicate<User> predicate=us -> us.getUsername()==username;
		try {
			user=UserDataStorage.getInstance().findUserByPredicate(predicate);
		} catch (CloneNotSupportedException e) {
				throw new DAOException("UserDAO.getByBusinessKey. username="+username, e);
		}
		return user;
	}

	
	@Override
	public User findItemByCondition(Predicate<User> predicate) throws DAOException {
		User user=null;
		try {
			user=UserDataStorage.getInstance().findUserByPredicate(predicate);
		} catch (CloneNotSupportedException e) {
				throw new DAOException("UserDAO.findItem. predicate="+predicate.toString(), e);
		}
		return user;
	}

	@Override
	public List<User> getAll() throws DAOException {
		List<User> users;
		Predicate<User> filterCondition=u->u.getUserId()!=null;
		try {
			users = UserDataStorage.getInstance().findUsersByPredicate(filterCondition);
		} catch (CloneNotSupportedException e) {
			throw new DAOException("UserDAO.findItemsByCondition. filterCondition="+filterCondition.toString(), e);
		}
		return users;
	}

	@Override
	public List<User> findItemsByCondition(Predicate<User> filterCondition) throws DAOException {
		List<User> users;
		try {
			users = UserDataStorage.getInstance().findUsersByPredicate(filterCondition);
		} catch (CloneNotSupportedException e) {
			throw new DAOException("UserDAO.findItemsByCondition. filterCondition="+filterCondition.toString(), e);
		}
		return users;
	}
	
	
	
	

}
