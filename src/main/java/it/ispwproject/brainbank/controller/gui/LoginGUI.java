package it.ispwproject.brainbank.controller.gui;

import it.ispwproject.brainbank.controller.applicativo.LoginController;
import it.ispwproject.brainbank.controller.applicativo.LoginController.LoginResult;
import it.ispwproject.brainbank.exception.LoginException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class LoginGUI {

    private final Stage stage;
    private final LoginController loginController = new LoginController();

    private TextField emailField;
    private PasswordField passwordField;
    private TextField visiblePasswordField;
    private Label errorLabel;

    public LoginGUI(Stage stage) {
        this.stage = stage;
    }

    public void show() {

        HBox root = new HBox(75);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(25, 40, 25, 40));
        root.getStyleClass().add("brainbank-background");

        VBox leftPanel = buildLeftPanel();
        VBox rightPanel = buildRightPanel();

        root.getChildren().addAll(leftPanel, rightPanel);

        Scene scene = GUIUtils.createScene(root);

        stage.setScene(scene);
        stage.show();
    }

    private VBox buildLeftPanel() {

        VBox panel = new VBox(12);
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(10));

        Label benvenuto = new Label("Benvenuto su");
        benvenuto.getStyleClass().add("title-label");

        Image logo = new Image(
                getClass().getResourceAsStream("/images/logo.png")
        );

        ImageView logoView = new ImageView(logo);
        logoView.setFitWidth(145);
        logoView.setPreserveRatio(true);

        Label brand = new Label("BrainBank");
        brand.getStyleClass().add("brand-label");

        Label tagline = new Label("Connettiti al sapere!");
        tagline.getStyleClass().add("subtitle-label");

        panel.getChildren().addAll(
                benvenuto,
                logoView,
                brand,
                tagline
        );

        return panel;
    }

    private VBox buildRightPanel() {

        VBox panel = new VBox(14);
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(20));
        panel.setMaxWidth(300);

        // ── EMAIL ─────────────────────────────────────────────

        Label emailLabel = fieldLabel("Email");

        emailField = new TextField();
        emailField.setPromptText("Inserisci email");
        emailField.setPrefWidth(250);
        emailField.setPrefHeight(48);
        emailField.setOnAction(e -> passwordField.requestFocus());

        VBox emailBox = new VBox(5, emailLabel, emailField);
        emailBox.setAlignment(Pos.CENTER_LEFT);

        // ── PASSWORD ──────────────────────────────────────────

        Label passwordLabel = fieldLabel("Password");

        passwordField = new PasswordField();
        passwordField.setPromptText("Inserisci password");
        passwordField.setPrefWidth(250);
        passwordField.setPrefHeight(48);
        passwordField.setOnAction(e -> handleLogin());

        visiblePasswordField = new TextField();
        visiblePasswordField.setPromptText("Inserisci password");
        visiblePasswordField.setPrefWidth(250);
        visiblePasswordField.setPrefHeight(48);
        visiblePasswordField.setOnAction(e -> handleLogin());

        visiblePasswordField.setVisible(false);

        visiblePasswordField.managedProperty().bind(
                visiblePasswordField.visibleProperty()
        );

        passwordField.managedProperty().bind(
                passwordField.visibleProperty()
        );

        visiblePasswordField.textProperty().bindBidirectional(
                passwordField.textProperty()
        );

        StackPane passwordBox = new StackPane(
                passwordField,
                visiblePasswordField
        );

        CheckBox showPasswordCheck = new CheckBox("Mostra password");

        showPasswordCheck.selectedProperty().addListener(
                (obs, oldValue, show) -> {
                    visiblePasswordField.setVisible(show);
                    passwordField.setVisible(!show);
                }
        );

        VBox passwordContainer = new VBox(
                5,
                passwordLabel,
                passwordBox,
                showPasswordCheck
        );

        passwordContainer.setAlignment(Pos.CENTER_LEFT);

        // ── ERROR LABEL ───────────────────────────────────────

        errorLabel = new Label("");
        errorLabel.setWrapText(true);
        errorLabel.getStyleClass().add("error-label");

        // ── LOGIN BUTTON ──────────────────────────────────────

        Button loginBtn = new Button("Login");
        loginBtn.setPrefWidth(95);
        loginBtn.setPrefHeight(42);

        loginBtn.setOnAction(e -> handleLogin());

        // ── REGISTRATION ──────────────────────────────────────

        Label linkLabel = new Label("Non hai ancora un account?");
        linkLabel.getStyleClass().add("register-label");

        Hyperlink registerLink = new Hyperlink("Registrati qui");

        registerLink.setOnAction(e -> MainGUI.showRegistration());

        VBox registerBox = new VBox(
                0,
                linkLabel,
                registerLink
        );

        registerBox.setAlignment(Pos.CENTER);

        // ── ADD ALL ───────────────────────────────────────────

        panel.getChildren().addAll(
                emailBox,
                passwordContainer,
                errorLabel,
                loginBtn,
                registerBox
        );

        return panel;
    }

    private void handleLogin() {

        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {

            errorLabel.setText(
                    "Inserisci sia email che password."
            );

            return;
        }

        try {

            LoginResult result = loginController.login(
                    email,
                    password
            );

            switch (result) {

                case SUCCESSO_STUDENT ->
                        MainGUI.showDashboardStudent();

                case SUCCESSO_TUTOR ->
                        MainGUI.showDashboardTutor();

                case SUCCESSO_ADMIN ->
                        MainGUI.showDashboardAdmin();
            }

        } catch (LoginException e) {

            errorLabel.setText(e.getMessage());
        }
    }

    private Label fieldLabel(String text) {

        Label label = new Label(text);

        label.getStyleClass().add("field-label");

        return label;
    }
}