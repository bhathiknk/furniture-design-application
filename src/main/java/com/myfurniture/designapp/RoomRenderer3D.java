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
        setBorder(new TitledBorder("3D Room View (Enhanced Realism + Correct Floor Mapping)"));
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
        // Enable anti-aliasing for smooth drawing.
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int panelW = getWidth();
        int panelH = getHeight();

        double scale = 0.5;
        int roomW = (int) (room.getRoomWidth() * scale);
        int roomH = (int) (room.getRoomHeight() * scale);
        int depth = 150;

        // Calculate origin: (ox, oy) positions the front face of the 3D room.
        int ox = (panelW - roomW) / 2;
        int oy = (panelH - roomH) / 2 + 100;

        // Draw the 3D room structure.
        draw3DRoom(g2, room, scale, ox, oy, depth);

        // Draw each furniture item on the 3D floor.
        for (FurnitureItem item : room.getFurniture()) {
            drawRealisticFurniture(g2, item, scale, ox, oy, depth, item == hoveredItem, room);
        }

        // Display the type of the hovered furniture.
        if (hoveredItem != null) {
            g2.setColor(new Color(0, 0, 0, 200));
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            g2.drawString(hoveredItem.getType(), 15, 20);
        }
    }

    private void draw3DRoom(Graphics2D g2, RoomDesign room, double scale, int ox, int oy, int depth) {
        int roomW = (int) (room.getRoomWidth() * scale);
        int roomH = (int) (room.getRoomHeight() * scale);

        // Define the 3D quadrilateral for the floor.
        // Front edge of the floor (in 3D view) comes from the 2D design bottom edge.
        // The floor quadrilateral has corners:
        // A (Front Left), B (Front Right), C (Back Right), D (Back Left)
        Point A = new Point(ox, oy + roomH);
        Point B = new Point(ox + roomW, oy + roomH);
        Point C = new Point(ox + roomW + depth, oy + roomH - depth);
        Point D = new Point(ox + depth, oy + roomH - depth);

        // Draw the floor using these four points.
        Polygon floor = new Polygon(
                new int[]{A.x, B.x, C.x, D.x},
                new int[]{A.y, B.y, C.y, D.y},
                4
        );
        g2.setColor(room.getRoomColor().darker());
        g2.fill(floor);
        drawGrid(g2, floor, roomW, roomH, ox, oy, depth);

        // Back wall: defined by the top edge of the floor quadrilateral.
        Point backTopLeft = D;
        Point backTopRight = C;
        // For a simple effect, use the same color gradient.
        Polygon backWall = new Polygon(
                new int[]{backTopLeft.x, backTopRight.x, backTopRight.x, backTopLeft.x},
                new int[]{backTopLeft.y, backTopRight.y, backTopRight.y + 20, backTopLeft.y + 20},
                4
        );
        g2.setColor(room.getRoomColor().brighter());
        g2.fill(backWall);

        // Left wall.
        Polygon leftWall = new Polygon(
                new int[]{ox, D.x, D.x, ox},
                new int[]{oy + roomH, D.y, D.y + 20, oy + roomH},
                4
        );
        g2.setColor(room.getRoomColor().darker().darker());
        g2.fill(leftWall);

        // Right wall.
        Polygon rightWall = new Polygon(
                new int[]{ox + roomW, C.x, C.x, ox + roomW},
                new int[]{oy + roomH, C.y, C.y + 20, oy + roomH},
                4
        );
        g2.setColor(room.getRoomColor().darker());
        g2.fill(rightWall);

        g2.setColor(Color.BLACK);
        g2.draw(floor);
        g2.draw(backWall);
        g2.draw(leftWall);
        g2.draw(rightWall);
    }

    private void drawGrid(Graphics2D g2, Polygon floor, int roomW, int roomH, int ox, int oy, int depth) {
        g2.setColor(new Color(0, 0, 0, 30));
        // Draw vertical grid lines along the floor quadrilateral.
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
     * Projects 2D room coordinates (from the 2D design) onto the 3D floor quadrilateral using bilinear interpolation.
     *
     * @param x2D      The x-coordinate in 2D.
     * @param y2D      The y-coordinate in 2D.
     * @param scale    Scaling factor used for the room.
     * @param ox       X-origin for the front face of the room.
     * @param oy       Y-origin for the front face of the room.
     * @param depth    Depth factor for perspective.
     * @param room     The RoomDesign containing room dimensions.
     * @return A Point representing the projected 3D floor coordinate.
     */
    private Point project2Dto3DFloor_Bilinear(int x2D, int y2D, double scale, int ox, int oy, int depth, RoomDesign room) {
        int roomW = (int) (room.getRoomWidth() * scale);
        int roomH = (int) (room.getRoomHeight() * scale);
        // u and v are relative positions within the 2D design.
        double u = (double) x2D / room.getRoomWidth();
        double v = (double) y2D / room.getRoomHeight();

        // Define floor quadrilateral corners:
        // A (Front Left), B (Front Right), C (Back Right), D (Back Left)
        double Ax = ox;
        double Ay = oy + roomH;
        double Bx = ox + roomW;
        double By = oy + roomH;
        double Cx = ox + roomW + depth;
        double Cy = oy + roomH - depth;
        double Dx = ox + depth;
        double Dy = oy + roomH - depth;

        // Bilinear interpolation:
        double xProj = (1 - v) * ((1 - u) * Dx + u * Cx) + v * ((1 - u) * Ax + u * Bx);
        double yProj = (1 - v) * ((1 - u) * Dy + u * Cy) + v * ((1 - u) * Ay + u * By);
        return new Point((int) xProj, (int) yProj);
    }

    private void drawRealisticFurniture(Graphics2D g2, FurnitureItem item, double scale, int ox, int oy, int depth,
                                        boolean highlight, RoomDesign room) {
        // Use the bilinear projection method so that the furniture is correctly placed on the 3D floor.
        Point p = project2Dto3DFloor_Bilinear(item.getX(), item.getY(), scale, ox, oy, depth, room);
        int x = p.x;
        int y = p.y;
        int w = (int) (item.getWidth() * scale);
        int h = (int) (item.getHeight() * scale);

        // Highlight if the mouse is over the item.
        if (highlight) {
            g2.setColor(new Color(255, 255, 0, 160));
            g2.setStroke(new BasicStroke(3));
            g2.drawRect(x - 2, y - 2, w + 4, h + 4);
        }

        // Setup a gradient for shading to simulate lighting.
        GradientPaint lightToDark = new GradientPaint(x, y, item.getPrimaryColor().brighter(),
                x, y + h, item.getPrimaryColor().darker());

        // Render furniture based on its type.
        switch (item.getType().toLowerCase()) {
            case "chair":
                // Draw the seat (flat shape on the floor) with a subtle backrest.
                g2.setPaint(lightToDark);
                g2.fillRoundRect(x, y, w, h / 2, 10, 10);
                g2.setColor(item.getSecondaryColor());
                g2.fillRoundRect(x, y - (h / 4), w, h / 4, 10, 10);
                break;
            case "table":
                // Draw the table top and two legs.
                g2.setPaint(lightToDark);
                g2.fillRect(x, y, w, h / 6);
                g2.setColor(item.getSecondaryColor());
                int legW = 6;
                g2.fillRect(x, y + h / 6, legW, h / 2);
                g2.fillRect(x + w - legW, y + h / 6, legW, h / 2);
                break;
            case "bed":
                // Draw the bed with a mattress and a pillow.
                g2.setPaint(lightToDark);
                g2.fillRoundRect(x, y, w, h / 2, 20, 20);
                g2.setColor(item.getSecondaryColor());
                g2.fillRoundRect(x + 10, y - 8, w / 3, 12, 10, 10); // pillow detail
                break;
            case "sofa":
                // Draw the sofa seat and a backrest.
                g2.setPaint(new GradientPaint(x, y, item.getPrimaryColor(), x + w, y + h / 2, item.getPrimaryColor().darker()));
                g2.fillRoundRect(x, y, w, h / 2, 25, 25);
                g2.setColor(item.getSecondaryColor());
                g2.fillRoundRect(x + 5, y - 10, w - 10, 12, 10, 10); // backrest
                g2.fillRoundRect(x, y, 10, h / 2, 10, 10);  // left arm
                g2.fillRoundRect(x + w - 10, y, 10, h / 2, 10, 10);  // right arm
                break;
            case "bookshelf":
                // Draw a bookshelf with shelves.
                g2.setColor(item.getPrimaryColor());
                g2.fillRect(x, y, w, h);
                g2.setColor(item.getSecondaryColor());
                for (int i = 1; i < 4; i++) {
                    int shelfY = y + (i * h / 4);
                    g2.drawLine(x, shelfY, x + w, shelfY);
                }
                break;
        }

        // Draw an outline around the furniture.
        g2.setColor(Color.DARK_GRAY);
        g2.drawRect(x, y, w, h);
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
