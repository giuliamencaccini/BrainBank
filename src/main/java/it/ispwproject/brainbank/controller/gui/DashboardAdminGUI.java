package it.ispwproject.brainbank.controller.gui;

import it.ispwproject.brainbank.pattern.singleton.SessionManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class DashboardAdminGUI {

    private final Stage stage;

    public DashboardAdminGUI(Stage stage) { this.stage = stage; }

    public void show() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("brainbank-background");
        root.setTop(buildNavbar());
        root.setCenter(buildBody());
        stage.setScene(GUIUtils.createScene(root));
        stage.show();
    }

    // ── Navbar ───────────────────────────────────────────────────────────

    private HBox buildNavbar() {
        HBox navbar = new HBox();
        navbar.getStyleClass().add("navbar");
        navbar.setAlignment(Pos.CENTER_LEFT);

        var logoStream = getClass().getResourceAsStream("/images/logo.png");
        if (logoStream != null) {
            ImageView logoView = new ImageView(
                    new Image(logoStream, 80, 80, true, true));
            logoView.setFitHeight(56); logoView.setFitWidth(56);
            logoView.setPreserveRatio(true); logoView.setSmooth(true);
            String nome = SessionManager.getInstance().getLoggedUser().getName();
            Label welcome = new Label("Benvenuto\n" + nome + "!");
            welcome.getStyleClass().add("welcome-label");
            HBox left = new HBox(10, logoView, welcome);
            left.setAlignment(Pos.CENTER_LEFT);
            HBox.setHgrow(left, Priority.ALWAYS);
            navbar.getChildren().add(left);
        }

        Label ruolo = new Label("Admin");
        ruolo.getStyleClass().add("role-label");
        ruolo.setMaxWidth(Double.MAX_VALUE);
        ruolo.setAlignment(Pos.CENTER);
        HBox.setHgrow(ruolo, Priority.ALWAYS);

        Button logoutBtn = new Button("Log out");
        logoutBtn.getStyleClass().add("button");
        logoutBtn.setPadding(new Insets(6, 18, 6, 18));
        logoutBtn.setOnAction(e -> {
            try { it.ispwproject.brainbank.dao.ConnectionFactory.clearRole(); }
            catch (java.sql.SQLException ex) { /* ignora */ }
            SessionManager.getInstance().clearSession();
            MainGUI.showLogin();
        });

        HBox right = new HBox(logoutBtn);
        right.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(right, Priority.ALWAYS);

        navbar.getChildren().addAll(ruolo, right);
        return navbar;
    }

    // ── Body ─────────────────────────────────────────────────────────────

    private VBox buildBody() {
        VBox body = new VBox(20);
        body.getStyleClass().add("brainbank-background");
        body.setAlignment(Pos.CENTER);
        body.setPadding(new Insets(40));

        Button reportBtn = new Button("Report statistiche");
        reportBtn.getStyleClass().add("button");
        reportBtn.setPrefWidth(220);
        reportBtn.setPrefHeight(42);
        reportBtn.setOnAction(e -> new ReportStatisticsGUI(stage).show());

        body.getChildren().add(reportBtn);
        return body;
    }
}