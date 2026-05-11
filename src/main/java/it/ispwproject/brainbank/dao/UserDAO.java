package it.ispwproject.brainbank.dao;

import it.ispwproject.brainbank.enumerator.Role;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.model.Student;
import it.ispwproject.brainbank.model.Tutor;
import it.ispwproject.brainbank.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    private static final String FIND_BY_EMAIL =
            "SELECT u.id, u.name, u.surname, u.email, u.role, td.bio " +
                    "FROM user u " +
                    "LEFT JOIN tutor_detail td ON u.id = td.user_id " +
                    "WHERE u.email = ?";

    public UserDAO() {}

    public static User findByEmail(String email) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_EMAIL)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new DAOException("Utente non trovato: " + email);
                }

                int    id      = rs.getInt("id");
                String name    = rs.getString("name");
                String surname = rs.getString("surname");
                Role   role    = Role.valueOf(rs.getString("role").toUpperCase());
                String bio     = rs.getString("bio");

                return buildUser(id, name, surname, email, role, bio);
            }

        } catch (SQLException e) {
            throw new DAOException("Errore nel caricamento utente: " + e.getMessage(), e);
        }
    }

    private static User buildUser(int id, String name, String surname,
                                  String email, Role role, String bio) throws DAOException {
        return switch (role) {
            case STUDENT -> new Student(id, name, surname, email, null);
            case TUTOR   -> new Tutor(id, name, surname, email, null, bio);
            default      -> throw new DAOException("Ruolo non riconosciuto: " + role);
        };
    }
}