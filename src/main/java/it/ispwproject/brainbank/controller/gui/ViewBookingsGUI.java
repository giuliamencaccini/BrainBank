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

import java.util.List;

public class ViewBookingsGUI {

    private final Stage             stage;
    private final BookingController bookingController = new BookingController();

    public ViewBookingsGUI(Stage stage) { this.stage = stage; }

    public void show() {
        BorderPane root = buildShell();
        VBox content = new VBox(12);
        content.setPadding(new Insets(24));
        content.setAlignment(Pos.TOP_CENTER);

        Label errorLabel = new Label("");
        errorLabel.getStyleClass().add("error-label");

        try {
            int id = SessionManager.getInstance().getLoggedUser().getId();
            List<BookingResponseBean> bookings = bookingController.getStudentBookings(id);
            if (bookings.isEmpty()) {
                Label empty = new Label("Non hai ancora prenotazioni.");
                empty.getStyleClass().add("register-label");
                content.getChildren().add(empty);
            } else {
                for (BookingResponseBean b : bookings)
                    content.getChildren().add(buildBookingCard(b));
            }
        } catch (DAOException | BookingException e) {
            errorLabel.setText("Errore: " + e.getMessage());
        }

        content.getChildren().add(errorLabel);
        ScrollPane scroll = transparentScroll(content);
        root.setCenter(scroll);
        stage.setScene(GUIUtils.createScene(root));
        stage.show();
    }

    private VBox buildBookingCard(BookingResponseBean b) {
        VBox card = new VBox(6);
        card.getStyleClass().add("info-card");
        card.setMaxWidth(600);

        boolean confirmed = b.getStatus().equals("CONFIRMED");
        Label status = new Label("● " + b.getStatus());
        status.setStyle("-fx-text-fill: " + (confirmed ? "#27AE60" : "#E74C3C") +
                "; -fx-font-weight: bold; -fx-font-size: 12px;");

        Label subject = new Label(b.getSubject().getName());
        subject.getStyleClass().add("field-label");
        subject.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #4B4B4B;");

        Label tutor = new Label("Tutor: " + b.getTutor().getName() + " " + b.getTutor().getSurname());
        tutor.getStyleClass().add("register-label");

        Label date = new Label(b.getTimeSlot().getDate() + "   " +
                b.getTimeSlot().getStartTime() + " – " + b.getTimeSlot().getEndTime());
        date.getStyleClass().add("register-label");

        card.getChildren().addAll(status, subject, tutor, date);

        if (b.getMeetLink() != null && !b.getMeetLink().isBlank()) {
            Label meet = new Label("Meet: " + b.getMeetLink());
            meet.setStyle("-fx-font-size: 12px; -fx-text-fill: #3498DB;");
            card.getChildren().add(meet);
        }
        return card;
    }

    private BorderPane buildShell() {
        BorderPane shell = new BorderPane();
        shell.getStyleClass().add("brainbank-background");
        shell.setTop(buildTopBar("Le mie prenotazioni", () -> MainGUI.showDashboardStudent()));
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
