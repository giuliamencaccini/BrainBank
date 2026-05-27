package it.ispwproject.brainbank.dao;

import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.model.User;

import java.util.List;

public interface UserDAO {
    User findByEmail(String email) throws DAOException;
    void updateEmail(int id, String newEmail) throws DAOException;
    List<User> getAll() throws DAOException;
}