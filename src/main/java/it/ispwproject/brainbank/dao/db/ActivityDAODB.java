package it.ispwproject.brainbank.dao.db;

import it.ispwproject.brainbank.dao.ActivityDAO;
import it.ispwproject.brainbank.dao.ConnectionFactory;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.model.Activity;
import it.ispwproject.brainbank.model.Student;
import it.ispwproject.brainbank.model.Tutor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ActivityDAODB implements ActivityDAO {

    private static final String SAVE =
            "INSERT INTO activity (tutor_id, student_id, description) VALUES (?, ?, ?)";

    private static final String GET_BY_STUDENT_AND_TUTOR =
            "SELECT a.id, a.description, a.completed, a.created_at, " +
                    "       u_t.id t_id, u_t.name t_name, u_t.surname t_surname, " +
                    "       u_s.id s_id, u_s.name s_name, u_s.surname s_surname, u_s.email s_email " +
                    "FROM activity a " +
                    "JOIN user u_t ON a.tutor_id   = u_t.id " +
                    "JOIN user u_s ON a.student_id = u_s.id " +
                    "WHERE a.tutor_id = ? AND a.student_id = ? ORDER BY a.created_at DESC";

    private static final String GET_BY_STUDENT =
            "SELECT a.id, a.description, a.completed, a.created_at, " +
                    "       u_t.id t_id, u_t.name t_name, u_t.surname t_surname, " +
                    "       u_s.id s_id, u_s.name s_name, u_s.surname s_surname, u_s.email s_email " +
                    "FROM activity a " +
                    "JOIN user u_t ON a.tutor_id   = u_t.id " +
                    "JOIN user u_s ON a.student_id = u_s.id " +
                    "WHERE a.student_id = ? ORDER BY a.completed ASC, a.created_at DESC";

    private static final String FIND_BY_ID =
            "SELECT a.id, a.description, a.completed, a.created_at, " +
                    "       u_t.id t_id, u_t.name t_name, u_t.surname t_surname, " +
                    "       u_s.id s_id, u_s.name s_name, u_s.surname s_surname, u_s.email s_email " +
                    "FROM activity a " +
                    "JOIN user u_t ON a.tutor_id   = u_t.id " +
                    "JOIN user u_s ON a.student_id = u_s.id " +
                    "WHERE a.id = ? AND a.student_id = ?";

    private static final String MARK_AS_COMPLETED =
            "UPDATE activity SET completed = TRUE WHERE id = ? AND student_id = ?";

    private static final String DELETE =
            "DELETE FROM activity WHERE id = ? AND tutor_id = ?";

    @Override
    public void delete(int activityId, int tutorId) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE)) {
            ps.setInt(1, activityId);
            ps.setInt(2, tutorId);
            int rows = ps.executeUpdate();
            if (rows == 0) throw new DAOException("Attività non trovata o non autorizzata.");
        } catch (SQLException e) {
            throw new DAOException("Errore eliminazione attività: " + e.getMessage(), e);
        }
    }

    @Override
    public void save(Activity activity) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(SAVE, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, activity.getTutor().getId());
            ps.setInt(2, activity.getStudent().getId());
            ps.setString(3, activity.getDescription());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) activity.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new DAOException("Errore durante il salvataggio: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Activity> getByStudentAndTutor(int tutorId, int studentId) throws DAOException {
        List<Activity> result = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_BY_STUDENT_AND_TUTOR)) {
            ps.setInt(1, tutorId);
            ps.setInt(2, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapToActivity(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Errore nel caricamento: " + e.getMessage(), e);
        }
        return result;
    }

    @Override
    public List<Activity> getByStudent(int studentId) throws DAOException {
        List<Activity> result = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_BY_STUDENT)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapToActivity(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Errore nel caricamento: " + e.getMessage(), e);
        }
        return result;
    }

    @Override
    public void markAsCompleted(int activityId, int studentId) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(MARK_AS_COMPLETED)) {
            ps.setInt(1, activityId);
            ps.setInt(2, studentId);
            int rows = ps.executeUpdate();
            if (rows == 0) throw new DAOException("Attività non trovata o non autorizzata.");
        } catch (SQLException e) {
            throw new DAOException("Errore aggiornamento: " + e.getMessage(), e);
        }
    }

    @Override
    public Activity findById(int activityId, int studentId) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_ID)) {
            ps.setInt(1, activityId);
            ps.setInt(2, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapToActivity(rs);
            }
        } catch (SQLException e) {
            throw new DAOException("Errore nel caricamento attività: " + e.getMessage(), e);
        }
        return null;
    }

    private Activity mapToActivity(ResultSet rs) throws SQLException {
        Tutor tutor = new Tutor(rs.getInt("t_id"), rs.getString("t_name"),
                rs.getString("t_surname"), null, null, null);
        Student student = new Student(rs.getInt("s_id"), rs.getString("s_name"),
                rs.getString("s_surname"), rs.getString("s_email"), null);
        Activity a = new Activity(tutor, student, rs.getString("description"));
        a.setId(rs.getInt("id"));
        a.setCompleted(rs.getBoolean("completed"));
        a.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return a;
    }
}