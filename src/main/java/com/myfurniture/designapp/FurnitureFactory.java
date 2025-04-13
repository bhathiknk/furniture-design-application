package com.myfurniture.designapp;

import java.awt.*;

public class FurnitureFactory {
    public static FurnitureItem createFurniture(String type) {
        if ("Chair".equalsIgnoreCase(type)) {
            // Default dimensions and colors for a chair
            return new FurnitureItem("Chair", 100, 100, 80, 80, Color.ORANGE, Color.GRAY);
        } else if ("Table".equalsIgnoreCase(type)) {
            return new FurnitureItem("Table", 100, 100, 120, 80, Color.CYAN, Color.DARK_GRAY);
        }
        return null;
    }
}
