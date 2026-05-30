package it.ispwproject.brainbank.dao.db;

import it.ispwproject.brainbank.dao.ConnectionFactory;
import it.ispwproject.brainbank.dao.SubjectDAO;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.model.Subject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SubjectDAODB implements SubjectDAO {

    private static final String GET_ALL  =
            "SELECT DISTINCT s.id, " +
            "s.name FROM subject s " +
            "JOIN tutor_subject ts ON s.id = ts.subject_id";
    private static final String FIND_BY_ID = "SELECT id, name FROM subject WHERE id = ?";

    @Override
    public List<Subject> getAll() throws DAOException {
        List<Subject> result = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(new Subject(rs.getInt("id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            throw new DAOException("Errore nel caricamento delle materie: " + e.getMessage(), e);
        }
        return result;
    }

    @Override
    public Subject findById(int id) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return new Subject(rs.getInt("id"), rs.getString("name"));
            }
        } catch (SQLException e) {
            throw new DAOException("Errore nel caricamento della materia: " + e.getMessage(), e);
        }
        return null;
    }
}