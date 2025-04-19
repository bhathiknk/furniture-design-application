package com.myfurniture.designapp.UI;
import com.myfurniture.designapp.Factory.Furniture2DFactory;
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
        // Left panel container
        palettePanel = new VBox(20);
        palettePanel.setPadding(new Insets(20));
        palettePanel.setPrefWidth(300);
        palettePanel.setStyle(
                "-fx-background-color: #061fc1;" +
                        "-fx-border-color: #061fc1;" +
                        "-fx-border-width: 0 2 0 0;"
        );

        // ROOM SETTINGS
        txtRoomWidth = new TextField(String.valueOf(currentRoomDesign.getRoomWidth()));
        txtRoomHeight = new TextField(String.valueOf(currentRoomDesign.getRoomHeight()));
        VBox roomSettingsBox = new VBox(8,
                new Label("Width:"), txtRoomWidth,
                new Label("Height:"), txtRoomHeight
        );
        styleSection(roomSettingsBox);
        Button btnRoomColor = styledButton("Room Color");
        btnRoomColor.setOnAction(e -> {
            Color c = ColorPickerDialog.showDialog(currentRoomDesign.getRoomColor());
            if (c != null) {
                currentRoomDesign.setRoomColor(c);
                canvas.draw(); notify3D();
            }
        });
        Button btnApplyRoom = styledButton("Apply");
        btnApplyRoom.setOnAction(e -> applyRoomSettings());
        VBox roomControls = new VBox(10, roomSettingsBox, btnRoomColor, btnApplyRoom);
        TitledPane roomPane = titledPane("Room Settings", roomControls);

        // COLORS
        Button btnPrimaryColor = styledButton("Primary Color");
        btnPrimaryColor.setOnAction(e -> {
            Color c = ColorPickerDialog.showDialog(chosenPrimary);
            if (c != null) chosenPrimary = c;
        });
        Button btnSecondaryColor = styledButton("Secondary Color");
        btnSecondaryColor.setOnAction(e -> {
            Color c = ColorPickerDialog.showDialog(chosenSecondary);
            if (c != null) chosenSecondary = c;
        });
        VBox colorBox = new VBox(10, btnPrimaryColor, btnSecondaryColor);
        TitledPane colorPane = titledPane("Furniture Colors", colorBox);

        // WALL COLORS
        Button btnBackWall = styledButton("Back Wall");
        btnBackWall.setOnAction(e -> {
            Color c = ColorPickerDialog.showDialog(currentRoomDesign.getBackWallColor());
            if (c != null) { currentRoomDesign.setBackWallColor(c); notify3D(); }
        });
        Button btnLeftWall = styledButton("Left Wall");
        btnLeftWall.setOnAction(e -> {
            Color c = ColorPickerDialog.showDialog(currentRoomDesign.getLeftWallColor());
            if (c != null) { currentRoomDesign.setLeftWallColor(c); notify3D(); }
        });
        Button btnRightWall = styledButton("Right Wall");
        btnRightWall.setOnAction(e -> {
            Color c = ColorPickerDialog.showDialog(currentRoomDesign.getRightWallColor());
            if (c != null) { currentRoomDesign.setRightWallColor(c); notify3D(); }
        });
        VBox wallBox = new VBox(10, btnBackWall, btnLeftWall, btnRightWall);
        TitledPane wallPane = titledPane("Wall Colors", wallBox);

        // FURNITURE LIST WITH SCROLL
        FlowPane furnitureButtons = new FlowPane(10, 10);
        furnitureButtons.setPrefWidth(260);
        String[] furnitureTypes = {
                "Chair","Table","Bed","Sofa","Bookshelf",
                "Wardrobe","Dining Table","Lamp","TV Stand","Coffee Table"
        };
        for (String type : furnitureTypes) {
            Button btn = styledButton(type);
            btn.setPrefWidth(120);
            btn.setOnAction(e -> addFurniture(type));
            furnitureButtons.getChildren().add(btn);
        }
        ScrollPane furnitureScroll = new ScrollPane(furnitureButtons);
        furnitureScroll.setFitToWidth(true);
        furnitureScroll.setPrefHeight(200);
        VBox furnitureBox = new VBox(10, furnitureScroll);
        TitledPane furniturePane = titledPane("Add Furniture", furnitureBox);

        // ACTIONS & TRANSFORM
        Button btnSave = styledButton("Save Design");
        btnSave.setOnAction(e -> saveDesign());
        Button btnLoad = styledButton("Load Design");
        btnLoad.setOnAction(e -> loadDesign());
        Button btnDelete = styledButton("Delete Selected");
        btnDelete.setOnAction(e -> { if (canvas.deleteSelected()) notify3D(); });

        Button btnInc = styledButton("Size +");
        btnInc.setOnAction(e -> canvas.adjustSize(true));
        Button btnDec = styledButton("Size -");
        btnDec.setOnAction(e -> canvas.adjustSize(false));
        Button btnRot = styledButton("Rotate");
        btnRot.setOnAction(e -> canvas.rotateSelected(90));

        VBox actionBox = new VBox(10,
                btnSave, btnLoad, btnDelete,
                new Separator(), btnInc, btnDec, btnRot
        );
        styleSection(actionBox);

        // assemble
        palettePanel.getChildren().addAll(
                roomPane, colorPane, wallPane, furniturePane, actionBox
        );

        // --- 2D Canvas Center ---
        canvas = new DesignerCanvas(currentRoomDesign);
        StackPane canvasWrapper = new StackPane(canvas);
        canvasWrapper.setStyle("-fx-background-color: white;");
        canvasWrapper.setAlignment(Pos.CENTER);
        canvasWrapper.widthProperty().addListener((o,__,___)-> canvas.draw());
        canvasWrapper.heightProperty().addListener((o,__,___)-> canvas.draw());

        setLeft(palettePanel);
        setCenter(canvasWrapper);
    }

    private void styleSection(Region r) {
        r.setPadding(new Insets(8));
        r.setStyle("-fx-background-color: white; -fx-background-radius: 4;");
    }

    private Button styledButton(String text) {
        Button b = new Button(text);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setStyle(
                "-fx-background-color: #3498db;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 4;" +
                        "-fx-font-size: 12;"
        );
        return b;
    }

    private TitledPane titledPane(String title, Region content) {
        TitledPane tp = new TitledPane(title, content);
        tp.setExpanded(false);
        tp.setAnimated(true);
        tp.setStyle(
                "-fx-text-fill: black;" +
                        "-fx-font-weight: bold;"
        );
        tp.getContent().setStyle("-fx-background-color: #ecf0f1;");
        return tp;
    }

    private void notify3D() {
        canvas.draw();
        if (update3DCallback != null) update3DCallback.run();
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

                Furniture2DFactory.drawFurniture(gc, it, isSel);

                gc.restore();
            }
        }
    }
}
