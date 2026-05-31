package it.ispwproject.brainbank.view.gui;

import it.ispwproject.brainbank.bean.StatisticsBean;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;

import java.util.Map;

public class ReportStatisticsGUIView extends PageGUIView {

    public BorderPane buildRoot(Runnable onBack) {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("brainbank-background");
        root.setTop(buildTopBar("Report Statistiche", onBack));
        return root;
    }

    public VBox buildContent(StatisticsBean stats) {
        VBox content = new VBox(24);
        content.getStyleClass().add("brainbank-background");
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(30, 60, 30, 60));

        HBox numCards = new HBox(16);
        numCards.setAlignment(Pos.CENTER);
        numCards.setMaxWidth(760);
        numCards.getChildren().addAll(
                buildNumCard("Prenotazioni totali",    String.valueOf(stats.getTotalBookings()),    "green"),
                buildNumCard("Prenotazioni annullate", String.valueOf(stats.getCancelledBookings()), "red"),
                buildNumCard("Tasso cancellazione",    String.format("%.1f%%", stats.getCancellationRate()), "orange")
        );

        HBox rankCards = new HBox(20);
        rankCards.setAlignment(Pos.TOP_CENTER);
        rankCards.setMaxWidth(760);
        rankCards.getChildren().addAll(
                buildRankingCard("🏆  Top Tutor",   stats.getTopTutors(),   "lezioni"),
                buildRankingCard("📚  Top Materie", stats.getTopSubjects(), "prenotazioni")
        );

        content.getChildren().addAll(numCards, rankCards);
        return content;
    }

    public VBox buildErrorContent(String message) {
        VBox content = new VBox(24);
        content.getStyleClass().add("brainbank-background");
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(30, 60, 30, 60));
        Label errorLabel = new Label("Errore: " + message);
        errorLabel.getStyleClass().add("error-label");
        content.getChildren().add(errorLabel);
        return content;
    }

    private VBox buildNumCard(String labelText, String value, String color) {
        VBox card = new VBox(6);
        card.getStyleClass().add("stat-card-" + color);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPrefWidth(220);
        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("stat-value-" + color);
        Label textLabel = new Label(labelText);
        textLabel.getStyleClass().add("stat-label");
        textLabel.setWrapText(true);
        card.getChildren().addAll(valueLabel, textLabel);
        return card;
    }

    private VBox buildRankingCard(String title, Map<String, Integer> data, String unit) {
        VBox card = new VBox(12);
        card.getStyleClass().add("ranking-card");
        card.setPrefWidth(350);

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("ranking-title");
        card.getChildren().addAll(titleLabel, new Separator());

        if (data == null || data.isEmpty()) {
            Label empty = new Label("Nessun dato disponibile");
            empty.getStyleClass().add("register-label");
            card.getChildren().add(empty);
            return card;
        }

        int rank = 1;
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            Label pos = new Label("#" + rank++);
            pos.getStyleClass().add("ranking-badge");
            pos.setPrefWidth(40); pos.setAlignment(Pos.CENTER);
            Label name = new Label(entry.getKey());
            name.getStyleClass().add("ranking-row");
            HBox.setHgrow(name, Priority.ALWAYS);
            Label val = new Label(entry.getValue() + " " + unit);
            val.getStyleClass().add("ranking-badge");
            row.getChildren().addAll(pos, name, val);
            card.getChildren().add(row);
        }
        return card;
    }
}