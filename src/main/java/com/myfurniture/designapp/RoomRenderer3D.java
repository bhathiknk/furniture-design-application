package com.myfurniture.designapp;

import javafx.beans.binding.Bindings;
import javafx.scene.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class RoomRenderer3D extends StackPane {
    private DesignManager designManager;
    private SubScene subScene;
    private Group root3D;
    private PerspectiveCamera camera;
    private double anchorX, anchorY;
    private double anchorAngleX = 0;
    private double anchorAngleY = 0;
    private Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
    private Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);

    public RoomRenderer3D(DesignManager designManager) {
        this.designManager = designManager;
        // No need to set a fixed preferred size if you want full-screen behavior.
        init3D();
    }

    private void init3D() {
        root3D = new Group();
        root3D.getTransforms().addAll(rotateX, rotateY);
        buildScene();

        // Create SubScene with initial sizes of 0 and bind later.
        subScene = new SubScene(root3D, 0, 0, true, SceneAntialiasing.BALANCED);
        subScene.setFill(Color.WHITE);

        camera = new PerspectiveCamera(true);
        // Position the camera based on the room dimensions.
        RoomDesign room = designManager.getCurrentDesign();
        if (room != null) {
            double roomWidth = room.getRoomWidth();
            double roomDepth = room.getRoomHeight();
            // Place the camera roughly in front of the room.
            camera.getTransforms().add(new Translate(roomWidth / 2.0, -200, -roomDepth));
        }
        camera.setNearClip(1);
        camera.setFarClip(5000);
        subScene.setCamera(camera);

        // Bind subScene width and height to this container.
        subScene.widthProperty().bind(this.widthProperty());
        subScene.heightProperty().bind(this.heightProperty());

        getChildren().add(subScene);
        addMouseControl();
    }

    // Called whenever the 2D design updates.
    public void updateScene() {
        buildScene();
    }

    private void buildScene() {
        root3D.getChildren().clear();
        RoomDesign room = designManager.getCurrentDesign();
        if (room != null) {
            // Build and add the 3D room.
            Group room3D = Room3DFactory.createRoom(room);
            root3D.getChildren().add(room3D);
            // Add each piece of furniture.
            for (FurnitureItem item : room.getFurniture()) {
                root3D.getChildren().add(Furniture3DFactory.createFurniture3D(item));
            }
        }
        AmbientLight ambient = new AmbientLight(Color.WHITE);
        root3D.getChildren().add(ambient);
    }

    // Mouse-drag to rotate the scene.
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
}
