package it.ispwproject.brainbank.bean;

import java.util.Map;

public class StatisticsBean {

    private int totalBookings;
    private int cancelledBookings;
    private double cancellationRate;
    private Map<String, Integer> topTutors;
    private Map<String, Integer> topSubjects;

    public StatisticsBean(int totalBookings, int cancelledBookings, double cancellationRate,
                          Map<String, Integer> topTutors, Map<String, Integer> topSubjects) {
        this.totalBookings    = totalBookings;
        this.cancelledBookings = cancelledBookings;
        this.cancellationRate = cancellationRate;
        this.topTutors        = topTutors;
        this.topSubjects      = topSubjects;
    }

    public int getTotalBookings()       { return totalBookings; }
    public int getCancelledBookings()   { return cancelledBookings; }
    public double getCancellationRate() { return cancellationRate; }
    public Map<String, Integer> getTopTutors()   { return topTutors; }
    public Map<String, Integer> getTopSubjects() { return topSubjects; }
}