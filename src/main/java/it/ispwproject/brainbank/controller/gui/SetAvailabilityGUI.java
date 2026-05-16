package it.ispwproject.brainbank.controller.gui;

import it.ispwproject.brainbank.bean.TimeSlotBean;
import it.ispwproject.brainbank.controller.applicativo.AvailabilityController;
import it.ispwproject.brainbank.exception.AvailabilityException;
import it.ispwproject.brainbank.exception.DAOException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class SetAvailabilityGUI {

    private final Stage                  stage;
    private final AvailabilityController availabilityController = new AvailabilityController();

    private DatePicker datePicker;
    private TextField  startTimeField;
    private TextField  endTimeField;
    private Label      errorLabel;

    public SetAvailabilityGUI(Stage stage) { this.stage = stage; }

    public void show() {
        BorderPane root = buildShell();

        VBox content = new VBox(14);
        content.setPadding(new Insets(30, 40, 30, 40));
        content.setAlignment(Pos.TOP_CENTER);
        content.setMaxWidth(420);

        Label subtitle = new Label("Aggiungi un nuovo slot");
        subtitle.getStyleClass().add("small-label");

        Label dateLabel = new Label("Data");
        dateLabel.getStyleClass().add("small-label");
        datePicker = new DatePicker(LocalDate.now().plusDays(1));
        datePicker.setPrefWidth(300);
        datePicker.setDayCellFactory(dp -> new DateCell() {
            @Override public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setDisable(empty || item.isBefore(LocalDate.now()));
            }
        });

        Label startLabel = new Label("Ora inizio (HH:MM)");
        startLabel.getStyleClass().add("small-label");
        startTimeField = new TextField();
        startTimeField.getStyleClass().add("text-field");
        startTimeField.setPromptText("es. 09:00");
        startTimeField.setPrefWidth(300); startTimeField.setPrefHeight(40);

        Label endLabel = new Label("Ora fine (HH:MM)");
        endLabel.getStyleClass().add("small-label");
        endTimeField = new TextField();
        endTimeField.getStyleClass().add("text-field");
        endTimeField.setPromptText("es. 11:00");
        endTimeField.setPrefWidth(300); endTimeField.setPrefHeight(40);

        errorLabel = new Label("");
        errorLabel.getStyleClass().add("error-label");
        errorLabel.setWrapText(true);

        Button saveBtn = new Button("Aggiungi slot");
        saveBtn.getStyleClass().add("button");
        saveBtn.setPrefWidth(200); saveBtn.setPrefHeight(42);
        saveBtn.setOnAction(e -> handleSave());

        content.getChildren().addAll(subtitle, dateLabel, datePicker,
                startLabel, startTimeField, endLabel, endTimeField,
                errorLabel, saveBtn);

        BorderPane centerWrapper = new BorderPane(content);
        centerWrapper.getStyleClass().add("brainbank-background");
        root.setCenter(centerWrapper);

        stage.setScene(GUIUtils.createScene(root));
        stage.show();
    }

    private void handleSave() {
        errorLabel.setText("");
        LocalDate date = datePicker.getValue();
        if (date == null) { errorLabel.setText("Seleziona una data valida."); return; }
        LocalTime startTime, endTime;
        try { startTime = LocalTime.parse(startTimeField.getText().trim()); }
        catch (DateTimeParseException e) { errorLabel.setText("Formato ora inizio non valido. Usa HH:MM."); return; }
        try { endTime = LocalTime.parse(endTimeField.getText().trim()); }
        catch (DateTimeParseException e) { errorLabel.setText("Formato ora fine non valido. Usa HH:MM."); return; }

        try {
            availabilityController.addSlot(new TimeSlotBean(0, date, startTime, endTime, true));
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Slot aggiunto"); alert.setHeaderText(null);
            alert.setContentText("✓ Slot aggiunto con successo!"); alert.showAndWait();
            MainGUI.showDashboardTutor();
        } catch (DAOException | AvailabilityException e) {
            errorLabel.setText("Errore: " + e.getMessage());
        }
    }

    private BorderPane buildShell() {
        BorderPane shell = new BorderPane();
        shell.getStyleClass().add("brainbank-background");
        shell.setTop(buildTopBar("Imposta disponibilità", () -> MainGUI.showDashboardTutor()));
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

        ImageView logo = new ImageView(new Image(
                getClass().getResourceAsStream("/images/logo.png"), 60, 60, true, true));
        logo.setFitHeight(38); logo.setPreserveRatio(true); logo.setSmooth(true);

        bar.getChildren().addAll(backBtn, title, logo);
        return bar;
    }
}