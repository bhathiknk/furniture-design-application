package com.myfurniture.designapp;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.geom.Path2D;
import java.util.List;

public class RoomRenderer3D extends JPanel {
    private DesignManager designManager;

    public RoomRenderer3D(DesignManager designManager) {
        this.designManager = designManager;
        setBorder(new TitledBorder("3D Room View (Refined)"));
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(1000, 700));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        RoomDesign room = designManager.getCurrentDesign();
        if (room == null) {
            g.drawString("No design available. Please create a design first.", 50, 50);
            return;
        }

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int panelW = getWidth();
        int panelH = getHeight();

        double scale = 0.5;
        int roomW = (int) (room.getRoomWidth() * scale);
        int roomH = (int) (room.getRoomHeight() * scale);
        int depth = 150;

        int ox = (panelW - roomW) / 2;
        int oy = (panelH - roomH) / 2 + 100;

        Point frontTopLeft = new Point(ox, oy);
        Point frontBottomLeft = new Point(ox, oy + roomH);
        Point frontTopRight = new Point(ox + roomW, oy);
        Point frontBottomRight = new Point(ox + roomW, oy + roomH);

        Point backTopLeft = new Point(ox + depth, oy - depth);
        Point backTopRight = new Point(ox + roomW + depth, oy - depth);
        Point backBottomLeft = new Point(ox + depth, oy + roomH - depth);
        Point backBottomRight = new Point(ox + roomW + depth, oy + roomH - depth);

        // Draw floor
        Polygon floor = new Polygon();
        floor.addPoint(frontBottomLeft.x, frontBottomLeft.y);
        floor.addPoint(frontBottomRight.x, frontBottomRight.y);
        floor.addPoint(backBottomRight.x, backBottomRight.y);
        floor.addPoint(backBottomLeft.x, backBottomLeft.y);
        g2.setColor(room.getRoomColor().darker());
        g2.fill(floor);
        drawGrid(g2, floor, roomW, roomH, ox, oy, depth);

        // Draw walls
        Polygon backWall = new Polygon();
        backWall.addPoint(backTopLeft.x, backTopLeft.y);
        backWall.addPoint(backTopRight.x, backTopRight.y);
        backWall.addPoint(backBottomRight.x, backBottomRight.y);
        backWall.addPoint(backBottomLeft.x, backBottomLeft.y);
        g2.setColor(room.getRoomColor().brighter());
        g2.fill(backWall);

        Polygon leftWall = new Polygon();
        leftWall.addPoint(backTopLeft.x, backTopLeft.y);
        leftWall.addPoint(frontTopLeft.x, frontTopLeft.y);
        leftWall.addPoint(frontBottomLeft.x, frontBottomLeft.y);
        leftWall.addPoint(backBottomLeft.x, backBottomLeft.y);
        g2.setColor(room.getRoomColor().darker().darker());
        g2.fill(leftWall);

        Polygon rightWall = new Polygon();
        rightWall.addPoint(backTopRight.x, backTopRight.y);
        rightWall.addPoint(frontTopRight.x, frontTopRight.y);
        rightWall.addPoint(frontBottomRight.x, frontBottomRight.y);
        rightWall.addPoint(backBottomRight.x, backBottomRight.y);
        g2.setColor(room.getRoomColor().darker());
        g2.fill(rightWall);

        g2.setColor(Color.BLACK);
        g2.draw(floor);
        g2.draw(backWall);
        g2.draw(leftWall);
        g2.draw(rightWall);

        // Draw furniture items
        List<FurnitureItem> items = room.getFurniture();
        for (FurnitureItem item : items) {
            drawFlatFurniture(g2, item, scale, ox, oy, depth);
        }
    }

    private void drawGrid(Graphics2D g2, Polygon floor, int roomW, int roomH, int ox, int oy, int depth) {
        g2.setColor(new Color(0, 0, 0, 40));
        for (int i = 0; i <= 10; i++) {
            int x1 = ox + (i * roomW / 10);
            int y1 = oy + roomH;
            int x2 = x1 + depth;
            int y2 = y1 - depth;
            g2.drawLine(x1, y1, x2, y2);
        }

        for (int i = 0; i <= 10; i++) {
            int y1 = oy + (i * roomH / 10);
            int x1 = ox;
            int y2 = y1 - depth;
            int x2 = ox + depth;
            g2.drawLine(x1, y1, x2, y2);
        }
    }

    private void drawFlatFurniture(Graphics2D g2, FurnitureItem item, double scale, int ox, int oy, int depth) {
        int x = (int) (item.getX() * scale) + ox;
        int y = (int) (item.getY() * scale) + oy;
        int w = (int) (item.getWidth() * scale);
        int h = (int) (item.getHeight() * scale);
        int offset = depth / 5;

        if ("Chair".equals(item.getType())) {
            // Draw seat
            g2.setColor(item.getPrimaryColor());
            g2.fillRect(x, y, w, h);

            // Draw backrest
            g2.setColor(item.getSecondaryColor());
            g2.fillRect(x, y - h / 2, w, h / 2);

            // Outline
            g2.setColor(Color.DARK_GRAY);
            g2.drawRect(x, y, w, h);
            g2.drawRect(x, y - h / 2, w, h / 2);
        } else if ("Table".equals(item.getType())) {
            // Draw table top
            g2.setColor(item.getPrimaryColor().brighter());
            g2.fillRect(x, y, w, h / 6);

            // Draw legs
            g2.setColor(item.getSecondaryColor().darker());
            int legW = 5;
            int legH = h;
            g2.fillRect(x, y + h / 6, legW, legH);
            g2.fillRect(x + w - legW, y + h / 6, legW, legH);
            g2.fillRect(x, y + h / 6 + legH - legW, legW, legW);
            g2.fillRect(x + w - legW, y + h / 6 + legH - legW, legW, legW);

            g2.setColor(Color.DARK_GRAY);
            g2.drawRect(x, y, w, h / 6);
        }
    }
}
