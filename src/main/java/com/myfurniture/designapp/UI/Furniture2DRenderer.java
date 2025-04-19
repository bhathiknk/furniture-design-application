package com.myfurniture.designapp.UI;

import com.myfurniture.designapp.Core.FurnitureItem;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Furniture2DRenderer {

    /**
     * Draws a 2D “icon” for the given furniture item.
     *
     * @param gc          the GraphicsContext to draw into
     * @param item        the furniture item
     * @param isSelected  whether to highlight it
     */
    public static void drawFurniture(GraphicsContext gc, FurnitureItem item, boolean isSelected) {
        double w = item.getWidth(), h = item.getHeight();
        // draw base shape
        switch (item.getType().toLowerCase()) {
            case "chair":
                drawChair(gc, item);
                break;
            case "table":
            case "dining table":
                drawTable(gc, item);
                break;
            case "bed":
                drawBed(gc, item);
                break;
            case "sofa":
                drawSofa(gc, item);
                break;
            case "bookshelf":
                drawBookshelf(gc, item);
                break;
            case "wardrobe":
                drawWardrobe(gc, item);
                break;
            case "lamp":
                drawLamp(gc, item);
                break;
            case "tv stand":
                drawTVStand(gc, item);
                break;
            case "coffee table":
                drawCoffeeTable(gc, item);
                break;
            default:
                // fallback to simple rectangle
                gc.setFill(item.getPrimaryColor());
                gc.fillRect(0, 0, w, h);
        }

        // stroke outline
        gc.setStroke(isSelected ? Color.RED : Color.DARKGRAY);
        gc.setLineWidth(isSelected ? 3 : 1);
        gc.strokeRect(0, 0, w, h);
    }

    private static void drawChair(GraphicsContext gc, FurnitureItem it) {
        double w = it.getWidth(), h = it.getHeight();
        // seat
        gc.setFill(it.getPrimaryColor());
        gc.fillRect(0, h * 0.6, w, h * 0.3);
        // backrest
        gc.setFill(it.getSecondaryColor());
        gc.fillRect(0, 0, w, h * 0.4);
    }

    private static void drawTable(GraphicsContext gc, FurnitureItem it) {
        double w = it.getWidth(), h = it.getHeight();
        // top
        gc.setFill(it.getPrimaryColor());
        gc.fillRect(0, 0, w, h * 0.2);
        // legs
        gc.setFill(it.getSecondaryColor());
        double leg = Math.min(w, h) * 0.1;
        gc.fillRect(0, h - leg, leg, leg);
        gc.fillRect(w - leg, h - leg, leg, leg);
        gc.fillRect(0, 0, leg, leg);
        gc.fillRect(w - leg, 0, leg, leg);
    }

    private static void drawBed(GraphicsContext gc, FurnitureItem it) {
        double w = it.getWidth(), h = it.getHeight();
        // mattress
        gc.setFill(it.getPrimaryColor());
        gc.fillRect(0, 0, w, h * 0.7);
        // pillows
        gc.setFill(it.getSecondaryColor());
        gc.fillOval(w * 0.1, h * 0.7, w * 0.3, h * 0.2);
        gc.fillOval(w * 0.6, h * 0.7, w * 0.3, h * 0.2);
    }

    private static void drawSofa(GraphicsContext gc, FurnitureItem it) {
        double w = it.getWidth(), h = it.getHeight();
        // seat
        gc.setFill(it.getPrimaryColor());
        gc.fillRect(0, h * 0.4, w, h * 0.4);
        // back
        gc.setFill(it.getPrimaryColor().darker());
        gc.fillRect(0, 0, w, h * 0.4);
        // arms
        gc.setFill(it.getSecondaryColor());
        gc.fillRect(0, h * 0.4, w * 0.1, h * 0.4);
        gc.fillRect(w * 0.9, h * 0.4, w * 0.1, h * 0.4);
    }

    private static void drawBookshelf(GraphicsContext gc, FurnitureItem it) {
        double w = it.getWidth(), h = it.getHeight();
        gc.setFill(it.getPrimaryColor());
        gc.fillRect(0, 0, w, h);
        gc.setStroke(it.getSecondaryColor());
        for (int i = 1; i < 4; i++) {
            double y = i * h / 4.0;
            gc.strokeLine(0, y, w, y);
        }
    }

    private static void drawWardrobe(GraphicsContext gc, FurnitureItem it) {
        double w = it.getWidth(), h = it.getHeight();
        gc.setFill(it.getPrimaryColor());
        gc.fillRect(0, 0, w, h);
        gc.setStroke(it.getSecondaryColor());
        gc.strokeLine(w / 2, 0, w / 2, h);
    }

    private static void drawLamp(GraphicsContext gc, FurnitureItem it) {
        double w = it.getWidth(), h = it.getHeight();
        // shade
        gc.setFill(it.getPrimaryColor());
        gc.fillOval(0, 0, w, h * 0.6);
        // stand
        gc.setStroke(it.getSecondaryColor());
        gc.setLineWidth(2);
        gc.strokeLine(w / 2, h * 0.6, w / 2, h);
    }

    private static void drawTVStand(GraphicsContext gc, FurnitureItem it) {
        double w = it.getWidth(), h = it.getHeight();
        // body
        gc.setFill(it.getPrimaryColor());
        gc.fillRect(0, 0, w, h * 0.6);
        // shelf
        gc.setFill(it.getSecondaryColor());
        gc.fillRect(w * 0.1, h * 0.6, w * 0.8, h * 0.3);
    }

    private static void drawCoffeeTable(GraphicsContext gc, FurnitureItem it) {
        double w = it.getWidth(), h = it.getHeight();
        // top
        gc.setFill(it.getPrimaryColor());
        gc.fillOval(0, 0, w, h * 0.4);
        // legs
        gc.setFill(it.getSecondaryColor());
        double leg = Math.min(w, h) * 0.05;
        gc.fillRect(leg, h * 0.4, leg, leg);
        gc.fillRect(w - 2*leg, h * 0.4, leg, leg);
    }
}
