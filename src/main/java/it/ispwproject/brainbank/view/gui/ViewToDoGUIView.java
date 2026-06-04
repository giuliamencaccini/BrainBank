package it.ispwproject.brainbank.view.gui;

import it.ispwproject.brainbank.bean.ActivityBean;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;
import java.util.function.Consumer;

public class ViewToDoGUIView extends PageGUIView {

    public final Label errorLabel = buildErrorLabel();

    public void setError(String message) { errorLabel.setText(message); }
    public void clearError()             { errorLabel.setText(""); }

    public BorderPane buildRoot(Runnable onBack) {
        return buildShell("To-do", onBack);
    }

    public void buildContent(BorderPane root,
                             List<ActivityBean> pending,
                             List<ActivityBean> completed,
                             Consumer<ActivityBean> onMarkDone) {
        VBox content = new VBox(16);
        content.setPadding(new Insets(24));
        content.setAlignment(Pos.TOP_CENTER);

        if (pending.isEmpty() && completed.isEmpty()) {
            Label empty = new Label("Nessuna attività assegnata.");
            empty.getStyleClass().add("register-label");
            content.getChildren().add(empty);
        } else {
            if (!pending.isEmpty()) {
                Label t = new Label("Da completare");
                t.getStyleClass().add("small-label");
                content.getChildren().add(t);
                for (ActivityBean a : pending)
                    content.getChildren().add(buildActivityCard(a, false, onMarkDone));
            }
            if (!completed.isEmpty()) {
                Label t = new Label("Completate");
                t.getStyleClass().add("small-label");
                content.getChildren().add(t);
                for (ActivityBean a : completed)
                    content.getChildren().add(buildActivityCard(a, true, onMarkDone));
            }
        }

        content.getChildren().add(errorLabel);
        root.setCenter(transparentScroll(content));
    }

    private HBox buildActivityCard(ActivityBean a, boolean done,
                                   Consumer<ActivityBean> onMarkDone) {
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

        if (a.getTutor() != null) {
            Label tutorLbl = new Label("Assegnata da: " +
                    a.getTutor().getName() + " " + a.getTutor().getSurname());
            tutorLbl.getStyleClass().add("info-text");
            info.getChildren().add(tutorLbl);
        }
        if (a.getCreatedAt() != null) {
            Label date = new Label("📅 " + a.getCreatedAt().toLocalDate()
                    .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            date.setStyle("-fx-font-size: 11px; -fx-text-fill: #888;");
            info.getChildren().add(date);
        }

        card.getChildren().addAll(icon, info);

        if (!done) {
            Button doneBtn = new Button("Segna completata");
            doneBtn.getStyleClass().add("success-button");
            doneBtn.setOnAction(e -> onMarkDone.accept(a));
            card.getChildren().add(doneBtn);
        }
        return card;
    }
}