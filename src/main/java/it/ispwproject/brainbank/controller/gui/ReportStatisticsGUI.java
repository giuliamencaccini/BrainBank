package it.ispwproject.brainbank.controller.gui;

import it.ispwproject.brainbank.bean.StatisticsBean;
import it.ispwproject.brainbank.controller.applicativo.ReportStatisticsController;
import it.ispwproject.brainbank.exception.DAOException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.Map;

public class ReportStatisticsGUI {

    private final Stage stage;
    private final ReportStatisticsController controller = new ReportStatisticsController();

    public ReportStatisticsGUI(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        VBox root = new VBox(22);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(35, 60, 35, 60));
        root.getStyleClass().add("brainbank-background");

        Label title = new Label("Report statistiche");
        title.getStyleClass().add("title-label");

        VBox contentBox = new VBox(18);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setMaxWidth(700);

        try {
            StatisticsBean stats = controller.getStatistics();

            HBox cardsBox = new HBox(18);
            cardsBox.setAlignment(Pos.CENTER);

            cardsBox.getChildren().addAll(
                    buildStatCard("Prenotazioni totali", String.valueOf(stats.getTotalBookings())),
                    buildStatCard("Prenotazioni annullate", String.valueOf(stats.getCancelledBookings())),
                    buildStatCard("Tasso cancellazione", String.format("%.2f%%", stats.getCancellationRate()))
            );

            HBox rankingBox = new HBox(24);
            rankingBox.setAlignment(Pos.TOP_CENTER);

            rankingBox.getChildren().addAll(
                    buildRankingCard("Top Tutor", stats.getTopTutors()),
                    buildRankingCard("Top Materie", stats.getTopSubjects())
            );

            contentBox.getChildren().addAll(cardsBox, rankingBox);

        } catch (DAOException e) {
            Label errorLabel = new Label("Errore nel caricamento delle statistiche: " + e.getMessage());
            errorLabel.getStyleClass().add("error-label");
            contentBox.getChildren().add(errorLabel);
        }

        Button backBtn = new Button("Torna alla dashboard");
        backBtn.setPrefWidth(220);
        backBtn.setPrefHeight(42);
        backBtn.setOnAction(e -> MainGUI.showDashboardAdmin());

        root.getChildren().addAll(title, contentBox, backBtn);

        Scene scene = GUIUtils.createScene(root);
        stage.setScene(scene);
        stage.show();
    }

    private VBox buildStatCard(String label, String value) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(18));
        card.setPrefWidth(200);
        card.getStyleClass().add("stat-card");

        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("stat-value");

        Label textLabel = new Label(label);
        textLabel.getStyleClass().add("stat-label");
        textLabel.setWrapText(true);

        card.getChildren().addAll(valueLabel, textLabel);
        return card;
    }

    private VBox buildRankingCard(String title, Map<String, Integer> data) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(18));
        card.setPrefWidth(300);
        card.getStyleClass().add("stat-card");

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("field-label");

        card.getChildren().add(titleLabel);

        if (data == null || data.isEmpty()) {
            Label emptyLabel = new Label("Nessun dato disponibile");
            emptyLabel.getStyleClass().add("register-label");
            card.getChildren().add(emptyLabel);
            return card;
        }

        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            Label row = new Label(entry.getKey() + " — " + entry.getValue());
            row.getStyleClass().add("register-label");
            card.getChildren().add(row);
        }

        return card;
    }
}
