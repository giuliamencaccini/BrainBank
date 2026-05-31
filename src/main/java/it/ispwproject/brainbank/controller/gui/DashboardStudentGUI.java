package it.ispwproject.brainbank.controller.gui;

import it.ispwproject.brainbank.bean.BookingResponseBean;
import it.ispwproject.brainbank.controller.applicativo.BookingController;
import it.ispwproject.brainbank.controller.applicativo.UserController;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.model.User;
import it.ispwproject.brainbank.pattern.singleton.SessionManager;
import it.ispwproject.brainbank.view.gui.DashboardStudentGUIView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

public class DashboardStudentGUI {

    private final Stage stage;
    private final BookingController bookingController = new BookingController();
    private final UserController userController = new UserController();
    private final DashboardStudentGUIView view = new DashboardStudentGUIView();

    private int         weekOffset = 0;
    private final int[] weekOffRef = {0};

    public DashboardStudentGUI(Stage stage) { this.stage = stage; }

    public void show() {
        User user = SessionManager.getInstance().getLoggedUser();

        // ── Navbar ───────────────────────────────────────────────────────────
        HBox navbar = view.buildNavbar("Studente", this::handleLogout);

        // ── Calendario ───────────────────────────────────────────────────────
        VBox calendarSection = view.buildCalendarSection(
                () -> { weekOffset--; weekOffRef[0] = weekOffset; view.refreshCalendar(loadBookings(), weekOffset); },
                () -> { weekOffset++; weekOffRef[0] = weekOffset; view.refreshCalendar(loadBookings(), weekOffset); },
                () -> { weekOffset = 0; weekOffRef[0] = 0;        view.refreshCalendar(loadBookings(), weekOffset); }
        );
        List<BookingResponseBean> bookings = loadBookings();
        view.bindCalendarWidth(bookings, weekOffRef);
        view.refreshCalendar(bookings, weekOffset);

        // ── Sezione destra ───────────────────────────────────────────────────
        VBox actionButtons = view.buildActionButtons(
                e -> new BookLessonGUI(stage).show(),
                e -> new ViewBookingsGUI(stage).show(),
                e -> new ViewToDoGUI(stage).show()
        );
        VBox accordion    = view.buildUserInfoAccordion(user, this::handleSaveEmail);
        VBox rightSection = view.buildRightSection(actionButtons, accordion);

        // ── Layout ───────────────────────────────────────────────────────────
        HBox body = new HBox(20);
        body.getStyleClass().add("brainbank-background");
        body.setPadding(new Insets(20, 24, 20, 24));
        body.setAlignment(Pos.CENTER);
        HBox.setHgrow(calendarSection, Priority.ALWAYS);
        body.getChildren().addAll(calendarSection, rightSection);

        BorderPane root = new BorderPane();
        root.getStyleClass().add("brainbank-background");
        root.setTop(navbar);
        root.setCenter(body);

        stage.setScene(GUIUtils.createScene(root));
        stage.show();
    }

    // ────────────────────────────────────────────────────────────────────────
    // Azioni
    // ────────────────────────────────────────────────────────────────────────

    private List<BookingResponseBean> loadBookings() {
        try {
            return bookingController.getStudentBookings(
                    SessionManager.getInstance().getLoggedUser().getId());
        } catch (DAOException e) { return List.of(); }
    }

    private void handleLogout() {
        try { it.ispwproject.brainbank.dao.ConnectionFactory.clearRole(); }
        catch (java.sql.SQLException ex) { /* ignora */ }
        SessionManager.getInstance().clearSession();
        MainGUI.showLogin();
    }

    private void handleSaveEmail(String newEmail) {
        try {
            userController.updateEmail(newEmail);
        } catch (DAOException ex) {
            // l'errore è già visibile nella label email aggiornata dalla view
        }
    }
}