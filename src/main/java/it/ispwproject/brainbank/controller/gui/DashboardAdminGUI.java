package it.ispwproject.brainbank.controller.gui;

import it.ispwproject.brainbank.pattern.singleton.SessionManager;
import it.ispwproject.brainbank.view.gui.DashboardAdminGUIView;
import javafx.stage.Stage;

public class DashboardAdminGUI {

    private final Stage stage;
    private final DashboardAdminGUIView view = new DashboardAdminGUIView();

    public DashboardAdminGUI(Stage stage) { this.stage = stage; }

    public void show() {
        String nome = SessionManager.getInstance().getLoggedUser().getName();
        view.reportBtn.setOnAction(e -> new ReportStatisticsGUI(stage).show());

        stage.setScene(GUIUtils.createScene(view.buildRoot(nome, this::handleLogout)));
        stage.show();
    }

    private void handleLogout() {
        try { it.ispwproject.brainbank.dao.ConnectionFactory.clearRole(); }
        catch (java.sql.SQLException ex) { /* ignora */ }
        it.ispwproject.brainbank.pattern.singleton.SessionManager.getInstance().clearSession();
        MainGUI.showLogin();
    }
}