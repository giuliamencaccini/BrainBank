package it.ispwproject.brainbank.dao;

import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.model.TimeSlot;
import it.ispwproject.brainbank.model.Tutor;

import java.util.List;

public interface TimeSlotDAO {
    List<TimeSlot> getAvailableByTutor(Tutor tutor) throws DAOException;
    List<TimeSlot> getAllByTutor(int tutorId) throws DAOException;
    List<TimeSlot> getPastByTutor(int tutorId) throws DAOException;
    TimeSlot findById(int id) throws DAOException;
    void save(TimeSlot slot, int tutorId) throws DAOException;
    boolean reserveSlot(int slotId, int minutes) throws DAOException;
    void releaseSlot(int slotId) throws DAOException;
    void delete(int slotId, int tutorId) throws DAOException;
}