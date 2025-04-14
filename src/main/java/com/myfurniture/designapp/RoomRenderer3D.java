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
        camera.getTransforms().add(new Translate(0, 0, -800)); // Move back for a full view
        subScene.setCamera(camera);

        getChildren().add(subScene);
        addMouseControl(); // Optional: for rotating the view
    }

    private void buildScene() {
        root3D.getChildren().clear();
        RoomDesign room = designManager.getCurrentDesign();
        if (room != null) {
            Group contentGroup = new Group();

            // Create booth-style room (includes 3 walls and the floor)
            Group boothWalls = BoothRoomFactory.createBooth(room);
            contentGroup.getChildren().add(boothWalls);

            // Add furniture
            for (FurnitureItem item : room.getFurniture()) {
                contentGroup.getChildren().add(Furniture3DFactory.createFurniture3D(item));
            }

            double roomWidth = room.getRoomWidth();
            double roomDepth = room.getRoomHeight();

            double scaleX = displayWidth / roomWidth;
            double scaleZ = displayDepth / roomDepth;
            double uniformScale = Math.min(scaleX, scaleZ);

            // Flip vertically (upside down)
            contentGroup.getTransforms().addAll(
                    new Scale(uniformScale, -uniformScale, uniformScale), // flip Y-axis
                    new Translate(-roomWidth * uniformScale / 2.0, 75, -roomDepth * uniformScale / 2.0)
            );

            root3D.getChildren().add(contentGroup);
        }

        AmbientLight ambient = new AmbientLight(Color.WHITE);
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
