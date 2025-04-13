package com.myfurniture.designapp;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class RoomDesignerPanel extends JPanel {
    private DesignManager designManager;
    private RoomDesign currentRoomDesign;
    private DesignCanvas canvas;
    private JPanel palettePanel;

    public RoomDesignerPanel(DesignManager designManager) {
        this.designManager = designManager;
        // Initialize the current room design with default dimensions and color
        currentRoomDesign = new RoomDesign(800, 600, Color.LIGHT_GRAY);
        designManager.setCurrentDesign(currentRoomDesign);
        setLayout(new BorderLayout());
        initComponents();
    }

    private void initComponents() {
        // Palette panel with buttons to add furniture
        palettePanel = new JPanel();
        palettePanel.setLayout(new BoxLayout(palettePanel, BoxLayout.Y_AXIS));
        palettePanel.setBorder(new TitledBorder("Furniture Palette"));

        JButton btnAddChair = new JButton("Add Chair");
        btnAddChair.addActionListener(e -> {
            FurnitureItem chair = FurnitureFactory.createFurniture("Chair");
            // Place the chair at the center of the room by default
            chair.setX(currentRoomDesign.getRoomWidth() / 2 - 50);
            chair.setY(currentRoomDesign.getRoomHeight() / 2 - 50);
            currentRoomDesign.addFurniture(chair);
            canvas.repaint();
        });

        JButton btnAddTable = new JButton("Add Table");
        btnAddTable.addActionListener(e -> {
            FurnitureItem table = FurnitureFactory.createFurniture("Table");
            table.setX(currentRoomDesign.getRoomWidth() / 2 - 50);
            table.setY(currentRoomDesign.getRoomHeight() / 2 - 50);
            currentRoomDesign.addFurniture(table);
            canvas.repaint();
        });

        palettePanel.add(btnAddChair);
        palettePanel.add(Box.createVerticalStrut(10));
        palettePanel.add(btnAddTable);

        add(palettePanel, BorderLayout.WEST);

        // Canvas for 2D room design
        canvas = new DesignCanvas();
        canvas.setPreferredSize(new Dimension(currentRoomDesign.getRoomWidth(), currentRoomDesign.getRoomHeight()));
        canvas.setBackground(currentRoomDesign.getRoomColor());
        add(new JScrollPane(canvas), BorderLayout.CENTER);
    }

    // Inner canvas class that draws the room and furniture; supports drag-and-drop
    private class DesignCanvas extends JPanel implements MouseListener, MouseMotionListener {
        private FurnitureItem selectedItem = null;
        private int offsetX, offsetY;

        public DesignCanvas() {
            addMouseListener(this);
            addMouseMotionListener(this);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // Draw room boundary
            g.setColor(Color.BLACK);
            g.drawRect(0, 0, currentRoomDesign.getRoomWidth() - 1, currentRoomDesign.getRoomHeight() - 1);
            // Draw each furniture item
            List<FurnitureItem> furnitureList = currentRoomDesign.getFurniture();
            if (furnitureList != null) {
                for (FurnitureItem item : furnitureList) {
                    drawFurnitureItem(g, item);
                }
            }
        }

        private void drawFurnitureItem(Graphics g, FurnitureItem item) {
            Graphics2D g2d = (Graphics2D) g;
            if ("Chair".equals(item.getType())) {
                // Draw a chair as a seat (rectangle) with a backrest (smaller rectangle above)
                g2d.setColor(item.getPrimaryColor());
                g2d.fillRect(item.getX(), item.getY(), item.getWidth(), item.getHeight());
                g2d.setColor(item.getSecondaryColor());
                g2d.fillRect(item.getX(), item.getY() - item.getHeight() / 2, item.getWidth(), item.getHeight() / 2);
            } else if ("Table".equals(item.getType())) {
                // Draw a table as a rectangle with four small leg rectangles at the corners
                g2d.setColor(item.getPrimaryColor());
                g2d.fillRect(item.getX(), item.getY(), item.getWidth(), item.getHeight());
                g2d.setColor(item.getSecondaryColor());
                int legSize = 10;
                g2d.fillRect(item.getX(), item.getY(), legSize, legSize);
                g2d.fillRect(item.getX() + item.getWidth() - legSize, item.getY(), legSize, legSize);
                g2d.fillRect(item.getX(), item.getY() + item.getHeight() - legSize, legSize, legSize);
                g2d.fillRect(item.getX() + item.getWidth() - legSize, item.getY() + item.getHeight() - legSize, legSize, legSize);
            }
            // Draw an outline
            g2d.setColor(Color.DARK_GRAY);
            g2d.drawRect(item.getX(), item.getY(), item.getWidth(), item.getHeight());
        }

        @Override
        public void mousePressed(MouseEvent e) {
            int mx = e.getX();
            int my = e.getY();
            for (FurnitureItem item : currentRoomDesign.getFurniture()) {
                if (mx >= item.getX() && mx <= item.getX() + item.getWidth() &&
                        my >= item.getY() && my <= item.getY() + item.getHeight()) {
                    selectedItem = item;
                    offsetX = mx - item.getX();
                    offsetY = my - item.getY();
                    break;
                }
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            selectedItem = null;
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (selectedItem != null) {
                int newX = e.getX() - offsetX;
                int newY = e.getY() - offsetY;
                // Keep the item within room boundaries
                newX = Math.max(0, Math.min(newX, currentRoomDesign.getRoomWidth() - selectedItem.getWidth()));
                newY = Math.max(0, Math.min(newY, currentRoomDesign.getRoomHeight() - selectedItem.getHeight()));
                selectedItem.setX(newX);
                selectedItem.setY(newY);
                repaint();
            }
        }

        // Unused mouse events
        @Override public void mouseClicked(MouseEvent e) {}
        @Override public void mouseEntered(MouseEvent e) {}
        @Override public void mouseExited(MouseEvent e) {}
        @Override public void mouseMoved(MouseEvent e) {}
    }
}
