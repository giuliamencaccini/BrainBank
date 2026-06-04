package it.ispwproject.brainbank.controller.gui;

import it.ispwproject.brainbank.bean.TimeSlotBean;
import it.ispwproject.brainbank.controller.applicativo.AvailabilityController;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.view.gui.ViewSlotsGUIView;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

public class ViewSlotsGUI {

    private final Stage stage;
    private final AvailabilityController availabilityController = new AvailabilityController();
    private final ViewSlotsGUIView view = new ViewSlotsGUIView();

    public ViewSlotsGUI(Stage stage) { this.stage = stage; }

    public void show() {
        BorderPane root = view.buildRoot(MainGUI::showDashboardTutor);
        view.clearError();

        try {
            List<TimeSlotBean> futuri  = availabilityController.getSlots();
            List<TimeSlotBean> passati = availabilityController.getPastSlots();
            Map<Integer, String> subjectBySlot = availabilityController.getSubjectBySlot();
            List<TimeSlotBean> prenotati   = futuri.stream().filter(s -> !s.isAvailable()).toList();
            List<TimeSlotBean> disponibili = futuri.stream().filter(TimeSlotBean::isAvailable).toList();
            view.buildContent(root, disponibili, prenotati, passati, subjectBySlot, this::handleDelete);
        } catch (DAOException e) {
            view.setError("Errore: " + e.getMessage());
            root.setCenter(view.errorLabel);
        }

        stage.setScene(GUIUtils.createScene(root));
        stage.show();
    }

    private void handleDelete(TimeSlotBean s) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Conferma eliminazione");
        confirm.setHeaderText(null);
        confirm.setContentText("Vuoi eliminare questo slot?\n" +
                s.getDate() + "  " + s.getStartTime() + " – " + s.getEndTime());
        confirm.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                try {
                    availabilityController.deleteSlot(s.getId());
                    show();
                } catch (DAOException ex) {
                    view.setError("Errore: " + ex.getMessage());
                }
            }
        });
    }
}