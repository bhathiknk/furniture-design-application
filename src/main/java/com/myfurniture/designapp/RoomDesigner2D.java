package com.myfurniture.designapp;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class RoomDesigner2D extends BorderPane {
    private final DesignManager designManager;
    private final RoomDesign currentRoomDesign;
    private DesignerCanvas canvas;
    private VBox palettePanel;
    private Color chosenPrimary = Color.ORANGE;
    private Color chosenSecondary = Color.DARKGRAY;
    private Runnable update3DCallback;
    private TextField txtRoomWidth;
    private TextField txtRoomHeight;

    public RoomDesigner2D(DesignManager designManager, Runnable update3DCallback) {
        this.designManager = designManager;
        this.update3DCallback = update3DCallback;
        currentRoomDesign = new RoomDesign(800, 600, Color.LIGHTGRAY);
        designManager.setCurrentDesign(currentRoomDesign);
        initUI();
    }

    private void initUI() {
        palettePanel = new VBox(15);
        palettePanel.setPadding(new Insets(15));
        palettePanel.setPrefWidth(300);
        palettePanel.setStyle("-fx-background-color: #f8f8f8;");

        // ========== Room Settings ==========
        VBox roomSettingsBox = new VBox(10);
        roomSettingsBox.getChildren().addAll(
                new Label("Room Width:"), txtRoomWidth = new TextField(String.valueOf(currentRoomDesign.getRoomWidth())),
                new Label("Room Height:"), txtRoomHeight = new TextField(String.valueOf(currentRoomDesign.getRoomHeight()))
        );

        Button btnRoomColor = new Button("Select Room Color");
        btnRoomColor.setMaxWidth(Double.MAX_VALUE);
        btnRoomColor.setOnAction(e -> {
            Color newColor = ColorPickerDialog.showDialog(currentRoomDesign.getRoomColor());
            if (newColor != null) {
                currentRoomDesign.setRoomColor(newColor);
                canvas.draw();
                if (update3DCallback != null) update3DCallback.run();
            }
        });

        Button btnApplyRoom = new Button("Apply Settings");
        btnApplyRoom.setMaxWidth(Double.MAX_VALUE);
        btnApplyRoom.setOnAction(e -> applyRoomSettings());

        TitledPane roomPane = new TitledPane("Room Settings", new VBox(10, roomSettingsBox, btnRoomColor, btnApplyRoom));
        roomPane.setExpanded(true);

        // ========== Color Picker ==========
        Button btnPrimaryColor = new Button("Select Primary Color");
        btnPrimaryColor.setMaxWidth(Double.MAX_VALUE);
        btnPrimaryColor.setOnAction(e -> {
            Color newColor = ColorPickerDialog.showDialog(chosenPrimary);
            if (newColor != null) chosenPrimary = newColor;
        });

        Button btnSecondaryColor = new Button("Select Secondary Color");
        btnSecondaryColor.setMaxWidth(Double.MAX_VALUE);
        btnSecondaryColor.setOnAction(e -> {
            Color newColor = ColorPickerDialog.showDialog(chosenSecondary);
            if (newColor != null) chosenSecondary = newColor;
        });

        TitledPane colorPane = new TitledPane("Furniture Colors", new VBox(10, btnPrimaryColor, btnSecondaryColor));
        colorPane.setExpanded(false);

        // ========== Furniture Buttons ==========
        FlowPane furnitureButtons = new FlowPane(10, 10);
        furnitureButtons.setPrefWrapLength(260);

        String[] furnitureTypes = {
                "Chair", "Table", "Bed", "Sofa", "Bookshelf",
                "Wardrobe", "Dining Table", "Lamp", "TV Stand", "Coffee Table"
        };

        for (String type : furnitureTypes) {
            Button btn = new Button(type);
            btn.setOnAction(e -> addFurniture(type));
            btn.setPrefWidth(120);
            furnitureButtons.getChildren().add(btn);
        }

        TitledPane furniturePane = new TitledPane("Add Furniture", furnitureButtons);
        furniturePane.setExpanded(true);

        // ========== Rotate Button ==========
        Button btnRotateSelected = new Button("Rotate Selected");
        btnRotateSelected.setMaxWidth(Double.MAX_VALUE);
        btnRotateSelected.setOnAction(e -> canvas.rotateSelected(90));

        // ========== Assemble Left Panel ==========
        palettePanel.getChildren().addAll(roomPane, colorPane, furniturePane, btnRotateSelected);

        // ========== 2D Canvas (Centered Wrapper) ==========
        canvas = new DesignerCanvas(currentRoomDesign);
        StackPane canvasWrapper = new StackPane(canvas);
        canvasWrapper.setStyle("-fx-background-color: white;");
        canvasWrapper.setAlignment(Pos.CENTER);

        // Bind canvasWrapper to available size
        canvasWrapper.widthProperty().addListener((obs, oldVal, newVal) -> canvas.draw());
        canvasWrapper.heightProperty().addListener((obs, oldVal, newVal) -> canvas.draw());

        setLeft(palettePanel);
        setCenter(canvasWrapper);
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
            if (update3DCallback != null) update3DCallback.run();
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
            if (update3DCallback != null) update3DCallback.run();
        }
    }

    private class DesignerCanvas extends Canvas {
        private final RoomDesign roomDesign;
        private FurnitureItem selectedItem = null;
        private FurnitureItem lastSelectedItem = null;
        private double offsetX, offsetY;

        public DesignerCanvas(RoomDesign roomDesign) {
            super(roomDesign.getRoomWidth(), roomDesign.getRoomHeight());
            this.roomDesign = roomDesign;
            draw();
            setOnMousePressed(this::onMousePressed);
            setOnMouseDragged(this::onMouseDragged);
            // Keep the last selection so the item remains accessible for rotation
            setOnMouseReleased(e -> {
                if (selectedItem != null) {
                    lastSelectedItem = selectedItem;
                }
            });
        }

        private void onMousePressed(MouseEvent e) {
            double x = e.getX();
            double y = e.getY();
            selectedItem = null;
            for (FurnitureItem item : roomDesign.getFurniture()) {
                if (x >= item.getX() && x <= item.getX() + item.getWidth() &&
                        y >= item.getY() && y <= item.getY() + item.getHeight()) {
                    selectedItem = item;
                    offsetX = x - item.getX();
                    offsetY = y - item.getY();
                    lastSelectedItem = item;
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
                if (update3DCallback != null) update3DCallback.run();
            }
        }

        public void rotateSelected(double angle) {
            // Rotate the currently selected or last-selected furniture
            FurnitureItem itemToRotate = (selectedItem != null) ? selectedItem : lastSelectedItem;
            if (itemToRotate != null) {
                itemToRotate.setRotation((itemToRotate.getRotation() + angle) % 360);
                draw();
                if (update3DCallback != null) update3DCallback.run();
            }
        }

        public void draw() {
            GraphicsContext gc = getGraphicsContext2D();
            double centerX = (getWidth() - roomDesign.getRoomWidth()) / 2.0;
            double centerY = (getHeight() - roomDesign.getRoomHeight()) / 2.0;

            gc.clearRect(0, 0, getWidth(), getHeight());
            gc.save();
            gc.translate(centerX, centerY);

            gc.setFill(roomDesign.getRoomColor());
            gc.fillRect(0, 0, roomDesign.getRoomWidth(), roomDesign.getRoomHeight());
            gc.setStroke(Color.BLACK);
            gc.strokeRect(0, 0, roomDesign.getRoomWidth(), roomDesign.getRoomHeight());

            for (FurnitureItem item : roomDesign.getFurniture()) {
                drawFurniture(gc, item);
            }

            gc.restore();
        }

        private void drawFurniture(GraphicsContext gc, FurnitureItem item) {
            gc.save();
            // Translate so that (0,0) becomes the top-left of the furniture
            gc.translate(item.getX(), item.getY());
            // Translate to center, rotate, then translate back
            gc.translate(item.getWidth() / 2.0, item.getHeight() / 2.0);
            gc.rotate(item.getRotation());
            gc.translate(-item.getWidth() / 2.0, -item.getHeight() / 2.0);

            // Draw based on furniture type (using relative coordinates)
            switch (item.getType().toLowerCase()) {
                case "chair":
                    gc.setFill(item.getPrimaryColor());
                    gc.fillRect(0, 0, item.getWidth(), item.getHeight());
                    gc.setFill(item.getSecondaryColor());
                    gc.fillRect(0, -item.getHeight() / 2, item.getWidth(), item.getHeight() / 2);
                    break;
                case "table":
                case "dining table":
                case "coffee table":
                    gc.setFill(item.getPrimaryColor());
                    gc.fillRect(0, 0, item.getWidth(), item.getHeight());
                    gc.setFill(item.getSecondaryColor());
                    double legSize = 10;
                    gc.fillRect(0, 0, legSize, legSize);
                    gc.fillRect(item.getWidth() - legSize, 0, legSize, legSize);
                    gc.fillRect(0, item.getHeight() - legSize, legSize, legSize);
                    gc.fillRect(item.getWidth() - legSize, item.getHeight() - legSize, legSize, legSize);
                    break;
                case "bed":
                    gc.setFill(item.getPrimaryColor());
                    gc.fillRect(0, 0, item.getWidth(), item.getHeight() / 2);
                    gc.setFill(item.getSecondaryColor());
                    gc.fillRect(10, -8, item.getWidth() / 3.0, 10);
                    break;
                case "sofa":
                    gc.setFill(item.getPrimaryColor());
                    gc.fillRect(0, 0, item.getWidth(), item.getHeight() / 2);
                    gc.setFill(item.getSecondaryColor());
                    gc.fillRect(5, -10, item.getWidth() - 10, 10);
                    break;
                case "bookshelf":
                case "wardrobe":
                    gc.setFill(item.getPrimaryColor());
                    gc.fillRect(0, 0, item.getWidth(), item.getHeight());
                    gc.setStroke(item.getSecondaryColor());
                    if (item.getType().equalsIgnoreCase("bookshelf")) {
                        for (int i = 1; i < 4; i++) {
                            double shelfY = i * item.getHeight() / 4.0;
                            gc.strokeLine(0, shelfY, item.getWidth(), shelfY);
                        }
                    } else {
                        gc.strokeLine(item.getWidth() / 2.0, 0, item.getWidth() / 2.0, item.getHeight());
                    }
                    break;
                case "lamp":
                    gc.setFill(item.getPrimaryColor());
                    gc.fillOval(0, 0, item.getWidth(), item.getHeight() / 2);
                    gc.setFill(item.getSecondaryColor());
                    gc.fillRect(item.getWidth() / 2 - 2, item.getHeight() / 2, 4, item.getHeight() / 2);
                    break;
                case "tv stand":
                    gc.setFill(item.getPrimaryColor());
                    gc.fillRect(0, 0, item.getWidth(), item.getHeight());
                    gc.setFill(item.getSecondaryColor());
                    gc.fillRect(item.getWidth() / 3, -10, item.getWidth() / 3, 10);
                    break;
                default:
                    gc.setFill(item.getPrimaryColor());
                    gc.fillRect(0, 0, item.getWidth(), item.getHeight());
            }
            gc.setStroke(Color.DARKGRAY);
            gc.strokeRect(0, 0, item.getWidth(), item.getHeight());
            gc.restore();
        }
    }
}
