package it.ispwproject.brainbank.dao;

import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.model.Booking;

import java.util.List;

public interface BookingDAO {
    void save(Booking booking) throws DAOException;
    List<Booking> findByStudent(int studentId) throws DAOException;
    List<Booking> findByTutor(int tutorId) throws DAOException;
    List<Booking> findCompletedByStudentAndTutor(int studentId, int tutorId) throws DAOException;
    List<Booking> findUpcomingByStudentAndTutor(int studentId, int tutorId) throws DAOException;
    void cancel(int bookingId, int studentId) throws DAOException;
    List<Booking> findAll() throws DAOException;
}
