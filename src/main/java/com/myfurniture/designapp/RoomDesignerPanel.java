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

    // Fields for room settings
    private JTextField txtRoomWidth;
    private JTextField txtRoomHeight;
    private JButton btnRoomColor;
    private JButton btnApplyRoomSettings;

    // Fields for furniture color override
    private JButton btnPrimaryColor;
    private JButton btnSecondaryColor;
    private Color chosenPrimary = Color.ORANGE;
    private Color chosenSecondary = Color.DARK_GRAY;

    public RoomDesignerPanel(DesignManager designManager) {
        this.designManager = designManager;
        // Initialize the current room design with default dimensions and color
        currentRoomDesign = new RoomDesign(800, 600, Color.LIGHT_GRAY);
        designManager.setCurrentDesign(currentRoomDesign);

        setLayout(new BorderLayout());
        initComponents();
    }

    private void initComponents() {
        // Palette + Settings Panel
        palettePanel = new JPanel();
        palettePanel.setLayout(new BoxLayout(palettePanel, BoxLayout.Y_AXIS));
        palettePanel.setBorder(new TitledBorder("Room & Furniture Settings"));

        // Room dimension input fields
        palettePanel.add(new JLabel("Room Width:"));
        txtRoomWidth = new JTextField(String.valueOf(currentRoomDesign.getRoomWidth()), 6);
        palettePanel.add(txtRoomWidth);

        palettePanel.add(new JLabel("Room Height:"));
        txtRoomHeight = new JTextField(String.valueOf(currentRoomDesign.getRoomHeight()), 6);
        palettePanel.add(txtRoomHeight);

        // Room color
        btnRoomColor = new JButton("Select Room Color");
        btnRoomColor.addActionListener(e -> {
            Color c = JColorChooser.showDialog(this,
                    "Choose Room Color", currentRoomDesign.getRoomColor());
            if (c != null) {
                currentRoomDesign.setRoomColor(c);
                canvas.setBackground(c);
                canvas.repaint();
            }
        });
        palettePanel.add(btnRoomColor);

        // Apply room settings
        btnApplyRoomSettings = new JButton("Apply Room Size");
        btnApplyRoomSettings.addActionListener(e -> applyRoomSettings());
        palettePanel.add(btnApplyRoomSettings);

        palettePanel.add(Box.createVerticalStrut(20));

        // Furniture color pickers
        palettePanel.add(new JLabel("Furniture Primary Color:"));
        btnPrimaryColor = new JButton("Select Primary Color");
        btnPrimaryColor.addActionListener(e -> {
            Color c = JColorChooser.showDialog(this,
                    "Choose Furniture Primary Color", chosenPrimary);
            if (c != null) {
                chosenPrimary = c;
            }
        });
        palettePanel.add(btnPrimaryColor);

        palettePanel.add(new JLabel("Furniture Secondary Color:"));
        btnSecondaryColor = new JButton("Select Secondary Color");
        btnSecondaryColor.addActionListener(e -> {
            Color c = JColorChooser.showDialog(this,
                    "Choose Furniture Secondary Color", chosenSecondary);
            if (c != null) {
                chosenSecondary = c;
            }
        });
        palettePanel.add(btnSecondaryColor);

        palettePanel.add(Box.createVerticalStrut(15));
        palettePanel.add(new JLabel("Add Furniture:"));

        // Furniture Buttons
        JButton btnAddChair = new JButton("Chair");
        btnAddChair.addActionListener(e -> addFurniture("Chair"));
        palettePanel.add(btnAddChair);

        JButton btnAddTable = new JButton("Table");
        btnAddTable.addActionListener(e -> addFurniture("Table"));
        palettePanel.add(btnAddTable);

        JButton btnAddBed = new JButton("Bed");
        btnAddBed.addActionListener(e -> addFurniture("Bed"));
        palettePanel.add(btnAddBed);

        JButton btnAddSofa = new JButton("Sofa");
        btnAddSofa.addActionListener(e -> addFurniture("Sofa"));
        palettePanel.add(btnAddSofa);

        JButton btnAddBookshelf = new JButton("Bookshelf");
        btnAddBookshelf.addActionListener(e -> addFurniture("Bookshelf"));
        palettePanel.add(btnAddBookshelf);

        add(palettePanel, BorderLayout.WEST);

        // 2D Canvas for the Room
        canvas = new DesignCanvas();
        canvas.setPreferredSize(new Dimension(currentRoomDesign.getRoomWidth(), currentRoomDesign.getRoomHeight()));
        canvas.setBackground(currentRoomDesign.getRoomColor());
        add(new JScrollPane(canvas), BorderLayout.CENTER);
    }

    private void applyRoomSettings() {
        try {
            int w = Integer.parseInt(txtRoomWidth.getText().trim());
            int h = Integer.parseInt(txtRoomHeight.getText().trim());
            currentRoomDesign.setRoomWidth(w);
            currentRoomDesign.setRoomHeight(h);

            // Update canvas size
            canvas.setPreferredSize(new Dimension(w, h));
            canvas.revalidate();
            canvas.repaint();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Please enter valid integers for width/height.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addFurniture(String furnitureType) {
        // Create furniture from factory
        FurnitureItem item = FurnitureFactory.createFurniture(furnitureType);
        if (item != null) {
            // Override default colors with user-chosen colors
            item.setPrimaryColor(chosenPrimary);
            item.setSecondaryColor(chosenSecondary);

            // Place item near center
            item.setX(currentRoomDesign.getRoomWidth() / 2 - item.getWidth() / 2);
            item.setY(currentRoomDesign.getRoomHeight() / 2 - item.getHeight() / 2);

            currentRoomDesign.addFurniture(item);
            canvas.repaint();
        }
    }

    // -------------------- 2D Designer Canvas with Drag-and-Drop --------------------
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
            // Draw the bounding box for the room
            g.setColor(Color.BLACK);
            g.drawRect(0, 0,
                    currentRoomDesign.getRoomWidth() - 1,
                    currentRoomDesign.getRoomHeight() - 1);

            // Draw all furniture items
            List<FurnitureItem> furnitureList = currentRoomDesign.getFurniture();
            if (furnitureList != null) {
                for (FurnitureItem item : furnitureList) {
                    drawFurnitureItem(g, item);
                }
            }
        }

        private void drawFurnitureItem(Graphics g, FurnitureItem item) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(item.getPrimaryColor());

            switch (item.getType()) {
                case "Chair" -> {
                    // seat
                    g2d.fillRect(item.getX(), item.getY(), item.getWidth(), item.getHeight());
                    // backrest
                    g2d.setColor(item.getSecondaryColor());
                    g2d.fillRect(item.getX(), item.getY() - item.getHeight() / 2,
                            item.getWidth(), item.getHeight() / 2);
                }
                case "Table" -> {
                    g2d.fillRect(item.getX(), item.getY(), item.getWidth(), item.getHeight());
                    g2d.setColor(item.getSecondaryColor());
                    int legSize = 10;
                    g2d.fillRect(item.getX(), item.getY(), legSize, legSize);
                    g2d.fillRect(item.getX() + item.getWidth() - legSize, item.getY(),
                            legSize, legSize);
                    g2d.fillRect(item.getX(), item.getY() + item.getHeight() - legSize,
                            legSize, legSize);
                    g2d.fillRect(item.getX() + item.getWidth() - legSize,
                            item.getY() + item.getHeight() - legSize,
                            legSize, legSize);
                }
                case "Bed" -> {
                    g2d.fillRect(item.getX(), item.getY(),
                            item.getWidth(), item.getHeight() / 2);
                    g2d.setColor(item.getSecondaryColor());
                    // pillow
                    g2d.fillRect(item.getX() + 10, item.getY() - 8,
                            item.getWidth() / 3, 10);
                }
                case "Sofa" -> {
                    g2d.fillRect(item.getX(), item.getY(),
                            item.getWidth(), item.getHeight() / 2);
                    g2d.setColor(item.getSecondaryColor());
                    // back
                    g2d.fillRect(item.getX() + 5, item.getY() - 10,
                            item.getWidth() - 10, 10);
                }
                case "Bookshelf" -> {
                    g2d.fillRect(item.getX(), item.getY(),
                            item.getWidth(), item.getHeight());
                    g2d.setColor(item.getSecondaryColor());
                    for (int i = 1; i < 4; i++) {
                        int shelfY = item.getY() + (i * item.getHeight() / 4);
                        g2d.drawLine(item.getX(), shelfY,
                                item.getX() + item.getWidth(), shelfY);
                    }
                }
                default -> {
                    // If we have any unknown type
                    g2d.fillRect(item.getX(), item.getY(),
                            item.getWidth(), item.getHeight());
                }
            }
            // Outline
            g2d.setColor(Color.DARK_GRAY);
            g2d.drawRect(item.getX(), item.getY(),
                    item.getWidth(), item.getHeight());
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
                // Keep furniture within the room boundary
                newX = Math.max(0,
                        Math.min(newX,
                                currentRoomDesign.getRoomWidth()
                                        - selectedItem.getWidth()));
                newY = Math.max(0,
                        Math.min(newY,
                                currentRoomDesign.getRoomHeight()
                                        - selectedItem.getHeight()));
                selectedItem.setX(newX);
                selectedItem.setY(newY);
                repaint();
            }
        }

        // Unused events
        @Override public void mouseClicked(MouseEvent e) {}
        @Override public void mouseEntered(MouseEvent e) {}
        @Override public void mouseExited(MouseEvent e) {}
        @Override public void mouseMoved(MouseEvent e) {}
    }
}
