package it.ispwproject.brainbank.controller.applicativo;

import it.ispwproject.brainbank.bean.TimeSlotBean;
import it.ispwproject.brainbank.dao.DAOFactory;
import it.ispwproject.brainbank.dao.TimeSlotDAO;
import it.ispwproject.brainbank.exception.AvailabilityException;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.model.TimeSlot;
import it.ispwproject.brainbank.model.Tutor;
import it.ispwproject.brainbank.util.singleton.SessionManager;

import java.time.LocalDate;
import java.util.List;

public class AvailabilityController {

    private final TimeSlotDAO timeSlotDAO;

    public AvailabilityController() {
        this.timeSlotDAO = DAOFactory.getTimeSlotDAO();
    }

    public void addSlot(TimeSlotBean slotBean) throws DAOException, AvailabilityException {
        if (slotBean.getDate().isBefore(LocalDate.now())) {
            throw new AvailabilityException("Non puoi aggiungere slot nel passato.");
        }
        if (!slotBean.getStartTime().isBefore(slotBean.getEndTime())) {
            throw new AvailabilityException("L'ora di inizio deve essere precedente all'ora di fine.");
        }

        Tutor tutor = (Tutor) SessionManager.getInstance().getLoggedUser();
        TimeSlot newSlot = new TimeSlot(0, tutor, slotBean.getDate(),
                slotBean.getStartTime(), slotBean.getEndTime());

        List<TimeSlot> existing = timeSlotDAO.getAvailableByTutor(tutor);
        for (TimeSlot s : existing) {
            if (newSlot.overlaps(s)) {
                throw new AvailabilityException(
                        "Lo slot si sovrappone con uno già esistente: " +
                                s.getDate() + " " + s.getStartTime() + " – " + s.getEndTime());
            }
        }

        timeSlotDAO.save(newSlot, tutor.getId());
        slotBean.setId(newSlot.getId());
    }

    public List<TimeSlotBean> getSlots() throws DAOException {
        Tutor tutor = (Tutor) SessionManager.getInstance().getLoggedUser();
        return timeSlotDAO.getAllByTutorWithStudent(tutor.getId());
    }
}
