package com.myfurniture.designapp;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Translate;

public class Room3DFactory {
    public static Group createRoom(RoomDesign room) {
        Group group = new Group();

        // Use the 2D room dimensions directly:
        double width = room.getRoomWidth();   // X-axis size
        double depth = room.getRoomHeight();    // Z-axis size

        // Define thickness and wall height (you may adjust these values)
        double floorThickness = 10;
        double wallHeight = 300;
        double wallThickness = 10;

        // Create the floor with a material based on the room color.
        PhongMaterial floorMat = new PhongMaterial(room.getRoomColor().darker());
        Box floor = new Box(width, floorThickness, depth);
        floor.setMaterial(floorMat);
        // Position the floor so that its top is at y = 0.
        floor.getTransforms().add(new Translate(width / 2.0, floorThickness / 2.0, depth / 2.0));
        group.getChildren().add(floor);

        // Create the back wall.
        PhongMaterial backWallMat = new PhongMaterial(room.getRoomColor().brighter());
        Box backWall = new Box(width, wallHeight, wallThickness);
        backWall.setMaterial(backWallMat);
        backWall.getTransforms().add(new Translate(width / 2.0, wallHeight / 2.0, -wallThickness / 2.0));
        group.getChildren().add(backWall);

        // Create the left wall.
        PhongMaterial leftWallMat = new PhongMaterial(Color.DARKGRAY);
        Box leftWall = new Box(wallThickness, wallHeight, depth);
        leftWall.setMaterial(leftWallMat);
        leftWall.getTransforms().add(new Translate(-wallThickness / 2.0, wallHeight / 2.0, depth / 2.0));
        group.getChildren().add(leftWall);

        // Create the right wall.
        PhongMaterial rightWallMat = new PhongMaterial(Color.LIGHTGRAY);
        Box rightWall = new Box(wallThickness, wallHeight, depth);
        rightWall.setMaterial(rightWallMat);
        rightWall.getTransforms().add(new Translate(width + wallThickness / 2.0, wallHeight / 2.0, depth / 2.0));
        group.getChildren().add(rightWall);

        // (Front remains open to allow an unobstructed view into the room.)
        return group;
    }
}
