package it.ispwproject.brainbank.controller.gui;

import it.ispwproject.brainbank.bean.BookingResponseBean;
import it.ispwproject.brainbank.controller.applicativo.BookingController;
import it.ispwproject.brainbank.controller.applicativo.UserController;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.model.User;
import it.ispwproject.brainbank.util.singleton.SessionManager;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class DashboardStudentGUI {

    private static final int HOUR_START   = 8;
    private static final int HOUR_END     = 19;
    private static final int HOUR_HEIGHT  = 28;  // altezza pixel per ora
    private static final int LABEL_WIDTH  = 42;  // larghezza colonna ore
    private static final int HEADER_H     = 72;  // altezza header (mese + giorno)
    private static final int DAYS         = 7;   // LUN → DOM

    private static final String BOOKING_COLOR = "#8FBC8F";

    private final Stage             stage;
    private final BookingController bookingController = new BookingController();
    private final UserController userController = new UserController();
    private int weekOffset = 0;

    public DashboardStudentGUI(Stage stage) { this.stage = stage; }

    public void show() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("brainbank-background");
        root.setTop(buildNavbar());
        root.setCenter(buildBody());
        stage.setScene(GUIUtils.createScene(root));
        stage.show();
    }

    // ── Navbar ───────────────────────────────────────────────────────────

    private HBox buildNavbar() {
        HBox navbar = new HBox();
        navbar.getStyleClass().add("navbar");
        navbar.setAlignment(Pos.CENTER_LEFT);

        Image logoImg = new Image(
                getClass().getResourceAsStream("/images/logo.png"), 80, 80, true, true);
        ImageView logoView = new ImageView(logoImg);
        logoView.setFitHeight(56); logoView.setFitWidth(56);
        logoView.setPreserveRatio(true); logoView.setSmooth(true);

        String nome = SessionManager.getInstance().getLoggedUser().getName();
        Label welcome = new Label("Bentornato\n" + nome + "!");
        welcome.getStyleClass().add("welcome-label");

        HBox left = new HBox(10, logoView, welcome);
        left.setAlignment(Pos.CENTER_LEFT);

        Label ruolo = new Label("Studente");
        ruolo.getStyleClass().add("role-label");
        ruolo.setMaxWidth(Double.MAX_VALUE);
        ruolo.setAlignment(Pos.CENTER);

        Button logoutBtn = new Button("Log out");
        logoutBtn.getStyleClass().add("button");
        logoutBtn.setPadding(new Insets(6, 18, 6, 18));
        logoutBtn.setOnAction(e -> {
            try { it.ispwproject.brainbank.dao.ConnectionFactory.clearRole(); }
            catch (java.sql.SQLException ex) { /* ignora */ }
            SessionManager.getInstance().clearSession();
            MainGUI.showLogin();
        });

        HBox right = new HBox(logoutBtn);
        right.setAlignment(Pos.CENTER_RIGHT);

        HBox.setHgrow(left,  Priority.ALWAYS);
        HBox.setHgrow(ruolo, Priority.ALWAYS);
        HBox.setHgrow(right, Priority.ALWAYS);

        navbar.getChildren().addAll(left, ruolo, right);
        return navbar;
    }

    // ── Body ─────────────────────────────────────────────────────────────

    private HBox buildBody() {
        HBox body = new HBox(20);
        body.getStyleClass().add("brainbank-background");
        body.setPadding(new Insets(20, 24, 20, 24));
        body.setAlignment(Pos.CENTER);

        VBox calendarSection = buildCalendarSection();
        VBox rightSection    = buildRightSection();

        HBox.setHgrow(calendarSection, Priority.ALWAYS);
        body.getChildren().addAll(calendarSection, rightSection);
        return body;
    }

    // ── Calendario responsive ────────────────────────────────────────────

    private VBox buildCalendarSection() {
        VBox section = new VBox(10);
        section.setPrefWidth(620);
        section.setMaxWidth(620);
        section.setAlignment(Pos.TOP_LEFT);
        VBox.setVgrow(section, Priority.ALWAYS);

        Label title = new Label("Calendario Lezioni");
        title.getStyleClass().add("calendar-title");

        // ScrollPane verticale — si adatta in altezza alla finestra
        ScrollPane scroll = new ScrollPane();
        scroll.getStyleClass().add("transparent-scroll");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setFitToWidth(true);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        // Altezza scroll adattiva alla finestra
        scroll.setMinHeight(200);

        // Rebuild quando larghezza o altezza cambiano
        scroll.widthProperty().addListener((obs, oldW, newW) -> {
            if (newW.doubleValue() > 10)
                scroll.setContent(buildWeekCalendar(loadBookings(), newW.doubleValue()));
        });

        Runnable refresh = () -> {
            double w = scroll.getWidth() > 10 ? scroll.getWidth() : 560;
            scroll.setContent(buildWeekCalendar(loadBookings(), w));
        };
        refresh.run();

        Button prevBtn  = makeNavBtn("‹", e -> { weekOffset--; refresh.run(); });
        Button nextBtn  = makeNavBtn("›", e -> { weekOffset++; refresh.run(); });
        Button todayBtn = makeTodayBtn(e  -> { weekOffset = 0; refresh.run(); });

        HBox navBar = new HBox(8, prevBtn, todayBtn, nextBtn);
        navBar.setAlignment(Pos.CENTER_LEFT);

        HBox titleRow = new HBox(16, title, navBar);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        section.getChildren().addAll(titleRow, scroll);
        return section;
    }

    private List<BookingResponseBean> loadBookings() {
        try {
            return bookingController.getStudentBookings(
                    SessionManager.getInstance().getLoggedUser().getId());
        } catch (DAOException e) { return List.of(); }
    }

    private Pane buildWeekCalendar(List<BookingResponseBean> bookings, double availWidth) {
        LocalDate today = LocalDate.now();
        // Settimana LUN→DOM: troviamo il lunedì corrente
        LocalDate monday = today.with(DayOfWeek.MONDAY).plusWeeks(weekOffset);

        int totalHours = HOUR_END - HOUR_START;
        int gridHeight = totalHours * HOUR_HEIGHT;

        // Colonne dinamiche in base alla larghezza disponibile
        int colW = (int) Math.max(48, (availWidth - LABEL_WIDTH - 2) / DAYS);

        Pane pane = new Pane();
        pane.setPrefSize(LABEL_WIDTH + DAYS * colW + 8, gridHeight + HEADER_H);
        pane.getStyleClass().add("calendar-pane");

        addMonthRow(pane, monday, colW);
        addDayHeaders(pane, monday, today, colW);
        addHourRows(pane, totalHours, colW, gridHeight);
        addBookingBlocks(pane, bookings, monday, totalHours, colW);

        return pane;
    }

    private void addMonthRow(Pane pane, LocalDate firstDay, int colW) {
        LocalDate lastDay = firstDay.plusDays(DAYS - 1);
        String month = firstDay.getMonth() == lastDay.getMonth()
                ? cap(firstDay.getMonth().getDisplayName(TextStyle.FULL, Locale.ITALIAN))
                + " " + firstDay.getYear()
                : cap(firstDay.getMonth().getDisplayName(TextStyle.SHORT, Locale.ITALIAN))
                + " – "
                + cap(lastDay.getMonth().getDisplayName(TextStyle.SHORT, Locale.ITALIAN))
                + " " + lastDay.getYear();

        Label lbl = new Label(month);
        lbl.getStyleClass().add("calendar-month-label");
        lbl.setLayoutX(LABEL_WIDTH); lbl.setLayoutY(8);
        lbl.setPrefWidth(DAYS * colW); lbl.setAlignment(Pos.CENTER);
        pane.getChildren().add(lbl);

    }

    private void addDayHeaders(Pane pane, LocalDate firstDay, LocalDate today, int colW) {
        for (int d = 0; d < DAYS; d++) {
            LocalDate date    = firstDay.plusDays(d);
            boolean   isToday = date.equals(today);
            String dayName = date.getDayOfWeek()
                    .getDisplayName(TextStyle.SHORT, Locale.ITALIAN).toUpperCase();
            double x = LABEL_WIDTH + d * colW;

            Label dayAbbr = new Label(dayName);
            dayAbbr.getStyleClass().add("calendar-day-label");
            dayAbbr.setLayoutX(x); dayAbbr.setLayoutY(32);
            dayAbbr.setPrefWidth(colW); dayAbbr.setAlignment(Pos.CENTER);
            pane.getChildren().add(dayAbbr);

            if (isToday) {
                Label badge = new Label(String.valueOf(date.getDayOfMonth()));
                badge.getStyleClass().add("calendar-today-badge");
                badge.setPrefSize(22, 22); badge.setAlignment(Pos.CENTER);
                badge.setLayoutX(x + (colW - 22) / 2.0);
                badge.setLayoutY(HEADER_H - 30);
                pane.getChildren().add(badge);
            } else {
                Label num = new Label(String.valueOf(date.getDayOfMonth()));
                num.getStyleClass().add("calendar-day-label");
                num.setLayoutX(x); num.setLayoutY(HEADER_H - 28);
                num.setPrefWidth(colW); num.setAlignment(Pos.CENTER);
                pane.getChildren().add(num);
            }
        }
    }

    private void addHourRows(Pane pane, int totalHours, int colW, int gridHeight) {
        // Linea separatrice sotto header
        Region sep = new Region();
        sep.setPrefSize(DAYS * colW, 1);
        sep.getStyleClass().add("calendar-separator");
        sep.setLayoutX(LABEL_WIDTH); sep.setLayoutY(HEADER_H);
        pane.getChildren().add(sep);

        for (int h = 0; h < totalHours; h++) {
            int hour = HOUR_START + h;
            int y    = HEADER_H + h * HOUR_HEIGHT;
            String ht = hour < 12 ? hour + " AM" : hour == 12 ? "12 PM" : (hour - 12) + " PM";

            Label lbl = new Label(ht);
            lbl.getStyleClass().add("calendar-hour-label");
            lbl.setPrefWidth(LABEL_WIDTH - 4); lbl.setAlignment(Pos.CENTER_RIGHT);
            lbl.setLayoutX(0); lbl.setLayoutY(y - 7);
            pane.getChildren().add(lbl);

            Region hLine = new Region();
            hLine.setPrefSize(DAYS * colW, 1);
            hLine.getStyleClass().add("calendar-grid-line");
            hLine.setLayoutX(LABEL_WIDTH); hLine.setLayoutY(y);
            pane.getChildren().add(hLine);
        }

        for (int d = 1; d < DAYS; d++) {
            Region vLine = new Region();
            vLine.setPrefSize(1, gridHeight);
            vLine.getStyleClass().add("calendar-grid-line");
            vLine.setLayoutX(LABEL_WIDTH + d * colW); vLine.setLayoutY(HEADER_H);
            pane.getChildren().add(vLine);
        }
    }

    private void addBookingBlocks(Pane pane, List<BookingResponseBean> bookings,
                                  LocalDate firstDay, int totalHours, int colW) {
        for (BookingResponseBean b : bookings) {
            if (b.getStatus().equals("CANCELLED")) continue;
            LocalDate bDate  = b.getTimeSlot().getDate();
            LocalTime bStart = b.getTimeSlot().getStartTime();
            LocalTime bEnd   = b.getTimeSlot().getEndTime();

            int dayOffset = -1;
            for (int d = 0; d < DAYS; d++)
                if (firstDay.plusDays(d).equals(bDate)) { dayOffset = d; break; }
            if (dayOffset < 0) continue;

            double sf = (bStart.getHour() + bStart.getMinute() / 60.0) - HOUR_START;
            double ef = (bEnd.getHour()   + bEnd.getMinute()   / 60.0) - HOUR_START;
            if (sf < 0 || ef > totalHours) continue;

            VBox block = new VBox(1);
            block.setLayoutX(LABEL_WIDTH + dayOffset * colW + 2);
            block.setLayoutY(HEADER_H + sf * HOUR_HEIGHT);
            block.setPrefWidth(colW - 4);
            block.setPrefHeight(Math.max((ef - sf) * HOUR_HEIGHT - 2, 20));
            block.setPadding(new Insets(2, 3, 2, 3));
            block.setStyle("-fx-background-color: " + BOOKING_COLOR + "; -fx-background-radius: 4;");

            Label s = new Label(b.getSubject().getName() + " " + b.getTutor().getSurname());
            s.getStyleClass().add("calendar-block-title");
            s.setWrapText(true);
            Label t = new Label(bStart.getHour() + "-" + bEnd.getHour() +
                    (bEnd.getHour() < 12 ? "AM" : "PM"));
            t.getStyleClass().add("calendar-block-time");
            block.getChildren().addAll(s, t);
            pane.getChildren().add(block);
        }
    }

    // ── Sezione destra ────────────────────────────────────────────────────

    private VBox buildRightSection() {
        VBox section = new VBox(14);
        section.setAlignment(Pos.TOP_CENTER);
        section.setPrefWidth(320);
        section.setMinWidth(320);
        section.setPadding(new Insets(0));

        Region spacer = new Region();
        spacer.setPrefHeight(40); spacer.setMinHeight(40); spacer.setMaxHeight(40);

        VBox actionButtons = buildActionButtons();
        VBox.setVgrow(actionButtons, Priority.NEVER);

        VBox accordion = buildUserInfoAccordion();
        VBox.setVgrow(accordion, Priority.NEVER);

        section.getChildren().addAll(spacer, actionButtons, accordion);
        return section;
    }

    private VBox buildActionButtons() {
        VBox buttons = new VBox(14);
        buttons.setAlignment(Pos.CENTER);

        buttons.getChildren().addAll(
                actionTile("booking.png",        "Prenota Lezione",
                        e -> new BookLessonGUI(stage).show()),
                actionTile("lightbulb-on.png",   "Le mie prenotazioni",
                        e -> new ViewBookingsGUI(stage).show()),
                actionTile("task-checklist.png", "To-do",
                        e -> new ViewToDoGUI(stage).show())
        );
        return buttons;
    }

    private HBox actionTile(String iconFile, String text,
                            EventHandler<ActionEvent> handler) {
        HBox tile = new HBox(14);
        tile.getStyleClass().add("action-tile");
        tile.setAlignment(Pos.CENTER_LEFT);
        tile.setPrefHeight(58);
        tile.setMaxWidth(Double.MAX_VALUE);
        tile.setPadding(new Insets(12, 16, 12, 16));
        tile.setOnMouseClicked(e -> handler.handle(new ActionEvent(tile, null)));

        ImageView icon = new ImageView(new Image(
                getClass().getResourceAsStream("/icons/" + iconFile), 48, 48, true, true));
        icon.setFitHeight(22); icon.setFitWidth(22);
        icon.setPreserveRatio(true); icon.setSmooth(true);
        ColorAdjust ca = new ColorAdjust(); ca.setBrightness(1.0);
        icon.setEffect(ca);

        Label lbl = new Label(text);
        lbl.getStyleClass().add("action-tile-label");
        lbl.setWrapText(false);

        tile.getChildren().addAll(icon, lbl);
        return tile;
    }

    private VBox buildUserInfoAccordion() {
        User user = SessionManager.getInstance().getLoggedUser();

        HBox header = new HBox(8);
        header.getStyleClass().addAll("accordion-header");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(12, 16, 12, 16));

        Label headerLbl = new Label("Le tue informazioni");
        headerLbl.getStyleClass().add("small-label");
        HBox.setHgrow(headerLbl, Priority.ALWAYS);

        Label arrow = new Label("▼");
        arrow.getStyleClass().add("accordion-arrow");
        header.getChildren().addAll(headerLbl, arrow);

        VBox content = new VBox(10);
        content.getStyleClass().add("accordion-content");
        content.setPadding(new Insets(10, 16, 14, 16));
        content.getChildren().addAll(
                new Separator(),
                infoRow("Nome",    user.getName()),
                infoRow("Cognome", user.getSurname()),
                buildEmailRow(user));
        content.setVisible(false);
        content.setManaged(false);

        final boolean[] open = {false};
        header.setOnMouseClicked(e -> {
            open[0] = !open[0];
            content.setVisible(open[0]);
            content.setManaged(open[0]);
            arrow.setText(open[0] ? "▲" : "▼");
            header.getStyleClass().setAll(open[0] ? "accordion-header-open" : "accordion-header");
        });

        VBox accordion = new VBox(0, header, content);
        accordion.getStyleClass().add("accordion-panel");
        return accordion;
    }

    private HBox buildEmailRow(User user) {
        Label emailLbl = new Label(user.getEmail() != null ? user.getEmail() : "—");
        emailLbl.getStyleClass().add("info-text");

        // Icona matita da file con fallback emoji
        Button editBtn = new Button();
        var pencilStream = getClass().getResourceAsStream("/icons/pencil.png");
        if (pencilStream != null) {
            ImageView pencilIcon = new ImageView(new Image(pencilStream, 32, 32, true, true));
            pencilIcon.setFitHeight(16); pencilIcon.setFitWidth(16);
            pencilIcon.setPreserveRatio(true); pencilIcon.setSmooth(true);
            editBtn.setGraphic(pencilIcon);
        } else {
            editBtn.setText("✏");
        }
        editBtn.getStyleClass().add("edit-button");
        editBtn.setAlignment(Pos.CENTER);

        Label emailKey = new Label("Email:");
        emailKey.getStyleClass().add("small-label");
        emailKey.setPrefWidth(70);

        HBox viewRow = new HBox(8, emailKey, emailLbl, editBtn);
        viewRow.setAlignment(Pos.CENTER_LEFT);

        TextField emailField = new TextField(user.getEmail());
        emailField.getStyleClass().add("text-field");
        emailField.setPrefHeight(32);

        Button saveBtn   = new Button("✓");
        Button cancelBtn = new Button("✗");
        saveBtn.getStyleClass().add("save-button");
        saveBtn.setMinWidth(32); saveBtn.setMinHeight(32);
        cancelBtn.getStyleClass().add("cancel-inline-button");
        cancelBtn.setMinWidth(32); cancelBtn.setMinHeight(32);

        HBox editRow = new HBox(6, emailField, saveBtn, cancelBtn);
        editRow.setAlignment(Pos.CENTER_LEFT);
        editRow.setVisible(false); editRow.setManaged(false);

        VBox emailContainer = new VBox(4, viewRow, editRow);

        editBtn.setOnAction(e -> {
            viewRow.setVisible(false); viewRow.setManaged(false);
            editRow.setVisible(true);  editRow.setManaged(true);
            emailField.requestFocus();
        });
        cancelBtn.setOnAction(e -> {
            emailField.setText(user.getEmail());
            editRow.setVisible(false);  editRow.setManaged(false);
            viewRow.setVisible(true);   viewRow.setManaged(true);
        });
        saveBtn.setOnAction(e -> {
            String newEmail = emailField.getText().trim();
            if (newEmail.isEmpty()) return;
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Conferma cambio email");
            confirm.setHeaderText(null);
            confirm.setContentText("Vuoi cambiare l'email a:\n" + newEmail + "?");
            confirm.showAndWait().ifPresent(r -> {
                if (r == ButtonType.OK) {
                    try {
                        userController.updateEmail(newEmail);
                        emailLbl.setText(newEmail);
                        editRow.setVisible(false);
                        editRow.setManaged(false);
                        viewRow.setVisible(true);
                        viewRow.setManaged(true);
                    } catch (DAOException ex) {
                        emailLbl.setText("❌ " + ex.getMessage());
                    }
                }
            });
        });


        return new HBox(emailContainer);
    }

    private HBox infoRow(String label, String value) {
        Label lbl = new Label(label + ":");
        lbl.getStyleClass().add("small-label");
        lbl.setPrefWidth(70);
        Label val = new Label(value != null ? value : "—");
        val.getStyleClass().add("info-text");
        val.setWrapText(true);
        HBox row = new HBox(8, lbl, val);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    // ── Helpers ──────────────────────────────────────────────────────────

    private Button makeNavBtn(String text, EventHandler<ActionEvent> h) {
        Button btn = new Button(text);
        btn.getStyleClass().add("nav-button");
        btn.setPrefSize(36, 36);
        btn.setMinSize(36, 36);
        btn.setOnAction(h);
        return btn;
    }

    private Button makeTodayBtn(EventHandler<ActionEvent> h) {
        Button btn = new Button("Oggi");
        btn.getStyleClass().add("today-button");
        btn.setOnAction(h);
        return btn;
    }

    private String cap(String s) {
        return (s == null || s.isEmpty()) ? s :
                Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}