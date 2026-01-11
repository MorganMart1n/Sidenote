package com.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;

public class App extends Application {


    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/com/example/noteList.fxml"));
        
        // Use fxmlLoader here, NOT loader
        Parent root = fxmlLoader.load();

        stage.initStyle(StageStyle.TRANSPARENT);
        scene = new Scene(root, 640, 480); 
        scene.setFill(Color.TRANSPARENT);

        stage.setMinWidth(400);
        stage.setMinHeight(500);
        stage.setScene(scene);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        // Safety check to prevent the crash
        if (scene != null) {
            scene.setRoot(loadFXML(fxml));
        } else {
            System.err.println("Scene was not initialized");
        }
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void setRootWithNote(String fxml, String noteName) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        Parent root = fxmlLoader.load();

        noteViewController controller = fxmlLoader.getController();
        controller.findAndLoad(noteName);

        if (scene != null) {
            scene.setRoot(root);
        }
    }

    public static void main(String[] args) {
        launch();
    }
}