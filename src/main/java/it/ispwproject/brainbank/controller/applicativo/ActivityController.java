package it.ispwproject.brainbank.controller.applicativo;

import it.ispwproject.brainbank.bean.*;
import it.ispwproject.brainbank.dao.*;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.model.*;
import it.ispwproject.brainbank.util.logger.AppLogger;
import it.ispwproject.brainbank.util.singleton.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class ActivityController {

    private final StudentDAO  studentDAO;
    private final ActivityDAO activityDAO;
    private final ProgressDAO progressDAO;
    private final BookingDAO  bookingDAO;

    public ActivityController() {
        this.studentDAO  = DAOFactory.getStudentDAO();
        this.activityDAO = DAOFactory.getActivityDAO();
        this.progressDAO = DAOFactory.getProgressDAO();
        this.bookingDAO  = DAOFactory.getBookingDAO();
    }

    public List<StudentBean> getStudents() throws DAOException {
        Tutor tutor = (Tutor) SessionManager.getInstance().getLoggedUser();
        List<StudentBean> result = new ArrayList<>();
        for (Student student : studentDAO.getByTutor(tutor.getId())) {
            result.add(new StudentBean(student.getId(), student.getName(),
                    student.getSurname(), student.getEmail()));
        }
        return result;
    }

    public List<BookingResponseBean> getCompletedLessons(int studentId) throws DAOException {
        Tutor tutor = (Tutor) SessionManager.getInstance().getLoggedUser();
        return buildBookingResponseList(
                bookingDAO.findCompletedByStudentAndTutor(studentId, tutor.getId()));
    }

    public List<BookingResponseBean> getUpcomingLessons(int studentId) throws DAOException {
        Tutor tutor = (Tutor) SessionManager.getInstance().getLoggedUser();
        return buildBookingResponseList(
                bookingDAO.findUpcomingByStudentAndTutor(studentId, tutor.getId()));
    }

    private List<BookingResponseBean> buildBookingResponseList(List<Booking> bookings) {
        List<BookingResponseBean> result = new ArrayList<>();
        for (Booking booking : bookings) {
            Tutor    tutor   = booking.getTutor();
            Subject  subject = booking.getSubject();
            TimeSlot slot    = booking.getTimeSlot();
            if (tutor == null || subject == null || slot == null) continue;
            result.add(new BookingResponseBean(
                    booking.getId(), booking.getStatus().name(), booking.getMeetLink(),
                    new TutorBean(tutor.getId(), tutor.getName(), tutor.getSurname(), null, null, false),
                    new SubjectBean(subject.getId(), subject.getName()),
                    new TimeSlotBean(slot.getId(), slot.getDate(),
                            slot.getStartTime(), slot.getEndTime(), slot.isAvailable())));
        }
        return result;
    }

    public void assignActivity(ActivityBean bean) throws DAOException {
        Tutor   tutor   = (Tutor) SessionManager.getInstance().getLoggedUser();
        Student student = studentDAO.findById(bean.getStudent().getId());
        if (student == null) throw new DAOException("Studente non trovato.");
        Activity activity = new Activity(tutor, student, bean.getDescription());
        activityDAO.save(activity);
        bean.setId(activity.getId());
        try {
            NotificationController.sendNewActivity(
                    student.getEmail(),
                    student.getFullName(),
                    tutor.getFullName(),
                    activity.getDescription()
            );
        } catch (it.ispwproject.brainbank.exception.NotificationException e) {
            AppLogger.logWarning("Notifica attività non inviata: " + e.getMessage());
        }
    }

    public List<ActivityBean> getActivities(int studentId) throws DAOException {
        Tutor tutor = (Tutor) SessionManager.getInstance().getLoggedUser();
        List<ActivityBean> result = new ArrayList<>();
        TutorBean tutorBean = new TutorBean(tutor.getId(), tutor.getName(),
                tutor.getSurname(), null,null, false);
        for (Activity a : activityDAO.getByStudentAndTutor(tutor.getId(), studentId)) {
            StudentBean studentBean = new StudentBean(
                    a.getStudent().getId(), a.getStudent().getName(),
                    a.getStudent().getSurname(), a.getStudent().getEmail());
            result.add(new ActivityBean(a.getId(), studentBean, tutorBean,
                    a.getDescription(), a.isCompleted(), a.getCreatedAt()));
        }
        return result;
    }

    public List<ActivityBean> getMyActivities() throws DAOException {
        Student student = (Student) SessionManager.getInstance().getLoggedUser();
        List<ActivityBean> result = new ArrayList<>();
        for (Activity a : activityDAO.getByStudent(student.getId())) {
            StudentBean studentBean = new StudentBean(
                    a.getStudent().getId(), a.getStudent().getName(),
                    a.getStudent().getSurname(), a.getStudent().getEmail());
            TutorBean tutorBean = null;
            if (a.getTutor() != null) {
                tutorBean = new TutorBean(a.getTutor().getId(), a.getTutor().getName(), a.getTutor().getSurname(), null, null, false);
            }
            result.add(new ActivityBean(a.getId(), studentBean, tutorBean,
                    a.getDescription(), a.isCompleted(), a.getCreatedAt()));
        }
        return result;
    }

    public void markActivityCompleted(int activityId) throws DAOException {
        Student student = (Student) SessionManager.getInstance().getLoggedUser();
        Activity activity = activityDAO.findById(activityId, student.getId());
        if (activity == null) throw new DAOException("Attività non trovata o non autorizzata.");
        activity.complete();
        activityDAO.markAsCompleted(activityId, student.getId());
    }

    public void deleteActivity(int activityId) throws DAOException {
        Tutor tutor = (Tutor) SessionManager.getInstance().getLoggedUser();
        activityDAO.delete(activityId, tutor.getId());
    }

    public void updateProgress(ProgressBean bean) throws DAOException {
        Tutor   tutor   = (Tutor) SessionManager.getInstance().getLoggedUser();
        Student student = studentDAO.findById(bean.getStudent().getId());
        if (student == null) throw new DAOException("Studente non trovato.");
        Progress progress = progressDAO.findByStudentAndTutor(tutor.getId(), student.getId());
        if (progress == null) {
            progress = new Progress(tutor, student, bean.getNotes());
        } else {
            progress.updateNotes(bean.getNotes());
        }
        progressDAO.saveOrUpdate(progress);
    }

    public ProgressBean getProgress(int studentId) throws DAOException {
        Tutor    tutor    = (Tutor) SessionManager.getInstance().getLoggedUser();
        Progress progress = progressDAO.findByStudentAndTutor(tutor.getId(), studentId);
        if (progress == null) return null;
        StudentBean studentBean = new StudentBean(
                progress.getStudent().getId(), progress.getStudent().getName(),
                progress.getStudent().getSurname(), progress.getStudent().getEmail());
        return new ProgressBean(studentBean, progress.getNotes(), progress.getUpdatedAt());
    }
}