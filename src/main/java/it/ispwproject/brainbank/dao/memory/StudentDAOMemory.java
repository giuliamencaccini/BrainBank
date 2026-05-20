package it.ispwproject.brainbank.dao.memory;

import it.ispwproject.brainbank.controller.demo.DemoDataStore;
import it.ispwproject.brainbank.dao.StudentDAO;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.model.Student;
import java.util.ArrayList;
import java.util.Map;

import java.util.List;

public class StudentDAOMemory implements StudentDAO {

    private final DemoDataStore store = DemoDataStore.getInstance();
    private final Map<Integer, List<Integer>> favouritesByStudent =
            DemoDataStore.getInstance().getFavouritesByStudent();

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
    @Override
    public void addFavouriteTutor(int studentId, int tutorId) throws DAOException {
        favouritesByStudent
                .computeIfAbsent(studentId, id -> new ArrayList<>());

        if (!favouritesByStudent.get(studentId).contains(tutorId)) {
            favouritesByStudent.get(studentId).add(tutorId);
        }
    }

    @Override
    public void removeFavouriteTutor(int studentId, int tutorId) throws DAOException {
        List<Integer> favourites = favouritesByStudent.get(studentId);

        if (favourites != null) {
            favourites.remove(Integer.valueOf(tutorId));
        }
    }

    @Override
    public boolean isFavouriteTutor(int studentId, int tutorId) throws DAOException {
        List<Integer> favourites = favouritesByStudent.get(studentId);

        return favourites != null && favourites.contains(tutorId);
    }

}