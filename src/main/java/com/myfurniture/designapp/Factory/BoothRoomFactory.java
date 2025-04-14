package com.myfurniture.designapp.Factory;

import com.myfurniture.designapp.Core.RoomDesign;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Translate;

public class BoothRoomFactory {

    public static Group createBooth(RoomDesign room) {
        Group group = new Group();

        double width = room.getRoomWidth();
        double depth = room.getRoomHeight();
        double wallHeight = 150;
        double wallThickness = 10;

        // Material for walls (user-selected room color)
        PhongMaterial wallMaterial = new PhongMaterial(room.getRoomColor());

        // Material for floor (solid grey)
        PhongMaterial floorMaterial = new PhongMaterial(Color.LIGHTGRAY);

        // --- FLOOR ---
        MeshView floor = createPlainFloor(width, depth, floorMaterial);
        group.getChildren().add(floor);

        // --- BACK WALL ---
        Box backWall = new Box(width, wallHeight, wallThickness);
        backWall.setMaterial(wallMaterial);
        backWall.getTransforms().add(new Translate(width / 2.0, wallHeight / 2.0, 0));
        group.getChildren().add(backWall);

        // --- LEFT WALL ---
        Box leftWall = new Box(wallThickness, wallHeight, depth);
        leftWall.setMaterial(wallMaterial);
        leftWall.getTransforms().add(new Translate(0, wallHeight / 2.0, depth / 2.0));
        group.getChildren().add(leftWall);

        // --- RIGHT WALL ---
        Box rightWall = new Box(wallThickness, wallHeight, depth);
        rightWall.setMaterial(wallMaterial);
        rightWall.getTransforms().add(new Translate(width, wallHeight / 2.0, depth / 2.0));
        group.getChildren().add(rightWall);

        return group;
    }

    private static MeshView createPlainFloor(double width, double depth, PhongMaterial material) {
        TriangleMesh mesh = new TriangleMesh();

        float w = (float) width;
        float d = (float) depth;

        // 4 corners of the floor (flat rectangle)
        mesh.getPoints().addAll(
                0, 0, 0,   // 0
                w, 0, 0,   // 1
                w, 0, d,   // 2
                0, 0, d    // 3
        );

        // Dummy texture coordinates
        mesh.getTexCoords().addAll(0, 0);

        // Two triangles facing up (outside top face)
        mesh.getFaces().addAll(
                0, 0, 1, 0, 2, 0,
                0, 0, 2, 0, 3, 0
        );

        // Two triangles facing down (inside bottom face)
        mesh.getFaces().addAll(
                2, 0, 1, 0, 0, 0,
                3, 0, 2, 0, 0, 0
        );

        MeshView floor = new MeshView(mesh);
        floor.setMaterial(material);
        floor.setTranslateY(0);
        return floor;
    }


}
