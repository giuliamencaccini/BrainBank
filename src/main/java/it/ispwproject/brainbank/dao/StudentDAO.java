package it.ispwproject.brainbank.dao;

import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.model.Student;

import java.util.List;

public interface StudentDAO {
    Student findById(int id) throws DAOException;
    List<Student> getByTutor(int tutorId) throws DAOException;
    void addFavouriteTutor(int studentId, int tutorId) throws DAOException;
    void removeFavouriteTutor(int studentId, int tutorId) throws DAOException;
    boolean isFavouriteTutor(int studentId, int tutorId) throws DAOException;
}