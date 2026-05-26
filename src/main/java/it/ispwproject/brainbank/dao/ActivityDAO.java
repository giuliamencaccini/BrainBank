package it.ispwproject.brainbank.dao;

import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.model.Activity;

import java.util.List;

public interface ActivityDAO {
    void save(Activity activity) throws DAOException;
    List<Activity> getByStudentAndTutor(int tutorId, int studentId) throws DAOException;
    List<Activity> getByStudent(int studentId) throws DAOException;
    void markAsCompleted(int activityId, int studentId) throws DAOException;
    Activity findById(int activityId, int studentId) throws DAOException;
    void delete(int activityId, int tutorId) throws DAOException;
}
