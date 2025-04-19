package com.myfurniture.designapp.UI;

import com.myfurniture.designapp.Core.FurnitureItem;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Furniture2DRenderer V4
 * ----------------------
 * Simple, schematic 2D icons for quick recognition:
 * - Solid fill for main body
 * - Secondary-color accents for key parts
 * - Black outline (or red when selected)
 */
public class Furniture2DRenderer {

    public static void drawFurniture(GraphicsContext g,
                                     FurnitureItem it,
                                     boolean selected) {

        double w = it.getWidth();
        double h = it.getHeight();
        Color primary   = it.getPrimaryColor();
        Color secondary = it.getSecondaryColor();

        g.save();

        switch (it.getType().toLowerCase()) {
            case "chair"        -> drawChair(g, primary, secondary, w, h);
            case "table", "dining table"
                    -> drawTable(g, primary, secondary, w, h);
            case "bed"          -> drawBed(g, primary, secondary, w, h);
            case "sofa"         -> drawSofa(g, primary, secondary, w, h);
            case "bookshelf"    -> drawShelf(g, primary, secondary, w, h);
            case "wardrobe"     -> drawWardrobe(g, primary, secondary, w, h);
            case "lamp"         -> drawLamp(g, primary, secondary, w, h);
            case "tv stand"     -> drawTV(g, primary, secondary, w, h);
            case "coffee table" -> drawCoffee(g, primary, secondary, w, h);
            default             -> drawDefault(g, primary, w, h);
        }

        // outline
        g.setStroke(selected ? Color.RED : Color.BLACK);
        g.setLineWidth(1);
        g.strokeRect(0, 0, w, h);

        g.restore();
    }

    private static void drawChair(GraphicsContext g, Color p, Color s, double w, double h) {
        // seat
        g.setFill(p);
        g.fillRect(w*0.2, h*0.5, w*0.6, h*0.4);
        // backrest
        g.setFill(s);
        g.fillRect(w*0.2, h*0.2, w*0.6, h*0.2);
    }

    private static void drawTable(GraphicsContext g, Color p, Color s, double w, double h) {
        // tabletop
        g.setFill(p);
        g.fillRect(0, 0, w, h*0.2);
        // legs as small rectangles at corners
        g.setFill(s);
        double lw = w*0.1, lh = h*0.3;
        g.fillRect(0, h*0.2, lw, lh);
        g.fillRect(w-lw, h*0.2, lw, lh);
        g.fillRect(0, h*0.5, lw, lh);
        g.fillRect(w-lw, h*0.5, lw, lh);
    }

    private static void drawBed(GraphicsContext g, Color p, Color s, double w, double h) {
        // mattress
        g.setFill(p);
        g.fillRect(0, h*0.2, w, h*0.6);
        // pillows
        g.setFill(s);
        g.fillOval(w*0.1, 0, w*0.3, h*0.2);
        g.fillOval(w*0.6, 0, w*0.3, h*0.2);
    }

    private static void drawSofa(GraphicsContext g, Color p, Color s, double w, double h) {
        // back
        g.setFill(p);
        g.fillRect(0, 0, w, h*0.3);
        // seat
        g.setFill(p.darker());
        g.fillRect(0, h*0.3, w, h*0.4);
        // arms
        g.setFill(s);
        g.fillRect(0, h*0.3, w*0.1, h*0.4);
        g.fillRect(w*0.9, h*0.3, w*0.1, h*0.4);
    }

    private static void drawShelf(GraphicsContext g, Color p, Color s, double w, double h) {
        g.setFill(p);
        g.fillRect(0, 0, w, h);
        g.setStroke(s.darker());
        for (int i = 1; i <= 3; i++) {
            double y = i * h / 4;
            g.strokeLine(0, y, w, y);
        }
    }

    private static void drawWardrobe(GraphicsContext g, Color p, Color s, double w, double h) {
        g.setFill(p);
        g.fillRect(0, 0, w, h);
        g.setStroke(s.darker());
        g.strokeLine(w/2, 0, w/2, h);
    }

    private static void drawLamp(GraphicsContext g, Color p, Color s, double w, double h) {
        // shade
        g.setFill(p);
        g.fillOval(w*0.2, 0, w*0.6, h*0.3);
        // stand
        g.setFill(s);
        g.fillRect(w*0.48, h*0.3, w*0.04, h*0.5);
    }

    private static void drawTV(GraphicsContext g, Color p, Color s, double w, double h) {
        // screen
        g.setFill(p.darker());
        g.fillRect(w*0.1, 0, w*0.8, h*0.3);
        // stand
        g.setFill(s);
        g.fillRect(w*0.4, h*0.3, w*0.2, h*0.05);
    }

    private static void drawCoffee(GraphicsContext g, Color p, Color s, double w, double h) {
        // top
        g.setFill(p);
        g.fillOval(0, 0, w, h*0.2);
        // legs
        g.setStroke(s.darker());
        g.setLineWidth(2);
        double lw = w*0.05;
        g.strokeLine(lw, h*0.2, lw, h);
        g.strokeLine(w-lw, h*0.2, w-lw, h);
    }

    private static void drawDefault(GraphicsContext g, Color p, double w, double h) {
        g.setFill(p);
        g.fillRect(0, 0, w, h);
    }
}
