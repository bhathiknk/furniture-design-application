package com.myfurniture.designapp;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Translate;

public class Furniture3DFactory {

    public static Group createFurniture3D(FurnitureItem item) {
        switch (item.getType().toLowerCase()) {
            case "chair":
                return createChair(item);
            case "table":
                return createTable(item);
            case "bed":
                return createBed(item);
            case "sofa":
                return createSofa(item);
            case "bookshelf":
                return createBookshelf(item);
            default:
                return new Group(); // Unknown type
        }
    }

    private static Group createChair(FurnitureItem item) {
        Group group = new Group();
        double x = item.getX();
        double y = item.getY();
        double w = item.getWidth();
        double d = item.getHeight();
        double seatHeight = 15;
        double legHeight = 30;
        double backHeight = 50;

        PhongMaterial seatMaterial = new PhongMaterial(item.getPrimaryColor());
        PhongMaterial legMaterial = new PhongMaterial(Color.DARKGRAY);
        PhongMaterial backMaterial = new PhongMaterial(item.getSecondaryColor());

        // Seat
        Box seat = new Box(w, seatHeight, d);
        seat.setMaterial(seatMaterial);
        seat.getTransforms().add(new Translate(x + w / 2.0, legHeight + seatHeight / 2.0, y + d / 2.0));
        group.getChildren().add(seat);

        // Backrest
        Box back = new Box(w, backHeight, 5);
        back.setMaterial(backMaterial);
        back.getTransforms().add(new Translate(x + w / 2.0, legHeight + seatHeight + backHeight / 2.0, y + 2.5));
        group.getChildren().add(back);

        // Legs (4 corners)
        double legSize = 5;
        double[][] legOffsets = {
                {0, 0}, {w - legSize, 0}, {0, d - legSize}, {w - legSize, d - legSize}
        };

        for (double[] offset : legOffsets) {
            Box leg = new Box(legSize, legHeight, legSize);
            leg.setMaterial(legMaterial);
            leg.getTransforms().add(new Translate(x + offset[0] + legSize / 2.0, legHeight / 2.0, y + offset[1] + legSize / 2.0));
            group.getChildren().add(leg);
        }

        return group;
    }

    private static Group createTable(FurnitureItem item) {
        Group group = new Group();
        double x = item.getX();
        double y = item.getY();
        double w = item.getWidth();
        double d = item.getHeight();
        double topThickness = 10;
        double legHeight = 40;

        PhongMaterial topMaterial = new PhongMaterial(item.getPrimaryColor());
        PhongMaterial legMaterial = new PhongMaterial(item.getSecondaryColor());

        // Tabletop
        Box top = new Box(w, topThickness, d);
        top.setMaterial(topMaterial);
        top.getTransforms().add(new Translate(x + w / 2.0, legHeight + topThickness / 2.0, y + d / 2.0));
        group.getChildren().add(top);

        // Legs
        double legSize = 5;
        double[][] legOffsets = {
                {0, 0}, {w - legSize, 0}, {0, d - legSize}, {w - legSize, d - legSize}
        };

        for (double[] offset : legOffsets) {
            Box leg = new Box(legSize, legHeight, legSize);
            leg.setMaterial(legMaterial);
            leg.getTransforms().add(new Translate(x + offset[0] + legSize / 2.0, legHeight / 2.0, y + offset[1] + legSize / 2.0));
            group.getChildren().add(leg);
        }

        return group;
    }

    private static Group createBed(FurnitureItem item) {
        Group group = new Group();
        double x = item.getX();
        double y = item.getY();
        double w = item.getWidth();
        double d = item.getHeight();
        double baseHeight = 20;
        double headboardHeight = 50;

        PhongMaterial baseMaterial = new PhongMaterial(item.getPrimaryColor());
        PhongMaterial headboardMaterial = new PhongMaterial(item.getSecondaryColor());

        // Bed base
        Box base = new Box(w, baseHeight, d);
        base.setMaterial(baseMaterial);
        base.getTransforms().add(new Translate(x + w / 2.0, baseHeight / 2.0, y + d / 2.0));
        group.getChildren().add(base);

        // Headboard
        Box headboard = new Box(w, headboardHeight, 5);
        headboard.setMaterial(headboardMaterial);
        headboard.getTransforms().add(new Translate(x + w / 2.0, headboardHeight / 2.0 + baseHeight, y + 2.5));
        group.getChildren().add(headboard);

        return group;
    }

    private static Group createSofa(FurnitureItem item) {
        Group group = new Group();
        double x = item.getX();
        double y = item.getY();
        double w = item.getWidth();
        double d = item.getHeight();
        double baseHeight = 20;
        double backHeight = 30;
        double armWidth = 10;

        PhongMaterial baseMaterial = new PhongMaterial(item.getPrimaryColor());
        PhongMaterial detailMaterial = new PhongMaterial(item.getSecondaryColor());

        // Seat base
        Box base = new Box(w - 2 * armWidth, baseHeight, d);
        base.setMaterial(baseMaterial);
        base.getTransforms().add(new Translate(x + w / 2.0, baseHeight / 2.0, y + d / 2.0));
        group.getChildren().add(base);

        // Backrest
        Box back = new Box(w - 2 * armWidth, backHeight, 5);
        back.setMaterial(detailMaterial);
        back.getTransforms().add(new Translate(x + w / 2.0, baseHeight + backHeight / 2.0, y + 2.5));
        group.getChildren().add(back);

        // Left and Right Arms
        Box leftArm = new Box(armWidth, baseHeight + backHeight, 5);
        leftArm.setMaterial(detailMaterial);
        leftArm.getTransforms().add(new Translate(x + armWidth / 2.0, (baseHeight + backHeight) / 2.0, y + d / 2.0));
        group.getChildren().add(leftArm);

        Box rightArm = new Box(armWidth, baseHeight + backHeight, 5);
        rightArm.setMaterial(detailMaterial);
        rightArm.getTransforms().add(new Translate(x + w - armWidth / 2.0, (baseHeight + backHeight) / 2.0, y + d / 2.0));
        group.getChildren().add(rightArm);

        return group;
    }

    private static Group createBookshelf(FurnitureItem item) {
        Group group = new Group();
        double x = item.getX();
        double y = item.getY();
        double w = item.getWidth();
        double h = item.getHeight();
        double depth = 20;
        double shelfThickness = 5;
        int shelfCount = 4;

        PhongMaterial shelfMaterial = new PhongMaterial(item.getPrimaryColor());

        // Side panels
        Box left = new Box(shelfThickness, h, depth);
        left.setMaterial(shelfMaterial);
        left.getTransforms().add(new Translate(x + shelfThickness / 2.0, h / 2.0, y + depth / 2.0));
        group.getChildren().add(left);

        Box right = new Box(shelfThickness, h, depth);
        right.setMaterial(shelfMaterial);
        right.getTransforms().add(new Translate(x + w - shelfThickness / 2.0, h / 2.0, y + depth / 2.0));
        group.getChildren().add(right);

        // Back panel
        Box back = new Box(w, h, shelfThickness);
        back.setMaterial(new PhongMaterial(Color.DARKGRAY));
        back.getTransforms().add(new Translate(x + w / 2.0, h / 2.0, y + shelfThickness / 2.0));
        group.getChildren().add(back);

        // Shelves
        for (int i = 0; i <= shelfCount; i++) {
            double shelfY = i * (h / shelfCount);
            Box shelf = new Box(w - 2 * shelfThickness, shelfThickness, depth);
            shelf.setMaterial(shelfMaterial);
            shelf.getTransforms().add(new Translate(x + w / 2.0, shelfY + shelfThickness / 2.0, y + depth / 2.0));
            group.getChildren().add(shelf);
        }

        return group;
    }
}
