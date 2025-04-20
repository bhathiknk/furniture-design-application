package com.myfurniture.designapp.UI;

import com.myfurniture.designapp.Core.DesignManager;
import com.myfurniture.designapp.Core.FurnitureItem;
import com.myfurniture.designapp.Core.RoomDesign;
import com.myfurniture.designapp.Factory.BoothRoomFactory;
import com.myfurniture.designapp.Factory.Furniture3DFactory;
import javafx.animation.*;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

public class RoomRenderer3D extends StackPane {

    private final DesignManager designManager;
    private final Group roomGroup  = new Group();
    private final Group pivotGroup = new Group(roomGroup);
    private final Group root3D     = new Group(pivotGroup);

    private final PerspectiveCamera camera = new PerspectiveCamera(true);
    private OrbitCameraController cameraController;

    private AmbientLight ambient;
    private PointLight   sun;
    private AnimationTimer lightUpdater;

    private boolean isLightMode    = true;
    private boolean isAutoRotating = false;
    private Timeline autoRotateTimeline;

    private static final double FIT_W = 700, FIT_D = 500;

    public RoomRenderer3D(DesignManager designManager) {
        this.designManager = designManager;
        init3D();
    }

    private void init3D() {
        SubScene subScene = new SubScene(root3D, 0,0, true, SceneAntialiasing.BALANCED);
        subScene.widthProperty().bind(widthProperty());
        subScene.heightProperty().bind(heightProperty());
        subScene.setFill(Color.rgb(240,240,245));
        subScene.setCamera(camera);
        getChildren().add(subScene);

        camera.setFieldOfView(35);
        camera.setNearClip(0.1);
        camera.setFarClip(10000);

        cameraController = new OrbitCameraController(camera, pivotGroup);

        // Mouse controls
        setOnMousePressed(e  -> cameraController.onMousePressed(e.getSceneX(), e.getSceneY()));
        setOnMouseDragged(e  -> cameraController.onMouseDragged(e.getSceneX(), e.getSceneY()));
        setOnMouseReleased(e -> cameraController.onMouseReleased());
        addEventHandler(ScrollEvent.SCROLL, e -> cameraController.zoom(e.getDeltaY()));

        widthProperty().addListener((o, __, ___) -> rebuild());
        heightProperty().addListener((o, __, ___) -> rebuild());

        // Setup our dynamic lights
        ambient = new AmbientLight();
        sun     = new PointLight();
        root3D.getChildren().addAll(ambient, sun);

        // Update sun position each frame
        lightUpdater = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateSunPosition();
            }
        };
        lightUpdater.start();

        rebuild();
        showUserHint("💡 Drag to rotate, scroll to zoom");
        addOverlayButtons();
    }

    private void rebuild() {
        roomGroup.getChildren().clear();

        RoomDesign room = designManager.getCurrentDesign();
        if (room == null) return;

        // Walls + floor
        roomGroup.getChildren().add(BoothRoomFactory.createBooth(room));
        // Furniture
        for (FurnitureItem item : room.getFurniture()) {
            roomGroup.getChildren().add(Furniture3DFactory.createFurniture3D(item));
        }

        // Scale & center
        double sX = FIT_W / room.getRoomWidth();
        double sZ = FIT_D / room.getRoomHeight();
        double scale = Math.min(sX, sZ);
        roomGroup.getTransforms().setAll(new Scale(scale, -scale, -scale));

        Bounds b = roomGroup.getBoundsInParent();
        double cX = (b.getMinX()+b.getMaxX())/2.0;
        double cY = (b.getMinY()+b.getMaxY())/2.0;
        double cZ = (b.getMinZ()+b.getMaxZ())/2.0;
        roomGroup.getTransforms().add(new Translate(-cX, -cY, -cZ));

        setupLighting();  // set base intensities
    }

    private void setupLighting() {
        if (isLightMode) {
            ambient.setColor(Color.rgb(200,200,200, 0.10));
            sun.setColor   (Color.rgb(255,244,220, 0.60));
        } else {
            ambient.setColor(Color.rgb( 60, 60, 80, 0.05));
            sun.setColor   (Color.rgb(140,150,200, 0.40));
        }
        // initial placement
        updateSunPosition();
    }

    /**
     * Place the “sun” very far away in whatever direction the camera is pointing.
     * As the user orbits, rotateX/Y change, so this moves too — real‑time shading.
     */
    private void updateSunPosition() {
        double ry = Math.toRadians(cameraController.getAngleY());
        double rx = Math.toRadians(cameraController.getAngleX());
        // camera forward vector in world coords
        double dx = -Math.sin(ry);
        double dy =  Math.sin(rx);
        double dz = -Math.cos(ry);
        double D = 30_000; // far enough to approximate directional
        sun.setTranslateX(dx * D);
        sun.setTranslateY(dy * D);
        sun.setTranslateZ(dz * D);
    }

    private void addOverlayButtons() {
        Button btnReset       = overlayButton("🔄 Reset View");
        Button btnLightToggle = overlayButton("💡 Toggle Light");
        Button btnAutoRotate  = overlayButton("🎥 Toggle Auto-Rotate");

        btnReset      .setOnAction(e -> cameraController.resetView());
        btnLightToggle.setOnAction(e -> { isLightMode = !isLightMode; setupLighting(); });
        btnAutoRotate .setOnAction(e -> {
            isAutoRotating = !isAutoRotating;
            if (isAutoRotating) startAutoRotate();
            else              stopAutoRotate();
        });

        VBox box = new VBox(8, btnReset, btnLightToggle, btnAutoRotate);
        box.setStyle("-fx-padding:10;");
        box.setTranslateX(10);
        box.setTranslateY(10);
        getChildren().add(box);
        StackPane.setAlignment(box, javafx.geometry.Pos.TOP_LEFT);
    }

    private void startAutoRotate() {
        autoRotateTimeline = new Timeline(new KeyFrame(Duration.millis(16), e -> {
            double cx = getWidth()/2.0, cy = getHeight()/2.0;
            cameraController.onMousePressed(cx, cy);
            cameraController.onMouseDragged(cx+1, cy);
            cameraController.onMouseReleased();
        }));
        autoRotateTimeline.setCycleCount(Animation.INDEFINITE);
        autoRotateTimeline.play();
    }

    private void stopAutoRotate() {
        if (autoRotateTimeline != null) autoRotateTimeline.stop();
    }

    private Button overlayButton(String label) {
        Button b = new Button(label);
        b.setFont(Font.font(13));
        b.setStyle("""
            -fx-background-color: linear-gradient(to right,#3498db,#2980b9);
            -fx-text-fill:white;
            -fx-background-radius:8;
            -fx-padding:6 12;
        """);
        return b;
    }

    public void updateScene() {
        cameraController.resetView();
        rebuild();
    }

    private void showUserHint(String message) {
        Label hint = new Label(message);
        hint.setStyle("""
            -fx-background-color:#000000cc;
            -fx-text-fill:white;
            -fx-padding:6 12;
            -fx-background-radius:10;
        """);
        hint.setFont(Font.font(13));
        getChildren().add(hint);
        StackPane.setAlignment(hint, javafx.geometry.Pos.BOTTOM_CENTER);
        Timeline fade = new Timeline(new KeyFrame(Duration.seconds(4),
                new KeyValue(hint.opacityProperty(), 0)));
        fade.setOnFinished(e -> getChildren().remove(hint));
        fade.play();
    }
}
