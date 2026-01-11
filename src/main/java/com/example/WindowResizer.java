package com.example;

import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;

import javafx.scene.layout.Region;

import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class WindowResizer {
    private Stage stage;
    private final Region root;
    private final Region titleBar;
    private final double minW, minH;

    private double startX, startY, startStageX, startStageY, startWidth, startHeight, dragOffsetX, dragOffsetY;
    private Cursor resizeCursor = Cursor.DEFAULT;
    private Stage ghostStage;
    private static final double MARGIN = 10;

    private boolean isNorth, isSouth, isEast, isWest;

    public static void makeResizable(Region root, Region titleBar, double minW, double minH) {
        WindowResizer resizer = new WindowResizer(root, titleBar, minW, minH);
        
        if (root.getScene() != null && root.getScene().getWindow() != null) {
            resizer.stage = (Stage) root.getScene().getWindow();
            resizer.init();
        } else {
            root.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {
                    newScene.windowProperty().addListener((obsW, oldW, newW) -> {
                        if (newW != null) {
                            resizer.stage = (Stage) newW;
                            resizer.init();
                        }
                    });
                }
            });
        }
    }

    private WindowResizer(Region root, Region titleBar, double minW, double minH) {
        this.root = root;
        this.titleBar = titleBar;
        this.minW = minW;
        this.minH = minH;
    }

    private void init() {
        root.setOnMouseMoved(e -> {
            if (stage.isMaximized()) {
                root.setCursor(Cursor.DEFAULT);
                return;
            }
            double x = e.getX(), y = e.getY(), w = root.getWidth(), h = root.getHeight();
            Cursor c = Cursor.DEFAULT;

            if (x < MARGIN && y < MARGIN) c = Cursor.NW_RESIZE;
            else if (x > w - MARGIN && y < MARGIN) c = Cursor.NE_RESIZE;
            else if (x < MARGIN && y > h - MARGIN) c = Cursor.SW_RESIZE;
            else if (x > w - MARGIN && y > h - MARGIN) c = Cursor.SE_RESIZE;
            else if (x < MARGIN) c = Cursor.W_RESIZE;
            else if (x > w - MARGIN) c = Cursor.E_RESIZE;
            else if (y < MARGIN) c = Cursor.N_RESIZE;
            else if (y > h - MARGIN) c = Cursor.S_RESIZE;
            
            root.setCursor(c);
        });

        root.setOnMousePressed(e -> {
            resizeCursor = root.getCursor();
            isNorth = isSouth = isEast = isWest = false;

          
            if (resizeCursor == Cursor.N_RESIZE) { isNorth = true; }
            else if (resizeCursor == Cursor.S_RESIZE) { isSouth = true; }
            else if (resizeCursor == Cursor.E_RESIZE) { isEast = true; }
            else if (resizeCursor == Cursor.W_RESIZE) { isWest = true; }
            else if (resizeCursor == Cursor.NW_RESIZE) { isNorth = true; isWest = true; }
            else if (resizeCursor == Cursor.NE_RESIZE) { isNorth = true; isEast = true; }
            else if (resizeCursor == Cursor.SW_RESIZE) { isSouth = true; isWest = true; }
            else if (resizeCursor == Cursor.SE_RESIZE) { isSouth = true; isEast = true; }

            startX = e.getScreenX(); 
            startY = e.getScreenY();
            startStageX = stage.getX(); 
            startStageY = stage.getY();
            startWidth = stage.getWidth(); 
            startHeight = stage.getHeight();
        });

        root.setOnMouseDragged(e -> {
            if (resizeCursor == Cursor.DEFAULT || stage.isMaximized()) return;

            double dx = e.getScreenX() - startX;
            double dy = e.getScreenY() - startY;
            if (isWest) {
                double nW = Math.max(minW, startWidth - dx);
                stage.setX(startStageX + (startWidth - nW));
                stage.setWidth(nW);
            } else if (isEast) {
                stage.setWidth(Math.max(minW, startWidth + dx));
            }

            if (isNorth) {
                double nH = Math.max(minH, startHeight - dy);
                stage.setY(startStageY + (startHeight - nH));
                stage.setHeight(nH);
            } else if (isSouth) {
                stage.setHeight(Math.max(minH, startHeight + dy));
            }
        });

        titleBar.setOnMousePressed(e -> {
            if (root.getCursor() == Cursor.DEFAULT) {
                if (stage.isMaximized()) {
                    stage.setMaximized(false);
                    dragOffsetX = stage.getWidth() / 2;
                } else {
                    dragOffsetX = e.getSceneX();
                }
                dragOffsetY = e.getSceneY();
            }
        });

        titleBar.setOnMouseDragged(e -> {
            if (root.getCursor() == Cursor.DEFAULT) {
                stage.setX(e.getScreenX() - dragOffsetX);
                stage.setY(e.getScreenY() - dragOffsetY);
                checkSnap(e.getScreenX(), e.getScreenY());
            }
        });

        titleBar.setOnMouseReleased(e -> {
            if (ghostStage != null && ghostStage.isShowing()) {
                stage.setX(ghostStage.getX());
                stage.setY(ghostStage.getY());
                stage.setWidth(ghostStage.getWidth());
                stage.setHeight(ghostStage.getHeight());
                ghostStage.hide();
            }

            isNorth = isSouth = isEast = isWest = false;
        });
    }

 private void checkSnap(double sX, double sY) {
    ObservableList<Screen> screens = Screen.getScreensForRectangle(sX, sY, 1, 1);
    if (screens.isEmpty()) return;
    
    Screen currentScreen = screens.get(0);
    Rectangle2D bounds = currentScreen.getVisualBounds();
    if (ghostStage == null) createGhost();


    double edgeThreshold = 15; 
    double cornerThreshold = 40;

    boolean nearTop = (sY - bounds.getMinY()) < edgeThreshold;
    boolean nearBottom = (bounds.getMaxY() - sY) < edgeThreshold;
    boolean nearLeft = (sX - bounds.getMinX()) < edgeThreshold;
    boolean nearRight = (bounds.getMaxX() - sX) < edgeThreshold;

    if (nearTop && (sX - bounds.getMinX() < cornerThreshold)) {
        showGhost(bounds.getMinX(), bounds.getMinY(), bounds.getWidth() / 2, bounds.getHeight() / 2);
    } else if (nearTop && (bounds.getMaxX() - sX < cornerThreshold)) {
        showGhost(bounds.getMinX() + bounds.getWidth() / 2, bounds.getMinY(), bounds.getWidth() / 2, bounds.getHeight() / 2);
    } else if (nearBottom && (sX - bounds.getMinX() < cornerThreshold)) {
        showGhost(bounds.getMinX(), bounds.getMinY() + bounds.getHeight() / 2, bounds.getWidth() / 2, bounds.getHeight() / 2);
    } else if (nearBottom && (bounds.getMaxX() - sX < cornerThreshold)) {
        showGhost(bounds.getMinX() + bounds.getWidth() / 2, bounds.getMinY() + bounds.getHeight() / 2, bounds.getWidth() / 2, bounds.getHeight() / 2);
    } 
   
    else if (nearTop) {
        showGhost(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight());
    } else if (nearLeft) {
        showGhost(bounds.getMinX(), bounds.getMinY(), bounds.getWidth() / 2, bounds.getHeight());
    } else if (nearRight) {
        showGhost(bounds.getMinX() + bounds.getWidth() / 2, bounds.getMinY(), bounds.getWidth() / 2, bounds.getHeight());
    } else {
        ghostStage.hide();
    }
}

private void createGhost() {
    ghostStage = new Stage(StageStyle.TRANSPARENT);
    ghostStage.initOwner(stage);

    VBox v = new VBox();
    v.setPrefSize(300, 180);

    v.setStyle(
        "-fx-background-color: rgba(0, 120, 215, 0.12); " + 
        "-fx-border-color: rgba(0, 120, 215, 0.6); " +
        "-fx-border-width: 2; " +
        "-fx-background-radius: 8; " +
        "-fx-border-radius: 8;"
    );

    Scene scene = new Scene(v);
    scene.setFill(Color.TRANSPARENT);
    ghostStage.setScene(scene);
}

private void showGhost(double x, double y, double w, double h) {
    ghostStage.setX(x + 4);
    ghostStage.setY(y + 4);
    ghostStage.setWidth(w - 8);
    ghostStage.setHeight(h - 8);
    if (!ghostStage.isShowing()) ghostStage.show();
}}