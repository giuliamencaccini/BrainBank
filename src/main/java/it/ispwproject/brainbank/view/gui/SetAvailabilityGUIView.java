package it.ispwproject.brainbank.view.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;

public class SetAvailabilityGUIView extends PageGUIView {

    public final DatePicker datePicker     = new DatePicker(LocalDate.now().plusDays(1));
    public final TextField  startTimeField = new TextField();
    public final TextField  endTimeField   = new TextField();
    public final Button     saveBtn        = new Button("Aggiungi");
    public final Label      errorLabel     = buildErrorLabel();

    public SetAvailabilityGUIView() {
        datePicker.setPrefWidth(300);
        datePicker.setDayCellFactory(dp -> new DateCell() {
            @Override public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setDisable(empty || item.isBefore(LocalDate.now()));
            }
        });

        startTimeField.getStyleClass().add("text-field");
        startTimeField.setPromptText("es. 09:00");
        startTimeField.setPrefWidth(300); startTimeField.setPrefHeight(40);

        endTimeField.getStyleClass().add("text-field");
        endTimeField.setPromptText("es. 11:00");
        endTimeField.setPrefWidth(300); endTimeField.setPrefHeight(40);

        errorLabel.setMaxWidth(300);
        errorLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");

        saveBtn.getStyleClass().add("button");
        saveBtn.setPrefWidth(160); saveBtn.setPrefHeight(42);
    }

    public BorderPane buildRoot(Runnable onBack) {
        BorderPane root = buildShell("Imposta disponibilità", onBack);

        Label subtitle  = sectionLabel("Aggiungi un nuovo slot");
        Label dateLabel = sectionLabel("Data");
        Label startLabel = sectionLabel("Ora inizio (HH:MM)");
        Label endLabel   = sectionLabel("Ora fine (HH:MM)");

        HBox btnRow = new HBox(saveBtn);
        btnRow.setAlignment(Pos.CENTER);

        VBox content = new VBox(14, subtitle, dateLabel, datePicker,
                startLabel, startTimeField, endLabel, endTimeField,
                errorLabel, btnRow);
        content.setAlignment(Pos.TOP_LEFT);
        content.setMaxWidth(380);
        content.setPrefWidth(380);

        VBox card = new VBox(content);
        card.getStyleClass().add("summary-card");
        card.setMaxHeight(420);
        card.setPrefWidth(420);

        HBox centerWrapper = new HBox(card);
        centerWrapper.getStyleClass().add("brainbank-background");
        centerWrapper.setAlignment(Pos.TOP_CENTER);
        centerWrapper.setPadding(new Insets(30, 0, 0, 0));

        root.setCenter(centerWrapper);
        return root;
    }

    public void setError(String message) { errorLabel.setText(message); }
    public void clearError()             { errorLabel.setText(""); }

    private Label sectionLabel(String text) {
        Label lbl = new Label(text);
        lbl.getStyleClass().add("small-label");
        return lbl;
    }
}