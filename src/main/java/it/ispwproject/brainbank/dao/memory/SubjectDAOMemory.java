package it.ispwproject.brainbank.dao.memory;

import it.ispwproject.brainbank.demo.DemoDataStore;
import it.ispwproject.brainbank.dao.SubjectDAO;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.model.Subject;

import java.util.List;

public class SubjectDAOMemory implements SubjectDAO {

    private final DemoDataStore store = DemoDataStore.getInstance();

    @Override
    public List<Subject> getAll() throws DAOException {
        return List.copyOf(store.getSubjects());
    }

    @Override
    public Subject findById(int id) throws DAOException {
        return store.getSubjects().stream()
                .filter(s -> s.getId() == id)
                .findFirst()
                .orElse(null);
    }
}