package it.ispwproject.brainbank.controller.gui;

import it.ispwproject.brainbank.bean.TimeSlotBean;
import it.ispwproject.brainbank.bean.BookingResponseBean;
import it.ispwproject.brainbank.controller.applicativo.AvailabilityController;
import it.ispwproject.brainbank.controller.applicativo.BookingController;
import it.ispwproject.brainbank.exception.BookingException;
import it.ispwproject.brainbank.exception.DAOException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

public class ViewSlotsGUI {

    private final Stage                  stage;
    private final AvailabilityController availabilityController = new AvailabilityController();
    private final BookingController      bookingController      = new BookingController();

    public ViewSlotsGUI(Stage stage) { this.stage = stage; }

    public void show() {
        BorderPane root = buildShell();
        VBox content = new VBox(12);
        content.setPadding(new Insets(24));
        content.setAlignment(Pos.TOP_CENTER);

        Label errorLabel = new Label("");
        errorLabel.getStyleClass().add("error-label");

        try {
            List<TimeSlotBean> slots = availabilityController.getSlots();
            int tutorId = it.ispwproject.brainbank.util.singleton.SessionManager
                    .getInstance().getLoggedUser().getId();

            java.util.Map<Integer, String> subjectBySlot = new java.util.HashMap<>();
            try {
                for (BookingResponseBean b : bookingController.getTutorBookings(tutorId))
                    subjectBySlot.put(b.getTimeSlot().getId(), b.getSubject().getName());
            } catch (Exception ignored) {}

            List<TimeSlotBean> prenotati   = slots.stream().filter(s -> !s.isAvailable()).toList();
            List<TimeSlotBean> disponibili = slots.stream().filter(TimeSlotBean::isAvailable).toList();

            // ── Toggle bar ──────────────────────────────────
            ToggleButton btnPrenotati   = new ToggleButton("Prenotati (" + prenotati.size() + ")");
            ToggleButton btnDisponibili = new ToggleButton("Disponibili (" + disponibili.size() + ")");
            btnPrenotati.getStyleClass().add("toggle-card");
            btnDisponibili.getStyleClass().add("toggle-card");
            btnPrenotati.setPrefWidth(200); btnPrenotati.setPrefHeight(36);
            btnDisponibili.setPrefWidth(200); btnDisponibili.setPrefHeight(36);

            ToggleGroup group = new ToggleGroup();
            btnPrenotati.setToggleGroup(group);
            btnDisponibili.setToggleGroup(group);
            btnPrenotati.setSelected(true);

            HBox toggleBar = new HBox(8, btnPrenotati, btnDisponibili);
            toggleBar.setAlignment(Pos.CENTER);
            toggleBar.setMaxWidth(640);

            // ── Lista aggiornabile ───────────────────────────
            VBox listBox = new VBox(12);
            listBox.setAlignment(Pos.TOP_CENTER);

            Runnable refreshList = () -> {
                listBox.getChildren().clear();
                List<TimeSlotBean> current = btnPrenotati.isSelected() ? prenotati : disponibili;
                if (current.isEmpty()) {
                    Label empty = new Label(btnPrenotati.isSelected()
                            ? "Nessuno slot prenotato." : "Nessuno slot disponibile.");
                    empty.getStyleClass().add("register-label");
                    listBox.getChildren().add(empty);
                } else {
                    for (TimeSlotBean s : current)
                        listBox.getChildren().add(buildSlotCard(s, subjectBySlot.get(s.getId())));
                }
            };
            refreshList.run();

            btnPrenotati.setOnAction(e -> refreshList.run());
            btnDisponibili.setOnAction(e -> refreshList.run());

            if (slots.isEmpty()) {
                Label empty = new Label("Non hai ancora slot.");
                empty.getStyleClass().add("register-label");
                content.getChildren().addAll(toggleBar, empty);
            } else {
                content.getChildren().addAll(toggleBar, listBox);
            }

        } catch (DAOException e) { errorLabel.setText("Errore: " + e.getMessage()); }

        content.getChildren().add(errorLabel);
        root.setCenter(transparentScroll(content));
        stage.setScene(GUIUtils.createScene(root));
        stage.show();
    }

    private HBox buildSlotCard(TimeSlotBean s, String subjectName) {
        HBox card = new HBox(16);
        card.getStyleClass().add("info-card");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setMaxWidth(640);

        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);

        // Riga data + pallino allineati
        Label dot = new Label("●");
        dot.getStyleClass().add(s.isAvailable() ? "success-label" : "error-label");
        dot.setStyle("-fx-font-size: 14px;");

        String dateStr = s.getDate().format(
                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        Label dateTime = new Label(dateStr + "   " + s.getStartTime() + " – " + s.getEndTime());
        dateTime.getStyleClass().add("welcome-label");

        HBox dateRow = new HBox(8, dot, dateTime);
        dateRow.setAlignment(Pos.CENTER_LEFT);

        Label status = new Label(s.isAvailable() ? "Disponibile" : "Prenotato");
        status.getStyleClass().add(s.isAvailable() ? "success-label" : "error-label");
        status.setStyle("-fx-font-weight: bold;");

        info.getChildren().addAll(dateRow, status);

        if (!s.isAvailable()) {
            if (subjectName != null) {
                Label subject = new Label("Materia: " + subjectName);
                subject.getStyleClass().add("small-label");
                info.getChildren().add(subject);
            }
            if (s.getBookedByName() != null) {
                Label student = new Label("Studente: " + s.getBookedByName());
                student.getStyleClass().add("register-label");
                info.getChildren().add(student);
            }
            if (s.getMeetLink() != null) {
                Label meet = new Label("Meet: " + s.getMeetLink());
                meet.getStyleClass().add("info-text");
                meet.setStyle("-fx-text-fill: #3498DB;");
                info.getChildren().add(meet);
            }
        }

        card.getChildren().add(info);
        return card;
    }

    private BorderPane buildShell() {
        BorderPane shell = new BorderPane();
        shell.getStyleClass().add("brainbank-background");
        shell.setTop(buildTopBar("I miei slot", () -> MainGUI.showDashboardTutor()));
        return shell;
    }

    private HBox buildTopBar(String titleText, Runnable onBack) {
        HBox bar = new HBox();
        bar.getStyleClass().add("navbar");
        bar.setAlignment(Pos.CENTER_LEFT);

        Button backBtn = new Button("⟪  Indietro");
        backBtn.getStyleClass().add("back-button");
        backBtn.setOnAction(e -> onBack.run());
        HBox left = new HBox(backBtn);
        left.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(left, Priority.ALWAYS);

        Label title = new Label(titleText);
        title.getStyleClass().add("page-title");
        title.setMaxWidth(Double.MAX_VALUE);
        title.setAlignment(Pos.CENTER);
        HBox.setHgrow(title, Priority.ALWAYS);

        var logoStream = getClass().getResourceAsStream("/images/logo.png");
        HBox right = new HBox();
        right.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(right, Priority.ALWAYS);
        if (logoStream != null) {
            javafx.scene.image.ImageView logo = new javafx.scene.image.ImageView(
                    new javafx.scene.image.Image(logoStream, 60, 60, true, true));
            logo.setFitHeight(56); logo.setPreserveRatio(true); logo.setSmooth(true);
            right.getChildren().add(logo);
        }

        left.setPrefWidth(150);
        right.setPrefWidth(150);

        bar.getChildren().addAll(left, title, right);
        return bar;
    }

    private ScrollPane transparentScroll(javafx.scene.Node content) {
        ScrollPane scroll = new ScrollPane(content);
        scroll.getStyleClass().add("transparent-scroll");
        scroll.setFitToWidth(true);
        return scroll;
    }
}