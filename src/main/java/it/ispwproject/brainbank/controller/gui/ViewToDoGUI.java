package it.ispwproject.brainbank.controller.gui;

import it.ispwproject.brainbank.bean.ActivityBean;
import it.ispwproject.brainbank.controller.applicativo.ActivityController;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.view.gui.ViewToDoGUIView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.List;

public class ViewToDoGUI {

    private final Stage stage;
    private final ActivityController activityController = new ActivityController();
    private final ViewToDoGUIView view = new ViewToDoGUIView();

    public ViewToDoGUI(Stage stage) { this.stage = stage; }

    public void show() {
        BorderPane root = view.buildRoot(MainGUI::showDashboardStudent);
        view.clearError();

        try {
            List<ActivityBean> activities = activityController.getMyActivities();
            List<ActivityBean> pending   = activities.stream().filter(a -> !a.isCompleted()).toList();
            List<ActivityBean> completed = activities.stream().filter(ActivityBean::isCompleted).toList();
            view.buildContent(root, pending, completed, this::handleMarkDone);
        } catch (DAOException e) {
            view.setError("Errore: " + e.getMessage());
            root.setCenter(view.errorLabel);
        }

        stage.setScene(GUIUtils.createScene(root));
        stage.show();
    }

    private void handleMarkDone(ActivityBean a) {
        try {
            activityController.markActivityCompleted(a.getId());
            show();
        } catch (DAOException ex) {
            view.setError("Errore: " + ex.getMessage());
        }
    }
}