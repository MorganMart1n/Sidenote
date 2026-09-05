package com.example;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Set;

public class noteSettingController {

    /** Themes that belong to the "sticky note" family: the grey Default plus every
     *  crafted/derived note colour. The Note Colour swatch grid is only shown while
     *  one of these is active — dedicated look-and-feel themes (Cyberpunk, Twilight)
     *  hide it. */
    private static final Set<String> STICKY_FAMILY_PALETTES = Set.of(
            "/com/example/css/Default.css",
            "/com/example/css/StickyYellow.css",
            "/com/example/css/StickyGreen.css",
            "/com/example/css/StickyPink.css",
            "/com/example/css/StickyBlue.css",
            "/com/example/css/StickyPurple.css"
    );

    @FXML private VBox mainRoot;
    @FXML private VBox titleBar;
    @FXML private VBox settingsContainer;
    @FXML private VBox noteColourSection;
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

    /* ==================== Bundled palette presets ==================== */

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

    @FXML
    private void switchToTwilight() {
        App.changeTheme("/com/example/css/Twilight.css");
        updateThemeLabel();
    }

    @FXML
    private void switchToStickyYellow() {
        App.changeTheme("/com/example/css/StickyYellow.css");
        updateThemeLabel();
    }

    @FXML
    private void switchToStickyGreen() {
        App.changeTheme("/com/example/css/StickyGreen.css");
        updateThemeLabel();
    }

    @FXML
    private void switchToStickyPink() {
        App.changeTheme("/com/example/css/StickyPink.css");
        updateThemeLabel();
    }

    @FXML
    private void switchToStickyBlue() {
        App.changeTheme("/com/example/css/StickyBlue.css");
        updateThemeLabel();
    }

    @FXML
    private void switchToStickyPurple() {
        App.changeTheme("/com/example/css/StickyPurple.css");
        updateThemeLabel();
    }

    /* ==================== Note colour swatches ====================
       Formerly the "Custom Colour" ColorPicker: each swatch is a fixed
       colour, and the whole theme (background gradient, text, accent,
       controls) is derived from it by ThemeManager/ColorUtils. */

    @FXML
    private void selectNoteColor(ActionEvent event) {
        if (!(event.getSource() instanceof Button button)) return;
        String hex = extractHex(button.getStyle());
        if (hex == null) return;
        App.changeCustomColor(Color.web(hex));
        updateThemeLabel();
    }

    private String extractHex(String style) {
        if (style == null) return null;
        int idx = style.indexOf('#');
        if (idx == -1 || idx + 7 > style.length()) return null;
        return style.substring(idx, idx + 7);
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
        updateNoteColourVisibility();
    }

    private void updateNoteColourVisibility() {
        if (noteColourSection == null) return;
        boolean showSwatches = ThemeManager.getMode() == ThemeManager.Mode.CUSTOM
                || STICKY_FAMILY_PALETTES.contains(ThemeManager.getCurrentPalette());
        noteColourSection.setVisible(showSwatches);
        noteColourSection.setManaged(showSwatches);
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
