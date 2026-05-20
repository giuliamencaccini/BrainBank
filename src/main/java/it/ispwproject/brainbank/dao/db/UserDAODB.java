package it.ispwproject.brainbank.dao.db;

import it.ispwproject.brainbank.dao.ConnectionFactory;
import it.ispwproject.brainbank.dao.UserDAO;
import it.ispwproject.brainbank.enumerator.Role;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.model.Admin;
import it.ispwproject.brainbank.model.Student;
import it.ispwproject.brainbank.model.Tutor;
import it.ispwproject.brainbank.model.User;

import java.sql.*;

public class UserDAODB implements UserDAO {

    private static final String FIND_BY_EMAIL =
            "SELECT u.id, u.name, u.surname, u.email, u.role, td.bio " +
                    "FROM user u " +
                    "LEFT JOIN tutor_detail td ON u.id = td.user_id " +
                    "WHERE u.email = ?";

    private static final String UPDATE_EMAIL =
            "UPDATE user SET email = ? WHERE id = ?";

    @Override
    public void updateEmail(int id, String newEmail) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_EMAIL)) {
            ps.setString(1, newEmail);
            ps.setInt(2, id);
            int rows = ps.executeUpdate();
            if (rows == 0) throw new DAOException("Utente non trovato (ID: " + id + ")");
        } catch (SQLException e) {
            throw new DAOException("Errore aggiornamento email: " + e.getMessage(), e);
        }
    }

    @Override
    public User findByEmail(String email) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_EMAIL)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new DAOException("Utente non trovato: " + email);
                int    id      = rs.getInt("id");
                String name    = rs.getString("name");
                String surname = rs.getString("surname");
                Role   role    = Role.valueOf(rs.getString("role").toUpperCase());
                String bio     = rs.getString("bio");
                return buildUser(id, name, surname, email, role, bio);
            }
        } catch (SQLException e) {
            throw new DAOException("Errore caricamento utente: " + e.getMessage(), e);
        }
    }

    private User buildUser(int id, String name, String surname,
                           String email, Role role, String bio) throws DAOException {
        return switch (role) {
            case STUDENT -> new Student(id, name, surname, email, null);
            case TUTOR   -> new Tutor(id, name, surname, email, null, bio);
            case ADMIN -> new Admin(id, name, surname, email, null);
            default      -> throw new DAOException("Ruolo non riconosciuto: " + role);
        };
    }
}