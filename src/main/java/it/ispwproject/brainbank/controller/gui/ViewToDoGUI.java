package it.ispwproject.brainbank.controller.gui;

import it.ispwproject.brainbank.bean.ActivityBean;
import it.ispwproject.brainbank.controller.applicativo.ActivityController;
import it.ispwproject.brainbank.exception.DAOException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

public class ViewToDoGUI {

    private final Stage              stage;
    private final ActivityController activityController = new ActivityController();

    public ViewToDoGUI(Stage stage) { this.stage = stage; }

    public void show() {
        BorderPane root = buildShell();
        VBox content = new VBox(16);
        content.setPadding(new Insets(24));
        content.setAlignment(Pos.TOP_CENTER);

        Label errorLabel = new Label("");
        errorLabel.getStyleClass().add("error-label");

        try {
            List<ActivityBean> activities = activityController.getMyActivities();
            List<ActivityBean> pending   = activities.stream().filter(a -> !a.isCompleted()).toList();
            List<ActivityBean> completed = activities.stream().filter(ActivityBean::isCompleted).toList();

            if (activities.isEmpty()) {
                Label empty = new Label("Nessuna attività assegnata.");
                empty.getStyleClass().add("register-label");
                content.getChildren().add(empty);
            } else {
                if (!pending.isEmpty()) {
                    Label t = new Label("Da completare");
                    t.getStyleClass().add("small-label");
                    content.getChildren().add(t);
                    for (ActivityBean a : pending)
                        content.getChildren().add(buildActivityCard(a, false, errorLabel));
                }
                if (!completed.isEmpty()) {
                    Label t = new Label("Completate");
                    t.getStyleClass().add("small-label");
                    content.getChildren().add(t);
                    for (ActivityBean a : completed)
                        content.getChildren().add(buildActivityCard(a, true, errorLabel));
                }
            }
        } catch (DAOException e) { errorLabel.setText("Errore: " + e.getMessage()); }

        content.getChildren().add(errorLabel);
        root.setCenter(transparentScroll(content));
        stage.setScene(GUIUtils.createScene(root));
        stage.show();
    }

    private HBox buildActivityCard(ActivityBean a, boolean done, Label errorLabel) {
        HBox card = new HBox(16);
        card.getStyleClass().add("info-card");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setMaxWidth(640);

        Label icon = new Label(done ? "✓" : "✗");
        icon.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " +
                (done ? "#27AE60" : "#E74C3C") + ";");

        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);
        Label desc = new Label(a.getDescription());
        desc.getStyleClass().add("register-label");
        desc.setWrapText(true);
        info.getChildren().add(desc);
        if (a.getCreatedAt() != null) {
            Label date = new Label("Assegnata il: " + a.getCreatedAt().toLocalDate());
            date.setStyle("-fx-font-size: 11px; -fx-text-fill: #888;");
            info.getChildren().add(date);
        }

        card.getChildren().addAll(icon, info);

        if (!done) {
            Button doneBtn = new Button("Segna completata");
            doneBtn.getStyleClass().add("success-button");
            doneBtn.setOnAction(e -> {
                try { activityController.markActivityCompleted(a.getId()); show(); }
                catch (DAOException ex) { errorLabel.setText("Errore: " + ex.getMessage()); }
            });
            card.getChildren().add(doneBtn);
        }
        return card;
    }

    private BorderPane buildShell() {
        BorderPane shell = new BorderPane();
        shell.getStyleClass().add("brainbank-background");
        shell.setTop(buildTopBar("To-do", () -> MainGUI.showDashboardStudent()));
        return shell;
    }

    private HBox buildTopBar(String titleText, Runnable onBack) {
        HBox bar = new HBox();
        bar.getStyleClass().add("navbar");
        bar.setAlignment(Pos.CENTER_LEFT);

        Button backBtn = new Button("⟪  Indietro");
        backBtn.getStyleClass().add("back-button");
        backBtn.setOnAction(e -> onBack.run());
        HBox left = new HBox(backBtn);
        left.setAlignment(Pos.CENTER_LEFT);
        left.setPrefWidth(150);
        HBox.setHgrow(left, Priority.ALWAYS);

        Label title = new Label(titleText);
        title.getStyleClass().add("page-title");
        title.setMaxWidth(Double.MAX_VALUE);
        title.setAlignment(Pos.CENTER);
        HBox.setHgrow(title, Priority.ALWAYS);

        HBox right = new HBox();
        right.setAlignment(Pos.CENTER_RIGHT);
        right.setPrefWidth(150);
        HBox.setHgrow(right, Priority.ALWAYS);
        var logoStream = getClass().getResourceAsStream("/images/logo.png");
        if (logoStream != null) {
            ImageView logo = new ImageView(
                    new Image(logoStream, 60, 60, true, true));
            logo.setFitHeight(56); logo.setPreserveRatio(true); logo.setSmooth(true);
            right.getChildren().add(logo);
        }

        bar.getChildren().addAll(left, title, right);
        return bar;
    }

    private ScrollPane transparentScroll(javafx.scene.Node content) {
        ScrollPane scroll = new ScrollPane(content);
        scroll.getStyleClass().add("transparent-scroll");
        scroll.setFitToWidth(true);
        return scroll;
    }
}
