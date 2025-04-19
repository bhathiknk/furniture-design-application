// src/main/java/com/myfurniture/designapp/UI/RoomRenderer3D.java
package com.myfurniture.designapp.UI;

import com.myfurniture.designapp.Core.DesignManager;
import com.myfurniture.designapp.Core.FurnitureItem;
import com.myfurniture.designapp.Core.RoomDesign;
import com.myfurniture.designapp.Factory.BoothRoomFactory;
import com.myfurniture.designapp.Factory.Furniture3DFactory;
import javafx.geometry.Bounds;
import javafx.scene.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

/**
 * Renders the current {@link RoomDesign} in 3‑D.
 * Room dimensions (width/depth) drive the shape, so selecting “Square”
 * in 2D (which forces roomHeight == roomWidth) automatically shows a cube.
 */
public class RoomRenderer3D extends StackPane {

    private final DesignManager designManager;
    private final Group contentGroup = new Group();
    private final Group pivotGroup   = new Group(contentGroup);
    private final Group root3D       = new Group(pivotGroup);

    private final Rotate rotateX = new Rotate(-30, Rotate.X_AXIS);
    private final Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);

    private final PerspectiveCamera camera = new PerspectiveCamera(true);
    private double cameraDistance = 1400;
    private static final double MIN_DIST = 500;
    private static final double MAX_DIST = 5000;

    private double anchorX, anchorY;
    private double anchorAngleX, anchorAngleY;
    private static final double FIT_W = 600;
    private static final double FIT_D = 400;

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

        camera.setNearClip(0.1);
        camera.setFarClip(10000);
        subScene.setCamera(camera);

        getChildren().add(subScene);

        setOnMousePressed(this::onMousePressed);
        setOnMouseDragged(this::onMouseDragged);
        addEventHandler(ScrollEvent.SCROLL, this::onScroll);

        widthProperty().addListener((o, __, ___) -> rebuild());
        heightProperty().addListener((o, __, ___) -> rebuild());

        rebuild();
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

    private static double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }

    private void rebuild() {
        contentGroup.getChildren().clear();

        RoomDesign room = designManager.getCurrentDesign();
        if (room == null) return;

        // Booth floor + walls
        contentGroup.getChildren().add(BoothRoomFactory.createBooth(room));

        // Furniture
        for (FurnitureItem item : room.getFurniture()) {
            contentGroup.getChildren().add(Furniture3DFactory.createFurniture3D(item));
        }

        // Scale to fit
        double sX = FIT_W / room.getRoomWidth();
        double sZ = FIT_D / room.getRoomHeight();
        double scaleFactor = Math.min(sX, sZ);

        contentGroup.getTransforms().setAll(
                new Scale(scaleFactor, scaleFactor, scaleFactor),
                new Rotate(180, Rotate.X_AXIS)  // flip so floor faces down
        );

        // Center
        Bounds b = contentGroup.getBoundsInParent();
        double cX = (b.getMinX() + b.getMaxX()) / 2;
        double cY = (b.getMinY() + b.getMaxY()) / 2;
        double cZ = (b.getMinZ() + b.getMaxZ()) / 2;
        contentGroup.getTransforms().add(new Translate(-cX, -cY, -cZ));

        updateCameraPosition();

        // Ambient light once
        if (root3D.getChildren().stream().noneMatch(n -> n instanceof AmbientLight)) {
            root3D.getChildren().add(new AmbientLight(Color.WHITE));
        }
    }

    private void updateCameraPosition() {
        camera.setTranslateZ(-cameraDistance);
        camera.setTranslateY(-100);
    }

    public void updateScene() {
        rotateX.setAngle(-30);
        rotateY.setAngle(0);
        rebuild();
    }
}
