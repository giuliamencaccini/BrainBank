package it.ispwproject.brainbank.controller.gui;

import it.ispwproject.brainbank.bean.BookingResponseBean;
import it.ispwproject.brainbank.controller.applicativo.BookingController;
import it.ispwproject.brainbank.exception.BookingException;
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
    private static final int HOUR_END     = 18;
    private static final int HOUR_HEIGHT  = 44;  // altezza pixel per ora
    private static final int LABEL_WIDTH  = 48;  // larghezza colonna ore
    private static final int HEADER_H     = 68;  // altezza header (mese + giorno)
    private static final int DAYS         = 7;   // DOM → SAB

    private static final String BOOKING_COLOR = "#8FBC8F";

    private final Stage             stage;
    private final BookingController bookingController = new BookingController();
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
        Label welcome = new Label("Bentornata\n" + nome + "!");
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
        body.setAlignment(Pos.TOP_LEFT);

        VBox calendarSection = buildCalendarSection();
        VBox rightSection    = buildRightSection();

        HBox.setHgrow(calendarSection, Priority.ALWAYS);
        body.getChildren().addAll(calendarSection, rightSection);
        return body;
    }

    // ── Calendario responsive ────────────────────────────────────────────

    private VBox buildCalendarSection() {
        VBox section = new VBox(10);
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
        } catch (DAOException | BookingException e) { return List.of(); }
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
        pane.setPrefSize(LABEL_WIDTH + DAYS * colW, gridHeight + HEADER_H);
        pane.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 6, 0, 0, 2);");

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
        lbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #4B4B4B;");
        lbl.setLayoutX(LABEL_WIDTH); lbl.setLayoutY(4);
        lbl.setPrefWidth(DAYS * colW); lbl.setAlignment(Pos.CENTER);
        pane.getChildren().add(lbl);

        Label gmt = new Label("GMT+01");
        gmt.getStyleClass().add("calendar-gmt-label");
        gmt.setLayoutX(2); gmt.setLayoutY(HEADER_H - 14);
        pane.getChildren().add(gmt);
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
            dayAbbr.setLayoutX(x); dayAbbr.setLayoutY(24);
            dayAbbr.setPrefWidth(colW); dayAbbr.setAlignment(Pos.CENTER);
            pane.getChildren().add(dayAbbr);

            if (isToday) {
                Label badge = new Label(String.valueOf(date.getDayOfMonth()));
                badge.getStyleClass().add("calendar-today-badge");
                badge.setPrefSize(26, 26); badge.setAlignment(Pos.CENTER);
                badge.setLayoutX(x + (colW - 26) / 2.0);
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
        sep.setStyle("-fx-background-color: #dde6ee;");
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
            hLine.setStyle("-fx-background-color: #eef2f6;");
            hLine.setLayoutX(LABEL_WIDTH); hLine.setLayoutY(y);
            pane.getChildren().add(hLine);
        }

        for (int d = 1; d < DAYS; d++) {
            Region vLine = new Region();
            vLine.setPrefSize(1, gridHeight);
            vLine.setStyle("-fx-background-color: #eef2f6;");
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
            block.setPadding(new Insets(3, 4, 3, 4));
            block.setStyle("-fx-background-color: " + BOOKING_COLOR + "; -fx-background-radius: 4;");

            Label s = new Label(b.getSubject().getName() + " " + b.getTutor().getSurname());
            s.setStyle("-fx-font-size: 9px; -fx-font-weight: bold; -fx-text-fill: white;");
            s.setWrapText(true);
            Label t = new Label(bStart.getHour() + "-" + bEnd.getHour() +
                    (bEnd.getHour() < 12 ? "AM" : "PM"));
            t.setStyle("-fx-font-size: 8px; -fx-text-fill: white;");
            block.getChildren().addAll(s, t);
            pane.getChildren().add(block);
        }
    }

    // ── Sezione destra ────────────────────────────────────────────────────

    private VBox buildRightSection() {
        VBox section = new VBox(16);
        section.setAlignment(Pos.TOP_CENTER);
        section.setPrefWidth(390);
        section.setPadding(new Insets(10, 0, 0, 0));
        section.getChildren().addAll(buildActionGrid(), buildUserInfoCard());
        return section;
    }

    private GridPane buildActionGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(14); grid.setVgap(14);
        grid.setAlignment(Pos.CENTER);
        grid.add(actionTile("booking.png",        "Prenota\nLezione",
                e -> new BookLessonGUI(stage).show()),   0, 0);
        grid.add(actionTile("lightbulb-on.png",   "Gestisci\nLezioni",
                e -> new ViewBookingsGUI(stage).show()), 1, 0);
        grid.add(actionTile("task-checklist.png", "To-do",
                e -> new ViewToDoGUI(stage).show()),     0, 1);
        grid.add(actionTile("star.png",           "Tutor\npreferiti",
                e -> new ViewBookingsGUI(stage).show()), 1, 1);
        return grid;
    }

    private HBox actionTile(String iconFile, String text,
                            EventHandler<ActionEvent> handler) {
        HBox tile = new HBox(14);
        tile.getStyleClass().add("action-tile");
        tile.setAlignment(Pos.CENTER_LEFT);
        tile.setPrefSize(175, 90);
        tile.setPadding(new Insets(14, 18, 14, 18));
        tile.setOnMouseClicked(e -> handler.handle(new ActionEvent(tile, null)));

        var iconStream = getClass().getResourceAsStream("/icons/" + iconFile);
        if (iconStream != null) {
            ImageView icon = new ImageView(
                    new Image(iconStream, 48, 48, true, true));
            icon.setFitHeight(30); icon.setFitWidth(30);
            icon.setPreserveRatio(true); icon.setSmooth(true);
            ColorAdjust ca = new ColorAdjust(); ca.setBrightness(1.0);
            icon.setEffect(ca);
            tile.getChildren().add(icon);
        }

        Label lbl = new Label(text);
        lbl.getStyleClass().add("action-tile-label");
        lbl.setWrapText(true);
        tile.getChildren().add(lbl);
        return tile;
    }

    private VBox buildUserInfoCard() {
        User user = SessionManager.getInstance().getLoggedUser();
        VBox card = new VBox(10);
        card.getStyleClass().add("user-info-card");
        Label title = new Label("Le tue informazioni");
        title.getStyleClass().add("small-label");
        card.getChildren().addAll(title, new Separator(),
                infoRow("Nome",    user.getName()),
                infoRow("Cognome", user.getSurname()),
                infoRow("Email",   user.getEmail()));
        return card;
    }

    private HBox infoRow(String label, String value) {
        Label lbl = new Label(label + ":");
        lbl.getStyleClass().add("small-label");
        lbl.setPrefWidth(70);
        Label val = new Label(value != null ? value : "—");
        val.getStyleClass().add("register-label");
        val.setStyle("-fx-font-size: 12px;");
        val.setWrapText(true);
        HBox row = new HBox(8, lbl, val);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    // ── Helpers ──────────────────────────────────────────────────────────

    private Button makeNavBtn(String text, EventHandler<ActionEvent> h) {
        Button btn = new Button(text);
        btn.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; " +
                "-fx-text-fill: #4B4B4B; -fx-cursor: hand; -fx-background-color: transparent;");
        btn.setOnAction(h);
        return btn;
    }

    private Button makeTodayBtn(EventHandler<ActionEvent> h) {
        Button btn = new Button("Oggi");
        btn.setStyle("-fx-font-size: 11px; -fx-padding: 3 10; " +
                "-fx-background-color: #4B4B4B; -fx-text-fill: white; " +
                "-fx-background-radius: 12; -fx-cursor: hand;");
        btn.setOnAction(h);
        return btn;
    }

    private String cap(String s) {
        return (s == null || s.isEmpty()) ? s :
                Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}