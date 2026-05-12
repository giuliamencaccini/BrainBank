package it.ispwproject.brainbank.dao;

import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.model.User;

public interface UserDAO {
    User findByEmail(String email) throws DAOException;
}