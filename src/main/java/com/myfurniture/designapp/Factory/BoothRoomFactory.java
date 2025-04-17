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

        double width       = room.getRoomWidth();
        double depth       = room.getRoomHeight();
        double wallHeight  = 150;
        double wallThickness = 10;

        // per‐wall materials
        PhongMaterial backWallMat  = new PhongMaterial(room.getBackWallColor());
        PhongMaterial leftWallMat  = new PhongMaterial(room.getLeftWallColor());
        PhongMaterial rightWallMat = new PhongMaterial(room.getRightWallColor());
        PhongMaterial floorMat     = new PhongMaterial(Color.LIGHTGRAY);

        // FLOOR
        MeshView floor = createPlainFloor(width, depth, floorMat);
        group.getChildren().add(floor);

        // BACK WALL
        Box backWall = new Box(width, wallHeight, wallThickness);
        backWall.setMaterial(backWallMat);
        backWall.getTransforms().add(new Translate(width/2.0, wallHeight/2.0, 0));
        group.getChildren().add(backWall);

        // LEFT WALL
        Box leftWall = new Box(wallThickness, wallHeight, depth);
        leftWall.setMaterial(leftWallMat);
        leftWall.getTransforms().add(new Translate(0, wallHeight/2.0, depth/2.0));
        group.getChildren().add(leftWall);

        // RIGHT WALL
        Box rightWall = new Box(wallThickness, wallHeight, depth);
        rightWall.setMaterial(rightWallMat);
        rightWall.getTransforms().add(new Translate(width, wallHeight/2.0, depth/2.0));
        group.getChildren().add(rightWall);

        return group;
    }

    private static MeshView createPlainFloor(double width, double depth, PhongMaterial mat) {
        TriangleMesh mesh = new TriangleMesh();
        float w = (float) width, d = (float) depth;
        mesh.getPoints().addAll(
                0,0,0, w,0,0, w,0,d, 0,0,d
        );
        mesh.getTexCoords().addAll(0,0);
        mesh.getFaces().addAll(
                0,0, 1,0, 2,0,
                0,0, 2,0, 3,0,
                2,0, 1,0, 0,0,
                3,0, 2,0, 0,0
        );
        MeshView floor = new MeshView(mesh);
        floor.setMaterial(mat);
        floor.setTranslateY(0);
        return floor;
    }
}
