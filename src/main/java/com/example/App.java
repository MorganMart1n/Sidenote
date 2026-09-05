package com.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class App extends Application {

    private static Scene scene;
    private static Map<String, Parent> screenCache = new HashMap<>();

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = loadFXML("noteList");

        stage.initStyle(StageStyle.TRANSPARENT);
        scene = new Scene(root, 640, 480);
        scene.setFill(Color.TRANSPARENT);

        ThemeManager.applyTheme(scene); // apply default theme

        stage.setMinWidth(400);
        stage.setMinHeight(500);
        stage.setScene(scene);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/com/example/images/logo.png")));
        stage.show();
    }

    /** Switch to one of the bundled palette stylesheets, e.g. "/com/example/css/palettes/StickyYellow.css". */
    public static void changeTheme(String paletteResourcePath) {
        ThemeManager.switchToPreset(paletteResourcePath);
        ThemeManager.applyTheme(scene);
    }

    /** Switch to a fully custom colour picked by the user. */
    public static void changeCustomColor(Color color) {
        ThemeManager.switchToCustomColor(color);
        ThemeManager.applyTheme(scene);
    }

    public static String getThemeDisplayName() {
        if (ThemeManager.getMode() == ThemeManager.Mode.CUSTOM) {
            return "Custom Colour";
        }
        switch (ThemeManager.getCurrentPalette()) {
            case "/com/example/css/CyberpunkBlue.css":
                return "Cyberpunk Blue";
            case "/com/example/css/CyberpunkRed.css":
                return "Cyberpunk Red";
            case "/com/example/css/Twilight.css":
                return "Twilight";
            case "/com/example/css/Default.css":
            default:
                return "Default";
        }
    }

    /** Load FXML without setting */
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return loader.load();
    }

    /** Set root and ensure theme applies */
    public static void setRoot(String fxml) throws IOException {
        Parent root = loadFXML(fxml);
        if (scene != null) {
            scene.setRoot(root);
            ThemeManager.applyTheme(scene); // reapply theme after switching pages
        }
    }

    public static void main(String[] args) {
        launch();
    }
}