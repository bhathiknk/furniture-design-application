package com.myfurniture.designapp.UI;
import com.myfurniture.designapp.UI.Furniture2DRenderer;
import com.myfurniture.designapp.Core.DesignManager;
import com.myfurniture.designapp.Core.FurnitureItem;
import com.myfurniture.designapp.Core.RoomDesign;
import com.myfurniture.designapp.Factory.FurnitureFactory;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDesigner2D extends BorderPane {
    private final DesignManager designManager;
    private RoomDesign currentRoomDesign;
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

        // --- Wall Color Pane ---
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

        // --- Add Furniture Pane ---
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

        // --- Save / Load / Delete Controls ---
        Button btnSave = new Button("Save Design...");
        btnSave.setMaxWidth(Double.MAX_VALUE);
        btnSave.setOnAction(e -> saveDesign());

        Button btnLoad = new Button("Load Design...");
        btnLoad.setMaxWidth(Double.MAX_VALUE);
        btnLoad.setOnAction(e -> loadDesign());

        Button btnDelete = new Button("Delete Selected");
        btnDelete.setMaxWidth(Double.MAX_VALUE);
        btnDelete.setOnAction(e -> {
            if (canvas.deleteSelected()) {
                if (update3DCallback != null) update3DCallback.run();
            }
        });

        VBox actionBox = new VBox(10, btnSave, btnLoad, btnDelete);

        // --- Size & Rotate ---
        Button btnIncreaseSize = new Button("Increase Size");
        btnIncreaseSize.setMaxWidth(Double.MAX_VALUE);
        btnIncreaseSize.setOnAction(e -> canvas.adjustSize(true));
        Button btnDecreaseSize = new Button("Decrease Size");
        btnDecreaseSize.setMaxWidth(Double.MAX_VALUE);
        btnDecreaseSize.setOnAction(e -> canvas.adjustSize(false));
        Button btnRotateSelected = new Button("Rotate Selected");
        btnRotateSelected.setMaxWidth(Double.MAX_VALUE);
        btnRotateSelected.setOnAction(e -> canvas.rotateSelected(90));
        TitledPane sizePane = new TitledPane("Adjust Size & Rotate",
                new VBox(10, btnIncreaseSize, btnDecreaseSize, btnRotateSelected));
        sizePane.setExpanded(false);

        // --- Assemble Left Panel ---
        palettePanel.getChildren().addAll(
                roomPane, colorPane, wallColorPane, furniturePane, sizePane, actionBox
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
            showAlert("Please enter valid integers for width and height.");
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

    private void saveDesign() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save Design");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Design files","*.design"));
        File file = chooser.showSaveDialog(getScene().getWindow());
        if (file != null) {
            try (PrintWriter pw = new PrintWriter(file)) {
                // First line: room settings
                pw.printf("%d,%d,%s,%s,%s,%s%n",
                        currentRoomDesign.getRoomWidth(),
                        currentRoomDesign.getRoomHeight(),
                        toHex(currentRoomDesign.getRoomColor()),
                        toHex(currentRoomDesign.getBackWallColor()),
                        toHex(currentRoomDesign.getLeftWallColor()),
                        toHex(currentRoomDesign.getRightWallColor())
                );
                // Following lines: each furniture
                for (FurnitureItem it : currentRoomDesign.getFurniture()) {
                    pw.printf("%s;%d;%d;%d;%d;%s;%s;%s;%.2f%n",
                            it.getType(), it.getX(), it.getY(),
                            it.getWidth(), it.getHeight(),
                            toHex(it.getPrimaryColor()),
                            toHex(it.getSecondaryColor()),
                            it.getMaterial(),
                            it.getRotation()
                    );
                }
                showAlert("Design saved successfully.");
            } catch (IOException e) {
                showAlert("Error saving design: " + e.getMessage());
            }
        }
    }

    private void loadDesign() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open Design");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Design files","*.design"));
        File file = chooser.showOpenDialog(getScene().getWindow());
        if (file != null) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                // Read room settings
                String[] roomParts = br.readLine().split(",");
                currentRoomDesign = new RoomDesign(
                        Integer.parseInt(roomParts[0]),
                        Integer.parseInt(roomParts[1]),
                        Color.web(roomParts[2])
                );
                currentRoomDesign.setBackWallColor(Color.web(roomParts[3]));
                currentRoomDesign.setLeftWallColor(Color.web(roomParts[4]));
                currentRoomDesign.setRightWallColor(Color.web(roomParts[5]));
                // Read furniture
                List<FurnitureItem> list = new ArrayList<>();
                String line;
                while ((line = br.readLine()) != null) {
                    String[] p = line.split(";");
                    FurnitureItem it = new FurnitureItem(
                            p[0],
                            Integer.parseInt(p[1]),
                            Integer.parseInt(p[2]),
                            Integer.parseInt(p[3]),
                            Integer.parseInt(p[4]),
                            Color.web(p[5]),
                            Color.web(p[6]),
                            p[7]
                    );
                    it.setRotation(Double.parseDouble(p[8]));
                    list.add(it);
                }
                currentRoomDesign.getFurniture().clear();
                currentRoomDesign.getFurniture().addAll(list);
                designManager.setCurrentDesign(currentRoomDesign);
                canvas.setRoom(currentRoomDesign);
                canvas.draw();
                if (update3DCallback != null) update3DCallback.run();
                showAlert("Design loaded successfully.");
            } catch (Exception e) {
                showAlert("Error loading design: " + e.getMessage());
            }
        }
    }

    private String toHex(Color c) {
        return String.format("#%02X%02X%02X",
                (int)(c.getRed()*255),
                (int)(c.getGreen()*255),
                (int)(c.getBlue()*255));
    }

    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg);
        a.initOwner(getScene().getWindow());
        a.showAndWait();
    }

    // --- Inner Canvas Class ---
    private class DesignerCanvas extends Canvas {
        private RoomDesign roomDesign;
        private FurnitureItem selectedItem = null;
        private FurnitureItem lastSelectedItem = null;
        private double offsetX, offsetY;

        public DesignerCanvas(RoomDesign roomDesign) {
            super(roomDesign.getRoomWidth(), roomDesign.getRoomHeight());
            this.roomDesign = roomDesign;
            draw();

            setOnMousePressed(this::onMousePressed);
            setOnMouseDragged(this::onMouseDragged);
            setOnMouseReleased(e -> {
                if (selectedItem != null) lastSelectedItem = selectedItem;
            });
            // right-click context menu for delete
            setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.SECONDARY && selectedItem != null) {
                    ContextMenu menu = new ContextMenu();
                    MenuItem del = new MenuItem("Delete");
                    del.setOnAction(ev -> {
                        roomDesign.removeFurniture(selectedItem);
                        selectedItem = null;
                        draw();
                        if (update3DCallback != null) update3DCallback.run();
                    });
                    menu.getItems().add(del);
                    menu.show(this, e.getScreenX(), e.getScreenY());
                }
            });
        }

        public void setRoom(RoomDesign rd) {
            this.roomDesign = rd;
        }

        private void onMousePressed(MouseEvent e) {
            double x = e.getX(), y = e.getY();
            selectedItem = null;
            for (FurnitureItem it : roomDesign.getFurniture()) {
                if (x >= it.getX() && x <= it.getX() + it.getWidth() &&
                        y >= it.getY() && y <= it.getY() + it.getHeight()) {
                    selectedItem = it;
                    offsetX = x - it.getX();
                    offsetY = y - it.getY();
                    lastSelectedItem = it;
                    break;
                }
            }
            draw();
        }

        private void onMouseDragged(MouseEvent e) {
            if (selectedItem != null) {
                double newX = e.getX() - offsetX;
                double newY = e.getY() - offsetY;
                newX = clamp(newX, 0, roomDesign.getRoomWidth() - selectedItem.getWidth());
                newY = clamp(newY, 0, roomDesign.getRoomHeight() - selectedItem.getHeight());
                selectedItem.setX((int)newX);
                selectedItem.setY((int)newY);
                draw();
                if (update3DCallback != null) update3DCallback.run();
            }
        }

        private double clamp(double v, double min, double max) {
            return Math.max(min, Math.min(v, max));
        }

        public boolean deleteSelected() {
            if (lastSelectedItem != null) {
                roomDesign.removeFurniture(lastSelectedItem);
                lastSelectedItem = null;
                selectedItem = null;
                draw();
                return true;
            }
            return false;
        }

        public void adjustSize(boolean inc) { /* same as before */ }
        public void rotateSelected(double ang) { /* same as before */ }


        public void draw() {
            GraphicsContext gc = getGraphicsContext2D();
            // draw room background
            gc.setFill(roomDesign.getRoomColor());
            gc.fillRect(0, 0, roomDesign.getRoomWidth(), roomDesign.getRoomHeight());
            gc.setStroke(Color.BLACK);
            gc.strokeRect(0, 0, roomDesign.getRoomWidth(), roomDesign.getRoomHeight());

            // draw each furniture item using our new 2D renderer
            for (FurnitureItem it : roomDesign.getFurniture()) {
                boolean isSel = (it == selectedItem || it == lastSelectedItem);
                gc.save();
                gc.translate(it.getX() + it.getWidth() / 2.0, it.getY() + it.getHeight() / 2.0);
                gc.rotate(it.getRotation());
                gc.translate(-it.getWidth() / 2.0, -it.getHeight() / 2.0);

                Furniture2DRenderer.drawFurniture(gc, it, isSel);

                gc.restore();
            }
        }
    }
}
