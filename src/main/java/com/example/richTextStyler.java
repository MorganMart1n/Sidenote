package com.example;

import org.fxmisc.richtext.InlineCssTextArea;
import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

public class richTextStyler {

    private final InlineCssTextArea contentArea;
    private final VBox sliderContainer;
    private final Slider slide = new Slider(10, 50, 12);
    private final Runnable onContentChanged;

    private String currentTextStyle = "-fx-font-family: 'Segoe UI'; -fx-fill: white;";
    private int currentFontSize = 12;
    private boolean isSyncing = false;
    private boolean isSliderVisible = false;
    private Boolean internalChange = false;

    public richTextStyler(InlineCssTextArea contentArea, VBox sliderContainer, Runnable onContentChanged) {
        this.contentArea = contentArea;
        this.sliderContainer = sliderContainer;
        this.onContentChanged = onContentChanged;

        setupSlider();
        contentAreaListeners();
    }

    private String getFinalStyle() {
        String base = currentTextStyle.replaceAll("-fx-font-size: \\d+pt;?", "").trim();
        return base + (base.endsWith(";") ? "" : ";") + " -fx-font-size: " + currentFontSize + "pt;";
    }

    public void applyBold() {
        if (currentTextStyle.contains("bold")) {
            currentTextStyle = currentTextStyle.replaceAll("-fx-font-weight: bold;?", "").trim();
        } else {
            currentTextStyle = updateStyle(currentTextStyle, "-fx-font-weight", "bold");
        }
        applyToSelection();
    }

    public void applyItalic() {
        if (currentTextStyle.contains("italic")) {
            currentTextStyle = currentTextStyle.replaceAll("-fx-font-style: italic;?", "").trim();
        } else {
            currentTextStyle = updateStyle(currentTextStyle, "-fx-font-style", "italic");
        }
        applyToSelection();
    }

    public void applyBulletPoint() {
        internalChange = true;
        int caret = contentArea.getCaretPosition();
        String text = contentArea.getText();

        int startOfLine = text.lastIndexOf("\n", caret - 1);
        startOfLine = (startOfLine == -1) ? 0 : startOfLine + 1;

        String bullet = "• ";
        if (!text.startsWith(bullet, startOfLine)) {
            contentArea.replaceText(startOfLine, startOfLine, bullet);
            contentArea.setStyle(startOfLine, startOfLine + bullet.length(), getFinalStyle());
            contentArea.moveTo(startOfLine + bullet.length());
        }
        internalChange = false;
        contentArea.requestFocus();
    }

    private String updateStyle(String currentStyle, String key, String value) {
        if (currentStyle.contains(key)) {
            return currentStyle.replaceAll(key + ": [^;]+;?", key + ": " + value + ";");
        } else {
            return currentStyle.trim() + (currentStyle.endsWith(";") ? "" : ";") + " " + key + ": " + value + ";";
        }
    }

    private void applyToSelection() {
        IndexRange sel = contentArea.getSelection();
        String style = getFinalStyle();
        if (sel.getLength() > 0) {
            contentArea.setStyle(sel.getStart(), sel.getEnd(), style);
        }
        contentArea.requestFocus();
    }

    private void setupSlider() {
        slide.setOrientation(Orientation.HORIZONTAL);
        slide.setShowTickLabels(true);
        slide.setFocusTraversable(false);

        slide.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (isSyncing)
                return;
            currentFontSize = newVal.intValue();
            applyToSelection();
        });
    }

    public void contentAreaListeners() {
        contentArea.selectionProperty().addListener((obs, oldSel, newSel) -> {
            int pos = contentArea.getCaretPosition();
            int samplePos = (newSel.getLength() > 0) ? newSel.getStart() : pos - 1;

            if (samplePos >= 0 && samplePos < contentArea.getLength()) {
                String sampledStyle = contentArea.getStyleAtPosition(samplePos);
                if (sampledStyle != null && !sampledStyle.isEmpty()) {
                    syncStateFromStyle(sampledStyle);
                }
            }
        });

        contentArea.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() != KeyCode.ENTER)
                return;
            int caret = contentArea.getCaretPosition();
            String text = contentArea.getText();
            int startOfLine = text.lastIndexOf("\n", caret - 1);
            startOfLine = (startOfLine == -1) ? 0 : startOfLine + 1;
            String currentLine = text.substring(startOfLine, caret);

            if (!currentLine.stripLeading().startsWith("•"))
                return;

            event.consume();
            if (currentLine.strip().equals("•")) {
                contentArea.deleteText(startOfLine, caret);
                contentArea.insertText(startOfLine, "\n");
                contentArea.moveTo(startOfLine + 1);
            } else {
                String insert = "\n• ";
                contentArea.insertText(caret, insert);
                contentArea.setStyle(caret + 1, caret + insert.length(), getFinalStyle());
                contentArea.moveTo(caret + insert.length());
            }
        });

        contentArea.richChanges().subscribe(change -> {
            if (internalChange || change.getInserted().length() != 1) {
                if (onContentChanged != null)
                    onContentChanged.run();
                return;
            }
            int start = change.getPosition();
            internalChange = true;
            Platform.runLater(() -> {
                if (start + 1 <= contentArea.getLength()) {
                    contentArea.setStyle(start, start + 1, getFinalStyle());
                }
                internalChange = false;
                if (onContentChanged != null)
                    onContentChanged.run();
            });
        });
    }

    private void syncStateFromStyle(String style) {
        if (style.contains("-fx-font-size:")) {
            try {
                String sizeStr = style.split("-fx-font-size:")[1].split("pt")[0].trim();
                currentFontSize = Integer.parseInt(sizeStr);
                isSyncing = true;
                slide.setValue(currentFontSize);
                isSyncing = false;
            } catch (Exception e) {
                isSyncing = false;
            }
        }
        currentTextStyle = style.replaceAll("-fx-font-size: \\d+pt;?", "").trim();
    }

    public void applySlider() {
        if (isSliderVisible) {
            sliderContainer.getChildren().clear();
            isSliderVisible = false;
        } else {
            if (!sliderContainer.getChildren().contains(slide)) {
                sliderContainer.getChildren().add(slide);
            }
            isSliderVisible = true;
        }
        contentArea.requestFocus();
    }
}