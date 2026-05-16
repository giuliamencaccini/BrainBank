package it.ispwproject.brainbank.controller.gui;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;

public final class GUIUtils {

    private GUIUtils() {
    }

    public static Scene createScene(Parent root) {
        loadFonts();

        Scene scene = new Scene(
                root,
                MainGUI.WINDOW_WIDTH,
                MainGUI.WINDOW_HEIGHT
        );

        scene.getStylesheets().add(
                GUIUtils.class
                        .getResource("/styles/brainbank.css")
                        .toExternalForm()
        );

        return scene;
    }

    private static void loadFonts() {
        Font.loadFont(GUIUtils.class.getResourceAsStream("/fonts/Poppins-Regular.ttf"), 14);
        Font.loadFont(GUIUtils.class.getResourceAsStream("/fonts/Poppins-Bold.ttf"), 14);
        Font.loadFont(GUIUtils.class.getResourceAsStream("/fonts/Poppins-Italic.ttf"), 14);
        Font.loadFont(GUIUtils.class.getResourceAsStream("/fonts/Poppins-BoldItalic.ttf"), 14);
    }
}