package it.ispwproject.brainbank.dao;

public class DAOFactory {

    public static final String FILE     = "file";
    public static final String DATABASE = "database";

    private static String bookingPersistence = DATABASE;

    private DAOFactory() {}

    // ================================================================== //
    //  BookingDAO — doppia persistenza FILE / DATABASE
    // ================================================================== //

    public static BookingDAO getBookingDAO() {
        if (DATABASE.equalsIgnoreCase(bookingPersistence)) {
            return new BookingDAODB();
        }
        return new BookingDAOFile();
    }

    public static void setBookingPersistence(String persistence) {
        if (persistence != null && !persistence.isBlank()) {
            bookingPersistence = persistence;
        }
    }

    // ================================================================== //
    //  Altri DAO — singola persistenza DB
    // ================================================================== //

    public static UserDAO getUserDAO() {
        return new UserDAO();
    }

    public static SubjectDAO getSubjectDAO() {
        return new SubjectDAO();
    }

    public static TutorDAO getTutorDAO() {
        return new TutorDAO();
    }

    public static TimeSlotDAO getTimeSlotDAO() {
        return new TimeSlotDAO();
    }

    public static StudentDAO getStudentDAO() {
        return new StudentDAO();
    }

    public static ActivityDAO getActivityDAO() {
        return new ActivityDAO();
    }

    public static ProgressDAO getProgressDAO() {
        return new ProgressDAO();
    }

    public static RegistrationDAO getRegistrationDAO() {
        return new RegistrationDAO();
    }
}
