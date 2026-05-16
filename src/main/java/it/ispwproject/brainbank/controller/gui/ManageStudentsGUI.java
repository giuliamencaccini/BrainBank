package it.ispwproject.brainbank.controller.gui;

import it.ispwproject.brainbank.bean.*;
import it.ispwproject.brainbank.controller.applicativo.ActivityController;
import it.ispwproject.brainbank.exception.DAOException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

public class ManageStudentsGUI {

    private final Stage              stage;
    private final ActivityController activityController = new ActivityController();

    private Label    errorLabel;
    private ComboBox<StudentBean> studentCombo;

    public ManageStudentsGUI(Stage stage) { this.stage = stage; }

    public void show() {
        BorderPane root = buildShell();

        VBox content = new VBox(20);
        content.setPadding(new Insets(24, 40, 24, 40));
        content.setAlignment(Pos.TOP_CENTER);

        errorLabel = new Label("");
        errorLabel.getStyleClass().add("error-label");

        Label studentLabel = new Label("Studente");
        studentLabel.getStyleClass().add("small-label");

        studentCombo = new ComboBox<>();
        studentCombo.getStyleClass().add("combo-box");
        studentCombo.setPromptText("Seleziona studente...");
        studentCombo.setPrefWidth(300);
        studentCombo.setCellFactory(lv -> studentCell());
        studentCombo.setButtonCell(studentCell());

        try { studentCombo.getItems().setAll(activityController.getStudents()); }
        catch (DAOException e) { errorLabel.setText("Errore: " + e.getMessage()); }

        HBox selectorRow = new HBox(12, studentLabel, studentCombo);
        selectorRow.setAlignment(Pos.CENTER_LEFT);
        selectorRow.setMaxWidth(700);

        VBox studentCard = new VBox(16);
        studentCard.setMaxWidth(700);
        studentCard.setVisible(false); studentCard.setManaged(false);

        studentCombo.setOnAction(e -> {
            StudentBean selected = studentCombo.getValue();
            if (selected == null) return;
            studentCard.getChildren().clear();
            studentCard.setVisible(true); studentCard.setManaged(true);
            buildStudentCard(selected, studentCard);
        });

        content.getChildren().addAll(selectorRow, errorLabel, studentCard);

        ScrollPane scroll = new ScrollPane(content);
        scroll.getStyleClass().add("transparent-scroll");
        scroll.setFitToWidth(true);

        root.setCenter(scroll);
        stage.setScene(GUIUtils.createScene(root));
        stage.show();
    }

    private void buildStudentCard(StudentBean student, VBox card) {
        try {
            ProgressBean       progress   = activityController.getProgress(student.getId());
            List<ActivityBean> activities = activityController.getActivities(student.getId());

            Label nameLabel = new Label("Studente: " + student.getFullName());
            nameLabel.getStyleClass().add("small-label");

            // ── Pannello progressi ──────────────────────────
            VBox progressBox = new VBox(8);
            progressBox.getStyleClass().add("info-card");
            progressBox.setMaxWidth(340);

            Label progressTitle = new Label("Annota progressi");
            progressTitle.getStyleClass().add("small-label");

            TextArea notesArea = new TextArea();
            notesArea.getStyleClass().add("text-area");
            notesArea.setPrefRowCount(3); notesArea.setWrapText(true);
            notesArea.setPromptText("Note sui progressi...");
            if (progress != null) notesArea.setText(progress.getNotes());

            Button updateBtn = new Button("Aggiorna");
            updateBtn.getStyleClass().add("button");
            updateBtn.setOnAction(e -> handleUpdateProgress(student, notesArea.getText()));

            Label lastUpdate = new Label(progress != null
                    ? "Ultimo aggiornamento " + progress.getUpdatedAt().toLocalDate() : "");
            lastUpdate.setStyle("-fx-font-size: 11px; -fx-text-fill: #888;");

            progressBox.getChildren().addAll(progressTitle, notesArea, updateBtn, lastUpdate);

            // ── Pannello to-do ──────────────────────────────
            VBox todoBox = new VBox(8);
            todoBox.getStyleClass().add("info-card");
            todoBox.setMaxWidth(340);

            Label todoTitle = new Label("Assegna to-do list");
            todoTitle.getStyleClass().add("small-label");
            todoBox.getChildren().add(todoTitle);

            for (ActivityBean a : activities) {
                Label actLabel = new Label((a.isCompleted() ? "✓ " : "• ") + a.getDescription());
                actLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: " +
                        (a.isCompleted() ? "#27AE60" : "#4B4B4B") + ";");
                todoBox.getChildren().add(actLabel);
            }

            TextField newActivityField = new TextField();
            newActivityField.getStyleClass().add("text-field");
            newActivityField.setPromptText("Nuova attività..."); newActivityField.setPrefHeight(36);

            Button assignBtn = new Button("Assegna");
            assignBtn.getStyleClass().add("button");
            assignBtn.setOnAction(e -> {
                String desc = newActivityField.getText().trim();
                if (desc.isBlank()) return;
                handleAssignActivity(student, desc, card);
                newActivityField.clear();
            });

            todoBox.getChildren().addAll(newActivityField, assignBtn);

            HBox panels = new HBox(20, progressBox, todoBox);
            panels.setAlignment(Pos.TOP_LEFT);

            card.getChildren().addAll(nameLabel, panels);

        } catch (DAOException e) { errorLabel.setText("Errore: " + e.getMessage()); }
    }

    private void handleUpdateProgress(StudentBean student, String notes) {
        if (notes.isBlank()) { errorLabel.setText("Le note non possono essere vuote."); return; }
        try {
            activityController.updateProgress(new ProgressBean(student, notes, null));
            showInfo("Progressi aggiornati con successo.");
        } catch (DAOException e) { errorLabel.setText("Errore: " + e.getMessage()); }
    }

    private void handleAssignActivity(StudentBean student, String desc, VBox card) {
        try {
            activityController.assignActivity(new ActivityBean(0, student, desc, false, null));
            card.getChildren().clear();
            buildStudentCard(student, card);
        } catch (DAOException e) { errorLabel.setText("Errore: " + e.getMessage()); }
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Operazione completata");
        alert.setHeaderText(null); alert.setContentText(message); alert.showAndWait();
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

    private BorderPane buildShell() {
        BorderPane shell = new BorderPane();
        shell.getStyleClass().add("brainbank-background");
        shell.setTop(buildTopBar("Gestisci Studenti", () -> MainGUI.showDashboardTutor()));
        return shell;
    }

    private HBox buildTopBar(String titleText, Runnable onBack) {
        HBox bar = new HBox();
        bar.getStyleClass().add("page-topbar");
        bar.setAlignment(Pos.CENTER);

        Button backBtn = new Button("⟪  Indietro");
        backBtn.getStyleClass().add("back-button");
        backBtn.setOnAction(e -> onBack.run());

        Label title = new Label(titleText);
        title.getStyleClass().add("page-title");
        title.setMaxWidth(Double.MAX_VALUE);
        title.setAlignment(Pos.CENTER);
        HBox.setHgrow(title, Priority.ALWAYS);

        javafx.scene.image.ImageView logo = new ImageView(new Image(
                getClass().getResourceAsStream("/images/logo.png"), 60, 60, true, true));
        logo.setFitHeight(38); logo.setPreserveRatio(true); logo.setSmooth(true);

        bar.getChildren().addAll(backBtn, title, logo);
        return bar;
    }
}