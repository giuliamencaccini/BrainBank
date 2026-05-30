package it.ispwproject.brainbank.controller.gui;

import it.ispwproject.brainbank.bean.TimeSlotBean;
import it.ispwproject.brainbank.controller.applicativo.AvailabilityController;
import it.ispwproject.brainbank.exception.DAOException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

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
            List<TimeSlotBean> futuri  = availabilityController.getSlots();
            List<TimeSlotBean> passati = availabilityController.getPastSlots();

            Map<Integer, String> subjectBySlot = availabilityController.getSubjectBySlot();

            List<TimeSlotBean> prenotati   = futuri.stream().filter(s -> !s.isAvailable()).toList();
            List<TimeSlotBean> disponibili = futuri.stream().filter(TimeSlotBean::isAvailable).toList();

            ToggleButton btnDisponibili = new ToggleButton("Disponibili (" + disponibili.size() + ")");
            ToggleButton btnPrenotati   = new ToggleButton("Prenotati (" + prenotati.size() + ")");
            ToggleButton btnPassati     = new ToggleButton("Scaduti (" + passati.size() + ")");

            btnDisponibili.getStyleClass().add("toggle-card");
            btnPrenotati.getStyleClass().addAll("toggle-card","cancelled");
            btnPassati.getStyleClass().addAll("toggle-card","pending");


            btnDisponibili.setPrefWidth(180); btnDisponibili.setPrefHeight(36);
            btnPrenotati.setPrefWidth(180);   btnPrenotati.setPrefHeight(36);
            btnPassati.setPrefWidth(180);     btnPassati.setPrefHeight(36);

            ToggleGroup group = new ToggleGroup();
            btnDisponibili.setToggleGroup(group);
            btnPrenotati.setToggleGroup(group);
            btnPassati.setToggleGroup(group);
            btnDisponibili.setSelected(true);

            HBox toggleBar = new HBox(8, btnDisponibili, btnPrenotati, btnPassati);
            toggleBar.setAlignment(Pos.CENTER);
            toggleBar.setMaxWidth(640);

            VBox listBox = new VBox(12);
            listBox.setAlignment(Pos.TOP_CENTER);


            Runnable refreshList = () -> {
                listBox.getChildren().clear();
                List<TimeSlotBean> current;
                String emptyMsg;
                boolean isPast;
                if (btnDisponibili.isSelected()) {
                    current = disponibili;
                    emptyMsg = "Nessuno slot disponibile.";
                    isPast = false;
                } else if (btnPrenotati.isSelected()) {
                    current = prenotati;
                    emptyMsg = "Nessuno slot prenotato.";
                    isPast = false;
                } else {
                    current = passati;
                    emptyMsg = "Nessuno slot passato.";
                    isPast = true;
                }
                if (current.isEmpty()) {
                    Label empty = new Label(emptyMsg);
                    empty.getStyleClass().add("register-label");
                    listBox.getChildren().add(empty);
                } else {
                    for (TimeSlotBean s : current)
                        listBox.getChildren().add(
                                buildSlotCard(s, subjectBySlot.get(s.getId()), isPast));
                }
            };

            refreshList.run();
            btnDisponibili.setOnAction(e -> refreshList.run());
            btnPrenotati.setOnAction(e -> refreshList.run());
            btnPassati.setOnAction(e -> refreshList.run());

            if (futuri.isEmpty() && passati.isEmpty()) {
                Label empty = new Label("Non hai ancora slot.");
                empty.getStyleClass().add("register-label");
                content.getChildren().addAll(toggleBar, empty);
            } else {
                content.getChildren().addAll(toggleBar, listBox);
            }

        } catch (DAOException e) { errorLabel.setText("Errore: " + e.getMessage()); }

        content.getChildren().add(errorLabel);
        root.setCenter(transparentScroll(content));
        stage.setScene(GUIUtils.createScene(root));
        stage.show();
    }

    private HBox buildSlotCard(TimeSlotBean s, String subjectName, boolean isPast) {
        HBox card = new HBox(16);
        card.getStyleClass().add("info-card");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setMaxWidth(640);

        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label dot = new Label("●");
        dot.setStyle("-fx-font-size: 14px;");

        String dateStr = s.getDate().format(
                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        Label dateTime = new Label(dateStr + "   " + s.getStartTime() + " – " + s.getEndTime());
        dateTime.getStyleClass().add("welcome-label");

        HBox dateRow = new HBox(8, dot, dateTime);
        dateRow.setAlignment(Pos.CENTER_LEFT);

        Label status = new Label();

        info.getChildren().addAll(dateRow, status);

        if (isPast) {
            if (!s.isAvailable()) {
                dot.getStyleClass().add("error-label");
                status.setText("Utilizzato");
                status.getStyleClass().add("error-label");
            } else {
                dot.getStyleClass().add("past-label");
                status.setText("Non utilizzato");
                status.getStyleClass().add("past-label");
            }
        } else if (!s.isAvailable()) {
            dot.getStyleClass().add("error-label");
            status.setText("Prenotato");
            status.getStyleClass().add("error-label");
            if (subjectName != null) {
                Label subject = new Label("Materia: " + subjectName);
                subject.getStyleClass().add("small-label");
                info.getChildren().add(subject);
            }
            if (s.getBookedByName() != null) {
                Label student = new Label("Studente: " + s.getBookedByName());
                student.getStyleClass().add("register-label");
                info.getChildren().add(student);
            }
            if (s.getMeetLink() != null) {
                Label meet = new Label("Meet: " + s.getMeetLink());
                meet.getStyleClass().add("info-text");
                meet.setStyle("-fx-text-fill: #3498DB;");
                info.getChildren().add(meet);
            }
        } else {
            dot.getStyleClass().add("success-label");
            status.setText("Disponibile");
            status.getStyleClass().add("success-label");
            Button deleteBtn = new Button("Elimina");
            deleteBtn.getStyleClass().add("danger-button");
            deleteBtn.setOnAction(e -> {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Conferma eliminazione");
                confirm.setHeaderText(null);
                confirm.setContentText("Vuoi eliminare questo slot?\n" +
                        s.getDate() + "  " + s.getStartTime() + " – " + s.getEndTime());
                confirm.showAndWait().ifPresent(r -> {
                    if (r == ButtonType.OK) {
                        try {
                            availabilityController.deleteSlot(s.getId());
                            show();
                        } catch (DAOException ex) {
                            // errore eliminazione
                        }
                    }
                });
            });
            HBox btnRow = new HBox(deleteBtn);
            btnRow.setAlignment(Pos.CENTER_RIGHT);
            info.getChildren().add(btnRow);
        }

        card.getChildren().add(info);
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
        bar.getStyleClass().add("navbar");
        bar.setAlignment(Pos.CENTER_LEFT);

        Button backBtn = new Button("⟪  Indietro");
        backBtn.getStyleClass().add("back-button");
        backBtn.setOnAction(e -> onBack.run());
        HBox left = new HBox(backBtn);
        left.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(left, Priority.ALWAYS);

        Label title = new Label(titleText);
        title.getStyleClass().add("page-title");
        title.setMaxWidth(Double.MAX_VALUE);
        title.setAlignment(Pos.CENTER);
        HBox.setHgrow(title, Priority.ALWAYS);

        var logoStream = getClass().getResourceAsStream("/images/logo.png");
        HBox right = new HBox();
        right.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(right, Priority.ALWAYS);
        if (logoStream != null) {
            javafx.scene.image.ImageView logo = new javafx.scene.image.ImageView(
                    new javafx.scene.image.Image(logoStream, 60, 60, true, true));
            logo.setFitHeight(56); logo.setPreserveRatio(true); logo.setSmooth(true);
            right.getChildren().add(logo);
        }

        left.setPrefWidth(150);
        right.setPrefWidth(150);

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