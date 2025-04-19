package com.myfurniture.designapp.Factory;

import com.myfurniture.designapp.Core.FurnitureItem;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class Furniture3DFactory {

    public static Group createFurniture3D(FurnitureItem item) {
        Group group;
        switch (item.getType().toLowerCase()) {
            case "chair":        group = createChair(item, new Group()); break;
            case "table":        group = createTable(item, new Group()); break;
            case "bed":          group = createBed(item, new Group()); break;
            case "sofa":         group = createSofa(item, new Group()); break;
            case "bookshelf":    group = createBookshelf(item, new Group()); break;
            case "wardrobe":     group = createWardrobe(item, new Group()); break;
            case "dining table": group = createDiningTable(item, new Group()); break;
            case "lamp":         group = createLamp(item, new Group()); break;
            case "tv stand":     group = createTVStand(item, new Group()); break;
            case "coffee table": group = createCoffeeTable(item, new Group()); break;
            default:             group = new Group(); break;
        }

        // Corrected: match 2D exactly without negating
        double pivotX = item.getX() + item.getWidth() / 2.0;
        double pivotZ = item.getY() + item.getHeight() / 2.0;

        group.getTransforms().add(
                new Rotate(
                        item.getRotation(),             // ✅ Corrected: no negative
                        pivotX, 0, pivotZ,
                        new Point3D(0, 1, 0)
                )
        );

        return group;
    }


    private static PhongMaterial woodMaterial() {
        Canvas canvas = new Canvas(64, 64);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BURLYWOOD);
        gc.fillRect(0, 0, 64, 64);
        gc.setStroke(Color.SADDLEBROWN);
        for (int i = 0; i < 64; i += 8) gc.strokeLine(i, 0, i, 64);
        WritableImage img = canvas.snapshot(null, null);
        PhongMaterial mat = new PhongMaterial();
        mat.setDiffuseMap(img);
        mat.setSpecularColor(Color.LIGHTGRAY);
        mat.setSpecularPower(64);
        return mat;
    }

    private static PhongMaterial smoothMaterial(Color color) {
        PhongMaterial mat = new PhongMaterial(color);
        mat.setSpecularColor(Color.WHITE);
        mat.setSpecularPower(128);
        return mat;
    }

    private static Group createChair(FurnitureItem item, Group group) {
        double x = item.getX(), y = item.getY(),
                w = item.getWidth(), d = item.getHeight();
        double legH = 40, seatH = 6, backH = 30;
        PhongMaterial seatMat = smoothMaterial(item.getPrimaryColor());
        PhongMaterial legMat  = woodMaterial();
        PhongMaterial backMat = smoothMaterial(item.getSecondaryColor());
        // seat
        Box seat = new Box(w - 6, seatH, d - 6);
        seat.setMaterial(seatMat);
        seat.getTransforms().add(new Translate(x + w/2, legH + seatH/2, y + d/2));
        group.getChildren().add(seat);
        // legs
        double[][] legs = {{3,3},{w-6,3},{3,d-6},{w-6,d-6}};
        for (double[] p : legs) {
            Cylinder leg = new Cylinder(2, legH);
            leg.setMaterial(legMat);
            leg.getTransforms().add(new Translate(x + p[0]+2, legH/2, y + p[1]+2));
            group.getChildren().add(leg);
        }
        // backrest
        Box back = new Box(w - 6, backH, 2);
        back.setMaterial(backMat);
        back.getTransforms().add(new Translate(x + w/2, legH + seatH + backH/2, y + 4));
        group.getChildren().add(back);
        return group;
    }

    private static Group createTable(FurnitureItem item, Group group) {
        double x = item.getX(), y = item.getY(),
                w = item.getWidth(), d = item.getHeight();
        double topH = 6, legH = 48;
        PhongMaterial topMat = smoothMaterial(item.getPrimaryColor());
        PhongMaterial legMat = woodMaterial();
        Box top = new Box(w, topH, d);
        top.setMaterial(topMat);
        top.getTransforms().add(new Translate(x + w/2, legH + topH/2, y + d/2));
        group.getChildren().add(top);
        double[][] offs = {{4,4},{w-4,4},{4,d-4},{w-4,d-4}};
        for (double[] p : offs) {
            Cylinder leg = new Cylinder(3, legH);
            leg.setMaterial(legMat);
            leg.getTransforms().add(new Translate(x + p[0], legH/2, y + p[1]));
            group.getChildren().add(leg);
        }
        return group;
    }

    private static Group createBed(FurnitureItem item, Group group) {
        double x = item.getX(), y = item.getY(), w = item.getWidth(), d = item.getHeight();
        double baseH = 10, mattressH = 10, pillowH = 4, legH = 15;

        PhongMaterial baseMat = woodMaterial();
        PhongMaterial mattressMat = smoothMaterial(item.getPrimaryColor());
        PhongMaterial pillowMat = smoothMaterial(item.getSecondaryColor());
        PhongMaterial legMat = woodMaterial();

        // Mattress dimensions
        double mattressExtraWidth = 5;
        double mattressExtraDepth = 150;
        double mattressW = w + mattressExtraWidth;
        double mattressD = d + mattressExtraDepth;

        // Corrected mattress center Z
        double mattressZ = y + mattressD / 2;

        // Mattress
        Box mattress = new Box(mattressW, mattressH, mattressD);
        mattress.setMaterial(mattressMat);
        mattress.getTransforms().add(new Translate(x + w / 2, baseH + mattressH / 2, mattressZ));
        group.getChildren().add(mattress);

        // Pillows (placed near head of the new deeper mattress)
        Box pillow1 = new Box(w / 5, pillowH, 10);
        pillow1.setMaterial(pillowMat);
        pillow1.getTransforms().add(new Translate(x + w / 3, baseH + mattressH + pillowH / 2, y + 12));

        Box pillow2 = new Box(w / 5, pillowH, 10);
        pillow2.setMaterial(pillowMat);
        pillow2.getTransforms().add(new Translate(x + 2 * w / 3, baseH + mattressH + pillowH / 2, y + 12));

        group.getChildren().addAll(pillow1, pillow2);

        // Legs – fixed at corners of mattress, not original frame
        double legOffset = 6;
        double[][] legPositions = {
                {x + legOffset, y + legOffset}, // front-left
                {x + w - legOffset, y + legOffset}, // front-right
                {x + legOffset, y + mattressD - legOffset}, // back-left
                {x + w - legOffset, y + mattressD - legOffset} // back-right
        };

        for (double[] pos : legPositions) {
            Cylinder leg = new Cylinder(3, legH);
            leg.setMaterial(legMat);
            leg.getTransforms().add(new Translate(pos[0], legH / 2, pos[1]));
            group.getChildren().add(leg);
        }

        return group;
    }





    private static Group createSofa(FurnitureItem item, Group group) {
        double x = item.getX(), y = item.getY(), w = item.getWidth(), d = item.getHeight();
        double baseH = 14, cushionH = 10, backH = 20, armH = 20;

        PhongMaterial baseMat = woodMaterial();
        PhongMaterial cushionMat = smoothMaterial(item.getPrimaryColor());
        PhongMaterial armMat = smoothMaterial(item.getSecondaryColor());

        // Base
        Box base = new Box(w, baseH, d);
        base.setMaterial(baseMat);
        base.getTransforms().add(new Translate(x + w / 2, baseH / 2, y + d / 2));
        group.getChildren().add(base);

        // Cushion
        Box cushion = new Box(w - 20, cushionH, d - 10);
        cushion.setMaterial(cushionMat);
        cushion.getTransforms().add(new Translate(x + w / 2, baseH + cushionH / 2, y + d / 2));
        group.getChildren().add(cushion);

        // Backrest
        Box back = new Box(w - 20, backH, 4);
        back.setMaterial(cushionMat);
        back.getTransforms().add(new Translate(x + w / 2, baseH + cushionH + backH / 2, y + 4));
        group.getChildren().add(back);

        // Armrests
        Box leftArm = new Box(8, armH, 6);
        leftArm.setMaterial(armMat);
        leftArm.getTransforms().add(new Translate(x + 4, baseH + armH / 2, y + d / 2));
        Box rightArm = new Box(8, armH, 6);
        rightArm.setMaterial(armMat);
        rightArm.getTransforms().add(new Translate(x + w - 4, baseH + armH / 2, y + d / 2));
        group.getChildren().addAll(leftArm, rightArm);

        return group;
    }

    private static Group createBookshelf(FurnitureItem item, Group group) {
        double x = item.getX(), y = item.getY(), w = item.getWidth(), h = item.getHeight(), d = 18;
        int shelves = 5;

        PhongMaterial woodMat = woodMaterial();
        PhongMaterial backMat = smoothMaterial(Color.rgb(60, 60, 60));

        // Left + right walls
        Box left = new Box(5, h, d);
        left.setMaterial(woodMat);
        left.getTransforms().add(new Translate(x + 2.5, h / 2, y + d / 2));
        Box right = new Box(5, h, d);
        right.setMaterial(woodMat);
        right.getTransforms().add(new Translate(x + w - 2.5, h / 2, y + d / 2));
        // Back panel
        Box back = new Box(w, h, 2);
        back.setMaterial(backMat);
        back.getTransforms().add(new Translate(x + w / 2, h / 2, y + 1));
        group.getChildren().addAll(left, right, back);

        // Shelves
        for (int i = 0; i <= shelves; i++) {
            Box shelf = new Box(w - 10, 3, d);
            shelf.setMaterial(woodMat);
            shelf.getTransforms().add(new Translate(x + w / 2, 10 + i * (h / (shelves + 1)), y + d / 2));
            group.getChildren().add(shelf);
        }

        return group;
    }

    private static Group createWardrobe(FurnitureItem item, Group group) {
        double x = item.getX(), y = item.getY(), w = item.getWidth(), h = item.getHeight(), d = 25;
        PhongMaterial mat = woodMaterial();
        Box body = new Box(w, h, d);
        body.setMaterial(mat);
        body.getTransforms().add(new Translate(x + w / 2, h / 2, y + d / 2));
        group.getChildren().add(body);
        return group;
    }

    private static Group createDiningTable(FurnitureItem item, Group group) {
        double x = item.getX(), y = item.getY(), w = item.getWidth(), d = item.getHeight();
        double topH = 6, legH = 50;
        PhongMaterial topMat = smoothMaterial(item.getPrimaryColor());
        PhongMaterial legMat = woodMaterial();
        Box top = new Box(w, topH, d);
        top.setMaterial(topMat);
        top.getTransforms().add(new Translate(x + w / 2, legH + topH / 2, y + d / 2));
        group.getChildren().add(top);
        for (double[] pos : new double[][]{{4, 4}, {w - 4, 4}, {4, d - 4}, {w - 4, d - 4}}) {
            Cylinder leg = new Cylinder(3, legH);
            leg.setMaterial(legMat);
            leg.getTransforms().add(new Translate(x + pos[0], legH / 2, y + pos[1]));
            group.getChildren().add(leg);
        }
        return group;
    }

    private static Group createLamp(FurnitureItem item, Group group) {
        double x = item.getX(), y = item.getY(), w = item.getWidth(), h = item.getHeight();
        PhongMaterial baseMat = smoothMaterial(item.getSecondaryColor());
        PhongMaterial headMat = smoothMaterial(item.getPrimaryColor());
        Cylinder stand = new Cylinder(2, h - 20);
        stand.setMaterial(baseMat);
        stand.getTransforms().add(new Translate(x + w / 2, (h - 20) / 2, y + w / 2));
        Box head = new Box(w + 10, 10, w + 10);
        head.setMaterial(headMat);
        head.getTransforms().add(new Translate(x + w / 2, h - 5, y + w / 2));
        group.getChildren().addAll(stand, head);
        return group;
    }

    private static Group createTVStand(FurnitureItem item, Group group) {
        double x = item.getX(), y = item.getY(), w = item.getWidth(), d = item.getHeight(), h = 30;
        PhongMaterial mat = woodMaterial();
        Box body = new Box(w, h, d);
        body.setMaterial(mat);
        body.getTransforms().add(new Translate(x + w / 2, h / 2, y + d / 2));
        group.getChildren().add(body);
        return group;
    }

    private static Group createCoffeeTable(FurnitureItem item, Group group) {
        double x = item.getX(), y = item.getY(),
                w = item.getWidth(), d = item.getHeight(), h = 25;
        PhongMaterial topMat = smoothMaterial(item.getPrimaryColor());
        PhongMaterial legMat = woodMaterial();
        Box top = new Box(w, 4, d);
        top.setMaterial(topMat);
        top.getTransforms().add(new Translate(x + w/2, h, y + d/2));
        group.getChildren().add(top);
        double[][] legs = {{4,4},{w-4,4},{4,d-4},{w-4,d-4}};
        for (double[] p : legs) {
            Cylinder leg = new Cylinder(2.5, h);
            leg.setMaterial(legMat);
            leg.getTransforms().add(new Translate(x + p[0], h/2, y + p[1]));
            group.getChildren().add(leg);
        }
        return group;
    }
}
