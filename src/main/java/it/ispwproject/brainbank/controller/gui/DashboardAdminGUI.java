package it.ispwproject.brainbank.controller.gui;

import it.ispwproject.brainbank.util.singleton.SessionManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DashboardAdminGUI {

    private final Stage stage;

    public DashboardAdminGUI(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        VBox root = new VBox(24);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.getStyleClass().add("brainbank-background");

        String nome = SessionManager.getInstance().getLoggedUser().getName();

        Label title = new Label("Benvenuto, " + nome);
        title.getStyleClass().add("title-label");

        Label subtitle = new Label("Dashboard Admin");
        subtitle.getStyleClass().add("subtitle-label");

        Button reportBtn = new Button("Report statistiche");
        reportBtn.setPrefWidth(220);
        reportBtn.setPrefHeight(42);
        reportBtn.setOnAction(e -> new ReportStatisticsGUI(stage).show());

        Button logoutBtn = new Button("Logout");
        logoutBtn.setPrefWidth(220);
        logoutBtn.setPrefHeight(42);
        logoutBtn.setOnAction(e -> handleLogout());

        root.getChildren().addAll(
                title,
                subtitle,
                reportBtn,
                logoutBtn
        );

        Scene scene = GUIUtils.createScene(root);
        stage.setScene(scene);
        stage.show();
    }

    private void handleLogout() {
        SessionManager.getInstance().clearSession();
        MainGUI.showLogin();
    }
}