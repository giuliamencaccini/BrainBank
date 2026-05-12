package it.ispwproject.brainbank.controller.demo;

import it.ispwproject.brainbank.model.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Fonte dati condivisa per la modalità demo (in-memory).
 * Singleton — una sola istanza per tutta l'applicazione.
 * Tutti i DAO Memory leggono e scrivono su questa classe.
 */
public class DemoDataStore {

    private static DemoDataStore instance;

    private final List<User>     users      = new ArrayList<>();
    private final List<Subject>  subjects   = new ArrayList<>();
    private final List<TimeSlot> timeSlots  = new ArrayList<>();
    private final List<Booking>  bookings   = new ArrayList<>();
    private final List<Activity> activities = new ArrayList<>();
    private final List<Progress> progresses = new ArrayList<>();

    private int nextUserId     = 10;
    private int nextBookingId  = 1;
    private int nextActivityId = 1;
    private int nextProgressId = 1;
    private int nextSlotId     = 10;

    private DemoDataStore() {
        initData();
    }

    public static DemoDataStore getInstance() {
        if (instance == null) {
            instance = new DemoDataStore();
        }
        return instance;
    }

    public static void reset() {
        instance = null;
    }

    private void initData() {
        Student s1 = new Student(1, "Demo", "Student", "student@demo", null);
        Student s2 = new Student(2, "Emma", "Rossi", "emma@demo", null);
        Tutor   t1 = new Tutor(3, "Demo", "Tutor", "tutor@demo", null, "Tutor demo");
        Tutor   t2 = new Tutor(4, "Gabriele", "Bianchi", "gabriele@demo", null,
                "Laurea in Matematica, 5 anni di esperienza");
        users.add(s1);
        users.add(s2);
        users.add(t1);
        users.add(t2);

        subjects.add(new Subject(1, "Analisi 1"));
        subjects.add(new Subject(2, "Fisica 1"));
        subjects.add(new Subject(3, "Algebra"));
        subjects.add(new Subject(4, "Chimica"));
        subjects.add(new Subject(5, "Programmazione"));

        timeSlots.add(new TimeSlot(1, t1, LocalDate.now().plusDays(1),
                LocalTime.of(9, 0), LocalTime.of(11, 0)));
        timeSlots.add(new TimeSlot(2, t1, LocalDate.now().plusDays(1),
                LocalTime.of(11, 0), LocalTime.of(13, 0)));
        timeSlots.add(new TimeSlot(3, t1, LocalDate.now().plusDays(2),
                LocalTime.of(14, 0), LocalTime.of(16, 0)));
        timeSlots.add(new TimeSlot(4, t2, LocalDate.now().plusDays(1),
                LocalTime.of(10, 0), LocalTime.of(12, 0)));

        // Prenotazione demo già confermata
        Booking b = new Booking(s1, t1, subjects.get(2), timeSlots.get(0));
        b.setId(nextBookingId++);
        b.confirm();
        b.setMeetLink("https://meet.jit.si/brainbank-demo");
        timeSlots.get(0).setAvailable(false);
        bookings.add(b);
    }

    public List<User>     getUsers()      { return users; }
    public List<Subject>  getSubjects()   { return subjects; }
    public List<TimeSlot> getTimeSlots()  { return timeSlots; }
    public List<Booking>  getBookings()   { return bookings; }
    public List<Activity> getActivities() { return activities; }
    public List<Progress> getProgresses() { return progresses; }

    public int nextUserId()     { return nextUserId++; }
    public int nextBookingId()  { return nextBookingId++; }
    public int nextActivityId() { return nextActivityId++; }
    public int nextProgressId() { return nextProgressId++; }
    public int nextSlotId()     { return nextSlotId++; }
}