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

        VBox content = new VBox(12);
        content.setPadding(new Insets(28, 48, 28, 48));
        content.setAlignment(Pos.TOP_CENTER);


        errorLabel = new Label("");
        errorLabel.getStyleClass().add("error-label");

        // ── Selector con card azzurra ─────────────────────
        VBox selectorCard = new VBox(10);
        selectorCard.getStyleClass().add("info-card");
        selectorCard.setMaxWidth(720);
        selectorCard.setAlignment(Pos.CENTER_LEFT);

        Label studentLabel = new Label("Seleziona studente");
        studentLabel.getStyleClass().add("small-label");

        studentCombo = new ComboBox<>();
        studentCombo.getStyleClass().add("combo-box");
        studentCombo.setPromptText("Cerca studente...");
        studentCombo.setMaxWidth(Double.MAX_VALUE);
        studentCombo.setCellFactory(lv -> studentCell());
        studentCombo.setButtonCell(studentCell());

        try { studentCombo.getItems().setAll(activityController.getStudents()); }
        catch (DAOException e) { errorLabel.setText("Errore: " + e.getMessage()); }

        selectorCard.getChildren().addAll(studentLabel, studentCombo);

        // ── Card studente (appare dopo selezione) ──────────
        VBox studentCard = new VBox(16);
        studentCard.setMaxWidth(720);
        studentCard.setVisible(false); studentCard.setManaged(false);

        studentCombo.setOnAction(e -> {
            StudentBean selected = studentCombo.getValue();
            if (selected == null) return;
            studentCard.getChildren().clear();
            studentCard.setVisible(true); studentCard.setManaged(true);
            buildStudentCard(selected, studentCard);
        });

        content.getChildren().addAll(selectorCard, errorLabel, studentCard);

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

            // ── Header studente ───────────────────────────
            HBox studentHeader = new HBox(12);
            studentHeader.setAlignment(Pos.CENTER_LEFT);
            studentHeader.getStyleClass().add("info-card");
            studentHeader.setMaxWidth(720);
            studentHeader.setPadding(new Insets(12, 16, 12, 16));

            // Avatar iniziali
            Label avatar = new Label(
                    String.valueOf(student.getName().charAt(0)).toUpperCase() +
                            String.valueOf(student.getSurname().charAt(0)).toUpperCase());
            avatar.setStyle(
                    "-fx-background-color: #8EADC2; -fx-background-radius: 20; " +
                            "-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; " +
                            "-fx-min-width: 40; -fx-min-height: 40; -fx-alignment: center;");

            VBox studentInfo = new VBox(2);
            Label nameLabel = new Label(student.getFullName());
            nameLabel.getStyleClass().add("welcome-label");
            Label emailLabel = new Label(student.getEmail());
            emailLabel.getStyleClass().add("info-text");
            studentInfo.getChildren().addAll(nameLabel, emailLabel);

            studentHeader.getChildren().addAll(avatar, studentInfo);

            // ── Pannelli affiancati ───────────────────────
            VBox progressBox = new VBox(12);
            progressBox.getStyleClass().add("info-card");
            progressBox.setPrefWidth(340); progressBox.setMinWidth(280);

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
                    ? "Aggiornato il " + progress.getUpdatedAt().toLocalDate().format(
                    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "Nessun aggiornamento");lastUpdate.getStyleClass().add("info-text");
            lastUpdate.setStyle("-fx-text-fill: #999;");
            HBox.setHgrow(lastUpdate, Priority.ALWAYS);

            Button updateBtn = new Button("Salva");
            updateBtn.getStyleClass().add("save-button");
            updateBtn.setPrefWidth(80);
            updateBtn.setOnAction(e -> handleUpdateProgress(student, notesArea.getText()));

            progressFooter.getChildren().addAll(lastUpdate, updateBtn);
            progressBox.getChildren().addAll(progressTitle, notesArea, progressFooter);

            // ── To-do ────────────────────────────────────
            VBox todoBox = new VBox(8);
            todoBox.getStyleClass().add("info-card");
            todoBox.setPrefWidth(340); todoBox.setMinWidth(280);

            Label todoTitle = new Label("✅  Attività");
            todoTitle.getStyleClass().add("small-label");
            todoBox.getChildren().add(todoTitle);

            if (activities.isEmpty()) {
                Label none = new Label("Nessuna attività assegnata");
                none.getStyleClass().add("info-text");
                none.setStyle("-fx-text-fill: #aaa; -fx-font-style: italic;");
                todoBox.getChildren().add(none);
            } else {
                for (ActivityBean a : activities) {
                    HBox actRow = new HBox(8);
                    actRow.setAlignment(Pos.CENTER_LEFT);
                    Label check = new Label(a.isCompleted() ? "✓" : "○");
                    check.getStyleClass().add(a.isCompleted() ? "success-label" : "info-text");
                    check.setStyle("-fx-font-size: 14px;");
                    Label actLabel = new Label(a.getDescription());
                    actLabel.getStyleClass().add(a.isCompleted() ? "success-label" : "register-label");
                    actLabel.setWrapText(true);
                    if (a.isCompleted())
                        actLabel.setStyle("-fx-strikethrough: true;");
                    actRow.getChildren().addAll(check, actLabel);
                    todoBox.getChildren().add(actRow);
                }
            }

            // Separatore + input nuova attività
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
                if (desc.isBlank()) return;
                handleAssignActivity(student, desc, card);
                newActivityField.clear();
            });

            addRow.getChildren().addAll(newActivityField, assignBtn);
            todoBox.getChildren().add(addRow);

            HBox panels = new HBox(16, progressBox, todoBox);
            panels.setAlignment(Pos.TOP_CENTER);
            HBox.setHgrow(progressBox, Priority.ALWAYS);
            HBox.setHgrow(todoBox, Priority.ALWAYS);

            card.getChildren().addAll(studentHeader, panels);

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

        bar.getChildren().addAll(left, title, right);
        return bar;
    }
}