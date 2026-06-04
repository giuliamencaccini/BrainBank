package it.ispwproject.brainbank.view.gui;

import it.ispwproject.brainbank.bean.BookingResponseBean;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

public class DashboardStudentGUIView extends DashboardGUIView {

    private static final String BOOKING_COLOR = "#8FBC8F";

    // ScrollPane esposto al controller per aggiornare il contenuto
    public final ScrollPane calendarScroll = new ScrollPane();

    // ────────────────────────────────────────────────────────────────────────
    // Sezione calendario studente
    // ────────────────────────────────────────────────────────────────────────

    public VBox buildCalendarSection(Runnable onPrev, Runnable onNext, Runnable onToday) {
        return super.buildCalendarSection(onPrev, onNext, onToday, calendarScroll);
    }

    public void refreshCalendar(List<BookingResponseBean> bookings,
                                int weekOffset) {
        double w = calendarScroll.getWidth() > 10 ? calendarScroll.getWidth() : 560;
        calendarScroll.setContent(buildWeekCalendar(bookings, weekOffset, w));
    }

    public void bindCalendarWidth(List<BookingResponseBean> bookings, int[] weekOffsetRef) {
        calendarScroll.widthProperty().addListener((obs, oldW, newW) -> {
            if (newW.doubleValue() > 10)
                calendarScroll.setContent(
                        buildWeekCalendar(bookings, weekOffsetRef[0], newW.doubleValue()));
        });
    }

    private Pane buildWeekCalendar(List<BookingResponseBean> bookings,
                                   int weekOffset, double availWidth) {
        LocalDate today  = LocalDate.now(ZoneId.systemDefault());
        LocalDate monday = today.with(DayOfWeek.MONDAY).plusWeeks(weekOffset);

        int totalHours = HOUR_END - HOUR_START;
        int gridHeight = totalHours * HOUR_HEIGHT;
        int colW = (int) Math.max(48, (availWidth - LABEL_WIDTH - 2) / DAYS);

        Pane pane = buildCalendarPane(monday, colW);
        addMonthRow(pane, monday, colW);
        addDayHeaders(pane, monday, today, colW);
        addHourRows(pane, totalHours, colW, gridHeight);
        addBookingBlocks(pane, bookings, monday, totalHours, colW);
        return pane;
    }

    private void addBookingBlocks(Pane pane, List<BookingResponseBean> bookings,
                                  LocalDate firstDay, int totalHours, int colW) {
        for (BookingResponseBean b : bookings) {
            if (b.getStatus().equals("CANCELLED")) continue;
            LocalDate bDate  = b.getTimeSlot().getDate();
            LocalTime bStart = b.getTimeSlot().getStartTime();
            LocalTime bEnd   = b.getTimeSlot().getEndTime();

            int dayOffset = -1;
            for (int d = 0; d < DAYS; d++)
                if (firstDay.plusDays(d).equals(bDate)) { dayOffset = d; break; }
            if (dayOffset < 0) continue;

            double sf = (bStart.getHour() + bStart.getMinute() / 60.0) - HOUR_START;
            double ef = (bEnd.getHour()   + bEnd.getMinute()   / 60.0) - HOUR_START;
            if (sf < 0 || ef > totalHours) continue;

            VBox block = new VBox(1);
            block.setLayoutX(LABEL_WIDTH + dayOffset * colW + 2);
            block.setLayoutY(HEADER_H + sf * HOUR_HEIGHT);
            block.setPrefWidth(colW - 4);
            block.setPrefHeight(Math.max((ef - sf) * HOUR_HEIGHT - 2, 20));
            block.setPadding(new Insets(2, 3, 2, 3));
            block.setStyle("-fx-background-color: " + BOOKING_COLOR +
                    "; -fx-background-radius: 4;");

            Label s = new Label(b.getSubject().getName() + " " + b.getTutor().getSurname());
            s.getStyleClass().add("calendar-block-title");
            s.setWrapText(true);
            Label t = new Label(bStart.getHour() + "-" + bEnd.getHour() +
                    (bEnd.getHour() < 12 ? "AM" : "PM"));
            t.getStyleClass().add("calendar-block-time");
            block.getChildren().addAll(s, t);

            // Tooltip
            Tooltip tooltip = new Tooltip(
                    b.getSubject().getName() + "\n" +
                            "Tutor: " + b.getTutor().getName() + " " + b.getTutor().getSurname() + "\n" +
                            bStart + " – " + bEnd + "\n" +
                            (b.getMeetLink() != null ? "Meet: " + b.getMeetLink() : ""));
            Tooltip.install(block, tooltip);

            // Click → dialog
            block.setOnMouseClicked(e -> {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                        javafx.scene.control.Alert.AlertType.INFORMATION);
                alert.setTitle("Dettagli lezione");
                alert.setHeaderText(b.getSubject().getName());
                alert.setContentText(
                        "Tutor:   " + b.getTutor().getName() + " " + b.getTutor().getSurname() + "\n" +
                                "Data:    " + bDate + "\n" +
                                "Orario:  " + bStart + " – " + bEnd + "\n" +
                                (b.getMeetLink() != null ? "Meet:    " + b.getMeetLink() : ""));
                alert.showAndWait();
            });
            block.setStyle(block.getStyle() + " -fx-cursor: hand;");

            pane.getChildren().add(block);
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    // Sezione destra studente
    // ────────────────────────────────────────────────────────────────────────

    public VBox buildRightSection(VBox actionButtons, VBox accordion) {
        VBox section = new VBox(14);
        section.setAlignment(Pos.TOP_CENTER);
        section.setPrefWidth(320);
        section.setMinWidth(320);
        section.setPadding(new Insets(0));

        Region spacer = new Region();
        spacer.setPrefHeight(40); spacer.setMinHeight(40); spacer.setMaxHeight(40);

        VBox.setVgrow(actionButtons, Priority.NEVER);
        VBox.setVgrow(accordion,     Priority.NEVER);

        section.getChildren().addAll(spacer, actionButtons, accordion);
        return section;
    }

    public VBox buildActionButtons(EventHandler<ActionEvent> onBook,
                                   EventHandler<ActionEvent> onViewBookings,
                                   EventHandler<ActionEvent> onTodo) {
        VBox buttons = new VBox(14);
        buttons.setAlignment(Pos.CENTER);
        buttons.getChildren().addAll(
                buildActionTile("booking.png",        "Prenota Lezione",      onBook),
                buildActionTile("lightbulb-on.png",   "Le mie prenotazioni",  onViewBookings),
                buildActionTile("task-checklist.png",  "To-do",               onTodo)
        );
        return buttons;
    }
}