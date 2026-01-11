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

public class noteCreationController {

    private final String notePath = "src/main/java/com/example/Notes/";
    
    // Switch to RichTextFX component
    private InlineCssTextArea contentArea = new InlineCssTextArea();
    
    @FXML private TextField titleField;
    @FXML private HBox titleBar;
    @FXML private VBox mainRoot;
    @FXML private VBox editorContainer; 
    @FXML private VBox slider;

    private String noteName = "Untitled";
    private Slider slide = new Slider(10, 50, 14);
    private boolean isSliderVisible = false;
    private String currentTextStyle = "-fx-font-family: 'Segoe UI'; -fx-font-size: 14pt; -fx-fill: white;";

    @FXML
    public void initialize() {
        setupRichEditor();
        setupSliderLogic();

        Platform.runLater(() -> {
            if (mainRoot != null) WindowResizer.makeResizable(mainRoot, titleBar, 400, 500);
            titleField.requestFocus();
        });

        titleField.textProperty().addListener((obs, oldV, newV) -> {
            if (newV != null && !newV.trim().isEmpty()) {
                noteName = newV.trim();
      
            }
        });

contentArea.richChanges()
    .filter(ch -> !ch.isIdentity())
    .subscribe(change ->{

    saveContent();

    if (change.getInserted().length() == 1 && change.getRemoved().length() == 0) {
        int start = change.getPosition();
        int end = start + 1;
        Platform.runLater(() -> 
            contentArea.setStyle(start, end, currentTextStyle)
        );
    }
});
    }

    private void setupRichEditor() {
        VirtualizedScrollPane<InlineCssTextArea> vsPane = new VirtualizedScrollPane<>(contentArea);
        VBox.setVgrow(vsPane, Priority.ALWAYS);
        editorContainer.getChildren().add(vsPane);
        contentArea.setFocusTraversable(true);
        // Initial style for new notes change incase
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

    private void saveContent() {
        File file = new File(notePath, noteName);
        
        // Define the Codec for Rich Text
        Codec<StyledDocument<String, String, String>> codec =
            ReadOnlyStyledDocument.codec(
                Codec.STRING_CODEC,
                Codec.styledTextCodec(Codec.STRING_CODEC),
                SegmentOps.styledTextOps()
            );

        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(file))) {
            //Encodes
            codec.encode(dos, contentArea.getDocument());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBold() {
        if (currentTextStyle.contains("-fx-font-weight: bold")) {
            currentTextStyle = currentTextStyle.replace("-fx-font-weight: bold;", "").trim();
        } else {
            currentTextStyle += " -fx-font-weight: bold;";
        }
        
        IndexRange selection = contentArea.getSelection();
        if (selection.getLength() > 0) {
            contentArea.setStyle(selection.getStart(), selection.getEnd(), currentTextStyle);
        }
        contentArea.requestFocus();
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

    @FXML private void closeWindow(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML public void apearOnTop(ActionEvent event) {
        ApearOnTop.apearOnTop(event);
    }

    @FXML private void minimizeWindow(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML private void switchToNoteList() throws IOException {
        App.setRoot("noteList");
    }
}