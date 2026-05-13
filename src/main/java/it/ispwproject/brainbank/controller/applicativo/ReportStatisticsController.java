package it.ispwproject.brainbank.controller.applicativo;

import it.ispwproject.brainbank.bean.StatisticsBean;
import it.ispwproject.brainbank.dao.BookingDAO;
import it.ispwproject.brainbank.dao.DAOFactory;
import it.ispwproject.brainbank.enumerator.BookingStatus;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.model.Booking;

import java.util.*;
import java.util.stream.Collectors;

public class ReportStatisticsController {

    private final BookingDAO bookingDAO;

    public ReportStatisticsController() {
        this.bookingDAO = DAOFactory.getBookingDAO();
    }

    public StatisticsBean getStatistics() throws DAOException {
        List<Booking> all = bookingDAO.findAll();

        int total = all.size();

        int cancelled = (int) all.stream()
                .filter(b -> b.getStatus() == BookingStatus.CANCELLED)
                .count();

        double cancellationRate = total == 0 ? 0.0
                : Math.round((cancelled * 100.0 / total) * 10.0) / 10.0;

        Map<String, Integer> topTutors = all.stream()
                .filter(b -> b.getTutor() != null && b.getStatus() == BookingStatus.CONFIRMED)
                .collect(Collectors.groupingBy(
                        b -> b.getTutor().getName() + " " + b.getTutor().getSurname(),
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(3)
                .collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));

        Map<String, Integer> topSubjects = all.stream()
                .filter(b -> b.getSubject() != null)
                .collect(Collectors.groupingBy(
                        b -> b.getSubject().getName(),
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(3)
                .collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));

        return new StatisticsBean(total, cancelled, cancellationRate, topTutors, topSubjects);
    }
}