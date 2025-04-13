package com.myfurniture.designapp;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.*;
import java.util.List;

public class RoomRenderer3D extends JPanel {
    private final DesignManager designManager;
    private FurnitureItem hoveredItem = null;

    public RoomRenderer3D(DesignManager designManager) {
        this.designManager = designManager;
        setBorder(new TitledBorder("3D Room View (Enhanced Realism + Fixed Floor Mapping)"));
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(1000, 700));

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                hoveredItem = getHoveredItem(e.getPoint());
                repaint();
            }
        });
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
        // Enable anti-aliasing for smoother drawing.
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int panelW = getWidth();
        int panelH = getHeight();

        double scale = 0.5;
        int roomW = (int) (room.getRoomWidth() * scale);
        int roomH = (int) (room.getRoomHeight() * scale);
        int depth = 150;

        // Calculate the origin such that the front face is centered.
        int ox = (panelW - roomW) / 2;
        int oy = (panelH - roomH) / 2 + 100;

        // Draw 3D room structure (floor and walls)
        draw3DRoom(g2, room, scale, ox, oy, depth);

        // Draw furniture items onto the 3D floor.
        for (FurnitureItem item : room.getFurniture()) {
            drawRealisticFurniture(g2, item, scale, ox, oy, depth, item == hoveredItem, room);
        }

        // Re-draw room outlines so walls remain visible.
        drawRoomOutlines(g2, room, scale, ox, oy, depth);

        // If an item is hovered, display its type.
        if (hoveredItem != null) {
            g2.setColor(new Color(0, 0, 0, 200));
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            g2.drawString(hoveredItem.getType(), 15, 20);
        }
    }

    private void draw3DRoom(Graphics2D g2, RoomDesign room, double scale, int ox, int oy, int depth) {
        int roomW = (int) (room.getRoomWidth() * scale);
        int roomH = (int) (room.getRoomHeight() * scale);

        // Define front and back corners for the floor quadrilateral.
        Point ftl = new Point(ox, oy);
        Point fbl = new Point(ox, oy + roomH);
        Point ftr = new Point(ox + roomW, oy);
        Point fbr = new Point(ox + roomW, oy + roomH);
        Point btl = new Point(ox + depth, oy - depth);
        Point btr = new Point(ox + roomW + depth, oy - depth);
        Point bbl = new Point(ox + depth, oy + roomH - depth);
        Point bbr = new Point(ox + roomW + depth, oy + roomH - depth);

        // Floor quadrilateral.
        Polygon floor = new Polygon(
                new int[]{fbl.x, fbr.x, bbr.x, bbl.x},
                new int[]{fbl.y, fbr.y, bbr.y, bbl.y},
                4
        );
        g2.setColor(room.getRoomColor().darker());
        g2.fill(floor);
        drawGrid(g2, floor, roomW, roomH, ox, oy, depth);

        // Back wall from points: btl, btr, bbr, bbl.
        Polygon back = new Polygon(
                new int[]{btl.x, btr.x, bbr.x, bbl.x},
                new int[]{btl.y, btr.y, bbr.y, bbl.y},
                4
        );
        g2.setColor(room.getRoomColor().brighter());
        g2.fill(back);

        // Left wall from: btl, ftl, fbl, bbl.
        Polygon left = new Polygon(
                new int[]{btl.x, ftl.x, fbl.x, bbl.x},
                new int[]{btl.y, ftl.y, fbl.y, bbl.y},
                4
        );
        g2.setColor(room.getRoomColor().darker().darker());
        g2.fill(left);

        // Right wall from: btr, ftr, fbr, bbr.
        Polygon right = new Polygon(
                new int[]{btr.x, ftr.x, fbr.x, bbr.x},
                new int[]{btr.y, ftr.y, fbr.y, bbr.y},
                4
        );
        g2.setColor(room.getRoomColor().darker());
        g2.fill(right);

        g2.setColor(Color.BLACK);
        g2.draw(floor);
        g2.draw(back);
        g2.draw(left);
        g2.draw(right);
    }

    private void drawGrid(Graphics2D g2, Polygon floor, int roomW, int roomH, int ox, int oy, int depth) {
        g2.setColor(new Color(0, 0, 0, 30));
        // Draw vertical grid lines.
        for (int i = 0; i <= 10; i++) {
            int x1 = ox + (i * roomW / 10);
            int y1 = oy + roomH;
            int x2 = x1 + depth;
            int y2 = y1 - depth;
            g2.drawLine(x1, y1, x2, y2);
        }
        // Draw horizontal grid lines.
        for (int i = 0; i <= 10; i++) {
            int y1 = oy + (i * roomH / 10);
            int x1 = ox;
            int y2 = y1 - depth;
            int x2 = ox + depth;
            g2.drawLine(x1, y1, x2, y2);
        }
    }

    /**
     * Uses bilinear interpolation to project 2D coordinates onto the 3D floor quadrilateral.
     *
     * @param x2D   The x-coordinate from the 2D design.
     * @param y2D   The y-coordinate from the 2D design.
     * @param scale Scaling factor.
     * @param ox    X-origin of the front face.
     * @param oy    Y-origin of the front face.
     * @param depth Depth factor for perspective.
     * @param room  The room design.
     * @return The projected 3D point.
     */
    private Point project2Dto3DFloor_Bilinear(int x2D, int y2D, double scale, int ox, int oy, int depth, RoomDesign room) {
        int roomW = (int) (room.getRoomWidth() * scale);
        int roomH = (int) (room.getRoomHeight() * scale);
        double u = (double) x2D / room.getRoomWidth();
        double v = (double) y2D / room.getRoomHeight();

        // Floor quadrilateral corners.
        double Ax = ox;
        double Ay = oy + roomH;
        double Bx = ox + roomW;
        double By = oy + roomH;
        double Cx = ox + roomW + depth;
        double Cy = oy + roomH - depth;
        double Dx = ox + depth;
        double Dy = oy + roomH - depth;

        // Bilinear interpolation.
        double xProj = (1 - v) * ((1 - u) * Dx + u * Cx) + v * ((1 - u) * Ax + u * Bx);
        double yProj = (1 - v) * ((1 - u) * Dy + u * Cy) + v * ((1 - u) * Ay + u * By);
        return new Point((int) xProj, (int) yProj);
    }

    private void drawRealisticFurniture(Graphics2D g2, FurnitureItem item, double scale, int ox, int oy, int depth,
                                        boolean highlight, RoomDesign room) {
        // Project the 2D coordinates onto the 3D floor using bilinear interpolation.
        Point p = project2Dto3DFloor_Bilinear(item.getX(), item.getY(), scale, ox, oy, depth, room);
        int x = p.x;
        int y = p.y;
        int w = (int) (item.getWidth() * scale);
        int h = (int) (item.getHeight() * scale);

        if (highlight) {
            g2.setColor(new Color(255, 255, 0, 160));
            g2.setStroke(new BasicStroke(3));
            g2.drawRect(x - 2, y - 2, w + 4, h + 4);
        }

        // Create a gradient to simulate overhead lighting.
        GradientPaint lightToDark = new GradientPaint(x, y, item.getPrimaryColor().brighter(),
                x, y + h, item.getPrimaryColor().darker());

        switch (item.getType().toLowerCase()) {
            case "chair" -> {
                g2.setPaint(lightToDark);
                // Draw the seat with a slight perspective (a trapezoid).
                Polygon seat = new Polygon(
                        new int[]{x, x + w, x + w - 10, x + 10},
                        new int[]{y + h, y + h, y + h - 10, y + h - 10},
                        4
                );
                g2.fill(seat);
                g2.setColor(item.getSecondaryColor());
                g2.fillRoundRect(x, y - h / 4, w, h / 4, 10, 10); // backrest
            }
            case "table" -> {
                g2.setPaint(lightToDark);
                // Draw the table top as a trapezoid.
                Polygon top = new Polygon(
                        new int[]{x, x + w, x + w - 8, x + 8},
                        new int[]{y, y, y + 8, y + 8},
                        4
                );
                g2.fill(top);
                g2.setColor(item.getSecondaryColor());
                int legW = 6;
                g2.fillRect(x, y + 8, legW, h / 2);
                g2.fillRect(x + w - legW, y + 8, legW, h / 2);
            }
            case "bed" -> {
                g2.setPaint(lightToDark);
                g2.fillRoundRect(x, y, w, h / 2, 20, 20);
                g2.setColor(item.getSecondaryColor());
                g2.fillRoundRect(x + 10, y - 8, w / 3, 12, 10, 10); // pillow
            }
            case "sofa" -> {
                g2.setPaint(new GradientPaint(x, y, item.getPrimaryColor(),
                        x + w, y + h / 2, item.getPrimaryColor().darker()));
                g2.fillRoundRect(x, y, w, h / 2, 25, 25);
                g2.setColor(item.getSecondaryColor());
                g2.fillRoundRect(x + 5, y - 10, w - 10, 12, 10, 10); // backrest
                g2.fillRoundRect(x, y, 10, h / 2, 10, 10);           // left arm
                g2.fillRoundRect(x + w - 10, y, 10, h / 2, 10, 10);   // right arm
            }
            case "bookshelf" -> {
                g2.setColor(item.getPrimaryColor());
                g2.fillRect(x, y, w, h);
                g2.setColor(item.getSecondaryColor());
                for (int i = 1; i < 4; i++) {
                    int shelfY = y + (i * h / 4);
                    g2.drawLine(x, shelfY, x + w, shelfY);
                }
            }
        }

        g2.setColor(Color.DARK_GRAY);
        g2.drawRect(x, y, w, h);
    }

    /**
     * Draws thick outlines along the edges of the 3D room floor quadrilateral so walls remain visible.
     */
    private void drawRoomOutlines(Graphics2D g2, RoomDesign room, double scale, int ox, int oy, int depth) {
        int roomW = (int) (room.getRoomWidth() * scale);
        int roomH = (int) (room.getRoomHeight() * scale);

        // Floor quadrilateral corners
        Point A = new Point(ox, oy + roomH);                 // Front Left
        Point B = new Point(ox + roomW, oy + roomH);           // Front Right
        Point C = new Point(ox + roomW + depth, oy + roomH - depth); // Back Right
        Point D = new Point(ox + depth, oy + roomH - depth);   // Back Left

        g2.setStroke(new BasicStroke(3));
        g2.setColor(Color.BLACK);
        // Draw floor outline
        g2.draw(new Line2D.Double(A, B));
        g2.draw(new Line2D.Double(B, C));
        g2.draw(new Line2D.Double(C, D));
        g2.draw(new Line2D.Double(D, A));

        // Draw wall outlines
        g2.draw(new Line2D.Double(D, C)); // Back wall top line
        g2.draw(new Line2D.Double(A, D)); // Left wall
        g2.draw(new Line2D.Double(B, C)); // Right wall
    }

    private FurnitureItem getHoveredItem(Point p) {
        RoomDesign room = designManager.getCurrentDesign();
        if (room == null) return null;

        double scale = 0.5;
        int roomW = (int) (room.getRoomWidth() * scale);
        int roomH = (int) (room.getRoomHeight() * scale);
        int depth = 150;
        int ox = (getWidth() - roomW) / 2;
        int oy = (getHeight() - roomH) / 2 + 100;

        for (FurnitureItem item : room.getFurniture()) {
            Point pr = project2Dto3DFloor_Bilinear(item.getX(), item.getY(), scale, ox, oy, depth, room);
            int x = pr.x;
            int y = pr.y;
            int w = (int) (item.getWidth() * scale);
            int h = (int) (item.getHeight() * scale);
            Rectangle2D rect = new Rectangle2D.Double(x, y, w, h);
            if (rect.contains(p)) return item;
        }
        return null;
    }
}
