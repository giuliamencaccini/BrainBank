package it.ispwproject.brainbank.view.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.*;

public class DashboardAdminGUIView extends DashboardGUIView {

    public final Button reportBtn = new Button("Report statistiche");

    public DashboardAdminGUIView() {
        reportBtn.getStyleClass().add("button");
        reportBtn.setPrefWidth(220);
        reportBtn.setPrefHeight(42);
    }

    public BorderPane buildRoot(String nomeutente, Runnable onLogout) {
        HBox navbar = buildNavbar("Admin", onLogout);

        VBox body = new VBox(20);
        body.getStyleClass().add("brainbank-background");
        body.setAlignment(Pos.CENTER);
        body.setPadding(new Insets(40));
        body.getChildren().add(reportBtn);

        BorderPane root = new BorderPane();
        root.getStyleClass().add("brainbank-background");
        root.setTop(navbar);
        root.setCenter(body);
        return root;
    }
}
