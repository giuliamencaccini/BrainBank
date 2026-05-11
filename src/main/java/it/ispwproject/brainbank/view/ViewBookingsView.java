package it.ispwproject.brainbank.view;

import it.ispwproject.brainbank.bean.BookingResponseBean;

import java.util.List;

public class ViewBookingsView {

    private static final String SEPARATOR = "─".repeat(50);

    public void mostraIntestazione() {
        System.out.println();
        System.out.println(SEPARATOR);
        System.out.println("  BrainBank – Le mie prenotazioni");
        System.out.println(SEPARATOR);
    }

    public void mostraPrenotazioni(List<BookingResponseBean> bookings) {
        if (bookings.isEmpty()) {
            System.out.println("  Non hai ancora prenotazioni.");
        } else {
            for (BookingResponseBean b : bookings) {
                System.out.printf("  ID %-3d  [%s]%n", b.getId(), b.getStatus());
                System.out.printf("         %s  –  %s%n",
                        b.getSubject().getName(), b.getTutor().getName());
                System.out.printf("         %s  %s – %s%n",
                        b.getTimeSlot().getDate(),
                        b.getTimeSlot().getStartTime(),
                        b.getTimeSlot().getEndTime());
                System.out.println();
            }
        }
        System.out.println(SEPARATOR);
    }

    public void mostraErrore(String messaggio) {
        System.out.println("  ❌ " + messaggio);
    }
}