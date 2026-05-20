package it.ispwproject.brainbank.dao.file;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import it.ispwproject.brainbank.bean.TimeSlotBean;
import it.ispwproject.brainbank.dao.TimeSlotDAO;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.model.TimeSlot;
import it.ispwproject.brainbank.model.Tutor;
import it.ispwproject.brainbank.util.logger.AppLogger;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TimeSlotDAOFile implements TimeSlotDAO {

    private static final String FILE_PATH = "timeslots.json";
    private final Gson gson;
    private final List<TimeSlot> cache;

    public TimeSlotDAOFile() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
                .setPrettyPrinting()
                .create();
        this.cache = loadFromFile();
    }

    @Override
    public List<TimeSlot> getAvailableByTutor(Tutor tutor) throws DAOException {
        return cache.stream()
                .filter(s -> s.getTutor() != null
                        && s.getTutor().getId() == tutor.getId()
                        && s.isAvailable())
                .toList();
    }

    @Override
    public List<TimeSlotBean> getAllByTutorWithStudent(int tutorId) throws DAOException {
        return cache.stream()
                .filter(s -> s.getTutor() != null && s.getTutor().getId() == tutorId)
                .map(s -> new TimeSlotBean(s.getId(), s.getDate(),
                        s.getStartTime(), s.getEndTime(), s.isAvailable()))
                .toList();
    }

    @Override
    public TimeSlot findById(int id) throws DAOException {
        return cache.stream()
                .filter(s -> s.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void save(TimeSlot slot, int tutorId) throws DAOException {
        slot.setId(generateId());
        cache.add(slot);
        saveToFile();
    }

    private int generateId() {
        return cache.stream()
                .mapToInt(TimeSlot::getId)
                .max()
                .orElse(0) + 1;
    }

    private List<TimeSlot> loadFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new ArrayList<>();
        try (Reader reader = new FileReader(file)) {
            Type listType = new TypeToken<List<TimeSlot>>() {}.getType();
            List<TimeSlot> loaded = gson.fromJson(reader, listType);
            return loaded != null ? loaded : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    private void saveToFile() {
        try (Writer writer = new FileWriter(FILE_PATH)) {
            gson.toJson(cache, writer);
        } catch (IOException e) {
            AppLogger.logError("Errore salvataggio timeslots su file: " + e.getMessage());
        }
    }
}