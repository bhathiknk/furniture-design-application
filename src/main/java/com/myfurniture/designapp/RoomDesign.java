package com.myfurniture.designapp;

import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;

public class RoomDesign {
    private int roomWidth;
    private int roomHeight;
    private Color roomColor;
    private List<FurnitureItem> furniture;

    public RoomDesign(int roomWidth, int roomHeight, Color roomColor) {
        this.roomWidth = roomWidth;
        this.roomHeight = roomHeight;
        this.roomColor = roomColor;
        this.furniture = new ArrayList<>();
    }

    public int getRoomWidth() {
        return roomWidth;
    }

    public void setRoomWidth(int roomWidth) {
        this.roomWidth = roomWidth;
    }

    public int getRoomHeight() {
        return roomHeight;
    }

    public void setRoomHeight(int roomHeight) {
        this.roomHeight = roomHeight;
    }

    public Color getRoomColor() {
        return roomColor;
    }

    public void setRoomColor(Color roomColor) {
        this.roomColor = roomColor;
    }

    public List<FurnitureItem> getFurniture() {
        return furniture;
    }

    public void addFurniture(FurnitureItem item) {
        furniture.add(item);
    }

    public void removeFurniture(FurnitureItem item) {
        furniture.remove(item);
    }
}
