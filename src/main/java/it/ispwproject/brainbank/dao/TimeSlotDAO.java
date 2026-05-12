package it.ispwproject.brainbank.dao;

import it.ispwproject.brainbank.bean.TimeSlotBean;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.model.TimeSlot;
import it.ispwproject.brainbank.model.Tutor;

import java.util.List;

public interface TimeSlotDAO {
    List<TimeSlot> getAvailableByTutor(Tutor tutor) throws DAOException;
    List<TimeSlotBean> getAllByTutorWithStudent(int tutorId) throws DAOException;
    TimeSlot findById(int id) throws DAOException;
    void save(TimeSlot slot, int tutorId) throws DAOException;
}