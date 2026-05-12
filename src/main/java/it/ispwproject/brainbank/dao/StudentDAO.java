package it.ispwproject.brainbank.dao;

import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.model.Student;

import java.util.List;

public interface StudentDAO {
    Student findById(int id) throws DAOException;
    List<Student> getByTutor(int tutorId) throws DAOException;
}