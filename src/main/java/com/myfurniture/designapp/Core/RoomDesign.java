package com.myfurniture.designapp.Core;

import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;

public class RoomDesign {
    private int roomWidth;
    private int roomHeight;
    private Color roomColor;

    // NEW: Colors for individual walls
    private Color backWallColor;
    private Color leftWallColor;
    private Color rightWallColor;

    private List<FurnitureItem> furniture;

    public RoomDesign(int roomWidth, int roomHeight, Color roomColor) {
        this.roomWidth = roomWidth;
        this.roomHeight = roomHeight;
        this.roomColor = roomColor;

        // initialize all walls to the room color by default
        this.backWallColor = roomColor;
        this.leftWallColor = roomColor;
        this.rightWallColor = roomColor;

        this.furniture = new ArrayList<>();
    }

    // existing getters/setters...
    public int getRoomWidth() { return roomWidth; }
    public void setRoomWidth(int roomWidth) { this.roomWidth = roomWidth; }
    public int getRoomHeight() { return roomHeight; }
    public void setRoomHeight(int roomHeight) { this.roomHeight = roomHeight; }
    public Color getRoomColor() { return roomColor; }
    public void setRoomColor(Color roomColor) { this.roomColor = roomColor; }

    // NEW: per-wall getters/setters
    public Color getBackWallColor() { return backWallColor; }
    public void setBackWallColor(Color backWallColor) { this.backWallColor = backWallColor; }
    public Color getLeftWallColor() { return leftWallColor; }
    public void setLeftWallColor(Color leftWallColor) { this.leftWallColor = leftWallColor; }
    public Color getRightWallColor() { return rightWallColor; }
    public void setRightWallColor(Color rightWallColor) { this.rightWallColor = rightWallColor; }

    public List<FurnitureItem> getFurniture() { return furniture; }
    public void addFurniture(FurnitureItem item) { furniture.add(item); }
    public void removeFurniture(FurnitureItem item) { furniture.remove(item); }
}
