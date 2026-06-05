package it.ispwproject.brainbank.view.gui;

import it.ispwproject.brainbank.bean.TimeSlotBean;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ViewSlotsGUIView extends PageGUIView {

    public final Label errorLabel = buildErrorLabel();

    public void setError(String message) { errorLabel.setText(message); }
    public void clearError()             { errorLabel.setText(""); }

    public BorderPane buildRoot(Runnable onBack) {
        return buildShell("I miei slot", onBack);
    }

    public void buildContent(BorderPane root,
                             List<TimeSlotBean> disponibili,
                             List<TimeSlotBean> prenotati,
                             List<TimeSlotBean> passati,
                             Map<Integer, String> subjectBySlot,
                             Consumer<TimeSlotBean> onDelete) {
        VBox content = new VBox(12);
        content.setPadding(new Insets(24));
        content.setAlignment(Pos.TOP_CENTER);

        ToggleButton btnDisponibili = new ToggleButton("Disponibili (" + disponibili.size() + ")");
        ToggleButton btnPrenotati   = new ToggleButton("Prenotati (" + prenotati.size() + ")");
        ToggleButton btnPassati     = new ToggleButton("Scaduti (" + passati.size() + ")");

        btnDisponibili.getStyleClass().add("toggle-card");
        btnPrenotati.getStyleClass().addAll("toggle-card", "cancelled");
        btnPassati.getStyleClass().addAll("toggle-card", "expired");

        btnDisponibili.setPrefWidth(180); btnDisponibili.setPrefHeight(36);
        btnPrenotati.setPrefWidth(180);   btnPrenotati.setPrefHeight(36);
        btnPassati.setPrefWidth(180);     btnPassati.setPrefHeight(36);

        ToggleGroup group = new ToggleGroup();
        btnDisponibili.setToggleGroup(group);
        btnPrenotati.setToggleGroup(group);
        btnPassati.setToggleGroup(group);
        btnDisponibili.setSelected(true);

        HBox toggleBar = new HBox(8, btnDisponibili, btnPrenotati, btnPassati);
        toggleBar.setAlignment(Pos.CENTER);
        toggleBar.setMaxWidth(640);

        VBox listBox = new VBox(12);
        listBox.setAlignment(Pos.TOP_CENTER);

        Runnable refreshList = () -> {
            listBox.getChildren().clear();
            List<TimeSlotBean> current;
            String emptyMsg;
            boolean isPast;
            if (btnDisponibili.isSelected())    { current = disponibili; emptyMsg = "Nessuno slot disponibile."; isPast = false; }
            else if (btnPrenotati.isSelected()) { current = prenotati;   emptyMsg = "Nessuno slot prenotato.";   isPast = false; }
            else                                { current = passati;     emptyMsg = "Nessuno slot passato.";     isPast = true;  }

            if (current.isEmpty()) {
                Label empty = new Label(emptyMsg);
                empty.getStyleClass().add("register-label");
                listBox.getChildren().add(empty);
            } else {
                for (TimeSlotBean s : current)
                    listBox.getChildren().add(
                            buildSlotCard(s, subjectBySlot.get(s.getId()), isPast, onDelete));
            }
        };

        refreshList.run();
        btnDisponibili.setOnAction(e -> refreshList.run());
        btnPrenotati.setOnAction(e -> refreshList.run());
        btnPassati.setOnAction(e -> refreshList.run());

        if (disponibili.isEmpty() && prenotati.isEmpty() && passati.isEmpty()) {
            Label empty = new Label("Non hai ancora slot.");
            empty.getStyleClass().add("register-label");
            content.getChildren().addAll(toggleBar, empty);
        } else {
            content.getChildren().addAll(toggleBar, listBox);
        }

        content.getChildren().add(errorLabel);
        root.setCenter(transparentScroll(content));
    }

    private HBox buildSlotCard(TimeSlotBean s, String subjectName,
                               boolean isPast, Consumer<TimeSlotBean> onDelete) {
        HBox card = new HBox(16);
        card.getStyleClass().add("info-card");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setMaxWidth(640);

        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label dot = new Label("●");
        dot.setStyle("-fx-font-size: 14px;");

        String dateStr = s.getDate().format(
                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        Label dateTime = new Label(dateStr + "   " + s.getStartTime() + " – " + s.getEndTime());
        dateTime.getStyleClass().add("welcome-label");

        HBox dateRow = new HBox(8, dot, dateTime);
        dateRow.setAlignment(Pos.CENTER_LEFT);

        Label status = new Label();
        info.getChildren().addAll(dateRow, status);

        if (isPast) {
            if (!s.isAvailable()) {
                dot.getStyleClass().add("error-label");
                status.setText("Utilizzato");
                status.getStyleClass().add("error-label");
                aggiungiDettagliPrenotazione(info, subjectName, s);
            } else {
                dot.getStyleClass().add("past-label");
                status.setText("Non utilizzato");
                status.getStyleClass().add("past-label");
            }
        } else if (!s.isAvailable()) {
            dot.getStyleClass().add("error-label");
            status.setText("Prenotato");
            status.getStyleClass().add("error-label");
            aggiungiDettagliPrenotazione(info, subjectName, s);
            if (s.getMeetLink() != null) {
                Hyperlink meet = new Hyperlink("🎥  Apri Meet");
                meet.getStyleClass().add("info-text");
                meet.setOnAction(e -> {
                    try { java.awt.Desktop.getDesktop().browse(new java.net.URI(s.getMeetLink())); }
                    catch (Exception ex) { /* link non apribile */ }
                });
                info.getChildren().add(meet);
            }
        } else {
            dot.getStyleClass().add("success-label");
            status.setText("Disponibile");
            status.getStyleClass().add("success-label");
            Button deleteBtn = new Button("Elimina");
            deleteBtn.getStyleClass().add("danger-button");
            deleteBtn.setOnAction(e -> onDelete.accept(s));
            HBox btnRow = new HBox(deleteBtn);
            btnRow.setAlignment(Pos.CENTER_RIGHT);
            info.getChildren().add(btnRow);
        }

        card.getChildren().add(info);
        return card;
    }

    private void aggiungiDettagliPrenotazione(VBox info, String subjectName, TimeSlotBean s) {
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
    }
}