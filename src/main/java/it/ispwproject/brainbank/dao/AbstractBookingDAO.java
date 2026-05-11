package it.ispwproject.brainbank.dao;

import it.ispwproject.brainbank.model.Booking;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractBookingDAO implements BookingDAO {

    // Identity Map — tiene tutte le istanze già caricate
    protected final List<Booking> identityMap = new ArrayList<>();

    protected Booking findInCache(int id) {
        return identityMap.stream()
                .filter(b -> b.getId() == id)
                .findFirst()
                .orElse(null);
    }

    protected List<Booking> findInCacheByStudent(int studentId) {
        return identityMap.stream()
                .filter(b -> b.getStudent() != null && b.getStudent().getId() == studentId)
                .toList();
    }

    protected void addToCache(Booking booking) {
        if (findInCache(booking.getId()) == null) {
            identityMap.add(booking);
        }
    }

    protected void updateInCache(int bookingId) {
        Booking cached = findInCache(bookingId);
        if (cached != null) {
            cached.cancel();
        }
    }
}