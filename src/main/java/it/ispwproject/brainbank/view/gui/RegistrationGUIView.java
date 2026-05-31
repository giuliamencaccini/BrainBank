package it.ispwproject.brainbank.view.gui;

import it.ispwproject.brainbank.bean.SubjectBean;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.util.List;

public class RegistrationGUIView {

    private static final double FORM_WIDTH         = 900;
    private static final double TUTOR_SECTION_WIDTH = 820;

    // Campi esposti al controller
    public final TextField      nameField                    = new TextField();
    public final TextField      surnameField                 = new TextField();
    public final TextField      emailField                   = new TextField();
    public final PasswordField  passwordField                = new PasswordField();
    public final PasswordField  confirmPasswordField         = new PasswordField();
    public final TextField      visiblePasswordField         = new TextField();
    public final TextField      visibleConfirmPasswordField  = new TextField();
    public final RadioButton    studentRadio                 = new RadioButton("Studente");
    public final RadioButton    tutorRadio                   = new RadioButton("Tutor");
    public final TextArea       bioField                     = new TextArea();
    public final ListView<SubjectBean> subjectsListView      = new ListView<>();
    public final Label          errorLabel                   = new Label("");
    public final Button         registerBtn                  = new Button("Registrami");

    private VBox tutorSection;

    public RegistrationGUIView() {
        // Password bindings
        visiblePasswordField.setVisible(false);
        visibleConfirmPasswordField.setVisible(false);
        visiblePasswordField.managedProperty().bind(visiblePasswordField.visibleProperty());
        visibleConfirmPasswordField.managedProperty().bind(visibleConfirmPasswordField.visibleProperty());
        passwordField.managedProperty().bind(passwordField.visibleProperty());
        confirmPasswordField.managedProperty().bind(confirmPasswordField.visibleProperty());
        visiblePasswordField.textProperty().bindBidirectional(passwordField.textProperty());
        visibleConfirmPasswordField.textProperty().bindBidirectional(confirmPasswordField.textProperty());

        errorLabel.setWrapText(true);
        errorLabel.getStyleClass().add("error-label");
        errorLabel.setMaxWidth(FORM_WIDTH);

        registerBtn.getStyleClass().add("button");
        registerBtn.setPrefWidth(130);
        registerBtn.setPrefHeight(34);

        subjectsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        subjectsListView.setPrefHeight(115);
        subjectsListView.setPrefWidth(TUTOR_SECTION_WIDTH);
        subjectsListView.setMaxWidth(TUTOR_SECTION_WIDTH);
        subjectsListView.setCellFactory(lv -> subjectCell());

        bioField.setPromptText("Breve descrizione di te e delle tue competenze");
        bioField.setPrefRowCount(3);
        bioField.setPrefWidth(TUTOR_SECTION_WIDTH);
        bioField.setWrapText(true);
    }

    public ScrollPane buildRoot(Runnable onBack) {
        tutorSection = buildTutorSection();
        tutorSection.setVisible(false);
        tutorSection.setManaged(false);

        bindRoleRadios();

        VBox root = new VBox(14);
        root.setPadding(new Insets(18, 60, 24, 60));
        root.setAlignment(Pos.TOP_CENTER);
        root.getStyleClass().add("brainbank-background");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().addAll("transparent-scroll", "brainbank-scroll");
        scrollPane.viewportBoundsProperty().addListener((obs, o, n) ->
                root.setMinHeight(n.getHeight()));

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setMaxWidth(FORM_WIDTH);
        Button backBtn = new Button("‹ Indietro");
        backBtn.getStyleClass().add("back-button");
        backBtn.setOnAction(e -> onBack.run());
        header.getChildren().add(backBtn);

        Label title = new Label("Registrazione");
        title.getStyleClass().add("title-label");

        ImageView logoView = new ImageView();
        var logoStream = getClass().getResourceAsStream("/images/logo.png");
        if (logoStream != null) {
            logoView.setImage(new Image(logoStream, 80, 80, true, true));
            logoView.setFitHeight(58); logoView.setFitWidth(58);
            logoView.setPreserveRatio(true); logoView.setSmooth(true);
        }

        GridPane form = buildForm();

        root.getChildren().addAll(header, title, logoView, form, tutorSection, errorLabel, registerBtn);
        scrollPane.setContent(root);
        return scrollPane;
    }

    public void setSubjects(List<SubjectBean> subjects) {
        subjectsListView.getItems().setAll(subjects);
    }

    public void setError(String message) { errorLabel.setText(message); }

    public List<SubjectBean> getSelectedSubjects() {
        return List.copyOf(subjectsListView.getSelectionModel().getSelectedItems());
    }

    private GridPane buildForm() {
        GridPane grid = new GridPane();
        grid.setHgap(34); grid.setVgap(8);
        grid.setPrefWidth(FORM_WIDTH); grid.setMaxWidth(FORM_WIDTH);
        grid.setAlignment(Pos.CENTER);

        nameField.setPromptText("inserisci nome");       nameField.setPrefWidth(270);    nameField.setPrefHeight(30);
        surnameField.setPromptText("inserisci cognome"); surnameField.setPrefWidth(270); surnameField.setPrefHeight(30);
        emailField.setPromptText("inserisci email");     emailField.setPrefWidth(270);   emailField.setPrefHeight(30);
        passwordField.setPromptText("inserisci password");       passwordField.setPrefWidth(320);        passwordField.setPrefHeight(30);
        confirmPasswordField.setPromptText("ripeti password");   confirmPasswordField.setPrefWidth(320); confirmPasswordField.setPrefHeight(30);
        visiblePasswordField.setPromptText("inserisci password"); visiblePasswordField.setPrefWidth(320); visiblePasswordField.setPrefHeight(30);
        visibleConfirmPasswordField.setPromptText("ripeti password"); visibleConfirmPasswordField.setPrefWidth(320); visibleConfirmPasswordField.setPrefHeight(30);

        VBox leftColumn = new VBox(6,
                fieldBlock("Nome *",    nameField),
                fieldBlock("Cognome *", surnameField),
                fieldBlock("Email *",   emailField),
                requiredLabel());

        VBox centerColumn = new VBox(12);
        centerColumn.setAlignment(Pos.TOP_LEFT);
        centerColumn.getChildren().addAll(fieldLabel("Ruolo *"), buildRoleBox());

        CheckBox showPasswords = new CheckBox("Mostra password");
        showPasswords.selectedProperty().addListener((obs, o, show) -> {
            visiblePasswordField.setVisible(show);        passwordField.setVisible(!show);
            visibleConfirmPasswordField.setVisible(show); confirmPasswordField.setVisible(!show);
        });
        HBox showBox = new HBox(showPasswords);
        showBox.setAlignment(Pos.CENTER_RIGHT);

        VBox rightColumn = new VBox(6,
                passwordBlock(), passwordRules(), confirmPasswordBlock(), showBox);

        grid.add(leftColumn,  0, 0);
        grid.add(centerColumn, 1, 0);
        grid.add(rightColumn,  2, 0);

        ColumnConstraints c1 = new ColumnConstraints(); c1.setPrefWidth(270);
        ColumnConstraints c2 = new ColumnConstraints(); c2.setPrefWidth(180);
        ColumnConstraints c3 = new ColumnConstraints(); c3.setPrefWidth(320);
        grid.getColumnConstraints().addAll(c1, c2, c3);
        return grid;
    }

    private void bindRoleRadios() {
        tutorRadio.setOnAction(e  -> { tutorSection.setVisible(true);  tutorSection.setManaged(true); });
        studentRadio.setOnAction(e -> { tutorSection.setVisible(false); tutorSection.setManaged(false); });
    }

    private VBox buildRoleBox() {
        ToggleGroup roleGroup = new ToggleGroup();
        studentRadio.setToggleGroup(roleGroup); studentRadio.setSelected(true);
        tutorRadio.setToggleGroup(roleGroup);
        VBox box = new VBox(8, studentRadio, tutorRadio);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private VBox buildTutorSection() {
        VBox section = new VBox(8);
        section.setAlignment(Pos.TOP_LEFT);
        section.setPrefWidth(TUTOR_SECTION_WIDTH);
        section.setMaxWidth(TUTOR_SECTION_WIDTH);
        section.setPadding(new Insets(12, 0, 0, 0));
        section.getChildren().addAll(
                fieldLabel("Bio"), bioField,
                fieldLabel("Materie che insegni"), subjectsListView);
        return section;
    }

    private VBox fieldBlock(String labelText, Control field) {
        return new VBox(3, fieldLabel(labelText), field);
    }

    private Label fieldLabel(String text) {
        Label lbl = new Label(text);
        lbl.getStyleClass().add("small-label");
        return lbl;
    }

    private Label requiredLabel() {
        Label lbl = new Label("* campi obbligatori");
        lbl.getStyleClass().add("register-label");
        return lbl;
    }

    private Label passwordRules() {
        Label lbl = new Label("""
                La tua password deve includere:
                • almeno 8 caratteri
                • una lettera maiuscola
                • almeno un numero""");
        lbl.getStyleClass().add("register-label");
        lbl.setWrapText(true); lbl.setMaxWidth(300);
        return lbl;
    }

    private VBox passwordBlock() {
        return new VBox(3, fieldLabel("Password *"),
                new StackPane(passwordField, visiblePasswordField));
    }

    private VBox confirmPasswordBlock() {
        return new VBox(3, fieldLabel("Conferma Password *"),
                new StackPane(confirmPasswordField, visibleConfirmPasswordField));
    }

    private ListCell<SubjectBean> subjectCell() {
        return new ListCell<>() {
            private final CheckBox checkBox = new CheckBox();
            @Override protected void updateItem(SubjectBean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); checkBox.setOnAction(null); return; }
                checkBox.setText(item.getName());
                checkBox.setSelected(subjectsListView.getSelectionModel().isSelected(getIndex()));
                checkBox.setOnAction(e -> {
                    int idx = getIndex();
                    if (idx < 0 || idx >= subjectsListView.getItems().size()) return;
                    if (checkBox.isSelected()) subjectsListView.getSelectionModel().select(idx);
                    else subjectsListView.getSelectionModel().clearSelection(idx);
                });
                setGraphic(checkBox); setText(null);
            }
        };
    }
}

