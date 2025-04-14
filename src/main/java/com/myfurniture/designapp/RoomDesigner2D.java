package com.myfurniture.designapp;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.geometry.Insets;

public class RoomDesigner2D extends BorderPane {
    private DesignManager designManager;
    private RoomDesign currentRoomDesign;
    private DesignerCanvas canvas;
    private VBox palettePanel;
    private TextField txtRoomWidth;
    private TextField txtRoomHeight;
    private Button btnRoomColor;
    private Button btnApplyRoomSettings;
    private Button btnPrimaryColor;
    private Button btnSecondaryColor;
    private Color chosenPrimary = Color.ORANGE;
    private Color chosenSecondary = Color.DARKGRAY;
    private Runnable update3DCallback;

    public RoomDesigner2D(DesignManager designManager, Runnable update3DCallback) {
        this.designManager = designManager;
        this.update3DCallback = update3DCallback;
        currentRoomDesign = new RoomDesign(800, 600, Color.LIGHTGRAY);
        designManager.setCurrentDesign(currentRoomDesign);
        initComponents();
    }

    private void initComponents() {
        palettePanel = new VBox(10);
        palettePanel.setPadding(new Insets(10));

        Label lblRoomWidth = new Label("Room Width:");
        txtRoomWidth = new TextField(String.valueOf(currentRoomDesign.getRoomWidth()));
        Label lblRoomHeight = new Label("Room Height:");
        txtRoomHeight = new TextField(String.valueOf(currentRoomDesign.getRoomHeight()));

        btnRoomColor = new Button("Select Room Color");
        btnRoomColor.setOnAction(e -> {
            Color newColor = ColorPickerDialog.showDialog(currentRoomDesign.getRoomColor());
            if (newColor != null) {
                currentRoomDesign.setRoomColor(newColor);
                canvas.draw();
                if (update3DCallback != null) {
                    update3DCallback.run();
                }
            }
        });

        btnApplyRoomSettings = new Button("Apply Room Settings");
        btnApplyRoomSettings.setOnAction(e -> applyRoomSettings());

        Label lblPrimary = new Label("Furniture Primary Color:");
        btnPrimaryColor = new Button("Select Primary Color");
        btnPrimaryColor.setOnAction(e -> {
            Color newColor = ColorPickerDialog.showDialog(chosenPrimary);
            if (newColor != null) {
                chosenPrimary = newColor;
            }
        });

        Label lblSecondary = new Label("Furniture Secondary Color:");
        btnSecondaryColor = new Button("Select Secondary Color");
        btnSecondaryColor.setOnAction(e -> {
            Color newColor = ColorPickerDialog.showDialog(chosenSecondary);
            if (newColor != null) {
                chosenSecondary = newColor;
            }
        });

        Label lblAddFurniture = new Label("Add Furniture:");
        Button btnAddChair = new Button("Chair");
        btnAddChair.setOnAction(e -> addFurniture("Chair"));
        Button btnAddTable = new Button("Table");
        btnAddTable.setOnAction(e -> addFurniture("Table"));
        Button btnAddBed = new Button("Bed");
        btnAddBed.setOnAction(e -> addFurniture("Bed"));
        Button btnAddSofa = new Button("Sofa");
        btnAddSofa.setOnAction(e -> addFurniture("Sofa"));
        Button btnAddBookshelf = new Button("Bookshelf");
        btnAddBookshelf.setOnAction(e -> addFurniture("Bookshelf"));

        palettePanel.getChildren().addAll(
                lblRoomWidth, txtRoomWidth, lblRoomHeight, txtRoomHeight,
                btnRoomColor, btnApplyRoomSettings,
                lblPrimary, btnPrimaryColor, lblSecondary, btnSecondaryColor,
                lblAddFurniture, new HBox(10, btnAddChair, btnAddTable, btnAddBed, btnAddSofa, btnAddBookshelf)
        );

        canvas = new DesignerCanvas(currentRoomDesign);
        setLeft(palettePanel);
        setCenter(new ScrollPane(canvas));
    }

    private void applyRoomSettings() {
        try {
            int w = Integer.parseInt(txtRoomWidth.getText().trim());
            int h = Integer.parseInt(txtRoomHeight.getText().trim());
            currentRoomDesign.setRoomWidth(w);
            currentRoomDesign.setRoomHeight(h);
            canvas.setWidth(w);
            canvas.setHeight(h);
            canvas.draw();
            if (update3DCallback != null) {
                update3DCallback.run();
            }
        } catch (NumberFormatException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter valid integers for width and height.");
            alert.showAndWait();
        }
    }

    private void addFurniture(String furnitureType) {
        FurnitureItem item = FurnitureFactory.createFurniture(furnitureType);
        if (item != null) {
            item.setPrimaryColor(chosenPrimary);
            item.setSecondaryColor(chosenSecondary);
            item.setX(currentRoomDesign.getRoomWidth() / 2 - item.getWidth() / 2);
            item.setY(currentRoomDesign.getRoomHeight() / 2 - item.getHeight() / 2);
            currentRoomDesign.addFurniture(item);
            canvas.draw();
            if (update3DCallback != null) {
                update3DCallback.run();
            }
        }
    }

    private class DesignerCanvas extends Canvas {
        private RoomDesign roomDesign;
        private FurnitureItem selectedItem = null;
        private double offsetX, offsetY;

        public DesignerCanvas(RoomDesign roomDesign) {
            super(roomDesign.getRoomWidth(), roomDesign.getRoomHeight());
            this.roomDesign = roomDesign;
            draw();
            setOnMousePressed(this::onMousePressed);
            setOnMouseDragged(this::onMouseDragged);
            setOnMouseReleased(e -> selectedItem = null);
        }

        private void onMousePressed(MouseEvent e) {
            double x = e.getX();
            double y = e.getY();
            for (FurnitureItem item : roomDesign.getFurniture()) {
                if (x >= item.getX() && x <= item.getX() + item.getWidth() &&
                        y >= item.getY() && y <= item.getY() + item.getHeight()) {
                    selectedItem = item;
                    offsetX = x - item.getX();
                    offsetY = y - item.getY();
                    break;
                }
            }
        }

        private void onMouseDragged(MouseEvent e) {
            if (selectedItem != null) {
                double newX = e.getX() - offsetX;
                double newY = e.getY() - offsetY;
                newX = Math.max(0, Math.min(newX, roomDesign.getRoomWidth() - selectedItem.getWidth()));
                newY = Math.max(0, Math.min(newY, roomDesign.getRoomHeight() - selectedItem.getHeight()));
                selectedItem.setX((int) newX);
                selectedItem.setY((int) newY);
                draw();
                if (update3DCallback != null) {
                    update3DCallback.run();
                }
            }
        }

        public void draw() {
            GraphicsContext gc = getGraphicsContext2D();
            // Fill background with the selected room color.
            gc.setFill(roomDesign.getRoomColor());
            gc.fillRect(0, 0, getWidth(), getHeight());
            // Draw room boundary.
            gc.setStroke(Color.BLACK);
            gc.strokeRect(0, 0, roomDesign.getRoomWidth(), roomDesign.getRoomHeight());
            // Draw each furniture item.
            for (FurnitureItem item : roomDesign.getFurniture()) {
                drawFurniture(gc, item);
            }
        }

        private void drawFurniture(GraphicsContext gc, FurnitureItem item) {
            gc.setFill(item.getPrimaryColor());
            switch (item.getType().toLowerCase()) {
                case "chair":
                    gc.fillRect(item.getX(), item.getY(), item.getWidth(), item.getHeight());
                    gc.setFill(item.getSecondaryColor());
                    gc.fillRect(item.getX(), item.getY() - item.getHeight() / 2, item.getWidth(), item.getHeight() / 2);
                    break;
                case "table":
                    gc.fillRect(item.getX(), item.getY(), item.getWidth(), item.getHeight());
                    gc.setFill(item.getSecondaryColor());
                    double legSize = 10;
                    gc.fillRect(item.getX(), item.getY(), legSize, legSize);
                    gc.fillRect(item.getX() + item.getWidth() - legSize, item.getY(), legSize, legSize);
                    gc.fillRect(item.getX(), item.getY() + item.getHeight() - legSize, legSize, legSize);
                    gc.fillRect(item.getX() + item.getWidth() - legSize, item.getY() + item.getHeight() - legSize, legSize, legSize);
                    break;
                case "bed":
                    gc.fillRect(item.getX(), item.getY(), item.getWidth(), item.getHeight() / 2);
                    gc.setFill(item.getSecondaryColor());
                    gc.fillRect(item.getX() + 10, item.getY() - 8, item.getWidth() / 3, 10);
                    break;
                case "sofa":
                    gc.fillRect(item.getX(), item.getY(), item.getWidth(), item.getHeight() / 2);
                    gc.setFill(item.getSecondaryColor());
                    gc.fillRect(item.getX() + 5, item.getY() - 10, item.getWidth() - 10, 10);
                    break;
                case "bookshelf":
                    gc.fillRect(item.getX(), item.getY(), item.getWidth(), item.getHeight());
                    gc.setStroke(item.getSecondaryColor());
                    for (int i = 1; i < 4; i++) {
                        double shelfY = item.getY() + i * item.getHeight() / 4.0;
                        gc.strokeLine(item.getX(), shelfY, item.getX() + item.getWidth(), shelfY);
                    }
                    break;
                default:
                    gc.fillRect(item.getX(), item.getY(), item.getWidth(), item.getHeight());
            }
            gc.setStroke(Color.DARKGRAY);
            gc.strokeRect(item.getX(), item.getY(), item.getWidth(), item.getHeight());
        }
    }
}
