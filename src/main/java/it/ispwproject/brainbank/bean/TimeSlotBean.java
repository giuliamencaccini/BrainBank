package it.ispwproject.brainbank.bean;

import java.time.LocalDate;
import java.time.LocalTime;

public class TimeSlotBean {
    private int id;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean available;
    private String bookedByName;
    private String meetLink;

    public TimeSlotBean() {}

    public TimeSlotBean(int id, LocalDate date,
                        LocalTime startTime, LocalTime endTime,
                        boolean available) {
        this.id        = id;
        this.date      = date;
        this.startTime = startTime;
        this.endTime   = endTime;
        this.available = available;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public String getBookedByName() { return bookedByName; }
    public void setBookedByName(String bookedByName) { this.bookedByName = bookedByName; }
    public String getMeetLink() { return meetLink; }
    public void setMeetLink(String meetLink) { this.meetLink = meetLink; }
}