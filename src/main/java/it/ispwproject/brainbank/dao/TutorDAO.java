package it.ispwproject.brainbank.dao;

import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.model.Subject;
import it.ispwproject.brainbank.model.Tutor;

import java.util.List;

public interface TutorDAO {
    List<Tutor> getBySubject(Subject subject) throws DAOException;
    Tutor findById(int id) throws DAOException;
}