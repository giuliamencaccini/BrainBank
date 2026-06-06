package it.ispwproject.brainbank.controller.gui;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainGUI extends Application {

    public static final int WINDOW_WIDTH  = 900;
    public static final int WINDOW_HEIGHT = 580;

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        stage.setTitle("BrainBank");
        stage.setWidth(WINDOW_WIDTH);
        stage.setHeight(WINDOW_HEIGHT);
        stage.setMinWidth(WINDOW_WIDTH);
        stage.setMinHeight(WINDOW_HEIGHT);
        stage.setResizable(true);

        showLogin();
    }

    public static void showLogin() {
        new LoginGUI(primaryStage).show();
    }

    public static void showRegistration() {
        new RegistrationGUI(primaryStage).show();
    }

    public static void showDashboardStudent() {
        new DashboardStudentGUI(primaryStage).show();
    }

    public static void showDashboardTutor() {
        new DashboardTutorGUI(primaryStage).show();
    }

    public static void showDashboardAdmin() {
        new DashboardAdminGUI(primaryStage).show();
    }

    public static void launch(String[] args) {
        Application.launch(MainGUI.class, args);
    }
}