package it.ispwproject.brainbank.controller.applicativo;

import it.ispwproject.brainbank.bean.TimeSlotBean;
import it.ispwproject.brainbank.dao.BookingDAO;
import it.ispwproject.brainbank.dao.DAOFactory;
import it.ispwproject.brainbank.dao.TimeSlotDAO;
import it.ispwproject.brainbank.exception.AvailabilityException;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.model.Booking;
import it.ispwproject.brainbank.model.TimeSlot;
import it.ispwproject.brainbank.model.Tutor;
import it.ispwproject.brainbank.pattern.singleton.SessionManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AvailabilityController {

    private final TimeSlotDAO timeSlotDAO;
    private final BookingDAO  bookingDAO;

    public AvailabilityController() {
        this.timeSlotDAO = DAOFactory.getTimeSlotDAO();
        this.bookingDAO  = DAOFactory.getBookingDAO();
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

        for (TimeSlot s : timeSlotDAO.getAvailableByTutor(tutor)) {
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
        return buildSlotBeans(timeSlotDAO.getAllByTutor(tutor.getId()), tutor);
    }

    public List<TimeSlotBean> getPastSlots() throws DAOException {
        Tutor tutor = (Tutor) SessionManager.getInstance().getLoggedUser();
        return buildSlotBeans(timeSlotDAO.getPastByTutor(tutor.getId()), tutor);
    }

    public Map<Integer, String> getSubjectBySlot() throws DAOException {
        Tutor tutor = (Tutor) SessionManager.getInstance().getLoggedUser();
        Map<Integer, String> result = new HashMap<>();
        for (Booking b : bookingDAO.findByTutor(tutor.getId())) {
            if (b.getTimeSlot() != null && b.getSubject() != null) {
                result.put(b.getTimeSlot().getId(), b.getSubject().getName());
            }
        }
        return result;
    }

    public void deleteSlot(int slotId) throws DAOException {
        Tutor tutor = (Tutor) SessionManager.getInstance().getLoggedUser();
        timeSlotDAO.delete(slotId, tutor.getId());
    }

    private List<TimeSlotBean> buildSlotBeans(List<TimeSlot> slots, Tutor tutor) throws DAOException {
        Map<Integer, Booking> bookingBySlot = new HashMap<>();
        for (Booking b : bookingDAO.findByTutor(tutor.getId())) {
            if (b.getTimeSlot() != null) {
                bookingBySlot.put(b.getTimeSlot().getId(), b);
            }
        }

        List<TimeSlotBean> result = new ArrayList<>();
        for (TimeSlot slot : slots) {
            TimeSlotBean bean = new TimeSlotBean(slot.getId(), slot.getDate(),
                    slot.getStartTime(), slot.getEndTime(), slot.isAvailable());
            Booking booking = bookingBySlot.get(slot.getId());
            if (booking != null) {
                if (booking.getStudent() != null)
                    bean.setBookedByName(booking.getStudent().getFullName());
                bean.setMeetLink(booking.getMeetLink());
            }
            result.add(bean);
        }
        return result;
    }
}