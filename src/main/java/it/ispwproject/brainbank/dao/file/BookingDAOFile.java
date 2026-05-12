package it.ispwproject.brainbank.dao.file;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import it.ispwproject.brainbank.dao.AbstractBookingDAO;
import it.ispwproject.brainbank.enumerator.BookingStatus;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.model.Booking;
import it.ispwproject.brainbank.model.Student;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BookingDAOFile extends AbstractBookingDAO {

    private static final String FILE_PATH = "bookings.json";
    private final Gson gson;

    public BookingDAOFile() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .setPrettyPrinting()
                .create();

        loadAllFromFile().forEach(this::addToCache);
    }

    @Override
    public void save(Booking booking) throws DAOException {
        booking.setId(generateId());
        booking.setStatus(BookingStatus.CONFIRMED);
        addToCache(booking);
        saveToFile();
    }

    @Override
    public List<Booking> findByStudent(int studentId) throws DAOException {
        List<Booking> cached = findInCacheByStudent(studentId);
        if (!cached.isEmpty()) return cached;

        loadAllFromFile().forEach(this::addToCache);
        return findInCacheByStudent(studentId);
    }

    @Override
    public List<Booking> findCompletedByStudentAndTutor(int studentId, int tutorId) throws DAOException {
        return identityMap.stream()
                .filter(b -> b.getStudent() != null && b.getStudent().getId() == studentId
                        && b.getTutor() != null && b.getTutor().getId() == tutorId
                        && b.getStatus() == BookingStatus.CONFIRMED)
                .toList();
    }

    @Override
    public List<Booking> findUpcomingByStudentAndTutor(int studentId, int tutorId) throws DAOException {
        return identityMap.stream()
                .filter(b -> b.getStudent() != null && b.getStudent().getId() == studentId
                        && b.getTutor() != null && b.getTutor().getId() == tutorId
                        && b.getStatus() == BookingStatus.CONFIRMED)
                .toList();
    }

    @Override
    public void cancel(int bookingId, int studentId) throws DAOException {
        Booking booking = findInCache(bookingId);

        if (booking == null) {
            throw new DAOException("Prenotazione non trovata (ID: " + bookingId + ")");
        }

        Student student = booking.getStudent();
        if (student == null || student.getId() != studentId) {
            throw new DAOException("Non puoi annullare una prenotazione che non ti appartiene.");
        }

        booking.cancel();
        updateInCache(bookingId);
        saveToFile();
    }

    private int generateId() {
        return identityMap.stream()
                .mapToInt(Booking::getId)
                .max()
                .orElse(0) + 1;
    }

    private List<Booking> loadAllFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new ArrayList<>();

        try (Reader reader = new FileReader(file)) {
            Type listType = new TypeToken<List<Booking>>() {}.getType();
            List<Booking> loaded = gson.fromJson(reader, listType);
            return loaded != null ? loaded : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    private void saveToFile() {
        try (Writer writer = new FileWriter(FILE_PATH)) {
            gson.toJson(identityMap, writer);
        } catch (IOException e) {
            // log silenzioso
        }
    }
}