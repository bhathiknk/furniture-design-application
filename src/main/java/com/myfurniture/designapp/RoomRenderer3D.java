package com.myfurniture.designapp;

import javafx.scene.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

public class RoomRenderer3D extends StackPane {
    private final DesignManager designManager;
    private SubScene subScene;
    private Group root3D;
    private PerspectiveCamera camera;

    private double anchorX, anchorY;
    private double anchorAngleX = 0;
    private double anchorAngleY = 0;
    private final Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
    private final Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);

    private final double displayWidth = 600;
    private final double displayDepth = 400;

    public RoomRenderer3D(DesignManager designManager) {
        this.designManager = designManager;
        init3D();
    }

    private void init3D() {
        root3D = new Group();
        root3D.getTransforms().addAll(rotateX, rotateY);
        buildScene();

        subScene = new SubScene(root3D, 0, 0, true, SceneAntialiasing.BALANCED);
        subScene.setFill(Color.WHITE);
        subScene.widthProperty().bind(this.widthProperty());
        subScene.heightProperty().bind(this.heightProperty());

        camera = new PerspectiveCamera(true);
        camera.setNearClip(1);
        camera.setFarClip(5000);
        camera.getTransforms().addAll(
                new Translate(0, 0, -1000) // pull camera back
        );
        subScene.setCamera(camera);

        getChildren().add(subScene);
        addMouseControl();
    }

    private void buildScene() {
        root3D.getChildren().clear();
        RoomDesign room = designManager.getCurrentDesign();

        if (room != null) {
            Group contentGroup = new Group();

            // Create booth-style room (walls and floor)
            Group boothWalls = BoothRoomFactory.createBooth(room);
            contentGroup.getChildren().add(boothWalls);

            // Add all furniture
            for (FurnitureItem item : room.getFurniture()) {
                contentGroup.getChildren().add(Furniture3DFactory.createFurniture3D(item));
            }

            double roomWidth = room.getRoomWidth();
            double roomDepth = room.getRoomHeight();

            double scaleX = displayWidth / roomWidth;
            double scaleZ = displayDepth / roomDepth;
            double uniformScale = Math.min(scaleX, scaleZ);

            // Apply uniform scale with Y-axis flipped
            contentGroup.getTransforms().add(new Scale(uniformScale, -uniformScale, uniformScale));

            // Center the room based on width and depth
            double offsetX = -roomWidth / 2.0;
            double offsetZ = -roomDepth / 2.0;

            // Adjust Y to keep it visually centered after flipping
            double offsetY = 100; // Raise the scene upward after flip

            contentGroup.getTransforms().add(new Translate(offsetX, offsetY, offsetZ));

            root3D.getChildren().add(contentGroup);
        }

        // Add ambient light
        AmbientLight ambient = new AmbientLight(Color.color(1, 1, 1));
        root3D.getChildren().add(ambient);
    }

    private void addMouseControl() {
        this.setOnMousePressed((MouseEvent e) -> {
            anchorX = e.getSceneX();
            anchorY = e.getSceneY();
            anchorAngleX = rotateX.getAngle();
            anchorAngleY = rotateY.getAngle();
        });

        this.setOnMouseDragged((MouseEvent e) -> {
            rotateX.setAngle(anchorAngleX - (e.getSceneY() - anchorY) * 0.5);
            rotateY.setAngle(anchorAngleY + (e.getSceneX() - anchorX) * 0.5);
        });
    }

    public void updateScene() {
        buildScene();
    }
}
