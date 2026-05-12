package it.ispwproject.brainbank.dao.db;

import it.ispwproject.brainbank.bean.TimeSlotBean;
import it.ispwproject.brainbank.dao.ConnectionFactory;
import it.ispwproject.brainbank.dao.TimeSlotDAO;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.model.TimeSlot;
import it.ispwproject.brainbank.model.Tutor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TimeSlotDAODB implements TimeSlotDAO {

    private static final String GET_AVAILABLE_BY_TUTOR =
            "SELECT id, date, start_time, end_time, available " +
                    "FROM time_slot WHERE tutor_id = ? AND available = TRUE " +
                    "ORDER BY date, start_time";

    private static final String GET_ALL_BY_TUTOR_WITH_STUDENT =
            "SELECT ts.id, ts.date, ts.start_time, ts.end_time, ts.available, " +
                    "       u.name AS booked_by_name, u.surname AS booked_by_surname, b.meet_link " +
                    "FROM time_slot ts " +
                    "LEFT JOIN booking b ON ts.id = b.slot_id AND b.status = 'CONFIRMED' " +
                    "LEFT JOIN user u    ON b.student_id = u.id " +
                    "WHERE ts.tutor_id = ? ORDER BY ts.date, ts.start_time";

    private static final String FIND_BY_ID =
            "SELECT id, date, start_time, end_time, available FROM time_slot WHERE id = ?";

    private static final String SAVE =
            "INSERT INTO time_slot (tutor_id, date, start_time, end_time, available) VALUES (?, ?, ?, ?, TRUE)";

    @Override
    public List<TimeSlot> getAvailableByTutor(Tutor tutor) throws DAOException {
        List<TimeSlot> result = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_AVAILABLE_BY_TUTOR)) {
            ps.setInt(1, tutor.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapToTimeSlot(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Errore nel caricamento degli slot: " + e.getMessage(), e);
        }
        return result;
    }

    @Override
    public List<TimeSlotBean> getAllByTutorWithStudent(int tutorId) throws DAOException {
        List<TimeSlotBean> result = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_ALL_BY_TUTOR_WITH_STUDENT)) {
            ps.setInt(1, tutorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TimeSlotBean bean = new TimeSlotBean(
                            rs.getInt("id"), rs.getDate("date").toLocalDate(),
                            rs.getTime("start_time").toLocalTime(),
                            rs.getTime("end_time").toLocalTime(),
                            rs.getBoolean("available"));
                    String name    = rs.getString("booked_by_name");
                    String surname = rs.getString("booked_by_surname");
                    if (name != null) bean.setBookedByName(name + " " + surname);
                    bean.setMeetLink(rs.getString("meet_link"));
                    result.add(bean);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Errore nel caricamento degli slot: " + e.getMessage(), e);
        }
        return result;
    }

    @Override
    public TimeSlot findById(int id) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapToTimeSlot(rs);
            }
        } catch (SQLException e) {
            throw new DAOException("Errore nel caricamento dello slot: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void save(TimeSlot slot, int tutorId) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(SAVE, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, tutorId);
            ps.setDate(2, Date.valueOf(slot.getDate()));
            ps.setTime(3, Time.valueOf(slot.getStartTime()));
            ps.setTime(4, Time.valueOf(slot.getEndTime()));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) slot.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new DAOException("Errore durante il salvataggio dello slot: " + e.getMessage(), e);
        }
    }

    private TimeSlot mapToTimeSlot(ResultSet rs) throws SQLException {
        return new TimeSlot(rs.getInt("id"), rs.getDate("date").toLocalDate(),
                rs.getTime("start_time").toLocalTime(), rs.getTime("end_time").toLocalTime());
    }
}