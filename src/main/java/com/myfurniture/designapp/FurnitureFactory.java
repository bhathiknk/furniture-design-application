package com.myfurniture.designapp;

import javafx.scene.paint.Color;

public class FurnitureFactory {
    public static FurnitureItem createFurniture(String type) {
        switch (type.toLowerCase()) {
            case "chair":
                return new FurnitureItem("Chair", 50, 50, 80, 80, Color.ORANGE, Color.DARKGRAY, "fabric");
            case "table":
                return new FurnitureItem("Table", 50, 50, 100, 60, Color.CYAN, Color.GRAY, "wood");
            case "bed":
                return new FurnitureItem("Bed", 50, 50, 160, 80, Color.rgb(200, 150, 120), Color.WHITE, "fabric");
            case "sofa":
                return new FurnitureItem("Sofa", 50, 50, 120, 70, Color.rgb(150, 80, 80), Color.rgb(120, 60, 60), "leather");
            case "bookshelf":
                return new FurnitureItem("Bookshelf", 50, 50, 60, 120, Color.rgb(100, 70, 40), Color.BLACK, "wood");
            default:
                return null;
        }
    }
}
