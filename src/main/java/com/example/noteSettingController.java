package com.example;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class noteSettingController {

    @FXML private VBox mainRoot;
    @FXML private VBox titleBar;
    @FXML private VBox settingsContainer;
    @FXML private Button cyberpunkBtn;
    @FXML private Button defaultBtn;
    @FXML private MenuButton themeMenu;
  

    @FXML
    public void initialize() {
        // Handle window resizing and focus on load
        Platform.runLater(() -> {
            if (mainRoot != null && titleBar != null) {
                WindowResizer.makeResizable(mainRoot, titleBar, 400, 500);
            }
        });
        updateThemeLabel();
    }

        @FXML
        private void switchToCyberpunkBlue() {
            App.changeTheme("/com/example/css/CyberpunkBlue.css");
            updateThemeLabel();
        }
          @FXML
        private void switchToCyberpunkRed() {
            App.changeTheme("/com/example/css/CyberpunkRed.css");
            updateThemeLabel();
        }

        @FXML
        private void switchToDefault() {
            App.changeTheme("/com/example/css/Default.css");
            updateThemeLabel();
        }

    /** Navigation and Window Controls */
    @FXML
    private void switchToNoteList(ActionEvent event) {
        try {
            App.setRoot("noteList");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void updateThemeLabel() {
    themeMenu.setText(App.getThemeDisplayName());
    }

    @FXML
    private void closeWindow(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    @FXML
    private void minimizeWindow(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).setIconified(true);
    }

    @FXML
    private void apearOnTop(ActionEvent event) {
        ApearOnTop.apearOnTop(event);
    }
}