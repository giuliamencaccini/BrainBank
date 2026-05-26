package it.ispwproject.brainbank.dao.memory;

import it.ispwproject.brainbank.demo.DemoDataStore;
import it.ispwproject.brainbank.dao.TutorDAO;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.model.Subject;
import it.ispwproject.brainbank.model.Tutor;

import java.util.List;
import java.util.Map;

public class TutorDAOMemory implements TutorDAO {

    private final DemoDataStore store = DemoDataStore.getInstance();

    @Override
    public List<Tutor> getBySubject(Subject subject) throws DAOException {
        List<Integer> tutorIds = store.getSubjectsByTutor().entrySet().stream()
                .filter(e -> e.getValue().contains(subject.getId()))
                .map(Map.Entry::getKey)
                .toList();

        return store.getUsers().stream()
                .filter(Tutor.class::isInstance)
                .map(Tutor.class::cast)
                .filter(t -> tutorIds.contains(t.getId()))
                .toList();
    }

    @Override
    public Tutor findById(int id) throws DAOException {
        return store.getUsers().stream()
                .filter(Tutor.class::isInstance)
                .map(Tutor.class::cast)
                .filter(tutor -> tutor.getId() == id)
                .findFirst()
                .orElse(null);
    }
}