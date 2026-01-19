package com.example;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.Node;

import javafx.scene.control.ScrollPane;

import javafx.scene.control.TextField;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.InlineCssTextArea;
import org.fxmisc.richtext.model.Codec;
import org.fxmisc.richtext.model.ReadOnlyStyledDocument;
import org.fxmisc.richtext.model.SegmentOps;
import org.fxmisc.richtext.model.StyledDocument;

public class noteViewController {

    private final static String notePath = "src/main/java/com/example/Notes/";
    private static InlineCssTextArea contentArea = new InlineCssTextArea();

    @FXML
    private TextField titleField;
    @FXML
    private VBox editorContainer;
    @FXML
    private HBox titleBar;
    @FXML
    private VBox mainRoot;
    @FXML
    private VBox slider;
    public static String selectedNoteName;
    private static String currentFileName;
    private String currentTextStyle = "-fx-font-family: 'Segoe UI'; -fx-font-size: 12pt; -fx-fill: white;";

    private boolean internalChange = false;
    private richTextStyler textStyler;

    @FXML
    public void initialize() {
        textStyler = new richTextStyler(contentArea, slider, noteViewController::saveContent);
        setupRichEditor();

        if (selectedNoteName != null) {
            findAndLoad(selectedNoteName);
        }

        Platform.runLater(() -> {
            if (mainRoot != null) {
                WindowResizer.makeResizable(mainRoot, titleBar, 400, 500);
            }
            contentArea.requestFocus();
        });

        titleField.focusedProperty().addListener((obs, was, is) -> {
            if (!is && currentFileName != null) {
                renameNoteFile(titleField.getText());
            }
        });

    }

    private void setupRichEditor() {
        VirtualizedScrollPane<InlineCssTextArea> vsPane = new VirtualizedScrollPane<>(contentArea);

        contentArea.setWrapText(true);
        vsPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        vsPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        VBox.setVgrow(vsPane, Priority.ALWAYS);
        editorContainer.getChildren().add(vsPane);

        contentArea.setFocusTraversable(true);
        contentArea.setStyle(currentTextStyle);
    }

    public void findAndLoad(String noteToFind) {
        File file = new File(notePath, noteToFind);
        currentFileName = noteToFind;
        titleField.setText(noteToFind);

        if (file.exists() && file.length() > 0) {
            try (java.io.DataInputStream dis = new java.io.DataInputStream(new java.io.FileInputStream(file))) {

                Codec<StyledDocument<String, String, String>> codec = ReadOnlyStyledDocument.codec(
                        Codec.STRING_CODEC,
                        Codec.styledTextCodec(Codec.STRING_CODEC),
                        SegmentOps.styledTextOps());

                StyledDocument<String, String, String> doc = codec.decode(dis);

                internalChange = true;
                contentArea.replace(0, contentArea.getLength(), doc);
                internalChange = false;

            } catch (IOException e) {
                contentArea.replaceText("");
            }
        } else {
            contentArea.replaceText("");
        }

        Platform.runLater(() -> contentArea.requestFocus());
    }

    private static void saveContent() {

        File file = new File(notePath, currentFileName);

        Codec<StyledDocument<String, String, String>> codec = ReadOnlyStyledDocument.codec(
                Codec.STRING_CODEC,
                Codec.styledTextCodec(Codec.STRING_CODEC),
                SegmentOps.styledTextOps());

        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(file))) {
            codec.encode(dos, contentArea.getDocument());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void renameNoteFile(String newTitle) {
        if (newTitle == null || newTitle.isBlank() ||
                newTitle.equals(currentFileName))
            return;

        File oldFile = new File(notePath, currentFileName);
        File newFile = new File(notePath, newTitle);

        if (oldFile.renameTo(newFile)) {
            currentFileName = newTitle;
            selectedNoteName = newTitle;
        }
    }

    @FXML
    public void handleBold(ActionEvent event) {
        textStyler.applyBold();
        saveContent();
    }

    @FXML
    public void handleSlider(ActionEvent event) {
        textStyler.applySlider();
        saveContent();
    }

    @FXML
    public void handleBulletPoint(ActionEvent event) {
        textStyler.applyBulletPoint();
        saveContent();
    }

    @FXML
    public void handleItalic(ActionEvent event) {
        textStyler.applyItalic();
        saveContent();
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
    public void apearOnTop(ActionEvent event) {
        ApearOnTop.apearOnTop(event);
    }

    @FXML
    private void switchToNoteList(ActionEvent event) throws IOException {
        App.setRoot("noteList");
    }
}
