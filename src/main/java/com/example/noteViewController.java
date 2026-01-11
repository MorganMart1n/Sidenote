package com.example;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import java.io.IOException;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import org.fxmisc.richtext.InlineCssTextArea;
import org.fxmisc.richtext.model.Codec;
import org.fxmisc.richtext.model.ReadOnlyStyledDocument;
import org.fxmisc.richtext.model.SegmentOps;
import org.fxmisc.richtext.model.StyledDocument;
import org.fxmisc.flowless.VirtualizedScrollPane;
import javafx.scene.control.IndexRange;

public class noteViewController {

    private final String notePath = "src/main/java/com/example/Notes/";
    private InlineCssTextArea contentArea = new InlineCssTextArea();
    
    @FXML private TextField titleField;  
    @FXML private VBox editorContainer; 
    @FXML private HBox titleBar;
    @FXML private VBox mainRoot;
    @FXML private VBox slider;

    public static String selectedNoteName;
    private String currentFileName;
    
    private Slider slide = new Slider(10, 50, 14); 
    private boolean isSliderVisible = false;
    private String currentTextStyle = "-fx-font-family: 'Segoe UI'; -fx-font-size: 14pt; -fx-fill: white;";

    @FXML
    public void initialize() {
        //Initalise UI Components
        setupRichEditor();
        setupSliderLogic(); 

        //Check for null
        if (selectedNoteName != null) {
            findAndLoad(selectedNoteName);
        }
        //Make window
        Platform.runLater(() -> {
            if (mainRoot != null) {
                WindowResizer.makeResizable(mainRoot, titleBar, 400, 500);
            }
            contentArea.requestFocus();
        });

        contentArea.richChanges().subscribe(change -> {
            if (currentFileName != null) {
                saveContent();
            }
            
            if (change.getInserted().length() == 1) {
                int start = change.getPosition();
                int end = start + 1;
                Platform.runLater(() -> {
                    contentArea.setStyle(start, end, currentTextStyle);
                });
            }
        });

        contentArea.setOnMouseClicked(event -> {
            int pos = contentArea.getCaretPosition();
            if (pos > 0) {
                String sampledStyle = contentArea.getStyleAtPosition(pos - 1);
                if (sampledStyle != null && !sampledStyle.isEmpty()) {
                    currentTextStyle = sampledStyle;
                    syncSliderToStyle(currentTextStyle);
                }
            }
        });

        titleField.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (!isFocused && currentFileName != null) {
                renameNoteFile(titleField.getText());
            }
        });
    }
    

    private void syncSliderToStyle(String style) {
        if (style.contains("-fx-font-size:")) {
            try {
                String sizeStr = style.split("-fx-font-size: ")[1].split("pt")[0].trim();
                slide.setValue(Double.parseDouble(sizeStr));
            } catch (Exception e) {}
        }
    }

    private void setupRichEditor() {
        VirtualizedScrollPane<InlineCssTextArea> vsPane = new VirtualizedScrollPane<>(contentArea);
        VBox.setVgrow(vsPane, Priority.ALWAYS);
        editorContainer.getChildren().add(vsPane);
        contentArea.setFocusTraversable(true);
        contentArea.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14pt; -fx-fill: white;");
    }

    private void setupSliderLogic() {
        slide.setOrientation(Orientation.HORIZONTAL);
        slide.setShowTickLabels(true);
        slide.setFocusTraversable(false);

        slide.valueProperty().addListener((obs, oldVal, newVal) -> {
            String newSize = "-fx-font-size: " + newVal.intValue() + "pt;";
            currentTextStyle = currentTextStyle.replaceAll("-fx-font-size: \\d+pt;", "").trim() + " " + newSize;

            IndexRange selection = contentArea.getSelection();
            if (selection.getLength() > 0) {
                contentArea.setStyle(selection.getStart(), selection.getEnd(), currentTextStyle);
            }
            contentArea.requestFocus();
        });
    }

    @FXML
    public void handleSlider(ActionEvent event) {
        if (isSliderVisible) {
            slider.getChildren().clear();
            isSliderVisible = false;
        } else {
            if (!slider.getChildren().contains(slide)) {
                slider.getChildren().add(slide);
            }
            isSliderVisible = true;
        }
        contentArea.requestFocus();
    }

    @FXML
    private void handleBold() {
        if (currentTextStyle.contains("-fx-font-weight: bold")) {
            currentTextStyle = currentTextStyle.replace("-fx-font-weight: bold;", "").trim();
        } else {
            currentTextStyle += " -fx-font-weight: bold;";
        }
        //selection index i.e highlight
        IndexRange selection = contentArea.getSelection();
        if (selection.getLength() > 0) {
            contentArea.setStyle(selection.getStart(), selection.getEnd(), currentTextStyle);
            saveContent();
        }
        contentArea.requestFocus();
    }

    public void findAndLoad(String noteToFind) {
    File file = new File(notePath, noteToFind);
    if (file.exists() && file.length() > 0) {
        currentFileName = noteToFind;

        try (java.io.DataInputStream dis = new java.io.DataInputStream(new java.io.FileInputStream(file))) {
            Codec<StyledDocument<String, String, String>> codec = ReadOnlyStyledDocument.codec(
                Codec.STRING_CODEC,
                Codec.styledTextCodec(Codec.STRING_CODEC),
                SegmentOps.styledTextOps()
            );
            //Decode
            StyledDocument<String, String, String> doc = codec.decode(dis);
            titleField.setText(noteToFind);

       
            contentArea.replace(0, contentArea.getLength(), doc);

        
            if (contentArea.getLength() > 0) {
                currentTextStyle = contentArea.getStyleAtPosition(0);
                syncSliderToStyle(currentTextStyle);
            }
        } catch (IOException e) {
            System.err.println("Error decoding rich text " + e.getMessage());
            contentArea.replaceText("");
        }
    }else{
        titleField.setText(noteToFind);
        contentArea.replaceText("");
    }
}
    //Save explicitly with codec for stlying using DataStream
    private void saveContent() {
        if (currentFileName == null) return; 
            File file = new File(notePath, currentFileName);

        Codec<StyledDocument<String, String, String>>codec =
         ReadOnlyStyledDocument.codec(
            Codec.STRING_CODEC,
            Codec.styledTextCodec(Codec.STRING_CODEC),
            SegmentOps.styledTextOps()
        );

        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(file))){
            codec.encode(dos, contentArea.getDocument());
        } catch (IOException e) {
           e.printStackTrace();
        }
    }
    //Renaming for title
    private void renameNoteFile(String newTitle) {
        if (newTitle == null || newTitle.trim().isEmpty() || newTitle.equals(currentFileName)) return;
        File oldFile = new File(notePath, currentFileName);
        File newFile = new File(notePath, newTitle);
        if (oldFile.renameTo(newFile)) {
            currentFileName = newTitle;
            selectedNoteName = newTitle;
        }
    }

    @FXML
    private void closeWindow(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    private void minimizeWindow(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
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