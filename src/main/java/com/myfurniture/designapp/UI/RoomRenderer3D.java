package com.myfurniture.designapp.UI;

import com.myfurniture.designapp.Core.DesignManager;
import com.myfurniture.designapp.Core.FurnitureItem;
import com.myfurniture.designapp.Core.RoomDesign;
import com.myfurniture.designapp.Factory.BoothRoomFactory;
import com.myfurniture.designapp.Factory.Furniture3DFactory;
import javafx.animation.*;
import javafx.geometry.Bounds;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

public class RoomRenderer3D extends StackPane {

    private final DesignManager designManager;
    private final Group contentGroup = new Group();
    private final Group pivotGroup = new Group(contentGroup);
    private final Group root3D = new Group(pivotGroup);

    private final Rotate rotateX = new Rotate(-25, Rotate.X_AXIS);
    private final Rotate rotateY = new Rotate(180, Rotate.Y_AXIS);

    private final PerspectiveCamera camera = new PerspectiveCamera(true);
    private double cameraDistance = 1400;
    private static final double MIN_DIST = 500;
    private static final double MAX_DIST = 5000;

    private double anchorX, anchorY;
    private double anchorAngleX, anchorAngleY;
    private static final double FIT_W = 700;
    private static final double FIT_D = 500;

    private boolean isLightMode = true;

    public RoomRenderer3D(DesignManager designManager) {
        this.designManager = designManager;
        init3D();
    }

    private void init3D() {
        pivotGroup.getTransforms().addAll(rotateX, rotateY);

        SubScene subScene = new SubScene(root3D, 0, 0, true, SceneAntialiasing.BALANCED);
        subScene.widthProperty().bind(widthProperty());
        subScene.heightProperty().bind(heightProperty());
        subScene.setFill(Color.WHITE);
        subScene.setCamera(camera);

        getChildren().add(subScene);

        camera.setFieldOfView(35);
        camera.setNearClip(0.1);
        camera.setFarClip(10000);

        setOnMousePressed(this::onMousePressed);
        setOnMouseDragged(this::onMouseDragged);
        addEventHandler(ScrollEvent.SCROLL, this::onScroll);

        widthProperty().addListener((o, __, ___) -> rebuild());
        heightProperty().addListener((o, __, ___) -> rebuild());

        rebuild();
        showUserHint("💡 Drag to rotate, scroll to zoom");
        addOverlayButtons();
    }

    private void rebuild() {
        contentGroup.getChildren().clear();

        RoomDesign room = designManager.getCurrentDesign();
        if (room == null) return;

        contentGroup.getChildren().add(BoothRoomFactory.createBooth(room));
        for (FurnitureItem item : room.getFurniture()) {
            contentGroup.getChildren().add(Furniture3DFactory.createFurniture3D(item));
        }

        // Auto-scale to fit view
        double sX = FIT_W / room.getRoomWidth();
        double sZ = FIT_D / room.getRoomHeight();
        double scaleFactor = Math.min(sX, sZ);

        // Flip vertically so floor appears at bottom (Y-axis scale: -1)
        contentGroup.getTransforms().setAll(
                new Scale(scaleFactor, -scaleFactor, scaleFactor)  // flipped Y
        );

        // Center content
        Bounds bounds = contentGroup.getBoundsInParent();
        double cX = (bounds.getMinX() + bounds.getMaxX()) / 2.0;
        double cY = (bounds.getMinY() + bounds.getMaxY()) / 2.0;
        double cZ = (bounds.getMinZ() + bounds.getMaxZ()) / 2.0;
        contentGroup.getTransforms().add(new Translate(-cX, -cY, -cZ));

        updateCameraDistance(bounds);
        updateCameraPosition();
        setupLighting();
    }

    private void setupLighting() {
        root3D.getChildren().removeIf(n -> n instanceof LightBase);

        AmbientLight ambientLight = new AmbientLight(isLightMode ? Color.WHITE : Color.DARKGRAY);
        PointLight pointLight = new PointLight(isLightMode ? Color.WHITE : Color.GRAY);
        pointLight.setTranslateX(-300);
        pointLight.setTranslateY(-250);
        pointLight.setTranslateZ(-400);

        root3D.getChildren().addAll(ambientLight, pointLight);
    }

    private void updateCameraDistance(Bounds bounds) {
        double maxDim = Math.max(bounds.getWidth(), Math.max(bounds.getHeight(), bounds.getDepth()));
        cameraDistance = maxDim * 1.8;
    }

    private void updateCameraPosition() {
        camera.setTranslateZ(-cameraDistance);
        camera.setTranslateY(-100);
    }

    private void onMousePressed(MouseEvent e) {
        anchorX = e.getSceneX();
        anchorY = e.getSceneY();
        anchorAngleX = rotateX.getAngle();
        anchorAngleY = rotateY.getAngle();
    }

    private void onMouseDragged(MouseEvent e) {
        double dx = e.getSceneX() - anchorX;
        double dy = e.getSceneY() - anchorY;
        rotateY.setAngle(anchorAngleY + dx * 0.5);
        rotateX.setAngle(clamp(anchorAngleX - dy * 0.5, -85, 85));
    }

    private void onScroll(ScrollEvent e) {
        cameraDistance = clamp(cameraDistance - e.getDeltaY(), MIN_DIST, MAX_DIST);
        updateCameraPosition();
    }

    private static double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }

    public void updateScene() {
        rotateX.setAngle(-25);
        rotateY.setAngle(180);
        rebuild();
    }

    private void addOverlayButtons() {
        Button btnReset = overlayButton("🔄 Reset View");
        Button btnZoomFit = overlayButton("🔍 Zoom to Fit");
        Button btnLight = overlayButton("💡 Toggle Light");

        btnReset.setOnAction(e -> {
            rotateX.setAngle(-25);
            rotateY.setAngle(180);
            updateCameraPosition();
        });

        btnZoomFit.setOnAction(e -> rebuild());
        btnLight.setOnAction(e -> {
            isLightMode = !isLightMode;
            setupLighting();
        });

        VBox box = new VBox(8, btnReset, btnZoomFit, btnLight);
        box.setStyle("-fx-padding: 10;");
        box.setTranslateX(10);
        box.setTranslateY(10);
        getChildren().add(box);
        StackPane.setAlignment(box, javafx.geometry.Pos.TOP_LEFT);
    }

    private Button overlayButton(String label) {
        Button b = new Button(label);
        b.setFont(Font.font(13));
        b.setStyle("""
            -fx-background-color: #3498db;
            -fx-text-fill: white;
            -fx-background-radius: 8;
            -fx-padding: 6 12;
        """);
        return b;
    }

    private void showUserHint(String message) {
        Label hint = new Label(message);
        hint.setStyle("""
            -fx-background-color: #333333dd;
            -fx-text-fill: white;
            -fx-padding: 6px 12px;
            -fx-background-radius: 10;
        """);
        hint.setFont(Font.font(13));
        getChildren().add(hint);
        StackPane.setAlignment(hint, javafx.geometry.Pos.BOTTOM_CENTER);
        Timeline fade = new Timeline(
                new KeyFrame(Duration.seconds(4), new KeyValue(hint.opacityProperty(), 0)));
        fade.setOnFinished(e -> getChildren().remove(hint));
        fade.play();
    }
}
