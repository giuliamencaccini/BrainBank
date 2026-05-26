package it.ispwproject.brainbank.dao.memory;

import it.ispwproject.brainbank.demo.DemoDataStore;
import it.ispwproject.brainbank.dao.ProgressDAO;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.model.Progress;

import java.time.LocalDateTime;

public class ProgressDAOMemory implements ProgressDAO {

    private final DemoDataStore store = DemoDataStore.getInstance();

    @Override
    public void saveOrUpdate(Progress progress) throws DAOException {
        Progress existing = findByStudentAndTutor(
                progress.getTutor().getId(), progress.getStudent().getId());
        if (existing == null) {
            progress.setId(store.nextProgressId());
            progress.setUpdatedAt(LocalDateTime.now());
            store.getProgresses().add(progress);
        } else {
            existing.setNotes(progress.getNotes());
            existing.setUpdatedAt(LocalDateTime.now());
        }
    }

    @Override
    public Progress findByStudentAndTutor(int tutorId, int studentId) throws DAOException {
        return store.getProgresses().stream()
                .filter(p -> p.getTutor() != null && p.getTutor().getId() == tutorId
                        && p.getStudent() != null && p.getStudent().getId() == studentId)
                .findFirst()
                .orElse(null);
    }
}