package it.ispwproject.brainbank.dao;

import it.ispwproject.brainbank.dao.db.*;
import it.ispwproject.brainbank.dao.file.BookingDAOFile;
import it.ispwproject.brainbank.dao.memory.*;

public class DAOFactory {

    public static final String DATABASE = "database";
    public static final String FILE     = "file";
    public static final String MEMORY   = "memory";

    private static String persistence = DATABASE;

    private DAOFactory() {}

    public static void setPersistence(String mode) {
        if (mode != null && !mode.isBlank()) {
            persistence = mode;
        }
    }

    public static String getPersistence() {
        return persistence;
    }

    public static LoginDAO getLoginDAO() {
        if (MEMORY.equalsIgnoreCase(persistence)) return new LoginDAOMemory();
        return new LoginDAODB();
    }

    public static BookingDAO getBookingDAO() {
        return switch (persistence.toLowerCase()) {
            case FILE   -> new BookingDAOFile();
            case MEMORY -> new BookingDAOMemory();
            default     -> new BookingDAODB();
        };
    }

    public static SubjectDAO getSubjectDAO() {
        if (MEMORY.equalsIgnoreCase(persistence)) return new SubjectDAOMemory();
        return new SubjectDAODB();
    }

    public static TutorDAO getTutorDAO() {
        if (MEMORY.equalsIgnoreCase(persistence)) return new TutorDAOMemory();
        return new TutorDAODB();
    }

    public static TimeSlotDAO getTimeSlotDAO() {
        if (MEMORY.equalsIgnoreCase(persistence)) return new TimeSlotDAOMemory();
        return new TimeSlotDAODB();
    }

    public static StudentDAO getStudentDAO() {
        if (MEMORY.equalsIgnoreCase(persistence)) return new StudentDAOMemory();
        return new StudentDAODB();
    }

    public static ActivityDAO getActivityDAO() {
        if (MEMORY.equalsIgnoreCase(persistence)) return new ActivityDAOMemory();
        return new ActivityDAODB();
    }

    public static ProgressDAO getProgressDAO() {
        if (MEMORY.equalsIgnoreCase(persistence)) return new ProgressDAOMemory();
        return new ProgressDAODB();
    }

    public static RegistrationDAO getRegistrationDAO() {
        if (MEMORY.equalsIgnoreCase(persistence)) return new RegistrationDAOMemory();
        return new RegistrationDAODB();
    }

    public static UserDAO getUserDAO() {
        if (MEMORY.equalsIgnoreCase(persistence)) return new UserDAOMemory();
        return new UserDAODB();
    }
}