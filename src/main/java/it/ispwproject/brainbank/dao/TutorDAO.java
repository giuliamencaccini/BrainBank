package it.ispwproject.brainbank.dao;

import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.model.Subject;
import it.ispwproject.brainbank.model.Tutor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TutorDAO {

    private static final String GET_BY_SUBJECT =
            "SELECT u.id, u.name, u.surname, u.email, td.bio " +
                    "FROM user u " +
                    "JOIN tutor_detail td ON u.id = td.user_id " +
                    "JOIN tutor_subject ts ON u.id = ts.tutor_id " +
                    "WHERE ts.subject_id = ?";

    private static final String FIND_BY_ID =
            "SELECT u.id, u.name, u.surname, u.email, td.bio " +
                    "FROM user u " +
                    "JOIN tutor_detail td ON u.id = td.user_id " +
                    "WHERE u.id = ?";

    public TutorDAO() {}

    public List<Tutor> getBySubject(Subject subject) throws DAOException {
        List<Tutor> result = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_BY_SUBJECT)) {

            ps.setInt(1, subject.getId());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new Tutor(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("surname"),
                            rs.getString("email"),
                            null,
                            rs.getString("bio")
                    ));
                }
            }

        } catch (SQLException e) {
            throw new DAOException("Errore nel caricamento dei tutor: " + e.getMessage(), e);
        }

        return result;
    }

    public Tutor findById(int id) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_ID)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Tutor(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("surname"),
                            rs.getString("email"),
                            null,
                            rs.getString("bio")
                    );
                }
            }

        } catch (SQLException e) {
            throw new DAOException("Errore nel caricamento del tutor: " + e.getMessage(), e);
        }

        return null;
    }
}