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
    private static String currentTheme = "/com/example/css/Default.css"; // default theme
    private static Map<String, Parent> screenCache = new HashMap<>();

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = loadFXML("noteList");

        stage.initStyle(StageStyle.TRANSPARENT);
        scene = new Scene(root, 640, 480); 
        scene.setFill(Color.TRANSPARENT);

        applyTheme();  // apply default theme

        stage.setMinWidth(400);
        stage.setMinHeight(500);
        stage.setScene(scene);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/com/example/images/logo.png")));
        stage.show();
    }

    /** Change theme from anywhere */
    public static void changeTheme(String themePath) {
        currentTheme = themePath;
        applyTheme();
    }

    /** Apply current theme to scene */
    private static void applyTheme() {
        if (scene != null) {
            scene.getStylesheets().clear();
            scene.getStylesheets().add(App.class.getResource(currentTheme).toExternalForm());
            
        }
    }
    public static String getThemeDisplayName() {
        switch (currentTheme) {
            case "/com/example/css/CyberpunkBlue.css":
                return "Cyberpunk Blue";
            case "/com/example/css/CyberpunkRed.css":
                return "Cyberpunk Red";
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
            applyTheme(); // reapply theme after switching pages
        }
    }

    public static void main(String[] args) {
        launch();
    }
}