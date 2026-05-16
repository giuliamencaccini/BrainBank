package it.ispwproject.brainbank.controller.gui;

import it.ispwproject.brainbank.bean.*;
import it.ispwproject.brainbank.controller.applicativo.BookingController;
import it.ispwproject.brainbank.exception.BookingException;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.model.Student;
import it.ispwproject.brainbank.util.singleton.SessionManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

public class BookLessonGUI {

    private final Stage             stage;
    private final BookingController bookingController = new BookingController();

    private SubjectBean  selectedSubject;
    private TutorBean    selectedTutor;
    private TimeSlotBean selectedSlot;

    public BookLessonGUI(Stage stage) { this.stage = stage; }

    public void show() { showStepSubject(); }

    // ── Step 1: materia ──────────────────────────────────────────────────

    private void showStepSubject() {
        Label errorLabel = new Label("");
        errorLabel.getStyleClass().add("error-label");

        Label subtitle = new Label("Seleziona Materia");
        subtitle.getStyleClass().add("register-label");

        ComboBox<SubjectBean> combo = new ComboBox<>();
        combo.getStyleClass().add("combo-box");
        combo.setPromptText("Seleziona...");
        combo.setPrefWidth(260); combo.setPrefHeight(40);
        combo.setCellFactory(lv -> subjectCell());
        combo.setButtonCell(subjectCell());

        try { combo.getItems().setAll(bookingController.getAvailableSubjects()); }
        catch (DAOException e) { errorLabel.setText("Errore: " + e.getMessage()); }

        VBox content = new VBox(16, subtitle, combo, errorLabel);
        content.setAlignment(Pos.CENTER);

        buildAndShow("Home", true, () -> {
            SubjectBean sel = combo.getValue();
            if (sel == null) { errorLabel.setText("Seleziona una materia."); return; }
            selectedSubject = sel;
            showStepTutor();
        }, content);
    }

    // ── Step 2: tutor ────────────────────────────────────────────────────

    private void showStepTutor() {
        Label errorLabel = new Label("");
        errorLabel.getStyleClass().add("error-label");

        Label subtitle = new Label("Tutor disponibili per: " + selectedSubject.getName());
        subtitle.getStyleClass().add("register-label");

        ToggleGroup group = new ToggleGroup();
        VBox tutorList = new VBox(8);
        tutorList.setAlignment(Pos.CENTER);

        try {
            for (TutorBean t : bookingController.getTutorsBySubject(selectedSubject))
                tutorList.getChildren().add(buildToggleCard(
                        (t.isFavourite() ? "⭐ " : "") + t.getName() + " " + t.getSurname(),
                        t, group));
        } catch (DAOException e) { errorLabel.setText("Errore: " + e.getMessage()); }

        ScrollPane scroll = buildTransparentScroll(tutorList, 220);

        VBox content = new VBox(14, subtitle, scroll, errorLabel);
        content.setAlignment(Pos.CENTER);

        buildAndShow("Back", false, () -> {
            Toggle sel = group.getSelectedToggle();
            if (sel == null) { errorLabel.setText("Seleziona un tutor."); return; }
            selectedTutor = (TutorBean) sel.getUserData();
            showStepSlot();
        }, content);
    }

    // ── Step 3: slot ─────────────────────────────────────────────────────

    private void showStepSlot() {
        Label errorLabel = new Label("");
        errorLabel.getStyleClass().add("error-label");

        Label subtitle = new Label(
                "Slot disponibili per: " + selectedTutor.getName() + " " + selectedTutor.getSurname());
        subtitle.getStyleClass().add("register-label");

        ToggleGroup group = new ToggleGroup();
        VBox slotList = new VBox(8);
        slotList.setAlignment(Pos.CENTER);

        try {
            List<TimeSlotBean> available = bookingController
                    .getTutorAvailability(selectedTutor)
                    .stream().filter(TimeSlotBean::isAvailable).toList();
            if (available.isEmpty()) {
                Label empty = new Label("Nessuno slot disponibile.");
                empty.getStyleClass().add("register-label");
                slotList.getChildren().add(empty);
            } else {
                for (TimeSlotBean s : available)
                    slotList.getChildren().add(buildToggleCard(
                            s.getDate() + "   " + s.getStartTime() + " – " + s.getEndTime(),
                            s, group));
            }
        } catch (DAOException e) { errorLabel.setText("Errore: " + e.getMessage()); }

        ScrollPane scroll = buildTransparentScroll(slotList, 200);

        VBox content = new VBox(14, subtitle, scroll, errorLabel);
        content.setAlignment(Pos.CENTER);

        buildAndShow("Back", false, () -> {
            Toggle sel = group.getSelectedToggle();
            if (sel == null) { errorLabel.setText("Seleziona uno slot."); return; }
            selectedSlot = (TimeSlotBean) sel.getUserData();
            showStepSummary();
        }, content);
    }

    // ── Step 4: riepilogo ────────────────────────────────────────────────

    private void showStepSummary() {
        Label errorLabel = new Label("");
        errorLabel.getStyleClass().add("error-label");

        Label cardTitle = new Label("Riepilogo prenotazione");
        cardTitle.getStyleClass().add("field-label");

        Label timeIcon = new Label("🕐 " + selectedSlot.getStartTime().getHour() + ":" +
                String.format("%02d", selectedSlot.getStartTime().getMinute()));
        timeIcon.getStyleClass().add("small-label");
        HBox timeRow = new HBox(timeIcon);
        timeRow.setAlignment(Pos.CENTER_RIGHT);

        GridPane grid = new GridPane();
        grid.setHgap(20); grid.setVgap(12);
        addSummaryRow(grid, 0, "Materia:",         selectedSubject.getName());
        addSummaryRow(grid, 1, "Tutor:",
                selectedTutor.getName() + " " + selectedTutor.getSurname());
        addSummaryRow(grid, 2, "Giorno:",
                selectedSlot.getDate().getDayOfMonth() + "-" +
                        selectedSlot.getDate().getMonthValue() + "-" +
                        selectedSlot.getDate().getYear());
        addSummaryRow(grid, 3, "Slot:",
                selectedSlot.getStartTime() + " - " + selectedSlot.getEndTime());

        Button bookBtn = new Button("Prenota");
        bookBtn.getStyleClass().add("button");
        bookBtn.setPrefWidth(140); bookBtn.setPrefHeight(40);
        bookBtn.setOnAction(e -> confirmBooking(errorLabel));

        HBox btnRow = new HBox(bookBtn);
        btnRow.setAlignment(Pos.CENTER);

        VBox card = new VBox(14, timeRow, cardTitle, grid, errorLabel, btnRow);
        card.getStyleClass().add("summary-card");
        card.setMaxWidth(420);

        VBox content = new VBox(card);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(20));

        BorderPane root = buildShell("Indietro", false, null);
        // Override back → torna allo slot
        HBox topBar = (HBox) root.getTop();
        ((Button) topBar.getChildren().get(0)).setOnAction(e -> showStepSlot());

        VBox center = new VBox(content);
        center.getStyleClass().add("brainbank-background");
        center.setAlignment(Pos.CENTER);
        root.setCenter(center);

        stage.setScene(GUIUtils.createScene(root));
        stage.show();
    }

    private void confirmBooking(Label errorLabel) {
        Student student = (Student) SessionManager.getInstance().getLoggedUser();
        StudentBean sb = new StudentBean(student.getId(), student.getName(),
                student.getSurname(), student.getEmail());
        try {
            bookingController.createBooking(
                    new BookingRequestBean(sb, selectedTutor, selectedSubject, selectedSlot));
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Prenotazione confermata");
            alert.setHeaderText(null);
            alert.setContentText("✓ Prenotazione effettuata con successo!");
            alert.showAndWait();
            MainGUI.showDashboardStudent();
        } catch (DAOException | BookingException e) {
            errorLabel.setText("Errore: " + e.getMessage());
        }
    }

    // ── Shell comune ─────────────────────────────────────────────────────

    private void buildAndShow(String backLabel, boolean backToDash,
                              Runnable onNext, javafx.scene.Node content) {
        BorderPane root = buildShell(backLabel, backToDash, onNext);
        VBox center = new VBox(content);
        center.getStyleClass().add("brainbank-background");
        center.setAlignment(Pos.CENTER);
        root.setCenter(center);
        stage.setScene(GUIUtils.createScene(root));
        stage.show();
    }

    private BorderPane buildShell(String backLabel, boolean backToDash, Runnable onNext) {
        BorderPane shell = new BorderPane();
        shell.getStyleClass().add("brainbank-background");

        HBox topBar = new HBox();
        topBar.getStyleClass().add("page-topbar");
        topBar.setAlignment(Pos.CENTER);

        Button backBtn = new Button("⟪  " + backLabel);
        backBtn.getStyleClass().add("back-button");
        if (backToDash) backBtn.setOnAction(e -> MainGUI.showDashboardStudent());

        Label title = new Label("Prenota Lezione");
        title.getStyleClass().add("page-title");
        title.setMaxWidth(Double.MAX_VALUE);
        title.setAlignment(Pos.CENTER);
        HBox.setHgrow(title, Priority.ALWAYS);

        ImageView logoView = new ImageView(new Image(
                getClass().getResourceAsStream("/images/logo.png"), 60, 60, true, true));
        logoView.setFitHeight(38); logoView.setPreserveRatio(true); logoView.setSmooth(true);

        topBar.getChildren().add(backBtn);
        topBar.getChildren().add(title);

        if (onNext != null) {
            Button nextBtn = new Button("Next  ⟫");
            nextBtn.getStyleClass().add("next-button");
            nextBtn.setOnAction(e -> onNext.run());
            HBox rightBox = new HBox(12, nextBtn, logoView);
            rightBox.setAlignment(Pos.CENTER_RIGHT);
            topBar.getChildren().add(rightBox);
        } else {
            topBar.getChildren().add(logoView);
        }

        shell.setTop(topBar);
        return shell;
    }

    // ── Helpers ──────────────────────────────────────────────────────────

    private ToggleButton buildToggleCard(String text, Object userData, ToggleGroup group) {
        ToggleButton card = new ToggleButton(text);
        card.getStyleClass().add("toggle-card");
        card.setToggleGroup(group);
        card.setUserData(userData);
        card.setPrefWidth(300); card.setPrefHeight(38);
        return card;
    }

    private ScrollPane buildTransparentScroll(javafx.scene.Node content, double height) {
        ScrollPane scroll = new ScrollPane(content);
        scroll.getStyleClass().add("transparent-scroll");
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(height);
        return scroll;
    }

    private void addSummaryRow(GridPane grid, int row, String label, String value) {
        Label lbl = new Label(label);
        lbl.getStyleClass().add("small-label");
        Label val = new Label(value);
        val.getStyleClass().add("register-label");
        grid.add(lbl, 0, row); grid.add(val, 1, row);
    }

    private ListCell<SubjectBean> subjectCell() {
        return new ListCell<>() {
            @Override protected void updateItem(SubjectBean item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        };
    }
}
