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
import java.nio.file.Path;
import java.nio.file.Paths;

public class App extends Application {
    private static Scene scene;
    @Override
    public void start(Stage stage) throws IOException {
        

        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/com/example/noteList.fxml"));
        Parent root = fxmlLoader.load();
        stage.initStyle(StageStyle.TRANSPARENT);
        scene = new Scene(root, 640, 480); 
        scene.setFill(Color.TRANSPARENT);
        
        stage.setMinWidth(400);
        stage.setMinHeight(500);
        stage.setScene(scene);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/com/example/images/logo.png")));
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
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