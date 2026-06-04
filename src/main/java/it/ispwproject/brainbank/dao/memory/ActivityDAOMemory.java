package it.ispwproject.brainbank.dao.memory;

import it.ispwproject.brainbank.demo.DemoDataStore;
import it.ispwproject.brainbank.dao.ActivityDAO;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.model.Activity;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

public class ActivityDAOMemory implements ActivityDAO {

    private final DemoDataStore store = DemoDataStore.getInstance();

    @Override
    public void save(Activity activity) throws DAOException {
        activity.setId(store.nextActivityId());
        activity.setCreatedAt(LocalDateTime.now(ZoneId.systemDefault()));
        store.getActivities().add(activity);
    }

    @Override
    public List<Activity> getByStudentAndTutor(int tutorId, int studentId) throws DAOException {
        return store.getActivities().stream()
                .filter(a -> a.getTutor() != null && a.getTutor().getId() == tutorId
                        && a.getStudent() != null && a.getStudent().getId() == studentId)
                .toList();
    }

    @Override
    public List<Activity> getByStudent(int studentId) throws DAOException {
        return store.getActivities().stream()
                .filter(a -> a.getStudent() != null && a.getStudent().getId() == studentId)
                .toList();
    }

    @Override
    public void markAsCompleted(int activityId, int studentId) throws DAOException {
        store.getActivities().stream()
                .filter(a -> a.getId() == activityId
                        && a.getStudent() != null && a.getStudent().getId() == studentId)
                .findFirst()
                .ifPresent(a -> a.setCompleted(true));
    }

    @Override
    public Activity findById(int activityId, int studentId) throws DAOException {
        return store.getActivities().stream()
                .filter(a -> a.getId() == activityId
                        && a.getStudent() != null
                        && a.getStudent().getId() == studentId)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void delete(int activityId, int tutorId) throws DAOException {
        store.getActivities().removeIf(a -> a.getId() == activityId
                && a.getTutor() != null && a.getTutor().getId() == tutorId);
    }
}