package it.ispwproject.brainbank.dao;

import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.model.User;

import java.util.List;

public interface RegistrationDAO {
    boolean emailExists(String email) throws DAOException;
    void save(User user, String bio, List<Integer> subjectIds) throws DAOException;
}