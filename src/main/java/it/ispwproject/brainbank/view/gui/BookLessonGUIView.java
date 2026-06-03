package it.ispwproject.brainbank.view.gui;

import it.ispwproject.brainbank.bean.SubjectBean;
import it.ispwproject.brainbank.bean.TimeSlotBean;
import it.ispwproject.brainbank.bean.TutorBean;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

public class BookLessonGUIView {

    // ── Dot della lifeline ───────────────────────────────────────────────────
    public final Region step1Dot = stepDot();
    public final Region step2Dot = stepDot();
    public final Region step3Dot = stepDot();

    // ── Sezione Materia ──────────────────────────────────────────────────────
    public final TextField subjectField = new TextField();
    public final ListView<SubjectBean> subjectList  = new ListView<>();

    // ── Sezione Tutor ────────────────────────────────────────────────────────
    public final VBox tutorList = new VBox(6);
    public final VBox tutorSection = buildSection("2.  Tutor");

    // ── Sezione Orario ───────────────────────────────────────────────────────
    public final VBox slotList = new VBox(6);
    public final VBox slotSection  = buildSection("3.  Orario");

    // ── Bottone e feedback ───────────────────────────────────────────────────
    public final Button bookBtn = new Button("Prenota");
    public final Label  errorLabel = new Label("");

    // ────────────────────────────────────────────────────────────────────────
    // Costruttore
    // ────────────────────────────────────────────────────────────────────────

    public BookLessonGUIView() {
        // subjectField
        subjectField.getStyleClass().add("text-field");
        subjectField.setPromptText("Cerca materia...");
        subjectField.setPrefHeight(40);

        // subjectList
        subjectList.getStyleClass().add("list-view");
        subjectList.setPrefHeight(-1);
        subjectList.setVisible(false);
        subjectList.setManaged(false);
        subjectList.setCellFactory(lv -> subjectCell());

        // sezioni tutor e slot inizialmente disabilitate
        tutorSection.setOpacity(0.5);
        tutorList.getChildren().add(hintLabel("Seleziona prima una materia"));
        tutorSection.getChildren().add(tutorList);

        slotSection.setOpacity(0.5);
        slotList.getChildren().add(hintLabel("Seleziona prima un tutor"));
        slotSection.getChildren().add(slotList);

        // bottone
        bookBtn.getStyleClass().add("button");
        bookBtn.setPrefWidth(180);
        bookBtn.setPrefHeight(44);
        bookBtn.setDisable(true);

        // error label
        errorLabel.getStyleClass().add("error-label");
        errorLabel.setWrapText(true);
    }

    // ────────────────────────────────────────────────────────────────────────
    // Costruzione della scena
    // ────────────────────────────────────────────────────────────────────────

    public BorderPane buildRoot() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("brainbank-background");
        root.setTop(buildTopBar());
        root.setCenter(buildScrollContent());
        return root;
    }

    // ────────────────────────────────────────────────────────────────────────
    // Metodi di stato UI
    // ────────────────────────────────────────────────────────────────────────

    public void setStepDone(Region dot) {
        dot.getStyleClass().setAll("step-dot-done");
    }

    public void setStepPending(Region dot) {
        dot.getStyleClass().setAll("step-dot");
    }

    public void setTutorSectionEnabled(boolean enabled) {
        tutorSection.setOpacity(enabled ? 1.0 : 0.5);
    }

    public void setSlotSectionEnabled(boolean enabled) {
        slotSection.setOpacity(enabled ? 1.0 : 0.5);
    }

    public void showSubjectList(boolean visible) {
        subjectList.setVisible(visible);
        subjectList.setManaged(visible);
    }

    public void updateSubjectListHeight(int itemCount) {
        subjectList.setPrefHeight(Math.min(itemCount * 32, 180));
    }

    public void setError(String message) {
        errorLabel.setText(message);
    }

    public void clearError() {
        errorLabel.setText("");
    }

    // ────────────────────────────────────────────────────────────────────────
    // Builder di widget pubblici (usati dal controller grafico)
    // ────────────────────────────────────────────────────────────────────────

    public ToggleButton buildToggle(String text, Object userData, ToggleGroup group) {
        ToggleButton btn = new ToggleButton(text);
        btn.getStyleClass().add("toggle-card");
        btn.setToggleGroup(group);
        btn.setUserData(userData);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPrefHeight(38);
        return btn;
    }

    public HBox buildTutorRow(TutorBean t, ToggleGroup group,
                              Runnable onInfoClick,
                              Runnable onStarClick,
                              boolean isFavourite) {
        ToggleButton toggle = new ToggleButton(t.getName() + " " + t.getSurname());
        toggle.getStyleClass().add("toggle-card");
        toggle.setToggleGroup(group);
        toggle.setUserData(t);
        toggle.setPrefHeight(38);
        HBox.setHgrow(toggle, Priority.ALWAYS);

        // Icona info
        ImageView infoIcon = new ImageView(
                new Image(getClass().getResourceAsStream("/icons/info.png")));
        infoIcon.setFitWidth(16);
        infoIcon.setFitHeight(16);
        Label info = new Label();
        info.setGraphic(infoIcon);
        info.getStyleClass().add("icon-label");
        info.setOnMouseClicked(e -> onInfoClick.run());

        // Stella
        Button star = new Button("★");
        star.getStyleClass().add("star-button");
        star.setPrefWidth(36);
        star.setPrefHeight(38);
        star.setStyle("-fx-text-fill: " + (isFavourite ? "#F1C40F" : "#CCCCCC") + ";");
        star.setOnAction(e -> {
            onStarClick.run();
            // aggiorna colore in base allo stato corrente del bean
            star.setStyle("-fx-text-fill: " + (t.isFavourite() ? "#F1C40F" : "#CCCCCC") + ";");
        });

        HBox row = new HBox(4, toggle, info, star);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMaxWidth(Double.MAX_VALUE);
        return row;
    }

    public Label buildHintLabel(String text) {
        return hintLabel(text);
    }

    // ────────────────────────────────────────────────────────────────────────
    // Builder privati
    // ────────────────────────────────────────────────────────────────────────

    private ScrollPane buildScrollContent() {
        VBox subjectSection = buildSection("1.  Materia");
        subjectSection.getChildren().addAll(subjectField, subjectList);

        HBox btnRow = new HBox(bookBtn);
        btnRow.setAlignment(Pos.CENTER);

        VBox form = new VBox(4);
        form.setAlignment(Pos.TOP_CENTER);
        form.setPadding(new Insets(20, 0, 0, 0));
        form.setPrefWidth(540);
        form.getChildren().addAll(subjectSection, tutorSection, slotSection, errorLabel, btnRow);

        Region rightSpacer = new Region();
        rightSpacer.setPrefWidth(90);
        HBox.setHgrow(rightSpacer, Priority.NEVER);

        HBox formWrapper = new HBox(buildLifeline(), form, rightSpacer);
        formWrapper.setAlignment(Pos.TOP_CENTER);
        formWrapper.getStyleClass().add("brainbank-background");
        formWrapper.setPadding(new Insets(20, 0, 0, 0));
        HBox.setHgrow(form, Priority.ALWAYS);

        ScrollPane scroll = new ScrollPane(formWrapper);
        scroll.getStyleClass().add("transparent-scroll");
        scroll.setFitToWidth(true);
        return scroll;
    }

    private GridPane buildLifeline() {
        GridPane lifeline = new GridPane();
        lifeline.setPadding(new Insets(30, 12, 0, 20));
        lifeline.setMinWidth(90);

        ColumnConstraints dotCol = new ColumnConstraints(20);
        dotCol.setHalignment(javafx.geometry.HPos.CENTER);
        ColumnConstraints txtCol = new ColumnConstraints();
        lifeline.getColumnConstraints().addAll(dotCol, txtCol);

        lifeline.add(step1Dot,   0, 0); lifeline.add(stepLabel("Materia"), 1, 0);
        Region line1 = stepLine();
        lifeline.add(line1, 0, 1);
        GridPane.setHalignment(line1, javafx.geometry.HPos.CENTER);

        lifeline.add(step2Dot,   0, 2); lifeline.add(stepLabel("Tutor"),   1, 2);
        Region line2 = stepLine();
        lifeline.add(line2, 0, 3);
        GridPane.setHalignment(line2, javafx.geometry.HPos.CENTER);

        lifeline.add(step3Dot,   0, 4); lifeline.add(stepLabel("Orario"),  1, 4);
        return lifeline;
    }

    private HBox buildTopBar() {
        HBox bar = new HBox();
        bar.getStyleClass().add("navbar");
        bar.setAlignment(Pos.CENTER_LEFT);

        Button backBtn = new Button("⟪  Indietro");
        backBtn.getStyleClass().add("back-button");
        backBtn.setOnAction(e -> it.ispwproject.brainbank.controller.gui.MainGUI.showDashboardStudent());

        HBox left = new HBox(backBtn);
        left.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(left, Priority.ALWAYS);
        left.setPrefWidth(150);

        Label title = new Label("Prenota Lezione");
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
            ImageView logo = new ImageView(new Image(logoStream, 60, 60, true, true));
            logo.setFitHeight(56); logo.setPreserveRatio(true); logo.setSmooth(true);
            right.getChildren().add(logo);
        }

        bar.getChildren().addAll(left, title, right);
        return bar;
    }

    private static Region stepDot() {
        Region dot = new Region();
        dot.getStyleClass().add("step-dot");
        return dot;
    }

    private static Region stepLine() {
        Region line = new Region();
        line.setPrefWidth(2); line.setMaxWidth(2);
        line.setPrefHeight(20);
        line.setStyle("-fx-background-color: #b8d4ea;");
        return line;
    }

    private static Label stepLabel(String text) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #4B4B4B; -fx-padding: 0 0 0 6;");
        return lbl;
    }

    private static VBox buildSection(String title) {
        VBox section = new VBox(10);
        section.getStyleClass().add("info-card");
        section.setMaxWidth(500);
        Label lbl = new Label(title);
        lbl.getStyleClass().add("small-label");
        section.getChildren().add(lbl);
        return section;
    }

    private static Label hintLabel(String text) {
        Label lbl = new Label(text);
        lbl.getStyleClass().add("info-text");
        lbl.setStyle("-fx-text-fill: #aaa; -fx-font-style: italic;");
        return lbl;
    }

    private static ListCell<SubjectBean> subjectCell() {
        return new ListCell<>() {
            @Override protected void updateItem(SubjectBean item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        };
    }
}
