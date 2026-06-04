package it.ispwproject.brainbank.view.gui;

import it.ispwproject.brainbank.bean.TimeSlotBean;
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

public class DashboardTutorGUIView extends DashboardGUIView {

    private static final String SLOT_AVAILABLE_COLOR = "#8FBC8F";
    private static final String SLOT_BOOKED_COLOR    = "#E74C3C";

    // ScrollPane esposto al controller per aggiornare il contenuto
    public final ScrollPane calendarScroll = new ScrollPane();

    // ────────────────────────────────────────────────────────────────────────
    // Sezione calendario tutor
    // ────────────────────────────────────────────────────────────────────────

    public VBox buildCalendarSection(Runnable onPrev, Runnable onNext, Runnable onToday) {
        return super.buildCalendarSection(onPrev, onNext, onToday, calendarScroll);
    }

    public void refreshCalendar(List<TimeSlotBean> slots, int weekOffset) {
        double w = calendarScroll.getWidth() > 10 ? calendarScroll.getWidth() : 560;
        calendarScroll.setContent(buildWeekCalendar(slots, weekOffset, w));
    }

    public void bindCalendarWidth(List<TimeSlotBean> slots, int[] weekOffsetRef) {
        calendarScroll.widthProperty().addListener((obs, oldW, newW) -> {
            if (newW.doubleValue() > 10)
                calendarScroll.setContent(
                        buildWeekCalendar(slots, weekOffsetRef[0], newW.doubleValue()));
        });
    }

    private Pane buildWeekCalendar(List<TimeSlotBean> slots,
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
        addSlotBlocks(pane, slots, monday, totalHours, colW);
        return pane;
    }

    private void addSlotBlocks(Pane pane, List<TimeSlotBean> slots,
                               LocalDate firstDay, int totalHours, int colW) {
        for (TimeSlotBean s : slots) {
            int dayOffset = -1;
            for (int d = 0; d < DAYS; d++)
                if (firstDay.plusDays(d).equals(s.getDate())) { dayOffset = d; break; }
            if (dayOffset < 0) continue;

            LocalTime bStart = s.getStartTime();
            LocalTime bEnd   = s.getEndTime();
            double sf = (bStart.getHour() + bStart.getMinute() / 60.0) - HOUR_START;
            double ef = (bEnd.getHour()   + bEnd.getMinute()   / 60.0) - HOUR_START;
            if (sf < 0 || ef > totalHours) continue;

            VBox block = new VBox(2);
            block.setLayoutX(LABEL_WIDTH + dayOffset * colW + 2);
            block.setLayoutY(HEADER_H + sf * HOUR_HEIGHT);
            block.setPrefWidth(colW - 4);
            block.setPrefHeight(Math.max((ef - sf) * HOUR_HEIGHT - 2, 20));
            block.setPadding(new Insets(2, 3, 2, 3));
            block.setStyle("-fx-background-color: " +
                    (s.isAvailable() ? SLOT_AVAILABLE_COLOR : SLOT_BOOKED_COLOR) +
                    "; -fx-background-radius: 4;");

            Label top = new Label(s.isAvailable() ? "Disponibile" : "Prenotato");
            top.getStyleClass().add("calendar-block-title");
            top.setWrapText(true);
            Label time = new Label(bStart + " - " + bEnd);
            time.getStyleClass().add("calendar-block-time");
            block.getChildren().addAll(top, time);

            if (!s.isAvailable() && s.getBookedByName() != null) {
                Label stud = new Label(s.getBookedByName());
                stud.getStyleClass().add("calendar-block-time");
                stud.setWrapText(true);
                block.getChildren().add(stud);
            }

            String details = (s.isAvailable() ? "Disponibile" : "Prenotato") + "\n" +
                    "Orario: " + bStart + " – " + bEnd +
                    (!s.isAvailable() && s.getBookedByName() != null
                            ? "\nStudente: " + s.getBookedByName() : "") +
                    (s.getMeetLink() != null ? "\nMeet: " + s.getMeetLink() : "");

            Tooltip tooltip = new Tooltip(details);
            Tooltip.install(block, tooltip);

            block.setOnMouseClicked(e -> {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                        javafx.scene.control.Alert.AlertType.INFORMATION);
                alert.setTitle("Dettagli slot");
                alert.setHeaderText(s.isAvailable() ? "Slot disponibile" : "Slot prenotato");
                alert.setContentText(details);
                alert.showAndWait();
            });
            block.setStyle(block.getStyle() + " -fx-cursor: hand;");

            pane.getChildren().add(block);
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    // Sezione destra tutor
    // ────────────────────────────────────────────────────────────────────────

    public VBox buildRightSection(VBox actionGrid, VBox accordion) {
        VBox section = new VBox(14);
        section.setAlignment(Pos.TOP_CENTER);
        section.setPrefWidth(320);
        section.setMinWidth(320);
        section.setPadding(new Insets(0));

        Region spacer = new Region();
        spacer.setPrefHeight(40); spacer.setMinHeight(40); spacer.setMaxHeight(40);

        VBox.setVgrow(actionGrid, Priority.NEVER);
        VBox.setVgrow(accordion,  Priority.NEVER);

        section.getChildren().addAll(spacer, actionGrid, accordion);
        return section;
    }

    public VBox buildActionGrid(EventHandler<ActionEvent> onAvailability,
                                EventHandler<ActionEvent> onSlots,
                                EventHandler<ActionEvent> onStudents) {
        VBox col = new VBox(10);
        col.setAlignment(Pos.TOP_CENTER);
        col.getChildren().addAll(
                buildActionTile("set-availability.png", "Imposta Disponibilità", onAvailability),
                buildActionTile("time-check.png",        "I miei slot",           onSlots),
                buildActionTile("my-students.png",       "Gestisci Studenti",     onStudents)
        );
        return col;
    }
}
