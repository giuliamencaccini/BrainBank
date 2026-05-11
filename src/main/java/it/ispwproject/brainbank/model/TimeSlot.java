package it.ispwproject.brainbank.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class TimeSlot {

    /**
     * id — IDENTIFICATORE PROPRIO di questo slot.
     * NON è una FK — è l'identità di questo oggetto.
     */
    private int id;

    /**
     * tutor — PUNTATORE diretto all'oggetto Tutor proprietario.
     * NON è tutorId (FK) — è un riferimento in memoria.
     */
    private Tutor tutor;

    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean available;

    public TimeSlot() {}

    public TimeSlot(int id, LocalDate date,
                    LocalTime startTime, LocalTime endTime) {
        this.id        = id;
        this.date      = date;
        this.startTime = startTime;
        this.endTime   = endTime;
        this.available = true;
    }

    public TimeSlot(int id, Tutor tutor, LocalDate date,
                    LocalTime startTime, LocalTime endTime) {
        this.id        = id;
        this.tutor     = tutor;
        this.date      = date;
        this.startTime = startTime;
        this.endTime   = endTime;
        this.available = true;
    }

    public boolean overlaps(TimeSlot other) {
        return this.date.equals(other.date) &&
                this.startTime.isBefore(other.endTime) &&
                this.endTime.isAfter(other.startTime);
    }

    @Override
    public String toString() {
        return date + " " + startTime + " - " + endTime;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Tutor getTutor() { return tutor; }
    public void setTutor(Tutor tutor) { this.tutor = tutor; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}