package it.ispwproject.brainbank.view.gui;

import it.ispwproject.brainbank.bean.BookingResponseBean;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.layout.FlowPane;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.BiConsumer;

public class ViewBookingsGUIView extends PageGUIView {

    public BorderPane buildRoot(Runnable onBack) {
        return buildShell("Le mie prenotazioni", onBack);
    }

    public void buildContent(BorderPane root,
                             List<BookingResponseBean> confirmed,
                             List<BookingResponseBean> cancelled,
                             List<BookingResponseBean> past,
                             Label errorLabel,
                             BiConsumer<BookingResponseBean, Integer> onCancel,
                             int studentId) {
        VBox content = new VBox(12);
        content.setPadding(new Insets(24));
        content.setAlignment(Pos.TOP_CENTER);

        ToggleButton btnConfirmed = new ToggleButton("Confermate (" + confirmed.size() + ")");
        ToggleButton btnCancelled = new ToggleButton("Cancellate (" + cancelled.size() + ")");
        ToggleButton btnPast      = new ToggleButton("Scadute (" + past.size() + ")");

        btnConfirmed.getStyleClass().add("toggle-card");
        btnCancelled.getStyleClass().addAll("toggle-card", "cancelled");
        btnPast.getStyleClass().addAll("toggle-card", "expired");

        btnConfirmed.setPrefWidth(180); btnConfirmed.setPrefHeight(36);
        btnCancelled.setPrefWidth(180); btnCancelled.setPrefHeight(36);
        btnPast.setPrefWidth(180);      btnPast.setPrefHeight(36);

        ToggleGroup group = new ToggleGroup();
        btnConfirmed.setToggleGroup(group);
        btnCancelled.setToggleGroup(group);
        btnPast.setToggleGroup(group);
        btnConfirmed.setSelected(true);

        HBox toggleBar = new HBox(8, btnConfirmed, btnCancelled, btnPast);
        toggleBar.setAlignment(Pos.CENTER);
        toggleBar.setMaxWidth(640);

        VBox listBox = new VBox(12);
        listBox.setAlignment(Pos.TOP_CENTER);

        // Materie disponibili nello storico per i filtri
        List<String> subjects = past.stream()
                .map(b -> b.getSubject().getName())
                .distinct()
                .sorted()
                .toList();

        // Barra filtri materia (visibile solo nella tab Scadute)
        FlowPane subjectBar = buildSubjectFilterBar(subjects);
        subjectBar.setVisible(false);
        subjectBar.setManaged(false);

        // Filtro materia selezionato
        final String[] selectedSubject = {null};

        // Aggiorna filtri materia — array trick per self-reference nel lambda
        Runnable[] refreshSubjectBar = {null};
        refreshSubjectBar[0] = () -> {
            subjectBar.getChildren().clear();
            ToggleGroup subjectGroup = new ToggleGroup();

            ToggleButton btnAll = new ToggleButton("Tutte");
            btnAll.getStyleClass().addAll("toggle-card", "expired");
            btnAll.setToggleGroup(subjectGroup);
            btnAll.setPrefHeight(30);
            btnAll.setSelected(selectedSubject[0] == null);
            btnAll.setOnAction(e -> { selectedSubject[0] = null; refreshPastList(listBox, past, null); });
            subjectBar.getChildren().add(btnAll);

            for (String s : subjects) {
                ToggleButton btn = new ToggleButton(s);
                btn.getStyleClass().addAll("toggle-card", "expired");
                btn.setToggleGroup(subjectGroup);
                btn.setPrefHeight(30);
                btn.setSelected(s.equals(selectedSubject[0]));
                btn.setOnAction(e -> { selectedSubject[0] = s; refreshPastList(listBox, past, s); });
                subjectBar.getChildren().add(btn);
            }
        };
        refreshSubjectBar[0].run();

        Runnable refreshList = () -> {
            listBox.getChildren().clear();
            subjectBar.setVisible(btnPast.isSelected());
            subjectBar.setManaged(btnPast.isSelected());

            if (btnConfirmed.isSelected()) {
                if (confirmed.isEmpty()) {
                    listBox.getChildren().add(emptyLabel("Non hai prenotazioni confermate."));
                } else {
                    for (BookingResponseBean b : confirmed)
                        listBox.getChildren().add(buildBookingCard(b, true, studentId, onCancel));
                }
            } else if (btnCancelled.isSelected()) {
                if (cancelled.isEmpty()) {
                    listBox.getChildren().add(emptyLabel("Non hai prenotazioni cancellate."));
                } else {
                    for (BookingResponseBean b : cancelled)
                        listBox.getChildren().add(buildBookingCard(b, false, studentId, onCancel));
                }
            } else {
                selectedSubject[0] = null;
                refreshSubjectBar[0].run();
                refreshPastList(listBox, past, null);
            }
        };

        refreshList.run();
        btnConfirmed.setOnAction(e -> refreshList.run());
        btnCancelled.setOnAction(e -> refreshList.run());
        btnPast.setOnAction(e -> refreshList.run());

        content.getChildren().addAll(toggleBar, subjectBar, listBox, errorLabel);
        root.setCenter(transparentScroll(content));
    }

    private void refreshPastList(VBox listBox, List<BookingResponseBean> past, String subjectFilter) {
        listBox.getChildren().clear();

        List<BookingResponseBean> filtered = subjectFilter == null ? past :
                past.stream().filter(b -> b.getSubject().getName().equals(subjectFilter)).toList();

        if (filtered.isEmpty()) {
            listBox.getChildren().add(emptyLabel("Nessuna lezione scaduta."));
            return;
        }

        // Raggruppa per materia
        List<String> subjects = filtered.stream()
                .map(b -> b.getSubject().getName())
                .distinct()
                .sorted()
                .toList();

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (String subject : subjects) {
            List<BookingResponseBean> group = filtered.stream()
                    .filter(b -> b.getSubject().getName().equals(subject))
                    .sorted((a, b) -> b.getTimeSlot().getDate().compareTo(a.getTimeSlot().getDate()))
                    .toList();

            // Header materia
            HBox subjectHeader = new HBox(8);
            subjectHeader.setAlignment(Pos.CENTER_LEFT);
            subjectHeader.setMaxWidth(640);
            subjectHeader.setPadding(new Insets(8, 0, 4, 0));

            Label subjectLabel = new Label(subject);
            subjectLabel.getStyleClass().add("small-label");
            subjectLabel.setStyle("-fx-font-weight: bold;");

            Label countLabel = new Label("(" + group.size() + " " +
                    (group.size() == 1 ? "lezione" : "lezioni") + ")");
            countLabel.getStyleClass().add("info-text");
            countLabel.setStyle("-fx-text-fill: #888;");

            subjectHeader.getChildren().addAll(subjectLabel, countLabel);
            listBox.getChildren().add(subjectHeader);

            // Card per ogni lezione
            VBox groupCard = new VBox(0);
            groupCard.getStyleClass().add("info-card");
            groupCard.setMaxWidth(640);

            for (int i = 0; i < group.size(); i++) {
                BookingResponseBean b = group.get(i);

                HBox row = new HBox(12);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setPadding(new Insets(8, 12, 8, 12));
                row.setMaxWidth(Double.MAX_VALUE);

                Label dateLbl = new Label(b.getTimeSlot().getDate().format(fmt));
                dateLbl.getStyleClass().add("register-label");
                dateLbl.setPrefWidth(90);

                Label timeLbl = new Label(b.getTimeSlot().getStartTime() +
                        " – " + b.getTimeSlot().getEndTime());
                timeLbl.getStyleClass().add("info-text");
                timeLbl.setPrefWidth(110);

                Label tutorLbl = new Label(b.getTutor().getName() + " " + b.getTutor().getSurname());
                tutorLbl.getStyleClass().add("register-label");
                HBox.setHgrow(tutorLbl, Priority.ALWAYS);

                row.getChildren().addAll(dateLbl, timeLbl, tutorLbl);

                if (b.getMeetLink() != null && !b.getMeetLink().isBlank()) {
                    Hyperlink meet = new Hyperlink("🎥");
                    meet.getStyleClass().add("hyperlink");
                    meet.setOnAction(e -> {
                        try { java.awt.Desktop.getDesktop().browse(new java.net.URI(b.getMeetLink())); }
                        catch (Exception ex) { /* ignora */ }
                    });
                    row.getChildren().add(meet);
                }

                groupCard.getChildren().add(row);

                // Separatore sottile tra righe (non sull'ultima)
                if (i < group.size() - 1) {
                    Region line = new Region();
                    line.setPrefHeight(1);
                    line.setMaxWidth(Double.MAX_VALUE);
                    line.setStyle("-fx-background-color: #eef2f6;");
                    line.setMouseTransparent(true);
                    groupCard.getChildren().add(line);
                }
            }

            listBox.getChildren().add(groupCard);
        }
    }

    private FlowPane buildSubjectFilterBar(List<String> subjects) {
        FlowPane bar = new FlowPane(6, 6);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setMaxWidth(640);
        bar.setPadding(new Insets(0, 0, 4, 0));
        return bar;
    }

    private Label emptyLabel(String text) {
        Label lbl = new Label(text);
        lbl.getStyleClass().add("register-label");
        return lbl;
    }

    private VBox buildBookingCard(BookingResponseBean b, boolean cancellable,
                                  int studentId,
                                  BiConsumer<BookingResponseBean, Integer> onCancel) {
        VBox card = new VBox(8);
        card.getStyleClass().add("info-card");
        card.setMaxWidth(640);

        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label dot = new Label("●");
        dot.getStyleClass().add(cancellable ? "success-label" : "error-label");
        dot.setStyle("-fx-font-size: 14px;");

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        Label dateTime = new Label(b.getTimeSlot().getDate().format(fmt) + "   " +
                b.getTimeSlot().getStartTime() + " – " + b.getTimeSlot().getEndTime());
        dateTime.getStyleClass().add("welcome-label");

        HBox dateRow = new HBox(8, dot, dateTime);
        dateRow.setAlignment(Pos.CENTER_LEFT);

        Label status = new Label(cancellable ? "Confermata" : "Cancellata");
        status.getStyleClass().add(cancellable ? "success-label" : "error-label");
        status.setStyle("-fx-font-weight: bold;");

        Label subject = new Label("Materia: " + b.getSubject().getName());
        subject.getStyleClass().add("small-label");

        Label tutor = new Label("Tutor: " + b.getTutor().getName() + " " + b.getTutor().getSurname());
        tutor.getStyleClass().add("register-label");

        info.getChildren().addAll(dateRow, status, subject, tutor);

        if (b.getTutor().getEmail() != null) {
            Label tutorEmail = new Label("Email  " + b.getTutor().getEmail());
            tutorEmail.getStyleClass().add("info-text");
            info.getChildren().add(tutorEmail);
        }

        if (cancellable || (b.getMeetLink() != null && !b.getMeetLink().isBlank())) {
            HBox bottomRow = new HBox();
            bottomRow.setAlignment(Pos.CENTER_LEFT);
            bottomRow.setMaxWidth(Double.MAX_VALUE);

            if (b.getMeetLink() != null && !b.getMeetLink().isBlank()) {
                Hyperlink meet = new Hyperlink("🎥  Apri Meet");
                meet.getStyleClass().add("hyperlink");
                meet.setOnAction(e -> {
                    try { java.awt.Desktop.getDesktop().browse(new java.net.URI(b.getMeetLink())); }
                    catch (Exception ex) { /* link non apribile */ }
                });
                bottomRow.getChildren().add(meet);
            }

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            bottomRow.getChildren().add(spacer);

            if (cancellable) {
                Button cancelBtn = new Button("Annulla");
                cancelBtn.getStyleClass().add("danger-button");
                cancelBtn.setOnAction(e -> onCancel.accept(b, studentId));
                bottomRow.getChildren().add(cancelBtn);
            }
            info.getChildren().add(bottomRow);
        }

        card.getChildren().add(info);
        return card;
    }
}