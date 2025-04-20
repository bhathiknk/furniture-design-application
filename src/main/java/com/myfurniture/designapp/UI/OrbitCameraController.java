package com.myfurniture.designapp.UI;

import javafx.animation.AnimationTimer;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class OrbitCameraController {

    private final Rotate rotateX = new Rotate(-25, Rotate.X_AXIS);
    private final Rotate rotateY = new Rotate(0,   Rotate.Y_AXIS);
    private final Translate translate = new Translate(0, 0, -1400);

    // Expose current angles for the renderer
    public double getAngleX() { return rotateX.getAngle(); }
    public double getAngleY() { return rotateY.getAngle(); }

    private double anchorX, anchorY;
    private double velocityX = 0, velocityY = 0;
    private boolean dragging = false;

    private static final double ROTATION_SPEED    = 0.25;
    private static final double DAMPING           = 0.87;
    private static final double VELOCITY_THRESHOLD= 0.03;
    private static final double ZOOM_MIN          = -5000;
    private static final double ZOOM_MAX          = -500;

    private long lastTime = 0;

    public OrbitCameraController(PerspectiveCamera camera, Group pivotGroup) {
        camera.getTransforms().addAll(rotateY, rotateX, translate);
        startAnimationLoop();
    }

    public void onMousePressed(double x, double y) {
        anchorX = x; anchorY = y;
        dragging = true;
        velocityX = velocityY = 0;
    }

    public void onMouseDragged(double x, double y) {
        double dx = x - anchorX;
        double dy = y - anchorY;

        rotateY.setAngle(rotateY.getAngle() + dx * ROTATION_SPEED);
        rotateX.setAngle(clamp(rotateX.getAngle() - dy * ROTATION_SPEED * 0.7, -60, 60));

        velocityX = dx;
        velocityY = dy;

        anchorX = x;
        anchorY = y;
    }

    public void onMouseReleased() {
        dragging = false;
    }

    public void zoom(double delta) {
        double newZ = clamp(translate.getZ() + delta, ZOOM_MIN, ZOOM_MAX);
        translate.setZ(newZ);
    }

    public void resetView() {
        rotateX.setAngle(-25);
        rotateY.setAngle(0);
        translate.setZ(-1400);
        velocityX = velocityY = 0;
    }

    private void startAnimationLoop() {
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastTime == 0) {
                    lastTime = now;
                    return;
                }
                if (!dragging &&
                        (Math.abs(velocityX) > VELOCITY_THRESHOLD
                                || Math.abs(velocityY) > VELOCITY_THRESHOLD))
                {
                    rotateY.setAngle(rotateY.getAngle() + velocityX * ROTATION_SPEED * 0.1);
                    rotateX.setAngle(clamp(rotateX.getAngle() + velocityY * ROTATION_SPEED * 0.07, -60, 60));
                    velocityX *= DAMPING;
                    velocityY *= DAMPING;
                }
            }
        }.start();
    }

    private double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }
}
