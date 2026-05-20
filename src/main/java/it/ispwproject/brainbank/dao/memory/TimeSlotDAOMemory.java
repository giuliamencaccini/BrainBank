package it.ispwproject.brainbank.dao.memory;

import it.ispwproject.brainbank.bean.TimeSlotBean;
import it.ispwproject.brainbank.controller.demo.DemoDataStore;
import it.ispwproject.brainbank.dao.TimeSlotDAO;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.model.TimeSlot;
import it.ispwproject.brainbank.model.Tutor;

import java.util.List;

public class TimeSlotDAOMemory implements TimeSlotDAO {

    private final DemoDataStore store = DemoDataStore.getInstance();

    @Override
    public List<TimeSlot> getAvailableByTutor(Tutor tutor) throws DAOException {
        return store.getTimeSlots().stream()
                .filter(s -> s.getTutor() != null
                        && s.getTutor().getId() == tutor.getId()
                        && s.isAvailable())
                .toList();
    }

    @Override
    public List<TimeSlotBean> getAllByTutorWithStudent(int tutorId) throws DAOException {
        return store.getTimeSlots().stream()
                .filter(s -> s.getTutor() != null && s.getTutor().getId() == tutorId)
                .map(s -> {
                    TimeSlotBean bean = new TimeSlotBean(s.getId(), s.getDate(),
                            s.getStartTime(), s.getEndTime(), s.isAvailable());
                    if (!s.isAvailable()) {
                        store.getBookings().stream()
                                .filter(b -> b.getTimeSlot() != null
                                        && b.getTimeSlot().getId() == s.getId())
                                .findFirst()
                                .ifPresent(b -> {
                                    if (b.getStudent() != null)
                                        bean.setBookedByName(b.getStudent().getFullName());
                                    bean.setMeetLink(b.getMeetLink());
                                });
                    }
                    return bean;
                })
                .toList();
    }

    @Override
    public TimeSlot findById(int id) throws DAOException {
        return store.getTimeSlots().stream()
                .filter(s -> s.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void save(TimeSlot slot, int tutorId) throws DAOException {
        slot.setId(store.nextSlotId());
        store.getUsers().stream()
                .filter(u -> u instanceof Tutor && u.getId() == tutorId)
                .map(u -> (Tutor) u)
                .findFirst()
                .ifPresent(slot::setTutor);
        store.getTimeSlots().add(slot);
    }

    @Override
    public boolean reserveSlot(int slotId, int minutes) throws DAOException {
        TimeSlot slot = store.getTimeSlots().stream()
                .filter(s -> s.getId() == slotId && s.isAvailable())
                .findFirst()
                .orElse(null);
        if (slot == null) return false;
        slot.setReservedUntil(java.time.LocalDateTime.now().plusMinutes(minutes));
        return true;
    }

    @Override
    public void releaseSlot(int slotId) throws DAOException {
        store.getTimeSlots().stream()
                .filter(s -> s.getId() == slotId)
                .findFirst()
                .ifPresent(s -> s.setReservedUntil(null));
    }
}