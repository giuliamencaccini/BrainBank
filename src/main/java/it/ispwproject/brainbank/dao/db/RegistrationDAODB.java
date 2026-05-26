package it.ispwproject.brainbank.dao.db;

import it.ispwproject.brainbank.dao.ConnectionFactory;
import it.ispwproject.brainbank.dao.RegistrationDAO;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.model.Tutor;
import it.ispwproject.brainbank.model.User;

import it.ispwproject.brainbank.util.logger.AppLogger;

import java.sql.*;
import java.util.List;

public class RegistrationDAODB implements RegistrationDAO {

    private static final String INSERT_USER =
            "INSERT INTO user (name, surname, email, password, role) VALUES (?, ?, ?, ?, ?)";

    private static final String INSERT_TUTOR_DETAIL =
            "INSERT INTO tutor_detail (user_id, bio) VALUES (?, ?)";

    private static final String INSERT_TUTOR_SUBJECT =
            "INSERT INTO tutor_subject (tutor_id, subject_id) VALUES (?, ?)";

    private static final String CHECK_EMAIL =
            "SELECT COUNT(*) FROM user WHERE email = ?";

    @Override
    public boolean emailExists(String email) throws DAOException {
        try { ConnectionFactory.clearRole(); }
        catch (SQLException e) { AppLogger.logWarning("clearRole fallito: " + e.getMessage()); }

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(CHECK_EMAIL)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            throw new DAOException(
                    "Errore verifica email: " + e.getMessage(),
                    e
            );
        }

        return false;
    }

    @Override
    public void save(User user, String bio, List<Integer> subjectIds) throws DAOException {
        try { ConnectionFactory.clearRole(); }
        catch (SQLException e) { AppLogger.logWarning("clearRole fallito: " + e.getMessage()); }

        try (Connection conn = ConnectionFactory.getConnection()) {

            executeSaveTransaction(conn, user, bio, subjectIds);

        } catch (SQLException e) {

            throw new DAOException(
                    "Errore connessione: " + e.getMessage(),
                    e
            );
        }
    }

    private void executeSaveTransaction(Connection conn,
                                        User user,
                                        String bio,
                                        List<Integer> subjectIds)
            throws SQLException, DAOException {

        conn.setAutoCommit(false);

        try {

            int userId = insertUser(conn, user);
            user.setId(userId);

            if (user instanceof Tutor) {

                insertTutorDetail(conn, userId, bio);

                if (subjectIds != null && !subjectIds.isEmpty()) {
                    insertTutorSubjects(conn, userId, subjectIds);
                }
            }

            conn.commit();

        } catch (SQLException e) {

            conn.rollback();

            throw new DAOException(
                    "Errore registrazione: " + e.getMessage(),
                    e
            );

        } finally {

            conn.setAutoCommit(true);
        }
    }

    private int insertUser(Connection conn,
                           User user) throws SQLException {

        try (PreparedStatement ps = conn.prepareStatement(
                INSERT_USER,
                Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getSurname());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPassword());
            ps.setString(5, user.getRole().name());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {

                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }

        throw new SQLException("ID utente non generato.");
    }

    private void insertTutorDetail(Connection conn,
                                   int tutorId,
                                   String bio) throws SQLException {

        try (PreparedStatement ps =
                     conn.prepareStatement(INSERT_TUTOR_DETAIL)) {

            ps.setInt(1, tutorId);
            ps.setString(2, bio);

            ps.executeUpdate();
        }
    }

    private void insertTutorSubjects(Connection conn,
                                     int tutorId,
                                     List<Integer> subjectIds)
            throws SQLException {

        try (PreparedStatement ps =
                     conn.prepareStatement(INSERT_TUTOR_SUBJECT)) {

            ps.setInt(1, tutorId);

            for (int subjectId : subjectIds) {
                ps.setInt(2, subjectId);
                ps.addBatch();
            }

            ps.executeBatch();
        }
    }
}