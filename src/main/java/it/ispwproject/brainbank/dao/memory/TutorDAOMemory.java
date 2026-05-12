package it.ispwproject.brainbank.dao.memory;

import it.ispwproject.brainbank.controller.demo.DemoDataStore;
import it.ispwproject.brainbank.dao.TutorDAO;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.model.Subject;
import it.ispwproject.brainbank.model.Tutor;

import java.util.List;

public class TutorDAOMemory implements TutorDAO {

    private final DemoDataStore store = DemoDataStore.getInstance();

    @Override
    public List<Tutor> getBySubject(Subject subject) throws DAOException {
        // In demo tutti i tutor insegnano tutte le materie
        return store.getUsers().stream()
                .filter(u -> u instanceof Tutor)
                .map(u -> (Tutor) u)
                .toList();
    }

    @Override
    public Tutor findById(int id) throws DAOException {
        return store.getUsers().stream()
                .filter(u -> u instanceof Tutor && u.getId() == id)
                .map(u -> (Tutor) u)
                .findFirst()
                .orElse(null);
    }
}