package it.ispwproject.brainbank.controller.gui;

import it.ispwproject.brainbank.bean.*;
import it.ispwproject.brainbank.controller.applicativo.BookingController;
import it.ispwproject.brainbank.exception.BookingException;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.model.Student;
import it.ispwproject.brainbank.pattern.singleton.SessionManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class BookLessonGUI {

    private final Stage             stage;
    private final BookingController bookingController = new BookingController();

    private SubjectBean  selectedSubject;
    private TutorBean    selectedTutor;
    private TimeSlotBean selectedSlot;

    public BookLessonGUI(Stage stage) { this.stage = stage; }

    public void show() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("brainbank-background");
        root.setTop(buildTopBar());

        // ── Lifeline con GridPane ─────────────────────────────────────────
        Region step1Dot = stepDot(false);
        Region step2Dot = stepDot(false);
        Region step3Dot = stepDot(false);

        GridPane lifeline = new GridPane();
        lifeline.setPadding(new Insets(30, 12, 0, 20));
        lifeline.setMinWidth(90);

        // Col 0: pallino + linea centrati | Col 1: testo
        ColumnConstraints dotCol = new ColumnConstraints(20);
        dotCol.setHalignment(javafx.geometry.HPos.CENTER);
        ColumnConstraints txtCol = new ColumnConstraints();
        lifeline.getColumnConstraints().addAll(dotCol, txtCol);

        // Step 1
        Label lbl1 = stepLabel("Materia");
        lifeline.add(step1Dot, 0, 0);
        lifeline.add(lbl1,     1, 0);

        // Linea 1→2
        Region line1 = stepLine();
        lifeline.add(line1, 0, 1);
        GridPane.setHalignment(line1, javafx.geometry.HPos.CENTER);

        // Step 2
        Label lbl2 = stepLabel("Tutor");
        lifeline.add(step2Dot, 0, 2);
        lifeline.add(lbl2,     1, 2);

        // Linea 2→3
        Region line2 = stepLine();
        lifeline.add(line2, 0, 3);
        GridPane.setHalignment(line2, javafx.geometry.HPos.CENTER);

        // Step 3
        Label lbl3 = stepLabel("Orario");
        lifeline.add(step3Dot, 0, 4);
        lifeline.add(lbl3,     1, 4);

        VBox form = new VBox(4);
        form.setAlignment(Pos.TOP_CENTER);
        form.setPadding(new Insets(20, 0, 0, 0));
        form.setPrefWidth(540);

        Label errorLabel = new Label("");
        errorLabel.getStyleClass().add("error-label");
        errorLabel.setWrapText(true);

        // ── 1. Materia ────────────────────────────────────────────────────
        VBox subjectSection = buildSection("1.  Materia");

        TextField subjectField = new TextField();
        subjectField.getStyleClass().add("text-field");
        subjectField.setPromptText("Cerca materia...");
        subjectField.setPrefHeight(40);

        ListView<SubjectBean> subjectList = new ListView<>();
        subjectList.getStyleClass().add("list-view");
        subjectList.setPrefHeight(-1);
        subjectList.setVisible(false); subjectList.setManaged(false);
        subjectList.setCellFactory(lv -> subjectCell());

        List<SubjectBean> allSubjects;
        try { allSubjects = bookingController.getAvailableSubjects(); }
        catch (DAOException e) { allSubjects = List.of(); }
        final List<SubjectBean> subjects = allSubjects;

        subjectSection.getChildren().addAll(subjectField, subjectList);

        // ── 2. Tutor ──────────────────────────────────────────────────────
        VBox tutorSection = buildSection("2.  Tutor");
        tutorSection.setOpacity(0.5);
        ToggleGroup tutorGroup = new ToggleGroup();
        final ToggleGroup[] tutorGroupRef = {tutorGroup};
        VBox tutorList = new VBox(6);
        Label tutorHint = hintLabel("Seleziona prima una materia");
        tutorList.getChildren().add(tutorHint);
        tutorSection.getChildren().add(tutorList);

        // ── 3. Orario ─────────────────────────────────────────────────────
        VBox slotSection = buildSection("3.  Orario");
        slotSection.setOpacity(0.5);
        ToggleGroup slotGroup = new ToggleGroup();
        final ToggleGroup[] slotGroupRef = {slotGroup};
        VBox slotList = new VBox(6);
        Label slotHint = hintLabel("Seleziona prima un tutor");
        slotList.getChildren().add(slotHint);
        slotSection.getChildren().add(slotList);

        // ── Bottone Prenota ───────────────────────────────────────────────
        Button bookBtn = new Button("Prenota");
        bookBtn.getStyleClass().add("button");
        bookBtn.setPrefWidth(180); bookBtn.setPrefHeight(44);
        bookBtn.setDisable(true);

        HBox btnRow = new HBox(bookBtn);
        btnRow.setAlignment(Pos.CENTER);

        // ── Logica cascata ────────────────────────────────────────────────

        subjectField.textProperty().addListener((obs, oldVal, newVal) -> {
            // Non resettare se il testo corrisponde alla materia già selezionata
            if (selectedSubject != null && selectedSubject.getName().equals(newVal)) return;
            selectedSubject = null;
            bookBtn.setDisable(true);
            // Reset tutor e slot
            tutorList.getChildren().setAll(hintLabel("Seleziona prima una materia"));
            tutorGroup.getToggles().clear();
            tutorSection.setOpacity(0.5);
            slotList.getChildren().setAll(hintLabel("Seleziona prima un tutor"));
            slotGroup.getToggles().clear();
            slotSection.setOpacity(0.5);

            if (newVal.isBlank()) {
                subjectList.setVisible(false); subjectList.setManaged(false);
            } else {
                List<SubjectBean> filtered = subjects.stream()
                        .filter(s -> s.getName().toLowerCase().startsWith(newVal.toLowerCase()))
                        .toList();
                subjectList.getItems().setAll(filtered);
                subjectList.setPrefHeight(Math.min(filtered.size() * 36 + 2, 150));
                subjectList.setVisible(!filtered.isEmpty());
                subjectList.setManaged(!filtered.isEmpty());
            }
        });

        // Aggiorna dot step 1 quando si seleziona materia
        subjectList.setOnMouseClicked(e -> {
            SubjectBean sel = subjectList.getSelectionModel().getSelectedItem();
            if (sel == null) return;
            selectedSubject = sel;
            subjectField.setText(sel.getName());
            subjectList.setVisible(false); subjectList.setManaged(false);
            setStepDone(step1Dot);

            tutorList.getChildren().clear();
            tutorGroupRef[0] = new ToggleGroup();
            tutorGroupRef[0].selectedToggleProperty().addListener((o, oldT, newT) -> {
                if (newT == null) return;
                selectedTutor = (TutorBean) newT.getUserData();
                setStepDone(step2Dot);
                setStepPending(step3Dot);
                selectedSlot = null;
                bookBtn.setDisable(true);
                slotList.getChildren().clear();
                slotGroupRef[0] = new ToggleGroup();
                slotGroupRef[0].selectedToggleProperty().addListener((oo, oS, nS) -> {
                    if (nS == null) return;
                    selectedSlot = (TimeSlotBean) nS.getUserData();
                    setStepDone(step3Dot);
                    bookBtn.setDisable(false);
                });
                slotSection.setOpacity(1.0);
                try {
                    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    List<TimeSlotBean> slots = bookingController
                            .getTutorAvailability(selectedTutor)
                            .stream().filter(TimeSlotBean::isAvailable).toList();
                    if (slots.isEmpty()) {
                        slotList.getChildren().add(hintLabel("Nessuno slot disponibile"));
                    } else {
                        for (TimeSlotBean s : slots)
                            slotList.getChildren().add(buildToggle(
                                    s.getDate().format(fmt) + "   " +
                                            s.getStartTime() + " – " + s.getEndTime(), s, slotGroupRef[0]));
                    }
                } catch (DAOException ex2) { errorLabel.setText("Errore: " + ex2.getMessage()); }
            });
            tutorSection.setOpacity(1.0);
            try {
                List<TutorBean> tutors = bookingController.getTutorsBySubject(sel);
                if (tutors.isEmpty()) {
                    tutorList.getChildren().add(hintLabel("Nessun tutor disponibile"));
                } else {
                    // Ordina: preferiti prima
                    List<TutorBean> sorted = new java.util.ArrayList<>(tutors);
                    sorted.sort((a, b2) -> Boolean.compare(!a.isFavourite(), !b2.isFavourite()));
                    for (TutorBean t : sorted)
                        tutorList.getChildren().add(buildTutorRow(t, tutorGroupRef[0], errorLabel));
                }
            } catch (DAOException ex) { errorLabel.setText("Errore: " + ex.getMessage()); }
        });

        bookBtn.setOnAction(e -> {
            if (selectedSubject == null || selectedTutor == null || selectedSlot == null) {
                errorLabel.setText("Completa tutte le selezioni.");
                return;
            }
            try {
                BookingRequestBean request = new BookingRequestBean(
                        new StudentBean(
                                ((Student) SessionManager.getInstance().getLoggedUser()).getId(),
                                ((Student) SessionManager.getInstance().getLoggedUser()).getName(),
                                ((Student) SessionManager.getInstance().getLoggedUser()).getSurname(),
                                ((Student) SessionManager.getInstance().getLoggedUser()).getEmail()),
                        selectedTutor, selectedSubject, selectedSlot);

                // Riserva lo slot — lancia BookingException se già occupato
                bookingController.prepareBookingSummary(request);

                // Mostra dialog con countdown
                showCountdownDialog(request, errorLabel);

            } catch (DAOException | BookingException ex) {
                errorLabel.setText("Errore: " + ex.getMessage());
            }
        });

        form.getChildren().addAll(subjectSection, tutorSection, slotSection, errorLabel, btnRow);

        Region rightSpacer = new Region();
        rightSpacer.setPrefWidth(90); // stessa larghezza della lifeline
        HBox.setHgrow(rightSpacer, Priority.NEVER);

        HBox formWrapper = new HBox(lifeline, form, rightSpacer);
        formWrapper.setAlignment(Pos.TOP_CENTER);
        formWrapper.getStyleClass().add("brainbank-background");
        formWrapper.setPadding(new Insets(20, 0, 0, 0));
        HBox.setHgrow(form, Priority.ALWAYS);

        ScrollPane scroll = new ScrollPane(formWrapper);
        scroll.getStyleClass().add("transparent-scroll");
        scroll.setFitToWidth(true);

        root.setCenter(scroll);

        stage.setScene(GUIUtils.createScene(root));
        stage.show();
    }

    private Region stepDot(boolean done) {
        Region dot = new Region();
        dot.getStyleClass().add(done ? "step-dot-done" : "step-dot");
        return dot;
    }

    private void setStepDone(Region dot) {
        dot.getStyleClass().setAll("step-dot-done");
    }

    private void setStepPending(Region dot) {
        dot.getStyleClass().setAll("step-dot");
    }

    private Label stepLabel(String text) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #4B4B4B; -fx-padding: 0 0 0 6;");
        return lbl;
    }

    private Region stepLine() {
        Region line = new Region();
        line.setPrefWidth(2); line.setMaxWidth(2);
        line.setPrefHeight(20);
        line.setStyle("-fx-background-color: #b8d4ea;");
        return line;
    }

    private VBox buildSection(String title) {
        VBox section = new VBox(10);
        section.getStyleClass().add("info-card");
        section.setMaxWidth(500);
        Label lbl = new Label(title);
        lbl.getStyleClass().add("small-label");
        section.getChildren().add(lbl);
        return section;
    }

    private Label hintLabel(String text) {
        Label lbl = new Label(text);
        lbl.getStyleClass().add("info-text");
        lbl.setStyle("-fx-text-fill: #aaa; -fx-font-style: italic;");
        return lbl;
    }

    private HBox buildTutorRow(TutorBean t, ToggleGroup group, Label errorLabel) {
        // Toggle principale
        ToggleButton toggle = new ToggleButton(t.getName() + " " + t.getSurname());
        toggle.getStyleClass().add("toggle-card");
        toggle.setToggleGroup(group);
        toggle.setUserData(t);
        toggle.setPrefHeight(38);
        HBox.setHgrow(toggle, Priority.ALWAYS);

        // Stella cliccabile
        boolean[] fav = {t.isFavourite()};
        Button star = new Button("★");
        star.getStyleClass().add("star-button");
        star.setPrefWidth(36);
        star.setPrefHeight(38);
        star.setStyle("-fx-text-fill: " + (fav[0] ? "#F1C40F" : "#CCCCCC") + ";");

        star.setOnAction(e -> {
            try {
                int studentId = SessionManager.getInstance().getLoggedUser().getId();
                if (fav[0]) {
                    bookingController.removeTutorFromFavourites(studentId, t.getId());
                    fav[0] = false;
                    star.setStyle("-fx-text-fill: #CCCCCC;");
                } else {
                    bookingController.addTutorToFavourites(studentId, t.getId());
                    fav[0] = true;
                    star.setStyle("-fx-text-fill: #F1C40F;");
                }
            } catch (DAOException ex) {
                errorLabel.setText("Errore: " + ex.getMessage());
            }
        });

        HBox row = new HBox(4, toggle, star);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMaxWidth(Double.MAX_VALUE);
        return row;
    }

    private ToggleButton buildToggle(String text, Object userData, ToggleGroup group) {
        ToggleButton btn = new ToggleButton(text);
        btn.getStyleClass().add("toggle-card");
        btn.setToggleGroup(group);
        btn.setUserData(userData);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPrefHeight(38);
        return btn;
    }

    private void showCountdownDialog(BookingRequestBean request, Label errorLabel) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        int[] secondsLeft = {180}; // 3 minuti

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Conferma prenotazione");
        confirm.setHeaderText("⏱ Slot riservato per 3 minuti");

        Label contentLabel = new Label(
                "Materia:  " + selectedSubject.getName() + "\n" +
                        "Tutor:    " + selectedTutor.getName() + " " + selectedTutor.getSurname() + "\n" +
                        "Giorno:   " + selectedSlot.getDate().format(fmt) + "\n" +
                        "Orario:   " + selectedSlot.getStartTime() + " – " + selectedSlot.getEndTime() + "\n\n" +
                        "Tempo rimasto: 3:00");
        confirm.getDialogPane().setContent(contentLabel);

        Timeline countdown = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {
            secondsLeft[0]--;
            int min = secondsLeft[0] / 60;
            int sec = secondsLeft[0] % 60;
            contentLabel.setText(
                    "Materia:  " + selectedSubject.getName() + "\n" +
                            "Tutor:    " + selectedTutor.getName() + " " + selectedTutor.getSurname() + "\n" +
                            "Giorno:   " + selectedSlot.getDate().format(fmt) + "\n" +
                            "Orario:   " + selectedSlot.getStartTime() + " – " + selectedSlot.getEndTime() + "\n\n" +
                            String.format("Tempo rimasto: %d:%02d", min, sec));
            if (secondsLeft[0] <= 0) {
                confirm.close();
                try { bookingController.releaseSlot(selectedSlot.getId()); }
                catch (DAOException ex) { /* ignora */ }
                Platform.runLater(() -> errorLabel.setText("Tempo scaduto. Lo slot è stato rilasciato."));
            }
        }));
        countdown.setCycleCount(180);
        countdown.play();

        confirm.showAndWait().ifPresent(r -> {
            countdown.stop();
            if (r == ButtonType.OK) {
                confirmBooking(errorLabel);
            } else {
                try { bookingController.releaseSlot(selectedSlot.getId()); }
                catch (DAOException ex) { errorLabel.setText("Errore: " + ex.getMessage()); }
            }
        });
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
        } catch (BookingException e) {
            try { bookingController.releaseSlot(selectedSlot.getId()); }
            catch (DAOException ex) { /* ignora */ }
            errorLabel.setText("Errore: " + e.getMessage());
        } catch (DAOException e) {
            errorLabel.setText("Errore: " + e.getMessage());
        }
    }

    private HBox buildTopBar() {
        HBox bar = new HBox();
        bar.getStyleClass().add("navbar");
        bar.setAlignment(Pos.CENTER_LEFT);

        Button backBtn = new Button("⟪  Indietro");
        backBtn.getStyleClass().add("back-button");
        backBtn.setOnAction(e -> MainGUI.showDashboardStudent());
        HBox left = new HBox(backBtn);
        left.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(left, Priority.ALWAYS);

        Label title = new Label("Prenota Lezione");
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

    private ListCell<SubjectBean> subjectCell() {
        return new ListCell<>() {
            @Override protected void updateItem(SubjectBean item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        };
    }
}