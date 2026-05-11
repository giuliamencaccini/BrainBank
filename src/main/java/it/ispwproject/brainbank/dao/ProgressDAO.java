package it.ispwproject.brainbank.dao;

import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.model.Progress;
import it.ispwproject.brainbank.model.Student;
import it.ispwproject.brainbank.model.Tutor;

import java.sql.*;

public class ProgressDAO {

    private static final String SAVE =
            "INSERT INTO progress (tutor_id, student_id, notes) VALUES (?, ?, ?)";

    private static final String UPDATE =
            "UPDATE progress SET notes = ?, updated_at = CURRENT_TIMESTAMP " +
                    "WHERE tutor_id = ? AND student_id = ?";

    private static final String FIND_BY_STUDENT_AND_TUTOR =
            "SELECT p.id, p.notes, p.updated_at, " +
                    "       u_t.id t_id, u_t.name t_name, u_t.surname t_surname, " +
                    "       u_s.id s_id, u_s.name s_name, u_s.surname s_surname, u_s.email s_email " +
                    "FROM progress p " +
                    "JOIN user u_t ON p.tutor_id   = u_t.id " +
                    "JOIN user u_s ON p.student_id = u_s.id " +
                    "WHERE p.tutor_id = ? AND p.student_id = ?";

    public ProgressDAO() {}

    public void saveOrUpdate(Progress progress) throws DAOException {
        Progress existing = findByStudentAndTutor(
                progress.getTutor().getId(), progress.getStudent().getId());
        if (existing == null) {
            save(progress);
        } else {
            update(progress);
        }
    }

    private void save(Progress progress) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(SAVE, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, progress.getTutor().getId());
            ps.setInt(2, progress.getStudent().getId());
            ps.setString(3, progress.getNotes());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) progress.setId(keys.getInt(1));
            }

        } catch (SQLException e) {
            throw new DAOException("Errore durante il salvataggio del progresso: " + e.getMessage(), e);
        }
    }

    private void update(Progress progress) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE)) {

            ps.setString(1, progress.getNotes());
            ps.setInt(2, progress.getTutor().getId());
            ps.setInt(3, progress.getStudent().getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DAOException("Errore durante l'aggiornamento del progresso: " + e.getMessage(), e);
        }
    }

    public Progress findByStudentAndTutor(int tutorId, int studentId) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_STUDENT_AND_TUTOR)) {

            ps.setInt(1, tutorId);
            ps.setInt(2, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Tutor tutor = new Tutor(
                            rs.getInt("t_id"), rs.getString("t_name"),
                            rs.getString("t_surname"), null, null, null);

                    Student student = new Student(
                            rs.getInt("s_id"), rs.getString("s_name"),
                            rs.getString("s_surname"), rs.getString("s_email"), null);

                    Progress progress = new Progress(tutor, student, rs.getString("notes"));
                    progress.setId(rs.getInt("id"));
                    progress.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                    return progress;
                }
            }

        } catch (SQLException e) {
            throw new DAOException("Errore nel caricamento del progresso: " + e.getMessage(), e);
        }

        return null;
    }
}