package it.ispwproject.brainbank.dao.db;

import it.ispwproject.brainbank.dao.AbstractBookingDAO;
import it.ispwproject.brainbank.dao.ConnectionFactory;
import it.ispwproject.brainbank.enumerator.BookingStatus;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDAODB extends AbstractBookingDAO {

    private static final String INSERT_BOOKING =
            "INSERT INTO booking (student_id, tutor_id, subject_id, slot_id, status, meet_link) " +
                    "VALUES (?, ?, ?, ?, 'CONFIRMED', ?)";

    private static final String CANCEL_BOOKING =
            "UPDATE booking SET status = 'CANCELLED' WHERE id = ? AND student_id = ?";

    private static final String FREE_SLOT =
            "UPDATE time_slot SET available = TRUE " +
                    "WHERE id = (SELECT slot_id FROM booking WHERE id = ? AND student_id = ?)";

    private static final String UPDATE_SLOT_AVAILABILITY =
            "UPDATE time_slot SET available = ? WHERE id = ?";

    private static final String SELECT_BOOKINGS =
            "SELECT b.id, b.status, b.meet_link, b.created_at, " +
                    "       u_s.id s_id, u_s.name s_name, u_s.surname s_surname, u_s.email s_email, " +
                    "       u_t.id t_id, u_t.name t_name, u_t.surname t_surname, u_t.email t_email, td.bio t_bio, " +
                    "       sub.id sub_id, sub.name sub_name, " +
                    "       ts.id ts_id, ts.date ts_date, ts.start_time, ts.end_time, ts.available " +
                    "FROM booking b " +
                    "JOIN user u_s ON b.student_id = u_s.id " +
                    "JOIN user u_t ON b.tutor_id   = u_t.id " +
                    "LEFT JOIN tutor_detail td ON u_t.id = td.user_id " +
                    "JOIN subject sub ON b.subject_id = sub.id " +
                    "JOIN time_slot ts ON b.slot_id   = ts.id ";

    private static final String FIND_BY_STUDENT = SELECT_BOOKINGS +
            "WHERE b.student_id = ? ORDER BY b.created_at DESC";

    private static final String FIND_BY_TUTOR = SELECT_BOOKINGS +
            "WHERE b.tutor_id = ? AND b.status = 'CONFIRMED' ORDER BY ts.date ASC";

    private static final String FIND_ALL = SELECT_BOOKINGS +
            "ORDER BY b.created_at DESC";

    private static final String FIND_COMPLETED_BY_STUDENT_AND_TUTOR = SELECT_BOOKINGS +
            "WHERE b.student_id = ? AND b.tutor_id = ? " +
            "  AND b.status = 'CONFIRMED' AND ts.date <= CURDATE() " +
            "ORDER BY ts.date DESC";

    private static final String FIND_UPCOMING_BY_STUDENT_AND_TUTOR = SELECT_BOOKINGS +
            "WHERE b.student_id = ? AND b.tutor_id = ? " +
            "  AND b.status = 'CONFIRMED' AND ts.date > CURDATE() " +
            "ORDER BY ts.date ASC";

    @Override
    public void save(Booking booking) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     INSERT_BOOKING, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, booking.getStudent().getId());
            ps.setInt(2, booking.getTutor().getId());
            ps.setInt(3, booking.getSubject().getId());
            ps.setInt(4, booking.getTimeSlot().getId());
            ps.setString(5, booking.getMeetLink());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) booking.setId(keys.getInt(1));
            }
            booking.setStatus(BookingStatus.CONFIRMED);
            updateSlotAvailability(conn, booking.getTimeSlot().getId(), false);
            addToCache(booking);
        } catch (SQLException e) {
            throw new DAOException("Errore durante il salvataggio: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Booking> findByStudent(int studentId) throws DAOException {
        List<Booking> cached = findInCacheByStudent(studentId);
        if (!cached.isEmpty()) return cached;
        List<Booking> result = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_STUDENT)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Booking b = mapToBooking(rs);
                    addToCache(b);
                    result.add(b);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Errore nel caricamento prenotazioni: " + e.getMessage(), e);
        }
        return result;
    }

    @Override
    public List<Booking> findByTutor(int tutorId) throws DAOException {
        List<Booking> result = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_TUTOR)) {
            ps.setInt(1, tutorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapToBooking(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Errore nel caricamento prenotazioni tutor: " + e.getMessage(), e);
        }
        return result;
    }

    @Override
    public List<Booking> findAll() throws DAOException {
        List<Booking> result = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) result.add(mapToBooking(rs));
        } catch (SQLException e) {
            throw new DAOException("Errore nel caricamento di tutte le prenotazioni: " + e.getMessage(), e);
        }
        return result;
    }

    @Override
    public List<Booking> findCompletedByStudentAndTutor(int studentId, int tutorId) throws DAOException {
        List<Booking> result = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_COMPLETED_BY_STUDENT_AND_TUTOR)) {
            ps.setInt(1, studentId);
            ps.setInt(2, tutorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapToBooking(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Errore nel caricamento lezioni effettuate: " + e.getMessage(), e);
        }
        return result;
    }

    @Override
    public List<Booking> findUpcomingByStudentAndTutor(int studentId, int tutorId) throws DAOException {
        List<Booking> result = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_UPCOMING_BY_STUDENT_AND_TUTOR)) {
            ps.setInt(1, studentId);
            ps.setInt(2, tutorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapToBooking(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Errore nel caricamento lezioni programmate: " + e.getMessage(), e);
        }
        return result;
    }

    @Override
    public void cancel(int bookingId, int studentId) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection()) {
            conn.setAutoCommit(false);
            executeCancel(conn, bookingId, studentId);
            conn.commit();
            conn.setAutoCommit(true);
            updateInCache(bookingId);
        } catch (SQLException e) {
            throw new DAOException("Errore durante l'annullamento: " + e.getMessage(), e);
        }
    }

    private void executeCancel(Connection conn, int bookingId,
                               int studentId) throws SQLException, DAOException {
        freeSlot(conn, bookingId, studentId);
        cancelBooking(conn, bookingId, studentId);
    }

    private void freeSlot(Connection conn, int bookingId, int studentId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(FREE_SLOT)) {
            ps.setInt(1, bookingId);
            ps.setInt(2, studentId);
            ps.executeUpdate();
        }
    }

    private void cancelBooking(Connection conn, int bookingId,
                               int studentId) throws SQLException, DAOException {
        try (PreparedStatement ps = conn.prepareStatement(CANCEL_BOOKING)) {
            ps.setInt(1, bookingId);
            ps.setInt(2, studentId);
            int rows = ps.executeUpdate();
            if (rows == 0) throw new DAOException("Prenotazione non trovata o non autorizzata.");
        }
    }

    private void updateSlotAvailability(Connection conn, int slotId,
                                        boolean available) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(UPDATE_SLOT_AVAILABILITY)) {
            ps.setBoolean(1, available);
            ps.setInt(2, slotId);
            ps.executeUpdate();
        }
    }

    private Booking mapToBooking(ResultSet rs) throws SQLException {
        Student student = new Student(rs.getInt("s_id"), rs.getString("s_name"),
                rs.getString("s_surname"), rs.getString("s_email"), null);
        Tutor tutor = new Tutor(rs.getInt("t_id"), rs.getString("t_name"),
                rs.getString("t_surname"), rs.getString("t_email"), null, rs.getString("t_bio"));
        Subject subject = new Subject(rs.getInt("sub_id"), rs.getString("sub_name"));
        TimeSlot slot = new TimeSlot(rs.getInt("ts_id"),
                rs.getDate("ts_date").toLocalDate(),
                rs.getTime("start_time").toLocalTime(),
                rs.getTime("end_time").toLocalTime());
        slot.setAvailable(rs.getBoolean("available"));
        Booking booking = new Booking(student, tutor, subject, slot);
        booking.setId(rs.getInt("id"));
        booking.setStatus(BookingStatus.valueOf(rs.getString("status")));
        booking.setMeetLink(rs.getString("meet_link"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) booking.setCreatedAt(createdAt.toLocalDateTime());
        return booking;
    }
}