package it.ispwproject.brainbank.controller.gui;

import it.ispwproject.brainbank.bean.TimeSlotBean;
import it.ispwproject.brainbank.controller.applicativo.AvailabilityController;
import it.ispwproject.brainbank.exception.DAOException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

public class ViewSlotsGUI {

    private final Stage                  stage;
    private final AvailabilityController availabilityController = new AvailabilityController();

    public ViewSlotsGUI(Stage stage) { this.stage = stage; }

    public void show() {
        BorderPane root = buildShell();
        VBox content = new VBox(12);
        content.setPadding(new Insets(24));
        content.setAlignment(Pos.TOP_CENTER);

        Label errorLabel = new Label("");
        errorLabel.getStyleClass().add("error-label");

        try {
            List<TimeSlotBean> slots = availabilityController.getSlots();
            if (slots.isEmpty()) {
                Label empty = new Label("Non hai ancora slot disponibili.");
                empty.getStyleClass().add("register-label");
                content.getChildren().add(empty);
            } else {
                for (TimeSlotBean s : slots)
                    content.getChildren().add(buildSlotCard(s));
            }
        } catch (DAOException e) { errorLabel.setText("Errore: " + e.getMessage()); }

        content.getChildren().add(errorLabel);
        root.setCenter(transparentScroll(content));
        stage.setScene(GUIUtils.createScene(root));
        stage.show();
    }

    private HBox buildSlotCard(TimeSlotBean s) {
        HBox card = new HBox(16);
        card.getStyleClass().add("info-card");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setMaxWidth(640);

        Label dot = new Label("●");
        dot.setStyle("-fx-font-size: 20px; -fx-text-fill: " +
                (s.isAvailable() ? "#27AE60" : "#E74C3C") + ";");

        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label dateTime = new Label(s.getDate() + "   " + s.getStartTime() + " – " + s.getEndTime());
        dateTime.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #4B4B4B;");

        Label status = new Label(s.isAvailable() ? "Disponibile" : "Prenotato");
        status.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " +
                (s.isAvailable() ? "#27AE60" : "#E74C3C") + ";");

        info.getChildren().addAll(dateTime, status);

        if (!s.isAvailable()) {
            if (s.getBookedByName() != null) {
                Label student = new Label("Studente: " + s.getBookedByName());
                student.getStyleClass().add("register-label");
                info.getChildren().add(student);
            }
            if (s.getMeetLink() != null) {
                Label meet = new Label("Meet: " + s.getMeetLink());
                meet.setStyle("-fx-font-size: 11px; -fx-text-fill: #3498DB;");
                info.getChildren().add(meet);
            }
        }

        card.getChildren().addAll(dot, info);
        return card;
    }

    private BorderPane buildShell() {
        BorderPane shell = new BorderPane();
        shell.getStyleClass().add("brainbank-background");
        shell.setTop(buildTopBar("I miei slot", () -> MainGUI.showDashboardTutor()));
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

    private ScrollPane transparentScroll(javafx.scene.Node content) {
        ScrollPane scroll = new ScrollPane(content);
        scroll.getStyleClass().add("transparent-scroll");
        scroll.setFitToWidth(true);
        return scroll;
    }
}
