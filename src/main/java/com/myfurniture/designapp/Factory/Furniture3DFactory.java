package com.myfurniture.designapp.Factory;

import com.myfurniture.designapp.Core.FurnitureItem;
import javafx.animation.FadeTransition;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

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

        double pivotX = item.getX() + item.getWidth() / 2.0;
        double pivotZ = item.getY() + item.getHeight() / 2.0;

        group.getTransforms().add(
                new Rotate(item.getRotation(), pivotX, 0, pivotZ, new Point3D(0, 1, 0))
        );

        addShadowBelow(item, group);
        addFadeInEffect(group);

        return group;
    }

    private static void addFadeInEffect(Group group) {
        FadeTransition fade = new FadeTransition(Duration.millis(700), group);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    private static void addShadowBelow(FurnitureItem item, Group group) {
        double w = item.getWidth(), d = item.getHeight();
        Box shadow = new Box(w * 1.05, 1, d * 1.05);
        PhongMaterial shadowMat = new PhongMaterial(Color.rgb(0, 0, 0, 0.15));
        shadow.setMaterial(shadowMat);
        shadow.setTranslateX(item.getX() + w / 2);
        shadow.setTranslateY(0.5);
        shadow.setTranslateZ(item.getY() + d / 2);
        group.getChildren().add(0, shadow);
    }

    private static PhongMaterial smoothMaterial(Color color) {
        PhongMaterial mat = new PhongMaterial(color);
        mat.setSpecularColor(Color.rgb(240, 240, 240));
        mat.setSpecularPower(96);
        return mat;
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
        mat.setSpecularColor(Color.rgb(160, 120, 90));
        mat.setSpecularPower(64);
        return mat;
    }

    // ---- All furniture creation functions below this line ----

    private static Group createChair(FurnitureItem item, Group group) {
        double x = item.getX(), y = item.getY(), w = item.getWidth(), d = item.getHeight();
        double legH = 40, seatH = 6, backH = 30;

        Box seat = new Box(w - 6, seatH, d - 6);
        seat.setMaterial(smoothMaterial(item.getPrimaryColor()));
        seat.getTransforms().add(new Translate(x + w / 2, legH + seatH / 2, y + d / 2));
        group.getChildren().add(seat);

        double[][] legs = {{3,3},{w-6,3},{3,d-6},{w-6,d-6}};
        for (double[] p : legs) {
            Cylinder leg = new Cylinder(2, legH);
            leg.setMaterial(woodMaterial());
            leg.setTranslateX(x + p[0]+2);
            leg.setTranslateY(legH / 2);
            leg.setTranslateZ(y + p[1]+2);
            group.getChildren().add(leg);
        }

        Box back = new Box(w - 6, backH, 2);
        back.setMaterial(smoothMaterial(item.getSecondaryColor()));
        back.getTransforms().add(new Translate(x + w / 2, legH + seatH + backH / 2, y + 4));
        group.getChildren().add(back);
        return group;
    }

    private static Group createTable(FurnitureItem item, Group group) {
        double x = item.getX(), y = item.getY(), w = item.getWidth(), d = item.getHeight();
        double topH = 6, legH = 48;

        Box top = new Box(w, topH, d);
        top.setMaterial(smoothMaterial(item.getPrimaryColor()));
        top.setTranslateX(x + w / 2);
        top.setTranslateY(legH + topH / 2);
        top.setTranslateZ(y + d / 2);
        group.getChildren().add(top);

        double[][] offs = {{4,4},{w-4,4},{4,d-4},{w-4,d-4}};
        for (double[] p : offs) {
            Cylinder leg = new Cylinder(3, legH);
            leg.setMaterial(woodMaterial());
            leg.setTranslateX(x + p[0]);
            leg.setTranslateY(legH / 2);
            leg.setTranslateZ(y + p[1]);
            group.getChildren().add(leg);
        }

        return group;
    }

    private static Group createBed(FurnitureItem item, Group group) {
        double x = item.getX(), y = item.getY(), w = item.getWidth(), d = item.getHeight();
        double baseH = 10, mattressH = 10, pillowH = 4, legH = 15;

        Box mattress = new Box(w + 5, mattressH, d + 150);
        mattress.setMaterial(smoothMaterial(item.getPrimaryColor()));
        mattress.setTranslateX(x + w / 2);
        mattress.setTranslateY(baseH + mattressH / 2);
        mattress.setTranslateZ(y + (d + 150) / 2);
        group.getChildren().add(mattress);

        Box pillow1 = new Box(w / 5, pillowH, 10);
        pillow1.setMaterial(smoothMaterial(item.getSecondaryColor()));
        pillow1.setTranslateX(x + w / 3);
        pillow1.setTranslateY(baseH + mattressH + pillowH / 2);
        pillow1.setTranslateZ(y + 12);
        Box pillow2 = new Box(w / 5, pillowH, 10);
        pillow2.setMaterial(smoothMaterial(item.getSecondaryColor()));
        pillow2.setTranslateX(x + 2 * w / 3);
        pillow2.setTranslateY(baseH + mattressH + pillowH / 2);
        pillow2.setTranslateZ(y + 12);
        group.getChildren().addAll(pillow1, pillow2);

        double[][] legPositions = {
                {x + 6, y + 6}, {x + w - 6, y + 6},
                {x + 6, y + d + 144}, {x + w - 6, y + d + 144}
        };
        for (double[] pos : legPositions) {
            Cylinder leg = new Cylinder(3, legH);
            leg.setMaterial(woodMaterial());
            leg.setTranslateX(pos[0]);
            leg.setTranslateY(legH / 2);
            leg.setTranslateZ(pos[1]);
            group.getChildren().add(leg);
        }

        return group;
    }

    private static Group createSofa(FurnitureItem item, Group group) {
        double x = item.getX(), y = item.getY(), w = item.getWidth(), d = item.getHeight();
        double baseH = 14, cushionH = 10, backH = 20, armH = 20;

        Box base = new Box(w, baseH, d);
        base.setMaterial(woodMaterial());
        base.setTranslateX(x + w / 2);
        base.setTranslateY(baseH / 2);
        base.setTranslateZ(y + d / 2);
        group.getChildren().add(base);

        Box cushion = new Box(w - 20, cushionH, d - 10);
        cushion.setMaterial(smoothMaterial(item.getPrimaryColor()));
        cushion.setTranslateX(x + w / 2);
        cushion.setTranslateY(baseH + cushionH / 2);
        cushion.setTranslateZ(y + d / 2);
        group.getChildren().add(cushion);

        Box back = new Box(w - 20, backH, 4);
        back.setMaterial(smoothMaterial(item.getPrimaryColor()));
        back.setTranslateX(x + w / 2);
        back.setTranslateY(baseH + cushionH + backH / 2);
        back.setTranslateZ(y + 4);
        group.getChildren().add(back);

        Box leftArm = new Box(8, armH, 6);
        leftArm.setMaterial(smoothMaterial(item.getSecondaryColor()));
        leftArm.setTranslateX(x + 4);
        leftArm.setTranslateY(baseH + armH / 2);
        leftArm.setTranslateZ(y + d / 2);
        Box rightArm = new Box(8, armH, 6);
        rightArm.setMaterial(smoothMaterial(item.getSecondaryColor()));
        rightArm.setTranslateX(x + w - 4);
        rightArm.setTranslateY(baseH + armH / 2);
        rightArm.setTranslateZ(y + d / 2);
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
        left.setTranslateX(x + 2.5);
        left.setTranslateY(h / 2);
        left.setTranslateZ(y + d / 2);
        Box right = new Box(5, h, d);
        right.setMaterial(woodMat);
        right.setTranslateX(x + w - 2.5);
        right.setTranslateY(h / 2);
        right.setTranslateZ(y + d / 2);

        // Back panel
        Box back = new Box(w, h, 2);
        back.setMaterial(backMat);
        back.setTranslateX(x + w / 2);
        back.setTranslateY(h / 2);
        back.setTranslateZ(y + 1);

        group.getChildren().addAll(left, right, back);

        // Shelves
        for (int i = 0; i <= shelves; i++) {
            Box shelf = new Box(w - 10, 3, d);
            shelf.setMaterial(woodMat);
            shelf.setTranslateX(x + w / 2);
            shelf.setTranslateY(10 + i * (h / (shelves + 1)));
            shelf.setTranslateZ(y + d / 2);
            group.getChildren().add(shelf);
        }

        return group;
    }

    private static Group createWardrobe(FurnitureItem item, Group group) {
        double x = item.getX(), y = item.getY(), w = item.getWidth(), h = item.getHeight(), d = 25;
        PhongMaterial mat = woodMaterial();
        Box body = new Box(w, h, d);
        body.setMaterial(mat);
        body.setTranslateX(x + w / 2);
        body.setTranslateY(h / 2);
        body.setTranslateZ(y + d / 2);
        group.getChildren().add(body);
        return group;
    }

    private static Group createDiningTable(FurnitureItem item, Group group) {
        double x = item.getX(), y = item.getY(), w = item.getWidth(), d = item.getHeight();
        double topH = 6, legH = 50;

        Box top = new Box(w, topH, d);
        top.setMaterial(smoothMaterial(item.getPrimaryColor()));
        top.setTranslateX(x + w / 2);
        top.setTranslateY(legH + topH / 2);
        top.setTranslateZ(y + d / 2);
        group.getChildren().add(top);

        for (double[] pos : new double[][]{{4, 4}, {w - 4, 4}, {4, d - 4}, {w - 4, d - 4}}) {
            Cylinder leg = new Cylinder(3, legH);
            leg.setMaterial(woodMaterial());
            leg.setTranslateX(x + pos[0]);
            leg.setTranslateY(legH / 2);
            leg.setTranslateZ(y + pos[1]);
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
        stand.setTranslateX(x + w / 2);
        stand.setTranslateY((h - 20) / 2);
        stand.setTranslateZ(y + w / 2);

        Box head = new Box(w + 10, 10, w + 10);
        head.setMaterial(headMat);
        head.setTranslateX(x + w / 2);
        head.setTranslateY(h - 5);
        head.setTranslateZ(y + w / 2);

        group.getChildren().addAll(stand, head);
        return group;
    }

    private static Group createTVStand(FurnitureItem item, Group group) {
        double x = item.getX(), y = item.getY(), w = item.getWidth(), d = item.getHeight(), h = 30;
        PhongMaterial mat = woodMaterial();
        Box body = new Box(w, h, d);
        body.setMaterial(mat);
        body.setTranslateX(x + w / 2);
        body.setTranslateY(h / 2);
        body.setTranslateZ(y + d / 2);
        group.getChildren().add(body);
        return group;
    }

    private static Group createCoffeeTable(FurnitureItem item, Group group) {
        double x = item.getX(), y = item.getY(), w = item.getWidth(), d = item.getHeight(), h = 25;
        PhongMaterial topMat = smoothMaterial(item.getPrimaryColor());
        PhongMaterial legMat = woodMaterial();

        Box top = new Box(w, 4, d);
        top.setMaterial(topMat);
        top.setTranslateX(x + w / 2);
        top.setTranslateY(h);
        top.setTranslateZ(y + d / 2);
        group.getChildren().add(top);

        double[][] legs = {{4, 4}, {w - 4, 4}, {4, d - 4}, {w - 4, d - 4}};
        for (double[] p : legs) {
            Cylinder leg = new Cylinder(2.5, h);
            leg.setMaterial(legMat);
            leg.setTranslateX(x + p[0]);
            leg.setTranslateY(h / 2);
            leg.setTranslateZ(y + p[1]);
            group.getChildren().add(leg);
        }

        return group;
    }

}
