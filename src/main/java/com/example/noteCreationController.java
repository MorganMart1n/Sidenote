package com.example;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
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
import javafx.scene.control.ScrollPane;

public class noteCreationController {

    private final static String notePath = "src/main/java/com/example/Notes/";

    // Switch to RichTextFX component
    private static InlineCssTextArea contentArea = new InlineCssTextArea();

    @FXML
    private TextField titleField;
    @FXML
    private HBox titleBar;
    @FXML
    private VBox mainRoot;
    @FXML
    private VBox editorContainer;
    @FXML
    private VBox inside;

    @FXML
    private VBox slider;

    private static String noteName = "Untitled";

    private richTextStyler textStyler;

    private Boolean isInitalised = false;

    @FXML
    public void initialize() {
   
        textStyler = new richTextStyler(contentArea, slider, noteCreationController::saveContent);
        setupRichEditor();

        Platform.runLater(() -> {
            if (mainRoot != null)
                WindowResizer.makeResizable(mainRoot, titleBar, 400, 500);
                isInitalised = true;
            titleField.requestFocus();
        });
        titleField.textProperty().addListener((obs, oldV, newV) -> {
            if (newV != null && !newV.trim().isEmpty()) {
                noteName = newV.trim();
            }
        });
     
    }

    private void setupRichEditor() {
    VirtualizedScrollPane<InlineCssTextArea> vsPane = new VirtualizedScrollPane<>(contentArea);
    contentArea.setWrapText(true);

    // seamless style
    contentArea.setStyle(
        "-fx-background-color: transparent; " +
        "-fx-text-fill: white; " +
        "-fx-font-family: 'Segoe UI'; " +
        "-fx-font-size: 14pt;"
    );

    vsPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    vsPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

    VBox.setVgrow(vsPane, Priority.ALWAYS);
    editorContainer.getChildren().add(vsPane);
    contentArea.setFocusTraversable(true);
}

    public static void saveContent() {
        //Maybe change due to exact string refs not being good practice
        if (noteName != "Untitled" && contentArea.getText() != null) {
        
        File file = new File(notePath, noteName);
        // Define the Codec for Rich Text
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
    }

    // Integrated Custom titlebar Logic
    @FXML
    private void closeWindow(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    public void apearOnTop(ActionEvent event) {
        ApearOnTop.apearOnTop(event);
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
    private void minimizeWindow(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void switchToNoteList() throws IOException {
        titleField.clear();
        contentArea.clear();
        App.setRoot("noteList");
    }
}