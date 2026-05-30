package it.ispwproject.brainbank.dao.memory;

import it.ispwproject.brainbank.demo.DemoDataStore;
import it.ispwproject.brainbank.dao.TimeSlotDAO;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.model.TimeSlot;
import it.ispwproject.brainbank.model.Tutor;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    public List<TimeSlot> getAllByTutor(int tutorId) throws DAOException {
        return store.getTimeSlots().stream()
                .filter(s -> s.getTutor() != null
                        && s.getTutor().getId() == tutorId
                        && !s.getDate().isBefore(LocalDate.now()))
                .toList();
    }

    @Override
    public List<TimeSlot> getPastByTutor(int tutorId) throws DAOException {
        return store.getTimeSlots().stream()
                .filter(s -> s.getTutor() != null
                        && s.getTutor().getId() == tutorId
                        && s.getDate().isBefore(LocalDate.now()))
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
        synchronized (store.getTimeSlots()) {
            TimeSlot slot = store.getTimeSlots().stream()
                    .filter(s -> s.getId() == slotId && s.isAvailable()
                            && (s.getReservedUntil() == null ||
                            s.getReservedUntil().isBefore(LocalDateTime.now())))
                    .findFirst()
                    .orElse(null);
            if (slot == null) return false;
            slot.setReservedUntil(LocalDateTime.now().plusMinutes(minutes));
            return true;
        }
    }

    @Override
    public void releaseSlot(int slotId) throws DAOException {
        store.getTimeSlots().stream()
                .filter(s -> s.getId() == slotId)
                .findFirst()
                .ifPresent(s -> s.setReservedUntil(null));
    }

    @Override
    public void delete(int slotId, int tutorId) throws DAOException {
        boolean removed = store.getTimeSlots().removeIf(s ->
                s.getId() == slotId
                        && s.getTutor() != null
                        && s.getTutor().getId() == tutorId
                        && s.isAvailable());
        if (!removed) throw new DAOException("Slot non trovato o già prenotato.");
    }
}