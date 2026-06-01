package it.ispwproject.brainbank.controller.gui;

import it.ispwproject.brainbank.bean.*;
import it.ispwproject.brainbank.controller.applicativo.BookingController;
import it.ispwproject.brainbank.exception.BookingException;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.model.Student;
import it.ispwproject.brainbank.pattern.singleton.SessionManager;
import it.ispwproject.brainbank.view.gui.BookLessonGUIView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.util.Duration;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BookLessonGUI {

    private final javafx.stage.Stage stage;
    private final BookingController  bookingController = new BookingController();
    private final BookLessonGUIView  view = new BookLessonGUIView();

    private List<SubjectBean> allSubjects = List.of();   // lista completa, mai sovrascritta

    private SubjectBean  selectedSubject;
    private TutorBean    selectedTutor;
    private TimeSlotBean selectedSlot;

    // ToggleGroup ricreati a ogni selezione a cascata
    private ToggleGroup tutorGroup = new ToggleGroup();
    private ToggleGroup slotGroup  = new ToggleGroup();

    public BookLessonGUI(javafx.stage.Stage stage) { this.stage = stage; }

    // ────────────────────────────────────────────────────────────────────────
    // Entry point
    // ────────────────────────────────────────────────────────────────────────

    public void show() {
        loadSubjects();
        bindSubjectField();
        bindSubjectList();
        bindBookButton();

        stage.setScene(GUIUtils.createScene(view.buildRoot()));
        stage.show();
    }

    // ────────────────────────────────────────────────────────────────────────
    // Caricamento dati iniziale
    // ────────────────────────────────────────────────────────────────────────

    private void loadSubjects() {
        try {
            allSubjects = bookingController.getAvailableSubjects();
            view.subjectList.getItems().setAll(allSubjects);
        } catch (DAOException e) {
            view.setError("Errore caricamento materie: " + e.getMessage());
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    // Binding listener
    // ────────────────────────────────────────────────────────────────────────

    private void bindSubjectField() {
        view.subjectField.textProperty().addListener((obs, oldVal, newVal) -> {
            // Non resettare se il testo corrisponde alla materia già selezionata
            if (selectedSubject != null && selectedSubject.getName().equals(newVal)) return;

            selectedSubject = null;
            view.bookBtn.setDisable(true);
            resetTutorSection();
            resetSlotSection();

            if (newVal.isBlank()) {
                view.subjectList.getItems().setAll(allSubjects);
                view.updateSubjectListHeight(allSubjects.size());
                view.showSubjectList(true);
            } else {
                List<SubjectBean> filtered = allSubjects.stream()
                        .filter(s -> s.getName().toLowerCase().startsWith(newVal.toLowerCase()))
                        .toList();
                view.subjectList.getItems().setAll(filtered);
                view.updateSubjectListHeight(filtered.size());
                view.showSubjectList(!filtered.isEmpty());
            }
        });

        view.subjectField.focusedProperty().addListener((obs, oldVal, focused) -> {
            if (focused && view.subjectField.getText().isBlank()) {
                view.subjectList.getItems().setAll(allSubjects);
                view.updateSubjectListHeight(allSubjects.size());
                view.showSubjectList(true);
            }
        });
    }

    private void bindSubjectList() {
        view.subjectList.setOnMouseClicked(e -> {
            SubjectBean sel = view.subjectList.getSelectionModel().getSelectedItem();
            if (sel == null) return;
            onSubjectSelected(sel);
        });
    }

    private void bindBookButton() {
        view.bookBtn.setOnAction(e -> {
            if (selectedSubject == null || selectedTutor == null || selectedSlot == null) {
                view.setError("Completa tutte le selezioni.");
                return;
            }
            try {
                BookingRequestBean request = buildRequest();
                bookingController.prepareBookingSummary(request);
                showCountdownDialog(request);
            } catch (DAOException | BookingException ex) {
                view.setError("Errore: " + ex.getMessage());
            }
        });
    }

    // ────────────────────────────────────────────────────────────────────────
    // Gestione cascata selezioni
    // ────────────────────────────────────────────────────────────────────────

    private void onSubjectSelected(SubjectBean sel) {
        selectedSubject = sel;
        view.subjectField.setText(sel.getName());
        view.showSubjectList(false);
        view.setStepDone(view.step1Dot);
        loadTutors(sel);
    }

    private void loadTutors(SubjectBean subject) {
        try {
            List<TutorBean> all = bookingController.getTutorsBySubject(subject);
            tutorGroup = new ToggleGroup();
            view.tutorList.getChildren().clear();
            view.setTutorSectionEnabled(true);

            if (all.isEmpty()) {
                view.tutorList.getChildren().add(view.buildHintLabel("Nessun tutor disponibile"));
                return;
            }

            // preferiti prima
            List<TutorBean> sorted = new ArrayList<>(all);
            sorted.sort((a, b) -> Boolean.compare(!a.isFavourite(), !b.isFavourite()));

            for (TutorBean t : sorted) {
                view.tutorList.getChildren().add(
                        view.buildTutorRow(t, tutorGroup,
                                () -> showTutorBio(t),
                                () -> toggleFavourite(t),
                                t.isFavourite()));
            }

            tutorGroup.selectedToggleProperty().addListener((o, oldT, newT) -> {
                if (newT == null) return;
                selectedTutor = (TutorBean) newT.getUserData();
                view.setStepDone(view.step2Dot);
                view.setStepPending(view.step3Dot);
                loadSlots(selectedTutor);
            });

        } catch (DAOException ex) {
            view.setError("Errore: " + ex.getMessage());
        }
    }

    private void loadSlots(TutorBean tutor) {
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            List<TimeSlotBean> slots = bookingController.getTutorAvailability(tutor)
                    .stream().filter(TimeSlotBean::isAvailable).toList();

            slotGroup = new ToggleGroup();
            view.slotList.getChildren().clear();
            view.setSlotSectionEnabled(true);
            selectedSlot = null;
            view.bookBtn.setDisable(true);

            if (slots.isEmpty()) {
                view.slotList.getChildren().add(view.buildHintLabel("Nessuno slot disponibile"));
                return;
            }

            for (TimeSlotBean s : slots) {
                view.slotList.getChildren().add(
                        view.buildToggle(
                                s.getDate().format(fmt) + "   " +
                                        s.getStartTime() + " – " + s.getEndTime(),
                                s, slotGroup));
            }

            slotGroup.selectedToggleProperty().addListener((o, oldS, newS) -> {
                if (newS == null) return;
                selectedSlot = (TimeSlotBean) newS.getUserData();
                view.setStepDone(view.step3Dot);
                view.bookBtn.setDisable(false);
            });

        } catch (DAOException ex) {
            view.setError("Errore: " + ex.getMessage());
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    // Azioni sui tutor
    // ────────────────────────────────────────────────────────────────────────

    private void showTutorBio(TutorBean t) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Bio tutor");
        alert.setHeaderText(t.getName() + " " + t.getSurname());
        alert.setContentText(
                t.getBio() == null || t.getBio().isBlank()
                        ? "Bio non disponibile."
                        : t.getBio());
        alert.showAndWait();
    }

    private void toggleFavourite(TutorBean t) {
        try {
            int studentId = SessionManager.getInstance().getLoggedUser().getId();
            if (t.isFavourite()) {
                bookingController.removeTutorFromFavourites(t.getId());
                t.setFavourite(false);
            } else {
                bookingController.addTutorToFavourites(t.getId());
                t.setFavourite(true);
            }
        } catch (DAOException ex) {
            view.setError("Errore: " + ex.getMessage());
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    // Dialogo countdown e conferma
    // ────────────────────────────────────────────────────────────────────────

    private void showCountdownDialog(BookingRequestBean request) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        int[] secondsLeft = {180};

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Conferma prenotazione");
        confirm.setHeaderText("⏱ Slot riservato per 3 minuti");

        Label contentLabel = new Label(buildSummaryText(fmt, 3, 0));
        confirm.getDialogPane().setContent(contentLabel);

        Timeline countdown = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {
            secondsLeft[0]--;
            int min = secondsLeft[0] / 60;
            int sec = secondsLeft[0] % 60;
            contentLabel.setText(buildSummaryText(fmt, min, sec));
            if (secondsLeft[0] <= 0) {
                confirm.close();
                try { bookingController.releaseSlot(selectedSlot.getId()); }
                catch (DAOException ex) { /* ignora */ }
                Platform.runLater(() -> view.setError("Tempo scaduto. Lo slot è stato rilasciato."));
            }
        }));
        countdown.setCycleCount(180);
        countdown.play();

        confirm.showAndWait().ifPresent(r -> {
            countdown.stop();
            if (r == ButtonType.OK) {
                confirmBooking();
            } else {
                try { bookingController.releaseSlot(selectedSlot.getId()); }
                catch (DAOException ex) { view.setError("Errore: " + ex.getMessage()); }
            }
        });
    }

    private String buildSummaryText(DateTimeFormatter fmt, int min, int sec) {
        return "Materia:  " + selectedSubject.getName() + "\n" +
                "Tutor:    " + selectedTutor.getName() + " " + selectedTutor.getSurname() + "\n" +
                "Giorno:   " + selectedSlot.getDate().format(fmt) + "\n" +
                "Orario:   " + selectedSlot.getStartTime() + " – " + selectedSlot.getEndTime() + "\n\n" +
                String.format("Tempo rimasto: %d:%02d", min, sec);
    }

    private void confirmBooking() {
        try {
            bookingController.createBooking(buildRequest());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Prenotazione confermata");
            alert.setHeaderText(null);
            alert.setContentText("✓ Prenotazione effettuata con successo!");
            alert.showAndWait();
            MainGUI.showDashboardStudent();
        } catch (BookingException e) {
            try { bookingController.releaseSlot(selectedSlot.getId()); }
            catch (DAOException ex) { /* ignora */ }
            view.setError("Errore: " + e.getMessage());
        } catch (DAOException e) {
            view.setError("Errore: " + e.getMessage());
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    // Reset sezioni
    // ────────────────────────────────────────────────────────────────────────

    private void resetTutorSection() {
        view.tutorList.getChildren().setAll(view.buildHintLabel("Seleziona prima una materia"));
        tutorGroup.getToggles().clear();
        view.setTutorSectionEnabled(false);
        resetSlotSection();
    }

    private void resetSlotSection() {
        view.slotList.getChildren().setAll(view.buildHintLabel("Seleziona prima un tutor"));
        slotGroup.getToggles().clear();
        view.setSlotSectionEnabled(false);
        selectedSlot = null;
    }

    // ────────────────────────────────────────────────────────────────────────
    // Utility
    // ────────────────────────────────────────────────────────────────────────

    private BookingRequestBean buildRequest() {
        Student s = (Student) SessionManager.getInstance().getLoggedUser();
        StudentBean sb = new StudentBean(s.getId(), s.getName(), s.getSurname(), s.getEmail());
        return new BookingRequestBean(sb, selectedTutor, selectedSubject, selectedSlot);
    }
}