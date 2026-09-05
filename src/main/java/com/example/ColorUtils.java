package com.example;

import javafx.scene.paint.Color;

/**
 * Pure colour math used to turn a single user-picked colour into an entire,
 * readable theme: a background gradient, a brighter "accent" colour for the
 * title bar/icons, and a text colour that is guaranteed to stay legible.
 *
 * Nothing here touches the UI directly — {@link ThemeManager} calls into
 * this class to build the CSS variable string it injects at runtime.
 */
public final class ColorUtils {

    private ColorUtils() {}

    /** Relative luminance (0 = black, 1 = white), used to decide dark-vs-light text. */
    public static double luminance(Color c) {
        return 0.2126 * c.getRed() + 0.7152 * c.getGreen() + 0.0722 * c.getBlue();
    }

    public static boolean isDark(Color c) {
        return luminance(c) < 0.5;
    }

    /** Multiplies HSB brightness by {@code factor}, clamped to [0,1]. Hue/saturation kept. */
    public static Color adjustBrightness(Color c, double factor) {
        double b = clamp(c.getBrightness() * factor, 0, 1);
        return Color.hsb(c.getHue(), c.getSaturation(), b, c.getOpacity());
    }

    /** Pushes a colour toward vivid + high-contrast, used for the "brighter instance" accent. */
    public static Color accentFor(Color base) {
        double sat = base.getSaturation();

        if (sat < 0.05) {
            // True neutral (white / grey / black). Saturation 0 means hue is
            // mathematically undefined, and JavaFX reports it as 0 (red) by
            // convention — boosting saturation on top of that silently turns
            // every neutral note into a red accent/border. Derive a plain grey
            // instead, scaled only by brightness, so the outline stays neutral.
            double bri = isDark(base)
                    ? clamp(base.getBrightness() + 0.35, 0.55, 0.85)
                    : clamp(base.getBrightness() - 0.30, 0.25, 0.65);
            return Color.hsb(0, 0, bri);
        }

        double hue = base.getHue();
        double boostedSat = Math.max(sat, 0.55);
        double bri;
        if (isDark(base)) {
            // Muted, earthy dark tones (e.g. browns) shouldn't be pushed all the way
            // to near-max brightness — that turns a gentle brown into a much more
            // vivid, unrelated-looking orange/red. Cap those lower; fully saturated
            // dark colours keep the brighter "glowing" accent (the Cyberpunk look).
            double maxBri = sat < 0.5 ? 0.75 : 1.0;
            bri = clamp(base.getBrightness() + 0.45, 0.55, maxBri);
        } else {
            // Light/pastel background -> deepen the hue so it still reads against the paper.
            bri = clamp(base.getBrightness() - 0.35, 0.25, 0.75);
        }
        return Color.hsb(hue, boostedSat, bri);
    }

    public static Color textFor(Color base) {
        return isDark(base) ? Color.web("#F5F5F5") : Color.web("#33301E");
    }

    public static String toHex(Color c) {
        return String.format("#%02X%02X%02X",
                (int) Math.round(c.getRed() * 255),
                (int) Math.round(c.getGreen() * 255),
                (int) Math.round(c.getBlue() * 255));
    }

    public static String toRgba(Color c, double alpha) {
        return String.format("rgba(%d,%d,%d,%.2f)",
                (int) Math.round(c.getRed() * 255),
                (int) Math.round(c.getGreen() * 255),
                (int) Math.round(c.getBlue() * 255),
                alpha);
    }

    private static double clamp(double v, double lo, double hi) {
        return Math.max(lo, Math.min(hi, v));
    }
}