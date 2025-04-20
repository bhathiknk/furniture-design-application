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
import javafx.util.Duration;

public class Furniture3DFactory {

    public static Group createFurniture3D(FurnitureItem item) {
        Group group;
        switch (item.getType().toLowerCase()) {
            case "chair":        group = createChair(item);        break;
            case "table":        group = createTable(item);        break;
            case "bed":          group = createBed(item);          break;
            case "sofa":         group = createSofa(item);         break;
            case "bookshelf":    group = createBookshelf(item);    break;
            case "wardrobe":     group = createWardrobe(item);     break;
            case "dining table": group = createDiningTable(item);  break;
            case "lamp":         group = createLamp(item);         break;
            case "tv stand":     group = createTVStand(item);      break;
            case "coffee table": group = createCoffeeTable(item);  break;
            default:             group = new Group();              break;
        }

        double pivotX = item.getX() + item.getWidth()  / 2.0;
        double pivotZ = item.getY() + item.getHeight() / 2.0;

        group.getTransforms().add(
                new Rotate(-item.getRotation(), pivotX, 0, pivotZ, new Point3D(0, 1, 0))
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

    // ------------------- MATERIALS -------------------

    /**
     * Softer, less intense diffuse color and gentler specular.
     */
    private static PhongMaterial smoothMaterial(Color color) {
        PhongMaterial mat = new PhongMaterial();
        // 80% of original brightness
        Color base = Color.color(
                color.getRed()   * 0.8,
                color.getGreen() * 0.8,
                color.getBlue()  * 0.8
        );
        mat.setDiffuseColor(base);
        // lower‑intensity white highlight
        mat.setSpecularColor(Color.color(1,1,1,0.3));
        mat.setSpecularPower(64);
        return mat;
    }

    /**
     * Dial back the metal shine a bit.
     */
    private static PhongMaterial metalMaterial(Color baseColor) {
        PhongMaterial mat = new PhongMaterial();
        mat.setDiffuseColor(
                Color.color(
                        baseColor.getRed()   * 0.8,
                        baseColor.getGreen() * 0.8,
                        baseColor.getBlue()  * 0.8
                )
        );
        mat.setSpecularColor(Color.LIGHTGRAY);
        mat.setSpecularPower(64);
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
        // slightly darker specular
        mat.setSpecularColor(Color.rgb(120, 80, 50, 0.5));
        mat.setSpecularPower(48);
        return mat;
    }

    // ------------------- FURNITURE -------------------

    private static Group createChair(FurnitureItem item) {
        Group group = new Group();
        double x = item.getX(), y = item.getY(), w = item.getWidth(), d = item.getHeight();
        double legH = 40, seatH = 6, backH = 30;

        Box seat = new Box(w - 6, seatH, d - 6);
        seat.setMaterial(smoothMaterial(item.getPrimaryColor()));
        seat.setTranslateX(x + w / 2);
        seat.setTranslateY(legH + seatH / 2);
        seat.setTranslateZ(y + d / 2);
        group.getChildren().add(seat);

        double[][] legs = {{3,3},{w-6,3},{3,d-6},{w-6,d-6}};
        for (double[] p : legs) {
            Cylinder leg = new Cylinder(2, legH);
            leg.setMaterial(woodMaterial());
            leg.setTranslateX(x + p[0] + 2);
            leg.setTranslateY(legH / 2);
            leg.setTranslateZ(y + p[1] + 2);
            group.getChildren().add(leg);
        }

        Box back = new Box(w - 6, backH, 2);
        back.setMaterial(smoothMaterial(item.getSecondaryColor()));
        back.setTranslateX(x + w / 2);
        back.setTranslateY(legH + seatH + backH / 2);
        back.setTranslateZ(y + 4);
        group.getChildren().add(back);

        return group;
    }

    private static Group createTable(FurnitureItem item) {
        Group group = new Group();
        double x = item.getX(), y = item.getY(), w = item.getWidth(), d = item.getHeight();
        double topH = 6, legH = 48;

        Box top = new Box(w, topH, d);
        top.setMaterial(smoothMaterial(item.getPrimaryColor()));
        top.setTranslateX(x + w / 2);
        top.setTranslateY(legH + topH / 2);
        top.setTranslateZ(y + d / 2);
        group.getChildren().add(top);

        double[][] offs = {{4, 4}, {w - 4, 4}, {4, d - 4}, {w - 4, d - 4}};
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

    private static Group createBed(FurnitureItem item) {
        Group group = new Group();

        // Scene‐scale: how many code‐units per foot?
        final double UNITS_PER_FOOT = 40.0;
        final double SIX_FEET      = 6 * UNITS_PER_FOOT; // 6 ft → 480 units

        // Original thickness values
        double baseH     = 10;
        double mattressH = 10;
        double pillowH   = 4;

        // Preserve the item's width, but override its depth (length)
        double w = item.getWidth();
        double length = SIX_FEET;

        // Original position
        double x0 = item.getX();
        double y0 = item.getY();

        // 1) Wooden base: width=x, depth=z
        Box base = new Box(w, baseH, length);
        base.setMaterial(woodMaterial());
        base.setTranslateX(x0 + w / 2);
        base.setTranslateY(baseH / 2);
        base.setTranslateZ(y0 + length / 2);
        group.getChildren().add(base);

        // 2) Mattress inset slightly on all sides
        Box mattress = new Box(w - 4, mattressH, length - 8);
        mattress.setMaterial(smoothMaterial(item.getPrimaryColor()));
        mattress.setTranslateX(x0 + w / 2);
        mattress.setTranslateY(baseH + mattressH / 2);
        mattress.setTranslateZ(y0 + length / 2);
        group.getChildren().add(mattress);

        // 3) Pillow sits at the "head" of the bed (along Z)
        Box pillow = new Box(w / 2, pillowH, 8);
        pillow.setMaterial(smoothMaterial(item.getSecondaryColor()));
        pillow.setTranslateX(x0 + w / 2);
        pillow.setTranslateY(baseH + mattressH + pillowH / 2);
        // put pillow just in front (at the start of the length)
        pillow.setTranslateZ(y0 + 8 / 2);
        group.getChildren().add(pillow);

        return group;
    }


    private static Group createSofa(FurnitureItem item) {
        Group group = new Group();
        double x = item.getX(), y = item.getY(), w = item.getWidth(), d = item.getHeight();
        double baseH = 14, cushionH = 10, backH = 20, armH = 20;

        Box base = new Box(w, baseH, d);
        base.setMaterial(woodMaterial());
        base.setTranslateX(x + w / 2);
        base.setTranslateY(baseH / 2);
        base.setTranslateZ(y + d / 2);

        Box cushion = new Box(w - 20, cushionH, d - 10);
        cushion.setMaterial(smoothMaterial(item.getPrimaryColor()));
        cushion.setTranslateX(x + w / 2);
        cushion.setTranslateY(baseH + cushionH / 2);
        cushion.setTranslateZ(y + d / 2);

        Box back = new Box(w - 20, backH, 4);
        back.setMaterial(smoothMaterial(item.getPrimaryColor()));
        back.setTranslateX(x + w / 2);
        back.setTranslateY(baseH + cushionH + backH / 2);
        back.setTranslateZ(y + 4);

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

        group.getChildren().addAll(base, cushion, back, leftArm, rightArm);
        return group;
    }

    private static Group createBookshelf(FurnitureItem item) {
        Group group = new Group();
        double x = item.getX(), y = item.getY(), w = item.getWidth(), h = item.getHeight(), d = 18;
        int shelves = 5;

        Box frame = new Box(w, h, d);
        frame.setMaterial(woodMaterial());
        frame.setTranslateX(x + w / 2);
        frame.setTranslateY(h / 2);
        frame.setTranslateZ(y + d / 2);
        group.getChildren().add(frame);

        for (int i = 0; i < shelves; i++) {
            Box shelf = new Box(w - 8, 3, d);
            shelf.setMaterial(smoothMaterial(Color.LIGHTGRAY));
            shelf.setTranslateX(x + w / 2);
            shelf.setTranslateY(10 + i * (h / (shelves + 1)));
            shelf.setTranslateZ(y + d / 2);
            group.getChildren().add(shelf);
        }

        return group;
    }

    private static Group createWardrobe(FurnitureItem item) {
        Group group = new Group();
        double x = item.getX(), y = item.getY(), w = item.getWidth(), h = item.getHeight(), d = 25;
        Box body = new Box(w, h, d);
        body.setMaterial(woodMaterial());
        body.setTranslateX(x + w / 2);
        body.setTranslateY(h / 2);
        body.setTranslateZ(y + d / 2);
        group.getChildren().add(body);
        return group;
    }

    private static Group createDiningTable(FurnitureItem item) {
        return createTable(item); // same layout logic
    }

    private static Group createLamp(FurnitureItem item) {
        Group group = new Group();
        double x = item.getX(), y = item.getY(), w = item.getWidth(), h = item.getHeight();

        Cylinder stand = new Cylinder(2, h - 20);
        stand.setMaterial(metalMaterial(item.getSecondaryColor()));
        stand.setTranslateX(x + w / 2);
        stand.setTranslateY((h - 20) / 2);
        stand.setTranslateZ(y + w / 2);

        Box head = new Box(w + 10, 10, w + 10);
        head.setMaterial(smoothMaterial(item.getPrimaryColor()));
        head.setTranslateX(x + w / 2);
        head.setTranslateY(h - 5);
        head.setTranslateZ(y + w / 2);

        group.getChildren().addAll(stand, head);
        return group;
    }

    private static Group createTVStand(FurnitureItem item) {
        Group group = new Group();
        double x = item.getX(), y = item.getY(), w = item.getWidth(), d = item.getHeight();

        double bodyHeight = 24;
        double shelfHeight = 6;

        // Main body
        Box body = new Box(w, bodyHeight, d);
        body.setMaterial(woodMaterial());
        body.setTranslateX(x + w / 2);
        body.setTranslateY(bodyHeight / 2);
        body.setTranslateZ(y + d / 2);
        group.getChildren().add(body);

        // Open shelf
        Box shelf = new Box(w - 10, shelfHeight, d - 8);
        shelf.setMaterial(smoothMaterial(Color.LIGHTGRAY));
        shelf.setTranslateX(x + w / 2);
        shelf.setTranslateY(bodyHeight - 6);
        shelf.setTranslateZ(y + d / 2);
        group.getChildren().add(shelf);

        return group;
    }


    private static Group createCoffeeTable(FurnitureItem item) {
        Group group = new Group();
        double x = item.getX(), y = item.getY(), w = item.getWidth(), d = item.getHeight();
        double topH = 4, legH = 20;

        Box top = new Box(w, topH, d);
        top.setMaterial(smoothMaterial(item.getPrimaryColor()));
        top.setTranslateX(x + w / 2);
        top.setTranslateY(legH + topH / 2);
        top.setTranslateZ(y + d / 2);
        group.getChildren().add(top);

        double[][] legs = {{4, 4}, {w - 4, 4}, {4, d - 4}, {w - 4, d - 4}};
        for (double[] p : legs) {
            Cylinder leg = new Cylinder(2.5, legH);
            leg.setMaterial(metalMaterial(item.getSecondaryColor()));
            leg.setTranslateX(x + p[0]);
            leg.setTranslateY(legH / 2);
            leg.setTranslateZ(y + p[1]);
            group.getChildren().add(leg);
        }

        return group;
    }

}
