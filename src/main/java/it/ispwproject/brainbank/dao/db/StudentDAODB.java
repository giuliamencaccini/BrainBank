package it.ispwproject.brainbank.dao.db;

import it.ispwproject.brainbank.dao.ConnectionFactory;
import it.ispwproject.brainbank.dao.StudentDAO;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAODB implements StudentDAO {

    private static final String FIND_BY_ID =
            "SELECT id, name, surname, email FROM user WHERE id = ? AND role = 'STUDENT'";

    private static final String GET_BY_TUTOR =
            "SELECT DISTINCT u.id, u.name, u.surname, u.email " +
                    "FROM user u " +
                    "JOIN booking b ON u.id = b.student_id " +
                    "WHERE b.tutor_id = ? " +
                    "ORDER BY u.name";

    private static final String ADD_FAVOURITE_TUTOR =
            "INSERT IGNORE INTO student_favourite_tutor (student_id, tutor_id) VALUES (?, ?)";

    private static final String REMOVE_FAVOURITE_TUTOR =
            "DELETE FROM student_favourite_tutor WHERE student_id = ? AND tutor_id = ?";

    private static final String IS_FAVOURITE_TUTOR =
            "SELECT COUNT(*) FROM student_favourite_tutor WHERE student_id = ? AND tutor_id = ?";

    @Override
    public Student findById(int id) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapToStudent(rs);
            }
        } catch (SQLException e) {
            throw new DAOException("Errore nel caricamento dello studente: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Student> getByTutor(int tutorId) throws DAOException {
        List<Student> result = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_BY_TUTOR)) {
            ps.setInt(1, tutorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapToStudent(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Errore nel caricamento degli studenti: " + e.getMessage(), e);
        }
        return result;
    }

    @Override
    public void addFavouriteTutor(int studentId, int tutorId) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(ADD_FAVOURITE_TUTOR)) {

            ps.setInt(1, studentId);
            ps.setInt(2, tutorId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DAOException("Errore nell'aggiunta del tutor preferito: " + e.getMessage(), e);
        }
    }

    @Override
    public void removeFavouriteTutor(int studentId, int tutorId) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(REMOVE_FAVOURITE_TUTOR)) {

            ps.setInt(1, studentId);
            ps.setInt(2, tutorId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DAOException("Errore nella rimozione del tutor preferito: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isFavouriteTutor(int studentId, int tutorId) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(IS_FAVOURITE_TUTOR)) {

            ps.setInt(1, studentId);
            ps.setInt(2, tutorId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            throw new DAOException("Errore nel controllo del tutor preferito: " + e.getMessage(), e);
        }
    }

    private Student mapToStudent(ResultSet rs) throws SQLException {
        return new Student(rs.getInt("id"), rs.getString("name"),
                rs.getString("surname"), rs.getString("email"), null);
    }
}