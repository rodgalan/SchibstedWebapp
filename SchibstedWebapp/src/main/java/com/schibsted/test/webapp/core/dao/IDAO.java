package com.schibsted.test.webapp.core.dao;

import java.util.List;
import java.util.function.Predicate;

import com.schibsted.test.webapp.core.exceptions.DAOException;

public interface IDAO<T> {
	
	public boolean add(T t) throws DAOException;
    public boolean update(T t) throws DAOException;
    public boolean remove(int index) throws DAOException;
    public T getById(int index) throws DAOException;
    public T getByBusinessKey(String key) throws DAOException;
    public List<T> findItemsByCondition(Predicate<T> filterCondition) throws DAOException;
    public T findItemByCondition(Predicate<T> predicate) throws DAOException;
    public List<T> getAll() throws DAOException;
}
