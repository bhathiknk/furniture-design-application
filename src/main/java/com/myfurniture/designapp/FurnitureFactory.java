package com.myfurniture.designapp;

import java.awt.*;

public class FurnitureFactory {
    public static FurnitureItem createFurniture(String type) {
        switch (type.toLowerCase()) {
            case "chair":
                return new FurnitureItem("Chair", 100, 100, 80, 80, Color.ORANGE, Color.DARK_GRAY, "fabric");
            case "table":
                return new FurnitureItem("Table", 100, 100, 100, 60, Color.CYAN, Color.GRAY, "wood");
            case "bed":
                return new FurnitureItem("Bed", 100, 100, 160, 80, new Color(200, 150, 120), Color.WHITE, "fabric");
            case "sofa":
                return new FurnitureItem("Sofa", 100, 100, 120, 70, new Color(150, 80, 80), new Color(120, 60, 60), "leather");
            case "bookshelf":
                return new FurnitureItem("Bookshelf", 100, 100, 60, 120, new Color(100, 70, 40), Color.BLACK, "wood");
            default:
                return null;
        }
    }
}
