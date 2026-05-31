package it.ispwproject.brainbank.view.gui;

import it.ispwproject.brainbank.bean.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;

public class ManageStudentsGUIView extends PageGUIView {

    public final ComboBox<StudentBean> studentCombo = new ComboBox<>();
    public final Label                 errorLabel   = buildErrorLabel();

    public ManageStudentsGUIView() {
        studentCombo.getStyleClass().add("combo-box");
        studentCombo.setPromptText("Cerca studente...");
        studentCombo.setMaxWidth(Double.MAX_VALUE);
        studentCombo.setCellFactory(lv -> studentCell());
        studentCombo.setButtonCell(studentCell());
    }

    public BorderPane buildRoot(Runnable onBack) {
        BorderPane root = buildShell("Gestisci Studenti", onBack);

        VBox content = new VBox(12);
        content.setPadding(new Insets(28, 48, 28, 48));
        content.setAlignment(Pos.TOP_CENTER);

        VBox selectorCard = new VBox(10);
        selectorCard.getStyleClass().add("info-card");
        selectorCard.setMaxWidth(720);
        selectorCard.setAlignment(Pos.CENTER_LEFT);

        Label studentLabel = new Label("Seleziona studente");
        studentLabel.getStyleClass().add("small-label");

        selectorCard.getChildren().addAll(studentLabel, studentCombo);

        VBox studentCard = new VBox(8);
        studentCard.setMaxWidth(720);
        studentCard.setVisible(false);
        studentCard.setManaged(false);

        // Il controller aggancerà l'azione sulla combo
        studentCombo.setUserData(studentCard);

        content.getChildren().addAll(selectorCard, studentCard, errorLabel);

        ScrollPane scroll = new ScrollPane(content);
        scroll.getStyleClass().add("transparent-scroll");
        scroll.setFitToWidth(true);
        root.setCenter(scroll);
        return root;
    }

    public VBox getStudentCard() {
        return (VBox) studentCombo.getUserData();
    }

    public void buildStudentCard(VBox card, StudentBean student,
                                 ProgressBean progress,
                                 List<ActivityBean> activities,
                                 List<BookingResponseBean> upcoming,
                                 List<BookingResponseBean> completed,
                                 Consumer<String> onSaveProgress,
                                 Consumer<String> onAssignActivity,
                                 Consumer<ActivityBean> onDeleteActivity) {
        card.getChildren().clear();
        card.setVisible(true);
        card.setManaged(true);

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // ── Header studente ───────────────────────────────────────────────
        HBox studentHeader = new HBox(12);
        studentHeader.setAlignment(Pos.CENTER_LEFT);
        studentHeader.getStyleClass().add("info-card");
        studentHeader.setMaxWidth(720);
        studentHeader.setPadding(new Insets(12, 16, 12, 16));

        Label avatar = new Label(
                String.valueOf(student.getName().charAt(0)).toUpperCase() +
                        String.valueOf(student.getSurname().charAt(0)).toUpperCase());
        avatar.setStyle("-fx-background-color: #8EADC2; -fx-background-radius: 20; " +
                "-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; " +
                "-fx-min-width: 40; -fx-min-height: 40; -fx-alignment: center;");

        VBox studentInfo = new VBox(4);
        Label nameLabel  = new Label(student.getFullName()); nameLabel.getStyleClass().add("welcome-label");
        Label emailLabel = new Label(student.getEmail());    emailLabel.getStyleClass().add("info-text");
        studentInfo.getChildren().addAll(nameLabel, emailLabel);

        if (!upcoming.isEmpty()) {
            BookingResponseBean next = upcoming.get(0);
            Label nextLesson = new Label("📅  Prossima: " +
                    next.getTimeSlot().getDate().format(fmt) + " — " +
                    next.getSubject().getName() + "  " +
                    next.getTimeSlot().getStartTime() + "–" + next.getTimeSlot().getEndTime());
            nextLesson.getStyleClass().add("info-text");
            nextLesson.setStyle("-fx-text-fill: #5a8a6a; -fx-font-weight: bold;");
            studentInfo.getChildren().add(nextLesson);
        }

        if (!completed.isEmpty()) {
            Label storicoBtn = new Label("📖  Lezioni effettuate (" + completed.size() + ")  ▼");
            storicoBtn.getStyleClass().add("info-text");
            storicoBtn.setStyle("-fx-text-fill: #888; -fx-cursor: hand;");
            VBox storicoContent = new VBox(4);
            storicoContent.setVisible(false); storicoContent.setManaged(false);
            storicoContent.setPadding(new Insets(4, 0, 0, 8));
            for (BookingResponseBean b : completed) {
                Label l = new Label("• " + b.getTimeSlot().getDate().format(fmt) +
                        " — " + b.getSubject().getName() + "  " +
                        b.getTimeSlot().getStartTime() + "–" + b.getTimeSlot().getEndTime());
                l.getStyleClass().add("info-text"); l.setStyle("-fx-text-fill: #888;");
                storicoContent.getChildren().add(l);
            }
            storicoBtn.setOnMouseClicked(e -> {
                boolean show = !storicoContent.isVisible();
                storicoContent.setVisible(show); storicoContent.setManaged(show);
                storicoBtn.setText("📖  Lezioni effettuate (" + completed.size() + ")  " + (show ? "▲" : "▼"));
            });
            studentInfo.getChildren().addAll(storicoBtn, storicoContent);
        }

        studentHeader.getChildren().addAll(avatar, studentInfo);

        // ── Progressi ────────────────────────────────────────────────────
        VBox progressBox = new VBox(12);
        progressBox.getStyleClass().add("info-card");
        progressBox.setPrefWidth(340); progressBox.setMinWidth(280);
        progressBox.setMaxHeight(Region.USE_PREF_SIZE);

        Label progressTitle = new Label("📝  Progressi");
        progressTitle.getStyleClass().add("small-label");

        TextArea notesArea = new TextArea();
        notesArea.getStyleClass().add("text-area");
        notesArea.setPrefRowCount(3); notesArea.setWrapText(true);
        notesArea.setPromptText("Note sui progressi dello studente...");
        if (progress != null) notesArea.setText(progress.getNotes());

        HBox progressFooter = new HBox(12);
        progressFooter.setAlignment(Pos.CENTER_LEFT);

        Label lastUpdate = new Label(progress != null
                ? "Aggiornato il " + progress.getUpdatedAt().format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy 'alle' HH:mm"))
                : "Nessun aggiornamento");
        lastUpdate.getStyleClass().add("info-text");
        lastUpdate.setStyle("-fx-text-fill: #999;");
        HBox.setHgrow(lastUpdate, Priority.ALWAYS);

        Button updateBtn = new Button("Salva");
        updateBtn.getStyleClass().add("save-button");
        updateBtn.setPrefWidth(80);
        updateBtn.setOnAction(e -> onSaveProgress.accept(notesArea.getText()));

        progressFooter.getChildren().addAll(lastUpdate, updateBtn);
        progressBox.getChildren().addAll(progressTitle, notesArea, progressFooter);

        // ── To-do ────────────────────────────────────────────────────────
        VBox todoBox = new VBox(8);
        todoBox.getStyleClass().add("info-card");
        todoBox.setPrefWidth(340); todoBox.setMinWidth(280);

        Label todoTitle = new Label("✅  Attività");
        todoTitle.getStyleClass().add("small-label");
        todoBox.getChildren().add(todoTitle);

        VBox activityList = new VBox(6);
        activityList.setMaxWidth(Double.MAX_VALUE);

        if (activities.isEmpty()) {
            Label none = new Label("Nessuna attività assegnata");
            none.getStyleClass().add("info-text");
            none.setStyle("-fx-text-fill: #aaa; -fx-font-style: italic;");
            activityList.getChildren().add(none);
        } else {
            for (ActivityBean a : activities) {
                HBox actRow = new HBox(8);
                actRow.setAlignment(Pos.CENTER_LEFT);
                actRow.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(actRow, Priority.ALWAYS);
                Label check = new Label(a.isCompleted() ? "✓" : "○");
                check.getStyleClass().add(a.isCompleted() ? "success-label" : "info-text");
                check.setStyle("-fx-font-size: 14px;");
                Label actLabel = new Label(a.getDescription());
                actLabel.getStyleClass().add(a.isCompleted() ? "success-label" : "register-label");
                actLabel.setMaxWidth(200); actLabel.setEllipsisString("...");
                HBox.setHgrow(actLabel, Priority.ALWAYS);
                if (a.isCompleted()) actLabel.setStyle("-fx-strikethrough: true;");

                Button deleteBtn = new Button("✕");
                deleteBtn.getStyleClass().add("cancel-inline-button");
                deleteBtn.setOnAction(e -> onDeleteActivity.accept(a));

                actRow.getChildren().addAll(check, actLabel, deleteBtn);
                activityList.getChildren().add(actRow);
            }
        }

        todoBox.getChildren().add(activityList);
        todoBox.getChildren().add(new Separator());

        HBox addRow = new HBox(8);
        addRow.setAlignment(Pos.CENTER_LEFT);
        TextField newActivityField = new TextField();
        newActivityField.getStyleClass().add("text-field");
        newActivityField.setPromptText("Nuova attività...");
        newActivityField.setPrefHeight(34);
        HBox.setHgrow(newActivityField, Priority.ALWAYS);

        Button assignBtn = new Button("＋");
        assignBtn.getStyleClass().add("save-button");
        assignBtn.setPrefWidth(36); assignBtn.setPrefHeight(34);
        assignBtn.setOnAction(e -> {
            String desc = newActivityField.getText().trim();
            if (!desc.isBlank()) { onAssignActivity.accept(desc); newActivityField.clear(); }
        });

        addRow.getChildren().addAll(newActivityField, assignBtn);
        todoBox.getChildren().add(addRow);

        HBox panels = new HBox(16, progressBox, todoBox);
        panels.setAlignment(Pos.TOP_CENTER);
        HBox.setHgrow(progressBox, Priority.ALWAYS);
        HBox.setHgrow(todoBox,     Priority.ALWAYS);

        card.getChildren().addAll(studentHeader, panels);
    }

    private ListCell<StudentBean> studentCell() {
        return new ListCell<>() {
            @Override protected void updateItem(StudentBean item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null :
                        item.getFullName() + " (" + item.getEmail() + ")");
            }
        };
    }
}