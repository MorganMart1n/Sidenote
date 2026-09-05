package com.example;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;

/**
 * Owns the current theme and knows how to apply it to a Scene.
 *
 * Every theme is base.css (all layout/shape rules) plus exactly one of:
 *   - a palette file (Default.css, CyberpunkBlue.css, StickyYellow.css, ...)
 *     which only overrides a handful of CSS variables, or
 *   - no file at all: a fully custom colour picked by the user, whose
 *     derived variables are computed by ColorUtils and injected as an
 *     inline style on the scene root (inline style always wins, so it
 *     cleanly overrides whatever base.css's fallback values are).
 *
 * This is what makes the system modular: adding a new preset is a ~12 line
 * CSS file with no layout code to duplicate, and the colour picker needs no
 * CSS file whatsoever.
 */
public class ThemeManager {

    private static final String BASE_CSS = "/com/example/css/base.css";

    public enum Mode { PRESET, CUSTOM }

    private static Mode currentMode = Mode.PRESET;
    private static String currentPalette = "/com/example/css/Default.css";
    private static Color currentCustomColor = Color.web("#2b5876");

    /** Switch to one of the bundled palette stylesheets (Default, Cyberpunk*, Sticky*). */
    public static void switchToPreset(String paletteResourcePath) {
        currentMode = Mode.PRESET;
        currentPalette = paletteResourcePath;
    }

    /** Switch to a fully custom, user-picked colour. No CSS file needed. */
    public static void switchToCustomColor(Color color) {
        currentMode = Mode.CUSTOM;
        currentCustomColor = color;
    }

    public static Mode getMode() {
        return currentMode;
    }

    public static String getCurrentPalette() {
        return currentPalette;
    }

    public static Color getCurrentCustomColor() {
        return currentCustomColor;
    }

    /** Applies the current theme (preset or custom) to the given scene. */
    public static void applyTheme(Scene scene) {
        if (scene == null) return;

        scene.getStylesheets().clear();
        var base = ThemeManager.class.getResource(BASE_CSS);
        if (base != null) {
            scene.getStylesheets().add(base.toExternalForm());
        } else {
            System.err.println("[ThemeManager] Could not find " + BASE_CSS
                    + " on the classpath — check it's under src/main/resources" + BASE_CSS);
        }

        Parent root = scene.getRoot();
        if (root == null) return;

        if (currentMode == Mode.PRESET) {
            // Clear any leftover inline colours from a previous custom theme.
            root.setStyle("");
            var palette = ThemeManager.class.getResource(currentPalette);
            if (palette != null) {
                scene.getStylesheets().add(palette.toExternalForm());
            } else {
                System.err.println("[ThemeManager] Could not find " + currentPalette
                        + " on the classpath — check it's under src/main/resources" + currentPalette);
            }
        } else {
            root.setStyle(buildCustomStyle(currentCustomColor));
        }
    }

    /** Builds the inline "-note-*" variable declarations for a custom colour. */
    private static String buildCustomStyle(Color base) {
        Color bgStart = ColorUtils.adjustBrightness(base, 1.12);
        Color bgEnd = ColorUtils.adjustBrightness(base, 0.82);
        Color accent = ColorUtils.accentFor(base);
        Color text = ColorUtils.textFor(base);
        boolean dark = ColorUtils.isDark(base);

        String glow = dark ? ColorUtils.toRgba(accent, 0.30) : "transparent";

        return String.join(" ",
                "-note-bg-start: " + ColorUtils.toHex(bgStart) + ";",
                "-note-bg-end: " + ColorUtils.toHex(bgEnd) + ";",
                "-note-border: " + ColorUtils.toRgba(accent, 0.30) + ";",
                "-note-text: " + ColorUtils.toHex(text) + ";",
                "-note-text-muted: " + ColorUtils.toRgba(text, 0.6) + ";",
                "-note-accent: " + ColorUtils.toHex(accent) + ";",
                "-note-control-bg: " + ColorUtils.toRgba(accent, 0.08) + ";",
                "-note-control-bg-hover: " + ColorUtils.toRgba(accent, 0.18) + ";",
                "-note-control-border: " + ColorUtils.toRgba(accent, 0.25) + ";",
                "-note-glow: " + glow + ";",
                "-note-scrollbar-thumb: " + ColorUtils.toRgba(accent, 0.5) + ";"
        );
    }
}
