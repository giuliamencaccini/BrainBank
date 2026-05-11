package it.ispwproject.brainbank.controller.demo;

import it.ispwproject.brainbank.controller.applicativo.BookingController;
import it.ispwproject.brainbank.controller.applicativo.LoginController;

public class DemoFactory {

    // cambiare flag DEMO_MODE per modalita demo
    private static final boolean DEMO_MODE = false;

    // Singleton — stessa istanza condivisa tra tutti i CLI controller
    private static BookingController bookingControllerInstance;

    private DemoFactory() {}

    public static LoginController getLoginController() {
        if (DEMO_MODE) {
            return new LoginControllerDemo();
        }
        return new LoginController();
    }

    public static BookingController getBookingController() {
        if (DEMO_MODE) {
            if (bookingControllerInstance == null) {
                bookingControllerInstance = new BookingControllerDemo();
            }
            return bookingControllerInstance;
        }
        return new BookingController();
    }
}