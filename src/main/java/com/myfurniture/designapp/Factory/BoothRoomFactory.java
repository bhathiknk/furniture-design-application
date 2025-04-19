package com.myfurniture.designapp.Factory;

import com.myfurniture.designapp.Core.RoomDesign;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
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
        double wallHeight = 300;
        double wallThickness = 10;

        // Wall materials
        PhongMaterial backWallMat = new PhongMaterial(room.getBackWallColor());
        PhongMaterial leftWallMat = new PhongMaterial(room.getLeftWallColor());
        PhongMaterial rightWallMat = new PhongMaterial(room.getRightWallColor());
        PhongMaterial floorMat = createFloorMaterial();

        // Floor
        MeshView floor = createPlainFloor(width, depth, floorMat);
        group.getChildren().add(floor);

        // Back wall
        Box backWall = new Box(width, wallHeight, wallThickness);
        backWall.setMaterial(backWallMat);
        backWall.getTransforms().add(new Translate(width / 2.0, wallHeight / 2.0, 0));
        group.getChildren().add(backWall);

        // Left wall (flipped)
        Box leftWall = new Box(wallThickness, wallHeight, depth);
        leftWall.setMaterial(leftWallMat);
        leftWall.getTransforms().add(new Translate(width, wallHeight / 2.0, depth / 2.0));
        group.getChildren().add(leftWall);

        // Right wall (flipped)
        Box rightWall = new Box(wallThickness, wallHeight, depth);
        rightWall.setMaterial(rightWallMat);
        rightWall.getTransforms().add(new Translate(0, wallHeight / 2.0, depth / 2.0));
        group.getChildren().add(rightWall);

        return group;
    }

    private static MeshView createPlainFloor(double width, double depth, PhongMaterial mat) {
        TriangleMesh mesh = new TriangleMesh();
        float w = (float) width;
        float d = (float) depth;

        // Points
        mesh.getPoints().addAll(
                0, 0, 0,
                w, 0, 0,
                w, 0, d,
                0, 0, d
        );

        // Texture coordinates
        mesh.getTexCoords().addAll(0, 0, 1, 0, 1, 1, 0, 1);

        // Faces - both front and back so it's double-sided
        mesh.getFaces().addAll(
                0, 0, 1, 1, 2, 2,
                0, 0, 2, 2, 3, 3,

                // back face
                2, 2, 1, 1, 0, 0,
                3, 3, 2, 2, 0, 0
        );

        MeshView floor = new MeshView(mesh);
        floor.setMaterial(mat);
        floor.setTranslateY(0);
        return floor;
    }

    private static PhongMaterial createFloorMaterial() {
        int size = 128;
        Canvas canvas = new Canvas(size, size);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Base floor color
        gc.setFill(Color.GRAY);
        gc.fillRect(0, 0, size, size);

        // Grid lines (subtle)
        gc.setStroke(Color.rgb(200, 200, 200, 0.3));
        for (int i = 0; i <= size; i += 16) {
            gc.strokeLine(i, 0, i, size); // vertical lines
            gc.strokeLine(0, i, size, i); // horizontal lines
        }

        WritableImage texture = canvas.snapshot(null, null);
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseMap(texture);
        material.setSpecularColor(Color.WHITE);       // light reflections
        material.setSpecularPower(32);
        return material;
    }
}
