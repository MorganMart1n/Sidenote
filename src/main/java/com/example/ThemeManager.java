package com.example;

import javafx.scene.Scene;

public class ThemeManager {
    private static String currentTheme = "/com/example/css/Default.css"; // default theme

    public static void applyTheme(Scene scene) {
        scene.getStylesheets().clear();
        if (currentTheme != null) {
            var resource = ThemeManager.class.getResource(currentTheme);
            if (resource != null) scene.getStylesheets().add(resource.toExternalForm());
        }
    }

    public static void switchTheme(String cssPath) {
        currentTheme = cssPath;
    }

    public static String getCurrentTheme() {
        return currentTheme;
    }
}