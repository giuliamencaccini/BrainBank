package it.ispwproject.brainbank.dao;

import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.model.Progress;

public interface ProgressDAO {
    void saveOrUpdate(Progress progress) throws DAOException;
    Progress findByStudentAndTutor(int tutorId, int studentId) throws DAOException;
}