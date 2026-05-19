package it.ispwproject.brainbank.dao.memory;

import it.ispwproject.brainbank.controller.demo.DemoDataStore;
import it.ispwproject.brainbank.dao.BookingDAO;
import it.ispwproject.brainbank.enumerator.BookingStatus;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.model.Booking;
import it.ispwproject.brainbank.model.Student;
import it.ispwproject.brainbank.model.TimeSlot;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BookingDAOMemory implements BookingDAO {

    private final DemoDataStore store = DemoDataStore.getInstance();

    @Override
    public void save(Booking booking) throws DAOException {
        booking.setId(store.nextBookingId());
        booking.setStatus(BookingStatus.CONFIRMED);
        if (booking.getMeetLink() == null) {
            booking.setMeetLink("https://meet.jit.si/brainbank-" +
                    UUID.randomUUID().toString().substring(0, 8));
        }
        store.getBookings().add(booking);
        booking.getTimeSlot().setAvailable(false);
    }

    @Override
    public List<Booking> findByStudent(int studentId) throws DAOException {
        return store.getBookings().stream()
                .filter(b -> b.getStudent() != null && b.getStudent().getId() == studentId)
                .toList();
    }

    @Override
    public List<Booking> findByTutor(int tutorId) throws DAOException {
        return store.getBookings().stream()
                .filter(b -> b.getTutor() != null && b.getTutor().getId() == tutorId
                        && b.getStatus() == BookingStatus.CONFIRMED)
                .toList();
    }

    @Override
    public List<Booking> findAll() throws DAOException {
        return new ArrayList<>(store.getBookings());
    }

    @Override
    public List<Booking> findCompletedByStudentAndTutor(int studentId, int tutorId) throws DAOException {
        return store.getBookings().stream()
                .filter(b -> b.getStudent() != null && b.getStudent().getId() == studentId
                        && b.getTutor() != null && b.getTutor().getId() == tutorId
                        && b.getStatus() == BookingStatus.CONFIRMED
                        && b.getTimeSlot() != null
                        && !b.getTimeSlot().getDate().isAfter(LocalDate.now()))
                .toList();
    }

    @Override
    public List<Booking> findUpcomingByStudentAndTutor(int studentId, int tutorId) throws DAOException {
        return store.getBookings().stream()
                .filter(b -> b.getStudent() != null && b.getStudent().getId() == studentId
                        && b.getTutor() != null && b.getTutor().getId() == tutorId
                        && b.getStatus() == BookingStatus.CONFIRMED
                        && b.getTimeSlot() != null
                        && b.getTimeSlot().getDate().isAfter(LocalDate.now()))
                .toList();
    }

    @Override
    public void cancel(int bookingId, int studentId) throws DAOException {
        Booking booking = store.getBookings().stream()
                .filter(b -> b.getId() == bookingId)
                .findFirst()
                .orElseThrow(() -> new DAOException("Prenotazione non trovata (ID: " + bookingId + ")"));
        Student student = booking.getStudent();
        if (student == null || student.getId() != studentId) {
            throw new DAOException("Non puoi annullare una prenotazione che non ti appartiene.");
        }
        booking.cancel();
        TimeSlot slot = booking.getTimeSlot();
        if (slot != null) slot.setAvailable(true);
    }
}