// src/main/java/com/myfurniture/designapp/Core/RoomDesign.java

package com.myfurniture.designapp.Core;

import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;
import com.myfurniture.designapp.Core.ShapeType;  // ← new import

public class RoomDesign {
    private int roomWidth;
    private int roomHeight;
    private Color roomColor;

    // NEW: Colors for individual walls
    private Color backWallColor;
    private Color leftWallColor;
    private Color rightWallColor;

    // NEW: Shape of the room
    private ShapeType shapeType = ShapeType.RECTANGLE;

    private List<FurnitureItem> furniture;

    public RoomDesign(int roomWidth, int roomHeight, Color roomColor) {
        this.roomWidth = roomWidth;
        this.roomHeight = roomHeight;
        this.roomColor = roomColor;

        this.backWallColor = roomColor;
        this.leftWallColor = roomColor;
        this.rightWallColor = roomColor;

        this.furniture = new ArrayList<>();
    }

    // getters & setters…
    public int getRoomWidth() { return roomWidth; }
    public void setRoomWidth(int roomWidth) { this.roomWidth = roomWidth; }
    public int getRoomHeight() { return roomHeight; }
    public void setRoomHeight(int roomHeight) { this.roomHeight = roomHeight; }
    public Color getRoomColor() { return roomColor; }
    public void setRoomColor(Color roomColor) { this.roomColor = roomColor; }

    public Color getBackWallColor() { return backWallColor; }
    public void setBackWallColor(Color backWallColor) { this.backWallColor = backWallColor; }
    public Color getLeftWallColor() { return leftWallColor; }
    public void setLeftWallColor(Color leftWallColor) { this.leftWallColor = leftWallColor; }
    public Color getRightWallColor() { return rightWallColor; }
    public void setRightWallColor(Color rightWallColor) { this.rightWallColor = rightWallColor; }

    // NEW shapeType
    public ShapeType getShapeType() { return shapeType; }
    public void setShapeType(ShapeType shapeType) { this.shapeType = shapeType; }

    public List<FurnitureItem> getFurniture() { return furniture; }
    public void addFurniture(FurnitureItem item) { furniture.add(item); }
    public void removeFurniture(FurnitureItem item) { furniture.remove(item); }
}
