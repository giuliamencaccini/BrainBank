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
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class RegistrationGUI {

    private final Stage stage;
    private final RegistrationController registrationController = new RegistrationController();

    private TextField nameField;
    private TextField surnameField;
    private TextField emailField;
    private PasswordField passwordField;
    private PasswordField confirmPasswordField;
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

        VBox root = new VBox(18);
        root.setPadding(new Insets(30, 60, 30, 60));
        root.setAlignment(Pos.TOP_CENTER);
        root.getStyleClass().add("brainbank-background");

        HBox header = buildHeader();

        Label title = new Label("Registrazione");
        title.getStyleClass().add("title-label");

        GridPane form = buildForm();

        errorLabel = new Label("");
        errorLabel.setWrapText(true);
        errorLabel.getStyleClass().add("error-label");

        Button registerBtn = new Button("Registrami");
        registerBtn.setPrefWidth(200);
        registerBtn.setPrefHeight(40);
        registerBtn.setOnAction(e -> handleRegistration());

        root.getChildren().addAll(header, title, form, tutorSection, errorLabel, registerBtn);
        scrollPane.setContent(root);

        Scene scene = GUIUtils.createScene(scrollPane);
        stage.setScene(scene);
        stage.show();

        loadSubjects();
    }

    private HBox buildHeader() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setMaxWidth(760);

        Button backBtn = new Button("←");
        backBtn.getStyleClass().add("back-button");

        backBtn.setOnAction(e -> MainGUI.showLogin());

        header.getChildren().add(backBtn);

        return header;
    }

    private GridPane buildForm() {
        GridPane grid = new GridPane();
        grid.setHgap(22);
        grid.setVgap(12);
        grid.setMaxWidth(760);

        nameField = styledTextField("Inserisci nome");
        surnameField = styledTextField("Inserisci cognome");
        emailField = styledTextField("Inserisci email");
        passwordField = styledPasswordField("Inserisci password");
        confirmPasswordField = styledPasswordField("Ripeti password");

        grid.add(fieldLabel("Nome *"), 0, 0);
        grid.add(nameField, 0, 1);

        grid.add(fieldLabel("Cognome *"), 1, 0);
        grid.add(surnameField, 1, 1);

        grid.add(fieldLabel("Email *"), 0, 2);
        grid.add(emailField, 0, 3);

        grid.add(fieldLabel("Password *"), 1, 2);
        grid.add(passwordField, 1, 3);

        grid.add(fieldLabel("Ruolo"), 0, 4);
        grid.add(buildRoleBox(), 0, 5);

        grid.add(fieldLabel("Conferma Password *"), 1, 4);
        grid.add(confirmPasswordField, 1, 5);

        tutorSection = buildTutorSection();
        tutorSection.setVisible(false);
        tutorSection.setManaged(false);

        ColumnConstraints col = new ColumnConstraints();
        col.setPercentWidth(50);
        grid.getColumnConstraints().addAll(col, col);

        return grid;
    }

    private HBox buildRoleBox() {
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

        HBox box = new HBox(20, studentRadio, tutorRadio);
        box.setAlignment(Pos.CENTER_LEFT);

        return box;
    }

    private VBox buildTutorSection() {
        VBox section = new VBox(10);
        section.setMaxWidth(760);

        Label bioLabel = fieldLabel("Bio");

        bioField = new TextArea();
        bioField.setPromptText("Breve descrizione di te e delle tue competenze");
        bioField.setPrefRowCount(3);

        Label subjectsLabel = fieldLabel("Materie che insegni");

        subjectsListView = new ListView<>();
        subjectsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        subjectsListView.setPrefHeight(120);

        subjectsListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(SubjectBean item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });

        section.getChildren().addAll(bioLabel, bioField, subjectsLabel, subjectsListView);

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
        label.getStyleClass().add("field-label");
        return label;
    }

    private TextField styledTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setPrefHeight(38);
        field.setMaxWidth(Double.MAX_VALUE);
        return field;
    }

    private PasswordField styledPasswordField(String prompt) {
        PasswordField field = new PasswordField();
        field.setPromptText(prompt);
        field.setPrefHeight(38);
        field.setMaxWidth(Double.MAX_VALUE);
        return field;
    }
}
