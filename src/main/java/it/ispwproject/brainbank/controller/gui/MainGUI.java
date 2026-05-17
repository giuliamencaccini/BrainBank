package it.ispwproject.brainbank.controller.gui;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainGUI extends Application {

    public static final String COLOR_PRIMARY = "#8EADC2";
    public static final String COLOR_LIGHT = "#CFE5F9";
    public static final String COLOR_DARK = "#4B4B4B";
    public static final String COLOR_WHITE = "#FFFFFF";
    public static final String COLOR_ERROR = "#C0392B";
    public static final String COLOR_SUCCESS = "#27AE60";

    public static final int WINDOW_WIDTH  = 1000;
    public static final int WINDOW_HEIGHT = 600;

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

    public static Stage getPrimaryStage() {
        return primaryStage;
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