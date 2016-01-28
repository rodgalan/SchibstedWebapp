package com.schibsted.test.webapp.dao;

import java.util.function.Predicate;

import com.schibsted.test.webapp.core.dao.IDAO;
import com.schibsted.test.webapp.core.exceptions.DAOException;
import com.schibsted.test.webapp.dataStorage.UserDataStorage;
import com.schibsted.test.webapp.model.User;

public class UserDAO<T> implements IDAO<User>{

	@Override
	public boolean add(User user) throws DAOException {
		UserDataStorage.getInstance().setUser(user);
		return true;
	}

	@Override
	public boolean update(User user) throws DAOException {
		UserDataStorage.getInstance().modifyUser(user);
		return false;
	}

	@Override
	public boolean remove(int userId) throws DAOException {
		UserDataStorage.getInstance().deleteUser(userId);
		return false;
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

}
