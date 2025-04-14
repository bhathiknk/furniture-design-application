package com.myfurniture.designapp;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Translate;

public class Furniture3DFactory {

    public static Group createFurniture3D(FurnitureItem item) {
        Group group = new Group();

        // Fixed vertical thickness of furniture
        double boxHeight = 15; // Y-axis (height up from floor)

        // Use actual 2D dimensions as 3D width and depth
        double boxWidth = item.getWidth();  // X-axis
        double boxDepth = item.getHeight(); // Z-axis

        // Create material for main furniture block
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(item.getPrimaryColor());

        // Create main box for the furniture
        Box box = new Box(boxWidth, boxHeight, boxDepth);
        box.setMaterial(material);

        // Position the box based on 2D (x, y) mapping
        double tx = item.getX() + boxWidth / 2.0;  // Center it in X
        double tz = item.getY() + boxDepth / 2.0;  // Center it in Z (2D Y maps to 3D Z)
        double ty = boxHeight / 2.0;               // Rest on floor (Y = height/2)
        box.getTransforms().add(new Translate(tx, ty, tz));
        group.getChildren().add(box);

        // Add backrest if chair
        if (item.getType().equalsIgnoreCase("chair")) {
            Group backrest = createChairBackrest(boxWidth, boxHeight, boxDepth, item.getSecondaryColor());
            // Position backrest behind the seat in Z-axis
            backrest.getTransforms().add(new Translate(tx, boxHeight + 15, tz - boxDepth / 2 + 2.5));
            group.getChildren().add(backrest);
        }

        return group;
    }

    private static Group createChairBackrest(double chairWidth, double seatHeight, double chairDepth, Color secondaryColor) {
        Group group = new Group();

        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(secondaryColor);

        double backrestHeight = seatHeight * 2;
        double backrestThickness = 5;

        Box backrest = new Box(chairWidth, backrestHeight, backrestThickness);
        backrest.setMaterial(material);

        // Centered on X and raised to match seat + backrest height
        backrest.getTransforms().add(new Translate(0, backrestHeight / 2.0, 0));
        group.getChildren().add(backrest);
        return group;
    }
}
