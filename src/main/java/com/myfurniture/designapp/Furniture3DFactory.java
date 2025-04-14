package com.myfurniture.designapp;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Translate;

public class Furniture3DFactory {

    public static Group createFurniture3D(FurnitureItem item) {
        Group group = new Group();
        // Use a fixed thickness for all furniture (vertical dimension in 3D)
        double thickness = 15;

        // Use the 2D dimensions directly:
        double boxWidth = item.getWidth();     // X-axis dimension
        double boxDepth = item.getHeight();      // Z-axis dimension (from 2D y-value)
        double boxHeight = thickness;            // Y-axis (vertical) thickness

        // Create a material using the primary color.
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(item.getPrimaryColor());

        // Create a box representing the furniture's main body.
        Box box = new Box(boxWidth, boxHeight, boxDepth);
        box.setMaterial(material);

        // Position the box so that:
        // – Its X,Z position comes from the 2D (x,y) position.
        // – We shift by half the box dimensions so that the item is centered.
        double tx = item.getX() + boxWidth / 2.0;
        double tz = item.getY() + boxDepth / 2.0;
        double ty = boxHeight / 2.0;
        box.getTransforms().add(new Translate(tx, ty, tz));
        group.getChildren().add(box);

        // If the furniture is a chair, add a backrest.
        if(item.getType().equalsIgnoreCase("chair")) {
            Group backrest = createChairBackrest(boxWidth, boxHeight, boxDepth, item.getSecondaryColor());
            group.getChildren().add(backrest);
        }

        return group;
    }

    // Creates a simple backrest for chairs.
    private static Group createChairBackrest(double chairWidth, double chairHeight, double chairDepth, Color secondaryColor) {
        Group group = new Group();
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(secondaryColor);

        // We'll make the backrest span the full width,
        // be somewhat taller than the seat (e.g. 2× the seat thickness),
        // and have a small depth (thickness).
        double backrestHeight = chairHeight * 2;
        double backrestThickness = 5;
        Box backrest = new Box(chairWidth, backrestHeight, backrestThickness);
        backrest.setMaterial(material);

        // Position the backrest so that it stands behind the seat.
        // The seat is centered at (chairWidth/2, chairHeight/2, chairDepth/2).
        // We translate the backrest so that its front face (the side with minimal depth) abuts the seat.
        backrest.getTransforms().add(new Translate(chairWidth / 2.0, backrestHeight / 2.0, -backrestThickness / 2.0));
        group.getChildren().add(backrest);
        return group;
    }
}
