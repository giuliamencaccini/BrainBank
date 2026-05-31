package it.ispwproject.brainbank.controller.gui;

import it.ispwproject.brainbank.bean.TimeSlotBean;
import it.ispwproject.brainbank.controller.applicativo.AvailabilityController;
import it.ispwproject.brainbank.controller.applicativo.UserController;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.model.User;
import it.ispwproject.brainbank.pattern.singleton.SessionManager;
import it.ispwproject.brainbank.view.gui.DashboardTutorGUIView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

public class DashboardTutorGUI {

    private final Stage stage;
    private final AvailabilityController availabilityController = new AvailabilityController();
    private final UserController userController = new UserController();
    private final DashboardTutorGUIView view = new DashboardTutorGUIView();

    private int         weekOffset = 0;
    private final int[] weekOffRef = {0};

    public DashboardTutorGUI(Stage stage) { this.stage = stage; }

    public void show() {
        User user = SessionManager.getInstance().getLoggedUser();

        // ── Navbar ───────────────────────────────────────────────────────────
        HBox navbar = view.buildNavbar("Tutor", this::handleLogout);

        // ── Calendario ───────────────────────────────────────────────────────
        VBox calendarSection = view.buildCalendarSection(
                () -> { weekOffset--; weekOffRef[0] = weekOffset; view.refreshCalendar(loadSlots(), weekOffset); },
                () -> { weekOffset++; weekOffRef[0] = weekOffset; view.refreshCalendar(loadSlots(), weekOffset); },
                () -> { weekOffset = 0; weekOffRef[0] = 0;        view.refreshCalendar(loadSlots(), weekOffset); }
        );
        List<TimeSlotBean> slots = loadSlots();
        view.bindCalendarWidth(slots, weekOffRef);
        view.refreshCalendar(slots, weekOffset);

        // ── Sezione destra ───────────────────────────────────────────────────
        VBox actionGrid   = view.buildActionGrid(
                e -> new SetAvailabilityGUI(stage).show(),
                e -> new ViewSlotsGUI(stage).show(),
                e -> new ManageStudentsGUI(stage).show()
        );
        VBox accordion    = view.buildUserInfoAccordion(user, this::handleSaveEmail);
        VBox rightSection = view.buildRightSection(actionGrid, accordion);

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

    private List<TimeSlotBean> loadSlots() {
        try { return availabilityController.getSlots(); }
        catch (DAOException e) { return List.of(); }
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