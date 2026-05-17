package it.ispwproject.brainbank.controller.gui;

import it.ispwproject.brainbank.bean.StatisticsBean;
import it.ispwproject.brainbank.controller.applicativo.ReportStatisticsController;
import it.ispwproject.brainbank.exception.DAOException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.Map;

public class ReportStatisticsGUI {

    private final Stage stage;
    private final ReportStatisticsController controller = new ReportStatisticsController();

    public ReportStatisticsGUI(Stage stage) { this.stage = stage; }

    public void show() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("brainbank-background");
        root.setTop(buildTopBar());
        root.setCenter(buildContent());
        stage.setScene(GUIUtils.createScene(root));
        stage.show();
    }

    // ── Topbar ───────────────────────────────────────────────────────────

    private HBox buildTopBar() {
        HBox bar = new HBox();
        bar.getStyleClass().add("page-topbar");
        bar.setAlignment(Pos.CENTER);

        Button backBtn = new Button("⟪  Indietro");
        backBtn.getStyleClass().add("back-button");
        backBtn.setOnAction(e -> MainGUI.showDashboardAdmin());

        Label title = new Label("Report Statistiche");
        title.getStyleClass().add("page-title");
        title.setMaxWidth(Double.MAX_VALUE);
        title.setAlignment(Pos.CENTER);
        HBox.setHgrow(title, Priority.ALWAYS);

        var logoStream = getClass().getResourceAsStream("/images/logo.png");
        if (logoStream != null) {
            ImageView logo = new ImageView(new Image(logoStream, 60, 60, true, true));
            logo.setFitHeight(38); logo.setPreserveRatio(true); logo.setSmooth(true);
            bar.getChildren().addAll(backBtn, title, logo);
        } else {
            bar.getChildren().addAll(backBtn, title);
        }

        return bar;
    }

    // ── Contenuto ────────────────────────────────────────────────────────

    private VBox buildContent() {
        VBox content = new VBox(24);
        content.getStyleClass().add("brainbank-background");
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(30, 60, 30, 60));

        try {
            StatisticsBean stats = controller.getStatistics();

            // ── Riga card numeriche ──────────────────────────
            HBox numCards = new HBox(16);
            numCards.setAlignment(Pos.CENTER);
            numCards.setMaxWidth(760);

            numCards.getChildren().addAll(
                    buildNumCard("Prenotazioni totali",
                            String.valueOf(stats.getTotalBookings()),
                            "green"),
                    buildNumCard("Prenotazioni annullate",
                            String.valueOf(stats.getCancelledBookings()),
                            "red"),
                    buildNumCard("Tasso cancellazione",
                            String.format("%.1f%%", stats.getCancellationRate()),
                            "orange")
            );

            // ── Riga card ranking ────────────────────────────
            HBox rankCards = new HBox(20);
            rankCards.setAlignment(Pos.TOP_CENTER);
            rankCards.setMaxWidth(760);

            rankCards.getChildren().addAll(
                    buildRankingCard("🏆  Top Tutor",    stats.getTopTutors(),    "lezioni"),
                    buildRankingCard("📚  Top Materie",  stats.getTopSubjects(),  "prenotazioni")
            );

            content.getChildren().addAll(numCards, rankCards);

        } catch (DAOException e) {
            Label errorLabel = new Label("Errore: " + e.getMessage());
            errorLabel.getStyleClass().add("error-label");
            content.getChildren().add(errorLabel);
        }

        return content;
    }

    // ── Card numerica con bordo colorato ─────────────────────────────────

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

    // ── Card ranking con lista ────────────────────────────────────────────

    private VBox buildRankingCard(String title, Map<String, Integer> data,
                                  String unit) {
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

            // Numero posizione
            Label pos = new Label("#" + rank++);
            pos.getStyleClass().add("ranking-badge");
            pos.setPrefWidth(40);
            pos.setAlignment(Pos.CENTER);

            // Nome
            Label name = new Label(entry.getKey());
            name.getStyleClass().add("ranking-row");
            HBox.setHgrow(name, Priority.ALWAYS);

            // Valore
            Label val = new Label(entry.getValue() + " " + unit);
            val.getStyleClass().add("ranking-badge");

            row.getChildren().addAll(pos, name, val);
            card.getChildren().add(row);
        }

        return card;
    }
}
