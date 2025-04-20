package com.myfurniture.designapp.UI;

import com.myfurniture.designapp.Core.DesignManager;
import com.myfurniture.designapp.Core.FurnitureItem;
import com.myfurniture.designapp.Core.RoomDesign;
import com.myfurniture.designapp.Core.ShapeType;
import com.myfurniture.designapp.Factory.Furniture2DFactory;
import com.myfurniture.designapp.Factory.FurnitureFactory;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDesigner2D extends BorderPane {

    /* --------------------------------------------------------------------- */
    /* fields                                                                 */
    /* --------------------------------------------------------------------- */
    private final DesignManager designManager;
    private RoomDesign          currentRoomDesign;
    private DesignerCanvas      canvas;

    private VBox  palettePanel;
    private Color chosenPrimary   = Color.ORANGE;
    private Color chosenSecondary = Color.DARKGRAY;
    private final Runnable update3DCallback;

    private TextField           txtRoomWidth, txtRoomHeight;
    private ComboBox<ShapeType> shapeCombo;

    private Region primarySwatch, secondarySwatch;

    /* --------------------------------------------------------------------- */
    /* constructor                                                            */
    /* --------------------------------------------------------------------- */
    public RoomDesigner2D(DesignManager dm, Runnable update3DCallback) {
        this.designManager    = dm;
        this.update3DCallback = update3DCallback;

        currentRoomDesign = new RoomDesign(800, 600, Color.LIGHTGRAY);
        designManager.setCurrentDesign(currentRoomDesign);

        initUI();
    }

    /* --------------------------------------------------------------------- */
    /* ui build                                                               */
    /* --------------------------------------------------------------------- */
    private void initUI() {
        /* palette --------------------------------------------------------- */
        palettePanel = new VBox(20);
        palettePanel.setPrefWidth(300);
        palettePanel.setPadding(new Insets(20));
        palettePanel.setStyle(
                "-fx-background-color:#061fc1;" +
                        "-fx-border-color:#34495e;" +
                        "-fx-border-width:0 2 0 0;");

        /* room settings --------------------------------------------------- */
        txtRoomWidth  = new TextField("800");
        txtRoomHeight = new TextField("600");

        txtRoomWidth.textProperty().addListener((o,oldVal,newVal) -> {
            if (shapeCombo.getValue() == ShapeType.SQUARE) txtRoomHeight.setText(newVal);
        });
        txtRoomHeight.textProperty().addListener((o,oldVal,newVal) -> {
            if (shapeCombo.getValue() == ShapeType.SQUARE) txtRoomWidth.setText(newVal);
        });

        shapeCombo = new ComboBox<>(FXCollections.observableArrayList(ShapeType.values()));
        shapeCombo.setValue(ShapeType.RECTANGLE);
        shapeCombo.setOnAction(e -> onShapeSelected());

        HBox shapeBox = new HBox(10, new Label("Shape:"), shapeCombo);
        shapeBox.setAlignment(Pos.CENTER_LEFT);

        VBox roomBox = new VBox(8,
                new Label("Width:"),  txtRoomWidth,
                new Label("Height:"), txtRoomHeight,
                shapeBox);
        styleCard(roomBox);

        Button btnApplyRoom = styledButton("Apply Size");
        btnApplyRoom.setOnAction(e -> applyRoomSettings());

        TitledPane roomPane = titled("Room Settings",
                new VBox(10, roomBox, btnApplyRoom));

        /* furniture colours ---------------------------------------------- */
        Button btnPrimary   = styledButton("Primary Colour");
        Button btnSecondary = styledButton("Secondary Colour");
        btnPrimary.setOnAction(e -> {
            Color c = ColorPickerDialog.showDialog(chosenPrimary);
            if (c != null) { chosenPrimary = c; updateColourPreview(); }
        });
        btnSecondary.setOnAction(e -> {
            Color c = ColorPickerDialog.showDialog(chosenSecondary);
            if (c != null) { chosenSecondary = c; updateColourPreview(); }
        });
        TitledPane colourPane = titled("Furniture Colours",
                new VBox(10, btnPrimary, btnSecondary));

        /* wall colours ---------------------------------------------------- */
        Button btnAllWalls = styledButton("All Walls Colour");
        btnAllWalls.setOnAction(e -> {
            Color c = ColorPickerDialog.showDialog(currentRoomDesign.getBackWallColor());
            if (c != null) {
                currentRoomDesign.setBackWallColor(c);
                currentRoomDesign.setLeftWallColor(c);
                currentRoomDesign.setRightWallColor(c);
                refreshAll();
            }
        });
        Button btnBack  = styledButton("Back Wall");
        Button btnLeft  = styledButton("Left Wall");
        Button btnRight = styledButton("Right Wall");
        btnBack .setOnAction(e -> pickWallColour("back"));
        btnLeft .setOnAction(e -> pickWallColour("left"));
        btnRight.setOnAction(e -> pickWallColour("right"));

        TitledPane wallPane = titled("Wall Colours",
                new VBox(10, btnAllWalls, btnBack, btnLeft, btnRight));

        /* add‑furniture list --------------------------------------------- */
        FlowPane flow = new FlowPane(10,10);
        flow.setPrefWidth(260);
        String[] types = {"Chair","Table","Bed","Sofa","Bookshelf",
                "Wardrobe","Dining Table","Lamp","TV Stand","Coffee Table"};
        for (String t : types) {
            Button b = styledButton(t);
            b.setPrefWidth(120);
            b.setOnAction(e -> addFurniture(t));
            flow.getChildren().add(b);
        }
        ScrollPane listScroll = new ScrollPane(flow);
        listScroll.setFitToWidth(true);
        listScroll.setPrefHeight(180);
        TitledPane furniturePane = titled("Add Furniture", listScroll);

        /* action buttons -------------------------------------------------- */
        Button btnSave = styledButton("Save Design");
        Button btnLoad = styledButton("Load Design");
        Button btnDel  = styledButton("Delete Selected");
        Button btnInc  = styledButton("Size +");
        Button btnDec  = styledButton("Size -");
        Button btnRot  = styledButton("Rotate");

        btnSave.setOnAction(e -> saveDesign());
        btnLoad.setOnAction(e -> loadDesign());
        btnDel .setOnAction(e -> { if (canvas.deleteSelected()) refreshAll(); });
        btnInc .setOnAction(e -> { canvas.adjustSize(true);  refreshAll(); });
        btnDec .setOnAction(e -> { canvas.adjustSize(false); refreshAll(); });
        btnRot .setOnAction(e -> { canvas.rotateSelected(90); refreshAll(); });

        VBox actionBox = new VBox(10,
                btnSave, btnLoad, btnDel,
                new Separator(),
                btnInc, btnDec, btnRot);
        styleCard(actionBox);

        palettePanel.getChildren().addAll(roomPane, colourPane, wallPane,
                furniturePane, actionBox);

        /* colour‑preview bar --------------------------------------------- */
        primarySwatch   = swatch(chosenPrimary);
        secondarySwatch = swatch(chosenSecondary);

        Label lblPrim = new Label("Primary");
        Label lblSec  = new Label("Secondary");
        VBox  boxPrim = new VBox(primarySwatch,   lblPrim);
        VBox  boxSec  = new VBox(secondarySwatch, lblSec);
        boxPrim.setAlignment(Pos.CENTER);
        boxSec .setAlignment(Pos.CENTER);

        HBox previewBar = new HBox(40, boxPrim, boxSec);
        previewBar.setAlignment(Pos.CENTER);
        previewBar.setPadding(new Insets(8));
        previewBar.setStyle("-fx-background-color:white;");

        /* centre – preview bar + canvas ---------------------------------- */
        canvas = new DesignerCanvas(currentRoomDesign);

        StackPane canvasHolder = new StackPane(canvas);
        canvasHolder.setAlignment(Pos.CENTER);
        canvasHolder.setStyle(
                "-fx-background-color:#ecf0f1;" +
                        "-fx-background-radius:8;" +
                        "-fx-padding:8;");
        canvasHolder.setEffect(new DropShadow(10, Color.gray(0,0.25)));
        canvasHolder.widthProperty().addListener((o,__,___)-> canvas.draw());
        canvasHolder.heightProperty().addListener((o,__,___)-> canvas.draw());

        VBox centreBox = new VBox(previewBar, canvasHolder);
        VBox.setVgrow(canvasHolder, Priority.ALWAYS);

        setLeft(palettePanel);
        setCenter(centreBox);

        updateColourPreview();
    }

    /* --------------------------------------------------------------------- */
    /* helpers                                                                */
    /* --------------------------------------------------------------------- */
    private Region swatch(Color colour) {
        Region chip = new Region();
        chip.setMinSize(40, 40);
        chip.setPrefSize(40, 40);
        chip.setMaxSize(40, 40);
        chip.setStyle("-fx-border-color:black;");
        chip.setBackground(new Background(
                new BackgroundFill(colour, CornerRadii.EMPTY, Insets.EMPTY)));
        return chip;
    }

    private void updateColourPreview() {
        primarySwatch .setStyle("-fx-background-color:" + toHex(chosenPrimary) + "; -fx-border-color:black;");
        secondarySwatch.setStyle("-fx-background-color:" + toHex(chosenSecondary) + "; -fx-border-color:black;");
    }

    private void onShapeSelected() {
        if (shapeCombo.getValue() == ShapeType.SQUARE) {
            txtRoomHeight.setText(txtRoomWidth.getText());
        } else {
            txtRoomWidth .setText("800");
            txtRoomHeight.setText("600");
        }
    }

    private void applyRoomSettings() {
        try {
            int w = Integer.parseInt(txtRoomWidth.getText().trim());
            int h = (shapeCombo.getValue() == ShapeType.SQUARE)
                    ? w
                    : Integer.parseInt(txtRoomHeight.getText().trim());

            currentRoomDesign.setShapeType(shapeCombo.getValue());
            currentRoomDesign.setRoomWidth(w);
            currentRoomDesign.setRoomHeight(h);

            canvas.setWidth(w);
            canvas.setHeight(h);

            refreshAll();
        } catch (NumberFormatException ex) {
            showAlert("Enter valid integers for size.");
        }
    }

    /** let user pick back/left/right wall colour */
    private void pickWallColour(String which) {
        Color current = switch (which) {
            case "back"  -> currentRoomDesign.getBackWallColor();
            case "left"  -> currentRoomDesign.getLeftWallColor();
            default      -> currentRoomDesign.getRightWallColor();
        };
        Color c = ColorPickerDialog.showDialog(current);
        if (c == null) return;
        switch (which) {
            case "back"  -> currentRoomDesign.setBackWallColor(c);
            case "left"  -> currentRoomDesign.setLeftWallColor(c);
            case "right" -> currentRoomDesign.setRightWallColor(c);
        }
        refreshAll();
    }

    /* --------------------------------------------------------------------- */
    /* updated: addFurniture now triggers immediate 3D update               */
    private void addFurniture(String type) {
        FurnitureItem it = FurnitureFactory.createFurniture(type);
        if (it == null) return;
        it.setPrimaryColor  (chosenPrimary);
        it.setSecondaryColor(chosenSecondary);
        it.setX(currentRoomDesign.getRoomWidth()/2  - it.getWidth()/2);
        it.setY(currentRoomDesign.getRoomHeight()/2 - it.getHeight()/2);
        currentRoomDesign.addFurniture(it);

        // instant 3D rebuild
        refreshAll();
    }

    private void saveDesign() {
        FileChooser ch = new FileChooser();
        ch.setTitle("Save Design");
        ch.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Design files","*.design"));
        File file = ch.showSaveDialog(getScene().getWindow());
        if (file == null) return;

        try (PrintWriter pw = new PrintWriter(file)) {
            pw.printf("%d,%d,%s,%s,%s,%s%n",
                    currentRoomDesign.getRoomWidth(),
                    currentRoomDesign.getRoomHeight(),
                    toHex(currentRoomDesign.getRoomColor()),
                    toHex(currentRoomDesign.getBackWallColor()),
                    toHex(currentRoomDesign.getLeftWallColor()),
                    toHex(currentRoomDesign.getRightWallColor()));
            for (FurnitureItem it : currentRoomDesign.getFurniture()) {
                pw.printf("%s;%d;%d;%d;%d;%s;%s;%s;%.2f%n",
                        it.getType(), it.getX(), it.getY(),
                        it.getWidth(), it.getHeight(),
                        toHex(it.getPrimaryColor()),
                        toHex(it.getSecondaryColor()),
                        it.getMaterial(),
                        it.getRotation());
            }
            showAlert("Design saved.");
        } catch (IOException ex) {
            showAlert("Save failed: " + ex.getMessage());
        }
    }

    private void loadDesign() {
        FileChooser ch = new FileChooser();
        ch.setTitle("Open Design");
        ch.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Design files","*.design"));
        File file = ch.showOpenDialog(getScene().getWindow());
        if (file == null) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String[] room = br.readLine().split(",");
            currentRoomDesign = new RoomDesign(
                    Integer.parseInt(room[0]),
                    Integer.parseInt(room[1]),
                    Color.web(room[2]));
            currentRoomDesign.setBackWallColor (Color.web(room[3]));
            currentRoomDesign.setLeftWallColor (Color.web(room[4]));
            currentRoomDesign.setRightWallColor(Color.web(room[5]));

            List<FurnitureItem> list = new ArrayList<>();
            for (String ln; (ln = br.readLine()) != null; ) {
                String[] p = ln.split(";");
                FurnitureItem it = new FurnitureItem(
                        p[0], Integer.parseInt(p[1]), Integer.parseInt(p[2]),
                        Integer.parseInt(p[3]), Integer.parseInt(p[4]),
                        Color.web(p[5]), Color.web(p[6]), p[7]);
                it.setRotation(Double.parseDouble(p[8]));
                list.add(it);
            }
            currentRoomDesign.getFurniture().clear();
            currentRoomDesign.getFurniture().addAll(list);

            designManager.setCurrentDesign(currentRoomDesign);
            canvas.setRoom(currentRoomDesign);
            refreshAll();
            showAlert("Design loaded.");
        } catch (Exception ex) {
            showAlert("Load failed: " + ex.getMessage());
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

    private Button styledButton(String t) {
        Button b = new Button(t);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setStyle(
                "-fx-background-color:#3498db;" +
                        "-fx-text-fill:white;" +
                        "-fx-background-radius:6;" +
                        "-fx-font-size:13;");
        return b;
    }

    private void styleCard(Region r) {
        r.setPadding(new Insets(10));
        r.setStyle(
                "-fx-background-color:white;" +
                        "-fx-background-radius:6;" +
                        "-fx-border-color:#dcdcdc;" +
                        "-fx-border-radius:6;");
    }

    private TitledPane titled(String t, Region content) {
        TitledPane tp = new TitledPane(t, content);
        tp.setExpanded(false);
        tp.setAnimated(true);
        tp.setStyle(
                "-fx-font-size:14;" +
                        "-fx-font-weight:bold;" +
                        "-fx-text-fill:black;" +
                        "-fx-background-color:#2980b9;");
        content.setStyle(
                "-fx-background-color:#ecf0f1;" +
                        "-fx-padding:8;" +
                        "-fx-background-radius:6;");
        return tp;
    }

    private void refreshAll() {
        canvas.draw();
        updateColourPreview();
        if (update3DCallback != null) update3DCallback.run();
    }

    /* --------------------------------------------------------------------- */
    /* inner canvas                                                          */
    /* --------------------------------------------------------------------- */
    private class DesignerCanvas extends Canvas {

        private RoomDesign   roomDesign;
        private FurnitureItem selectedItem, lastSelectedItem;
        private double offsetX, offsetY;

        DesignerCanvas(RoomDesign rd) {
            super(rd.getRoomWidth(), rd.getRoomHeight());
            roomDesign = rd;
            draw();

            setOnMousePressed(this::onMousePressed);
            setOnMouseDragged(this::onMouseDragged);
            setOnMouseReleased(e -> {
                if (selectedItem != null) lastSelectedItem = selectedItem;
                draw();
                // immediate 3D update after drag
                refreshAll();
            });
            setOnMouseClicked(this::onMouseClicked);
        }

        void setRoom(RoomDesign rd) {
            roomDesign = rd;
            setWidth(rd.getRoomWidth());
            setHeight(rd.getRoomHeight());
        }

        private void onMouseClicked(MouseEvent e) {
            if (e.getButton() == MouseButton.SECONDARY && selectedItem != null) {
                ContextMenu m = new ContextMenu();
                MenuItem del = new MenuItem("Delete");
                del.setOnAction(ev -> {
                    roomDesign.removeFurniture(selectedItem);
                    selectedItem = null;
                    draw();
                    refreshAll();
                });
                m.getItems().add(del);
                m.show(this, e.getScreenX(), e.getScreenY());
            }
        }

        private void onMousePressed(MouseEvent e) {
            double x = e.getX(), y = e.getY();
            selectedItem = null;
            for (FurnitureItem it : roomDesign.getFurniture()) {
                if (x >= it.getX() && x <= it.getX()+it.getWidth() &&
                        y >= it.getY() && y <= it.getY()+it.getHeight()) {
                    selectedItem = it;
                    offsetX = x - it.getX();
                    offsetY = y - it.getY();
                    break;
                }
            }
            draw();
        }

        private void onMouseDragged(MouseEvent e) {
            if (selectedItem == null) return;
            double nx = e.getX() - offsetX;
            double ny = e.getY() - offsetY;
            nx = clamp(nx, 0, roomDesign.getRoomWidth() - selectedItem.getWidth());
            ny = clamp(ny, 0, roomDesign.getRoomHeight() - selectedItem.getHeight());
            selectedItem.setX((int) nx);
            selectedItem.setY((int) ny);
            draw();
        }

        boolean deleteSelected() {
            if (lastSelectedItem == null) return false;
            roomDesign.removeFurniture(lastSelectedItem);
            lastSelectedItem = null;
            draw();
            return true;
        }

        void adjustSize(boolean inc) {
            FurnitureItem it = (selectedItem != null) ? selectedItem : lastSelectedItem;
            if (it == null) return;
            double f = inc ? 1.1 : 0.9;
            double cx = it.getX() + it.getWidth()/2.0;
            double cy = it.getY() + it.getHeight()/2.0;
            int nw = (int)(it.getWidth()*f), nh = (int)(it.getHeight()*f);
            it.setX((int)(cx - nw/2.0));
            it.setY((int)(cy - nh/2.0));
            it.setWidth(nw);
            it.setHeight(nh);
            draw();
        }

        void rotateSelected(double a) {
            FurnitureItem it = (selectedItem != null) ? selectedItem : lastSelectedItem;
            if (it == null) return;
            it.setRotation((it.getRotation() + a) % 360);
            draw();
        }

        void draw() {
            GraphicsContext gc = getGraphicsContext2D();
            double rw = roomDesign.getRoomWidth(), rh = roomDesign.getRoomHeight();

            gc.setFill(roomDesign.getRoomColor());
            gc.fillRect(0, 0, rw, rh);
            gc.setStroke(Color.BLACK);
            gc.strokeRect(0, 0, rw, rh);

            double t = 10;
            gc.setFill(roomDesign.getBackWallColor());
            gc.fillRect(0, 0, rw, t);
            gc.setFill(roomDesign.getLeftWallColor());
            gc.fillRect(0, 0, t, rh);
            gc.setFill(roomDesign.getRightWallColor());
            gc.fillRect(rw-t, 0, t, rh);

            for (FurnitureItem it : roomDesign.getFurniture()) {
                boolean sel = (it == selectedItem || it == lastSelectedItem);
                gc.save();
                gc.translate(it.getX() + it.getWidth()/2.0,
                        it.getY() + it.getHeight()/2.0);
                gc.rotate(it.getRotation());
                gc.translate(-it.getWidth()/2.0, -it.getHeight()/2.0);
                Furniture2DFactory.drawFurniture(gc, it, sel);
                gc.restore();
            }
        }

        private double clamp(double v, double min, double max) {
            return Math.max(min, Math.min(max, v));
        }
    }
}
