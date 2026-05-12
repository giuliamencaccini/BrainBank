package it.ispwproject.brainbank.dao.memory;

import it.ispwproject.brainbank.controller.demo.DemoDataStore;
import it.ispwproject.brainbank.dao.StudentDAO;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.model.Student;

import java.util.List;

public class StudentDAOMemory implements StudentDAO {

    private final DemoDataStore store = DemoDataStore.getInstance();

    @Override
    public Student findById(int id) throws DAOException {
        return store.getUsers().stream()
                .filter(u -> u instanceof Student && u.getId() == id)
                .map(u -> (Student) u)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Student> getByTutor(int tutorId) throws DAOException {
        List<Integer> studentIds = store.getBookings().stream()
                .filter(b -> b.getTutor() != null && b.getTutor().getId() == tutorId)
                .map(b -> b.getStudent().getId())
                .distinct()
                .toList();

        return store.getUsers().stream()
                .filter(u -> u instanceof Student && studentIds.contains(u.getId()))
                .map(u -> (Student) u)
                .toList();
    }
}