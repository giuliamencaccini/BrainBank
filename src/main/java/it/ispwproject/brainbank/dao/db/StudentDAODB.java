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
                    "WHERE b.tutor_id = ? AND b.status = 'CONFIRMED' " +
                    "ORDER BY u.name";

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

    private Student mapToStudent(ResultSet rs) throws SQLException {
        return new Student(rs.getInt("id"), rs.getString("name"),
                rs.getString("surname"), rs.getString("email"), null);
    }
}