package it.ispwproject.brainbank.controller.gui;

import it.ispwproject.brainbank.bean.BookingResponseBean;
import it.ispwproject.brainbank.controller.applicativo.BookingController;
import it.ispwproject.brainbank.enumerator.BookingStatus;
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

import java.util.List;

public class CancelBookingGUI {

    private final Stage             stage;
    private final BookingController bookingController = new BookingController();
    private final int               studentId =
            SessionManager.getInstance().getLoggedUser().getId();

    private Label errorLabel;

    public CancelBookingGUI(Stage stage) { this.stage = stage; }

    public void show() {
        BorderPane root = buildShell();
        VBox content = new VBox(12);
        content.setPadding(new Insets(24));
        content.setAlignment(Pos.TOP_CENTER);

        errorLabel = new Label("");
        errorLabel.getStyleClass().add("error-label");

        try {
            List<BookingResponseBean> cancellable = bookingController
                    .getStudentBookings(studentId).stream()
                    .filter(b -> !b.getStatus().equals(BookingStatus.CANCELLED.name()))
                    .toList();

            if (cancellable.isEmpty()) {
                Label empty = new Label("Nessuna prenotazione attiva da annullare.");
                empty.getStyleClass().add("register-label");
                content.getChildren().add(empty);
            } else {
                Label subtitle = new Label("Seleziona la prenotazione da annullare:");
                subtitle.getStyleClass().add("small-label");
                content.getChildren().add(subtitle);
                for (BookingResponseBean b : cancellable)
                    content.getChildren().add(buildCancellableCard(b));
            }
        } catch (DAOException | BookingException e) {
            errorLabel.setText("Errore: " + e.getMessage());
        }

        content.getChildren().add(errorLabel);
        root.setCenter(transparentScroll(content));
        stage.setScene(GUIUtils.createScene(root));
        stage.show();
    }

    private HBox buildCancellableCard(BookingResponseBean b) {
        HBox card = new HBox(16);
        card.getStyleClass().add("info-card");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setMaxWidth(640);

        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label subject = new Label(b.getSubject().getName());
        subject.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #4B4B4B;");
        Label tutor = new Label("Tutor: " + b.getTutor().getName() + " " + b.getTutor().getSurname());
        tutor.getStyleClass().add("register-label");
        Label date = new Label(b.getTimeSlot().getDate() + "   " +
                b.getTimeSlot().getStartTime() + " – " + b.getTimeSlot().getEndTime());
        date.getStyleClass().add("register-label");

        info.getChildren().addAll(subject, tutor, date);

        Button cancelBtn = new Button("Annulla");
        cancelBtn.getStyleClass().add("danger-button");
        cancelBtn.setOnAction(e -> confirmCancel(b));

        card.getChildren().addAll(info, cancelBtn);
        return card;
    }

    private void confirmCancel(BookingResponseBean b) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma annullamento");
        alert.setHeaderText(null);
        alert.setContentText("Vuoi annullare la prenotazione?\n" +
                b.getSubject().getName() + " — " + b.getTimeSlot().getDate() +
                "  " + b.getTimeSlot().getStartTime() + " – " + b.getTimeSlot().getEndTime());
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
        shell.setTop(buildTopBar("Annulla prenotazione", () -> MainGUI.showDashboardStudent()));
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