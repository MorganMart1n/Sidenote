package com.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class noteListController {

    @FXML private VBox mainRoot;
    @FXML private HBox titleBar;
    @FXML private VBox noteContainer;
    @FXML private javafx.scene.control.Button plusBtn; 




private final Path notesPath = Paths.get("src/main/java/com/example/Notes");

    @FXML
    //At startup load files
    public void initialize() {
        try {
            if (!Files.exists(notesPath)) {
                Files.createDirectories(notesPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Platform.runLater(() -> {
            if (mainRoot != null) WindowResizer.makeResizable(mainRoot, titleBar, 400, 500);
        });

        loadNotes();
    }
    //Load each index and get the relevant id 
    private void loadNotes() {
        AtomicInteger index = new AtomicInteger(0);
        try (Stream<Path> stream = Files.list(notesPath)) {
            Map<Integer, String> fileMap = stream
                .filter(p -> !Files.isDirectory(p))
                .map(p -> {
                    String fileName = p.getFileName().toString();
                    int dotIdx = fileName.lastIndexOf('.');
                    return (dotIdx == -1) ? fileName : fileName.substring(0, dotIdx);
                })
                .collect(Collectors.toMap(name -> index.incrementAndGet(), name -> name));
            //Implement on GUI
            FillGUI(fileMap);
        } catch (Exception e) {
            System.err.println("Could not load notes from: " + notesPath.toAbsolutePath());
            e.printStackTrace();
        }
    }
    //Add onto GUI
    private void FillGUI(Map<Integer, String> fileMap) {
        noteContainer.getChildren().clear();
        if (fileMap.isEmpty()) {
            Label emptyLabel = new Label("No notes found.");
            emptyLabel.setStyle("-fx-text-fill: rgba(255,255,255,0.5); -fx-font-style: italic;");
            noteContainer.getChildren().add(emptyLabel);
            return;
        }
        //Loop entry map id + name
        for (Map.Entry<Integer, String> entry : fileMap.entrySet()) {
            String fileName = entry.getValue();
            Label label = new Label(fileName);
            label.getStyleClass().add("note-button"); 
            label.setAlignment(Pos.CENTER_LEFT);
            label.setMaxWidth(Double.MAX_VALUE);
            label.setPrefHeight(60);
            label.setOnMouseClicked(event -> switchToNote(fileName));
            noteContainer.getChildren().add(label);
        }
    }
    @FXML private void closeWindow(ActionEvent event) {
    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    stage.close();
}
    @FXML private void minimizeWindow(ActionEvent event) {
    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    stage.setIconified(true);
}
    
    @FXML 
    private void createNewNoteSwitch() { 
        try { 
            App.setRoot("noteCreation"); 
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }
    
    public void switchToNote(String clickedName) {
        try {
            noteViewController.selectedNoteName = clickedName;
            App.setRoot("noteView");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

        public void noteSettings() {
        try {
      
            App.setRoot("noteSettings");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private boolean value = false;
    public void apearOnTop(ActionEvent event){
            ApearOnTop.apearOnTop(event);

            }



      
 
}