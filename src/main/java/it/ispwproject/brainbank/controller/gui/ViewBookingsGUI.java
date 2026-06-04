package it.ispwproject.brainbank.controller.gui;

import it.ispwproject.brainbank.bean.BookingResponseBean;
import it.ispwproject.brainbank.controller.applicativo.BookingController;
import it.ispwproject.brainbank.enumerator.BookingStatus;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.pattern.singleton.SessionManager;
import it.ispwproject.brainbank.view.gui.ViewBookingsGUIView;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.List;

public class ViewBookingsGUI {

    private final Stage stage;
    private final BookingController bookingController = new BookingController();
    private final ViewBookingsGUIView view = new ViewBookingsGUIView();

    public ViewBookingsGUI(Stage stage) { this.stage = stage; }

    public void show() {
        int studentId = SessionManager.getInstance().getLoggedUser().getId();
        BorderPane root = view.buildRoot(MainGUI::showDashboardStudent);
        view.clearError();

        try {
            List<BookingResponseBean> bookings = bookingController.getStudentBookings(studentId);
            List<BookingResponseBean> past     = bookingController.getStudentPastBookings(studentId);

            List<BookingResponseBean> confirmed = bookings.stream()
                    .filter(b -> b.getStatus() == BookingStatus.CONFIRMED)
                    .filter(b -> b.getTimeSlot().getDate().isAfter(java.time.LocalDate.now(ZoneId.systemDefault())) ||
                            (b.getTimeSlot().getDate().isEqual(java.time.LocalDate.now(ZoneId.systemDefault())) &&
                                    b.getTimeSlot().getEndTime().isAfter(java.time.LocalTime.now(ZoneId.systemDefault()))))
                    .sorted((a, b) -> a.getTimeSlot().getDate().compareTo(b.getTimeSlot().getDate()))
                    .toList();
            List<BookingResponseBean> cancelled = bookings.stream()
                    .filter(b -> b.getStatus() == BookingStatus.CANCELLED)
                    .sorted((a, b) -> a.getTimeSlot().getDate().compareTo(b.getTimeSlot().getDate()))
                    .toList();

            view.buildContent(root, confirmed, cancelled, past, this::confirmCancel, studentId);

        } catch (DAOException e) {
            view.setError("Errore: " + e.getMessage());
            root.setCenter(view.errorLabel);
        }

        stage.setScene(GUIUtils.createScene(root));
        stage.show();
    }

    private void confirmCancel(BookingResponseBean b, int studentId) {
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
                } catch (DAOException e) {
                    view.setError("Errore: " + e.getMessage());
                }
            }
        });
    }
}