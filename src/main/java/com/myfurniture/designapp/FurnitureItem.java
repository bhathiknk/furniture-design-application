package com.myfurniture.designapp;

import java.awt.*;

public class FurnitureItem {
    private String type;
    private int x, y;
    private int width, height;
    private Color primaryColor;
    private Color secondaryColor;
    private String material; // "wood", "fabric", "leather", etc.

    public FurnitureItem(String type, int x, int y, int width, int height, Color primaryColor, Color secondaryColor, String material) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        this.material = material;
    }

    public String getType() { return type; }
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }
    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }
    public Color getPrimaryColor() { return primaryColor; }
    public void setPrimaryColor(Color primaryColor) { this.primaryColor = primaryColor; }
    public Color getSecondaryColor() { return secondaryColor; }
    public void setSecondaryColor(Color secondaryColor) { this.secondaryColor = secondaryColor; }
    public String getMaterial() { return material; }
}
