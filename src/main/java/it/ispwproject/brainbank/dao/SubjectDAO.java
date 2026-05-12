package it.ispwproject.brainbank.dao;

import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.model.Subject;

import java.util.List;

public interface SubjectDAO {
    List<Subject> getAll() throws DAOException;

    Subject findById(int id) throws DAOException;
}