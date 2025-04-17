package com.myfurniture.designapp.UI;

import com.myfurniture.designapp.Core.DesignManager;
import com.myfurniture.designapp.Core.FurnitureItem;
import com.myfurniture.designapp.Core.RoomDesign;
import com.myfurniture.designapp.Factory.FurnitureFactory;
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

        // --- Room Settings Pane ---
        VBox roomSettingsBox = new VBox(10,
                new Label("Room Width:"), txtRoomWidth = new TextField(String.valueOf(currentRoomDesign.getRoomWidth())),
                new Label("Room Height:"), txtRoomHeight = new TextField(String.valueOf(currentRoomDesign.getRoomHeight()))
        );
        Button btnRoomColor = new Button("Select Room Color");
        btnRoomColor.setMaxWidth(Double.MAX_VALUE);
        btnRoomColor.setOnAction(e -> {
            Color c = ColorPickerDialog.showDialog(currentRoomDesign.getRoomColor());
            if (c != null) {
                currentRoomDesign.setRoomColor(c);
                canvas.draw();
                if (update3DCallback != null) update3DCallback.run();
            }
        });
        Button btnApplyRoom = new Button("Apply Settings");
        btnApplyRoom.setMaxWidth(Double.MAX_VALUE);
        btnApplyRoom.setOnAction(e -> applyRoomSettings());
        TitledPane roomPane = new TitledPane("Room Settings",
                new VBox(10, roomSettingsBox, btnRoomColor, btnApplyRoom));
        roomPane.setExpanded(true);

        // --- Furniture Color Pane ---
        Button btnPrimaryColor = new Button("Select Primary Color");
        btnPrimaryColor.setMaxWidth(Double.MAX_VALUE);
        btnPrimaryColor.setOnAction(e -> {
            Color c = ColorPickerDialog.showDialog(chosenPrimary);
            if (c != null) chosenPrimary = c;
        });
        Button btnSecondaryColor = new Button("Select Secondary Color");
        btnSecondaryColor.setMaxWidth(Double.MAX_VALUE);
        btnSecondaryColor.setOnAction(e -> {
            Color c = ColorPickerDialog.showDialog(chosenSecondary);
            if (c != null) chosenSecondary = c;
        });
        TitledPane colorPane = new TitledPane("Furniture Colors",
                new VBox(10, btnPrimaryColor, btnSecondaryColor));
        colorPane.setExpanded(false);

        // --- Wall Color Pane (NEW) ---
        Button btnBackWallColor = new Button("Back Wall Color");
        btnBackWallColor.setMaxWidth(Double.MAX_VALUE);
        btnBackWallColor.setOnAction(e -> {
            Color c = ColorPickerDialog.showDialog(currentRoomDesign.getBackWallColor());
            if (c != null) {
                currentRoomDesign.setBackWallColor(c);
                if (update3DCallback != null) update3DCallback.run();
            }
        });
        Button btnLeftWallColor = new Button("Left Wall Color");
        btnLeftWallColor.setMaxWidth(Double.MAX_VALUE);
        btnLeftWallColor.setOnAction(e -> {
            Color c = ColorPickerDialog.showDialog(currentRoomDesign.getLeftWallColor());
            if (c != null) {
                currentRoomDesign.setLeftWallColor(c);
                if (update3DCallback != null) update3DCallback.run();
            }
        });
        Button btnRightWallColor = new Button("Right Wall Color");
        btnRightWallColor.setMaxWidth(Double.MAX_VALUE);
        btnRightWallColor.setOnAction(e -> {
            Color c = ColorPickerDialog.showDialog(currentRoomDesign.getRightWallColor());
            if (c != null) {
                currentRoomDesign.setRightWallColor(c);
                if (update3DCallback != null) update3DCallback.run();
            }
        });
        TitledPane wallColorPane = new TitledPane("Wall Colors",
                new VBox(10, btnBackWallColor, btnLeftWallColor, btnRightWallColor));
        wallColorPane.setExpanded(false);

        // --- Add Furniture Pane (unchanged) ---
        FlowPane furnitureButtons = new FlowPane(10, 10);
        furnitureButtons.setPrefWrapLength(260);
        String[] furnitureTypes = {
                "Chair","Table","Bed","Sofa","Bookshelf",
                "Wardrobe","Dining Table","Lamp","TV Stand","Coffee Table"
        };
        for (String type : furnitureTypes) {
            Button btn = new Button(type);
            btn.setOnAction(e -> addFurniture(type));
            btn.setPrefWidth(120);
            furnitureButtons.getChildren().add(btn);
        }
        TitledPane furniturePane = new TitledPane("Add Furniture", furnitureButtons);
        furniturePane.setExpanded(true);

        // --- Size & Rotate (unchanged) ---
        Button btnIncreaseSize = new Button("Increase Size");
        btnIncreaseSize.setMaxWidth(Double.MAX_VALUE);
        btnIncreaseSize.setOnAction(e -> canvas.adjustSize(true));
        Button btnDecreaseSize = new Button("Decrease Size");
        btnDecreaseSize.setMaxWidth(Double.MAX_VALUE);
        btnDecreaseSize.setOnAction(e -> canvas.adjustSize(false));
        TitledPane sizePane = new TitledPane("Adjust Size",
                new VBox(10, btnIncreaseSize, btnDecreaseSize));
        sizePane.setExpanded(false);
        Button btnRotateSelected = new Button("Rotate Selected");
        btnRotateSelected.setMaxWidth(Double.MAX_VALUE);
        btnRotateSelected.setOnAction(e -> canvas.rotateSelected(90));

        // --- Assemble Left Panel ---
        palettePanel.getChildren().addAll(
                roomPane, colorPane, wallColorPane, furniturePane, sizePane, btnRotateSelected
        );

        // --- 2D Canvas Wrapper ---
        canvas = new DesignerCanvas(currentRoomDesign);
        StackPane canvasWrapper = new StackPane(canvas);
        canvasWrapper.setStyle("-fx-background-color: white;");
        canvasWrapper.setAlignment(Pos.CENTER);
        canvasWrapper.widthProperty().addListener((o,old,newW)->canvas.draw());
        canvasWrapper.heightProperty().addListener((o,old,newH)->canvas.draw());

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
            // Keep the last selection so the item remains accessible for rotation/size adjustment
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
            draw();
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

        /**
         * Adjust the size of the selected (or last-selected) furniture.
         * The size is increased (multiplied by 1.1) if increase is true,
         * otherwise decreased (multiplied by 0.9). The center of the furniture remains constant.
         */
        public void adjustSize(boolean increase) {
            FurnitureItem itemToAdjust = (selectedItem != null) ? selectedItem : lastSelectedItem;
            if (itemToAdjust != null) {
                double factor = increase ? 1.1 : 0.9;
                // Get the current center position
                double centerX = itemToAdjust.getX() + itemToAdjust.getWidth() / 2.0;
                double centerY = itemToAdjust.getY() + itemToAdjust.getHeight() / 2.0;
                // Calculate new dimensions
                int newWidth = (int) (itemToAdjust.getWidth() * factor);
                int newHeight = (int) (itemToAdjust.getHeight() * factor);
                // Update position so that center remains the same
                int newX = (int) (centerX - newWidth / 2.0);
                int newY = (int) (centerY - newHeight / 2.0);
                itemToAdjust.setWidth(newWidth);
                itemToAdjust.setHeight(newHeight);
                itemToAdjust.setX(newX);
                itemToAdjust.setY(newY);
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
            // Apply translation and rotation for the furniture item
            gc.translate(item.getX(), item.getY());
            gc.translate(item.getWidth() / 2.0, item.getHeight() / 2.0);
            gc.rotate(item.getRotation());
            gc.translate(-item.getWidth() / 2.0, -item.getHeight() / 2.0);

            // Draw the furniture depending on type
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
            // Highlight the selected furniture with a red stroke
            if (item == selectedItem || item == lastSelectedItem) {
                gc.setStroke(Color.RED);
                gc.setLineWidth(3);
            } else {
                gc.setStroke(Color.DARKGRAY);
                gc.setLineWidth(1);
            }
            gc.strokeRect(0, 0, item.getWidth(), item.getHeight());
            gc.restore();
        }
    }
}
