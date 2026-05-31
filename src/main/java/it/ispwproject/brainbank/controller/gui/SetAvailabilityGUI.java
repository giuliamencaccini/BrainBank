package it.ispwproject.brainbank.controller.gui;

import it.ispwproject.brainbank.bean.TimeSlotBean;
import it.ispwproject.brainbank.controller.applicativo.AvailabilityController;
import it.ispwproject.brainbank.exception.AvailabilityException;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.view.gui.SetAvailabilityGUIView;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class SetAvailabilityGUI {

    private final Stage                  stage;
    private final AvailabilityController availabilityController = new AvailabilityController();
    private final SetAvailabilityGUIView view = new SetAvailabilityGUIView();

    public SetAvailabilityGUI(Stage stage) { this.stage = stage; }

    public void show() {
        view.saveBtn.setOnAction(e -> handleSave());
        stage.setScene(GUIUtils.createScene(
                view.buildRoot(MainGUI::showDashboardTutor)));
        stage.show();
    }

    private void handleSave() {
        view.clearError();

        LocalDate date = view.datePicker.getValue();
        if (date == null) { view.setError("Seleziona una data valida."); return; }

        LocalTime startTime, endTime;
        try { startTime = LocalTime.parse(view.startTimeField.getText().trim()); }
        catch (DateTimeParseException e) { view.setError("Formato ora inizio non valido. Usa HH:MM."); return; }
        try { endTime = LocalTime.parse(view.endTimeField.getText().trim()); }
        catch (DateTimeParseException e) { view.setError("Formato ora fine non valido. Usa HH:MM."); return; }

        try {
            availabilityController.addSlot(new TimeSlotBean(0, date, startTime, endTime, true));
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Slot aggiunto");
            alert.setHeaderText(null);
            alert.setContentText("✓ Slot aggiunto con successo!");
            alert.showAndWait();
            MainGUI.showDashboardTutor();
        } catch (DAOException | AvailabilityException e) {
            view.setError("Errore: " + e.getMessage());
        }
    }
}