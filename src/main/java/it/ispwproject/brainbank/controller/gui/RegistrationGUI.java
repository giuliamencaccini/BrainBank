package it.ispwproject.brainbank.controller.gui;

import it.ispwproject.brainbank.bean.RegistrationBean;
import it.ispwproject.brainbank.bean.SubjectBean;
import it.ispwproject.brainbank.controller.applicativo.RegistrationController;
import it.ispwproject.brainbank.enumerator.Role;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.exception.RegistrationException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class RegistrationGUI {

    private static final double FORM_WIDTH = 900;
    private static final double TUTOR_SECTION_WIDTH = 820;

    private final Stage stage;
    private final RegistrationController registrationController = new RegistrationController();

    private TextField nameField;
    private TextField surnameField;
    private TextField emailField;
    private PasswordField passwordField;
    private PasswordField confirmPasswordField;
    private TextField visiblePasswordField;
    private TextField visibleConfirmPasswordField;
    private RadioButton studentRadio;
    private RadioButton tutorRadio;
    private TextArea bioField;
    private VBox tutorSection;
    private ListView<SubjectBean> subjectsListView;
    private Label errorLabel;

    public RegistrationGUI(Stage stage) {
        this.stage = stage;
    }

    public void show() {

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        scrollPane.getStyleClass().addAll(
                "transparent-scroll",
                "brainbank-scroll"
        );

        VBox root = new VBox(14);
        root.setPadding(new Insets(18, 60, 24, 60));
        root.setAlignment(Pos.TOP_CENTER);
        root.getStyleClass().add("brainbank-background");

        scrollPane.viewportBoundsProperty().addListener((obs, oldBounds, newBounds) ->
                root.setMinHeight(newBounds.getHeight())
        );

        HBox header = buildHeader();

        Label title = new Label("Registrazione");
        title.getStyleClass().add("title-label");

        ImageView logo = buildLogo();

        GridPane form = buildForm();

        errorLabel = new Label("");
        errorLabel.setWrapText(true);
        errorLabel.getStyleClass().add("error-label");
        errorLabel.setMaxWidth(FORM_WIDTH);

        Button registerBtn = new Button("Registrami");
        registerBtn.getStyleClass().add("button");
        registerBtn.setPrefWidth(130);
        registerBtn.setPrefHeight(34);
        registerBtn.setOnAction(e -> handleRegistration());

        root.getChildren().addAll(
                header,
                title,
                logo,
                form,
                tutorSection,
                errorLabel,
                registerBtn
        );

        scrollPane.setContent(root);

        Scene scene = GUIUtils.createScene(scrollPane);

        stage.setScene(scene);
        stage.show();

        loadSubjects();
    }
    private HBox buildHeader() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setMaxWidth(FORM_WIDTH);

        Button backBtn = new Button("‹ Indietro");
        backBtn.getStyleClass().add("back-button");
        backBtn.setOnAction(e -> MainGUI.showLogin());

        header.getChildren().add(backBtn);
        return header;
    }

    private ImageView buildLogo() {
        ImageView logoView = new ImageView();

        var logoStream = getClass().getResourceAsStream("/images/logo.png");
        if (logoStream != null) {
            logoView.setImage(new Image(logoStream, 80, 80, true, true));
            logoView.setFitHeight(58);
            logoView.setFitWidth(58);
            logoView.setPreserveRatio(true);
            logoView.setSmooth(true);
        }

        return logoView;
    }

    private GridPane buildForm() {
        GridPane grid = new GridPane();
        grid.setHgap(34);
        grid.setVgap(8);
        grid.setPrefWidth(FORM_WIDTH);
        grid.setMaxWidth(FORM_WIDTH);
        grid.setAlignment(Pos.CENTER);

        nameField = styledTextField("inserisci nome");
        surnameField = styledTextField("inserisci cognome");
        emailField = styledTextField("inserisci email");
        passwordField = styledPasswordField("inserisci password");
        confirmPasswordField = styledPasswordField("ripeti password");
        passwordField = styledPasswordField("inserisci password");
        confirmPasswordField = styledPasswordField("ripeti password");
        visiblePasswordField = styledTextField("inserisci password");
        visibleConfirmPasswordField = styledTextField("ripeti password");

        visiblePasswordField.setVisible(false);
        visibleConfirmPasswordField.setVisible(false);

        visiblePasswordField.managedProperty().bind(
                visiblePasswordField.visibleProperty()
        );

        visibleConfirmPasswordField.managedProperty().bind(
                visibleConfirmPasswordField.visibleProperty()
        );

        passwordField.managedProperty().bind(
                passwordField.visibleProperty()
        );

        confirmPasswordField.managedProperty().bind(
                confirmPasswordField.visibleProperty()
        );

        visiblePasswordField.textProperty().bindBidirectional(
                passwordField.textProperty()
        );

        visibleConfirmPasswordField.textProperty().bindBidirectional(
                confirmPasswordField.textProperty()
        );

        VBox leftColumn = new VBox(6);
        leftColumn.getChildren().addAll(
                fieldBlock("Nome *", nameField),
                fieldBlock("Cognome *", surnameField),
                fieldBlock("Email *", emailField),
                requiredLabel()
        );

        VBox centerColumn = new VBox(12);
        centerColumn.setAlignment(Pos.TOP_LEFT);
        centerColumn.getChildren().addAll(
                fieldLabel("Ruolo *"),
                buildRoleBox()
        );

        VBox rightColumn = new VBox(6);

        CheckBox showPasswords = showPasswordsCheck();

        HBox showBox = new HBox(showPasswords);
        showBox.setAlignment(Pos.CENTER_RIGHT);

        rightColumn.getChildren().addAll(
                passwordBlock(),
                passwordRules(),
                confirmPasswordBlock(),
                showBox
        );

        grid.add(leftColumn, 0, 0);
        grid.add(centerColumn, 1, 0);
        grid.add(rightColumn, 2, 0);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPrefWidth(270);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPrefWidth(180);

        ColumnConstraints col3 = new ColumnConstraints();
        col3.setPrefWidth(320);

        grid.getColumnConstraints().addAll(col1, col2, col3);

        tutorSection = buildTutorSection();
        tutorSection.setVisible(false);
        tutorSection.setManaged(false);

        return grid;
    }

    private VBox fieldBlock(String labelText, Control field) {
        VBox box = new VBox(3);
        box.getChildren().addAll(fieldLabel(labelText), field);
        return box;
    }

    private Label requiredLabel() {
        Label label = new Label("* campi obbligatori");
        label.getStyleClass().add("register-label");
        return label;
    }

    private Label passwordRules() {
        Label label = new Label("""
                La tua password deve includere:
                • almeno 8 caratteri
                • una lettera maiuscola
                • almeno un numero""");
        label.getStyleClass().add("register-label");
        label.setWrapText(true);
        label.setMaxWidth(300);
        return label;
    }

    private VBox buildRoleBox() {
        ToggleGroup roleGroup = new ToggleGroup();

        studentRadio = new RadioButton("Studente");
        studentRadio.setToggleGroup(roleGroup);
        studentRadio.setSelected(true);

        tutorRadio = new RadioButton("Tutor");
        tutorRadio.setToggleGroup(roleGroup);

        tutorRadio.setOnAction(e -> {
            tutorSection.setVisible(true);
            tutorSection.setManaged(true);
        });

        studentRadio.setOnAction(e -> {
            tutorSection.setVisible(false);
            tutorSection.setManaged(false);
        });

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

        Label bioLabel = fieldLabel("Bio");

        bioField = new TextArea();
        bioField.setPromptText("Breve descrizione di te e delle tue competenze");
        bioField.setPrefRowCount(3);
        bioField.setPrefWidth(TUTOR_SECTION_WIDTH);
        bioField.setMaxWidth(TUTOR_SECTION_WIDTH);
        bioField.setWrapText(true);

        Label subjectsLabel = fieldLabel("Materie che insegni");

        subjectsListView = new ListView<>();
        subjectsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        subjectsListView.setPrefHeight(115);
        subjectsListView.setPrefWidth(TUTOR_SECTION_WIDTH);
        subjectsListView.setMaxWidth(TUTOR_SECTION_WIDTH);

        subjectsListView.setCellFactory(lv -> new ListCell<>() {
            private final CheckBox checkBox = new CheckBox();

            @Override
            protected void updateItem(SubjectBean item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                    checkBox.setOnAction(null);
                    return;
                }

                checkBox.setText(item.getName());
                checkBox.setSelected(subjectsListView.getSelectionModel().isSelected(getIndex()));

                checkBox.setOnAction(e -> {
                    int index = getIndex();

                    if (index < 0 || index >= subjectsListView.getItems().size()) {
                        return;
                    }

                    if (checkBox.isSelected()) {
                        subjectsListView.getSelectionModel().select(index);
                    } else {
                        subjectsListView.getSelectionModel().clearSelection(index);
                    }
                });

                setGraphic(checkBox);
                setText(null);
            }
        });

        section.getChildren().addAll(
                bioLabel,
                bioField,
                subjectsLabel,
                subjectsListView
        );

        return section;
    }

    private void handleRegistration() {
        RegistrationBean bean = new RegistrationBean();

        bean.setName(nameField.getText().trim());
        bean.setSurname(surnameField.getText().trim());
        bean.setEmail(emailField.getText().trim());
        bean.setPassword(passwordField.getText().trim());
        bean.setConfirmPassword(confirmPasswordField.getText().trim());
        bean.setRole(tutorRadio.isSelected() ? Role.TUTOR : Role.STUDENT);

        if (tutorRadio.isSelected()) {
            bean.setBio(bioField.getText().trim());

            List<SubjectBean> selected = new ArrayList<>(
                    subjectsListView.getSelectionModel().getSelectedItems()
            );

            bean.setSubjects(selected);
        }

        try {
            registrationController.register(bean);
            showSuccess();
        } catch (RegistrationException e) {
            errorLabel.setText(e.getMessage());
        } catch (DAOException e) {
            errorLabel.setText("Errore di sistema: " + e.getMessage());
        }
    }

    private void showSuccess() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Registrazione completata");
        alert.setHeaderText(null);
        alert.setContentText("Registrazione completata! Ora puoi effettuare il login.");
        alert.showAndWait();

        MainGUI.showLogin();
    }

    private void loadSubjects() {
        try {
            List<SubjectBean> availableSubjects = registrationController.getAvailableSubjects();
            subjectsListView.getItems().setAll(availableSubjects);
        } catch (DAOException e) {
            errorLabel.setText("Errore nel caricamento delle materie.");
        }
    }

    private Label fieldLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("small-label");
        return label;
    }

    private TextField styledTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setPrefWidth(270);
        field.setPrefHeight(30);
        return field;
    }

    private PasswordField styledPasswordField(String prompt) {
        PasswordField field = new PasswordField();
        field.setPromptText(prompt);
        field.setPrefWidth(320);
        field.setPrefHeight(30);
        return field;
    }

    private VBox passwordBlock() {

        StackPane stack = new StackPane(
                passwordField,
                visiblePasswordField
        );

        VBox box = new VBox(
                3,
                fieldLabel("Password *"),
                stack
        );

        return box;
    }

    private VBox confirmPasswordBlock() {

        StackPane stack = new StackPane(
                confirmPasswordField,
                visibleConfirmPasswordField
        );
        VBox box = new VBox(
                3,
                fieldLabel("Conferma Password *"),
                stack
        );

        return box;
    }

    private CheckBox showPasswordsCheck() {

        CheckBox showPassword = new CheckBox("Mostra password");

        showPassword.selectedProperty().addListener(
                (obs, oldVal, show) -> {

                    visiblePasswordField.setVisible(show);
                    passwordField.setVisible(!show);

                    visibleConfirmPasswordField.setVisible(show);
                    confirmPasswordField.setVisible(!show);
                }
        );

        return showPassword;
    }
}

