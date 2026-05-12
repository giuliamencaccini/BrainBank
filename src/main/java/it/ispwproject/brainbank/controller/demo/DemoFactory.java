package it.ispwproject.brainbank.controller.demo;

import it.ispwproject.brainbank.controller.applicativo.BookingController;
import it.ispwproject.brainbank.controller.applicativo.LoginController;
import it.ispwproject.brainbank.dao.DAOFactory;

/**
 * Factory per la modalità demo.
 * Imposta la persistenza MEMORY e restituisce i controller applicativi standard.
 * NON ci sono più controller demo separati — si usano gli stessi controller
 * con DAO Memory invece di DAO DB.
 */
public class DemoFactory {

    private DemoFactory() {}

    public static void enableDemoMode() {
        DAOFactory.setPersistence(DAOFactory.MEMORY);
    }

    public static LoginController getLoginController() {
        return new LoginController();
    }

    public static BookingController getBookingController() {
        return new BookingController();
    }
}