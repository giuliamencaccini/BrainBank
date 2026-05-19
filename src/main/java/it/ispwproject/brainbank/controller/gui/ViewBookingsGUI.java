package it.ispwproject.brainbank.controller.gui;

import it.ispwproject.brainbank.bean.BookingResponseBean;
import it.ispwproject.brainbank.controller.applicativo.BookingController;
import it.ispwproject.brainbank.exception.BookingException;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.util.singleton.SessionManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ViewBookingsGUI {

    private final Stage stage;
    private final BookingController bookingController = new BookingController();
    private final int studentId = SessionManager.getInstance().getLoggedUser().getId();

    private Label errorLabel;

    public ViewBookingsGUI(Stage stage) { this.stage = stage; }

    public void show() {
        BorderPane root = buildShell();

        VBox content = new VBox(12);
        content.setPadding(new Insets(24));
        content.setAlignment(Pos.TOP_CENTER);

        errorLabel = new Label("");
        errorLabel.getStyleClass().add("error-label");

        try {
            List<BookingResponseBean> bookings = bookingController.getStudentBookings(studentId);

            List<BookingResponseBean> confirmed = bookings.stream()
                    .filter(b -> b.getStatus().equals("CONFIRMED"))
                    .sorted((a, b2) -> a.getTimeSlot().getDate().compareTo(b2.getTimeSlot().getDate()))
                    .toList();
            List<BookingResponseBean> cancelled = bookings.stream()
                    .filter(b -> b.getStatus().equals("CANCELLED"))
                    .sorted((a, b2) -> a.getTimeSlot().getDate().compareTo(b2.getTimeSlot().getDate()))
                    .toList();

            ToggleButton btnConfirmed = new ToggleButton("Confermate (" + confirmed.size() + ")");
            ToggleButton btnCancelled = new ToggleButton("Cancellate (" + cancelled.size() + ")");
            btnConfirmed.getStyleClass().add("toggle-card");
            btnCancelled.getStyleClass().add("toggle-card");
            btnConfirmed.setPrefWidth(200); btnConfirmed.setPrefHeight(36);
            btnCancelled.setPrefWidth(200); btnCancelled.setPrefHeight(36);

            ToggleGroup group = new ToggleGroup();
            btnConfirmed.setToggleGroup(group);
            btnCancelled.setToggleGroup(group);
            btnConfirmed.setSelected(true);

            HBox toggleBar = new HBox(8, btnConfirmed, btnCancelled);
            toggleBar.setAlignment(Pos.CENTER);
            toggleBar.setMaxWidth(640);

            VBox listBox = new VBox(12);
            listBox.setAlignment(Pos.TOP_CENTER);

            Runnable refreshList = () -> {
                listBox.getChildren().clear();
                List<BookingResponseBean> current = btnConfirmed.isSelected() ? confirmed : cancelled;
                if (current.isEmpty()) {
                    Label empty = new Label(btnConfirmed.isSelected()
                            ? "Non hai prenotazioni confermate."
                            : "Non hai prenotazioni cancellate.");
                    empty.getStyleClass().add("register-label");
                    listBox.getChildren().add(empty);
                } else {
                    for (BookingResponseBean b : current)
                        listBox.getChildren().add(buildBookingCard(b, btnConfirmed.isSelected()));
                }
            };

            refreshList.run();
            btnConfirmed.setOnAction(e -> refreshList.run());
            btnCancelled.setOnAction(e -> refreshList.run());

            content.getChildren().addAll(toggleBar, listBox);

        } catch (DAOException | BookingException e) {
            errorLabel.setText("Errore: " + e.getMessage());
        }

        content.getChildren().add(errorLabel);
        root.setCenter(transparentScroll(content));
        stage.setScene(GUIUtils.createScene(root));
        stage.show();
    }

    private VBox buildBookingCard(BookingResponseBean b, boolean cancellable) {
        VBox card = new VBox(8);
        card.getStyleClass().add("info-card");
        card.setMaxWidth(640);

        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);

        // Data + pallino
        Label dot = new Label("●");
        dot.getStyleClass().add(cancellable ? "success-label" : "error-label");
        dot.setStyle("-fx-font-size: 14px;");

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        Label dateTime = new Label(b.getTimeSlot().getDate().format(fmt) + "   " +
                b.getTimeSlot().getStartTime() + " – " + b.getTimeSlot().getEndTime());
        dateTime.getStyleClass().add("welcome-label");

        HBox dateRow = new HBox(8, dot, dateTime);
        dateRow.setAlignment(Pos.CENTER_LEFT);

        Label status = new Label(cancellable ? "Confermata" : "Cancellata");
        status.getStyleClass().add(cancellable ? "success-label" : "error-label");
        status.setStyle("-fx-font-weight: bold;");

        Label subject = new Label("Materia: " + b.getSubject().getName());
        subject.getStyleClass().add("small-label");

        Label tutor = new Label("Tutor: " + b.getTutor().getName() + " " + b.getTutor().getSurname());
        tutor.getStyleClass().add("register-label");

        info.getChildren().addAll(dateRow, status, subject, tutor);

        // Email tutor — contatto diretto
        if (b.getTutor().getEmail() != null) {
            Label tutorEmail = new Label("Email  " + b.getTutor().getEmail());
            tutorEmail.getStyleClass().add("info-text");
            info.getChildren().add(tutorEmail);
        }

        // Meet link + Annulla sulla stessa riga
        if (cancellable || (b.getMeetLink() != null && !b.getMeetLink().isBlank())) {
            HBox bottomRow = new HBox();
            bottomRow.setAlignment(Pos.CENTER_LEFT);
            bottomRow.setMaxWidth(Double.MAX_VALUE);

            if (b.getMeetLink() != null && !b.getMeetLink().isBlank()) {
                Hyperlink meet = new Hyperlink("🎥  Apri Meet");
                meet.getStyleClass().add("hyperlink");
                meet.setOnAction(e -> {
                    try {
                        java.awt.Desktop.getDesktop().browse(new java.net.URI(b.getMeetLink()));
                    } catch (Exception ex) {
                        // link non apribile
                    }
                });
                bottomRow.getChildren().add(meet);
            }

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            bottomRow.getChildren().add(spacer);

            if (cancellable) {
                Button cancelBtn = new Button("Annulla");
                cancelBtn.getStyleClass().add("danger-button");
                cancelBtn.setOnAction(e -> confirmCancel(b));
                bottomRow.getChildren().add(cancelBtn);
            }

            info.getChildren().add(bottomRow);
        }

        card.getChildren().add(info);

        return card;
    }

    private void confirmCancel(BookingResponseBean b) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma annullamento");
        alert.setHeaderText(null);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        alert.setContentText("Vuoi annullare la prenotazione?\n\n" +
                b.getSubject().getName() + " — " +
                b.getTimeSlot().getDate().format(fmt) + "  " +
                b.getTimeSlot().getStartTime() + " – " + b.getTimeSlot().getEndTime());
        alert.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    bookingController.cancelBooking(b.getId(), studentId);
                    show();
                } catch (DAOException | BookingException e) {
                    errorLabel.setText("Errore: " + e.getMessage());
                }
            }
        });
    }

    private BorderPane buildShell() {
        BorderPane shell = new BorderPane();
        shell.getStyleClass().add("brainbank-background");
        shell.setTop(buildTopBar("Le mie prenotazioni", () -> MainGUI.showDashboardStudent()));
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

        HBox right = new HBox();
        right.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(right, Priority.ALWAYS);
        var logoStream = getClass().getResourceAsStream("/images/logo.png");
        if (logoStream != null) {
            ImageView logo = new ImageView(new Image(logoStream, 60, 60, true, true));
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