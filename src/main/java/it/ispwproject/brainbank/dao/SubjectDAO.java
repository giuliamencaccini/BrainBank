package it.ispwproject.brainbank.dao;

import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.model.Subject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SubjectDAO {

    private static final String GET_ALL =
            "SELECT id, name FROM subject";

    private static final String FIND_BY_ID =
            "SELECT id, name FROM subject WHERE id = ?";

    public SubjectDAO() {}

    public List<Subject> getAll() throws DAOException {
        List<Subject> result = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.add(new Subject(rs.getInt("id"), rs.getString("name")));
            }

        } catch (SQLException e) {
            throw new DAOException("Errore nel caricamento delle materie: " + e.getMessage());
        }

        return result;
    }

    public Subject findById(int id) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_ID)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Subject(rs.getInt("id"), rs.getString("name"));
                }
            }

        } catch (SQLException e) {
            throw new DAOException("Errore nel caricamento della materia: " + e.getMessage());
        }

        return null;
    }
}